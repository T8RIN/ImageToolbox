package ru.tech.imageresizershrinker.presentation.main_screen.components

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face5
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.icons.emoji.EmojiData
import ru.tech.imageresizershrinker.presentation.root.icons.emoji.EmojiItem
import ru.tech.imageresizershrinker.presentation.root.shapes.CloverShape
import ru.tech.imageresizershrinker.presentation.root.theme.blend
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import kotlin.random.Random

@Composable
fun EmojiSheet(
    selectedEmojiIndex: Int,
    emojiWithCategories: List<EmojiData>,
    allEmojis: List<Uri>,
    onEmojiPicked: (Int) -> Unit,
    visible: MutableState<Boolean>
) {
    var showSheet by visible
    val scope = rememberCoroutineScope()
    val state = rememberLazyGridState()

    LaunchedEffect(showSheet) {
        if (selectedEmojiIndex >= 0) {
            state.animateScrollToItem(selectedEmojiIndex)
        }
    }

    val emojiEnabled = selectedEmojiIndex != -1
    val thumbIcon: (@Composable () -> Unit)? = if (emojiEnabled) {
        {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize)
            )
        }
    } else null
    val sheetContent: @Composable ColumnScope.() -> Unit = {
        Column(
            Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp))
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    .clip(MaterialTheme.shapes.large)
                    .clickable {
                        if (!emojiEnabled) onEmojiPicked(Random.nextInt(0, allEmojis.lastIndex))
                        else onEmojiPicked(-1)
                    }
                    .padding(start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.enable_emoji), modifier = Modifier.weight(1f))
                Switch(
                    thumbContent = thumbIcon,
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
            HorizontalDivider()
            LazyVerticalGrid(
                state = state,
                columns = GridCells.Adaptive(55.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .alpha(
                        animateFloatAsState(targetValue = if (emojiEnabled) 1f else 0.4f).value
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
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .container(
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                        0.4f
                                    ),
                                    resultPadding = 0.dp
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(icon, null, modifier = Modifier.padding(end = 16.dp))
                            Text(title)
                        }
                    }
                    emojis.forEach { emoji ->
                        val index = allEmojis.indexOf(emoji)
                        item {
                            val selected = index == selectedEmojiIndex
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .size(58.dp)
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
                                    emoji = emoji,
                                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                                    fontScale = 1f
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp))
                    .padding(16.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleItem(text = stringResource(R.string.emoji), icon = Icons.Outlined.Face5)
                Spacer(Modifier.weight(1f))
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
        }
    }

    SimpleSheet(
        nestedScrollEnabled = false,
        sheetContent = sheetContent,
        visible = visible
    )
}