public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Task task1 = new Task("ремонт","Ремонт в комнате");
        Task task2 = new Task("покупка","покупка мебели");

        TaskManager taskManager = new TaskManager();
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("стройка", "спланировать весь цикл постройки дома");
        taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask("проект", "заказать проет");
        SubTask subTask2 = new SubTask("смета", "посчитать смету");
        taskManager.createSubTask(subTask1, epic1);
        taskManager.createSubTask(subTask2, epic1);

        Epic epic2 = new Epic("отпуск", "спланировать отпуск");
        taskManager.createEpic(epic2);

        SubTask subTask3 = new SubTask("путевка", "выбрать тур в тайланд");
        taskManager.createSubTask(subTask3, epic2);

        taskManager.printAllTask();
        System.out.println();
        System.out.println();

        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);

        subTask1.setTaskStatus(TaskStatus.DONE);
        subTask2.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subTask1);
        taskManager.updateSubtask(subTask2);

        taskManager.printAllTask();
        System.out.println();
        System.out.println();

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic2.getId());

        taskManager.printAllTask();
    }

}
