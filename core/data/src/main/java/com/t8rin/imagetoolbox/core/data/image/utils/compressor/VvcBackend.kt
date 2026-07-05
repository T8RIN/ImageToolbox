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

package com.t8rin.imagetoolbox.core.data.image.utils.compressor

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.data.image.utils.ImageCompressorBackend
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.VvcBitDepth
import com.t8rin.imagetoolbox.core.domain.image.model.VvcChroma
import com.t8rin.trickle.VvcContainer
import com.t8rin.trickle.VvcEncoder
import com.t8rin.trickle.VvcBitDepth as BackendBitDepth
import com.t8rin.trickle.VvcChroma as BackendChroma

internal data class VvcBackend(
    private val isLossless: Boolean
) : ImageCompressorBackend {

    override suspend fun compress(
        image: Bitmap,
        quality: Quality
    ): ByteArray {
        val vvcQuality = quality as? Quality.Vvc ?: Quality.Vvc(
            qualityValue = quality.qualityValue.coerceIn(1..100)
        )

        return VvcEncoder.encode(
            bitmap = image,
            options = VvcEncoder.Options(
                quality = vvcQuality.qualityValue,
                lossless = isLossless,
                chroma = vvcQuality.chroma.toBackend(),
                bitDepth = vvcQuality.bitDepth.toBackend(),
                container = VvcContainer.HEIF
            )
        )
    }
}

private fun VvcChroma.toBackend(): BackendChroma = when (this) {
    VvcChroma.MONOCHROME -> BackendChroma.MONOCHROME
    VvcChroma.YUV_420 -> BackendChroma.YUV_420
    VvcChroma.YUV_422 -> BackendChroma.YUV_422
    VvcChroma.YUV_444 -> BackendChroma.YUV_444
}

private fun VvcBitDepth.toBackend(): BackendBitDepth = when (this) {
    VvcBitDepth.EIGHT -> BackendBitDepth.EIGHT
    VvcBitDepth.TEN -> BackendBitDepth.TEN
    VvcBitDepth.TWELVE -> BackendBitDepth.TWELVE
}