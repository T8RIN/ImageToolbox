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

package com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkOff
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.screenLogic.EmojiKitchenComponent

@Composable
internal fun EmojiKitchenFavoritesSheet(
    component: EmojiKitchenComponent,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        title = {
            TitleItem(
                text = stringResource(R.string.emoji_kitchen_favorites),
                icon = Icons.Rounded.Bookmark
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(R.string.close)
                )
            }
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(104.dp),
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSizeNoClip(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            emojiKitchenFavorites(
                component = component,
                onDismiss = onDismiss
            )
        }
    }
}

private fun LazyGridScope.emojiKitchenFavorites(
    component: EmojiKitchenComponent,
    onDismiss: () -> Unit
) {
    when {
        component.isFavoritesLoading && component.favoriteCombinations.isEmpty() -> item(
            key = "loading",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                EnhancedLoadingIndicator()
            }
        }

        component.favoriteCombinations.isEmpty() -> item(
            key = "empty_favorites",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.BookmarkOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.emoji_kitchen_favorites_empty),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> items(
            items = component.favoriteCombinations,
            key = { "${it.firstEmojiId}:${it.secondEmojiId}" },
            contentType = { "favorites" }
        ) { combination ->
            val emojis = component.collectionState.collection?.emojis.orEmpty()
            val first = emojis.find { it.id == combination.firstEmojiId }
            val second = emojis.find { it.id == combination.secondEmojiId }
            val imageUri by produceState<String?>(
                initialValue = null,
                key1 = combination,
                key2 = component.collectionState.collection
            ) {
                value = null
                value = component.resolveFavorite(combination)
            }

            Column(
                modifier = Modifier
                    .animateItem()
                    .container(
                        shape = ShapeDefaults.default,
                        color = EnhancedBottomSheetDefaults.contentContainerColor,
                        resultPadding = 8.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EnhancedIconButton(
                    onClick = {
                        component.selectFavorite(combination)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    enabled = component.collectionState.progress == null,
                    shape = ShapeDefaults.small,
                ) {
                    Picture(
                        model = imageUri,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit,
                        showTransparencyChecker = false,
                        shimmerEnabled = false
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${first?.emoji.orEmpty()} + ${second?.emoji.orEmpty()}",
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 2.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                        maxLines = 1
                    )
                    EnhancedIconButton(
                        onClick = { component.removeFavorite(combination) },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        forceMinimumInteractiveComponentSize = false,
                        enabled = component.collectionState.progress == null,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkOff,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
