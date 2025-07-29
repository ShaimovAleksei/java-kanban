package com.yandex.kanban.service;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
}
