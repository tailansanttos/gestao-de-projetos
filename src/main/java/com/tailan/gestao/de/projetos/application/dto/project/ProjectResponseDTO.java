package com.tailan.gestao.de.projetos.application.dto.project;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProjectResponseDTO(UUID projectId,
                                 String name,
                                 String description,
                                 LocalDate endDate,
                                 LocalDate createdAt) {
}
