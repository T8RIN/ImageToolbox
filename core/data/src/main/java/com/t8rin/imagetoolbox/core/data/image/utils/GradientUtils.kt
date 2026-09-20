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
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.SweepGradient
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import com.t8rin.imagetoolbox.core.domain.model.GradientType

fun GradientFill.createShader(
    width: Float,
    height: Float,
    left: Float = 0f,
    top: Float = 0f
): Shader {
    val colors = palette.colors.map { it.colorInt }.toIntArray()
    val centerX = centerXFor(width, left)
    val centerY = centerYFor(height, top)
    return when (type) {
        GradientType.Linear -> {
            val line = lineFor(width, height, left, top)
            LinearGradient(
                line.startX, line.startY, line.endX, line.endY,
                colors, null, Shader.TileMode.CLAMP
            )
        }

        GradientType.Radial -> RadialGradient(
            centerX, centerY, radiusFor(width, height),
            colors, null, Shader.TileMode.CLAMP
        )

        GradientType.Sweep -> SweepGradient(centerX, centerY, colors, null).apply {
            setLocalMatrix(Matrix().apply {
                setRotate(angle.takeIf(Float::isFinite) ?: 0f, centerX, centerY)
            })
        }
    }
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
                shader = gradient.createShader(width.toFloat(), height.toFloat())
            })
    }
}