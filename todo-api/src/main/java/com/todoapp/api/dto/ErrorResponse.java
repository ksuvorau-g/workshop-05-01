package com.todoapp.api.dto;

import java.time.Instant;

/**
 * Standard error response DTO.
 */
public record ErrorResponse(
    int status,
    String error,
    String message,
    String path,
    Instant timestamp
) {}
