/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.settings.presentation.components

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.emoji.Emoji
import ru.tech.imageresizershrinker.core.resources.icons.Cool
import ru.tech.imageresizershrinker.core.resources.icons.Robot
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.scaleOnTap
import ru.tech.imageresizershrinker.core.ui.widget.other.EmojiItem
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRow
import ru.tech.imageresizershrinker.feature.settings.presentation.components.additional.EmojiSheet

@Composable
fun EmojiSettingItem(
    selectedEmojiIndex: Int,
    onAddColorTupleFromEmoji: (getEmoji: (Int?) -> String, showShoeDescription: (String) -> Unit) -> Unit,
    onUpdateEmoji: (Int) -> Unit,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp),
    shape: Shape = ContainerShapeDefaults.topShape
) {
    val settingsState = LocalSettingsState.current
    val toastHost = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showShoeDescriptionDialog by rememberSaveable { mutableStateOf("") }
    var showEmojiDialog by rememberSaveable { mutableStateOf(false) }

    PreferenceRow(
        modifier = modifier,
        shape = shape,
        title = stringResource(R.string.emoji),
        subtitle = stringResource(R.string.emoji_sub),
        onClick = {
            showEmojiDialog = true
        },
        startIcon = Icons.Outlined.Cool,
        enabled = !settingsState.useRandomEmojis,
        onDisabledClick = {
            scope.launch {
                toastHost.showToast(
                    message = context.getString(R.string.emoji_selection_error),
                    icon = Icons.Rounded.Robot
                )
            }
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
                                        onAddColorTupleFromEmoji(
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
        onEmojiPicked = onUpdateEmoji,
        visible = showEmojiDialog,
        onDismiss = {
            showEmojiDialog = false
        }
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