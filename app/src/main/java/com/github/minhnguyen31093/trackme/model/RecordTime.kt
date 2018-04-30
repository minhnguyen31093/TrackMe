package com.github.minhnguyen31093.trackme.model

data class RecordTime(var hours: Int, var minutes: Int, var seconds: Int) {
    companion object {
        fun convertToRecordTime(milliseconds: Long): RecordTime {
            val seconds = (milliseconds / 1000).toInt() % 60
            val minutes = (milliseconds / (1000 * 60) % 60).toInt()
            val hours = (milliseconds / (1000 * 60 * 60) % 24).toInt()
            return RecordTime(hours, minutes, seconds)
        }
    }
}