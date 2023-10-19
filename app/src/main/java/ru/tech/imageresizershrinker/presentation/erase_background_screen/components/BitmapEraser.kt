package ru.tech.imageresizershrinker.presentation.erase_background_screen.components

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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents
import com.smarttoolfactory.image.util.update
import com.smarttoolfactory.image.zoom.animatedZoom
import com.smarttoolfactory.image.zoom.rememberAnimatedZoomState
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.domain.model.IntegerSize
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.helper.scaleToFitCanvas
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.transparencyChecker

@Composable
fun BitmapEraser(
    imageBitmap: ImageBitmap,
    imageBitmapForShader: ImageBitmap?,
    paths: List<PathPaint>,
    brushSoftness: Pt,
    onAddPath: (PathPaint) -> Unit,
    strokeWidth: Pt,
    isRecoveryOn: Boolean = false,
    modifier: Modifier,
    onErased: (Bitmap) -> Unit = {},
    zoomEnabled: Boolean
) {
    val zoomState = rememberAnimatedZoomState(maxZoom = 30f)
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (zoomEnabled) {
                    Modifier.animatedZoom(animatedZoomState = zoomState)
                } else {
                    Modifier.graphicsLayer {
                        update(zoomState)
                    }
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
                Bitmap.createScaledBitmap(
                    imageBitmap.asAndroidBitmap(),
                    imageWidth,
                    imageHeight,
                    false
                ).asImageBitmap()
            }

            val shaderBitmap = remember(imageBitmapForShader, constraints) {
                imageBitmapForShader?.asAndroidBitmap()?.let {
                    Bitmap.createScaledBitmap(
                        it,
                        imageWidth,
                        imageHeight,
                        false
                    ).asImageBitmap()
                }
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
                        drawPath.moveTo(currentPosition.x, currentPosition.y)
                        previousPosition = currentPosition
                    }

                    MotionEvent.Move -> {
                        drawPath.quadraticBezierTo(
                            previousPosition.x,
                            previousPosition.y,
                            (previousPosition.x + currentPosition.x) / 2,
                            (previousPosition.y + currentPosition.y) / 2
                        )
                        previousPosition = currentPosition
                    }

                    MotionEvent.Up -> {
                        drawPath.lineTo(currentPosition.x, currentPosition.y)
                        currentPosition = Offset.Unspecified
                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                        onAddPath(
                            PathPaint(
                                path = drawPath,
                                strokeWidth = strokeWidth,
                                brushSoftness = brushSoftness,
                                isErasing = isRecoveryOn,
                                canvasSize = canvasSize
                            )
                        )
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

            val canvasModifier = Modifier.pointerMotionEvents(
                onDown = { pointerInputChange ->
                    motionEvent = MotionEvent.Down
                    currentPosition = pointerInputChange.position
                    pointerInputChange.consume()
                    invalidations++
                },
                onMove = { pointerInputChange ->
                    motionEvent = MotionEvent.Move
                    currentPosition = pointerInputChange.position
                    pointerInputChange.consume()
                    invalidations++
                },
                onUp = { pointerInputChange ->
                    motionEvent = MotionEvent.Up
                    pointerInputChange.consume()
                    invalidations++
                },
                delayAfterDownInMillis = 20
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