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

package com.t8rin.imagetoolbox.feature.weight_resize.data

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.feature.weight_resize.domain.WeightImageScaler
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject
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
        runSuspendCatching {
            val initialSize = imageCompressor.calculateImageSize(
                image = image,
                imageInfo = ImageInfo(
                    width = image.width,
                    height = image.height,
                    imageFormat = imageFormat
                )
            )
            val normalization = 2048 * (initialSize / (5 * 1024 * 1024)).coerceAtLeast(1)

            val targetSize = (maxBytes - normalization).coerceAtLeast(1024)

            if (initialSize > targetSize) {
                var outArray = ByteArray(initialSize.toInt())
                var compressQuality = 100
                var newSize = image.size()

                if (imageFormat.canChangeCompressionValue) {
                    while (outArray.size > targetSize) {
                        ensureActive()
                        outArray = imageCompressor.compressAndTransform(
                            image = image,
                            imageInfo = ImageInfo(
                                width = newSize.width,
                                height = newSize.height,
                                quality = Quality.Base(compressQuality),
                                imageFormat = imageFormat
                            )
                        )
                        compressQuality -= 1

                        if (compressQuality < 15) break
                    }

                    compressQuality = 15
                }

                while (outArray.size > targetSize) {
                    ensureActive()

                    newSize = scaleImage(
                        image = image,
                        width = (newSize.width * 0.93f).roundToInt(),
                        height = (newSize.height * 0.93f).roundToInt(),
                        imageScaleMode = imageScaleMode
                    ).size()

                    outArray = imageCompressor.compressAndTransform(
                        image = image,
                        imageInfo = ImageInfo(
                            quality = Quality.Base(compressQuality),
                            imageFormat = imageFormat,
                            width = newSize.width,
                            height = newSize.height
                        )
                    )
                }

                outArray to ImageInfo(
                    width = newSize.width,
                    height = newSize.height,
                    imageFormat = imageFormat
                )
            } else null
        }.getOrNull()
    }

    private fun Bitmap.size(): IntegerSize = IntegerSize(width, height)
}