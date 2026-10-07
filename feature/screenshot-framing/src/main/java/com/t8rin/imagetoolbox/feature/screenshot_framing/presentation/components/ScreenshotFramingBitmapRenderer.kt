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

package com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.components

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withSave
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.model.ScreenshotFramingParams
import kotlin.math.min
import kotlin.math.sqrt

internal fun renderScreenshotFramingBitmap(
    image: Bitmap,
    params: ScreenshotFramingParams,
    maxBitmapPixels: Float = 16_777_216f
): Bitmap {
    val unit = min(image.width, image.height) / 100f
    val blur = if (params.showShadow) params.shadowBlur * unit else 0f
    val shadowOffset = blur / 2f
    val inset = params.padding * unit + blur * 2f + shadowOffset
    var width = image.width + inset * 2f
    var height = image.height + inset * 2f
    params.aspectRatio.value.takeIf { it.isFinite() && it > 0f }?.let { ratio ->
        if (width / height < ratio) width = height * ratio
        else height = width / ratio
    }

    val runtime = Runtime.getRuntime()
    val availableMemory = runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory()
    val pixelLimit = min(maxBitmapPixels, availableMemory * 0.3f / 4f).coerceAtLeast(1f)
    val scale = minOf(
        1f,
        sqrt(pixelLimit / (width * height)),
        8192f / maxOf(width, height)
    )
    val result = createBitmap(
        (width * scale).toInt().coerceAtLeast(1),
        (height * scale).toInt().coerceAtLeast(1)
    )
    val imageWidth = image.width * scale
    val imageHeight = image.height * scale
    val rect = RectF(
        (result.width - imageWidth) / 2f,
        (result.height - imageHeight) / 2f,
        (result.width + imageWidth) / 2f,
        (result.height + imageHeight) / 2f
    )
    val cornerRadius = params.cornerRadius * unit * scale
    val colors = params.backgroundColors.map { it.copy(alpha = 1f).toArgb() }
    Canvas(result).apply {
        drawPaint(
            Paint().apply {
                color = colors.first()
                if (params.useGradient) {
                    shader = LinearGradient(
                        0f,
                        0f,
                        result.width.toFloat(),
                        result.height.toFloat(),
                        colors.toIntArray(),
                        null,
                        Shader.TileMode.CLAMP
                    )
                }
            }
        )
        if (blur > 0f && params.shadowOpacity > 0) {
            withSave {
                // Keep the shadow outside the image, including transparent source pixels.
                clipPath(
                    Path().apply {
                        addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
                        fillType = Path.FillType.INVERSE_WINDING
                    }
                )
                drawRoundRect(
                    rect,
                    cornerRadius,
                    cornerRadius,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.BLACK
                        setShadowLayer(
                            blur * scale,
                            0f,
                            shadowOffset * scale,
                            Color.Black.copy(alpha = params.shadowOpacity / 100f).toArgb()
                        )
                    }
                )
            }
        }
        drawRoundRect(
            rect,
            cornerRadius,
            cornerRadius,
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                shader = BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP).apply {
                    setLocalMatrix(
                        Matrix().apply {
                            setScale(scale, scale)
                            postTranslate(rect.left, rect.top)
                        }
                    )
                }
            }
        )
    }
    return result
}