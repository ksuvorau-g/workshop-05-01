package com.todoapp.service;

import com.todoapp.api.dto.CreateTaskRequest;
import com.todoapp.api.dto.TaskResponse;

/**
 * Service interface for task management operations.
 */
public interface TaskService {
    
    /**
     * Creates a new task.
     *
     * @param request the task creation request
     * @return the created task response
     */
    TaskResponse createTask(CreateTaskRequest request);
}
