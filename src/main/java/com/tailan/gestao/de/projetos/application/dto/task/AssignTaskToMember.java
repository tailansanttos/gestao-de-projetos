package com.tailan.gestao.de.projetos.application.dto.task;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignTaskToMember(
        @NotNull(message = "O ID da task é obrigatório")
        UUID taskId,

        @NotNull(message = "O ID do membro é obrigatório")
        UUID memberId
) {}
