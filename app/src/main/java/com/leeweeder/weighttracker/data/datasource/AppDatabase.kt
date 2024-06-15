package com.leeweeder.weighttracker.data.datasource

import com.leeweeder.weighttracker.domain.model.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Log::class],
    version = 1
)
@TypeConverters(
    Converters::class
)
abstract class AppDatabase: RoomDatabase() {
    abstract val logDao: LogDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}