package com.example.rsshool2021_android_task_pomodoro

interface StopwatchListener {

    fun start(id: Int, currentMs: Long,value: Long)

    fun stop(id: Int, currentMs: Long,value: Long)

    fun delete(id: Int)

    fun stopAnother(id:Int)
}
