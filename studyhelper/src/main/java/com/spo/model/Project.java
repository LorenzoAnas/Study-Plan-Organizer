package com.spo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



public class Project {
    private String name;
    private LocalDate dueDate;
    private int tasks;
    private List<Task> tasksList;

    public Project(String name, LocalDate dueDate, int tasks) {
        this.name = name;
        this.dueDate = dueDate;
        this.tasks = tasks;
        this.tasksList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public int getTasks() {
        return tasks;
    }

    public List<Task> getTasksList() {
        return tasksList;
    }

    public int getDaysLeft() {
        return (int) LocalDate.now().until(dueDate).getDays();
    }

    public int getTasksLeft() {
        return tasksList.size();
    }

    public double getTaskDayRatio() {
        int daysLeft = getDaysLeft();
        if (daysLeft == 0) {
            return tasksList.size();
        } else {
            return (double) tasksList.size() / daysLeft;
        }
    }

    public void addTask(Task task) {
        tasksList.add(task);
    }
}


