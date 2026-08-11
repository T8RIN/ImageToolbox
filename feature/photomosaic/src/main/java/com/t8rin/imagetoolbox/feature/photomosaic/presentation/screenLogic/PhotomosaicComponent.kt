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

package com.t8rin.imagetoolbox.feature.photomosaic.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import coil3.transform.Transformation
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.data.utils.toCoil
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.transformation.GenericTransformation
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.leftFrom
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.rightFrom
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.photomosaic.domain.PhotomosaicMaker
import com.t8rin.imagetoolbox.feature.photomosaic.domain.PhotomosaicParams
import com.t8rin.imagetoolbox.feature.photomosaic.presentation.screenLogic.PhotomosaicComponent.HistorySnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class PhotomosaicComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val photomosaicMaker: PhotomosaicMaker<Bitmap>,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
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

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private val _tileUris: MutableState<List<Uri>> = mutableStateOf(emptyList())
    val tileUris by _tileUris

    private val _params: MutableState<PhotomosaicParams> = mutableStateOf(PhotomosaicParams())
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

    fun setUris(uris: List<Uri>) {
        clearHistory()
        registerChangesCleared()
        val targetUris = uris.distinct()
        _uris.update { targetUris }
        _selectedUri.update { targetUris.firstOrNull() }
        _previewBitmap.update { null }
        _tileUris.update { tiles -> tiles.filterNot(targetUris::contains) }
        calculatePreview()
        resetHistory()
    }

    fun updateSelectedUri(uri: Uri) {
        _selectedUri.update { uri }
        _previewBitmap.update { null }
        calculatePreview()
    }

    fun updateUrisSilently(removedUri: Uri) {
        if (selectedUri == removedUri) {
            val index = uris.indexOf(removedUri)
            uris.getOrNull(if (index == 0) 1 else index - 1)?.let(::updateSelectedUri)
        }
        _uris.update { it - removedUri }
        if (uris.isEmpty()) {
            _selectedUri.update { null }
            _previewBitmap.update { null }
        }
        registerChanges()
    }

    fun setTileUris(uris: List<Uri>) {
        val updatedUris = uris
            .filterNot(this.uris::contains)
            .distinct()
            .take(params.maxTiles)
        if (updatedUris == tileUris) return

        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _tileUris.update { updatedUris }
        calculatePreview()
        commitHistoryFrom(beforeSnapshot)
    }

    fun addTileUris(uris: List<Uri>) {
        setTileUris((tileUris + uris).distinct())
    }

    fun removeTileAt(index: Int) {
        setTileUris(tileUris.toMutableList().apply { removeAt(index) })
    }

    fun updateParams(value: PhotomosaicParams) {
        val normalized = value.normalized()
        if (normalized == params) return

        beginPendingHistoryTransaction()
        _params.update { normalized }
        if (tileUris.size > params.maxTiles) {
            _tileUris.update { it.take(params.maxTiles) }
        }
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
        val target = selectedUri
        if (target == null || tileUris.isEmpty()) {
            _previewBitmap.update { null }
            return
        }

        debouncedImageCalculation(delay = 350) {
            _previewBitmap.update {
                photomosaicMaker.create(
                    targetUri = target.toString(),
                    tileUris = tileUris.map(Uri::toString),
                    params = params,
                    preview = true,
                    onProgress = { _, _ -> }
                )
            }
        }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun save(oneTimeSaveLocationUri: String?) {
        savingJob = trackProgress {
            prepareSaving()
            val results = uris.map { uri ->
                createResult(uri)?.let { bitmap ->
                    val info = bitmap.imageInfo(uri)
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = info,
                            originalUri = uri.toString(),
                            sequenceNumber = done + 1,
                            metadata = null,
                            data = imageCompressor.compressAndTransform(bitmap, info)
                        ),
                        keepOriginalMetadata = false,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    )
                } ?: SaveResult.Error.Exception(
                    IllegalStateException("Unable to create photomosaic")
                )
            }.onEach {
                updateSavingProgress()
                updateProgress(done = done, total = total)
            }

            parseSaveResults(results.onSuccess(::registerSave))
            _isSaving.update { false }
        }
    }

    fun share() {
        savingJob = trackProgress {
            prepareSaving()
            shareProvider.shareImages(
                uris = uris.map(Uri::toString),
                imageLoader = { uri ->
                    createResult(uri.toUri())?.let { bitmap ->
                        bitmap to bitmap.imageInfo(uri.toUri())
                    }
                },
                onProgressChange = { progress ->
                    if (progress == -1) {
                        AppToastHost.showConfetti()
                        _isSaving.update { false }
                        _done.update { 0 }
                    } else {
                        _done.update { progress }
                    }
                    updateProgress(done = done, total = total)
                }
            )
        }
    }

    fun cache(onComplete: (Uri) -> Unit) {
        val uri = selectedUri ?: return
        savingJob = trackProgress {
            prepareSaving(total = 1)
            createResult(uri)?.let { bitmap ->
                shareProvider.cacheImage(
                    image = bitmap,
                    imageInfo = bitmap.imageInfo(uri)
                )?.let { onComplete(it.toUri()) }
            }
            updateSavingProgress()
            _isSaving.update { false }
        }
    }

    fun cacheAll(onComplete: (List<Uri>) -> Unit) {
        savingJob = trackProgress {
            prepareSaving()
            val result = uris.mapNotNull { uri ->
                createResult(uri)?.let { bitmap ->
                    shareProvider.cacheImage(
                        image = bitmap,
                        imageInfo = bitmap.imageInfo(uri)
                    )?.toUri()
                }.also {
                    updateSavingProgress()
                    updateProgress(done = done, total = total)
                }
            }
            onComplete(result)
            _isSaving.update { false }
        }
    }

    private suspend fun createResult(target: Uri): Bitmap? {
        if (tileUris.isEmpty()) return null

        return photomosaicMaker.create(
            targetUri = target.toString(),
            tileUris = tileUris.map(Uri::toString),
            params = params,
            preview = false,
            onProgress = { _, _ -> }
        )
    }

    private fun prepareSaving(total: Int = uris.size) {
        _isSaving.update { true }
        _done.update { 0 }
        _total.update { total }
    }

    private fun updateSavingProgress() {
        _done.update { it + 1 }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    fun selectLeftUri() {
        uris.indexOf(selectedUri)
            .takeIf { it >= 0 }
            ?.let { uris.leftFrom(it) }
            ?.let(::updateSelectedUri)
    }

    fun selectRightUri() {
        uris.indexOf(selectedUri)
            .takeIf { it >= 0 }
            ?.let { uris.rightFrom(it) }
            ?.let(::updateSelectedUri)
    }

    fun getPhotomosaicTransformation(): Transformation {
        val transformationParams = params
        val transformationTileUris = tileUris.map(Uri::toString)

        return GenericTransformation<Bitmap>(
            key = transformationParams to transformationTileUris
        ) { input ->
            val target = if (maxOf(input.width, input.height) > 512) {
                imageScaler.scaleImage(
                    image = input,
                    width = 512,
                    height = 512,
                    resizeType = ResizeType.Flexible
                )
            } else {
                input
            }

            photomosaicMaker.create(
                target = target,
                tileUris = transformationTileUris,
                params = transformationParams,
                preview = true,
                onProgress = { _, _ -> }
            ) ?: target
        }.toCoil()
    }

    fun getFormatForFilenameSelection(): ImageFormat? =
        imageFormat.takeIf { uris.size == 1 }

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        tileUris = tileUris,
        params = params,
        imageFormat = imageFormat,
        quality = quality
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _tileUris.update { snapshot.tileUris }
        _params.update { snapshot.params }
        _imageFormat.update { snapshot.imageFormat }
        _quality.update { snapshot.quality }
        calculatePreview()
    }

    data class HistorySnapshot(
        val tileUris: List<Uri>,
        val params: PhotomosaicParams,
        val imageFormat: ImageFormat,
        val quality: Quality
    )

    private fun Bitmap.imageInfo(uri: Uri) = ImageInfo(
        width = width,
        height = height,
        imageFormat = imageFormat,
        quality = quality,
        originalUri = uri.toString()
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): PhotomosaicComponent
    }
}
