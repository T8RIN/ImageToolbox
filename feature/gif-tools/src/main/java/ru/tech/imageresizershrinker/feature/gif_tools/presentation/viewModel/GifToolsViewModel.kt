/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.gif_tools.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.gif_tools.domain.GifConverter
import ru.tech.imageresizershrinker.feature.gif_tools.domain.GifFrames
import ru.tech.imageresizershrinker.feature.gif_tools.domain.GifParams
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GifToolsViewModel @Inject constructor(
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val fileController: FileController,
    private val gifConverter: GifConverter,
    private val shareProvider: ShareProvider<Bitmap>
) : ViewModel() {

    private val _type: MutableState<Screen.GifTools.Type?> = mutableStateOf(null)
    val type by _type

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading by _isLoading

    private val _isLoadingGifImages: MutableState<Boolean> = mutableStateOf(false)
    val isLoadingGifImages by _isLoadingGifImages

    private val _params: MutableState<GifParams> = mutableStateOf(GifParams.Default)
    val params by _params

    private val _convertedImageUris: MutableState<List<String>> = mutableStateOf(emptyList())
    val convertedImageUris by _convertedImageUris

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Default())
    val imageFormat by _imageFormat

    private val _gifFrames: MutableState<GifFrames> = mutableStateOf(GifFrames.All)
    val gifFrames by _gifFrames

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private var gifData: ByteArray? = null

    fun setType(type: Screen.GifTools.Type) {
        when (type) {
            is Screen.GifTools.Type.GifToImage -> {
                type.gifUri?.let { setGifUri(it) } ?: _type.update { null }
            }

            is Screen.GifTools.Type.ImageToGif -> {
                _type.update { type }
            }
        }
    }

    fun setImageUris(uris: List<Uri>) {
        clearAll()
        _type.update {
            Screen.GifTools.Type.ImageToGif(uris)
        }
    }

    private var collectionJob: Job? = null
    fun setGifUri(uri: Uri) {
        clearAll()
        _type.update {
            Screen.GifTools.Type.GifToImage(uri)
        }
        updateGifFrames(GifFrames.All)
        collectionJob?.cancel()
        collectionJob = viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            _isLoadingGifImages.update { true }
            gifConverter.extractFramesFromGif(
                gifUri = uri.toString(),
                imageFormat = imageFormat,
                gifFrames = GifFrames.All,
                quality = params.quality
            ).onCompletion {
                _isLoading.update { false }
                _isLoadingGifImages.update { false }
            }.collect { nextUri ->
                if (isLoading) {
                    _isLoading.update { false }
                }
                _convertedImageUris.update { it + nextUri }
            }
        }
    }

    fun clearAll() {
        collectionJob?.cancel()
        collectionJob = null
        _type.update { null }
        _convertedImageUris.update { emptyList() }
        gifData = null
        savingJob?.cancel()
        savingJob = null
        _params.update { GifParams.Default }
    }

    fun updateGifFrames(gifFrames: GifFrames) {
        _gifFrames.update { gifFrames }
    }

    fun clearConvertedImagesSelection() = updateGifFrames(GifFrames.ManualSelection(emptyList()))

    fun selectAllConvertedImages() = updateGifFrames(GifFrames.All)

    private var savingJob: Job? = null

    fun saveGifTo(
        outputStream: OutputStream?,
        onComplete: (Throwable?) -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isSaving.value = true
                kotlin.runCatching {
                    outputStream?.use {
                        it.write(gifData)
                    }
                }.exceptionOrNull().let(onComplete)
                _isSaving.value = false
                gifData = null
            }
        }
    }

    fun saveBitmaps(
        onGifSaveResult: (String) -> Unit,
        onResult: (Int, String) -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            _left.value = 1
            _done.value = 0
            when (val type = _type.value) {
                is Screen.GifTools.Type.GifToImage -> {
                    var failed = 0
                    type.gifUri?.toString()?.also { gifUri ->
                        gifConverter.extractFramesFromGif(
                            gifUri = gifUri,
                            imageFormat = imageFormat,
                            gifFrames = gifFrames,
                            quality = params.quality,
                            onGetFramesCount = {
                                if (it == 0) {
                                    _isSaving.value = false
                                    savingJob?.cancel()
                                    return@extractFramesFromGif onResult(-1, "")
                                }
                                _left.value = gifFrames.getFramePositions(it).size
                            }
                        ).onCompletion {
                            onResult(failed, fileController.savingPath)
                        }.collect { uri ->
                            imageGetter.getImage(
                                data = uri,
                                originalSize = true
                            )?.let { localBitmap ->
                                val imageInfo = ImageInfo(
                                    imageFormat = imageFormat,
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                )
                                val result = fileController.save(
                                    saveTarget = ImageSaveTarget<ExifInterface>(
                                        imageInfo = imageInfo,
                                        originalUri = uri,
                                        sequenceNumber = _done.value + 1,
                                        data = imageCompressor.compress(
                                            image = localBitmap,
                                            imageFormat = imageFormat,
                                            quality = params.quality
                                        )
                                    ),
                                    keepMetadata = false
                                )
                                if (result is SaveResult.Error.MissingPermissions) {
                                    _isSaving.value = false
                                    savingJob?.cancel()
                                    return@let onResult(-1, "")
                                }
                            } ?: {
                                failed += 1
                            }
                            _done.value++
                        }
                    }
                }

                is Screen.GifTools.Type.ImageToGif -> {
                    _left.value = type.imageUris?.size ?: -1
                    gifData = type.imageUris?.map { it.toString() }?.let { list ->
                        gifConverter.createGifFromImageUris(
                            imageUris = list,
                            params = params,
                            onProgress = {
                                _done.update { it + 1 }
                            }
                        ).also {
                            val timeStamp = SimpleDateFormat(
                                "yyyy-MM-dd_HH-mm-ss",
                                Locale.getDefault()
                            ).format(Date()) + "_${
                                Random(Random.nextInt()).hashCode().toString().take(4)
                            }"
                            val gifName = "GIF_$timeStamp"
                            onGifSaveResult(gifName)
                        }
                    }
                }

                null -> Unit
            }
            _isSaving.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun reorderImageUris(uris: List<Uri>?) {
        if (type is Screen.GifTools.Type.ImageToGif) {
            _type.update {
                Screen.GifTools.Type.ImageToGif(uris)
            }
        }
    }

    fun addImageToUris(uris: List<Uri>) {
        val type = _type.value
        if (type is Screen.GifTools.Type.ImageToGif) {
            _type.update {
                val newUris = type.imageUris?.plus(uris)?.toSet()?.toList()

                Screen.GifTools.Type.ImageToGif(newUris)
            }
        }
    }

    fun removeImageAt(index: Int) {
        val type = _type.value
        if (type is Screen.GifTools.Type.ImageToGif) {
            _type.update {
                val newUris = type.imageUris?.toMutableList()?.apply {
                    removeAt(index)
                }

                Screen.GifTools.Type.ImageToGif(newUris)
            }
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
    }

    fun setQuality(quality: Quality) {
        _params.update { it.copy(quality = quality) }
    }

    fun updateParams(params: GifParams) {
        _params.update { params }
    }

    fun setUseOriginalSize(value: Boolean) {
        _params.update {
            it.copy(size = if (value) null else IntegerSize(1000, 1000))
        }
    }

    fun performSharing(onComplete: () -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            _left.value = 1
            _done.value = 0
            when (val type = _type.value) {
                is Screen.GifTools.Type.GifToImage -> {
                    _left.value = -1
                    val positions =
                        gifFrames.getFramePositions(convertedImageUris.size).map { it - 1 }
                    val uris = convertedImageUris.filterIndexed { index, _ ->
                        index in positions
                    }
                    shareProvider.shareImageUris(uris)
                    onComplete()
                }

                is Screen.GifTools.Type.ImageToGif -> {
                    _left.value = type.imageUris?.size ?: -1
                    type.imageUris?.map { it.toString() }?.let { list ->
                        gifConverter.createGifFromImageUris(
                            imageUris = list,
                            params = params,
                            onProgress = {
                                _done.update { it + 1 }
                            }
                        ).also { byteArray ->
                            val timeStamp = SimpleDateFormat(
                                "yyyy-MM-dd_HH-mm-ss",
                                Locale.getDefault()
                            ).format(Date()) + "_${
                                Random(Random.nextInt()).hashCode().toString().take(4)
                            }"
                            val gifName = "GIF_$timeStamp"
                            shareProvider.shareByteArray(
                                byteArray = byteArray,
                                filename = "$gifName.gif",
                                onComplete = onComplete
                            )
                        }
                    }
                }

                null -> {
                    Unit
                }
            }
            _isSaving.value = false
        }
    }

}