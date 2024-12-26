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

package ru.tech.imageresizershrinker.feature.root.presentation.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.domain.TELEGRAM_CHANNEL_LINK
import ru.tech.imageresizershrinker.core.domain.TELEGRAM_GROUP_LINK
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Telegram
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedAlertDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton

@Composable
fun TelegramGroupDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onRedirected: () -> Unit
) {
    val linkHandler = LocalUriHandler.current
    EnhancedAlertDialog(
        visible = visible,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Telegram,
                contentDescription = "Telegram"
            )
        },
        title = {
            Text(stringResource(R.string.image_toolbox_in_telegram))
        },
        text = {
            Text(stringResource(R.string.image_toolbox_in_telegram_sub))
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    onRedirected()
                    linkHandler.openUri(TELEGRAM_GROUP_LINK)
                }
            ) {
                Text(stringResource(R.string.group))
            }
        },
        dismissButton = {
            EnhancedIconButton(
                onClick = {
                    onRedirected()
                },
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Icon(
                    imageVector = Icons.Rounded.Cancel,
                    contentDescription = null
                )
            }
            EnhancedButton(
                onClick = {
                    onRedirected()
                    linkHandler.openUri(TELEGRAM_CHANNEL_LINK)
                },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Text(stringResource(R.string.ci_channel))
            }
        },
        onDismissRequest = onDismiss
    )
}