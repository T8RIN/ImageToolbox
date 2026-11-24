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

package com.t8rin.documentscanner.extensions

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import com.t8rin.documentscanner.models.Quad

/**
 * This draws a quad (used to draw cropper). It draws 4 circles and
 * 4 connecting lines
 *
 * @param quad 4 corners
 * @param pointRadius corner circle radius
 * @param cropperLineStyle quad line style (color, thickness for example)
 * @param cropperCornerStyle quad corner style (color, thickness for example)
 * @param cropperCornerOutlineStyle quad corner outer outline style
 */
fun Canvas.drawQuad(
    quad: Quad,
    pointRadius: Float,
    cropperLineStyle: Paint,
    cropperCornerStyle: Paint,
    cropperCornerOutlineStyle: Paint
) {
    for (edge in quad.edges) {
        drawLine(edge.from.x, edge.from.y, edge.to.x, edge.to.y, cropperLineStyle)
    }

    for (cornerPoint in quad.corners.values) {
        drawCircle(
            cornerPoint.x,
            cornerPoint.y,
            pointRadius + cropperCornerOutlineStyle.strokeWidth,
            cropperCornerOutlineStyle
        )
        drawCircle(cornerPoint.x, cornerPoint.y, pointRadius, cropperCornerStyle)
    }
}

/**
 * This draws the check icon on the finish document scan button. It's needed
 * because the inner circle covers the check icon.
 *
 * @param buttonCenterX the button center x coordinate
 * @param buttonCenterY the button center y coordinate
 * @param drawable the check icon
 */
fun Canvas.drawCheck(
    buttonCenterX: Float,
    buttonCenterY: Float,
    drawable: Drawable,
    tintColor: Int? = null
) {
    val mutate = drawable.constantState?.newDrawable()?.mutate()
    tintColor?.let { mutate?.setTint(it) }
    mutate?.setBounds(
        (buttonCenterX - drawable.intrinsicWidth.toFloat() / 2).toInt(),
        (buttonCenterY - drawable.intrinsicHeight.toFloat() / 2).toInt(),
        (buttonCenterX + drawable.intrinsicWidth.toFloat() / 2).toInt(),
        (buttonCenterY + drawable.intrinsicHeight.toFloat() / 2).toInt()
    )
    mutate?.draw(this)
}
