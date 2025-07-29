package com.yandex.kanban;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.service.InMemoryTaskManager;
import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Task task1 = new Task("ремонт","Ремонт в комнате");
        Task task2 = new Task("покупка","покупка мебели");

        TaskManager manager = Managers.getDefault();
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("стройка", "спланировать весь цикл постройки дома");
        manager.createEpic(epic1);

        SubTask subTask1 = new SubTask("проект", "заказать проет", epic1.getId());
        SubTask subTask2 = new SubTask("смета", "посчитать смету", epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        Epic epic2 = new Epic("отпуск", "спланировать отпуск");
        manager.createEpic(epic2);

        SubTask subTask3 = new SubTask("путевка", "выбрать тур в тайланд", epic2.getId());
        manager.createSubTask(subTask3);

        //manager.printAllTask();
        System.out.println();
        System.out.println();

        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);

        subTask1.setTaskStatus(TaskStatus.DONE);
        subTask2.setTaskStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subTask1);
        manager.updateSubtask(subTask2);

        //manager.printAllTask();
        System.out.println();
        System.out.println();

        manager.deleteTaskById(task1.getId());
        manager.deleteEpicById(epic2.getId());

        //manager.printAllTask();

        System.out.println(manager.getTaskById(task2.getId()));
        System.out.println(manager.getEpicById(epic1.getId()));
        System.out.println(manager.getSubTaskById(subTask2.getId()));
        System.out.println(manager.getSubTaskById(subTask1.getId()));

        manager.printHistory();
        System.out.println();
        System.out.println();

        System.out.println(manager.getTaskById(task2.getId()));
        System.out.println(manager.getSubTaskById(subTask2.getId()));
        System.out.println();
        System.out.println();

        manager.printHistory();
    }

}
