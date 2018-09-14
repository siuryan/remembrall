package com.siuryan.watchschedule;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

public class DetailsActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Task task = (Task) getIntent().getSerializableExtra("TASK");

        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setText(task.getContent());

        // Enables Always-on
        setAmbientEnabled();
    }
}
