package com.tailan.gestao.de.projetos.application.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


public record UpdateUserDTO(
        @Size(min = 2, max = 120, message = "O nome, se atualizado, deve ter entre 2 e 120 caracteres.")
        String name,

        @Email(message = "Formato de email inválido.")
        String email
) {}