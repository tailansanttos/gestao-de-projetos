package com.tailan.gestao.de.projetos.application.service.task.impl;

import com.tailan.gestao.de.projetos.application.dto.task.AssignTaskToMember;
import com.tailan.gestao.de.projetos.application.dto.task.CreateTaskDTO;
import com.tailan.gestao.de.projetos.application.dto.task.TaskResponseDTO;
import com.tailan.gestao.de.projetos.application.dto.task.UpdateTaskDTO;
import com.tailan.gestao.de.projetos.application.mapper.TaskMapper;
import com.tailan.gestao.de.projetos.application.service.project.ProjectService;
import com.tailan.gestao.de.projetos.application.service.task.TaskService;
import com.tailan.gestao.de.projetos.application.service.user.UserService;
import com.tailan.gestao.de.projetos.core.enums.ProjectRole;
import com.tailan.gestao.de.projetos.core.enums.TaskStatus;
import com.tailan.gestao.de.projetos.core.model.project.Project;
import com.tailan.gestao.de.projetos.core.model.projectMember.ProjectMember;
import com.tailan.gestao.de.projetos.core.model.task.Task;
import com.tailan.gestao.de.projetos.core.model.user.User;
import com.tailan.gestao.de.projetos.core.repository.TaskRepository;
import com.tailan.gestao.de.projetos.infrastructure.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository  taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final ProjectService projectService;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, UserService userService, ProjectService projectService) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userService = userService;
        this.projectService = projectService;
    }

    private User getUserByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||!(authentication.getPrincipal() instanceof CustomUserDetails)) throw new IllegalArgumentException("Usuário não autenticado");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUser();
    }
    @Override
    public TaskResponseDTO createTaskToProject(CreateTaskDTO createTaskDTO) {
        // Pegar o usuario autenticado (OWNER OU ADMIN) e SETAR COMO CRIADOR DA TASK
        // BUSCAR O MEMBRO DO PROJETO PELO PROJETO, E SETAR A TASK NELE

        User ownerOrAdmin = getUserByAuthentication();
        Project project = projectService.getProject(createTaskDTO.projectId());

        boolean permitAddTask = projectService.isOwnerOrAdmin(ownerOrAdmin, project);

        if(!permitAddTask){
            throw new IllegalArgumentException("Você não tem permissão para atribuir uma Task. Somente admin ou owner.");
        }
        Task task = taskMapper.toEntity(createTaskDTO, ownerOrAdmin, project);

        project.addTask(task);

        //TALVEZ NAO VÁ SALVAR NO PROJETO A TASK, QUALQUER COISA LIST.OF(TASK) NO SET TASK
        Task taskSaved = taskRepository.save(task);
        return taskMapper.toResponseDTO(taskSaved);

    }



    @Override
    public TaskResponseDTO updateTaskToProject(UUID taskId, UpdateTaskDTO updateTaskDTO) {
        User user = getUserByAuthentication();
        Task task = getTask(taskId);
        Project project = task.getProject();

        boolean isProjectMember = projectService.isProjectMember(user, project);

        if(!isProjectMember){
            throw new IllegalArgumentException("Você não tem permissão para alterar o status dessa tarefa.");
        }

        ProjectMember member = project.getMemberToUser(user.getId());

        if (member.getProjectRole() == ProjectRole.VIEWER) {
            throw new IllegalArgumentException("Você não tem permissão para alterar uma tarefa. Somente membros, admin ou dono do projeto.");
        }

        task.updateDetails(updateTaskDTO.title(), updateTaskDTO.description(), updateTaskDTO.priority(), updateTaskDTO.dueDate());
        Task taskSaved = taskRepository.save(task);
        return taskMapper.toResponseDTO(taskSaved);

    }

    @Override
    public TaskResponseDTO assignTaskToMember(UUID projectId, AssignTaskToMember assignTaskToMemberDTO) {
        User ownerOrAdmin = getUserByAuthentication();
        Project project = projectService.getProject(projectId);

        boolean isOwnerOrAdmin = projectService.isOwnerOrAdmin(ownerOrAdmin, project);

        if(!isOwnerOrAdmin){
            throw new IllegalArgumentException("Você não tem permissão para atribuir uma Task. Somente admin ou owner.");
        }

        ProjectMember member = getMemberToProject(project.getId(), assignTaskToMemberDTO.memberId());
        User memberUser = member.getUser();
        Task task = getTask(assignTaskToMemberDTO.taskId());
        task.assignTo(memberUser);

        Task savedTask = taskRepository.save(task);

        return taskMapper.toResponseDTO(savedTask);

    }

    private Task getTask(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task não encontrado"));
    }

    @Override
    public void markDone(UUID taskId) {
        User user = getUserByAuthentication();
        Task task = getTask(taskId);
        Project project = task.getProject();

        boolean isProjectMember = projectService.isProjectMember(user, project);
        if(!isProjectMember){
            throw new IllegalArgumentException("Você não tem permissão para alterar o status dessa tarefa.");
        }

        task.markDone();
        task.setUpdatedAt(OffsetDateTime.now());
        taskRepository.save(task);
    }

    @Override
    public void markTodo(UUID taskId) {
        User user = getUserByAuthentication();
        Task task = getTask(taskId);
        Project project = task.getProject();

        boolean isProjectMember = projectService.isProjectMember(user, project);
        if(!isProjectMember){
            throw new IllegalArgumentException("Você não tem permissão para alterar o status dessa tarefa.");
        }
        if (task.getStatus() == TaskStatus.DONE) {
            throw new IllegalArgumentException("Tarefa já concluida.");
        }

        task.markTodo();
        task.setUpdatedAt(OffsetDateTime.now());
        taskRepository.save(task);
    }

    @Override
    public void deleteTaskToProject(UUID taskId) {
        User user = getUserByAuthentication();
        Task task = getTask(taskId);
        Project project = task.getProject();

        boolean permitDelete = projectService.isOwnerOrAdmin(user, project);
        if (!permitDelete) {
            throw new IllegalArgumentException("Você não pode deletar essa tarefa. Somente admin ou owner.");
        }

        project.removeTask(task);
        taskRepository.delete(task);
    }

    @Override
    public Page<TaskResponseDTO> getTasksToProject(UUID projectId, int page, int size) {
        User user = getUserByAuthentication();
        //QUANDO LISTAR AS TASKS, ORDENAR POR PRIORITY
        Project project = projectService.getProject(projectId);
        boolean isProjectMember = projectService.isProjectMember(user, project);

        if(!isProjectMember){
            throw new IllegalArgumentException("Você não tem permissão para acessar as tarefas desse projeto.");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasksToProject = taskRepository.findAllByProjectOrderByPriorityAsc(project, pageable);

        List<TaskResponseDTO> tasksDto = tasksToProject.stream().map(taskMapper::toResponseDTO).collect(Collectors.toList());

        return new PageImpl<>(
                tasksDto,
                pageable,
                tasksToProject.getTotalElements()
        );
    }



    @Override
    public TaskResponseDTO getTaskToProject(UUID taskId) {
        User user = getUserByAuthentication();
        Task task = getTask(taskId);
        Project project = task.getProject();
        boolean isProjectMember = projectService.isProjectMember(user, project);
        if(!isProjectMember){
            throw new IllegalArgumentException("Você não tem permissão para acessar as tarefas desse projeto.");
        }

        return taskMapper.toResponseDTO(task);

    }


    private ProjectMember getMemberToProject(UUID projectId, UUID memberId) {

        Project project = projectService.getProject(projectId);
        ProjectMember member = project.getMember(memberId);

        return member;

    }
}
