package com.siuryan.watchschedule;


import android.util.Log;

import java.util.ArrayList;

public class TaskList {

    private ArrayList<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

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

    public Object[] getTodayTaskContents() {
        ArrayList<String> contents = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (Clock.isToday(tasks.get(i).getDue())) {
                Log.d("testing", tasks.get(i).getContent());
                contents.add(tasks.get(i).getContent());
            }
        }
        return contents.toArray();
    }

    @Override
    public String toString() {
        return tasks.toString();
    }
}
