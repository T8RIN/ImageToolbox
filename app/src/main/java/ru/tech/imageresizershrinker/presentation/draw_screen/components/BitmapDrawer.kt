package ru.tech.imageresizershrinker.presentation.draw_screen.components

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Matrix
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isUnspecified
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents
import com.smarttoolfactory.image.util.update
import com.smarttoolfactory.image.zoom.animatedZoom
import com.smarttoolfactory.image.zoom.rememberAnimatedZoomState
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.PathPaint
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.transformation.filter.StackBlurFilter
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.transparencyChecker
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import android.graphics.Canvas as AndroidCanvas


@Composable
fun BitmapDrawer(
    imageBitmap: ImageBitmap,
    imageManager: ImageManager<Bitmap, *>,
    paths: List<PathPaint>,
    brushSoftness: Float,
    onAddPath: (PathPaint) -> Unit,
    strokeWidth: Float,
    isEraserOn: Boolean,
    drawMode: DrawMode,
    modifier: Modifier,
    drawArrowsEnabled: Boolean,
    onDraw: (Bitmap) -> Unit,
    backgroundColor: Color,
    zoomEnabled: Boolean,
    drawColor: Color
) {
    val zoomState = rememberAnimatedZoomState(maxZoom = 30f)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val transformations = remember(context) {
        listOf(
            StackBlurFilter(
                context,
                0.3f to 20
            )
        )
    }

    //TODO: AUTO SCALE PATH TO CANVAS SIZE

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

            var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
            // This is our motion event we get from touch motion
            var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
            // This is previous motion event before next touch is saved into this current position
            var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

            val imageWidth = constraints.maxWidth
            val imageHeight = constraints.maxHeight


            val drawImageBitmap = remember {
                Bitmap.createScaledBitmap(
                    imageBitmap.asAndroidBitmap(),
                    imageWidth,
                    imageHeight,
                    false
                ).apply {
                    val canvas = AndroidCanvas(this)
                    val paint = android.graphics.Paint().apply {
                        color = backgroundColor.toArgb()
                    }
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }.asImageBitmap()
            }

            val drawBitmap: ImageBitmap = remember {
                Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
                    .asImageBitmap()
            }

            val drawPathBitmap: ImageBitmap = remember {
                Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
                    .asImageBitmap()
            }

            val outputImage = drawImageBitmap.overlay(drawBitmap)

            SideEffect {
                onDraw(outputImage.asAndroidBitmap())
            }

            val canvas: Canvas = remember {
                Canvas(drawBitmap)
            }

            val drawPathCanvas: Canvas = remember {
                Canvas(drawPathBitmap)
            }

            val drawPaint =
                remember(strokeWidth, isEraserOn, drawColor, brushSoftness, drawMode) {
                    Paint().apply {
                        blendMode = if (!isEraserOn) blendMode else BlendMode.Clear
                        style = PaintingStyle.Stroke
                        strokeCap =
                            if (drawMode is DrawMode.Highlighter) StrokeCap.Square else StrokeCap.Round
                        color = if (drawMode is DrawMode.PrivacyBlur) {
                            Color.Transparent
                        } else drawColor
                        alpha = drawColor.alpha
                        this.strokeWidth = strokeWidth
                        strokeJoin = StrokeJoin.Round
                        isAntiAlias = true
                    }.asFrameworkPaint().apply {
                        if (drawMode is DrawMode.Neon && !isEraserOn) {
                            this.color = Color.White.toArgb()
                            setShadowLayer(
                                brushSoftness,
                                0f,
                                0f,
                                drawColor
                                    .copy(alpha = .8f)
                                    .toArgb()
                            )
                        } else if (brushSoftness > 0f) {
                            maskFilter = BlurMaskFilter(brushSoftness, BlurMaskFilter.Blur.NORMAL)
                        }
                    }
                }

            var drawPath by remember(drawMode) { mutableStateOf(Path()) }

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

                        if (drawArrowsEnabled && !isEraserOn) {
                            val preLastPoint = PathMeasure().apply {
                                setPath(drawPath, false)
                            }.let {
                                it.getPosition(it.length - strokeWidth * 3f)
                            }.let { if (it.isUnspecified) Offset.Zero else it }

                            val lastPoint = currentPosition.let {
                                if (it.isUnspecified) Offset.Zero else it
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

                            if (abs(arrowVector.x) < 3f * strokeWidth && abs(arrowVector.y) < 3f * strokeWidth && preLastPoint != Offset.Zero) {
                                drawArrow()
                            }
                        }

                        currentPosition = Offset.Unspecified
                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                        onAddPath(
                            PathPaint(
                                path = drawPath,
                                strokeWidth = strokeWidth,
                                brushSoftness = brushSoftness,
                                drawColor = drawColor,
                                isErasing = isEraserOn,
                                drawMode = drawMode
                            )
                        )
                        scope.launch {
                            if (drawMode is DrawMode.PrivacyBlur && !isEraserOn) Unit
                            else drawPath = Path()
                        }
                    }

                    else -> Unit
                }

                with(nativeCanvas) {
                    drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
                    drawColor(backgroundColor.toArgb())

                    paths.forEach { (path, stroke, radius, drawColor, isErasing, effect) ->
                        var shaderSource by remember {
                            mutableStateOf<ImageBitmap?>(null)
                        }

                        if (effect is DrawMode.PrivacyBlur && !isErasing) {
                            LaunchedEffect(shaderSource) {
                                if (shaderSource == null) {
                                    shaderSource = imageManager.transform(
                                        image = outputImage.asAndroidBitmap(),
                                        transformations = transformations
                                    )?.asImageBitmap()?.clipBitmap(
                                        path = path,
                                        paint = Paint().apply {
                                            style = PaintingStyle.Stroke
                                            strokeCap = StrokeCap.Round
                                            this.strokeWidth = stroke
                                            strokeJoin = StrokeJoin.Round
                                            isAntiAlias = true
                                            color = Color.Transparent
                                            blendMode = BlendMode.Clear
                                        }
                                    )
                                }
                            }
                            if (shaderSource != null) {
                                LaunchedEffect(shaderSource) {
                                    drawPath = Path()
                                }
                                drawImage(
                                    shaderSource!!, Offset.Zero, Paint()
                                )
                            }
                        } else {
                            drawPath(
                                path.asAndroidPath(),
                                Paint().apply {
                                    blendMode = if (!isErasing) blendMode else BlendMode.Clear
                                    style = PaintingStyle.Stroke
                                    strokeCap =
                                        if (effect is DrawMode.Highlighter) StrokeCap.Square else StrokeCap.Round
                                    this.strokeWidth = stroke
                                    strokeJoin = StrokeJoin.Round
                                    isAntiAlias = true
                                    color = drawColor
                                    alpha = drawColor.alpha
                                }.asFrameworkPaint().apply {
                                    if (effect is DrawMode.Neon && !isErasing) {
                                        this.color = Color.White.toArgb()
                                        setShadowLayer(
                                            radius,
                                            0f,
                                            0f,
                                            drawColor
                                                .copy(alpha = .8f)
                                                .toArgb()
                                        )
                                    } else if (radius > 0f) {
                                        maskFilter =
                                            BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
                                    }
                                }
                            )
                        }
                    }

                    if (drawMode !is DrawMode.PrivacyBlur || isEraserOn) {
                        drawPath(
                            drawPath.asAndroidPath(),
                            drawPaint
                        )
                    }
                }
            }

            var blurredBitmap by remember {
                mutableStateOf<ImageBitmap?>(null)
            }

            LaunchedEffect(outputImage, paths) {
                blurredBitmap = imageManager.transform(
                    image = outputImage.asAndroidBitmap(),
                    transformations = transformations
                )?.asImageBitmap()
            }

            val shaderBitmap = remember(blurredBitmap) {
                blurredBitmap?.asAndroidBitmap()?.let {
                    Bitmap.createScaledBitmap(
                        it,
                        imageWidth,
                        imageHeight,
                        false
                    ).asImageBitmap()
                }
            }

            if (drawMode is DrawMode.PrivacyBlur && shaderBitmap != null && !isEraserOn) {
                drawPathCanvas.apply {
                    with(nativeCanvas) {
                        drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)

                        val paint = Paint().apply {
                            style = PaintingStyle.Stroke
                            strokeCap = StrokeCap.Round
                            this.strokeWidth = strokeWidth
                            strokeJoin = StrokeJoin.Round
                            isAntiAlias = true
                            color = Color.Transparent
                            blendMode = BlendMode.Clear
                        }
                        val newPath = android.graphics.Path(drawPath.asAndroidPath())

                        drawImage(
                            shaderBitmap, Offset.Zero, Paint()
                        )
                        drawPath(
                            newPath.apply {
                                fillType = android.graphics.Path.FillType.INVERSE_WINDING
                            },
                            paint.asFrameworkPaint()
                        )
                    }
                }
            }

            val canvasModifier = Modifier.pointerMotionEvents(
                onDown = { pointerInputChange ->
                    motionEvent = MotionEvent.Down
                    currentPosition = pointerInputChange.position
                    pointerInputChange.consume()
                },
                onMove = { pointerInputChange ->
                    motionEvent = MotionEvent.Move
                    currentPosition = pointerInputChange.position
                    pointerInputChange.consume()
                },
                onUp = { pointerInputChange ->
                    motionEvent = MotionEvent.Up
                    pointerInputChange.consume()
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
                bitmap = outputImage.overlay(drawPathBitmap),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

private fun Offset.rotateVector(
    angle: Double
): Offset = Offset(
    x = (x * cos(Math.toRadians(angle)) - y * sin(Math.toRadians(angle))).toFloat(),
    y = (x * sin(Math.toRadians(angle)) + y * cos(Math.toRadians(angle))).toFloat()
)

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