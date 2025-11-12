package com.todoapp.service;

import com.todoapp.api.dto.CreateTaskRequest;
import com.todoapp.api.dto.TaskResponse;
import com.todoapp.domain.Task;
import com.todoapp.domain.TaskStatus;
import com.todoapp.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    private TaskServiceImpl taskService;
    
    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository);
    }
    
    @Test
    void createTask_validRequest_returnsCreatedTask() {
        // Arrange
        CreateTaskRequest request = new CreateTaskRequest("Buy groceries");
        
        Task savedTask = new Task("Buy groceries");
        savedTask.setId(UUID.randomUUID());
        savedTask.setCreatedAt(Instant.now());
        savedTask.setUpdatedAt(Instant.now());
        savedTask.setStatus(TaskStatus.ACTIVE);
        savedTask.setCompleted(false);
        
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        
        // Act
        TaskResponse response = taskService.createTask(request);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.description()).isEqualTo("Buy groceries");
        assertThat(response.completed()).isFalse();
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.id()).isNotNull();
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();
        
        // Verify repository interaction
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        
        Task capturedTask = taskCaptor.getValue();
        assertThat(capturedTask.getDescription()).isEqualTo("Buy groceries");
    }
    
    @Test
    void createTask_longDescription_acceptsUpTo500Characters() {
        // Arrange
        String longDescription = "A".repeat(500);
        CreateTaskRequest request = new CreateTaskRequest(longDescription);
        
        Task savedTask = new Task(longDescription);
        savedTask.setId(UUID.randomUUID());
        savedTask.setCreatedAt(Instant.now());
        savedTask.setUpdatedAt(Instant.now());
        savedTask.setStatus(TaskStatus.ACTIVE);
        savedTask.setCompleted(false);
        
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        
        // Act
        TaskResponse response = taskService.createTask(request);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.description()).hasSize(500);
        verify(taskRepository).save(any(Task.class));
    }
}
