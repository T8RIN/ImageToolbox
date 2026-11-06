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

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.math.roundToInt

@Composable
fun ScrollAnchoredColumn(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    contentPadding: PaddingValues = PaddingValues(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    val anchors = remember(scrollState) {
        ScrollAnchors(
            scrollState = scrollState,
            scrollPosition = { scrollState.value },
            positionOf = { anchor ->
                anchor.position.takeUnless { it == AlignmentLine.Unspecified }
                    ?.minus(scrollState.value)
            }
        )
    }
    val group = remember(anchors) { ScrollAnchorGroup(anchors) }
    anchors.ObserveScrolling()

    CompositionLocalProvider(LocalScrollAnchorGroup provides group) {
        Column(
            modifier = modifier
                .maintainScrollAnchor(anchors)
                .enhancedVerticalScroll(scrollState)
                .padding(contentPadding)
                .measureScrollAnchors(group),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}

@Composable
internal fun rememberScrollAnchor(): ScrollAnchor? {
    val group = LocalScrollAnchorGroup.current ?: return null
    val anchor = remember(group) { ScrollAnchor(group) }

    DisposableEffect(anchor) {
        group.anchors.add(anchor)
        onDispose {
            group.anchors.remove(anchor)
            group.owner.remove(anchor)
        }
    }

    return anchor
}

fun Modifier.scrollAnchor(): Modifier = composed { scrollAnchor(rememberScrollAnchor()) }

internal fun Modifier.scrollAnchor(anchor: ScrollAnchor?): Modifier =
    if (anchor == null) this
    else this
        .pointerInput(anchor) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                if (waitForUpOrCancellation(pass = PointerEventPass.Initial) != null) {
                    anchor.activate()
                }
            }
        }
        .onGloballyPositioned { anchor.group.owner.onPositioned(anchor) }
        .layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            layout(
                width = placeable.width,
                height = placeable.height,
                alignmentLines = mapOf(anchor.line to 0)
            ) {
                placeable.place(0, 0)
            }
        }

internal fun Modifier.measureScrollAnchors(group: ScrollAnchorGroup): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        group.updatePositions(placeable)
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }

internal fun Modifier.maintainScrollAnchor(anchors: ScrollAnchors): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            // Correct the offset before placing the scrolling content in this frame.
            anchors.maintainPosition()
            placeable.place(0, 0)
        }
    }

internal class ScrollAnchor(val group: ScrollAnchorGroup) {
    val line = HorizontalAlignmentLine(::minOf)
    var position by mutableIntStateOf(AlignmentLine.Unspecified)

    fun activate() {
        group.owner.activate(this)
    }
}

internal class ScrollAnchorGroup(
    val owner: ScrollAnchors,
    val key: Any? = null
) {
    // Resolving alignment lines can change which anchors are registered during measurement.
    val anchors: MutableSet<ScrollAnchor> = CopyOnWriteArraySet()

    fun updatePositions(placeable: Placeable) {
        anchors.forEach { it.position = placeable[it.line] }
    }
}

internal class ScrollAnchors(
    private val scrollState: ScrollableState,
    private val scrollPosition: () -> Any,
    private val positionOf: (ScrollAnchor) -> Int?,
    private val afterScroll: (() -> Unit)? = null
) {
    private data class Target(
        val anchor: ScrollAnchor,
        val position: Int,
        val scrollPosition: Any,
        val layoutPosition: Int
    )

    private var active by mutableStateOf<Target?>(null)
    private var correcting = false

    @Composable
    fun ObserveScrolling() {
        LaunchedEffect(this) {
            snapshotFlow { scrollState.isScrollInProgress }.collect {
                if (it) clear()
            }
        }
    }

    fun activate(anchor: ScrollAnchor) {
        positionOf(anchor)?.let {
            active = Target(anchor, it, scrollPosition(), anchor.position)
        }
    }

    fun remove(anchor: ScrollAnchor) {
        if (active?.anchor === anchor) clear()
    }

    fun maintainPosition(): Boolean {
        val target = active ?: return false
        if (scrollState.isScrollInProgress) {
            clear()
            return false
        }
        val position = positionOf(target.anchor)
        if (position == null) {
            clear()
            return false
        }
        val currentScroll = scrollPosition()
        if (currentScroll != target.scrollPosition &&
            target.anchor.position == target.layoutPosition
        ) {
            clear()
            return false
        }
        val delta = position - target.position
        val consumed = if (delta == 0) 0 else {
            scrollState.dispatchRawDelta(delta.toFloat()).roundToInt()
        }
        if (active === target) {
            active = target.copy(
                position = position - consumed,
                scrollPosition = scrollPosition(),
                layoutPosition = target.anchor.position
            )
        }
        return consumed != 0
    }

    fun onPositioned(anchor: ScrollAnchor) {
        if (afterScroll == null || correcting || active?.anchor !== anchor) return
        correcting = true
        try {
            if (maintainPosition()) afterScroll()
        } finally {
            correcting = false
        }
    }

    private fun clear() {
        active = null
    }
}

internal val LocalScrollAnchorGroup = compositionLocalOf<ScrollAnchorGroup?> { null }