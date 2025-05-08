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

package ru.tech.imageresizershrinker.feature.draw.presentation.components

import android.graphics.Bitmap
import android.graphics.PorterDuff
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.smarttoolfactory.gesture.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.ZoomableDefaults.defaultZoomOnDoubleTap
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Pt
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.createScaledBitmap
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.HelperGridParams
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHelperGrid
import ru.tech.imageresizershrinker.core.ui.widget.modifier.observePointersCountWithOffset
import ru.tech.imageresizershrinker.core.ui.widget.modifier.smartDelayAfterDownInMillis
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.feature.draw.domain.DrawLineStyle
import ru.tech.imageresizershrinker.feature.draw.domain.DrawMode
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode
import ru.tech.imageresizershrinker.feature.draw.presentation.components.utils.copy
import ru.tech.imageresizershrinker.feature.draw.presentation.components.utils.copyAsAndroidPath
import ru.tech.imageresizershrinker.feature.draw.presentation.components.utils.drawRepeatedImageOnPath
import ru.tech.imageresizershrinker.feature.draw.presentation.components.utils.drawRepeatedTextOnPath
import ru.tech.imageresizershrinker.feature.draw.presentation.components.utils.overlay
import ru.tech.imageresizershrinker.feature.draw.presentation.components.utils.rememberPaint
import ru.tech.imageresizershrinker.feature.draw.presentation.components.utils.rememberPathEffectPaint
import ru.tech.imageresizershrinker.feature.draw.presentation.components.utils.rememberPathHelper
import ru.tech.imageresizershrinker.feature.draw.presentation.components.utils.transformationsForMode
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Path as NativePath


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
    onDrawStart: () -> Unit = {},
    onDraw: (Bitmap) -> Unit = {},
    onDrawFinish: () -> Unit = {},
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
    var globalTouchPosition by remember { mutableStateOf(Offset.Unspecified) }
    var globalTouchPointersCount by remember { mutableIntStateOf(0) }

    var currentDrawPosition by remember { mutableStateOf(Offset.Unspecified) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .observePointersCountWithOffset { size, offset ->
                globalTouchPointersCount = size
                globalTouchPosition = offset
            }
            .then(
                if (magnifierEnabled) {
                    Modifier.magnifier(
                        sourceCenter = {
                            if (currentDrawPosition.isSpecified) {
                                globalTouchPosition
                            } else Offset.Unspecified
                        },
                        magnifierCenter = {
                            globalTouchPosition - Offset(0f, 100.dp.toPx())
                        },
                        size = DpSize(height = 100.dp, width = 100.dp),
                        cornerRadius = 50.dp,
                        elevation = 2.dp
                    )
                } else Modifier
            )
            .clipToBounds()
            .zoomable(
                zoomState = zoomState,
                zoomEnabled = (globalTouchPointersCount >= 2 || panEnabled),
                enableOneFingerZoom = panEnabled,
                onDoubleTap = { pos ->
                    if (panEnabled) zoomState.defaultZoomOnDoubleTap(pos)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(modifier) {
            var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
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

            val drawPathBitmap: ImageBitmap by remember(imageWidth, imageHeight) {
                derivedStateOf {
                    createBitmap(imageWidth, imageHeight).asImageBitmap()
                }
            }

            var invalidations by remember {
                mutableIntStateOf(0)
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

            LaunchedEffect(invalidations) {
                val outImage = outputImage.overlay(drawPathBitmap).asAndroidBitmap()
                onDraw(outImage)
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
                    IntegerSize(canvas.nativeCanvas.width, canvas.nativeCanvas.height)
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

            with(canvas) {
                val drawHelper by rememberPathHelper(
                    drawDownPosition = drawDownPosition,
                    currentDrawPosition = currentDrawPosition,
                    onPathChange = { drawPath = it },
                    strokeWidth = strokeWidth,
                    canvasSize = canvasSize,
                    drawPathMode = drawPathMode,
                    isEraserOn = isEraserOn
                )

                when (motionEvent) {

                    MotionEvent.Down -> {
                        if (currentDrawPosition.isSpecified) {
                            onDrawStart()
                            drawPath.moveTo(currentDrawPosition.x, currentDrawPosition.y)
                            previousDrawPosition = currentDrawPosition
                            pathWithoutTransformations = drawPath.copy()
                        } else {
                            drawPath = Path()
                            pathWithoutTransformations = Path()
                        }

                        motionEvent = MotionEvent.Idle
                    }

                    MotionEvent.Move -> {
                        drawHelper.drawPath(
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
                            }
                        )

                        motionEvent = MotionEvent.Idle
                    }

                    MotionEvent.Up -> {
                        if (currentDrawPosition.isSpecified && drawDownPosition.isSpecified) {
                            drawHelper.drawPath(
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

                        currentDrawPosition = Offset.Unspecified
                        previousDrawPosition = Offset.Unspecified
                        motionEvent = MotionEvent.Idle

                        scope.launch {
                            if ((drawMode is DrawMode.PathEffect || drawMode is DrawMode.SpotHeal) && !isEraserOn) Unit
                            else drawPath = Path()

                            pathWithoutTransformations = Path()
                        }
                        onDrawFinish()
                    }

                    else -> Unit
                }

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
                            onClearDrawPath = { drawPath = Path() },
                            onRequestFiltering = onRequestFiltering,
                            canvasSize = canvasSize
                        )
                    }

                    if (drawMode !is DrawMode.PathEffect || isEraserOn) {
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
                            drawPath(
                                androidPath,
                                drawPaint.apply { color = Color.Red.copy(0.5f).toArgb() }
                            )
                        } else {
                            drawPath(androidPath, drawPaint)
                        }
                    }
                }
            }

            if (drawMode is DrawMode.PathEffect && !isEraserOn) {
                var shaderBitmap by remember {
                    mutableStateOf<ImageBitmap?>(null)
                }

                LaunchedEffect(outputImage, paths, backgroundColor, drawMode) {
                    shaderBitmap = onRequestFiltering(
                        outputImage.asAndroidBitmap(),
                        transformationsForMode(
                            drawMode = drawMode,
                            canvasSize = canvasSize
                        )
                    )?.createScaledBitmap(
                        width = imageWidth,
                        height = imageHeight
                    )?.asImageBitmap()
                }

                shaderBitmap?.let {
                    drawPathCanvas.apply {
                        with(nativeCanvas) {
                            drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)

                            val paint by rememberPathEffectPaint(
                                strokeWidth = strokeWidth,
                                drawPathMode = drawPathMode,
                                canvasSize = canvasSize
                            )
                            val newPath = drawPath.copyAsAndroidPath().apply {
                                fillType = NativePath.FillType.INVERSE_WINDING
                            }
                            val imagePaint = remember { Paint() }

                            drawImage(
                                image = it,
                                topLeftOffset = Offset.Zero,
                                paint = imagePaint
                            )
                            drawPath(newPath, paint)
                        }
                    }
                }
            }

            var drawStartedWithOnePointer by remember {
                mutableStateOf(false)
            }

            val canvasModifier = Modifier.pointerMotionEvents(
                onDown = { pointerInputChange ->
                    drawStartedWithOnePointer = globalTouchPointersCount <= 1

                    if (drawStartedWithOnePointer) {
                        motionEvent = MotionEvent.Down
                        currentDrawPosition = pointerInputChange.position
                        drawDownPosition = pointerInputChange.position
                        pointerInputChange.consume()
                        invalidations++
                    }
                },
                onMove = { pointerInputChange ->
                    if (drawStartedWithOnePointer) {
                        motionEvent = MotionEvent.Move
                        currentDrawPosition = pointerInputChange.position
                        pointerInputChange.consume()
                        invalidations++
                    }
                },
                onUp = { pointerInputChange ->
                    if (drawStartedWithOnePointer) {
                        motionEvent = MotionEvent.Up
                        pointerInputChange.consume()
                        invalidations++
                    }
                    drawStartedWithOnePointer = false
                },
                delayAfterDownInMillis = smartDelayAfterDownInMillis(globalTouchPointersCount)
            )

            val previewBitmap by remember(invalidations) {
                derivedStateOf {
                    outputImage.overlay(drawPathBitmap)
                }
            }
            Picture(
                model = previewBitmap,
                modifier = Modifier
                    .matchParentSize()
                    .then(
                        if (!panEnabled) canvasModifier
                        else Modifier
                    )
                    .clip(RoundedCornerShape(2.dp))
                    .transparencyChecker()
                    .drawHelperGrid(helperGridParams)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant(),
                        shape = RoundedCornerShape(2.dp)
                    ),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }
    }
}