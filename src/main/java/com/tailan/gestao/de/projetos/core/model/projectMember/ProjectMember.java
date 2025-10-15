package com.tailan.gestao.de.projetos.core.model.projectMember;

import com.tailan.gestao.de.projetos.core.enums.ProjectRole;
import com.tailan.gestao.de.projetos.core.model.comment.Comment;
import com.tailan.gestao.de.projetos.core.model.project.Project;
import com.tailan.gestao.de.projetos.core.model.task.Task;
import com.tailan.gestao.de.projetos.core.model.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_members")
public class ProjectMember {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_role",nullable = false)
    private ProjectRole projectRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    protected ProjectMember() {

    }

    public ProjectMember(ProjectRole projectRole, User user, Project project) {

        if (user == null || project == null || projectRole == null){
            throw new IllegalArgumentException("Campos user, project e role são obrigatorios");
        }
        this.projectRole = projectRole;
        this.user = user;
        this.project = project;
    }

    public void assignRole(ProjectRole projectRole){
        this.projectRole = projectRole;
    }

    public boolean canEditTask(Task task){
        return projectRole != ProjectRole.VIEWER;
    }

    public boolean canDeleteComment(Comment comment){
        return projectRole != ProjectRole.VIEWER && projectRole != ProjectRole.MEMBER;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ProjectRole getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(ProjectRole projectRole) {
        this.projectRole = projectRole;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
