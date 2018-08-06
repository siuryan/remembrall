package com.siuryan.watchschedule;


import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Schedule {

    private HashMap<String, String> items;

    private TextView remainingTime;
    private TextView nextActivity;

    private String currentNextActivity = "";
    private boolean hasVibrated = false;

    public Schedule(HashMap<String, String> items, TextView remainingTime, TextView nextActivity) {
        this.items = items;
        this.remainingTime = remainingTime;
        this.nextActivity = nextActivity;
    }

    public boolean update() {
        String nextActivityName = nameOfNextActivity(Calendar.getInstance());
        int minutesToActivity = minutesToNextActivity(nextActivityName);
        int hoursToActivity = hoursToNextActivity(minutesToActivity);

        setRemainingTime(minutesToActivity % 60, hoursToActivity);
        nextActivity.setText(String.format("%s: %s",
                "Your next scheduled activity is",
                nextActivityName));

        if (!currentNextActivity.equals(nextActivityName)) {
            hasVibrated = false;
        }

        if (minutesToActivity <= 10 && !hasVibrated) {
            hasVibrated = true;
            currentNextActivity = nextActivityName;
            return true;
        }

        currentNextActivity = nextActivityName;
        return false;

    }

    private void setRemainingTime(int minutesToNextActivity, int hoursToNextActivity) {
        if (minutesToNextActivity == 1 && hoursToNextActivity == 0) {
            remainingTime.setText(String.format(Locale.US,
                    "%d min",
                    minutesToNextActivity));
        } else if (minutesToNextActivity == 1 && hoursToNextActivity == 1) {
            remainingTime.setText(String.format(Locale.US,
                    "%d hr %d min",
                    hoursToNextActivity, minutesToNextActivity));
        } else if (hoursToNextActivity == 1) {
            remainingTime.setText(String.format(Locale.US,
                    "%d hr %d mins",
                    hoursToNextActivity, minutesToNextActivity));
        } else {
            remainingTime.setText(String.format(Locale.US,
                    "%d hrs %d mins",
                    hoursToNextActivity, minutesToNextActivity));
        }
    }

    private int minutesToNextActivity(String nameOfNextActivity) {
        String val = items.get(nameOfNextActivity);

        Calendar activityTime = parseCalendar(val);
        double diff = subtractCalendars(activityTime, Calendar.getInstance());

        return (int) Math.round(diff / (1000 * 60));
    }

    private int hoursToNextActivity(int minutes) {
        return minutes / 60;
    }

    private String nameOfNextActivity(Calendar time) {
        String kingKey = "";
        Calendar kingTime = Calendar.getInstance();
        kingTime.add(Calendar.HOUR, 24);
        kingTime.add(Calendar.HOUR_OF_DAY, 24);

        for (Map.Entry<String, String> item : items.entrySet()) {
            String key = item.getKey();
            String val = item.getValue();

            Calendar compare = parseCalendar(val);

            if (compare.compareTo(time) < 0) {
                compare.add(Calendar.HOUR, 24);
            }

            if (subtractCalendars(compare, time) < subtractCalendars(kingTime, time)) {
                kingTime = compare;
                kingKey = key;
            }
        }

        return kingKey;
    }

    private long subtractCalendars(Calendar cal1, Calendar cal2) {
        return cal1.getTimeInMillis() - cal2.getTimeInMillis();
    }

    private Calendar parseCalendar(String exp) {
        Pattern p = Pattern.compile("(\\d+):(\\d+) ([AP]M)");
        Matcher m = p.matcher(exp);

        int hour = 0, minute = 0;
        String ampm = "AM";

        if (m.matches()) {
            hour = Integer.parseInt(m.group(1));
            minute = Integer.parseInt(m.group(2));
            ampm = m.group(3);
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.AM_PM, ampm.equals("AM") ? Calendar.AM : Calendar.PM);
        cal.set(Calendar.HOUR_OF_DAY, hour + 12 * cal.get(Calendar.AM_PM));

        return cal;
    }

}
