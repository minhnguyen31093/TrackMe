package com.github.minhnguyen31093.trackme.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface RecordDao {
    @Query("SELECT * FROM Record ORDER BY dateTime DESC LIMIT 1")
    fun getLatestRecord(): Flowable<Record>

    @Query("SELECT * FROM Record ORDER BY dateTime DESC")
    fun getAllRecords(): Flowable<List<Record>>

    @Query("SELECT * FROM Record ORDER BY dateTime DESC LIMIT:limit OFFSET:offset")
    fun getRecords(limit: Int, offset: Int): Flowable<List<Record>>

    @Insert(onConflict = REPLACE)
    fun insertRecord(record: Record)
}