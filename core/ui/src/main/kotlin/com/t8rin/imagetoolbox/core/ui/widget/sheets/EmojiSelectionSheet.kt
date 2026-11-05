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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.emoji.Emoji
import com.t8rin.imagetoolbox.core.resources.icons.Face5
import com.t8rin.imagetoolbox.core.resources.icons.Face6
import com.t8rin.imagetoolbox.core.resources.icons.KeyboardArrowDown
import com.t8rin.imagetoolbox.core.resources.icons.MotionMode
import com.t8rin.imagetoolbox.core.resources.icons.Shuffle
import com.t8rin.imagetoolbox.core.resources.shapes.SquircleShape
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
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
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlin.random.Random

@Composable
fun EmojiSelectionSheet(
    selectedEmojiIndex: Int?,
    onEmojiPicked: (Int) -> Unit,
    visible: Boolean,
    icon: ImageVector = Icons.Rounded.Face5,
    onDismiss: () -> Unit,
    useAnimatedEmojis: Boolean = LocalSettingsState.current.useAnimatedEmojis
) {
    val state = rememberLazyListState()
    val emojiWithCategories = Emoji.allIconsCategorized
    val allEmojis = Emoji.allIcons
    val emojiIndices = remember(allEmojis) {
        allEmojis.withIndex().associate { (index, emoji) ->
            emoji to index
        }
    }
    val animatedEmojiPaths = remember(allEmojis) {
        allEmojis.associateWith { emoji ->
            Emoji.animatedIconFor(emoji)?.toString()
        }
    }

    var expandedCategories by rememberSaveable(visible) {
        mutableStateOf(emptyList<Int>())
    }
    var scrollToEmoji by remember(visible) {
        mutableStateOf(selectedEmojiIndex?.let { it to false })
    }

    val emojiEnabled = selectedEmojiIndex != -1

    EnhancedModalBottomSheet(
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedEmojiIndex != null) {
                    EnhancedIconButton(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        enabled = emojiEnabled && allEmojis.isNotEmpty(),
                        onClick = {
                            val index = Random.nextInt(allEmojis.size)
                            onEmojiPicked(index)
                            scrollToEmoji = index to true
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
            if (emojiEnabled) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    )
                ) {
                    TopAppBarEmoji(
                        allowMultiple = false,
                        containerColor = MaterialTheme.colorScheme.surface,
                        shape = SquircleShape,
                        contentPadding = 4.dp
                    )
                    Text(
                        text = stringResource(R.string.emoji),
                        style = PreferenceItemDefaults.TitleFontStyle,
                        modifier = Modifier.offset(x = (-8).dp)
                    )
                }
            } else {
                TitleItem(
                    text = stringResource(R.string.emoji),
                    icon = icon
                )
            }
        },
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        }
    ) {
        val alphaState = if (emojiEnabled) 1f else 0.4f

        BoxWithConstraints {
            val horizontalPadding = 16.dp
            val buttonSize = 52.dp
            val horizontalSpacing = 4.dp
            val availableWidth = maxWidth - horizontalPadding * 2
            val columns = ((availableWidth + horizontalSpacing) / (buttonSize + horizontalSpacing))
                .toInt().coerceAtLeast(1)
            val emojiRows = remember(emojiWithCategories, columns) {
                emojiWithCategories.associate { (title, _, emojis) ->
                    title to emojis.chunked(columns)
                }
            }

            LaunchedEffect(visible, scrollToEmoji, emojiRows, expandedCategories) {
                if (!visible) return@LaunchedEffect
                val (index, animate) = scrollToEmoji ?: return@LaunchedEffect
                val emoji = allEmojis.getOrNull(index) ?: return@LaunchedEffect
                var itemIndex = 0
                for ((title, _, emojis) in emojiWithCategories) {
                    val expanded = title in expandedCategories
                    val emojiIndex = emojis.indexOf(emoji)
                    if (emojiIndex >= 0) {
                        if (expanded) {
                            val rowIndex = itemIndex + 1 + emojiIndex / columns
                            if (animate) state.animateScrollToItem(rowIndex)
                            else state.requestScrollToItem(rowIndex)
                            scrollToEmoji = null
                        } else {
                            expandedCategories = expandedCategories + title
                        }
                        break
                    }
                    itemIndex += if (expanded) emojiRows.getValue(title).size + 2 else 1
                }
            }

            val density = LocalDensity.current
            var topPadding by remember {
                mutableStateOf(0.dp)
            }
            val contentPadding by remember(topPadding) {
                derivedStateOf {
                    PaddingValues(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        bottom = 16.dp,
                        top = topPadding
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    state = state,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .alpha(
                            animateFloatAsState(alphaState).value
                        ),
                    userScrollEnabled = emojiEnabled,
                    verticalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = contentPadding,
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    emojiWithCategories.forEach { (title, icon, _) ->
                        val expanded = title in expandedCategories
                        item(
                            key = title,
                            contentType = "category"
                        ) {
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
                                        enabled = emojiEnabled,
                                        interactionSource = interactionSource,
                                        indication = LocalIndication.current
                                    ) {
                                        expandedCategories = if (expanded) {
                                            expandedCategories - title
                                        } else expandedCategories + title
                                    }
                                    .padding(16.dp),
                                text = stringResource(title),
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
                        if (expanded) {
                            items(
                                items = emojiRows.getValue(title),
                                key = { "${title}_${it.first()}" },
                                contentType = { "emojis" }
                            ) { row ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateItem(),
                                    horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
                                ) {
                                    row.forEach { emoji ->
                                        val index = emojiIndices[emoji] ?: -1

                                        EnhancedIconButton(
                                            onClick = { onEmojiPicked(index) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .wrapContentSize()
                                                .size(buttonSize),
                                            enabled = emojiEnabled,
                                            containerColor = if (index == selectedEmojiIndex) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else Color.Transparent,
                                            shape = ShapeDefaults.default
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val animatedEmoji = animatedEmojiPaths[emoji]
                                                EmojiItem(
                                                    emoji = emoji.toString(),
                                                    animatedEmoji = animatedEmoji.takeIf {
                                                        useAnimatedEmojis && emojiEnabled
                                                    },
                                                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                                                    filterQuality = FilterQuality.Medium,
                                                    animateEmojiChange = false,
                                                    shimmerEnabled = false
                                                )
                                                if (useAnimatedEmojis && animatedEmoji != null) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.MotionMode,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary.copy(
                                                            0.6f
                                                        ),
                                                        modifier = Modifier
                                                            .align(Alignment.TopEnd)
                                                            .offset((-6).dp, 6.dp)
                                                            .size(12.dp)
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
                            item(
                                key = "spacer_$title",
                                contentType = "spacer"
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
