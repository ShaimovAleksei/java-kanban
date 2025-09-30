package com.yandex.kanban.model;

import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIDs;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskType.EPIC);
        subTaskIDs = new ArrayList<>();
    }

    @Override
    public Duration getDuration() {
        return super.getDuration(); // Будет рассчитываться в менеджере
    }

    @Override
    public LocalDateTime getStartTime() {
        return super.getStartTime(); // Будет рассчитываться в менеджере
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s",
                getId(),
                getTaskType(),
                getName(),
                getTaskStatus(),
                getDescription(),
                getDuration().toMinutes(),
                getStartTime() != null ? getStartTime().toString() : "null");
    }

    public ArrayList<Integer> getSubTaskID() {
        return subTaskIDs;
    }

    public void setSubTaskID(ArrayList<Integer> subTaskID) {
        this.subTaskIDs = subTaskID;
    }

    public boolean addSubTaskID(int id) {
        if (id == this.getId()) {
            return false;
        }
        return subTaskIDs.add(id);

    }

    public void removeSubTaskId(int id) {
        Integer parseId = Integer.valueOf(id);
        subTaskIDs.remove(parseId);
    }
}
