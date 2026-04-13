package br.com.ead.authuser.mapper;

import br.com.ead.authuser.dtos.UserRequestDTO;
import br.com.ead.authuser.dtos.UserResponseDTO;
import br.com.ead.authuser.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequestDTO dto);
    UserResponseDTO toDTO(User user);
}
