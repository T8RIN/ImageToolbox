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

package ru.tech.imageresizershrinker.feature.apng_tools.data

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import oupson.apng.decoder.ApngDecoder
import oupson.apng.encoder.ApngEncoder
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.core.domain.model.ResizeType
import ru.tech.imageresizershrinker.feature.apng_tools.domain.ApngConverter
import ru.tech.imageresizershrinker.feature.apng_tools.domain.ApngParams
import java.io.ByteArrayOutputStream
import javax.inject.Inject


internal class AndroidApngConverter @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageShareProvider: ShareProvider<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    @ApplicationContext private val context: Context
) : ApngConverter {

    override fun extractFramesFromApng(
        apngUri: String,
        imageFormat: ImageFormat,
        quality: Quality
    ): Flow<String> = channelFlow {
        ApngDecoder(
            context = context,
            uri = apngUri.toUri()
        ).decodeAsync(Dispatchers.IO) { frame ->
            if (!currentCoroutineContext().isActive) {
                currentCoroutineContext().cancel(null)
                return@decodeAsync
            }
            imageShareProvider.cacheImage(
                image = frame,
                imageInfo = ImageInfo(
                    width = frame.width,
                    height = frame.height,
                    imageFormat = imageFormat,
                    quality = quality
                ),
                name = "apng_image"
            )?.let { send(it) }
        }
    }

    override suspend fun createApngFromImageUris(
        imageUris: List<String>,
        params: ApngParams,
        onProgress: () -> Unit
    ): ByteArray = withContext(Dispatchers.IO) {
        val out = ByteArrayOutputStream()
        val size = params.size ?: imageGetter.getImage(data = imageUris[0])!!.run {
            IntegerSize(width, height)
        }

        val encoder = ApngEncoder(
            outputStream = out,
            width = size.width,
            height = size.height,
            numberOfFrames = imageUris.size
        ).apply {
            setOptimiseApng(false)
            setRepetitionCount(params.repeatCount)
            setCompressionLevel(params.quality.qualityValue)
        }
        imageUris.forEach { uri ->
            imageGetter.getImage(
                data = uri,
                size = size
            )?.let {
                encoder.writeFrame(
                    btm = imageScaler.scaleImage(
                        image = it,
                        width = size.width,
                        height = size.height,
                        resizeType = ResizeType.CenterCrop(
                            canvasColor = Color.Transparent.toArgb()
                        )
                    ),
                    delay = params.delay.toFloat()
                )
            }
            onProgress()
        }
        encoder.writeEnd()

        out.toByteArray()
    }


}