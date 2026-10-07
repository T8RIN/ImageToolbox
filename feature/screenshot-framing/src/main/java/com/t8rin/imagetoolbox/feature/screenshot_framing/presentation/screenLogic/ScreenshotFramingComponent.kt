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

package com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.t8rin.dynamic.theme.extractPrimaryColor
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.palette_selection.GradientBackgroundPreset
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.feature.screenshot_framing.data.getScreenshotFramingImage
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.components.renderScreenshotFramingBitmap
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.model.ScreenshotFramingParams
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class ScreenshotFramingComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val imageGetter: ImageGetter<Bitmap>,
    private val fileController: FileController,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseHistoryComponent<ScreenshotFramingParams>(dispatchersHolder, componentContext) {

    private val _params = mutableStateOf(ScreenshotFramingParams())
    val params: ScreenshotFramingParams by _params

    private val _uri = mutableStateOf<Uri?>(null)
    val uri: Uri? by _uri

    private var sourceBitmap: Bitmap? = null

    private val _previewBitmap = mutableStateOf<Bitmap?>(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _isSaving = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _isPreviewLoading = mutableStateOf(false)
    val isPreviewLoading: Boolean by _isPreviewLoading

    private var loadingJob: Job? by smartJob()
    private var previewJob: Job? by smartJob()
    private var savingJob: Job? by smartJob()

    init {
        resetHistory()
        initialUri?.let(::setUri)

        doOnDestroy {
            loadingJob = null
            previewJob = null
            savingJob = null
            sourceBitmap = null
            _previewBitmap.value = null
        }
    }

    fun setUri(value: Uri) {
        loadingJob = componentScope.launch {
            _isImageLoading.value = true
            try {
                runSuspendCatching {
                    val bitmap = withContext(defaultDispatcher) {
                        imageGetter.getScreenshotFramingImage(value, maxSide = 1600)
                    } ?: error(getString(R.string.failed_to_open))
                    val colors = withContext(defaultDispatcher) {
                        val primary = bitmap.extractPrimaryColor(default = 0xFF6D5DFB.toInt())
                            .copy(alpha = 1f)
                        listOf(lerp(primary, Color.White, 0.6f), lerp(primary, Color.Black, 0.18f))
                    }
                    ensureActive()
                    sourceBitmap = bitmap
                    _previewBitmap.value = null
                    _uri.value = value
                    if (params.preset == null) {
                        _params.value = params.copy(backgroundColors = colors)
                    }
                    resetHistory()
                    registerChanges()
                    updatePreview(debounce = false)
                }.onFailure(AppToastHost::showFailureToast)
            } finally {
                _isImageLoading.value = false
            }
        }
    }

    fun updatePreset(value: GradientBackgroundPreset) = updateParams {
        copy(
            preset = value,
            backgroundColors = listOf(value.startColor, value.endColor)
        )
    }

    fun updateGradientPalette(value: GradientPalette) = updateParams {
        copy(
            preset = GradientBackgroundPreset.Custom,
            backgroundColors = value.colors.map { it.toColor() }
        )
    }

    fun updateStartColor(value: Color) = updateParams {
        copy(
            preset = GradientBackgroundPreset.Custom,
            backgroundColors = backgroundColors.toMutableList().apply { this[0] = value }
        )
    }

    fun updateEndColor(value: Color) = updateParams {
        copy(
            preset = GradientBackgroundPreset.Custom,
            backgroundColors = backgroundColors.toMutableList().apply { this[lastIndex] = value }
        )
    }

    fun updateGradient(value: Boolean) = updateParams { copy(useGradient = value) }

    fun updateAspectRatio(value: DomainAspectRatio) = updateParams {
        copy(aspectRatio = value)
    }

    fun updatePadding(value: Int) = updateParams { copy(padding = value.coerceIn(0, 40)) }

    fun updateCornerRadius(value: Int) = updateParams {
        copy(cornerRadius = value.coerceIn(0, 20))
    }

    fun updateShadow(value: Boolean) = updateParams { copy(showShadow = value) }

    fun updateShadowBlur(value: Int) = updateParams { copy(shadowBlur = value.coerceIn(1, 12)) }

    fun updateShadowOpacity(value: Int) = updateParams {
        copy(shadowOpacity = value.coerceIn(0, 100))
    }

    fun updateOutputFormat(value: ImageFormat) = updateParams { copy(outputFormat = value) }

    override fun currentHistorySnapshot(): ScreenshotFramingParams = params

    override fun applyHistorySnapshot(snapshot: ScreenshotFramingParams) {
        val previous = params
        _params.value = snapshot
        if (previous.copy(outputFormat = snapshot.outputFormat) != snapshot) updatePreview()
    }

    private fun updateParams(transform: ScreenshotFramingParams.() -> ScreenshotFramingParams) {
        val previous = params
        val updated = previous.transform()
        if (updated == previous) return
        beginPendingHistoryTransaction()
        _params.value = updated
        if (previous.copy(outputFormat = updated.outputFormat) != updated) updatePreview()
        schedulePendingHistoryCommit()
        registerChanges()
    }

    private fun updatePreview(debounce: Boolean = true) {
        val image = sourceBitmap ?: return
        val renderParams = params
        previewJob = componentScope.launch {
            _isPreviewLoading.value = true
            try {
                if (debounce) delay(100)
                runSuspendCatching {
                    val result = withContext(defaultDispatcher) {
                        renderScreenshotFramingBitmap(
                            image = image,
                            params = renderParams,
                            maxBitmapPixels = 1_500_000f
                        )
                    }
                    ensureActive()
                    if (
                        sourceBitmap === image &&
                        params.copy(outputFormat = renderParams.outputFormat) == renderParams
                    ) {
                        _previewBitmap.value = result
                    }
                }.onFailure(AppToastHost::showFailureToast)
            } finally {
                _isPreviewLoading.value = false
            }
        }
    }

    fun saveBitmap(oneTimeSaveLocationUri: String?) = exportBitmap { bitmap, renderParams, source ->
        parseSaveResult(
            fileController.save(
                saveTarget = ImageSaveTarget(
                    imageInfo = bitmap.imageInfo(renderParams.outputFormat),
                    originalUri = source.toString(),
                    sequenceNumber = null,
                    data = imageCompressor.compress(
                        image = bitmap,
                        imageFormat = renderParams.outputFormat,
                        quality = Quality.Base(100)
                    )
                ),
                keepOriginalMetadata = false,
                oneTimeSaveLocationUri = oneTimeSaveLocationUri
            ).onSuccess {
                if (params == renderParams && uri == source) registerSave()
            }
        )
    }

    fun shareBitmap() = exportBitmap { bitmap, renderParams, _ ->
        shareProvider.shareImage(
            image = bitmap,
            imageInfo = bitmap.imageInfo(renderParams.outputFormat),
            onComplete = AppToastHost::showConfetti
        )
    }

    fun cacheBitmap(onComplete: (Uri) -> Unit) = exportBitmap { bitmap, renderParams, _ ->
        shareProvider.cacheImage(
            image = bitmap,
            imageInfo = bitmap.imageInfo(renderParams.outputFormat)
        )?.let { onComplete(it.toUri()) }
    }

    private fun exportBitmap(
        action: suspend (Bitmap, ScreenshotFramingParams, Uri) -> Unit
    ) {
        val source = uri ?: return
        val renderParams = params
        savingJob = trackProgress {
            _isSaving.value = true
            try {
                runSuspendCatching {
                    val result = withContext(defaultDispatcher) {
                        val image = imageGetter.getScreenshotFramingImage(
                            uri = source,
                            maxSide = 4096
                        ) ?: error(getString(R.string.failed_to_open))
                        ensureActive()
                        renderScreenshotFramingBitmap(image, renderParams)
                    }
                    try {
                        currentCoroutineContext().ensureActive()
                        action(result, renderParams, source)
                    } finally {
                        result.recycle()
                    }
                }.onFailure(AppToastHost::showFailureToast)
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun cancelSaving() {
        savingJob = null
        _isSaving.value = false
    }

    private fun Bitmap.imageInfo(format: ImageFormat) = ImageInfo(
        width = width,
        height = height,
        imageFormat = format
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): ScreenshotFramingComponent
    }
}