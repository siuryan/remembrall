package com.siuryan.watchschedule;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class TodoistHandler {

    public static void parseJSON(TaskList items, String input) {
        try {
            JSONArray jsonArray = new JSONArray(input);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                Task task;

                if (jsonObject.has("due")) {

                    if (jsonObject.getJSONObject("due").has("datetime")) {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(jsonObject.getJSONObject("due").getString("datetime"), dateTimeFormatter)
                                .withZoneSameInstant(ZoneId.systemDefault());
                        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

                        task = new Task(
                                jsonObject.getLong("id"),
                                jsonObject.getLong("project_id"),
                                jsonObject.getString("content"),
                                jsonObject.getInt("order"),
                                jsonObject.getInt("priority"),
                                jsonObject.getString("url"),
                                jsonObject.getBoolean("completed"),
                                localDateTime.toLocalDate(),
                                localDateTime
                        );
                    } else {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
                        LocalDate localDate = LocalDate.from(dateTimeFormatter.parse(jsonObject.getJSONObject("due").getString("date")));

                        task = new Task(
                                jsonObject.getLong("id"),
                                jsonObject.getLong("project_id"),
                                jsonObject.getString("content"),
                                jsonObject.getInt("order"),
                                jsonObject.getInt("priority"),
                                jsonObject.getString("url"),
                                jsonObject.getBoolean("completed"),
                                localDate,
                                null
                        );
                    }

                } else {
                    task = new Task(
                            jsonObject.getLong("id"),
                            jsonObject.getLong("project_id"),
                            jsonObject.getString("content"),
                            jsonObject.getInt("order"),
                            jsonObject.getInt("priority"),
                            jsonObject.getString("url"),
                            jsonObject.getBoolean("completed"),
                            null,
                            null
                    );
                }

                items.addTask(task);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String parseJSONProject(String input) {
        try {
            JSONObject jsonObject = new JSONObject(input);

            if (jsonObject.has("name")) {
                return jsonObject.getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}
