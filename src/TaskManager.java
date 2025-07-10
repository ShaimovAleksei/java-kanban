import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> taskList;
    private HashMap<Integer, Epic> epicList;
    private HashMap<Integer, SubTask> subTaskList;
    private int taskManagerID = 0;

    public TaskManager() {
        taskList = new HashMap<>();
        epicList = new HashMap<>();
        subTaskList = new HashMap<>();
    }

    public ArrayList<Task> getAllTask() {
        ArrayList<Task> listTask = new ArrayList<>(taskList.values());
        return listTask;
    }

    public void createTask(Task task) {
        task.setId(taskManagerID++);
        taskList.put(task.getId(), task);
    }


    public void deleteAllTasks() {
        taskList.clear();
    }

    public Task getTaskById(int id) {
        return taskList.get(id);
    }

    public void deleteTaskById(int id) {
        taskList.remove(id);
    }

    public void updateTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            taskList.put(task.getId(), task);
        }
    }

    public ArrayList<SubTask> getAllSubTask(){
        ArrayList<SubTask> listSubTask = new ArrayList<>(subTaskList.values());
        return  listSubTask;
    }

    public void createSubTask(SubTask subTask,Epic epic){
        subTask.setId(taskManagerID++);
        subTask.setEpicID(epic.getId());
        epic.addSubTaskID(subTask.getId());
        subTaskList.put(subTask.getId(),subTask);
        updateEpicStatus(epic.getId());
    }

    public void deleteAllSubTask(){
        subTaskList.clear();
        ArrayList<Epic> epicListInArr = new ArrayList<>(epicList.values());
        for (Epic epic : epicListInArr) {
            epic.getSubTaskID().clear();
            epic.setTaskStatus(TaskStatus.NEW);
        }
    }

    public SubTask getSubTaskById(int id){
        return subTaskList.get(id);
    }

    public void deleteSubTaskById(int id){
        int epicId = subTaskList.get(id).getEpicID();
        Epic epic = epicList.get(epicId);
        epic.getSubTaskID().remove(id);
        subTaskList.remove(id);
        updateEpicStatus(epic.getId());

    }

    public void updateSubtask(SubTask subtask) {
        if (subTaskList.containsKey(subtask.getId())) {
            subTaskList.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicID());
        }
    }

    public ArrayList<Epic> getAllEpic(){
        ArrayList<Epic> listEpic = new ArrayList<>(epicList.values());
        return listEpic;
    }

    public void createEpic(Epic epic){
        epic.setId(taskManagerID++);
        epicList.put(epic.getId(), epic);
    }

    public void deleteAllEpic(){
        epicList.clear();
        subTaskList.clear();
    }

    public Epic getEpicById(int id){
        return epicList.get(id);
    }

    public void deleteEpicById(int id){
        epicList.remove(id);
        ArrayList<SubTask> newSubTasksList = new ArrayList<>(subTaskList.values());
        for (SubTask subTask : newSubTasksList) {
            if (subTask.getEpicID() == id){
                subTaskList.remove((subTask.getId()));
            }
        }
    }

    public void updateEpic(Epic epic) {
        if (epicList.containsKey(epic.getId())) {
            Epic savedEpic = epicList.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    public ArrayList<SubTask> getSubTasksByEpicId(int id){
        ArrayList<SubTask> subTaskListByEpicId = new ArrayList<>();
        Epic epic = epicList.get(id);
        for (int subTaskId: epic.getSubTaskID()){
            SubTask subTask = subTaskList.get(subTaskId);
            subTaskListByEpicId.add(subTask);
        }
        return subTaskListByEpicId;
    }

    private void updateEpicStatus(int Id) {
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

    public void printAllTask(){
        System.out.println("Все задачи:");
        System.out.println(getAllTask());
        System.out.println("Все эпики:");
        System.out.println(getAllEpic());
        System.out.println("Все подзадачи:");
        System.out.println(getAllSubTask());
    }
}
