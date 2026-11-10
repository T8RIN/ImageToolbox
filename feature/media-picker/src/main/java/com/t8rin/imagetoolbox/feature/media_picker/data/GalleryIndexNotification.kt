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

package com.t8rin.imagetoolbox.feature.media_picker.data

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ForegroundServiceStartNotAllowedException
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import com.t8rin.imagetoolbox.core.resources.R
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit

@SuppressLint("NewApi") //Embedded picker is >= 26 by default
internal class GalleryIndexNotification(
    private val context: Context,
    private val channelId: String,
    workId: UUID,
    private val title: Int
) {

    private val id = workId.hashCode().takeIf { it != 0 } ?: 1900
    private val manager = context.getSystemService<NotificationManager>()

    private val builder by lazy {
        val name = context.getString(title)
        manager?.createNotificationChannel(
            NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_LOW).apply {
                setSound(null, null)
                enableVibration(false)
            }
        )
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP }
            ?.let {
                PendingIntent.getActivity(
                    context,
                    id,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        NotificationCompat.Builder(context, channelId)
            .setContentTitle(name)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setContentIntent(intent)
    }

    fun info(progress: GalleryIndexProgress) = ForegroundInfo(
        id,
        build(progress),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        } else 0
    )

    fun show(progress: GalleryIndexProgress) {
        manager?.notify(id, build(progress, timeout = TimeUnit.MINUTES.toMillis(10)))
    }

    fun dismiss() {
        manager?.cancel(id)
    }

    private fun build(progress: GalleryIndexProgress, timeout: Long = 0) = builder
        .setContentText(context.getString(progress.text, progress.processed, progress.total))
        .setProgress(progress.total, progress.processed, progress.total == 0)
        .setTimeoutAfter(timeout)
        .build()

}

internal data class GalleryIndexProgress(val processed: Int, val total: Int, val text: Int)

internal suspend fun <T> CoroutineWorker.indexWithProgress(
    notification: GalleryIndexNotification,
    progress: Flow<GalleryIndexProgress>,
    index: suspend () -> T
): T = coroutineScope {
    val updates = launch(start = CoroutineStart.UNDISPATCHED) {
        var foreground = canStartForeground()
        try {
            progress.distinctUntilChanged().conflate().collect {
                if (foreground) {
                    try {
                        setForeground(notification.info(it))
                    } catch (error: IllegalStateException) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                            error !is ForegroundServiceStartNotAllowedException
                        ) throw error
                        foreground = false
                    }
                }
                if (!foreground) notification.show(it)
                delay(1_000)
            }
        } finally {
            if (!foreground) notification.dismiss()
        }
    }
    try {
        index()
    } finally {
        updates.cancelAndJoin()
    }
}

private fun canStartForeground(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
    val process = ActivityManager.RunningAppProcessInfo()
    ActivityManager.getMyMemoryState(process)
    return process.importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
}