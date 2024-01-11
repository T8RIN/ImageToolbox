package ru.tech.imageresizershrinker.feature.erase_background.presentation.components

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ZoomableDefaults.defaultZoomOnDoubleTap
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.domain.image.draw.Pt
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.ui.model.UiPathPaint
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.createScaledBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.scaleToFitCanvas
import ru.tech.imageresizershrinker.core.ui.widget.modifier.observePointersCount
import ru.tech.imageresizershrinker.core.ui.widget.modifier.smartDelayAfterDownInMillis
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker

@Composable
fun BitmapEraser(
    imageBitmap: ImageBitmap,
    imageBitmapForShader: ImageBitmap?,
    paths: List<UiPathPaint>,
    brushSoftness: Pt,
    onAddPath: (UiPathPaint) -> Unit,
    strokeWidth: Pt,
    isRecoveryOn: Boolean = false,
    modifier: Modifier,
    onErased: (Bitmap) -> Unit = {},
    zoomEnabled: Boolean
) {
    val zoomState = rememberZoomState(maxScale = 30f)
    val scope = rememberCoroutineScope()

    var pointersCount by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .observePointersCount {
                pointersCount = it
            }
            .zoomable(
                zoomState = zoomState,
                enabled = { _, _ ->
                    (pointersCount >= 2 || zoomEnabled)
                },
                enableOneFingerZoom = false,
                onDoubleTap = { pos ->
                    if (zoomEnabled) zoomState.defaultZoomOnDoubleTap(pos)
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
            // This is our motion event we get from touch motion
            var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
            // This is previous motion event before next touch is saved into this current position
            var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

            val imageWidth = constraints.maxWidth
            val imageHeight = constraints.maxHeight


            val drawImageBitmap = remember(constraints) {
                imageBitmap
                    .asAndroidBitmap()
                    .createScaledBitmap(
                        width = imageWidth,
                        height = imageHeight
                    )
                    .asImageBitmap()
            }

            val shaderBitmap = remember(imageBitmapForShader, constraints) {
                imageBitmapForShader
                    ?.asAndroidBitmap()
                    ?.createScaledBitmap(
                        imageWidth,
                        imageHeight
                    )
                    ?.asImageBitmap()
            }

            val erasedBitmap: ImageBitmap = remember(constraints) {
                Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
                    .asImageBitmap()
            }

            val outputImage by remember(invalidations) {
                derivedStateOf {
                    erasedBitmap.recompose()
                }
            }

            LaunchedEffect(invalidations) {
                onErased(outputImage.asAndroidBitmap())
            }

            val canvas: Canvas = remember(constraints) {
                Canvas(erasedBitmap)
            }

            val paint = remember { Paint() }

            val canvasSize = remember(canvas.nativeCanvas) {
                IntegerSize(canvas.nativeCanvas.width, canvas.nativeCanvas.height)
            }

            val drawPaint = remember(strokeWidth, isRecoveryOn, brushSoftness, canvasSize) {
                Paint().apply {
                    blendMode = if (isRecoveryOn) blendMode else BlendMode.Clear
                    style = PaintingStyle.Stroke
                    strokeCap = StrokeCap.Round
                    shader = if (isRecoveryOn) shaderBitmap?.let { ImageShader(it) } else shader
                    this.strokeWidth = strokeWidth.toPx(canvasSize)
                    strokeJoin = StrokeJoin.Round
                    isAntiAlias = true
                }.asFrameworkPaint().apply {
                    if (brushSoftness.value > 0f) maskFilter =
                        BlurMaskFilter(brushSoftness.toPx(canvasSize), BlurMaskFilter.Blur.NORMAL)
                }
            }

            var drawPath by remember(
                strokeWidth,
                brushSoftness
            ) { mutableStateOf(Path()) }

            canvas.apply {
                when (motionEvent) {

                    MotionEvent.Down -> {
                        if (currentPosition.isSpecified) {
                            drawPath.moveTo(currentPosition.x, currentPosition.y)
                            previousPosition = currentPosition
                        } else {
                            drawPath = Path()
                        }
                        motionEvent = MotionEvent.Idle
                    }

                    MotionEvent.Move -> {
                        if (previousPosition.isSpecified && currentPosition.isSpecified) {
                            drawPath.quadraticBezierTo(
                                previousPosition.x,
                                previousPosition.y,
                                (previousPosition.x + currentPosition.x) / 2,
                                (previousPosition.y + currentPosition.y) / 2
                            )
                            previousPosition = currentPosition
                        }
                        motionEvent = MotionEvent.Idle
                    }

                    MotionEvent.Up -> {
                        PathMeasure().apply {
                            setPath(drawPath, false)
                        }.let {
                            it.getPosition(it.length)
                        }.takeOrElse { currentPosition }.let { lastPoint ->
                            if (currentPosition.isSpecified) {
                                drawPath.moveTo(lastPoint.x, lastPoint.y)
                                drawPath.lineTo(currentPosition.x, currentPosition.y)
                                onAddPath(
                                    UiPathPaint(
                                        path = drawPath,
                                        strokeWidth = strokeWidth,
                                        brushSoftness = brushSoftness,
                                        isErasing = isRecoveryOn,
                                        canvasSize = canvasSize
                                    )
                                )
                            }
                        }
                        currentPosition = Offset.Unspecified
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


                    drawImageRect(
                        image = drawImageBitmap,
                        dstSize = IntSize(canvasSize.width, canvasSize.height),
                        paint = paint
                    )

                    paths.forEach { (nonScaledPath, stroke, radius, _, isRecoveryOn, _, size) ->
                        val path = nonScaledPath.scaleToFitCanvas(
                            currentSize = canvasSize,
                            oldSize = size
                        )
                        this.drawPath(
                            path.asAndroidPath(),
                            Paint().apply {
                                blendMode = if (isRecoveryOn) blendMode else BlendMode.Clear
                                style = PaintingStyle.Stroke
                                strokeCap = StrokeCap.Round
                                shader =
                                    if (isRecoveryOn) shaderBitmap?.let { ImageShader(it) } else shader
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
                        )
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
                    drawStartedWithOnePointer = pointersCount <= 1

                    if (drawStartedWithOnePointer) {
                        motionEvent = MotionEvent.Down
                        currentPosition = pointerInputChange.position
                        pointerInputChange.consume()
                        invalidations++
                    }
                },
                onMove = { pointerInputChange ->
                    if (drawStartedWithOnePointer) {
                        motionEvent = MotionEvent.Move
                        currentPosition = pointerInputChange.position
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
                delayAfterDownInMillis = smartDelayAfterDownInMillis(pointersCount)
            )

            Image(
                modifier = Modifier
                    .matchParentSize()
                    .then(
                        if (!zoomEnabled) canvasModifier
                        else Modifier
                    )
                    .clip(RoundedCornerShape(2.dp))
                    .transparencyChecker()
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