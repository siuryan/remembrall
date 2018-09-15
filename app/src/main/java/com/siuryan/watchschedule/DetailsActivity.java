package com.siuryan.watchschedule;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static com.siuryan.watchschedule.Config.COMPLETE_URL;
import static com.siuryan.watchschedule.Config.DELETE_URL;
import static com.siuryan.watchschedule.Config.DETAILS_FORMAT;
import static com.siuryan.watchschedule.Config.GET_PROJECT_URL;
import static com.siuryan.watchschedule.TodoistHandler.parseJSONProject;

public class DetailsActivity extends WearableActivity {

    private TextView mTextView;
    private ImageButton mDeleteButton;
    private ImageButton mCompleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final Task task = (Task) getIntent().getSerializableExtra("TASK");

        mTextView = findViewById(R.id.task_description);
        String date = task.getDue() != null ? task.getDue().toString() : (task.getDueDate() != null ? task.getDueDate().toString() : "");
        String project = "";
        try {
            project = new GetProjectTask().execute(String.format(GET_PROJECT_URL, task.getProjectId())).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        project = parseJSONProject(project);

        mTextView.setText(String.format(DETAILS_FORMAT,
                task.getContent(),
                project,
                date,
                "p" + task.getPriority()));

        mDeleteButton = findViewById(R.id.delete_button);
        mCompleteButton = findViewById(R.id.complete_button);

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeleteTask().execute(String.format(DELETE_URL, task.getId()));
                Toast.makeText(DetailsActivity.this, "Deleted task", Toast.LENGTH_SHORT).show();
                closeDetails(task);
            }
        });

        mCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CompleteTask().execute(String.format(COMPLETE_URL, task.getId()));
                Toast.makeText(DetailsActivity.this, "Completed task", Toast.LENGTH_SHORT).show();
                closeDetails(task);
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    private void closeDetails(Task task) {
        Intent data = new Intent();
        data.putExtra("TASK_HANDLED", task);
        setResult(RESULT_OK, data);
        finish();
    }

    private static class DeleteTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Authorization", "Bearer " + Config.API_KEY);
                conn.setDoInput(true);
                // Starts the query
                conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class CompleteTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + Config.API_KEY);
                conn.setDoInput(true);
                // Starts the query
                conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class GetProjectTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + Config.API_KEY);
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                InputStream inputStream = conn.getInputStream();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }

                return responseStrBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
