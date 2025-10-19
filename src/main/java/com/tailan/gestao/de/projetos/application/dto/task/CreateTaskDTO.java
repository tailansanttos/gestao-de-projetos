package com.tailan.gestao.de.projetos.application.dto.task;

import com.tailan.gestao.de.projetos.core.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTaskDTO(
        @NotNull(message = "O projeto é obrigatório")
        UUID projectId,

        @NotBlank(message = "O título não pode estar vazio")
        String title,

        @NotBlank(message = "A descrição não pode estar vazia")
        String description,

        @NotNull(message = "A prioridade é obrigatória")
        TaskPriority priority
) {}
