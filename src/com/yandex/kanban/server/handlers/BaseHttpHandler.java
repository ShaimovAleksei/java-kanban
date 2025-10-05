package com.yandex.kanban.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        String response = "Not Found";
        h.sendResponseHeaders(404, response.length());
        h.getResponseBody().write(response.getBytes());
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        String response = "Task has time intersections";
        h.sendResponseHeaders(406, response.length());
        h.getResponseBody().write(response.getBytes());
        h.close();
    }

    protected void sendCreated(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendInternalError(HttpExchange h) throws IOException {
        String response = "Internal Server Error";
        h.sendResponseHeaders(500, response.length());
        h.getResponseBody().write(response.getBytes());
        h.close();
    }

    protected String getPathId(String path) {
        String[] pathParts = path.split("/");
        if (pathParts.length > 2) {
            return pathParts[2];
        }
        return null;
    }
}
