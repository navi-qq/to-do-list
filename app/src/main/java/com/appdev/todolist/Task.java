package com.appdev.todolist;


import java.util.Date;


public class Task {

    String taskDescription;
    public boolean taskStatus;
    Date reminderDate;

    Task(String taskDescription, boolean taskStatus, Date reminderDate) {
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.reminderDate = reminderDate;
    }

    void setTaskStatus(boolean taskStatus) {
        this.taskStatus = taskStatus;
    }

    void setNewTaskDescription(String updatedTaskDescription) {
        this.taskDescription = updatedTaskDescription;
    }
}
