package br.com.ead.authuser.service;

import br.com.ead.authuser.dtos.UserRequestDTO;
import br.com.ead.authuser.dtos.UserResponseDTO;
import br.com.ead.authuser.dtos.UserUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDTO findById(UUID id);
    List<UserResponseDTO> findAll();
    UserResponseDTO update(UUID id, UserUpdateDTO userUpdateDTO);
    void delete(UUID id);
}
