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

@file:Suppress("SameParameterValue")

package com.t8rin.curves

import android.app.Activity
import android.graphics.Bitmap
import android.view.MotionEvent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.TouchBoundsExpansion
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.t8rin.curves.utils.safeAspectRatio
import com.t8rin.curves.view.PhotoFilterCurvesControl
import com.t8rin.histogram.Histogram
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import jp.co.cyberagent.android.gpuimage.GLTextureView
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

enum class ImageCurvesEditorLayout {
    Overlay,
    Separate
}

@Composable
fun ImageCurvesEditor(
    bitmap: Bitmap?,
    state: ImageCurvesEditorState = remember {
        ImageCurvesEditorState.Default
    },
    onStateChange: (ImageCurvesEditorState) -> Unit,
    imageObtainingTrigger: Boolean,
    onImageObtained: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    curvesSelectionText: @Composable (curveType: Int) -> Unit = {
        Text(
            text = when (it) {
                0 -> stringResource(R.string.all)
                1 -> stringResource(R.string.color_red)
                2 -> stringResource(R.string.color_green)
                3 -> stringResource(R.string.color_blue)
                else -> ""
            },
            style = MaterialTheme.typography.labelMedium
        )
    },
    colors: ImageCurvesEditorColors = ImageCurvesEditorDefaults.Colors,
    drawNotActiveCurves: Boolean = true,
    placeControlsAtTheEnd: Boolean = false,
    showOriginal: Boolean = false,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    disallowInterceptTouchEvents: Boolean = true,
    layout: ImageCurvesEditorLayout = ImageCurvesEditorLayout.Overlay,
    showHistogram: Boolean = true,
    showImagePreview: Boolean = true,
    showAsRow: Boolean = false,
    editorHeight: Dp = 220.dp
) {
    val context = LocalContext.current as Activity

    AnimatedContent(
        modifier = containerModifier,
        targetState = bitmap,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { image ->
        if (image != null) {
            val gpuImage = remember(context, image) {
                GPUImage(context).apply {
                    setImage(image)
                    setFilter(state.buildFilter())
                }
            }
            var activeCurveType by remember(state) {
                mutableIntStateOf(state.curvesToolValue.activeType)
            }
            var curvesView by remember(image, layout) {
                mutableStateOf<PhotoFilterCurvesControl?>(null)
            }
            var canDeleteSelectedPoint by remember(image, layout) {
                mutableStateOf(false)
            }

            LaunchedEffect(showOriginal, state) {
                gpuImage.setFilter(
                    if (showOriginal) GPUImageContrastFilter(1f)
                    else state.buildFilter()
                )
            }

            LaunchedEffect(imageObtainingTrigger, gpuImage) {
                if (imageObtainingTrigger) {
                    onImageObtained(gpuImage.bitmapWithFilterApplied)
                }
            }

            val applyCurveChange = {
                val snapshot = state.snapshot()
                onStateChange(snapshot)
                gpuImage.setFilter(snapshot.buildFilter())
            }
            val onCurveTypeChange: (Int) -> Unit = { type ->
                state.curvesToolValue.activeType = type
                activeCurveType = type
                curvesView?.setActiveCurveType(type)
            }
            val onDeleteSelectedPoint = {
                curvesView?.deleteSelectedPoint()
                Unit
            }

            val editorContent: @Composable (Bitmap?, () -> Unit) -> Unit =
                { histogramBitmap, onCurveChanged ->
                    when (layout) {
                        ImageCurvesEditorLayout.Overlay -> {
                            OverlayEditor(
                                bitmap = image,
                                histogramBitmap = histogramBitmap,
                                gpuImage = gpuImage,
                                state = state,
                                activeCurveType = activeCurveType,
                                canDeleteSelectedPoint = canDeleteSelectedPoint,
                                onCurveChanged = onCurveChanged,
                                onCurveTypeChange = onCurveTypeChange,
                                onDeleteSelectedPoint = onDeleteSelectedPoint,
                                onCurvesViewChange = { curvesView = it },
                                onDeleteAvailabilityChange = { canDeleteSelectedPoint = it },
                                modifier = modifier,
                                contentPadding = contentPadding,
                                curvesSelectionText = curvesSelectionText,
                                colors = colors,
                                drawNotActiveCurves = drawNotActiveCurves,
                                placeControlsAtTheEnd = placeControlsAtTheEnd,
                                shape = shape,
                                disallowInterceptTouchEvents = disallowInterceptTouchEvents,
                                histogramAlpha = 1f
                            )
                        }

                        ImageCurvesEditorLayout.Separate -> {
                            SeparateEditor(
                                bitmap = image,
                                histogramBitmap = histogramBitmap,
                                gpuImage = gpuImage,
                                state = state,
                                activeCurveType = activeCurveType,
                                canDeleteSelectedPoint = canDeleteSelectedPoint,
                                onCurveChanged = onCurveChanged,
                                onCurveTypeChange = onCurveTypeChange,
                                onDeleteSelectedPoint = onDeleteSelectedPoint,
                                onCurvesViewChange = { curvesView = it },
                                onDeleteAvailabilityChange = { canDeleteSelectedPoint = it },
                                modifier = modifier,
                                contentPadding = contentPadding,
                                curvesSelectionText = curvesSelectionText,
                                colors = colors,
                                drawNotActiveCurves = drawNotActiveCurves,
                                showAsRow = showAsRow,
                                shape = shape,
                                disallowInterceptTouchEvents = disallowInterceptTouchEvents,
                                histogramAlpha = 0.5f,
                                showImagePreview = showImagePreview,
                                editorHeight = editorHeight
                            )
                        }
                    }
                }

            if (showHistogram) {
                var histogramBitmap by remember(image) {
                    mutableStateOf(image)
                }
                var histogramRevision by remember(image) {
                    mutableIntStateOf(0)
                }

                LaunchedEffect(histogramRevision, showOriginal, state) {
                    delay(90)
                    histogramBitmap = if (showOriginal) {
                        image
                    } else {
                        val filter = state.snapshot().buildFilter()
                        withContext(Dispatchers.Default) {
                            GPUImage(context).apply {
                                setImage(image)
                                setFilter(filter)
                            }.bitmapWithFilterApplied
                        }.also {
                            coroutineContext.ensureActive()
                        }
                    }
                }

                editorContent(histogramBitmap) {
                    applyCurveChange()
                    histogramRevision++
                }
            } else {
                editorContent(null, applyCurveChange)
            }
        }
    }
}

@Composable
private fun OverlayEditor(
    bitmap: Bitmap,
    histogramBitmap: Bitmap?,
    gpuImage: GPUImage,
    state: ImageCurvesEditorState,
    activeCurveType: Int,
    canDeleteSelectedPoint: Boolean,
    onCurveChanged: () -> Unit,
    onCurveTypeChange: (Int) -> Unit,
    onDeleteSelectedPoint: () -> Unit,
    onCurvesViewChange: (PhotoFilterCurvesControl) -> Unit,
    onDeleteAvailabilityChange: (Boolean) -> Unit,
    modifier: Modifier,
    contentPadding: PaddingValues,
    curvesSelectionText: @Composable (Int) -> Unit,
    colors: ImageCurvesEditorColors,
    drawNotActiveCurves: Boolean,
    placeControlsAtTheEnd: Boolean,
    shape: Shape,
    disallowInterceptTouchEvents: Boolean,
    histogramAlpha: Float
) {
    var controlsPadding by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        CurveEditorPane(
            bitmap = histogramBitmap,
            state = state,
            activeCurveType = activeCurveType,
            onCurveChanged = onCurveChanged,
            onCurvesViewChange = onCurvesViewChange,
            onDeleteAvailabilityChange = onDeleteAvailabilityChange,
            colors = colors,
            drawNotActiveCurves = drawNotActiveCurves,
            disallowInterceptTouchEvents = disallowInterceptTouchEvents,
            histogramAlpha = histogramAlpha,
            modifier = Modifier
                .padding(contentPadding)
                .then(
                    if (placeControlsAtTheEnd) {
                        Modifier.padding(end = controlsPadding + 12.dp)
                    } else {
                        Modifier.padding(bottom = controlsPadding)
                    }
                )
                .aspectRatio(bitmap.safeAspectRatio),
            shape = shape,
            imageContent = {
                GPUImagePreview(
                    gpuImage = gpuImage,
                    modifier = Modifier.matchParentSize()
                )
            }
        )

        CurvesControls(
            activeCurveType = activeCurveType,
            canDeleteSelectedPoint = canDeleteSelectedPoint,
            onCurveTypeChange = onCurveTypeChange,
            onDeleteSelectedPoint = onDeleteSelectedPoint,
            colors = colors,
            curvesSelectionText = curvesSelectionText,
            vertical = placeControlsAtTheEnd,
            modifier = Modifier
                .align(
                    if (placeControlsAtTheEnd) Alignment.CenterEnd
                    else Alignment.BottomCenter
                )
                .then(
                    if (placeControlsAtTheEnd) {
                        Modifier.padding(
                            top = contentPadding.calculateTopPadding(),
                            bottom = contentPadding.calculateBottomPadding(),
                            end = contentPadding.calculateEndPadding(direction)
                        )
                    } else {
                        Modifier.padding(
                            bottom = contentPadding.calculateBottomPadding(),
                            start = contentPadding.calculateStartPadding(direction),
                            end = contentPadding.calculateEndPadding(direction)
                        )
                    }
                )
                .onGloballyPositioned {
                    controlsPadding = with(density) {
                        if (placeControlsAtTheEnd) it.size.width.toDp()
                        else it.size.height.toDp()
                    }
                }
        )
    }
}

@Composable
private fun SeparateEditor(
    bitmap: Bitmap,
    histogramBitmap: Bitmap?,
    gpuImage: GPUImage,
    state: ImageCurvesEditorState,
    activeCurveType: Int,
    canDeleteSelectedPoint: Boolean,
    onCurveChanged: () -> Unit,
    onCurveTypeChange: (Int) -> Unit,
    onDeleteSelectedPoint: () -> Unit,
    onCurvesViewChange: (PhotoFilterCurvesControl) -> Unit,
    onDeleteAvailabilityChange: (Boolean) -> Unit,
    modifier: Modifier,
    contentPadding: PaddingValues,
    curvesSelectionText: @Composable (Int) -> Unit,
    colors: ImageCurvesEditorColors,
    drawNotActiveCurves: Boolean,
    showAsRow: Boolean,
    shape: Shape,
    disallowInterceptTouchEvents: Boolean,
    histogramAlpha: Float,
    showImagePreview: Boolean,
    editorHeight: Dp
) {
    if (showAsRow) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(contentPadding)
        ) {
            if (showImagePreview) {
                GPUImagePreview(
                    gpuImage = gpuImage,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(bitmap.safeAspectRatio, true)
                        .clip(shape)
                )
            }
            CurveEditorPane(
                bitmap = histogramBitmap,
                state = state,
                activeCurveType = activeCurveType,
                onCurveChanged = onCurveChanged,
                onCurvesViewChange = onCurvesViewChange,
                onDeleteAvailabilityChange = onDeleteAvailabilityChange,
                colors = colors,
                drawNotActiveCurves = drawNotActiveCurves,
                disallowInterceptTouchEvents = disallowInterceptTouchEvents,
                histogramAlpha = histogramAlpha,
                modifier = Modifier
                    .weight(1f)
                    .height(editorHeight)
                    .background(MaterialTheme.colorScheme.surfaceContainer, shape),
                shape = shape
            )
            CurvesControls(
                activeCurveType = activeCurveType,
                canDeleteSelectedPoint = canDeleteSelectedPoint,
                onCurveTypeChange = onCurveTypeChange,
                onDeleteSelectedPoint = onDeleteSelectedPoint,
                colors = colors,
                curvesSelectionText = curvesSelectionText,
                vertical = true,
                modifier = Modifier
            )
        }
    } else {
        val direction = LocalLayoutDirection.current
        val horizontalPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(direction),
            end = contentPadding.calculateEndPadding(direction)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
        ) {
            if (showImagePreview) {
                GPUImagePreview(
                    gpuImage = gpuImage,
                    modifier = Modifier
                        .padding(
                            top = contentPadding.calculateTopPadding(),
                            start = contentPadding.calculateStartPadding(direction),
                            end = contentPadding.calculateEndPadding(direction)
                        )
                        .fillMaxWidth()
                        .weight(1f, false)
                        .aspectRatio(bitmap.safeAspectRatio, true)
                        .clip(shape)
                )
            }
            CurveEditorPane(
                bitmap = histogramBitmap,
                state = state,
                activeCurveType = activeCurveType,
                onCurveChanged = onCurveChanged,
                onCurvesViewChange = onCurvesViewChange,
                onDeleteAvailabilityChange = onDeleteAvailabilityChange,
                colors = colors,
                drawNotActiveCurves = drawNotActiveCurves,
                disallowInterceptTouchEvents = disallowInterceptTouchEvents,
                histogramAlpha = histogramAlpha,
                modifier = Modifier
                    .padding(
                        top = if (showImagePreview) 0.dp else {
                            contentPadding.calculateTopPadding()
                        },
                        start = horizontalPadding.calculateStartPadding(direction),
                        end = horizontalPadding.calculateEndPadding(direction)
                    )
                    .fillMaxWidth()
                    .height(editorHeight)
                    .background(MaterialTheme.colorScheme.surfaceContainer, shape),
                shape = shape
            )
            CurvesControls(
                activeCurveType = activeCurveType,
                canDeleteSelectedPoint = canDeleteSelectedPoint,
                onCurveTypeChange = onCurveTypeChange,
                onDeleteSelectedPoint = onDeleteSelectedPoint,
                colors = colors,
                curvesSelectionText = curvesSelectionText,
                vertical = false,
                modifier = Modifier.padding(
                    start = contentPadding.calculateStartPadding(direction),
                    end = contentPadding.calculateEndPadding(direction),
                    bottom = contentPadding.calculateBottomPadding()
                )
            )
        }
    }
}

@Composable
private fun CurveEditorPane(
    bitmap: Bitmap?,
    state: ImageCurvesEditorState,
    activeCurveType: Int,
    onCurveChanged: () -> Unit,
    onCurvesViewChange: (PhotoFilterCurvesControl) -> Unit,
    onDeleteAvailabilityChange: (Boolean) -> Unit,
    colors: ImageCurvesEditorColors,
    drawNotActiveCurves: Boolean,
    disallowInterceptTouchEvents: Boolean,
    histogramAlpha: Float,
    modifier: Modifier,
    shape: Shape,
    imageContent: (@Composable () -> Unit)? = null
) {
    val graphView = remember {
        mutableStateOf<PhotoFilterCurvesControl?>(null)
    }
    val pointsView = remember {
        mutableStateOf<PhotoFilterCurvesControl?>(null)
    }
    val pointOverflow = with(LocalDensity.current) {
        CurvePointTouchExpansion.roundToPx()
    }

    Box(
        modifier = modifier.expandedCurvePointTouchTarget(
            expansion = pointOverflow,
            coordinateOffset = pointOverflow,
            viewProvider = { pointsView.value }
        )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer(
                    shape = shape,
                    clip = true
                )
        ) {
            imageContent?.invoke()
            if (bitmap != null) {
                CurveChannelHistogram(
                    bitmap = bitmap,
                    curveType = activeCurveType,
                    colors = colors,
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer(alpha = histogramAlpha.coerceIn(0f, 1f))
                )
            }

            AndroidView(
                modifier = Modifier.matchParentSize(),
                factory = { context ->
                    PhotoFilterCurvesControl(
                        context = context,
                        value = state.curvesToolValue
                    ).apply {
                        graphView.value = this
                        isEnabled = false
                        setDrawControlPoints(false)
                    }
                },
                update = { view ->
                    graphView.value = view
                    view.updateValue(state.curvesToolValue)
                    view.setActiveCurveType(activeCurveType)
                    view.setDrawNotActiveCurves(drawNotActiveCurves)
                    view.setDrawEditorContent(true)
                    view.setDrawControlPoints(false)
                    view.setColors(
                        lumaCurveColor = colors.lumaCurveColor.toArgb(),
                        redCurveColor = colors.redCurveColor.toArgb(),
                        greenCurveColor = colors.greenCurveColor.toArgb(),
                        blueCurveColor = colors.blueCurveColor.toArgb(),
                        defaultCurveColor = colors.defaultCurveColor.toArgb(),
                        guidelinesColor = colors.guidelinesColor.toArgb(),
                        editorBackgroundColor = if (imageContent == null) {
                            Color.Transparent
                        } else {
                            colors.editorBackgroundColor
                        }.toArgb()
                    )
                    view.setActualAreaInset(0f)
                }
            )
        }

        AndroidView(
            modifier = Modifier
                .matchParentSize()
                .expandBeyondBounds(pointOverflow),
            factory = { context ->
                PhotoFilterCurvesControl(
                    context = context,
                    value = state.curvesToolValue
                ).apply {
                    pointsView.value = this
                    isEnabled = false
                    onCurvesViewChange(this)
                    setDrawEditorContent(false)
                    setDrawControlPoints(true)
                    setDelegate {
                        graphView.value?.invalidate()
                        onCurveChanged()
                    }
                    setSelectionDelegate(onDeleteAvailabilityChange)
                }
            },
            update = { view ->
                pointsView.value = view
                onCurvesViewChange(view)
                view.updateValue(state.curvesToolValue)
                view.setActiveCurveType(activeCurveType)
                view.setDelegate {
                    graphView.value?.invalidate()
                    onCurveChanged()
                }
                view.setSelectionDelegate(onDeleteAvailabilityChange)
                view.setDrawEditorContent(false)
                view.setDrawControlPoints(true)
                view.setDisallowInterceptTouchEvents(disallowInterceptTouchEvents)
                view.setColors(
                    lumaCurveColor = colors.lumaCurveColor.toArgb(),
                    redCurveColor = colors.redCurveColor.toArgb(),
                    greenCurveColor = colors.greenCurveColor.toArgb(),
                    blueCurveColor = colors.blueCurveColor.toArgb(),
                    defaultCurveColor = colors.defaultCurveColor.toArgb(),
                    guidelinesColor = colors.guidelinesColor.toArgb(),
                    editorBackgroundColor = Color.Transparent.toArgb()
                )
                view.setActualAreaInset(pointOverflow.toFloat())
            }
        )
    }
}

@Composable
private fun CurveChannelHistogram(
    bitmap: Bitmap,
    curveType: Int,
    colors: ImageCurvesEditorColors,
    modifier: Modifier
) {
    var histogram by remember {
        mutableStateOf<Histogram?>(null)
    }

    LaunchedEffect(bitmap) {
        val updatedHistogram = withContext(Dispatchers.Default) {
            Histogram.from(bitmap)
        }
        coroutineContext.ensureActive()
        histogram = updatedHistogram
    }

    val targetValues = remember(histogram, curveType) {
        histogram?.let { data ->
            when (curveType) {
                PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeRed -> data.redData
                PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeGreen -> data.greenData
                PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeBlue -> data.blueData
                else -> data.brightnessData
            }.normalizedHistogramValues()
        } ?: FloatArray(0)
    }
    var startValues by remember {
        mutableStateOf(FloatArray(0))
    }
    var endValues by remember {
        mutableStateOf(FloatArray(0))
    }
    val transitionProgress = remember {
        Animatable(1f)
    }

    LaunchedEffect(targetValues) {
        if (targetValues.isEmpty()) return@LaunchedEffect

        val currentValues = interpolatedHistogramValues(
            start = startValues,
            end = endValues,
            fraction = transitionProgress.value,
            targetSize = targetValues.size
        )
        startValues = currentValues
        endValues = currentValues
        transitionProgress.snapTo(0f)
        endValues = targetValues
        transitionProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 240,
                easing = FastOutSlowInEasing
            )
        )
    }

    val targetColor = when (curveType) {
        PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeRed -> colors.redCurveColor
        PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeGreen -> colors.greenCurveColor
        PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeBlue -> colors.blueCurveColor
        else -> Color.White
    }
    val color by animateColorAsState(targetColor)

    Canvas(modifier = modifier) {
        if (startValues.size < 2 || startValues.size != endValues.size) return@Canvas

        val step = size.width / startValues.lastIndex
        val linePath = Path()
        val fillPath = Path().apply {
            moveTo(0f, size.height)
        }

        startValues.indices.forEach { index ->
            val value = startValues[index] +
                    (endValues[index] - startValues[index]) * transitionProgress.value
            val x = index * step
            val y = size.height * (1f - value)
            if (index == 0) linePath.moveTo(x, y)
            else linePath.lineTo(x, y)
            fillPath.lineTo(x, y)
        }
        fillPath.lineTo(size.width, size.height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    color.copy(alpha = 0.72f),
                    color.copy(alpha = 0.08f)
                )
            )
        )
        drawPath(
            path = linePath,
            color = color.copy(alpha = 0.9f),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

private fun List<Double>.normalizedHistogramValues(): FloatArray {
    val maxValue = maxOrNull()?.takeIf { it > 0.0 } ?: return FloatArray(size)
    return FloatArray(size) { index ->
        (this[index] / maxValue).toFloat().coerceIn(0f, 1f)
    }
}

private fun interpolatedHistogramValues(
    start: FloatArray,
    end: FloatArray,
    fraction: Float,
    targetSize: Int
): FloatArray = FloatArray(targetSize) { index ->
    val position = if (targetSize > 1) {
        index / (targetSize - 1f)
    } else {
        0f
    }
    val startValue = start.valueAt(position)
    startValue + (end.valueAt(position) - startValue) * fraction
}

private fun FloatArray.valueAt(position: Float): Float {
    if (isEmpty()) return 0f
    if (size == 1) return first()

    val scaledIndex = position.coerceIn(0f, 1f) * lastIndex
    val lowerIndex = scaledIndex.toInt()
    val upperIndex = (lowerIndex + 1).coerceAtMost(lastIndex)
    val fraction = scaledIndex - lowerIndex
    return this[lowerIndex] + (this[upperIndex] - this[lowerIndex]) * fraction
}

@Composable
private fun GPUImagePreview(
    gpuImage: GPUImage,
    modifier: Modifier
) {
    AndroidView(
        modifier = modifier.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
        factory = { context ->
            GLTextureView(context).apply {
                gpuImage.setGLTextureView(this)
            }
        }
    )
}

@Composable
private fun CurvesControls(
    activeCurveType: Int,
    canDeleteSelectedPoint: Boolean,
    onCurveTypeChange: (Int) -> Unit,
    onDeleteSelectedPoint: () -> Unit,
    colors: ImageCurvesEditorColors,
    curvesSelectionText: @Composable (Int) -> Unit,
    vertical: Boolean,
    modifier: Modifier
) {
    val items = listOf(
        PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeLuminance to colors.lumaCurveColor,
        PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeRed to colors.redCurveColor,
        PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeGreen to colors.greenCurveColor,
        PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeBlue to colors.blueCurveColor
    )
    val curveButton: @Composable (Pair<Int, Color>) -> Unit = { (type, color) ->
        CurveSelectionButton(
            selected = activeCurveType == type,
            color = color,
            onClick = { onCurveTypeChange(type) },
            content = { curvesSelectionText(type) }
        )
    }

    if (vertical) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            items.forEach { curveButton(it) }
            PointDeleteButton(
                enabled = canDeleteSelectedPoint,
                onClick = onDeleteSelectedPoint,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    } else {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            itemVerticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            items.dropLast(1).forEach { curveButton(it) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                curveButton(items.last())
                PointDeleteButton(
                    enabled = canDeleteSelectedPoint,
                    onClick = onDeleteSelectedPoint
                )
            }
        }
    }
}

@Composable
private fun CurveSelectionButton(
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        color = animateColorAsState(
            if (selected) {
                color.copy(alpha = 0.2f)
            } else {
                Color.Transparent
            }
        ).value,
        contentColor = color,
        border = BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = if (selected) 0.8f else 0.26f)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun PointDeleteButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        if (enabled) 1f else 0.5f
    )
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.errorContainer.copy(0.5f * alpha),
        contentColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        }
    }
}

private val CurvePointTouchExpansion = 12.dp

private fun Modifier.expandBeyondBounds(
    expansion: Int
): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(
        Constraints.fixed(
            width = constraints.maxWidth + expansion * 2,
            height = constraints.maxHeight + expansion * 2
        )
    )
    layout(constraints.maxWidth, constraints.maxHeight) {
        placeable.place(-expansion, -expansion)
    }
}

private fun Modifier.expandedCurvePointTouchTarget(
    expansion: Int,
    coordinateOffset: Int,
    viewProvider: () -> PhotoFilterCurvesControl?
): Modifier = this then ExpandedCurvePointTouchElement(
    expansion = expansion,
    coordinateOffset = coordinateOffset,
    viewProvider = viewProvider
)

private data class ExpandedCurvePointTouchElement(
    val expansion: Int,
    val coordinateOffset: Int,
    val viewProvider: () -> PhotoFilterCurvesControl?
) : ModifierNodeElement<ExpandedCurvePointTouchNode>() {

    override fun create() = ExpandedCurvePointTouchNode(
        expansion = expansion,
        coordinateOffset = coordinateOffset,
        viewProvider = viewProvider
    )

    override fun update(node: ExpandedCurvePointTouchNode) {
        node.update(
            expansion = expansion,
            coordinateOffset = coordinateOffset,
            viewProvider = viewProvider
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "expandedCurvePointTouchTarget"
        properties["expansion"] = expansion
    }
}

private class ExpandedCurvePointTouchNode(
    private var expansion: Int,
    private var coordinateOffset: Int,
    private var viewProvider: () -> PhotoFilterCurvesControl?
) : Modifier.Node(), PointerInputModifierNode {

    private var forwarding = false
    private var downTime = 0L
    private var lastX = 0f
    private var lastY = 0f

    override val touchBoundsExpansion: TouchBoundsExpansion
        get() = TouchBoundsExpansion.Absolute(
            left = expansion,
            top = expansion,
            right = expansion,
            bottom = expansion
        )

    fun update(
        expansion: Int,
        coordinateOffset: Int,
        viewProvider: () -> PhotoFilterCurvesControl?
    ) {
        this.expansion = expansion
        this.coordinateOffset = coordinateOffset
        this.viewProvider = viewProvider
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        if (pass != PointerEventPass.Initial) return

        val change = pointerEvent.changes.firstOrNull() ?: return
        val position = change.position
        val viewX = position.x + coordinateOffset
        val viewY = position.y + coordinateOffset
        lastX = viewX
        lastY = viewY

        if (!forwarding) {
            if (!change.changedToDownIgnoreConsumed()) return

            val isOutside = position.x < 0f ||
                    position.x > bounds.width ||
                    position.y < 0f ||
                    position.y > bounds.height
            val view = viewProvider() ?: return
            if (isOutside && !view.isControlPointHit(viewX, viewY)) return

            forwarding = true
            downTime = change.uptimeMillis
            dispatchToView(
                view = view,
                action = MotionEvent.ACTION_DOWN,
                eventTime = change.uptimeMillis,
                x = viewX,
                y = viewY
            )
            change.consume()
            return
        }

        val action = if (change.changedToUpIgnoreConsumed()) {
            MotionEvent.ACTION_UP
        } else {
            MotionEvent.ACTION_MOVE
        }
        viewProvider()?.let { view ->
            dispatchToView(
                view = view,
                action = action,
                eventTime = change.uptimeMillis,
                x = viewX,
                y = viewY
            )
        }
        change.consume()

        if (action == MotionEvent.ACTION_UP) {
            forwarding = false
        }
    }

    override fun onCancelPointerInput() {
        if (forwarding) {
            viewProvider()?.let { view ->
                dispatchToView(
                    view = view,
                    action = MotionEvent.ACTION_CANCEL,
                    eventTime = downTime,
                    x = lastX,
                    y = lastY
                )
            }
            forwarding = false
        }
    }

    private fun dispatchToView(
        view: PhotoFilterCurvesControl,
        action: Int,
        eventTime: Long,
        x: Float,
        y: Float
    ) {
        MotionEvent.obtain(
            downTime,
            eventTime,
            action,
            x,
            y,
            0
        ).also { event ->
            view.onTouchEvent(event)
            event.recycle()
        }
    }
}
