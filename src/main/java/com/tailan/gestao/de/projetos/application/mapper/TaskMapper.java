package com.tailan.gestao.de.projetos.application.mapper;

import com.tailan.gestao.de.projetos.application.dto.task.CreateTaskDTO;
import com.tailan.gestao.de.projetos.application.dto.task.TaskResponseDTO;
import com.tailan.gestao.de.projetos.core.model.project.Project;
import com.tailan.gestao.de.projetos.core.model.task.Task;
import com.tailan.gestao.de.projetos.core.model.user.User;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TaskMapper {
    public Task toEntity(CreateTaskDTO createTaskDTO, User user, Project project) {
        return new Task(project, user, createTaskDTO.title(), createTaskDTO.description(), createTaskDTO.priority());
    }

    public TaskResponseDTO toResponseDTO(Task task) {
        UUID assigneId = task.getAssignee() != null ? task.getAssignee().getId() : null;
        String emailAssign = task.getAssignee() != null ? task.getAssignee().getEmail() : null;
        return new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getPriority(), assigneId, emailAssign, task.getProject().getId());
    }
}
