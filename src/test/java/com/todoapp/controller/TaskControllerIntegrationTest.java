package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.api.dto.CreateTaskRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TaskController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void createTask_validRequest_returns201AndCreatedTask() throws Exception {
        // Arrange
        CreateTaskRequest request = new CreateTaskRequest("Buy groceries");
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.description").value("Buy groceries"))
            .andExpect(jsonPath("$.completed").value(false))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists());
    }
    
    @Test
    void createTask_emptyDescription_returns400() throws Exception {
        // Arrange
        CreateTaskRequest request = new CreateTaskRequest("");
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.message").value(containsString("Task description cannot be empty")));
    }
    
    @Test
    void createTask_nullDescription_returns400() throws Exception {
        // Arrange
        String requestJson = "{}";
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation Failed"));
    }
    
    @Test
    void createTask_descriptionTooLong_returns400() throws Exception {
        // Arrange - Create a description with 501 characters
        String longDescription = "A".repeat(501);
        CreateTaskRequest request = new CreateTaskRequest(longDescription);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.message").value(containsString("Description must not exceed 500 characters")));
    }
    
    @Test
    void createTask_exactlyMaxLength_returns201() throws Exception {
        // Arrange - Create a description with exactly 500 characters
        String maxLengthDescription = "A".repeat(500);
        CreateTaskRequest request = new CreateTaskRequest(maxLengthDescription);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.description").value(maxLengthDescription))
            .andExpect(jsonPath("$.description", hasLength(500)));
    }
}
