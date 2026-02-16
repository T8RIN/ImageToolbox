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

@file:Suppress("FunctionName")

package com.t8rin.imagetoolbox.core.data.coil

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
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

class PdfDecoder(
    private val source: ImageSource,
    private val options: Options,
) : Decoder {

    override suspend fun decode(): DecodeResult {
        val context = options.context
        val pdfRenderer = PdfRenderer(
            ParcelFileDescriptor.open(
                source.file().toFile(),
                ParcelFileDescriptor.MODE_READ_ONLY,
            )
        )
        val page = pdfRenderer.openPage(options.pdfPage)
        val densityDpi = context.resources.displayMetrics.densityDpi

        val size = IntegerSize(
            width = densityDpi * page.width / 72,
            height = densityDpi * page.height / 72
        ).flexibleResize(
            w = options.size.width.pxOrElse { 0 },
            h = options.size.height.pxOrElse { 0 }
        )

        val bitmap = createBitmap(
            width = size.width,
            height = size.height
        )
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        pdfRenderer.close()

        return DecodeResult(
            image = bitmap.toDrawable(context.resources).asImage(),
            isSampled = true,
        )
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            if (!isApplicable(result)) return null
            return PdfDecoder(result.source, options)
        }

        private fun isApplicable(result: SourceFetchResult): Boolean =
            result.mimeType == "application/pdf"
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