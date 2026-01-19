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

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.feature.erase_background.presentation.components

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Pt
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.createScaledBitmap
import com.t8rin.imagetoolbox.core.ui.utils.helper.scaleToFitCanvas
import com.t8rin.imagetoolbox.core.ui.widget.modifier.HelperGridParams
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.BitmapDrawerPreview
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.MotionEvent
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.copy
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.floodFill
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.handle
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.pointerDrawObserver
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.rememberPathHelper
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState

@Composable
fun BitmapEraser(
    imageBitmap: ImageBitmap,
    imageBitmapForShader: ImageBitmap?,
    paths: List<UiPathPaint>,
    brushSoftness: Pt,
    originalImagePreviewAlpha: Float,
    onAddPath: (UiPathPaint) -> Unit,
    drawPathMode: DrawPathMode = DrawPathMode.Lasso,
    strokeWidth: Pt,
    isRecoveryOn: Boolean = false,
    modifier: Modifier,
    onErased: (Bitmap) -> Unit = {},
    panEnabled: Boolean,
    helperGridParams: HelperGridParams = remember { HelperGridParams() },
) {
    val zoomState = rememberZoomState(maxScale = 30f)
    val scope = rememberCoroutineScope()

    val settingsState = LocalSettingsState.current
    val magnifierEnabled by remember(zoomState.scale, settingsState.magnifierEnabled) {
        derivedStateOf {
            zoomState.scale <= 3f && !panEnabled && settingsState.magnifierEnabled
        }
    }
    val globalTouchPointersCount = remember { mutableIntStateOf(0) }

    var currentDrawPosition by remember { mutableStateOf(Offset.Unspecified) }

    Box(
        modifier = Modifier.pointerDrawObserver(
            magnifierEnabled = magnifierEnabled,
            currentDrawPosition = currentDrawPosition,
            zoomState = zoomState,
            globalTouchPointersCount = globalTouchPointersCount,
            panEnabled = panEnabled
        ),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(modifier) {
            var invalidations by remember {
                mutableIntStateOf(0)
            }

            LaunchedEffect(paths, constraints) {
                invalidations++
            }

            val motionEvent = remember { mutableStateOf(MotionEvent.Idle) }
            var previousDrawPosition by remember { mutableStateOf(Offset.Unspecified) }
            var drawDownPosition by remember { mutableStateOf(Offset.Unspecified) }

            val imageWidth = constraints.maxWidth
            val imageHeight = constraints.maxHeight

            val drawImageBitmap by remember(imageWidth, imageHeight) {
                derivedStateOf {
                    imageBitmap
                        .asAndroidBitmap()
                        .createScaledBitmap(
                            width = imageWidth,
                            height = imageHeight
                        )
                        .asImageBitmap()
                }
            }

            val shaderBitmap by remember(imageBitmapForShader, imageWidth, imageHeight) {
                derivedStateOf {
                    imageBitmapForShader
                        ?.asAndroidBitmap()
                        ?.createScaledBitmap(
                            imageWidth,
                            imageHeight
                        )
                        ?.asImageBitmap()
                }
            }

            val erasedBitmap: ImageBitmap by remember(imageWidth, imageHeight) {
                derivedStateOf {
                    createBitmap(imageWidth, imageHeight).asImageBitmap()
                }
            }

            val outputImage by remember(invalidations) {
                derivedStateOf {
                    erasedBitmap.copy()
                }
            }

            LaunchedEffect(invalidations) {
                onErased(outputImage.asAndroidBitmap())
            }

            val canvas: Canvas by remember(imageHeight, imageWidth, erasedBitmap) {
                derivedStateOf {
                    Canvas(erasedBitmap)
                }
            }

            val canvasSize by remember(canvas.nativeCanvas) {
                derivedStateOf {
                    IntegerSize(canvas.nativeCanvas.width, canvas.nativeCanvas.height)
                }
            }

            val drawPaint by remember(
                drawPathMode,
                strokeWidth,
                isRecoveryOn,
                brushSoftness,
                canvasSize
            ) {
                derivedStateOf {
                    Paint().apply {
                        style = if (drawPathMode.isFilled) {
                            PaintingStyle.Fill
                        } else PaintingStyle.Stroke

                        blendMode = if (isRecoveryOn) blendMode else BlendMode.Clear
                        shader = if (isRecoveryOn) shaderBitmap?.let { ImageShader(it) } else shader
                        this.strokeWidth = drawPathMode.convertStrokeWidth(
                            strokeWidth = strokeWidth,
                            canvasSize = canvasSize
                        )
                        if (drawPathMode.isSharpEdge) {
                            strokeCap = StrokeCap.Square
                        } else {
                            strokeCap = StrokeCap.Round
                            strokeJoin = StrokeJoin.Round
                        }
                        isAntiAlias = true
                    }.asFrameworkPaint().apply {
                        if (brushSoftness.value > 0f) maskFilter =
                            BlurMaskFilter(
                                brushSoftness.toPx(canvasSize),
                                BlurMaskFilter.Blur.NORMAL
                            )
                    }
                }
            }

            var drawPath by remember(
                strokeWidth,
                brushSoftness
            ) { mutableStateOf(Path()) }

            with(canvas) {
                with(nativeCanvas) {
                    drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)

                    val imagePaint = remember { Paint() }
                    drawImageRect(
                        image = drawImageBitmap,
                        dstSize = IntSize(canvasSize.width, canvasSize.height),
                        paint = imagePaint
                    )

                    paths.forEach { (nonScaledPath, stroke, radius, _, isRecoveryOn, _, size, mode) ->
                        val path by remember(nonScaledPath, size, canvasSize) {
                            derivedStateOf {
                                nonScaledPath.scaleToFitCanvas(
                                    currentSize = canvasSize,
                                    oldSize = size
                                ).asAndroidPath()
                            }
                        }
                        val paint by remember(
                            mode,
                            isRecoveryOn,
                            shaderBitmap,
                            stroke,
                            canvasSize,
                            radius
                        ) {
                            derivedStateOf {
                                Paint().apply {
                                    style = if (mode.isFilled) {
                                        PaintingStyle.Fill
                                    } else PaintingStyle.Stroke
                                    blendMode = if (isRecoveryOn) blendMode else BlendMode.Clear

                                    if (isRecoveryOn) shader = shaderBitmap?.let { ImageShader(it) }
                                    this.strokeWidth = drawPathMode.convertStrokeWidth(
                                        strokeWidth = stroke,
                                        canvasSize = canvasSize
                                    )
                                    if (mode.isSharpEdge) {
                                        strokeCap = StrokeCap.Square
                                    } else {
                                        strokeCap = StrokeCap.Round
                                        strokeJoin = StrokeJoin.Round
                                    }
                                    isAntiAlias = true
                                }.asFrameworkPaint().apply {
                                    if (radius.value > 0f) {
                                        maskFilter =
                                            BlurMaskFilter(
                                                radius.toPx(canvasSize),
                                                BlurMaskFilter.Blur.NORMAL
                                            )
                                    }
                                }
                            }
                        }
                        drawPath(path, paint)
                    }
                    drawPath(
                        drawPath.asAndroidPath(),
                        drawPaint
                    )
                }

                val drawHelper by rememberPathHelper(
                    drawDownPosition = drawDownPosition,
                    currentDrawPosition = currentDrawPosition,
                    onPathChange = { drawPath = it },
                    strokeWidth = strokeWidth,
                    canvasSize = canvasSize,
                    drawPathMode = drawPathMode,
                    isEraserOn = false,
                    drawMode = DrawMode.Pen
                )

                motionEvent.value.handle(
                    onDown = {
                        if (currentDrawPosition.isSpecified) {
                            drawPath.moveTo(currentDrawPosition.x, currentDrawPosition.y)
                            previousDrawPosition = currentDrawPosition
                        } else {
                            drawPath = Path()
                        }
                        motionEvent.value = MotionEvent.Idle
                    },
                    onMove = {
                        drawHelper.drawPath(
                            onBaseDraw = {
                                if (previousDrawPosition.isUnspecified && currentDrawPosition.isSpecified) {
                                    drawPath.moveTo(currentDrawPosition.x, currentDrawPosition.y)
                                    previousDrawPosition = currentDrawPosition
                                }

                                if (currentDrawPosition.isSpecified && previousDrawPosition.isSpecified) {
                                    drawPath.quadraticTo(
                                        previousDrawPosition.x,
                                        previousDrawPosition.y,
                                        (previousDrawPosition.x + currentDrawPosition.x) / 2,
                                        (previousDrawPosition.y + currentDrawPosition.y) / 2
                                    )
                                }
                                previousDrawPosition = currentDrawPosition
                            },
                            currentDrawPath = drawPath
                        )

                        motionEvent.value = MotionEvent.Idle
                    },
                    onUp = {
                        drawHelper.drawPath(
                            onBaseDraw = {
                                PathMeasure().apply {
                                    setPath(drawPath, false)
                                }.let {
                                    it.getPosition(it.length)
                                }.takeOrElse { currentDrawPosition }.let { lastPoint ->
                                    drawPath.moveTo(lastPoint.x, lastPoint.y)
                                    drawPath.lineTo(
                                        currentDrawPosition.x,
                                        currentDrawPosition.y
                                    )
                                }
                            },
                            onFloodFill = { tolerance ->
                                erasedBitmap.floodFill(
                                    offset = currentDrawPosition,
                                    tolerance = tolerance
                                )?.let { drawPath = it }
                            },
                            currentDrawPath = null
                        )

                        onAddPath(
                            UiPathPaint(
                                path = drawPath,
                                strokeWidth = strokeWidth,
                                brushSoftness = brushSoftness,
                                isErasing = isRecoveryOn,
                                canvasSize = canvasSize,
                                drawPathMode = drawPathMode
                            )
                        )

                        currentDrawPosition = Offset.Unspecified
                        previousDrawPosition = Offset.Unspecified
                        motionEvent.value = MotionEvent.Idle

                        scope.launch {
                            drawPath = Path()
                        }
                    }
                )
            }

            BitmapDrawerPreview(
                preview = outputImage,
                globalTouchPointersCount = globalTouchPointersCount,
                onReceiveMotionEvent = { motionEvent.value = it },
                onInvalidate = { invalidations++ },
                onUpdateCurrentDrawPosition = { currentDrawPosition = it },
                onUpdateDrawDownPosition = { drawDownPosition = it },
                drawEnabled = !panEnabled,
                helperGridParams = helperGridParams,
                beforeHelperGridModifier = Modifier.drawBehind {
                    if (originalImagePreviewAlpha != 0f) {
                        drawContext.canvas.apply {
                            drawImageRect(
                                image = imageBitmapForShader ?: drawImageBitmap,
                                dstSize = IntSize(canvasSize.width, canvasSize.height),
                                paint = Paint().apply {
                                    alpha = originalImagePreviewAlpha
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}