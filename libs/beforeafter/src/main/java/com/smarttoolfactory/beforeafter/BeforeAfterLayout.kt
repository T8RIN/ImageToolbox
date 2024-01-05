package com.smarttoolfactory.beforeafter

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpSize

/**
 * A composable that lays out and draws a given [beforeContent] and [afterContent]
 * based on [contentOrder]. This overload uses [DefaultOverlay] to draw vertical slider and thumb.
 *
 * @param enableProgressWithTouch flag to enable drag and change progress with touch
 * @param enableZoom when enabled images are zoomable and pannable
 * @param contentOrder order of composables to be drawn
 * @param overlayStyle styling values for [DefaultOverlay] to set divier color, thumb shape, size,
 * elevation and other properties
 * @param beforeContent content to be drawn as before Composable
 * @param afterContent content to be drawn as after Composable
 * @param beforeLabel label for [beforeContent]. It's [BeforeLabel] by default
 * @param afterLabel label for [afterContent]. It's [AfterLabel] by default
 *
 */
@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    overlayStyle: OverlayStyle = OverlayStyle(),
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
) {
    var progress by remember { mutableStateOf(50f) }

    Layout(
        modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress,
        onProgressChange = {
            progress = it
        },
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = { dpSize: DpSize, offset: Offset ->
            DefaultOverlay(
                width = dpSize.width,
                height = dpSize.height,
                position = offset,
                overlayStyle = overlayStyle
            )
        }
    )
}

/**
 * A composable that lays out and draws a given [beforeContent] and [afterContent]
 * based on [contentOrder]. This overload uses [DefaultOverlay] to draw vertical slider and thumb
 * and has [progress] and [onProgressChange] which makes it eligible to animate by changing
 * [progress] value.
 *
 * @param enableProgressWithTouch flag to enable drag and change progress with touch
 * @param enableZoom when enabled images are zoomable and pannable
 * @param contentOrder order of composables to be drawn
 * @param progress current position or progress of before/after
 * @param onProgressChange current position or progress of before/after
 * @param overlayStyle styling values for [DefaultOverlay] to set divier color, thumb shape, size,
 * elevation and other properties
 * @param beforeContent content to be drawn as before Composable
 * @param afterContent content to be drawn as after Composable
 * @param beforeLabel label for [beforeContent]. It's [BeforeLabel] by default
 * @param afterLabel label for [afterContent]. It's [AfterLabel] by default
 */
@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    overlayStyle: OverlayStyle = OverlayStyle(),
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
) {

    Layout(
        modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress,
        onProgressChange = onProgressChange,
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = { dpSize: DpSize, offset: Offset ->
            DefaultOverlay(
                width = dpSize.width,
                height = dpSize.height,
                position = offset,
                overlayStyle = overlayStyle
            )
        }
    )
}

/**
 * A composable that lays out and draws a given [beforeContent] and [afterContent]
 * based on [contentOrder].
 *
 * @param enableProgressWithTouch flag to enable drag and change progress with touch
 * @param enableZoom when enabled images are zoomable and pannable
 * @param contentOrder order of composables to be drawn

 * It's between [0f-100f] to set thumb's vertical position in layout
 * @param beforeContent content to be drawn as before Composable
 * @param afterContent content to be drawn as after Composable
 * @param beforeLabel label for [beforeContent]. It's [BeforeLabel] by default
 * @param afterLabel label for [afterContent]. It's [AfterLabel] by default
 * @param overlay Composable for drawing overlay over this Composable. It returns dimensions
 * of ancestor and touch position
 */
@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
    overlay: @Composable ((DpSize, Offset) -> Unit)?
) {
    var progress by remember { mutableStateOf(50f) }

    Layout(
        modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress,
        onProgressChange = {
            progress = it
        },
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = overlay
    )
}

/**
 * A composable that lays out and draws a given [beforeContent] and [afterContent]
 * based on [contentOrder].
 *
 * @param enableProgressWithTouch flag to enable drag and change progress with touch
 * @param enableZoom when enabled images are zoomable and pannable
 * @param contentOrder order of composables to be drawn
 * @param progress current position or progress of before/after
 * @param onProgressChange current position or progress of before/after
 * It's between [0f-100f] to set thumb's vertical position in layout
 * @param beforeContent content to be drawn as before Composable
 * @param afterContent content to be drawn as after Composable
 * @param beforeLabel label for [beforeContent]. It's [BeforeLabel] by default
 * @param afterLabel label for [afterContent]. It's [AfterLabel] by default
 * @param overlay Composable for drawing overlay over this Composable. It returns dimensions
 * of ancestor and touch position
 */
@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable (BoxScope.() -> Unit)? = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable (BoxScope.() -> Unit)? = { AfterLabel(contentOrder = contentOrder) },
    overlay: @Composable ((DpSize, Offset) -> Unit)?
) {

    Layout(
        modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress,
        onProgressChange = onProgressChange,
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = overlay
    )
}