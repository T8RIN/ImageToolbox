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

package com.t8rin.imagetoolbox.feature.ai_tools.data.model

import kotlin.math.ceil

internal data class TileGrid(
    val imageWidth: Int,
    val imageHeight: Int,
    val tileLimit: Int,
    val overlap: Int,
    val step: Int,
    val columns: Int,
    val rows: Int
) {
    fun tiles(filesFor: (Int) -> TileFiles): List<Tile> = buildList {
        var index = 0
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val x = column * step
                val y = row * step
                val width = minOf(x + tileLimit, imageWidth) - x
                val height = minOf(y + tileLimit, imageHeight) - y

                if (width > 0 && height > 0) {
                    add(
                        Tile(
                            index = index,
                            area = TileArea(
                                x = x,
                                y = y,
                                width = width,
                                height = height
                            ),
                            position = TilePosition(
                                column = column,
                                row = row
                            ),
                            files = filesFor(index)
                        )
                    )
                    index++
                }
            }
        }
    }

    companion object {
        fun from(
            imageWidth: Int,
            imageHeight: Int,
            tileLimit: Int,
            overlap: Int
        ): TileGrid {
            val step = tileLimit - overlap
            val columns = if (imageWidth <= tileLimit) {
                1
            } else {
                ceil((imageWidth - overlap).toFloat() / step).toInt()
            }
            val rows = if (imageHeight <= tileLimit) {
                1
            } else {
                ceil((imageHeight - overlap).toFloat() / step).toInt()
            }

            return TileGrid(
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                tileLimit = tileLimit,
                overlap = overlap,
                step = step,
                columns = columns,
                rows = rows
            )
        }
    }
}