package com.todoapp.repository;

import com.todoapp.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Repository interface for Task entity.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
}
