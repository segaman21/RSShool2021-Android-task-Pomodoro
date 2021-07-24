package com.example.rsshool2021_android_task_pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding
    private val stopwatchAdapter = StopwatchAdapter(this)
    private val stopwatches = mutableListOf<Stopwatch>()
    private var nextId = 0
    private var timeFinish = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }
        binding.addNewStopwatchButton.setOnClickListener {
            try {
                val timerValue: Long? = binding.timeValue.text.toString().toLong()
                if (timerValue != null) {
                    stopwatches.add(
                        Stopwatch(
                            nextId++,
                            timerValue * 60000,
                            timerValue * 60000L,
                            false
                        )
                    )
                    stopwatchAdapter.submitList(stopwatches.toList())
                }
            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    "Enter the number of minutes",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    override fun start(id: Int, currentMs: Long, value: Long) {
        timeFinish = System.currentTimeMillis() + currentMs
        changeStopwatch(id, currentMs, value, true)
    }

    override fun stop(id: Int, currentMs: Long, value: Long) {
        changeStopwatch(id, currentMs, value, false)
        timeFinish = 0L
    }

    override fun delete(id: Int) {
        timeFinish = 0L
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, timeFinish)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        onAppForegrounded()
    }

    override fun stopAnother(id: Int) {
        stopwatches.forEach {
            if (it.id != id) {
                it.isStarted = false
            }
        }
        stopwatchAdapter.notifyDataSetChanged()
    }


    private fun changeStopwatch(id: Int, currentMs: Long?, value: Long, isStarted: Boolean) {
        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if (it.id == id) {
                newTimers.add(Stopwatch(it.id, currentMs ?: it.currentMs, value, isStarted))
            } else {
                newTimers.add(it)
            }
        }
        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }
}