/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.core.data.saving

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.getSystemService
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.logger.makeLog
import kotlin.math.roundToInt

internal class KeepAliveForegroundService : Service() {

    private val notificationManager: NotificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }
    private var title = ""
    private var description = ""
    private var progress: Float = KeepAliveService.PROGRESS_NO_PROGRESS
    private var removeNotification: Boolean = true

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForegroundSafe()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (intent == null) return START_NOT_STICKY

        handleIntent(intent)

        return START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action.makeLog("KeepAliveForegroundService")) {
            ACTION_UPDATE -> {
                title = intent.getStringExtra(EXTRA_TITLE) ?: title
                description = intent.getStringExtra(EXTRA_DESC) ?: description
                progress = intent.getFloatExtra(
                    EXTRA_PROGRESS,
                    KeepAliveService.PROGRESS_NO_PROGRESS
                )

                "UPDATE: title = $title, description = $description, progress = $progress".makeLog("KeepAliveForegroundService")

                startForeground()
            }

            ACTION_STOP -> {
                startForeground()

                removeNotification = intent.getBooleanExtra(
                    EXTRA_REMOVE_NOTIFICATION, true
                ).makeLog("KeepAliveForegroundService")

                stopForegroundSafe()
            }

            else -> startForeground()
        }
    }

    private fun startForeground() {
        runCatching {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                buildNotification(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                } else {
                    0
                }
            )
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.processing_channel),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Suppress("DEPRECATION")
    private fun stopForegroundSafe() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(
                if (removeNotification) {
                    STOP_FOREGROUND_REMOVE
                } else {
                    STOP_FOREGROUND_DETACH
                }
            )
        } else {
            stopForeground(removeNotification)
        }
        if (removeNotification) {
            notificationManager.cancel(NOTIFICATION_ID)
        }
        stopSelf()
    }

    private fun buildNotification(): Notification {
        val launchIntent = Intent(
            this, Class.forName(
                "com.t8rin.imagetoolbox.app.presentation.AppActivity"
            )
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (PendingIntent.FLAG_IMMUTABLE)
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title.ifEmpty { getString(R.string.processing) })
            .setContentText(description.takeIf { it.isNotEmpty() })
            .setSmallIcon(R.drawable.ic_notification_icon)
            .run {
                when (progress) {
                    KeepAliveService.PROGRESS_NO_PROGRESS -> this
                    KeepAliveService.PROGRESS_INDETERMINATE -> setProgress(0, 0, true)
                    else -> setProgress(100, (progress * 100).roundToInt(), false)
                }
            }
            .setOngoing(true)
            .setContentIntent(contentPendingIntent)
            .build()
            .also {
                notificationManager.notify(NOTIFICATION_ID, it)
            }
    }

    override fun onBind(intent: Intent?) = null

    companion object {
        internal const val CHANNEL_ID = "keep_alive_channel"
        internal const val NOTIFICATION_ID = 1

        internal const val ACTION_STOP = "ACTION_STOP"
        internal const val ACTION_UPDATE = "ACTION_UPDATE"
        internal const val EXTRA_TITLE = "EXTRA_TITLE"
        internal const val EXTRA_DESC = "EXTRA_DESC"
        internal const val EXTRA_PROGRESS = "EXTRA_PROGRESS"
        internal const val EXTRA_REMOVE_NOTIFICATION = "REMOVE_NOTIFICATION"
    }
}