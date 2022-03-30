package com.slment.jsefuel.utils;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v4.media.RatingCompat;
import android.util.Pair;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.slment.jsefuel.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * This class extends TimePickerDialog with a new option: it is configurable
 * with a step to increment/decrement minutes. E.g. it is now possible to get a
 * time picker with, say, a 15 minute step (versus 1 minute in standard
 * TimePickerDialog).
 *
 * @author <a href="mailto:vit at cleverua.com">Vitaliy Khudenko</a>
 */
public class CustomTimePickerDialog extends TimePickerDialog {
    private final static int MINS_IN_HOUR = 60;
    private int lastMinute;
    private Pair<Integer, Integer> ignoreTimeChangeData;
    private final int step;
    private TimePicker mTimePicker;

    /**
     * @param context Parent.
     * @param callBack How parent is notified.
     * @param hourOfDay The initial hour.
     * @param minute The initial minute.
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     * @param minutesStep A step to change minutes.
     *
     * @throws IllegalArgumentException
     * <ul>
     *   <li>If (0 &lt; minutesStep &lt; 60) condition is not met.</li>
     *   <li>If sum of minutesSteps can not be equal 60.</li>
     * </ul>
     */
    public CustomTimePickerDialog(Context context, OnTimeSetListener callBack,
                                  int hourOfDay, int minute, boolean is24HourView, int minutesStep) {

        //  AlertDialog.THEME_HOLO_LIGHT <- deprecated
        //  android.R.style.Theme_Material_Light_Dialog_Alert.
        //  android.R.style.Theme_Holo_Light_Dialog_NoActionBar
        //  R.style.myTimePicker
        super(context, R.style.myTimePicker, callBack,
                hourOfDay, minute, is24HourView);

        lastMinute = minute;

        if (minutesStep <= 0) {
            throw new IllegalArgumentException("invalid minutesStep: should be a positive number");
        }
        if (MINS_IN_HOUR <= minutesStep) {
            throw new IllegalArgumentException("invalid minutesStep: should be less than 60");
        }
        if ((MINS_IN_HOUR % minutesStep) != 0) {
            throw new IllegalArgumentException("invalid minutesStep: sum of steps should equal 60");
        }

        step = minutesStep;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            mTimePicker = (TimePicker) findViewById(timePickerField.getInt(null));
            Field field = classForid.getField("minute");

            NumberPicker minuteSpinner = (NumberPicker) mTimePicker
                    .findViewById(field.getInt(null));
            minuteSpinner.setMinValue(0);
            minuteSpinner.setMaxValue((60 / step) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += step) {
                displayedValues.add(String.format("%02d", i));
            }
            minuteSpinner.setDisplayedValues(displayedValues
                    .toArray(new String[displayedValues.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if (ignoreTimeChangeData != null
                && ignoreTimeChangeData.first.intValue() == hourOfDay
                && ignoreTimeChangeData.second.intValue() == minute) {
            // reset the ignoreTimeChangeData
            ignoreTimeChangeData = null;
            return;
        }

        if ((minute % step) == 0) {
            // no need to fix minutes
            lastMinute = minute; // track lastMinute
            super.onTimeChanged(view, hourOfDay, minute);
        } else {
            // need to fix minutes

            // track the data we should ignore on the subsequent call
            ignoreTimeChangeData = new Pair<Integer, Integer>(hourOfDay, minute);

            if ((minute > lastMinute) && (minute != (MINS_IN_HOUR - 1))) {
                // we're going UP
                for (int i = step; i <= MINS_IN_HOUR; i += step) {
                    if (minute < i) {
                        minute = (i == MINS_IN_HOUR) ? 0 : i;
                        break;
                    }
                }
            } else {
                // we're going DOWN
                for (int i = (MINS_IN_HOUR - step); i >= 0; i -= step) {
                    if (minute > i) {
                        minute = i;
                        break;
                    }
                }
            }

            // this will call onTimeChanged() for hour change first, however
            // ignoreTimeChangeData enables us not to fall into an endless loop

//  this had to be removed because it caused exceptions in library calls
//  updating the time is now handled in the routine that calls this custom dialog
//            updateTime(hourOfDay, minute);
        }
    }
}
