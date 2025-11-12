package com.todoapp.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new task.
 */
public record CreateTaskRequest(
    @NotBlank(message = "Task description cannot be empty")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description
) {}
