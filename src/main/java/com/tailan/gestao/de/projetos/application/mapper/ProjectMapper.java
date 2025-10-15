package com.tailan.gestao.de.projetos.application.mapper;

import com.tailan.gestao.de.projetos.application.dto.project.CreateProjectDTO;
import com.tailan.gestao.de.projetos.application.dto.project.ProjectResponseDTO;
import com.tailan.gestao.de.projetos.core.model.project.Project;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class ProjectMapper {
    public Project toEntity(CreateProjectDTO createProjectDTO){
        return new Project(createProjectDTO.name(),
                createProjectDTO.description(),
                createProjectDTO.startDate(),
                createProjectDTO.ownerId());
    }

    public ProjectResponseDTO toResponse(Project project){
        return new ProjectResponseDTO(project.getId(),
                project.getName(),
                project.getDescription(),
                project.getEndDate(),
                project.getStartDate());
    }

}
