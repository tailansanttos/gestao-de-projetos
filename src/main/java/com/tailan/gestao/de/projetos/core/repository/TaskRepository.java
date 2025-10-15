package com.tailan.gestao.de.projetos.core.repository;

import com.tailan.gestao.de.projetos.core.model.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
}
