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

package com.t8rin.collages.geometry

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal fun collageOutputDimensions(
    sourceSize: Int,
    aspectRatio: Float,
    scale: Float,
    maxPixels: Long
): Pair<Int, Int> {
    val ratio = aspectRatio.toDouble().takeIf { it.isFinite() && it > 0.0 } ?: 1.0
    val longToShort = max(ratio, 1.0 / ratio)
    val limit = min(8192.0, sqrt(maxPixels * longToShort))
    val longEdge = (min(sourceSize.coerceAtLeast(1).toDouble(), limit) * scale.coerceIn(0.01f, 1f))
        .roundToInt().coerceAtLeast(1)
    val shortEdge = (longEdge / longToShort).roundToInt().coerceAtLeast(1)
    return if (ratio >= 1.0) longEdge to shortEdge else shortEdge to longEdge
}

internal fun collagePreviewSize(
    imageCount: Int,
    relativeArea: Float,
    lowMemory: Boolean
): Int = when {
    imageCount > 20 -> (1024 * sqrt(20f * relativeArea.coerceIn(0f, 1f)))
        .toInt().coerceIn(256, 1600)

    imageCount > 4 || lowMemory -> 1024
    else -> 1600
}