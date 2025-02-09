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

package ru.tech.imageresizershrinker.core.data.image.utils.compressor

import android.graphics.Bitmap
import com.gemalto.jp2.JP2Encoder
import ru.tech.imageresizershrinker.core.data.image.utils.ImageCompressorBackend
import ru.tech.imageresizershrinker.core.domain.image.model.Quality

internal data class Jpeg2000Backend(
    private val isJ2K: Boolean
) : ImageCompressorBackend {

    override suspend fun compress(
        image: Bitmap,
        quality: Quality
    ): ByteArray = JP2Encoder(image)
        .setOutputFormat(
            if (isJ2K) {
                JP2Encoder.FORMAT_J2K
            } else {
                JP2Encoder.FORMAT_JP2
            }
        )
        .setVisualQuality(quality.qualityValue.toFloat())
        .encode()

}