/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.utils.confetti.LocalConfettiHostState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun <T : Any> DataSelector(
    value: T,
    onValueChange: (T) -> Unit,
    entries: List<T>,
    title: String,
    titleIcon: ImageVector?,
    itemContentText: @Composable (T) -> String,
    spanCount: Int = 3,
    modifier: Modifier = Modifier,
    badgeContent: (@Composable RowScope.() -> Unit)? = null,
    shape: Shape = ShapeDefaults.large,
    color: Color = Color.Unspecified,
    selectedItemColor: Color = MaterialTheme.colorScheme.tertiary,
    initialExpanded: Boolean = false
) {
    val realSpanCount = spanCount.coerceAtLeast(1)

    Column(
        modifier = modifier.container(
            shape = shape,
            color = color
        )
    ) {
        var expanded by rememberSaveable(initialExpanded, realSpanCount) {
            mutableStateOf(
                initialExpanded && realSpanCount > 1
            )
        }
        Row {
            val rotation by animateFloatAsState(if (expanded) 180f else 0f)
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleItem(
                    text = title,
                    icon = titleIcon,
                    modifier = Modifier
                        .padding(top = 12.dp, start = 12.dp, bottom = 8.dp)
                        .weight(1f, false)
                )
                badgeContent?.let {
                    val scope = rememberCoroutineScope()
                    val confettiHostState = LocalConfettiHostState.current
                    val showConfetti: () -> Unit = {
                        scope.launch {
                            confettiHostState.showConfetti()
                        }
                    }
                    Badge(
                        content = badgeContent,
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .padding(bottom = 12.dp)
                            .scaleOnTap {
                                showConfetti()
                            }
                    )
                }
            }

            if (realSpanCount > 1) {
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Expand",
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
        }
        val state = rememberLazyStaggeredGridState()

        LaunchedEffect(value, entries) {
            delay(300)
            val targetIndex = entries.indexOf(value).takeIf { it >= 0 } ?: 0
            if (state.layoutInfo.visibleItemsInfo.all { it.index != targetIndex }) {
                state.scrollToItem(targetIndex)
            }
        }

        LazyHorizontalStaggeredGrid(
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            state = state,
            horizontalItemSpacing = 8.dp,
            rows = StaggeredGridCells.Adaptive(30.dp),
            modifier = Modifier
                .heightIn(
                    max = animateDpAsState(
                        if (expanded) {
                            52.dp * realSpanCount - 8.dp * (realSpanCount - 1)
                        } else 52.dp
                    ).value
                )
                .fadingEdges(
                    scrollableState = state,
                    isVertical = false,
                    spanCount = realSpanCount
                ),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(entries) {
                val selected by remember(it, value) {
                    derivedStateOf {
                        value == it
                    }
                }
                EnhancedChip(
                    selected = selected,
                    onClick = {
                        onValueChange(it)
                    },
                    selectedColor = selectedItemColor,
                    contentPadding = PaddingValues(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = itemContentText(it)
                    )
                }
            }
        }
    }
}