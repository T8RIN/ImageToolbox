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
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkerParameters
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.feature.media_picker.domain.MediaRetriever
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchScheduler
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
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
                        repository.index(media)
                        repository.updateWorkState(isQueued = false, isWaitingForPermission = false)
                        Result.success()
                    }
                } catch (_: TimeoutCancellationException) {
                    repository.updateWorkState(isQueued = true, isWaitingForPermission = false)
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
                        isQueued = true,
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

    companion object {
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