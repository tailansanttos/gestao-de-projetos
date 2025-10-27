package com.tailan.gestao.de.projetos.application.service.task.impl;

import com.tailan.gestao.de.projetos.application.dto.task.AssignTaskToMember;
import com.tailan.gestao.de.projetos.application.dto.task.CreateTaskDTO;
import com.tailan.gestao.de.projetos.application.dto.task.TaskResponseDTO;
import com.tailan.gestao.de.projetos.application.dto.task.UpdateTaskDTO;
import com.tailan.gestao.de.projetos.application.mapper.TaskMapper;
import com.tailan.gestao.de.projetos.application.service.project.ProjectService;
import com.tailan.gestao.de.projetos.core.enums.TaskPriority;
import com.tailan.gestao.de.projetos.core.enums.TaskStatus;
import com.tailan.gestao.de.projetos.core.model.project.Project;
import com.tailan.gestao.de.projetos.core.model.projectMember.ProjectMember;
import com.tailan.gestao.de.projetos.core.model.task.Task;
import com.tailan.gestao.de.projetos.core.model.user.User;
import com.tailan.gestao.de.projetos.core.repository.TaskRepository;
import com.tailan.gestao.de.projetos.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TaskServiceImplTest {

    @Mock private TaskRepository taskRepository;
    @Mock private TaskMapper taskMapper;
    @Mock private ProjectService projectService;

    @InjectMocks private TaskServiceImpl taskService;

    private User usuarioAutenticado;
    private Project project;
    private UUID userId;
    private UUID projectId;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        usuarioAutenticado = mock(User.class);
        lenient().when(usuarioAutenticado.getId()).thenReturn(userId);

        project = mock(Project.class);
        lenient().when(project.getId()).thenReturn(projectId);

        // Configura o SecurityContext
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        lenient().when(customUserDetails.getUser()).thenReturn(usuarioAutenticado);

        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(customUserDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void createTaskToProject_success() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO(projectId, "Implementar login", "Criar a tela de login e autenticação de usuários", TaskPriority.LOW);

        Task task = mock(Task.class);
        Task savedTask = mock(Task.class);
        TaskResponseDTO responseDTO = mock(TaskResponseDTO.class);

        when(projectService.getProject(projectId)).thenReturn(project);
        when(projectService.isOwnerOrAdmin(usuarioAutenticado, project)).thenReturn(true);
        when(taskMapper.toEntity(createTaskDTO, usuarioAutenticado, project)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskMapper.toResponseDTO(savedTask)).thenReturn(responseDTO);

        TaskResponseDTO result = taskService.createTaskToProject(createTaskDTO);

        assertEquals(responseDTO, result);
        verify(project).addTask(task);
        verify(taskRepository).save(task);
    }

    @Test
    public void createTaskToProject_noPermission_throwsException() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO(projectId, "Criar nova feature restrita", "Tentar criar uma tarefa em projeto sem permissão de administrador", TaskPriority.LOW);
        when(projectService.getProject(projectId)).thenReturn(project);
        when(projectService.isOwnerOrAdmin(usuarioAutenticado, project)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> taskService.createTaskToProject(createTaskDTO));
    }

    @Test
    public void assignTaskToMember_success() {
        UUID memberId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        AssignTaskToMember assignDTO = new AssignTaskToMember(taskId, memberId);

        Task task = mock(Task.class);
        ProjectMember member = mock(ProjectMember.class);
        User memberUser = mock(User.class);

        when(projectService.getProject(projectId)).thenReturn(project);
        when(projectService.isOwnerOrAdmin(usuarioAutenticado, project)).thenReturn(true);
        when(project.getMember(memberId)).thenReturn(member);
        when(member.getUser()).thenReturn(memberUser);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponseDTO(task)).thenReturn(mock(TaskResponseDTO.class));
        when(task.getTitle()).thenReturn("Implementar login");
        when(task.getDescription()).thenReturn("Criar a tela de login e autenticação de usuários");

        TaskResponseDTO result = taskService.assignTaskToMember(projectId, assignDTO);

        assertNotNull(result);
        verify(task).assignTo(memberUser);
    }

    @Test
    public void assignTaskToMember_noPermission_throwsException() {
        AssignTaskToMember assignDTO = new AssignTaskToMember(UUID.randomUUID(), UUID.randomUUID());
        when(projectService.getProject(projectId)).thenReturn(project);
        when(projectService.isOwnerOrAdmin(usuarioAutenticado, project)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> taskService.assignTaskToMember(projectId, assignDTO));
    }

    @Test
    public void markDone_success() {
        UUID taskId = UUID.randomUUID();
        Task task = mock(Task.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(task.getProject()).thenReturn(project);
        when(projectService.isProjectMember(usuarioAutenticado, project)).thenReturn(true);
        when(task.getTitle()).thenReturn("Concluir reunião de planejamento");
        when(task.getDescription()).thenReturn("Registrar que a reunião de planejamento foi finalizada e todas as pautas discutidas");

        taskService.markDone(taskId);

        verify(task).markDone();
        verify(task).setUpdatedAt(any(OffsetDateTime.class));
        verify(taskRepository).save(task);
    }

    @Test
    public void markTodo_success() {
        UUID taskId = UUID.randomUUID();
        Task task = mock(Task.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(task.getProject()).thenReturn(project);
        when(projectService.isProjectMember(usuarioAutenticado, project)).thenReturn(true);
        when(task.getStatus()).thenReturn(TaskStatus.TODO);
        when(task.getTitle()).thenReturn("Preparar relatório semanal");
        when(task.getDescription()).thenReturn("Criar o relatório semanal de atividades da equipe e definir tarefas pendentes");

        taskService.markTodo(taskId);

        verify(task).markTodo();
        verify(task).setUpdatedAt(any(OffsetDateTime.class));
        verify(taskRepository).save(task);
    }

    @Test
    public void markTodo_alreadyDone_throwsException() {
        UUID taskId = UUID.randomUUID();
        Task task = mock(Task.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(task.getProject()).thenReturn(project);
        when(projectService.isProjectMember(usuarioAutenticado, project)).thenReturn(true);
        when(task.getStatus()).thenReturn(TaskStatus.DONE);

        assertThrows(IllegalArgumentException.class,
                () -> taskService.markTodo(taskId));
    }

    @Test
    public void deleteTaskToProject_success() {
        UUID taskId = UUID.randomUUID();
        Task task = mock(Task.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(task.getProject()).thenReturn(project);
        when(projectService.isOwnerOrAdmin(usuarioAutenticado, project)).thenReturn(true);
        when(task.getTitle()).thenReturn("Remover usuário inativo");
        when(task.getDescription()).thenReturn("Excluir usuários que não acessaram o sistema há mais de 6 meses");

        taskService.deleteTaskToProject(taskId);

        verify(project).removeTask(task);
        verify(taskRepository).delete(task);
    }

    @Test
    public void deleteTaskToProject_noPermission_throwsException() {
        UUID taskId = UUID.randomUUID();
        Task task = mock(Task.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(task.getProject()).thenReturn(project);
        when(projectService.isOwnerOrAdmin(usuarioAutenticado, project)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> taskService.deleteTaskToProject(taskId));
    }

    @Test
    public void getTasksToProject_success() {
        Task task1 = mock(Task.class);
        when(task1.getTitle()).thenReturn("Atualizar documentação");
        when(task1.getDescription()).thenReturn("Adicionar novos endpoints na documentação");
        Task task2 = mock(Task.class);
        when(task2.getTitle()).thenReturn("Testar integração API");
        when(task2.getDescription()).thenReturn("Verificar se a API responde corretamente a todas as requisições");
        List<Task> taskList = Arrays.asList(task1, task2);
        Page<Task> taskPage = new PageImpl<>(taskList);

        when(projectService.getProject(projectId)).thenReturn(project);
        when(projectService.isProjectMember(usuarioAutenticado, project)).thenReturn(true);
        when(taskRepository.findAllByProjectOrderByPriorityAsc(eq(project), any(PageRequest.class))).thenReturn(taskPage);
        when(taskMapper.toResponseDTO(any(Task.class))).thenReturn(mock(TaskResponseDTO.class));

        Page<TaskResponseDTO> result = taskService.getTasksToProject(projectId, 0, 10);

        assertEquals(2, result.getContent().size());
        verify(taskMapper, times(2)).toResponseDTO(any(Task.class));
    }

    @Test
    public void getTaskToProject_success() {
        UUID taskId = UUID.randomUUID();
        Task task = mock(Task.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(task.getProject()).thenReturn(project);
        when(projectService.isProjectMember(usuarioAutenticado, project)).thenReturn(true);
        when(taskMapper.toResponseDTO(task)).thenReturn(mock(TaskResponseDTO.class));

        TaskResponseDTO result = taskService.getTaskToProject(taskId);

        assertNotNull(result);
    }
}
