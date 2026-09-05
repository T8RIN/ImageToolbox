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

package com.t8rin.imagetoolbox.feature.draw.presentation.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.core.graphics.createBitmap
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.createFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.ui.utils.helper.scaleToFitCanvas
import com.t8rin.imagetoolbox.core.utils.toImageModel
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.createDrawPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawBitmapThroughPath
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawPathWithGradient
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawRepeatedBitmapOnPath
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawRepeatedTextOnPath
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.pathEffectPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.transformationsForMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.withPathGradient
import com.t8rin.trickle.WarpBrush
import com.t8rin.trickle.WarpEngine
import com.t8rin.trickle.WarpMode
import kotlin.math.roundToInt
import android.graphics.Paint as AndroidPaint

internal suspend fun Canvas.drawCommittedPath(
    uiPathPaint: UiPathPaint,
    canvasSize: IntegerSize,
    context: Context,
    source: () -> Bitmap,
    onRequestFiltering: suspend (Bitmap, List<Filter<*>>) -> Bitmap?,
    preparedEffect: Bitmap? = null
) {
    val (nonScaledPath, strokeWidth, brushSoftness, drawColor, isEraserOn, drawMode, size, drawPathMode, drawLineStyle, gradientPalette) = uiPathPaint
    val path = nonScaledPath.scaleToFitCanvas(canvasSize, size).asAndroidPath()
    if (!isEraserOn && (drawMode is DrawMode.PathEffect || drawMode is DrawMode.SpotHeal)) {
        val paint = pathEffectPaint(strokeWidth, drawPathMode, canvasSize)
        val effect = preparedEffect ?: run {
            val filters = if (drawMode is DrawMode.SpotHeal) {
                val mask = createBitmap(canvasSize.width, canvasSize.height)
                Canvas(mask).apply {
                    drawColor(android.graphics.Color.BLACK)
                    drawPath(path, AndroidPaint(paint).apply {
                        color = android.graphics.Color.WHITE
                        xfermode = null
                    })
                }
                listOf(createFilter<Pair<ImageModel, SpotHealMode>, Filter.SpotHeal>(mask.toImageModel() to drawMode.mode))
            } else transformationsForMode(drawMode, canvasSize)
            onRequestFiltering(source(), filters)
        }
        effect?.let {
            drawBitmapThroughPath(it, path, paint)
        }
    } else if (drawMode is DrawMode.Warp && !isEraserOn) {
        val engine = WarpEngine(source())
        try {
            drawMode.strokes.forEach {
                val stroke = it.scaleToFitCanvas(canvasSize, size)
                engine.applyStroke(
                    stroke.fromX, stroke.fromY, stroke.toX, stroke.toY,
                    WarpBrush(strokeWidth.toPx(canvasSize), drawMode.strength, drawMode.hardness),
                    WarpMode.valueOf(drawMode.warpMode.name)
                )
            }
            drawBitmap(engine.render(), 0f, 0f, null)
        } finally {
            engine.release()
        }
    } else {
        val pathPaint = createDrawPaint(
            strokeWidth = strokeWidth,
            isEraserOn = isEraserOn,
            drawColor = drawColor,
            brushSoftness = brushSoftness,
            drawMode = drawMode,
            canvasSize = canvasSize,
            drawPathMode = drawPathMode,
            drawLineStyle = drawLineStyle,
            context = context
        )
        if (drawMode is DrawMode.Text && !isEraserOn) {
            val textPaint = if (gradientPalette != null) {
                pathPaint.withPathGradient(
                    path = path,
                    palette = gradientPalette
                )
            } else pathPaint
            if (drawMode.isRepeated) {
                drawRepeatedTextOnPath(
                    text = drawMode.text,
                    path = path,
                    paint = textPaint,
                    interval = drawMode.repeatingInterval.toPx(canvasSize)
                )
            } else if (drawMode.text.isNotEmpty() && !path.isEmpty) {
                drawTextOnPath(drawMode.text, path, 0f, 0f, textPaint)
            }
        } else if (drawMode is DrawMode.Image && !isEraserOn) {
            val image = context.imageLoader.execute(
                ImageRequest.Builder(context).data(drawMode.imageData)
                    .size(strokeWidth.toPx(canvasSize).roundToInt().coerceAtLeast(1)).build()
            ).image?.toBitmap()
            image?.let {
                drawRepeatedBitmapOnPath(
                    it,
                    path,
                    pathPaint,
                    drawMode.repeatingInterval.toPx(canvasSize)
                )
            }
        } else if (drawPathMode is DrawPathMode.Outlined && !isEraserOn) {
            drawPathMode.fillColor?.let { fillColor ->
                val filledPaint = AndroidPaint().apply {
                    set(pathPaint)
                    style = AndroidPaint.Style.FILL
                    color = fillColor.colorInt
                    if (Color(fillColor.colorInt).alpha == 1f) {
                        alpha = (drawColor.alpha * 255).roundToInt().coerceIn(0, 255)
                    }
                    pathEffect = null
                }

                drawPath(path, filledPaint)
            }
            drawPathWithGradient(
                path = path,
                paint = pathPaint,
                palette = gradientPalette.takeUnless { isEraserOn },
                isFilled = false,
                canvasSize = canvasSize,
                softnessRadius = brushSoftness.toPx(canvasSize)
            )
        } else {
            drawPathWithGradient(
                path = path,
                paint = pathPaint,
                palette = gradientPalette.takeUnless { isEraserOn },
                isFilled = !isEraserOn && drawPathMode.isFilled,
                canvasSize = canvasSize,
                softnessRadius = brushSoftness.toPx(canvasSize)
            )
        }
    }
}