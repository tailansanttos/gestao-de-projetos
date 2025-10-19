package com.tailan.gestao.de.projetos.application.controller.project;

import com.tailan.gestao.de.projetos.application.dto.member.ProjectMemberDTO;
import com.tailan.gestao.de.projetos.application.dto.project.*;
import com.tailan.gestao.de.projetos.application.dto.task.TaskResponseDTO;
import com.tailan.gestao.de.projetos.application.service.project.ProjectService;
import com.tailan.gestao.de.projetos.application.service.task.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }
    @PostMapping("/create")
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody CreateProjectDTO createProjectDTO) {
        ProjectResponseDTO projectResponseDTO = projectService.createProject(createProjectDTO);
        return new ResponseEntity<>(projectResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/addMember")
    public ResponseEntity<Void> addMemberToProject(@RequestBody AddMemberDTO addMemberDTO) {
        projectService.addMemberToProject(addMemberDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/removeMember")
    public ResponseEntity<Void> removeMemberFromProject(@RequestBody RemoveMemberDTO removeMemberDTO) {
        projectService.removeMemberToProject(removeMemberDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{projectId}/updateProject")
    public ResponseEntity<ProjectResponseDTO> updateDetailsToProject(@PathVariable UUID projectId, @RequestBody UpdateProjectDto updateProjectDTO) {
        ProjectResponseDTO projectResponseDTO = projectService.updateProject(projectId, updateProjectDTO);
        return new ResponseEntity<>(projectResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}/delete")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{ownerId}/owner")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByOwner(@PathVariable UUID ownerId) {
        List<ProjectResponseDTO> projectResponseDTOS = projectService.getProjectsByOwner(ownerId);
        return new ResponseEntity<>(projectResponseDTOS, HttpStatus.OK);
    }

    @GetMapping("/{projectId}/projectMembers")
    public ResponseEntity<List<ProjectMemberDTO>> getProjectMembersByProject(@PathVariable UUID projectId) {
        List<ProjectMemberDTO> list = projectService.getProjectMembers(projectId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{projectId}/get/{userId}")
    public ResponseEntity<ProjectMemberDTO> getProjectMember(@PathVariable UUID projectId, @PathVariable UUID projectMemberId) {
        ProjectMemberDTO projectMemberDTO = projectService.getProjectMember(projectId, projectMemberId);
        return new ResponseEntity<>(projectMemberDTO, HttpStatus.OK);
    }


}
