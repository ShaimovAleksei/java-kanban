package com.yandex.kanban.service;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }

    private Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        head = new Node(null, null, null);
        tail = new Node(null, head, null);
        head.next = tail;
    }

    @Override
    public void add(Task task) {
        Task copy = copyTask(task);

        remove(copy.getId());
        linkLast(copy);
    }

    @Override
    public void remove(int id) {
        Node node = history.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }


    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head.next;
        while (current != tail) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    private void linkLast(Task task) {
        Task copy = copyTask(task);
        Node newNode = new Node(copy, tail.prev, tail);

        tail.prev.next = newNode;
        tail.prev = newNode;

        history.put(copy.getId(), newNode);
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private Task copyTask(Task task) {
        Task copy;
        switch (task.getTaskType()) {
            case EPIC:
                Epic epic = (Epic) task;
                copy = new Epic(epic.getName(), epic.getDescription());
                break;
            case SUBTASK:
                SubTask subTask = (SubTask) task;
                copy = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getEpicID());
                break;
            default:
                copy = new Task(task.getName(), task.getDescription(), TaskType.TASK);
        }

        copy.setId(task.getId());
        copy.setTaskStatus(task.getTaskStatus());
        return copy;
    }

    public void printHistory() {
        System.out.println("История просмотров");
        for (Node node : history.values()) {
            System.out.println(node.task);
        }
    }
}
