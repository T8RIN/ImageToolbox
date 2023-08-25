package com.smarttoolfactory.image.transform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * Composable that changes dimensions of its content from handles, translates its position
 * when dragged inside bounds
 *
 * @param modifier is modifier of Composable used as [content]
 * @param containerModifier is modifier of SubcomposeLayout that measures content. Do not
 * set a **Size** modifier because [MorphSubcomposeLayout] measures with unbounded constraints
 * and size of this composable is changed. If you set a fixed size, size won't be calculated
 * accurately.
 * @param enabled flag for enabling morph operations and border and handle display
 * @param handleRadius radius of circular handles to implement morphing operations
 * @param handlePlacement determines how handles should be placed. They can be placed at corners
 * ot center of each side or both.
 * @param updatePhysicalSize when true this Composable's width inside parent is updated and
 * siblings get updated according to updated dimensions
 * @param onDown callback invoked when gesture has started
 * @param onMove callback notifies composable has a pointer down and invoking operations
 * @param onUp notifies last pointer down is now up
 * @param content is the composable that will be contained in this Composable
 */
@Composable
fun MorphLayout(
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    enabled: Boolean = true,
    handleRadius: Dp = 15.dp,
    handlePlacement: HandlePlacement = HandlePlacement.Corner,
    updatePhysicalSize: Boolean = false,
    onDown: () -> Unit = {},
    onMove: (DpSize) -> Unit = {},
    onUp: () -> Unit = {},
    content: @Composable () -> Unit
) {
    MorphSubcomposeLayout(
        modifier = containerModifier,
        handleRadius = handleRadius,
        updatePhysicalSize = updatePhysicalSize,
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

            MorphLayout(
                handleRadius = handleRadius,
                enabled = enabled,
                dpSize = dpSize,
                constraints = constraints,
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
private fun MorphLayout(
    enabled: Boolean = true,
    handleRadius: Dp,
    dpSize: DpSize,
    constraints: Constraints,
    handlePlacement: HandlePlacement,
    onDown: () -> Unit = {},
    onMove: (DpSize) -> Unit = {},
    onUp: () -> Unit = {},
    content: @Composable () -> Unit
) {

    val density = LocalDensity.current
    val touchRegionRadius: Float
    val minDimension: Float

    val initialSize = remember {
        DpSize(
            dpSize.width + handleRadius * 2,
            dpSize.height + handleRadius * 2
        )
    }

    var updatedSize by remember {
        mutableStateOf(initialSize)
    }

    with(density) {
        touchRegionRadius = handleRadius.toPx()
        minDimension =
            (touchRegionRadius * if (handlePlacement == HandlePlacement.Corner) 4 else 6)
    }

    val rectDraw = remember(updatedSize) {
        with(density) {
            val width = updatedSize.width.toPx().coerceAtMost(constraints.maxWidth.toFloat())
            val height = updatedSize.height.toPx().coerceAtMost(constraints.maxHeight.toFloat())
            Rect(offset = Offset.Zero, size = Size(width, height))
        }
    }

    val editModifier = Modifier
        .morph(
            enabled = enabled,
            initialSize = initialSize,
            touchRegionRadius = touchRegionRadius,
            minDimension = minDimension,
            constraints = constraints,
            handlePlacement = handlePlacement,
            onDown = onDown,
            onMove = { dpSizeChange: DpSize ->
                updatedSize = dpSizeChange
                onMove(updatedSize)
            },
            onUp = onUp
        )

    val transformModifier = Modifier
        .padding(handleRadius)
        .fillMaxSize()

    ResizeImpl(
        modifier = editModifier,
        transformModifier = transformModifier,
        enabled = enabled,
        touchRegionRadius = touchRegionRadius,
        rectDraw = rectDraw,
        handlePlacement = handlePlacement,
        content = content
    )
}

@Composable
private fun ResizeImpl(
    modifier: Modifier,
    transformModifier: Modifier,
    enabled: Boolean,
    touchRegionRadius: Float,
    rectDraw: Rect,
    handlePlacement: HandlePlacement,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = transformModifier,
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        if (enabled) {
            HandleOverlay(
                modifier = Modifier.fillMaxSize(),
                radius = touchRegionRadius,
                rectDraw = rectDraw,
                handlePlacement = handlePlacement
            )
        }


    }
}
