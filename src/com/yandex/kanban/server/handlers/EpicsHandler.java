package com.yandex.kanban.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/epics")) {
            List<Epic> epics = taskManager.getAllEpic();
            String response = gson.toJson(epics);
            sendText(exchange, response);
        } else if (path.matches("/epics/\\d+")) {
            String idStr = getPathId(path);
            try {
                int id = Integer.parseInt(idStr);
                Epic epic = taskManager.getEpicById(id);
                if (epic != null) {
                    String response = gson.toJson(epic);
                    sendText(exchange, response);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else if (path.matches("/epics/\\d+/subtasks")) {
            String epicPath = path.split("/subtasks")[0];
            String idStr = getPathId(epicPath);
            try {
                int id = Integer.parseInt(idStr);
                Epic epic = taskManager.getEpicById(id);
                if (epic != null) {
                    List<SubTask> subtasks = taskManager.getSubTasksByEpicId(id);
                    String response = gson.toJson(subtasks);
                    sendText(exchange, response);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try {
            System.out.println("Received JSON: " + body);

            Epic epic = gson.fromJson(body, Epic.class);
            System.out.println("Deserialized epic - ID: " + epic.getId() + ", Name: " + epic.getName());

            if (epic.getId() != 0) {
                Epic existingEpic = taskManager.getEpicById(epic.getId());
                System.out.println("Existing epic found: " + (existingEpic != null));

                if (existingEpic != null) {
                    taskManager.updateEpic(epic);
                    String response = gson.toJson(epic);
                    System.out.println("Updating epic, sending 200");
                    sendText(exchange, response); // 200 OK
                    return;
                }
            }

            taskManager.createEpic(epic);
            String response = gson.toJson(epic);
            System.out.println("Creating new epic, sending 201");
            sendCreated(exchange, response); // 201 Created

        } catch (JsonSyntaxException e) {
            exchange.sendResponseHeaders(400, -1);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics/\\d+")) {
            String idStr = getPathId(path);
            try {
                int id = Integer.parseInt(idStr);
                Epic epic = taskManager.getEpicById(id);
                if (epic != null) {
                    taskManager.deleteEpicById(id);
                    sendText(exchange, "Epic deleted");
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}
