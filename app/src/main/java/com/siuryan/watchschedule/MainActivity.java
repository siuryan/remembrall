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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends WearableActivity {

    // Milliseconds between waking processor/screen for updates when active
    private static final long ACTIVE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);
    // 60 seconds for updating the clock in active mode
    private static final long MINUTE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(60);

    // Handler for updating the clock in active mode
    private final Handler mScheduleHandler = new UpdateScheduleHandler(this);

    private TextView mTime;
    private TextView mRemainingTime;
    private TextView mNextActivity;
    private BoxInsetLayout mBackground;
    private Clock mClock;
    private Schedule mSchedule;

    private int mActiveBackgroundColor;

    private HashMap<String, String> items = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();

        InputStream inputStream = null;
        try {
            inputStream = new ReadXMLTask().execute("https://raw.githubusercontent.com/siuryan/remembrall/master/schedule.xml").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        ReadXML.parseXML(items, inputStream);
        Log.d("testing", items.toString());

        mTime = findViewById(R.id.time);
        mRemainingTime = findViewById(R.id.remaining_time);
        mNextActivity = findViewById(R.id.next_activity);
        mBackground = findViewById(R.id.background);

        mClock = new Clock(mTime);
        mSchedule = new Schedule(items, mRemainingTime, mNextActivity);

        mActiveBackgroundColor = getResources().getColor(R.color.dark_grey, null);

        mScheduleHandler.sendEmptyMessage(R.id.msg_update);
    }

    private void updateDisplayAndSetRefresh() {
        mClock.update();
        if (mSchedule.update()) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            assert vibrator != null;
            vibrator.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        }

        if (!this.isAmbient()) {
            // In Active mode update directly via handler.
            long timeMs = System.currentTimeMillis();
            long delayMs = ACTIVE_INTERVAL_MS - (timeMs % ACTIVE_INTERVAL_MS);
            mScheduleHandler.sendEmptyMessageDelayed(R.id.msg_update, delayMs);

            int minsRemaining = mSchedule.minutesToNextActivity(mSchedule.nameOfNextActivity(Calendar.getInstance()));

            if (minsRemaining <= 10) {
                mActiveBackgroundColor = (Integer) new ArgbEvaluator().evaluate((minsRemaining / 10f),
                        ContextCompat.getColor(this, R.color.red),
                        ContextCompat.getColor(this, R.color.dark_grey));
                mBackground.setBackgroundColor(mActiveBackgroundColor);
            }
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

    private static class ReadXMLTask extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                return conn.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
