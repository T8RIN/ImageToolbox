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

package com.t8rin.imagetoolbox.feature.maze_generation.rendering

import android.graphics.Picture
import android.graphics.RectF
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import com.caverock.androidsvg.SVG
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

/** Retains SVG drawing commands; zoom never enlarges a rasterized preview bitmap. */
class MazeSvgPainter
private constructor(private val picture: Picture, override val intrinsicSize: Size) : Painter() {
    override fun DrawScope.onDraw() {
        drawContext.canvas.nativeCanvas.drawPicture(picture, RectF(0f, 0f, size.width, size.height))
    }

    companion object {
        suspend fun create(source: String, size: IntegerSize): MazeSvgPainter {
            currentCoroutineContext().ensureActive()
            val svg = SVG.getFromString(source)
            val picture = svg.renderToPicture(size.width, size.height)
            currentCoroutineContext().ensureActive()
            return MazeSvgPainter(picture, Size(size.width.toFloat(), size.height.toFloat()))
        }
    }
}
