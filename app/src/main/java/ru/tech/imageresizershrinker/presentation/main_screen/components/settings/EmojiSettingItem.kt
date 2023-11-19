package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.main_screen.components.EmojiSheet
import ru.tech.imageresizershrinker.presentation.root.icons.emoji.Emoji
import ru.tech.imageresizershrinker.presentation.root.icons.emoji.EmojiItem
import ru.tech.imageresizershrinker.presentation.root.icons.emoji.allIcons
import ru.tech.imageresizershrinker.presentation.root.icons.emoji.allIconsCategorized
import ru.tech.imageresizershrinker.presentation.root.icons.material.Cool
import ru.tech.imageresizershrinker.presentation.root.shapes.CloverShape
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.scaleOnTap
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRow
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun EmojiSettingItem(
    selectedEmojiIndex: Int,
    addColorTupleFromEmoji: (getEmoji: (Int?) -> String, showShoeDescription: (String) -> Unit) -> Unit,
    updateEmoji: (Int) -> Unit,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp),
    shape: Shape = SettingsShapeDefaults.bottomShape
) {
    var showShoeDescriptionDialog by rememberSaveable { mutableStateOf("") }
    val showEmojiDialog = rememberSaveable { mutableStateOf(false) }
    PreferenceRow(
        modifier = modifier,
        applyHorPadding = false,
        shape = shape,
        title = stringResource(R.string.emoji),
        subtitle = stringResource(R.string.emoji_sub),
        onClick = {
            showEmojiDialog.value = true
        },
        startContent = {
            Icon(
                imageVector = Icons.Outlined.Cool,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
        endContent = {
            val emoji = LocalSettingsState.current.selectedEmoji
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(64.dp)
                    .container(
                        shape = CloverShape,
                        color = MaterialTheme.colorScheme
                            .surfaceVariant
                            .copy(alpha = 0.5f),
                        borderColor = MaterialTheme.colorScheme.outlineVariant(
                            0.2f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                val emojis = Emoji.allIcons()
                EmojiItem(
                    emoji = emoji?.toString(),
                    modifier = Modifier.then(
                        if (emoji != null) {
                            Modifier.scaleOnTap(
                                onRelease = { time ->
                                    if (time > 500) {
                                        addColorTupleFromEmoji(
                                            { index ->
                                                index?.let {
                                                    emojis[it].toString()
                                                } ?: ""
                                            }, {
                                                showShoeDescriptionDialog = it
                                            }
                                        )
                                    }
                                }
                            )
                        } else Modifier
                    ),
                    fontScale = 1f,
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    onNoEmoji = { size ->
                        Icon(
                            imageVector = Icons.Rounded.Block,
                            contentDescription = null,
                            modifier = Modifier.size(size)
                        )
                    }
                )
            }
        }
    )
    EmojiSheet(
        selectedEmojiIndex = selectedEmojiIndex,
        emojiWithCategories = Emoji.allIconsCategorized(),
        allEmojis = Emoji.allIcons(),
        onEmojiPicked = updateEmoji,
        visible = showEmojiDialog
    )

    if (showShoeDescriptionDialog.isNotEmpty()) {
        AlertDialog(
            icon = {
                EmojiItem(
                    emoji = showShoeDescriptionDialog,
                    fontScale = 1f,
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                )
            },
            title = {
                Text(text = "Shoe")
            },
            text = {
                Text(text = "15.07.1981 - Shoe, (ShoeUnited since 1998)")
            },
            confirmButton = {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { showShoeDescriptionDialog = "" }
                ) {
                    Text(stringResource(R.string.close))
                }
            },
            onDismissRequest = {
                showShoeDescriptionDialog = ""
            },
            modifier = Modifier.alertDialogBorder()
        )
    }
}