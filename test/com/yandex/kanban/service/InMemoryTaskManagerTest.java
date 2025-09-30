package com.yandex.kanban.service;

import com.yandex.kanban.model.TaskType;
import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryTaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    void shouldAddAndFindDifferentTaskTypes() {
        Task task = new Task("Покупка", "Мебель", TaskType.TASK);
        Epic epic = new Epic("Путешествие", "Египет");
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Купить", "Билет", epic.getId(),
                Duration.ofHours(1), LocalDateTime.now().plusHours(2));

        manager.createTask(task);
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
        subTask.setId(epic.getId()); // Устанавливаем тот же ID

        assertFalse(manager.createSubTask(subTask));
    }

    @Test
    void shouldRemoveSubTaskFromEpicWhenDeleted() {
        Epic epic = new Epic("Путешествие", "Египет");
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Купить", "Билет", epic.getId(),
                Duration.ofHours(1), LocalDateTime.now().plusHours(1));
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

        SubTask subTask = new SubTask("Купить", "Билет", epic.getId(),
                Duration.ofHours(1), LocalDateTime.now().plusHours(1));
        manager.createSubTask(subTask);

        manager.deleteEpicById(epic.getId());

        assertNull(manager.getSubTaskById(subTask.getId()));
    }

    @Test
    void shouldNotKeepDeletedTasksInHistory() {
        Task task = new Task("Путешествие", "Египет", TaskType.TASK,
                Duration.ofHours(1), LocalDateTime.now());
        manager.createTask(task);
        manager.getTaskById(task.getId());

        manager.deleteTaskById(task.getId());

        assertFalse(manager.getHistory().contains(task));
    }

    @Test
    void testPrioritizedTasksOrder() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task 1", "Desc", TaskType.TASK,
                Duration.ofHours(1), now.plusHours(3));
        Task task2 = new Task("Task 2", "Desc", TaskType.TASK,
                Duration.ofHours(1), now.plusHours(1));

        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals(task2, prioritized.get(0)); // Более ранняя задача первой
        assertEquals(task1, prioritized.get(1));
    }

    @Test
    void testTasksWithoutTimeNotInPrioritized() {
        Task task1 = new Task("Task 1", "Desc", TaskType.TASK); // Без времени
        Task task2 = new Task("Task 2", "Desc", TaskType.TASK,
                Duration.ofHours(1), LocalDateTime.now());

        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(1, prioritized.size());
        assertEquals(task2, prioritized.get(0));
    }
}