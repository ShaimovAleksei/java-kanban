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

        Task foundTask = manager.getTaskById(task.getId());
        Epic foundEpic = manager.getEpicById(epic.getId());
        SubTask foundSubTask = manager.getSubTaskById(subTask.getId());

        assertNotNull(foundTask, "Задача не найдена");
        assertNotNull(foundEpic, "Эпик не найден");
        assertNotNull(foundSubTask, "Подзадача не найдена");
    }

    @Test
    void shouldNotAllowEpicToBeItsOwnSubtask() {
        Epic epic = new Epic("Путешествие", "Египет");
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Купить", "Билет", epic.getId());

        assertFalse(manager.createSubTask(subTask),
                "Подзадача не должна быть создана с ID равным ID эпика");
    }

    @Test
    void shouldRemoveSubTaskFromEpicWhenDeleted() {
        Epic epic = new Epic("Путешествие", "Египет");
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Купить", "Билет", epic.getId(),
                Duration.ofHours(1), LocalDateTime.now().plusHours(1));
        manager.createSubTask(subTask);

        List<Integer> epicSubTasks = manager.getEpicById(epic.getId()).getSubTaskID();
        assertEquals(1, epicSubTasks.size(), "Эпик должен содержать одну подзадачу");
        assertTrue(epicSubTasks.contains(subTask.getId()),
                "Эпик должен содержать ID созданной подзадачи");

        manager.deleteSubTaskById(subTask.getId());

        assertTrue(manager.getEpicById(epic.getId()).getSubTaskID().isEmpty(),
                "После удаления подзадачи эпик не должен содержать подзадач");
    }

    @Test
    void shouldRemoveSubTasksWhenEpicDeleted() {
        Epic epic = new Epic("Путешествие", "Египет");
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Купить", "Билет", epic.getId(),
                Duration.ofHours(1), LocalDateTime.now().plusHours(1));
        manager.createSubTask(subTask);

        int subTaskId = subTask.getId();

        manager.deleteEpicById(epic.getId());

        assertNull(manager.getSubTaskById(subTaskId),
                "Подзадача должна быть удалена при удалении эпика");
    }

    @Test
    void shouldNotKeepDeletedTasksInHistory() {
        Task task = new Task("Путешествие", "Египет", TaskType.TASK,
                Duration.ofHours(1), LocalDateTime.now());
        manager.createTask(task);
        manager.getTaskById(task.getId());

        manager.deleteTaskById(task.getId());

        assertFalse(manager.getHistory().contains(task),
                "Удаленная задача не должна оставаться в истории");
    }

    @Test
    void testPrioritizedTasksOrder() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Ремонт", "Зал", TaskType.TASK,
                Duration.ofHours(1), now.plusHours(3));
        Task task2 = new Task("Покупка", "Мебель", TaskType.TASK,
                Duration.ofHours(1), now.plusHours(1));

        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(2, prioritized.size(), "Должно быть 2 задачи в приоритетном списке");
        assertEquals(task2.getId(), prioritized.get(0).getId(), "Более ранняя задача должна быть первой");
        assertEquals(task1.getId(), prioritized.get(1).getId(), "Более поздняя задача должна быть второй");
    }

    @Test
    void testTasksWithoutTimeNotInPrioritized() {
        Task task1 = new Task("Уборка", "Кухня", TaskType.TASK); // Без времени
        Task task2 = new Task("Покупка", "Мебель", TaskType.TASK,
                Duration.ofHours(1), LocalDateTime.now());

        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(1, prioritized.size(), "Только задачи со временем должны быть в приоритетном списке");
        assertEquals(task2.getId(), prioritized.get(0).getId(),
                "Задача со временем должна быть в приоритетном списке");
    }
}