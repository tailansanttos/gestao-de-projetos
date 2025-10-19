package com.tailan.gestao.de.projetos.application.service.project;

import com.tailan.gestao.de.projetos.application.dto.member.ProjectMemberDTO;
import com.tailan.gestao.de.projetos.application.dto.project.*;
import com.tailan.gestao.de.projetos.core.model.project.Project;
import com.tailan.gestao.de.projetos.core.model.projectMember.ProjectMember;
import com.tailan.gestao.de.projetos.core.model.user.User;


import java.util.List;
import java.util.UUID;

public interface ProjectService {
    boolean isOwnerOrAdmin(User user, Project project);boolean isOwner(User user, Project project);
    boolean isProjectMember(User user, Project project);

    public ProjectResponseDTO createProject(CreateProjectDTO createProjectRequest);
    public void addMemberToProject(AddMemberDTO addMemberRequest);
    public void removeMemberToProject(RemoveMemberDTO addMemberRequest);

    ProjectResponseDTO updateProject(UUID projectId, UpdateProjectDto updateRequest);

    public void deleteProject(UUID projectId);

    public List<ProjectResponseDTO> getProjectsByOwner(UUID owner);


    public List<ProjectMemberDTO> getProjectMembers(UUID projectId);
    public ProjectMemberDTO getProjectMember(UUID projectId, UUID projectMemberId);

    public Project getProject(UUID projectId);
}
