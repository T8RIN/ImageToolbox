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

package com.t8rin.imagetoolbox.feature.draw.presentation.components

import android.graphics.Bitmap
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Pt
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.createScaledBitmap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.HelperGridParams
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.domain.WarpStroke
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.BitmapDrawerPreview
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.DrawPathEffectPreview
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.MotionEvent
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.clipBitmap
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.copy
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawRepeatedImageOnPath
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawRepeatedTextOnPath
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.floodFill
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.handle
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.overlay
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.pointerDrawObserver
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.rememberPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.rememberPathHelper
import com.t8rin.trickle.WarpBrush
import com.t8rin.trickle.WarpEngine
import com.t8rin.trickle.WarpMode
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.rememberZoomState
import android.graphics.Canvas as AndroidCanvas


@Composable
fun BitmapDrawer(
    imageBitmap: ImageBitmap,
    onRequestFiltering: suspend (Bitmap, List<Filter<*>>) -> Bitmap?,
    paths: List<UiPathPaint>,
    brushSoftness: Pt,
    zoomState: ZoomState = rememberZoomState(maxScale = 30f),
    onAddPath: (UiPathPaint) -> Unit,
    strokeWidth: Pt,
    isEraserOn: Boolean,
    drawMode: DrawMode,
    modifier: Modifier,
    drawPathMode: DrawPathMode = DrawPathMode.Free,
    onDrawStart: (() -> Unit)? = null,
    onDraw: ((Bitmap) -> Unit)? = null,
    onDrawFinish: (() -> Unit)? = null,
    backgroundColor: Color,
    panEnabled: Boolean,
    drawColor: Color,
    drawLineStyle: DrawLineStyle = DrawLineStyle.None,
    helperGridParams: HelperGridParams = remember { HelperGridParams() },
) {
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
            val motionEvent = remember { mutableStateOf(MotionEvent.Idle) }
            var previousDrawPosition by remember { mutableStateOf(Offset.Unspecified) }
            var drawDownPosition by remember { mutableStateOf(Offset.Unspecified) }

            val imageWidth = constraints.maxWidth
            val imageHeight = constraints.maxHeight

            val drawImageBitmap by remember(imageWidth, imageHeight, backgroundColor) {
                derivedStateOf {
                    imageBitmap.asAndroidBitmap().createScaledBitmap(
                        width = imageWidth,
                        height = imageHeight
                    ).apply {
                        val canvas = AndroidCanvas(this)
                        val paint = android.graphics.Paint().apply {
                            color = backgroundColor.toArgb()
                        }
                        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                    }.asImageBitmap()
                }
            }

            val drawBitmap: ImageBitmap by remember(imageWidth, imageHeight) {
                derivedStateOf {
                    createBitmap(imageWidth, imageHeight).asImageBitmap()
                }
            }

            var invalidations by remember {
                mutableIntStateOf(0)
            }

            val drawPathBitmap: ImageBitmap by remember(imageWidth, imageHeight, invalidations) {
                derivedStateOf {
                    createBitmap(imageWidth, imageHeight).asImageBitmap()
                }
            }

            LaunchedEffect(
                paths,
                drawMode,
                backgroundColor,
                drawPathMode,
                imageWidth,
                imageHeight
            ) {
                invalidations++
            }

            val outputImage by remember(invalidations) {
                derivedStateOf {
                    drawImageBitmap.overlay(drawBitmap)
                }
            }

            val canvas: Canvas by remember(drawBitmap, imageHeight, imageWidth) {
                derivedStateOf {
                    Canvas(drawBitmap)
                }
            }

            val drawPathCanvas: Canvas by remember(drawPathBitmap, imageWidth, imageHeight) {
                derivedStateOf {
                    Canvas(drawPathBitmap)
                }
            }

            val canvasSize by remember(canvas.nativeCanvas) {
                derivedStateOf {
                    IntegerSize(
                        width = canvas.nativeCanvas.width,
                        height = canvas.nativeCanvas.height
                    )
                }
            }

            val drawPaint by rememberPaint(
                strokeWidth = strokeWidth,
                isEraserOn = isEraserOn,
                drawColor = drawColor,
                brushSoftness = brushSoftness,
                drawMode = drawMode,
                canvasSize = canvasSize,
                drawPathMode = drawPathMode,
                drawLineStyle = drawLineStyle
            )

            var drawPath by remember(
                drawMode,
                strokeWidth,
                isEraserOn,
                drawColor,
                brushSoftness,
                drawPathMode
            ) { mutableStateOf(Path()) }

            var pathWithoutTransformations by remember(
                drawMode,
                strokeWidth,
                isEraserOn,
                drawColor,
                brushSoftness,
                drawPathMode
            ) { mutableStateOf(Path()) }

            var warpRuntimeStrokes by remember(drawMode) {
                mutableStateOf(emptyList<WarpStroke>())
            }
            var warpClearTrigger by remember {
                mutableIntStateOf(0)
            }

            LaunchedEffect(paths.size) {
                if (paths.isEmpty()) warpClearTrigger++
            }

            with(canvas) {
                with(nativeCanvas) {
                    drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
                    drawColor(backgroundColor.toArgb())

                    paths.forEach { uiPathPaint ->
                        UiPathPaintCanvasAction(
                            uiPathPaint = uiPathPaint,
                            invalidations = invalidations,
                            onInvalidate = { invalidations++ },
                            pathsCount = paths.size,
                            backgroundColor = backgroundColor,
                            drawImageBitmap = drawImageBitmap,
                            drawBitmap = drawBitmap,
                            onClearDrawPath = {
                                drawPath = Path()
                                warpClearTrigger++
                                warpRuntimeStrokes = emptyList()
                            },
                            onRequestFiltering = onRequestFiltering,
                            canvasSize = canvasSize
                        )
                    }

                    if ((drawMode !is DrawMode.PathEffect && drawMode !is DrawMode.Warp) || isEraserOn) {
                        val androidPath by remember(drawPath) {
                            derivedStateOf {
                                drawPath.asAndroidPath()
                            }
                        }
                        if (drawMode is DrawMode.Text && !isEraserOn) {
                            if (drawMode.isRepeated) {
                                drawRepeatedTextOnPath(
                                    text = drawMode.text,
                                    path = androidPath,
                                    paint = drawPaint,
                                    interval = drawMode.repeatingInterval.toPx(canvasSize)
                                )
                            } else {
                                drawTextOnPath(drawMode.text, androidPath, 0f, 0f, drawPaint)
                            }
                        } else if (drawMode is DrawMode.Image && !isEraserOn) {
                            drawRepeatedImageOnPath(
                                drawMode = drawMode,
                                strokeWidth = strokeWidth,
                                canvasSize = canvasSize,
                                path = androidPath,
                                paint = drawPaint,
                                invalidations = invalidations
                            )
                        } else if (drawMode is DrawMode.SpotHeal && !isEraserOn) {
                            with(drawPathCanvas.nativeCanvas) {
                                drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
                                drawPath(
                                    androidPath,
                                    drawPaint.apply { color = Color.Red.copy(0.5f).toArgb() }
                                )
                            }
                        } else {
                            drawPath(androidPath, drawPaint)
                        }
                    }
                }

                val drawHelper by rememberPathHelper(
                    drawDownPosition = drawDownPosition,
                    currentDrawPosition = currentDrawPosition,
                    onPathChange = { drawPath = it },
                    strokeWidth = strokeWidth,
                    canvasSize = canvasSize,
                    drawPathMode = drawPathMode,
                    isEraserOn = isEraserOn,
                    drawMode = drawMode
                )

                motionEvent.value.handle(
                    onDown = {
                        warpClearTrigger++
                        warpRuntimeStrokes = emptyList()

                        if (currentDrawPosition.isSpecified) {
                            onDrawStart?.invoke()
                            drawPath.moveTo(currentDrawPosition.x, currentDrawPosition.y)
                            previousDrawPosition = currentDrawPosition
                            pathWithoutTransformations = drawPath.copy()
                        } else {
                            drawPath = Path()
                            pathWithoutTransformations = Path()
                        }

                        motionEvent.value = MotionEvent.Idle
                    },
                    onMove = {
                        if (drawMode is DrawMode.Warp && !isEraserOn) {
                            if (
                                previousDrawPosition.isSpecified &&
                                currentDrawPosition.isSpecified
                            ) {
                                warpRuntimeStrokes += WarpStroke(
                                    fromX = previousDrawPosition.x,
                                    fromY = previousDrawPosition.y,
                                    toX = currentDrawPosition.x,
                                    toY = currentDrawPosition.y
                                )
                            }
                        }

                        drawHelper.drawPath(
                            currentDrawPath = drawPath,
                            onDrawFreeArrow = {
                                if (previousDrawPosition.isUnspecified && currentDrawPosition.isSpecified) {
                                    drawPath = Path().apply {
                                        moveTo(
                                            currentDrawPosition.x,
                                            currentDrawPosition.y
                                        )
                                    }
                                    pathWithoutTransformations = drawPath.copy()
                                    previousDrawPosition = currentDrawPosition
                                }
                                if (previousDrawPosition.isSpecified && currentDrawPosition.isSpecified) {
                                    drawPath = pathWithoutTransformations
                                    drawPath.quadraticTo(
                                        previousDrawPosition.x,
                                        previousDrawPosition.y,
                                        (previousDrawPosition.x + currentDrawPosition.x) / 2,
                                        (previousDrawPosition.y + currentDrawPosition.y) / 2
                                    )
                                    previousDrawPosition = currentDrawPosition

                                    pathWithoutTransformations = drawPath.copy()

                                    drawArrowsIfNeeded(drawPath)
                                }
                            },
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
                        )

                        motionEvent.value = MotionEvent.Idle
                    },
                    onUp = {
                        if (currentDrawPosition.isSpecified && drawDownPosition.isSpecified) {
                            if (drawMode is DrawMode.Warp && warpRuntimeStrokes.isNotEmpty() && !isEraserOn) {
                                PathMeasure().apply {
                                    setPath(drawPath, false)
                                }.let {
                                    it.getPosition(it.length)
                                }.takeOrElse { currentDrawPosition }.let { lastPoint ->
                                    warpRuntimeStrokes += WarpStroke(
                                        fromX = lastPoint.x,
                                        fromY = lastPoint.y,
                                        toX = currentDrawPosition.x,
                                        toY = currentDrawPosition.y
                                    )
                                }

                                onAddPath(
                                    UiPathPaint(
                                        path = drawPath,
                                        strokeWidth = strokeWidth,
                                        brushSoftness = 0.pt,
                                        drawColor = Color.Transparent,
                                        isErasing = false,
                                        drawMode = drawMode.copy(
                                            strokes = warpRuntimeStrokes.toList()
                                        ),
                                        canvasSize = canvasSize,
                                        drawPathMode = DrawPathMode.Free,
                                        drawLineStyle = DrawLineStyle.None
                                    )
                                )
                            } else {
                                drawHelper.drawPath(
                                    currentDrawPath = null,
                                    onDrawFreeArrow = {
                                        drawPath = pathWithoutTransformations
                                        PathMeasure().apply {
                                            setPath(drawPath, false)
                                        }.let {
                                            it.getPosition(it.length)
                                        }.let { lastPoint ->
                                            if (!lastPoint.isSpecified) {
                                                drawPath.moveTo(
                                                    currentDrawPosition.x,
                                                    currentDrawPosition.y
                                                )
                                            }
                                            drawPath.lineTo(
                                                currentDrawPosition.x,
                                                currentDrawPosition.y
                                            )
                                        }

                                        drawArrowsIfNeeded(drawPath)
                                    },
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
                                        outputImage
                                            .floodFill(
                                                offset = currentDrawPosition,
                                                tolerance = tolerance
                                            )
                                            ?.let { drawPath = it }
                                    }
                                )

                                onAddPath(
                                    UiPathPaint(
                                        path = drawPath,
                                        strokeWidth = strokeWidth,
                                        brushSoftness = brushSoftness,
                                        drawColor = drawColor,
                                        isErasing = isEraserOn,
                                        drawMode = drawMode,
                                        canvasSize = canvasSize,
                                        drawPathMode = drawPathMode,
                                        drawLineStyle = drawLineStyle
                                    )
                                )
                            }
                        }

                        currentDrawPosition = Offset.Unspecified
                        previousDrawPosition = Offset.Unspecified
                        motionEvent.value = MotionEvent.Idle

                        scope.launch {
                            if ((drawMode is DrawMode.PathEffect || drawMode is DrawMode.SpotHeal || drawMode is DrawMode.Warp) && !isEraserOn) Unit
                            else drawPath = Path()

                            pathWithoutTransformations = Path()
                        }
                        onDrawFinish?.invoke()
                    }
                )
            }

            if (drawMode is DrawMode.PathEffect && !isEraserOn) {
                DrawPathEffectPreview(
                    drawPathCanvas = drawPathCanvas,
                    drawMode = drawMode,
                    canvasSize = canvasSize,
                    imageWidth = imageWidth,
                    imageHeight = imageHeight,
                    outputImage = outputImage,
                    onRequestFiltering = onRequestFiltering,
                    paths = paths,
                    drawPath = drawPath,
                    backgroundColor = backgroundColor,
                    strokeWidth = strokeWidth,
                    drawPathMode = drawPathMode
                )
            }

            var warpEngine by remember {
                mutableStateOf(
                    WarpEngine(
                        src = outputImage.asAndroidBitmap()
                    )
                )
            }

            LaunchedEffect(warpClearTrigger, drawMode) {
                warpEngine.release()
                warpEngine = WarpEngine(
                    src = outputImage.asAndroidBitmap()
                )
            }

            LaunchedEffect(warpEngine) {
                snapshotFlow { warpRuntimeStrokes.lastOrNull() }
                    .filterNotNull()
                    .collect {
                        if (drawMode is DrawMode.Warp) {
                            warpEngine.applyStroke(
                                fromX = it.fromX,
                                fromY = it.fromY,
                                toX = it.toX,
                                toY = it.toY,
                                brush = WarpBrush(
                                    radius = strokeWidth.toPx(canvasSize),
                                    strength = drawMode.strength,
                                    hardness = drawMode.hardness
                                ),
                                mode = WarpMode.valueOf(drawMode.warpMode.name)
                            )
                            invalidations++
                        }
                    }
            }

            val warpedImage by remember(invalidations, warpEngine) {
                derivedStateOf {
                    if (warpRuntimeStrokes.isNotEmpty()) {
                        outputImage.overlay(
                            warpEngine.render().asImageBitmap().clipBitmap(
                                path = drawPath,
                                paint = Paint().apply {
                                    style = PaintingStyle.Stroke
                                    this.strokeWidth = strokeWidth.toPx(canvasSize)
                                    strokeCap = StrokeCap.Round
                                    strokeJoin = StrokeJoin.Round
                                    color = Color.White
                                    blendMode = BlendMode.Clear
                                }
                            )
                        )
                    } else {
                        outputImage.overlay(drawPathBitmap)
                    }
                }
            }

            val previewBitmap by remember(invalidations) {
                derivedStateOf {
                    if (drawMode is DrawMode.Warp) {
                        warpedImage
                    } else {
                        outputImage.overlay(drawPathBitmap)
                    }
                }
            }

            onDraw?.let {
                LaunchedEffect(invalidations) {
                    onDraw(previewBitmap.asAndroidBitmap())
                }
            }

            BitmapDrawerPreview(
                preview = previewBitmap,
                globalTouchPointersCount = globalTouchPointersCount,
                onReceiveMotionEvent = { motionEvent.value = it },
                onInvalidate = { invalidations++ },
                onUpdateCurrentDrawPosition = { currentDrawPosition = it },
                onUpdateDrawDownPosition = { drawDownPosition = it },
                drawEnabled = !panEnabled,
                helperGridParams = helperGridParams
            )
        }
    }
}