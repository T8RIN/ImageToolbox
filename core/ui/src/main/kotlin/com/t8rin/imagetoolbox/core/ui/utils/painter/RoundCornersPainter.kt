/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.utils.painter

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.painter.Painter

private class RoundCornersPainter(
    private val wrappedPainter: Painter,
    private val cornerFactor: Float
) : Painter() {

    override val intrinsicSize: Size
        get() = wrappedPainter.intrinsicSize

    override fun DrawScope.onDraw() {
        val cornerRadius = size.minDimension / 2f * cornerFactor.coerceIn(0f, 1f)
        clipPath(
            Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(Offset.Zero, size),
                        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                    )
                )
            }
        ) {
            with(wrappedPainter) {
                draw(size)
            }
        }
    }
}

fun Painter.roundCorners(size: Float): Painter = RoundCornersPainter(this, size)
