package com.smarttoolfactory.beforeafter

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale

/**
 * A composable that lays out and draws a given [beforeImage] and [afterImage] at given [contentOrder]
 * with specified [contentScale] and returns draw area and section of drawn bitmap.
 *
 * [BeforeAfterImageScope] extends [ImageScope] that returns draw area dimensions and image draw rect
 * and touch position of user on screen.
 *
 * @param beforeImage image that show initial progress
 * @param afterImage image that show final progress
 * @param enableProgressWithTouch flag to enable drag and change progress with touch
 * @param enableZoom when enabled images are zoomable and pannable
 * @param overlayStyle styling values for [DefaultOverlay] to set divier color, thumb shape, size,
 * elevation and other properties
 * @param contentOrder order of images to be drawn
 * @param alignment determines where image will be aligned inside [BoxWithConstraints]
 * This is observable when bitmap image/width ratio differs from [Canvas] that draws [ImageBitmap]
 * @param contentDescription text used by accessibility services to describe what this image
 * represents. This should always be provided unless this image is used for decorative purposes,
 * and does not represent a meaningful action that a user can take. This text should be
 * localized, such as by using [androidx.compose.ui.res.stringResource] or similar
 * @param contentScale how image should be scaled inside Canvas to match parent dimensions.
 * [ContentScale.Fit] for instance maintains src ratio and scales image to fit inside the parent.
 */
@Composable
fun BeforeAfterImage(
    modifier: Modifier = Modifier,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    overlayStyle: OverlayStyle = OverlayStyle(),
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    contentDescription: String? = null,
) {
    var progress by remember { mutableStateOf(50f) }

    BeforeAfterImageImpl(
        modifier = modifier,
        beforeImage = beforeImage,
        afterImage = afterImage,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        contentOrder = contentOrder,
        progress = progress,
        onProgressChange = {
            progress = it
        },
        contentScale = contentScale,
        alignment = alignment,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        contentDescription = contentDescription,
    ) {

        DefaultOverlay(
            width = imageWidth,
            height = imageHeight,
            position = position,
            overlayStyle = overlayStyle
        )
    }
}

/**
 * A composable that lays out and draws a given [beforeImage] and [afterImage] at given [contentOrder]
 * with specified [contentScale] and returns draw area and section of drawn bitmap.
 *
 * [BeforeAfterImageScope] extends [ImageScope] that returns draw area dimensions and image draw rect
 * and touch position of user on screen.
 *
 * @param beforeImage image that show initial progress
 * @param afterImage image that show final progress
 * @param enableProgressWithTouch flag to enable drag and change progress with touch
 * @param enableZoom when enabled images are zoomable and pannable
 * @param overlayStyle styling values for [DefaultOverlay] to set divier color, thumb shape, size,
 * elevation and other properties
 * @param contentOrder order of images to be drawn
 * @param alignment determines where image will be aligned inside [BoxWithConstraints]
 * This is observable when bitmap image/width ratio differs from [Canvas] that draws [ImageBitmap]
 * @param contentDescription text used by accessibility services to describe what this image
 * represents. This should always be provided unless this image is used for decorative purposes,
 * and does not represent a meaningful action that a user can take. This text should be
 * localized, such as by using [androidx.compose.ui.res.stringResource] or similar
 * @param contentScale how image should be scaled inside Canvas to match parent dimensions.
 * [ContentScale.Fit] for instance maintains src ratio and scales image to fit inside the parent.
 */
@Composable
fun BeforeAfterImage(
    modifier: Modifier = Modifier,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    overlayStyle: OverlayStyle = OverlayStyle(),
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    contentDescription: String? = null,
) {
    BeforeAfterImageImpl(
        modifier = modifier,
        beforeImage = beforeImage,
        afterImage = afterImage,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        contentOrder = contentOrder,
        progress = progress,
        onProgressChange = onProgressChange,
        contentScale = contentScale,
        alignment = alignment,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        contentDescription = contentDescription,
    ) {

        DefaultOverlay(
            width = imageWidth,
            height = imageHeight,
            position = position,
            overlayStyle = overlayStyle
        )
    }
}

/**
 * A composable that lays out and draws a given [beforeImage] and [afterImage] at given [contentOrder]
 * with specified [contentScale] and returns draw area and section of drawn bitmap.
 *
 * [BeforeAfterImageScope] extends [ImageScope] that returns draw area dimensions and image draw rect
 * and touch position of user on screen.
 *
 * @param beforeImage image that show initial progress
 * @param afterImage image that show final progress
 * @param enableProgressWithTouch flag to enable drag and change progress with touch
 * @param enableZoom when enabled images are zoomable and pannable
 * @param contentOrder order of images to be drawn
 * @param alignment determines where image will be aligned inside [BoxWithConstraints]
 * This is observable when bitmap image/width ratio differs from [Canvas] that draws [ImageBitmap]
 * @param contentDescription text used by accessibility services to describe what this image
 * represents. This should always be provided unless this image is used for decorative purposes,
 * and does not represent a meaningful action that a user can take. This text should be
 * localized, such as by using [androidx.compose.ui.res.stringResource] or similar
 * @param contentScale how image should be scaled inside Canvas to match parent dimensions.
 * [ContentScale.Fit] for instance maintains src ratio and scales image to fit inside the parent.
 * @param alpha Opacity to be applied to [beforeImage] from 0.0f to 1.0f representing
 * fully transparent to fully opaque respectively
 * @param colorFilter ColorFilter to apply to the [beforeImage] when drawn into the destination
 * @param filterQuality Sampling algorithm applied to the [beforeImage] when it is scaled and drawn
 * into the destination. The default is [FilterQuality.Low] which scales using a bilinear
 * sampling algorithm
 * @param overlay is a Composable that can be matched at exact position where [beforeImage] is drawn.
 * This is useful for drawing thumbs, cropping or another layout that should match position
 * with the image that is scaled is drawn
 */
@Composable
fun BeforeAfterImage(
    modifier: Modifier = Modifier,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
    overlay: @Composable BeforeAfterImageScope.() -> Unit
) {

    BeforeAfterImageImpl(
        modifier = modifier,
        beforeImage = beforeImage,
        afterImage = afterImage,
        progress = progress,
        onProgressChange = onProgressChange,
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        alignment = alignment,
        contentScale = contentScale,
        contentDescription = contentDescription,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        overlay = overlay
    )
}

/**
 * A composable that lays out and draws a given [beforeImage] and [afterImage] at given [contentOrder]
 * with specified [contentScale] and returns draw area and section of drawn bitmap.
 *
 * [BeforeAfterImageScope] extends [ImageScope] that returns draw area dimensions and image draw rect
 * and touch position of user on screen.
 *
 * @param beforeImage image that show initial progress
 * @param afterImage image that show final progress
 * @param enableProgressWithTouch flag to enable drag and change progress with touch
 * @param enableZoom when enabled images are zoomable and pannable
 * @param contentOrder order of images to be drawn
 * @param alignment determines where image will be aligned inside [BoxWithConstraints]
 * This is observable when bitmap image/width ratio differs from [Canvas] that draws [ImageBitmap]
 * @param contentDescription text used by accessibility services to describe what this image
 * represents. This should always be provided unless this image is used for decorative purposes,
 * and does not represent a meaningful action that a user can take. This text should be
 * localized, such as by using [androidx.compose.ui.res.stringResource] or similar
 * @param contentScale how image should be scaled inside Canvas to match parent dimensions.
 * [ContentScale.Fit] for instance maintains src ratio and scales image to fit inside the parent.
 * @param alpha Opacity to be applied to [beforeImage] from 0.0f to 1.0f representing
 * fully transparent to fully opaque respectively
 * @param colorFilter ColorFilter to apply to the [beforeImage] when drawn into the destination
 * @param filterQuality Sampling algorithm applied to the [beforeImage] when it is scaled and drawn
 * into the destination. The default is [FilterQuality.Low] which scales using a bilinear
 * sampling algorithm
 * @param overlay is a Composable that can be matched at exact position where [beforeImage] is drawn.
 * This is useful for drawing thumbs, cropping or another layout that should match position
 * with the image that is scaled is drawn
 */
@Composable
fun BeforeAfterImage(
    modifier: Modifier = Modifier,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
    overlay: @Composable BeforeAfterImageScope.() -> Unit
) {
    var progress by remember { mutableStateOf(50f) }

    BeforeAfterImageImpl(
        modifier = modifier,
        beforeImage = beforeImage,
        afterImage = afterImage,
        progress = progress,
        onProgressChange = {
            progress = it
        },
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        alignment = alignment,
        contentScale = contentScale,
        contentDescription = contentDescription,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        overlay = overlay
    )
}
