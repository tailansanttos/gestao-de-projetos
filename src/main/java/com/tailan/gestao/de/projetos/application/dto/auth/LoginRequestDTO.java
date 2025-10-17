package com.tailan.gestao.de.projetos.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "O email é obrigatório.")
        @Email(message = "Formato de email inválido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String password
) {}