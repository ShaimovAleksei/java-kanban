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
        HistoryManager historyManager1 = Managers.getDefaultHistory();
        HistoryManager historyManager2 = Managers.getDefaultHistory();

        assertNotNull(taskManager1);
        assertNotNull(historyManager1);

        assertFalse(taskManager1 == taskManager2);
        assertFalse(historyManager1 == historyManager2);
    }
}