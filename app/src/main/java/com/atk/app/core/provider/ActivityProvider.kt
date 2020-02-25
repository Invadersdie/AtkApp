package com.atk.app.core.provider

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatActivity

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ActivityProvider @Inject constructor(application: Application) {

    private var channel = Channel<AppCompatActivity.() -> Unit>()

    private val activityLifecycleCallbacks: SimpleActivityLifecycleCallbacks

    init {
        activityLifecycleCallbacks =
            object : SimpleActivityLifecycleCallbacks() {

                var scope: CoroutineScope? = null
                private var job: Job? = null

                override fun onActivityResumed(activity: Activity) {
                    if (activity is AppCompatActivity) {
                        scope = CoroutineScope(Dispatchers.Main)
                        synchronized(this@ActivityProvider) {
                            job = scope?.launch {
                                channel.consumeEach {
                                    activity.it()
                                }
                            }
                        }
                    }
                }

                override fun onActivityPaused(activity: Activity) {
                    if (activity is AppCompatActivity) {
                        synchronized(this@ActivityProvider) {
                            job?.cancel()
                            scope = null
                            channel = Channel()
                        }
                    }
                }
            }
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    suspend fun submitBlock(block: AppCompatActivity.() -> Unit) = channel.send(block)
}