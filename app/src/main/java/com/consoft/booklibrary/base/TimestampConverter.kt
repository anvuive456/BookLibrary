package com.consoft.booklibrary.base

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime


object TimestampConverter {
  @TypeConverter
  fun fromTimestamp(value: String?): LocalDate? {
    return value?.let { LocalDate.parse(it) }
  }

  @TypeConverter
  fun dateToTimestamp(date: LocalDate?): String? {
    return date?.toString()
  }
}