package com.todoapp.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Marks this class as a REST controller, handling incoming HTTP requests
@RequestMapping("/api/todos") // Base path for all endpoints in this controller
@CrossOrigin(origins = "*") // Allows requests from any origin (for development; restrict in production)
public class TodoController {

    @Autowired // Injects an instance of TodoRepository
    private TodoRepository todoRepository;

    // GET all to-do items
    // Handles GET requests to /api/todos
    @GetMapping
    public List<Todo> getAllTodos() {
        // Returns all Todo items from the database
        // Spring Data JPA's findAll() method is used here
        return todoRepository.findAll();
    }

    // GET a single to-do item by ID
    // Handles GET requests to /api/todos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        // findById returns an Optional<Todo> to handle cases where the ID might not exist
        Optional<Todo> todo = todoRepository.findById(id);
        // If the Todo is found, return it with HTTP status 200 (OK)
        // Otherwise, return HTTP status 404 (Not Found)
        return todo.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST a new to-do item
    // Handles POST requests to /api/todos
    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        // Ensure ID is null for new creation (database will generate it)
        todo.setId(null);
        // Ensure completed is false for new tasks, and set creation timestamp
        todo.setCompleted(false);
        todo.setCreatedAt(java.time.LocalDateTime.now());
        // Save the new todo item to the database
        Todo savedTodo = todoRepository.save(todo);
        // Return the saved todo item with HTTP status 201 (Created)
        return new ResponseEntity<>(savedTodo, HttpStatus.CREATED);
    }

    // PUT (update) an existing to-do item
    // Handles PUT requests to /api/todos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails) {
        // Check if the to-do item exists
        Optional<Todo> todoOptional = todoRepository.findById(id);

        if (todoOptional.isPresent()) {
            Todo existingTodo = todoOptional.get();
            // Update properties from the request body
            existingTodo.setText(todoDetails.getText());
            existingTodo.setCompleted(todoDetails.isCompleted());
            // Note: createdAt is usually not updated here, but you could add logic if needed

            // Save the updated todo item
            Todo updatedTodo = todoRepository.save(existingTodo);
            // Return the updated todo item with HTTP status 200 (OK)
            return ResponseEntity.ok(updatedTodo);
        } else {
            // If not found, return HTTP status 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE a to-do item
    // Handles DELETE requests to /api/todos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        // Check if the to-do item exists before deleting
        if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id);
            // Return HTTP status 204 (No Content) for successful deletion
            return ResponseEntity.noContent().build();
        } else {
            // If not found, return HTTP status 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }
}