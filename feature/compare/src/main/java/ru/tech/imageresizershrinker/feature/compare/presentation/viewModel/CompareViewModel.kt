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

package ru.tech.imageresizershrinker.feature.compare.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageManager
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageData
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.ResizeType
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.createScaledBitmap
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareType
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val imageManager: ImageManager<Bitmap, ExifInterface>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val fileController: FileController,
    private val shareProvider: ShareProvider<Bitmap>
) : ViewModel() {

    private val _bitmapData: MutableState<Pair<Bitmap?, Bitmap?>?> = mutableStateOf(null)
    val bitmapData by _bitmapData

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

    private val _rotation: MutableState<Float> = mutableFloatStateOf(0f)
    val rotation by _rotation

    private val _compareType: MutableState<CompareType> = mutableStateOf(CompareType.Slide)
    val compareType by _compareType

    fun updateBitmapData(newBeforeBitmap: Bitmap?, newAfterBitmap: Bitmap?) {
        viewModelScope.launch {
            _isImageLoading.value = true
            var bmp1: Bitmap?
            var bmp2: Bitmap?
            withContext(Dispatchers.IO) {
                bmp1 = imageScaler.scaleUntilCanShow(newBeforeBitmap)
                bmp2 = imageScaler.scaleUntilCanShow(newAfterBitmap)
            }
            _rotation.value = 0f
            _bitmapData.value = (bmp1 to bmp2)
                .let { (b, a) ->
                    val (bW, bH) = b?.run { width to height } ?: (0 to 0)
                    val (aW, aH) = a?.run { width to height } ?: (0 to 0)

                    if (bW * bH > aH * aW) {
                        b to a?.let {
                            imageManager.resize(
                                image = it,
                                width = bW,
                                height = bH,
                                resizeType = ResizeType.Flexible,
                                imageScaleMode = ImageScaleMode.NotPresent
                            )
                        }
                    } else if (bW * bH < aH * aW) {
                        b?.let {
                            imageManager.resize(
                                image = it,
                                width = aW,
                                height = aH,
                                resizeType = ResizeType.Flexible,
                                imageScaleMode = ImageScaleMode.NotPresent
                            )
                        } to a
                    } else {
                        b to a
                    }
                }
            _isImageLoading.value = false
        }
    }

    fun rotate() {
        val old = _rotation.value
        _rotation.value = _rotation.value.let {
            if (it == 90f) 0f
            else 90f
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _bitmapData.value?.let { (f, s) ->
                    if (f != null && s != null) {
                        _isImageLoading.value = true
                        _bitmapData.value = with(imageManager) {
                            rotate(
                                image = rotate(
                                    image = f,
                                    degrees = 180f - old
                                ),
                                degrees = rotation
                            ) to rotate(
                                image = rotate(
                                    image = s,
                                    degrees = 180f - old
                                ),
                                degrees = rotation
                            )
                        }
                        _isImageLoading.value = false
                    }
                }
            }
        }
    }

    fun swap() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isImageLoading.value = true
                _bitmapData.value = _bitmapData.value?.run { second to first }
                _isImageLoading.value = false
            }
        }
    }

    fun updateBitmapDataAsync(
        loader: suspend () -> Pair<Bitmap?, Bitmap?>,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val data = loader()
                if (data.first == null || data.second == null) onError()
                else {
                    _bitmapData.value = data
                    onSuccess()
                }
            }
        }
    }

    suspend fun getBitmapByUri(
        uri: Uri,
        originalSize: Boolean
    ): Bitmap? = imageGetter.getImage(uri.toString(), originalSize)?.image

    private var savingJob: Job? = null

    fun shareBitmap(
        percent: Float,
        imageFormat: ImageFormat,
        onComplete: () -> Unit
    ) {
        _isImageLoading.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isImageLoading.value = true
            getOverlayedImage(percent)?.let {
                shareProvider.shareImage(
                    image = it,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = it.width,
                        height = it.height
                    ),
                    onComplete = onComplete
                )
            } ?: onComplete()
            _isImageLoading.value = false
        }
    }

    fun saveBitmap(
        percent: Float,
        imageFormat: ImageFormat,
        onComplete: (saveResult: SaveResult) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isImageLoading.value = true
            getOverlayedImage(percent)?.let { localBitmap ->
                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget<ExifInterface>(
                            imageInfo = ImageInfo(
                                imageFormat = imageFormat,
                                width = localBitmap.width,
                                height = localBitmap.height
                            ),
                            originalUri = "",
                            sequenceNumber = null,
                            data = imageManager.compress(
                                ImageData(
                                    image = localBitmap,
                                    imageInfo = ImageInfo(
                                        imageFormat = imageFormat,
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    )
                                )
                            )
                        ), keepMetadata = false
                    )
                )
            }
            _isImageLoading.value = false
        }
    }.also {
        _isImageLoading.value = false
        savingJob?.cancel()
        savingJob = it
    }

    private fun Bitmap.overlay(overlay: Bitmap, percent: Float): Bitmap {
        val finalBitmap = overlay.copy(overlay.config, true).apply { setHasAlpha(true) }
        val canvas = android.graphics.Canvas(finalBitmap)
        val image = createScaledBitmap(canvas.width, canvas.height)
        kotlin.runCatching {
            canvas.drawBitmap(
                Bitmap.createBitmap(
                    image,
                    0,
                    0,
                    (image.width * percent / 100).roundToInt(),
                    image.height
                ), 0f, 0f, null
            )
        }
        return finalBitmap
    }

    fun getOverlayedImage(percent: Float): Bitmap? {
        return _bitmapData.value?.let { (b, a) ->
            a?.let { b?.overlay(it, percent) }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isImageLoading.value = false
    }

    fun setCompareType(value: CompareType) {
        _compareType.update { value }
    }

}