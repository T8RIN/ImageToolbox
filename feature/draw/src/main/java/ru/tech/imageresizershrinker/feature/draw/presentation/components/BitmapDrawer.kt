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
import android.graphics.BlurMaskFilter
import android.graphics.Matrix
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import coil.request.ImageRequest
import com.smarttoolfactory.gesture.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.ZoomableDefaults.defaultZoomOnDoubleTap
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiPixelationFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiStackBlurFilter
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.createScaledBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.rotateVector
import ru.tech.imageresizershrinker.core.ui.utils.helper.scaleToFitCanvas
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalImageLoader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.observePointersCountWithOffset
import ru.tech.imageresizershrinker.core.ui.widget.modifier.smartDelayAfterDownInMillis
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.feature.draw.domain.DrawMode
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode
import ru.tech.imageresizershrinker.feature.draw.domain.Pt
import ru.tech.imageresizershrinker.feature.draw.presentation.components.utils.drawRepeatedBitmapOnPath
import ru.tech.imageresizershrinker.feature.draw.presentation.components.utils.drawRepeatedTextOnPath
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import android.graphics.Canvas as AndroidCanvas


@Composable
fun BitmapDrawer(
    imageBitmap: ImageBitmap,
    onRequestFiltering: suspend (Bitmap, List<UiFilter<*>>) -> Bitmap?,
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
    drawColor: Color
) {
    val context = LocalContext.current
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
                    Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
                        .asImageBitmap()
                }
            }

            val drawPathBitmap: ImageBitmap by remember(imageWidth, imageHeight) {
                derivedStateOf {
                    Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
                        .asImageBitmap()
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

            fun transformationsForMode(
                drawMode: DrawMode
            ): List<UiFilter<*>> = when (drawMode) {
                is DrawMode.PathEffect.PrivacyBlur -> {
                    listOf(
                        UiStackBlurFilter(
                            value = when {
                                drawMode.blurRadius < 10 -> 0.8f
                                drawMode.blurRadius < 20 -> 0.5f
                                else -> 0.3f
                            } to drawMode.blurRadius
                        )
                    )
                }

                is DrawMode.PathEffect.Pixelation -> {
                    listOf(
                        UiStackBlurFilter(
                            value = when {
                                drawMode.pixelSize < 10 -> 0.8f
                                drawMode.pixelSize < 20 -> 0.5f
                                else -> 0.3f
                            } to 20
                        ),
                        UiPixelationFilter(
                            value = drawMode.pixelSize
                        )
                    )
                }

                else -> emptyList()
            }

            val drawPaint by remember(
                strokeWidth,
                isEraserOn,
                drawColor,
                brushSoftness,
                drawMode,
                canvasSize,
                drawPathMode
            ) {
                derivedStateOf {
                    val isRect = listOf(
                        DrawPathMode.OutlinedRect,
                        DrawPathMode.OutlinedOval,
                        DrawPathMode.Rect,
                        DrawPathMode.Oval,
                        DrawPathMode.Lasso
                    ).any { drawPathMode::class.isInstance(it) }

                    val isFilled = listOf(
                        DrawPathMode.Rect,
                        DrawPathMode.Oval,
                        DrawPathMode.Lasso
                    ).any { drawPathMode::class.isInstance(it) }

                    Paint().apply {
                        blendMode = if (!isEraserOn) blendMode else BlendMode.Clear
                        if (isEraserOn) {
                            style = PaintingStyle.Stroke
                            this.strokeWidth = strokeWidth.toPx(canvasSize)
                            strokeCap = StrokeCap.Round
                            strokeJoin = StrokeJoin.Round
                        } else {
                            if (drawMode !is DrawMode.Text) {
                                if (isFilled) {
                                    style = PaintingStyle.Fill
                                } else {
                                    style = PaintingStyle.Stroke
                                    this.strokeWidth = strokeWidth.toPx(canvasSize)
                                    if (drawMode is DrawMode.Highlighter || isRect) {
                                        strokeCap = StrokeCap.Square
                                    } else {
                                        strokeCap = StrokeCap.Round
                                        strokeJoin = StrokeJoin.Round
                                    }
                                }
                            }
                        }
                        color = if (drawMode is DrawMode.PathEffect) {
                            Color.Transparent
                        } else drawColor
                        alpha = drawColor.alpha
                    }.asFrameworkPaint().apply {
                        if (drawMode is DrawMode.Neon && !isEraserOn) {
                            this.color = Color.White.toArgb()
                            setShadowLayer(
                                brushSoftness.toPx(canvasSize),
                                0f,
                                0f,
                                drawColor
                                    .copy(alpha = .8f)
                                    .toArgb()
                            )
                        } else if (brushSoftness.value > 0f) {
                            maskFilter = BlurMaskFilter(
                                brushSoftness.toPx(canvasSize),
                                BlurMaskFilter.Blur.NORMAL
                            )
                        }
                        if (drawMode is DrawMode.Text && !isEraserOn) {
                            isAntiAlias = true
                            textSize = strokeWidth.toPx(canvasSize)
                            if (drawMode.font != 0) {
                                typeface = ResourcesCompat.getFont(context, drawMode.font)
                            }
                        }
                    }
                }
            }

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

            canvas.apply {
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
                        val baseMove = {
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
                        if (!isEraserOn) {
                            when (drawPathMode) {
                                DrawPathMode.DoubleLinePointingArrow,
                                DrawPathMode.Line,
                                DrawPathMode.LinePointingArrow -> {
                                    if (drawDownPosition.isSpecified && currentDrawPosition.isSpecified) {
                                        val newPath = Path().apply {
                                            moveTo(drawDownPosition.x, drawDownPosition.y)
                                            lineTo(currentDrawPosition.x, currentDrawPosition.y)
                                        }
                                        drawPathMode.drawArrowsIfNeeded(
                                            drawPath = newPath,
                                            strokeWidth = strokeWidth,
                                            canvasSize = canvasSize
                                        )
                                        drawPath = newPath
                                    }
                                }

                                DrawPathMode.PointingArrow,
                                DrawPathMode.DoublePointingArrow -> {
                                    if (previousDrawPosition.isUnspecified && currentDrawPosition.isSpecified) {
                                        drawPath = Path()
                                        drawPath.moveTo(
                                            currentDrawPosition.x,
                                            currentDrawPosition.y
                                        )
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

                                        drawPathMode.drawArrowsIfNeeded(
                                            drawPath = drawPath,
                                            strokeWidth = strokeWidth,
                                            canvasSize = canvasSize
                                        )
                                    }
                                }

                                DrawPathMode.Rect,
                                DrawPathMode.OutlinedRect -> {
                                    if (drawDownPosition.isSpecified && currentDrawPosition.isSpecified) {
                                        val top = max(drawDownPosition.y, currentDrawPosition.y)
                                        val left = min(drawDownPosition.x, currentDrawPosition.x)
                                        val bottom = min(drawDownPosition.y, currentDrawPosition.y)
                                        val right = max(drawDownPosition.x, currentDrawPosition.x)

                                        val newPath = Path().apply {
                                            moveTo(left, top)
                                            lineTo(right, top)
                                            lineTo(right, bottom)
                                            lineTo(left, bottom)
                                            lineTo(left, top)
                                        }
                                        drawPath = newPath
                                    }
                                }

                                DrawPathMode.Oval,
                                DrawPathMode.OutlinedOval -> {
                                    if (drawDownPosition.isSpecified && currentDrawPosition.isSpecified) {
                                        val newPath = Path().apply {
                                            addOval(
                                                Rect(
                                                    top = max(
                                                        drawDownPosition.y,
                                                        currentDrawPosition.y
                                                    ),
                                                    left = min(
                                                        drawDownPosition.x,
                                                        currentDrawPosition.x
                                                    ),
                                                    bottom = min(
                                                        drawDownPosition.y,
                                                        currentDrawPosition.y
                                                    ),
                                                    right = max(
                                                        drawDownPosition.x,
                                                        currentDrawPosition.x
                                                    ),
                                                )
                                            )
                                        }
                                        drawPath = newPath
                                    }
                                }

                                else -> baseMove()
                            }
                        } else baseMove()

                        motionEvent = MotionEvent.Idle
                    }

                    MotionEvent.Up -> {
                        val baseMove = {
                            PathMeasure().apply {
                                setPath(drawPath, false)
                            }.let {
                                it.getPosition(it.length)
                            }.takeOrElse { currentDrawPosition }.let { lastPoint ->
                                drawPath.moveTo(lastPoint.x, lastPoint.y)
                                drawPath.lineTo(currentDrawPosition.x, currentDrawPosition.y)
                            }
                        }
                        if (currentDrawPosition.isSpecified && drawDownPosition.isSpecified) {
                            if (!isEraserOn) {
                                when (drawPathMode) {
                                    DrawPathMode.DoubleLinePointingArrow,
                                    DrawPathMode.Line,
                                    DrawPathMode.LinePointingArrow -> {
                                        drawPath = Path().apply {
                                            moveTo(drawDownPosition.x, drawDownPosition.y)
                                            lineTo(currentDrawPosition.x, currentDrawPosition.y)
                                        }
                                        drawPathMode.drawArrowsIfNeeded(
                                            drawPath = drawPath,
                                            strokeWidth = strokeWidth,
                                            canvasSize = canvasSize
                                        )
                                    }

                                    DrawPathMode.PointingArrow,
                                    DrawPathMode.DoublePointingArrow -> {
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

                                        drawPathMode.drawArrowsIfNeeded(
                                            drawPath = drawPath,
                                            strokeWidth = strokeWidth,
                                            canvasSize = canvasSize
                                        )
                                    }

                                    DrawPathMode.Rect,
                                    DrawPathMode.OutlinedRect -> {
                                        val top = max(drawDownPosition.y, currentDrawPosition.y)
                                        val left = min(drawDownPosition.x, currentDrawPosition.x)
                                        val bottom = min(drawDownPosition.y, currentDrawPosition.y)
                                        val right = max(drawDownPosition.x, currentDrawPosition.x)

                                        val newPath = Path().apply {
                                            moveTo(left, top)
                                            lineTo(right, top)
                                            lineTo(right, bottom)
                                            lineTo(left, bottom)
                                            lineTo(left, top)
                                        }
                                        drawPath = newPath
                                    }

                                    DrawPathMode.Oval,
                                    DrawPathMode.OutlinedOval -> {
                                        val newPath = Path().apply {
                                            addOval(
                                                Rect(
                                                    top = max(
                                                        drawDownPosition.y,
                                                        currentDrawPosition.y
                                                    ),
                                                    left = min(
                                                        drawDownPosition.x,
                                                        currentDrawPosition.x
                                                    ),
                                                    bottom = min(
                                                        drawDownPosition.y,
                                                        currentDrawPosition.y
                                                    ),
                                                    right = max(
                                                        drawDownPosition.x,
                                                        currentDrawPosition.x
                                                    ),
                                                )
                                            )
                                        }
                                        drawPath = newPath
                                    }

                                    else -> baseMove()
                                }
                            } else baseMove()

                            onAddPath(
                                UiPathPaint(
                                    path = drawPath,
                                    strokeWidth = strokeWidth,
                                    brushSoftness = brushSoftness,
                                    drawColor = drawColor,
                                    isErasing = isEraserOn,
                                    drawMode = drawMode,
                                    canvasSize = canvasSize,
                                    drawPathMode = drawPathMode
                                )
                            )
                        }

                        currentDrawPosition = Offset.Unspecified
                        previousDrawPosition = Offset.Unspecified
                        motionEvent = MotionEvent.Idle

                        scope.launch {
                            if (drawMode is DrawMode.PathEffect && !isEraserOn) Unit
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

                    paths.forEach { (nonScaledPath, nonScaledStroke, radius, drawColor, isErasing, effect, size, pathMode) ->
                        val stroke by remember(nonScaledPath, canvasSize) {
                            derivedStateOf {
                                nonScaledStroke.toPx(canvasSize)
                            }
                        }
                        val path by remember(nonScaledPath, canvasSize, size) {
                            derivedStateOf {
                                nonScaledPath.scaleToFitCanvas(
                                    currentSize = canvasSize,
                                    oldSize = size
                                ).asAndroidPath()
                            }
                        }
                        val isRect by remember(pathMode) {
                            derivedStateOf {
                                listOf(
                                    DrawPathMode.OutlinedRect,
                                    DrawPathMode.OutlinedOval,
                                    DrawPathMode.Rect,
                                    DrawPathMode.Oval,
                                    DrawPathMode.Lasso
                                ).any { pathMode::class.isInstance(it) }
                            }
                        }

                        val isFilled by remember {
                            derivedStateOf {
                                listOf(
                                    DrawPathMode.Rect,
                                    DrawPathMode.Oval,
                                    DrawPathMode.Lasso
                                ).any { pathMode::class.isInstance(it) }
                            }
                        }

                        if (effect is DrawMode.PathEffect && !isErasing) {
                            var shaderSource by remember(backgroundColor) {
                                mutableStateOf<ImageBitmap?>(null)
                            }
                            LaunchedEffect(shaderSource, invalidations) {
                                if (shaderSource == null || invalidations <= paths.size) {
                                    shaderSource = onRequestFiltering(
                                        drawImageBitmap.overlay(drawBitmap)
                                            .asAndroidBitmap(),
                                        transformationsForMode(effect)
                                    )?.asImageBitmap()?.clipBitmap(
                                        path = path.asComposePath(),
                                        paint = Paint().apply {
                                            if (isFilled) {
                                                style = PaintingStyle.Fill
                                            } else {
                                                style = PaintingStyle.Stroke
                                                this.strokeWidth = stroke
                                                if (isRect) {
                                                    strokeCap = StrokeCap.Square
                                                } else {
                                                    strokeCap = StrokeCap.Round
                                                    strokeJoin = StrokeJoin.Round
                                                }
                                            }

                                            color = Color.Transparent
                                            blendMode = BlendMode.Clear
                                        }
                                    )?.also {
                                        it.prepareToDraw()
                                        invalidations++
                                    }
                                }
                            }
                            if (shaderSource != null) {
                                LaunchedEffect(shaderSource) {
                                    drawPath = Path()
                                }
                                val imagePaint = remember { Paint() }
                                drawImage(
                                    image = shaderSource!!,
                                    topLeftOffset = Offset.Zero,
                                    paint = imagePaint
                                )
                            }
                        } else {
                            val pathPaint by remember(
                                isErasing,
                                isFilled,
                                stroke,
                                isRect,
                                effect,
                                drawColor,
                                radius,
                                canvasSize
                            ) {
                                derivedStateOf {
                                    Paint().apply {
                                        blendMode = if (!isErasing) blendMode else BlendMode.Clear
                                        if (isErasing) {
                                            style = PaintingStyle.Stroke
                                            this.strokeWidth = stroke
                                            strokeCap = StrokeCap.Round
                                            strokeJoin = StrokeJoin.Round
                                        } else {
                                            if (effect !is DrawMode.Text) {
                                                if (isFilled) {
                                                    style = PaintingStyle.Fill
                                                } else {
                                                    style = PaintingStyle.Stroke
                                                    this.strokeWidth = stroke
                                                    if (effect is DrawMode.Highlighter || isRect) {
                                                        strokeCap = StrokeCap.Square
                                                    } else {
                                                        strokeCap = StrokeCap.Round
                                                        strokeJoin = StrokeJoin.Round
                                                    }
                                                }
                                            }
                                        }
                                        color = drawColor
                                        alpha = drawColor.alpha
                                    }.asFrameworkPaint().apply {
                                        if (effect is DrawMode.Neon && !isErasing) {
                                            this.color = Color.White.toArgb()
                                            setShadowLayer(
                                                radius.toPx(canvasSize),
                                                0f,
                                                0f,
                                                drawColor
                                                    .copy(alpha = .8f)
                                                    .toArgb()
                                            )
                                        } else if (radius.value > 0f) {
                                            maskFilter =
                                                BlurMaskFilter(
                                                    radius.toPx(canvasSize),
                                                    BlurMaskFilter.Blur.NORMAL
                                                )
                                        }
                                        if (effect is DrawMode.Text && !isErasing) {
                                            isAntiAlias = true
                                            textSize = strokeWidth.toPx(canvasSize)
                                            if (effect.font != 0) {
                                                typeface =
                                                    ResourcesCompat.getFont(context, effect.font)
                                            }
                                        }
                                    }
                                }
                            }
                            if (effect is DrawMode.Text && !isErasing) {
                                if (effect.isRepeated) {
                                    drawRepeatedTextOnPath(
                                        text = effect.text,
                                        path = path,
                                        paint = pathPaint,
                                        interval = effect.repeatingInterval.toPx(canvasSize)
                                    )
                                } else {
                                    drawTextOnPath(effect.text, path, 0f, 0f, pathPaint)
                                }
                            } else if (effect is DrawMode.Image && !isErasing) {
                                var pathImage by remember(effect, stroke) {
                                    mutableStateOf<Bitmap?>(null)
                                }
                                val imageLoader = LocalImageLoader.current
                                LaunchedEffect(pathImage, effect, stroke, canvasSize) {
                                    if (pathImage == null) {
                                        pathImage = imageLoader.execute(
                                            ImageRequest.Builder(context)
                                                .data(effect.imageData)
                                                .size(strokeWidth.toPx(canvasSize).roundToInt())
                                                .build()
                                        ).drawable?.toBitmap()
                                    }
                                }
                                if (pathImage != null) {
                                    drawRepeatedBitmapOnPath(
                                        bitmap = pathImage!!,
                                        path = path,
                                        paint = pathPaint,
                                        interval = effect.repeatingInterval.toPx(canvasSize)
                                    )
                                }
                            } else {
                                drawPath(path, pathPaint)
                            }
                        }
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
                            var pathImage by remember(drawMode, strokeWidth) {
                                mutableStateOf<Bitmap?>(null)
                            }
                            val imageLoader = LocalImageLoader.current
                            LaunchedEffect(pathImage, drawMode, strokeWidth, canvasSize) {
                                if (pathImage == null) {
                                    pathImage = imageLoader.execute(
                                        ImageRequest.Builder(context)
                                            .data(drawMode.imageData)
                                            .size(strokeWidth.toPx(canvasSize).roundToInt())
                                            .build()
                                    ).drawable?.toBitmap()
                                }
                            }
                            if (pathImage != null) {
                                drawRepeatedBitmapOnPath(
                                    bitmap = pathImage!!,
                                    path = androidPath,
                                    paint = drawPaint,
                                    interval = drawMode.repeatingInterval.toPx(canvasSize)
                                )
                            }
                        } else {
                            drawPath(androidPath, drawPaint)
                        }
                    }
                }
            }

            var shaderBitmap by remember {
                mutableStateOf<ImageBitmap?>(null)
            }

            LaunchedEffect(outputImage, paths, backgroundColor, drawMode) {
                shaderBitmap = onRequestFiltering(
                    outputImage.asAndroidBitmap(),
                    transformationsForMode(drawMode)
                )?.createScaledBitmap(
                    width = imageWidth,
                    height = imageHeight
                )?.asImageBitmap()
            }

            if (drawMode is DrawMode.PathEffect && shaderBitmap != null && !isEraserOn) {
                drawPathCanvas.apply {
                    with(nativeCanvas) {
                        drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)

                        val isRect by remember(drawPathMode) {
                            derivedStateOf {
                                listOf(
                                    DrawPathMode.OutlinedRect,
                                    DrawPathMode.OutlinedOval,
                                    DrawPathMode.Rect,
                                    DrawPathMode.Oval,
                                    DrawPathMode.Lasso
                                ).any { drawPathMode::class.isInstance(it) }
                            }
                        }

                        val isFilled by remember(drawPathMode) {
                            derivedStateOf {
                                listOf(
                                    DrawPathMode.Rect,
                                    DrawPathMode.Oval,
                                    DrawPathMode.Lasso
                                ).any { drawPathMode::class.isInstance(it) }
                            }
                        }

                        val paint by remember(isFilled, strokeWidth, canvasSize, isRect) {
                            derivedStateOf {
                                Paint().apply {
                                    if (isFilled) {
                                        style = PaintingStyle.Fill
                                    } else {
                                        style = PaintingStyle.Stroke
                                        this.strokeWidth = strokeWidth.toPx(canvasSize)
                                        if (isRect) {
                                            strokeCap = StrokeCap.Square
                                        } else {
                                            strokeCap = StrokeCap.Round
                                            strokeJoin = StrokeJoin.Round
                                        }
                                    }

                                    color = Color.Transparent
                                    blendMode = BlendMode.Clear
                                }.asFrameworkPaint()
                            }
                        }
                        val newPath = drawPath.copy().asAndroidPath().apply {
                            fillType = android.graphics.Path.FillType.INVERSE_WINDING
                        }
                        val imagePaint = remember { Paint() }

                        drawImage(
                            shaderBitmap!!, Offset.Zero, imagePaint
                        )
                        drawPath(newPath, paint)
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
            Image(
                modifier = Modifier
                    .matchParentSize()
                    .then(
                        if (!panEnabled) canvasModifier
                        else Modifier
                    )
                    .clip(RoundedCornerShape(2.dp))
                    .transparencyChecker()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant(),
                        RoundedCornerShape(2.dp)
                    ),
                bitmap = previewBitmap,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

private fun Path.copy(): Path = android.graphics.Path(this.asAndroidPath()).asComposePath()

private fun DrawPathMode.drawArrowsIfNeeded(
    drawPath: Path,
    strokeWidth: Pt,
    canvasSize: IntegerSize
) {
    when (this) {
        DrawPathMode.DoublePointingArrow,
        DrawPathMode.DoubleLinePointingArrow -> {

            drawEndArrow(
                drawPath = drawPath,
                strokeWidth = strokeWidth,
                canvasSize = canvasSize
            )

            drawStartArrow(
                drawPath = drawPath,
                strokeWidth = strokeWidth,
                canvasSize = canvasSize
            )
        }

        DrawPathMode.PointingArrow,
        DrawPathMode.LinePointingArrow -> {
            drawEndArrow(
                drawPath = drawPath,
                strokeWidth = strokeWidth,
                canvasSize = canvasSize
            )
        }

        else -> Unit
    }
}

private fun ImageBitmap.clipBitmap(
    path: Path,
    paint: Paint,
): ImageBitmap {
    val bitmap = this.asAndroidBitmap()
    val newPath = android.graphics.Path(path.asAndroidPath())
    AndroidCanvas(bitmap).apply {
        drawPath(
            newPath.apply {
                fillType = android.graphics.Path.FillType.INVERSE_WINDING
            },
            paint.asFrameworkPaint()
        )
    }
    return bitmap.asImageBitmap()
}

private fun ImageBitmap.overlay(overlay: ImageBitmap): ImageBitmap {
    val image = this.asAndroidBitmap()
    val finalBitmap = Bitmap.createBitmap(image.width, image.height, image.config)
    val canvas = AndroidCanvas(finalBitmap)
    canvas.drawBitmap(image, Matrix(), null)
    canvas.drawBitmap(overlay.asAndroidBitmap(), 0f, 0f, null)
    return finalBitmap.asImageBitmap()
}

private fun drawEndArrow(
    drawPath: Path,
    strokeWidth: Pt,
    canvasSize: IntegerSize
) {
    val (preLastPoint, lastPoint) = PathMeasure().apply {
        setPath(drawPath, false)
    }.let {
        Pair(
            it.getPosition(it.length - strokeWidth.toPx(canvasSize) * 3f)
                .takeOrElse { Offset.Zero },
            it.getPosition(it.length).takeOrElse { Offset.Zero }
        )
    }

    val arrowVector = lastPoint - preLastPoint
    fun drawArrow() {

        val (rx1, ry1) = arrowVector.rotateVector(150.0)
        val (rx2, ry2) = arrowVector.rotateVector(210.0)


        drawPath.apply {
            relativeLineTo(rx1, ry1)
            moveTo(lastPoint.x, lastPoint.y)
            relativeLineTo(rx2, ry2)
        }
    }

    if (abs(arrowVector.x) < 3f * strokeWidth.toPx(canvasSize) && abs(
            arrowVector.y
        ) < 3f * strokeWidth.toPx(canvasSize) && preLastPoint != Offset.Zero
    ) {
        drawArrow()
    }
}

private fun drawStartArrow(
    drawPath: Path,
    strokeWidth: Pt,
    canvasSize: IntegerSize
) {
    val (firstPoint, secondPoint) = PathMeasure().apply {
        setPath(drawPath, false)
    }.let {
        Pair(
            it.getPosition(0f).takeOrElse { Offset.Zero },
            it.getPosition(strokeWidth.toPx(canvasSize) * 3f).takeOrElse { Offset.Zero }
        )
    }

    val arrowVector = firstPoint - secondPoint
    fun drawArrow() {

        val (rx1, ry1) = arrowVector.rotateVector(150.0)
        val (rx2, ry2) = arrowVector.rotateVector(210.0)


        drawPath.apply {
            moveTo(firstPoint.x, firstPoint.y)
            relativeLineTo(rx1, ry1)
            moveTo(firstPoint.x, firstPoint.y)
            relativeLineTo(rx2, ry2)
        }
    }

    if (abs(arrowVector.x) < 3f * strokeWidth.toPx(canvasSize) && abs(
            arrowVector.y
        ) < 3f * strokeWidth.toPx(canvasSize) && secondPoint != Offset.Zero
    ) {
        drawArrow()
    }
}