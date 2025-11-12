package com.todoapp.controller;

import com.todoapp.api.dto.CreateTaskRequest;
import com.todoapp.api.dto.TaskResponse;
import com.todoapp.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for task management operations.
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    
    private final TaskService taskService;
    
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    /**
     * Creates a new task.
     *
     * @param request the task creation request
     * @return the created task
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
