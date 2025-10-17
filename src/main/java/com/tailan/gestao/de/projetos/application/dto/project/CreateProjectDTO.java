package com.tailan.gestao.de.projetos.application.dto.project;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateProjectDTO(
        @NotBlank(message = "O nome do projeto é obrigatório.")
        @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres.")
        String name,

        @Size(max = 2000, message = "A descrição não pode exceder 2000 caracteres.")
        String description, // Descrição é opcional

        @NotNull(message = "A data de início é obrigatória.")
        @FutureOrPresent(message = "A data de início não pode ser no passado.")
        LocalDate startDate
) {}