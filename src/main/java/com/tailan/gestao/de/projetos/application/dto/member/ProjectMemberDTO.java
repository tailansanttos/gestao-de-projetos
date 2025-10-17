package com.tailan.gestao.de.projetos.application.dto.member;

import com.tailan.gestao.de.projetos.core.enums.ProjectRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProjectMemberDTO(UUID memberId, ProjectRole projectRole, OffsetDateTime createdAt) {
}
