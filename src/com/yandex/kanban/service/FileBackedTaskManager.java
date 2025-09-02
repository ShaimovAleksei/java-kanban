package com.yandex.kanban.service;

import com.yandex.kanban.model.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : getAllTask()) {
                writer.write(toString(task));
                writer.newLine();
            }

            for (Epic epic : getAllEpic()) {
                writer.write(toString(epic));
                writer.newLine();
            }

            for (SubTask subTask : getAllSubTask()) {
                writer.write(toString(subTask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getTaskType()).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getTaskStatus()).append(",");
        sb.append(task.getDescription()).append(",");

        switch (task.getTaskType()) {
            case SUBTASK:
                sb.append(((SubTask) task).getEpicID());
                break;
            case TASK:
            case EPIC:
                break;
        }

        return sb.toString();
    }

    private Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 5) {
            return null;
        }

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description, type);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                if (parts.length < 6) {
                    return null;
                }
                int epicId = Integer.parseInt(parts[5]);
                task = new SubTask(name, description, epicId);
                break;
            default:
                return null;
        }

        task.setId(id);
        task.setTaskStatus(status);
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                Task task = manager.fromString(line);
                if (task != null) {
                    switch (task.getTaskType()) {
                        case TASK:
                            manager.taskList.put(task.getId(), task);
                            break;
                        case EPIC:
                            manager.epicList.put(task.getId(), (Epic) task);
                            break;
                        case SUBTASK:
                            manager.subTaskList.put(task.getId(), (SubTask) task);
                            Epic epic = manager.epicList.get(((SubTask) task).getEpicID());
                            if (epic != null) {
                                epic.addSubTaskID(task.getId());
                            }
                            break;
                    }

                    if (task.getId() >= manager.taskManagerID) {
                        manager.taskManagerID = task.getId() + 1;
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }

        return manager;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public boolean createSubTask(SubTask subTask) {
        boolean result = super.createSubTask(subTask);
        if (result) {
            save();
        }
        return result;
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    public static void main(String[] args) {
        try {
            File tempFile = File.createTempFile("tasks", ".csv");

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            Task task1 = new Task("Уборка", "Зал", TaskType.TASK);
            manager.createTask(task1);

            Epic epic1 = new Epic("Отпуск", "Египет");
            manager.createEpic(epic1);

            SubTask subTask1 = new SubTask("Покупка", "Билеты", epic1.getId());
            manager.createSubTask(subTask1);

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            System.out.println("Оригинальные задачи: " + manager.getAllTask().size());
            System.out.println("Загруженные задачи: " + loadedManager.getAllTask().size());

            tempFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
