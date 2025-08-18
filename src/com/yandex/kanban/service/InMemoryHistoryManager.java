package com.yandex.kanban.service;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.SubTask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

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
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    private void linkLast(Task task) {
        Task copy = copyTask(task);
        Node newNode = new Node(copy, tail, null);

        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;

        history.put(copy.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    private Task copyTask(Task task) {
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            Epic copy = new Epic(epic.getName(), epic.getDescription());
            copy.setId(epic.getId());
            copy.setTaskStatus(epic.getTaskStatus());
            return copy;
        } else if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            SubTask copy = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getEpicID());
            copy.setId(subTask.getId());
            copy.setTaskStatus(subTask.getTaskStatus());
            return copy;
        } else {
            Task copy = new Task(task.getName(), task.getDescription());
            copy.setId(task.getId());
            copy.setTaskStatus(task.getTaskStatus());
            return copy;
        }
    }

    public void printHistory() {
        System.out.println("История просмотров");
        for (Node node : history.values()) {
            System.out.println(node.task);
        }
    }
}
