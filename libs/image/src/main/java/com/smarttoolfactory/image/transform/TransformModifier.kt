package com.smarttoolfactory.image.transform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChange
import com.smarttoolfactory.gesture.pointerMotionEvents
import com.smarttoolfactory.image.util.getDistanceToEdgeFromTouch
import com.smarttoolfactory.image.util.getTouchRegion

internal fun Modifier.transform(
    enabled: Boolean,
    size: Size,
    touchRegionRadius: Float,
    minDimension: Float,
    handlePlacement: HandlePlacement,
    transform: Transform,
    onDown: (Transform, Rect) -> Unit,
    onMove: (Transform, Rect) -> Unit,
    onUp: (Transform, Rect) -> Unit,
) = composed(

    factory = {

        // This rectangle is the initial rectangle
        // It's used for scaling touch position from scaled Composable to screen Coordinates
        // No matter what scale a Composable has it's bottom end is always returns (size,width)
        // of the initial rectangle or Size of Composable
        val rectBounds by remember {
            mutableStateOf(
                Rect(
                    offset = Offset.Zero,
                    size = size
                )
            )
        }

        // Rectangle for drawing borders of Composable that contains
        // scaled and translated  values
        var rectDraw by remember {
            mutableStateOf(rectBounds.copy())
        }

        var currentTransform by remember {
            mutableStateOf(transform)
        }

        // This rectangle is needed to set bounds set at first touch position while
        // moving to constraint current bounds to temp one from first down
        // When pointer is up
        var rectTemp by remember { mutableStateOf(rectBounds.copy()) }

        var touchRegion by remember(enabled) { mutableStateOf(TouchRegion.None) }

        // This is the real position of touch in screen, when scaled
        // right bottom corner of 1000x1000px Composable is always (1000,1000)
        // but we need screen coordinates to set draw rectangle after scaling and translating
        // rectDraw is drawn based on touch position on screen
        var touchPositionOnScreen: Offset

        // Touch position for edge of the rectangle, used for not jumping to edge of rectangle
        // when user moves a handle. We set positionActual as position of selected handle
        // and using this distance as offset to not have a jump from touch position

        var distanceToEdgeFromTouch = Offset.Zero

        pointerMotionEvents(
            onDown = { change: PointerInputChange ->

                if (enabled) {
                    rectTemp = rectDraw.copy()

                    // This is the position inside Composable, it doesn't depend on scale
                    // or translation bottom end of 1000x1000 Composable is
                    // always returns (1000,1000)
                    val position = change.position

                    // Get touch position on device screen
                    // It's calculated by current position of drawing rectangle and
                    // ratio of drawing rectangle to initial rectangle.
                    // When scale is 0.5 for a Composable with 1000x1000 Size
                    // for touch position (1000,1000) position in Composable
                    // we should get (500,500) on screen
                    val touchPositionScreenX =
                        rectDraw.left + position.x * rectDraw.width / rectBounds.width
                    val touchPositionScreenY =
                        rectDraw.top + position.y * rectDraw.height / rectBounds.height

                    touchPositionOnScreen = Offset(touchPositionScreenX, touchPositionScreenY)

                    touchRegion = getTouchRegion(
                        position = touchPositionOnScreen,
                        rect = rectDraw,
                        handlePlacement = handlePlacement,
                        threshold = touchRegionRadius * 2
                    )

                    // This is the difference between touch position and edge
                    // This is required for not moving edge of draw rect to touch position on move
                    distanceToEdgeFromTouch =
                        getDistanceToEdgeFromTouch(touchRegion, rectTemp, touchPositionOnScreen)

                    onDown(currentTransform, rectDraw)
                }
            },
            onMove = { change: PointerInputChange ->

                if (!enabled) return@pointerMotionEvents

                val position = change.position

                // Get screen coordinates from touch position inside composable
                // Left side of the rectangle is our start position for screen because
                // when translated top left always is equal to translation amount
                // Then we scale touch in Composable based on it's current scale
                // 1000px Composable with 0.5 scale should return (500,500) coordinates when
                // its bottom right is touched. Without scaling it always returns (width, height)
                val screenPositionX =
                    rectDraw.left + position.x * rectDraw.width / rectBounds.width +
                            distanceToEdgeFromTouch.x
                val screenPositionY =
                    rectDraw.top + position.y * rectDraw.height / rectBounds.height +
                            distanceToEdgeFromTouch.y

                when (touchRegion) {

                    // Corners
                    TouchRegion.TopLeft -> {

                        // Set position of top left while moving with top left handle and
                        // limit position to not intersect other handles
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtMost(rectTemp.right - minDimension),
                            screenPositionY.coerceAtMost(rectTemp.bottom - minDimension)
                        )

                        rectDraw = Rect(
                            left = touchPositionOnScreen.x,
                            top = touchPositionOnScreen.y,
                            right = rectTemp.right,
                            bottom = rectTemp.bottom
                        )

                        // TransformOrion is center of composable, by changing
                        // it to 0,0 only for translation we move the composable
                        // from left and top
                        val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                        val verticalCenter = (rectDraw.height - rectBounds.height) / 2

                        // Ratio of dimensions of 
                        // rectangle drawn(It's the outer rectangle that covers handles)
                        // to initial rectangle of this Composable
                        val widthRatio = rectDraw.width / rectBounds.width
                        val heightRatio = rectDraw.height / rectBounds.height

                        // TransformationOrigin of Composable is center
                        // so need to take center into consideration
                        // to translate from handle position
                        val xTranslation = touchPositionOnScreen.x + horizontalCenter
                        val yTranslation = touchPositionOnScreen.y + verticalCenter

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            translationY = yTranslation,
                            scaleX = widthRatio,
                            scaleY = heightRatio
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.BottomLeft -> {

                        // Set position of top left while moving with bottom left handle and
                        // limit position to not intersect other handles
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtMost(rectTemp.right - minDimension),
                            screenPositionY.coerceAtLeast(rectTemp.top + minDimension)
                        )

                        rectDraw = Rect(
                            left = touchPositionOnScreen.x,
                            top = rectTemp.top,
                            right = rectTemp.right,
                            bottom = touchPositionOnScreen.y,
                        )

                        // TransformOrion is center of composable, by changing
                        // it to 0,0 only for translation we move the composable
                        // from left and bottom
                        val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                        val verticalCenter = (rectDraw.height - rectBounds.height) / 2

                        // Ratio of dimensions of 
                        // rectangle drawn(It's the outer rectangle that covers handles)
                        // to initial rectangle of this Composable
                        val widthRatio = rectDraw.width / rectBounds.width
                        val heightRatio = rectDraw.height / rectBounds.height

                        // TransformationOrigin of Composable is center
                        // so need to take center into consideration
                        // to translate from handle position
                        val xTranslation = touchPositionOnScreen.x + horizontalCenter
                        val yTranslation =
                            touchPositionOnScreen.y - rectDraw.height + verticalCenter

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            translationY = yTranslation,
                            scaleX = widthRatio,
                            scaleY = heightRatio
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.TopRight -> {

                        // Set position of top left while moving with top right handle and
                        // limit position to not intersect other handles
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtLeast(rectTemp.left + minDimension),
                            screenPositionY.coerceAtMost(rectTemp.bottom - minDimension)
                        )

                        rectDraw = Rect(
                            left = rectTemp.left,
                            top = touchPositionOnScreen.y,
                            right = touchPositionOnScreen.x,
                            bottom = rectTemp.bottom,
                        )

                        // TransformOrion is center of composable, by changing
                        // it to 0,0 only for translation we move the composable
                        // from top and right
                        val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                        val verticalCenter = (rectDraw.height - rectBounds.height) / 2

                        // Ratio of dimensions of 
                        // rectangle drawn(It's the outer rectangle that covers handles)
                        // to initial rectangle of this Composable
                        val widthRatio = rectDraw.width / rectBounds.width
                        val heightRatio = rectDraw.height / rectBounds.height

                        // TransformationOrigin of Composable is center
                        // so need to take center into consideration
                        // to translate from handle position
                        val xTranslation =
                            touchPositionOnScreen.x - rectDraw.width + horizontalCenter
                        val yTranslation = touchPositionOnScreen.y + verticalCenter

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            translationY = yTranslation,
                            scaleX = widthRatio,
                            scaleY = heightRatio
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.BottomRight -> {

                        // Set position of top left while moving with bottom right handle and
                        // limit position to not intersect other handles
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtLeast(rectTemp.left + minDimension),
                            screenPositionY.coerceAtLeast(rectTemp.top + minDimension)
                        )

                        rectDraw = Rect(
                            left = rectTemp.left,
                            top = rectTemp.top,
                            right = touchPositionOnScreen.x,
                            bottom = touchPositionOnScreen.y,
                        )

                        // TransformOrion is center of composable, by changing
                        // it to 0,0 only for translation we move the composable
                        // from right and bottom
                        val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                        val verticalCenter = (rectDraw.height - rectBounds.height) / 2

                        // Ratio of dimensions of 
                        // rectangle drawn(It's the outer rectangle that covers handles)
                        // to initial rectangle of this Composable
                        val widthRatio = rectDraw.width / rectBounds.width
                        val heightRatio = rectDraw.height / rectBounds.height

                        // TransformationOrigin of Composable is center
                        // so need to take center into consideration
                        // to translate from handle position
                        val xTranslation =
                            touchPositionOnScreen.x - rectDraw.width + horizontalCenter
                        val yTranslation =
                            touchPositionOnScreen.y - rectDraw.height + verticalCenter

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            translationY = yTranslation,
                            scaleX = widthRatio,
                            scaleY = heightRatio
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    // Sides
                    TouchRegion.CenterLeft -> {

                        // Set position of left while moving with left center handle and
                        // limit position to not intersect other handles
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtMost(rectTemp.right - minDimension),
                            screenPositionY.coerceAtMost(rectTemp.bottom - minDimension)
                        )

                        rectDraw = rectDraw.copy(left = touchPositionOnScreen.x)

                        val horizontalCenter = (rectDraw.width - rectBounds.width) / 2
                        val widthRatio = rectDraw.width / rectBounds.width
                        val xTranslation = touchPositionOnScreen.x + horizontalCenter

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            scaleX = widthRatio,
                        )
                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.TopCenter -> {

                        // Set position of top while moving with top center handle and
                        // limit position to not intersect other handles
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtMost(rectTemp.right - minDimension),
                            screenPositionY.coerceAtMost(rectTemp.bottom - minDimension)
                        )

                        rectDraw = rectDraw.copy(top = touchPositionOnScreen.y)

                        // TransformOrion is center of composable, by changing
                        // it to 0,0 only for translation we move the composable
                        // from left and top
                        val verticalCenter = (rectDraw.height - rectBounds.height) / 2

                        // Ratio of dimensions of 
                        // rectangle drawn(It's the outer rectangle that covers handles)
                        // to initial rectangle of this Composable
                        val heightRatio = rectDraw.height / rectBounds.height

                        // TransformationOrigin of Composable is center
                        // so need to take center into consideration
                        // to translate from handle position
                        val yTranslation = touchPositionOnScreen.y + verticalCenter

                        currentTransform = currentTransform.copy(
                            translationY = yTranslation,
                            scaleY = heightRatio
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.CenterRight -> {

                        // Set position of right while moving with right center handle and
                        // limit position to not intersect other handles
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtLeast(rectTemp.left + minDimension),
                            screenPositionY.coerceAtLeast(rectTemp.top + minDimension)
                        )

                        rectDraw = rectDraw.copy(right = touchPositionOnScreen.x)

                        // TransformOrion is center of composable, by changing
                        // it to 0,0 only for translation we move the composable
                        // from right and bottom
                        val horizontalCenter = (rectDraw.width - rectBounds.width) / 2

                        // Ratio of dimensions of 
                        // rectangle drawn(It's the outer rectangle that covers handles)
                        // to initial rectangle of this Composable
                        val widthRatio = rectDraw.width / rectBounds.width

                        // TransformationOrigin of Composable is center
                        // so need to take center into consideration
                        // to translate from handle position
                        val xTranslation =
                            touchPositionOnScreen.x - rectDraw.width + horizontalCenter

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            scaleX = widthRatio
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.BottomCenter -> {

                        // Set position of bottom while moving with bottom center handle and
                        // limit position to not intersect other handles
                        touchPositionOnScreen = Offset(
                            screenPositionX.coerceAtLeast(rectTemp.left + minDimension),
                            screenPositionY.coerceAtLeast(rectTemp.top + minDimension)
                        )

                        rectDraw = rectDraw.copy(bottom = touchPositionOnScreen.y)

                        // TransformOrion is center of composable, by changing
                        // it to 0,0 only for translation we move the composable
                        // from right and bottom
                        val verticalCenter = (rectDraw.height - rectBounds.height) / 2

                        // Ratio of dimensions of 
                        // rectangle drawn(It's the outer rectangle that covers handles)
                        // to initial rectangle of this Composable
                        val heightRatio = rectDraw.height / rectBounds.height

                        // TransformationOrigin of Composable is center
                        // so need to take center into consideration
                        // to translate from handle position
                        val yTranslation =
                            touchPositionOnScreen.y - rectDraw.height + verticalCenter

                        currentTransform = currentTransform.copy(
                            translationY = yTranslation,
                            scaleY = heightRatio
                        )
                        onMove(currentTransform, rectDraw)
                    }

                    TouchRegion.Inside -> {
                        val drag = change.positionChange()

                        val scaledDragX = drag.x * rectDraw.width / rectBounds.width
                        val scaledDragY = drag.y * rectDraw.height / rectBounds.height

                        val xTranslation = currentTransform.translationX + scaledDragX
                        val yTranslation = currentTransform.translationY + scaledDragY

                        rectDraw = rectDraw.translate(scaledDragX, scaledDragY)

                        currentTransform = currentTransform.copy(
                            translationX = xTranslation,
                            translationY = yTranslation
                        )

                        onMove(currentTransform, rectDraw)
                    }

                    else -> Unit
                }

                if (touchRegion != TouchRegion.None) {
                    change.consume()
                }
            },
            onUp = {
                touchRegion = TouchRegion.None
                rectTemp = rectDraw.copy()
                onUp(currentTransform, rectDraw)
            },
            key1 = enabled
        )
    },
    inspectorInfo = {
        name = "transform"
        // add name and value of each argument
        properties["enabled"] = enabled
        properties["size"] = size
        properties["touchRegionRadius"] = touchRegionRadius
        properties["minDimension"] = minDimension
        properties["transform"] = transform
        properties["onDown"] = onDown
        properties["onMove"] = onMove
        properties["onUp"] = onUp
    }
)
