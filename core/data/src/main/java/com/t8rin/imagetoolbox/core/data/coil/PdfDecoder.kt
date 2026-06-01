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

@file:Suppress("FunctionName", "unused")

package com.t8rin.imagetoolbox.core.data.coil

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import coil3.Extras
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.getExtra
import coil3.request.ImageRequest
import coil3.request.Options
import coil3.size.Size
import coil3.size.pxOrElse
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.flexibleResize
import com.t8rin.imagetoolbox.core.utils.appContext
import okio.ByteString.Companion.toByteString
import kotlin.math.roundToInt

internal class PdfDecoder(
    private val source: ImageSource,
    private val options: Options,
) : Decoder {

    override suspend fun decode(): DecodeResult {
        val file = source.file().toFile()

        val image = ParcelFileDescriptor.open(
            file,
            ParcelFileDescriptor.MODE_READ_ONLY
        ).use { fileDescriptor ->
            PdfRenderer(fileDescriptor).use { renderer ->
                val pageIndex = options.pdfPage.coerceIn(0, renderer.pageCount - 1)

                renderer.openPage(pageIndex).use { page ->
                    val originalWidth = page.width
                    val originalHeight = page.height

                    val targetSize = IntegerSize(
                        width = originalWidth,
                        height = originalHeight
                    ).flexibleResize(
                        w = options.size.width.pxOrElse { 0 },
                        h = options.size.height.pxOrElse { 0 }
                    )

                    val scaleX = targetSize.width.toFloat() / originalWidth
                    val scaleY = targetSize.height.toFloat() / originalHeight
                    val scale = minOf(scaleX, scaleY).coerceAtMost(2f)

                    val bitmap = createBitmap(
                        (originalWidth * scale).roundToInt().coerceAtLeast(1),
                        (originalHeight * scale).roundToInt().coerceAtLeast(1)
                    ).apply {
                        eraseColor(Color.WHITE)
                    }

                    page.render(
                        bitmap,
                        null,
                        Matrix().apply { setScale(scale, scale) },
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                    )

                    bitmap.asImage()
                }
            }
        }

        return DecodeResult(
            image = image,
            isSampled = true
        )
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            return if (isPdf(result)) {
                PdfDecoder(
                    source = result.source,
                    options = options
                )
            } else null
        }

        private fun isPdf(result: SourceFetchResult): Boolean {
            val pdfMagic = byteArrayOf(0x25, 0x50, 0x44, 0x46).toByteString()

            return result.source.source()
                .rangeEquals(0, pdfMagic) || result.mimeType == "application/pdf"
        }
    }

}

fun PdfImageRequest(
    data: Any?,
    pdfPage: Int = 0,
    size: Size? = null
): ImageRequest = ImageRequest.Builder(appContext)
    .data(data)
    .pdfPage(pdfPage)
    .memoryCacheKey(data.toString() + pdfPage)
    .diskCacheKey(data.toString() + pdfPage)
    .apply {
        size?.let(::size)
    }
    .build()

fun ImageRequest.Builder.pdfPage(pdfPage: Int) = apply {
    extras[pdfPageKey] = pdfPage
}

val ImageRequest.pdfPage: Int
    get() = getExtra(pdfPageKey)

val Options.pdfPage: Int
    get() = getExtra(pdfPageKey)

val Extras.Key.Companion.pdfPage: Extras.Key<Int>
    get() = pdfPageKey

private val pdfPageKey = Extras.Key(default = 0)