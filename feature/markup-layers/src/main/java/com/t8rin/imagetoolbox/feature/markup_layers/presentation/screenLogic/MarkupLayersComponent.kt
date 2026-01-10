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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.toSoftware
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayersApplier
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.BackgroundBehavior
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.asDomain
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext


class MarkupLayersComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    dispatchersHolder: DispatchersHolder,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val markupLayersApplier: MarkupLayersApplier<Bitmap>
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUri?.let {
                setUri(
                    uri = it,
                    onFailure = {}
                )
            }
        }
    }

    private val _backgroundBehavior: MutableState<BackgroundBehavior> =
        mutableStateOf(BackgroundBehavior.None)
    val backgroundBehavior: BackgroundBehavior by _backgroundBehavior

    private val _layers: MutableState<List<UiMarkupLayer>> = mutableStateOf(emptyList())
    val layers: List<UiMarkupLayer> by _layers

    private val _lastLayers: MutableState<List<UiMarkupLayer>> = mutableStateOf(emptyList())
    val lastLayers: List<UiMarkupLayer> by _lastLayers

    private val _undoneLayers: MutableState<List<UiMarkupLayer>> = mutableStateOf(emptyList())
    val undoneLayers: List<UiMarkupLayer> by _undoneLayers

    val coerceToBounds get() = layers.all { it.state.coerceToBounds }

    fun undo() {
        if (layers.isEmpty() && lastLayers.isNotEmpty()) {
            _layers.value = lastLayers
            _lastLayers.value = listOf()
            return
        }
        if (layers.isEmpty()) return

        val lastLayer = layers.last()

        _layers.update { it - lastLayer }
        _undoneLayers.update { it + lastLayer }
        registerChanges()
    }

    fun redo() {
        if (undoneLayers.isEmpty()) return

        val lastLayer = undoneLayers.last()
        _layers.update { it + lastLayer }
        _undoneLayers.update { it - lastLayer }
        registerChanges()
    }

    fun clearLayers() {
        if (layers.isNotEmpty()) {
            _lastLayers.value = layers
            _layers.value = listOf()
            _undoneLayers.value = listOf()
            registerChanges()
        }
    }

    fun addLayer(layer: UiMarkupLayer) {
        _layers.update { it + layer }
    }

    fun deactivateAllLayers() {
        _layers.value.forEach { it.state.deactivate() }
    }

    fun activateLayer(layer: UiMarkupLayer) {
        deactivateAllLayers()
        layer.state.activate()
    }

    fun copyLayer(layer: UiMarkupLayer) {
        val copied = layer.copy(
            isActive = false
        )
        _layers.update {
            it.toMutableList().apply {
                add(indexOf(layer), copied)
            }
        }
        activateLayer(copied)
    }

    fun updateLayerAt(
        index: Int,
        layer: UiMarkupLayer
    ) {
        _layers.update {
            it.toMutableList().apply {
                set(index, layer)
            }
        }
    }

    fun removeLayer(layer: UiMarkupLayer) {
        _layers.update { it - layer }
    }

    fun reorderLayers(layers: List<UiMarkupLayer>) {
        _layers.update { layers }
    }

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _uri = mutableStateOf(Uri.EMPTY)

    private val _imageFormat = mutableStateOf(ImageFormat.Default)
    val imageFormat by _imageFormat

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _saveExif: MutableState<Boolean> = mutableStateOf(false)
    val saveExif: Boolean by _saveExif

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmap(
        oneTimeSaveLocationUri: String?,
        onComplete: (saveResult: SaveResult) -> Unit,
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            getDrawingBitmap().let { localBitmap ->
                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = ImageInfo(
                                imageFormat = imageFormat,
                                width = localBitmap.width,
                                height = localBitmap.height
                            ),
                            originalUri = _uri.value.toString(),
                            sequenceNumber = null,
                            data = imageCompressor.compressAndTransform(
                                image = localBitmap,
                                imageInfo = ImageInfo(
                                    imageFormat = imageFormat,
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                )
                            )
                        ),
                        keepOriginalMetadata = _saveExif.value,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    ).onSuccess(::registerSave)
                )
            }
            _isSaving.value = false
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.value = imageFormat
        registerChanges()
    }

    fun setSaveExif(bool: Boolean) {
        _saveExif.value = bool
        registerChanges()
    }

    private fun updateBitmap(bitmap: Bitmap?) {
        componentScope.launch {
            _isImageLoading.value = true
            _bitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            _isImageLoading.value = false
        }
    }

    fun setUri(
        uri: Uri,
        onFailure: (Throwable) -> Unit,
    ) {
        componentScope.launch {
            _layers.update { emptyList() }
            _isImageLoading.value = true

            _uri.value = uri
            if (backgroundBehavior !is BackgroundBehavior.Image) {
                _backgroundBehavior.update {
                    BackgroundBehavior.Image
                }
            }
            imageGetter.getImageAsync(
                uri = uri.toString(),
                originalSize = true,
                onGetImage = { data ->
                    updateBitmap(data.image)
                    _imageFormat.update { data.imageInfo.imageFormat }
                },
                onFailure = onFailure
            )
        }
    }

    private suspend fun Bitmap.overlay(overlay: Bitmap): Bitmap {
        val image = this
        val config = image.safeConfig.toSoftware()

        return createBitmap(
            width = image.width,
            height = image.height,
            config = config
        ).applyCanvas {
            drawBitmap(image)
            drawBitmap(
                imageScaler.scaleImage(
                    image = overlay.copy(config, false),
                    width = width,
                    height = height
                )
            )
        }
    }

    private val captureRequestChannel: Channel<Boolean> = Channel(Channel.BUFFERED)
    val captureRequestFlow: Flow<Boolean> = captureRequestChannel.receiveAsFlow()

    private val capturedImageChannel: Channel<Deferred<ImageBitmap>> = Channel(1)

    fun sendCapturedImage(image: Deferred<ImageBitmap>) {
        componentScope.launch {
            capturedImageChannel.send(image)
        }
    }

    private suspend fun getDrawingBitmap(): Bitmap = withContext(defaultDispatcher) {
        coroutineScope {
            deactivateAllLayers()
            delay(500)
        }

        val backgroundGetter = suspend {
            imageGetter.getImage(data = _uri.value)
                ?: (backgroundBehavior as? BackgroundBehavior.Color)?.run {
                    ImageBitmap(width, height).asAndroidBitmap()
                        .applyCanvas { drawColor(color) }
                }
        }

        val oldGetter = suspend {
            captureRequestChannel.send(true)

            capturedImageChannel.receive().await().asAndroidBitmap().let { layers ->
                layers.setHasAlpha(true)
                backgroundGetter()?.overlay(layers) ?: layers
            }
        }

        if (useOldLayers) return@withContext oldGetter()

        markupLayersApplier.applyToImage(
            image = backgroundGetter() ?: return@withContext oldGetter(),
            layers = layers.map { it.asDomain() }
        )
    }

    override fun resetState() {
        _bitmap.value = null
        _backgroundBehavior.update {
            BackgroundBehavior.None
        }
        _uri.value = Uri.EMPTY
        _layers.update { emptyList() }
        registerChangesCleared()
    }

    fun startDrawOnBackground(
        reqWidth: Int,
        reqHeight: Int,
        color: Color,
    ) {
        val width = reqWidth.takeIf { it > 0 } ?: 1
        val height = reqHeight.takeIf { it > 0 } ?: 1
        width / height.toFloat()
        _backgroundBehavior.update {
            BackgroundBehavior.Color(
                width = width,
                height = height,
                color = color.toArgb()
            )
        }
    }

    fun shareBitmap(onComplete: () -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            getDrawingBitmap().let {
                shareProvider.shareImage(
                    image = it,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = it.width,
                        height = it.height
                    ),
                    onComplete = onComplete
                )
            }
            _isSaving.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            getDrawingBitmap().let { image ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = image.width,
                        height = image.height
                    )
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat

    fun updateBackgroundColor(color: Color) {
        _backgroundBehavior.update {
            if (it is BackgroundBehavior.Color) {
                it.copy(color = color.toArgb())
            } else it
        }
    }

    fun toggleCoerceToBounds() {
        val result = !coerceToBounds

        _layers.update { list ->
            list.map {
                it.copy(
                    coerceToBounds = result
                )
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): MarkupLayersComponent
    }

}

@Suppress("MayBeConstant", "RedundantSuppression")
private val useOldLayers = true//!BuildConfig.DEBUG