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

package ru.tech.imageresizershrinker.feature.bytes_resize.data

import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.di.DefaultDispatcher
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.feature.bytes_resize.domain.BytesImageScaler
import javax.inject.Inject
import kotlin.math.roundToInt

internal class AndroidBytesImageScaler @Inject constructor(
    imageScaler: ImageScaler<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : BytesImageScaler<Bitmap>, ImageScaler<Bitmap> by imageScaler {

    override suspend fun scaleByMaxBytes(
        image: Bitmap,
        imageFormat: ImageFormat,
        imageScaleMode: ImageScaleMode,
        maxBytes: Long
    ): Pair<Bitmap, ImageInfo>? = withContext(dispatcher) {
        runCatching {
            if (
                imageCompressor.calculateImageSize(
                    image = image,
                    imageInfo = ImageInfo(
                        width = image.width,
                        height = image.height,
                        imageFormat = imageFormat
                    )
                ) > maxBytes
            ) {
                var outArray = ByteArray(maxBytes.toInt())
                var compressQuality: Int
                var lowQuality = 15
                var highQuality = 100
                var newSize = image.width to image.height

                while (lowQuality <= highQuality) {
                    compressQuality = (lowQuality + highQuality) / 2

                    outArray = imageCompressor.compressAndTransform(
                        image = image,
                        imageInfo = ImageInfo(
                            quality = if (imageFormat is ImageFormat.Jxl) {
                                Quality.Jxl(compressQuality)
                            } else Quality.Base(compressQuality),
                            imageFormat = imageFormat
                        )
                    )

                    if (outArray.size < maxBytes) {
                        lowQuality = compressQuality + 1
                    } else {
                        highQuality = compressQuality - 1
                    }
                }

                compressQuality = 15

                while (outArray.size > maxBytes) {
                    val temp = scaleImage(
                        image = image,
                        width = (newSize.first * 0.9f).roundToInt(),
                        height = (newSize.second * 0.9f).roundToInt(),
                        imageScaleMode = imageScaleMode
                    )
                    newSize = temp.width to temp.height

                    outArray = imageCompressor.compressAndTransform(
                        image = image,
                        imageInfo = ImageInfo(
                            quality = if (imageFormat is ImageFormat.Jxl) {
                                Quality.Jxl(compressQuality)
                            } else Quality.Base(compressQuality),
                            imageFormat = imageFormat,
                            width = newSize.first,
                            height = newSize.second
                        )
                    )
                }

                imageGetter.getImage(outArray)!! to compressQuality
            } else null
        }.getOrNull()?.let { (bitmap, compressQuality) ->
            bitmap to ImageInfo(
                width = bitmap.width,
                height = bitmap.height,
                quality = if (imageFormat is ImageFormat.Jxl) {
                    Quality.Jxl(compressQuality)
                } else Quality.Base(compressQuality)
            )
        }
    }


}