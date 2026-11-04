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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.EmojiEmotions
import com.t8rin.imagetoolbox.core.resources.icons.EmojiEvents
import com.t8rin.imagetoolbox.core.resources.icons.EmojiFoodBeverage
import com.t8rin.imagetoolbox.core.resources.icons.EmojiNature
import com.t8rin.imagetoolbox.core.resources.icons.EmojiObjects
import com.t8rin.imagetoolbox.core.resources.icons.EmojiSymbols
import com.t8rin.imagetoolbox.core.resources.icons.EmojiTransportation
import com.t8rin.imagetoolbox.core.resources.icons.KeyboardArrowDown
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.image.imageStickyHeader
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenCategory
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenEmoji
import com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.screenLogic.EmojiKitchenComponent

@Composable
internal fun EmojiKitchenControls(
    component: EmojiKitchenComponent,
    listState: LazyListState,
    isPortrait: Boolean,
    imagePreview: (@Composable () -> Unit)?
) {
    var collapsedCategories by rememberSaveable { mutableStateOf(emptyList<String>()) }
    val emojis = component.collectionState.collection?.emojis.orEmpty()
    val groupedEmojis = remember(emojis) {
        emojis.groupBy { it.category }
    }
    val other = if (component.activeSlot == 0) component.second else component.first

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val columns = ((maxWidth - 28.dp) / 56.dp).toInt().coerceAtLeast(1)

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = if (isPortrait) 0.dp else 12.dp,
                end = 16.dp,
                bottom = if (isPortrait) 128.dp else 20.dp
            ) + WindowInsets.navigationBars
                .only(WindowInsetsSides.Bottom).asPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            flingBehavior = enhancedFlingBehavior()
        ) {
            if (imagePreview != null) {
                imageStickyHeader(
                    visible = true,
                    isBlocked = true,
                    internalHeight = Dp.Unspecified,
                    allowStateChanges = false,
                    padding = 16.dp,
                    imageBlock = imagePreview
                )
            }
            item(key = "collection") {
                EmojiKitchenCollectionInfoCard(
                    component = component
                )
            }
            emojiKitchenPicker(
                emojis = groupedEmojis,
                columns = columns,
                collapsedCategories = collapsedCategories,
                onToggleCategory = { category ->
                    collapsedCategories = if (category.key in collapsedCategories) {
                        collapsedCategories - category.key
                    } else collapsedCategories + category.key
                },
                selectedEmoji = if (component.activeSlot == 0) component.first else component.second,
                compatibleEmojis = component.compatibleEmojis.takeIf { other != null },
                enabled = component.collectionState.progress == null,
                onSelect = component::selectEmoji
            )
        }
    }
}

internal fun LazyListScope.emojiKitchenPicker(
    emojis: Map<EmojiKitchenCategory, List<EmojiKitchenEmoji>>,
    columns: Int,
    collapsedCategories: List<String>,
    onToggleCategory: (EmojiKitchenCategory) -> Unit,
    selectedEmoji: EmojiKitchenEmoji?,
    compatibleEmojis: Set<String>?,
    enabled: Boolean,
    onSelect: (EmojiKitchenEmoji) -> Unit
) {
    EmojiKitchenCategory.entries.forEach { category ->
        val categoryEmojis = emojis[category].orEmpty()
        if (categoryEmojis.isEmpty()) return@forEach
        val expanded = category.key !in collapsedCategories

        item(key = "category_${category.key}") {
            val rotation by animateFloatAsState(if (expanded) 180f else 0f)
            val interactionSource = remember { MutableInteractionSource() }

            TitleItem(
                modifier = Modifier
                    .animateItem()
                    .padding(
                        bottom = animateDpAsState(
                            if (expanded) 8.dp else 0.dp
                        ).value
                    )
                    .container(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        resultPadding = 0.dp,
                        shape = shapeByInteraction(
                            shape = ShapeDefaults.default,
                            pressedShape = ShapeDefaults.pressed,
                            interactionSource = interactionSource
                        )
                    )
                    .hapticsClickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current
                    ) {
                        onToggleCategory(category)
                    }
                    .padding(16.dp),
                text = stringResource(category.title),
                icon = category.icon,
                endContent = {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                }
            )
        }
        if (expanded) {
            items(
                items = categoryEmojis.chunked(columns),
                key = { "${category.key}_${it.first().id}" },
                contentType = { "emojis" }
            ) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    row.forEach { emoji ->
                        val compatible = compatibleEmojis == null || emoji.id in compatibleEmojis

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            EnhancedIconButton(
                                onClick = { onSelect(emoji) },
                                modifier = Modifier
                                    .size(52.dp)
                                    .alpha(if (compatible) 1f else 0.3f),
                                enabled = enabled && compatible,
                                containerColor = if (emoji == selectedEmoji) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else Color.Transparent,
                                shape = ShapeDefaults.default
                            ) {
                                Picture(
                                    model = emoji.imageUri,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(36.dp),
                                    showTransparencyChecker = false,
                                    shimmerEnabled = false
                                )
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
}

private val EmojiKitchenCategory.title: Int
    get() = when (this) {
        EmojiKitchenCategory.Smileys -> R.string.emoji_kitchen_category_smileys
        EmojiKitchenCategory.Animals -> R.string.nature_and_animals
        EmojiKitchenCategory.Food -> R.string.food_and_drink
        EmojiKitchenCategory.Travel -> R.string.emoji_kitchen_category_travel
        EmojiKitchenCategory.Activities -> R.string.emoji_kitchen_category_activities
        EmojiKitchenCategory.Objects -> R.string.objects
        EmojiKitchenCategory.Symbols -> R.string.symbols
    }

private val EmojiKitchenCategory.icon: ImageVector
    get() = when (this) {
        EmojiKitchenCategory.Smileys -> Icons.Outlined.EmojiEmotions
        EmojiKitchenCategory.Animals -> Icons.Outlined.EmojiNature
        EmojiKitchenCategory.Food -> Icons.Outlined.EmojiFoodBeverage
        EmojiKitchenCategory.Travel -> Icons.Outlined.EmojiTransportation
        EmojiKitchenCategory.Activities -> Icons.Outlined.EmojiEvents
        EmojiKitchenCategory.Objects -> Icons.Outlined.EmojiObjects
        EmojiKitchenCategory.Symbols -> Icons.Rounded.EmojiSymbols
    }
