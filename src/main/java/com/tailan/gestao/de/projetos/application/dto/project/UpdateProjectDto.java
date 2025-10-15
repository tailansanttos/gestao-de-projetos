package com.tailan.gestao.de.projetos.application.dto.project;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateProjectDto(UUID projectId, String name, String description, LocalDate endDate) {
}
