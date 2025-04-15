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

package ru.tech.imageresizershrinker.feature.image_stitch.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.utils.aspectRatio
import ru.tech.imageresizershrinker.core.data.utils.getSuitableConfig
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImagePreviewCreator
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageShareProvider
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.model.ImageWithSize
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.image.model.withSize
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.FadeSide
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.SideFadeParams
import ru.tech.imageresizershrinker.core.filters.domain.model.createFilter
import ru.tech.imageresizershrinker.core.settings.domain.SettingsProvider
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.feature.image_stitch.domain.CombiningParams
import ru.tech.imageresizershrinker.feature.image_stitch.domain.ImageCombiner
import ru.tech.imageresizershrinker.feature.image_stitch.domain.StitchAlignment
import ru.tech.imageresizershrinker.feature.image_stitch.domain.StitchMode
import javax.inject.Inject
import kotlin.math.absoluteValue
import kotlin.math.max

internal class AndroidImageCombiner @Inject constructor(
    private val imageScaler: ImageScaler<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>,
    settingsProvider: SettingsProvider,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, ImageCombiner<Bitmap> {

    private var generatePreviews = SettingsState.Default.generatePreviews

    init {
        settingsProvider
            .getSettingsStateFlow()
            .onEach {
                generatePreviews = it.generatePreviews
            }.launchIn(CoroutineScope(defaultDispatcher))
    }

    override suspend fun combineImages(
        imageUris: List<String>,
        combiningParams: CombiningParams,
        imageScale: Float,
        onProgress: (Int) -> Unit
    ): Pair<Bitmap, ImageInfo> = withContext(defaultDispatcher) {
        suspend fun getImageData(
            imagesUris: List<String>,
            isHorizontal: Boolean
        ): Pair<Bitmap, ImageInfo> {
            val (size, images) = calculateCombinedImageDimensionsAndBitmaps(
                imageUris = imagesUris,
                isHorizontal = isHorizontal,
                scaleSmallImagesToLarge = combiningParams.scaleSmallImagesToLarge,
                imageSpacing = combiningParams.spacing
            )

            val bitmaps = images.map { image ->
                if (
                    combiningParams.scaleSmallImagesToLarge && image.shouldUpscale(
                        isHorizontal = isHorizontal,
                        size = size
                    )
                ) {
                    image.upscale(isHorizontal, size)
                } else image
            }

            val bitmap = createBitmap(size.width, size.height, getSuitableConfig())
            val canvas = Canvas(bitmap).apply {
                drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
                drawColor(combiningParams.backgroundColor)
            }

            var pos = 0
            for (i in imagesUris.indices) {
                var bmp = bitmaps[i]

                combiningParams.spacing.takeIf { it < 0 && combiningParams.fadingEdgesMode != null }
                    ?.let {
                        val space = combiningParams.spacing.absoluteValue
                        val bottomFilter = createFilter<SideFadeParams, Filter.SideFade>(
                            SideFadeParams.Absolute(
                                side = if (isHorizontal) {
                                    FadeSide.End
                                } else FadeSide.Bottom,
                                size = space
                            )
                        )
                        val topFilter = createFilter<SideFadeParams, Filter.SideFade>(
                            SideFadeParams.Absolute(
                                side = if (isHorizontal) {
                                    FadeSide.Start
                                } else FadeSide.Top,
                                size = space
                            )
                        )
                        val filters = if (combiningParams.fadingEdgesMode == 0) {
                            when (i) {
                                0 -> listOf()
                                else -> listOf(topFilter)
                            }
                        } else {
                            when (i) {
                                0 -> listOf(bottomFilter)
                                imagesUris.lastIndex -> listOf(topFilter)
                                else -> listOf(topFilter, bottomFilter)
                            }
                        }.map {
                            filterProvider.filterToTransformation(it)
                        }

                        imageTransformer.transform(bmp, filters)?.let { bmp = it }
                    }

                if (isHorizontal) {
                    canvas.drawBitmap(
                        bmp,
                        pos.toFloat(),
                        when (combiningParams.alignment) {
                            StitchAlignment.Start -> 0f
                            StitchAlignment.Center -> (canvas.height - bmp.height) / 2f
                            StitchAlignment.End -> (canvas.height - bmp.height).toFloat()
                        },
                        null
                    )
                } else {
                    canvas.drawBitmap(
                        bmp,
                        when (combiningParams.alignment) {
                            StitchAlignment.Start -> 0f
                            StitchAlignment.Center -> (canvas.width - bmp.width) / 2f
                            StitchAlignment.End -> (canvas.width - bmp.width).toFloat()
                        },
                        pos.toFloat(),
                        null
                    )
                }
                pos += if (isHorizontal) {
                    (bmp.width + combiningParams.spacing).coerceAtLeast(1)
                } else (bmp.height + combiningParams.spacing).coerceAtLeast(1)

                onProgress(i + 1)
            }

            return imageScaler.scaleImage(
                image = bitmap,
                width = (size.width * imageScale).toInt(),
                height = (size.height * imageScale).toInt(),
                imageScaleMode = ImageScaleMode.NotPresent
            ) to ImageInfo(
                width = (size.width * imageScale).toInt(),
                height = (size.height * imageScale).toInt(),
                imageFormat = ImageFormat.Png.Lossless
            )
        }

        if (combiningParams.stitchMode.gridCellsCount().let { !(it == 0 || it > imageUris.size) }) {
            combineImages(
                imageUris = distributeImages(
                    images = imageUris,
                    cellCount = combiningParams.stitchMode.gridCellsCount()
                ).mapNotNull { images ->
                    val data = getImageData(
                        imagesUris = images,
                        isHorizontal = combiningParams.stitchMode.isHorizontal()
                    )
                    shareProvider.cacheImage(
                        image = data.first,
                        imageInfo = data.second
                    )
                },
                combiningParams = combiningParams.copy(
                    stitchMode = when (combiningParams.stitchMode) {
                        is StitchMode.Grid.Horizontal -> StitchMode.Vertical
                        else -> StitchMode.Horizontal
                    }
                ),
                imageScale = imageScale,
                onProgress = onProgress
            )
        } else {
            getImageData(
                imagesUris = imageUris,
                isHorizontal = combiningParams.stitchMode.isHorizontal()
            )
        }
    }

    override suspend fun calculateCombinedImageDimensions(
        imageUris: List<String>,
        combiningParams: CombiningParams
    ): IntegerSize {
        return if (combiningParams.stitchMode.gridCellsCount()
                .let { it == 0 || it > imageUris.size }
        ) {
            calculateCombinedImageDimensionsAndBitmaps(
                imageUris = imageUris,
                isHorizontal = combiningParams.stitchMode.isHorizontal(),
                scaleSmallImagesToLarge = combiningParams.scaleSmallImagesToLarge,
                imageSpacing = combiningParams.spacing
            ).first
        } else {
            val isHorizontalGrid = combiningParams.stitchMode.isHorizontal()
            var size = IntegerSize(0, 0)
            distributeImages(
                images = imageUris,
                cellCount = combiningParams.stitchMode.gridCellsCount()
            ).forEach { images ->
                calculateCombinedImageDimensionsAndBitmaps(
                    imageUris = images,
                    isHorizontal = !isHorizontalGrid,
                    scaleSmallImagesToLarge = combiningParams.scaleSmallImagesToLarge,
                    imageSpacing = combiningParams.spacing
                ).first.let { newSize ->
                    size = if (isHorizontalGrid) {
                        size.copy(
                            height = size.height + newSize.height,
                            width = max(newSize.width, size.width)
                        )
                    } else {
                        size.copy(
                            height = max(newSize.height, size.height),
                            width = size.width + newSize.width,
                        )
                    }
                }
            }
            IntegerSize(
                width = size.width.coerceAtLeast(1),
                height = size.height.coerceAtLeast(1)
            )
        }
    }

    private suspend fun calculateCombinedImageDimensionsAndBitmaps(
        imageUris: List<String>,
        isHorizontal: Boolean,
        scaleSmallImagesToLarge: Boolean,
        imageSpacing: Int,
    ): Pair<IntegerSize, List<Bitmap>> = withContext(defaultDispatcher) {
        var w = 0
        var h = 0
        var maxHeight = 0
        var maxWidth = 0
        val drawables = imageUris.mapNotNull { uri ->
            imageGetter.getImage(
                data = uri,
                originalSize = true
            )?.apply {
                maxWidth = max(maxWidth, width)
                maxHeight = max(maxHeight, height)
            }
        }

        drawables.forEachIndexed { index, image ->
            val width = image.width
            val height = image.height

            val spacing = if (index != drawables.lastIndex) imageSpacing else 0

            if (scaleSmallImagesToLarge && image.shouldUpscale(
                    isHorizontal = isHorizontal,
                    size = IntegerSize(maxWidth, maxHeight)
                )
            ) {
                val targetHeight: Int
                val targetWidth: Int

                if (isHorizontal) {
                    targetHeight = maxHeight
                    targetWidth = (targetHeight * image.aspectRatio).toInt()
                } else {
                    targetWidth = maxWidth
                    targetHeight = (targetWidth / image.aspectRatio).toInt()
                }
                if (isHorizontal) {
                    w += (targetWidth + spacing).coerceAtLeast(1)
                } else {
                    h += (targetHeight + spacing).coerceAtLeast(1)
                }
            } else {
                if (isHorizontal) {
                    w += (width + spacing).coerceAtLeast(1)
                } else {
                    h += (height + spacing).coerceAtLeast(1)
                }
            }
        }

        if (isHorizontal) {
            h = maxHeight
        } else {
            w = maxWidth
        }

        IntegerSize(
            width = w.coerceAtLeast(1),
            height = h.coerceAtLeast(1)
        ) to drawables
    }

    private fun distributeImages(
        images: List<String>,
        cellCount: Int
    ): List<List<String>> {
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
        onGetByteCount: (Int) -> Unit
    ): ImageWithSize<Bitmap?> = withContext(defaultDispatcher) {
        val imageSize = calculateCombinedImageDimensions(
            imageUris = imageUris,
            combiningParams = combiningParams
        )

        if (!generatePreviews) return@withContext null withSize imageSize

        combineImages(
            imageUris = imageUris,
            combiningParams = combiningParams,
            imageScale = 1f,
            onProgress = {}
        ).let { (image, imageInfo) ->
            return@let imagePreviewCreator.createPreview(
                image = image,
                imageInfo = imageInfo.copy(
                    imageFormat = imageFormat,
                    quality = quality
                ),
                transformations = emptyList(),
                onGetByteCount = onGetByteCount
            ) withSize imageSize
        }
    }

    private fun Bitmap.shouldUpscale(
        isHorizontal: Boolean,
        size: IntegerSize
    ): Boolean {
        return if (isHorizontal) this.height != size.height
        else this.width != size.width
    }

    private suspend fun Bitmap.upscale(
        isHorizontal: Boolean,
        size: IntegerSize
    ): Bitmap {
        return if (isHorizontal) {
            imageScaler.scaleImage(
                image = this,
                width = (size.height * aspectRatio).toInt(),
                height = size.height,
                imageScaleMode = ImageScaleMode.NotPresent
            )
        } else {
            imageScaler.scaleImage(
                image = this,
                width = size.width,
                height = (size.width / aspectRatio).toInt(),
                imageScaleMode = ImageScaleMode.NotPresent
            )
        }
    }

}