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

package com.t8rin.imagetoolbox.feature.webp_tools.data

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import com.t8rin.awebp.decoder.AnimatedWebpDecoder
import com.t8rin.awebp.encoder.AnimatedWebpEncoder
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.feature.webp_tools.domain.WebpConverter
import com.t8rin.imagetoolbox.feature.webp_tools.domain.WebpParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


internal class AndroidWebpConverter @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageShareProvider: ImageShareProvider<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    @ApplicationContext private val context: Context,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, WebpConverter {

    override fun extractFramesFromWebp(
        webpUri: String,
        imageFormat: ImageFormat,
        quality: Quality
    ): Flow<String> = AnimatedWebpDecoder(
        sourceFile = webpUri.file,
        coroutineScope = CoroutineScope(decodingDispatcher)
    ).frames().mapNotNull { frame ->
        imageShareProvider.cacheImage(
            image = frame.bitmap,
            imageInfo = ImageInfo(
                width = frame.bitmap.width,
                height = frame.bitmap.height,
                imageFormat = imageFormat,
                quality = quality
            )
        ).also {
            frame.bitmap.recycle()
        }
    }

    override suspend fun createWebpFromImageUris(
        imageUris: List<String>,
        params: WebpParams,
        onFailure: (Throwable) -> Unit,
        onProgress: () -> Unit
    ): ByteArray? = withContext(defaultDispatcher) {
        val size = params.size ?: imageGetter.getImage(data = imageUris[0])!!.run {
            IntegerSize(width, height)
        }

        if (size.width <= 0 || size.height <= 0) {
            onFailure(IllegalArgumentException("Width and height must be > 0"))
            return@withContext null
        }

        val encoder = AnimatedWebpEncoder(
            quality = params.quality.qualityValue,
            loopCount = params.repeatCount,
            backgroundColor = Color.Transparent.toArgb()
        )

        imageUris.forEach { uri ->
            imageGetter.getImage(
                data = uri,
                size = size
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
                    ),
                    duration = params.delay
                )
            }
            onProgress()
        }

        runCatching {
            encoder.encode()
        }.onFailure(onFailure).getOrNull()
    }

    private val String.inputStream: InputStream?
        get() = context
            .contentResolver
            .openInputStream(toUri())

    private val String.file: File
        get() {
            val gifFile = File(context.cacheDir, "temp.webp")
            inputStream?.use { gifStream ->
                gifStream.copyTo(FileOutputStream(gifFile))
            }
            return gifFile
        }


}