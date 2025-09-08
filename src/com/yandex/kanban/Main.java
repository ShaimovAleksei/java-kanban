package com.yandex.kanban;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskType;
import com.yandex.kanban.service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        try {
            File tempFile = File.createTempFile("tasks", ".csv");

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            Task task1 = new Task("Уборка", "Зал", TaskType.TASK);
            manager.createTask(task1);

            Epic epic1 = new Epic("Отпуск", "Египет");
            manager.createEpic(epic1);

            SubTask subTask1 = new SubTask("Покупка", "Билеты", epic1.getId());
            manager.createSubTask(subTask1);

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            System.out.println("Оригинальные задачи: " + manager.getAllTask().size());
            System.out.println("Загруженные задачи: " + loadedManager.getAllTask().size());

            tempFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


