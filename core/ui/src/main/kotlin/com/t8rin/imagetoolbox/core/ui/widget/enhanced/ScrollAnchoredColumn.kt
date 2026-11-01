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
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout

@Composable
internal fun ScrollAnchoredColumn(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable ColumnScope.() -> Unit
) {
    val anchors = remember(scrollState) { ScrollAnchors() }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.isScrollInProgress }.collect {
            if (it) anchors.active = null
        }
    }

    CompositionLocalProvider(LocalScrollAnchors provides anchors) {
        Column(
            modifier = modifier
                .layout { measurable, constraints ->
                    val previousScroll = Snapshot.withoutReadObservation { scrollState.value }
                    val placeable = measurable.measure(constraints)
                    val delta = anchors.pendingScroll
                    anchors.pendingScroll = 0
                    if (delta != 0) {
                        // The scroll range has been updated by verticalScroll at this point.
                        scrollState.dispatchRawDelta(
                            (previousScroll + delta - scrollState.value).toFloat()
                        )
                    }
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                }
                .enhancedVerticalScroll(scrollState)
                .padding(contentPadding)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    anchors.updatePositions(placeable)
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                },
            content = content
        )
    }
}

@Composable
internal fun rememberScrollAnchor(): ScrollAnchor? {
    val anchors = LocalScrollAnchors.current ?: return null
    val anchor = remember(anchors) { ScrollAnchor(anchors) }

    DisposableEffect(anchor) {
        anchors.positions[anchor.line] = AlignmentLine.Unspecified
        onDispose {
            anchors.positions.remove(anchor.line)
            if (anchors.active == anchor.line) anchors.active = null
        }
    }

    return anchor
}

internal fun Modifier.scrollAnchor(anchor: ScrollAnchor?): Modifier =
    if (anchor == null) this
    else layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(
            width = placeable.width,
            height = placeable.height,
            alignmentLines = mapOf(anchor.line to 0)
        ) {
            placeable.place(0, 0)
        }
    }

internal class ScrollAnchor(private val anchors: ScrollAnchors) {
    val line = HorizontalAlignmentLine(::minOf)

    fun activate() {
        anchors.active = line
    }
}

internal class ScrollAnchors {
    val positions = mutableMapOf<HorizontalAlignmentLine, Int>()
    var active: HorizontalAlignmentLine? = null
    var pendingScroll = 0

    fun updatePositions(placeable: Placeable) {
        positions.forEach { (line, previous) ->
            val position = placeable[line]
            if (line == active && previous != AlignmentLine.Unspecified &&
                position != AlignmentLine.Unspecified
            ) {
                pendingScroll += position - previous
            }
            positions[line] = position
        }
    }
}

private val LocalScrollAnchors = compositionLocalOf<ScrollAnchors?> { null }