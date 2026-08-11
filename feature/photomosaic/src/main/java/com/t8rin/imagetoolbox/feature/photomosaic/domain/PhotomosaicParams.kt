/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t8rin.imagetoolbox.feature.photomosaic.domain

data class PhotomosaicParams(
    val columns: Int = 40,
    val colorBlend: Float = DEFAULT_COLOR_BLEND,
    val repeatDistance: Int = 2,
    val maxTiles: Int = 300
) {
    fun normalized() = copy(
        columns = columns.coerceIn(MIN_COLUMNS, MAX_COLUMNS),
        colorBlend = colorBlend.coerceIn(0f, MAX_COLOR_BLEND),
        repeatDistance = repeatDistance.coerceIn(0, MAX_REPEAT_DISTANCE),
        maxTiles = maxTiles.coerceIn(MIN_TILES, MAX_TILES)
    )

    companion object {
        const val MIN_COLUMNS = 10
        const val MAX_COLUMNS = 100
        const val MIN_TILES = 10
        const val MAX_TILES = 500
        const val DEFAULT_COLOR_BLEND = 0.12f
        const val MAX_COLOR_BLEND = 0.6f
        const val MAX_REPEAT_DISTANCE = 10
    }
}
