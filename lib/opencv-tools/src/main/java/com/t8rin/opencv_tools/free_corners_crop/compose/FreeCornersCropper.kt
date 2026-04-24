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

package com.t8rin.opencv_tools.free_corners_crop.compose

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.magnifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.t8rin.gesture.observePointersCountWithOffset
import com.t8rin.image.ImageWithConstraints
import com.t8rin.opencv_tools.free_corners_crop.FreeCrop
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import kotlin.math.roundToInt

@Composable
fun FreeCornersCropper(
    imageModel: Any?,
    croppingTrigger: Boolean,
    onCropped: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
    showMagnifier: Boolean = true,
    handlesSize: Dp = 8.dp,
    frameStrokeWidth: Dp = 1.2.dp,
    coercePointsToImageArea: Boolean = true,
    overlayColor: Color = Color.Black.copy(0.5f),
    contentPadding: PaddingValues = PaddingValues(24.dp),
    containerModifier: Modifier = Modifier,
    onLoadingStateChange: (Boolean) -> Unit = {}
) {
    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }
    val context = LocalContext.current

    LaunchedEffect(imageModel) {
        bitmap = if (imageModel is Bitmap?) imageModel
        else {
            onLoadingStateChange(true)
            context.imageLoader.execute(
                ImageRequest.Builder(context).data(imageModel)
                    .allowHardware(false).build()
            ).image?.toBitmap()
        }
        onLoadingStateChange(false)
    }

    AnimatedContent(
        targetState = bitmap,
        modifier = containerModifier,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { image ->
        if (image != null) {
            FreeCornersCropper(
                bitmap = image,
                croppingTrigger = croppingTrigger,
                onCropped = onCropped,
                modifier = modifier,
                showMagnifier = showMagnifier,
                contentPadding = contentPadding,
                coercePointsToImageArea = coercePointsToImageArea,
                handlesSize = handlesSize,
                frameStrokeWidth = frameStrokeWidth,
                overlayColor = overlayColor
            )
        }
    }
}

@Composable
fun FreeCornersCropper(
    bitmap: Bitmap,
    croppingTrigger: Boolean,
    onCropped: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
    showMagnifier: Boolean = true,
    handlesSize: Dp = 8.dp,
    frameStrokeWidth: Dp = 1.2.dp,
    coercePointsToImageArea: Boolean = true,
    overlayColor: Color = Color.Black.copy(0.5f),
    contentPadding: PaddingValues = PaddingValues(24.dp)
) {
    val density = LocalDensity.current

    val handleRadiusPx = with(density) {
        handlesSize.toPx()
    }
    val strictCornerTouchRadiusPx = with(density) {
        maxOf(handleRadiusPx * StrictCornerTouchRadiusMultiplier, MinStrictCornerTouchRadius.toPx())
    }
    val cornerTouchRadiusPx = with(density) {
        maxOf(handleRadiusPx * CornerTouchRadiusMultiplier, MinCornerTouchRadius.toPx())
    }
    val edgeTouchRadiusPx = with(density) {
        maxOf(handleRadiusPx * EdgeTouchRadiusMultiplier, MinEdgeTouchRadius.toPx())
    }
    val frameStrokeWidthPx = with(density) {
        frameStrokeWidth.toPx()
    }

    var dragTarget by remember { mutableStateOf<DragTarget>(DragTarget.None) }
    var globalTouchPointersCount by remember { mutableIntStateOf(0) }

    val colorScheme = MaterialTheme.colorScheme

    val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }

    var magnifierCenter by remember { mutableStateOf(Offset.Unspecified) }

    ImageWithConstraints(
        modifier = modifier
            .clipToBounds()
            .observePointersCountWithOffset { size, _ ->
                globalTouchPointersCount = size
            }
            .drawBehind {
                drawRect(overlayColor)
            }
            .zoomable(
                zoomState = rememberZoomState(maxScale = 10f),
                zoomEnabled = globalTouchPointersCount >= 2
            ),
        imageBitmap = imageBitmap,
        drawImage = false
    ) {
        var imageWidth by remember {
            mutableIntStateOf(bitmap.width)
        }

        var imageHeight by remember {
            mutableIntStateOf(bitmap.height)
        }

        val internalPaddingDp = 16.dp
        val internalPadding = with(density) { internalPaddingDp.toPx() }

        var topOffset by remember {
            mutableIntStateOf(0)
        }

        var startOffset by remember {
            mutableIntStateOf(0)
        }

        val drawPoints = rememberSaveable(
            topOffset,
            startOffset,
            imageWidth,
            imageHeight,
            contentPadding,
            stateSaver = OffsetListSaver
        ) {
            mutableStateOf(
                listOf(
                    Offset(
                        x = internalPadding + startOffset,
                        y = internalPadding + topOffset
                    ),
                    Offset(
                        x = imageWidth - internalPadding + startOffset,
                        y = internalPadding + topOffset
                    ),
                    Offset(
                        x = imageWidth - internalPadding + startOffset,
                        y = imageHeight - internalPadding + topOffset
                    ),
                    Offset(
                        x = internalPadding + startOffset,
                        y = imageHeight - internalPadding + topOffset
                    )
                )
            )
        }

        val selectedPointIndices = dragTarget.pointIndices
        val pointScales = List(drawPoints.value.size) {
            animateFloatAsState(if (it in selectedPointIndices) 1.4f else 1f)
        }

        LaunchedEffect(croppingTrigger) {
            if (croppingTrigger) {
                val widthScale = bitmap.width.toFloat() / imageWidth
                val heightScale = bitmap.height.toFloat() / imageHeight
                onCropped(
                    FreeCrop.crop(
                        bitmap = bitmap,
                        points = drawPoints.value.map {
                            Offset(
                                x = ((it.x - startOffset) * widthScale).roundToInt()
                                    .coerceIn(0, bitmap.width).toFloat(),
                                y = ((it.y - topOffset) * heightScale).roundToInt()
                                    .coerceIn(0, bitmap.height).toFloat()
                            )
                        }
                    )
                )
            }
        }

        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = Modifier
                .padding(internalPaddingDp)
                .padding(contentPadding)
                .aspectRatio(bitmap.width / bitmap.height.toFloat())
                .onGloballyPositioned {
                    topOffset = it.positionInParent().y.toInt()
                    startOffset = it.positionInParent().x.toInt()
                    imageWidth = it.size.width
                    imageHeight = it.size.height
                },
            contentScale = ContentScale.FillBounds
        )

        fun Offset.coerceToImageBounds(): Offset = coerceIn(
            horizontalRange = (startOffset).toFloat()..((imageWidth + startOffset).toFloat()),
            verticalRange = (topOffset).toFloat()..((imageHeight + topOffset).toFloat())
        )

        fun Offset.coerceDragAmountFor(points: List<Offset>): Offset {
            if (!coercePointsToImageArea) return this

            val horizontalRange = (startOffset).toFloat()..((imageWidth + startOffset).toFloat())
            val verticalRange = (topOffset).toFloat()..((imageHeight + topOffset).toFloat())
            var minX = Float.NEGATIVE_INFINITY
            var maxX = Float.POSITIVE_INFINITY
            var minY = Float.NEGATIVE_INFINITY
            var maxY = Float.POSITIVE_INFINITY

            points.forEach { point ->
                minX = minX.coerceAtLeast(horizontalRange.start - point.x)
                maxX = maxX.coerceAtMost(horizontalRange.endInclusive - point.x)
                minY = minY.coerceAtLeast(verticalRange.start - point.y)
                maxY = maxY.coerceAtMost(verticalRange.endInclusive - point.y)
            }

            return Offset(
                x = x.coerceIn(minX, maxX),
                y = y.coerceIn(minY, maxY)
            )
        }

        LaunchedEffect(coercePointsToImageArea) {
            drawPoints.value = drawPoints.value.map {
                it.coerceToImageBounds()
            }
        }

        Canvas(
            modifier = Modifier
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .size(maxWidth, maxHeight)
                .then(
                    if (showMagnifier && magnifierCenter.isSpecified) {
                        Modifier.magnifier(
                            magnifierCenter = {
                                magnifierCenter - Offset(0f, 100.dp.toPx())
                            },
                            sourceCenter = {
                                magnifierCenter
                            },
                            size = DpSize(100.dp, 100.dp),
                            cornerRadius = 50.dp,
                            elevation = 2.dp
                        )
                    } else Modifier
                )
                .pointerInput(
                    contentPadding,
                    coercePointsToImageArea,
                    handleRadiusPx,
                    strictCornerTouchRadiusPx,
                    cornerTouchRadiusPx,
                    edgeTouchRadiusPx,
                    bitmap,
                    showMagnifier
                ) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            dragTarget = findDragTarget(
                                points = drawPoints.value,
                                touchPosition = offset,
                                strictCornerTouchRadius = strictCornerTouchRadiusPx,
                                cornerTouchRadius = cornerTouchRadiusPx,
                                edgeTouchRadius = edgeTouchRadiusPx
                            )

                            magnifierCenter =
                                if (showMagnifier && dragTarget != DragTarget.None) {
                                    dragTarget.focusPoint(drawPoints.value)
                                } else Offset.Unspecified
                        },
                        onDrag = { _, dragAmount ->
                            val selectedIndices = dragTarget.pointIndices
                            val selectedPoints = selectedIndices.mapNotNull { index ->
                                drawPoints.value.getOrNull(index)
                            }
                            val coercedDragAmount = dragAmount.coerceDragAmountFor(selectedPoints)

                            if (selectedIndices.isNotEmpty()) {
                                drawPoints.value = drawPoints.value
                                    .toMutableList()
                                    .apply {
                                        selectedIndices.forEach { index ->
                                            this[index] = this[index]
                                                .plus(coercedDragAmount)
                                                .let {
                                                    if (coercePointsToImageArea) {
                                                        it.coerceToImageBounds()
                                                    } else it
                                                }
                                        }
                                    }

                                if (showMagnifier) {
                                    magnifierCenter = dragTarget.focusPoint(drawPoints.value)
                                }
                            }
                        },
                        onDragEnd = {
                            dragTarget = DragTarget.None
                            magnifierCenter = Offset.Unspecified
                        },
                        onDragCancel = {
                            dragTarget = DragTarget.None
                            magnifierCenter = Offset.Unspecified
                        }
                    )
                }
        ) {
            val (x, y) = drawPoints.value[0]
            val (x1, y1) = drawPoints.value[1]
            val (x2, y2) = drawPoints.value[2]
            val (x3, y3) = drawPoints.value[3]

            val framePath = Path().apply {
                moveTo(x, y)
                lineTo(x1, y1)
                lineTo(x2, y2)
                lineTo(x3, y3)
                close()
            }

            drawPath(
                path = framePath,
                brush = SolidColor(Color.Transparent),
                blendMode = BlendMode.Clear
            )

            drawPath(
                path = framePath,
                brush = SolidColor(colorScheme.primaryContainer),
                style = Stroke(frameStrokeWidthPx)
            )

            drawPoints.value.forEachIndexed { index, point ->
                val scale = pointScales[index].value

                drawCircle(
                    color = colorScheme.primary,
                    center = point,
                    radius = handleRadiusPx * scale
                )
                drawCircle(
                    color = colorScheme.primaryContainer,
                    center = point,
                    radius = handleRadiusPx * 0.8f * scale
                )
            }
        }
    }
}

private sealed class DragTarget {
    object None : DragTarget()
    data class Corner(val index: Int) : DragTarget()
    data class Edge(
        val firstIndex: Int,
        val secondIndex: Int
    ) : DragTarget()

    val pointIndices: List<Int>
        get() = when (this) {
            is Corner -> listOf(index)
            is Edge -> listOf(firstIndex, secondIndex)
            None -> emptyList()
        }
}

private fun findDragTarget(
    points: List<Offset>,
    touchPosition: Offset,
    strictCornerTouchRadius: Float,
    cornerTouchRadius: Float,
    edgeTouchRadius: Float
): DragTarget {
    val touchedStrictCornerIndex = findTouchedCornerIndex(
        points = points,
        touchPosition = touchPosition,
        distanceThresholdSquared = strictCornerTouchRadius.square()
    )

    if (touchedStrictCornerIndex != null) return DragTarget.Corner(touchedStrictCornerIndex)

    val touchedEdge = edgeIndices
        .mapNotNull { (firstIndex, secondIndex) ->
            val firstPoint = points.getOrNull(firstIndex) ?: return@mapNotNull null
            val secondPoint = points.getOrNull(secondIndex) ?: return@mapNotNull null
            val edgeTouch = touchPosition.edgeTouch(firstPoint, secondPoint)

            if (
                edgeTouch.projection in EdgeTouchProjectionRange &&
                edgeTouch.distanceSquared < edgeTouchRadius.square()
            ) {
                DragTarget.Edge(firstIndex, secondIndex) to edgeTouch.distanceSquared
            } else null
        }
        .minByOrNull { it.second }
        ?.first

    if (touchedEdge != null) return touchedEdge

    val touchedCornerIndex = findTouchedCornerIndex(
        points = points,
        touchPosition = touchPosition,
        distanceThresholdSquared = cornerTouchRadius.square()
    )

    return touchedCornerIndex?.let(DragTarget::Corner) ?: DragTarget.None
}

private fun DragTarget.focusPoint(points: List<Offset>): Offset {
    return when (this) {
        is DragTarget.Corner -> points.getOrNull(index) ?: Offset.Unspecified
        is DragTarget.Edge -> {
            val firstPoint = points.getOrNull(firstIndex) ?: return Offset.Unspecified
            val secondPoint = points.getOrNull(secondIndex) ?: return Offset.Unspecified
            Offset(
                x = (firstPoint.x + secondPoint.x) / 2f,
                y = (firstPoint.y + secondPoint.y) / 2f
            )
        }

        DragTarget.None -> Offset.Unspecified
    }
}

private fun findTouchedCornerIndex(
    points: List<Offset>,
    touchPosition: Offset,
    distanceThresholdSquared: Float
): Int? = points
    .mapIndexedNotNull { index, point ->
        val distanceSquared = point.minus(touchPosition).getDistanceSquared()
        if (distanceSquared < distanceThresholdSquared) {
            index to distanceSquared
        } else null
    }
    .minByOrNull { it.second }
    ?.first

private fun Float.square(): Float = this * this

private data class EdgeTouch(
    val distanceSquared: Float,
    val projection: Float
)

private fun Offset.edgeTouch(start: Offset, end: Offset): EdgeTouch {
    val segmentX = end.x - start.x
    val segmentY = end.y - start.y
    val lengthSquared = segmentX * segmentX + segmentY * segmentY

    if (lengthSquared == 0f) {
        return EdgeTouch(
            distanceSquared = minus(start).getDistanceSquared(),
            projection = 0f
        )
    }

    val projection = (((x - start.x) * segmentX + (y - start.y) * segmentY) / lengthSquared)
        .coerceIn(0f, 1f)
    val closestPoint = Offset(
        x = start.x + projection * segmentX,
        y = start.y + projection * segmentY
    )

    return EdgeTouch(
        distanceSquared = minus(closestPoint).getDistanceSquared(),
        projection = projection
    )
}

private val edgeIndices = listOf(
    0 to 1,
    1 to 2,
    2 to 3,
    3 to 0
)

private val EdgeTouchProjectionRange = 0.20f..0.80f

private val MinStrictCornerTouchRadius = 22.dp
private val MinCornerTouchRadius = 30.dp
private val MinEdgeTouchRadius = 22.dp

private const val StrictCornerTouchRadiusMultiplier = 1.9f
private const val CornerTouchRadiusMultiplier = 3.2f
private const val EdgeTouchRadiusMultiplier = 2.5f

private val OffsetListSaver: Saver<List<Offset>, String> = Saver(
    save = { list ->
        list.joinToString(",") { (x, y) ->
            "$x:$y"
        }
    },
    restore = { string ->
        string.split(",").map { o ->
            val (x, y) = o.split(":").map { it.toFloat() }
            Offset(x, y)
        }
    }
)

private fun Offset.coerceIn(
    horizontalRange: ClosedRange<Float>,
    verticalRange: ClosedRange<Float>
) = Offset(this.x.coerceIn(horizontalRange), this.y.coerceIn(verticalRange))
