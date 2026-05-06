/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.core.data.image.utils.compressor

import android.graphics.Bitmap
import android.media.MediaCodecList
import android.os.Build
import android.util.Log
import com.radzivon.bartoshyk.avif.coder.AvifChromaSubsampling
import com.radzivon.bartoshyk.avif.coder.AvifSpeed
import com.radzivon.bartoshyk.avif.coder.AvifSurfaceMode
import com.radzivon.bartoshyk.avif.coder.HeifCoder
import com.radzivon.bartoshyk.avif.coder.PreciseMode
import com.t8rin.imagetoolbox.core.data.image.utils.ImageCompressorBackend
import com.t8rin.imagetoolbox.core.domain.image.model.Quality

internal class AvifHardwareBackend(
    private val isLossless: Boolean
) : ImageCompressorBackend {

    private val softwareFallback = AvifBackend(isLossless = isLossless)

    override suspend fun compress(
        image: Bitmap,
        quality: Quality
    ): ByteArray {
        if (!isHardwareAvifEncodingSupported()) {
            Log.d(TAG, "AVIF hardware encode is unavailable, using software backend")
            return softwareFallback.compress(image, quality)
        }

        val avifQuality = quality as? Quality.Avif ?: Quality.Avif()
        return runCatching {
            HeifCoder().encodeAvif(
                image,
                avifQuality.qualityValue,
                AvifSpeed.entries.firstOrNull {
                    it.ordinal == 10 - avifQuality.effort
                } ?: AvifSpeed.TEN,
                if (isLossless) {
                    PreciseMode.LOSSLESS
                } else {
                    PreciseMode.LOSSY
                },
                AvifSurfaceMode.AUTO,
                AvifChromaSubsampling.AUTO
            )
        }.onFailure {
            Log.w(TAG, "AVIF hardware encode failed, falling back to software", it)
        }.getOrElse {
            softwareFallback.compress(image, quality)
        }
    }

    companion object {
        private const val TAG = "AvifHardwareBackend"
        private const val AVIF_MIME = "image/avif"

        fun isHardwareAvifEncodingSupported(): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return false

            return runCatching {
                MediaCodecList(MediaCodecList.ALL_CODECS)
                    .codecInfos
                    .asSequence()
                    .filter { it.isEncoder && !it.isSoftwareOnly }
                    .any { info ->
                        info.supportedTypes.any { it.equals(AVIF_MIME, ignoreCase = true) }
                    }
            }.getOrDefault(false)
        }
    }
}
