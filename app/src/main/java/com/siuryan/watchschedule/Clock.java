package com.siuryan.watchschedule;


import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class Clock {

    private TextView time;
    private Calendar now;

    public Clock(TextView time) {
        this.time = time;
        this.now = Calendar.getInstance();
    }

    public void update() {
        now = Calendar.getInstance();
        time.setText(String.format(Locale.US,"%s:%s %s",
                formatHour(now.get(Calendar.HOUR_OF_DAY)),
                formatMinute(now.get(Calendar.MINUTE)),
                formatAMPM(now.get(Calendar.AM_PM)))
        );
    }

    private String formatHour(int hour) {
        if (hour % 12 == 0) {
            return "12";
        }
        return String.valueOf(hour % 12);
    }

    private String formatMinute(int minute) {
        String formattedMinute = String.valueOf(minute);
        if (formattedMinute.length() == 1) {
            formattedMinute = "0" + formattedMinute;
        }
        return formattedMinute;
    }

    private String formatAMPM(int ampm) {
        return ampm == Calendar.AM ? "AM" : "PM";
    }
}
