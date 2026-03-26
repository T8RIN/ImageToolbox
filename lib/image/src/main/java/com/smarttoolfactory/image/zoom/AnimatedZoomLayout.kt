package com.smarttoolfactory.image.zoom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import com.smarttoolfactory.image.SlotsEnum


/**
 * Layout that can zoom, rotate, pan its content with fling and moving back to bounds animation.
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
 * @param fling when set to true dragging pointer builds up velocity. When last
 * pointer leaves Composable a movement invoked against friction till velocity drops below
 * to threshold
 * @param moveToBounds when set to true if image zoom is lower than initial zoom or
 * panned out of image boundaries moves back to bounds with animation.
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 * @param zoomOnDoubleTap lambda that returns current [ZoomLevel] and based on current level
 * enables developer to define zoom on double tap gesture
 * @param enabled lambda can be used selectively enable or disable pan and intercepting with
 * scroll, drag or lists or pagers using current zoom, pan or rotation values
 */
@Composable
fun AnimatedZoomLayout(
    modifier: Modifier = Modifier,
    clip: Boolean = true,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 3f,
    fling: Boolean = true,
    moveToBounds: Boolean = false,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = true,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = DefaultOnDoubleTap,
    content: @Composable () -> Unit
) {
    AnimatedZoomSubcomposeLayout(
        modifier = modifier,
        mainContent = { content() }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .animatedZoom(
                    enabled = enabled,
                    clip = clip,
                    zoomOnDoubleTap = zoomOnDoubleTap,
                    animatedZoomState = rememberAnimatedZoomState(
                        minZoom = minZoom,
                        maxZoom = maxZoom,
                        initialZoom = initialZoom,
                        fling = fling,
                        moveToBounds = moveToBounds,
                        zoomable = zoomable,
                        pannable = pannable,
                        rotatable = rotatable,
                        limitPan = limitPan,
                        contentSize = it
                    ),
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

/**
 * SubcomposeLayout for getting dimensions of [mainContent] while laying out with [modifier]
 * Use of this layout is suitable when size of parent doesn't match content
 * and size [mainContent] is required inside [dependentContent] to use [mainContent] size
 * as reference or dimensions for child composables inside [dependentContent].
 *
 */
@Composable
private fun AnimatedZoomSubcomposeLayout(
    modifier: Modifier = Modifier,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (DpSize) -> Unit
) {

    val density = LocalDensity.current

    SubcomposeLayout(
        modifier = modifier
    ) { constraints: Constraints ->

        // Subcompose(compose only a section) main content and get Placeable
        val mainPlaceables: List<Placeable> = subcompose(SlotsEnum.Main, mainContent)
            .map {
                it.measure(constraints.copy(minWidth = 0, minHeight = 0))
            }

        // Get max width and height of main component
        var maxWidth = 0
        var maxHeight = 0

        mainPlaceables.forEach { placeable: Placeable ->
            maxWidth += placeable.width
            maxHeight = placeable.height
        }

        val dependentPlaceables: List<Placeable> = subcompose(SlotsEnum.Dependent) {
            val dpSize = density.run { DpSize(maxWidth.toDp(), maxHeight.toDp()) }
            dependentContent(dpSize)
        }
            .map { measurable: Measurable ->
                measurable.measure(constraints)
            }

        layout(constraints.maxWidth, constraints.maxHeight) {


            dependentPlaceables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}
