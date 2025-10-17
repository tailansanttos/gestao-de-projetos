package com.tailan.gestao.de.projetos.application.dto.project;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateProjectDto(
        @Size(min = 3, max = 120, message = "O nome, se atualizado, deve ter entre 3 e 120 caracteres.")
        String name,

        @Size(max = 2000, message = "A descrição, se atualizada, não pode exceder 2000 caracteres.")
        String description,

        @FutureOrPresent(message = "A data de término, se atualizada, não pode ser no passado.")
        LocalDate endDate
) {}