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

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.theme.blend


fun Modifier.transparencyChecker(
    colorScheme: ColorScheme? = null,
    checkerWidth: Dp = 10.dp,
    checkerHeight: Dp = 10.dp,
) = this.composed {
    val scheme = colorScheme ?: MaterialTheme.colorScheme

    Modifier.drawWithCache {
        val width = this.size.width
        val height = this.size.height

        val checkerWidthPx = checkerWidth.toPx()
        val checkerHeightPx = checkerHeight.toPx()

        val horizontalSteps = (width / checkerWidthPx).toInt()
        val verticalSteps = (height / checkerHeightPx).toInt()

        onDrawBehind {
            drawRect(
                scheme.surfaceColorAtElevation(20.dp).blend(scheme.surface, 0.5f)
            )
            for (y in 0..verticalSteps) {
                for (x in 0..horizontalSteps) {
                    val isGrayTile = ((x + y) % 2 == 1)
                    drawRect(
                        color = if (isGrayTile) {
                            scheme.surfaceColorAtElevation(20.dp)
                        } else scheme.surface,
                        topLeft = Offset(x * checkerWidthPx, y * checkerHeightPx),
                        size = Size(checkerWidthPx, checkerHeightPx)
                    )
                }
            }
        }
    }
}