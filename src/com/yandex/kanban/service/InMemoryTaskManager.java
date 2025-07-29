package com.yandex.kanban.service;
import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> taskList;
    private HashMap<Integer, Epic> epicList;
    private HashMap<Integer, SubTask> subTaskList;
    private int taskManagerID = 0;
    private  List<Integer> listAllIdToHistory;

    public InMemoryTaskManager() {
        taskList = new HashMap<>();
        epicList = new HashMap<>();
        subTaskList = new HashMap<>();
        listAllIdToHistory = new ArrayList<>();
    }

    @Override
    public ArrayList<Task> getAllTask() {
        ArrayList<Task> listTask = new ArrayList<>(taskList.values());
        return listTask;
    }

    @Override
    public void createTask(Task task) {
        task.setId(taskManagerID++);
        taskList.put(task.getId(), task);
    }


    @Override
    public void deleteAllTasks() {
        taskList.clear();
    }

    @Override
    public Task getTaskById(int id) {
        listAllIdToHistory.add(id);
        checkSizeListAllId();
        return taskList.get(id);
    }

    @Override
    public void deleteTaskById(int id) {
        taskList.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            taskList.put(task.getId(), task);
        }
    }

    @Override
    public ArrayList<SubTask> getAllSubTask(){
        ArrayList<SubTask> listSubTask = new ArrayList<>(subTaskList.values());
        return  listSubTask;
    }

    @Override
    public void createSubTask(SubTask subTask){
        subTask.setId(taskManagerID++);
       // subTask.setEpicID(epic.getId());
        epicList.get(subTask.getEpicID()).addSubTaskID(subTask.getId());
        subTaskList.put(subTask.getId(),subTask);
        updateEpicStatus(subTask.getEpicID());
    }

    @Override
    public void deleteAllSubTask(){
        subTaskList.clear();
        ArrayList<Epic> epicListInArr = new ArrayList<>(epicList.values());
        for (Epic epic : epicListInArr) {
            epic.getSubTaskID().clear();
            epic.setTaskStatus(TaskStatus.NEW);
        }
    }

    @Override
    public SubTask getSubTaskById(int id){
        listAllIdToHistory.add(id);
        checkSizeListAllId();
        return subTaskList.get(id);
    }

    @Override
    public void deleteSubTaskById(int id){
        int epicId = subTaskList.get(id).getEpicID();
        Epic epic = epicList.get(epicId);
        epic.removeSubTaskId(id);
        subTaskList.remove(id);
        updateEpicStatus(epic.getId());

    }

    @Override
    public void updateSubtask(SubTask subtask) {
        if (subTaskList.containsKey(subtask.getId())) {
            subTaskList.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicID());
        }
    }

    @Override
    public ArrayList<Epic> getAllEpic(){
        ArrayList<Epic> listEpic = new ArrayList<>(epicList.values());
        return listEpic;
    }

    @Override
    public void createEpic(Epic epic){
        epic.setId(taskManagerID++);
        epicList.put(epic.getId(), epic);
    }

    @Override
    public void deleteAllEpic(){
        epicList.clear();
        subTaskList.clear();
    }

    @Override
    public Epic getEpicById(int id){
        listAllIdToHistory.add(id);
        checkSizeListAllId();
        return epicList.get(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epicList.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubTaskID()) {
                subTaskList.remove(subtaskId);
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
    public ArrayList<SubTask> getSubTasksByEpicId(int id){
        ArrayList<SubTask> subTaskListByEpicId = new ArrayList<>();
        Epic epic = epicList.get(id);
        for (int subTaskId: epic.getSubTaskID()){
            SubTask subTask = subTaskList.get(subTaskId);
            subTaskListByEpicId.add(subTask);
        }
        return subTaskListByEpicId;
    }

    @Override
    public void printAllTask(){
        System.out.println("Все задачи:");
        System.out.println(getAllTask());
        System.out.println("Все эпики:");
        System.out.println(getAllEpic());
        System.out.println("Все подзадачи:");
        System.out.println(getAllSubTask());
    }

    @Override
    public void updateEpicStatus(int Id) {
        Epic epic = epicList.get(Id);
        if (epic == null) return;

        ArrayList<SubTask> epicSubtasks = getSubTasksByEpicId(Id);
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
        List<Task> history = new ArrayList<>();
        for (int id : listAllIdToHistory) {
            if (taskList.containsKey(id)) {
                history.add(taskList.get(id));
            } else if (epicList.containsKey(id)) {
                history.add(epicList.get(id));
            } else if (subTaskList.containsKey(id)) {
                history.add(subTaskList.get(id));
            }
        }
        return history;
    }

    public void checkSizeListAllId(){
        if (listAllIdToHistory.size() > 10){
            listAllIdToHistory.remove(0);
        }
    }

    @Override
    public void printHistory(){
        System.out.println("История просмотров");
        for (Task task : getHistory()) {
            System.out.println(task);
        }
    }
}
