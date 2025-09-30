package com.yandex.kanban.service;

import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ManagersTest {
    @Test
    void shouldReturnInitializedManagers() {
        TaskManager taskManager1 = Managers.getDefault();
        TaskManager taskManager2 = Managers.getDefault();

        assertNotNull(taskManager1);
        assertNotNull(taskManager2);

        assertNotSame(taskManager1, taskManager2);
    }

    @Test
    void shouldReturnHistoryManager() {
        HistoryManager historyManager1 = Managers.getDefaultHistory();
        HistoryManager historyManager2 = Managers.getDefaultHistory();

        assertNotNull(historyManager1);
        assertNotNull(historyManager2);

        assertNotSame(historyManager1, historyManager2);
    }
}