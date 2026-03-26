package com.smarttoolfactory.cropper.draw

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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.smarttoolfactory.cropper.model.CropImageMask
import com.smarttoolfactory.cropper.model.CropOutline
import com.smarttoolfactory.cropper.model.CropPath
import com.smarttoolfactory.cropper.model.CropShape
import com.smarttoolfactory.cropper.util.drawGrid
import com.smarttoolfactory.cropper.util.drawWithLayer
import com.smarttoolfactory.cropper.util.scaleAndTranslatePath

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
    handleSize: Float,
    middleHandleSize: Float,
) {
    val density = LocalDensity.current
    val layoutDirection: LayoutDirection = LocalLayoutDirection.current

    val strokeWidthPx = LocalDensity.current.run { strokeWidth.toPx() }

    val pathHandles = remember {
        Path()
    }

    val middlePathHandles = remember {
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
                handleSize = handleSize,
                middleHandleSize = middleHandleSize,
                pathHandles = pathHandles,
                middlePathHandles = middlePathHandles,
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
                handleSize = handleSize,
                middleHandleSize = middleHandleSize,
                pathHandles = pathHandles,
                middlePathHandles = middlePathHandles,
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
                handleSize = handleSize,
                middleHandleSize = middleHandleSize,
                pathHandles = pathHandles,
                middlePathHandles = middlePathHandles,
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
    pathHandles: Path,
    middlePathHandles: Path,
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
            pathHandles = pathHandles,
            middlePathHandles = middlePathHandles
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
    pathHandles: Path,
    middlePathHandles: Path,
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
            pathHandles = pathHandles,
            middlePathHandles = middlePathHandles
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
    pathHandles: Path,
    middlePathHandles: Path,
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
            pathHandles = pathHandles,
            middlePathHandles = middlePathHandles
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
    pathHandles: Path,
    middlePathHandles: Path,
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
                color = handleColor.copy(0.9f).compositeOver(Color.Black),
                style = Stroke(
                    width = strokeWidth * 4,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            drawPath(
                path = pathHandles,
                color = handleColor,
                style = Stroke(
                    width = strokeWidth * 4,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
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