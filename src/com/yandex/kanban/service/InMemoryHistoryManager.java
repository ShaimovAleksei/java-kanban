package com.yandex.kanban.service;

import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager{
    private  LinkedList<Task> history = new LinkedList<>();
    private static final int MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        Task copy = new Task(task.getName(), task.getDescription());
        copy.setId(task.getId());
        copy.setTaskStatus(task.getTaskStatus());

        history.add(copy);
        checkHistorySize();
    }

    void checkHistorySize(){
        if (history.size() > MAX_SIZE) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    public void printHistory() {
        System.out.println("История просмотров");
        for (Task task : history) {
            System.out.println(task);
        }
    }
}
