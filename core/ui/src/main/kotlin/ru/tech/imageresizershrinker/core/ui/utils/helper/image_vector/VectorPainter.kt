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

package ru.tech.imageresizershrinker.core.ui.utils.helper.image_vector

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.LayoutDirection

/**
 * [Painter] implementation that abstracts the drawing of a Vector graphic.
 * This can be represented by either a [ImageVector] or a programmatic
 * composition of a vector
 */
internal class VectorPainter internal constructor(
    root: GroupComponent = GroupComponent()
) : Painter() {

    internal var size by mutableStateOf(Size.Zero)

    internal var autoMirror by mutableStateOf(false)

    /**
     * configures the intrinsic tint that may be defined on a VectorPainter
     */
    internal var intrinsicColorFilter: ColorFilter?
        get() = vector.intrinsicColorFilter
        set(value) {
            vector.intrinsicColorFilter = value
        }

    internal var viewportSize: Size
        get() = vector.viewportSize
        set(value) {
            vector.viewportSize = value
        }

    internal var name: String
        get() = vector.name
        set(value) {
            vector.name = value
        }

    internal val vector = VectorComponent(root).apply {
        invalidateCallback = {
            if (drawCount == invalidateCount) {
                invalidateCount++
            }
        }
    }

    private var invalidateCount by mutableIntStateOf(0)

    private var currentAlpha: Float = 1.0f
    private var currentColorFilter: ColorFilter? = null

    override val intrinsicSize: Size
        get() = size

    private var drawCount = -1

    override fun DrawScope.onDraw() {
        with(vector) {
            val filter = currentColorFilter ?: intrinsicColorFilter
            if (autoMirror && layoutDirection == LayoutDirection.Rtl) {
                mirror {
                    draw(currentAlpha, filter)
                }
            } else {
                draw(currentAlpha, filter)
            }
        }
        // This assignment is necessary to obtain invalidation callbacks as the state is
        // being read here which adds this callback to the snapshot observation
        drawCount = invalidateCount
    }

    override fun applyAlpha(alpha: Float): Boolean {
        currentAlpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        currentColorFilter = colorFilter
        return true
    }

    private inline fun DrawScope.mirror(block: DrawScope.() -> Unit) {
        scale(-1f, 1f, block = block)
    }
}