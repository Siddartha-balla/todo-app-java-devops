document.addEventListener('DOMContentLoaded', () => {
    const newTaskInput = document.getElementById('newTaskInput');
    const addTaskButton = document.getElementById('addTaskButton');
    const todoList = document.getElementById('todoList');
    const messageBox = document.getElementById('messageBox');

    // Base URL for your backend API
    // IMPORTANT: If you deploy your frontend and backend separately,
    // you will need to change this to your backend's deployed URL.
    const API_BASE_URL = 'http://localhost:8080/api/todos';

    // --- Message Handling ---
    function showMessage(text, type) {
        messageBox.textContent = text;
        messageBox.className = `message ${type}`; // Add type class for styling (success, error, warning)
        messageBox.style.display = 'block'; // Show the message box
        setTimeout(() => {
            messageBox.style.display = 'none'; // Hide after 3 seconds
            messageBox.textContent = '';
            messageBox.className = 'message';
        }, 3000);
    }

    // --- Fetch and Render To-Dos ---
    async function fetchTodos() {
        try {
            const response = await fetch(API_BASE_URL);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const todos = await response.json();
            renderTodos(todos);
        } catch (error) {
            console.error('Error fetching todos:', error);
            showMessage('Failed to load tasks. Please try again.', 'error');
        }
    }

    function renderTodos(todos) {
        todoList.innerHTML = ''; // Clear existing list
        // Sort todos: incomplete first, then completed. Within each, sort by ID or creation time.
        todos.sort((a, b) => {
            if (a.completed === b.completed) {
                // If same completion status, sort by ID (or createdAt if you add it to the model)
                return a.id - b.id;
            }
            return a.completed ? 1 : -1; // Incomplete (false) comes before completed (true)
        });

        if (todos.length === 0) {
            todoList.innerHTML = '<li class="text-center text-gray-500 text-lg">No tasks yet! Add one above.</li>';
            return;
        }

        todos.forEach(todo => {
            const li = document.createElement('li');
            li.className = todo.completed ? 'completed' : '';
            li.dataset.id = todo.id; // Store ID for easy access

            li.innerHTML = `
                <span>${todo.text}</span>
                <div class="actions">
                    <input type="checkbox" ${todo.completed ? 'checked' : ''} data-action="toggle">
                    <button data-action="delete">✖</button>
                </div>
            `;

            // Add event listeners for toggling and deleting
            li.querySelector('[data-action="toggle"]').addEventListener('change', () => toggleTodo(todo.id, todo.completed));
            li.querySelector('[data-action="delete"]').addEventListener('click', () => deleteTodo(todo.id));

            todoList.appendChild(li);
        });
    }

    // --- Add New To-Do ---
    addTaskButton.addEventListener('click', addTodo);
    newTaskInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            addTodo();
        }
    });

    async function addTodo() {
        const text = newTaskInput.value.trim();
        if (text === '') {
            showMessage('Task cannot be empty.', 'warning');
            return;
        }

        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ text: text, completed: false }),
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const newTodo = await response.json();
            showMessage('Task added successfully!', 'success');
            newTaskInput.value = ''; // Clear input
            fetchTodos(); // Re-fetch all todos to update the list
        } catch (error) {
            console.error('Error adding todo:', error);
            showMessage('Failed to add task. Please try again.', 'error');
        }
    }

    // --- Toggle To-Do Completion ---
    async function toggleTodo(id, currentCompletedStatus) {
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                // Send only the properties that change
                body: JSON.stringify({ completed: !currentCompletedStatus }),
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            showMessage('Task updated!', 'success');
            fetchTodos(); // Re-fetch to update UI
        } catch (error) {
            console.error('Error toggling todo:', error);
            showMessage('Failed to update task. Please try again.', 'error');
        }
    }

    // --- Delete To-Do ---
    async function deleteTodo(id) {
        if (!confirm('Are you sure you want to delete this task?')) {
            return; // User cancelled
        }
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`, {
                method: 'DELETE',
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            showMessage('Task deleted!', 'success');
            fetchTodos(); // Re-fetch to update UI
        } catch (error) {
            console.error('Error deleting todo:', error);
            showMessage('Failed to delete task. Please try again.', 'error');
        }
    }

    // Initial fetch when the page loads
    fetchTodos();
});