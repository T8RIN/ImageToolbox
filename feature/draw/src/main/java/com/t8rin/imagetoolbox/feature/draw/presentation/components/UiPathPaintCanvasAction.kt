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

package com.t8rin.imagetoolbox.feature.draw.presentation.components

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePaint
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.createFilter
import com.t8rin.imagetoolbox.core.ui.utils.helper.scaleToFitCanvas
import com.t8rin.imagetoolbox.core.ui.utils.helper.toImageModel
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.clipBitmap
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawRepeatedImageOnPath
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawRepeatedTextOnPath
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.overlay
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.pathEffectPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.rememberPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.transformationsForMode

@Composable
internal fun Canvas.UiPathPaintCanvasAction(
    uiPathPaint: UiPathPaint,
    invalidations: Int,
    onInvalidate: () -> Unit,
    canvasSize: IntegerSize,
    pathsCount: Int,
    backgroundColor: Color,
    drawImageBitmap: ImageBitmap,
    drawBitmap: ImageBitmap,
    onClearDrawPath: () -> Unit,
    onRequestFiltering: suspend (Bitmap, List<Filter<*>>) -> Bitmap?,
) = with(nativeCanvas) {
    val (nonScaledPath, strokeWidth, brushSoftness, drawColor, isEraserOn, drawMode, size, drawPathMode, drawLineStyle) = uiPathPaint

    val path by remember(nonScaledPath, canvasSize, size) {
        derivedStateOf {
            nonScaledPath.scaleToFitCanvas(
                currentSize = canvasSize,
                oldSize = size
            ).asAndroidPath()
        }
    }

    if (drawMode is DrawMode.PathEffect && !isEraserOn) {
        var shaderSource by remember(backgroundColor) {
            mutableStateOf<ImageBitmap?>(null)
        }
        LaunchedEffect(shaderSource, invalidations) {
            if (shaderSource == null || invalidations <= pathsCount) {
                shaderSource = onRequestFiltering(
                    drawImageBitmap.overlay(drawBitmap)
                        .asAndroidBitmap(),
                    transformationsForMode(
                        drawMode = drawMode,
                        canvasSize = canvasSize
                    )
                )?.asImageBitmap()?.clipBitmap(
                    path = path.asComposePath(),
                    paint = pathEffectPaint(
                        strokeWidth = strokeWidth,
                        drawPathMode = drawPathMode,
                        canvasSize = canvasSize
                    ).asComposePaint()
                )?.also {
                    it.prepareToDraw()
                    onInvalidate()
                }
            }
        }
        if (shaderSource != null) {
            LaunchedEffect(shaderSource) {
                onClearDrawPath()
            }
            val imagePaint = remember { Paint() }
            drawImage(
                image = shaderSource!!,
                topLeftOffset = Offset.Zero,
                paint = imagePaint
            )
        }
    } else if (drawMode is DrawMode.SpotHeal && !isEraserOn) {
        val paint = remember(uiPathPaint, canvasSize) {
            val isSharpEdge = drawPathMode.isSharpEdge
            val isFilled = drawPathMode.isFilled
            val stroke = strokeWidth.toPx(canvasSize)

            Paint().apply {
                if (isFilled) {
                    style = PaintingStyle.Fill
                } else {
                    style = PaintingStyle.Stroke
                    this.strokeWidth = stroke
                    if (isSharpEdge) {
                        strokeCap = StrokeCap.Square
                    } else {
                        strokeCap = StrokeCap.Round
                        strokeJoin = StrokeJoin.Round
                    }
                }

                color = Color.White
            }
        }

        var shaderSource by remember(backgroundColor) {
            mutableStateOf<ImageBitmap?>(null)
        }
        LaunchedEffect(shaderSource, invalidations) {
            if (shaderSource == null || invalidations <= pathsCount) {
                shaderSource = onRequestFiltering(
                    drawImageBitmap.overlay(drawBitmap).asAndroidBitmap(),
                    listOf(
                        createFilter<ImageModel, Filter.SpotHeal>(
                            createBitmap(
                                width = canvasSize.width,
                                height = canvasSize.height
                            ).applyCanvas {
                                drawColor(Color.Black.toArgb())
                                drawPath(
                                    path,
                                    paint.asFrameworkPaint()
                                )
                            }.toImageModel()
                        )
                    )
                )?.asImageBitmap()?.clipBitmap(
                    path = path.asComposePath(),
                    paint = paint.apply {
                        blendMode = BlendMode.Clear
                    }
                )?.also {
                    it.prepareToDraw()
                    onInvalidate()
                }
            }
        }
        if (shaderSource != null) {
            LaunchedEffect(shaderSource) {
                onClearDrawPath()
                onInvalidate()
            }
            val imagePaint = remember { Paint() }
            drawImage(
                image = shaderSource!!,
                topLeftOffset = Offset.Zero,
                paint = imagePaint
            )
        }
    } else {
        val pathPaint by rememberPaint(
            strokeWidth = strokeWidth,
            isEraserOn = isEraserOn,
            drawColor = drawColor,
            brushSoftness = brushSoftness,
            drawMode = drawMode,
            canvasSize = canvasSize,
            drawPathMode = drawPathMode,
            drawLineStyle = drawLineStyle
        )
        if (drawMode is DrawMode.Text && !isEraserOn) {
            if (drawMode.isRepeated) {
                drawRepeatedTextOnPath(
                    text = drawMode.text,
                    path = path,
                    paint = pathPaint,
                    interval = drawMode.repeatingInterval.toPx(canvasSize)
                )
            } else {
                drawTextOnPath(drawMode.text, path, 0f, 0f, pathPaint)
            }
        } else if (drawMode is DrawMode.Image && !isEraserOn) {
            drawRepeatedImageOnPath(
                drawMode = drawMode,
                strokeWidth = strokeWidth,
                canvasSize = canvasSize,
                path = path,
                paint = pathPaint,
                invalidations = invalidations
            )
        } else {
            drawPath(path, pathPaint)
        }
    }
}