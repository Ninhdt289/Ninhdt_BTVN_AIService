package com.example.aisevice.data.interceptor

object TimeStampManage {
    private var timeDiff: Long = 0L
    val timeStamp: Long get() = System.currentTimeMillis() + timeDiff

    fun setTimeStamp(serverTimestamp: Long) {
        val clientTimestamp = System.currentTimeMillis()
        timeDiff = serverTimestamp - clientTimestamp
    }
}