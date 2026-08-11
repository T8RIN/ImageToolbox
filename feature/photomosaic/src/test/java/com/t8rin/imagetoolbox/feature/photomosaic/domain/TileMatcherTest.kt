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

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class TileMatcherTest {

    @Test
    fun closestTilesAreSelectedByColor() {
        val dark = LabColor(lightness = 10.0, a = 0.0, b = 0.0)
        val light = LabColor(lightness = 90.0, a = 0.0, b = 0.0)

        val result = TileMatcher.match(
            targets = listOf(light, dark),
            tiles = listOf(dark, light),
            columns = 2,
            repeatDistance = 0
        )

        assertArrayEquals(intArrayOf(1, 0), result)
    }

    @Test
    fun nearbyRepetitionIsAvoidedWhenAlternativeExists() {
        val target = LabColor(lightness = 50.0, a = 0.0, b = 0.0)
        val exact = target
        val close = LabColor(lightness = 51.0, a = 0.0, b = 0.0)

        val result = TileMatcher.match(
            targets = List(4) { target },
            tiles = listOf(exact, close),
            columns = 2,
            repeatDistance = 1
        )

        assertArrayEquals(intArrayOf(0, 1, 1, 0), result)
    }

    @Test
    fun repeatAvoidanceHoldsAcrossRowsWithEnoughTiles() {
        val columns = 8
        val repeatDistance = 3
        val tiles = List(32) { LabColor(50.0, 0.0, 0.0) }
        val result = TileMatcher.match(
            targets = List(columns * 8) { tiles.first() },
            tiles = tiles,
            columns = columns,
            repeatDistance = repeatDistance
        )

        result.forEachIndexed { index, tileIndex ->
            val row = index / columns
            val column = index % columns
            for (previousIndex in 0 until index) {
                val previousRow = previousIndex / columns
                val previousColumn = previousIndex % columns
                val distance = abs(row - previousRow) + abs(column - previousColumn)
                if (distance <= repeatDistance) {
                    assertTrue(result[previousIndex] != tileIndex)
                }
            }
        }
    }

    @Test
    fun insufficientTilesPreferTheFarthestRecentUse() {
        val exact = LabColor(50.0, 0.0, 0.0)
        val close = LabColor(51.0, 0.0, 0.0)

        val result = TileMatcher.match(
            targets = List(6) { exact },
            tiles = listOf(exact, close),
            columns = 6,
            repeatDistance = 5
        )

        assertArrayEquals(intArrayOf(0, 1, 0, 1, 0, 1), result)
    }

    @Test
    fun repeatDistanceZeroAlwaysUsesClosestColor() {
        val exact = LabColor(50.0, 0.0, 0.0)
        val far = LabColor(80.0, 0.0, 0.0)

        val result = TileMatcher.match(
            targets = List(12) { exact },
            tiles = listOf(exact, far),
            columns = 4,
            repeatDistance = 0
        )

        result.forEach { assertEquals(0, it) }
    }

    @Test
    fun oversizedRepeatDistanceAndPartialRowProduceValidMatches() {
        val tiles = List(3) { LabColor(it * 30.0, 0.0, 0.0) }

        val result = TileMatcher.match(
            targets = List(7) { LabColor(0.0, 0.0, 0.0) },
            tiles = tiles,
            columns = 5,
            repeatDistance = 50
        )

        assertEquals(7, result.size)
        result.forEach { assertTrue(it in tiles.indices) }
    }

    @Test
    fun gridRepeatAndTileCountCombinationsAvoidRepeatsWheneverPossible() {
        listOf(1, 2, 7, 100).forEach { columns ->
            (0..PhotomosaicParams.MAX_REPEAT_DISTANCE).forEach { repeatDistance ->
                listOf(1, 2, 7, 64).forEach { tileCount ->
                    val tiles = List(tileCount) { index ->
                        LabColor(index.toDouble(), index / 2.0, -index.toDouble())
                    }
                    val result = TileMatcher.match(
                        targets = List(columns * 3 + columns / 2 + 1) { index ->
                            LabColor(
                                lightness = (index * 17 % 100).toDouble(),
                                a = (index * 7 % 40).toDouble(),
                                b = -(index * 11 % 40).toDouble()
                            )
                        },
                        tiles = tiles,
                        columns = columns,
                        repeatDistance = repeatDistance
                    )

                    result.forEachIndexed { index, tileIndex ->
                        assertTrue(tileIndex in tiles.indices)
                        val nearbyTiles = nearbyTiles(
                            result = result,
                            currentIndex = index,
                            columns = columns,
                            distance = repeatDistance
                        )
                        if (nearbyTiles.size < tileCount) {
                            assertTrue(tileIndex !in nearbyTiles)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun maximumSupportedGridAndTileCountProduceValidMatches() {
        val columns = PhotomosaicParams.MAX_COLUMNS
        val tiles = List(PhotomosaicParams.MAX_TILES) { index ->
            LabColor(
                lightness = (index % 100).toDouble(),
                a = (index % 50).toDouble(),
                b = -(index % 50).toDouble()
            )
        }
        val result = TileMatcher.match(
            targets = List(columns * columns) { index ->
                LabColor(
                    lightness = (index % 100).toDouble(),
                    a = (index % 50).toDouble(),
                    b = -(index % 50).toDouble()
                )
            },
            tiles = tiles,
            columns = columns,
            repeatDistance = PhotomosaicParams.MAX_REPEAT_DISTANCE
        )

        assertEquals(columns * columns, result.size)
        result.forEach { assertTrue(it in tiles.indices) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun emptyTileSetIsRejected() {
        TileMatcher.match(
            targets = listOf(LabColor(0.0, 0.0, 0.0)),
            tiles = emptyList(),
            columns = 1,
            repeatDistance = 0
        )
    }

    @Test
    fun emptyTargetSetProducesEmptyMatches() {
        val result = TileMatcher.match(
            targets = emptyList(),
            tiles = listOf(LabColor(0.0, 0.0, 0.0)),
            columns = 1,
            repeatDistance = 5
        )

        assertEquals(0, result.size)
    }

    private fun nearbyTiles(
        result: IntArray,
        currentIndex: Int,
        columns: Int,
        distance: Int
    ): Set<Int> {
        val currentRow = currentIndex / columns
        val currentColumn = currentIndex % columns
        return (0 until currentIndex).mapNotNullTo(mutableSetOf()) { previousIndex ->
            val previousRow = previousIndex / columns
            val previousColumn = previousIndex % columns
            val cellDistance = abs(currentRow - previousRow) + abs(currentColumn - previousColumn)
            result[previousIndex].takeIf { cellDistance <= distance }
        }
    }
}
