package com.io.wordguard.ui.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    private static final String DATE_FORMAT = "dd/MM/yy hh:mm";

    public static final String DATE_FORMAT_ONLY_TIME = "hh:mm";
    public static final String DATE_FORMAT_ONLY_DATE = "DD/MM";

    public static Date stringToDate(String string) {
        if (string != null) {
            DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            try {
                return format.parse(string);
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String dateToString(Date date) {
        if (date != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                return dateFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else return "";
    }

    public static String dateToString(Date date, String format) {
        if (date != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
                return dateFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else return "";
    }

    public static String getTimeStringFromDate(Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            String fullDateTime[] = dateFormat.format(date).split(" ");
            return fullDateTime[1];
        } else return "";
    }

    public static String getDateStringFromDate(Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            String fullDateTime[] = dateFormat.format(date).split(" ");
            return fullDateTime[0];
        } else return "";
    }
}
