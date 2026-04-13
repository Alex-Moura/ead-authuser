package br.com.ead.authuser.service;

import br.com.ead.authuser.dtos.UserRequestDTO;
import br.com.ead.authuser.dtos.UserResponseDTO;

public interface UserService {
    UserResponseDTO signupUser(UserRequestDTO dto);
}
