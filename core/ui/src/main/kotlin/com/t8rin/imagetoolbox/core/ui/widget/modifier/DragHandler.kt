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

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import android.annotation.SuppressLint
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


/**
[Modifier] which helps you to implement google photos selection grid,
to make it work pass item key which is should be "[key]-index"
 **/
@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.dragHandler(
    key: Any?,
    isVertical: Boolean,
    lazyGridState: LazyGridState,
    selectedItems: MutableState<Set<Int>>,
    onSelectionChange: (Set<Int>) -> Unit = {},
    enabled: Boolean = true,
    onTap: (Int) -> Unit = {},
    onLongTap: (Int) -> Unit = {},
    shouldHandleLongTap: Boolean = true,
    tapEnabled: Boolean = true
): Modifier = this.composed {
    val haptics = LocalHapticFeedback.current
    val isRtl = !isVertical && LocalLayoutDirection.current == LayoutDirection.Rtl

    val autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() }

    val isPortrait by isPortraitOrientationAsState()

    Modifier
        .pointerInput(key, tapEnabled, enabled, isRtl, isPortrait) {
            if (enabled) {
                detectTapGestures { offset ->
                    lazyGridState
                        .gridItemKeyAtPosition(
                            if (isRtl) offset.copy(x = size.width - offset.x) else offset
                        )
                        ?.let { key ->
                            if (tapEnabled) {
                                val newItems = if (selectedItems.value.contains(key)) {
                                    selectedItems.value - key
                                } else {
                                    selectedItems.value + key
                                }
                                selectedItems.update { newItems }
                                onSelectionChange(newItems)
                            }
                            haptics.longPress()
                            onTap(key - 1)
                        }
                }
            }
        }
        .pointerInput(key, shouldHandleLongTap, enabled, isRtl, isPortrait) {
            if (enabled) {
                coroutineScope {
                    var initialKey: Int? = null
                    var currentKey: Int? = null
                    var dragPosition: Offset?
                    var autoScrollSpeed = 0f
                    var autoScrollJob: Job? = null

                    fun updateSelection(position: Offset) {
                        val initial = initialKey ?: return

                        lazyGridState
                            .gridItemKeyAtPosition(position)
                            ?.let { key ->
                                if (currentKey != key) {
                                    val newItems = selectedItems.value
                                        .minus(initial..currentKey!!)
                                        .minus(currentKey!!..initial)
                                        .plus(initial..key)
                                        .plus(key..initial)

                                    selectedItems.update { newItems }
                                    onSelectionChange(newItems)
                                    currentKey = key
                                }
                            }
                    }

                    fun stopDrag() {
                        initialKey = null
                        currentKey = null
                        dragPosition = null
                        autoScrollSpeed = 0f
                        autoScrollJob?.cancel()
                        autoScrollJob = null
                    }

                    detectDragGesturesAfterLongPressInInitialPass(
                        onDragStart = { offset ->
                            val position =
                                if (isRtl) offset.copy(x = size.width - offset.x) else offset
                            lazyGridState
                                .gridItemKeyAtPosition(position)
                                ?.let { key ->
                                    if (!selectedItems.value.contains(key) && shouldHandleLongTap) {
                                        initialKey = key
                                        currentKey = key
                                        dragPosition = position
                                        val newItems = selectedItems.value + key
                                        selectedItems.update { newItems }
                                        onSelectionChange(newItems)

                                        autoScrollJob = launch {
                                            lazyGridState.scroll(MutatePriority.PreventUserInput) {
                                                while (isActive) {
                                                    val speed = autoScrollSpeed
                                                    if (speed != 0f) {
                                                        scrollBy(speed)
                                                        dragPosition?.let(::updateSelection)
                                                    }
                                                    delay(10)
                                                }
                                            }
                                        }
                                    }
                                    haptics.longPress()
                                    onLongTap(key - 1)
                                }
                        },
                        onDragCancel = ::stopDrag,
                        onDragEnd = ::stopDrag,
                        onDrag = { change, _ ->
                            if (initialKey != null) {
                                val position = if (isRtl) {
                                    change.position.copy(x = size.width - change.position.x)
                                } else {
                                    change.position
                                }

                                dragPosition = position
                                val distFromBottom = if (isVertical) {
                                    lazyGridState.layoutInfo.viewportSize.height - position.y
                                } else lazyGridState.layoutInfo.viewportSize.width - position.x
                                val distFromTop = if (isVertical) {
                                    position.y
                                } else position.x
                                autoScrollSpeed = when {
                                    distFromBottom < autoScrollThreshold -> autoScrollThreshold - distFromBottom
                                    distFromTop < autoScrollThreshold -> -(autoScrollThreshold - distFromTop)
                                    else -> 0f
                                }

                                updateSelection(position)
                            }
                        }
                    )
                }
            }
        }
}

private suspend fun PointerInputScope.detectDragGesturesAfterLongPressInInitialPass(
    onDragStart: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
) {
    awaitEachGesture {
        try {
            val down = awaitFirstDown(requireUnconsumed = false)
            val longPress = awaitLongPressOrCancellation(down.id) ?: return@awaitEachGesture

            onDragStart(longPress.position)
            var pointerId = longPress.id
            var isFinished = false

            while (!isFinished) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                var change = event.changes.firstOrNull { it.id == pointerId }

                if (change?.pressed != true) {
                    change = event.changes.firstOrNull { it.pressed }
                    if (change == null) {
                        event.changes
                            .filter(PointerInputChange::changedToUpIgnoreConsumed)
                            .forEach(PointerInputChange::consume)
                        isFinished = true
                    } else {
                        pointerId = change.id
                    }
                }

                if (!isFinished && change != null) {
                    val dragAmount = change.position - change.previousPosition
                    onDrag(change, dragAmount)
                    change.consume()
                }
            }

            onDragEnd()
        } catch (exception: CancellationException) {
            onDragCancel()
            throw exception
        }
    }
}

private fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): Int? {
    val find = layoutInfo.visibleItemsInfo.find { itemInfo ->
        itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
    }
    val itemKey = find?.key
    return itemKey?.toString()?.takeLastWhile { it != '-' }?.toIntOrNull()
}