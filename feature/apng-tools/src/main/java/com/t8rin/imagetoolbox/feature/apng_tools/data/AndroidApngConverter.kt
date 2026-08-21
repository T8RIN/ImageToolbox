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

package com.t8rin.imagetoolbox.feature.apng_tools.data

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import com.awxkee.jxlcoderlibjxl.JxlCoder
import com.awxkee.jxlcoderlibjxl.JxlDecodingSpeed
import com.awxkee.jxlcoderlibjxl.JxlEffort
import com.t8rin.awebp.encoder.AnimatedWebpEncoder
import com.t8rin.gif_converter.GifEncoder
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.feature.apng_tools.domain.ApngConverter
import com.t8rin.imagetoolbox.feature.apng_tools.domain.ApngParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import oupson.apng.decoder.ApngDecoder
import oupson.apng.encoder.ApngEncoder
import java.io.ByteArrayOutputStream
import javax.inject.Inject

private const val DEFAULT_FRAME_DELAY_MILLIS = 100
private const val MAX_GIF_LOOP_COUNT = 65_535
private const val MIN_FRAME_DELAY_MILLIS = 10


internal class AndroidApngConverter @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    @ApplicationContext private val context: Context,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, ApngConverter {

    override fun extractFramesFromApng(
        apngUri: String,
        imageFormat: ImageFormat,
        quality: Quality
    ): Flow<String> = channelFlow {
        ApngDecoder(
            context = context,
            uri = apngUri.toUri()
        ).decodeAsync(defaultDispatcher) { frame ->
            if (!currentCoroutineContext().isActive) {
                currentCoroutineContext().cancel(null)
                return@decodeAsync
            }
            shareProvider.cacheImage(
                image = frame,
                imageInfo = ImageInfo(
                    width = frame.width,
                    height = frame.height,
                    imageFormat = imageFormat,
                    quality = quality
                )
            )?.let { send(it) }
        }
    }

    override suspend fun createApngFromImageUris(
        imageUris: List<String>,
        params: ApngParams,
        onFailure: (Throwable) -> Unit,
        onProgress: () -> Unit
    ): String? = withContext(defaultDispatcher) {
        val size = params.size ?: imageGetter.getImage(data = imageUris[0])!!.run {
            IntegerSize(width, height)
        }

        if (size.width <= 0 || size.height <= 0) {
            onFailure(IllegalArgumentException("Width and height must be > 0"))
            return@withContext null
        }

        shareProvider.cacheData(
            writeData = { writeable ->
                val encoder = ApngEncoder(
                    outputStream = writeable.outputStream(),
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
                            ),
                            delay = params.delay.toFloat()
                        )
                    }
                    onProgress()
                }
                encoder.writeEnd()
            },
            filename = "temp_apng.png"
        )
    }

    override suspend fun convertApngToJxl(
        apngUris: List<String>,
        quality: Quality.Jxl,
        onProgress: suspend (String, ByteArray) -> Unit
    ) = withContext(defaultDispatcher) {
        apngUris.forEach { uri ->
            uri.bytes?.let { apngData ->
                runSuspendCatching {
                    JxlCoder.Convenience.apng2JXL(
                        apngData = apngData,
                        quality = quality.qualityValue,
                        effort = JxlEffort.entries.first { it.ordinal == quality.effort },
                        decodingSpeed = JxlDecodingSpeed.entries.first { it.ordinal == quality.speed }
                    ).let {
                        onProgress(uri, it)
                    }
                }
            }
        }
    }

    override suspend fun convertApngToGif(
        apngUris: List<String>,
        quality: Quality.Base,
        onProgress: suspend (String, ByteArray) -> Unit
    ) = withContext(defaultDispatcher) {
        apngUris.forEach { uri ->
            uri.bytes?.let { apngData ->
                runSuspendCatching {
                    val animationInfo = apngData.animationInfo()
                    val output = ByteArrayOutputStream()
                    var encoder: GifEncoder? = null
                    var frameIndex = 0

                    ApngDecoder(
                        context = context,
                        uri = uri.toUri()
                    ).decodeAsync(defaultDispatcher) { frame ->
                        currentCoroutineContext().ensureActive()
                        val gifEncoder = encoder ?: GifEncoder()
                            .setRepeat(animationInfo.loopCount)
                            .setQuality(quality.qualityValue)
                            .setDispose(2)
                            .setTransparent(Color.Transparent.toArgb())
                            .setSize(frame.width, frame.height)
                            .also {
                                it.start(output)
                                encoder = it
                            }
                        gifEncoder
                            .setDelay(
                                animationInfo.frameDelaysMillis.getOrElse(frameIndex) {
                                    DEFAULT_FRAME_DELAY_MILLIS
                                }
                            )
                            .addFrame(frame)
                        frameIndex++
                    }

                    require(frameIndex > 0)
                    requireNotNull(encoder).finish()
                    onProgress(uri, output.toByteArray())
                }
            }
        }
    }

    override suspend fun convertApngToWebp(
        apngUris: List<String>,
        quality: Quality.Base,
        onProgress: suspend (String, ByteArray) -> Unit
    ) = withContext(defaultDispatcher) {
        apngUris.forEach { uri ->
            uri.bytes?.let { apngData ->
                runSuspendCatching {
                    val animationInfo = apngData.animationInfo()
                    val encoder = AnimatedWebpEncoder(
                        quality = quality.qualityValue,
                        loopCount = animationInfo.loopCount,
                        backgroundColor = Color.Transparent.toArgb()
                    )
                    var frameIndex = 0

                    ApngDecoder(
                        context = context,
                        uri = uri.toUri()
                    ).decodeAsync(defaultDispatcher) { frame ->
                        currentCoroutineContext().ensureActive()
                        encoder.addFrame(
                            bitmap = frame.copy(frame.safeConfig, false),
                            duration = animationInfo.frameDelaysMillis.getOrElse(frameIndex) {
                                DEFAULT_FRAME_DELAY_MILLIS
                            }
                        )
                        frameIndex++
                    }

                    require(frameIndex > 0)
                    onProgress(uri, encoder.encode())
                }
            }
        }
    }

    private val String.bytes: ByteArray?
        get() = context
            .contentResolver
            .openInputStream(toUri())?.use {
                it.readBytes()
            }

    private data class ApngAnimationInfo(
        val loopCount: Int,
        val frameDelaysMillis: List<Int>
    )

    private fun ByteArray.animationInfo(): ApngAnimationInfo {
        var loopCount = 0
        val frameDelays = mutableListOf<Int>()
        var offset = 8

        while (offset + 12 <= size) {
            val chunkSize = readUInt32BigEndian(offset)
            val typeOffset = offset + 4
            val dataOffset = offset + 8
            if (dataOffset.toLong() + chunkSize + 4 > size) break

            when {
                matchesAscii(typeOffset, "acTL") && chunkSize >= 8 -> {
                    loopCount = readUInt32BigEndian(dataOffset + 4)
                        .coerceAtMost(MAX_GIF_LOOP_COUNT.toLong())
                        .toInt()
                }

                matchesAscii(typeOffset, "fcTL") && chunkSize >= 26 -> {
                    val numerator = readUInt16BigEndian(dataOffset + 20)
                    val denominator = readUInt16BigEndian(dataOffset + 22).takeIf { it != 0 } ?: 100
                    frameDelays += ((numerator * 1000L + denominator / 2) / denominator)
                        .toInt()
                        .coerceAtLeast(MIN_FRAME_DELAY_MILLIS)
                }
            }

            offset = (dataOffset.toLong() + chunkSize + 4).toInt()
        }

        return ApngAnimationInfo(
            loopCount = loopCount,
            frameDelaysMillis = frameDelays
        )
    }

    private fun ByteArray.readUInt16BigEndian(offset: Int): Int =
        ((this[offset].toInt() and 0xFF) shl 8) or
                (this[offset + 1].toInt() and 0xFF)

    private fun ByteArray.readUInt32BigEndian(offset: Int): Long =
        ((this[offset].toLong() and 0xFF) shl 24) or
                ((this[offset + 1].toLong() and 0xFF) shl 16) or
                ((this[offset + 2].toLong() and 0xFF) shl 8) or
                (this[offset + 3].toLong() and 0xFF)

    private fun ByteArray.matchesAscii(offset: Int, value: String): Boolean =
        value.indices.all { index -> this[offset + index] == value[index].code.toByte() }

}