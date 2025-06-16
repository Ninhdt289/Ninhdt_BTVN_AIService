package com.example.aisevice.data.interceptor

object TimeStampManage {
    private var timeDiff: Long = 0L
    val timeStamp: Long get() = System.currentTimeMillis() + timeDiff
}