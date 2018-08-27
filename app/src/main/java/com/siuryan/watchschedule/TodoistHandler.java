package com.siuryan.watchschedule;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;


public class TodoistHandler {

    public static void parseJSON(TaskList items, String input) {
        try {
            JSONArray jsonArray = new JSONArray(input);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                Task task = new Task(
                        jsonObject.getLong("id"),
                        jsonObject.getLong("project_id"),
                        jsonObject.getString("content"),
                        jsonObject.getInt("order"),
                        jsonObject.getInt("priority"),
                        jsonObject.getString("url"),
                        jsonObject.getBoolean("completed"),
                        jsonObject.getJSONObject("due").getString("date")
                );

                items.addTask(task);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
