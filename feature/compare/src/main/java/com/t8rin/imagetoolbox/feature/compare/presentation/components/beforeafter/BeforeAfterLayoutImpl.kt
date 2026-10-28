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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import net.engawapg.lib.zoomable.ZoomState
import kotlin.math.abs
import kotlin.math.sign

@Composable
internal fun Layout(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 100.0) progress: () -> Float = { 50f },
    sharedProgress: MutableFloatState? = null,
    onProgressChange: ((Float) -> Unit)? = null,
    enableProgressWithTouch: Boolean = true,
    zoomState: ZoomState? = null,
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
                zoomState = zoomState,
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
    zoomState: ZoomState?,
    contentOrder: ContentOrder,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable (BoxScope.() -> Unit)?,
    afterLabel: @Composable (BoxScope.() -> Unit)?,
    overlay: @Composable ((DpSize, Offset) -> Unit)?,
) {
    val boxWidth = contentSize.width
    val boxHeight = contentSize.height
    val density = LocalDensity.current
    val boxWidthInDp = with(density) { boxWidth.toDp() }
    val boxHeightInDp = with(density) { boxHeight.toDp() }
    val touchRadius = with(density) { 24.dp.toPx() }

    fun scaleToUserValue(offset: Float) =
        scale(0f, boxWidth, offset, 0f, 100f)

    fun scaleToOffset(userValue: Float) =
        scale(0f, 100f, userValue, 0f, boxWidth)

    var dragOffset by remember { mutableFloatStateOf(0f) }
    var touchY by remember(boxHeight) { mutableFloatStateOf(boxHeight / 2f) }
    var isHandleTouched by remember { mutableStateOf(false) }

    val currentProgress = if (isHandleTouched) {
        scaleToUserValue(dragOffset)
    } else {
        (sharedProgress?.floatValue ?: progress()).coerceIn(0f, 100f)
    }
    val clipX = scaleToOffset(currentProgress)
    val currentClipX by rememberUpdatedState(clipX)
    val currentSharedProgress by rememberUpdatedState(sharedProgress)
    val currentOnProgressChange by rememberUpdatedState(onProgressChange)

    val touchModifier = Modifier.pointerInput(boxWidth, boxHeight, touchRadius, zoomState) {
        awaitEachGesture {
            val down = awaitFirstDown(pass = PointerEventPass.Initial)
            val initialX = currentClipX
            val zoomScale = zoomState?.scale ?: 1f
            val hitRadius = touchRadius / zoomScale
            val touchSlop = viewConfiguration.touchSlop / zoomScale
            val handleInset = minOf(hitRadius, boxHeight / 2f)
            val handleY = touchY.coerceIn(handleInset, boxHeight - handleInset)
            val nearDivider = abs(down.position.x - initialX) <= hitRadius
            val nearHandle = abs(down.position.y - handleY) <= hitRadius
            val isZoomed = zoomScale > 1f

            if (!nearDivider || (isZoomed && !nearHandle)) return@awaitEachGesture

            var previousPosition = down.position

            try {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    if (event.changes.count { it.pressed } > 1) break

                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (!change.pressed || change.isConsumed) break

                    val movement = change.position - down.position
                    if (!isHandleTouched) {
                        if (abs(movement.y) > touchSlop &&
                            abs(movement.y) > abs(movement.x)
                        ) break

                        if (abs(movement.x) <= touchSlop) continue

                        dragOffset = initialX + movement.x -
                                movement.x.sign * touchSlop
                        isHandleTouched = true
                    } else {
                        dragOffset += change.position.x - previousPosition.x
                    }

                    dragOffset = dragOffset.coerceIn(0f, boxWidth)
                    touchY = change.position.y
                    previousPosition = change.position
                    currentSharedProgress?.floatValue = scaleToUserValue(dragOffset)
                    change.consume()
                }
            } finally {
                if (isHandleTouched) {
                    isHandleTouched = false
                    currentOnProgressChange?.invoke(scaleToUserValue(dragOffset))
                }
            }
        }
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
        DpSize(boxWidthInDp, boxHeightInDp), Offset(clipX, touchY)
    )
}
