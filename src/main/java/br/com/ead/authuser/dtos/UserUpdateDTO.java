package br.com.ead.authuser.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record UserUpdateDTO(

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String fullName,

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(
            regexp = "^\\d{10,11}$",
            message = "Telefone deve conter 10 ou 11 dígitos (somente números)"
    )
    String phoneNumber,

    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    String cpf
) {
}
