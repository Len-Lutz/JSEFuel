package com.slment.jsefuel.utils;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date timestampToDate(Long value) {return value == null ? null : new Date(value);}

    @TypeConverter
    public static Long dateToTimestamp(Date date) {return date == null ? null : date.getTime();}
}

