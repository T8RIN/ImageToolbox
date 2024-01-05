package com.smarttoolfactory.image.transform

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.image.SlotsEnum

/**
 * [SubcomposeLayout] layouts children like [Box] and returns [IntSize] of this Composable.
 * It doesn't layout mainContent because our [mainContent] and [dependentContent] are
 * combined as one Composable. We just measure exact size of initial dimensions
 * for a Composable to use it inside for setting size of Rectangles for initial bounds
 * for instance.
 *
 * @param handleRadius radius of handles on corners or sides to drag
 * @param updatePhysicalSize when we change size of a child composable whether it should
 * increase dimension of this Layout
 * @param mainContent is content that will be drawn and measured
 * with [SubcomposeMeasureScope.subcompose] to pass to [dependentContent] to create touch region
 * and overlay
 */
@Composable
internal fun MorphSubcomposeLayout(
    modifier: Modifier = Modifier,
    handleRadius: Dp = 15.dp,
    updatePhysicalSize: Boolean = false,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (IntSize, Constraints) -> Unit
) {

    val handleRadiusInPx = with(LocalDensity.current) {
        handleRadius.roundToPx()
    }

    SubcomposeLayout(modifier = modifier) { constraints ->

        // Subcompose(compose only a section) main content and get Placeable
        val mainPlaceables: List<Placeable> = subcompose(SlotsEnum.Main, mainContent)
            .map {
                it.measure(constraints)
            }

        // Get max width and height of main component
        var maxWidth = 0
        var maxHeight = 0

        mainPlaceables.forEach { placeable: Placeable ->
            maxWidth += placeable.width
            maxHeight = placeable.height
        }

        val handleSize = handleRadiusInPx * 2
        maxWidth = maxWidth.coerceAtMost(constraints.maxWidth - handleSize)
        maxHeight = maxHeight.coerceAtMost(constraints.maxHeight - handleSize)

        val maxSize = IntSize(maxWidth, maxHeight)

        val dependentPlaceables = subcompose(SlotsEnum.Dependent) {
            dependentContent(maxSize, constraints)
        }.map {
            it.measure(constraints)
        }

        val dependentMaxSize =
            dependentPlaceables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = maxOf(currentMax.width, placeable.width),
                    height = maxOf(currentMax.height, placeable.height)
                )
            }

        val width: Int
        val height: Int
        if (updatePhysicalSize) {
            // Set  sum of content and handle size as total size of this Composable
            width = dependentMaxSize.width
            height = dependentMaxSize.height

        } else {
            // Set  sum of dependent size, if it grows physical size of Composable also grows
            width = maxSize.width + 2 * handleRadiusInPx
            height = maxSize.height + 2 * handleRadiusInPx
        }

        layout(width, height) {
            dependentPlaceables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}
