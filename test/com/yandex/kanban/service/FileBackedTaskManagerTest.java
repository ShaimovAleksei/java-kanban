package com.yandex.kanban.service;

import com.yandex.kanban.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest {
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test_tasks", ".csv");
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void testSaveAndLoadEmptyFile() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        manager.save();

        String content = Files.readString(tempFile.toPath());
        String[] lines = content.split("\n");

        assertEquals(1, lines.length);
        assertEquals("id,type,name,status,description,duration,startTime,epic", lines[0].trim(),
                "Неверный формат заголовка");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTask().isEmpty());
        assertTrue(loadedManager.getAllEpic().isEmpty());
        assertTrue(loadedManager.getAllSubTask().isEmpty());
    }

    @Test
    void testSaveMultipleTasks() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Ремонт", "Зал", TaskType.TASK,
                Duration.ofHours(2), LocalDateTime.now());
        Epic epic = new Epic("Отпуск", "Египет");
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Купить", "Билет", epic.getId(),
                Duration.ofHours(1), LocalDateTime.now().plusHours(1));

        manager.createTask(task);
        manager.createSubTask(subTask);

        assertEquals(1, manager.getAllTask().size());
        assertEquals(1, manager.getAllEpic().size());
        assertEquals(1, manager.getAllSubTask().size());

        String content = Files.readString(tempFile.toPath());
        String[] lines = content.split("\n");

        assertEquals(4, lines.length); // заголовок + 3 задачи
        assertEquals("id,type,name,status,description,duration,startTime,epic", lines[0].trim(),
                "Неверный формат заголовка");

        assertTrue(content.contains("Ремонт"));
        assertTrue(content.contains("Отпуск"));
        assertTrue(content.contains("Купить"));
    }

    @Test
    void testLoadMultipleTasks() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        LocalDateTime baseTime = LocalDateTime.now();

        Task task = new Task("Ремонт", "Зал", TaskType.TASK,
                Duration.ofHours(2), baseTime);
        Epic epic = new Epic("Отпуск", "Египет");
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Купить", "Билет", epic.getId(),
                Duration.ofHours(1), baseTime.plusHours(1));

        manager.createTask(task);
        manager.createSubTask(subTask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTask().size());
        assertEquals(1, loadedManager.getAllEpic().size());
        assertEquals(1, loadedManager.getAllSubTask().size());

        Task loadedTask = loadedManager.getTaskById(0);
        Epic loadedEpic = loadedManager.getEpicById(1);
        SubTask loadedSubTask = loadedManager.getSubTaskById(2);

        assertNotNull(loadedTask);
        assertNotNull(loadedEpic);
        assertNotNull(loadedSubTask);

        assertEquals("Ремонт", loadedTask.getName(), "Название задачи не совпадает");
        assertEquals("Отпуск", loadedEpic.getName(), "Название эпика не совпадает");
        assertEquals("Купить", loadedSubTask.getName(), "Название подзадачи не совпадает");

        assertEquals("Зал", loadedTask.getDescription(), "Описание задачи не совпадает");
        assertEquals("Египет", loadedEpic.getDescription(), "Описание эпика не совпадает");
        assertEquals("Билет", loadedSubTask.getDescription(), "Описание подзадачи не совпадает");

        assertEquals(TaskStatus.NEW, loadedTask.getTaskStatus(), "Статус задачи не совпадает");
        assertEquals(TaskStatus.NEW, loadedEpic.getTaskStatus(), "Статус эпика не совпадает");
        assertEquals(TaskStatus.NEW, loadedSubTask.getTaskStatus(), "Статус подзадачи не совпадает");

        assertEquals(1, loadedSubTask.getEpicID(), "ID эпика в подзадаче не совпадает");
        assertTrue(loadedEpic.getSubTaskID().contains(2), "Эпик должен содержать ID подзадачи");

        assertEquals(Duration.ofHours(2), loadedTask.getDuration());
        assertEquals(baseTime, loadedTask.getStartTime());
        assertEquals(Duration.ofHours(1), loadedSubTask.getDuration());
        assertEquals(baseTime.plusHours(1), loadedSubTask.getStartTime());
    }
}

