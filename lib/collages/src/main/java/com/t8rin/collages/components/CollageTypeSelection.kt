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

package com.t8rin.collages.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.collages.components.preview.CollagePreview
import com.t8rin.collages.components.preview.CollagePreviewCache
import com.t8rin.collages.layout.CollageLayoutStore
import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.model.CollageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ceil

@Composable
fun CollageTypeSelection(
    imagesCount: Int,
    value: CollageType,
    favorites: Set<String> = emptySet(),
    itemBottomContent: @Composable (CollageType) -> Unit = {},
    itemSize: Dp = 68.dp,
    rowHeight: Dp = itemSize,
    onValueChange: (CollageType) -> Unit,
    loadingItem: @Composable () -> Unit,
    onLayoutCountChange: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    itemModifierFactory: @Composable (isSelected: Boolean) -> Modifier = { Modifier },
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    previewColor: Color = MaterialTheme.colorScheme.secondary,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    if (imagesCount <= 0) return
    var allFrames: List<CollageLayout> by remember {
        mutableStateOf(emptyList())
    }
    LaunchedEffect(imagesCount) {
        allFrames = withContext(Dispatchers.Default) {
            CollageLayoutStore.getLayouts(imagesCount).mapNotNull { it.layout }
        }
    }

    val availableFrames by remember(allFrames, imagesCount, favorites) {
        derivedStateOf {
            allFrames.filter {
                it.photoItemList.size == imagesCount
            }.sortedByDescending { it.title in favorites }
        }
    }

    LaunchedEffect(availableFrames) {
        if (availableFrames.isNotEmpty()) onLayoutCountChange(availableFrames.size)
        if (
            availableFrames.isNotEmpty() && (value == CollageType.Empty || (value.layout?.photoItemList?.size
                ?: 0) != imagesCount)
        ) {
            onValueChange(CollageType(availableFrames.first()))
        }
    }

    BoxWithConstraints(modifier) {
        val columns = ceil(maxWidth / (itemSize + 8.dp)).toInt().coerceAtLeast(1)
        val rows = ((maxHeight - contentPadding.calculateTopPadding() -
                contentPadding.calculateBottomPadding()) / rowHeight).toInt().coerceAtLeast(1)
        LazyHorizontalStaggeredGrid(
            state = state,
            rows = StaggeredGridCells.Adaptive(rowHeight),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalItemSpacing = 8.dp,
            contentPadding = contentPadding
        ) {
            if (availableFrames.isEmpty()) {
                items(columns * rows) {
                    Column(Modifier.width(itemSize)) { loadingItem() }
                }
            }
            items(
                items = availableFrames,
                key = { it.title }
            ) { layout ->
                val preview by produceState(
                    initialValue = CollagePreviewCache.peek(layout),
                    key1 = layout.title
                ) {
                    this.value = withContext(Dispatchers.Default) {
                        CollagePreviewCache.get(layout)
                    }
                }
                Column(
                    Modifier
                        .animateItem()
                        .width(itemSize),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (preview == null) {
                        loadingItem()
                        return@Column
                    }
                    val type = remember(layout) { CollageType(layout) }
                    CollagePreview(
                        shapes = requireNotNull(preview),
                        color = previewColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(shape)
                            .clickable {
                                onValueChange(CollageType(layout))
                            }
                            .then(itemModifierFactory(value.layoutId == layout.title))
                    )
                    itemBottomContent(type)
                }
            }
        }
    }

}