package com.tailan.gestao.de.projetos.application.service.project;

import com.tailan.gestao.de.projetos.application.dto.project.*;
import com.tailan.gestao.de.projetos.core.model.projectMember.ProjectMember;


import java.util.List;
import java.util.UUID;

public interface ProjectService {
    public ProjectResponseDTO createProject(CreateProjectDTO createProjectRequest);
    public void addMemberToProject(AddMemberDTO addMemberRequest);
    public void removeMemberToProject(RemoveMemberDTO addMemberRequest);

    ProjectResponseDTO updateProject(UpdateProjectDto updateRequest);

    public void deleteProject(UUID projectId);

    public List<ProjectResponseDTO> getProjectsByOwner(UUID owner);

    public List<ProjectMember> getProjectMembers(UUID projectId);
    public ProjectMember getProjectMember(UUID projectId, UUID projectMemberId);
}
