package com.github.minhnguyen31093.trackme.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "Record")
data class Record(
        @ColumnInfo(name = "points") var points: String,
        @ColumnInfo(name = "distance") var distance: Double,
        @ColumnInfo(name = "avgSpeed") var avgSpeed: Double,
        @ColumnInfo(name = "recordTime") var recordTime: Long,
        @ColumnInfo(name = "mapImage") var mapImage: String,
        @ColumnInfo(name = "dateTime") var dateTime: Long) {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true) var id: Long = 0

    companion object {
        fun convertPointsToString(points: ArrayList<RecordLocation>):String {
            return Gson().toJson(points)
        }

        fun convertPointsToObject(points: String):ArrayList<RecordLocation> {
            val listType = object : TypeToken<ArrayList<RecordLocation>>() {}.getType()
            return Gson().fromJson(points, listType)
        }
    }
}