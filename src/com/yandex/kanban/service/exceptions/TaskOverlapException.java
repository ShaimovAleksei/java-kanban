package com.yandex.kanban.service.exceptions;

public class TaskOverlapException extends RuntimeException {
    public TaskOverlapException(String message) {
        super(message);
    }

    public TaskOverlapException(String message, Throwable cause) {
        super(message, cause);
    }
}
