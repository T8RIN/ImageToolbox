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

package com.websitebeaver.documentscanner.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import com.websitebeaver.documentscanner.R
import com.websitebeaver.documentscanner.extensions.drawCheck

/**
 * This class creates a circular done button by modifying an image button. The user presses
 * this button once they finish cropping an image
 *
 * @param context image button context
 * @param attrs image button attributes
 * @constructor creates done button
 */
class DoneButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {
    /**
     * @property ring the button's outer ring
     */
    private val ring = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * @property circle the button's inner circle
     */
    private val circle = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        // set outer ring style
        ring.color = Color.WHITE
        ring.style = Paint.Style.STROKE
        ring.strokeWidth = resources.getDimension(R.dimen.large_button_ring_thickness)

        // set inner circle style
        circle.color = ContextCompat.getColor(context, R.color.done_button_inner_circle_color)
        circle.style = Paint.Style.FILL
    }

    /**
     * This gets called repeatedly. We use it to draw the done button.
     *
     * @param canvas the image button canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // calculate button center point, outer ring radius, and inner circle radius
        val centerX = width.toFloat() / 2
        val centerY = height.toFloat() / 2
        val outerRadius = (width.toFloat() - ring.strokeWidth) / 2
        val innerRadius = outerRadius - resources.getDimension(
            R.dimen.large_button_outer_ring_offset
        )

        // draw outer ring
        canvas.drawCircle(centerX, centerY, outerRadius, ring)

        // draw inner circle
        canvas.drawCircle(centerX, centerY, innerRadius, circle)

        // draw check icon since it gets covered by inner circle
        canvas.drawCheck(centerX, centerY, drawable)
    }
}