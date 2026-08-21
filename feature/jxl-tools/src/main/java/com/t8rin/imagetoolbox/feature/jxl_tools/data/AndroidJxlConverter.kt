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

package com.t8rin.imagetoolbox.feature.jxl_tools.data

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import com.awxkee.jxlcoderlibjxl.JxlAnimatedEncoder
import com.awxkee.jxlcoderlibjxl.JxlAnimatedImage
import com.awxkee.jxlcoderlibjxl.JxlChannelsConfiguration
import com.awxkee.jxlcoderlibjxl.JxlCoder
import com.awxkee.jxlcoderlibjxl.JxlCompressionOption
import com.awxkee.jxlcoderlibjxl.JxlDecodingSpeed
import com.t8rin.awebp.encoder.AnimatedWebpEncoder
import com.t8rin.gif_converter.GifEncoder
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFrames
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.feature.jxl_tools.domain.AnimatedJxlParams
import com.t8rin.imagetoolbox.feature.jxl_tools.domain.JxlConverter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import oupson.apng.encoder.ApngEncoder
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.CRC32
import javax.inject.Inject

private const val MIN_FRAME_DELAY_MILLIS = 10


internal class AndroidJxlConverter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageShareProvider: ImageShareProvider<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, JxlConverter {

    override suspend fun jpegToJxl(
        jpegUris: List<String>,
        onFailure: (Throwable) -> Unit,
        onProgress: suspend (String, ByteArray) -> Unit
    ) = withContext(defaultDispatcher) {
        jpegUris.forEach { uri ->
            runSuspendCatching {
                uri.jxl?.let { onProgress(uri, it) }
            }.onFailure(onFailure)
        }
    }

    override suspend fun jxlToJpeg(
        jxlUris: List<String>,
        onFailure: (Throwable) -> Unit,
        onProgress: suspend (String, ByteArray) -> Unit
    ) = withContext(defaultDispatcher) {
        jxlUris.forEach { uri ->
            runSuspendCatching {
                uri.jpeg?.let { onProgress(uri, it) }
            }.onFailure(onFailure)
        }
    }

    override suspend fun createJxlAnimation(
        imageUris: List<String>,
        params: AnimatedJxlParams,
        onFailure: (Throwable) -> Unit,
        onProgress: () -> Unit
    ): ByteArray? = withContext(defaultDispatcher) {
        val jxlQuality = params.quality as? Quality.Jxl

        if (jxlQuality == null) {
            onFailure(IllegalArgumentException("Quality Must be Jxl"))
            return@withContext null
        }

        runSuspendCatching {
            val size = params.size ?: imageGetter.getImage(data = imageUris[0])!!.run {
                IntegerSize(width, height)
            }
            if (size.width <= 0 || size.height <= 0) {
                onFailure(IllegalArgumentException("Width and height must be > 0"))
                return@withContext null
            }

            val (quality, compressionOption) = if (params.isLossy) {
                params.quality.qualityValue to JxlCompressionOption.LOSSY
            } else 100 to JxlCompressionOption.LOSSLESS

            val encoder = JxlAnimatedEncoder(
                width = size.width,
                height = size.height,
                numLoops = params.repeatCount,
                channelsConfiguration = when (params.quality.channels) {
                    Quality.Channels.RGBA -> JxlChannelsConfiguration.RGBA
                    Quality.Channels.RGB -> JxlChannelsConfiguration.RGB
                    Quality.Channels.Monochrome -> JxlChannelsConfiguration.MONOCHROME
                },
                compressionOption = compressionOption,
                effort = params.quality.effort.coerceAtLeast(1),
                quality = quality,
                decodingSpeed = JxlDecodingSpeed.entries.first { it.ordinal == params.quality.speed }
            )
            imageUris.forEach { uri ->
                imageGetter.getImage(
                    data = uri,
                    size = params.size
                )?.let {
                    encoder.addFrame(
                        bitmap = imageScaler.scaleImage(
                            image = imageScaler.scaleImage(
                                image = it,
                                width = size.width,
                                height = size.height,
                                resizeType = ResizeType.Flexible
                            ),
                            width = size.width,
                            height = size.height,
                            resizeType = ResizeType.CenterCrop(
                                canvasColor = Color.Transparent.toArgb()
                            )
                        ).apply {
                            setHasAlpha(true)
                        },
                        duration = params.delay
                    )
                }
                onProgress()
            }
            encoder.encode()
        }.onFailure {
            onFailure(it)
            return@withContext null
        }.getOrNull()
    }

    override fun extractFramesFromJxl(
        jxlUri: String,
        imageFormat: ImageFormat,
        imageFrames: ImageFrames,
        quality: Quality,
        onFailure: (Throwable) -> Unit,
        onGetFramesCount: (frames: Int) -> Unit
    ): Flow<String> = flow {
        val bytes = jxlUri.bytes ?: return@flow

        val decoder = JxlAnimatedImage(bytes)

        onGetFramesCount(decoder.numberOfFrames)
        val indexes = imageFrames
            .getFramePositions(decoder.numberOfFrames)
            .map { it - 1 }
        repeat(decoder.numberOfFrames) { pos ->
            if (!currentCoroutineContext().isActive) {
                currentCoroutineContext().cancel(null)
                return@repeat
            }
            decoder.getFrame(pos).let { frame ->
                imageShareProvider.cacheImage(
                    image = frame,
                    imageInfo = ImageInfo(
                        width = frame.width,
                        height = frame.height,
                        imageFormat = imageFormat,
                        quality = quality
                    )
                )
            }?.takeIf {
                pos in indexes
            }?.let { emit(it) }
        }
    }.catch {
        onFailure(it)
    }

    override suspend fun convertJxlToGif(
        jxlUris: List<String>,
        quality: Quality.Base,
        onProgress: suspend (String, ByteArray) -> Unit
    ) = withContext(defaultDispatcher) {
        jxlUris.forEach { uri ->
            runSuspendCatching {
                val output = ByteArrayOutputStream()
                JxlAnimatedImage(requireNotNull(uri.bytes)).use { decoder ->
                    val encoder = GifEncoder()
                        .setRepeat(decoder.loopsCount)
                        .setQuality(quality.qualityValue)
                        .setDispose(2)
                        .setTransparent(Color.Transparent.toArgb())
                    require(decoder.numberOfFrames > 0)
                    repeat(decoder.numberOfFrames) { index ->
                        currentCoroutineContext().ensureActive()
                        val frame = decoder.getFrame(index)
                        if (index == 0) {
                            encoder.setSize(frame.width, frame.height)
                            require(encoder.start(output))
                        }
                        encoder
                            .setDelay(
                                decoder.getFrameDuration(index)
                                    .coerceAtLeast(MIN_FRAME_DELAY_MILLIS)
                            )
                            .addFrame(frame)
                        frame.recycle()
                    }
                    require(encoder.finish())
                }
                onProgress(uri, output.toByteArray())
            }
        }
    }

    override suspend fun convertJxlToApng(
        jxlUris: List<String>,
        onProgress: suspend (String, ByteArray) -> Unit
    ) = withContext(defaultDispatcher) {
        jxlUris.forEach { uri ->
            runSuspendCatching {
                val output = ByteArrayOutputStream()
                var loopCount = 0
                JxlAnimatedImage(requireNotNull(uri.bytes)).use { decoder ->
                    require(decoder.numberOfFrames > 0)
                    loopCount = decoder.loopsCount
                    var encoder: ApngEncoder? = null
                    repeat(decoder.numberOfFrames) { index ->
                        currentCoroutineContext().ensureActive()
                        val frame = decoder.getFrame(index)
                        val apngEncoder = encoder ?: ApngEncoder(
                            outputStream = output,
                            width = frame.width,
                            height = frame.height,
                            numberOfFrames = decoder.numberOfFrames
                        ).apply {
                            setEncodeAlpha(true)
                            setOptimiseApng(false)
                            setRepetitionCount(decoder.loopsCount)
                        }.also { encoder = it }
                        apngEncoder.writeFrame(
                            btm = frame,
                            delay = decoder.getFrameDuration(index)
                                .coerceAtLeast(MIN_FRAME_DELAY_MILLIS)
                                .toFloat()
                        )
                        frame.recycle()
                    }
                    requireNotNull(encoder).writeEnd()
                }
                onProgress(uri, output.toByteArray().withApngLoopCount(loopCount))
            }
        }
    }

    override suspend fun convertJxlToWebp(
        jxlUris: List<String>,
        quality: Quality.Base,
        onProgress: suspend (String, ByteArray) -> Unit
    ) = withContext(defaultDispatcher) {
        jxlUris.forEach { uri ->
            runSuspendCatching {
                val encoded = JxlAnimatedImage(requireNotNull(uri.bytes)).use { decoder ->
                    require(decoder.numberOfFrames > 0)
                    val encoder = AnimatedWebpEncoder(
                        quality = quality.qualityValue,
                        loopCount = decoder.loopsCount,
                        backgroundColor = Color.Transparent.toArgb()
                    )
                    val frames = mutableListOf<Bitmap>()
                    try {
                        repeat(decoder.numberOfFrames) { index ->
                            currentCoroutineContext().ensureActive()
                            val frame = decoder.getFrame(index)
                            frames.add(frame)
                            encoder.addFrame(
                                bitmap = frame,
                                duration = decoder.getFrameDuration(index)
                                    .coerceAtLeast(MIN_FRAME_DELAY_MILLIS)
                            )
                        }
                        encoder.encode()
                    } finally {
                        frames.forEach(Bitmap::recycle)
                    }
                }
                onProgress(uri, encoded)
            }
        }
    }

    private val String.jxl: ByteArray?
        get() = bytes?.let { JxlCoder.Convenience.construct(it) }

    private val String.jpeg: ByteArray?
        get() = bytes?.let { JxlCoder.Convenience.reconstructJPEG(it) }

    private val String.bytes: ByteArray?
        get() = context
            .contentResolver
            .openInputStream(toUri())?.use {
                it.readBytes()
            }

}

private fun ByteArray.withApngLoopCount(loopCount: Int): ByteArray = apply {
    var offset = 8
    while (offset + 12 <= size) {
        val length = ByteBuffer.wrap(this, offset, 4).order(ByteOrder.BIG_ENDIAN).int
        val typeOffset = offset + 4
        val dataOffset = typeOffset + 4
        if (dataOffset + length + 4 > size) return@apply
        if (decodeToString(typeOffset, dataOffset) == "acTL" && length >= 8) {
            ByteBuffer.wrap(this, dataOffset + 4, 4)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(loopCount)
            val crc = CRC32().apply {
                update(this@withApngLoopCount, typeOffset, 4 + length)
            }
            ByteBuffer.wrap(this, dataOffset + length, 4)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(crc.value.toInt())
            return@apply
        }
        offset = dataOffset + length + 4
    }
}
