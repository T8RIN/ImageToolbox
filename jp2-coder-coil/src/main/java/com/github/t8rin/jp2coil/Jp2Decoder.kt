package com.github.t8rin.jp2coil

import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.decode.DecodeResult
import coil.decode.Decoder
import coil.fetch.SourceResult
import coil.request.Options
import com.gemalto.jp2.JP2Decoder
import kotlinx.coroutines.runInterruptible
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8

class Jp2Decoder(
    private val source: SourceResult,
    private val options: Options,
    private val imageLoader: ImageLoader
) : Decoder {

    override suspend fun decode(): DecodeResult? = runInterruptible {
        val byteArray = source.source.source().readByteArray()
        if (!JP2Decoder.isJPEG2000(byteArray)) {
            return@runInterruptible null
        }

        return@runInterruptible DecodeResult(
            BitmapDrawable(
                options.context.resources,
                JP2Decoder(byteArray).decode()
            ), false
        )
    }


    class Factory : Decoder.Factory {
        override fun create(
            result: SourceResult,
            options: Options,
            imageLoader: ImageLoader
        ) = if (isJpeg2000(result.source.source()) || result.mimeType?.contains("jp2") == true) {
            Jp2Decoder(result, options, imageLoader)
        } else null
    }

}

private fun isJpeg2000(source: BufferedSource): Boolean {
    return source.rangeEquals(4, "jP__".encodeUtf8())
}