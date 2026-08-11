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

import kotlin.math.roundToInt

internal object AdaptiveTileLayout {

    data class Tile(
        val aspectRatio: Float,
        val color: LabColor
    )

    data class Placement(
        val tileIndex: Int,
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float
    )

    fun create(
        width: Int,
        height: Int,
        columns: Int,
        tiles: List<Tile>,
        repeatDistance: Int,
        colorAt: (x: Int, y: Int) -> LabColor,
        detailAt: (y: Int) -> Float
    ): List<Placement> {
        require(width > 0)
        require(height > 0)
        require(columns > 0)
        require(tiles.isNotEmpty())

        val placements = mutableListOf<Placement>()
        val lastUsedAt = IntArray(tiles.size) { Int.MIN_VALUE }
        val baseRowHeight = width / columns.toFloat()
        var top = 0f

        while (top < height) {
            val detail = detailAt(
                (top + baseRowHeight / 2f).roundToInt().coerceIn(0, height - 1)
            ).coerceIn(0f, 1f)
            val desiredRowHeight = baseRowHeight * (
                    MAX_ROW_SCALE - detail * (MAX_ROW_SCALE - MIN_ROW_SCALE)
                    )
            val targetAspectSum = width / desiredRowHeight
            val rowTiles = mutableListOf<Int>()
            var aspectSum = 0f

            while (aspectSum < targetAspectSum || rowTiles.isEmpty()) {
                val estimatedX = (aspectSum / targetAspectSum * width)
                    .roundToInt()
                    .coerceIn(0, width - 1)
                val estimatedY = (top + desiredRowHeight / 2f)
                    .roundToInt()
                    .coerceIn(0, height - 1)
                val tileIndex = selectTile(
                    target = colorAt(estimatedX, estimatedY),
                    tiles = tiles,
                    lastUsedAt = lastUsedAt,
                    placementIndex = placements.size + rowTiles.size,
                    repeatDistance = repeatDistance
                )

                rowTiles += tileIndex
                lastUsedAt[tileIndex] = placements.size + rowTiles.lastIndex
                aspectSum += tiles[tileIndex].aspectRatio.coerceAtLeast(MIN_ASPECT_RATIO)
            }

            val rowHeight = width / aspectSum
            var left = 0f
            var accumulatedAspect = 0f
            rowTiles.forEachIndexed { index, tileIndex ->
                accumulatedAspect += tiles[tileIndex].aspectRatio.coerceAtLeast(MIN_ASPECT_RATIO)
                val right = if (index == rowTiles.lastIndex) {
                    width.toFloat()
                } else {
                    width * accumulatedAspect / aspectSum
                }
                placements += Placement(
                    tileIndex = tileIndex,
                    left = left,
                    top = top,
                    right = right,
                    bottom = top + rowHeight
                )
                left = right
            }
            top += rowHeight
        }

        return placements
    }

    private fun selectTile(
        target: LabColor,
        tiles: List<Tile>,
        lastUsedAt: IntArray,
        placementIndex: Int,
        repeatDistance: Int
    ): Int {
        val distance = repeatDistance.coerceAtLeast(0)
        val available = tiles.indices.filter { index ->
            placementIndex.toLong() - lastUsedAt[index].toLong() > distance
        }
        val candidates = available.ifEmpty {
            val safestDistance = tiles.indices.maxOf { index ->
                placementIndex.toLong() - lastUsedAt[index].toLong()
            }
            tiles.indices.filter { index ->
                placementIndex.toLong() - lastUsedAt[index].toLong() == safestDistance
            }
        }

        return candidates.minBy { index ->
            target.distanceSquared(tiles[index].color)
        }
    }

    private const val MIN_ROW_SCALE = 0.65f
    private const val MAX_ROW_SCALE = 1.5f
    private const val MIN_ASPECT_RATIO = 0.05f
}
