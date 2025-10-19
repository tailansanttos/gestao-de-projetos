package com.tailan.gestao.de.projetos.application.dto.task;

import com.tailan.gestao.de.projetos.core.enums.TaskPriority;
import com.tailan.gestao.de.projetos.core.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

public record UpdateTaskDTO(
        @NotBlank(message = "O título não pode estar vazio")
        String title,

        @NotBlank(message = "A descrição não pode estar vazia")
        String description,

        @NotNull(message = "O status é obrigatório")
        TaskStatus status,

        @NotNull(message = "A prioridade é obrigatória")
        TaskPriority priority,

        @FutureOrPresent(message = "A data de vencimento deve ser hoje ou no futuro")
        LocalDate dueDate
) {}
