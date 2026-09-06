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

package com.t8rin.imagetoolbox.core.data.image.utils

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette

fun GradientPalette.createShader(
    width: Float,
    height: Float,
    angle: Float = 0f,
    left: Float = 0f,
    top: Float = 0f
): Shader {
    val line = GradientFill(this, angle).lineFor(width, height, left, top)
    return LinearGradient(
        line.startX,
        line.startY,
        line.endX,
        line.endY,
        colors.map { it.colorInt }.toIntArray(),
        null,
        Shader.TileMode.CLAMP
    )
}

fun Canvas.drawBackground(color: Int, gradient: GradientFill?) {
    if (gradient == null) {
        drawColor(color)
    } else {
        drawRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
                shader =
                    gradient.palette.createShader(width.toFloat(), height.toFloat(), gradient.angle)
            })
    }
}