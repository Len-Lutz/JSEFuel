package com.slment.jsefuel.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.slment.jsefuel.R;
import com.slment.jsefuel.utils.CustomTimePickerDialog;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

public class DateTimePopups {
    public static String LOG_TAG = "##### DateTimePopups: ";
    public static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    public static SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat sdfDateTime24 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat sdfTime12 = new SimpleDateFormat("hh:mm aa");
    public static String strWorkingDate;
    public static String strWorkingTime;
    static Date workingDate = null;
    static Calendar workingCalendar = Calendar.getInstance();
    static DatePickerDialog datePicker;
    static DatePickerDialog.OnDateSetListener dateSetListener;
    static CustomTimePickerDialog timePicker;
    static CustomTimePickerDialog.OnTimeSetListener timeSetListener;

    public static void openDatePicker(Context context, View v, EditText dateField, String fieldName) {
        strWorkingDate = dateField.getText().toString();
        try {
            workingCalendar.setTime(sdf.parse(strWorkingDate, new ParsePosition(0)));
        } catch (DateTimeParseException e) {
            workingCalendar = Calendar.getInstance();
        }
        int year = workingCalendar.get(Calendar.YEAR);
        int month = workingCalendar.get(Calendar.MONTH);
        int day = workingCalendar.get(Calendar.DAY_OF_MONTH);

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                workingCalendar.set(year, month, day);
                strWorkingDate = sdf.format(workingCalendar.getTime());
                dateField.setText(strWorkingDate);
            }
        };

        datePicker = new DatePickerDialog(context,
                R.style.myCalendar,
                dateSetListener,
                year, month, day);

        datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.getWindow().setGravity(Gravity.CENTER);
        datePicker.setTitle("Select " + fieldName + " Date:");
        datePicker.show();
    }

    // we use a CustomTimePickerDialog so we can set the allowable minutes
    // a step of 1 will display the normal 00 - 59 minutes
    // a step of 15 will allow only 00, 15, 30, and 45 minutes to be displayed and selected
    public static void openTimePicker(Context context, View v, EditText timeField,
                                     String fieldName, int step) {
        strWorkingTime = timeField.getText().toString();
        Log.println(Log.VERBOSE, LOG_TAG, "strWorkingTime = " + strWorkingTime);
        try {
            workingCalendar.setTime(sdfTime12.parse(strWorkingTime, new ParsePosition(0)));
        } catch (DateTimeParseException e) {
            workingCalendar = Calendar.getInstance();
        }
        int year = workingCalendar.get(Calendar.YEAR);
        int month = workingCalendar.get(Calendar.MONTH);
        int day = workingCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = workingCalendar.get(Calendar.HOUR);
        char letter = strWorkingTime.charAt(6);
        if ((letter =='P') && ( hour < 12)) {
            hour += 12;
        } else if ((letter =='A') && ( hour == 12)) {
            hour = 0;
        }
        int minute = workingCalendar.get(Calendar.MINUTE);

        timeSetListener = new CustomTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                // here the minute is actually the index of the array of acceptable minutes
                // so we must mutiply the minute by the step to set the actual time
                minute *= step;
                workingCalendar.set(year, month, day, hour, minute);
                strWorkingTime = sdfTime12.format(workingCalendar.getTime());
                timeField.setText(strWorkingTime);
            }
        };

        //  minute must be set to the "index" of the array of acceptable minutes based on the "steps"
        //  so we divide the actual minute by the step
        if (minute > 0) minute = minute / step;
        timePicker = new CustomTimePickerDialog(context,
        timeSetListener,
        hour, minute,
        false, step);

        timePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePicker.getWindow().setGravity(Gravity.CENTER);
        timePicker.setTitle("  Select " + fieldName +" Time:");
        timePicker.show();
    }
}
