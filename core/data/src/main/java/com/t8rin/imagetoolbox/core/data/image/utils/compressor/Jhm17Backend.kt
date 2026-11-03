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
import com.t8rin.imagetoolbox.core.domain.image.model.Jhm17Chroma
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.trickle.Jhm17

internal data object Jhm17Backend : ImageCompressorBackend {

    override suspend fun compress(
        image: Bitmap,
        quality: Quality
    ): ByteArray {
        val jhm17Quality = quality as? Quality.Jhm17 ?: Quality.Jhm17(
            qualityValue = quality.qualityValue.coerceIn(1..100)
        )

        return Jhm17.encode(
            bitmap = image,
            options = Jhm17.Options(
                quality = jhm17Quality.qualityValue,
                progressive = jhm17Quality.progressive,
                chroma = jhm17Quality.chroma.toBackend(),
                polish = jhm17Quality.polish.toBackend()
            )
        )
    }
}

private fun Jhm17Chroma.toBackend(): Jhm17.Chroma = when (this) {
    Jhm17Chroma.AUTO -> Jhm17.Chroma.AUTO
    Jhm17Chroma.YUV_420 -> Jhm17.Chroma.YUV_420
    Jhm17Chroma.YUV_422 -> Jhm17.Chroma.YUV_422
    Jhm17Chroma.YUV_444 -> Jhm17.Chroma.YUV_444
    Jhm17Chroma.YUV_440 -> Jhm17.Chroma.YUV_440
    Jhm17Chroma.YUV_441 -> Jhm17.Chroma.YUV_441
    Jhm17Chroma.YUV_421 -> Jhm17.Chroma.YUV_421
    Jhm17Chroma.YUV_411 -> Jhm17.Chroma.YUV_411
    Jhm17Chroma.YUV_410 -> Jhm17.Chroma.YUV_410
}

private fun Quality.Jhm17.Polish.toBackend(): Jhm17.Polish = when (this) {
    Quality.Jhm17.Polish.OFF -> Jhm17.Polish.OFF
    Quality.Jhm17.Polish.GREEDY -> Jhm17.Polish.GREEDY
    Quality.Jhm17.Polish.TRELLIS -> Jhm17.Polish.TRELLIS
    Quality.Jhm17.Polish.TRELLIS_GREEDY -> Jhm17.Polish.TRELLIS_GREEDY
}