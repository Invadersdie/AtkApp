package com.atk.app.screens.track.background

import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnAlarmReceiver : DaggerHiltBroadcastReceiver() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context, intent: Intent) {
        val workRequest = OneTimeWorkRequestBuilder<TrackWorker>().build()
        workManager.beginUniqueWork(TRACK_WORKER, ExistingWorkPolicy.KEEP, workRequest).enqueue()
    }
}