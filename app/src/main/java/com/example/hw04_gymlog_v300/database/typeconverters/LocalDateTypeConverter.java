package com.example.hw04_gymlog_v300.database.typeconverters;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTypeConverter {

    @TypeConverter
    public static Long convertDateToLong(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @TypeConverter
    public static LocalDateTime convertLongToDate(Long epochMilli) {
        if (epochMilli == null) {
            return null;
        }
        return Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
