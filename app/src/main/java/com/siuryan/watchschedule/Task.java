package com.siuryan.watchschedule;


public class Task {

    private long id;
    private long projectId;
    private String content;
    private int order;
    private int priority;
    private String url;
    private boolean completed;
    private String due;

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

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public Task(long id, long projectId, String content, int order, int priority, String url, boolean completed, String due) {
        this.id = id;
        this.projectId = projectId;
        this.content = content;
        this.order = order;
        this.priority = priority;
        this.url = url;
        this.completed = completed;
        this.due = due;
    }

    @Override
    public String toString() {
        return this.content;
    }
}
