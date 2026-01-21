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

package com.t8rin.imagetoolbox.core.filters.data

import android.graphics.Bitmap
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.FadeSide

fun FadeSide.getPaint(
    bmp: Bitmap,
    length: Int,
    strength: Float
): Paint {
    val paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    val w = bmp.width.toFloat()
    val h = bmp.height.toFloat()
    val l = length.toFloat()

    val gradientStart = l * (1f - strength.coerceAtLeast(0.001f))

    val (x1, y1, x2, y2) = when (this) {
        FadeSide.Start -> {
            arrayOf(
                gradientStart, h / 2,
                l, h / 2
            )
        }

        FadeSide.Top -> {
            arrayOf(
                w / 2, gradientStart,
                w / 2, l
            )
        }

        FadeSide.End -> {
            arrayOf(
                w - gradientStart, h / 2,
                w - l, h / 2
            )
        }

        FadeSide.Bottom -> {
            arrayOf(
                w / 2, h - gradientStart,
                w / 2, h - l
            )
        }
    }

    paint.shader = LinearGradient(
        x1, y1, x2, y2,
        intArrayOf(
            Color.Black.copy(alpha = 0f).toArgb(),
            Color.Black.toArgb()
        ),
        floatArrayOf(
            0f,
            1f
        ),
        Shader.TileMode.CLAMP
    )

    return paint
}