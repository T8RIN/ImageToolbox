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
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


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
    val autoScrollSpeed: MutableState<Float> = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(autoScrollSpeed.value) {
        if (autoScrollSpeed.value != 0f) {
            while (isActive) {
                lazyGridState.scrollBy(autoScrollSpeed.value)
                delay(10)
            }
        }
    }

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
                var initialKey: Int? = null
                var currentKey: Int? = null
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        lazyGridState
                            .gridItemKeyAtPosition(
                                if (isRtl) offset.copy(x = size.width - offset.x) else offset
                            )
                            ?.let { key ->
                                if (!selectedItems.value.contains(key) && shouldHandleLongTap) {
                                    initialKey = key
                                    currentKey = key
                                    val newItems = selectedItems.value + key
                                    selectedItems.update { newItems }
                                    onSelectionChange(newItems)
                                }
                                haptics.longPress()
                                onLongTap(key - 1)
                            }
                    },
                    onDragCancel = {
                        initialKey = null
                        autoScrollSpeed.value = 0f
                    },
                    onDragEnd = {
                        initialKey = null
                        autoScrollSpeed.value = 0f
                    },
                    onDrag = { change, _ ->
                        if (initialKey != null) {
                            val position =
                                if (isRtl) change.position.copy(x = size.width - change.position.x) else change.position

                            val distFromBottom = if (isVertical) {
                                lazyGridState.layoutInfo.viewportSize.height - position.y
                            } else lazyGridState.layoutInfo.viewportSize.width - position.x
                            val distFromTop = if (isVertical) {
                                position.y
                            } else position.x
                            autoScrollSpeed.value = when {
                                distFromBottom < autoScrollThreshold -> autoScrollThreshold - distFromBottom
                                distFromTop < autoScrollThreshold -> -(autoScrollThreshold - distFromTop)
                                else -> 0f
                            }

                            lazyGridState
                                .gridItemKeyAtPosition(position)
                                ?.let { key ->
                                    if (currentKey != key) {
                                        val newItems = selectedItems.value
                                            .minus(initialKey!!..currentKey!!)
                                            .minus(currentKey!!..initialKey!!)
                                            .plus(initialKey!!..key)
                                            .plus(key..initialKey!!)

                                        selectedItems.update { newItems }
                                        onSelectionChange(newItems)
                                        currentKey = key
                                    }
                                }
                        }
                    }
                )
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