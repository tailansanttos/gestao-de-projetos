package com.tailan.gestao.de.projetos.application.dto.project;

import java.util.UUID;

public record RemoveMemberDTO(UUID userId, UUID projectId) {
}
