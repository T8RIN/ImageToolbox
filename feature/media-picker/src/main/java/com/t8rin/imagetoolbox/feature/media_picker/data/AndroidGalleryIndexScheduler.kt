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
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.saving.FailureNotifier
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.feature.media_picker.domain.GalleryIndexScheduler
import com.t8rin.imagetoolbox.feature.media_picker.domain.GalleryOcrRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchRepository
import com.t8rin.neural_tools.faces.ArcFace
import com.t8rin.neural_tools.search.ClipImageSearch
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AndroidGalleryIndexScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appScope: AppScope,
    private val settingsManager: SettingsManager,
    private val failureNotifier: FailureNotifier,
    private val semantic: SemanticSearchRepository,
    private val ocr: GalleryOcrRepository
) : GalleryIndexScheduler {

    private val started = AtomicBoolean()
    private val mutex = Mutex()
    private val workManager get() = WorkManager.getInstance(context)
    private val tasks = GalleryIndex.entries.associateWith { Task() }

    override fun start() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !started.compareAndSet(
                false,
                true
            )
        ) return

        for (index in GalleryIndex.entries) {
            appScope.launch {
                workManager.getWorkInfosByTagFlow(index.tag)
                    .map { work -> work.any { !it.state.isFinished && it.state != WorkInfo.State.RUNNING } }
                    .distinctUntilChanged()
                    .collect { queued ->
                        updateWorkState(
                            index,
                            queued = queued && index.isEnabled(settingsManager.getSettingsState())
                        )
                    }
            }
        }
        appScope.launch {
            runSuspendCatching {
                workManager.cancelUniqueWork("gallery-ocr-periodic").await()
            }.onFailure(failureNotifier::send)
            var previous: List<GalleryIndex>? = null
            settingsManager.settingsState
                .map { settings -> GalleryIndex.entries.filter { it.isEnabled(settings) } }
                .distinctUntilChanged()
                .collect { enabled ->
                    val changed = GalleryIndex.entries.filter {
                        previous?.contains(it) != (it in enabled)
                    }
                    previous = enabled

                    runSuspendCatching {
                        for (index in changed) {
                            if (index in enabled) enqueue(index) else stop(index)
                        }
                        if (changed.any { it != GalleryIndex.Faces }) {
                            scheduleObserver(ExistingWorkPolicy.KEEP)
                            schedulePeriodicWork()
                        }
                    }.onFailure(failureNotifier::send)
                }
        }
    }

    override fun refresh() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        appScope.launch { refreshIndex() }
    }

    override suspend fun stopSemanticSearch() {
        stop(GalleryIndex.Faces)
        stop(GalleryIndex.Semantic)
    }

    internal suspend fun refreshIndex(mediaChanged: Boolean = false) {
        runSuspendCatching {
            val settings = settingsManager.getSettingsState()
            for (index in GalleryIndex.entries) {
                if (index.isEnabled(settings)) enqueue(
                    index,
                    append = mediaChanged
                ) else stop(index)
            }
            scheduleObserver(if (mediaChanged) ExistingWorkPolicy.APPEND_OR_REPLACE else ExistingWorkPolicy.KEEP)
        }.onFailure(failureNotifier::send)
    }

    private suspend fun schedulePeriodicWork() = mutex.withLock {
        val settings = settingsManager.getSettingsState()
        if (GalleryIndex.entries.none { it.isEnabled(settings) }) {
            workManager.cancelUniqueWork(PERIODIC_WORK).await()
            return@withLock
        }
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            PeriodicWorkRequestBuilder<GalleryIndexRefreshWorker>(6, TimeUnit.HOURS)
                .setInitialDelay(6, TimeUnit.HOURS)
                .build()
        ).await()
    }

    private suspend fun scheduleObserver(policy: ExistingWorkPolicy) = mutex.withLock {
        val settings = settingsManager.getSettingsState()
        if (GalleryIndex.entries.none { it.isEnabled(settings) }) {
            workManager.cancelUniqueWork(OBSERVER_WORK).await()
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
                .build()
        ).await()
    }

    internal suspend fun runIndex(index: GalleryIndex, action: suspend () -> Boolean) =
        supervisorScope {
            try {
                val task = mutex.withLock {
                    if (!index.isEnabled(settingsManager.getSettingsState()) || !canIndex(index)) {
                        return@supervisorScope
                    }
                    async {
                        // OCR yields between photos when running without a foreground service.
                        if (index == GalleryIndex.Ocr) action()
                        else withTimeoutOrNull(TimeUnit.MINUTES.toMillis(8)) { action() } ?: true
                    }.also { tasks.getValue(index).indexing = it }
                }
                if (task.await()) enqueue(index, append = true)
            } catch (_: CancellationException) {
                // Disabling one index must let the next worker in the chain start.
                currentCoroutineContext().ensureActive()
            } catch (error: Exception) {
                failureNotifier.send(error)
            }
        }

    private suspend fun stop(index: GalleryIndex) {
        val jobs = mutex.withLock {
            if (index.isEnabled(settingsManager.getSettingsState())) return
            val task = tasks.getValue(index)
            listOfNotNull(task.download, task.indexing).onEach { it.cancel() }.also {
                task.download = null
                task.indexing = null
                updateWorkState(index, queued = false, waitingForPermission = false)
            }
        }
        jobs.joinAll()
    }

    private suspend fun enqueue(index: GalleryIndex, append: Boolean = false) = mutex.withLock {
        if (!index.isEnabled(settingsManager.getSettingsState()) || !canIndex(index)) return@withLock
        val work = workManager.getWorkInfosByTagFlow(index.tag).first()
        if (work.any { !it.state.isFinished && it.state != WorkInfo.State.RUNNING }) {
            updateWorkState(index, queued = true)
            return@withLock
        }
        if (!append && work.any { it.state == WorkInfo.State.RUNNING }) return@withLock
        val request = when (index) {
            GalleryIndex.Semantic -> OneTimeWorkRequestBuilder<SemanticSearchWorker>()
            GalleryIndex.Faces -> OneTimeWorkRequestBuilder<FaceGroupingWorker>()
            GalleryIndex.Ocr -> OneTimeWorkRequestBuilder<GalleryOcrWorker>()
        }.setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
            .addTag(index.tag)
            .build()
        workManager.enqueueUniqueWork(INDEX_WORK, ExistingWorkPolicy.APPEND_OR_REPLACE, request)
            .await()
    }

    private fun canIndex(index: GalleryIndex): Boolean {
        val hasPermission = context.hasSemanticSearchPermission()
        updateWorkState(index, waitingForPermission = !hasPermission)
        if (!hasPermission) return false
        val ready = when (index) {
            GalleryIndex.Semantic -> ClipImageSearch.isDownloaded()
            GalleryIndex.Faces -> return ArcFace.isDownloaded()
            GalleryIndex.Ocr -> ocr.state.value.modelsReady
        }
        if (!ready) downloadModels(index)
        return ready
    }

    private fun downloadModels(index: GalleryIndex) {
        val task = tasks.getValue(index)
        if (task.download?.isActive == true) return
        task.download = appScope.launch(start = CoroutineStart.LAZY) {
            runSuspendCatching {
                when (index) {
                    GalleryIndex.Semantic -> semantic.downloadModels()
                    GalleryIndex.Ocr -> ocr.downloadModels()
                    GalleryIndex.Faces -> return@launch
                }
                enqueue(index)
            }.onFailure { error ->
                failureNotifier.send(error)
                if (index == GalleryIndex.Ocr && !ocr.state.value.modelsReady) {
                    runSuspendCatching { settingsManager.setGalleryOcrEnabled(false) }
                        .onFailure(failureNotifier::send)
                }
            }
        }.also { it.start() }
    }

    private fun updateWorkState(
        index: GalleryIndex,
        queued: Boolean? = null,
        waitingForPermission: Boolean? = null
    ) = when (index) {
        GalleryIndex.Semantic -> semantic.updateWorkState(queued, waitingForPermission)
        GalleryIndex.Faces -> semantic.updateWorkState(
            isFacesQueued = queued,
            isWaitingForPermission = waitingForPermission.takeUnless { queued == false }
        )

        GalleryIndex.Ocr -> ocr.updateWorkState(queued, waitingForPermission)
    }

    private class Task(var download: Job? = null, var indexing: Job? = null)

    private companion object {
        const val INDEX_WORK = "gallery-index"
        const val PERIODIC_WORK = "semantic-search-periodic"
        const val OBSERVER_WORK = "semantic-search-observer"
    }

}

internal enum class GalleryIndex(val tag: String) {
    Semantic("gallery-index-semantic"),
    Faces("gallery-index-faces"),
    Ocr("gallery-index-ocr");

    fun isEnabled(settings: SettingsState) = when (this) {
        Semantic -> settings.isSemanticSearchEnabled
        Faces -> settings.isSemanticSearchEnabled && settings.semanticSearchSettings.groupPeople
        Ocr -> settings.isGalleryOcrEnabled
    }
}