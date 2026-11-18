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

import android.app.ActivityManager
import android.app.job.JobScheduler
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.provider.MediaStore
import androidx.core.content.getSystemService
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
import com.t8rin.imagetoolbox.core.utils.makeLog
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
import kotlinx.coroutines.flow.onEach
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
                    .onEach { work -> logWork(index.name, work) }
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
            }.onFailure(::reportFailure)
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
                    }.onFailure(::reportFailure)
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

    override suspend fun logDiagnostics() {
        runSuspendCatching {
            withTimeoutOrNull(5_000) {
                val settings = settingsManager.getSettingsState()
                val activity = context.getSystemService<ActivityManager>()
                val power = context.getSystemService<PowerManager>()
                val backgroundRestricted = if (Build.VERSION.SDK_INT >= 28) {
                    activity?.isBackgroundRestricted
                } else null
                ("enabled: semantic=${settings.isSemanticSearchEnabled}, " +
                        "faces=${settings.semanticSearchSettings.groupPeople}, " +
                        "ocr=${settings.isGalleryOcrEnabled}, " +
                        "photoAccess=${context.hasSemanticSearchPermission()}, " +
                        "backgroundRestricted=$backgroundRestricted, " +
                        "powerSave=${power?.isPowerSaveMode}, idle=${power?.isDeviceIdleMode}, " +
                        "ignoringBatteryOptimizations=${
                            power?.isIgnoringBatteryOptimizations(
                                context.packageName
                            )
                        }")
                    .makeLog("GalleryIndex")
                "semantic=${semantic.state.value}, ocr=${ocr.state.value}".makeLog("GalleryIndex")
                for (name in listOf(
                    INDEX_WORK, PERIODIC_WORK, OBSERVER_WORK,
                    "semantic-search-index", "gallery-ocr-periodic"
                )) {
                    logWork(name, workManager.getWorkInfosForUniqueWorkFlow(name).first())
                }
                logPendingJobs()
                true
            } ?: "Diagnostics timed out".makeLog("GalleryIndex")
        }.onFailure { it.makeLog("GalleryIndex") }
    }

    private fun logWork(name: String, work: List<WorkInfo>) {
        val pending = work.filterNot { it.state.isFinished }
        "$name: total=${work.size}, unfinished=${pending.size}".makeLog("GalleryIndex")
        for (info in pending + work.filter { it.state.isFinished }.takeLast(5)) {
            ("$name: id=${info.id}, worker=${info.workerClassName}, state=${info.state}, " +
                    "attempts=${info.runAttemptCount}, generation=${info.generation}, " +
                    "stopReason=${if (Build.VERSION.SDK_INT >= 31) info.stopReason else null}, " +
                    "nextRun=${info.nextScheduleTimeMillis}, constraints=${info.constraints}")
                .makeLog("GalleryIndex")
        }
    }

    private fun logPendingJobs() {
        val scheduler = context.getSystemService<JobScheduler>() ?: return
        val schedulers = buildList {
            add("default" to scheduler)
            if (Build.VERSION.SDK_INT >= 34) {
                add("androidx.work.systemjobscheduler" to scheduler.forNamespace("androidx.work.systemjobscheduler"))
            }
        }
        for ((namespace, jobs) in schedulers) {
            for (job in jobs.allPendingJobs.filter {
                it.service.className == "androidx.work.impl.background.systemjob.SystemJobService"
            }) {
                val reasons = when {
                    Build.VERSION.SDK_INT >= 36 -> jobs.getPendingJobReasons(job.id)
                        .contentToString()

                    Build.VERSION.SDK_INT >= 34 -> jobs.getPendingJobReason(job.id).toString()
                    else -> null
                }
                ("job=${job.id}, namespace=$namespace, " +
                        "workId=${job.extras.getString("EXTRA_WORK_SPEC_ID")}, pendingReasons=$reasons")
                    .makeLog("GalleryIndex")
            }
        }
    }

    private fun reportFailure(error: Throwable) {
        error.makeLog("GalleryIndex")
        failureNotifier.send(error)
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
        }.onFailure(::reportFailure)
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
                reportFailure(error)
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

        workManager
            .enqueueUniqueWork(
                uniqueWorkName = INDEX_WORK,
                existingWorkPolicy = ExistingWorkPolicy.APPEND_OR_REPLACE,
                request = when (index) {
                    GalleryIndex.Semantic -> OneTimeWorkRequestBuilder<SemanticSearchWorker>()
                    GalleryIndex.Faces -> OneTimeWorkRequestBuilder<FaceGroupingWorker>()
                    GalleryIndex.Ocr -> OneTimeWorkRequestBuilder<GalleryOcrWorker>()
                }.addTag(index.tag).build()
            )
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
                reportFailure(error)
                if (index == GalleryIndex.Ocr && !ocr.state.value.modelsReady) {
                    runSuspendCatching { settingsManager.setGalleryOcrEnabled(false) }
                        .onFailure(::reportFailure)
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