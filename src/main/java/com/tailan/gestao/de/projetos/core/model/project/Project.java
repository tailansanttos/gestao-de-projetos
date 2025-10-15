package com.tailan.gestao.de.projetos.core.model.project;

import com.tailan.gestao.de.projetos.core.enums.ProjectRole;
import com.tailan.gestao.de.projetos.core.model.projectMember.ProjectMember;
import com.tailan.gestao.de.projetos.core.model.task.Task;
import com.tailan.gestao.de.projetos.core.model.user.User;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "projects")
public class Project {
    private static final Logger log = LoggerFactory.getLogger(Project.class);

    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    protected Project() {

    }

    public Project(String name, LocalDate startDate, UUID ownerId) {
        setName(name);
        this.startDate = startDate;
        this.ownerId = ownerId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Data de término não pode ser antes do ínicio.");
        }
        this.endDate = endDate;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public List<ProjectMember> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public void setMembers(List<ProjectMember> members) {
        this.members = members;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    //Metodos do agregado

    public void addMember(User user, ProjectRole role) {

        if (members.stream().anyMatch(member -> member.getUser().getId().equals(user.getId()))){
            throw new IllegalArgumentException("Usuário já é membro do projeto.");
        }

        ProjectMember member = new ProjectMember(role, user, this);
        members.add(member);

    }


    public void removeMember(UUID userId) {
        Optional<ProjectMember> memberOpt = members.stream()
                .filter(member -> member.getUser().getId().equals(userId))
                .findFirst();

        if (memberOpt.isEmpty()){
            throw new IllegalArgumentException("Usuário não faz parte do projeto.");
        }

        ProjectMember member = memberOpt.get();

        if (member.getProjectRole() == ProjectRole.OWNER){
            throw new IllegalArgumentException("Não é possível remover o owner do projeto.");
        }
        members.remove(member);
        member.setProject(null);
        log.info("Membro {} removido do projeto {}", userId, this.id);

    }

    public boolean isAdmin(UUID userId){
        ProjectMember member = getMember(userId);
        if (!(member.getProjectRole() == ProjectRole.ADMIN)){
            return true;
        }
        return false;
    }

    private ProjectMember getMember(UUID userId){
        Optional<ProjectMember> memberOpt = members.stream()
                .filter(member -> member.getUser().getId().equals(userId))
                .findFirst();

        if (memberOpt.isEmpty()){
            throw new IllegalArgumentException("Usuário não faz parte do projeto.");
        }

        ProjectMember member = memberOpt.get();
        return member;
    }

    public void addTask(Task task) {
        if (!tasks.contains(task)) {
            tasks.add(task);
            task.setProject(this);
        }
    }

    public void removeTask(Task task){
        if (tasks.contains(task)) {
            tasks.remove(task);
            task.setProject(null);
        }
    }


    public List<ProjectMember> getMembersWithRole(ProjectRole role) {
        List<ProjectMember> membersWithRole = members.stream()
                .filter(member -> member.getProjectRole().equals(role)).toList();
        return membersWithRole;

    }

    public void updateDetails(String name, String description, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Data de término não pode ser antes do inicio do projeto.");
        }
        this.name = name;
        this.description = description;
        this.endDate = endDate;
    }



}
