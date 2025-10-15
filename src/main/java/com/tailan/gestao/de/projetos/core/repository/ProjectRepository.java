package com.tailan.gestao.de.projetos.core.repository;

import com.tailan.gestao.de.projetos.core.model.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    public List<Project> findAllByOwnerId(UUID id);
}
