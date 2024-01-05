package com.smarttoolfactory.image.transform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Composable that changes scale of its content from handles, translates its position
 * when dragged inside bounds
 *
 * @param modifier is modifier of Composable used as [content]

 * @param enabled flag for enabling morph operations and boroder and handle display
 * @param handleRadius radius of circular handles to implement morphing operations
 * @param handlePlacement determines how handles should be placed. They can be placed at corners
 * ot center of each side or both.
 * @param onDown callback invoked when gesture has started
 * @param onMove callback notifies composable has a pointer down and invoking operations
 * @param onUp notifies last pointer down is now up
 * @param content is the composable that will be contained in this Composable
 */
@Composable
fun TransformLayout(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    handleRadius: Dp = 15.dp,
    handlePlacement: HandlePlacement = HandlePlacement.Corner,
    onDown: (Transform) -> Unit = {},
    onMove: (Transform) -> Unit = {},
    onUp: (Transform) -> Unit = {},
    content: @Composable () -> Unit
) {

    MorphSubcomposeLayout(
        modifier = modifier
            .requiredSizeIn(
                minWidth = handleRadius * 2,
                minHeight = handleRadius * 2
            ),
        handleRadius = handleRadius.coerceAtLeast(12.dp),
        updatePhysicalSize = false,
        mainContent = {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        },
        dependentContent = { intSize: IntSize, constraints: Constraints ->

            val dpSize = with(LocalDensity.current) {
                val rawWidth = intSize.width.toDp()
                val rawHeight = intSize.height.toDp()
                DpSize(rawWidth, rawHeight)
            }

            TransformLayout(
                enabled = enabled,
                handleRadius = handleRadius,
                dpSize = dpSize,
                handlePlacement = handlePlacement,
                onDown = onDown,
                onMove = onMove,
                onUp = onUp,
                content = content
            )
        }
    )
}

@Composable
private fun TransformLayout(
    enabled: Boolean = true,
    handleRadius: Dp = 15.dp,
    dpSize: DpSize,
    transform: Transform = Transform(),
    handlePlacement: HandlePlacement,
    onDown: (Transform) -> Unit = {},
    onMove: (Transform) -> Unit = {},
    onUp: (Transform) -> Unit = {},
    content: @Composable () -> Unit
) {

    val touchRegionRadius: Float
    val minDimension: Float
    val size: Size

    with(LocalDensity.current) {
        touchRegionRadius = handleRadius.coerceAtLeast(12.dp).toPx()
        minDimension = (touchRegionRadius * if (handlePlacement == HandlePlacement.Corner) 4 else 6)

        size = DpSize(
            dpSize.width + handleRadius * 2,
            dpSize.height + handleRadius * 2
        ).toSize()
    }

    var outerTransform by remember {
        mutableStateOf(
            transform
        )
    }

    var rectDraw by remember {
        mutableStateOf(
            Rect(
                offset = Offset.Zero,
                size = size
            )
        )
    }

    val editModifier =
        Modifier
            .graphicsLayer {
                translationX = outerTransform.translationX
                translationY = outerTransform.translationY
                scaleX = outerTransform.scaleX
                scaleY = outerTransform.scaleY
                rotationZ = outerTransform.rotation
            }

            .transform(
                enabled = enabled,
                size = size,
                touchRegionRadius = touchRegionRadius,
                minDimension = minDimension,
                handlePlacement = handlePlacement,
                transform = outerTransform,
                onDown = { transformChange: Transform, rect: Rect ->
                    outerTransform = transformChange
                    rectDraw = rect
                    onDown(outerTransform)
                },
                onMove = { transformChange: Transform, rect: Rect ->
                    outerTransform = transformChange
                    rectDraw = rect
                    onMove(outerTransform)
                },
                onUp = { transformChange: Transform, rect: Rect ->
                    outerTransform = transformChange
                    rectDraw = rect
                    onUp(outerTransform)
                }
            )
            // Padding is required to keep touch position of handles inside composable
            // without padding only 1 quarter of corner handles are in composable
            // padding is scaled because scale change also changes padding dimensions
            .padding(
                horizontal = handleRadius / abs(outerTransform.scaleX),
                vertical = handleRadius / abs(outerTransform.scaleY)
            )

    TransformImpl(
        modifier = editModifier,
        enabled = enabled,
        dpSize = dpSize,
        handleRadius = handleRadius,
        touchRegionRadius = touchRegionRadius,
        rectDraw = rectDraw,
        handlePlacement = handlePlacement,
        content = content
    )
}

@Composable
private fun TransformImpl(
    modifier: Modifier,
    enabled: Boolean,
    dpSize: DpSize,
    handleRadius: Dp,
    touchRegionRadius: Float,
    rectDraw: Rect,
    handlePlacement: HandlePlacement,
    content: @Composable () -> Unit
) {
    Box(
        Modifier.size(
            width = dpSize.width + handleRadius * 2,
            height = dpSize.height + handleRadius * 2
        ),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        if (enabled) {
            HandleOverlay(
                modifier = Modifier.fillMaxSize(),
                radius = touchRegionRadius,
                handlePlacement = handlePlacement,
                rectDraw = rectDraw
            )
        }
    }
}
