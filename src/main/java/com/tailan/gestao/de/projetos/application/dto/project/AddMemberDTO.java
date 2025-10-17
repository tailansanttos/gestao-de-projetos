package com.tailan.gestao.de.projetos.application.dto.project;

import com.tailan.gestao.de.projetos.core.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddMemberDTO(
        @NotNull(message = "O ID do usuário é obrigatório.")
        UUID userId,

        @NotNull(message = "O papel (role) do usuário é obrigatório.")
        ProjectRole role,

        @NotNull(message = "O ID do projeto é obrigatório.")
        UUID projectId
) {}