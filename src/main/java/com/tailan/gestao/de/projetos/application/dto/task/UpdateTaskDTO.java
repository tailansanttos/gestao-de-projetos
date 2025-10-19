package com.tailan.gestao.de.projetos.application.dto.task;

import com.tailan.gestao.de.projetos.core.enums.TaskPriority;
import com.tailan.gestao.de.projetos.core.enums.TaskStatus;

import java.time.LocalDate;


public record UpdateTaskDTO(String title, String description, TaskStatus status, TaskPriority priority, LocalDate dueDate) {
}
