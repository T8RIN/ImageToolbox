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
    val frameStrokeWidthPx = with(density) {
        frameStrokeWidth.toPx()
    }

    val touchIndex = remember {
        mutableIntStateOf(-1)
    }
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

        val pointScales = List(drawPoints.value.size) {
            animateFloatAsState(if (it == touchIndex.intValue) 1.4f else 1f)
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
                    bitmap,
                    showMagnifier
                ) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            touchIndex.intValue = -1
                            drawPoints.value.forEachIndexed { index, drawProperties ->
                                val isTouched = isTouched(
                                    center = drawProperties,
                                    touchPosition = offset,
                                    radius = handleRadiusPx
                                )

                                if (isTouched) {
                                    touchIndex.intValue = index
                                }
                            }

                            magnifierCenter =
                                if (showMagnifier && touchIndex.intValue != -1) offset
                                else Offset.Unspecified
                        },
                        onDrag = { _, dragAmount ->
                            drawPoints.value
                                .getOrNull(touchIndex.intValue)
                                ?.let { point ->
                                    drawPoints.value = drawPoints.value
                                        .toMutableList()
                                        .apply {
                                            this[touchIndex.intValue] = point
                                                .plus(dragAmount)
                                                .let {
                                                    if (coercePointsToImageArea) {
                                                        it.coerceToImageBounds()
                                                    } else it
                                                }
                                                .also { newPoint ->
                                                    if (showMagnifier) magnifierCenter = newPoint
                                                }
                                        }
                                }
                        },
                        onDragEnd = {
                            drawPoints.value
                                .getOrNull(touchIndex.intValue)
                                ?.let { point ->
                                    drawPoints.value = drawPoints.value
                                        .toMutableList()
                                        .apply {
                                            this[touchIndex.intValue] = point
                                        }
                                }
                            touchIndex.intValue = -1
                            magnifierCenter = Offset.Unspecified
                        },
                        onDragCancel = {
                            drawPoints.value
                                .getOrNull(touchIndex.intValue)
                                ?.let { point ->
                                    drawPoints.value = drawPoints.value
                                        .toMutableList()
                                        .apply {
                                            this[touchIndex.intValue] = point
                                        }
                                }
                            touchIndex.intValue = -1
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

private fun isTouched(center: Offset, touchPosition: Offset, radius: Float): Boolean {
    return center.minus(touchPosition).getDistanceSquared() < radius * radius * radius
}

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