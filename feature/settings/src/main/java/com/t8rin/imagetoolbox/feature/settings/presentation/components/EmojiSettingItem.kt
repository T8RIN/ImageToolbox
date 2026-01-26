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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Casino
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
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Cool
import com.t8rin.imagetoolbox.core.resources.shapes.CloverShape
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap
import com.t8rin.imagetoolbox.core.ui.widget.other.EmojiItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRow
import com.t8rin.imagetoolbox.core.ui.widget.sheets.EmojiSelectionSheet

@Composable
fun EmojiSettingItem(
    selectedEmojiIndex: Int,
    onAddColorTupleFromEmoji: (String) -> Unit,
    onUpdateEmoji: (Int) -> Unit,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp),
    shape: Shape = ShapeDefaults.top
) {
    val settingsState = LocalSettingsState.current
    val essentials = rememberLocalEssentials()
    var showSecretDescriptionDialog by rememberSaveable { mutableStateOf("") }
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
            essentials.showToast(
                message = essentials.getString(R.string.emoji_selection_error),
                icon = Icons.Rounded.Casino
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
                EmojiItem(
                    emoji = emoji?.toString(),
                    modifier = Modifier.then(
                        if (emoji != null) {
                            Modifier.scaleOnTap(
                                onRelease = { time ->
                                    if (time > 500) {
                                        onAddColorTupleFromEmoji(emoji.toString())
                                        if (emoji.toString().contains("frog", true)) {
                                            showSecretDescriptionDialog = emoji.toString()
                                        } else if (emoji.toString().contains("shoe", true)) {
                                            showShoeDescriptionDialog = emoji.toString()
                                        }
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
    EmojiSelectionSheet(
        selectedEmojiIndex = selectedEmojiIndex,
        onEmojiPicked = onUpdateEmoji,
        visible = showEmojiDialog,
        onDismiss = {
            showEmojiDialog = false
        }
    )

    EnhancedAlertDialog(
        visible = showShoeDescriptionDialog.isNotEmpty(),
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
        }
    )

    EnhancedAlertDialog(
        visible = showSecretDescriptionDialog.isNotEmpty(),
        icon = {
            EmojiItem(
                emoji = showSecretDescriptionDialog,
                fontScale = 1f,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            )
        },
        text = {
            Text(
                text = "\uD83D\uDC49 \uD83D\uDC46, \uD83D\uDC47 \uD83D\uDE4B \uD83D\uDC70 ❗ \uD83D\uDC64 \uD83D\uDC96 \uD83D\uDCF6 \uD83C\uDF05",
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { showSecretDescriptionDialog = "" }
            ) {
                Text(stringResource(R.string.close))
            }
        },
        onDismissRequest = {
            showSecretDescriptionDialog = ""
        }
    )
}