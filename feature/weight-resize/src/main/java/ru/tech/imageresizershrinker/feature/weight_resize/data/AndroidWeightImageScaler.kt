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

package ru.tech.imageresizershrinker.feature.weight_resize.data

import android.graphics.Bitmap
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.feature.weight_resize.domain.WeightImageScaler
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

internal class AndroidWeightImageScaler @Inject constructor(
    imageScaler: ImageScaler<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder,
    ImageScaler<Bitmap> by imageScaler,
    WeightImageScaler<Bitmap> {

    override suspend fun scaleByMaxBytes(
        image: Bitmap,
        imageFormat: ImageFormat,
        imageScaleMode: ImageScaleMode,
        maxBytes: Long
    ): Pair<ByteArray, ImageInfo>? = withContext(defaultDispatcher) {
        runCatching {
            val targetSize = maxBytes - 2048
            var initialSize: Long
            if (
                imageCompressor.calculateImageSize(
                    image = image,
                    imageInfo = ImageInfo(
                        width = image.width,
                        height = image.height,
                        imageFormat = imageFormat
                    )
                ).also { initialSize = it } > targetSize
            ) {
                var outArray = ByteArray(initialSize.toInt())
                var compressQuality = 100
                var newSize = image.width to image.height

                if (imageFormat.canChangeCompressionValue) {
                    while (outArray.size > targetSize) {
                        ensureActive()
                        outArray = imageCompressor.compressAndTransform(
                            image = image,
                            imageInfo = ImageInfo(
                                width = newSize.first,
                                height = newSize.second,
                                quality = Quality.Base(compressQuality),
                                imageFormat = imageFormat
                            )
                        )
                        compressQuality -= 1

                        if (compressQuality < 15) break
                    }

                    compressQuality = 15
                }

                while (abs(outArray.size - targetSize) > 2048) {
                    ensureActive()
                    val temp = if (outArray.size > targetSize) {
                        scaleImage(
                            image = image,
                            width = (newSize.first * 0.9f).roundToInt(),
                            height = (newSize.second * 0.9f).roundToInt(),
                            imageScaleMode = imageScaleMode
                        )
                    } else {
                        scaleImage(
                            image = image,
                            width = (newSize.first * 1.01f).roundToInt(),
                            height = (newSize.second * 1.01f).roundToInt(),
                            imageScaleMode = imageScaleMode
                        )
                    }
                    newSize = temp.width to temp.height

                    outArray = imageCompressor.compressAndTransform(
                        image = image,
                        imageInfo = ImageInfo(
                            quality = Quality.Base(compressQuality),
                            imageFormat = imageFormat,
                            width = newSize.first,
                            height = newSize.second
                        )
                    )
                }

                outArray to ImageInfo(
                    width = newSize.first,
                    height = newSize.second,
                    imageFormat = imageFormat
                )
            } else null
        }.getOrNull()
    }


}