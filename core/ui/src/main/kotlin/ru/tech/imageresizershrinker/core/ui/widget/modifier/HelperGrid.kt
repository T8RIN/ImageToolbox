/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Pt
import ru.tech.imageresizershrinker.core.domain.model.pt
import ru.tech.imageresizershrinker.core.ui.theme.toColor

@Stable
@Immutable
data class HelperGridParams(
    val color: Color = Color.Black,
    val cellWidth: Pt = 20.pt,
    val cellHeight: Pt = 20.pt,
    val linesWidth: Dp = 1.dp,
    val enabled: Boolean = false,
) {

    companion object {
        val Saver = listSaver<HelperGridParams, Float>(
            save = {
                listOf(
                    it.color.toArgb().toFloat(),
                    it.cellWidth.value,
                    it.cellHeight.value,
                    it.linesWidth.value,
                    if (it.enabled) 1f else 0f
                )
            },
            restore = {
                HelperGridParams(
                    color = it[0].toInt().toColor(),
                    cellWidth = it[1].pt,
                    cellHeight = it[2].pt,
                    linesWidth = it[3].dp,
                    enabled = if (it[4] > 0f) true else false
                )
            }
        )
    }

}

fun Modifier.helperGrid(
    params: HelperGridParams,
) = drawWithCache {
    with(params) {
        val width = size.width
        val height = size.height

        val canvasSize = IntegerSize(
            width = width.toInt(),
            height = height.toInt()
        )

        val cellWidthPx = cellWidth.toPx(canvasSize)
        val cellHeightPx = cellHeight.toPx(canvasSize)

        val horizontalSteps = (width / cellWidthPx).toInt()
        val verticalSteps = (height / cellHeightPx).toInt()

        onDrawWithContent {
            drawContent()

            if (enabled) {
                for (x in 0..horizontalSteps) {
                    drawLine(
                        color = color,
                        start = Offset(x = x * cellWidthPx, y = 0f),
                        end = Offset(x = x * cellWidthPx, y = height),
                    )
                }

                for (y in 0..verticalSteps) {
                    drawLine(
                        color = color,
                        start = Offset(x = 0f, y = y * cellHeightPx),
                        end = Offset(x = width, y = y * cellHeightPx),
                    )
                }
            }
        }
    }

}