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

package com.t8rin.imagetoolbox.feature.fractal_generation.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.FractalRenderer
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalCamera
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalFormula
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalParams
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalRenderRequest
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalViewport
import com.t8rin.imagetoolbox.feature.fractal_generation.presentation.model.FractalPreviewFrame
import com.t8rin.imagetoolbox.feature.fractal_generation.presentation.screenLogic.FractalGenerationComponent.HistorySnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt
import kotlin.math.sqrt

class FractalGenerationComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    dispatchersHolder: DispatchersHolder,
    private val fractalRenderer: FractalRenderer<Bitmap>,
    private val fileController: FileController,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val settingsManager: SettingsManager
) : BaseHistoryComponent<HistorySnapshot>(
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext
) {

    private val _previewFrame: MutableState<FractalPreviewFrame?> = mutableStateOf(null)
    val previewFrame: FractalPreviewFrame? by _previewFrame
    val previewBitmap: Bitmap?
        get() = previewFrame?.bitmap
    private val retiredPreviewBitmaps = mutableListOf<Bitmap>()
    private var isPreviewAttached = false
    private var isDestroyed = false

    private val _params = mutableStateOf(FractalParams.Default)
    val params: FractalParams by _params

    private val _outputSize = mutableStateOf(IntegerSize(DEFAULT_OUTPUT_SIZE, DEFAULT_OUTPUT_SIZE))
    val outputSize: IntegerSize by _outputSize

    private val _specifiedOutputSize = mutableStateOf(IntegerSize.Zero)
    val specifiedOutputSize: IntegerSize by _specifiedOutputSize

    private val _imageFormat = mutableStateOf<ImageFormat>(ImageFormat.Png.Lossless)
    val imageFormat: ImageFormat by _imageFormat

    private val _quality = mutableStateOf<Quality>(Quality.Base(100))
    val quality: Quality by _quality

    private val _isSaving = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    val supportedFormulas: List<FractalFormula> = FractalFormula.entries.filter {
        it in fractalRenderer.supportedFormulas
    }

    private var previewRequestId = 0
    private var exportRequestId = 0
    private var outputSizeInitialized = false
    private var availableOutputSize = IntegerSize(DEFAULT_OUTPUT_SIZE, DEFAULT_OUTPUT_SIZE)
    private var viewportGestureActive = false
    private var paramsBeforeGesture: FractalParams? = null
    private var hasShownDeepZoomWarning = false

    init {
        resetHistory()
        doOnDestroy {
            previewRequestId += 1
            exportRequestId += 1
            cancelImageLoading()
            savingJob?.cancel()
            savingJob = null
            isDestroyed = true
            retireCurrentPreviewFrame()
            if (!isPreviewAttached) releaseRetiredPreviewBitmaps()
        }
    }

    private var savingJob: Job? = null

    private val maxOutputPixels: Long
        get() = FractalRenderRequest.maxOutputPixelsFor(params.formula)

    fun updateParams(value: FractalParams) {
        val normalized = value.normalized()
        val constrainedSpecifiedOutputSize = specifiedOutputSize
            .takeUnless(IntegerSize::isZero)
            ?.fitWithinOutputLimits(
                maxPixels = FractalRenderRequest.maxOutputPixelsFor(normalized.formula)
            )
            ?: specifiedOutputSize
        val constrainedOutputSize = constrainedSpecifiedOutputSize
            .takeUnless(IntegerSize::isZero)
            .let { it ?: availableOutputSize }
            .fitWithinOutputLimits(
                maxPixels = FractalRenderRequest.maxOutputPixelsFor(normalized.formula)
            )
        if (
            _params.value == normalized &&
            outputSize == constrainedOutputSize &&
            specifiedOutputSize == constrainedSpecifiedOutputSize
        ) return

        beginPendingHistoryTransaction()
        _params.update { normalized }
        _outputSize.update { constrainedOutputSize }
        _specifiedOutputSize.update { constrainedSpecifiedOutputSize }
        updatePreview()
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setFormula(formula: FractalFormula) {
        if (formula in supportedFormulas && params.formula != formula) {
            updateParams(params.withFormula(formula))
        }
    }

    fun setOutputWidth(width: Int) {
        setSpecifiedOutputSize(
            specifiedOutputSize.copy(width = width.coerceIn(0, MAX_OUTPUT_DIMENSION))
        )
    }

    fun setOutputHeight(height: Int) {
        setSpecifiedOutputSize(
            specifiedOutputSize.copy(height = height.coerceIn(0, MAX_OUTPUT_DIMENSION))
        )
    }

    private fun setSpecifiedOutputSize(value: IntegerSize) {
        val constrainedValue = value
            .takeUnless(IntegerSize::isZero)
            ?.fitWithinOutputLimits(maxOutputPixels)
            ?: value
        val resolvedOutputSize = constrainedValue
            .takeUnless(IntegerSize::isZero)
            ?: availableOutputSize.fitWithinOutputLimits(maxOutputPixels)
        if (
            specifiedOutputSize == constrainedValue &&
            outputSize == resolvedOutputSize
        ) return

        val outputSizeChanged = outputSize != resolvedOutputSize
        beginPendingHistoryTransaction()
        _specifiedOutputSize.update { constrainedValue }
        _outputSize.update { resolvedOutputSize }
        if (outputSizeChanged) updatePreview()
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setImageFormat(value: ImageFormat) {
        if (imageFormat != value) {
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
    }

    fun setQuality(value: Quality) {
        if (quality != value) {
            beginPendingHistoryTransaction()
            _quality.update { value }
            registerChanges()
            schedulePendingHistoryCommit()
        }
    }

    fun onViewportGestureStart() {
        if (viewportGestureActive) return

        finalizePendingHistoryTransaction()
        previewRequestId += 1
        cancelImageLoading()
        beginPendingHistoryTransaction()
        paramsBeforeGesture = params
        viewportGestureActive = true
    }

    fun updateAvailableOutputSize(
        width: Int,
        height: Int
    ) {
        if (width <= 0 || height <= 0) return

        val newAvailableOutputSize = IntegerSize(width, height)
        if (outputSizeInitialized && availableOutputSize == newAvailableOutputSize) return

        val wasInitialized = outputSizeInitialized
        availableOutputSize = newAvailableOutputSize
        val resolvedOutputSize = specifiedOutputSize
            .takeUnless(IntegerSize::isZero)
            ?: availableOutputSize.fitWithinOutputLimits(maxOutputPixels)
        val outputSizeChanged = outputSize != resolvedOutputSize
        outputSizeInitialized = true
        _outputSize.update {
            resolvedOutputSize
        }
        if (wasInitialized) {
            if (outputSizeChanged) updatePreview(delay = 0L)
        } else {
            resetHistory()
            updatePreview(delay = 0L)
        }
    }

    fun onViewportGesture(
        anchorX: Float,
        anchorY: Float,
        panX: Float,
        panY: Float,
        zoomFactor: Float,
        aspectRatio: Float
    ) {
        if (!viewportGestureActive) onViewportGestureStart()

        _params.update {
            if (it.formula.isThreeDimensional) {
                val camera = it.camera
                    .orbit(
                        yawDelta = panX * FractalCamera.ORBIT_DEGREES_PER_VIEWPORT,
                        pitchDelta = panY * FractalCamera.ORBIT_DEGREES_PER_VIEWPORT
                    )
                    .zoomBy(zoomFactor.toDouble())
                it.copy(camera = camera).normalized()
            } else {
                val requestedViewport = it.viewport
                    .zoomBy(
                        factor = zoomFactor.toDouble(),
                        anchorX = anchorX.toDouble(),
                        anchorY = anchorY.toDouble(),
                        aspectRatio = aspectRatio.toDouble()
                    )
                    .panBy(
                        normalizedDeltaX = -panX.toDouble(),
                        normalizedDeltaY = -panY.toDouble(),
                        aspectRatio = aspectRatio.toDouble()
                    )
                if (
                    !it.isDeepZoomAvailable &&
                    requestedViewport.span < FractalViewport.MIN_DIRECT_RENDER_SPAN &&
                    !hasShownDeepZoomWarning
                ) {
                    hasShownDeepZoomWarning = true
                    AppToastHost.showToast(R.string.fractal_deep_zoom_unavailable)
                }
                it.copy(viewport = requestedViewport).normalized()
            }
        }
    }

    fun onViewportGestureEnd() {
        if (!viewportGestureActive) return

        val paramsChanged = paramsBeforeGesture != params
        viewportGestureActive = false
        paramsBeforeGesture = null
        if (paramsChanged) {
            registerChanges()
            schedulePendingHistoryCommit()
        } else {
            cancelPendingHistoryTransaction()
        }
        updatePreview(delay = 0L)
    }

    fun resetViewport() {
        val resetParams = params.resetView()
        if (params == resetParams) return

        beginPendingHistoryTransaction()
        _params.update { resetParams }
        registerChanges()
        schedulePendingHistoryCommit()
        updatePreview(delay = 0L)
    }

    fun onPreviewFrameDisplayed(bitmap: Bitmap) {
        if (_previewFrame.value?.bitmap !== bitmap) return

        releaseRetiredPreviewBitmaps()
    }

    fun onPreviewAttached() {
        if (!isDestroyed) isPreviewAttached = true
    }

    fun onPreviewDetached() {
        isPreviewAttached = false
        releaseRetiredPreviewBitmaps()
    }

    fun coordinateAt(
        normalizedX: Float,
        normalizedY: Float,
        aspectRatio: Float
    ): String {
        val coordinate = params.viewport.pointAt(
            normalizedX = normalizedX.toDouble(),
            normalizedY = normalizedY.toDouble(),
            aspectRatio = aspectRatio.toDouble()
        )
        return "${coordinate.real.toShortString()}, ${coordinate.imaginary.toShortString()}i"
    }

    fun copyCoordinateAt(
        normalizedX: Float,
        normalizedY: Float,
        aspectRatio: Float
    ) {
        Clipboard.copy(coordinateAt(normalizedX, normalizedY, aspectRatio))
    }

    fun saveFractal(oneTimeSaveLocationUri: String?) {
        finalizePendingHistoryTransaction()
        val requestId = ++exportRequestId
        savingJob?.cancel()
        val snapshot = currentExportSnapshot()
        val shouldResumePreview = pausePreviewRendering()
        savingJob = trackProgress {
            _isSaving.update { true }
            try {
                runCatching {
                    withRenderedOutput(snapshot) { bitmap ->
                        val imageInfo = bitmap.imageInfo(snapshot)
                        fileController.save(
                            saveTarget = ImageSaveTarget(
                                imageInfo = imageInfo,
                                metadata = null,
                                originalUri = "",
                                sequenceNumber = null,
                                data = imageCompressor.compress(
                                    image = bitmap,
                                    imageFormat = snapshot.imageFormat,
                                    quality = snapshot.quality
                                )
                            ),
                            keepOriginalMetadata = true,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        ).onSuccess {
                            if (snapshot == currentExportSnapshot()) registerSave()
                        }
                    }
                }.onSuccess(::parseSaveResult).onFailure(::handleRenderFailure)
            } finally {
                if (requestId == exportRequestId) {
                    savingJob = null
                    _isSaving.update { false }
                    if (shouldResumePreview) updatePreview(delay = 0L)
                }
            }
        }
    }

    fun cacheCurrentFractal(onComplete: (Uri) -> Unit) {
        val requestId = ++exportRequestId
        savingJob?.cancel()
        val snapshot = currentExportSnapshot()
        val shouldResumePreview = pausePreviewRendering()
        savingJob = trackProgress {
            _isSaving.update { true }
            try {
                runCatching {
                    withRenderedOutput(snapshot) { bitmap ->
                        shareProvider.cacheImage(
                            image = bitmap,
                            imageInfo = bitmap.imageInfo(snapshot)
                        )
                    }
                }.onSuccess { uri ->
                    uri?.let { onComplete(it.toUri()) }
                }.onFailure(::handleRenderFailure)
            } finally {
                if (requestId == exportRequestId) {
                    savingJob = null
                    _isSaving.update { false }
                    if (shouldResumePreview) updatePreview(delay = 0L)
                }
            }
        }
    }

    fun shareFractal() {
        cacheCurrentFractal { uri ->
            componentScope.launch {
                shareProvider.shareUri(
                    uri = uri.toString(),
                    onComplete = AppToastHost::showConfetti
                )
            }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat

    private fun pausePreviewRendering(): Boolean {
        val shouldResume = previewBitmap == null || isImageLoading
        previewRequestId += 1
        cancelImageLoading()
        return shouldResume
    }

    private fun currentExportSnapshot(): ExportSnapshot = ExportSnapshot(
        params = params,
        outputSize = outputSize,
        imageFormat = imageFormat,
        quality = quality
    )

    private suspend fun renderOutput(snapshot: ExportSnapshot): Bitmap = fractalRenderer.render(
        snapshot.params.toRenderRequest(
            width = snapshot.outputSize.width,
            height = snapshot.outputSize.height
        )
    )

    private suspend fun <T> withRenderedOutput(
        snapshot: ExportSnapshot,
        block: suspend (Bitmap) -> T
    ): T {
        val bitmap = renderOutput(snapshot)
        return try {
            block(bitmap)
        } finally {
            bitmap.recycle()
        }
    }

    private fun Bitmap.imageInfo(snapshot: ExportSnapshot): ImageInfo = ImageInfo(
        width = width,
        height = height,
        quality = snapshot.quality,
        imageFormat = snapshot.imageFormat
    )

    private fun handleRenderFailure(throwable: Throwable) {
        if (throwable is CancellationException) throw throwable

        parseSaveResult(SaveResult.Error.Exception(throwable))
    }

    private fun updatePreview(delay: Long = PREVIEW_DEBOUNCE) {
        val requestId = ++previewRequestId
        val paramsSnapshot = params
        val sizeSnapshot = outputSize
        val viewportAspectRatio = sizeSnapshot.width.toDouble() / sizeSnapshot.height
        val previewSize = if (
            paramsSnapshot.viewport.span < FractalViewport.MIN_DIRECT_RENDER_SPAN
        ) {
            DEEP_ZOOM_PREVIEW_SIZE
        } else {
            PREVIEW_SIZE
        }
        val targetSize = sizeSnapshot.fitWithin(previewSize, previewSize)
        val outputPlan = paramsSnapshot.toRenderRequest(
            width = sizeSnapshot.width,
            height = sizeSnapshot.height
        ).resolvePlan()
        val previewParams = outputPlan.params

        debouncedImageCalculation(delay = delay) {
            val bitmap = fractalRenderer.render(
                previewParams.toRenderRequest(
                    width = targetSize.width,
                    height = targetSize.height,
                    viewportAspectRatio = viewportAspectRatio
                )
            )
            applyPreviewIfCurrent(
                requestId = requestId,
                bitmap = bitmap
            )
        }
        _isImageLoading.update { true }
    }

    private suspend fun applyPreviewIfCurrent(
        requestId: Int,
        bitmap: Bitmap
    ) {
        var accepted = false
        try {
            withContext(uiDispatcher) {
                currentCoroutineContext().ensureActive()
                if (requestId == previewRequestId) {
                    val previousFrame = _previewFrame.value
                    _previewFrame.value = FractalPreviewFrame(
                        bitmap = bitmap,
                        targetRevision = requestId
                    )
                    previousFrame?.bitmap?.let { previousBitmap ->
                        if (
                            previousBitmap !== bitmap &&
                            retiredPreviewBitmaps.none { it === previousBitmap }
                        ) {
                            retiredPreviewBitmaps += previousBitmap
                        }
                    }
                    accepted = true
                    if (!isPreviewAttached) releaseRetiredPreviewBitmaps()
                }
            }
        } finally {
            if (!accepted) bitmap.recycle()
        }
    }

    private fun retireCurrentPreviewFrame() {
        _previewFrame.value?.bitmap?.let { bitmap ->
            if (retiredPreviewBitmaps.none { it === bitmap }) {
                retiredPreviewBitmaps += bitmap
            }
        }
        _previewFrame.value = null
    }

    private fun releaseRetiredPreviewBitmaps() {
        retiredPreviewBitmaps.forEach { bitmap ->
            if (!bitmap.isRecycled) bitmap.recycle()
        }
        retiredPreviewBitmaps.clear()
    }

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        params = params,
        outputSize = outputSize,
        specifiedOutputSize = specifiedOutputSize,
        imageFormat = imageFormat,
        quality = quality,
        backgroundColorForNoAlphaFormats = settingsManager
            .settingsState
            .value
            .backgroundForNoAlphaImageFormats
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        viewportGestureActive = false
        paramsBeforeGesture = null
        _params.update { snapshot.params }
        _outputSize.update { snapshot.outputSize }
        _specifiedOutputSize.update { snapshot.specifiedOutputSize }
        _imageFormat.update { snapshot.imageFormat }
        _quality.update { snapshot.quality }
        restoreBackgroundColorForNoAlphaFormats(
            settingsManager = settingsManager,
            backgroundColorForNoAlphaFormats = snapshot.backgroundColorForNoAlphaFormats
        )
        updatePreview(delay = 0L)
    }

    data class HistorySnapshot(
        val params: FractalParams = FractalParams.Default,
        val outputSize: IntegerSize = IntegerSize(DEFAULT_OUTPUT_SIZE, DEFAULT_OUTPUT_SIZE),
        val specifiedOutputSize: IntegerSize = IntegerSize.Zero,
        val imageFormat: ImageFormat = ImageFormat.Png.Lossless,
        val quality: Quality = Quality.Base(100),
        val backgroundColorForNoAlphaFormats: ColorModel = ColorModel(-0x1000000)
    )

    private data class ExportSnapshot(
        val params: FractalParams,
        val outputSize: IntegerSize,
        val imageFormat: ImageFormat,
        val quality: Quality
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): FractalGenerationComponent
    }

    private companion object {
        const val DEFAULT_OUTPUT_SIZE = 1080
        const val MAX_OUTPUT_DIMENSION = FractalRenderRequest.MAX_OUTPUT_DIMENSION
        const val PREVIEW_SIZE = 2048
        const val DEEP_ZOOM_PREVIEW_SIZE = 640
        const val PREVIEW_DEBOUNCE = 350L
    }
}

private fun IntegerSize.fitWithin(
    maxWidth: Int,
    maxHeight: Int
): IntegerSize {
    val scale = minOf(
        maxWidth.toDouble() / width.coerceAtLeast(1),
        maxHeight.toDouble() / height.coerceAtLeast(1),
        1.0
    )
    return IntegerSize(
        width = (width * scale).roundToInt().coerceAtLeast(1),
        height = (height * scale).roundToInt().coerceAtLeast(1)
    )
}

private fun IntegerSize.fitWithinOutputLimits(maxPixels: Long): IntegerSize {
    val pixelCount = width.toLong() * height
    val scale = minOf(
        1.0,
        FractalRenderRequest.MAX_OUTPUT_DIMENSION.toDouble() / width,
        FractalRenderRequest.MAX_OUTPUT_DIMENSION.toDouble() / height,
        sqrt(maxPixels.toDouble() / pixelCount)
    )
    val scaledHeight = (height * scale).roundToInt().coerceAtLeast(1)
    val scaledWidth = (width * scale)
        .roundToInt()
        .coerceIn(
            1,
            (maxPixels / scaledHeight)
                .toInt()
                .coerceAtMost(FractalRenderRequest.MAX_OUTPUT_DIMENSION)
        )
    return IntegerSize(scaledWidth, scaledHeight)
}

private fun BigDecimal.toShortString(): String {
    val value = stripTrailingZeros()
    val exponent = value.precision() - value.scale() - 1
    return if (exponent in -5..6) value.toPlainString() else value.toEngineeringString()
}
