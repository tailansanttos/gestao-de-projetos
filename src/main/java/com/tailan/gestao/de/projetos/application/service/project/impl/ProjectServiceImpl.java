package com.tailan.gestao.de.projetos.application.service.project.impl;

import com.tailan.gestao.de.projetos.application.dto.member.ProjectMemberDTO;
import com.tailan.gestao.de.projetos.application.dto.project.*;
import com.tailan.gestao.de.projetos.application.mapper.ProjectMapper;
import com.tailan.gestao.de.projetos.application.service.project.ProjectService;
import com.tailan.gestao.de.projetos.application.service.user.UserService;
import com.tailan.gestao.de.projetos.core.enums.ProjectRole;
import com.tailan.gestao.de.projetos.core.model.project.Project;
import com.tailan.gestao.de.projetos.core.model.projectMember.ProjectMember;
import com.tailan.gestao.de.projetos.core.model.user.User;
import com.tailan.gestao.de.projetos.core.repository.ProjectRepository;
import com.tailan.gestao.de.projetos.core.repository.UserRepository;
import com.tailan.gestao.de.projetos.infrastructure.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserService userService, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectMapper = projectMapper;
    }

    private User getUserByAuthentication(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||!(authentication.getPrincipal() instanceof CustomUserDetails)) throw new IllegalArgumentException("Usuário não autenticado");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        return customUserDetails.getUser();
    }

    private boolean isOwnerOrAdmin(User user, Project project) {
        return user.getId().equals(project.getOwnerId()) || project.isAdmin(user.getId());
    }

    public boolean enableUpdateOrAddOrRemoveMemberToProject(User user, Project project) {
        return isOwnerOrAdmin(user, project);
    }

    private boolean isOwner(User user, Project project) {
        return user.getId().equals(project.getOwnerId());
    }



    @Override
    public ProjectResponseDTO createProject(CreateProjectDTO createProjectRequest) {
        User user = getUserByAuthentication();

        //Pegar o ID do user autenticado que vai fazer a requisição e passar como OWNER ID

        Project project = projectMapper.toEntity(createProjectRequest, user.getId());
        project.setEndDate(LocalDate.now().plusDays(50));
        Project createdProject = projectRepository.save(project);

        return projectMapper.toResponse(createdProject);
    }

    @Override
    public void addMemberToProject(AddMemberDTO addMemberRequest) {
        User user = getUserByAuthentication();
        Project project = getProject(addMemberRequest.projectId());
        // Verificar se quem está tentando adicionar membro é o DONO OU ADMIN PELA ROLE

        boolean allowedToAddMember = enableUpdateOrAddOrRemoveMemberToProject(user, project);

        User userAddToMember = userService.getUserById(addMemberRequest.userId());

        project.addMember(userAddToMember, addMemberRequest.role());
        projectRepository.save(project);
    }


    @Override
    public void removeMemberToProject(RemoveMemberDTO removeMemberRequest) {
        User user = getUserByAuthentication();
        Project project = getProject(removeMemberRequest.projectId());

        boolean allowedToRemoveMember = enableUpdateOrAddOrRemoveMemberToProject(user, project);

        // Verificar se quem está tentando remover membro é o DONO OU ADMIN PELA ROLE

        project.removeMember(removeMemberRequest.userId());
        projectRepository.save(project);
    }

    @Override
    public ProjectResponseDTO updateProject(UUID projectId, UpdateProjectDto updateRequest) {
        User user = getUserByAuthentication();
        Project project = getProject(projectId);

        boolean enableUpdateProject = enableUpdateOrAddOrRemoveMemberToProject(user, project);
        if (!enableUpdateProject) throw new IllegalArgumentException("Você não pode atualizar detalhes do projeto.");

        project.updateDetails(updateRequest.name(), updateRequest.description(), updateRequest.endDate());
        projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public void deleteProject(UUID projectId) {
        User user = getUserByAuthentication();
        Project project = getProject(projectId);

        boolean enableDeleteProject = isOwner(user, project);
        if (!enableDeleteProject) throw new IllegalArgumentException("Você não pode atualizar detalhes do projeto. Somente o dono.");
        projectRepository.delete(project);
    }

    @Override
    public List<ProjectResponseDTO> getProjectsByOwner(UUID owner) {
        User user = getUserByAuthentication();

        List<Project> listProjectsByUser = projectRepository.findAllByOwnerId(user.getId());
        return listProjectsByUser.stream().map(projectMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProjectMemberDTO> getProjectMembers(UUID projectId) {
        User user = getUserByAuthentication();

        Project project = getProject(projectId);
        return project.getMembers().stream()
                 .map(this::toDto).toList();

    }

    @Override
    public ProjectMemberDTO getProjectMember(UUID projectId, UUID projectMemberId) {

        Project project = getProject(projectId);
        ProjectMember projectMember = project.getMember(projectMemberId);
        return toDto(projectMember);
    }

    private Project getProject(UUID projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()){
            throw new IllegalArgumentException("Projeto ainda não criado");
        }
        return project.get();
    }

    public ProjectMemberDTO toDto(ProjectMember projectMember) {
        return new ProjectMemberDTO(projectMember.getId(), projectMember.getProjectRole(),
                projectMember.getCreatedAt());
    }



}
