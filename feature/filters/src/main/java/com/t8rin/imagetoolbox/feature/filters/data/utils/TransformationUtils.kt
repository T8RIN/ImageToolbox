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

package com.t8rin.imagetoolbox.feature.filters.data.utils

import android.graphics.Bitmap
import androidx.core.graphics.scale
import coil3.size.Size
import coil3.size.pxOrElse
import com.t8rin.imagetoolbox.core.data.utils.aspectRatio
import java.lang.Integer.max

internal fun Bitmap.flexible(size: Size): Bitmap = flexibleResize(
    image = this,
    max = max(
        size.height.pxOrElse { height },
        size.width.pxOrElse { width }
    )
)

private fun flexibleResize(
    image: Bitmap,
    max: Int
): Bitmap {
    return runCatching {
        if (image.height >= image.width) {
            val aspectRatio = image.aspectRatio
            val targetWidth = (max * aspectRatio).toInt()
            image.scale(targetWidth, max)
        } else {
            val aspectRatio = 1f / image.aspectRatio
            val targetHeight = (max * aspectRatio).toInt()
            image.scale(max, targetHeight)
        }
    }.getOrNull() ?: image
}