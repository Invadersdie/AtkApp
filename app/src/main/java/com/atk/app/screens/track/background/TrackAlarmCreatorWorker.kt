package com.atk.app.screens.track.background

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.*

@HiltWorker
class TrackAlarmCreatorWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, OnAlarmReceiver::class.java)

        for (i in 0..30) {
            val pendingIntent = PendingIntent.getBroadcast(context, i, intent, 0)
            alarmManager.setInexactRepeating(
                RTC_WAKEUP,
                Calendar.getInstance().apply { add(Calendar.MINUTE, 1) }.timeInMillis,
                1 * 1000 * 60,
                pendingIntent
            )
        }
        return Result.success()
    }
}