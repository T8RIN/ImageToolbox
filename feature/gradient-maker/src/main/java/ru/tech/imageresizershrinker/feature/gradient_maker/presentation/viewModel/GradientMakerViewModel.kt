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

package ru.tech.imageresizershrinker.feature.gradient_maker.presentation.viewModel

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientState
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientType
import javax.inject.Inject

@HiltViewModel
class GradientMakerViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>
) : ViewModel() {

    private val gradientState = GradientState()

    val brush: ShaderBrush? get() = gradientState.brush
    val gradientType: GradientType get() = gradientState.gradientType
    val colorStops: List<Pair<Float, Color>> get() = gradientState.colorStops
    val tileMode: TileMode get() = gradientState.tileMode
    val angle: Float get() = gradientState.linearGradientAngle
    val centerFriction: Offset get() = gradientState.centerFriction
    val radiusFriction: Float get() = gradientState.radiusFriction

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _imageFormat = mutableStateOf(ImageFormat.Default())
    val imageFormat by _imageFormat

    private val _gradientSize: MutableState<IntegerSize> = mutableStateOf(IntegerSize(1000, 1000))
    val gradientSize by _gradientSize

    suspend fun createGradientBitmap(
        integerSize: IntegerSize = gradientSize
    ): Bitmap? = withContext(Dispatchers.IO) {
        val size = Size(
            integerSize.width.coerceAtLeast(1).toFloat(),
            integerSize.height.coerceAtLeast(1).toFloat(),
        )
        gradientState.getBrush(size)?.let { brush ->
            Bitmap.createBitmap(
                size.width.toInt(),
                size.height.toInt(),
                Bitmap.Config.ARGB_8888
            ).apply {
                Canvas(asImageBitmap()).apply {
                    drawRect(
                        paint = Paint().apply {
                            shader = brush.createShader(size)
                        },
                        rect = Rect(offset = Offset.Zero, size = size)
                    )
                }
            }
        }
    }

    private var savingJob: Job? = null

    fun saveBitmap(
        onComplete: (saveResult: SaveResult) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            createGradientBitmap()?.let { localBitmap ->
                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget<ExifInterface>(
                            imageInfo = ImageInfo(
                                imageFormat = imageFormat,
                                width = localBitmap.width,
                                height = localBitmap.height
                            ),
                            originalUri = "Gradient",
                            sequenceNumber = null,
                            data = imageCompressor.compressAndTransform(
                                image = localBitmap,
                                imageInfo = ImageInfo(
                                    imageFormat = imageFormat,
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                )
                            )
                        ), keepMetadata = false
                    )
                )
            }
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    fun shareBitmap(onComplete: () -> Unit) {
        savingJob?.cancel()
        _isSaving.value = true
        savingJob = viewModelScope.launch {
            createGradientBitmap()?.let {
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

    fun updateHeight(value: Int) {
        _gradientSize.update {
            it.copy(height = value)
        }
    }

    fun updateWidth(value: Int) {
        _gradientSize.update {
            it.copy(width = value)
        }
    }

    fun setGradientType(gradientType: GradientType) {
        gradientState.gradientType = gradientType
    }

    fun setPreviewSize(size: Size) {
        gradientState.size = size
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
    }

    fun updateLinearAngle(angle: Float) {
        gradientState.linearGradientAngle = angle
    }

    fun setRadialProperties(center: Offset, radius: Float) {
        gradientState.centerFriction = center
        gradientState.radiusFriction = radius
    }

    fun setTileMode(tileMode: TileMode) {
        gradientState.tileMode = tileMode
    }

    fun addColorStop(pair: Pair<Float, Color>) {
        gradientState.colorStops.add(pair)
    }

    fun updateColorStop(index: Int, pair: Pair<Float, Color>) {
        gradientState.colorStops[index] = pair.copy()
    }

    fun removeColorStop(index: Int) {
        if (gradientState.colorStops.size > 2) {
            gradientState.colorStops.removeAt(index)
        }
    }

}