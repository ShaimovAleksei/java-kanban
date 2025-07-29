package com.yandex.kanban.service;

import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {

        history.add(task);
        checkHistorySyze();
    }

    void checkHistorySyze(){
        if (history.size() > 10) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    public void printHistory() {
        System.out.println("История просмотров");
        for (Task task : history) {
            System.out.println(task);
        }
    }
}
