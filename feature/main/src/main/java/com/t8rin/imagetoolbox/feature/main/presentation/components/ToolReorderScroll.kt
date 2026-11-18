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

package com.t8rin.imagetoolbox.feature.main.presentation.components

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import sh.calvin.reorderable.DragGestureDetector

@Composable
internal fun rememberToolReorderScroll(
    state: LazyStaggeredGridState
): ToolReorderScroll = remember(state) {
    ToolReorderScroll(state) { key, delta ->
        with(state.layoutInfo) {
            limitToolScroll(
                delta = delta,
                key = key,
                items = visibleItemsInfo,
                firstOffset = viewportStartOffset,
                lastOffset = viewportEndOffset,
                contentEnd = viewportEndOffset - afterContentPadding,
                lastIndex = totalItemsCount - 1,
                itemKey = { it.key },
                itemIndex = { it.index },
                itemTop = { it.offset.y },
                itemBottom = { it.offset.y + it.size.height }
            )
        }
    }
}

@Composable
internal fun rememberToolReorderScroll(
    state: LazyGridState
): ToolReorderScroll = remember(state) {
    ToolReorderScroll(state) { key, delta ->
        with(state.layoutInfo) {
            limitToolScroll(
                delta = delta,
                key = key,
                items = visibleItemsInfo,
                firstOffset = viewportStartOffset,
                lastOffset = viewportEndOffset,
                contentEnd = viewportEndOffset - afterContentPadding,
                lastIndex = totalItemsCount - 1,
                itemKey = { it.key },
                itemIndex = { it.index },
                itemTop = { it.offset.y },
                itemBottom = { it.offset.y + it.size.height }
            )
        }
    }
}

internal class ToolReorderScroll(
    private val state: ScrollableState,
    private val limitScroll: (Int, Float) -> Float
) : ScrollableState by state {
    private val scrollScope = MutableStateFlow<ScrollScope?>(null)
    private var activeKey: Int? = null
    private var releasedKey: Int? = null

    @Composable
    fun LockWhileDragging(isDragging: Boolean) {
        LaunchedEffect(this, isDragging) {
            if (isDragging) {
                // The reorder scroller owns the grid until this gesture finishes.
                state.scroll(MutatePriority.PreventUserInput) {
                    scrollScope.value = this
                    try {
                        awaitCancellation()
                    } finally {
                        scrollScope.value = null
                    }
                }
            }
        }
    }

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        val scope = scrollScope.filterNotNull().first()
        block(object : ScrollScope {
            override fun scrollBy(pixels: Float): Float {
                val key = activeKey ?: return 0f
                if (scrollScope.value !== scope || !pixels.isFinite()) return 0f
                if (pixels > 0f && !state.canScrollForward ||
                    pixels < 0f && !state.canScrollBackward
                ) return 0f
                val delta = limitScroll(key, pixels)
                return if (delta == 0f) 0f else scope.scrollBy(delta)
            }
        })
    }

    fun detector(key: Int): DragGestureDetector =
        DragGestureDetector { onStart, onEnd, onCancel, onDrag ->
            with(DragGestureDetector.LongPress) {
                detect(
                    onDragStart = { position ->
                        activeKey = key
                        releasedKey = null
                        onStart(position)
                    },
                    onDragEnd = {
                        if (activeKey == key) releasedKey = key
                        onEnd()
                    },
                    onDragCancel = onCancel,
                    onDrag = onDrag
                )
            }
        }

    fun finish(key: Int): Boolean {
        if (activeKey != key) return false
        val released = releasedKey == key
        activeKey = null
        releasedKey = null
        return released
    }
}

private inline fun <T> limitToolScroll(
    delta: Float,
    key: Int,
    items: List<T>,
    firstOffset: Int,
    lastOffset: Int,
    contentEnd: Int,
    lastIndex: Int,
    itemKey: (T) -> Any,
    itemIndex: (T) -> Int,
    itemTop: (T) -> Int,
    itemBottom: (T) -> Int
): Float {
    val dragged = items.firstOrNull { itemKey(it) == key } ?: return 0f
    return if (delta > 0f) {
        val visibleRoom = itemBottom(dragged) - firstOffset - 1
        val contentRoom = if (items.any { itemIndex(it) == lastIndex }) {
            items.maxOf { itemBottom(it) } - contentEnd
        } else Int.MAX_VALUE
        delta.coerceAtMost(minOf(visibleRoom, contentRoom).coerceAtLeast(0).toFloat())
    } else {
        val visibleRoom = lastOffset - itemTop(dragged) - 1
        val contentRoom = items.firstOrNull { itemIndex(it) == 0 }?.let { -itemTop(it) }
            ?: Int.MAX_VALUE
        delta.coerceAtLeast(-minOf(visibleRoom, contentRoom).coerceAtLeast(0).toFloat())
    }
}