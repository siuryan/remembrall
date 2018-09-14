package com.siuryan.watchschedule;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;

public class TaskView extends RelativeLayout {

    private Task task;
    private TextView content;
    private TextView time;

    public TaskView(Context context) {
        super(context);

        inflate(getContext(), R.layout.task_list_element, this);
        content = findViewById(R.id.label);
        time = findViewById(R.id.task_time);
    }

    public TaskView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(getContext(), R.layout.task_list_element, this);
        content = findViewById(R.id.label);
        time = findViewById(R.id.task_time);
    }

    public TaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(getContext(), R.layout.task_list_element, this);
        content = findViewById(R.id.label);
        time = findViewById(R.id.task_time);
    }

    public void setTask(Task task) {
        this.task = task;

        content.setText(task.getContent());

        if (task.getDue() != null) {
            time.setText(task.getDue().format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            time.setText("");
        }
    }

    public Task getTask() {
        return task;
    }

}
