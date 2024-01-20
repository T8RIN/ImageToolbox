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

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.image.filters.StackBlurFilter
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageManager
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.domain.image.filters.provider.FilterProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageData
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Preset
import ru.tech.imageresizershrinker.core.domain.model.ResizeType
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import coil.transform.Transformation as CoilTransformation


internal class AndroidImageManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    private val filterProvider: FilterProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>
) : ImageManager<Bitmap, ExifInterface> {

    override fun getFilterProvider(): FilterProvider<Bitmap> = filterProvider

    private fun toCoil(transformation: Transformation<Bitmap>): CoilTransformation {
        return object : CoilTransformation {
            override val cacheKey: String
                get() = transformation.cacheKey

            override suspend fun transform(
                input: Bitmap,
                size: Size
            ): Bitmap = transformation.transform(input, size)
        }
    }

    override suspend fun transform(
        image: Bitmap,
        transformations: List<Transformation<Bitmap>>,
        originalSize: Boolean
    ): Bitmap? = withContext(Dispatchers.IO) {
        val request = ImageRequest
            .Builder(context)
            .data(image)
            .transformations(
                transformations.map(::toCoil)
            )
            .apply {
                if (originalSize) size(Size.ORIGINAL)
            }
            .build()

        return@withContext imageLoader.execute(request).drawable?.toBitmap()
    }

    override suspend fun filter(
        image: Bitmap,
        filters: List<Filter<Bitmap, *>>,
        originalSize: Boolean
    ): Bitmap? = transform(
        image = image,
        transformations = filters.map { filterProvider.filterToTransformation(it) },
        originalSize = originalSize
    )

    override suspend fun transform(
        image: Bitmap,
        transformations: List<Transformation<Bitmap>>,
        size: IntegerSize
    ): Bitmap? = withContext(Dispatchers.IO) {
        val request = ImageRequest
            .Builder(context)
            .data(image)
            .transformations(
                transformations.map(::toCoil)
            )
            .size(size.width, size.height)
            .build()

        return@withContext imageLoader.execute(request).drawable?.toBitmap()
    }

    override suspend fun filter(
        image: Bitmap,
        filters: List<Filter<Bitmap, *>>,
        size: IntegerSize
    ): Bitmap? = transform(
        image = image,
        transformations = filters.map { filterProvider.filterToTransformation(it) },
        size = size
    )

    override suspend fun createFilteredPreview(
        image: Bitmap,
        imageInfo: ImageInfo,
        filters: List<Filter<Bitmap, *>>,
        onGetByteCount: (Int) -> Unit
    ): Bitmap = createPreview(
        image = image,
        imageInfo = imageInfo,
        transformations = filters.map { filterProvider.filterToTransformation(it) },
        onGetByteCount = onGetByteCount
    )

    override suspend fun resize(
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
                image.createScaledBitmap(
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

    private suspend fun flexibleResize(
        image: Bitmap,
        max: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap {
        return kotlin.runCatching {
            if (image.height >= image.width) {
                val aspectRatio = image.width.toDouble() / image.height.toDouble()
                val targetWidth = (max * aspectRatio).toInt()
                image.createScaledBitmap(targetWidth, max, imageScaleMode)
            } else {
                val aspectRatio = image.height.toDouble() / image.width.toDouble()
                val targetHeight = (max * aspectRatio).toInt()
                image.createScaledBitmap(max, targetHeight, imageScaleMode)
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
                return resize(
                    image = image,
                    width = limitWidth,
                    height = limitWidth,
                    resizeType = ResizeType.Flexible,
                    imageScaleMode = imageScaleMode
                )
            } else if (image.aspectRatio < limitAspectRatio) {
                return resize(
                    image = image,
                    width = limitHeight,
                    height = limitHeight,
                    resizeType = ResizeType.Flexible,
                    imageScaleMode = imageScaleMode
                )
            } else {
                return resize(
                    image = image,
                    width = limitWidth,
                    height = limitHeight,
                    resizeType = ResizeType.Flexible,
                    imageScaleMode = imageScaleMode
                )
            }
        } else {
            return when (this) {
                is ResizeType.Limits.Recode -> image

                is ResizeType.Limits.Zoom -> resize(
                    image = image,
                    width = limitWidth,
                    height = limitHeight,
                    resizeType = ResizeType.Flexible,
                    imageScaleMode = imageScaleMode
                )

                is ResizeType.Limits.Skip -> null
            }
        }
    }

    private val Bitmap.aspectRatio: Float get() = width / height.toFloat()

    override fun applyPresetBy(image: Bitmap?, preset: Preset, currentInfo: ImageInfo): ImageInfo {
        if (image == null) return currentInfo


        val rotated = abs(currentInfo.rotationDegrees) % 180 != 0f
        fun Bitmap.width() = if (rotated) height else width
        fun Bitmap.height() = if (rotated) width else height
        fun Int.calc(cnt: Int): Int = (this * (cnt / 100f)).toInt()

        return when (preset) {
            is Preset.Telegram -> {
                currentInfo.copy(
                    width = 512,
                    height = 512,
                    imageFormat = ImageFormat.Png,
                    resizeType = ResizeType.Flexible,
                    quality = 100f
                )
            }

            is Preset.Numeric -> currentInfo.copy(
                quality = preset.value.toFloat(),
                width = image.width().calc(preset.value),
                height = image.height().calc(preset.value),
            )

            is Preset.None -> currentInfo
        }
    }

    override suspend fun trimEmptyParts(image: Bitmap): Bitmap = BackgroundRemover.trim(image)

    override fun removeBackgroundFromImage(
        image: Bitmap,
        onSuccess: (Bitmap) -> Unit,
        onFailure: (Throwable) -> Unit,
        trimEmptyParts: Boolean
    ) {
        kotlin.runCatching {
            BackgroundRemover.bitmapForProcessing(
                bitmap = image,
                scope = CoroutineScope(Dispatchers.IO)
            ) { result ->
                if (result.isSuccess) {
                    result.getOrNull()?.let {
                        if (trimEmptyParts) trimEmptyParts(it)
                        else it
                    }?.let(onSuccess)
                } else result.exceptionOrNull()?.let(onFailure)
            }
        }.exceptionOrNull()?.let(onFailure)
    }

    override suspend fun scaleByMaxBytes(
        image: Bitmap,
        imageFormat: ImageFormat,
        imageScaleMode: ImageScaleMode,
        maxBytes: Long
    ): ImageData<Bitmap, ExifInterface>? = withContext(Dispatchers.IO) {
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
                calculateImageSize(
                    imageData = ImageData(
                        image = image,
                        imageInfo = ImageInfo(imageFormat = imageFormat)
                    )
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
                        compress(
                            ImageData(
                                image = image,
                                imageInfo = ImageInfo(
                                    quality = compressQuality.toFloat(),
                                    imageFormat = imageFormat
                                )
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
                        val temp = resize(
                            image = image,
                            width = (newSize.first * 0.98).toInt(),
                            height = (newSize.second * 0.98).toInt(),
                            resizeType = ResizeType.Explicit,
                            imageScaleMode = imageScaleMode
                        )
                        bmpStream.write(
                            temp?.let {
                                newSize = it.width to it.height
                                compress(
                                    ImageData(
                                        image = image,
                                        imageInfo = ImageInfo(
                                            quality = compressQuality.toFloat(),
                                            imageFormat = imageFormat,
                                            width = newSize.first,
                                            height = newSize.second
                                        )
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
            ImageData(
                it.first,
                imageInfo = ImageInfo(
                    width = it.first.width,
                    height = it.first.height,
                    quality = it.second.toFloat()
                )
            )
        }
    }

    override fun canShow(image: Bitmap): Boolean {
        return canShow(image.size())
    }

    private fun canShow(size: Int): Boolean {
        return size < 3096 * 3096 * 3
    }

    override suspend fun calculateImageSize(imageData: ImageData<Bitmap, ExifInterface>): Long {
        return compress(imageData).size.toLong()
    }

    override suspend fun compress(
        imageData: ImageData<Bitmap, ExifInterface>,
        onImageReadyToCompressInterceptor: suspend (Bitmap) -> Bitmap,
        applyImageTransformations: Boolean
    ): ByteArray = withContext(Dispatchers.IO) {
        val currentImage: Bitmap
        if (applyImageTransformations) {
            currentImage = resize(
                image = rotate(
                    image = imageData.image.apply { setHasAlpha(true) },
                    degrees = imageData.imageInfo.rotationDegrees
                ),
                width = imageData.imageInfo.width,
                height = imageData.imageInfo.height,
                resizeType = imageData.imageInfo.resizeType,
                imageScaleMode = imageData.imageInfo.imageScaleMode
            )?.let {
                flip(
                    image = it,
                    isFlipped = imageData.imageInfo.isFlipped
                )
            }?.let {
                onImageReadyToCompressInterceptor(it)
            } ?: return@withContext ByteArray(0)
        } else currentImage = onImageReadyToCompressInterceptor(imageData.image)

        return@withContext runCatching {
            imageCompressor.compress(
                image = currentImage,
                imageFormat = imageData.imageInfo.imageFormat,
                quality = imageData.imageInfo.quality
            )
        }.getOrNull() ?: ByteArray(0)
    }

    override fun flip(image: Bitmap, isFlipped: Boolean): Bitmap {
        return if (isFlipped) {
            val matrix = Matrix().apply { postScale(-1f, 1f, image.width / 2f, image.height / 2f) }
            return Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        } else image
    }

    override fun rotate(image: Bitmap, degrees: Float): Bitmap {
        //TODO: Add free rotation( maybe :) )
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
    }

    private suspend fun compressCenterCrop(
        scaleFactor: Float,
        onImageReadyToCompressInterceptor: suspend (Bitmap) -> Bitmap,
        imageData: ImageData<Bitmap, ExifInterface>
    ): ByteArray = withContext(Dispatchers.IO) {

        val currentImage: Bitmap =
            (imageData.imageInfo.resizeType as? ResizeType.CenterCrop)?.resizeWithCenterCrop(
                image = rotate(
                    image = imageData.image.apply { setHasAlpha(true) },
                    degrees = imageData.imageInfo.rotationDegrees
                ),
                w = imageData.imageInfo.width,
                h = imageData.imageInfo.height,
                scaleFactor = scaleFactor,
                imageScaleMode = imageData.imageInfo.imageScaleMode
            )?.let {
                flip(
                    image = it,
                    isFlipped = imageData.imageInfo.isFlipped
                )
            }?.let {
                onImageReadyToCompressInterceptor(it)
            } ?: return@withContext ByteArray(0)

        return@withContext runCatching {
            imageCompressor.compress(
                image = currentImage,
                imageFormat = imageData.imageInfo.imageFormat,
                quality = imageData.imageInfo.quality
            )
        }.getOrNull() ?: ByteArray(0)
    }

    override suspend fun createPreview(
        image: Bitmap,
        imageInfo: ImageInfo,
        transformations: List<Transformation<Bitmap>>,
        onGetByteCount: (Int) -> Unit
    ): Bitmap = withContext(Dispatchers.IO) {
        if (imageInfo.height == 0 || imageInfo.width == 0) return@withContext image
        var width = imageInfo.width
        var height = imageInfo.height

        launch {
            if (imageInfo.resizeType !is ResizeType.CenterCrop) {
                compress(
                    imageData = ImageData(
                        image = image,
                        imageInfo = imageInfo
                    ),
                    onImageReadyToCompressInterceptor = {
                        transform(
                            image = it,
                            transformations = transformations
                        ) ?: it
                    }
                )
            } else {
                compressCenterCrop(
                    scaleFactor = 1f,
                    onImageReadyToCompressInterceptor = {
                        transform(
                            image = it,
                            transformations = transformations
                        ) ?: it
                    },
                    imageData = ImageData(
                        image = image,
                        imageInfo = imageInfo
                    )
                )
            }.let { onGetByteCount(it.size) }
        }

        var scaleFactor = 1f
        while (!canShow(size = height * width * 4)) {
            height = (height * 0.85f).roundToInt()
            width = (width * 0.85f).roundToInt()
            scaleFactor *= 0.85f
        }

        val bytes = if (imageInfo.resizeType !is ResizeType.CenterCrop) {
            compress(
                imageData = ImageData(
                    image = image,
                    imageInfo = imageInfo.copy(
                        width = width,
                        height = height
                    )
                ),
                onImageReadyToCompressInterceptor = {
                    transform(
                        image = it,
                        transformations = transformations
                    ) ?: it
                }
            )
        } else {
            compressCenterCrop(
                scaleFactor = scaleFactor,
                onImageReadyToCompressInterceptor = {
                    transform(
                        image = it,
                        transformations = transformations
                    ) ?: it
                },
                imageData = ImageData(
                    image = image,
                    imageInfo = imageInfo.copy(
                        width = width,
                        height = height
                    )
                )
            )
        }
        val bitmap = imageLoader.execute(
            ImageRequest.Builder(context).data(bytes).build()
        ).drawable?.toBitmap()
        return@withContext bitmap ?: image
    }

    private fun Drawable.toBitmap(): Bitmap {
        val drawable = this
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                getSuitableConfig()
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                getSuitableConfig()
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private suspend fun ResizeType.CenterCrop.resizeWithCenterCrop(
        image: Bitmap,
        w: Int,
        h: Int,
        scaleFactor: Float = 1f,
        imageScaleMode: ImageScaleMode
    ): Bitmap {
        if (w == image.width && h == image.height) return image
        val bitmap = transform(
            image = image.let {
                val xScale: Float = w.toFloat() / it.width
                val yScale: Float = h.toFloat() / it.height
                val scale = xScale.coerceAtLeast(yScale)
                it.createScaledBitmap(
                    width = (scale * it.width).toInt(),
                    height = (scale * it.height).toInt(),
                    imageScaleMode = imageScaleMode
                )
            },
            transformations = listOf(
                StackBlurFilter(
                    value = 0.5f to blurRadius
                )
            )
        )
        val drawImage = image.createScaledBitmap(
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

    @TargetApi(Build.VERSION_CODES.O)
    private fun Bitmap.size(): Int {
        return width * height * when (config) {
            Bitmap.Config.RGB_565 -> 2
            Bitmap.Config.RGBA_F16 -> 8
            else -> 4
        }
    }

    private fun getSuitableConfig(
        image: Bitmap? = null
    ): Bitmap.Config = image?.config ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Bitmap.Config.RGBA_1010102
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Bitmap.Config.RGBA_F16
    } else {
        Bitmap.Config.ARGB_8888
    }

    private suspend fun Bitmap.createScaledBitmap(
        width: Int,
        height: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap = imageScaler.scaleImage(
        image = this,
        width = width,
        height = height,
        imageScaleMode = imageScaleMode
    )

}