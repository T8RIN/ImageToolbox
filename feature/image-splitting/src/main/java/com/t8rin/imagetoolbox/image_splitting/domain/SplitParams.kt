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

package com.t8rin.imagetoolbox.image_splitting.domain

import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality

data class SplitParams(
    val rowsCount: Int,
    val columnsCount: Int,
    val rowPercentages: List<Float>,
    val columnPercentages: List<Float>,
    val imageFormat: ImageFormat,
    val quality: Quality,
) {
    fun withAspectRatio(
        targetAspectRatio: Float,
        maxRows: Int = rowsCount,
        maxColumns: Int = columnsCount
    ): SplitParams {
        require(targetAspectRatio > 0f) { "aspectRatio must be > 0" }

        var bestRows = 1
        var bestCols = 1

        for (rows in 1..maxRows) {
            val tileHeight = 1f / rows
            val tileWidth = tileHeight * targetAspectRatio
            val cols = (1f / tileWidth).toInt()
            if (cols in 1..maxColumns) {
                bestRows = rows
                bestCols = cols
            }
        }

        val rowsCount = bestRows
        val columnsCount = bestCols

        val rowPercentages = List(rowsCount) { 1f / rowsCount }.let { rows ->
            rows.dropLast(1) + (1f - rows.dropLast(1).sum())
        }

        val columnPercentages = List(columnsCount) { 1f / columnsCount }.let { cols ->
            cols.dropLast(1) + (1f - cols.dropLast(1).sum())
        }


        return this.copy(
            rowsCount = rowsCount,
            columnsCount = columnsCount,
            rowPercentages = rowPercentages,
            columnPercentages = columnPercentages
        )
    }

    companion object {
        val Default by lazy {
            SplitParams(
                rowsCount = 2,
                columnsCount = 2,
                rowPercentages = emptyList(),
                columnPercentages = emptyList(),
                imageFormat = ImageFormat.Default,
                quality = Quality.Base()
            )
        }
    }
}