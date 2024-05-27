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

package ru.tech.imageresizershrinker.core.data.image

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.BitmapCompat
import androidx.core.graphics.applyCanvas
import com.awxkee.aire.Aire
import com.awxkee.aire.BitmapScaleMode
import com.t8rin.logger.makeLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.utils.aspectRatio
import ru.tech.imageresizershrinker.core.data.utils.toSoftware
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeAnchor
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeType
import ru.tech.imageresizershrinker.core.domain.image.model.ScaleColorSpace
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.settings.domain.SettingsProvider
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt
import com.awxkee.aire.ScaleColorSpace as AireScaleColorSpace

internal class AndroidImageScaler @Inject constructor(
    settingsProvider: SettingsProvider,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, ImageScaler<Bitmap> {

    private var defaultImageScaleMode: ImageScaleMode = ImageScaleMode.Default

    init {
        settingsProvider
            .getSettingsStateFlow().onEach {
                defaultImageScaleMode = it.defaultImageScaleMode
            }.launchIn(CoroutineScope(defaultDispatcher))
    }

    override suspend fun scaleImage(
        image: Bitmap,
        width: Int,
        height: Int,
        resizeType: ResizeType,
        imageScaleMode: ImageScaleMode
    ): Bitmap = withContext(defaultDispatcher) {

        val widthInternal = width.takeIf { it > 0 } ?: image.width
        val heightInternal = height.takeIf { it > 0 } ?: image.height

        when (resizeType) {
            ResizeType.Explicit -> {
                createScaledBitmap(
                    image = image,
                    width = widthInternal,
                    height = heightInternal,
                    imageScaleMode = imageScaleMode
                )
            }

            is ResizeType.Flexible -> {
                flexibleResize(
                    image = image,
                    width = widthInternal,
                    height = heightInternal,
                    resizeAnchor = resizeType.resizeAnchor,
                    imageScaleMode = imageScaleMode
                )
            }

            is ResizeType.CenterCrop -> {
                resizeType.resizeWithCenterCrop(
                    image = image,
                    targetWidth = widthInternal,
                    targetHeight = heightInternal,
                    imageScaleMode = imageScaleMode,
                    scaleFactor = resizeType.scaleFactor
                )
            }
        }
    }

    override suspend fun scaleUntilCanShow(
        image: Bitmap?
    ): Bitmap? = withContext(defaultDispatcher) {
        if (image == null) return@withContext null

        var (height, width) = image.run { height to width }

        var iterations = 0
        while (!canShow(size = height * width * 4)) {
            height = (height * 0.85f).roundToInt()
            width = (width * 0.85f).roundToInt()
            iterations++
        }

        if (iterations == 0) image
        else scaleImage(
            image = image,
            height = height,
            width = width,
            imageScaleMode = ImageScaleMode.Bicubic()
        )
    }

    private fun canShow(size: Int): Boolean {
        return size < 3096 * 3096 * 3
    }

    private suspend fun ResizeType.CenterCrop.resizeWithCenterCrop(
        image: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        scaleFactor: Float,
        imageScaleMode: ImageScaleMode
    ): Bitmap = withContext(defaultDispatcher) {
        val mTargetWidth = (targetWidth / scaleFactor).roundToInt()
        val mTargetHeight = (targetHeight / scaleFactor).roundToInt()

        val originalSize = if (!originalSize.isDefined()) {
            IntegerSize(
                (image.width * scaleFactor).roundToInt(),
                (image.height * scaleFactor).roundToInt()
            )
        } else originalSize

        if (mTargetWidth == originalSize.width && mTargetHeight == originalSize.height) {
            return@withContext image
        }

        val bitmap = imageTransformer.transform(
            image = image.let { bitmap ->
                val xScale: Float = mTargetWidth.toFloat() / originalSize.width
                val yScale: Float = mTargetHeight.toFloat() / originalSize.height
                val scale = xScale.coerceAtLeast(yScale)
                createScaledBitmap(
                    image = bitmap,
                    width = (scale * originalSize.width).toInt(),
                    height = (scale * originalSize.height).toInt(),
                    imageScaleMode = imageScaleMode
                )
            },
            transformations = listOf(
                filterProvider.filterToTransformation(
                    object : Filter.StackBlur<Bitmap> {
                        override val value: Pair<Float, Int>
                            get() = 0.5f to blurRadius
                    }
                )
            )
        )

        val drawImage = createScaledBitmap(
            image = image,
            width = (originalSize.width * scaleFactor).roundToInt(),
            height = (originalSize.height * scaleFactor).roundToInt(),
            imageScaleMode = imageScaleMode
        )

        Bitmap.createBitmap(
            mTargetWidth,
            mTargetHeight,
            drawImage.config
        ).apply {
            setHasAlpha(true)
        }.applyCanvas {
            drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
            this@resizeWithCenterCrop.canvasColor?.let {
                drawColor(it)
            } ?: bitmap?.let {
                drawBitmap(
                    bitmap,
                    (width - bitmap.width) / 2f,
                    (height - bitmap.height) / 2f,
                    null
                )
            }
            val left = (width - drawImage.width) / 2f
            val top = (height - drawImage.height) / 2f
            drawBitmap(
                drawImage,
                null,
                RectF(
                    left,
                    top,
                    drawImage.width + left,
                    drawImage.height + top
                ),
                null
            )
        }
    }

    private suspend fun createScaledBitmap(
        image: Bitmap,
        width: Int,
        height: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap = withContext(defaultDispatcher) {
        if (width == image.width && height == image.height) return@withContext image

        if (imageScaleMode is ImageScaleMode.Base) {
            return@withContext if (width < image.width && height < image.width) {
                BitmapCompat.createScaledBitmap(image, width, height, null, true)
            } else {
                Bitmap.createScaledBitmap(image, width, height, true)
            }
        }

        val mode = imageScaleMode.takeIf {
            it != ImageScaleMode.NotPresent && it.value >= 0
        } ?: defaultImageScaleMode

        Aire.scale(
            bitmap = image.toSoftware(),
            dstWidth = width,
            dstHeight = height,
            scaleMode = BitmapScaleMode.entries.firstOrNull {
                it.ordinal == mode.value
            } ?: BitmapScaleMode.Bilinear,
            colorSpace = mode.scaleColorSpace.toColorSpace()
        )
    }

    private fun ScaleColorSpace.toColorSpace(): AireScaleColorSpace = when (this) {
        ScaleColorSpace.LAB -> AireScaleColorSpace.LAB
        ScaleColorSpace.Linear -> AireScaleColorSpace.LINEAR
        ScaleColorSpace.SRGB -> AireScaleColorSpace.SRGB
        ScaleColorSpace.LUV -> AireScaleColorSpace.LUV
    }

    private suspend fun flexibleResize(
        image: Bitmap,
        width: Int,
        height: Int,
        resizeAnchor: ResizeAnchor,
        imageScaleMode: ImageScaleMode
    ): Bitmap = withContext(defaultDispatcher) {
        val max = max(width, height)

        val scaleByWidth = suspend {
            val aspectRatio = image.aspectRatio
            createScaledBitmap(
                image = image,
                width = width,
                height = (width / aspectRatio).toInt(),
                imageScaleMode = ImageScaleMode.NotPresent
            )
        }

        val scaleByHeight = suspend {
            val aspectRatio = image.aspectRatio.makeLog()
            createScaledBitmap(
                image = image,
                width = (height * aspectRatio).toInt(),
                height = height,
                imageScaleMode = ImageScaleMode.NotPresent
            )
        }

        when (resizeAnchor) {
            ResizeAnchor.Max -> {
                if (width >= height) {
                    scaleByWidth()
                } else scaleByHeight()
            }

            ResizeAnchor.Min -> {
                if (width >= height) {
                    scaleByHeight()
                } else scaleByWidth()
            }

            ResizeAnchor.Width -> scaleByWidth()

            ResizeAnchor.Height -> scaleByHeight()

            ResizeAnchor.Default -> {
                runCatching {
                    if (image.height >= image.width) {
                        val aspectRatio = image.aspectRatio
                        val targetWidth = (max * aspectRatio).toInt()
                        createScaledBitmap(image, targetWidth, max, imageScaleMode)
                    } else {
                        val aspectRatio = 1f / image.aspectRatio
                        val targetHeight = (max * aspectRatio).toInt()
                        createScaledBitmap(image, max, targetHeight, imageScaleMode)
                    }
                }.getOrNull() ?: image
            }
        }
    }

}