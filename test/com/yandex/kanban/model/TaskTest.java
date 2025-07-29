package com.yandex.kanban.model;

import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Покупка", "Мебель");
        task1.setId(1);

        Task task2 = new Task("Покупка", "Мебель на кухню");
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    void taskShouldNotChangeAfterAddingToManager() {
        Task task1 = new Task("Покупка", "Мебель");
        TaskManager manager = Managers.getDefault();

        manager.createTask(task1);
        Task task2 = manager.getTaskById(task1.getId());

        assertEquals(task1.getName(), task2.getName());
        assertEquals(task1.getDescription(), task2.getDescription());
        assertEquals(task1.getTaskStatus(), task2.getTaskStatus());
    }

}