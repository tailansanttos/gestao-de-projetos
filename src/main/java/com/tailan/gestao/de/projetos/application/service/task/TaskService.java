package com.tailan.gestao.de.projetos.application.service.task;

import com.tailan.gestao.de.projetos.application.dto.task.AssignTaskToMember;
import com.tailan.gestao.de.projetos.application.dto.task.CreateTaskDTO;
import com.tailan.gestao.de.projetos.application.dto.task.TaskResponseDTO;
import com.tailan.gestao.de.projetos.application.dto.task.UpdateTaskDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    public TaskResponseDTO createTaskToProject(CreateTaskDTO createTaskDTO);
    public TaskResponseDTO  updateTaskToProject(UUID taskId, UpdateTaskDTO updateTaskDTO);

    public TaskResponseDTO assignTaskToMember(UUID projectId, AssignTaskToMember  assignTaskToMemberDTO);


    void markDone(UUID taskId);

    public void markTodo(UUID taskId);

    void deleteTaskToProject(UUID taskId);


    Page<TaskResponseDTO> getTasksToProject(UUID projectId, int page, int size);

    TaskResponseDTO getTaskToProject(UUID taskId);

}
