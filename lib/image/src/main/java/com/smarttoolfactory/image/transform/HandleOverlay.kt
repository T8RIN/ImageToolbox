package com.smarttoolfactory.image.transform

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.image.util.drawBorderCircle

/**
 * Overlay that draws border and handles over
 */
@Composable
internal fun HandleOverlay(
    modifier: Modifier,
    radius: Float,
    rectDraw: Rect,
    handlePlacement: HandlePlacement = HandlePlacement.Corner
) {

    val transition: InfiniteTransition = rememberInfiniteTransition()

    // Infinite phase animation for PathEffect
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(20f, 20f),
        phase = phase
    )

    // This rect what we get after offsetting inside padding area to draw actual composable
    val rect = Rect(
        topLeft = Offset(
            rectDraw.topLeft.x + radius,
            rectDraw.topLeft.y + radius
        ),
        bottomRight = Offset(
            rectDraw.bottomRight.x - radius,
            rectDraw.bottomRight.y - radius
        )
    )

    Canvas(modifier = modifier) {


        drawRect(
            topLeft = rect.topLeft,
            size = rect.size,
            color = Color.White,
            style = Stroke(
                width = 2.dp.toPx()
            )
        )
        drawRect(
            topLeft = rect.topLeft,
            size = rect.size,
            color = Color.Black,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = pathEffect
            )
        )

        if (handlePlacement == HandlePlacement.Corner || handlePlacement == HandlePlacement.Both) {
            drawBorderCircle(
                radius = radius,
                center = rect.topLeft
            )
            drawBorderCircle(
                radius = radius,
                center = rect.topRight
            )
            drawBorderCircle(
                radius = radius,
                center = rect.bottomLeft
            )
            drawBorderCircle(
                radius = radius,
                center = rect.bottomRight
            )
        }

        if (handlePlacement == HandlePlacement.Side || handlePlacement == HandlePlacement.Both) {
            drawBorderCircle(
                radius = radius,
                center = rect.centerLeft
            )
            drawBorderCircle(
                radius = radius,
                center = rect.topCenter
            )
            drawBorderCircle(
                radius = radius,
                center = rect.centerRight
            )
            drawBorderCircle(
                radius = radius,
                center = rect.bottomCenter
            )
        }
    }
}
