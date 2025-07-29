package com.yandex.kanban.service;

import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
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
    void shouldNotExceedLimit() {
        HistoryManager history = Managers.getDefaultHistory();

        for (int i = 0; i < 15; i++) {
            Task task = new Task("Покупка " + i, "Мебель");
            task.setId(i);
            history.add(task);
        }

        assertEquals(10, history.getHistory().size());
    }

}