package com.yandex.kanban.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
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
        if (path.equals("/tasks")) {
            List<Task> tasks = taskManager.getAllTask();
            String response = gson.toJson(tasks);
            sendText(exchange, response);
        } else if (path.matches("/tasks/\\d+")) {
            String idStr = getPathId(path);
            try {
                int id = Integer.parseInt(idStr);
                Task task = taskManager.getTaskById(id);
                if (task != null) {
                    String response = gson.toJson(task);
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
            Task task = gson.fromJson(body, Task.class);

            if (task.getId() != 0 && taskManager.getTaskById(task.getId()) != null) {
                taskManager.updateTask(task);
                String response = gson.toJson(task);
                sendText(exchange, response); // 200 OK
            } else {
                taskManager.createTask(task);
                String response = gson.toJson(task);
                sendCreated(exchange, response); // 201 Created
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
        if (path.matches("/tasks/\\d+")) {
            String idStr = getPathId(path);
            try {
                int id = Integer.parseInt(idStr);
                Task task = taskManager.getTaskById(id);
                if (task != null) {
                    taskManager.deleteTaskById(id);
                    sendText(exchange, "Task deleted");
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
