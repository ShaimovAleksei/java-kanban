package com.yandex.kanban.server;

import com.yandex.kanban.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest extends HttpTaskManagerTest {
    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", TaskType.TASK);
        Task task2 = new Task("Task 2", "Description 2", TaskType.TASK);
        manager.createTask(task1);
        manager.createTask(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Testing task", TaskType.TASK);
        manager.createTask(task);
        int taskId = task.getId();

        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(responseTask);
        assertEquals(taskId, responseTask.getId());
        assertEquals("Test Task", responseTask.getName());
    }

    @Test
    public void testGetTaskByIdNotFound() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Testing task", TaskType.TASK);
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTask();
        assertEquals(1, tasksFromManager.size());
        assertEquals("Test Task", tasksFromManager.get(0).getName());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Original Task", "Original Description", TaskType.TASK);
        manager.createTask(task);
        int taskId = task.getId();

        task.setName("Updated Task");
        task.setDescription("Updated Description");
        task.setTaskStatus(TaskStatus.IN_PROGRESS);

        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task updatedTask = manager.getTaskById(taskId);
        assertEquals("Updated Task", updatedTask.getName());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getTaskStatus());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task to delete", "Description", TaskType.TASK);
        manager.createTask(task);
        int taskId = task.getId();

        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertNull(manager.getTaskById(taskId));
    }

    @Test
    public void testCreateTaskWithTimeConflict() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(30);

        Task task1 = new Task("Task 1", "Description 1", TaskType.TASK, duration, startTime);
        manager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2", TaskType.TASK, duration, startTime.plusMinutes(10));
        String taskJson = gson.toJson(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }
}
