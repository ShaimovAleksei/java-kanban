package com.yandex.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicID;

    public SubTask(String name, String description, int epicID) {
        super(name, description, TaskType.SUBTASK);
        this.epicID = epicID;

    }

    public SubTask(String name, String description, int epicID, Duration duration, LocalDateTime startTime) {
        super(name, description, TaskType.SUBTASK, duration, startTime);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%d",
                getId(),
                getTaskType(),
                getName(),
                getTaskStatus(),
                getDescription(),
                getDuration().toMinutes(),
                getStartTime() != null ? getStartTime().toString() : "null",
                epicID);
    }

    public int getEpicID() {
        return epicID;
    }

    public boolean setEpicID(int epicID) {
        if (epicID == this.getId()) {
            return false;
        }
        this.epicID = epicID;
        return true;
    }
}
