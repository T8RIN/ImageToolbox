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

@file:Suppress("ConstPropertyName")

package com.t8rin.imagetoolbox.feature.curves.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.curves.CurvesPresetCodec
import com.t8rin.curves.GPUImageToneCurveFilter
import com.t8rin.curves.ImageCurvesEditorState
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageData
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.transformation.GenericTransformation
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.leftFrom
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.rightFrom
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.transformation.ImageInfoTransformation
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.curves.presentation.screenLogic.CurvesComponent.HistorySnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class CurvesComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageInfoTransformationFactory: ImageInfoTransformation.Factory,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder
) : BaseHistoryComponent<HistorySnapshot>(
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext
) {

    init {
        debounce {
            initialUris?.let(::setUris)
        }
    }

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _selectedUri = mutableStateOf<Uri?>(null)
    val selectedUri by _selectedUri

    private val _bitmap = mutableStateOf<Bitmap?>(null)
    val bitmap by _bitmap

    private val _curvesState = mutableStateOf(ImageCurvesEditorState.Default)
    val curvesState by _curvesState

    private var lastControlPoints = curvesState.controlPoints

    private val _imageInfo = mutableStateOf(ImageInfo())
    val imageInfo by _imageInfo

    private val _keepExif = mutableStateOf(false)
    val keepExif by _keepExif

    private val _isSaving = mutableStateOf(false)
    val isSaving by _isSaving

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private var imageLoadingJob: Job? by smartJob {
        _isImageLoading.update { false }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun setUris(uris: List<Uri>?) {
        _uris.value = uris
        val uri = uris?.firstOrNull() ?: return
        _selectedUri.value = uri

        _isImageLoading.update { true }
        _imageInfo.update { it.copy(originalUri = uri.toString()) }
        imageGetter.getImageAsync(
            uri = uri.toString(),
            originalSize = false,
            onGetImage = ::setImageData,
            onFailure = AppToastHost::showFailureToast
        )
    }

    fun updateUrisSilently(removedUri: Uri) {
        componentScope.launch {
            val currentUris = uris.orEmpty()
            if (selectedUri == removedUri) {
                val index = currentUris.indexOf(removedUri)
                currentUris.getOrNull(
                    if (index <= 0) 1 else index - 1
                )?.let(::updateSelectedUri)
            }

            _uris.value = currentUris - removedUri
            if (_uris.value.isNullOrEmpty()) {
                _selectedUri.value = null
                _bitmap.value = null
            }
            registerChanges()
        }
    }

    fun calculatePreview() {
        selectedUri?.let(::loadSelectedUri)
    }

    private fun setImageData(imageData: ImageData<Bitmap>) {
        imageLoadingJob = componentScope.launch {
            _isImageLoading.update { true }
            val source = imageData.image
            _bitmap.value = imageScaler.scaleUntilCanShow(source)
            _imageInfo.value = imageData.imageInfo.copy(
                width = source.width,
                height = source.height,
                originalUri = selectedUri?.toString()
            )
            resetHistory()
            registerChangesCleared()
            _isImageLoading.update { false }
        }
    }

    fun setCurvesState(state: ImageCurvesEditorState) {
        val newControlPoints = state.controlPoints
        if (newControlPoints == lastControlPoints) return

        _curvesState.value = curvesState.copy(controlPoints = lastControlPoints)
        beginPendingHistoryTransaction()
        _curvesState.value = state.copy(controlPoints = newControlPoints)
        lastControlPoints = newControlPoints

        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        if (imageInfo.imageFormat == imageFormat) return

        if (pendingHistoryMode != PendingHistoryMode.FormatChange) {
            finalizePendingHistoryTransaction()
        }
        beginPendingHistoryTransaction(
            mode = PendingHistoryMode.FormatChange,
            commitDelayMillis = formatHistoryTransactionDebounce
        )
        _imageInfo.update {
            it.copy(
                imageFormat = imageFormat,
                quality = it.quality.coerceIn(imageFormat)
            )
        }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setKeepExif(keepExif: Boolean) {
        if (_keepExif.value == keepExif) return

        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _keepExif.value = keepExif
        commitHistoryFrom(beforeSnapshot)
    }

    fun resetValues() {
        finalizePendingHistoryTransaction()
        val defaultControlPoints = ImageCurvesEditorState.Default.controlPoints
        if (curvesState.controlPoints == defaultControlPoints) return

        val beforeSnapshot = currentHistorySnapshot()
        _curvesState.value = curvesState.copy(controlPoints = defaultControlPoints)
        lastControlPoints = defaultControlPoints
        commitHistoryFrom(beforeSnapshot)
    }

    fun importCurvesPreset(uri: Uri) {
        componentScope.launch {
            runSuspendCatching {
                val bytes = fileController.readBytes(uri.toString())
                require(bytes.size <= MaxPresetSizeBytes) { "The curve preset is too large" }
                val importedState = CurvesPresetCodec.decode(bytes)

                finalizePendingHistoryTransaction()
                val beforeSnapshot = currentHistorySnapshot()
                _curvesState.value = importedState
                lastControlPoints = importedState.controlPoints
                commitHistoryFrom(beforeSnapshot)
                AppToastHost.showConfetti()
            }.onFailure(AppToastHost::showFailureToast)
        }
    }

    fun exportCurvesPreset(uri: Uri) {
        componentScope.launch {
            fileController.writeBytes(uri.toString()) { output ->
                output.writeBytes(
                    CurvesPresetCodec.encode(curvesState).toByteArray(Charsets.UTF_8)
                )
            }.onSuccess(AppToastHost::showConfetti)
                .let { result ->
                    if (result is SaveResult.Error) {
                        AppToastHost.showFailureToast(result.throwable)
                    }
                }
        }
    }

    fun createTargetFilename(): String = "ImageToolbox_Curves_${timestamp()}.itcurve"

    fun updateSelectedUri(uri: Uri) {
        if (selectedUri == uri) return
        _selectedUri.value = uri
        loadSelectedUri(uri)
    }

    private fun loadSelectedUri(uri: Uri) {
        imageLoadingJob = componentScope.launch {
            runSuspendCatching {
                _isImageLoading.update { true }
                val selectedFormat = imageInfo.imageFormat
                val imageData = imageGetter.getImage(
                    uri = uri.toString(),
                    originalSize = false
                ) ?: return@runSuspendCatching
                val source = imageData.image
                _bitmap.value = imageScaler.scaleUntilCanShow(source)
                _imageInfo.value = imageData.imageInfo.copy(
                    width = source.width,
                    height = source.height,
                    imageFormat = selectedFormat,
                    quality = imageInfo.quality.coerceIn(selectedFormat),
                    originalUri = uri.toString()
                )
            }.onFailure(AppToastHost::showFailureToast)
            _isImageLoading.update { false }
        }
    }

    private suspend fun renderFullImage(uri: String): Pair<Bitmap, ImageInfo>? {
        val source = imageGetter.getImage(uri)?.image ?: return null
        val rendered = renderCurves(
            bitmap = source,
            controlPoints = curvesState.controlPoints
        )
        val info = imageInfo.copy(
            width = source.width,
            height = source.height,
            originalUri = uri
        )
        return rendered to info
    }

    private suspend fun renderCurves(
        bitmap: Bitmap,
        controlPoints: List<List<Float>>
    ): Bitmap {
        val state = ImageCurvesEditorState(controlPoints)
        return withContext(defaultDispatcher) {
            if (state.isDefault()) {
                bitmap
            } else {
                GPUImage(appContext).apply {
                    setImage(bitmap)
                    setFilter(GPUImageToneCurveFilter(state))
                }.bitmapWithFilterApplied
            }
        }
    }

    fun saveBitmaps(oneTimeSaveLocationUri: String?) {
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            val results = mutableListOf<SaveResult>()

            uris.orEmpty().forEach { uri ->
                runSuspendCatching {
                    renderFullImage(uri.toString())
                }.getOrNull()?.let { (image, info) ->
                    results += fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = info,
                            metadata = null,
                            originalUri = uri.toString(),
                            sequenceNumber = done + 1,
                            data = imageCompressor.compressAndTransform(
                                image = image,
                                imageInfo = info
                            )
                        ),
                        keepOriginalMetadata = keepExif,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    )
                } ?: run {
                    results += SaveResult.Error.Exception(
                        Throwable("Failed to process image: $uri")
                    )
                }

                _done.value += 1
                updateProgress(done = done, total = uris.orEmpty().size)
            }

            parseSaveResults(results.onSuccess(::registerSave))
            _isSaving.value = false
        }
    }

    fun shareBitmaps() {
        savingJob = trackProgress {
            _isSaving.value = true
            shareProvider.shareImages(
                uris = uris.orEmpty().map(Uri::toString),
                imageLoader = ::renderFullImage,
                onProgressChange = {
                    if (it == -1) {
                        AppToastHost.showConfetti()
                        _isSaving.value = false
                        _done.value = 0
                    } else {
                        _done.value = it
                    }
                    updateProgress(done = done, total = uris.orEmpty().size)
                }
            )
        }
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        val uri = selectedUri?.toString() ?: return
        savingJob = trackProgress {
            _isSaving.value = true
            renderFullImage(uri)?.let { (image, info) ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = info
                )?.let { onComplete(it.toUri()) }
            }
            _isSaving.value = false
        }
    }

    fun cacheImages(onComplete: (List<Uri>) -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            val cachedUris = mutableListOf<Uri>()
            uris.orEmpty().forEach { uri ->
                renderFullImage(uri.toString())?.let { (image, info) ->
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = info
                    )?.let { cachedUris += it.toUri() }
                }
                _done.value += 1
                updateProgress(done = done, total = uris.orEmpty().size)
            }
            onComplete(cachedUris)
            _isSaving.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun selectLeftUri() {
        uris
            ?.indexOf(selectedUri ?: Uri.EMPTY)
            ?.takeIf { it >= 0 }
            ?.let { uris?.leftFrom(it) }
            ?.let(::updateSelectedUri)
    }

    fun selectRightUri() {
        uris
            ?.indexOf(selectedUri ?: Uri.EMPTY)
            ?.takeIf { it >= 0 }
            ?.let { uris?.rightFrom(it) }
            ?.let(::updateSelectedUri)
    }

    fun getFormatForFilenameSelection(): ImageFormat? =
        imageInfo.imageFormat.takeIf { uris?.size == 1 }

    fun getConversionTransformation() = curvesState.controlPoints.let { controlPoints ->
        listOf(
            imageInfoTransformationFactory(
                imageInfo = imageInfo,
                preset = Preset.Original,
                transformations = listOf(
                    GenericTransformation(
                        key = controlPoints
                    ) { bitmap: Bitmap ->
                        renderCurves(
                            bitmap = bitmap,
                            controlPoints = controlPoints
                        )
                    }
                )
            )
        )
    }

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        controlPoints = curvesState.controlPoints,
        imageInfo = imageInfo.asHistoryImageInfo(),
        keepExif = keepExif,
        backgroundColorForNoAlphaFormats = settingsManager
            .settingsState
            .value
            .backgroundForNoAlphaImageFormats
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _curvesState.value = curvesState.copy(controlPoints = snapshot.controlPoints)
        lastControlPoints = snapshot.controlPoints
        _imageInfo.update {
            it.copy(
                imageFormat = snapshot.imageInfo.imageFormat,
                quality = snapshot.imageInfo.quality
            )
        }
        _keepExif.value = snapshot.keepExif
        restoreBackgroundColorForNoAlphaFormats(
            settingsManager = settingsManager,
            backgroundColorForNoAlphaFormats = snapshot.backgroundColorForNoAlphaFormats
        )
    }

    private fun ImageInfo.asHistoryImageInfo() = ImageInfo(
        imageFormat = imageFormat,
        quality = quality
    )

    data class HistorySnapshot(
        val controlPoints: List<List<Float>>,
        val imageInfo: ImageInfo,
        val keepExif: Boolean,
        val backgroundColorForNoAlphaFormats: ColorModel
    )

    private companion object {
        const val MaxPresetSizeBytes = 20 * 1024 * 1024
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): CurvesComponent
    }
}
