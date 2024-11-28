package com.leongao.magtime.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String date(String datePattern) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        return dateFormat.format(date);
    }
}
