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

package com.t8rin.imagetoolbox.core.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.size.Size
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.utils.aspectRatio
import com.t8rin.imagetoolbox.core.data.utils.toCoil
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.sizeTo
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


internal class AndroidImageTransformer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    defaultDispatchersHolder: DispatchersHolder
) : DispatchersHolder by defaultDispatchersHolder, ImageTransformer<Bitmap> {

    override suspend fun transform(
        image: Bitmap,
        transformations: List<Transformation<Bitmap>>,
        originalSize: Boolean
    ): Bitmap? = withContext(defaultDispatcher) {
        if (transformations.isEmpty()) return@withContext image

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

        return@withContext imageLoader.execute(request).image?.toBitmap()
    }

    override suspend fun transform(
        image: Bitmap,
        transformations: List<Transformation<Bitmap>>,
        size: IntegerSize
    ): Bitmap? = withContext(defaultDispatcher) {
        if (transformations.isEmpty()) return@withContext image

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

        return@withContext imageLoader.execute(request).image?.toBitmap()
    }

    override suspend fun applyPresetBy(
        image: Bitmap?,
        preset: Preset,
        currentInfo: ImageInfo
    ): ImageInfo = withContext(defaultDispatcher) {
        if (image == null || preset is Preset.None) return@withContext currentInfo

        val size = currentInfo.originalUri?.let { uri ->
            imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(uri)
                    .size(Size.ORIGINAL)
                    .build()
            ).image?.takeIf {
                (it.aspectRatio - image.aspectRatio).absoluteValue <= 0.001
            }?.run { width sizeTo height }
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
                    imageFormat = ImageFormat.Png.Lossless,
                    resizeType = ResizeType.Flexible,
                    quality = Quality.Base(100)
                )
            }

            is Preset.Percentage -> currentInfo.copy(
                width = calcWidth().calc(preset.value),
                height = calcHeight().calc(preset.value),
            )

            is Preset.AspectRatio -> {
                val originalWidth = calcWidth().toFloat()
                val originalHeight = calcHeight().toFloat()

                val newWidth: Float
                val newHeight: Float

                val condition = if (preset.isFit) preset.ratio > originalWidth / originalHeight
                else preset.ratio < originalWidth / originalHeight

                if (condition) {
                    newWidth = originalHeight * preset.ratio
                    newHeight = originalHeight
                } else {
                    newWidth = originalWidth
                    newHeight = originalWidth / preset.ratio
                }

                currentInfo.copy(
                    width = newWidth.roundToInt(),
                    height = newHeight.roundToInt()
                )
            }

            Preset.None -> currentInfo
        }
    }

    override suspend fun flip(
        image: Bitmap,
        isFlipped: Boolean
    ): Bitmap = withContext(defaultDispatcher) {
        if (isFlipped) {
            val matrix = Matrix().apply { postScale(-1f, 1f, image.width / 2f, image.height / 2f) }
            Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        } else image
    }

    override suspend fun rotate(
        image: Bitmap,
        degrees: Float
    ): Bitmap = withContext(defaultDispatcher) {
        if (degrees == 0f) return@withContext image

        if (degrees % 90 == 0f) {
            val matrix = Matrix().apply { postRotate(degrees) }
            Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        } else {
            val matrix = Matrix().apply {
                setRotate(degrees, image.width.toFloat() / 2, image.height.toFloat() / 2)
            }
            Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        }
    }

}