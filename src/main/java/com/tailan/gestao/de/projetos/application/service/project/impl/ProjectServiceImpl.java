package com.tailan.gestao.de.projetos.application.service.project.impl;

import com.tailan.gestao.de.projetos.application.dto.project.*;
import com.tailan.gestao.de.projetos.application.mapper.ProjectMapper;
import com.tailan.gestao.de.projetos.application.service.project.ProjectService;
import com.tailan.gestao.de.projetos.core.model.project.Project;
import com.tailan.gestao.de.projetos.core.model.projectMember.ProjectMember;
import com.tailan.gestao.de.projetos.core.model.user.User;
import com.tailan.gestao.de.projetos.core.repository.ProjectRepository;
import com.tailan.gestao.de.projetos.core.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public ProjectResponseDTO createProject(CreateProjectDTO createProjectRequest) {

        //Pegar o ID do user autenticado que vai fazer a requisição e passar como OWNER ID
        Project project = projectMapper.toEntity(createProjectRequest);
        Project createdProject = projectRepository.save(project);

        return projectMapper.toResponse(createdProject);
    }

    @Override
    public void addMemberToProject(AddMemberDTO addMemberRequest) {
        // Verificar se quem está tentando adicionar membro é o DONO OU ADMIN PELA ROLE
        Optional<User> userOpt = userRepository.findById(addMemberRequest.userId());
        if (userOpt.isEmpty()){
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        Project project = getProject(addMemberRequest.projectId());

        User user = userOpt.get();

        project.addMember(user, addMemberRequest.role());
        projectRepository.save(project);
    }

    @Override
    public void removeMemberToProject(RemoveMemberDTO removeMemberRequest) {
        // Verificar se quem está tentando remover membro é o DONO OU ADMIN PELA ROLE

        Project project = getProject(removeMemberRequest.projectId());
        project.removeMember(removeMemberRequest.userId());
        projectRepository.save(project);
    }

    @Override
    public ProjectResponseDTO updateProject(UpdateProjectDto updateRequest) {
        Project project = getProject(updateRequest.projectId());
        project.updateDetails(updateRequest.name(), updateRequest.description(), updateRequest.endDate());
        projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public void deleteProject(UUID projectId) {
        Project project = getProject(projectId);
        projectRepository.delete(project);
    }

    @Override
    public List<ProjectResponseDTO> getProjectsByOwner(UUID owner) {
        Optional<User> OptionalUser = userRepository.findById(owner);
        if (OptionalUser.isEmpty()){
            throw new IllegalArgumentException("Usuário não existe.");
        }

        List<Project> listProjectsByUser = projectRepository.findAllByOwnerId(owner);
        return listProjectsByUser.stream().map(projectMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProjectMember> getProjectMembers(UUID projectId) {
        Project project = getProject(projectId);
        return project.getMembers();
    }

    @Override
    public ProjectMember getProjectMember(UUID projectId, UUID projectMemberId) {
        Project project = getProject(projectId);
        return project.getMember(projectMemberId);

    }

    private Project getProject(UUID projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()){
            throw new IllegalArgumentException("Projeto ainda não criado");
        }
        return project.get();
    }


}
