package com.todoapp.api.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for task data.
 */
public record TaskResponse(
    UUID id,
    String description,
    Boolean completed,
    String status,
    Instant createdAt,
    Instant updatedAt
) {}
