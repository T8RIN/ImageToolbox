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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import com.t8rin.gesture.detectMotionEvents

@Composable
internal fun Layout(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 100.0) progress: () -> Float = { 50f },
    sharedProgress: MutableFloatState? = null,
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
        mainContent = beforeContent,
        dependentContent = { contentSize ->
            BeforeAfterSplitLayout(
                contentSize = contentSize,
                progress = progress,
                sharedProgress = sharedProgress,
                onProgressChange = onProgressChange,
                enableProgressWithTouch = enableProgressWithTouch,
                contentOrder = contentOrder,
                beforeContent = beforeContent,
                afterContent = afterContent,
                beforeLabel = beforeLabel,
                afterLabel = afterLabel,
                overlay = overlay,
            )
        }
    )
}

@Composable
private fun BeforeAfterSplitLayout(
    contentSize: Size,
    progress: () -> Float,
    sharedProgress: MutableFloatState?,
    onProgressChange: ((Float) -> Unit)?,
    enableProgressWithTouch: Boolean,
    contentOrder: ContentOrder,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable (BoxScope.() -> Unit)?,
    afterLabel: @Composable (BoxScope.() -> Unit)?,
    overlay: @Composable ((DpSize, Offset) -> Unit)?,
) {
    val boxWidth = contentSize.width
    val boxHeight = contentSize.height

    val boxWidthInDp = with(LocalDensity.current) { boxWidth.toDp() }
    val boxHeightInDp = with(LocalDensity.current) { boxHeight.toDp() }

    fun scaleToUserValue(offset: Float) =
        scale(0f, boxWidth, offset, 0f, 100f)

    fun scaleToOffset(userValue: Float) =
        scale(0f, 100f, userValue, 0f, boxWidth)

    var rawOffset by remember(boxWidth, boxHeight) {
        mutableStateOf(
            Offset(
                x = scaleToOffset(progress()),
                y = boxHeight / 2f,
            )
        )
    }

    var isHandleTouched by remember { mutableStateOf(false) }

    LaunchedEffect(boxWidth, boxHeight, progress, sharedProgress) {
        if (sharedProgress != null) return@LaunchedEffect
        snapshotFlow {
            progress() to isHandleTouched
        }.collect { (value, isTouched) ->
            if (!isTouched) {
                rawOffset = rawOffset.copy(x = scaleToOffset(value))
            }
        }
    }

    val clipX = if (sharedProgress != null && !isHandleTouched) {
        scaleToOffset(sharedProgress.floatValue)
    } else {
        rawOffset.x
    }

    val overlayOffset = if (isHandleTouched) {
        rawOffset
    } else {
        Offset(x = clipX, y = rawOffset.y)
    }

    val touchModifier = Modifier.pointerInput(boxWidth, boxHeight) {
        detectMotionEvents(
            onDown = {
                val position = it.position
                val xPos = position.x
                val currentX = if (sharedProgress != null && !isHandleTouched) {
                    scaleToOffset(sharedProgress.floatValue)
                } else {
                    rawOffset.x
                }

                isHandleTouched =
                    ((currentX - xPos) * (currentX - xPos) < 5000)

                if (isHandleTouched) {
                    rawOffset = Offset(
                        x = currentX,
                        y = boxHeight / 2f,
                    )
                }
            },
            onMove = {
                if (isHandleTouched) {
                    rawOffset = it.position
                    sharedProgress?.floatValue = scaleToUserValue(rawOffset.x)
                    it.consume()
                }
            },
            onUp = {
                if (isHandleTouched) {
                    onProgressChange?.invoke(scaleToUserValue(rawOffset.x))
                }
                isHandleTouched = false
            }
        )
    }

    val parentModifier = Modifier
        .size(boxWidthInDp, boxHeightInDp)
        .clipToBounds()
        .then(if (enableProgressWithTouch) touchModifier else Modifier)

    val beforeClipModifier = Modifier
        .fillMaxSize()
        .drawWithContent {
            clipRect(left = 0f, top = 0f, right = clipX, bottom = size.height) {
                this@drawWithContent.drawContent()
            }
        }

    val afterClipModifier = Modifier
        .fillMaxSize()
        .drawWithContent {
            clipRect(left = clipX, top = 0f, right = size.width, bottom = size.height) {
                this@drawWithContent.drawContent()
            }
        }

    Box(modifier = parentModifier) {
        val before = @Composable {
            Box(if (contentOrder == ContentOrder.BeforeAfter) beforeClipModifier else afterClipModifier) {
                beforeContent()
                beforeLabel?.invoke(this)
            }
        }

        val after = @Composable {
            Box(if (contentOrder == ContentOrder.BeforeAfter) afterClipModifier else beforeClipModifier) {
                afterContent()
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
        DpSize(boxWidthInDp, boxHeightInDp), overlayOffset
    )
}
