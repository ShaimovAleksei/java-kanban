package com.yandex.kanban.service;

import com.yandex.kanban.model.*;
import com.yandex.kanban.service.exceptions.ManagerSaveException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    void testEpicStatusAllNew() {
        Epic epic = new Epic("Путешествие", "Египет");
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask("Купить", "Билет", epic.getId());
        SubTask subTask2 = new SubTask("Забронировать", "Отель", epic.getId());

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    void testEpicStatusAllDone() {
        Epic epic = new Epic("Путешествие", "Египет");
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask("Купить", "Билет", epic.getId());
        SubTask subTask2 = new SubTask("Забронировать", "Отель", epic.getId());

        subTask1.setTaskStatus(TaskStatus.DONE);
        subTask2.setTaskStatus(TaskStatus.DONE);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }

    @Test
    void testEpicStatusMixed() {
        Epic epic = new Epic("Путешествие", "Египет");
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask("Купить", "Билет", epic.getId());
        SubTask subTask2 = new SubTask("Забронировать", "Отель", epic.getId());

        subTask1.setTaskStatus(TaskStatus.NEW);
        subTask2.setTaskStatus(TaskStatus.DONE);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    void testEpicStatusInProgress() {
        Epic epic = new Epic("Путешествие", "Египет");
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask("Купить", "Билет", epic.getId());
        SubTask subTask2 = new SubTask("Забронировать", "Отель", epic.getId());

        subTask1.setTaskStatus(TaskStatus.IN_PROGRESS);
        subTask2.setTaskStatus(TaskStatus.IN_PROGRESS);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    void testTaskTimeFields() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        Task task = new Task("Уборка", "Зал", TaskType.TASK, duration, startTime);

        taskManager.createTask(task);

        assertEquals(duration, task.getDuration());
        assertEquals(startTime, task.getStartTime());
        assertEquals(startTime.plus(duration), task.getEndTime());
    }


    @Test
    void testEpicTimeCalculation() {
        Epic epic = new Epic("Отпуск", "Египет");
        taskManager.createEpic(epic);

        LocalDateTime start1 = LocalDateTime.now();
        LocalDateTime start2 = start1.plusHours(1);
        Duration duration = Duration.ofHours(1);

        SubTask subTask1 = new SubTask("Покупка", "Билеты", epic.getId(), duration, start1);
        SubTask subTask2 = new SubTask("Бронирование", "Отель", epic.getId(), duration, start2);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertEquals(Duration.ofHours(2), epic.getDuration());
        assertEquals(start1, epic.getStartTime());
        assertEquals(start2.plus(duration), epic.getEndTime());
    }

    @Test
    void testGetPrioritizedTasks() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Ремонт", "Зал", TaskType.TASK, Duration.ofHours(1), now.plusHours(2));
        Task task2 = new Task("Покупка", "Мебель", TaskType.TASK, Duration.ofHours(1), now.plusHours(1));
        Task task3 = new Task("Уборка", "Кухня", TaskType.TASK, Duration.ofHours(1), null);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        List<Task> prioritized = taskManager.getPrioritizedTasks();

        assertEquals(2, prioritized.size());
        assertEquals(task2.getId(), prioritized.get(0).getId()); // Сравниваем по ID
        assertEquals(task1.getId(), prioritized.get(1).getId()); // Сравниваем по ID
    }

    @Test
    void testTimeOverlapDetection() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);

        Task task1 = new Task("Ремонт", "Зал", TaskType.TASK, duration, startTime);
        taskManager.createTask(task1); // ← ДОБАВИТЬ

        Task task2 = new Task("Покупка", "Мебель", TaskType.TASK, duration, startTime.plusHours(1));

        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void testNoTimeOverlap() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);

        Task task1 = new Task("Ремонт", "Зал", TaskType.TASK, duration, startTime);
        taskManager.createTask(task1); // ← ДОБАВИТЬ

        Task task2 = new Task("Покупка", "Мебель", TaskType.TASK, duration, startTime.plusHours(3));

        assertDoesNotThrow(() -> taskManager.createTask(task2));
    }
}
