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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.utils.toCoil
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Preset
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.core.domain.model.ResizeType
import ru.tech.imageresizershrinker.core.domain.model.sizeTo
import javax.inject.Inject
import kotlin.math.abs


internal class AndroidImageTransformer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader
) : ImageTransformer<Bitmap> {

    override suspend fun transform(
        image: Bitmap,
        transformations: List<Transformation<Bitmap>>,
        originalSize: Boolean
    ): Bitmap? = withContext(Dispatchers.IO) {
        val request = ImageRequest
            .Builder(context)
            .data(image)
            .transformations(
                transformations.map {
                    it.toCoil()
                }
            )
            .apply {
                if (originalSize) size(Size.ORIGINAL)
            }
            .build()

        return@withContext imageLoader.execute(request).drawable?.toBitmap()
    }

    override suspend fun transform(
        image: Bitmap,
        transformations: List<Transformation<Bitmap>>,
        size: IntegerSize
    ): Bitmap? = withContext(Dispatchers.IO) {
        val request = ImageRequest
            .Builder(context)
            .data(image)
            .transformations(
                transformations.map {
                    it.toCoil()
                }
            )
            .size(size.width, size.height)
            .build()

        return@withContext imageLoader.execute(request).drawable?.toBitmap()
    }

    override suspend fun applyPresetBy(
        image: Bitmap?,
        preset: Preset,
        currentInfo: ImageInfo
    ): ImageInfo = withContext(Dispatchers.IO) {
        if (image == null) return@withContext currentInfo

        val size = currentInfo.originalUri?.let {
            imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(it)
                    .size(Size.ORIGINAL)
                    .build()
            ).drawable?.run { intrinsicWidth sizeTo intrinsicHeight }
        } ?: IntegerSize(image.width, image.height)

        val rotated = abs(currentInfo.rotationDegrees) % 180 != 0f
        fun calcWidth() = if (rotated) size.height else size.width
        fun calcHeight() = if (rotated) size.width else size.height
        fun Int.calc(cnt: Int): Int = (this * (cnt / 100f)).toInt()

        when (preset) {
            is Preset.Telegram -> {
                currentInfo.copy(
                    width = 512,
                    height = 512,
                    imageFormat = ImageFormat.PngLossless,
                    resizeType = ResizeType.Flexible,
                    quality = Quality.Base(100)
                )
            }

            is Preset.Numeric -> currentInfo.copy(
                quality = when (val quality = currentInfo.quality) {
                    is Quality.Base -> quality.copy(qualityValue = preset.value)
                    is Quality.Jxl -> quality.copy(qualityValue = preset.value)
                    else -> quality
                },
                width = calcWidth().calc(preset.value),
                height = calcHeight().calc(preset.value),
            )

            is Preset.None -> currentInfo
        }
    }

    override suspend fun flip(
        image: Bitmap,
        isFlipped: Boolean
    ): Bitmap {
        return if (isFlipped) {
            val matrix = Matrix().apply { postScale(-1f, 1f, image.width / 2f, image.height / 2f) }
            return Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        } else image
    }

    override suspend fun rotate(
        image: Bitmap,
        degrees: Float
    ): Bitmap {
        return if (degrees % 90 == 0f) {
            val matrix = Matrix().apply { postRotate(degrees) }
            Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        } else {
            val matrix = Matrix().apply {
                setRotate(degrees, image.width.toFloat() / 2, image.height.toFloat() / 2)
            }
            Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        }
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

    private fun getSuitableConfig(
        image: Bitmap? = null
    ): Bitmap.Config = image?.config ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Bitmap.Config.RGBA_1010102
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Bitmap.Config.RGBA_F16
    } else {
        Bitmap.Config.ARGB_8888
    }

}