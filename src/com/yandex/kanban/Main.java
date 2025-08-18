package com.yandex.kanban;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Встреча", "Встретить заказсчика");
        Task task2 = new Task("Тренировка", "Сходить в зал");
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("ремонт", "В спальне");
        manager.createEpic(epic1);

        SubTask subTask1 = new SubTask("Проект", "Заказать проект", epic1.getId());
        SubTask subTask2 = new SubTask("Материалы", "Купить материалы", epic1.getId());
        SubTask subTask3 = new SubTask("Подрядчик", "Позвонить подрядчику", epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);

        Epic epic2 = new Epic("Отпуск", "Забронировать гостиницу");
        manager.createEpic(epic2);

        System.out.println(manager.getTaskById(task1.getId()));
        System.out.println(manager.getEpicById(epic1.getId()));
        System.out.println(manager.getSubTaskById(subTask1.getId()));
        System.out.println(manager.getTaskById(task2.getId()));
        System.out.println(manager.getEpicById(epic1.getId()));
        System.out.println(manager.getSubTaskById(subTask2.getId()));
        System.out.println(manager.getTaskById(task1.getId()));


        manager.printHistory();

        manager.deleteTaskById(task1.getId());
        manager.printHistory();

        manager.deleteEpicById(epic1.getId());
        manager.printHistory();
    }

}


