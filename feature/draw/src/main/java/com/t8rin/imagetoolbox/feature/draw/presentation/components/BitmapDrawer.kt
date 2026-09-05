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

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.feature.draw.presentation.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.PorterDuff
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Pt
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.createScaledBitmap
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.modifier.HelperGridParams
import com.t8rin.imagetoolbox.core.ui.widget.saver.OneTimeEffect
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.domain.WarpStroke
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.LineAngleIndicator
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.BitmapDrawerPreview
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.DrawRenderCache
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.GradientStrokeCache
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.MotionEvent
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.copy
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawBitmapThroughPath
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawPathWithGradient
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawRepeatedBitmapOnPath
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.drawRepeatedTextOnPath
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.floodFill
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.handle
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.overlay
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.pathEffectPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.pointerDrawObserver
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.rememberDrawImage
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.rememberDrawPathEffect
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.rememberPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.rememberPathHelper
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.withPathGradient
import com.t8rin.trickle.WarpBrush
import com.t8rin.trickle.WarpEngine
import com.t8rin.trickle.WarpMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.rememberZoomState
import kotlin.math.roundToInt
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BitmapDrawer(
    imageBitmap: ImageBitmap,
    renderCache: DrawRenderCache,
    sourceKey: Any = imageBitmap.asAndroidBitmap(),
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
    onDrawStart: (() -> Unit)? = null,
    onDraw: ((Bitmap) -> Unit)? = null,
    onDrawFinish: (() -> Unit)? = null,
    onRenderReady: ((Boolean) -> Unit)? = null,
    backgroundColor: Color,
    panEnabled: Boolean,
    drawColor: Color,
    gradientPalette: GradientPalette? = null,
    gradientLength: Float = 1f,
    drawLineStyle: DrawLineStyle = DrawLineStyle.None,
    helperGridParams: HelperGridParams = remember { HelperGridParams() },
    showLineAngle: Boolean = false,
    onRemovePath: (UiPathPaint) -> Unit = {},
) {
    val context = LocalContext.current

    val settingsState = LocalSettingsState.current
    val magnifierEnabled by remember(zoomState.scale, settingsState.magnifierEnabled) {
        derivedStateOf {
            zoomState.scale <= 3f && !panEnabled && settingsState.magnifierEnabled
        }
    }
    val globalTouchPointersCount = remember { mutableIntStateOf(0) }

    var currentDrawPosition by remember { mutableStateOf(Offset.Unspecified) }

    Box(
        modifier = Modifier.pointerDrawObserver(
            magnifierEnabled = magnifierEnabled,
            currentDrawPosition = currentDrawPosition,
            zoomState = zoomState,
            globalTouchPointersCount = globalTouchPointersCount,
            panEnabled = panEnabled
        ),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(modifier) {
            val motionEvent = remember { mutableStateOf(MotionEvent.Idle) }
            var previousDrawPosition by remember { mutableStateOf(Offset.Unspecified) }
            var drawDownPosition by remember { mutableStateOf(Offset.Unspecified) }

            val imageWidth = constraints.maxWidth
            val imageHeight = constraints.maxHeight

            val drawImageBitmap by produceState<ImageBitmap?>(
                initialValue = null,
                imageBitmap,
                imageWidth,
                imageHeight,
                backgroundColor
            ) {
                value = null
                value = withContext(Dispatchers.Default) {
                    val original = imageBitmap.asAndroidBitmap()
                    val scaled = original.createScaledBitmap(imageWidth, imageHeight)
                    (if (scaled === original) scaled.copy(
                        Bitmap.Config.ARGB_8888,
                        true
                    ) else scaled).apply {
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

            var invalidations by remember {
                mutableIntStateOf(0)
            }

            val needsDrawPathBitmap = !isEraserOn && (
                    drawMode is DrawMode.SpotHeal
                            || drawMode is DrawMode.Warp
                    )

            val drawPathBitmap: ImageBitmap? by remember(
                imageWidth,
                imageHeight,
                invalidations,
                needsDrawPathBitmap
            ) {
                derivedStateOf {
                    if (needsDrawPathBitmap) {
                        createBitmap(imageWidth, imageHeight).asImageBitmap()
                    } else null
                }
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

            val canvas: Canvas = remember(drawBitmap, imageHeight, imageWidth) {
                Canvas(drawBitmap)
            }

            val drawPathCanvas = remember(drawPathBitmap, imageWidth, imageHeight) {
                drawPathBitmap?.let(::Canvas)
            }

            val canvasSize by remember(canvas.nativeCanvas) {
                derivedStateOf {
                    IntegerSize(
                        width = canvas.nativeCanvas.width,
                        height = canvas.nativeCanvas.height
                    )
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
                drawPathMode,
                gradientPalette,
                gradientLength
            ) { mutableStateOf(Path()) }

            var pathWithoutTransformations by remember(
                drawMode,
                strokeWidth,
                isEraserOn,
                drawColor,
                brushSoftness,
                drawPathMode,
                gradientPalette,
                gradientLength
            ) { mutableStateOf(Path()) }

            var warpRuntimeStrokes by remember(drawMode) {
                mutableStateOf(emptyList<WarpStroke>())
            }
            var warpClearTrigger by remember {
                mutableIntStateOf(0)
            }
            var warpPreviewToken by remember {
                mutableLongStateOf(0L)
            }
            var pendingWarpCommitToken by remember {
                mutableLongStateOf(-1L)
            }
            var previousPathsCount by remember {
                mutableIntStateOf(paths.size)
            }

            LaunchedEffect(paths.size) {
                if (paths.isEmpty() || paths.size < previousPathsCount) {
                    warpClearTrigger++
                    pendingWarpCommitToken = -1L
                }
                previousPathsCount = paths.size
            }

            LaunchedEffect(drawMode, isEraserOn) {
                if (drawMode !is DrawMode.Warp || isEraserOn) {
                    pendingWarpCommitToken = -1L
                }
            }

            val isWarpInputLocked by remember(drawMode, isEraserOn, pendingWarpCommitToken) {
                derivedStateOf {
                    drawMode is DrawMode.Warp && !isEraserOn && pendingWarpCommitToken >= 0L
                }
            }

            val gradientStroke = remember(canvasSize) { GradientStrokeCache() }
            DisposableEffect(gradientStroke) {
                onDispose { gradientStroke.clear() }
            }

            val history = renderCache.sessionFor(sourceKey, backgroundColor.toArgb())
            var historyBitmap by remember(history, canvasSize) { mutableStateOf<Bitmap?>(null) }
            var renderedPaths by remember(
                history,
                canvasSize
            ) { mutableStateOf(emptyList<UiPathPaint>()) }
            var renderingPath by remember(
                history,
                canvasSize
            ) { mutableStateOf<UiPathPaint?>(null) }
            var pendingCommit by remember { mutableStateOf<UiPathPaint?>(null) }
            var pendingPreview by remember(
                history,
                canvasSize
            ) { mutableStateOf<DrawPreviewSnapshot?>(null) }

            val effectSource = remember(historyBitmap, drawImageBitmap) {
                historyBitmap?.let { drawImageBitmap?.overlay(it.asImageBitmap()) }
                    ?: drawImageBitmap ?: imageBitmap
            }
            val preparedEffect = if (drawMode is DrawMode.PathEffect && !isEraserOn) {
                rememberDrawPathEffect(
                    drawMode = drawMode,
                    canvasSize = canvasSize,
                    imageWidth = imageWidth,
                    imageHeight = imageHeight,
                    outputImage = effectSource,
                    onRequestFiltering = onRequestFiltering,
                    onInvalidate = { invalidations++ }
                ).value
            } else null
            var pendingFilteredSource by remember { mutableStateOf<Bitmap?>(null) }

            LaunchedEffect(history, canvasSize, drawImageBitmap, paths) {
                val source = drawImageBitmap ?: return@LaunchedEffect
                val preparedPath = pendingCommit
                val preparedBitmap = pendingFilteredSource
                try {
                    val result = withContext(Dispatchers.Default) {
                        history.render(
                            paths = paths,
                            canvasSize = canvasSize,
                            source = source.asAndroidBitmap(),
                            context = context,
                            onRequestFiltering = onRequestFiltering,
                            preparedPath = preparedPath,
                            preparedEffect = preparedBitmap,
                            onRenderingPath = { renderingPath = it }
                        )
                    }
                    historyBitmap = result
                    renderedPaths = paths.toList()
                    invalidations++
                } finally {
                    renderingPath = null
                }
            }

            LaunchedEffect(renderedPaths, paths, pendingCommit) {
                pendingCommit?.let { pending ->
                    if (pending in renderedPaths || pending !in paths) {
                        drawPath = Path()
                        pendingCommit = null
                        pendingFilteredSource = null
                        warpClearTrigger++
                        warpRuntimeStrokes = emptyList()
                        pendingWarpCommitToken = -1L
                        invalidations++
                    }
                }
            }

            LaunchedEffect(renderedPaths, paths) {
                pendingPreview?.let { preview ->
                    if (!preview.isPrefixOf(paths) || preview.isPrefixOf(renderedPaths)) {
                        pendingPreview = null
                    }
                }
            }

            LoadingDialog(
                visible = renderingPath?.drawMode is DrawMode.SpotHeal,
                onCancelLoading = {
                    renderingPath?.let(onRemovePath)
                },
                canCancel = true
            )

            val outputImage by remember(invalidations, historyBitmap, drawImageBitmap, drawPath) {
                derivedStateOf { drawImageBitmap?.overlay(drawBitmap) ?: imageBitmap }
            }

            var finishedStroke: UiPathPaint? = null
            var pointerReleased = false
            with(canvas) {
                val drawHelper by rememberPathHelper(
                    drawDownPosition = drawDownPosition,
                    currentDrawPosition = currentDrawPosition,
                    onPathChange = { drawPath = it },
                    strokeWidth = strokeWidth,
                    canvasSize = canvasSize,
                    drawPathMode = drawPathMode,
                    isEraserOn = isEraserOn,
                    drawMode = drawMode
                )

                motionEvent.value.handle(
                    onDown = {
                        if (drawMode is DrawMode.Warp && !isEraserOn) {
                            warpPreviewToken++
                            warpRuntimeStrokes = emptyList()
                            drawPath = Path()
                            pathWithoutTransformations = Path()
                        } else {
                            warpClearTrigger++
                            warpRuntimeStrokes = emptyList()
                        }

                        if (currentDrawPosition.isSpecified) {
                            onDrawStart?.invoke()
                            drawPath.moveTo(currentDrawPosition.x, currentDrawPosition.y)
                            if (gradientPalette != null && drawPathMode == DrawPathMode.Free) {
                                drawPath.lineTo(currentDrawPosition.x, currentDrawPosition.y)
                            }
                            previousDrawPosition = currentDrawPosition
                            pathWithoutTransformations = drawPath.copy()
                        } else {
                            drawPath = Path()
                            pathWithoutTransformations = Path()
                        }

                        motionEvent.value = MotionEvent.Idle
                    },
                    onMove = {
                        if (drawMode is DrawMode.Warp && !isEraserOn) {
                            if (
                                previousDrawPosition.isSpecified &&
                                currentDrawPosition.isSpecified
                            ) {
                                warpRuntimeStrokes += WarpStroke(
                                    fromX = previousDrawPosition.x,
                                    fromY = previousDrawPosition.y,
                                    toX = currentDrawPosition.x,
                                    toY = currentDrawPosition.y
                                )
                            }
                        }

                        drawHelper.drawPath(
                            currentDrawPath = drawPath,
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
                            },
                        )

                        motionEvent.value = MotionEvent.Idle
                    },
                    onUp = {
                        if (currentDrawPosition.isSpecified && drawDownPosition.isSpecified) {
                            if (drawMode is DrawMode.Warp && warpRuntimeStrokes.isNotEmpty() && !isEraserOn) {
                                PathMeasure().apply {
                                    setPath(drawPath, false)
                                }.let {
                                    it.getPosition(it.length)
                                }.takeOrElse { currentDrawPosition }.let { lastPoint ->
                                    warpRuntimeStrokes += WarpStroke(
                                        fromX = lastPoint.x,
                                        fromY = lastPoint.y,
                                        toX = currentDrawPosition.x,
                                        toY = currentDrawPosition.y
                                    )
                                }

                                val committed = UiPathPaint(
                                    path = drawPath.copy(),
                                    strokeWidth = strokeWidth,
                                    brushSoftness = 0.pt,
                                    drawColor = Color.Transparent,
                                    isErasing = false,
                                    drawMode = drawMode.copy(
                                        strokes = warpRuntimeStrokes.toList(),
                                        previewClearToken = warpPreviewToken
                                    ),
                                    canvasSize = canvasSize,
                                    drawPathMode = DrawPathMode.Free,
                                    drawLineStyle = DrawLineStyle.None
                                )
                                pendingCommit = committed
                                finishedStroke = committed
                                pendingWarpCommitToken = warpPreviewToken
                            } else {
                                var addPath = true

                                drawHelper.drawPath(
                                    currentDrawPath = null,
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
                                            if (it.length < 10f && drawMode is DrawMode.Text) {
                                                addPath = false
                                            }

                                            it.getPosition(it.length)
                                        }.takeOrElse { currentDrawPosition }.let { lastPoint ->
                                            if (drawPath.isEmpty) {
                                                drawPath.moveTo(lastPoint.x, lastPoint.y)
                                            }
                                            drawPath.lineTo(
                                                currentDrawPosition.x,
                                                currentDrawPosition.y
                                            )
                                        }
                                    },
                                    onFloodFill = { tolerance ->
                                        outputImage
                                            .floodFill(
                                                offset = currentDrawPosition,
                                                tolerance = tolerance
                                            )
                                            ?.let { drawPath = it }
                                    }
                                )

                                if (addPath) {
                                    val committed = UiPathPaint(
                                        path = drawPath.copy(),
                                        strokeWidth = strokeWidth,
                                        brushSoftness = brushSoftness,
                                        drawColor = drawColor,
                                        isErasing = isEraserOn,
                                        drawMode = drawMode,
                                        canvasSize = canvasSize,
                                        drawPathMode = drawPathMode,
                                        drawLineStyle = drawLineStyle,
                                        gradientPalette = gradientPalette,
                                        gradientLength = gradientLength
                                    )
                                    if (!isEraserOn && (drawMode is DrawMode.PathEffect || drawMode is DrawMode.SpotHeal)) {
                                        pendingCommit = committed
                                        pendingFilteredSource = preparedEffect?.asAndroidBitmap()
                                    }
                                    finishedStroke = committed
                                }
                            }
                        }

                        motionEvent.value = MotionEvent.Idle
                        pointerReleased = true
                    }
                )

                with(nativeCanvas) {
                    drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
                    val completed = pendingPreview?.takeIf {
                        it.isPrefixOf(paths) && !it.isPrefixOf(renderedPaths)
                    }?.bitmap ?: historyBitmap
                    completed?.let { drawBitmap(it, 0f, 0f, null) }
                        ?: drawColor(backgroundColor.toArgb())

                    if (drawPath.isEmpty) gradientStroke.clear()

                    if ((pendingCommit == null || pendingCommit !in renderedPaths) &&
                        ((drawMode !is DrawMode.PathEffect && drawMode !is DrawMode.Warp) || isEraserOn)
                    ) {
                        val androidPath by remember(drawPath) {
                            derivedStateOf {
                                drawPath.asAndroidPath()
                            }
                        }
                        if (drawMode is DrawMode.Text && !isEraserOn) {
                            val textPaint = if (gradientPalette != null) {
                                drawPaint.withPathGradient(
                                    path = androidPath,
                                    palette = gradientPalette,
                                    gradientLength = gradientLength
                                )
                            } else drawPaint
                            if (drawMode.isRepeated) {
                                drawRepeatedTextOnPath(
                                    text = drawMode.text,
                                    path = androidPath,
                                    paint = textPaint,
                                    interval = drawMode.repeatingInterval.toPx(canvasSize)
                                )
                            } else if (drawMode.text.isNotEmpty() && !androidPath.isEmpty && (drawDownPosition - currentDrawPosition).getDistance() > 10f) {
                                var readyToDraw by rememberSaveable {
                                    mutableStateOf(false)
                                }
                                OneTimeEffect {
                                    delay(100)
                                    readyToDraw = true
                                }
                                if (readyToDraw) {
                                    drawTextOnPath(drawMode.text, androidPath, 0f, 0f, textPaint)
                                }
                            }
                        } else if (drawMode is DrawMode.Image && !isEraserOn) {
                            val image by rememberDrawImage(
                                drawMode = drawMode,
                                strokeWidth = strokeWidth,
                                canvasSize = canvasSize,
                                onInvalidate = { invalidations++ }
                            )
                            image?.let {
                                drawRepeatedBitmapOnPath(
                                    bitmap = it,
                                    path = androidPath,
                                    paint = drawPaint,
                                    interval = drawMode.repeatingInterval.toPx(canvasSize)
                                )
                            }
                        } else if (drawMode is DrawMode.SpotHeal && !isEraserOn) {
                            drawPathCanvas?.nativeCanvas?.let {
                                with(it) {
                                    drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
                                    drawPath(
                                        androidPath,
                                        drawPaint.apply { color = Color.Red.copy(0.5f).toArgb() }
                                    )
                                }
                            }
                        } else if (drawPathMode is DrawPathMode.Outlined && !isEraserOn) {
                            drawPathMode.fillColor?.let { fillColor ->
                                val filledPaint = remember(fillColor, drawPaint) {
                                    AndroidPaint().apply {
                                        set(drawPaint)
                                        style = AndroidPaint.Style.FILL
                                        color = fillColor.colorInt
                                        if (Color(fillColor.colorInt).alpha == 1f) {
                                            alpha =
                                                (drawColor.alpha * 255).roundToInt()
                                                    .coerceIn(0, 255)
                                        }
                                        pathEffect = null
                                    }
                                }

                                drawPath(androidPath, filledPaint)
                            }
                            drawPathWithGradient(
                                path = androidPath,
                                paint = drawPaint,
                                palette = gradientPalette.takeUnless { isEraserOn },
                                gradientLength = gradientLength,
                                isFilled = false,
                                canvasSize = canvasSize,
                                softnessRadius = brushSoftness.toPx(canvasSize),
                                cache = gradientStroke
                            )
                        } else {
                            drawPathWithGradient(
                                path = androidPath,
                                paint = drawPaint,
                                palette = gradientPalette.takeUnless { isEraserOn },
                                gradientLength = gradientLength,
                                isFilled = !isEraserOn && drawPathMode.isFilled,
                                canvasSize = canvasSize,
                                softnessRadius = brushSoftness.toPx(canvasSize),
                                cache = gradientStroke
                            )
                        }
                    }
                }
            }

            if (preparedEffect != null && (pendingCommit == null || pendingCommit !in renderedPaths)) {
                canvas.nativeCanvas.drawBitmapThroughPath(
                    bitmap = preparedEffect.asAndroidBitmap(),
                    path = drawPath.asAndroidPath(),
                    paint = pathEffectPaint(strokeWidth, drawPathMode, canvasSize)
                )
            }

            finishedStroke?.let { committed ->
                if (committed.isErasing || committed.drawMode !is DrawMode.SpotHeal &&
                    committed.drawMode !is DrawMode.Warp &&
                    (committed.drawMode !is DrawMode.PathEffect || preparedEffect != null)
                ) {
                    pendingPreview = DrawPreviewSnapshot(
                        paths = paths + committed,
                        bitmap = drawBitmap.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)
                    )
                    drawPath = Path()
                    gradientStroke.clear()
                }
                onAddPath(committed)
            }
            if (pointerReleased) {
                currentDrawPosition = Offset.Unspecified
                previousDrawPosition = Offset.Unspecified
                if (pendingCommit == null) drawPath = Path()
                pathWithoutTransformations = Path()
                onDrawFinish?.invoke()
            }

            var warpEngine by remember {
                mutableStateOf<WarpEngine?>(null)
            }

            LaunchedEffect(warpClearTrigger, drawMode) {
                if (drawMode is DrawMode.Warp && !isEraserOn) {
                    warpEngine?.release()
                    warpEngine = WarpEngine(
                        src = outputImage.asAndroidBitmap()
                    )
                } else {
                    warpEngine?.release()
                    warpEngine = null
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    warpEngine?.release()
                    warpEngine = null
                }
            }

            LaunchedEffect(warpEngine) {
                snapshotFlow { warpRuntimeStrokes.lastOrNull() }
                    .filterNotNull()
                    .collect {
                        val engine = warpEngine ?: return@collect
                        val warpMode = drawMode as? DrawMode.Warp ?: return@collect

                        engine.applyStroke(
                            fromX = it.fromX,
                            fromY = it.fromY,
                            toX = it.toX,
                            toY = it.toY,
                            brush = WarpBrush(
                                radius = strokeWidth.toPx(canvasSize),
                                strength = warpMode.strength,
                                hardness = warpMode.hardness
                            ),
                            mode = WarpMode.valueOf(warpMode.warpMode.name)
                        )
                        invalidations++
                    }
            }

            val warpedImage by remember(invalidations, warpEngine, historyBitmap, drawPath) {
                derivedStateOf {
                    warpEngine?.takeIf { warpRuntimeStrokes.isNotEmpty() }?.let { engine ->
                        engine.render().asImageBitmap().also {
                            it.prepareToDraw()
                        }
                    } ?: drawPathBitmap?.let(outputImage::overlay) ?: outputImage
                }
            }

            val previewBitmap by remember(invalidations, historyBitmap, drawPath) {
                derivedStateOf {
                    if (drawMode is DrawMode.Warp) {
                        warpedImage
                    } else {
                        drawPathBitmap?.let(outputImage::overlay) ?: outputImage
                    }
                }
            }

            LaunchedEffect(previewBitmap, paths, renderedPaths, pendingCommit, historyBitmap) {
                onDraw?.invoke(previewBitmap.asAndroidBitmap())
                onRenderReady?.invoke(drawImageBitmap != null && paths == renderedPaths && pendingCommit == null)
            }

            BitmapDrawerPreview(
                preview = previewBitmap,
                globalTouchPointersCount = globalTouchPointersCount,
                onReceiveMotionEvent = { motionEvent.value = it },
                onInvalidate = { invalidations++ },
                onUpdateCurrentDrawPosition = { currentDrawPosition = it },
                onUpdateDrawDownPosition = { drawDownPosition = it },
                drawEnabled = !panEnabled && !isWarpInputLocked && pendingCommit == null && drawImageBitmap != null &&
                        (renderedPaths == paths || isEraserOn || (drawMode !is DrawMode.PathEffect && drawMode !is DrawMode.SpotHeal && drawMode !is DrawMode.Warp)),
                helperGridParams = helperGridParams,
                drawBitmapBorder = settingsState.drawBitmapBorder
            )

            if (showLineAngle && drawPathMode.canShowLineAngle() && drawDownPosition.isSpecified && currentDrawPosition.isSpecified) {
                LineAngleIndicator(
                    drawDownPosition = drawDownPosition,
                    currentDrawPosition = currentDrawPosition,
                    imageWidth = imageWidth,
                    imageHeight = imageHeight,
                    isMagnifierEnabled = magnifierEnabled
                )
            }
        }
    }
}

private data class DrawPreviewSnapshot(
    val paths: List<UiPathPaint>,
    val bitmap: Bitmap
) {
    fun isPrefixOf(history: List<UiPathPaint>): Boolean =
        history.size >= paths.size && history.subList(0, paths.size) == paths
}