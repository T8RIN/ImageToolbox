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

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.feature.media_picker.domain.MediaRetriever
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchScheduler
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

internal class SemanticSearchWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val job = requireNotNull(currentCoroutineContext()[Job])
        activeJobs.add(job)
        try {
            return workMutex.withLock {
                val dependencies = applicationContext.semanticSearchDependencies()
                val settings = dependencies.settingsManager()
                val repository = dependencies.repository()
                if (!settings.getSettingsState().isSemanticSearchEnabled) {
                    return@withLock Result.success()
                }
                if (!applicationContext.hasSemanticSearchPermission()) {
                    repository.updateWorkState(isQueued = false, isWaitingForPermission = true)
                    return@withLock Result.success()
                }
                repository.updateWorkState(isQueued = false, isWaitingForPermission = false)
                try {
                    repository.refresh()
                    if (!repository.state.value.modelsReady) {
                        dependencies.scheduler().refresh()
                        return@withLock Result.success()
                    }
                    currentCoroutineContext().ensureActive()
                    if (!settings.getSettingsState().isSemanticSearchEnabled) return@withLock Result.success()
                    // Save each completed embedding and resume before WorkManager's execution limit.
                    withTimeout(TimeUnit.MINUTES.toMillis(8)) {
                        val media = dependencies.mediaRetriever()
                            .getMediaByType(AllowedMedia.Photos(null))
                            .first()
                            .getOrThrow()
                        indexWithProgress(repository, media)
                        repository.updateWorkState(isQueued = false, isWaitingForPermission = false)
                        Result.success()
                    }
                } catch (_: TimeoutCancellationException) {
                    repository.updateWorkState(isQueued = false, isWaitingForPermission = false)
                    runSuspendCatching {
                        (dependencies.scheduler() as AndroidSemanticSearchScheduler).continueIndexing()
                    }.fold(
                        onSuccess = { Result.success() },
                        onFailure = { Result.retry() }
                    )
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (exception: Exception) {
                    repository.updateWorkState(
                        isQueued = false,
                        isWaitingForPermission = false,
                        error = exception.localizedMessage ?: exception.toString()
                    )
                    Result.retry()
                }
            }
        } finally {
            activeJobs.remove(job)
        }
    }

    private suspend fun indexWithProgress(
        repository: SemanticSearchRepository,
        media: List<Media>
    ) = coroutineScope {
        val progressJob = launch {
            delay(500)
            repository.state
                .filter { it.isIndexing }
                .map { Triple(it.processedImages, it.totalImages, it.isFacesIndexing) }
                .distinctUntilChanged()
                .conflate()
                .collect { (processed, total, faces) ->
                    setForeground(createForegroundInfo(processed, total, faces))
                    delay(1_000)
                }
        }
        try {
            repository.index(
                media = media,
                groupPeople = applicationContext.semanticSearchDependencies().settingsManager()
                    .getSettingsState().semanticSearchSettings.groupPeople
            )
        } finally {
            progressJob.cancelAndJoin()
        }
    }

    private fun createForegroundInfo(processed: Int, total: Int, faces: Boolean = false) =
        ForegroundInfo(
        NOTIFICATION_ID,
        notificationBuilder
            .setContentText(
                applicationContext.getString(
                    if (faces) R.string.gallery_faces_indexing else R.string.gallery_search_indexing,
                    processed,
                    total
                )
            )
            .setProgress(total, processed, total == 0)
            .build(),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        } else 0
    )

    private val notificationBuilder by lazy {
        val context = applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService<NotificationManager>()?.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.gallery_search_auto),
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    setSound(null, null)
                    enableVibration(false)
                }
            )
        }
        val contentIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP }
            ?.let {
                PendingIntent.getActivity(
                    context,
                    NOTIFICATION_ID,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.gallery_search_auto))
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setContentIntent(contentIntent)
    }

    companion object {
        private const val CHANNEL_ID = "semantic_search_channel"
        private const val NOTIFICATION_ID = 1900
        private val activeJobs = ConcurrentHashMap.newKeySet<Job>()
        private val workMutex = Mutex()

        suspend fun cancelAndJoin() {
            activeJobs.toList().forEach { it.cancelAndJoin() }
        }
    }

}

internal class SemanticSearchObserverWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val dependencies = applicationContext.semanticSearchDependencies()
        if (dependencies.settingsManager().getSettingsState().isSemanticSearchEnabled) {
            val scheduler = dependencies.scheduler()
            scheduler.refresh()
            (scheduler as AndroidSemanticSearchScheduler)
                .scheduleObserver(ExistingWorkPolicy.APPEND_OR_REPLACE)
        }
        return Result.success()
    }

}

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface SemanticSearchWorkerEntryPoint {
    fun settingsManager(): SettingsManager
    fun repository(): SemanticSearchRepository
    fun mediaRetriever(): MediaRetriever
    fun scheduler(): SemanticSearchScheduler
}

private fun Context.semanticSearchDependencies(): SemanticSearchWorkerEntryPoint =
    EntryPointAccessors.fromApplication(this, SemanticSearchWorkerEntryPoint::class.java)

internal fun Context.hasSemanticSearchPermission(): Boolean {
    fun hasPermission(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    return when {
        Build.VERSION.SDK_INT >= 30 && Environment.isExternalStorageManager() -> true
        Build.VERSION.SDK_INT >= 34 && hasPermission(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) -> true
        Build.VERSION.SDK_INT >= 33 -> hasPermission(Manifest.permission.READ_MEDIA_IMAGES)
        else -> hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}