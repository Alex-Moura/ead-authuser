package br.com.ead.authuser.common;

import br.com.ead.authuser.dtos.UserRequestDTO;
import br.com.ead.authuser.dtos.UserResponseDTO;
import br.com.ead.authuser.dtos.UserUpdateDTO;
import br.com.ead.authuser.enums.UserRole;
import br.com.ead.authuser.enums.UserStatus;
import br.com.ead.authuser.model.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public class TestFixture {

    private TestFixture() {
    }

    public static final UUID USER_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    public static final String USERNAME = "joaosilva";
    public static final String EMAIL = "joao@email.com";
    public static final String PASSWORD = "Senha123";
    public static final String FULL_NAME    = "João da Silva";
    public static final String PHONE_NUMBER = "11987654321";
    public static final String CPF = "529.982.247-25";
    public static final String IMAGE_URL    = "https://img.example.com/avatar.jpg";
    public static final LocalDateTime FIXED_NOW = LocalDateTime.of(2025, 1, 15, 10, 0);

    public static final String FULL_NAME_UPDATED = "João Atualizado";
    public static final String PHONE_NUMBER_UPDATED = "11999999999";

    public static UserRequestDTO userRequest() {
        return new UserRequestDTO(
                USERNAME,
                EMAIL,
                PASSWORD,
                FULL_NAME,
                PHONE_NUMBER,
                CPF,
                IMAGE_URL
        );
    }

    public static UserRequestDTO userRequestWithEmail(String email) {
        return new UserRequestDTO(
                USERNAME,
                email,
                PASSWORD,
                FULL_NAME,
                PHONE_NUMBER,
                CPF,
                null
        );
    }

    public static UserRequestDTO userRequestWithUsername(String username) {
        return new UserRequestDTO(
                username,
                EMAIL,
                PASSWORD,
                FULL_NAME,
                PHONE_NUMBER,
                CPF,
                null
        );
    }

    public static UserUpdateDTO userUpdateDTO() {
        return new UserUpdateDTO(
                FULL_NAME_UPDATED,
                PHONE_NUMBER_UPDATED,
                CPF
        );
    }

    public static UserResponseDTO userResponse() {
        return new UserResponseDTO(
                USER_ID,
                USERNAME,
                EMAIL,
                FULL_NAME,
                UserStatus.ACTIVE,
                UserRole.STUDENT,
                PHONE_NUMBER,
                CPF,
                IMAGE_URL,
                FIXED_NOW,
                FIXED_NOW
        );
    }

    public static UserResponseDTO updatedUserResponse() {
        return new UserResponseDTO(
                USER_ID,
                USERNAME,
                EMAIL,
                FULL_NAME_UPDATED,
                UserStatus.ACTIVE,
                UserRole.STUDENT,
                PHONE_NUMBER_UPDATED,
                CPF,
                IMAGE_URL,
                FIXED_NOW,
                FIXED_NOW
        );
    }

    public static User userEntity() {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", USER_ID);
        ReflectionTestUtils.setField(user, "createdAt", FIXED_NOW);
        ReflectionTestUtils.setField(user,   "updatedAt", FIXED_NOW);
        user.setUserName(USERNAME);
        user.setEmail(EMAIL);
        user.setPassword("$2a$10$hashedpassword");
        user.setFullName(FULL_NAME);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserRole(UserRole.STUDENT);
        user.setPhoneNumber(PHONE_NUMBER);
        user.setCpf(CPF);
        user.setImageUrl(IMAGE_URL);
        return user;
    }
}
