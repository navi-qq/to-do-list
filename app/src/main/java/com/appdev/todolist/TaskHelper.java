package com.appdev.todolist;

public class TaskHelper {
    private String taskDescription;
    private String taskStatus;

    public TaskHelper() {
    }

    public TaskHelper(String taskDescription, String taskStatus) {
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public String getTaskStatus() {
        taskStatus = Boolean.parseBoolean(taskStatus) ? "Completed" : "Not Completed";
        return taskStatus;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

}
