package com.caregiving.services.android.caregiver.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by PC1 on 15/9/2016.
 */
public class DateUtils {

    public final static String SQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getCurrentDateNumber() {
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat("d").format(cal.getTime());
    }

    public static String getCurrentMonthName() {
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat("MMMM").format(cal.getTime());
    }

    public static String getCurrentDateFullString() {
        LocalDateTime localDateTime = new LocalDateTime();
        DateTimeFormatter format = DateTimeFormat.forPattern("d MMMM yyyy");
        return format.print(localDateTime);
    }

    public static String getCurrentTime() {
        LocalTime localTime = new LocalTime();
        DateTimeFormatter format = DateTimeFormat.forPattern("h:mm a");
        return format.print(localTime);
    }

    public static String formatTime(DateTime dateTime) {
        DateTimeFormatter format = DateTimeFormat.forPattern("h:mm a");
        return format.print(dateTime);
    }

    public static String formatTime(Date date) {
        return new SimpleDateFormat("h:mm a").format(date);
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }

    public static String formatDateString(String dateString) {
        DateTime dateTime = null;
        DateTimeFormatter dateTimeFormatter = null;
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            dateTime = dtf.parseDateTime(dateString);
            dateTimeFormatter = DateTimeFormat.forPattern("dd MMMM yyyy");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTimeFormatter.print(dateTime);
    }

    public static java.sql.Date convertJavaDateToSqlDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static java.util.Date convertSqlDateToJavaDate(java.sql.Date date) {
        return new java.util.Date(date.getTime());
    }

    public static String formatDateString(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }
}
