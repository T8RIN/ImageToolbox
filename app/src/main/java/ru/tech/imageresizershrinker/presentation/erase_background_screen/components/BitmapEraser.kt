package ru.tech.imageresizershrinker.presentation.erase_background_screen.components

import android.graphics.Bitmap
import android.graphics.PorterDuff
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import com.smarttoolfactory.gesture.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents
import com.smarttoolfactory.image.util.update
import com.smarttoolfactory.image.zoom.animatedZoom
import com.smarttoolfactory.image.zoom.rememberAnimatedZoomState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block

@Composable
fun BitmapEraser(
    imageBitmap: ImageBitmap,
    paths: List<PathPaint>,
    onAddPath: (PathPaint) -> Unit,
    strokeWidth: Float,
    isErasingOn: Boolean = false,
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
                )
                    .asImageBitmap()
            }

            val erasedBitmap: ImageBitmap = remember {
                Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
                    .asImageBitmap()
            }

            LaunchedEffect(currentPosition) {
                onErased(erasedBitmap.asAndroidBitmap())
            }

            val canvas: Canvas = remember {
                Canvas(erasedBitmap)
            }


            val paint = remember {
                Paint().apply {
                    isAntiAlias = true
                }
            }

            val drawPaint = remember(strokeWidth, isErasingOn) {
                Paint().apply {
                    blendMode = if (isErasingOn) BlendMode.DstIn else BlendMode.Clear
                    style = PaintingStyle.Stroke
                    strokeCap = StrokeCap.Round
                    this.strokeWidth = strokeWidth
                    strokeJoin = StrokeJoin.Round
                    isAntiAlias = true
                }
            }

            var drawPath by remember { mutableStateOf(Path()) }

            canvas.apply {
                val nativeCanvas = this.nativeCanvas
                val canvasWidth = nativeCanvas.width.toFloat()
                val canvasHeight = nativeCanvas.height.toFloat()


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
                                drawPath, drawPaint, isErasingOn
                            )
                        )
                        scope.launch {
                            delay(100L)
                            drawPath = Path()
                        }
                    }

                    else -> Unit
                }

                with(canvas.nativeCanvas) {
                    drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)


                    drawImageRect(
                        image = drawImageBitmap,
                        dstSize = IntSize(canvasWidth.toInt(), canvasHeight.toInt()),
                        paint = paint
                    )

                    paths.forEach { (path, paint) ->
                        this.drawPath(path.asAndroidPath(), paint.asFrameworkPaint())
                    }
                    this.drawPath(drawPath.asAndroidPath(), drawPaint.asFrameworkPaint())
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
                    .then(
                        if (!zoomEnabled) canvasModifier
                        else Modifier
                    )
                    .clipToBounds()
                    .clip(MaterialTheme.shapes.extraSmall)
                    .transparencyChecker()
                    .matchParentSize()
                    .block(MaterialTheme.shapes.extraSmall, Color.Transparent, false),
                bitmap = erasedBitmap,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }
    }
}