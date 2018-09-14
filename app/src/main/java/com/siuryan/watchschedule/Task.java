package com.siuryan.watchschedule;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task implements Serializable {

    private long id;
    private long projectId;
    private String content;
    private int order;
    private int priority;
    private String url;
    private boolean completed;
    private LocalDate dueDate;
    private LocalDateTime due;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getDue() {
        return due;
    }

    public void setDue(LocalDateTime due) {
        this.due = due;
    }

    public Task(long id, long projectId, String content, int order, int priority, String url, boolean completed, LocalDate dueDate, LocalDateTime due) {
        this.id = id;
        this.projectId = projectId;
        this.content = content;
        this.order = order;
        this.priority = priority;
        this.url = url;
        this.completed = completed;
        this.dueDate = dueDate;
        this.due = due;
    }

    @Override
    public String toString() {
        return this.content;
    }
}
