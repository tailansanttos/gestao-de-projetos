package com.tailan.gestao.de.projetos.application.dto.project;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RemoveMemberDTO(
        @NotNull(message = "ID do usuário é necessário para remover.")
        UUID userId,

        @NotNull(message = "ID do projeto é necessário.")
        UUID projectId
) {}