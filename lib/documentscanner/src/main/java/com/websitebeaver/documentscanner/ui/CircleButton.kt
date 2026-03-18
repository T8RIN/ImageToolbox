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
import com.websitebeaver.documentscanner.R

/**
 * This class creates a circular done button by modifying an image button. This is used for the
 * add new document button and retake photo button.
 *
 * @param context image button context
 * @param attrs image button attributes
 * @constructor creates circle button
 */
class CircleButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {
    /**
     * @property ring the button's outer ring
     */
    private val ring = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        // set outer ring style
        ring.color = Color.WHITE
        ring.style = Paint.Style.STROKE
        ring.strokeWidth = resources.getDimension(R.dimen.small_button_ring_thickness)
    }

    /**
     * This gets called repeatedly. We use it to draw the button
     *
     * @param canvas the image button canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // draw outer ring
        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width.toFloat() - ring.strokeWidth) / 2,
            ring
        )
    }
}