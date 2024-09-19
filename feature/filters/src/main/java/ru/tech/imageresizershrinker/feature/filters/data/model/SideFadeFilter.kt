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

package ru.tech.imageresizershrinker.feature.filters.data.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import ru.tech.imageresizershrinker.core.data.utils.safeConfig
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.FadeSide
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.SideFadeParams
import kotlin.math.roundToInt


internal class SideFadeFilter(
    override val value: SideFadeParams = SideFadeParams.Relative(FadeSide.Start, 0.5f),
) : Transformation<Bitmap>, Filter.SideFade {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val bitmap = input.copy(input.safeConfig, true).apply { setHasAlpha(true) }
        val fadeSize: Int = when (value) {
            is SideFadeParams.Absolute -> value.size
            is SideFadeParams.Relative -> {
                when (value.side) {
                    FadeSide.Start, FadeSide.End -> {
                        bitmap.width * value.scale
                    }

                    FadeSide.Bottom, FadeSide.Top -> {
                        bitmap.height * value.scale
                    }
                }.roundToInt()
            }
        }
        val canvas = Canvas(bitmap)
        canvas.drawPaint(value.side.getPaint(input, fadeSize))
        return bitmap
    }

    private fun FadeSide.getPaint(
        bmp: Bitmap,
        length: Int,
    ): Paint {
        val paint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        }

        val width = bmp.width.toFloat()
        val height = bmp.height.toFloat()

        val sideLength = length.toFloat()
        val g1x: Float
        val g1y: Float
        val g2x: Float
        val g2y: Float
        val startColor: Int
        val endColor: Int
        when (this) {
            FadeSide.Start -> {
                //left
                g1x = 0f
                g1y = height / 2
                g2x = sideLength
                g2y = height / 2
                startColor = Color.Transparent.toArgb()
                endColor = Color.Black.toArgb()
            }

            FadeSide.Top -> {
                //top
                g1x = width / 2
                g1y = 0f
                g2x = width / 2
                g2y = sideLength
                startColor = Color.Transparent.toArgb()
                endColor = Color.Black.toArgb()
            }

            FadeSide.End -> {
                //right
                g1x = width
                g1y = height / 2
                g2x = width - sideLength
                g2y = height / 2
                startColor = Color.Transparent.toArgb()
                endColor = Color.Black.toArgb()
            }

            FadeSide.Bottom -> {
                //bottom
                g1x = width / 2
                g1y = height
                g2x = width / 2
                g2y = height - sideLength
                startColor = Color.Transparent.toArgb()
                endColor = Color.Black.toArgb()
            }
        }
        paint.setShader(
            LinearGradient(
                g1x,
                g1y,
                g2x,
                g2y,
                startColor,
                endColor,
                Shader.TileMode.CLAMP
            )
        )
        return paint
    }

}