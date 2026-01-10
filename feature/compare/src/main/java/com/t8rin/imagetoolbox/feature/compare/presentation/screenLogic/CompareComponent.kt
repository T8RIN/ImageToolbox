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

package com.t8rin.imagetoolbox.feature.compare.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.applyCanvas
import androidx.core.net.toUri
import coil3.transform.Transformation
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.data.utils.asDomain
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.transformation.GenericTransformation
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.createScaledBitmap
import com.t8rin.imagetoolbox.core.ui.utils.helper.toCoil
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.compare.presentation.components.CompareType
import com.t8rin.imagetoolbox.feature.compare.presentation.components.PixelByPixelCompareState
import com.t8rin.imagetoolbox.feature.compare.presentation.components.model.CompareData
import com.t8rin.imagetoolbox.feature.compare.presentation.components.model.CompareEntry
import com.t8rin.imagetoolbox.feature.compare.presentation.components.model.ifNotEmpty
import com.t8rin.opencv_tools.image_comparison.ImageDiffTool
import com.t8rin.opencv_tools.image_comparison.model.ComparisonType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt

class CompareComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialComparableUris: Pair<Uri, Uri>?,
    @Assisted val onGoBack: () -> Unit,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val fileController: FileController,
    private val shareProvider: ImageShareProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialComparableUris?.let {
                updateUris(
                    uris = it,
                    onFailure = {},
                )
            }
        }
    }

    private val _compareProgress: MutableState<Float> = mutableFloatStateOf(50f)
    val compareProgress by _compareProgress

    fun setCompareProgress(progress: Float) {
        _compareProgress.update { progress }
    }

    private val _bitmapData: MutableState<CompareData?> = mutableStateOf(null)
    val bitmapData by _bitmapData

    private val _rotation: MutableState<Float> = mutableFloatStateOf(0f)
    val rotation by _rotation

    private val _compareType: MutableState<CompareType> = mutableStateOf(CompareType.Slide)
    val compareType by _compareType

    private val _pixelByPixelCompareState: MutableState<PixelByPixelCompareState> = mutableStateOf(
        PixelByPixelCompareState.Default
    )
    val pixelByPixelCompareState: PixelByPixelCompareState by _pixelByPixelCompareState

    fun updatePixelByPixelCompareState(state: PixelByPixelCompareState) {
        _pixelByPixelCompareState.update { state }
    }

    fun rotate() {
        val old = _rotation.value
        _rotation.value = _rotation.value.let {
            if (it == 90f) 0f
            else 90f
        }
        componentScope.launch {
            bitmapData?.ifNotEmpty { first, second ->
                _isImageLoading.value = true
                _bitmapData.value = with(imageTransformer) {
                    CompareData(
                        first = first.copy(
                            image = rotate(
                                image = rotate(
                                    image = first.image,
                                    degrees = 180f - old
                                ),
                                degrees = rotation
                            )
                        ),
                        second = second.copy(
                            image = rotate(
                                image = rotate(
                                    image = second.image,
                                    degrees = 180f - old
                                ),
                                degrees = rotation
                            )
                        )
                    )
                }
                _isImageLoading.value = false
            }
        }
    }

    fun swap() {
        componentScope.launch {
            _isImageLoading.value = true
            _bitmapData.value = bitmapData?.swap()
            _isImageLoading.value = false
        }
    }

    fun updateUris(
        uris: Pair<Uri, Uri>,
        onFailure: () -> Unit,
    ) {
        componentScope.launch {
            val data = getBitmapByUri(uris.first) to getBitmapByUri(uris.second)
            if (data.first == null || data.second == null) onFailure()
            else {
                _bitmapData.value = CompareData(
                    first = CompareEntry(
                        uri = uris.first,
                        image = data.first!!
                    ),
                    second = CompareEntry(
                        uri = uris.second,
                        image = data.second!!
                    )
                )
                setCompareProgress(
                    if (compareType == CompareType.PixelByPixel) 4f
                    else 50f
                )
            }
        }
    }

    private suspend fun getBitmapByUri(
        uri: Uri
    ): Bitmap? = imageGetter.getImage(
        data = uri.toString(),
        size = 4000
    )

    private var savingJob: Job? by smartJob {
        _isImageLoading.update { false }
    }

    fun shareBitmap(
        imageFormat: ImageFormat,
        onComplete: () -> Unit
    ) {
        savingJob = trackProgress {
            _isImageLoading.value = true
            getResultImage()?.let {
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
        imageFormat: ImageFormat,
        oneTimeSaveLocationUri: String?,
        onComplete: (saveResult: SaveResult) -> Unit
    ) {
        savingJob = trackProgress {
            _isImageLoading.value = true
            getResultImage()?.let { localBitmap ->
                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = ImageInfo(
                                imageFormat = imageFormat,
                                width = localBitmap.width,
                                height = localBitmap.height
                            ),
                            originalUri = "",
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
                        keepOriginalMetadata = false,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    )
                )
                _isImageLoading.value = false
            }
        }
    }

    private fun Bitmap.overlay(
        overlay: Bitmap,
        percent: Float
    ): Bitmap = overlay.copy(overlay.safeConfig, true)
        .apply { setHasAlpha(true) }
        .applyCanvas {
            runCatching {
                val image = createScaledBitmap(
                    width = width,
                    height = height
                )

                drawBitmap(
                    Bitmap.createBitmap(
                        image, 0, 0,
                        (image.width * percent / 100).roundToInt(),
                        image.height
                    )
                )
            }
        }

    private fun getOverlappedImage(): Bitmap? {
        return bitmapData?.ifNotEmpty { before, after ->
            before.image.overlay(
                overlay = after.image,
                percent = compareProgress
            )
        }
    }

    private suspend fun getResultImage(): Bitmap? = coroutineScope {
        when (compareType) {
            CompareType.PixelByPixel -> imageTransformer.transform(
                image = bitmapData?.first?.image ?: return@coroutineScope null,
                transformations = listOf(createPixelByPixelTransformation().asDomain())
            )

            CompareType.Slide -> getOverlappedImage()
            else -> null
        }
    }

    fun getImagePreview(): Bitmap? = when (compareType) {
        CompareType.PixelByPixel -> bitmapData?.first?.image
        CompareType.Slide -> getOverlappedImage()
        else -> null
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isImageLoading.value = false
    }

    fun setCompareType(value: CompareType) {
        _compareType.update { value }
        if (value == CompareType.PixelByPixel) {
            setCompareProgress(4f)
        }
    }

    fun cacheCurrentImage(
        imageFormat: ImageFormat,
        onComplete: (Uri) -> Unit
    ) {
        savingJob = trackProgress {
            _isImageLoading.value = true
            getResultImage()?.let {
                shareProvider.cacheImage(
                    image = it,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = it.width,
                        height = it.height
                    )
                )
            }?.let { uri ->
                onComplete(uri.toUri())
            }
            _isImageLoading.value = false
        }
    }

    fun createPixelByPixelTransformation(): Transformation =
        GenericTransformation<Bitmap> { first ->
            ImageDiffTool.highlightDifferences(
                input = first,
                other = bitmapData?.second?.image
                    ?: return@GenericTransformation first,
                comparisonType = ComparisonType.valueOf(pixelByPixelCompareState.comparisonType.name),
                highlightColor = pixelByPixelCompareState.highlightColor.toArgb(),
                threshold = compareProgress
            )
        }.toCoil()

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialComparableUris: Pair<Uri, Uri>?,
            onGoBack: () -> Unit,
        ): CompareComponent
    }

}