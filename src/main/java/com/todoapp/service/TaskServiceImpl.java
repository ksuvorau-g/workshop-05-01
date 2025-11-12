package com.todoapp.service;

import com.todoapp.api.dto.CreateTaskRequest;
import com.todoapp.api.dto.TaskResponse;
import com.todoapp.domain.Task;
import com.todoapp.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of TaskService.
 */
@Service
public class TaskServiceImpl implements TaskService {
    
    private final TaskRepository taskRepository;
    
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = new Task(request.description());
        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }
    
    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getDescription(),
            task.getCompleted(),
            task.getStatus().name(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}
