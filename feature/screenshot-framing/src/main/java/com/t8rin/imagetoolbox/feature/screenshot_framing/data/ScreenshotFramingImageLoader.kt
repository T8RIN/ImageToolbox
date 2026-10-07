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

package com.t8rin.imagetoolbox.feature.screenshot_framing.data

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.scale
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.utils.imageSize
import kotlin.math.sqrt

internal suspend fun ImageGetter<Bitmap>.getScreenshotFramingImage(
    uri: Uri,
    maxSide: Int
): Bitmap? {
    val sourceSize = uri.imageSize()
    val runtime = Runtime.getRuntime()
    val availableMemory = runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory()
    // An inexact decoder may return up to twice the requested side length.
    val memoryFraction = if (sourceSize == null) 0.05 else 0.2
    val sideLimit = sqrt(availableMemory * memoryFraction / 4).toInt().coerceIn(1, maxSide)
    val image = if (sourceSize != null) {
        val side = minOf(sideLimit, maxOf(sourceSize.width, sourceSize.height))
        getImage(data = uri, size = IntegerSize(side, side))
    } else {
        getImage(data = uri, size = sideLimit)
    } ?: return null

    val longestSide = maxOf(image.width, image.height)
    if (longestSide <= sideLimit) return image

    val scale = sideLimit.toFloat() / longestSide
    return image.scale(
        width = (image.width * scale).toInt().coerceAtLeast(1),
        height = (image.height * scale).toInt().coerceAtLeast(1)
    )
}