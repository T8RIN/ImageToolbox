/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.feature.compare.presentation.components.beforeafter

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import com.t8rin.gesture.detectMotionEvents


@Composable
internal fun Layout(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    enableProgressWithTouch: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable (BoxScope.() -> Unit)?,
    afterLabel: @Composable (BoxScope.() -> Unit)?,
    overlay: @Composable ((DpSize, Offset) -> Unit)?
) {
    DimensionSubcomposeLayout(
        modifier = modifier,
        placeMainContent = false,
        mainContent = {
            beforeContent()
        },
        dependentContent = { contentSize: Size ->

            val boxWidth = contentSize.width
            val boxHeight = contentSize.height

            val boxWidthInDp: Dp
            val boxHeightInDp: Dp

            with(LocalDensity.current) {
                boxWidthInDp = boxWidth.toDp()
                boxHeightInDp = boxHeight.toDp()
            }

            // Sales and interpolates from offset from dragging to user value in valueRange
            fun scaleToUserValue(offset: Float) =
                scale(0f, boxWidth, offset, 0f, 100f)

            // Scales user value using valueRange to position on x axis on screen
            fun scaleToOffset(userValue: Float) =
                scale(0f, 100f, userValue, 0f, boxWidth)

            var rawOffset by remember {
                mutableStateOf(
                    Offset(
                        x = scaleToOffset(progress),
                        y = boxHeight / 2f,
                    )
                )
            }

            rawOffset = rawOffset.copy(x = scaleToOffset(progress))

            var isHandleTouched by remember { mutableStateOf(false) }

            val touchModifier = Modifier.pointerInput(Unit) {
                detectMotionEvents(
                    onDown = {
                        val position = it.position
                        val xPos = position.x

                        isHandleTouched =
                            ((rawOffset.x - xPos) * (rawOffset.x - xPos) < 5000)
                    },
                    onMove = {
                        if (isHandleTouched) {
                            rawOffset = it.position
                            onProgressChange?.invoke(
                                scaleToUserValue(rawOffset.x)
                            )
                            it.consume()
                        }
                    },
                    onUp = {
                        isHandleTouched = false
                    }
                )
            }

            val handlePosition = rawOffset.x

            val shapeBefore by remember(handlePosition) {
                mutableStateOf(
                    GenericShape { size: Size, _: LayoutDirection ->
                        moveTo(0f, 0f)
                        lineTo(handlePosition, 0f)
                        lineTo(handlePosition, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                )
            }

            val shapeAfter by remember(handlePosition) {
                mutableStateOf(
                    GenericShape { size: Size, _: LayoutDirection ->
                        moveTo(handlePosition, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        lineTo(handlePosition, size.height)
                        close()
                    }
                )
            }

            val parentModifier = Modifier
                .size(boxWidthInDp, boxHeightInDp)
                .clipToBounds()
                .then(if (enableProgressWithTouch) touchModifier else Modifier)


            val beforeModifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.clip = true
                    this.shape = shapeBefore
                }


            val afterModifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.clip = true
                    this.shape = shapeAfter
                }

            LayoutImpl(
                modifier = parentModifier,
                beforeModifier = beforeModifier,
                afterModifier = afterModifier,
                graphicsModifier = Modifier,
                beforeContent = beforeContent,
                afterContent = afterContent,
                beforeLabel = beforeLabel,
                afterLabel = afterLabel,
                overlay = overlay,
                contentOrder = contentOrder,
                boxWidthInDp = boxWidthInDp,
                boxHeightInDp = boxHeightInDp,
                rawOffset = rawOffset
            )
        }
    )
}

@Composable
private fun LayoutImpl(
    modifier: Modifier,
    beforeModifier: Modifier,
    afterModifier: Modifier,
    graphicsModifier: Modifier,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable (BoxScope.() -> Unit)? = null,
    afterLabel: @Composable (BoxScope.() -> Unit)? = null,
    overlay: @Composable ((DpSize, Offset) -> Unit)? = null,
    contentOrder: ContentOrder,
    boxWidthInDp: Dp,
    boxHeightInDp: Dp,
    rawOffset: Offset,
) {
    Box(modifier = modifier) {

        // BEFORE
        val before = @Composable {
            Box(if (contentOrder == ContentOrder.BeforeAfter) beforeModifier else afterModifier) {
                Box(
                    modifier = Modifier.then(graphicsModifier)
                ) {
                    beforeContent()
                }
                beforeLabel?.invoke(this)
            }
        }

        // AFTER
        val after = @Composable {
            Box(if (contentOrder == ContentOrder.BeforeAfter) afterModifier else beforeModifier) {
                Box(
                    modifier = Modifier.then(graphicsModifier)
                ) {
                    afterContent()
                }
                afterLabel?.invoke(this)
            }
        }

        if (contentOrder == ContentOrder.BeforeAfter) {
            before()
            after()
        } else {
            after()
            before()
        }
    }

    overlay?.invoke(
        DpSize(boxWidthInDp, boxHeightInDp), rawOffset
    )
}
