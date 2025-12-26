/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import com.awxkee.aire.Aire
import com.awxkee.jxlcoder.JxlChannelsConfiguration
import com.awxkee.jxlcoder.JxlCoder
import com.awxkee.jxlcoder.JxlCompressionOption
import com.awxkee.jxlcoder.JxlDecodingSpeed
import com.awxkee.jxlcoder.JxlEffort
import com.t8rin.imagetoolbox.core.data.image.utils.ImageCompressorBackend
import com.t8rin.imagetoolbox.core.domain.image.model.Quality

internal data class JxlBackend(
    private val isLossless: Boolean
) : ImageCompressorBackend {

    override suspend fun compress(
        image: Bitmap,
        quality: Quality
    ): ByteArray {
        val jxlQuality = quality as? Quality.Jxl ?: Quality.Jxl()
        return JxlCoder.encode(
            bitmap = if (jxlQuality.channels == Quality.Channels.Monochrome) {
                Aire.grayscale(image)
            } else image,
            channelsConfiguration = when (jxlQuality.channels) {
                Quality.Channels.RGBA -> JxlChannelsConfiguration.RGBA
                Quality.Channels.RGB -> JxlChannelsConfiguration.RGB
                Quality.Channels.Monochrome -> JxlChannelsConfiguration.MONOCHROME
            },
            compressionOption = if (isLossless) {
                JxlCompressionOption.LOSSLESS
            } else {
                JxlCompressionOption.LOSSY
            },
            quality = if (isLossless) 100 else jxlQuality.qualityValue,
            effort = JxlEffort.entries.first { it.ordinal == jxlQuality.effort - 1 },
            decodingSpeed = JxlDecodingSpeed.entries.first { it.ordinal == jxlQuality.speed }
        )
    }

}