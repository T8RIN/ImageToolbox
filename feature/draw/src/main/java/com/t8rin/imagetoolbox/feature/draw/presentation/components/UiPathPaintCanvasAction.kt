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

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.createFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.ui.utils.helper.scaleToFitCanvas
import com.t8rin.imagetoolbox.core.ui.utils.helper.toImageModel
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.clipBitmap
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawRepeatedImageOnPath
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawRepeatedTextOnPath
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.overlay
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.pathEffectPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.rememberPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.transformationsForMode
import com.t8rin.trickle.WarpBrush
import com.t8rin.trickle.WarpEngine
import com.t8rin.trickle.WarpMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@SuppressLint("ComposableNaming")
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
            withContext(Dispatchers.Default) {
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

        var isLoading by remember {
            mutableStateOf(false)
        }

        var progress by remember {
            mutableFloatStateOf(0f)
        }

        var shaderSource by remember(backgroundColor) {
            mutableStateOf<ImageBitmap?>(null)
        }
        LaunchedEffect(shaderSource, invalidations) {
            withContext(Dispatchers.Default) {
                if (shaderSource == null || invalidations <= pathsCount) {
                    isLoading = true
                    val job = launch {
                        while (progress < 0.5f && isActive && isLoading) {
                            progress += 0.01f
                            delay(100)
                        }
                        while (progress < 0.75f && isActive && isLoading) {
                            progress += 0.0025f
                            delay(100)
                        }
                        while (progress < 1f && isActive && isLoading) {
                            progress += 0.0025f
                            delay(500)
                        }
                    }

                    shaderSource = withContext(Dispatchers.IO) {
                        onRequestFiltering(
                            drawImageBitmap.overlay(drawBitmap).asAndroidBitmap(),
                            listOf(
                                createFilter<Pair<ImageModel, SpotHealMode>, Filter.SpotHeal>(
                                    Pair(
                                        createBitmap(
                                            width = canvasSize.width,
                                            height = canvasSize.height
                                        ).applyCanvas {
                                            drawColor(Color.Black.toArgb())
                                            drawPath(
                                                path,
                                                paint.asFrameworkPaint()
                                            )
                                        }.toImageModel(),
                                        drawMode.mode
                                    )
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
                    isLoading = false
                    job.cancel()
                    progress = 0f
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

        LoadingDialog(
            visible = isLoading,
            canCancel = false,
            progress = { progress },
            loaderSize = 72.dp,
            additionalContent = {
                AutoSizeText(
                    text = "${(progress * 100).roundToInt()}%",
                    maxLines = 1,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(it * 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        )
    } else if (drawMode is DrawMode.Warp && !isEraserOn) {
        val paint = remember(uiPathPaint, canvasSize) {
            val stroke = strokeWidth.toPx(canvasSize)

            Paint().apply {
                style = PaintingStyle.Stroke
                this.strokeWidth = stroke
                strokeCap = StrokeCap.Round
                strokeJoin = StrokeJoin.Round
                color = Color.White
                blendMode = BlendMode.Clear
            }
        }
        var warpedBitmap by remember(uiPathPaint, canvasSize) {
            mutableStateOf<ImageBitmap?>(null)
        }

        LaunchedEffect(warpedBitmap, invalidations) {
            withContext(Dispatchers.Default) {
                if (warpedBitmap == null || invalidations <= pathsCount) {
                    val source = drawImageBitmap
                        .overlay(drawBitmap)
                        .asAndroidBitmap()

                    val engine = WarpEngine(source)

                    try {
                        drawMode.strokes.forEach { warp ->
                            val stroke = warp.scaleToFitCanvas(
                                currentSize = canvasSize,
                                oldSize = size
                            )
                            engine.applyStroke(
                                fromX = stroke.fromX,
                                fromY = stroke.fromY,
                                toX = stroke.toX,
                                toY = stroke.toY,
                                brush = WarpBrush(
                                    radius = strokeWidth.toPx(canvasSize),
                                    strength = drawMode.strength,
                                    hardness = drawMode.hardness
                                ),
                                mode = WarpMode.valueOf(drawMode.warpMode.name)
                            )
                        }

                        warpedBitmap = engine
                            .render()
                            .asImageBitmap()
                            .clipBitmap(
                                path = path.asComposePath(),
                                paint = paint
                            ).also {
                                it.prepareToDraw()
                                onInvalidate()
                            }
                    } finally {
                        engine.release()
                    }
                }
            }
        }

        warpedBitmap?.let {
            LaunchedEffect(warpedBitmap) {
                onClearDrawPath()
            }
            val imagePaint = remember { Paint() }
            drawImage(
                image = warpedBitmap!!,
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