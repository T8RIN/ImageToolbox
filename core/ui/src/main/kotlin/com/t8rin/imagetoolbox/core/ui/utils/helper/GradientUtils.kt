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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.graphics.Matrix
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SweepGradientShader
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import com.t8rin.imagetoolbox.core.domain.model.GradientType

fun GradientFill.toBrush(shaderSize: Size? = null): ShaderBrush = object : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val actualSize = shaderSize ?: size
        val colors = palette.colors.map { Color(it.colorInt) }
        val center = Offset(centerXFor(actualSize.width), centerYFor(actualSize.height))
        return when (type) {
            GradientType.Linear -> {
                val line = lineFor(actualSize.width, actualSize.height)
                LinearGradientShader(
                    from = Offset(line.startX, line.startY),
                    to = Offset(line.endX, line.endY),
                    colors = colors
                )
            }

            GradientType.Radial -> RadialGradientShader(
                center = center,
                radius = radiusFor(actualSize.width, actualSize.height),
                colors = colors
            )

            GradientType.Sweep -> SweepGradientShader(center = center, colors = colors).apply {
                setLocalMatrix(Matrix().apply {
                    setRotate(angle.takeIf(Float::isFinite) ?: 0f, center.x, center.y)
                })
            }
        }
    }
}