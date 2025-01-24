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
import android.os.Build
import ru.tech.imageresizershrinker.core.data.image.utils.ImageCompressorBackend
import ru.tech.imageresizershrinker.core.data.utils.compress
import ru.tech.imageresizershrinker.core.domain.image.model.Quality

internal data class WebpBackend(
    private val isLossless: Boolean
) : ImageCompressorBackend {

    override suspend fun compress(
        image: Bitmap,
        quality: Quality
    ): ByteArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        image.compress(
            format = if (isLossless) {
                Bitmap.CompressFormat.WEBP_LOSSLESS
            } else {
                Bitmap.CompressFormat.WEBP_LOSSY
            },
            quality = quality.qualityValue
        )
    } else {
        @Suppress("DEPRECATION")
        image.compress(
            format = Bitmap.CompressFormat.WEBP,
            quality = quality.qualityValue
        )
    }

}