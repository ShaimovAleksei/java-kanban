package com.yandex.kanban.server;

import com.yandex.kanban.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtasksTest extends HttpTaskManagerTest {
    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic Description");
        manager.createEpic(epic);

        SubTask subtask1 = new SubTask("Subtask 1", "Description 1", epic.getId());
        SubTask subtask2 = new SubTask("Subtask 2", "Description 2", epic.getId());
        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        SubTask[] subtasks = gson.fromJson(response.body(), SubTask[].class);
        assertEquals(2, subtasks.length);
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic Description");
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Test Subtask", "Testing subtask", epic.getId());
        manager.createSubTask(subtask);
        int subtaskId = subtask.getId();

        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        SubTask responseSubtask = gson.fromJson(response.body(), SubTask.class);
        assertNotNull(responseSubtask);
        assertEquals(subtaskId, responseSubtask.getId());
        assertEquals("Test Subtask", responseSubtask.getName());
    }

    @Test
    public void testGetSubtaskByIdNotFound() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic Description");
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Test Subtask", "Testing subtask", epic.getId());
        String subtaskJson = gson.toJson(subtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        SubTask createdSubtask = gson.fromJson(response.body(), SubTask.class);
        assertNotNull(createdSubtask);
        assertEquals("Test Subtask", createdSubtask.getName());

        SubTask subtaskFromManager = manager.getSubTaskById(createdSubtask.getId());
        assertNotNull(subtaskFromManager);
        assertEquals(createdSubtask.getId(), subtaskFromManager.getId());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic Description");
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Original Subtask", "Original Description", epic.getId());
        manager.createSubTask(subtask);
        int subtaskId = subtask.getId();

        subtask.setName("Updated Subtask");
        subtask.setDescription("Updated Description");
        subtask.setTaskStatus(TaskStatus.DONE);

        String subtaskJson = gson.toJson(subtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        SubTask updatedSubtask = manager.getSubTaskById(subtaskId);
        assertEquals("Updated Subtask", updatedSubtask.getName());
        assertEquals(TaskStatus.DONE, updatedSubtask.getTaskStatus());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic Description");
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask to delete", "Description", epic.getId());
        manager.createSubTask(subtask);
        int subtaskId = subtask.getId();

        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertNull(manager.getSubTaskById(subtaskId));
    }
}
