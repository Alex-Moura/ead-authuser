package br.com.ead.authuser.controller;


import br.com.ead.authuser.config.security.SecurityConfig;
import br.com.ead.authuser.exceptions.custom.ConflictException;
import br.com.ead.authuser.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.ead.authuser.common.TestFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @Nested
    @DisplayName("POST /auth/signup")
    class Signup{

        @Test
        @DisplayName("Should return status 201 and the created user DTO when the request is valid")
        void shouldReturn201WhenRequestIsValid() throws Exception{
            when(authService.register(any())).thenReturn(userResponse());

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(USER_ID.toString()))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.userName").value(USERNAME))
                    .andExpect(jsonPath("$.userRole").value("STUDENT"))
                    .andExpect(jsonPath("$.userStatus").value("ACTIVE"));

        }


        @Test
        @DisplayName("The response must not expose the password")
        void shouldNotExposePasswordInResponse() throws Exception {
            when(authService.register(any())).thenReturn(userResponse());

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.password").doesNotExist());
        }

        @Test
        @DisplayName("Should return 409 when the email is already registered")
        void shouldReturn409WhenEmailAlreadyRegistered() throws Exception {
            when(authService.register(any()))
                    .thenThrow(new ConflictException("Email já cadastrado"));

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRequest())))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.message").value("Email já cadastrado"));
        }

        @Test
        @DisplayName("Should return 409 when the username is already registered")
        void shouldReturn409WhenUsernameAlreadyRegistered() throws Exception {
            when(authService.register(any()))
                    .thenThrow(new ConflictException("Username já cadastrado"));

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRequest())))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.message").value("Username já cadastrado"));
        }

        @Test
        @DisplayName("Should return 400 when the username is blank")
        void shouldReturn400WhenUsernameIsBlank() throws Exception {
            String body = """
                {
                  "userName": "",
                  "email": "joao@email.com",
                  "password": "Senha123",
                  "fullName": "João da Silva",
                  "phoneNumber": "11987654321",
                  "cpf": "529.982.247-25"
                }
                """;

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields.userName").exists());
        }

        @Test
        @DisplayName("Should return 400 when the email is invalid")
        void shouldReturn400WhenEmailIsInvalid() throws Exception {
            String body = """
                {
                  "userName": "joaosilva",
                  "email": "nao-e-um-email",
                  "password": "Senha123",
                  "fullName": "João da Silva",
                  "phoneNumber": "11987654321",
                  "cpf": "529.982.247-25"
                }
                """;

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields.email").exists());
        }

        @Test
        @DisplayName("Should return 400 when the password does not meet the policy (no uppercase letter)")
        void shouldReturn400WhenPasswordHasNoUppercaseLetter() throws Exception {
            String body = """
                {
                  "userName": "joaosilva",
                  "email": "joao@email.com",
                  "password": "senha123",
                  "fullName": "João da Silva",
                  "phoneNumber": "11987654321",
                  "cpf": "529.982.247-25"
                }
                """;

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields.password").exists());
        }

        @Test
        @DisplayName("Should return 400 when the password does not meet the policy (no number)")
        void shouldReturn400WhenPasswordHasNoNumber() throws Exception {
            String body = """
                {
                  "userName": "joaosilva",
                  "email": "joao@email.com",
                  "password": "SenhaSemNumero",
                  "fullName": "João da Silva",
                  "phoneNumber": "11987654321",
                  "cpf": "529.982.247-25"
                }
                """;

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields.password").exists());
        }

        @Test
        @DisplayName("Should return 400 when the CPF is invalid")
        void shouldReturn400WhenCpfIsInvalid() throws Exception {
            String body = """
                {
                  "userName": "joaosilva",
                  "email": "joao@email.com",
                  "password": "Senha123",
                  "fullName": "João da Silva",
                  "phoneNumber": "11987654321",
                  "cpf": "000.000.000-00"
                }
                """;

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields.cpf").exists());
        }
        @Test
        @DisplayName("Should return 400 when the phone number has an invalid format (letters)")
        void shouldReturn400WhenPhoneNumberContainsLetters() throws Exception {
            String body = """
                {
                  "userName": "joaosilva",
                  "email": "joao@email.com",
                  "password": "Senha123",
                  "fullName": "João da Silva",
                  "phoneNumber": "119abc54321",
                  "cpf": "529.982.247-25"
                }
                """;

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields.phoneNumber").exists());
        }

        @Test
        @DisplayName("Should accept a request without imageUrl the field is optional")
        void shouldAcceptRequestWithoutOptionalImageUrl() throws Exception {
            when(authService.register(any())).thenReturn(userResponse());

            String body = """
                {
                  "userName": "joaosilva",
                  "email": "joao@email.com",
                  "password": "Senha123",
                  "fullName": "João da Silva",
                  "phoneNumber": "11987654321",
                  "cpf": "529.982.247-25"
                }
                """;

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());
        }
        @Test
        @DisplayName("Should return 400 when the body is completely empty")
        void shouldReturn400WhenBodyIsCompletelyEmpty() throws Exception {
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields").isMap());
        }

        @Test
        @DisplayName("Should return 400 when the username is too short (< 3 chars)")
        void shouldReturn400WhenUsernameIsTooShort() throws Exception {
            String body = """
                {
                  "userName": "jo",
                  "email": "joao@email.com",
                  "password": "Senha123",
                  "fullName": "João da Silva",
                  "phoneNumber": "11987654321",
                  "cpf": "529.982.247-25"
                }
                """;

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields.userName").exists());
        }
        @Test
        @DisplayName("Should return 400 when the fullname is too short (< 3 chars)")
        void shouldReturn400WhenFullNameIsTooShort() throws Exception {
            String body = """
                    {
                      "userName": "joaosilva",
                      "email": "joao@email.com",
                      "password": "Senha123",
                      "fullName": "Jo",
                      "phoneNumber": "11987654321",
                      "cpf": "529.982.247-25"
                    }
                    """;

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields.fullName").exists());
        }
    }
}