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

package com.t8rin.fastscroller

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt

class FastScrollerState internal constructor() {
    var draggedThumbCenter by mutableFloatStateOf(0f)
        internal set

    internal var scrollThumbCenter by mutableStateOf<() -> Float>({ 0f })

    val thumbCenter: Float
        get() = if (isDragging) draggedThumbCenter else scrollThumbCenter()

    var targetItemIndex by mutableIntStateOf(-1)
        internal set
    var isDragging by mutableStateOf(false)
        internal set
    var isVisible by mutableStateOf(false)
        internal set
}

@Composable
fun rememberFastScrollerState(): FastScrollerState = remember { FastScrollerState() }

@Composable
fun VerticalFastScroller(
    state: LazyGridState,
    fastScrollerState: FastScrollerState,
    modifier: Modifier = Modifier,
    thickness: Dp = 8.dp,
    minThumbHeight: Dp = 52.dp,
    thumbColor: Color,
    trackColor: Color = Color.Transparent,
    scrollByItemIndex: Boolean = false,
    autoHide: Boolean = false,
    thumbTouchWidth: Dp = thickness + 3.dp,
) {
    val density = LocalDensity.current
    val adapter = remember(state, scrollByItemIndex) { LazyGridAdapter(state, scrollByItemIndex) }
    LaunchedEffect(adapter, fastScrollerState) {
        if (scrollByItemIndex) {
            var lastRequestedIndex = -1
            snapshotFlow { fastScrollerState.targetItemIndex to fastScrollerState.isDragging }
                .collect { (index, dragging) ->
                    if (index < 0 && dragging) {
                        state.stopScroll()
                    } else if (index >= 0 && index != lastRequestedIndex) {
                        state.requestScrollToItem(index)
                        lastRequestedIndex = index
                    }
                    if (!dragging) lastRequestedIndex = -1
                }
        } else {
            adapter.collectScrollRequests()
        }
    }
    var trackHeight by remember { mutableIntStateOf(0) }
    val minThumbHeightPx = with(density) { minThumbHeight.toPx() }
    val thumbHeight = if (scrollByItemIndex) {
        minThumbHeightPx.coerceAtMost(trackHeight / 2f)
    } else {
        (trackHeight * adapter.visibleFraction).coerceAtLeast(minThumbHeightPx)
    }
    val maxThumbPosition = (trackHeight - thumbHeight).coerceAtLeast(0f)
    val scrollThumbCenter = remember(adapter, maxThumbPosition, thumbHeight) {
        { maxThumbPosition * adapter.scrollFraction + thumbHeight / 2f }
    }
    val isVisible = trackHeight > 0 && (state.canScrollBackward || state.canScrollForward)
    var showThumb by remember(autoHide) { mutableStateOf(!autoHide) }
    val isScrolling = state.isScrollInProgress || fastScrollerState.isDragging
    LaunchedEffect(autoHide, isScrolling) {
        if (!autoHide || isScrolling) {
            showThumb = true
        } else if (showThumb) {
            delay(900)
            showThumb = false
        }
    }
    val alpha by animateFloatAsState(
        targetValue = if (showThumb) 1f else 0f,
        animationSpec = tween(160)
    )
    val currentThumbHeight by rememberUpdatedState(thumbHeight)
    val currentMaxThumbPosition by rememberUpdatedState(maxThumbPosition)
    SideEffect {
        fastScrollerState.scrollThumbCenter = scrollThumbCenter
        fastScrollerState.isVisible = isVisible && showThumb
    }

    val dragModifier = Modifier.pointerInput(adapter, autoHide) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = autoHide)
            val gestureThumbHeight = currentThumbHeight
            val gestureMaxThumbPosition = currentMaxThumbPosition
            if (gestureMaxThumbPosition == 0f) return@awaitEachGesture
            if (autoHide) down.consume()

            val lastStartIndex = if (scrollByItemIndex) adapter.lastStartIndex else 0
            val lastItemIndex = if (scrollByItemIndex) adapter.lastItemIndex else 0
            val center = fastScrollerState.thumbCenter
            val isOnThumb = autoHide || abs(down.position.y - center) <= gestureThumbHeight / 2f
            var position = (if (isOnThumb) center else down.position.y) - gestureThumbHeight / 2f

            fun scrollToPosition() {
                val target = position.coerceIn(0f, gestureMaxThumbPosition)
                fastScrollerState.draggedThumbCenter = target + gestureThumbHeight / 2f
                val fraction = target / gestureMaxThumbPosition
                if (scrollByItemIndex) {
                    fastScrollerState.targetItemIndex = if (fraction == 1f) lastItemIndex
                    else (fraction * lastStartIndex).roundToInt().coerceAtMost(lastItemIndex)
                } else {
                    adapter.scrollToFractionOnTrack(fraction)
                }
            }

            try {
                fastScrollerState.targetItemIndex = -1
                fastScrollerState.draggedThumbCenter = center
                fastScrollerState.isDragging = true
                if (!autoHide) scrollToPosition()
                drag(down.id) { change ->
                    position += change.positionChange().y
                    scrollToPosition()
                    change.consume()
                }
            } finally {
                fastScrollerState.isDragging = false
            }
        }
    }

    Box(
        modifier = modifier
            .width(if (autoHide) maxOf(thickness, thumbTouchWidth) else thickness)
            .onSizeChanged { trackHeight = it.height }
            .then(
                if (isVisible && !autoHide) {
                    Modifier
                        .background(trackColor, MaterialTheme.shapes.extraLarge)
                        .then(dragModifier)
                } else Modifier
            )
    ) {
        if (isVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { thumbHeight.toDp() })
                    .offset {
                        IntOffset(
                            0,
                            (fastScrollerState.thumbCenter - thumbHeight / 2f).roundToInt()
                        )
                    }
                    .then(if (autoHide && showThumb) dragModifier else Modifier)
            ) {
                Box(
                    modifier = Modifier
                        .align(AbsoluteAlignment.CenterRight)
                        .width(thickness)
                        .fillMaxHeight()
                        .graphicsLayer { this.alpha = alpha }
                        .background(thumbColor, MaterialTheme.shapes.extraLarge)
                )
            }
        }
    }
}

private class LazyGridAdapter(
    private val state: LazyGridState,
    private val scrollByItemIndex: Boolean,
) {
    private val scrollRequests = Channel<Float>(Channel.CONFLATED)

    suspend fun collectScrollRequests() {
        for (fraction in scrollRequests) {
            scrollToFraction(fraction)
        }
    }

    private val layoutInfo get() = state.layoutInfo
    private val isVertical get() = layoutInfo.orientation == Orientation.Vertical
    private val visibleItems get() = layoutInfo.visibleItemsInfo
    private val slotsPerLine: Int
        get() {
            val vertical = isVertical
            var slots = 1
            for (item in visibleItems) {
                slots = maxOf(slots, (if (vertical) item.column else item.row) + 1)
            }
            return slots
        }
    private val firstVisibleItem: LazyGridItemInfo?
        get() = visibleItems.firstOrNull { if (isVertical) it.row >= 0 else it.column >= 0 }
    private val lineCount: Int
        get() = ((layoutInfo.totalItemsCount + slotsPerLine - 1) / slotsPerLine)
    private val averageLineSize: Double
        get() {
            val first = firstVisibleItem ?: return 0.0
            val firstLine = if (isVertical) first.row else first.column
            val last = visibleItems.lastOrNull() ?: return 0.0
            val lastLine = if (isVertical) last.row else last.column
            val lineCount = lastLine - firstLine + 1
            if (lineCount <= 0) return 0.0
            val firstOffset = if (isVertical) first.offset.y else first.offset.x
            val lastOffset = if (isVertical) last.offset.y else last.offset.x
            val lastSize = if (isVertical) last.size.height else last.size.width
            return (lastOffset + lastSize - firstOffset - (lineCount - 1) * layoutInfo.mainAxisItemSpacing).toDouble() / lineCount
        }
    val viewportSize: Double
        get() = if (isVertical) layoutInfo.viewportSize.height.toDouble() else layoutInfo.viewportSize.width.toDouble()
    val contentSize: Double
        get() = averageLineSize * lineCount + layoutInfo.mainAxisItemSpacing * (lineCount - 1).coerceAtLeast(
            0
        ) + layoutInfo.beforeContentPadding + layoutInfo.afterContentPadding
    val scrollOffset: Double
        get() {
            val item = firstVisibleItem ?: return 0.0
            val line = if (isVertical) item.row else item.column
            val offset = if (isVertical) item.offset.y else item.offset.x
            return line * (averageLineSize + layoutInfo.mainAxisItemSpacing) - offset
        }
    val lastItemIndex: Int
        get() = (layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
    val lastStartIndex: Int
        get() = (layoutInfo.totalItemsCount - visibleItems.size).coerceAtLeast(1)
    val visibleFraction: Float
        get() {
            val content = contentSize
            return if (content == 0.0) 1f else (viewportSize / content).coerceAtMost(1.0).toFloat()
        }
    val scrollFraction: Float
        get() = when {
            !state.canScrollBackward -> 0f
            !state.canScrollForward -> 1f
            scrollByItemIndex -> (state.firstVisibleItemIndex.toFloat() / lastStartIndex)
                .coerceAtMost(1f)

            else -> (scrollOffset / (contentSize - viewportSize).coerceAtLeast(1.0))
                .coerceIn(0.0, 1.0).toFloat()
        }

    fun scrollToFractionOnTrack(fraction: Float) {
        scrollRequests.trySend(fraction)
    }

    private suspend fun scrollToFraction(fraction: Float) {
        if (fraction == 0f || fraction == 1f) {
            state.scrollToItem(if (fraction == 0f) 0 else lastItemIndex)
            return
        }

        val targetOffset = fraction * (contentSize - viewportSize).coerceAtLeast(0.0)
        val distance = targetOffset - scrollOffset
        if (abs(distance) <= viewportSize) {
            state.scrollBy(distance.toFloat())
        } else {
            val lineSize = (averageLineSize + layoutInfo.mainAxisItemSpacing).coerceAtLeast(1.0)
            state.scrollToItem(
                index = ((targetOffset / lineSize).toInt() * slotsPerLine)
                    .coerceIn(0, lastItemIndex),
                scrollOffset = (targetOffset % lineSize).toInt().coerceAtLeast(0),
            )
        }
    }
}