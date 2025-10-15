package com.tailan.gestao.de.projetos.application.dto.project;

import com.tailan.gestao.de.projetos.core.enums.ProjectRole;

import java.util.UUID;

public record AddMemberDTO(UUID userId, ProjectRole role, UUID projectId) {
}
