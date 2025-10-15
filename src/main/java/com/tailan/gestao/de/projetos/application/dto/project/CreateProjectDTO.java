package com.tailan.gestao.de.projetos.application.dto.project;

import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectDTO(String name, String description, LocalDate startDate, UUID ownerId) {
}
