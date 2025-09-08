package com.yandex.kanban.service;

import com.yandex.kanban.model.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String CSV_HEADER = "id,type,name,status,description,epic";

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSV_HEADER);
            writer.newLine();

            for (Task task : getAllTask()) {
                writer.write(task.toString());
                writer.newLine();
            }

            for (Epic epic : getAllEpic()) {
                writer.write(epic.toString());
                writer.newLine();
            }

            for (SubTask subTask : getAllSubTask()) {
                writer.write(subTask.toString());
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
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

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }

                Task task = manager.fromString(line);
                if (task != null) {
                    switch (task.getTaskType()) {
                        case TASK:
                            manager.addTaskWithoutSaving(task);
                            break;
                        case EPIC:
                            manager.addEpicWithoutSaving((Epic) task);
                            break;
                        case SUBTASK:
                            manager.addSubtaskWithoutSaving((SubTask) task);
                            break;
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }

        return manager;
    }

    private void addTaskWithoutSaving(Task task) {
        taskList.put(task.getId(), task);
        if (task.getId() >= taskManagerID) {
            taskManagerID = task.getId() + 1;
        }
    }

    private void addEpicWithoutSaving(Epic epic) {
        epicList.put(epic.getId(), epic);
        if (epic.getId() >= taskManagerID) {
            taskManagerID = epic.getId() + 1;
        }
    }

    private void addSubtaskWithoutSaving(SubTask subTask) {
        subTaskList.put(subTask.getId(), subTask);
        if (subTask.getId() >= taskManagerID) {
            taskManagerID = subTask.getId() + 1;
        }

        Epic epic = epicList.get(subTask.getEpicID());
        if (epic != null) {
            epic.addSubTaskID(subTask.getId());
        }
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

}
