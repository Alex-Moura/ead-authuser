package br.com.ead.authuser.service.impl;

import br.com.ead.authuser.dtos.UserRequestDTO;
import br.com.ead.authuser.dtos.UserResponseDTO;
import br.com.ead.authuser.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public UserResponseDTO signupUser(UserRequestDTO dto) {
        return null;
    }
}

