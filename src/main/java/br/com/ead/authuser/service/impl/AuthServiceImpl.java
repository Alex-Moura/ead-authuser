package br.com.ead.authuser.service.impl;

import br.com.ead.authuser.config.security.SecurityConfig;
import br.com.ead.authuser.dtos.UserRequestDTO;
import br.com.ead.authuser.dtos.UserResponseDTO;
import br.com.ead.authuser.enums.UserRole;
import br.com.ead.authuser.enums.UserStatus;
import br.com.ead.authuser.exceptions.custom.ConflictException;
import br.com.ead.authuser.mapper.UserMapper;
import br.com.ead.authuser.model.User;
import br.com.ead.authuser.repository.UserRepository;
import br.com.ead.authuser.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityConfig securityConfig;


    @Override
    public UserResponseDTO register(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("Email já cadastrado");
        }
        if (userRepository.existsByUserName(dto.userName())) {
            throw new ConflictException("Username já cadastrado");
        }
        User user = userMapper.toEntity(dto);
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
        user.setUserRole(UserRole.STUDENT);
        user.setUserStatus(UserStatus.ACTIVE);

        return userMapper.toDTO(userRepository.save(user));
    }
}
