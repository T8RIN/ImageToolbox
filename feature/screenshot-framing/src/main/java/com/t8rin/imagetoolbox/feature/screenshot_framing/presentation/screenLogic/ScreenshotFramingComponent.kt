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
import androidx.compose.runtime.mutableIntStateOf
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
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.leftFrom
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.rightFrom
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.widget.palette_selection.GradientBackgroundPreset
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.feature.screenshot_framing.data.getScreenshotFramingImage
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.components.renderScreenshotFramingBitmap
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.model.ScreenshotFramingParams
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.model.ScreenshotFramingStyle
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.model.ScreenshotFramingStyles
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.model.toStyle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class ScreenshotFramingComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
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

    private var savedStyles by fileController.savable(
        scope = componentScope,
        initial = ScreenshotFramingStyles()
    )
    internal val styles: List<Pair<String, ScreenshotFramingStyle>>
        get() = savedStyles.styles.toList()

    private val _uris = mutableStateOf(emptyList<Uri>())
    val uris by _uris

    private val _selectedUri = mutableStateOf<Uri?>(null)
    val selectedUri by _selectedUri

    private var sourceBitmap: Bitmap? = null

    private val _previewBitmap = mutableStateOf<Bitmap?>(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _isSaving = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _done = mutableIntStateOf(0)
    val done by _done

    private val _left = mutableIntStateOf(-1)
    val left by _left

    private val _isPreviewLoading = mutableStateOf(false)
    val isPreviewLoading: Boolean by _isPreviewLoading

    private var imageLoadingJob: Job? by smartJob()
    private var previewJob: Job? by smartJob()
    private var savingJob: Job? by smartJob()

    init {
        resetHistory()
        initialUris?.let(::setUris)

        doOnDestroy {
            imageLoadingJob = null
            previewJob = null
            savingJob = null
            sourceBitmap = null
            _previewBitmap.value = null
        }
    }

    fun setUris(uris: List<Uri>) {
        val uri = uris.firstOrNull() ?: return
        _uris.value = uris.distinct()
        _selectedUri.value = uri
        registerChanges()
        loadSelectedUri(uri, isNewSelection = true)
    }

    fun updateUrisSilently(removedUri: Uri) {
        val index = uris.indexOf(removedUri)
        if (index < 0 || uris.size <= 1) return

        _uris.value = uris - removedUri
        if (removedUri == selectedUri) updateSelectedUri(uris[(index - 1).coerceAtLeast(0)])
        registerChanges()
    }

    fun selectLeftUri() {
        val index = uris.indexOf(selectedUri)
        if (index >= 0 && uris.size > 1) updateSelectedUri(uris.leftFrom(index))
    }

    fun selectRightUri() {
        val index = uris.indexOf(selectedUri)
        if (index >= 0 && uris.size > 1) updateSelectedUri(uris.rightFrom(index))
    }

    fun updateSelectedUri(uri: Uri) {
        if (uri == selectedUri) return
        _selectedUri.value = uri
        loadSelectedUri(uri)
    }

    private fun loadSelectedUri(uri: Uri, isNewSelection: Boolean = false) {
        previewJob = null
        _isPreviewLoading.value = false
        _isImageLoading.value = true
        sourceBitmap = null

        imageLoadingJob = componentScope.launch {
            runSuspendCatching {
                val bitmap = withContext(defaultDispatcher) {
                    imageGetter.getScreenshotFramingImage(uri, maxSide = 1600)
                } ?: error(getString(R.string.failed_to_open))
                if (isNewSelection && params.preset == null) {
                    val colors = withContext(defaultDispatcher) {
                        val primary = bitmap.extractPrimaryColor(default = 0xFF6D5DFB.toInt())
                            .copy(alpha = 1f)
                        listOf(
                            lerp(primary, Color.White, 0.6f),
                            lerp(primary, Color.Black, 0.18f)
                        )
                    }
                    _params.value = params.copy(backgroundColors = colors)
                }
                ensureActive()
                if (isNewSelection) resetHistory()
                sourceBitmap = bitmap
                updatePreview(debounce = false)
            }.onFailure {
                _previewBitmap.value = null
                AppToastHost.showFailureToast(it)
            }
            _isImageLoading.value = false
        }
    }

    internal fun applyStyle(style: ScreenshotFramingStyle) {
        val previous = params
        val updated = style.applyTo(previous)
        if (updated == previous) return

        finalizePendingHistoryTransaction()
        applyHistorySnapshot(updated)
        commitHistoryFrom(previous)
    }

    internal fun saveStyle(name: String) {
        val normalizedName = name.trim()
        if (normalizedName.isBlank()) return

        savedStyles = savedStyles.copy(
            styles = mapOf(normalizedName to params.toStyle()) +
                    savedStyles.styles.filterKeys { !it.equals(normalizedName, ignoreCase = true) }
        )
    }

    internal fun deleteStyle(name: String) {
        savedStyles = savedStyles.copy(
            styles = savedStyles.styles.filterKeys { !it.equals(name, ignoreCase = true) }
        )
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
        _isPreviewLoading.value = true
        previewJob = componentScope.launch {
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
            }.onFailure {
                _previewBitmap.value = null
                AppToastHost.showFailureToast(it)
            }
            _isPreviewLoading.value = false
        }
    }

    fun saveBitmaps(oneTimeSaveLocationUri: String?) {
        val renderParams = params
        savingJob = trackProgress {
            _isSaving.value = true
            _done.intValue = 0
            _left.intValue = uris.size
            try {
                val results = uris.mapIndexed { index, uri ->
                    val result = runSuspendCatching {
                        fileController.save(
                            saveTarget = saveTarget(
                                uri = uri,
                                params = renderParams,
                                sequenceNumber = (index + 1).takeIf { uris.size > 1 }
                            ),
                            keepOriginalMetadata = false,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        )
                    }.getOrElse { SaveResult.Error.Exception(it) }
                    _done.intValue = index + 1
                    updateProgress(done = done, total = left)
                    result
                }
                parseSaveResults(results.onSuccess(::registerSave))
            } finally {
                _isSaving.value = false
                _left.intValue = -1
            }
        }
    }

    fun shareBitmaps() {
        cacheImages { uris ->
            shareProvider.shareUris(uris.map(Uri::toString))
            AppToastHost.showConfetti()
        }
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        cacheImages(uris = listOfNotNull(selectedUri)) {
            it.firstOrNull()?.let(onComplete)
        }
    }

    fun cacheImages(
        uris: List<Uri> = this.uris,
        onComplete: suspend (List<Uri>) -> Unit
    ) {
        val renderParams = params
        savingJob = trackProgress {
            _isSaving.value = true
            _done.intValue = 0
            _left.intValue = uris.size
            try {
                var failure: Throwable? = null
                val cached = uris.mapIndexedNotNull { index, uri ->
                    val result = runSuspendCatching {
                        cacheImage(uri, renderParams)
                    }.onFailure {
                        if (failure == null) failure = it
                    }
                    _done.intValue = index + 1
                    updateProgress(done = done, total = left)
                    result.getOrNull()
                }
                failure?.let(AppToastHost::showFailureToast)
                onComplete(cached)
            } finally {
                _isSaving.value = false
                _left.intValue = -1
            }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
    }

    fun getFormatForFilenameSelection(): ImageFormat? =
        params.outputFormat.takeIf { uris.size == 1 }

    private suspend fun saveTarget(
        uri: Uri,
        params: ScreenshotFramingParams,
        sequenceNumber: Int?
    ): ImageSaveTarget {
        val bitmap = getFramedBitmap(uri, params)
        return try {
            ImageSaveTarget(
                imageInfo = bitmap.imageInfo(params.outputFormat),
                originalUri = uri.toString(),
                sequenceNumber = sequenceNumber,
                data = imageCompressor.compress(
                    image = bitmap,
                    imageFormat = params.outputFormat,
                    quality = Quality.Base(100)
                )
            )
        } finally {
            bitmap.recycle()
        }
    }

    private suspend fun cacheImage(uri: Uri, params: ScreenshotFramingParams): Uri? {
        val bitmap = getFramedBitmap(uri, params)
        return try {
            shareProvider.cacheImage(
                image = bitmap,
                imageInfo = bitmap.imageInfo(params.outputFormat).copy(originalUri = uri.toString())
            )?.toUri()
        } finally {
            bitmap.recycle()
        }
    }

    private suspend fun getFramedBitmap(
        uri: Uri,
        params: ScreenshotFramingParams
    ): Bitmap = withContext(defaultDispatcher) {
        val image = imageGetter.getScreenshotFramingImage(uri, maxSide = 4096)
            ?: error(getString(R.string.failed_to_open))
        ensureActive()
        renderScreenshotFramingBitmap(image, params)
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
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): ScreenshotFramingComponent
    }
}