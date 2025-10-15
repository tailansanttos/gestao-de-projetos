package com.tailan.gestao.de.projetos.application.dto.user;

import java.time.OffsetDateTime;

public record UserResponseDTO(String name, String email, OffsetDateTime createdAt) {
}
