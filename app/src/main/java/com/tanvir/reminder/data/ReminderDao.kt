package com.tanvir.reminder.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tanvir.reminder.data.entity.ToDoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReminder(toDoEntity: ToDoEntity): Long

    @Delete
    suspend fun deleteReminder(toDoEntity: ToDoEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateReminder(toDoEntity: ToDoEntity)

    @Query(
        """
            SELECT *
            FROM todoentity
        """
    )
    fun getAllReminder(): Flow<List<ToDoEntity>>

    @Query(
        """
            SELECT *
            FROM todoentity
            WHERE strftime('%Y-%m-%d', reminderTime / 1000, 'unixepoch', 'localtime') = :date
            ORDER BY reminderTime ASC
        """
    )
    fun getRemindersForDate(date: String): Flow<List<ToDoEntity>>

    @Query("SELECT * FROM todoentity WHERE id = :id")
    suspend fun getReminderById(id: Long): ToDoEntity?
}
