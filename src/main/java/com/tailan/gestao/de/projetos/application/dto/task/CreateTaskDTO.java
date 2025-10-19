package com.tailan.gestao.de.projetos.application.dto.task;

import com.tailan.gestao.de.projetos.core.enums.TaskPriority;

import java.util.UUID;

public record CreateTaskDTO(UUID projectId,
                            String title,
                            String description,
                            TaskPriority priority) {
}
