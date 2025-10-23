package com.tailan.gestao.de.projetos.application.service.project.impl;

import com.tailan.gestao.de.projetos.application.dto.member.ProjectMemberDTO;
import com.tailan.gestao.de.projetos.application.dto.project.*;
import com.tailan.gestao.de.projetos.application.mapper.ProjectMapper;
import com.tailan.gestao.de.projetos.application.service.user.UserService;
import com.tailan.gestao.de.projetos.core.enums.ProjectRole;
import com.tailan.gestao.de.projetos.core.model.project.Project;
import com.tailan.gestao.de.projetos.core.model.projectMember.ProjectMember;
import com.tailan.gestao.de.projetos.core.model.user.User;
import com.tailan.gestao.de.projetos.core.repository.ProjectRepository;
import com.tailan.gestao.de.projetos.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {
    @Mock
    private ProjectRepository repositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private ProjectMapper projectMapperMock;

    @InjectMocks
    private ProjectServiceImpl projectService;

    CreateProjectDTO createProjectDTO;
    User usuarioAutenticado;
    UUID userId;
    @BeforeEach
    public void setUp(){
        createProjectDTO = new CreateProjectDTO("Novo Projeto", "Descrição do projeto", LocalDate.now());
        usuarioAutenticado = new User("Teste", "teste@email.com", "abc");
        userId = UUID.randomUUID();
        usuarioAutenticado.setId(userId);

        // Configurar o SecurityContext para o usuário autenticado
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        when(customUserDetails.getUser()).thenReturn(usuarioAutenticado);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    public void createProjectTest(){
        Project project = mock(Project.class);
        Project savedProject = mock(Project.class);
        ProjectResponseDTO projectResponseDTOMock= mock(ProjectResponseDTO.class);

        when(projectMapperMock.toEntity(eq(createProjectDTO), eq(userId))).thenReturn(project);
        when(repositoryMock.save(project)).thenReturn(savedProject);
        when(projectMapperMock.toResponse(savedProject)).thenReturn(projectResponseDTOMock);

        ProjectResponseDTO responseDTO = projectService.createProject(createProjectDTO);

        assertNotNull(responseDTO);
        assertEquals(projectResponseDTOMock, responseDTO);

        verify(projectMapperMock, times(1)).toEntity(createProjectDTO, userId);
        verify(repositoryMock, times(1)).save(project);
        verify(projectMapperMock, times(1)).toResponse(savedProject);
        verify(project, times(1)).setEndDate(any(LocalDate.class));
        verify(project, times(1)).addMember(usuarioAutenticado, ProjectRole.OWNER);
    }
    @Test
    public void addMemberToProjectTest(){
        UUID projectId = UUID.randomUUID();
        Project project = new Project("Novo Projeto", "Descrição do projeto", LocalDate.now(), userId);
        project.setId(projectId);

        project.addMember(usuarioAutenticado, ProjectRole.ADMIN);

        User newUser = new User("Novo Usuário", "novo@email.com", "senha123");
        UUID newUserId = UUID.randomUUID();
        newUser.setId(newUserId);

        when(repositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(userServiceMock.getUserById(newUserId)).thenReturn(newUser);
        when(repositoryMock.save(any(Project.class))).thenReturn(project);

        AddMemberDTO addMemberDTO = new AddMemberDTO(newUserId, ProjectRole.MEMBER, projectId);

        projectService.addMemberToProject(addMemberDTO);

        verify(repositoryMock).findById(projectId);
        verify(userServiceMock).getUserById(newUserId);
        verify(repositoryMock).save(project);
        assertEquals(2, project.getMembers().size());
    }

    @Test
    public void removeMemberToProjectTest(){
        UUID projectId = UUID.randomUUID();
        Project project = new Project("Novo Projeto", "Descrição do projeto", LocalDate.now(), userId);
        project.setId(projectId);

        project.addMember(usuarioAutenticado, ProjectRole.ADMIN);

        RemoveMemberDTO removeMemberDTO = new RemoveMemberDTO(userId, projectId);

        when(repositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(repositoryMock.save(any(Project.class))).thenReturn(project);

        projectService.removeMemberToProject(removeMemberDTO);

        verify(repositoryMock).findById(projectId);
        verify(repositoryMock).save(project);
        assertFalse(project.getMembers().stream().anyMatch(m -> m.getUser().getId().equals(userId)));
    }

    @Test
    public void updateProjectTest(){
        UUID projectId = UUID.randomUUID();
        Project project = new Project("Novo Projeto", "Descrição do projeto", LocalDate.now(), userId);
        project.setId(projectId);

        project.addMember(usuarioAutenticado, ProjectRole.ADMIN);

        UpdateProjectDto updateProjectDto = new UpdateProjectDto("Projeto Atualizado", "Descrição Alterada", LocalDate.now());

        when(repositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(repositoryMock.save(any(Project.class))).thenReturn(project);

        projectService.updateProject(projectId, updateProjectDto);

        verify(repositoryMock).findById(projectId);
        verify(repositoryMock).save(project);
        assertEquals("Projeto Atualizado", project.getName());
    }

    @Test
    public void getProjectByOwnerTest(){
        Project project = new Project("Novo Projeto", "Descrição do projeto", LocalDate.now(), userId);
        Project project2 = new Project("Projeto 2", "Descrição do projeto", LocalDate.now(), userId);

        List<Project> projects = Arrays.asList(project, project2);

        when(repositoryMock.findAllByOwnerId(userId)).thenReturn(projects);

        ProjectResponseDTO dto1 = mock(ProjectResponseDTO.class);
        ProjectResponseDTO dto2 = mock(ProjectResponseDTO.class);
        when(projectMapperMock.toResponse(project)).thenReturn(dto1);
        when(projectMapperMock.toResponse(project2)).thenReturn(dto2);

        List<ProjectResponseDTO> result = projectService.getProjectsByOwner(userId);

        verify(repositoryMock, times(1)).findAllByOwnerId(userId);
        verify(projectMapperMock, times(1)).toResponse(project);
        verify(projectMapperMock, times(1)).toResponse(project2);
        assertEquals(2, result.size());
    }

    @Test
    public void getProjectMembersTest(){
        UUID projectId = UUID.randomUUID();
        Project project = new Project("Novo Projeto", "Descrição do projeto", LocalDate.now(), userId);

        project.addMember(usuarioAutenticado, ProjectRole.MEMBER);

        when(repositoryMock.findById(projectId)).thenReturn(Optional.of(project));

        ProjectMemberDTO projectMemberDTO = new ProjectMemberDTO(userId, ProjectRole.MEMBER, usuarioAutenticado.getCreatedAt());

        List<ProjectMemberDTO> result = projectService.getProjectMembers(projectId);

        verify(repositoryMock).findById(projectId);
        assertNotNull(result);
        assertEquals(1, result.size());
        // Dando falha
        assertEquals(projectMemberDTO, result.get(0));
    }
}
