package com.tailan.gestao.de.projetos.core.model.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tailan.gestao.de.projetos.core.enums.ProjectRole;
import com.tailan.gestao.de.projetos.core.enums.TaskPriority;
import com.tailan.gestao.de.projetos.core.enums.TaskStatus;
import com.tailan.gestao.de.projetos.core.model.comment.Comment;
import com.tailan.gestao.de.projetos.core.model.project.Project;
import com.tailan.gestao.de.projetos.core.model.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // Mapeia o relacionamento inverso para a lista de comentários
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    protected  Task(){

    }

    public Task(Project project, User createdBy, String title, String description,TaskPriority priority) {
        this.project = project;
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = TaskStatus.TODO;
    }

    public void assignTo(User user){
        boolean isMember = project.getMembers().stream()
                .anyMatch(member -> member.getId().equals(user.getId()));
        if (!isMember){
            throw new IllegalArgumentException("Usuário não é membro do projeto.");
        }

        this.assignee = user;
    }

    public boolean markDone(){
        if (this.status == TaskStatus.DONE) return false;

        this.status = TaskStatus.DONE;
        this.updatedAt = OffsetDateTime.now();
        return true;
    }

    public boolean markTodo(){
        if (this.status == TaskStatus.DONE) return false;

        this.status = TaskStatus.TODO;
        this.updatedAt = OffsetDateTime.now();
        return true;
    }


    public void updateDetails(String title, String description, TaskPriority priority, LocalDate dueDate) {
        if (title == null || description == null || priority == null || dueDate == null) {
            throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
        }
        if (dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("O dia de validade da task não pode ser no tempo atrás.");
        }
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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
