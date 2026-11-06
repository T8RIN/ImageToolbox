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

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.resources.icons.Casino
import com.t8rin.imagetoolbox.core.resources.icons.ContentCopy
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Shuffle
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenEmoji
import com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.screenLogic.EmojiKitchenComponent

@Composable
internal fun EmojiKitchenPreview(
    component: EmojiKitchenComponent,
    modifier: Modifier
) {
    val enabled = component.collectionState.progress == null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) { detectTapGestures { } },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EmojiKitchenSlot(
                emoji = component.first,
                selected = component.activeSlot == 0,
                enabled = enabled,
                onSelect = { component.selectSlot(0) },
                onRandomize = { component.randomize(0) },
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "+",
                modifier = Modifier.padding(top = 24.dp),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            EmojiKitchenSlot(
                emoji = component.second,
                selected = component.activeSlot == 1,
                enabled = enabled,
                onSelect = { component.selectSlot(1) },
                onRandomize = { component.randomize(1) },
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "=",
                modifier = Modifier.padding(top = 24.dp),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmojiKitchenResult(
                    component = component,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                )
                Row {
                    EnhancedIconButton(
                        onClick = { component.cacheImage(Clipboard::copy) },
                        enabled = component.canExport,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = null
                        )
                    }
                    EnhancedIconButton(
                        onClick = component::toggleFavorite,
                        enabled = component.canExport,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (component.isFavorite) {
                                Icons.Rounded.BookmarkRemove
                            } else {
                                Icons.Outlined.Bookmark
                            },
                            contentDescription = null
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                EnhancedButton(
                    onClick = { component.randomize() },
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.emoji_kitchen_shuffle)
                    )
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                EnhancedButton(
                    onClick = component::clearSelection,
                    enabled = enabled && (component.first != null || component.second != null),
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.clear)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmojiKitchenSlot(
    emoji: EmojiKitchenEmoji?,
    selected: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit,
    onRandomize: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EnhancedIconButton(
            onClick = onSelect,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
            shape = ShapeDefaults.large,
            enabled = enabled
        ) {
            AnimatedContent(
                targetState = emoji,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    if (it != null) {
                        Picture(
                            model = it.imageUri,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(60.dp),
                            showTransparencyChecker = false,
                            shimmerEnabled = false
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.MiniEdit,
                            contentDescription = null
                        )
                    }
                }
            }
        }
        EnhancedIconButton(
            onClick = onRandomize,
            enabled = enabled
        ) {
            Icon(
                imageVector = Icons.Outlined.Casino,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun EmojiKitchenResult(
    component: EmojiKitchenComponent,
    modifier: Modifier
) {
    Box(
        modifier = modifier.container(
            shape = ShapeDefaults.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
            resultPadding = 0.dp
        ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = component.selectedVariant?.uri,
            modifier = Modifier.fillMaxSize()
        ) { uri ->
            Box(contentAlignment = Alignment.Center) {
                if (uri != null) {
                    Picture(
                        model = uri,
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Fit,
                        showTransparencyChecker = false,
                        shimmerEnabled = false
                    )
                } else if (!component.isQuerying) {
                    AutoSizeText(
                        text = stringResource(
                            when {
                                component.first == null -> R.string.emoji_kitchen_choose_first
                                component.second == null -> R.string.emoji_kitchen_choose_second
                                else -> R.string.emoji_kitchen_no_combination
                            }
                        ),
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2
                    )
                }
            }
        }
    }
}