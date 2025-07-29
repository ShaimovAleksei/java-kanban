package com.yandex.kanban.model;

import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void shouldNotAllowSelfAsSubtask() {
        Epic epic = new Epic("Путешествие", "Египет");
        assertFalse(epic.addSubTaskID(epic.getId()));
    }

}