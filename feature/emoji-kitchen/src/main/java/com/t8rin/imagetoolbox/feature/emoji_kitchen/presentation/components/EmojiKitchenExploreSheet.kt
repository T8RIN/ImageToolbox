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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Explore
import com.t8rin.imagetoolbox.core.resources.icons.Face5
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.other.GradientEdge
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.screenLogic.EmojiKitchenComponent

@Composable
internal fun EmojiKitchenExploreSheet(
    component: EmojiKitchenComponent,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    var collapsedCategories by rememberSaveable { mutableStateOf(emptyList<String>()) }
    val emojis = component.collectionState.collection?.emojis.orEmpty()
    val groupedEmojis = remember(emojis) {
        emojis.groupBy { it.category }
    }

    LaunchedEffect(visible) {
        if (visible) {
            showPicker = false
            if (component.exploreEmoji == null) component.openExplore()
        }
    }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        title = {
            TitleItem(
                text = stringResource(R.string.emoji_kitchen_explore),
                icon = Icons.Rounded.Explore
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
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val resultColumns = ((maxWidth - 24.dp) / 104.dp).toInt().coerceAtLeast(1)
            val density = LocalDensity.current
            var topPadding by remember { mutableStateOf(0.dp) }

            key(component.exploreEmoji?.id) {
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                        top = topPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    emojiKitchenCombinations(
                        component = component,
                        columns = resultColumns,
                        onDismiss = onDismiss
                    )
                }
            }

            Column(
                modifier = Modifier.onGloballyPositioned {
                    topPadding = with(density) { it.size.height.toDp() }
                }
            ) {
                PreferenceItemOverload(
                    title = stringResource(R.string.emoji_kitchen_choose),
                    startIcon = {
                        component.exploreEmoji?.let { emoji ->
                            key(emoji.id) {
                                Picture(
                                    model = emoji.imageUri,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(40.dp),
                                    showTransparencyChecker = false,
                                    shape = ShapeDefaults.extraSmall
                                )
                            }
                        }
                    },
                    drawStartIconContainer = false,
                    endIcon = {
                        Icon(
                            imageVector = Icons.Rounded.MiniEdit,
                            contentDescription = null
                        )
                    },
                    onClick = { showPicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EnhancedBottomSheetDefaults.containerColor)
                        .padding(start = 16.dp, top = 20.dp, end = 16.dp),
                    shape = ShapeDefaults.large
                )
                GradientEdge(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                    startColor = EnhancedBottomSheetDefaults.containerColor,
                    endColor = Color.Transparent
                )
            }
        }
    }

    EnhancedAlertDialog(
        visible = showPicker,
        onDismissRequest = { showPicker = false },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Face5,
                contentDescription = null
            )
        },
        title = { Text(stringResource(R.string.emoji_kitchen_choose)) },
        confirmButton = {
            EnhancedButton(
                onClick = { showPicker = false },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        },
        text = {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSizeNoClip()
            ) {
                val columns = (maxWidth / 56.dp).toInt().coerceAtLeast(1)
                val listState = rememberLazyListState()

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fadingEdges(scrollableState = listState, isVertical = true),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    emojiKitchenPicker(
                        emojis = groupedEmojis,
                        columns = columns,
                        collapsedCategories = collapsedCategories,
                        onToggleCategory = { category ->
                            collapsedCategories = if (category.key in collapsedCategories) {
                                collapsedCategories - category.key
                            } else collapsedCategories + category.key
                        },
                        selectedEmoji = component.exploreEmoji,
                        compatibleEmojis = null,
                        enabled = component.collectionState.progress == null,
                        onSelect = { emoji ->
                            component.selectExploreEmoji(emoji)
                            showPicker = false
                        }
                    )
                }
            }
        },
        placeAboveAll = true
    )
}

private fun LazyListScope.emojiKitchenCombinations(
    component: EmojiKitchenComponent,
    columns: Int,
    onDismiss: () -> Unit
) {
    when {
        component.isExploringLoading && component.combinations.isEmpty() -> item(key = "loading") {
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

        component.combinations.isEmpty() -> item(key = "empty_combinations") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Explore,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.emoji_kitchen_no_combination),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> items(
            items = component.combinations.chunked(columns),
            key = { "combination_${it.first().otherEmojiId}" },
            contentType = { "combinations" }
        ) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { combination ->
                    val cacheKey = MemoryCache.Key(combination.variant.uri)

                    key(cacheKey) {
                        val request = remember(cacheKey) {
                            ImageRequest.Builder(appContext)
                                .data(combination.variant.uri)
                                .memoryCacheKey(cacheKey)
                                .scale(Scale.FIT)
                                .crossfade(false)
                                .build()
                        }
                        val cachedRequest = remember(cacheKey) {
                            appContext.imageLoader.memoryCache?.get(cacheKey)?.let {
                                request.newBuilder()
                                    .placeholder(it.image)
                                    .error(it.image)
                                    .build()
                            }
                        }
                        val imageRequest by produceState(cachedRequest) {
                            if (value == null) {
                                component.resolveCombination(combination)?.let {
                                    value = request
                                }
                            }
                        }

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            EnhancedIconButton(
                                onClick = {
                                    component.selectCombination(combination)
                                    onDismiss()
                                },
                                modifier = Modifier.size(96.dp),
                                containerColor = EnhancedBottomSheetDefaults.contentContainerColor,
                                shape = ShapeDefaults.large
                            ) {
                                Picture(
                                    model = imageRequest,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                        .height(72.dp),
                                    showTransparencyChecker = false,
                                    shape = ShapeDefaults.extraSmall
                                )
                            }
                        }
                    }
                }
                repeat(columns - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
