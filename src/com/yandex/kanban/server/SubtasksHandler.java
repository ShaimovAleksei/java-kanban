package com.yandex.kanban.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
        if (path.equals("/subtasks")) {
            List<SubTask> subtasks = taskManager.getAllSubTask();
            String response = gson.toJson(subtasks);
            sendText(exchange, response);
        } else if (path.matches("/subtasks/\\d+")) {
            String idStr = getPathId(path);
            try {
                int id = Integer.parseInt(idStr);
                SubTask subtask = taskManager.getSubTaskById(id);
                if (subtask != null) {
                    String response = gson.toJson(subtask);
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
            SubTask subtask = gson.fromJson(body, SubTask.class);

            if (subtask.getId() != 0 && taskManager.getSubTaskById(subtask.getId()) != null) {
                taskManager.updateSubtask(subtask);
                String response = gson.toJson(subtask);
                sendText(exchange, response); // 200 OK
            } else {
                boolean created = taskManager.createSubTask(subtask);
                if (created) {
                    String response = gson.toJson(subtask);
                    sendCreated(exchange, response); // 201 Created
                } else {
                    exchange.sendResponseHeaders(400, -1);
                }
            }
        } catch (JsonSyntaxException e) {
            exchange.sendResponseHeaders(400, -1);
        } catch (Exception e) {
            if (e.getMessage().contains("пересекается")) {
                sendHasInteractions(exchange);
            } else {
                sendInternalError(exchange);
            }
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/subtasks/\\d+")) {
            String idStr = getPathId(path);
            try {
                int id = Integer.parseInt(idStr);
                SubTask subtask = taskManager.getSubTaskById(id);
                if (subtask != null) {
                    taskManager.deleteSubTaskById(id);
                    sendText(exchange, "Subtask deleted");
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
