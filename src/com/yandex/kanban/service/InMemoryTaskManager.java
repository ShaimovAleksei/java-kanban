package com.yandex.kanban.service;

import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;

import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> taskList;
    protected Map<Integer, Epic> epicList;
    protected Map<Integer, SubTask> subTaskList;
    protected int taskManagerID = 0;
    protected HistoryManager historyManager;
    private final Set<Task> prioritizedTasks;


    public InMemoryTaskManager() {
        taskList = new HashMap<>();
        epicList = new HashMap<>();
        subTaskList = new HashMap<>();
        this.historyManager = new InMemoryHistoryManager();
        this.prioritizedTasks = new TreeSet<>(
                                Comparator.comparing(Task::getStartTime,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing(Task::getId)
        );
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean hasTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private boolean hasTimeOverlapWithAllTasks(Task newTask) {
        if (newTask.getStartTime() == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null)
                .anyMatch(task -> hasTimeOverlap(newTask, task));
    }

    @Override
    public ArrayList<Task> getAllTask() {
        ArrayList<Task> listTask = new ArrayList<>(taskList.values());
        return listTask;
    }

    @Override
    public void createTask(Task task) {
        if (hasTimeOverlapWithAllTasks(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей задачей");
        }

        task.setId(taskManagerID++);
        taskList.put(task.getId(), task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }


    @Override
    public void deleteAllTasks() {
        for (Task task : taskList.values()) {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        }
        taskList.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskList.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = taskList.remove(id);
        if (task != null) {
            historyManager.remove(id);
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            Task oldTask = taskList.get(task.getId());

            prioritizedTasks.remove(oldTask);

            if (hasTimeOverlapWithAllTasks(task)) {
                prioritizedTasks.add(oldTask);
                throw new ManagerSaveException("Обновленная задача пересекается по времени с существующей задачей");
            }

            taskList.put(task.getId(), task);

            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public ArrayList<SubTask> getAllSubTask() {
        ArrayList<SubTask> listSubTask = new ArrayList<>(subTaskList.values());
        return listSubTask;
    }

    @Override
    public boolean createSubTask(SubTask subTask) {
        if (subTask == null) {
            return false;
        }

        Epic epic = epicList.get(subTask.getEpicID());
        if (epic == null) {
            return false;
        }

        subTask.setId(taskManagerID++);

        if (subTask.getEpicID() == subTask.getId()) {
            return false;
        }

         if (hasTimeOverlapWithAllTasks(subTask)) {
             throw new ManagerSaveException("Подзадача пересекается по времени с существующей задачей");
         }

        subTaskList.put(subTask.getId(), subTask);
        epic.addSubTaskID(subTask.getId());

        updateEpicStatus(subTask.getEpicID());
        updateEpicTimeFields(subTask.getEpicID());

        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }

        return true;
    }

    private void updateEpicTimeFields(int epicId) {
        Epic epic = epicList.get(epicId);
        if (epic == null) return;

        List<SubTask> epicSubtasks = getSubTasksByEpicId(epicId);

        if (epicSubtasks.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        Duration totalDuration = epicSubtasks.stream()
                .map(SubTask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(totalDuration);

        LocalDateTime earliestStart = epicSubtasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        epic.setStartTime(earliestStart);

        LocalDateTime latestEnd = epicSubtasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        epic.setEndTime(latestEnd);
    }

    @Override
    public void deleteAllSubTask() {
        for (SubTask subTask : subTaskList.values()) {
            prioritizedTasks.remove(subTask);
            historyManager.remove(subTask.getId());
        }
        subTaskList.clear();

        for (Epic epic : epicList.values()) {
            epic.getSubTaskID().clear();
            epic.setTaskStatus(TaskStatus.NEW);
            updateEpicTimeFields(epic.getId());
        }
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTaskList.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = subTaskList.remove(id);
        if (subTask != null) {
            historyManager.remove(id);
            prioritizedTasks.remove(subTask);

            int epicId = subTask.getEpicID();
            Epic epic = epicList.get(epicId);
            if (epic != null) {
                epic.removeSubTaskId(id);
                updateEpicStatus(epic.getId());
                updateEpicTimeFields(epic.getId());
            }
        }
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        if (subTaskList.containsKey(subtask.getId())) {
            SubTask oldSubTask = subTaskList.get(subtask.getId());

            prioritizedTasks.remove(oldSubTask);

            if (hasTimeOverlapWithAllTasks(subtask)) {
                prioritizedTasks.add(oldSubTask);
                throw new ManagerSaveException("Обновленная подзадача пересекается по времени с существующей задачей");
            }

            subTaskList.put(subtask.getId(), subtask);

            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }

            updateEpicStatus(subtask.getEpicID());
            updateEpicTimeFields(subtask.getEpicID());
        }
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        ArrayList<Epic> listEpic = new ArrayList<>(epicList.values());
        return listEpic;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(taskManagerID++);
        epicList.put(epic.getId(), epic);
    }

    @Override
    public void deleteAllEpic() {
        for (Epic epic : epicList.values()) {
            historyManager.remove(epic.getId());
            for (Integer subTaskId : epic.getSubTaskID()) {
                SubTask subTask = subTaskList.remove(subTaskId);
                if (subTask != null) {
                    prioritizedTasks.remove(subTask);
                    historyManager.remove(subTaskId);
                }
            }
        }
        epicList.clear();
        subTaskList.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicList.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epicList.remove(id);
        if (epic != null) {
            historyManager.remove(id);
            for (Integer subTaskId : epic.getSubTaskID()) {
                SubTask subTask = subTaskList.remove(subTaskId);
                if (subTask != null) {
                    prioritizedTasks.remove(subTask);
                    historyManager.remove(subTaskId);
                }
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicList.containsKey(epic.getId())) {
            Epic savedEpic = epicList.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpicId(int id) {
        Epic epic = epicList.get(id);
        if (epic == null) {
            return new ArrayList<>();
        }

        return epic.getSubTaskID().stream()
                .map(subTaskList::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void printAllTask() {
        System.out.println("Все задачи:");
        System.out.println(getAllTask());
        System.out.println("Все эпики:");
        System.out.println(getAllEpic());
        System.out.println("Все подзадачи:");
        System.out.println(getAllSubTask());
    }

    @Override
    public void updateEpicStatus(int id) {
        Epic epic = epicList.get(id);
        if (epic == null) return;

        ArrayList<SubTask> epicSubtasks = getSubTasksByEpicId(id);
        if (epicSubtasks.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (SubTask subtask : epicSubtasks) {
            if (subtask.getTaskStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subtask.getTaskStatus() != TaskStatus.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void printHistory() {
        System.out.println("История просмотров");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }

}
