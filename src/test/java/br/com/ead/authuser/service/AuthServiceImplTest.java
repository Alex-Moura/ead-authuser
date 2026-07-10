package br.com.ead.authuser.service;

import br.com.ead.authuser.dtos.UserRequestDTO;
import br.com.ead.authuser.dtos.UserResponseDTO;
import br.com.ead.authuser.enums.UserRole;
import br.com.ead.authuser.enums.UserStatus;
import br.com.ead.authuser.exceptions.custom.ConflictException;
import br.com.ead.authuser.mapper.UserMapper;
import br.com.ead.authuser.model.User;
import br.com.ead.authuser.repository.UserRepository;
import br.com.ead.authuser.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static br.com.ead.authuser.common.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl")
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthServiceImpl authService;

    @Nested
    @DisplayName("register()")
    class Register{

        @Test
        @DisplayName("should register and return a DTO when the data is valid")
        void shouldRegisterAndReturnDTOWhenDataIsValid(){
            UserRequestDTO request = userRequest();
            User entity = userEntity();
            UserResponseDTO response = userResponse();

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userRepository.existsByUserName(request.userName())).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(entity);
            when(passwordEncoder.encode(entity.getPassword())).thenReturn("$2a$10$encoded");
            when(userRepository.save(entity)).thenReturn(entity);
            when(userMapper.toDTO(entity)).thenReturn(response);

            UserResponseDTO result = authService.register(request);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(USER_ID);
            assertThat(result.email()).isEqualTo(EMAIL);
            assertThat(result.userRole()).isEqualTo(UserRole.STUDENT);
            assertThat(result.userStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("It should always assign the `STUDENT` role to a new user, regardless of what the mapper returns")
        void shouldAssignStudentRoleToNewUser(){
            UserRequestDTO request = userRequest();
            User entity = userEntity();
            entity.setUserRole(UserRole.ADMIN);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUserName(anyString())).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(entity);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            when(userRepository.save(any())).thenReturn(entity);
            when(userMapper.toDTO(any())).thenReturn(userResponse());

            authService.register(request);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getUserRole()).isEqualTo(UserRole.STUDENT);
        }

        @Test
        @DisplayName("It should always assign the ACTIVE status to a new user")
        void shouldAlwaysAssignActiveStatusToNewUser(){
            UserRequestDTO request = userRequest();
            User entity = userEntity();
            entity.setUserStatus(UserStatus.BLOCKED);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUserName(anyString())).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(entity);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
            when(userRepository.save(any())).thenReturn(entity);
            when(userMapper.toDTO(any())).thenReturn(userResponse());

            authService.register(request);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        }
        @Test
        @DisplayName("It should encode the password before saving it and never persist it in plain text")
        void shouldEncodePasswordBeforeSaving(){
            UserRequestDTO request = userRequest();
            User entity = userEntity();
            entity.setPassword(PASSWORD);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUserName(anyString())).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(entity);
            when(passwordEncoder.encode(PASSWORD)).thenReturn("$2a$10$hashed");
            when(userRepository.save(any())).thenReturn(entity);
            when(userMapper.toDTO(any())).thenReturn(userResponse());

            authService.register(request);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getPassword())
                    .isNotEqualTo(PASSWORD)
                    .startsWith("$2a$10$");
        }
    }

    @Test
    @DisplayName("It should throw ConflictException when the email is already registered")
    void shouldThrowConflictExceptionWhenEmailAlreadyRegistered(){
        UserRequestDTO request = userRequest();
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(()->authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email já cadastrado");

        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("It should throw ConflictException when the username is already registered")
    void shouldThrowConflictExceptionWhenUsernameAlreadyRegistered() {
        UserRequestDTO request = userRequest();
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUserName(request.userName())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Username já cadastrado");

        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("It should check email BEFORE username correct short-circuit behavior")
    void shouldCheckEmailBeforeUsernameWithCorrectShortCircuit() {
        UserRequestDTO request = userRequest();
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email já cadastrado");

        verify(userRepository, never()).existsByUserName(anyString());
    }

}
