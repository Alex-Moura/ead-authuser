package br.com.ead.authuser.service;


import br.com.ead.authuser.dtos.UserResponseDTO;
import br.com.ead.authuser.dtos.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponseDTO findById(UUID id);
    UserResponseDTO update(UUID id, UserUpdateDTO userUpdateDTO);
    void delete(UUID id);
    Page<UserResponseDTO> findAll(Pageable pageable);
}
