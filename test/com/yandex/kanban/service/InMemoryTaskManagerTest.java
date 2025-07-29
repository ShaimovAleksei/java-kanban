package com.yandex.kanban.service;

import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    void shouldAddAndFindDifferentTaskTypes() {
        Task task = new Task("Покупка", "Мебель");
        Epic epic = new Epic("Путешествие", "Египет");
        SubTask subTask = new SubTask("Купить", "Билет", 1);

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubTask(subTask);

        assertNotNull(manager.getTaskById(task.getId()));
        assertNotNull(manager.getEpicById(epic.getId()));
        assertNotNull(manager.getSubTaskById(subTask.getId()));
    }

    @Test
    void shouldNotAllowEpicToBeItsOwnSubtask() {
        Epic epic = new Epic("Путешествие", "Египет");
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Купить", "Билет", epic.getId());
        subTask.setId(epic.getId());

        assertFalse(manager.createSubTask(subTask));
    }

}