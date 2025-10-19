package com.tailan.gestao.de.projetos.application.dto.task;

import com.tailan.gestao.de.projetos.core.enums.TaskPriority;
import com.tailan.gestao.de.projetos.core.enums.TaskStatus;

import java.util.UUID;

public record TaskResponseDTO(UUID id,
                              String title,
                              String description,
                              TaskStatus status,
                              TaskPriority priority,
                              UUID assigneeId,
                              String memberEmail,
                              UUID projectId) {
}
