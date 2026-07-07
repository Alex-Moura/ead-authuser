package br.com.ead.authuser.dtos;

import br.com.ead.authuser.enums.UserRole;
import br.com.ead.authuser.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
    UUID id,
    String userName,
    String email,
    String fullName,
    UserStatus userStatus,
    UserRole userRole,
    String phoneNumber,
    String cpf,
    String imageUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
