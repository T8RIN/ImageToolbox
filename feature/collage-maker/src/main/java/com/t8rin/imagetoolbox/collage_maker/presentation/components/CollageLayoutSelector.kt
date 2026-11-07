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

package com.t8rin.imagetoolbox.collage_maker.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.collages.components.CollageTypeSelection
import com.t8rin.collages.layout.CollageLayoutStore
import com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic.CollageMakerComponent
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.resources.icons.Collage
import com.t8rin.imagetoolbox.core.resources.icons.KeyboardArrowDown
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.scrollAnchor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun CollageLayoutSelector(
    component: CollageMakerComponent
) {
    val canChangeCollage = !component.uris.isNullOrEmpty()
    var expanded by rememberSaveable { mutableStateOf(false) }
    val imageCount = component.uris.orEmpty().size
    var layoutCount by remember(imageCount) {
        mutableIntStateOf(CollageLayoutStore.getLayoutCount(imageCount))
    }
    val isExpanded = expanded && layoutCount > 1
    val height by animateDpAsState(if (isExpanded) 378.dp else 142.dp)
    val rotation by animateFloatAsState(if (isExpanded) 180f else 0f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scrollAnchor()
            .then(
                if (canChangeCollage) {
                    Modifier.container(
                        resultPadding = 0.dp,
                        shape = ShapeDefaults.extraLarge
                    )
                } else Modifier
            )
    ) {
        if (canChangeCollage) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TitleItem(
                        text = stringResource(R.string.collage_type),
                        icon = Icons.Outlined.Collage,
                        modifier = Modifier.weight(1f, false)
                    )
                    EnhancedBadge(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .padding(bottom = 12.dp)
                    ) {
                        Text(layoutCount.toString())
                    }
                }
                if (layoutCount > 12) {
                    EnhancedIconButton(
                        onClick = {
                            expanded = !expanded
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                }
            }
        }
        val state = rememberLazyStaggeredGridState()
        CollageTypeSelection(
            state = state,
            onLayoutCountChange = { layoutCount = it },
            imagesCount = imageCount,
            value = component.collageType,
            favorites = component.library.favorites,
            itemSize = 76.dp,
            rowHeight = 110.dp,
            loadingItem = {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(ShapeDefaults.top)
                            .shimmer(true)
                    )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .clip(ShapeDefaults.bottom)
                            .shimmer(true)
                    )
                }
            },
            itemBottomContent = { type ->
                val isFavorite = type.layoutId in component.library.favorites
                EnhancedIconButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    shape = ShapeDefaults.bottom,
                    pressedShape = ShapeDefaults.bottom,
                    forceMinimumInteractiveComponentSize = false,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    onClick = { type.layoutId?.let(component::toggleFavorite) }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.BookmarkRemove else Icons.Outlined.Bookmark,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            onValueChange = {
                component.setCollageType(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .fadingEdges(
                    scrollableState = state,
                    isVertical = false,
                    spanCount = if (isExpanded) 3 else 1
                ),
            contentPadding = PaddingValues(16.dp),
            shape = ShapeDefaults.top,
            itemModifierFactory = { isSelected ->
                Modifier
                    .container(
                        resultPadding = 0.dp,
                        color = animateColorAsState(
                            targetValue = if (isSelected) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else MaterialTheme.colorScheme.surfaceContainerLowest,
                        ).value,
                        shape = ShapeDefaults.top
                    )
                    .padding(8.dp)
                    .clip(ShapeDefaults.extremeSmall)
            }
        )
    }
}