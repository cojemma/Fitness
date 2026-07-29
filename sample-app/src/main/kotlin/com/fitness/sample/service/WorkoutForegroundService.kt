package com.fitness.sample.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.fitness.sample.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Foreground service that keeps the process alive while a workout is active so the
 * system doesn't kill it and lose the in-memory session state, and surfaces the rest
 * timer + current/next exercise in a persistent notification.
 */
class WorkoutForegroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(WorkoutSessionNotifier.state.value))

        WorkoutSessionNotifier.state
            .onEach { info -> updateNotification(info) }
            .launchIn(serviceScope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        // Explicit, in case a stray notify() from WorkoutSessionNotifier races with teardown —
        // guarantees the notification is gone and the NotificationManager reference is released.
        stopForeground(STOP_FOREGROUND_REMOVE)
        getSystemService(NotificationManager::class.java)?.cancel(NOTIFICATION_ID)
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun updateNotification(info: WorkoutSessionNotifier.Info?) {
        val manager = getSystemService(NotificationManager::class.java) ?: return
        manager.notify(NOTIFICATION_ID, buildNotification(info))
    }

    private fun buildNotification(info: WorkoutSessionNotifier.Info?): Notification {
        val title = if (info?.isResting == true) {
            getString(R.string.notification_title_resting, formatSeconds(info.restSecondsRemaining))
        } else {
            getString(R.string.notification_title_workout_active)
        }

        val content = info?.let {
            if (it.isUpcomingExercise) {
                getString(R.string.notification_content_next_exercise, it.exerciseName)
            } else {
                getString(R.string.notification_content_current_exercise, it.exerciseName)
            }
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_workout)
            .setContentTitle(title)
            .setContentText(content)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .build()
    }

    private fun formatSeconds(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return String.format("%d:%02d", m, s)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_workout_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_workout_description)
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "workout_active_channel"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, WorkoutForegroundService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, WorkoutForegroundService::class.java))
        }
    }
}
