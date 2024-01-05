package com.smarttoolfactory.image.zoom

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import com.smarttoolfactory.image.ImageWithConstraints

/**
 * Zoomable image that zooms in and out in [ [minZoom], [maxZoom] ] interval and translates
 * zoomed image based on pointer position.
 * Double tap gestures reset image translation and zoom to default values with animation.
 * @param initialZoom zoom set initially
 * @param minZoom minimum zoom value this Composable can possess
 * @param maxZoom maximum zoom value this Composable can possess
 * @param clip whether image should be clip to bounds of Image
 * @param clipTransformToContentScale when set true zoomable image takes borders of image drawn
 * while zooming in. [contentScale] determines whether will be empty spaces on edges of Composable
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent.
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * event propagations
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param onGestureStart callback to to notify gesture has started and return current ZoomData
 * of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current ZoomData
 * of this modifier
 * @param onGestureEnd callback to notify that gesture finished and return current ZoomData
 * of this modifier
 */
@Composable
fun ZoomableImage(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    limitPan: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    clip: Boolean = true,
    clipTransformToContentScale: Boolean = false,
    consume: Boolean = true,
    onGestureStart: ((ZoomData) -> Unit)? = null,
    onGesture: ((ZoomData) -> Unit)? = null,
    onGestureEnd: ((ZoomData) -> Unit)? = null
) {

    val zoomModifier = Modifier
        .zoom(
            key1 = imageBitmap,
            key2 = contentScale,
            zoomState = rememberZoomState(
                key1 = imageBitmap,
                key2 = contentScale,
                initialZoom = initialZoom,
                minZoom = minZoom,
                maxZoom = maxZoom,
                limitPan = limitPan,
                zoomable = zoomable,
                pannable = pannable,
                rotatable = rotatable,
            ),
            consume = consume,
            clip = clip,
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
 * Zoomable image that zooms in and out in [zoomState.minZoom, zoomState.maxZoom] interval and translates
 * zoomed image based on pointer position.
 * Double tap gestures reset image translation and zoom to default values with animation.
 * @param clip whether image should be clip to bounds of Image
 * @param clipTransformToContentScale when set true zoomable image takes borders of image drawn
 * while zooming in. [contentScale] determines whether will be empty spaces on edges of Composable
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * event propagations
 * @param onGestureStart callback to to notify gesture has started and return current ZoomData
 * of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current ZoomData
 * of this modifier
 * @param onGestureEnd callback to notify that gesture finished and return current ZoomData
 * of this modifier
 */
@Composable
fun ZoomableImage(
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
    zoomState: ZoomState,
    consume: Boolean = true,
    onGestureStart: ((ZoomData) -> Unit)? = null,
    onGesture: ((ZoomData) -> Unit)? = null,
    onGestureEnd: ((ZoomData) -> Unit)? = null
) {

    val zoomModifier = Modifier
        .zoom(
            key1 = imageBitmap,
            key2 = contentScale,
            zoomState = zoomState,
            consume = consume,
            clip = clip,
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


