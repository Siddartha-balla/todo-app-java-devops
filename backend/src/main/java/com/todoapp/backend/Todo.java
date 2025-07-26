package com.todoapp.backend;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime; // For tracking creation time

@Entity // Marks this class as a JPA entity, mapping to a database table
public class Todo {

    @Id // Specifies the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the primary key generation strategy
    private Long id; // Unique identifier for the to-do item
    private String text; // The description of the to-do item
    private boolean completed; // Status of the to-do item (true if completed, false otherwise)
    private LocalDateTime createdAt; // Timestamp when the to-do item was created

    // Default constructor is required by JPA
    public Todo() {
    }

    // Constructor for creating new Todo items
    public Todo(String text) {
        this.text = text;
        this.completed = false; // New tasks are not completed by default
        this.createdAt = LocalDateTime.now(); // Set creation time automatically
    }

    // --- Getters and Setters ---
    // These methods allow access to and modification of the Todo object's properties

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Todo{" +
               "id=" + id +
               ", text='" + text + '\'' +
               ", completed=" + completed +
               ", createdAt=" + createdAt +
               '}';
    }
}