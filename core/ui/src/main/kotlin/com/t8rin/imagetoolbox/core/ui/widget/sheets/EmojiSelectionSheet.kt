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

package com.t8rin.imagetoolbox.core.ui.widget.sheets

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face5
import androidx.compose.material.icons.outlined.Face6
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.emoji.Emoji
import com.t8rin.imagetoolbox.core.resources.emoji.EmojiData
import com.t8rin.imagetoolbox.core.resources.shapes.CloverShape
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.other.EmojiItem
import com.t8rin.imagetoolbox.core.ui.widget.other.GradientEdge
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun EmojiSelectionSheet(
    selectedEmojiIndex: Int?,
    emojiWithCategories: ImmutableList<EmojiData> = Emoji.allIconsCategorized(),
    allEmojis: ImmutableList<Uri> = Emoji.allIcons(),
    onEmojiPicked: (Int) -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    val state = rememberLazyGridState()

    LaunchedEffect(visible) {
        delay(600)
        if ((selectedEmojiIndex ?: -1) >= 0) {
            var count = 0
            val item = emojiWithCategories.find { (_, _, emojis) ->
                count = 0
                emojis.forEach { emoji ->
                    count++
                    val index = allEmojis.indexOf(emoji)
                    if (index == selectedEmojiIndex) return@find true
                }
                return@find false
            } ?: return@LaunchedEffect
            val index = emojiWithCategories.indexOf(item)

            state.animateScrollToItem(
                index = index,
                scrollOffset = 60 * count / 6
            )
        }
    }

    val emojiEnabled by remember(selectedEmojiIndex) {
        derivedStateOf {
            selectedEmojiIndex != -1
        }
    }
    val scope = rememberCoroutineScope()

    EnhancedModalBottomSheet(
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedEmojiIndex != null) {
                    EnhancedIconButton(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        enabled = emojiEnabled,
                        onClick = {
                            onEmojiPicked(Random.nextInt(0, allEmojis.lastIndex))
                            scope.launch {
                                state.animateScrollToItem(selectedEmojiIndex)
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Shuffle,
                            contentDescription = stringResource(R.string.shuffle)
                        )
                    }
                }
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = onDismiss
                ) {
                    AutoSizeText(stringResource(R.string.close))
                }
            }
        },
        title = {
            TitleItem(
                text = stringResource(R.string.emoji),
                icon = Icons.Outlined.Face5
            )
        },
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        }
    ) {
        val alphaState by remember(emojiEnabled) {
            derivedStateOf {
                if (emojiEnabled) 1f else 0.4f
            }
        }

        Box {
            val density = LocalDensity.current
            var topPadding by remember {
                mutableStateOf(0.dp)
            }
            val contentPadding by remember(topPadding) {
                derivedStateOf {
                    PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                        top = topPadding
                    )
                }
            }
            var expandedCategories by rememberSaveable(visible) {
                mutableStateOf(
                    if ((selectedEmojiIndex ?: -1) >= 0) {
                        emojiWithCategories.find { (_, _, emojis) ->
                            emojis.forEach { emoji ->
                                val index = allEmojis.indexOf(emoji)
                                if (index == selectedEmojiIndex) return@find true
                            }
                            return@find false
                        }?.title ?: ""
                    } else ""
                )
            }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyVerticalGrid(
                    state = state,
                    columns = GridCells.Adaptive(55.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .alpha(
                            animateFloatAsState(alphaState).value
                        ),
                    userScrollEnabled = emojiEnabled,
                    verticalArrangement = Arrangement.spacedBy(
                        6.dp,
                        Alignment.CenterVertically
                    ),
                    horizontalArrangement = Arrangement.spacedBy(
                        6.dp,
                        Alignment.CenterHorizontally
                    ),
                    contentPadding = contentPadding,
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    emojiWithCategories.forEach { (title, icon, emojis) ->
                        item(
                            span = { GridItemSpan(maxLineSpan) },
                            key = icon.name
                        ) {
                            val expanded by remember(title, expandedCategories) {
                                derivedStateOf {
                                    title in expandedCategories
                                }
                            }
                            val interactionSource = remember { MutableInteractionSource() }

                            TitleItem(
                                modifier = Modifier
                                    .padding(
                                        bottom = animateDpAsState(
                                            if (expanded) 8.dp
                                            else 0.dp
                                        ).value
                                    )
                                    .container(
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
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
                                        expandedCategories = if (expanded) {
                                            expandedCategories.replace(title, "")
                                        } else expandedCategories + title
                                    }
                                    .padding(16.dp),
                                text = title,
                                icon = icon,
                                endContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier.rotate(
                                            animateFloatAsState(
                                                if (expanded) 180f
                                                else 0f
                                            ).value
                                        )
                                    )
                                }
                            )
                        }
                        if (title in expandedCategories) {
                            emojis.forEach { emoji ->
                                item(
                                    key = emoji
                                ) {
                                    val index by remember(allEmojis, emoji) {
                                        derivedStateOf {
                                            allEmojis.indexOf(emoji)
                                        }
                                    }
                                    val selected by remember(index, selectedEmojiIndex) {
                                        derivedStateOf {
                                            index == selectedEmojiIndex
                                        }
                                    }
                                    val color by animateColorAsState(
                                        if (selected) MaterialTheme.colorScheme.primaryContainer
                                        else SafeLocalContainerColor
                                    )
                                    val borderColor by animateColorAsState(
                                        if (selected) {
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
                                        } else MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                            alpha = 0.1f
                                        )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .animateItem()
                                            .aspectRatio(1f)
                                            .container(
                                                color = color,
                                                shape = CloverShape,
                                                borderColor = borderColor,
                                                resultPadding = 0.dp
                                            )
                                            .hapticsClickable {
                                                onEmojiPicked(index)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        EmojiItem(
                                            emoji = emoji.toString(),
                                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                                            fontScale = 1f
                                        )
                                    }
                                }
                            }
                            item(
                                span = { GridItemSpan(maxLineSpan) }
                            ) {
                                Spacer(Modifier.height(2.dp))
                            }
                        }
                    }
                }
            }

            if (selectedEmojiIndex != null) {
                Column(
                    modifier = Modifier.onGloballyPositioned {
                        topPadding = with(density) {
                            it.size.height.toDp()
                        }
                    }
                ) {
                    var toggleEmoji by remember {
                        mutableIntStateOf(0)
                    }
                    var checked by remember {
                        mutableStateOf(emojiEnabled)
                    }

                    LaunchedEffect(toggleEmoji) {
                        if (toggleEmoji > 0) {
                            if (checked) onEmojiPicked(Random.nextInt(0, allEmojis.lastIndex))
                            else onEmojiPicked(-1)
                        }
                    }

                    PreferenceRowSwitch(
                        title = stringResource(R.string.enable_emoji),
                        containerColor = animateColorAsState(
                            if (emojiEnabled) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceContainer
                        ).value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EnhancedBottomSheetDefaults.containerColor)
                            .padding(start = 16.dp, top = 20.dp, bottom = 8.dp, end = 16.dp),
                        shape = ShapeDefaults.extremeLarge,
                        checked = emojiEnabled,
                        startIcon = Icons.Outlined.Face6,
                        onClick = { checked = it; toggleEmoji++ }
                    )
                    GradientEdge(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp),
                        startColor = EnhancedBottomSheetDefaults.containerColor,
                        endColor = Color.Transparent
                    )
                }
            } else {
                LaunchedEffect(Unit) {
                    topPadding = 16.dp
                }
            }
        }
    }
}