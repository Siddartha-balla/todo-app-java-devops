package com.todoapp.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Marks this interface as a Spring Data repository
// JpaRepository provides standard CRUD (Create, Read, Update, Delete) operations
// <Todo, Long> specifies the Entity type (Todo) and the type of its primary key (Long)
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // Spring Data JPA will automatically generate implementations for common methods
    // like save(), findById(), findAll(), deleteById(), etc.
    // You can add custom query methods here if needed, e.g., List<Todo> findByCompleted(boolean completed);
}