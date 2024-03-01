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

package ru.tech.imageresizershrinker.feature.erase_background.presentation.components

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ZoomableDefaults.defaultZoomOnDoubleTap
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.createScaledBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.scaleToFitCanvas
import ru.tech.imageresizershrinker.core.ui.widget.modifier.observePointersCountWithOffset
import ru.tech.imageresizershrinker.core.ui.widget.modifier.smartDelayAfterDownInMillis
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode
import ru.tech.imageresizershrinker.feature.draw.domain.Pt
import ru.tech.imageresizershrinker.feature.draw.presentation.components.UiPathPaint

@Composable
fun BitmapEraser(
    imageBitmap: ImageBitmap,
    imageBitmapForShader: ImageBitmap?,
    paths: List<UiPathPaint>,
    brushSoftness: Pt,
    useLasso: Boolean,
    originalImagePreviewAlpha: Float,
    onAddPath: (UiPathPaint) -> Unit,
    strokeWidth: Pt,
    isRecoveryOn: Boolean = false,
    modifier: Modifier,
    onErased: (Bitmap) -> Unit = {},
    panEnabled: Boolean
) {
    val zoomState = rememberZoomState(maxScale = 30f)
    val scope = rememberCoroutineScope()

    val settingsState = LocalSettingsState.current
    val magnifierEnabled by remember(zoomState.scale) {
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
            .zoomable(
                zoomState = zoomState,
                enabled = { _, _ ->
                    (globalTouchPointersCount >= 2 || panEnabled)
                },
                enableOneFingerZoom = panEnabled,
                onDoubleTap = { pos ->
                    if (panEnabled) zoomState.defaultZoomOnDoubleTap(pos)
                }
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

            var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
            var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

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
                    Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
                        .asImageBitmap()
                }
            }

            val outputImage by remember(invalidations) {
                derivedStateOf {
                    erasedBitmap.recompose()
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
                strokeWidth,
                isRecoveryOn,
                brushSoftness,
                canvasSize,
                useLasso
            ) {
                derivedStateOf {
                    Paint().apply {
                        style = if (useLasso) {
                            PaintingStyle.Fill
                        } else PaintingStyle.Stroke

                        blendMode = if (isRecoveryOn) blendMode else BlendMode.Clear
                        strokeCap = StrokeCap.Round
                        shader = if (isRecoveryOn) shaderBitmap?.let { ImageShader(it) } else shader
                        this.strokeWidth = strokeWidth.toPx(canvasSize)
                        strokeJoin = StrokeJoin.Round
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

            canvas.apply {
                when (motionEvent) {

                    MotionEvent.Down -> {
                        if (currentDrawPosition.isSpecified) {
                            drawPath.moveTo(currentDrawPosition.x, currentDrawPosition.y)
                            previousPosition = currentDrawPosition
                        } else {
                            drawPath = Path()
                        }
                        motionEvent = MotionEvent.Idle
                    }

                    MotionEvent.Move -> {
                        if (previousPosition.isUnspecified && currentDrawPosition.isSpecified) {
                            drawPath.moveTo(currentDrawPosition.x, currentDrawPosition.y)
                            previousPosition = currentDrawPosition
                        }
                        if (previousPosition.isSpecified && currentDrawPosition.isSpecified) {
                            drawPath.quadraticTo(
                                previousPosition.x,
                                previousPosition.y,
                                (previousPosition.x + currentDrawPosition.x) / 2,
                                (previousPosition.y + currentDrawPosition.y) / 2
                            )
                        }
                        previousPosition = currentDrawPosition
                        motionEvent = MotionEvent.Idle
                    }

                    MotionEvent.Up -> {
                        PathMeasure().apply {
                            setPath(drawPath, false)
                        }.let {
                            it.getPosition(it.length)
                        }.takeOrElse { currentDrawPosition }.let { lastPoint ->
                            if (currentDrawPosition.isSpecified) {
                                drawPath.moveTo(lastPoint.x, lastPoint.y)
                                drawPath.lineTo(currentDrawPosition.x, currentDrawPosition.y)
                                onAddPath(
                                    UiPathPaint(
                                        path = drawPath,
                                        strokeWidth = strokeWidth,
                                        brushSoftness = brushSoftness,
                                        isErasing = isRecoveryOn,
                                        canvasSize = canvasSize,
                                        drawPathMode = if (useLasso) {
                                            DrawPathMode.Lasso
                                        } else DrawPathMode.Free
                                    )
                                )
                            }
                        }
                        currentDrawPosition = Offset.Unspecified
                        previousPosition = Offset.Unspecified
                        motionEvent = MotionEvent.Idle

                        scope.launch {
                            drawPath = Path()
                        }
                    }

                    else -> Unit
                }

                with(canvas.nativeCanvas) {
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
                                    style = if (mode is DrawPathMode.Lasso) {
                                        PaintingStyle.Fill
                                    } else PaintingStyle.Stroke
                                    blendMode = if (isRecoveryOn) blendMode else BlendMode.Clear
                                    strokeCap = StrokeCap.Round

                                    if (isRecoveryOn) shader = shaderBitmap?.let { ImageShader(it) }
                                    this.strokeWidth = stroke.toPx(canvasSize)
                                    strokeJoin = StrokeJoin.Round
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

            Image(
                modifier = Modifier
                    .matchParentSize()
                    .then(
                        if (!panEnabled) canvasModifier
                        else Modifier
                    )
                    .clip(RoundedCornerShape(2.dp))
                    .transparencyChecker()
                    .drawBehind {
                        if (originalImagePreviewAlpha != 0f) {
                            drawContext.canvas.apply {
                                drawImageRect(
                                    image = drawImageBitmap,
                                    dstSize = IntSize(canvasSize.width, canvasSize.height),
                                    paint = Paint().apply {
                                        alpha = originalImagePreviewAlpha
                                    }
                                )
                            }
                        }
                    }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant(),
                        RoundedCornerShape(2.dp)
                    ),
                bitmap = outputImage,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

private fun ImageBitmap.recompose(): ImageBitmap = asAndroidBitmap().asImageBitmap()