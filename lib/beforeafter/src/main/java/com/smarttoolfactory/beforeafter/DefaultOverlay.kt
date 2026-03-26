package com.smarttoolfactory.beforeafter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Default overlay for [BeforeAfterImage] and [BeforeAfterLayout] that draws line and
 * thumb with properties provided.
 *
 * @param width of the [BeforeAfterImage] or [BeforeAfterLayout]. You should get width from
 * scope of these Composables and pass to calculate bounds correctly
 * @param height of the [BeforeAfterImage] or [BeforeAfterLayout]. You should get height from
 * scope of these Composables and pass to calculate bounds correctly
 * @param position current position or progress of before/after
 */
@Composable
internal fun DefaultOverlay(
    width: Dp,
    height: Dp,
    position: Offset,
    overlayStyle: OverlayStyle
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Ltr
    ) {
        val verticalThumbMove = overlayStyle.verticalThumbMove
        val dividerWidth = overlayStyle.dividerWidth

        val dividerColor =
            overlayStyle.dividerColor.takeOrElse { MaterialTheme.colorScheme.primary }
        val secondDividerColor =
            overlayStyle.secondDividerColor.takeOrElse { MaterialTheme.colorScheme.primaryContainer }
        val thumbBackgroundColor =
            overlayStyle.thumbBackgroundColor.takeOrElse { MaterialTheme.colorScheme.primary }
        val thumbTintColor =
            overlayStyle.thumbTintColor.takeOrElse { MaterialTheme.colorScheme.primaryContainer }

        val thumbShape = overlayStyle.thumbShape
        val thumbElevation = overlayStyle.thumbElevation
        val thumbResource = overlayStyle.thumbResource
        val thumbHeight = overlayStyle.thumbHeight
        val thumbWidth = overlayStyle.thumbWidth
        val thumbPositionPercent = overlayStyle.thumbPositionPercent


        var thumbPosX = position.x
        var thumbPosY = position.y

        val linePosition: Float

        val density = LocalDensity.current

        with(density) {
            val thumbRadius = (maxOf(thumbHeight, thumbWidth) / 2).toPx()
            val imageWidthInPx = width.toPx()
            val imageHeightInPx = height.toPx()

            val horizontalOffset = imageWidthInPx / 2
            val verticalOffset = imageHeightInPx / 2

            linePosition = thumbPosX.coerceIn(0f, imageWidthInPx)
            thumbPosX = linePosition - horizontalOffset

            thumbPosY = if (verticalThumbMove) {
                (thumbPosY - verticalOffset)
                    .coerceIn(
                        -verticalOffset + thumbRadius,
                        verticalOffset - thumbRadius
                    )
            } else {
                ((imageHeightInPx * thumbPositionPercent / 100f - thumbRadius) - verticalOffset)
            }
        }

        Box(
            modifier = Modifier.size(width, height),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {

                drawLine(
                    color = dividerColor,
                    strokeWidth = dividerWidth.toPx(),
                    start = Offset(linePosition, 0f),
                    end = Offset(linePosition, size.height)
                )

                drawLine(
                    color = secondDividerColor,
                    strokeWidth = dividerWidth.toPx(),
                    start = Offset(linePosition, 0f),
                    end = Offset(linePosition, size.height),
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(
                            overlayStyle.dash.toPx(),
                            overlayStyle.gap.toPx()
                        )
                    )
                )

            }

            Icon(
                imageVector = thumbResource,
                contentDescription = "compare thumb",
                tint = thumbTintColor,
                modifier = Modifier
                    .offset {
                        IntOffset(thumbPosX.toInt(), thumbPosY.toInt())
                    }
                    .shadow(thumbElevation, thumbShape)
                    .background(thumbBackgroundColor)
                    .size(
                        width = thumbWidth,
                        height = thumbHeight,
                    )
                    .rotate(overlayStyle.iconRotation)
                    .scale(overlayStyle.iconScale)
            )
        }
    }
}

/**
 * Values for styling [DefaultOverlay]
 *  @param verticalThumbMove when true thumb can move vertically based on user touch
 * @param dividerColor color if divider line
 * @param dividerWidth width if divider line
 * @param thumbBackgroundColor background color of thumb [Icon]
 * @param thumbTintColor tint color of thumb [Icon]
 * @param thumbShape shape of thumb [Icon]
 * @param thumbElevation elevation of thumb [Icon]
 * @param thumbResource drawable resource that should be used with thumb
 * @param thumbSize size of the thumb in dp
 * @param thumbPositionPercent vertical position of thumb if [verticalThumbMove] is false
 * It's between [0f-100f] to set thumb's vertical position in layout
 */
@Immutable
class OverlayStyle(
    val dividerColor: Color = Color.Unspecified,
    val secondDividerColor: Color = Color.Unspecified,
    val dash: Dp = 24.dp,
    val gap: Dp = 24.dp,
    val dividerWidth: Dp = 2.dp,
    val verticalThumbMove: Boolean = true,
    val thumbBackgroundColor: Color = Color.Unspecified,
    val thumbTintColor: Color = Color.Unspecified,
    val thumbShape: Shape = CircleShape,
    val thumbElevation: Dp = 2.dp,
    val thumbResource: ImageVector = Icons.Rounded.DragHandle,
    val iconRotation: Float = 90f,
    val iconScale: Float = 1.2f,
    val thumbHeight: Dp = 36.dp,
    val thumbWidth: Dp = 18.dp,
    val thumbPositionPercent: Float = 85f,
)
