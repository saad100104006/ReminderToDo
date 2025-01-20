package com.tanvir.reminder.data

import androidx.room.TypeConverters
import com.tanvir.reminder.data.entity.ToDoEntity
import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [ToDoEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ToDoDatabase : RoomDatabase() {
    abstract val dao: ReminderDao
}
