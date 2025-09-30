package com.yandex.kanban;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskType;
import com.yandex.kanban.service.FileBackedTaskManager;
import com.yandex.kanban.service.Managers;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        try {
            File tempFile = File.createTempFile("tasks", ".csv");
            FileBackedTaskManager manager = (FileBackedTaskManager) Managers.getDefault();

            LocalDateTime baseTime = LocalDateTime.now();

            Task task1 = new Task("Уборка", "Зал", TaskType.TASK,
                    Duration.ofHours(2), baseTime.plusHours(1));
            manager.createTask(task1);

            Epic epic1 = new Epic("Отпуск", "Египет");
            manager.createEpic(epic1);

            SubTask subTask1 = new SubTask("Покупка", "Билеты", epic1.getId(),
                    Duration.ofDays(1), baseTime.plusDays(1));
            manager.createSubTask(subTask1);

            SubTask subTask2 = new SubTask("Отель", "Бронирование", epic1.getId(),
                    Duration.ofHours(4), baseTime.plusDays(2));
            manager.createSubTask(subTask2);

            System.out.println("Приоритетные задачи:");
            manager.getPrioritizedTasks().forEach(task ->
                    System.out.println(task.getName() + " - " + task.getStartTime()));

            System.out.println("Продолжительность эпика: " + epic1.getDuration().toHours() + " часов");
            System.out.println("Начало эпика: " + epic1.getStartTime());
            System.out.println("Окончание эпика: " + epic1.getEndTime());

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            System.out.println("Оригинальные задачи: " + manager.getAllTask().size());
            System.out.println("Загруженные задачи: " + loadedManager.getAllTask().size());

            tempFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}


