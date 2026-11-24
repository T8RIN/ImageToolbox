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

package com.t8rin.imagetoolbox.feature.photomosaic.domain

import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import kotlin.math.roundToInt

data class PhotomosaicParams(
    val columns: Int = 40,
    val colorBlend: Float = DEFAULT_COLOR_BLEND,
    val repeatDistance: Int = 2,
    val maxTiles: Int = 300,
    val outputScaleRatio: Float = 1f
) {
    fun normalized() = copy(
        columns = columns.coerceIn(MIN_COLUMNS, MAX_COLUMNS),
        colorBlend = colorBlend.coerceIn(0f, MAX_COLOR_BLEND),
        repeatDistance = repeatDistance.coerceIn(0, MAX_REPEAT_DISTANCE),
        maxTiles = maxTiles.coerceIn(MIN_TILES, MAX_TILES),
        outputScaleRatio = outputScaleRatio.coerceIn(0.1f, 1f)
    )

    internal fun outputSize(targetSize: IntegerSize): IntegerSize {
        val scale = MAX_OUTPUT_SIZE * outputScaleRatio.toDouble() /
                maxOf(targetSize.width, targetSize.height)
        return IntegerSize(
            width = (targetSize.width * scale).roundToInt().coerceAtLeast(1),
            height = (targetSize.height * scale).roundToInt().coerceAtLeast(1)
        )
    }

    companion object {
        const val MIN_COLUMNS = 10
        const val MAX_COLUMNS = 100
        const val MIN_TILES = 10
        const val MAX_TILES = 500
        const val DEFAULT_COLOR_BLEND = 0.12f
        const val MAX_COLOR_BLEND = 0.6f
        const val MAX_REPEAT_DISTANCE = 10
        internal const val MAX_OUTPUT_SIZE = 4096
    }
}
