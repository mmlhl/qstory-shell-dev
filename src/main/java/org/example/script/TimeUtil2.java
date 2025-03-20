package org.example.script;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil2 {
    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String getCustomFormat(String pattern) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }
}