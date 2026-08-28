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

package com.t8rin.imagetoolbox.feature.code_preview.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.code_preview.presentation.components.renderCodePreviewBitmap
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodeBackgroundPreset
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodeLanguage
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodePreviewParams
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodePreviewTheme
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.hossain.highlight.engine.HighlightEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class CodePreviewComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _params = mutableStateOf(CodePreviewParams.Default)
    val params: CodePreviewParams by _params

    private val highlightEngine = HighlightEngine(appContext)
    private val _previewBitmap = mutableStateOf<Bitmap?>(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _isSaving = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }
    private var previewJob: Job? by smartJob()
    private var highlightedSnapshot: HighlightedSnapshot? = null

    init {
        updatePreview()

        doOnDestroy {
            previewJob = null
            highlightEngine.destroy()
            previewBitmap?.recycle()
        }
    }

    fun updateCode(value: String) = updateParams { copy(code = value) }

    fun updateLanguage(value: CodeLanguage) = updateParams {
        copy(
            language = value,
            title = title.takeIf(String::isNotBlank)?.let {
                it.substringBeforeLast('.', it) + ".${value.fileExtension}"
            }.orEmpty()
        )
    }

    fun updateTheme(value: CodePreviewTheme) = updateParams { copy(theme = value) }

    fun updateBackgroundPreset(value: CodeBackgroundPreset) = updateParams {
        copy(
            backgroundPreset = value,
            backgroundStartColor = value.startColor,
            backgroundEndColor = value.endColor
        )
    }

    fun updateBackgroundStartColor(value: androidx.compose.ui.graphics.Color) = updateParams {
        copy(
            backgroundPreset = CodeBackgroundPreset.Custom,
            backgroundStartColor = value
        )
    }

    fun updateBackgroundEndColor(value: androidx.compose.ui.graphics.Color) = updateParams {
        copy(
            backgroundPreset = CodeBackgroundPreset.Custom,
            backgroundEndColor = value
        )
    }

    fun updateTitle(value: String) = updateParams { copy(title = value) }

    fun updateFontSize(value: Int) = updateParams { copy(fontSize = value.coerceIn(10, 30)) }

    fun updateOuterPadding(value: Int) = updateParams {
        copy(outerPadding = value.coerceIn(8, 80))
    }

    fun updateInnerPadding(value: Int) = updateParams {
        copy(innerPadding = value.coerceIn(8, 48))
    }

    fun updateCornerRadius(value: Int) = updateParams {
        copy(cornerRadius = value.coerceIn(0, 36))
    }

    fun updateCanvasCornerRadius(value: Int) = updateParams {
        copy(canvasCornerRadius = value.coerceIn(0, 64))
    }

    fun updateRotation(value: Float) = updateParams {
        copy(rotation = value.coerceIn(-15f, 15f))
    }

    fun toggleCanvasBackground(value: Boolean) = updateParams {
        copy(showCanvasBackground = value)
    }

    fun toggleWindowControls(value: Boolean) = updateParams { copy(showWindowControls = value) }

    fun toggleLineNumbers(value: Boolean) = updateParams { copy(showLineNumbers = value) }

    fun toggleTitle(value: Boolean) = updateParams { copy(showTitle = value) }

    fun toggleWrapLongLines(value: Boolean) = updateParams { copy(wrapLongLines = value) }

    fun updateOutputFormat(value: ImageFormat) = updateParams { copy(outputFormat = value) }

    fun saveBitmap(oneTimeSaveLocationUri: String?) {
        val renderParams = params
        val outputFormat = renderParams.outputFormat
        savingJob = trackProgress {
            _isSaving.update { true }
            try {
                withRenderedBitmap(renderParams) { bitmap ->
                    val imageInfo = bitmap.imageInfo(outputFormat)
                    parseSaveResult(
                        fileController.save(
                            saveTarget = ImageSaveTarget(
                                imageInfo = imageInfo,
                                originalUri = "",
                                sequenceNumber = null,
                                data = imageCompressor.compress(
                                    image = bitmap,
                                    imageFormat = outputFormat,
                                    quality = Quality.Base(100)
                                )
                            ),
                            keepOriginalMetadata = false,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        )
                    )
                }
            } finally {
                _isSaving.update { false }
            }
        }
    }

    fun shareBitmap() {
        val renderParams = params
        val outputFormat = renderParams.outputFormat
        savingJob = trackProgress {
            _isSaving.update { true }
            try {
                withRenderedBitmap(renderParams) { bitmap ->
                    shareProvider.shareImage(
                        image = bitmap,
                        imageInfo = bitmap.imageInfo(outputFormat),
                        onComplete = AppToastHost::showConfetti
                    )
                }
            } finally {
                _isSaving.update { false }
            }
        }
    }

    fun cacheBitmap(
        onComplete: (Uri) -> Unit
    ) {
        val renderParams = params
        val outputFormat = renderParams.outputFormat
        savingJob = trackProgress {
            _isSaving.update { true }
            try {
                withRenderedBitmap(renderParams) { bitmap ->
                    shareProvider.cacheImage(
                        image = bitmap,
                        imageInfo = bitmap.imageInfo(outputFormat)
                    )?.let { onComplete(it.toUri()) }
                }
            } finally {
                _isSaving.update { false }
            }
        }
    }

    fun cancelSaving() {
        savingJob = null
        _isSaving.update { false }
    }

    private fun updateParams(transform: CodePreviewParams.() -> CodePreviewParams) {
        val previousParams = params
        _params.update(transform)
        if (previousParams.outputFormat == params.outputFormat) {
            updatePreview()
        }
        registerChanges()
    }

    private fun updatePreview() {
        val renderParams = params
        previewJob = componentScope.launch {
            delay(100)
            val highlightedCode = getHighlightedCode(renderParams)
            var bitmap: Bitmap? = null
            try {
                bitmap = withContext(defaultDispatcher) {
                    renderCodePreviewBitmap(
                        params = renderParams,
                        highlightedCode = highlightedCode,
                        maxBitmapPixels = 2_000_000f
                    )
                }
                ensureActive()
                if (params == renderParams) {
                    _previewBitmap.value = bitmap
                    bitmap = null
                }
            } finally {
                bitmap?.recycle()
            }
        }
    }

    private suspend fun renderBitmap(renderParams: CodePreviewParams): Bitmap {
        val highlightedCode = getHighlightedCode(renderParams)
        return withContext(defaultDispatcher) {
            renderCodePreviewBitmap(
                params = renderParams,
                highlightedCode = highlightedCode
            )
        }
    }

    private suspend fun <T> withRenderedBitmap(
        renderParams: CodePreviewParams,
        action: suspend (Bitmap) -> T
    ): T {
        val bitmap = renderBitmap(renderParams)
        return try {
            action(bitmap)
        } finally {
            bitmap.recycle()
        }
    }

    private suspend fun getHighlightedCode(renderParams: CodePreviewParams): AnnotatedString {
        val key = renderParams.highlightKey
        highlightedSnapshot?.takeIf { it.key == key }?.let { return it.code }

        val highlightedCode = highlightEngine.highlight(
            code = renderParams.code,
            language = renderParams.language.highlightKey,
            theme = renderParams.theme.highlightTheme()
        ).getOrNull()?.annotated ?: return AnnotatedString(renderParams.code)

        highlightedSnapshot = HighlightedSnapshot(
            key = key,
            code = highlightedCode
        )
        return highlightedCode
    }

    private fun Bitmap.imageInfo(imageFormat: ImageFormat) = ImageInfo(
        width = width,
        height = height,
        imageFormat = imageFormat
    )

    private val CodePreviewParams.highlightKey: HighlightKey
        get() = HighlightKey(code, language, theme)

    private data class HighlightedSnapshot(
        val key: HighlightKey,
        val code: AnnotatedString
    )

    private data class HighlightKey(
        val code: String,
        val language: CodeLanguage,
        val theme: CodePreviewTheme
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): CodePreviewComponent
    }
}
