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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.t8rin.imagetoolbox.core.data.image.utils.createShader
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import kotlin.math.roundToInt

internal fun Canvas.drawOutlinedFill(
    path: Path,
    strokePaint: Paint,
    mode: DrawPathMode.Outlined,
    alpha: Float
) {
    val palette = mode.fillGradientPalette
    val fillColor = mode.fillColor?.colorInt
    if (palette == null && fillColor == null) return

    val paint = Paint(strokePaint).apply {
        style = Paint.Style.FILL
        color = if (palette == null) fillColor ?: Color.TRANSPARENT else Color.WHITE
        shader = palette?.let {
            val bounds = RectF()
            path.computeBounds(bounds, true)
            it.createShader(bounds.width(), bounds.height(), left = bounds.left, top = bounds.top)
        }
        if (palette != null || Color.alpha(color) == 255) {
            this.alpha = (alpha * 255).roundToInt().coerceIn(0, 255)
        }
        pathEffect = null
    }
    drawPath(path, paint)
}