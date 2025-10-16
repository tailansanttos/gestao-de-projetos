package com.tailan.gestao.de.projetos.application.dto.user;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponseDTO(UUID userId, String name, String email, OffsetDateTime createdAt) {
}
