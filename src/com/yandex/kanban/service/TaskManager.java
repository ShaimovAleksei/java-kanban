package com.yandex.kanban.service;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getAllTask();

    void createTask(Task task);

    void deleteAllTasks();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    void updateTask(Task task);

    ArrayList<SubTask> getAllSubTask();

    void createSubTask(SubTask subTask);

    void deleteAllSubTask();

    SubTask getSubTaskById(int id);

    void deleteSubTaskById(int id);

    void updateSubtask(SubTask subtask);

    ArrayList<Epic> getAllEpic();

    void createEpic(Epic epic);

    void deleteAllEpic();

    Epic getEpicById(int id);

    void deleteEpicById(int id);

    void updateEpic(Epic epic);

    ArrayList<SubTask> getSubTasksByEpicId(int id);

     void updateEpicStatus(int Id);

    void printAllTask();

    List<Task> getHistory();
}
