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

package com.t8rin.imagetoolbox.feature.image_stitch.data

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.data.image.utils.drawBackground
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.data.image.utils.toPaint
import com.t8rin.imagetoolbox.core.data.utils.getSuitableConfig
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImagePreviewCreator
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleDirection
import com.t8rin.imagetoolbox.core.domain.image.model.ImageWithSize
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.withSize
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.FilterProvider
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValues
import com.t8rin.imagetoolbox.core.filters.domain.model.createFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.FadeSide
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.feature.image_stitch.domain.CombiningParams
import com.t8rin.imagetoolbox.feature.image_stitch.domain.ImageCombiner
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchAlignment
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchFadeSide
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchMode
import com.t8rin.trickle.Trickle
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.roundToLong

internal class AndroidImageCombiner @Inject constructor(
    private val imageScaler: ImageScaler<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>,
    private val cvStitchHelper: CvStitchHelper,
    settingsProvider: SettingsProvider,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, ImageCombiner<Bitmap> {

    private val _settingsState = settingsProvider.settingsState
    private val settingsState get() = _settingsState.value

    override suspend fun combineImages(
        imageUris: List<String>,
        combiningParams: CombiningParams,
        onProgress: (Int) -> Unit
    ): Pair<Bitmap, ImageInfo> = withContext(defaultDispatcher) {
        if (combiningParams.stitchMode.isAuto()) {
            return@withContext cvStitchHelper.cvCombine(
                imageUris = imageUris,
                combiningParams = combiningParams
            )
        }

        combine(imageUris, combiningParams, onProgress)
    }

    private suspend fun combine(
        imageSources: List<Any>,
        combiningParams: CombiningParams,
        onProgress: (Int) -> Unit
    ): Pair<Bitmap, ImageInfo> = withContext(defaultDispatcher) {
        suspend fun getImageData(
            imagesUris: List<Any>,
            isHorizontal: Boolean,
            includeBackground: Boolean = true
        ): Pair<Bitmap, ImageInfo> {
            val imageSpacing = combiningParams.spacingFor(isHorizontal)
            val (size, images) = calculateCombinedImageDimensionsAndBitmaps(
                imageUris = imagesUris,
                isHorizontal = isHorizontal,
                scaleDirection = combiningParams.scaleDirection,
                imageSpacing = imageSpacing,
                imageScale = combiningParams.outputScale
            )

            val bitmap = createBitmap(
                width = size.width,
                height = size.height,
                config = getSuitableConfig()
            ).applyCanvas {
                drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
                if (includeBackground) {
                    drawBackground(
                        combiningParams.backgroundColor,
                        combiningParams.backgroundGradient
                    )
                }

                var pos = 0

                val fullSpace = imageSpacing.absoluteValue
                val halfSpace = fullSpace / 2
                val strength = combiningParams.fadeStrength

                for (i in images.indices) {
                    currentCoroutineContext().ensureActive()
                    var bmp = images[i]

                    imageSpacing.takeIf { it < 0 && combiningParams.fadingEdgesMode != StitchFadeSide.None }
                        ?.let {
                            val filters = when (combiningParams.fadingEdgesMode) {
                                StitchFadeSide.Start -> {
                                    when (i) {
                                        0 -> emptyList()
                                        else -> listOf(
                                            createFilter<Filter.SideFade.Value, Filter.SideFade>(
                                                FilterValues.sideFade(
                                                    side = if (isHorizontal) FadeSide.Start else FadeSide.Top,
                                                    scale = fullSpace.toFloat() / if (isHorizontal) bmp.width else bmp.height,
                                                    strength = strength
                                                )
                                            )
                                        )
                                    }
                                }

                                StitchFadeSide.End -> {
                                    when (i) {
                                        images.lastIndex -> emptyList()
                                        else -> listOf(
                                            createFilter<Filter.SideFade.Value, Filter.SideFade>(
                                                FilterValues.sideFade(
                                                    side = if (isHorizontal) FadeSide.End else FadeSide.Bottom,
                                                    scale = fullSpace.toFloat() / if (isHorizontal) bmp.width else bmp.height,
                                                    strength = strength
                                                )
                                            )
                                        )
                                    }
                                }

                                StitchFadeSide.Both -> {
                                    when (i) {
                                        0 -> listOf(
                                            createFilter<Filter.SideFade.Value, Filter.SideFade>(
                                                FilterValues.sideFade(
                                                    side = if (isHorizontal) FadeSide.End else FadeSide.Bottom,
                                                    scale = halfSpace.toFloat() / if (isHorizontal) bmp.width else bmp.height,
                                                    strength = strength
                                                )
                                            )
                                        )

                                        images.lastIndex -> listOf(
                                            createFilter<Filter.SideFade.Value, Filter.SideFade>(
                                                FilterValues.sideFade(
                                                    side = if (isHorizontal) FadeSide.Start else FadeSide.Top,
                                                    scale = halfSpace.toFloat() / if (isHorizontal) bmp.width else bmp.height,
                                                    strength = strength
                                                )
                                            )
                                        )

                                        else -> listOf(
                                            createFilter<Filter.SideFade.Value, Filter.SideFade>(
                                                FilterValues.sideFade(
                                                    side = if (isHorizontal) FadeSide.Start else FadeSide.Top,
                                                    scale = halfSpace.toFloat() / if (isHorizontal) bmp.width else bmp.height,
                                                    strength = strength
                                                )
                                            ),
                                            createFilter<Filter.SideFade.Value, Filter.SideFade>(
                                                FilterValues.sideFade(
                                                    side = if (isHorizontal) FadeSide.End else FadeSide.Bottom,
                                                    scale = halfSpace.toFloat() / if (isHorizontal) bmp.width else bmp.height,
                                                    strength = strength
                                                )
                                            )
                                        )
                                    }
                                }

                                else -> emptyList()
                            }

                            imageTransformer.transform(
                                image = bmp,
                                transformations = filters.map(filterProvider::filterToTransformation)
                            )?.let { bmp = it }
                        }

                    if (isHorizontal) {
                        drawBitmap(
                            bitmap = bmp,
                            left = pos.toFloat(),
                            top = when (combiningParams.alignment) {
                                StitchAlignment.Start -> 0f
                                StitchAlignment.Center -> (height - bmp.height) / 2f
                                StitchAlignment.End -> (height - bmp.height).toFloat()
                            },
                            paint = if (pos > 0) combiningParams.blendingMode.toPaint() else Paint()
                        )
                    } else {
                        drawBitmap(
                            bitmap = bmp,
                            left = when (combiningParams.alignment) {
                                StitchAlignment.Start -> 0f
                                StitchAlignment.Center -> (width - bmp.width) / 2f
                                StitchAlignment.End -> (width - bmp.width).toFloat()
                            },
                            top = pos.toFloat(),
                            paint = if (pos > 0) combiningParams.blendingMode.toPaint() else Paint()
                        )
                    }
                    pos += if (isHorizontal) {
                        (bmp.width + imageSpacing).coerceAtLeast(1)
                    } else (bmp.height + imageSpacing).coerceAtLeast(1)

                    onProgress(i + 1)
                }
            }

            return bitmap to ImageInfo(
                width = size.width,
                height = size.height,
                imageFormat = ImageFormat.Png.Lossless
            )
        }

        val gridCells = combiningParams.stitchMode.gridCellsCount()
        if (gridCells in 1..imageSources.size) {
            val gridImages = mutableListOf<Bitmap>()
            try {
                for (sources in distributeImages(imageSources, gridCells)) {
                    ensureActive()
                    val image = getImageData(
                        imagesUris = sources,
                        isHorizontal = combiningParams.stitchMode.isHorizontal(),
                        includeBackground = combiningParams.backgroundGradient == null
                    ).first
                    gridImages += image
                    if (settingsState.enableBackgroundColorForAlphaFormats) {
                        val flattened = Trickle.drawColorBehind(
                            color = settingsState.backgroundForNoAlphaImageFormats.colorInt,
                            input = image
                        )
                        gridImages[gridImages.lastIndex] = flattened
                        if (flattened !== image) image.recycle()
                    }
                }
                combine(
                    imageSources = gridImages,
                    combiningParams = combiningParams.copy(
                        stitchMode = when (combiningParams.stitchMode) {
                            is StitchMode.Grid.Horizontal -> StitchMode.Vertical
                            else -> StitchMode.Horizontal
                        },
                        outputScale = 1f
                    ),
                    onProgress = onProgress
                )
            } finally {
                gridImages.forEach(Bitmap::recycle)
            }
        } else {
            getImageData(
                imagesUris = imageSources,
                isHorizontal = combiningParams.stitchMode.isHorizontal()
            )
        }
    }

    override suspend fun calculateCombinedImageDimensions(
        imageUris: List<String>,
        combiningParams: CombiningParams
    ): IntegerSize = withContext(defaultDispatcher) {
        val isHorizontal = combiningParams.stitchMode.isHorizontal()
        suspend fun sizeOf(uris: List<String>): IntegerSize {
            val sizes = uris.mapNotNull { uri ->
                ensureActive()
                imageGetter.getImage(data = uri, originalSize = true)?.let {
                    IntegerSize(it.width, it.height)
                }
            }
            return scaledSizes(
                sizes,
                isHorizontal,
                combiningParams.scaleDirection,
                combiningParams.outputScale
            )
                .combinedSize(isHorizontal, combiningParams.spacingFor(isHorizontal))
        }

        val gridCells = combiningParams.stitchMode.gridCellsCount()
        if (gridCells in 1..imageUris.size) {
            val sizes = distributeImages(imageUris, gridCells).map { sizeOf(it) }
            scaledSizes(sizes, !isHorizontal, combiningParams.scaleDirection, 1f)
                .combinedSize(!isHorizontal, combiningParams.spacingFor(!isHorizontal))
        } else {
            sizeOf(imageUris)
        }
    }

    private fun scaledSizes(
        sizes: List<IntegerSize>,
        isHorizontal: Boolean,
        scaleDirection: ImageScaleDirection,
        imageScale: Float
    ): List<IntegerSize> {
        val targetWidth = scaleDirection.targetDimension(
            sizes.map { (it.width * imageScale).roundToInt().coerceAtLeast(1) }
        )
        val targetHeight = scaleDirection.targetDimension(
            sizes.map { (it.height * imageScale).roundToInt().coerceAtLeast(1) }
        )
        return sizes.map { size ->
            size.scaledSize(isHorizontal, imageScale, targetWidth, targetHeight)
        }
    }

    private fun IntegerSize.scaledSize(
        isHorizontal: Boolean,
        imageScale: Float,
        targetWidth: Int?,
        targetHeight: Int?
    ): IntegerSize {
        val scaledWidth = (width * imageScale).roundToInt().coerceAtLeast(1)
        val scaledHeight = (height * imageScale).roundToInt().coerceAtLeast(1)
        val aspectRatio = width.toFloat() / height
        return when {
            isHorizontal && targetHeight != null && scaledHeight != targetHeight -> IntegerSize(
                (targetHeight * aspectRatio).toInt().coerceAtLeast(1), targetHeight
            )

            !isHorizontal && targetWidth != null && scaledWidth != targetWidth -> IntegerSize(
                targetWidth, (targetWidth / aspectRatio).toInt().coerceAtLeast(1)
            )

            else -> IntegerSize(scaledWidth, scaledHeight)
        }
    }

    private fun List<IntegerSize>.combinedSize(
        isHorizontal: Boolean,
        imageSpacing: Int
    ): IntegerSize {
        var length = 0
        var crossSize = 1
        forEachIndexed { index, size ->
            val dimension = if (isHorizontal) size.width else size.height
            length += (dimension + if (index == lastIndex) 0 else imageSpacing).coerceAtLeast(1)
            crossSize = maxOf(crossSize, if (isHorizontal) size.height else size.width)
        }
        return if (isHorizontal) {
            IntegerSize(length.coerceAtLeast(1), crossSize)
        } else IntegerSize(crossSize, length.coerceAtLeast(1))
    }

    private suspend fun calculateCombinedImageDimensionsAndBitmaps(
        imageUris: List<Any>,
        isHorizontal: Boolean,
        scaleDirection: ImageScaleDirection,
        imageSpacing: Int,
        imageScale: Float
    ): Pair<IntegerSize, List<Bitmap>> = withContext(defaultDispatcher) {
        val imageSizes = if (scaleDirection != ImageScaleDirection.None) {
            imageUris.mapNotNull { uri ->
                ensureActive()
                imageGetter.getImage(data = uri, originalSize = true)?.let { image ->
                    IntegerSize(image.width, image.height)
                }
            }
        } else emptyList()
        val targetWidth = scaleDirection.targetDimension(
            imageSizes.map { (it.width * imageScale).roundToInt().coerceAtLeast(1) }
        )
        val targetHeight = scaleDirection.targetDimension(
            imageSizes.map { (it.height * imageScale).roundToInt().coerceAtLeast(1) }
        )
        val drawables = imageUris.mapNotNull { uri ->
            val image = imageGetter.getImage(data = uri, originalSize = true)
                ?: return@mapNotNull null
            ensureActive()
            val targetSize = IntegerSize(image.width, image.height).scaledSize(
                isHorizontal, imageScale, targetWidth, targetHeight
            )

            image.createScaledBitmap(
                width = targetSize.width,
                height = targetSize.height
            )
        }

        drawables.map { IntegerSize(it.width, it.height) }
            .combinedSize(isHorizontal, imageSpacing) to drawables
    }

    private fun <T> distributeImages(
        images: List<T>,
        cellCount: Int
    ): List<List<T>> {
        val imageCount = images.size
        val imagesPerRow = imageCount / cellCount
        val remainingImages = imageCount % cellCount

        val result = MutableList(cellCount) { imagesPerRow }

        for (i in 0 until remainingImages) {
            result[i] += 1
        }

        var offset = 0
        return result.map { count ->
            images.subList(
                fromIndex = offset,
                toIndex = offset + count
            ).also {
                offset += count
            }
        }
    }

    override suspend fun createCombinedImagesPreview(
        imageUris: List<String>,
        combiningParams: CombiningParams,
        imageFormat: ImageFormat,
        quality: Quality,
        onGetByteCount: (Long) -> Unit,
        onGetPreview: (ImageWithSize<Bitmap?>) -> Unit
    ): Unit = withContext(defaultDispatcher) {
        suspend fun calculateImageSize(): IntegerSize {
            return calculateCombinedImageDimensions(
                imageUris = imageUris,
                combiningParams = combiningParams
            ).let {
                it.copy(
                    width = (it.width / combiningParams.outputScale).roundToInt(),
                    height = (it.height / combiningParams.outputScale).roundToInt()
                )
            }
        }

        if (!settingsState.generatePreviews) {
            onGetPreview(null withSize calculateImageSize())
            return@withContext
        }

        val isAutoStitch = combiningParams.stitchMode.isAuto()
        val scale = if (isAutoStitch) 0.5f else 0.2f

        combineImages(
            imageUris = imageUris,
            combiningParams = combiningParams.copy(
                outputScale = scale
            ),
            onProgress = {}
        ).let { (image, imageInfo) ->
            val imageSize = if (isAutoStitch) {
                IntegerSize(
                    width = (imageInfo.width / scale).roundToInt(),
                    height = (imageInfo.height / scale).roundToInt()
                )
            } else calculateImageSize()

            imagePreviewCreator.createPreview(
                image = image,
                imageInfo = imageInfo.copy(
                    imageFormat = imageFormat,
                    quality = quality
                ),
                transformations = emptyList(),
                onGetByteCount = {
                    val original = it / (scale * scale)
                    onGetByteCount(original.roundToLong())
                },
                onGetPreview = { onGetPreview(it withSize imageSize) }
            )
        }
    }

    private suspend fun Bitmap.createScaledBitmap(
        width: Int,
        height: Int
    ): Bitmap = imageScaler.scaleImage(
        image = this,
        width = width,
        height = height
    )

}
