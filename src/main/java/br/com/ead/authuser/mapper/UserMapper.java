package br.com.ead.authuser.mapper;

import br.com.ead.authuser.dtos.UserRequestDTO;
import br.com.ead.authuser.dtos.UserResponseDTO;
import br.com.ead.authuser.dtos.UserUpdateDTO;
import br.com.ead.authuser.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequestDTO dto);
    UserResponseDTO toDTO(User user);
    List<UserResponseDTO> toDTOList(List<User> users);
    void updateUserFromDto(UserUpdateDTO dto, @MappingTarget User user);
}
