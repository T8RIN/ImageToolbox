package ru.tech.imageresizershrinker.presentation.main_screen.components

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face5
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.icons.emoji.EmojiData
import ru.tech.imageresizershrinker.coreui.icons.emoji.EmojiItem
import ru.tech.imageresizershrinker.coreui.shapes.CloverShape
import ru.tech.imageresizershrinker.coreui.theme.blend
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedSwitch
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.sheets.SimpleDragHandle
import ru.tech.imageresizershrinker.coreui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.coreui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.coreui.widget.text.TitleItem
import kotlin.random.Random

@Composable
fun EmojiSheet(
    selectedEmojiIndex: Int,
    emojiWithCategories: ImmutableList<EmojiData>,
    allEmojis: ImmutableList<Uri>,
    onEmojiPicked: (Int) -> Unit,
    visible: MutableState<Boolean>
) {
    var showSheet by visible
    val state = rememberLazyGridState()

    LaunchedEffect(showSheet) {
        if (selectedEmojiIndex >= 0) {
            state.animateScrollToItem(selectedEmojiIndex)
        }
    }

    val emojiEnabled by remember(selectedEmojiIndex) {
        derivedStateOf {
            selectedEmojiIndex != -1
        }
    }
    val scope = rememberCoroutineScope()

    val sheetContent: @Composable ColumnScope.() -> Unit = {
        val alphaState by remember(emojiEnabled) {
            derivedStateOf {
                if (emojiEnabled) 1f else 0.4f
            }
        }
        val spanModifier = Modifier
            .padding(vertical = 16.dp)
            .container(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    0.4f
                ),
                resultPadding = 0.dp
            )
            .padding(16.dp)
        Column(
            Modifier.fillMaxSize()
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
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                emojiWithCategories.forEach { (title, icon, emojis) ->
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                        key = icon.name
                    ) {
                        Row(
                            modifier = spanModifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(icon, null, modifier = Modifier.padding(end = 16.dp))
                            Text(title)
                        }
                    }
                    emojis.forEach { emoji ->
                        val index = allEmojis.indexOf(emoji)
                        val selected = index == selectedEmojiIndex
                        item(
                            key = emoji
                        ) {
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .container(
                                        color = if (selected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
                                        shape = CloverShape,
                                        borderColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            0.7f
                                        )
                                        else MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                            alpha = 0.1f
                                        ),
                                        resultPadding = 0.dp
                                    )
                                    .clickable {
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
                }
            }
        }
    }

    SimpleSheet(
        dragHandle = {
            SimpleDragHandle {
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(id = R.string.enable_emoji), modifier = Modifier.weight(1f))
                    EnhancedSwitch(
                        thumbIcon = if (emojiEnabled) {
                            Icons.Rounded.Check
                        } else null,
                        checked = emojiEnabled,
                        colors = SwitchDefaults.colors(
                            uncheckedBorderColor = MaterialTheme.colorScheme.outline.blend(
                                MaterialTheme.colorScheme.secondaryContainer, 0.3f
                            ),
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline.blend(
                                MaterialTheme.colorScheme.secondaryContainer, 0.2f
                            ),
                            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        onCheckedChange = {
                            if (!emojiEnabled) onEmojiPicked(Random.nextInt(0, allEmojis.lastIndex))
                            else onEmojiPicked(-1)
                        }
                    )
                }
            }
        },
        sheetContent = sheetContent,
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    Icon(Icons.Rounded.Shuffle, null)
                }
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { showSheet = false }
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
        visible = visible
    )
}