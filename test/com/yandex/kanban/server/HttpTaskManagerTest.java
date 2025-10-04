package com.yandex.kanban.server;

import com.google.gson.Gson;
import com.yandex.kanban.service.InMemoryTaskManager;
import com.yandex.kanban.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.http.HttpClient;

public class HttpTaskManagerTest {
    protected TaskManager manager;
    protected HttpTaskServer taskServer;
    protected Gson gson;
    protected HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }
}
