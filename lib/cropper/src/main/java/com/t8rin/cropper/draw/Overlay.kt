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

package com.t8rin.cropper.draw

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.t8rin.cropper.TouchRegion
import com.t8rin.cropper.model.CropImageMask
import com.t8rin.cropper.model.CropOutline
import com.t8rin.cropper.model.CropPath
import com.t8rin.cropper.model.CropShape
import com.t8rin.cropper.util.drawGrid
import com.t8rin.cropper.util.drawWithLayer
import com.t8rin.cropper.util.scaleAndTranslatePath
import com.t8rin.imagetoolbox.core.resources.utils.compositeOverSafe

/**
 * Draw overlay composed of 9 rectangles. When [drawHandles]
 * is set draw handles for changing drawing rectangle
 */
@Composable
internal fun DrawingOverlay(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    cropOutline: CropOutline,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Dp,
    drawHandles: Boolean,
    middleHandleSize: Float,
    selectedHandle: TouchRegion,
    selectedHandleScale: Float,
) {
    val density = LocalDensity.current
    val layoutDirection: LayoutDirection = LocalLayoutDirection.current

    val strokeWidthPx = LocalDensity.current.run { strokeWidth.toPx() }
    val handleSizePx = with(density) { CornerHandleSize.toPx() }
    val middleHandleSizePx = with(density) { middleHandleSize.dp.toPx() }
    val handleStrokeWidthPx = with(density) { CornerHandleStrokeWidth.toPx() }
    val middleHandleStrokeWidthPx = with(density) { MiddleHandleStrokeWidth.toPx() }

    val pathHandles = remember {
        Path()
    }

    val middlePathHandles = remember {
        Path()
    }

    val selectedPathHandle = remember {
        Path()
    }

    when (cropOutline) {
        is CropShape -> {

            val outline = remember(rect, cropOutline) {
                cropOutline.shape.createOutline(rect.size, layoutDirection, density)
            }

            DrawingOverlayImpl(
                modifier = modifier,
                drawOverlay = drawOverlay,
                rect = rect,
                drawGrid = drawGrid,
                transparentColor = transparentColor,
                overlayColor = overlayColor,
                handleColor = handleColor,
                strokeWidth = strokeWidthPx,
                drawHandles = drawHandles,
                handleSize = handleSizePx,
                middleHandleSize = middleHandleSizePx,
                handleStrokeWidth = handleStrokeWidthPx,
                middleHandleStrokeWidth = middleHandleStrokeWidthPx,
                selectedHandle = selectedHandle,
                selectedHandleScale = selectedHandleScale,
                pathHandles = pathHandles,
                middlePathHandles = middlePathHandles,
                selectedPathHandle = selectedPathHandle,
                outline = outline
            )
        }

        is CropPath -> {
            val path = remember(rect, cropOutline) {
                Path().apply {
                    addPath(cropOutline.path)
                    scaleAndTranslatePath(rect.width, rect.height)
                }
            }


            DrawingOverlayImpl(
                modifier = modifier,
                drawOverlay = drawOverlay,
                rect = rect,
                drawGrid = drawGrid,
                transparentColor = transparentColor,
                overlayColor = overlayColor,
                handleColor = handleColor,
                strokeWidth = strokeWidthPx,
                drawHandles = drawHandles,
                handleSize = handleSizePx,
                middleHandleSize = middleHandleSizePx,
                handleStrokeWidth = handleStrokeWidthPx,
                middleHandleStrokeWidth = middleHandleStrokeWidthPx,
                selectedHandle = selectedHandle,
                selectedHandleScale = selectedHandleScale,
                pathHandles = pathHandles,
                middlePathHandles = middlePathHandles,
                selectedPathHandle = selectedPathHandle,
                path = path
            )
        }

        is CropImageMask -> {
            val imageBitmap = cropOutline.image

            DrawingOverlayImpl(
                modifier = modifier,
                drawOverlay = drawOverlay,
                rect = rect,
                drawGrid = drawGrid,
                transparentColor = transparentColor,
                overlayColor = overlayColor,
                handleColor = handleColor,
                strokeWidth = strokeWidthPx,
                drawHandles = drawHandles,
                handleSize = handleSizePx,
                middleHandleSize = middleHandleSizePx,
                handleStrokeWidth = handleStrokeWidthPx,
                middleHandleStrokeWidth = middleHandleStrokeWidthPx,
                selectedHandle = selectedHandle,
                selectedHandleScale = selectedHandleScale,
                pathHandles = pathHandles,
                middlePathHandles = middlePathHandles,
                selectedPathHandle = selectedPathHandle,
                image = imageBitmap
            )
        }
    }
}

@Composable
private fun DrawingOverlayImpl(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Float,
    drawHandles: Boolean,
    handleSize: Float,
    middleHandleSize: Float,
    handleStrokeWidth: Float,
    middleHandleStrokeWidth: Float,
    selectedHandle: TouchRegion,
    selectedHandleScale: Float,
    pathHandles: Path,
    middlePathHandles: Path,
    selectedPathHandle: Path,
    outline: Outline,
) {
    Canvas(modifier = modifier) {
        drawOverlay(
            drawOverlay = drawOverlay,
            rect = rect,
            drawGrid = drawGrid,
            transparentColor = transparentColor,
            overlayColor = overlayColor,
            handleColor = handleColor,
            strokeWidth = strokeWidth,
            drawHandles = drawHandles,
            handleSize = handleSize,
            middleHandleSize = middleHandleSize,
            handleStrokeWidth = handleStrokeWidth,
            middleHandleStrokeWidth = middleHandleStrokeWidth,
            selectedHandle = selectedHandle,
            selectedHandleScale = selectedHandleScale,
            pathHandles = pathHandles,
            middlePathHandles = middlePathHandles,
            selectedPathHandle = selectedPathHandle
        ) {
            drawCropOutline(outline = outline)
        }
    }
}

@Composable
private fun DrawingOverlayImpl(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Float,
    drawHandles: Boolean,
    handleSize: Float,
    middleHandleSize: Float,
    handleStrokeWidth: Float,
    middleHandleStrokeWidth: Float,
    selectedHandle: TouchRegion,
    selectedHandleScale: Float,
    pathHandles: Path,
    middlePathHandles: Path,
    selectedPathHandle: Path,
    path: Path,
) {
    Canvas(modifier = modifier) {
        drawOverlay(
            drawOverlay = drawOverlay,
            rect = rect,
            drawGrid = drawGrid,
            transparentColor = transparentColor,
            overlayColor = overlayColor,
            handleColor = handleColor,
            strokeWidth = strokeWidth,
            drawHandles = drawHandles,
            handleSize = handleSize,
            middleHandleSize = middleHandleSize,
            handleStrokeWidth = handleStrokeWidth,
            middleHandleStrokeWidth = middleHandleStrokeWidth,
            selectedHandle = selectedHandle,
            selectedHandleScale = selectedHandleScale,
            pathHandles = pathHandles,
            middlePathHandles = middlePathHandles,
            selectedPathHandle = selectedPathHandle
        ) {
            drawCropPath(path)
        }
    }
}

@Composable
private fun DrawingOverlayImpl(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Float,
    drawHandles: Boolean,
    handleSize: Float,
    middleHandleSize: Float,
    handleStrokeWidth: Float,
    middleHandleStrokeWidth: Float,
    selectedHandle: TouchRegion,
    selectedHandleScale: Float,
    pathHandles: Path,
    middlePathHandles: Path,
    selectedPathHandle: Path,
    image: ImageBitmap,
) {
    Canvas(modifier = modifier) {
        drawOverlay(
            drawOverlay = drawOverlay,
            rect = rect,
            drawGrid = drawGrid,
            transparentColor = transparentColor,
            overlayColor = overlayColor,
            handleColor = handleColor,
            strokeWidth = strokeWidth,
            drawHandles = drawHandles,
            handleSize = handleSize,
            middleHandleSize = middleHandleSize,
            handleStrokeWidth = handleStrokeWidth,
            middleHandleStrokeWidth = middleHandleStrokeWidth,
            selectedHandle = selectedHandle,
            selectedHandleScale = selectedHandleScale,
            pathHandles = pathHandles,
            middlePathHandles = middlePathHandles,
            selectedPathHandle = selectedPathHandle
        ) {
            drawCropImage(rect, image)
        }
    }
}

private fun DrawScope.drawOverlay(
    drawOverlay: Boolean,
    rect: Rect,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Float,
    drawHandles: Boolean,
    handleSize: Float,
    middleHandleSize: Float,
    handleStrokeWidth: Float,
    middleHandleStrokeWidth: Float,
    selectedHandle: TouchRegion,
    selectedHandleScale: Float,
    pathHandles: Path,
    middlePathHandles: Path,
    selectedPathHandle: Path,
    drawBlock: DrawScope.() -> Unit
) {
    drawWithLayer {

        // Destination
        drawRect(transparentColor)

        // Source
        translate(left = rect.left, top = rect.top) {
            drawBlock()
        }

        if (drawGrid) {
            drawGrid(
                rect = rect,
                strokeWidth = strokeWidth,
                color = overlayColor
            )
        }
    }

    if (drawOverlay) {
        drawRect(
            topLeft = rect.topLeft,
            size = rect.size,
            color = overlayColor,
            style = Stroke(width = strokeWidth)
        )

        if (drawHandles) {
            pathHandles.apply {
                reset()
                updateHandlePath(rect, handleSize)
            }

            middlePathHandles.apply {
                reset()
                updateMiddleHandlePath(rect, middleHandleSize)
            }

            drawPath(
                path = middlePathHandles,
                color = handleColor.copy(0.9f).compositeOverSafe(Color.Black),
                style = Stroke(
                    width = middleHandleStrokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            drawPath(
                path = pathHandles,
                color = handleColor,
                style = Stroke(
                    width = handleStrokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            val selectedHandleSize = when (selectedHandle) {
                TouchRegion.TopCenter,
                TouchRegion.CenterRight,
                TouchRegion.BottomCenter,
                TouchRegion.CenterLeft -> middleHandleSize

                else -> handleSize
            }
            selectedPathHandle.apply {
                reset()
                updateSelectedHandlePath(
                    rect = rect,
                    handleSize = selectedHandleSize * selectedHandleScale,
                    selectedHandle = selectedHandle
                )
            }

            if (!selectedPathHandle.isEmpty) {
                val middleHandleSelected = selectedHandle == TouchRegion.TopCenter ||
                        selectedHandle == TouchRegion.CenterRight ||
                        selectedHandle == TouchRegion.BottomCenter ||
                        selectedHandle == TouchRegion.CenterLeft
                drawPath(
                    path = selectedPathHandle,
                    color = if (middleHandleSelected) {
                        handleColor.copy(0.9f).compositeOverSafe(Color.Black)
                    } else {
                        handleColor
                    },
                    style = Stroke(
                        width = (if (middleHandleSelected) {
                            middleHandleStrokeWidth
                        } else {
                            handleStrokeWidth
                        }) * selectedHandleScale,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

private fun DrawScope.drawCropImage(
    rect: Rect,
    imageBitmap: ImageBitmap,
    blendMode: BlendMode = BlendMode.DstOut
) {
    drawImage(
        image = imageBitmap,
        dstSize = IntSize(rect.size.width.toInt(), rect.size.height.toInt()),
        blendMode = blendMode
    )
}

private fun DrawScope.drawCropOutline(
    outline: Outline,
    blendMode: BlendMode = BlendMode.SrcOut
) {
    drawOutline(
        outline = outline,
        color = Color.Transparent,
        blendMode = blendMode
    )
}

private fun DrawScope.drawCropPath(
    path: Path,
    blendMode: BlendMode = BlendMode.SrcOut
) {
    drawPath(
        path = path,
        color = Color.Transparent,
        blendMode = blendMode
    )
}

private fun Path.updateHandlePath(
    rect: Rect,
    handleSize: Float
) {
    if (rect != Rect.Zero) {
        // Top left lines
        moveTo(rect.topLeft.x, rect.topLeft.y + handleSize)
        lineTo(rect.topLeft.x, rect.topLeft.y)
        lineTo(rect.topLeft.x + handleSize, rect.topLeft.y)

        // Top right lines
        moveTo(rect.topRight.x - handleSize, rect.topRight.y)
        lineTo(rect.topRight.x, rect.topRight.y)
        lineTo(rect.topRight.x, rect.topRight.y + handleSize)

        // Bottom right lines
        moveTo(rect.bottomRight.x, rect.bottomRight.y - handleSize)
        lineTo(rect.bottomRight.x, rect.bottomRight.y)
        lineTo(rect.bottomRight.x - handleSize, rect.bottomRight.y)

        // Bottom left lines
        moveTo(rect.bottomLeft.x + handleSize, rect.bottomLeft.y)
        lineTo(rect.bottomLeft.x, rect.bottomLeft.y)
        lineTo(rect.bottomLeft.x, rect.bottomLeft.y - handleSize)
    }
}


private fun Path.updateMiddleHandlePath(
    rect: Rect,
    handleSize: Float
) {
    if (rect != Rect.Zero) {
        // Top middle lines
        moveTo(rect.topCenter.x - handleSize / 2, rect.topCenter.y)
        lineTo(rect.topCenter.x + handleSize / 2, rect.topCenter.y)

        // Right middle lines
        moveTo(rect.centerRight.x, rect.centerRight.y - handleSize / 2)
        lineTo(rect.centerRight.x, rect.centerRight.y + handleSize / 2)

        // Bottom middle lines
        moveTo(rect.bottomCenter.x - handleSize / 2, rect.bottomCenter.y)
        lineTo(rect.bottomCenter.x + handleSize / 2, rect.bottomCenter.y)

        // Left middle lines
        moveTo(rect.centerLeft.x, rect.centerLeft.y - handleSize / 2)
        lineTo(rect.centerLeft.x, rect.centerLeft.y + handleSize / 2)
    }
}

private fun Path.updateSelectedHandlePath(
    rect: Rect,
    handleSize: Float,
    selectedHandle: TouchRegion
) {
    if (rect == Rect.Zero) return

    when (selectedHandle) {
        TouchRegion.TopLeft -> {
            moveTo(rect.left, rect.top + handleSize)
            lineTo(rect.left, rect.top)
            lineTo(rect.left + handleSize, rect.top)
        }

        TouchRegion.TopRight -> {
            moveTo(rect.right - handleSize, rect.top)
            lineTo(rect.right, rect.top)
            lineTo(rect.right, rect.top + handleSize)
        }

        TouchRegion.BottomRight -> {
            moveTo(rect.right, rect.bottom - handleSize)
            lineTo(rect.right, rect.bottom)
            lineTo(rect.right - handleSize, rect.bottom)
        }

        TouchRegion.BottomLeft -> {
            moveTo(rect.left + handleSize, rect.bottom)
            lineTo(rect.left, rect.bottom)
            lineTo(rect.left, rect.bottom - handleSize)
        }

        TouchRegion.TopCenter -> {
            moveTo(rect.topCenter.x - handleSize / 2, rect.top)
            lineTo(rect.topCenter.x + handleSize / 2, rect.top)
        }

        TouchRegion.CenterRight -> {
            moveTo(rect.right, rect.centerRight.y - handleSize / 2)
            lineTo(rect.right, rect.centerRight.y + handleSize / 2)
        }

        TouchRegion.BottomCenter -> {
            moveTo(rect.bottomCenter.x - handleSize / 2, rect.bottom)
            lineTo(rect.bottomCenter.x + handleSize / 2, rect.bottom)
        }

        TouchRegion.CenterLeft -> {
            moveTo(rect.left, rect.centerLeft.y - handleSize / 2)
            lineTo(rect.left, rect.centerLeft.y + handleSize / 2)
        }

        TouchRegion.Inside,
        TouchRegion.None -> Unit
    }
}

private val CornerHandleSize = 20.dp
private val CornerHandleStrokeWidth = 4.dp
private val MiddleHandleStrokeWidth = 3.5.dp