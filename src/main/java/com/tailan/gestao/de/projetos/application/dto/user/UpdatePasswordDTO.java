package com.tailan.gestao.de.projetos.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordDTO(
        @NotBlank(message = "A senha antiga é obrigatória.")
        String oldPassword,

        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(min = 8, max = 100, message = "A nova senha deve ter entre 8 e 100 caracteres.")
        String newPassword
) {}