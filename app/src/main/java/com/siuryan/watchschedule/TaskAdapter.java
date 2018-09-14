package com.siuryan.watchschedule;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private TaskList mTaskList;

    public TaskAdapter(TaskList list) {
        super();
        this.mTaskList = list;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskView taskView = new TaskView(parent.getContext());

        taskView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        return new TaskViewHolder(taskView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        holder.getTaskView().setTask(mTaskList.getTask(position));
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        public TaskView mView;

        public TaskViewHolder(View content) {
            super(content);
            this.mView = (TaskView) content;
        }

        public TaskView getTaskView() {
            return mView;
        }
    }

}