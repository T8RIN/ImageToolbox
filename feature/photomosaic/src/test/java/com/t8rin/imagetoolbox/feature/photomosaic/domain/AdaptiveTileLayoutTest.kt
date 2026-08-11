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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveTileLayoutTest {

    @Test
    fun layoutCoversCanvasWithoutGaps() {
        val width = 1200
        val height = 800
        val placements = createLayout(
            width = width,
            height = height,
            aspectRatios = listOf(0.75f, 1f, 1.5f, 1.8f)
        )
        val rows = placements.groupBy { it.top }.toSortedMap()

        assertEquals(0f, rows.keys.first(), DELTA)
        rows.values.forEach { row ->
            val sorted = row.sortedBy { it.left }
            assertEquals(0f, sorted.first().left, DELTA)
            assertEquals(width.toFloat(), sorted.last().right, DELTA)
            sorted.zipWithNext().forEach { (first, second) ->
                assertEquals(first.right, second.left, DELTA)
            }
        }
        rows.values.zipWithNext().forEach { (first, second) ->
            assertEquals(first.first().bottom, second.first().top, DELTA)
        }
        assertTrue(rows.values.last().first().bottom >= height)
    }

    @Test
    fun placementsPreserveSourceAspectRatios() {
        val aspectRatios = listOf(0.6f, 1f, 1.4f, 2.1f)
        val placements = createLayout(
            aspectRatios = aspectRatios,
            repeatDistance = aspectRatios.lastIndex
        )

        placements.forEach { placement ->
            val actualAspectRatio =
                (placement.right - placement.left) / (placement.bottom - placement.top)
            assertEquals(aspectRatios[placement.tileIndex], actualAspectRatio, DELTA)
        }
    }

    @Test
    fun detailedRegionsUseSmallerTiles() {
        val lowDetail = createLayout(detail = 0f)
        val highDetail = createLayout(detail = 1f)
        val lowDetailHeight = lowDetail.first().bottom - lowDetail.first().top
        val highDetailHeight = highDetail.first().bottom - highDetail.first().top

        assertTrue(highDetailHeight < lowDetailHeight)
        assertTrue(highDetail.size > lowDetail.size)
    }

    @Test
    fun repeatDistanceAvoidsRecentlyUsedTiles() {
        val repeatDistance = 3
        val placements = createLayout(
            aspectRatios = List(4) { 1f },
            repeatDistance = repeatDistance
        )

        placements.forEachIndexed { index, placement ->
            val recentTiles = placements
                .subList((index - repeatDistance).coerceAtLeast(0), index)
                .map(AdaptiveTileLayout.Placement::tileIndex)
            assertTrue(placement.tileIndex !in recentTiles)
        }
    }

    private fun createLayout(
        width: Int = 1000,
        height: Int = 700,
        aspectRatios: List<Float> = List(8) { 1f },
        repeatDistance: Int = 0,
        detail: Float = 0.5f
    ): List<AdaptiveTileLayout.Placement> = AdaptiveTileLayout.create(
        width = width,
        height = height,
        columns = 10,
        tiles = aspectRatios.mapIndexed { index, aspectRatio ->
            AdaptiveTileLayout.Tile(
                aspectRatio = aspectRatio,
                color = LabColor(index.toDouble(), 0.0, 0.0)
            )
        },
        repeatDistance = repeatDistance,
        colorAt = { _, _ -> LabColor(0.0, 0.0, 0.0) },
        detailAt = { detail }
    )

    private companion object {
        const val DELTA = 0.001f
    }
}
