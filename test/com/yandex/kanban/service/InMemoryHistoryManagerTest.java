package com.yandex.kanban.service;

import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldPreserveTaskVersionInHistory() {
        HistoryManager history = Managers.getDefaultHistory();
        Task task1 = new Task("Покупка", "Мебель");
        task1.setId(1);
        task1.setTaskStatus(TaskStatus.IN_PROGRESS);

        history.add(task1);

        task1.setName("Продажа");

        Task task2 = history.getHistory().get(0);
        assertEquals("Покупка", task2.getName());
    }

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task("Покупка", "Мебель");
        task.setId(1);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task = new Task("Покупка", "Мебель");
        task.setId(1);
        historyManager.add(task);
        historyManager.remove(1);

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldNotContainDuplicates() {
        Task task = new Task("Покупка", "Мебель");
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldMaintainInsertionOrder() {
        Task task1 = new Task("Покупка", "Мебель");
        Task task2 = new Task("Покупка1", "Билет");
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(2, history.get(0).getId());
        assertEquals(1, history.get(1).getId());
    }

}