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

package com.t8rin.imagetoolbox.feature.weight_resize.data

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
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
        maxBytes: Long,
        saveIfSmaller: Boolean
    ): Pair<ByteArray, ImageInfo>? = withContext(defaultDispatcher) {
        runSuspendCatching {
            var imageInfo = ImageInfo(
                width = image.width,
                height = image.height,
                imageFormat = imageFormat,
                imageScaleMode = imageScaleMode,
                quality = Quality.Base().coerceIn(imageFormat)
            )
            var data = imageCompressor.compressAndTransform(image, imageInfo)
            val normalization = 2048 * (data.size.toLong() / (5 * 1024 * 1024)).coerceAtLeast(1)
            val targetSize = (maxBytes - normalization).coerceAtLeast(1024)

            if (data.size <= targetSize) {
                return@runSuspendCatching if (saveIfSmaller) data to imageInfo else null
            }

            if (imageFormat.canChangeCompressionValue) {
                for (quality in 99 downTo 15) {
                    val nextQuality = Quality.Base(quality).coerceIn(imageFormat)
                    if (nextQuality == imageInfo.quality) continue
                    ensureActive()
                    imageInfo = imageInfo.copy(quality = nextQuality)
                    data = imageCompressor.compressAndTransform(image, imageInfo)
                    if (data.size <= targetSize) return@runSuspendCatching data to imageInfo
                }
            }

            while (data.size > targetSize) {
                ensureActive()
                if (imageInfo.width == 1 && imageInfo.height == 1) {
                    return@runSuspendCatching null
                }
                imageInfo = imageInfo.copy(
                    width = imageInfo.width.shrink(),
                    height = imageInfo.height.shrink()
                )
                data = imageCompressor.compressAndTransform(image, imageInfo)
            }

            data to imageInfo
        }.getOrNull()
    }

    private fun Int.shrink(): Int = (this * 0.93f).roundToInt()
        .coerceAtMost(this - 1)
        .coerceAtLeast(1)
}