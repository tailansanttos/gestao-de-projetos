package com.tailan.gestao.de.projetos.application.dto.auth;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record LoginResponseDTO(String token, String userOwnerTokenEmail) {
}
