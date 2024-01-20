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
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.BitmapCompat
import com.awxkee.jxlcoder.scale.BitmapScaleMode
import com.awxkee.jxlcoder.scale.BitmapScaler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.ResizeType
import ru.tech.imageresizershrinker.core.domain.repository.SettingsRepository
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt

class AndroidImageScaler @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>
) : ImageScaler<Bitmap> {

    override suspend fun scaleImage(
        image: Bitmap,
        width: Int,
        height: Int,
        resizeType: ResizeType,
        imageScaleMode: ImageScaleMode
    ): Bitmap? = withContext(Dispatchers.IO) {

        val widthInternal = width.takeIf { it > 0 } ?: image.width
        val heightInternal = height.takeIf { it > 0 } ?: image.height

        return@withContext when (resizeType) {
            ResizeType.Explicit -> {
                createScaledBitmap(
                    image,
                    width = widthInternal,
                    height = heightInternal,
                    imageScaleMode = imageScaleMode
                )
            }

            ResizeType.Flexible -> {
                flexibleResize(
                    image = image,
                    max = max(widthInternal, heightInternal),
                    imageScaleMode = imageScaleMode
                )
            }

            is ResizeType.Limits -> {
                resizeType.resizeWithLimits(
                    image = image,
                    width = widthInternal,
                    height = heightInternal,
                    imageScaleMode = imageScaleMode
                )
            }

            is ResizeType.CenterCrop -> {
                resizeType.resizeWithCenterCrop(
                    image = image,
                    w = widthInternal,
                    h = heightInternal,
                    imageScaleMode = imageScaleMode
                )
            }
        }
    }

    override suspend fun scaleUntilCanShow(
        image: Bitmap?
    ): Bitmap? = withContext(Dispatchers.IO) {
        if (image == null) return@withContext null

        var (height, width) = image.run { height to width }

        var iterations = 0
        while (!canShow(size = height * width * 4)) {
            height = (height * 0.85f).roundToInt()
            width = (width * 0.85f).roundToInt()
            iterations++
        }

        return@withContext if (iterations == 0) image
        else scaleImage(
            image = image,
            height = height,
            width = width,
            imageScaleMode = ImageScaleMode.Bicubic
        )
    }

    private fun canShow(size: Int): Boolean {
        return size < 3096 * 3096 * 3
    }

    override suspend fun scaleByMaxBytes(
        image: Bitmap,
        imageFormat: ImageFormat,
        imageScaleMode: ImageScaleMode,
        maxBytes: Long
    ): Pair<Bitmap, ImageInfo>? = withContext(Dispatchers.IO) {
        val maxBytes1 =
            maxBytes - maxBytes
                .times(0.04f)
                .roundToInt()
                .coerceIn(
                    minimumValue = 256,
                    maximumValue = 512
                )

        return@withContext kotlin.runCatching {
            if (
                imageCompressor.calculateImageSize(
                    image = image,
                    imageInfo = ImageInfo(imageFormat = imageFormat)
                ) > maxBytes1
            ) {
                var streamLength = maxBytes1
                var compressQuality = 100
                val bmpStream = ByteArrayOutputStream()
                var newSize = image.width to image.height

                while (streamLength >= maxBytes1) {
                    compressQuality -= 1

                    if (compressQuality < 20) break

                    bmpStream.use {
                        it.flush()
                        it.reset()
                    }
                    bmpStream.write(
                        imageCompressor.compressAndTransform(
                            image = image,
                            imageInfo = ImageInfo(
                                quality = compressQuality.toFloat(),
                                imageFormat = imageFormat
                            )
                        )
                    )
                    streamLength = (bmpStream.toByteArray().size).toLong()
                }

                if (compressQuality < 20) {
                    compressQuality = 20
                    while (streamLength >= maxBytes1) {
                        bmpStream.use {
                            it.flush()
                            it.reset()
                        }
                        val temp = scaleImage(
                            image = image,
                            width = (newSize.first * 0.98).toInt(),
                            height = (newSize.second * 0.98).toInt(),
                            imageScaleMode = imageScaleMode
                        )
                        bmpStream.write(
                            temp?.let {
                                newSize = it.width to it.height
                                imageCompressor.compressAndTransform(
                                    image = image,
                                    imageInfo = ImageInfo(
                                        quality = compressQuality.toFloat(),
                                        imageFormat = imageFormat,
                                        width = newSize.first,
                                        height = newSize.second
                                    )
                                )
                            }
                        )
                        streamLength = (bmpStream.toByteArray().size).toLong()
                    }
                }
                BitmapFactory.decodeStream(ByteArrayInputStream(bmpStream.toByteArray())) to compressQuality
            } else null
        }.getOrNull()?.let {
            it.first to ImageInfo(
                width = it.first.width,
                height = it.first.height,
                quality = it.second.toFloat()
            )
        }
    }

    private suspend fun ResizeType.CenterCrop.resizeWithCenterCrop(
        image: Bitmap,
        w: Int,
        h: Int,
        scaleFactor: Float = 1f,
        imageScaleMode: ImageScaleMode
    ): Bitmap {
        if (w == image.width && h == image.height) return image
        val bitmap = imageTransformer.transform(
            image = image.let { bitmap ->
                val xScale: Float = w.toFloat() / bitmap.width
                val yScale: Float = h.toFloat() / bitmap.height
                val scale = xScale.coerceAtLeast(yScale)
                createScaledBitmap(
                    bitmap,
                    width = (scale * bitmap.width).toInt(),
                    height = (scale * bitmap.height).toInt(),
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
            width = (image.width * scaleFactor).toInt(),
            height = (image.height * scaleFactor).toInt(),
            imageScaleMode = imageScaleMode
        )
        val canvas = Bitmap.createBitmap(w, h, drawImage.config).apply { setHasAlpha(true) }
        Canvas(canvas).apply {
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
            drawBitmap(
                drawImage,
                (width - drawImage.width) / 2f,
                (height - drawImage.height) / 2f,
                Paint()
            )
        }
        return canvas
    }

    private suspend fun createScaledBitmap(
        image: Bitmap,
        width: Int,
        height: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap {
        if (width == image.width && height == image.height) return image

        val mode = imageScaleMode.takeIf {
            it != ImageScaleMode.NotPresent
        } ?: settingsRepository.getSettingsState().defaultImageScaleMode

        return mode.takeIf { it != ImageScaleMode.Default }?.let {
            BitmapScaler.scale(
                bitmap = image,
                dstWidth = width,
                dstHeight = height,
                scaleMode = BitmapScaleMode.entries.first { e -> e.ordinal == it.value }
            )
        } ?: BitmapCompat.createScaledBitmap(
            image,
            width,
            height,
            null,
            true
        )
    }

    private val Bitmap.aspectRatio: Float get() = width / height.toFloat()

    private suspend fun flexibleResize(
        image: Bitmap,
        max: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap {
        return kotlin.runCatching {
            if (image.height >= image.width) {
                val aspectRatio = image.width.toDouble() / image.height.toDouble()
                val targetWidth = (max * aspectRatio).toInt()
                createScaledBitmap(image, targetWidth, max, imageScaleMode)
            } else {
                val aspectRatio = image.height.toDouble() / image.width.toDouble()
                val targetHeight = (max * aspectRatio).toInt()
                createScaledBitmap(image, max, targetHeight, imageScaleMode)
            }
        }.getOrNull() ?: image
    }

    private suspend fun ResizeType.Limits.resizeWithLimits(
        image: Bitmap,
        width: Int,
        height: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap? {
        val limitWidth: Int
        val limitHeight: Int

        if (autoRotateLimitBox && image.aspectRatio < 1f) {
            limitWidth = height
            limitHeight = width
        } else {
            limitWidth = width
            limitHeight = height
        }
        val limitAspectRatio = limitWidth / limitHeight.toFloat()

        if (image.height > limitHeight || image.width > limitWidth) {
            if (image.aspectRatio > limitAspectRatio) {
                return scaleImage(
                    image = image,
                    width = limitWidth,
                    height = limitWidth,
                    resizeType = ResizeType.Flexible,
                    imageScaleMode = imageScaleMode
                )
            } else if (image.aspectRatio < limitAspectRatio) {
                return scaleImage(
                    image = image,
                    width = limitHeight,
                    height = limitHeight,
                    imageScaleMode = imageScaleMode
                )
            } else {
                return scaleImage(
                    image = image,
                    width = limitWidth,
                    height = limitHeight,
                    imageScaleMode = imageScaleMode
                )
            }
        } else {
            return when (this) {
                is ResizeType.Limits.Recode -> image

                is ResizeType.Limits.Zoom -> scaleImage(
                    image = image,
                    width = limitWidth,
                    height = limitHeight,
                    imageScaleMode = imageScaleMode
                )

                is ResizeType.Limits.Skip -> null
            }
        }
    }

}