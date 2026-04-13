package br.com.ead.authuser.controller;

import br.com.ead.authuser.dtos.UserRequestDTO;
import br.com.ead.authuser.dtos.UserResponseDTO;
import br.com.ead.authuser.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(@RequestBody @Valid UserRequestDTO dto){
        UserResponseDTO userResponseDTO = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }
}
