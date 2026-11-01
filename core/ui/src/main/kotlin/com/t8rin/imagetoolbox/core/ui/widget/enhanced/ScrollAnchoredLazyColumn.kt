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

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Remeasurement
import androidx.compose.ui.layout.RemeasurementModifier

/** Use [scrollAnchoredItem] for dynamic controls; other items keep their regular layout. */
@Composable
fun ScrollAnchoredLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = enhancedFlingBehavior(),
    content: LazyListScope.() -> Unit
) {
    val remeasurement = remember { ScrollAnchorRemeasurement() }
    val anchors = remember(state, remeasurement) {
        ScrollAnchors(
            scrollState = state,
            scrollPosition = { state.firstVisibleItemIndex to state.firstVisibleItemScrollOffset },
            positionOf = { anchor ->
                val position = anchor.position
                if (position == AlignmentLine.Unspecified) null
                else state.layoutInfo.visibleItemsInfo
                    .find { it.key == anchor.group.key }
                    ?.let { it.offset + position }
            },
            afterScroll = remeasurement::forceRemeasure
        )
    }
    anchors.ObserveScrolling()

    CompositionLocalProvider(
        LocalLazyScrollAnchors provides anchors,
        LocalScrollAnchorGroup provides null
    ) {
        LazyColumn(
            modifier = modifier.then(remeasurement),
            state = state,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            flingBehavior = flingBehavior,
            content = content
        )
    }
}

fun LazyListScope.scrollAnchoredItem(
    key: Any,
    contentType: Any? = null,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    item(key = key, contentType = contentType) {
        val anchors = LocalLazyScrollAnchors.current
        val group = remember(anchors, key) { anchors?.let { ScrollAnchorGroup(it, key) } }
        CompositionLocalProvider(LocalScrollAnchorGroup provides group) {
            Column(
                modifier = if (group == null) modifier else modifier.measureScrollAnchors(group),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = content
            )
        }
    }
}

private val LocalLazyScrollAnchors = compositionLocalOf<ScrollAnchors?> { null }

private class ScrollAnchorRemeasurement : RemeasurementModifier {
    private var remeasurement: Remeasurement? = null

    override fun onRemeasurementAvailable(remeasurement: Remeasurement) {
        this.remeasurement = remeasurement
    }

    fun forceRemeasure() {
        remeasurement?.forceRemeasure()
    }
}