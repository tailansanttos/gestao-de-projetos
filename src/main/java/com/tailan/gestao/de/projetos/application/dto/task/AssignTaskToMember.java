package com.tailan.gestao.de.projetos.application.dto.task;

import java.util.UUID;

public record AssignTaskToMember(UUID taskId, UUID memberId) {
}
