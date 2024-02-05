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

package ru.tech.imageresizershrinker.feature.gif_tools.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.feature.gif_tools.domain.GifConverter
import ru.tech.imageresizershrinker.feature.gif_tools.domain.GifFrames
import ru.tech.imageresizershrinker.feature.gif_tools.domain.GifParams
import java.io.ByteArrayOutputStream
import javax.inject.Inject


internal class AndroidGifConverter @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageShareProvider: ShareProvider<Bitmap>,
    @ApplicationContext private val context: Context
) : GifConverter {

    override fun extractFramesFromGif(
        gifUri: String,
        imageFormat: ImageFormat,
        gifFrames: GifFrames,
        quality: Quality,
        onGetFramesCount: (frames: Int) -> Unit
    ): Flow<String> = flow {
        val bytes = runCatching {
            context.contentResolver.openInputStream(gifUri.toUri())?.use {
                it.readBytes()
            }
        }.getOrNull()
        val decoder = GifDecoder().apply {
            read(bytes)
        }
        onGetFramesCount(decoder.frameCount)
        val indexes = gifFrames
            .getFramePositions(decoder.frameCount)
            .map { it - 1 }
        repeat(decoder.frameCount) { pos ->
            if (!currentCoroutineContext().isActive) {
                currentCoroutineContext().cancel(null)
                return@repeat
            }
            decoder.advance()
            decoder.nextFrame?.let { frame ->
                imageShareProvider.cacheImage(
                    image = frame,
                    imageInfo = ImageInfo(
                        width = frame.width,
                        height = frame.height,
                        imageFormat = imageFormat,
                        quality = quality
                    ),
                    name = "gif_image"
                )
            }?.takeIf {
                pos in indexes
            }?.let { emit(it) }
        }
    }

    override suspend fun createGifFromImageUris(
        imageUris: List<String>,
        params: GifParams,
        onProgress: () -> Unit
    ): ByteArray = withContext(Dispatchers.IO) {
        val out = ByteArrayOutputStream()
        val encoder = GifEncoder().apply {
            params.size?.let { size ->
                width = size.width
                height = size.height
            }
            repeat = params.repeatCount
            delay = params.delay
            setQuality(
                (100 - ((params.quality.qualityValue - 1) * (100 / 19f))).toInt()
            )
            setFrameRate(params.fps.toFloat())
            start(out)
        }
        imageUris.forEach { uri ->
            imageGetter.getImage(
                data = uri,
                size = params.size
            )?.let(encoder::addFrame)
            onProgress()
        }
        encoder.finish()

        out.toByteArray()
    }


}