package com.spo.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Project {
    private String name;
    private LocalDate dueDate;
    private int tasks;

    public Project(String name, LocalDate dueDate, int tasks) {
        this.name = name;
        this.dueDate = dueDate;
        this.tasks = tasks;
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

    public int getDaysLeft() {
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }


    public double getTaskDayRatio() {
        int daysLeft = getDaysLeft();
        if (daysLeft == 0) {
            return tasks;
        } else {
            return (double) tasks / daysLeft;
        }
    }
}


