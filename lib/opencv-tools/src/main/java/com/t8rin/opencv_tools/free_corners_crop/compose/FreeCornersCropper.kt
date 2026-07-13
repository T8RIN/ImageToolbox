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
import android.net.Uri
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.magnifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import com.t8rin.gesture.detectTransformGestures
import com.t8rin.image.ImageWithConstraints
import com.t8rin.opencv_tools.free_corners_crop.FreeCrop
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.roundToInt

@Composable
fun FreeCornersCropper(
    bitmap: Bitmap,
    sourceImageUri: Uri? = null,
    sourceImageSize: IntSize = IntSize(bitmap.width, bitmap.height),
    croppingTrigger: Boolean,
    onCropped: (Uri?) -> Unit,
    modifier: Modifier = Modifier,
    showMagnifier: Boolean = true,
    handlesSize: Dp = 8.dp,
    frameStrokeWidth: Dp = 1.2.dp,
    onZoomChange: (Float) -> Unit = {},
    coercePointsToImageArea: Boolean = true,
    isOverlayDraggable: Boolean = true,
    overlayColor: Color = Color.Black.copy(0.55f),
    contentPadding: PaddingValues = PaddingValues(24.dp),
    gridColor: Color,
    handlesColor: Color
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageKey = FreeCornersCropperImageKey(
        bitmap = bitmap,
        generationId = bitmap.generationId
    )

    val handleRadiusPx = with(density) {
        handlesSize.toPx()
    }
    val strictCornerTouchRadiusPx = with(density) {
        maxOf(
            handleRadiusPx * STRICT_CORNER_TOUCH_RADIUS_MULTIPLIER,
            MinStrictCornerTouchRadius.toPx()
        )
    }
    val cornerTouchRadiusPx = with(density) {
        maxOf(handleRadiusPx * CORNER_TOUCH_RADIUS_MULTIPLIER, MinCornerTouchRadius.toPx())
    }
    val edgeTouchRadiusPx = with(density) {
        maxOf(handleRadiusPx * EDGE_TOUCH_RADIUS_MULTIPLIER, MinEdgeTouchRadius.toPx())
    }
    val frameStrokeWidthPx = with(density) {
        frameStrokeWidth.toPx()
    }
    val middleHandleStrokeWidthPx = with(density) {
        MiddleHandleStrokeWidth.toPx()
    }
    val imageBitmap = remember(imageKey) { bitmap.asImageBitmap() }

    var dragTarget by remember(imageKey) { mutableStateOf<DragTarget>(DragTarget.None) }
    var magnifierCenter by remember(imageKey) { mutableStateOf(Offset.Unspecified) }
    var baseImageBounds by remember(imageKey) { mutableStateOf(Rect.Zero) }
    var drawPoints by remember(imageKey) { mutableStateOf(emptyList<Offset>()) }
    var imageScale by remember(imageKey) { mutableFloatStateOf(1f) }
    var imageTranslation by remember(imageKey) { mutableStateOf(Offset.Zero) }
    var transformAnimationJob by remember { mutableStateOf<Job?>(null) }

    fun updateImageTransform(scale: Float, translation: Offset) {
        imageScale = scale
        imageTranslation = translation
    }

    onZoomChange(imageScale)

    ImageWithConstraints(
        modifier = modifier
            .clipToBounds()
            .pointerInput(imageKey, coercePointsToImageArea) {
                detectOneFingerZoomGestures(
                    onDoubleTap = { position ->
                        transformAnimationJob?.cancel()
                        val minScale = if (coercePointsToImageArea) {
                            calculateMinimumScale(
                                cropBounds = drawPoints.boundingRect(),
                                imageBounds = baseImageBounds
                            )
                        } else {
                            MINIMUM_CROP_ZOOM
                        }
                        val zoomedScale = DEFAULT_DOUBLE_TAP_SCALE.coerceIn(minScale, MAX_ZOOM)
                        val targetScale = if (
                            imageScale >= zoomedScale - SCALE_COMPARISON_TOLERANCE
                        ) {
                            minScale
                        } else {
                            zoomedScale
                        }
                        val target = calculateTransformAfterZoom(
                            currentScale = imageScale,
                            currentTranslation = imageTranslation,
                            targetScale = targetScale,
                            centroid = position,
                            transformCenter = baseImageBounds.center
                        )
                        transformAnimationJob = scope.launch {
                            animateTransformTo(
                                startScale = imageScale,
                                startTranslation = imageTranslation,
                                targetScale = targetScale,
                                targetTranslation = if (coercePointsToImageArea) {
                                    coerceImageTranslation(
                                        translation = target.translation,
                                        scale = targetScale,
                                        imageBounds = baseImageBounds,
                                        cropBounds = drawPoints.boundingRect(),
                                        coerceToCrop = true
                                    )
                                } else {
                                    target.translation
                                },
                                onUpdate = ::updateImageTransform
                            )
                        }
                    },
                    onZoomStart = {
                        transformAnimationJob?.cancel()
                    },
                    onZoom = { centroid, zoom ->
                        val minScale = if (coercePointsToImageArea) {
                            calculateMinimumScale(
                                cropBounds = drawPoints.boundingRect(),
                                imageBounds = baseImageBounds
                            )
                        } else {
                            MINIMUM_CROP_ZOOM
                        }
                        val targetScale = (imageScale * zoom).coerceIn(
                            minimumValue = minScale * MIN_SCALE_GESTURE_RESISTANCE,
                            maximumValue = MAX_ZOOM
                        )
                        val target = calculateTransformAfterZoom(
                            currentScale = imageScale,
                            currentTranslation = imageTranslation,
                            targetScale = targetScale,
                            centroid = centroid,
                            transformCenter = baseImageBounds.center
                        )
                        imageScale = targetScale
                        imageTranslation = if (coercePointsToImageArea) {
                            coerceImageTranslation(
                                translation = target.translation,
                                scale = targetScale,
                                imageBounds = baseImageBounds,
                                cropBounds = drawPoints.boundingRect(),
                                coerceToCrop = true
                            )
                        } else {
                            target.translation
                        }
                    },
                    onZoomEnd = {
                        if (!coercePointsToImageArea) {
                            return@detectOneFingerZoomGestures
                        }

                        transformAnimationJob = scope.launch {
                            val minScale = calculateMinimumScale(
                                cropBounds = drawPoints.boundingRect(),
                                imageBounds = baseImageBounds
                            )
                            val targetScale = imageScale.coerceIn(minScale, MAX_ZOOM)
                            val targetTranslation = coerceImageTranslation(
                                translation = imageTranslation,
                                scale = targetScale,
                                imageBounds = baseImageBounds,
                                cropBounds = drawPoints.boundingRect(),
                                coerceToCrop = coercePointsToImageArea
                            )
                            animateTransformTo(
                                startScale = imageScale,
                                startTranslation = imageTranslation,
                                targetScale = targetScale,
                                targetTranslation = targetTranslation,
                                onUpdate = ::updateImageTransform
                            )
                        }
                    }
                )
            }
            .pointerInput(imageKey, coercePointsToImageArea) {
                var transformGestureActive = false

                detectTransformGestures(
                    consume = true,
                    onGestureStart = {
                        transformGestureActive = false
                        transformAnimationJob?.cancel()
                    },
                    onGesture = { centroid, pan, zoom, _, _, _ ->
                        if (!transformGestureActive) {
                            transformGestureActive = true
                            transformAnimationJob?.cancel()
                        }
                        val minScale = if (coercePointsToImageArea) {
                            calculateMinimumScale(
                                cropBounds = drawPoints.boundingRect(),
                                imageBounds = baseImageBounds
                            )
                        } else {
                            MINIMUM_CROP_ZOOM
                        }
                        val targetScale = (imageScale * zoom).coerceIn(
                            minimumValue = minScale * MIN_SCALE_GESTURE_RESISTANCE,
                            maximumValue = MAX_ZOOM
                        )
                        val target = calculateTransformAfterZoom(
                            currentScale = imageScale,
                            currentTranslation = imageTranslation,
                            targetScale = targetScale,
                            centroid = centroid,
                            transformCenter = baseImageBounds.center
                        )
                        imageScale = targetScale
                        imageTranslation = target.translation + pan
                    },
                    onGestureEnd = {
                        if (!transformGestureActive || !coercePointsToImageArea) {
                            return@detectTransformGestures
                        }

                        transformAnimationJob = scope.launch {
                            val minScale = calculateMinimumScale(
                                cropBounds = drawPoints.boundingRect(),
                                imageBounds = baseImageBounds
                            )
                            val targetScale = imageScale.coerceIn(minScale, MAX_ZOOM)
                            val targetTranslation = coerceImageTranslation(
                                translation = imageTranslation,
                                scale = targetScale,
                                imageBounds = baseImageBounds,
                                cropBounds = drawPoints.boundingRect(),
                                coerceToCrop = coercePointsToImageArea
                            )
                            animateTransformTo(
                                startScale = imageScale,
                                startTranslation = imageTranslation,
                                targetScale = targetScale,
                                targetTranslation = targetTranslation,
                                onUpdate = ::updateImageTransform
                            )
                        }
                    }
                )
            },
        imageBitmap = imageBitmap,
        drawImage = false
    ) {
        val internalPaddingDp = 16.dp
        val internalPadding = with(density) { internalPaddingDp.toPx() }
        val containerWidthPx = with(density) { maxWidth.toPx() }
        val containerHeightPx = with(density) { maxHeight.toPx() }
        val horizontalPointInset = handleRadiusPx.coerceAtMost(containerWidthPx / 2f)
        val verticalPointInset = handleRadiusPx.coerceAtMost(containerHeightPx / 2f)
        val containerBounds = Rect(
            left = horizontalPointInset,
            top = verticalPointInset,
            right = containerWidthPx - horizontalPointInset,
            bottom = containerHeightPx - verticalPointInset
        )
        val selectedCornerIndex = (dragTarget as? DragTarget.Corner)?.index
        val selectedEdge = dragTarget as? DragTarget.Edge
        val cropPointsInitialized = drawPoints.size == CROP_POINTS_COUNT
        val pointScales = List(drawPoints.size) {
            animateFloatAsState(if (it == selectedCornerIndex) 1.4f else 1f)
        }
        val edgeScales = edgeIndices.map { (firstIndex, secondIndex) ->
            animateFloatAsState(
                if (selectedEdge?.firstIndex == firstIndex &&
                    selectedEdge.secondIndex == secondIndex
                ) {
                    1.4f
                } else {
                    1f
                }
            )
        }

        LaunchedEffect(croppingTrigger, baseImageBounds, cropPointsInitialized) {
            if (croppingTrigger && cropPointsInitialized && baseImageBounds.hasPositiveSize()) {
                fun scaledPointsFor(size: IntSize): List<Offset> {
                    val widthScale = size.width.toFloat() / baseImageBounds.width.coerceAtLeast(1f)
                    val heightScale =
                        size.height.toFloat() / baseImageBounds.height.coerceAtLeast(1f)

                    return drawPoints.map {
                        val untransformed = it.untransform(
                            center = baseImageBounds.center,
                            scale = imageScale,
                            translation = imageTranslation
                        )
                        Offset(
                            x = ((untransformed.x - baseImageBounds.left) * widthScale).roundToInt()
                                .coerceIn(0, size.width).toFloat(),
                            y = ((untransformed.y - baseImageBounds.top) * heightScale).roundToInt()
                                .coerceIn(0, size.height).toFloat()
                        )
                    }
                }

                onCropped(
                    FreeCrop.cropToCache(
                        context = context,
                        imageUri = sourceImageUri,
                        fallbackBitmap = bitmap,
                        fallbackPoints = scaledPointsFor(IntSize(bitmap.width, bitmap.height)),
                        sourcePoints = scaledPointsFor(
                            sourceImageSize.takeIf { it != IntSize.Zero }
                                ?: IntSize(bitmap.width, bitmap.height)
                        )
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
                    val position = it.positionInParent()
                    baseImageBounds = Rect(
                        offset = position,
                        size = it.size.toSize()
                    )
                }
                .graphicsLayer {
                    scaleX = imageScale
                    scaleY = imageScale
                    translationX = imageTranslation.x
                    translationY = imageTranslation.y
                },
            contentScale = ContentScale.FillBounds
        )

        LaunchedEffect(imageKey, baseImageBounds) {
            if (baseImageBounds.hasPositiveSize()) {
                transformAnimationJob?.cancel()
                transformAnimationJob = null
                val inset = internalPadding.coerceAtMost(
                    minOf(baseImageBounds.width, baseImageBounds.height) / 4f
                )
                drawPoints = baseImageBounds.insetBy(inset).corners()
                imageScale = 1f
                imageTranslation = Offset.Zero
                dragTarget = DragTarget.None
                magnifierCenter = Offset.Unspecified
            }
        }

        fun currentPointBounds(): Rect = containerBounds

        fun Offset.coerceToPointBounds(): Offset = currentPointBounds().let { bounds ->
            coerceIn(
                horizontalRange = bounds.left..bounds.right,
                verticalRange = bounds.top..bounds.bottom
            )
        }

        fun Offset.coerceDragAmountFor(points: List<Offset>): Offset {
            val bounds = currentPointBounds()
            val horizontalRange = bounds.left..bounds.right
            val verticalRange = bounds.top..bounds.bottom
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

        LaunchedEffect(coercePointsToImageArea, containerBounds, baseImageBounds) {
            transformAnimationJob?.cancel()
            if (drawPoints.size == CROP_POINTS_COUNT) {
                drawPoints = drawPoints.map {
                    it.coerceToPointBounds()
                }
            }
        }

        LaunchedEffect(drawPoints, coercePointsToImageArea, dragTarget) {
            transformAnimationJob?.cancel()
            if (!coercePointsToImageArea ||
                dragTarget != DragTarget.None ||
                drawPoints.size != CROP_POINTS_COUNT ||
                !baseImageBounds.hasPositiveSize()
            ) {
                return@LaunchedEffect
            }

            val targetScale = imageScale.coerceAtLeast(
                calculateMinimumScale(
                    cropBounds = drawPoints.boundingRect(),
                    imageBounds = baseImageBounds
                )
            )
            val targetTranslation = coerceImageTranslation(
                translation = imageTranslation,
                scale = targetScale,
                imageBounds = baseImageBounds,
                cropBounds = drawPoints.boundingRect(),
                coerceToCrop = true
            )

            if (targetScale != imageScale || targetTranslation != imageTranslation) {
                transformAnimationJob = scope.launch {
                    animateTransformTo(
                        startScale = imageScale,
                        startTranslation = imageTranslation,
                        targetScale = targetScale,
                        targetTranslation = targetTranslation,
                        onUpdate = ::updateImageTransform
                    )
                }
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
                    } else {
                        Modifier
                    }
                )
                .pointerInput(
                    contentPadding,
                    coercePointsToImageArea,
                    handleRadiusPx,
                    strictCornerTouchRadiusPx,
                    cornerTouchRadiusPx,
                    edgeTouchRadiusPx,
                    imageKey,
                    showMagnifier,
                    isOverlayDraggable,
                    containerBounds,
                    baseImageBounds
                ) {
                    detectCropDragGestures(
                        hitTest = { offset ->
                            findDragTarget(
                                points = drawPoints,
                                touchPosition = offset,
                                touchRadii = TouchRadii(
                                    strictCorner = strictCornerTouchRadiusPx,
                                    corner = cornerTouchRadiusPx,
                                    edge = edgeTouchRadiusPx
                                ),
                                isOverlayDraggable = isOverlayDraggable
                            )
                        },
                        onDragStart = { target ->
                            dragTarget = target
                            magnifierCenter =
                                if (showMagnifier && dragTarget != DragTarget.None) {
                                    dragTarget.focusPoint(drawPoints)
                                } else {
                                    Offset.Unspecified
                                }
                        },
                        onDrag = { dragAmount ->
                            val selectedIndices = dragTarget.pointIndices
                            val selectedPoints = selectedIndices.mapNotNull { index ->
                                drawPoints.getOrNull(index)
                            }
                            val coercedDragAmount = dragAmount.coerceDragAmountFor(selectedPoints)

                            if (selectedIndices.isNotEmpty()) {
                                drawPoints = drawPoints
                                    .toMutableList()
                                    .apply {
                                        selectedIndices.forEach { index ->
                                            this[index] = this[index]
                                                .plus(coercedDragAmount)
                                                .coerceToPointBounds()
                                        }
                                    }

                                if (showMagnifier) {
                                    magnifierCenter = dragTarget.focusPoint(drawPoints)
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
            if (drawPoints.size != CROP_POINTS_COUNT) return@Canvas

            val (x, y) = drawPoints[0]
            val (x1, y1) = drawPoints[1]
            val (x2, y2) = drawPoints[2]
            val (x3, y3) = drawPoints[3]

            val framePath = Path().apply {
                moveTo(x, y)
                lineTo(x1, y1)
                lineTo(x2, y2)
                lineTo(x3, y3)
                close()
            }

            drawRect(overlayColor)

            drawPath(
                path = framePath,
                brush = SolidColor(Color.Transparent),
                blendMode = BlendMode.Clear
            )

            clipPath(framePath) {
                drawPerspectiveGrid(
                    points = drawPoints,
                    color = gridColor,
                    strokeWidth = frameStrokeWidthPx
                )
            }

            drawPath(
                path = framePath,
                brush = SolidColor(gridColor),
                style = Stroke(frameStrokeWidthPx)
            )

            edgeIndices.forEachIndexed { index, (firstIndex, secondIndex) ->
                val firstPoint = drawPoints[firstIndex]
                val secondPoint = drawPoints[secondIndex]
                val edgeVector = secondPoint - firstPoint
                val edgeLength = edgeVector.getDistance()
                if (edgeLength > 0f) {
                    val center = (firstPoint + secondPoint) / 2f
                    val halfHandleLength = minOf(
                        handleRadiusPx * MIDDLE_HANDLE_LENGTH_MULTIPLIER *
                                edgeScales[index].value,
                        edgeLength / 2f
                    )
                    val handleVector = edgeVector / edgeLength * halfHandleLength

                    drawLine(
                        color = handlesColor,
                        start = center - handleVector,
                        end = center + handleVector,
                        strokeWidth = middleHandleStrokeWidthPx * edgeScales[index].value,
                        cap = StrokeCap.Round
                    )
                }
            }

            drawPoints.forEachIndexed { index, point ->
                val scale = pointScales[index].value

                drawCircle(
                    color = handlesColor,
                    center = point,
                    radius = handleRadiusPx * scale
                )
            }
        }
    }
}

private sealed class DragTarget {
    object None : DragTarget()
    object Overlay : DragTarget()
    data class Corner(val index: Int) : DragTarget()
    data class Edge(
        val firstIndex: Int,
        val secondIndex: Int
    ) : DragTarget()

    val pointIndices: List<Int>
        get() = when (this) {
            is Corner -> listOf(index)
            is Edge -> listOf(firstIndex, secondIndex)
            Overlay -> pointsIndices
            None -> emptyList()
        }

    private companion object {
        val pointsIndices = listOf(0, 1, 2, 3)
    }
}

private suspend fun PointerInputScope.detectCropDragGestures(
    hitTest: (Offset) -> DragTarget,
    onDragStart: (DragTarget) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    var lastTapUpTime: Long? = null
    var lastTapPosition = Offset.Unspecified

    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val timeSinceLastTap = lastTapUpTime?.let { down.uptimeMillis - it }
        val isSecondTap = lastTapPosition.isSpecified &&
                timeSinceLastTap != null &&
                timeSinceLastTap >= viewConfiguration.doubleTapMinTimeMillis &&
                timeSinceLastTap <= viewConfiguration.doubleTapTimeoutMillis &&
                (down.position - lastTapPosition).getDistance() <=
                viewConfiguration.touchSlop * DOUBLE_TAP_POSITION_TOLERANCE_MULTIPLIER
        val candidate = if (isSecondTap) DragTarget.None else hitTest(down.position)

        var accumulatedDrag = Offset.Zero
        var dragStarted = false
        var isCanceled = false
        var isMultiTouch = false
        var upTime = down.uptimeMillis
        var upPosition = down.position

        do {
            val event = awaitPointerEvent(PointerEventPass.Main)
            if (event.changes.size > 1) {
                isMultiTouch = true
                isCanceled = dragStarted
            }

            val change = event.changes.firstOrNull { it.id == down.id }
            if (change == null) {
                isCanceled = dragStarted
                break
            }

            upTime = change.uptimeMillis
            upPosition = change.position

            if (change.isConsumed) {
                isCanceled = dragStarted
                break
            }

            val dragAmount = change.positionChange()
            if (!isMultiTouch && candidate != DragTarget.None && dragAmount != Offset.Zero) {
                accumulatedDrag += dragAmount
                if (dragStarted || accumulatedDrag.getDistance() > viewConfiguration.touchSlop) {
                    val appliedDrag = if (dragStarted) dragAmount else accumulatedDrag
                    if (!dragStarted) {
                        dragStarted = true
                        onDragStart(candidate)
                    }
                    change.consume()
                    onDrag(appliedDrag)
                    accumulatedDrag = Offset.Zero
                }
            }
        } while (change.pressed)

        if (dragStarted) {
            if (isCanceled) onDragCancel() else onDragEnd()
            lastTapUpTime = null
            lastTapPosition = Offset.Unspecified
        } else {
            if (isSecondTap || isMultiTouch || upTime - down.uptimeMillis >
                viewConfiguration.longPressTimeoutMillis
            ) {
                lastTapUpTime = null
                lastTapPosition = Offset.Unspecified
            } else {
                lastTapUpTime = upTime
                lastTapPosition = upPosition
            }
        }
    }
}

private data class FreeCornersCropperImageKey(
    val bitmap: Bitmap,
    val generationId: Int
)

private data class ImageTransform(
    val translation: Offset
)

private suspend fun PointerInputScope.detectOneFingerZoomGestures(
    onDoubleTap: (Offset) -> Unit,
    onZoomStart: () -> Unit,
    onZoom: (centroid: Offset, zoom: Float) -> Unit,
    onZoomEnd: () -> Unit
) = awaitEachGesture {
    val firstDown = awaitFirstDown(requireUnconsumed = false)
    var firstUp = firstDown
    var hasMoved = false
    var isMultiTouch = false
    var isCanceled = false

    do {
        val event = awaitPointerEvent()
        if (event.changes.fastAny { it.isConsumed }) {
            isCanceled = true
            break
        }
        if (event.changes.fastAny { it.positionChanged() }) {
            hasMoved = true
        }
        if (event.changes.size > 1) {
            isMultiTouch = true
        }
        firstUp = event.changes.first()
    } while (event.changes.fastAny { it.pressed })

    val isTap = !hasMoved &&
            !isMultiTouch &&
            !isCanceled &&
            firstUp.uptimeMillis - firstDown.uptimeMillis <=
            viewConfiguration.longPressTimeoutMillis

    if (isTap) {
        val secondDown = awaitSecondDown(firstUp)
        if (secondDown != null) {
            var secondUp: PointerInputChange = secondDown
            var isZooming = false
            var isSecondMultiTouch = false
            var isSecondCanceled = false

            do {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                if (event.changes.fastAny { it.isConsumed }) {
                    isSecondCanceled = true
                    break
                }

                if (event.changes.size > 1) {
                    isSecondMultiTouch = true
                }

                val panChange = event.calculatePan()
                if (!isSecondMultiTouch && panChange != Offset.Zero) {
                    if (!isZooming) {
                        onZoomStart()
                    }
                    isZooming = true
                    val zoomChange = exp(panChange.y * ONE_FINGER_ZOOM_SENSITIVITY)
                    if (zoomChange != 1f) {
                        onZoom(
                            event.calculateCentroid(useCurrent = true),
                            zoomChange
                        )
                    }
                    event.changes.fastForEach {
                        if (it.positionChanged()) {
                            it.consume()
                        }
                    }
                }
                secondUp = event.changes.first()
            } while (event.changes.fastAny { it.pressed })

            if (isZooming && !isSecondMultiTouch && !isSecondCanceled) {
                onZoomEnd()
            } else if (!isSecondMultiTouch &&
                !isSecondCanceled &&
                secondUp.uptimeMillis - secondDown.uptimeMillis <=
                viewConfiguration.longPressTimeoutMillis
            ) {
                onDoubleTap(secondUp.position)
            }
        }
    }
}

private suspend fun AwaitPointerEventScope.awaitSecondDown(
    firstUp: PointerInputChange
): PointerInputChange? = withTimeoutOrNull(viewConfiguration.doubleTapTimeoutMillis) {
    val minUptime = firstUp.uptimeMillis + viewConfiguration.doubleTapMinTimeMillis
    var change: PointerInputChange
    do {
        change = awaitFirstDown()
    } while (change.uptimeMillis < minUptime)
    change
}

private fun calculateMinimumScale(
    cropBounds: Rect,
    imageBounds: Rect
): Float {
    if (cropBounds == Rect.Zero || !imageBounds.hasPositiveSize()) return 1f

    return maxOf(
        cropBounds.width / imageBounds.width,
        cropBounds.height / imageBounds.height
    ).coerceIn(MINIMUM_CROP_ZOOM, MAX_ZOOM)
}

private fun calculateTransformAfterZoom(
    currentScale: Float,
    currentTranslation: Offset,
    targetScale: Float,
    centroid: Offset,
    transformCenter: Offset
): ImageTransform {
    if (currentScale <= 0f || !centroid.isSpecified || !transformCenter.isSpecified) {
        return ImageTransform(currentTranslation)
    }

    val scaleChange = targetScale / currentScale
    return ImageTransform(
        translation = currentTranslation +
                (centroid - transformCenter - currentTranslation) * (1f - scaleChange)
    )
}

private fun coerceImageTranslation(
    translation: Offset,
    scale: Float,
    imageBounds: Rect,
    cropBounds: Rect,
    coerceToCrop: Boolean
): Offset {
    if (!imageBounds.hasPositiveSize()) return Offset.Zero

    val scaledImageBounds = imageBounds.transform(
        center = imageBounds.center,
        scale = scale,
        translation = Offset.Zero
    )

    val horizontalRange: ClosedFloatingPointRange<Float>
    val verticalRange: ClosedFloatingPointRange<Float>

    if (coerceToCrop && cropBounds != Rect.Zero) {
        val minTranslationX = cropBounds.right - scaledImageBounds.right
        val maxTranslationX = cropBounds.left - scaledImageBounds.left
        val minTranslationY = cropBounds.bottom - scaledImageBounds.bottom
        val maxTranslationY = cropBounds.top - scaledImageBounds.top
        horizontalRange = minTranslationX..maxTranslationX
        verticalRange = minTranslationY..maxTranslationY
    } else {
        val horizontalPan = ((scaledImageBounds.width - imageBounds.width) / 2f)
            .coerceAtLeast(0f)
        val verticalPan = ((scaledImageBounds.height - imageBounds.height) / 2f)
            .coerceAtLeast(0f)
        horizontalRange = -horizontalPan..horizontalPan
        verticalRange = -verticalPan..verticalPan
    }

    return Offset(
        x = translation.x.coerceInOrCenter(horizontalRange),
        y = translation.y.coerceInOrCenter(verticalRange)
    )
}

private suspend fun animateTransformTo(
    startScale: Float,
    startTranslation: Offset,
    targetScale: Float,
    targetTranslation: Offset,
    onUpdate: (scale: Float, translation: Offset) -> Unit
) {
    animate(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = spring()
    ) { progress, _ ->
        onUpdate(
            startScale + (targetScale - startScale) * progress,
            startTranslation + (targetTranslation - startTranslation) * progress
        )
    }
}

private fun findDragTarget(
    points: List<Offset>,
    touchPosition: Offset,
    touchRadii: TouchRadii,
    isOverlayDraggable: Boolean
): DragTarget {
    val touchedStrictCornerIndex = findTouchedCornerIndex(
        points = points,
        touchPosition = touchPosition,
        distanceThresholdSquared = touchRadii.strictCorner.square()
    )

    if (touchedStrictCornerIndex != null) return DragTarget.Corner(touchedStrictCornerIndex)

    val touchedEdge = edgeIndices
        .mapNotNull { (firstIndex, secondIndex) ->
            val firstPoint = points.getOrNull(firstIndex) ?: return@mapNotNull null
            val secondPoint = points.getOrNull(secondIndex) ?: return@mapNotNull null
            val edgeTouch = touchPosition.edgeTouch(firstPoint, secondPoint)

            if (edgeTouch.projection in EdgeTouchProjectionRange &&
                edgeTouch.distanceSquared < touchRadii.edge.square()
            ) {
                DragTarget.Edge(firstIndex, secondIndex) to edgeTouch.distanceSquared
            } else {
                null
            }
        }
        .minByOrNull { it.second }
        ?.first

    if (touchedEdge != null) return touchedEdge

    val touchedCornerIndex = findTouchedCornerIndex(
        points = points,
        touchPosition = touchPosition,
        distanceThresholdSquared = touchRadii.corner.square()
    )

    if (touchedCornerIndex != null) return DragTarget.Corner(touchedCornerIndex)

    return if (isOverlayDraggable && touchPosition.isInsidePolygon(points)) {
        DragTarget.Overlay
    } else {
        DragTarget.None
    }
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

        DragTarget.Overlay ->
            points
                .takeIf { it.isNotEmpty() }
                ?.reduce(Offset::plus)
                ?.div(points.size.toFloat())
                ?: Offset.Unspecified

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
        } else {
            null
        }
    }
    .minByOrNull { it.second }
    ?.first

private fun Float.square(): Float = this * this

private data class EdgeTouch(
    val distanceSquared: Float,
    val projection: Float
)

private data class TouchRadii(
    val strictCorner: Float,
    val corner: Float,
    val edge: Float
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

private fun Offset.isInsidePolygon(points: List<Offset>): Boolean {
    if (points.size < 3) return false

    var isInside = false
    var previous = points.last()
    points.forEach { current ->
        if ((current.y > y) != (previous.y > y)) {
            val intersectionX = (previous.x - current.x) * (y - current.y) /
                    (previous.y - current.y) + current.x
            if (x < intersectionX) isInside = !isInside
        }
        previous = current
    }
    return isInside
}

private fun List<Offset>.boundingRect(): Rect {
    if (isEmpty()) return Rect.Zero

    return Rect(
        left = minOf { it.x },
        top = minOf { it.y },
        right = maxOf { it.x },
        bottom = maxOf { it.y }
    )
}

private fun Rect.hasPositiveSize(): Boolean = width > 0f && height > 0f

private fun Rect.insetBy(value: Float): Rect = Rect(
    left = left + value,
    top = top + value,
    right = right - value,
    bottom = bottom - value
)

private fun Rect.corners(): List<Offset> = listOf(
    topLeft,
    topRight,
    bottomRight,
    bottomLeft
)

private fun Rect.transform(
    center: Offset,
    scale: Float,
    translation: Offset
): Rect = Rect(
    topLeft = topLeft.transform(center, scale, translation),
    bottomRight = bottomRight.transform(center, scale, translation)
)

private fun Offset.transform(
    center: Offset,
    scale: Float,
    translation: Offset
): Offset = center + (this - center) * scale + translation

private fun Offset.untransform(
    center: Offset,
    scale: Float,
    translation: Offset
): Offset = center + (this - center - translation) / scale

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

private const val STRICT_CORNER_TOUCH_RADIUS_MULTIPLIER = 1.9f
private const val CORNER_TOUCH_RADIUS_MULTIPLIER = 3.2f
private const val EDGE_TOUCH_RADIUS_MULTIPLIER = 2.5f
private const val MIDDLE_HANDLE_LENGTH_MULTIPLIER = 1.5f
private val MiddleHandleStrokeWidth = 3.5.dp
private const val DOUBLE_TAP_POSITION_TOLERANCE_MULTIPLIER = 4f
private const val MINIMUM_CROP_ZOOM = 0.05f
private const val CROP_POINTS_COUNT = 4
private const val MAX_ZOOM = 20f
private const val DEFAULT_DOUBLE_TAP_SCALE = 3f
private const val MIN_SCALE_GESTURE_RESISTANCE = 0.85f
private const val SCALE_COMPARISON_TOLERANCE = 0.01f
private const val ONE_FINGER_ZOOM_SENSITIVITY = 0.004f

private fun Offset.coerceIn(
    horizontalRange: ClosedRange<Float>,
    verticalRange: ClosedRange<Float>
) = Offset(this.x.coerceIn(horizontalRange), this.y.coerceIn(verticalRange))

private fun Float.coerceInOrCenter(range: ClosedFloatingPointRange<Float>): Float {
    return if (range.start <= range.endInclusive) {
        coerceIn(range)
    } else {
        (range.start + range.endInclusive) / 2f
    }
}
