package com.example.rsshool2021_android_task_pomodoro

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.annotation.RestrictTo
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import kotlinx.coroutines.*

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
) : RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null
    private var current = 0L
    private var scope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Default
    )

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
        if (stopwatch.isStarted) {
            startTimer(stopwatch)
        } else {
            stopTimer(stopwatch)
        }
        initButtonsListeners(stopwatch)


    }


    private fun startTimer(stopwatch: Stopwatch) {

        binding.startPauseButton.text = "STOP"
        timer = getCountDownTimer(stopwatch)
        timer?.cancel()
        timer?.start()
        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()

    }

    private fun stopTimer(stopwatch: Stopwatch) {

        timer?.cancel()
        binding.startPauseButton.text = "START"
        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }


    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startPauseButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs, stopwatch.value)
            } else {
                listener.stopAnother(stopwatch.id)
                listener.start(stopwatch.id, stopwatch.currentMs, stopwatch.value)
            }
        }

        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }

//    private fun startCustomView(stopwatch: Stopwatch) {
//       scope.launch {
//            binding.customViewTwo.setPeriod(stopwatch.value)
//            while (stopwatch.isStarted) {
//                current += INTERVAL
//                binding.customViewTwo.setCurrent(current)
//                delay(INTERVAL)
//            }
//        }
//    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {

        return object : CountDownTimer(stopwatch.currentMs, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                stopwatch.currentMs -= interval
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()

            }

            @SuppressLint("ResourceAsColor")
            override fun onFinish() {
                binding.mainItem.setBackgroundColor(R.color.green_light)
                binding.blinkingIndicator.isInvisible = true
                (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
            }
        }
    }


    private fun Long.displayTime(): String {
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60


        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {
        private const val INTERVAL = 1000L


        private const val UNIT_TEN_MS = 1000L

    }
}