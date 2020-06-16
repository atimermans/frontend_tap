package org.vaadin.example;

import java.io.Serializable;
import java.time.LocalDate;


@SuppressWarnings("serial")
public class Task implements Serializable, Cloneable {

    private Long id;
    private String taskTitle = "";
    private String taskDescription = "";
    private LocalDate deadLine;
    private TaskStatus status;
    private TaskPriority priority;
    private String list = "";

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskTitle() {
        return taskTitle;
    }
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public LocalDate getDeadLine() {
        return deadLine;
    }
    public void setDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

    public TaskStatus getStatus() {
        return status;
    }
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public String getList() {
        return list;
    }
    public void setList(String list) {
        this.list = list;
    }

    public boolean isPersisted() {
        return id != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.id == null) {
            return false;
        }

        if (obj instanceof Task && obj.getClass().equals(getClass())) {
            return this.id.equals(((Task) obj).id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (id == null ? 0 : id.hashCode());
        return hash;
    }

    @Override
    public Task clone() throws CloneNotSupportedException {
        return (Task) super.clone();
    }

    @Override
    public String toString() {
        return taskTitle + " - " + taskDescription;
    }
}