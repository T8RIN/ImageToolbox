/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t8rin.imagetoolbox.feature.photomosaic.presentation.screenLogic

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
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.photomosaic.domain.PhotomosaicMaker
import com.t8rin.imagetoolbox.feature.photomosaic.domain.PhotomosaicParams
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PhotomosaicComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val photomosaicMaker: PhotomosaicMaker<Bitmap>,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            _imageFormat.value = settingsManager.settingsState.value.defaultImageFormat
                ?: ImageFormat.Png.Lossless
            initialUri?.let(::setTargetUri)
        }
    }

    private val _targetUri: MutableState<Uri?> = mutableStateOf(null)
    val targetUri by _targetUri

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

    fun setTargetUri(uri: Uri) {
        _targetUri.update { uri }
        _tileUris.update { tiles -> tiles.filterNot { it == uri } }
        registerChanges()
        calculatePreview()
    }

    fun setTileUris(uris: List<Uri>) {
        _tileUris.update {
            uris.filterNot { it == targetUri }.distinct().take(params.maxTiles)
        }
        registerChanges()
        calculatePreview()
    }

    fun addTileUris(uris: List<Uri>) {
        setTileUris((tileUris + uris).distinct())
    }

    fun removeTileAt(index: Int) {
        setTileUris(tileUris.toMutableList().apply { removeAt(index) })
    }

    fun updateParams(value: PhotomosaicParams) {
        _params.update { value.normalized() }
        if (tileUris.size > params.maxTiles) {
            _tileUris.update { it.take(params.maxTiles) }
        }
        registerChanges()
        calculatePreview()
    }

    fun setImageFormat(value: ImageFormat) {
        _imageFormat.update { value }
        registerChanges()
    }

    fun setQuality(value: Quality) {
        _quality.update { value }
        registerChanges()
    }

    private fun calculatePreview() {
        val target = targetUri
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
        savingJob = createResult { bitmap ->
            val info = bitmap.imageInfo()
            parseSaveResult(
                fileController.save(
                    saveTarget = ImageSaveTarget(
                        imageInfo = info,
                        originalUri = targetUri.toString(),
                        sequenceNumber = null,
                        metadata = null,
                        data = imageCompressor.compressAndTransform(bitmap, info)
                    ),
                    keepOriginalMetadata = false,
                    oneTimeSaveLocationUri = oneTimeSaveLocationUri
                ).onSuccess(::registerSave)
            )
        }
    }

    fun share() {
        savingJob = createResult { bitmap ->
            shareProvider.shareImage(
                image = bitmap,
                imageInfo = bitmap.imageInfo(),
                onComplete = AppToastHost::showConfetti
            )
        }
    }

    fun cache(onComplete: (Uri) -> Unit) {
        savingJob = createResult { bitmap ->
            shareProvider.cacheImage(
                image = bitmap,
                imageInfo = bitmap.imageInfo()
            )?.let { onComplete(it.toUri()) }
        }
    }

    private fun createResult(onResult: suspend (Bitmap) -> Unit): Job = trackProgress {
        val target = targetUri ?: return@trackProgress
        if (tileUris.isEmpty()) return@trackProgress

        _isSaving.update { true }
        _done.update { 0 }
        photomosaicMaker.create(
            targetUri = target.toString(),
            tileUris = tileUris.map(Uri::toString),
            params = params,
            preview = false,
            onProgress = { done, total ->
                _done.update { done }
                _total.update { total }
            }
        )?.let { onResult(it) } ?: parseSaveResult(
            SaveResult.Error.Exception(IllegalStateException("Unable to create photomosaic"))
        )
        _isSaving.update { false }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat

    private fun Bitmap.imageInfo() = ImageInfo(
        width = width,
        height = height,
        imageFormat = imageFormat,
        quality = quality
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): PhotomosaicComponent
    }
}
