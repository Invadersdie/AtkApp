package com.atk.app.screens.track.background

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.atk.app.core.repository.DatabaseRepository
import com.atk.app.core.repository.wialon.WialonRepositoryImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect

const val TRACK_WORKER = "TRACK_WORKER"

@HiltWorker
class TrackWorker @AssistedInject constructor(
    private val wialonRepository: WialonRepositoryImpl,
    private val databaseRepository: DatabaseRepository,
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        var allDone = false
        databaseRepository.getAllTrackableObjects().collect {
            it.forEach {
                val result = wialonRepository.trackObject(it.wialonId, it.isLocal)
                databaseRepository.updateTrackableObjects(it.copy(data = result.getValueOrThrow().items[0].prms))
            }
        }

        if (allDone) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, OnAlarmReceiver::class.java)
            for (i in 0..30) {
                val pendingIntent = PendingIntent.getBroadcast(context, i, intent, 0)
                alarmManager.cancel(pendingIntent)
            }
        }

        return Result.success()
    }
}