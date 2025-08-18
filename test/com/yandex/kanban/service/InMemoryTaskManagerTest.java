package com.yandex.kanban.service;

import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

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

    @Test
    void shouldRemoveSubTaskFromEpicWhenDeleted() {
        Epic epic = new Epic("Путешествие", "Египет");
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Купить", "Билет", epic.getId());
        subTask.setId(1);
        manager.createSubTask(subTask);

        List<Integer> epicSubTasks = manager.getEpicById(epic.getId()).getSubTaskID();
        assertEquals(1, epicSubTasks.size());
        assertEquals(subTask.getId(), epicSubTasks.get(0));

        manager.deleteSubTaskById(subTask.getId());

        assertTrue(manager.getEpicById(epic.getId()).getSubTaskID().isEmpty());
    }

    @Test
    void shouldRemoveSubTasksWhenEpicDeleted() {
        Epic epic = new Epic("Путешествие", "Египет");
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Купить", "Билет", epic.getId());
        manager.createSubTask(subTask);

        manager.deleteEpicById(epic.getId());

        assertNull(manager.getSubTaskById(subTask.getId()));
    }

    @Test
    void shouldNotKeepDeletedTasksInHistory() {
        Task task = new Task("Путешествие", "Египет");
        manager.createTask(task);
        manager.getTaskById(task.getId());

        manager.deleteTaskById(task.getId());

        assertFalse(manager.getHistory().contains(task));
    }

}