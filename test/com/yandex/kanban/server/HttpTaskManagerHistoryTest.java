package com.yandex.kanban.server;

import com.yandex.kanban.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerHistoryTest extends HttpTaskManagerTest {
    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description", TaskType.TASK);
        manager.createTask(task);
        manager.getTaskById(task.getId());

        Epic epic = new Epic("Epic", "Epic Description");
        manager.createEpic(epic);
        manager.getEpicById(epic.getId());

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, history.length);
    }

    @Test
    public void testGetEmptyHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(0, history.length);
    }
}
