package com.tailan.gestao.de.projetos.application.controller.task;

import com.tailan.gestao.de.projetos.application.dto.task.AssignTaskToMember;
import com.tailan.gestao.de.projetos.application.dto.task.CreateTaskDTO;
import com.tailan.gestao.de.projetos.application.dto.task.TaskResponseDTO;
import com.tailan.gestao.de.projetos.application.dto.task.UpdateTaskDTO;
import com.tailan.gestao.de.projetos.application.service.task.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public ResponseEntity<TaskResponseDTO> createTaskToProject(@RequestBody CreateTaskDTO createTaskDTO) {
        TaskResponseDTO response = taskService.createTaskToProject(createTaskDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{projectId}/assignTo")
    public ResponseEntity<TaskResponseDTO> assignTaskToMember(@PathVariable("projectId")UUID projectId, @RequestBody AssignTaskToMember assignTaskToMember) {
        TaskResponseDTO response = taskService.assignTaskToMember(projectId, assignTaskToMember);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping("/{taskId}/markDone")
    public ResponseEntity<Void> markDoneTask(@PathVariable("taskId") UUID taskId) {
        taskService.markDone(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{taskId}/markTodo")
    public ResponseEntity<Void> markDoneTodo(@PathVariable("taskId") UUID taskId) {
        taskService.markTodo(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{taskId}/update")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable("taskId") UUID taskId, @RequestBody UpdateTaskDTO updateTaskDTO) {
        TaskResponseDTO responseDTO = taskService.updateTaskToProject(taskId, updateTaskDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{projectId}/projectTasks")
    public ResponseEntity<Page<TaskResponseDTO>> getTasks(@PathVariable("projectId") UUID projectId, @RequestParam(value = "page", defaultValue = "0" ) int page, @RequestParam(value = "size", defaultValue = "20") int size) {
        Page<TaskResponseDTO> response = taskService.getTasksToProject(projectId, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/{taskId}/projectTask")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable("taskId") UUID taskId) {
        TaskResponseDTO response = taskService.getTaskToProject(taskId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{taskId}/deleteTask")
    public ResponseEntity<Void> deleteTaskToProject(@PathVariable("taskId") UUID taskId) {
        taskService.deleteTaskToProject(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




}
