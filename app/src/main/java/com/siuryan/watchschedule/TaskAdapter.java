package com.siuryan.watchschedule;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class TaskAdapter extends ArrayAdapter<Task> {

    private Context mContext;
    private TaskList mTaskList;

    public TaskAdapter(Context context, TaskList list) {
        super(context, 0, list.getTasks());
        mContext = context;
        mTaskList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        ViewHolder viewHolder;

        if (convertView == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.task_list_element, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.content = listItem.findViewById(R.id.label);
            viewHolder.time = listItem.findViewById(R.id.task_time);

            listItem.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) listItem.getTag();
        }

        Task currentTask = mTaskList.getTask(position);

        viewHolder.content.setText(currentTask.getContent());

        if (currentTask.getDue() != null) {
            viewHolder.time.setText(currentTask.getDue().format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            viewHolder.time.setText("");
        }

        return listItem;
    }

    static class ViewHolder {
        TextView content;
        TextView time;
    }

}