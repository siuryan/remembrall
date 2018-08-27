package com.siuryan.watchschedule;


import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static Calendar parseDate(String exp) {
        Pattern p = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        Matcher m = p.matcher(exp);

        int year = 0, month = 0, day = 0;

        if (m.matches()) {
            year = Integer.parseInt(m.group(1));
            month = Integer.parseInt(m.group(2));
            day = Integer.parseInt(m.group(3));
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal;
    }

    public static boolean isToday(String exp) {
        Calendar cal1 = parseDate(exp);
        Calendar cal2 = Calendar.getInstance();

        Log.d("testing", cal1.toString());
        Log.d("testing", cal2.toString());

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) + 1 &&
                cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE);
    }
}
