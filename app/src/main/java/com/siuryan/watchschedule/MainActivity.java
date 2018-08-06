package com.siuryan.watchschedule;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();

        mTime = findViewById(R.id.time);
        mRemainingTime = findViewById(R.id.remaining_time);
        mNextActivity = findViewById(R.id.next_activity);
        mBackground = findViewById(R.id.background);

        mClock = new Clock(mTime);
        mSchedule = new Schedule(mRemainingTime, mNextActivity);

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

}
