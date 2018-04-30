package com.github.minhnguyen31093.trackme.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(Record::class), version = 1)
abstract class RecordDatabase : RoomDatabase() {

    abstract fun weatherDataDao(): RecordDao

    companion object {
        private var INSTANCE: RecordDatabase? = null

        fun getInstance(context: Context): RecordDatabase? {
            if (INSTANCE == null) {
                synchronized(RecordDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecordDatabase::class.java, "record.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}