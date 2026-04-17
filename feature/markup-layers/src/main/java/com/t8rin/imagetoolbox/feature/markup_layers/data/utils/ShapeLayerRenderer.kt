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

package com.t8rin.imagetoolbox.feature.markup_layers.data.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.ShapeMode
import com.t8rin.imagetoolbox.feature.markup_layers.domain.arrowAngle
import com.t8rin.imagetoolbox.feature.markup_layers.domain.arrowSizeScale
import com.t8rin.imagetoolbox.feature.markup_layers.domain.cornerRadius
import com.t8rin.imagetoolbox.feature.markup_layers.domain.innerRadiusRatio
import com.t8rin.imagetoolbox.feature.markup_layers.domain.isFilledShapeMode
import com.t8rin.imagetoolbox.feature.markup_layers.domain.isOutlinedShapeMode
import com.t8rin.imagetoolbox.feature.markup_layers.domain.isRegular
import com.t8rin.imagetoolbox.feature.markup_layers.domain.outlinedFillColorInt
import com.t8rin.imagetoolbox.feature.markup_layers.domain.rotationDegrees
import com.t8rin.imagetoolbox.feature.markup_layers.domain.usesStrokeWidth
import com.t8rin.imagetoolbox.feature.markup_layers.domain.vertices
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

internal data class ShapeLayerRenderData(
    val width: Float,
    val height: Float,
    val contentLeft: Float,
    val contentTop: Float,
    val contentWidth: Float,
    val contentHeight: Float,
    val shapeWidth: Float,
    val shapeHeight: Float,
    val shapeTranslateX: Float,
    val shapeTranslateY: Float
)

internal fun resolveShapeLayerRenderData(
    type: LayerType.Shape,
    referenceSize: Float,
    contentSize: IntegerSize = IntegerSize.Zero,
    maxWidth: Float = Float.POSITIVE_INFINITY,
    maxHeight: Float = Float.POSITIVE_INFINITY
): ShapeLayerRenderData {
    val shadowPadding = calculateShadowPadding(type.shadow)
    val horizontalShadowPadding = shadowPadding.leftPx + shadowPadding.rightPx
    val verticalShadowPadding = shadowPadding.topPx + shadowPadding.bottomPx
    val desiredShapeWidth = (referenceSize * type.widthRatio).coerceAtLeast(1f)
    val desiredShapeHeight = (referenceSize * type.heightRatio).coerceAtLeast(1f)
    val constrainedSize = contentSize.takeIf { it.width > 0 && it.height > 0 }
    val availableContentWidth = constrainedSize?.width?.toFloat()?.let {
        (it - horizontalShadowPadding).coerceAtLeast(1f)
    } ?: maxWidth
        .takeIf(Float::isFinite)
        ?.let { (it - horizontalShadowPadding).coerceAtLeast(1f) }
    ?: Float.POSITIVE_INFINITY
    val availableContentHeight = constrainedSize?.height?.toFloat()?.let {
        (it - verticalShadowPadding).coerceAtLeast(1f)
    } ?: maxHeight
        .takeIf(Float::isFinite)
        ?.let { (it - verticalShadowPadding).coerceAtLeast(1f) }
    ?: Float.POSITIVE_INFINITY
    val desiredPlacement = resolveShapePlacement(
        type = type,
        shapeWidth = desiredShapeWidth,
        shapeHeight = desiredShapeHeight
    )
    val fitScale = min(
        1f,
        min(
            availableContentWidth / desiredPlacement.contentWidth,
            availableContentHeight / desiredPlacement.contentHeight
        )
    ).coerceAtLeast(0.01f)
    val shapeWidth = desiredShapeWidth * fitScale
    val shapeHeight = desiredShapeHeight * fitScale
    val placement = if (fitScale == 1f) desiredPlacement else resolveShapePlacement(
        type = type,
        shapeWidth = shapeWidth,
        shapeHeight = shapeHeight
    )
    val resolvedContentWidth = constrainedSize?.let {
        (it.width.toFloat() - horizontalShadowPadding).coerceAtLeast(1f)
    } ?: placement.contentWidth
    val resolvedContentHeight = constrainedSize?.let {
        (it.height.toFloat() - verticalShadowPadding).coerceAtLeast(1f)
    } ?: placement.contentHeight
    val extraContentLeft = ((resolvedContentWidth - placement.contentWidth) / 2f).coerceAtLeast(0f)
    val extraContentTop = ((resolvedContentHeight - placement.contentHeight) / 2f).coerceAtLeast(0f)
    val totalWidth = constrainedSize?.width?.toFloat()?.coerceAtLeast(horizontalShadowPadding + 1f)
        ?: (placement.contentWidth + horizontalShadowPadding)
    val totalHeight = constrainedSize?.height?.toFloat()?.coerceAtLeast(verticalShadowPadding + 1f)
        ?: (placement.contentHeight + verticalShadowPadding)

    return ShapeLayerRenderData(
        width = totalWidth,
        height = totalHeight,
        contentLeft = shadowPadding.leftPx,
        contentTop = shadowPadding.topPx,
        contentWidth = resolvedContentWidth,
        contentHeight = resolvedContentHeight,
        shapeWidth = shapeWidth,
        shapeHeight = shapeHeight,
        shapeTranslateX = placement.shapeTranslateX + extraContentLeft,
        shapeTranslateY = placement.shapeTranslateY + extraContentTop
    )
}

internal fun buildShapeShadowRenderData(
    type: LayerType.Shape,
    data: ShapeLayerRenderData,
    rasterScale: Float = 1f
): PictureShadowRenderData? {
    if (type.shadow == null) return null

    val scaledData = data.scaleContent(rasterScale)
    val scaledWidth = scaledData.contentWidth.roundToInt().coerceAtLeast(1)
    val scaledHeight = scaledData.contentHeight.roundToInt().coerceAtLeast(1)
    val bitmap = renderShapeBitmap(
        type = type.copy(strokeWidth = type.strokeWidth * rasterScale),
        data = scaledData,
        width = scaledWidth,
        height = scaledHeight
    )

    return buildPictureShadowRenderData(
        sourceBitmap = bitmap,
        shadow = type.shadow,
        targetWidth = data.contentWidth,
        targetHeight = data.contentHeight,
        cornerRadiusPercent = 0,
        rasterScale = rasterScale
    )
}

internal fun DrawScope.drawShapeLayer(
    type: LayerType.Shape,
    data: ShapeLayerRenderData
) {
    val path = buildPlacedShapePath(
        type = type,
        data = data
    )

    if (type.shapeMode.isFilledShapeMode()) {
        drawPath(
            path = path,
            color = type.color.toColor(),
            style = Fill
        )
        return
    }

    if (type.shapeMode.isOutlinedShapeMode()) {
        type.shapeMode.outlinedFillColorInt()?.let {
            drawPath(
                path = path,
                color = Color(it),
                style = Fill
            )
        }
    }

    drawPath(
        path = path,
        color = type.color.toColor(),
        style = Stroke(
            width = type.strokeWidth.coerceAtLeast(1f),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

internal fun Canvas.drawShapeLayer(
    type: LayerType.Shape,
    data: ShapeLayerRenderData
) {
    val path = buildPlacedShapePath(
        type = type,
        data = data
    ).asAndroidPath()

    if (type.shapeMode.isFilledShapeMode()) {
        drawPath(
            path,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = type.color
                style = Paint.Style.FILL
            }
        )
        return
    }

    if (type.shapeMode.isOutlinedShapeMode()) {
        type.shapeMode.outlinedFillColorInt()?.let {
            drawPath(
                path,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = it
                    style = Paint.Style.FILL
                }
            )
        }
    }

    drawPath(
        path,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = type.color
            style = Paint.Style.STROKE
            strokeWidth = type.strokeWidth.coerceAtLeast(1f)
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    )
}

private fun renderShapeBitmap(
    type: LayerType.Shape,
    data: ShapeLayerRenderData,
    width: Int,
    height: Int
): Bitmap = createBitmap(
    width = width.coerceAtLeast(1),
    height = height.coerceAtLeast(1)
).apply {
    Canvas(this).drawShapeLayer(
        type = type,
        data = data
    )
}

private data class ShapePlacement(
    val contentWidth: Float,
    val contentHeight: Float,
    val shapeTranslateX: Float,
    val shapeTranslateY: Float
)

private fun resolveShapePlacement(
    type: LayerType.Shape,
    shapeWidth: Float,
    shapeHeight: Float
): ShapePlacement {
    val path = buildShapePath(
        type = type,
        width = shapeWidth,
        height = shapeHeight
    )
    val bounds = path.getBounds()
    val drawPadding = if (type.shapeMode.usesStrokeWidth()) {
        type.strokeWidth.coerceAtLeast(1f) / 2f + 1f
    } else {
        1f
    }
    val visibleLeft = bounds.left - drawPadding
    val visibleTop = bounds.top - drawPadding
    val visibleRight = bounds.right + drawPadding
    val visibleBottom = bounds.bottom + drawPadding

    return ShapePlacement(
        contentWidth = (visibleRight - visibleLeft).coerceAtLeast(1f),
        contentHeight = (visibleBottom - visibleTop).coerceAtLeast(1f),
        shapeTranslateX = -visibleLeft,
        shapeTranslateY = -visibleTop
    )
}

private fun buildPlacedShapePath(
    type: LayerType.Shape,
    data: ShapeLayerRenderData
): Path {
    val path = buildShapePath(
        type = type,
        width = data.shapeWidth,
        height = data.shapeHeight
    )
    if (data.shapeTranslateX == 0f && data.shapeTranslateY == 0f) return path

    val matrix = Matrix().apply {
        setTranslate(data.shapeTranslateX, data.shapeTranslateY)
    }
    return path.asAndroidPath().apply { transform(matrix) }.asComposePath()
}

private fun buildShapePath(
    type: LayerType.Shape,
    width: Float,
    height: Float
): Path {
    val strokeInset = if (type.shapeMode.usesStrokeWidth()) {
        type.strokeWidth / 2f + 1f
    } else {
        1f
    }
    val left = strokeInset.coerceAtMost(width / 2f)
    val top = strokeInset.coerceAtMost(height / 2f)
    val right = max(left + 1f, width - strokeInset)
    val bottom = max(top + 1f, height - strokeInset)

    return when (val mode = type.shapeMode) {
        is ShapeMode.Rect,
        is ShapeMode.OutlinedRect -> {
            buildRotatedRoundRectPath(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                rotationDegrees = mode.rotationDegrees(),
                cornerRadius = mode.cornerRadius()
            )
        }

        ShapeMode.Oval,
        is ShapeMode.OutlinedOval -> Path().apply {
            addOval(Rect(left, top, right, bottom))
        }

        ShapeMode.Triangle,
        is ShapeMode.OutlinedTriangle -> Path().apply {
            moveTo((left + right) / 2f, top)
            lineTo(right, bottom)
            lineTo(left, bottom)
            close()
        }

        is ShapeMode.Polygon,
        is ShapeMode.OutlinedPolygon -> {
            buildPolygonPath(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                vertices = mode.vertices(),
                rotationDegrees = mode.rotationDegrees(),
                isRegular = mode.isRegular()
            )
        }

        is ShapeMode.Star,
        is ShapeMode.OutlinedStar -> {
            buildStarPath(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                vertices = mode.vertices(),
                rotationDegrees = mode.rotationDegrees(),
                innerRadiusRatio = mode.innerRadiusRatio(),
                isRegular = mode.isRegular()
            )
        }

        is ShapeMode.Arrow -> {
            buildFilledArrowPath(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                doubleHeaded = false,
                sizeScale = mode.arrowSizeScale(),
                angle = mode.arrowAngle()
            )
        }

        is ShapeMode.DoubleArrow -> {
            buildFilledArrowPath(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                doubleHeaded = true,
                sizeScale = mode.arrowSizeScale(),
                angle = mode.arrowAngle()
            )
        }

        ShapeMode.Line,
        is ShapeMode.LineArrow,
        is ShapeMode.DoubleLineArrow -> {
            buildLineArrowPath(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                drawStartArrow = mode is ShapeMode.DoubleLineArrow,
                drawEndArrow = mode is ShapeMode.LineArrow || mode is ShapeMode.DoubleLineArrow,
                sizeScale = mode.arrowSizeScale(),
                angle = mode.arrowAngle()
            )
        }
    }
}

private fun buildRotatedRoundRectPath(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    rotationDegrees: Int,
    cornerRadius: Float
): Path {
    val width = right - left
    val height = bottom - top
    val radius = min(width, height) * cornerRadius.coerceIn(0f, 0.5f)

    val path = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(left, top, right, bottom),
                radiusX = radius,
                radiusY = radius
            )
        )
    }
    if (rotationDegrees == 0) return path

    val matrix = Matrix().apply {
        setRotate(
            rotationDegrees.toFloat(),
            (left + right) / 2f,
            (top + bottom) / 2f
        )
    }
    return path.asAndroidPath().apply { transform(matrix) }.asComposePath()
}

private fun buildPolygonPath(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    vertices: Int,
    rotationDegrees: Int,
    isRegular: Boolean
): Path {
    val centerX = (left + right) / 2f
    val centerY = (top + bottom) / 2f
    val width = right - left
    val height = bottom - top
    val safeVertices = vertices.coerceAtLeast(3)

    return Path().apply {
        if (isRegular) {
            val radius = min(width, height) / 2f
            val step = 360f / safeVertices
            val startAngle = rotationDegrees - 90f

            repeat(safeVertices) { index ->
                val angle = Math.toRadians((startAngle + index * step).toDouble())
                val x = centerX + radius * cos(angle).toFloat()
                val y = centerY + radius * sin(angle).toFloat()
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        } else {
            repeat(safeVertices) { index ->
                val angle = Math.toRadians(
                    (rotationDegrees - 90f + index * (360f / safeVertices)).toDouble()
                )
                val x = centerX + width / 2f * cos(angle).toFloat()
                val y = centerY + height / 2f * sin(angle).toFloat()
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        close()
    }
}

private fun buildStarPath(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    vertices: Int,
    rotationDegrees: Int,
    innerRadiusRatio: Float,
    isRegular: Boolean
): Path {
    val safeVertices = vertices.coerceAtLeast(3)
    val centerX = (left + right) / 2f
    val centerY = (top + bottom) / 2f
    val width = right - left
    val height = bottom - top
    val safeInnerRadiusRatio = innerRadiusRatio.coerceIn(0f, 1f)

    return Path().apply {
        if (isRegular) {
            val outerRadius = min(width, height) / 2f
            val innerRadius = outerRadius * safeInnerRadiusRatio
            val step = 360f / (safeVertices * 2)
            val startAngle = rotationDegrees - 90f

            repeat(safeVertices * 2) { index ->
                val radius = if (index % 2 == 0) outerRadius else innerRadius
                val angle = Math.toRadians((startAngle + index * step).toDouble())
                val x = centerX + radius * cos(angle).toFloat()
                val y = centerY + radius * sin(angle).toFloat()
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        } else {
            val step = 360f / (safeVertices * 2)
            val startAngle = rotationDegrees - 90f

            repeat(safeVertices * 2) { index ->
                val radiusX = if (index % 2 == 0) width / 2f else width / 2f * safeInnerRadiusRatio
                val radiusY =
                    if (index % 2 == 0) height / 2f else height / 2f * safeInnerRadiusRatio
                val angle = Math.toRadians((startAngle + index * step).toDouble())
                val x = centerX + radiusX * cos(angle).toFloat()
                val y = centerY + radiusY * sin(angle).toFloat()
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        close()
    }
}

private fun buildLineArrowPath(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    drawStartArrow: Boolean,
    drawEndArrow: Boolean,
    sizeScale: Float,
    angle: Float
): Path {
    val centerY = (top + bottom) / 2f
    val length = right - left
    val headLength = min(
        length / if (drawStartArrow && drawEndArrow) 3f else 2f,
        max(bottom - top, 1f) * sizeScale.coerceAtLeast(0.5f)
    ).coerceAtLeast(1f)

    return Path().apply {
        moveTo(left, centerY)
        lineTo(right, centerY)

        if (drawEndArrow) {
            addArrowHead(
                tip = Offset(right, centerY),
                directionAngleDegrees = 0f,
                headLength = headLength,
                angle = angle
            )
        }
        if (drawStartArrow) {
            addArrowHead(
                tip = Offset(left, centerY),
                directionAngleDegrees = 180f,
                headLength = headLength,
                angle = angle
            )
        }
    }
}

private fun Path.addArrowHead(
    tip: Offset,
    directionAngleDegrees: Float,
    headLength: Float,
    angle: Float
) {
    val first = directionAngleDegrees + angle
    val second = directionAngleDegrees - angle

    moveTo(tip.x, tip.y)
    lineTo(
        tip.x + cos(Math.toRadians(first.toDouble())).toFloat() * headLength,
        tip.y + sin(Math.toRadians(first.toDouble())).toFloat() * headLength
    )
    moveTo(tip.x, tip.y)
    lineTo(
        tip.x + cos(Math.toRadians(second.toDouble())).toFloat() * headLength,
        tip.y + sin(Math.toRadians(second.toDouble())).toFloat() * headLength
    )
}

private fun buildFilledArrowPath(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    doubleHeaded: Boolean,
    sizeScale: Float,
    angle: Float
): Path {
    val width = right - left
    val height = bottom - top
    val centerY = (top + bottom) / 2f
    val angleRadians = Math.toRadians(angle.coerceIn(100f, 175f).toDouble())
    val normalizedScale = (sizeScale.coerceIn(0.5f, 8f) - 0.5f) / 7.5f
    val desiredHeadLength = width * (0.22f + normalizedScale * 0.28f)
    val headBackOffset = abs(cos(angleRadians)).toFloat() * desiredHeadLength
    val tipHalfHeight = min(height / 2f, abs(sin(angleRadians)).toFloat() * desiredHeadLength)
    val shaftHalfHeight = min(height * 0.24f, tipHalfHeight * 0.55f).coerceAtLeast(height * 0.12f)

    return Path().apply {
        if (doubleHeaded) {
            val safeHeadOffset = min(headBackOffset, width / 2.5f)
            val leftBackX = left + safeHeadOffset
            val rightBackX = right - safeHeadOffset

            moveTo(left, centerY)
            lineTo(leftBackX, centerY - tipHalfHeight)
            lineTo(leftBackX, centerY - shaftHalfHeight)
            lineTo(rightBackX, centerY - shaftHalfHeight)
            lineTo(rightBackX, centerY - tipHalfHeight)
            lineTo(right, centerY)
            lineTo(rightBackX, centerY + tipHalfHeight)
            lineTo(rightBackX, centerY + shaftHalfHeight)
            lineTo(leftBackX, centerY + shaftHalfHeight)
            lineTo(leftBackX, centerY + tipHalfHeight)
        } else {
            val safeHeadOffset = min(headBackOffset, width * 0.6f)
            val backX = right - safeHeadOffset

            moveTo(left, centerY - shaftHalfHeight)
            lineTo(backX, centerY - shaftHalfHeight)
            lineTo(backX, centerY - tipHalfHeight)
            lineTo(right, centerY)
            lineTo(backX, centerY + tipHalfHeight)
            lineTo(backX, centerY + shaftHalfHeight)
            lineTo(left, centerY + shaftHalfHeight)
        }
        close()
    }
}

private fun ShapeLayerRenderData.scaleContent(
    scale: Float
): ShapeLayerRenderData = copy(
    width = width * scale,
    height = height * scale,
    contentLeft = contentLeft * scale,
    contentTop = contentTop * scale,
    contentWidth = contentWidth * scale,
    contentHeight = contentHeight * scale,
    shapeWidth = shapeWidth * scale,
    shapeHeight = shapeHeight * scale,
    shapeTranslateX = shapeTranslateX * scale,
    shapeTranslateY = shapeTranslateY * scale
)
