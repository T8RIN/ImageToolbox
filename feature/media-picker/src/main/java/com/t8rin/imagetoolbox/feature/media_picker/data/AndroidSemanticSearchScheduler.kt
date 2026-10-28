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

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchScheduler
import com.t8rin.neural_tools.search.ClipImageSearch
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

internal class AndroidSemanticSearchScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appScope: AppScope,
    private val settingsManager: SettingsManager,
    private val repository: SemanticSearchRepository
) : SemanticSearchScheduler {

    private val started = AtomicBoolean()
    private val schedulingMutex = Mutex()
    private val workManager get() = WorkManager.getInstance(context)
    private var downloadJob: Job? = null

    override fun start() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (!started.compareAndSet(false, true)) return
        appScope.launch {
            workManager.getWorkInfosForUniqueWorkFlow(INDEX_WORK)
                .map { work ->
                    work.none { it.state == WorkInfo.State.RUNNING } && work.any {
                        it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.BLOCKED
                    }
                }
                .distinctUntilChanged()
                .collect { queued ->
                    val state = repository.state.value
                    repository.updateWorkState(
                        isQueued = queued && settingsManager.getSettingsState().isSemanticSearchEnabled,
                        isWaitingForPermission = state.isWaitingForPermission,
                        error = state.error
                    )
                }
        }
        appScope.launch {
            var first = true
            var previous: Boolean? = null
            settingsManager.settingsState
                .map { it.isSemanticSearchEnabled }
                .distinctUntilChanged()
                .collect { value ->
                    // The StateFlow initially contains defaults while DataStore is still loading.
                    val enabled = if (first) {
                        first = false
                        settingsManager.getSettingsState().isSemanticSearchEnabled
                    } else value
                    if (enabled == previous) return@collect
                    previous = enabled
                    runSuspendCatching {
                        if (enabled) {
                            enqueueIndex()
                            schedulePeriodicWork()
                            scheduleObserver(ExistingWorkPolicy.KEEP)
                        } else {
                            stop()
                        }
                    }.onFailure(::reportFailure)
                }
        }
    }

    override fun refresh() {
        appScope.launch {
            runSuspendCatching {
                if (settingsManager.getSettingsState().isSemanticSearchEnabled) {
                    enqueueIndex()
                    schedulePeriodicWork()
                    scheduleObserver(ExistingWorkPolicy.KEEP)
                }
            }.onFailure(::reportFailure)
        }
    }

    override suspend fun stop() = schedulingMutex.withLock {
        downloadJob?.cancelAndJoin()
        downloadJob = null
        workManager.cancelAllWorkByTag(WORK_TAG).await()
        SemanticSearchWorker.cancelAndJoin()
        repository.updateWorkState(isQueued = false, isWaitingForPermission = false)
    }

    internal suspend fun continueIndexing() = enqueueIndex()

    private suspend fun enqueueIndex() = schedulingMutex.withLock {
        if (!settingsManager.getSettingsState().isSemanticSearchEnabled) return@withLock
        if (!context.hasSemanticSearchPermission()) {
            repository.updateWorkState(isQueued = false, isWaitingForPermission = true)
            return@withLock
        }
        if (!ClipImageSearch.isDownloaded()) {
            downloadModels()
            return@withLock
        }
        val unfinished = workManager.getWorkInfosForUniqueWorkFlow(INDEX_WORK).first()
            .filterNot { it.state.isFinished }
        val retry = unfinished.isNotEmpty() && unfinished.none { it.state == WorkInfo.State.RUNNING } &&
            unfinished.any { it.runAttemptCount > 0 }
        if (!retry && (unfinished.size >= 2 || unfinished.any { it.state != WorkInfo.State.RUNNING })) {
            if (unfinished.none { it.state == WorkInfo.State.RUNNING }) {
                repository.updateWorkState(
                    isQueued = true,
                    isWaitingForPermission = false,
                    error = repository.state.value.error
                )
            }
            return@withLock
        }
        if (unfinished.isEmpty()) {
            repository.updateWorkState(isQueued = true, isWaitingForPermission = false)
        }
        workManager.enqueueUniqueWork(
            INDEX_WORK,
            when {
                retry -> ExistingWorkPolicy.REPLACE
                unfinished.isEmpty() -> ExistingWorkPolicy.KEEP
                else -> ExistingWorkPolicy.APPEND_OR_REPLACE
            },
            OneTimeWorkRequestBuilder<SemanticSearchWorker>()
                .setConstraints(indexConstraints())
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .addTag(WORK_TAG)
                .build()
        ).await()
    }

    private fun downloadModels() {
        if (downloadJob?.isActive == true) return
        downloadJob = appScope.launch(start = CoroutineStart.LAZY) {
            repository.updateWorkState(isQueued = false, isWaitingForPermission = false)
            runSuspendCatching {
                repository.downloadModels()
                enqueueIndex()
            }.onFailure(::reportFailure)
        }.also { it.start() }
    }

    private suspend fun schedulePeriodicWork() = schedulingMutex.withLock {
        if (!settingsManager.getSettingsState().isSemanticSearchEnabled) return@withLock
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<SemanticSearchWorker>(6, TimeUnit.HOURS)
                .setInitialDelay(6, TimeUnit.HOURS)
                .setConstraints(indexConstraints())
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .addTag(WORK_TAG)
                .build()
        ).await()
    }

    internal suspend fun scheduleObserver(policy: ExistingWorkPolicy) = schedulingMutex.withLock {
        if (!settingsManager.getSettingsState().isSemanticSearchEnabled) {
            return@withLock
        }
        workManager.enqueueUniqueWork(
            OBSERVER_WORK,
            policy,
            OneTimeWorkRequestBuilder<SemanticSearchObserverWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .addContentUriTrigger(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true)
                        .addContentUriTrigger(MediaStore.Files.getContentUri("external"), true)
                        .setTriggerContentUpdateDelay(10, TimeUnit.SECONDS)
                        .setTriggerContentMaxDelay(1, TimeUnit.MINUTES)
                        .build()
                )
                .addTag(WORK_TAG)
                .build()
        ).await()
    }

    private fun reportFailure(error: Throwable) {
        repository.updateWorkState(
            isQueued = false,
            isWaitingForPermission = false,
            error = error.localizedMessage ?: error.toString()
        )
    }

    private fun indexConstraints() = Constraints.Builder()
        .setRequiresStorageNotLow(true)
        .build()

    private companion object {
        const val WORK_TAG = "semantic-search"
        const val INDEX_WORK = "semantic-search-index"
        const val PERIODIC_WORK = "semantic-search-periodic"
        const val OBSERVER_WORK = "semantic-search-observer"
    }

}