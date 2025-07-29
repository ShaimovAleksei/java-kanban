package com.yandex.kanban.model;

import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    void shouldNotBeItsOwnEpic() {
        SubTask subTask = new SubTask("Купить", "Билет", 1);
        subTask.setId(1);
        assertFalse(subTask.setEpicID(subTask.getId()));
    }

}