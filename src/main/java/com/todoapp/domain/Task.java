package com.todoapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

/**
 * Task entity representing a TODO item.
 */
@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 500)
    @NotBlank
    @Size(max = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.ACTIVE;
    
    @Column(nullable = false)
    private Boolean completed = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @Column(name = "completed_at")
    private Instant completedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
    
    // Constructors
    public Task() {
    }
    
    public Task(String description) {
        this.description = description;
    }
    
    // Getters and setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public Boolean getCompleted() {
        return completed;
    }
    
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Instant getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
