package com.smarttoolfactory.image

import androidx.annotation.IntRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.pointerMotionEvents
import kotlin.math.roundToInt

/**
 * [ImageWithThumbnail] with [ThumbnailLayout] displays thumbnail of bitmap it draws in corner specified
 * by [ThumbnailState.position]. When touch position is close to thumbnail position
 * if [ThumbnailState.dynamicPosition]
 * is set to true moves thumbnail to corner specified by [ThumbnailState.moveTo]
 *
 * @param imageBitmap The [ImageBitmap] to draw
 * @param contentScale Optional scale parameter used to determine the aspect ratio scaling to be used
 * if the bounds are a different size from the intrinsic size of the [ImageBitmap]
 * @param alignment Optional alignment parameter used to place the [ImageBitmap] in the given
 * bounds defined by the width and height
 * @param contentDescription text used by accessibility services to describe what this image
 * represents. This should always be provided unless this image is used for decorative purposes,
 * and does not represent a meaningful action that a user can take. This text should be
 * localized, such as by using [androidx.compose.ui.res.stringResource] or similar
 * @param thumbnailState contains UI properties such as shape, size, shadow, border, etc.
 * @param alpha Optional opacity to be applied to the [ImageBitmap] when it is rendered onscreen
 * @param colorFilter Optional ColorFilter to apply for the [ImageBitmap] when it is rendered
 * onscreen
 * @param filterQuality Sampling algorithm applied to the [imageBitmap] when it is scaled and drawn
 * into the destination. The default is [FilterQuality.Low] which scales using a bilinear
 * sampling algorithm
 * @param drawOriginalImage when set to false draws thumbnail only
 * @param onDown callback that notifies at first interaction and returns offset
 * @param onMove callback that notifier user's touch position when a pointer is moving
 * @param onUp callback that notifies last pointer on screen is up
 * event occurs on this Composable
 * @param onThumbnailCenterChange callback to get center of thumbnail
 * @param content is an optional Composable that can be matched at exact position
 * where [imageBitmap] is drawn. content can be used for drawing over thumb or using [Offset] from
 * motion callbacks or displaying image, icon or watermark above this Composable with
 * exact bounds [imageBitmap] is drawn
 */
@Composable
fun ImageWithThumbnail(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    contentDescription: String?,
    thumbnailState: ThumbnailState = rememberThumbnailState(),
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    drawOriginalImage: Boolean = true,
    onDown: ((Offset) -> Unit)? = null,
    onMove: ((Offset) -> Unit)? = null,
    onUp: (() -> Unit)? = null,
    onThumbnailCenterChange: ((Offset) -> Unit)? = null,
    content: @Composable ImageScope.() -> Unit = {}
) {

    ImageWithConstraints(
        modifier = modifier,
        contentScale = contentScale,
        alignment = alignment,
        contentDescription = contentDescription,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        imageBitmap = imageBitmap,
        drawImage = drawOriginalImage
    ) {

        val imageScope = this
        val density = LocalDensity.current

        val scaledImageBitmap = getScaledImageBitmap(imageBitmap, contentScale)

        val size = rememberUpdatedState(
            newValue = Size(
                width = imageWidth.value * density.density,
                height = imageHeight.value * density.density
            )
        )

        var offset by remember(key1 = contentScale, key2 = scaledImageBitmap) {
            mutableStateOf(
                Offset.Unspecified
            )
        }

        fun updateOffset(pointerInputChange: PointerInputChange): Offset {
            val offsetX = pointerInputChange.position.x
                .coerceIn(0f, size.value.width)
            val offsetY = pointerInputChange.position.y
                .coerceIn(0f, size.value.height)
            pointerInputChange.consume()
            return Offset(offsetX, offsetY)
        }

        val thumbnailModifier = Modifier
            .pointerMotionEvents(
                key1 = contentScale,
                key2 = scaledImageBitmap,
                onDown = { pointerInputChange: PointerInputChange ->
                    offset = updateOffset(pointerInputChange)
                    onDown?.invoke(offset)
                },
                onMove = { pointerInputChange: PointerInputChange ->
                    offset = updateOffset(pointerInputChange)
                    onMove?.invoke(offset)
                },
                onUp = {
                    onUp?.invoke()
                }
            )

        ThumbnailLayout(
            modifier = thumbnailModifier.size(this.imageWidth, this.imageHeight),
            imageBitmap = scaledImageBitmap,
            thumbnailState = thumbnailState,
            offset = offset,
            onThumbnailCenterChange = onThumbnailCenterChange
        )

        Box(
            modifier = Modifier
                .size(this.imageWidth, this.imageHeight),
        ) {
            imageScope.content()
        }
    }
}

/**
 * [ThumbnailLayout] displays thumbnail of bitmap it draws in corner specified
 * by [ThumbnailState.position]. When touch position is close to thumbnail
 * position if [ThumbnailState.dynamicPosition]
 * is set to true moves thumbnail to corner specified by [ThumbnailState.moveTo]
 *
 * @param imageBitmap The [ImageBitmap] to draw
 * into the destination. The default is [FilterQuality.Low] which scales using a bilinear
 * sampling algorithm
 *
 * @param onThumbnailCenterChange callback to get center of thumbnail
 */
@Composable
private fun ThumbnailLayout(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    thumbnailState: ThumbnailState,
    alpha: Float = 1.0f,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    offset: Offset,
    onThumbnailCenterChange: ((Offset) -> Unit)? = null
) {
    ThumbnailLayoutImpl(
        modifier = modifier,
        imageBitmap = imageBitmap,
        thumbnailState = thumbnailState,
        offset = offset,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        onThumbnailCenterChange = onThumbnailCenterChange
    )
}

@Composable
private fun ThumbnailLayoutImpl(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    thumbnailState: ThumbnailState,
    offset: Offset,
    alpha: Float = 1.0f,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    onThumbnailCenterChange: ((Offset) -> Unit)? = null
) {
    val thumbnailSize = thumbnailState.size
    val thumbnailPosition = thumbnailState.position
    val dynamicPosition = thumbnailState.dynamicPosition
    val moveTo = thumbnailState.moveTo
    val thumbnailZoom = thumbnailState.thumbnailZoom

    BoxWithConstraints(modifier) {

        val canvasWidth = constraints.maxWidth.toFloat()
        val canvasHeight = constraints.maxHeight.toFloat()

        val thumbnailWidthInPx: Float
        val thumbnailHeightInPx: Float


        with(LocalDensity.current) {
            thumbnailWidthInPx = thumbnailSize.width.toPx()
            thumbnailHeightInPx = thumbnailSize.height.toPx()
        }

        // Get thumbnail size as parameter but limit max size to minimum of canvasWidth and Height
        val imageThumbnailWidth: Int = thumbnailWidthInPx.coerceAtMost(canvasWidth).roundToInt()
        val imageThumbnailHeight: Int = thumbnailHeightInPx.coerceAtMost(canvasHeight).roundToInt()

        val thumbnailOffset = getThumbnailPositionOffset(
            offset = offset,
            canvasWidth = canvasWidth,
            canvasHeight = canvasHeight,
            imageThumbnailWidth = imageThumbnailWidth,
            imageThumbnailHeight = imageThumbnailHeight,
            thumbnailPosition = thumbnailPosition,
            dynamicPosition = dynamicPosition,
            moveTo = moveTo
        )

        // Center of  thumbnail
        val centerX: Float = thumbnailOffset.x + imageThumbnailWidth / 2f
        val centerY: Float = thumbnailOffset.y + imageThumbnailHeight / 2f
        onThumbnailCenterChange?.invoke(Offset(centerX, centerY))

        Canvas(modifier = Modifier
            .offset {
                thumbnailOffset
            }
            .then(
                thumbnailState.shadow?.let { shadow: MaterialShadow ->
                    Modifier.shadow(
                        elevation = shadow.elevation,
                        shape = thumbnailState.shape,
                        ambientColor = shadow.ambientShadowColor,
                        spotColor = shadow.spotColor
                    )
                } ?: Modifier
            )
            .then(
                thumbnailState.border?.let { border: Border ->
                    Modifier.border(
                        width = border.strokeWidth,
                        shape = thumbnailState.shape,
                        brush = border.color
                    )
                } ?: Modifier
            )
            .size(thumbnailSize)
        ) {

            val zoom = thumbnailZoom.coerceAtLeast(100)
            val zoomScale = zoom / 100f

            val srcOffset = if (offset.isSpecified && offset.isFinite) {
                getSrcOffset(
                    offset = offset,
                    imageBitmap = imageBitmap,
                    zoomScale = zoomScale,
                    size = Size(canvasWidth, canvasHeight),
                    imageThumbnailSize = imageThumbnailWidth
                )
            } else {
                IntOffset.Zero
            }


            drawImage(
                image = imageBitmap,
                srcOffset = srcOffset,
                srcSize = IntSize(
                    width = (imageThumbnailWidth / zoomScale).toInt(),
                    height = (imageThumbnailWidth / zoomScale).toInt()
                ),
                dstSize = IntSize(imageThumbnailWidth, imageThumbnailWidth),
                alpha = alpha,
                colorFilter = colorFilter,
                filterQuality = filterQuality,
            )
        }
    }
}

private fun getThumbnailPositionOffset(
    offset: Offset,
    canvasWidth: Float,
    canvasHeight: Float,
    thumbnailPosition: ThumbnailPosition = ThumbnailPosition.TopLeft,
    dynamicPosition: Boolean = true,
    moveTo: ThumbnailPosition = ThumbnailPosition.TopRight,
    imageThumbnailWidth: Int,
    imageThumbnailHeight: Int
): IntOffset {

    val thumbnailOffset = calculateThumbnailOffset(
        thumbnailPosition,
        canvasWidth,
        canvasHeight,
        imageThumbnailWidth,
        imageThumbnailHeight
    )

    if (offset.isUnspecified || !offset.isFinite) return thumbnailOffset
    if (!dynamicPosition || thumbnailPosition == moveTo) return thumbnailOffset

    val offsetX = offset.x
        .coerceIn(0f, canvasWidth)
    val offsetY = offset.y
        .coerceIn(0f, canvasHeight)

    // Calculate distance from touch position to center of thumbnail
    val x = offsetX - (thumbnailOffset.x + imageThumbnailWidth / 2)
    val y = offsetY - (thumbnailOffset.y + imageThumbnailHeight / 2)
    val distanceToThumbnailCenter = (x * x + y * y)

    // pointer position is in bounds of thumbnail, calculate alternative position to move to
    return if (distanceToThumbnailCenter < imageThumbnailWidth * imageThumbnailHeight) {
        calculateThumbnailOffset(
            moveTo,
            canvasWidth,
            canvasHeight,
            imageThumbnailWidth,
            imageThumbnailHeight
        )
    } else {
        thumbnailOffset
    }
}

/**
 * Calculate thumbnail position based on which corner it's in
 */
private fun calculateThumbnailOffset(
    thumbnailPosition: ThumbnailPosition,
    canvasWidth: Float,
    canvasHeight: Float,
    imageThumbnailWidth: Int,
    imageThumbnailHeight: Int
): IntOffset {
    return when (thumbnailPosition) {
        ThumbnailPosition.TopLeft -> {
            IntOffset(x = 0, y = 0)
        }

        ThumbnailPosition.TopRight -> {
            IntOffset(x = (canvasWidth - imageThumbnailWidth).toInt(), y = 0)
        }

        ThumbnailPosition.BottomLeft -> {
            IntOffset(x = 0, y = (canvasHeight - imageThumbnailHeight).toInt())
        }

        ThumbnailPosition.BottomRight -> {
            IntOffset(
                x = (canvasWidth - imageThumbnailWidth).toInt(),
                y = (canvasHeight - imageThumbnailHeight).toInt()
            )
        }
    }
}

/**
 * Get offset for Src. Src is the bitmap that will be drawn to canvas. Based on it's
 * size and offset any section or whole bitmap can be drawn.
 * Setting positive offset on x axis moves visible section of bitmap to the left.
 * @param offset pointer touch position
 * @param imageBitmap is image that will be drawn
 * @param zoomScale scale of zoom between [1f-5f]
 */
private fun getSrcOffset(
    offset: Offset,
    imageBitmap: ImageBitmap,
    zoomScale: Float,
    size: Size,
    imageThumbnailSize: Int
): IntOffset {

    val canvasWidth = size.width
    val canvasHeight = size.height

    val bitmapWidth = imageBitmap.width
    val bitmapHeight = imageBitmap.height

    val offsetX = offset.x
        .coerceIn(0f, canvasWidth)
    val offsetY = offset.y
        .coerceIn(0f, canvasHeight)

    // Setting offset for src moves the position in Bitmap
    // Bitmap is SRC while where we draw is DST.
    // Setting offset of dst moves where we draw in Canvas
    // Setting src moves to which part of the bitmap we draw
    // Coercing at right bound (bitmap.width - imageThumbnailSize) lets to limit offset
    // to thumbnailSize when user moves pointer to right.
    // If image has 100px width and thumbnail 10 when user moves to 95 we see a width with 5px
    // coercing lets you keep 10px all the time
    val srcOffsetX =
        (offsetX * bitmapWidth / canvasWidth - imageThumbnailSize / zoomScale / 2)
            .coerceIn(0f, bitmapWidth - imageThumbnailSize / zoomScale)
    val srcOffsetY =
        (offsetY * bitmapHeight / canvasHeight - imageThumbnailSize / zoomScale / 2)
            .coerceIn(0f, bitmapHeight - imageThumbnailSize / zoomScale)

    return IntOffset(srcOffsetX.toInt(), srcOffsetY.toInt())
}

enum class ThumbnailPosition {
    TopLeft, TopRight, BottomLeft, BottomRight
}

/**
 * Creates and stores UI properties for [ImageWithThumbnail].
 * @param size size of the thumbnail
 * @param position position of the thumbnail. It's top left corner by default
 * @param dynamicPosition flag that changes mobility of thumbnail when user touch is
 * in proximity of the thumbnail
 * @param moveTo corner to move thumbnail if user touch is in proximity of the thumbnail. By default
 * it's top right corner.
 * @param thumbnailZoom zoom amount of thumbnail. It's in range of [100-500]. 100 corresponds
 * @param shape of the thumbnail
 * @param shadow if not null draws shadow behind thumbnail with given [shape]
 * @param border if not null draws border around thumbnail with given [shape]
 */
@Composable
fun rememberThumbnailState(
    size: DpSize = DpSize(80.dp, 80.dp),
    position: ThumbnailPosition = ThumbnailPosition.TopLeft,
    dynamicPosition: Boolean = true,
    moveTo: ThumbnailPosition = ThumbnailPosition.TopRight,
    @IntRange(from = 100, to = 500) thumbnailZoom: Int = 200,
    shape: Shape = RoundedCornerShape(8.dp),
    shadow: MaterialShadow = MaterialShadow(
        elevation = 2.dp,
        ambientShadowColor = DefaultShadowColor,
        spotColor = DefaultShadowColor
    ),
    border: Border? = null,
): ThumbnailState {

    return remember {
        ThumbnailState(
            size = size,
            position = position,
            dynamicPosition = dynamicPosition,
            moveTo = moveTo,
            thumbnailZoom = thumbnailZoom,
            shape = shape,
            shadow = shadow,
            border = border
        )
    }
}

@Immutable
data class ThumbnailState internal constructor(
    @Stable
    val size: DpSize = DpSize(80.dp, 80.dp),
    @Stable
    val position: ThumbnailPosition = ThumbnailPosition.TopLeft,
    @Stable
    val dynamicPosition: Boolean = true,
    @Stable
    val moveTo: ThumbnailPosition = ThumbnailPosition.TopRight,
    @Stable
    @IntRange(from = 100, to = 500) val thumbnailZoom: Int = 200,
    val shape: Shape = RoundedCornerShape(8.dp),
    @Stable
    val shadow: MaterialShadow? = MaterialShadow(
        elevation = 2.dp,
        ambientShadowColor = DefaultShadowColor,
        spotColor = DefaultShadowColor
    ),
    @Stable
    val border: Border? = null
)

@Immutable
data class MaterialShadow(
    @Stable
    val elevation: Dp = 2.dp,
    @Stable
    val ambientShadowColor: Color = DefaultShadowColor,
    @Stable
    val spotColor: Color = DefaultShadowColor,
)

@Composable
fun Border(
    color: Color,
    strokeWidth: Dp,
): Border {
    return Border(strokeWidth = strokeWidth, color = SolidColor(color))
}

@Composable
fun Border(
    brush: Brush,
    strokeWidth: Dp
): Border {
    return Border(strokeWidth = strokeWidth, color = brush)
}

@Immutable
data class Border internal constructor(
    @Stable
    val strokeWidth: Dp,
    @Stable
    val color: Brush
)

val DefaultShadowColor = Color.Black
