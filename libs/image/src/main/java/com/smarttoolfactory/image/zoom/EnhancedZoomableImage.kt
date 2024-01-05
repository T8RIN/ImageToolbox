package com.smarttoolfactory.image.zoom

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.image.ImageWithConstraints

/**
 * Zoomable image that zooms in and out in [ [minZoom], [maxZoom] ] interval and pans
 * zoomed image based on pointer position.
 * Double tap gestures reset image translation and zoom to default values with animation.
 *
 * @param initialZoom zoom set initially
 * @param minZoom minimum zoom value this Composable can possess
 * @param maxZoom maximum zoom value this Composable can possess
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent.
 * @param fling when set to true dragging pointer builds up velocity. When last
 * pointer leaves Composable a movement invoked against friction till velocity drops down
 * to threshold
 * @param moveToBounds when set to true if image zoom is lower than initial zoom or
 * panned out of image boundaries moves back to bounds with animation.
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * @param clipTransformToContentScale when set true zoomable image takes borders of image drawn
 * while zooming in. [contentScale] determines whether will be empty spaces on edges of Composable
 * @param zoomOnDoubleTap lambda that returns current [ZoomLevel] and based on current level
 * enables developer to define zoom on double tap gesture
 * @param enabled lambda can be used selectively enable or disable pan and intercepting with
 * scroll, drag or lists or pagers using current zoom, pan or rotation values
 * @param onGestureStart callback to to notify gesture has started and return current ZoomData
 * of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current ZoomData
 * of this modifier
 * @param onGestureEnd callback to notify that gesture finished and return current ZoomData
 * of this modifier
 */
@Composable
fun EnhancedZoomableImage(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    initialZoom: Float = 1f,
    minZoom: Float = .5f,
    maxZoom: Float = 5f,
    limitPan: Boolean = true,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    clip: Boolean = true,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = DefaultOnDoubleTap,
    clipTransformToContentScale: Boolean = false,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null
) {

    val zoomModifier = Modifier
        .enhancedZoom(
            key1 = imageBitmap,
            key2 = contentScale,
            enhancedZoomState = rememberEnhancedZoomState(
                key1 = imageBitmap,
                key2 = contentScale,
                imageSize = IntSize(imageBitmap.width, imageBitmap.height),
                initialZoom = initialZoom,
                minZoom = minZoom,
                maxZoom = maxZoom,
                limitPan = limitPan,
                fling = fling,
                moveToBounds = moveToBounds,
                zoomable = zoomable,
                pannable = pannable,
                rotatable = rotatable
            ),
            clip = clip,
            enabled = enabled,
            zoomOnDoubleTap = zoomOnDoubleTap,
            onGestureStart = onGestureStart,
            onGesture = onGesture,
            onGestureEnd = onGestureEnd
        )

    ImageWithConstraints(
        modifier = if (clipTransformToContentScale) modifier else modifier.then(zoomModifier),
        imageBitmap = imageBitmap,
        alignment = alignment,
        contentScale = contentScale,
        contentDescription = contentDescription,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        drawImage = !clipTransformToContentScale
    ) {

        if (clipTransformToContentScale) {
            Image(
                bitmap = imageBitmap,
                contentScale = contentScale,
                modifier = zoomModifier,
                alignment = alignment,
                contentDescription = contentDescription,
                alpha = alpha,
                colorFilter = colorFilter,
                filterQuality = filterQuality,
            )
        }
    }
}

/**
 * Zoomable image that zooms in and out in [zoomState.minZoom, zoomState.maxZoom] interval
 * and pans zoomed image based on pointer position.
 * Double tap gestures reset image translation and zoom to default values with animation.
 *
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * @param clipTransformToContentScale when set true zoomable image takes borders of image drawn
 * while zooming in. [contentScale] determines whether will be empty spaces on edges of Composable
 * @param zoomOnDoubleTap lambda that returns current [ZoomLevel] and based on current level
 * enables developer to define zoom on double tap gesture
 * @param enabled lambda can be used selectively enable or disable pan and intercepting with
 * scroll, drag or lists or pagers using current zoom, pan or rotation values
 * @param onGestureStart callback to to notify gesture has started and return current ZoomData
 * of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current ZoomData
 * of this modifier
 * @param onGestureEnd callback to notify that gesture finished and return current ZoomData
 * of this modifier
 */
@Composable
fun EnhancedZoomableImage(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    clip: Boolean = true,
    clipTransformToContentScale: Boolean = false,
    moveToBounds: Boolean,
    fling: Boolean,
    enhancedZoomState: EnhancedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = enhancedZoomState.DefaultOnDoubleTap,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null
) {

    val zoomModifier = Modifier
        .enhancedZoom(
            enhancedZoomState = enhancedZoomState,
            clip = clip,
            enabled = enabled,
            zoomOnDoubleTap = zoomOnDoubleTap,
            onGestureStart = onGestureStart,
            onGesture = onGesture,
            onGestureEnd = onGestureEnd
        )

    ImageWithConstraints(
        modifier = if (clipTransformToContentScale) modifier else modifier.then(zoomModifier),
        imageBitmap = imageBitmap,
        alignment = alignment,
        contentScale = contentScale,
        contentDescription = contentDescription,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        drawImage = !clipTransformToContentScale
    ) {

        if (clipTransformToContentScale) {
            Image(
                bitmap = imageBitmap,
                contentScale = contentScale,
                modifier = zoomModifier,
                alignment = alignment,
                contentDescription = contentDescription,
                alpha = alpha,
                colorFilter = colorFilter,
                filterQuality = filterQuality,
            )
        }
    }
}
