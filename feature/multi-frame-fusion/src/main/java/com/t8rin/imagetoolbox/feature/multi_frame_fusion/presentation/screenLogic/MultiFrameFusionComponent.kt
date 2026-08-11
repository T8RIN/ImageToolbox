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

package com.t8rin.imagetoolbox.feature.multi_frame_fusion.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain.FusionParams
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain.MultiFrameFusionProcessor
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.presentation.screenLogic.MultiFrameFusionComponent.HistorySnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class MultiFrameFusionComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val processor: MultiFrameFusionProcessor<Bitmap>,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder
) : BaseHistoryComponent<HistorySnapshot>(dispatchersHolder, componentContext) {

    init {
        debounce {
            _imageFormat.value = settingsManager.settingsState.value.defaultImageFormat
                ?: ImageFormat.Png.Lossless
            initialUris?.let(::setUris)
            registerChangesCleared()
        }
    }

    private val _uris: MutableState<List<Uri>> = mutableStateOf(emptyList())
    val uris by _uris

    private val _params: MutableState<FusionParams> = mutableStateOf(FusionParams())
    val params by _params

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap by _previewBitmap

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Png.Lossless)
    val imageFormat by _imageFormat

    private val _quality: MutableState<Quality> = mutableStateOf(Quality.Base())
    val quality by _quality

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _done = mutableIntStateOf(0)
    val done by _done

    private val _total = mutableIntStateOf(0)
    val total by _total

    val canProcess: Boolean
        get() = uris.size >= FusionParams.MIN_IMAGES

    fun setUris(uris: List<Uri>) {
        clearHistory()
        registerChangesCleared()
        _uris.update {
            uris.distinct().take(FusionParams.MAX_IMAGES)
        }
        _previewBitmap.update { null }
        calculatePreview()
        if (this.uris.isNotEmpty()) resetHistory()
    }

    fun addUris(uris: List<Uri>) {
        val updated = (this.uris + uris)
            .distinct()
            .take(FusionParams.MAX_IMAGES)
        if (updated == this.uris) return

        finalizePendingHistoryTransaction()
        val before = currentHistorySnapshot()
        _uris.update { updated }
        _previewBitmap.update { null }
        commitHistoryFrom(before)
        calculatePreview()
    }

    fun removeImageAt(index: Int) {
        if (index !in uris.indices) return

        finalizePendingHistoryTransaction()
        val before = currentHistorySnapshot()
        _uris.update { it.toMutableList().apply { removeAt(index) } }
        _previewBitmap.update { null }
        commitHistoryFrom(before)
        calculatePreview()
    }

    fun reorderUris(uris: List<Uri>) {
        val reordered = uris.distinct().take(FusionParams.MAX_IMAGES)
        if (reordered == this.uris) return

        finalizePendingHistoryTransaction()
        val before = currentHistorySnapshot()
        _uris.update { reordered }
        _previewBitmap.update { null }
        commitHistoryFrom(before)
        calculatePreview()
    }

    fun updateParams(value: FusionParams) {
        val normalized = value.normalized()
        if (normalized == params) return

        beginPendingHistoryTransaction()
        _params.update { normalized }
        registerChanges()
        calculatePreview()
        schedulePendingHistoryCommit()
    }

    fun setImageFormat(value: ImageFormat) {
        if (value == imageFormat) return

        if (pendingHistoryMode != PendingHistoryMode.FormatChange) {
            finalizePendingHistoryTransaction()
        }
        beginPendingHistoryTransaction(
            mode = PendingHistoryMode.FormatChange,
            commitDelayMillis = formatHistoryTransactionDebounce
        )
        _imageFormat.update { value }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setQuality(value: Quality) {
        if (value == quality) return

        beginPendingHistoryTransaction()
        _quality.update { value }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    private fun calculatePreview() {
        if (!canProcess) {
            cancelImageLoading()
            _previewBitmap.update { null }
            return
        }

        debouncedImageCalculation(delay = 350) {
            processor.fuse(
                imageUris = uris.map(Uri::toString),
                params = params,
                preview = true,
                onProgress = { _, _ -> }
            )?.let { preview ->
                _previewBitmap.update { preview }
            }
        }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun save(oneTimeSaveLocationUri: String?) {
        savingJob = trackProgress {
            prepareProcessing()
            createResult()?.let { bitmap ->
                val info = bitmap.imageInfo()
                parseSaveResult(
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = info,
                            originalUri = uris.firstOrNull()?.toString().orEmpty(),
                            sequenceNumber = null,
                            metadata = null,
                            data = imageCompressor.compressAndTransform(bitmap, info)
                        ),
                        keepOriginalMetadata = false,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    ).onSuccess(::registerSave)
                )
            } ?: parseSaveResult(
                SaveResult.Error.Exception(IllegalStateException("Unable to fuse images"))
            )
            _isSaving.update { false }
        }
    }

    fun share() {
        savingJob = trackProgress {
            prepareProcessing()
            createResult()?.let { bitmap ->
                shareProvider.shareImage(
                    image = bitmap,
                    imageInfo = bitmap.imageInfo(),
                    onComplete = AppToastHost::showConfetti
                )
            }
            _isSaving.update { false }
        }
    }

    fun cache(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            prepareProcessing()
            createResult()?.let { bitmap ->
                shareProvider.cacheImage(
                    image = bitmap,
                    imageInfo = bitmap.imageInfo()
                )?.let { onComplete(it.toUri()) }
            }
            _isSaving.update { false }
        }
    }

    private suspend fun KeepAliveService.createResult(): Bitmap? = processor.fuse(
        imageUris = uris.map(Uri::toString),
        params = params,
        preview = false,
        onProgress = { done, total ->
            _done.update { done }
            _total.update { total }
            updateProgress(done = done, total = total)
        }
    )

    private fun prepareProcessing() {
        _isSaving.update { true }
        _done.update { 0 }
        _total.update { uris.size * 2 + 1 }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        uris = uris,
        params = params,
        imageFormat = imageFormat,
        quality = quality
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _uris.update { snapshot.uris }
        _params.update { snapshot.params }
        _imageFormat.update { snapshot.imageFormat }
        _quality.update { snapshot.quality }
        _previewBitmap.update { null }
        calculatePreview()
    }

    data class HistorySnapshot(
        val uris: List<Uri>,
        val params: FusionParams,
        val imageFormat: ImageFormat,
        val quality: Quality
    )

    private fun Bitmap.imageInfo() = ImageInfo(
        width = width,
        height = height,
        imageFormat = imageFormat,
        quality = quality,
        originalUri = uris.firstOrNull()?.toString()
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): MultiFrameFusionComponent
    }
}
