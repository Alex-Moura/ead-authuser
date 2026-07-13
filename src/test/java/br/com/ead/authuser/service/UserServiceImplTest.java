package br.com.ead.authuser.service;

import br.com.ead.authuser.dtos.UserResponseDTO;
import br.com.ead.authuser.dtos.UserUpdateDTO;
import br.com.ead.authuser.exceptions.ErrorMessages;
import br.com.ead.authuser.exceptions.custom.ResourceNotFoundException;
import br.com.ead.authuser.mapper.UserMapper;
import br.com.ead.authuser.model.User;
import br.com.ead.authuser.repository.UserRepository;
import br.com.ead.authuser.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.ead.authuser.common.TestFixture.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("It should return a DTO when the user exists")
        void shouldReturnDtoWhenUserExists() {
            User entity = userEntity();
            UserResponseDTO response = userResponse();

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(entity));
            when(userMapper.toDTO(entity)).thenReturn(response);

            UserResponseDTO result = userService.findById(USER_ID);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(USER_ID);
            assertThat(result.email()).isEqualTo(EMAIL);
            verify(userRepository).findById(USER_ID);
            verify(userMapper).toDTO(entity);
        }

        @Test
        @DisplayName("It should throw a ResourceNotFoundException when the user does not exist")
        void shouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
            UUID nonexistent = UUID.randomUUID();
            when(userRepository.findById(nonexistent)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.findById(nonexistent))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.USER_NOT_FOUND, nonexistent));

            verifyNoInteractions(userMapper);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {
        @Test
        @DisplayName("should return a Page of DTOs when users exist")
        void shouldReturnPageOfDTOsWhenUsersExist() {
            User entity = userEntity();
            UserResponseDTO response = userResponse();
            Page<User> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

            when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(userMapper.toDTO(entity)).thenReturn(response);

            Specification<User> NoFilter = (root, query, cb) -> null;
            Page<UserResponseDTO> result = userService.findAll(NoFilter, PageRequest.of(0, 10));

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).id()).isEqualTo(USER_ID);
            verify(userRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @DisplayName("should return an empty Page when there are no users")
        void shouldReturnEmptyPageWhenThereAreNoUsers() {
            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(Page.empty(PageRequest.of(0, 10)));

            Page<UserResponseDTO> result = userService.findAll((root, q, cb) -> null, PageRequest.of(0, 10));

            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
            verifyNoInteractions(userMapper);
        }

        @Test
        @DisplayName("Should respect the correct pagination, page, and size")
        void shouldRespectPagination() {
            User entity = userEntity();
            Page<User> secondPage = new PageImpl<>(List.of(entity), PageRequest.of(1, 1), 3);

            when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(secondPage);
            when(userMapper.toDTO(entity)).thenReturn(userResponse());

            Page<UserResponseDTO> result = userService.findAll((root, q, cb) -> null, PageRequest.of(1, 1));

            assertThat(result.getNumber()).isEqualTo(1);
            assertThat(result.getSize()).isEqualTo(1);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getTotalPages()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("update()")
    class Update{
        @Test
        @DisplayName("should update and return the DTO when the user exists")
        void shouldUpdateAndReturnDTOWhenUserExists() {
            User entity = userEntity();
            UserUpdateDTO updateDTO = userUpdateDTO();
            UserResponseDTO updatedResponse = updatedUserResponse();

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(entity));
            when(userRepository.save(entity)).thenReturn(entity);
            when(userMapper.toDTO(entity)).thenReturn(updatedResponse);

            UserResponseDTO result = userService.update(USER_ID, updateDTO);

            assertThat(result).isNotNull();
            assertThat(result.fullName()).isEqualTo("João Atualizado");
            verify(userMapper).updateUserFromDto(updateDTO, entity);
            verify(userRepository).save(entity);
        }

        @Test
        @DisplayName("should throw `ResourceNotFoundException` when the user does not exist in the update")
        void shouldThrowExceptionWhenUserDoesNotExistOnUpdate() {
            UUID nonexistent = UUID.randomUUID();
            when(userRepository.findById(nonexistent)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.update(nonexistent, userUpdateDTO()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.USER_NOT_FOUND, nonexistent));

            verify(userRepository, never()).save(any());
            verify(userMapper, never()).updateUserFromDto(any(), any());
        }
    }
    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("should delete without throwing an exception when the user exists")
        void shouldDeleteWithoutExceptionWhenUserExists() {
            User entity = userEntity();
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(entity));

            assertThatCode(() -> userService.delete(USER_ID)).doesNotThrowAnyException();

            verify(userRepository).delete(entity);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when deleting a nonexistent user")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            UUID nonexistent = UUID.randomUUID();
            when(userRepository.findById(nonexistent)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.delete(nonexistent))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.USER_NOT_FOUND, nonexistent));

            verify(userRepository, never()).delete(any(User.class));
        }
    }
}