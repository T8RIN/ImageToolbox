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

import kotlin.math.abs
import kotlin.math.pow

data class LabColor(
    val lightness: Double,
    val a: Double,
    val b: Double
) {
    fun distanceSquared(other: LabColor): Double =
        (lightness - other.lightness).pow(2) +
            (a - other.a).pow(2) +
            (b - other.b).pow(2)
}

object TileMatcher {

    fun match(
        targets: List<LabColor>,
        tiles: List<LabColor>,
        columns: Int,
        repeatDistance: Int
    ): IntArray {
        require(columns > 0)
        require(tiles.isNotEmpty())

        val result = IntArray(targets.size)
        val distance = repeatDistance.coerceAtLeast(0)
        val seenAtGeneration = IntArray(tiles.size)
        val nearestDistance = IntArray(tiles.size)

        targets.forEachIndexed { index, target ->
            val generation = index + 1
            markNearbyTiles(
                result = result,
                currentIndex = index,
                columns = columns,
                distance = distance,
                generation = generation,
                seenAtGeneration = seenAtGeneration,
                nearestDistance = nearestDistance
            )

            result[index] = findBestTile(
                target = target,
                tiles = tiles,
                generation = generation,
                seenAtGeneration = seenAtGeneration,
                nearestDistance = nearestDistance
            )
        }

        return result
    }

    private fun markNearbyTiles(
        result: IntArray,
        currentIndex: Int,
        columns: Int,
        distance: Int,
        generation: Int,
        seenAtGeneration: IntArray,
        nearestDistance: IntArray
    ) {
        if (distance == 0) return

        val currentRow = currentIndex / columns
        val currentColumn = currentIndex % columns

        for (row in (currentRow - distance).coerceAtLeast(0)..currentRow) {
            val startColumn = (currentColumn - distance).coerceAtLeast(0)
            val endColumn = (currentColumn + distance).coerceAtMost(columns - 1)

            for (column in startColumn..endColumn) {
                val candidateIndex = row * columns + column
                val candidateDistance = currentRow - row + abs(currentColumn - column)
                if (candidateIndex < currentIndex && candidateDistance <= distance) {
                    val tileIndex = result[candidateIndex]
                    if (seenAtGeneration[tileIndex] != generation) {
                        seenAtGeneration[tileIndex] = generation
                        nearestDistance[tileIndex] = candidateDistance
                    } else if (candidateDistance < nearestDistance[tileIndex]) {
                        nearestDistance[tileIndex] = candidateDistance
                    }
                }
            }
        }
    }

    private fun findBestTile(
        target: LabColor,
        tiles: List<LabColor>,
        generation: Int,
        seenAtGeneration: IntArray,
        nearestDistance: IntArray
    ): Int {
        var bestIndex = -1
        var bestColorDistance = Double.POSITIVE_INFINITY

        tiles.indices.forEach { tileIndex ->
            if (seenAtGeneration[tileIndex] != generation) {
                val colorDistance = target.distanceSquared(tiles[tileIndex])
                if (colorDistance < bestColorDistance) {
                    bestIndex = tileIndex
                    bestColorDistance = colorDistance
                }
            }
        }
        if (bestIndex >= 0) return bestIndex

        val safestDistance = tiles.indices.maxOf { nearestDistance[it] }
        tiles.indices.forEach { tileIndex ->
            if (nearestDistance[tileIndex] == safestDistance) {
                val colorDistance = target.distanceSquared(tiles[tileIndex])
                if (colorDistance < bestColorDistance) {
                    bestIndex = tileIndex
                    bestColorDistance = colorDistance
                }
            }
        }

        return bestIndex
    }
}
