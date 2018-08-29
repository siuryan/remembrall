package com.siuryan.watchschedule;


import java.util.ArrayList;

public class TaskList {

    private ArrayList<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    public ArrayList<Task> getTasks() { return tasks; }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public Task getTask(int position) { return tasks.get(position); }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public String[] getTaskContents() {
        String[] contents = new String[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            contents[i] = tasks.get(i).getContent();
        }
        return contents;
    }

    public void onlyToday() {
        int i = 0;
        while (i < tasks.size()) {
            if (tasks.get(i).getDueDate() != null && !Clock.isToday(tasks.get(i).getDueDate())) {
                tasks.remove(i);
            } else {
                i++;
            }
        }
    }

    @Override
    public String toString() {
        return tasks.toString();
    }
}
