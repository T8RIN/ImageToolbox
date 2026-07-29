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

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

class FastScrollerState internal constructor() {
    var thumbCenter by mutableFloatStateOf(0f)
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
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val adapter = remember(state, coroutineScope) { LazyGridAdapter(state, coroutineScope) }
    var trackHeight by remember { mutableIntStateOf(0) }
    var draggedThumbPosition by remember { mutableStateOf<Float?>(null) }
    val minThumbHeightPx = with(density) { minThumbHeight.toPx() }
    val thumbHeight = (trackHeight * adapter.visibleFraction).coerceAtLeast(minThumbHeightPx)
    val maxThumbPosition = (trackHeight - thumbHeight).coerceAtLeast(0f)
    val scrollThumbPosition =
        (maxThumbPosition * adapter.scrollFraction).coerceIn(0f, maxThumbPosition)
    val thumbPosition = draggedThumbPosition ?: scrollThumbPosition
    val isVisible = adapter.contentSize > adapter.viewportSize && trackHeight > 0
    val currentThumbHeight by rememberUpdatedState(thumbHeight)
    val currentMaxThumbPosition by rememberUpdatedState(maxThumbPosition)
    fastScrollerState.thumbCenter = thumbPosition + thumbHeight / 2f
    fastScrollerState.isVisible = isVisible

    Box(
        modifier = modifier
            .width(thickness)
            .onSizeChanged { trackHeight = it.height }
            .then(
                if (isVisible) Modifier.background(trackColor, MaterialTheme.shapes.extraLarge)
                else Modifier
            )
            .pointerInput(adapter) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val gestureThumbHeight = currentThumbHeight
                    val gestureMaxThumbPosition = currentMaxThumbPosition
                    val currentThumbCenter = fastScrollerState.thumbCenter
                    val dragOffset = (down.position.y - currentThumbCenter)
                        .takeIf { abs(it) <= gestureThumbHeight / 2f }
                        ?: 0f

                    fun scrollTo(pointerY: Float) {
                        val position = (pointerY - dragOffset - gestureThumbHeight / 2f)
                            .coerceIn(0f, gestureMaxThumbPosition)
                        draggedThumbPosition = position
                        adapter.scrollToTrackPosition(
                            position,
                            gestureMaxThumbPosition,
                        )
                    }

                    try {
                        fastScrollerState.isDragging = true
                        scrollTo(down.position.y)
                        drag(down.id) { change ->
                            scrollTo(change.position.y)
                            change.consume()
                        }
                    } finally {
                        fastScrollerState.isDragging = false
                        draggedThumbPosition = null
                    }
                }
            }
    ) {
        if (isVisible) {
            Box(
                modifier = Modifier
                    .width(thickness)
                    .height(with(density) { thumbHeight.toDp() })
                    .offset { IntOffset(0, thumbPosition.roundToInt()) }
                    .background(thumbColor, MaterialTheme.shapes.extraLarge)
            )
        }
    }
}

private class LazyGridAdapter(
    private val state: LazyGridState,
    coroutineScope: CoroutineScope,
) {
    private val scrollRequests = Channel<Float>(Channel.CONFLATED)

    init {
        coroutineScope.launch {
            for (fraction in scrollRequests) {
                scrollToFraction(fraction)
            }
        }
    }

    private val layoutInfo get() = state.layoutInfo
    private val isVertical get() = layoutInfo.orientation == Orientation.Vertical
    private val visibleItems get() = layoutInfo.visibleItemsInfo
    private val slotsPerLine: Int
        get() = visibleItems.mapNotNull { item ->
            val slot = if (isVertical) item.column else item.row
            slot.takeIf { it >= 0 }
        }.distinct().size.coerceAtLeast(1)
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
    val visibleFraction: Float
        get() = if (contentSize == 0.0) 1f else (viewportSize / contentSize).coerceAtMost(1.0)
            .toFloat()
    val scrollFraction: Float
        get() = ((scrollOffset / (contentSize - viewportSize).coerceAtLeast(1.0)).coerceIn(
            0.0,
            1.0
        )).toFloat()

    fun scrollToTrackPosition(position: Float, maxPosition: Float) {
        if (maxPosition == 0f) return
        scrollRequests.trySend((position / maxPosition).coerceIn(0f, 1f))
    }

    private suspend fun scrollToFraction(fraction: Float) {
        val targetOffset = fraction * (contentSize - viewportSize).coerceAtLeast(0.0)
        val distance = targetOffset - scrollOffset
        if (abs(distance) <= viewportSize) {
            state.scrollBy(distance.toFloat())
        } else {
            val lineSize = (averageLineSize + layoutInfo.mainAxisItemSpacing).coerceAtLeast(1.0)
            state.scrollToItem(
                index = ((targetOffset / lineSize).toInt() * slotsPerLine)
                    .coerceIn(0, (layoutInfo.totalItemsCount - 1).coerceAtLeast(0)),
                scrollOffset = (targetOffset % lineSize).toInt().coerceAtLeast(0),
            )
        }
    }
}