package com.siuryan.watchschedule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private TaskList mTaskList;
    private Context context;

    public TaskAdapter(Context context, TaskList list) {
        this.context = context;
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

    class TaskViewHolder extends RecyclerView.ViewHolder {

        public TaskView mView;

        public TaskViewHolder(final View content) {
            super(content);
            this.mView = (TaskView) content;
            this.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, DetailsActivity.class);
                    i.putExtra("TASK", mView.getTask());
                    ((Activity) context).startActivityForResult(i, 0);
                    mView.setSelected(true);
                }
            });
        }

        public TaskView getTaskView() {
            return mView;
        }
    }

}