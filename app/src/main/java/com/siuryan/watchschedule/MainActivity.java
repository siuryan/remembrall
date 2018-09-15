package com.siuryan.watchschedule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.siuryan.watchschedule.Config.GET_TASKS_URL;

public class MainActivity extends WearableActivity {

    // Milliseconds between waking processor/screen for updates when active
    private static final long ACTIVE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);
    // 30 seconds for updating the clock in active mode
    private static final long MINUTE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(30);

    // Handler for updating the clock in active mode
    private final Handler mScheduleHandler = new UpdateScheduleHandler(this);

    private TextView mTime;
    private WearableRecyclerView mTasksRecyclerView;
    private BoxInsetLayout mBackground;
    private Clock mClock;

    private ImageButton mAddButton;

    private TaskAdapter mTaskAdapter;

    private int mActiveBackgroundColor;

    private TaskList todayItems = new TaskList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_LONG).show();
        } else {
            String input = null;
            try {
                input = new GetTasksTask().execute(GET_TASKS_URL).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            TodoistHandler.parseJSON(todayItems, input);
        }

        mTime = findViewById(R.id.time);
        mTasksRecyclerView = findViewById(R.id.tasks);
        mBackground = findViewById(R.id.background);

        mClock = new Clock(mTime);

        todayItems.onlyToday();
        mTaskAdapter = new TaskAdapter(this, todayItems);
        mTasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTasksRecyclerView.setAdapter(mTaskAdapter);

        mActiveBackgroundColor = getResources().getColor(R.color.dark_grey, null);

        mAddButton = findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

        mScheduleHandler.sendEmptyMessage(R.id.msg_update);
    }

    private void updateDisplayAndSetRefresh() {
        mClock.update();
        /*
        if (mSchedule.update()) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            assert vibrator != null;
            vibrator.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        }
        */

        if (!this.isAmbient()) {
            // In Active mode update directly via handler.
            long timeMs = System.currentTimeMillis();
            long delayMs = ACTIVE_INTERVAL_MS - (timeMs % ACTIVE_INTERVAL_MS);
            mScheduleHandler.sendEmptyMessageDelayed(R.id.msg_update, delayMs);

            /*
            int minsRemaining = mSchedule.minutesToNextActivity(mSchedule.nameOfNextActivity(Calendar.getInstance()));

            if (minsRemaining <= 10) {
                mActiveBackgroundColor = (Integer) new ArgbEvaluator().evaluate((minsRemaining / 10f),
                        ContextCompat.getColor(this, R.color.red),
                        ContextCompat.getColor(this, R.color.dark_grey));
                mBackground.setBackgroundColor(mActiveBackgroundColor);
            }
            */
        } else {
            long timeMs = System.currentTimeMillis();
            long delayMs = MINUTE_INTERVAL_MS - (timeMs % MINUTE_INTERVAL_MS);
            mScheduleHandler.sendEmptyMessageDelayed(R.id.msg_update, delayMs);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Task taskToRemove = (Task) data.getSerializableExtra("TASK_HANDLED");
            todayItems.removeTask(taskToRemove);
            mTaskAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        mBackground.setBackgroundColor(Color.BLACK);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        mBackground.setBackgroundColor(mActiveBackgroundColor);
    }

    @Override
    public void onDestroy() {
        mScheduleHandler.removeMessages(R.id.msg_update);

        super.onDestroy();
    }

    private static abstract class UpdateHandler extends Handler {

        private final WeakReference<MainActivity> mainActivityWeakReference;

        public UpdateHandler(MainActivity reference) {
            mainActivityWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message message) {
            MainActivity mainActivity = mainActivityWeakReference.get();

            if (mainActivity == null) {
                return;
            }
            switch (message.what) {
                case R.id.msg_update:
                    handleUpdate(mainActivity);
                    break;
            }
        }

        /**
         * Handle the update within this method.
         *
         * @param stopwatchActivity The activity that handles the update.
         */
        public abstract void handleUpdate(MainActivity stopwatchActivity);
    }

    private static class UpdateScheduleHandler extends UpdateHandler {

        public UpdateScheduleHandler(MainActivity reference) {
            super(reference);
        }

        @Override
        public void handleUpdate(MainActivity mainActivity) {
            mainActivity.updateDisplayAndSetRefresh();
        }
    }

    private static class GetTasksTask extends AsyncTask<String, Void, String> {

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
