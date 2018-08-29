package com.siuryan.watchschedule;

import android.animation.ArgbEvaluator;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.ProgressSpinner;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends WearableActivity {

    // Milliseconds between waking processor/screen for updates when active
    private static final long ACTIVE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);
    // 30 seconds for updating the clock in active mode
    private static final long MINUTE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(30);

    // Handler for updating the clock in active mode
    private final Handler mScheduleHandler = new UpdateScheduleHandler(this);

    private TextView mTime;
    private ListView mTasksListView;
    private BoxInsetLayout mBackground;
    private Clock mClock;

    private TaskAdapter mTaskAdapter;

    private int mActiveBackgroundColor;

    private TaskList todayItems = new TaskList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();

        String input = null;
        try {
            input = new GetTasksTask().execute("https://beta.todoist.com/API/v8/tasks").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        TodoistHandler.parseJSON(todayItems, input);

        mTime = findViewById(R.id.time);
        mTasksListView = findViewById(R.id.tasks);
        mBackground = findViewById(R.id.background);

        mClock = new Clock(mTime);

        todayItems.onlyToday();
        mTaskAdapter = new TaskAdapter(this, todayItems);
        mTasksListView.setAdapter(mTaskAdapter);

        mActiveBackgroundColor = getResources().getColor(R.color.dark_grey, null);

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
