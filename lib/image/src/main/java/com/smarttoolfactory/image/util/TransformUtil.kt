package com.smarttoolfactory.image.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.image.transform.HandlePlacement
import com.smarttoolfactory.image.transform.TouchRegion
import kotlin.math.cos
import kotlin.math.sin

/**
 * Returns region of this [position] is at inside [rect] using threshold to determine distance
 * from corners or sides of [rect]
 * @param position position in or outside of [rect]
 * @param rect to determine which region is touched
 * @param threshold is distance from any corner or side to [position]
 */
internal fun getTouchRegion(
    position: Offset,
    rect: Rect,
    threshold: Float,
    handlePlacement: HandlePlacement
): TouchRegion {

    // Instead of using square check for power of 2 of threshold
    val squareOfThreshold = threshold * threshold

    return when (handlePlacement) {
        HandlePlacement.Corner -> {
            getCornerTouchRegion(position, rect, squareOfThreshold)
        }

        HandlePlacement.Side -> {
            getSideTouchRegion(position, rect, squareOfThreshold)
        }

        else -> {
            val touchRegion = getCornerTouchRegion(position, rect, squareOfThreshold)
            if (touchRegion == TouchRegion.Inside) {
                getSideTouchRegion(position, rect, squareOfThreshold)
            } else {
                touchRegion
            }
        }
    }
}

private fun getCornerTouchRegion(
    position: Offset,
    rect: Rect,
    squareOfThreshold: Float
): TouchRegion {
    return when {

        inDistanceSquared(
            position,
            rect.topLeft,
            squareOfThreshold
        ) -> TouchRegion.TopLeft

        inDistanceSquared(
            position,
            rect.topRight,
            squareOfThreshold
        ) -> TouchRegion.TopRight

        inDistanceSquared(
            position,
            rect.bottomLeft,
            squareOfThreshold
        ) -> TouchRegion.BottomLeft

        inDistanceSquared(
            position,
            rect.bottomRight,
            squareOfThreshold
        ) -> TouchRegion.BottomRight

        rect.contains(offset = position) -> TouchRegion.Inside
        else -> TouchRegion.None
    }
}

/**
 * Returns how far user touched to corner or center of sides of the screen. [TouchRegion]
 * where user exactly has touched is already passed to this function. For instance user
 * touched top left then this function returns distance to top left from user's position so
 * we can add an offset to not jump edge to position user touched.
 */
internal fun getDistanceToEdgeFromTouch(
    touchRegion: TouchRegion,
    rect: Rect,
    touchPosition: Offset
) = when (touchRegion) {
    // Corners
    TouchRegion.TopLeft -> {
        rect.topLeft - touchPosition
    }

    TouchRegion.TopRight -> {
        rect.topRight - touchPosition
    }

    TouchRegion.BottomLeft -> {
        rect.bottomLeft - touchPosition
    }

    TouchRegion.BottomRight -> {
        rect.bottomRight - touchPosition
    }
    // Sides
    TouchRegion.CenterLeft -> {
        rect.centerLeft - touchPosition
    }

    TouchRegion.TopCenter -> {
        rect.topCenter - touchPosition
    }

    TouchRegion.CenterRight -> {
        rect.centerRight - touchPosition
    }

    TouchRegion.BottomCenter -> {
        rect.bottomCenter - touchPosition
    }

    else -> {
        Offset.Zero
    }
}

private fun getSideTouchRegion(
    position: Offset,
    rect: Rect,
    squareOfThreshold: Float
): TouchRegion {
    return when {

        inDistanceSquared(
            position,
            rect.centerLeft,
            squareOfThreshold
        ) -> TouchRegion.CenterLeft

        inDistanceSquared(
            position,
            rect.topCenter,
            squareOfThreshold
        ) -> TouchRegion.TopCenter

        inDistanceSquared(
            position,
            rect.centerRight,
            squareOfThreshold
        ) -> TouchRegion.CenterRight

        inDistanceSquared(
            position,
            rect.bottomCenter,
            squareOfThreshold
        ) -> TouchRegion.BottomCenter

        rect.contains(offset = position) -> TouchRegion.Inside
        else -> TouchRegion.None
    }
}

/**
 * Check if [target] which is power of 2 of actual value to not use square to make this
 * operation cheaper
 */
internal fun inDistanceSquared(offset1: Offset, offset2: Offset, target: Float): Boolean {
    val x1 = offset1.x
    val y1 = offset1.y

    val x2 = offset2.x
    val y2 = offset2.y

    val distance = ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
    return distance < target
}

/**
 *  Draw one transparent and one white circle for border for handle
 */
internal fun DrawScope.drawBorderCircle(
    radius: Float,
    center: Offset
) {
    drawCircle(color = Color.White.copy(alpha = .7f), radius = radius, center = center)
    drawCircle(color = Color.White, radius = radius, center = center, style = Stroke(1.dp.toPx()))
}

/**
 * Rotates the given offset around the origin by the given angle in degrees.
 *
 * A positive angle indicates a counterclockwise rotation around the right-handed 2D Cartesian
 * coordinate system.
 *
 * See: [Rotation matrix](https://en.wikipedia.org/wiki/Rotation_matrix)
 */
fun Offset.rotateBy(
    angle: Float
): Offset {
    val angleInRadians = ROTATION_CONST * angle
    val newX = x * cos(angleInRadians) - y * sin(angleInRadians)
    val newY = x * sin(angleInRadians) + y * cos(angleInRadians)
    return Offset(newX, newY)
}

internal const val ROTATION_CONST = (Math.PI / 180f).toFloat()
