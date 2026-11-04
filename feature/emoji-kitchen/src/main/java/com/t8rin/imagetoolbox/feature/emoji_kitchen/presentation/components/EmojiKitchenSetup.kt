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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.EMOJI_KITCHEN_COLLECTION_SIZE
import com.t8rin.imagetoolbox.core.domain.EMOJI_KITCHEN_HOME_URL
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Download
import com.t8rin.imagetoolbox.core.resources.icons.FolderZip
import com.t8rin.imagetoolbox.core.resources.icons.Link
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCancellableCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenProgress
import com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.screenLogic.EmojiKitchenComponent

@Composable
internal fun EmojiKitchenSetup(
    component: EmojiKitchenComponent,
    onDownload: () -> Unit,
    onImport: () -> Unit
) {
    val state = component.collectionState
    val progress = state.progress
    val isDownloading = progress?.source == EmojiKitchenProgress.Stage.Downloading
    val isImporting = progress?.source == EmojiKitchenProgress.Stage.Importing
    val uriHandler = LocalUriHandler.current
    val canInstall = state.progress == null && !state.isLoading && !component.isSaving

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .container(
                resultPadding = 12.dp,
                shape = ShapeDefaults.extraLarge
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "😎 + 🔥",
            fontSize = 44.sp,
            modifier = Modifier.container(
                shape = ShapeDefaults.extraLarge,
                resultPadding = 24.dp,
                color = MaterialTheme.colorScheme.surface
            )
        )
        Text(
            text = stringResource(R.string.emoji_kitchen_setup_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.emoji_kitchen_setup_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(
                R.string.emoji_kitchen_download_size,
                humanFileSize(EMOJI_KITCHEN_COLLECTION_SIZE)
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnhancedIconButton(
                    onClick = {
                        uriHandler.openUri(EMOJI_KITCHEN_HOME_URL)
                    },
                    modifier = Modifier.size(52.dp),
                    shape = ShapeDefaults.topStart,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Link,
                        contentDescription = null
                    )
                }
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    EnhancedButton(
                        onClick = onDownload,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = ShapeDefaults.topEnd,
                        enabled = canInstall
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Download,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.download)
                        )
                    }
                }
            }
            EnhancedButton(
                onClick = onImport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = ShapeDefaults.bottom,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                enabled = canInstall
            ) {
                Icon(
                    imageVector = Icons.Rounded.FolderZip,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.emoji_kitchen_import)
                )
            }
        }
        if (progress != null && (isDownloading || isImporting)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .container(
                        resultPadding = 0.dp,
                        color = MaterialTheme.colorScheme.surface
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(
                            when (progress.stage) {
                                EmojiKitchenProgress.Stage.Downloading -> R.string.emoji_kitchen_downloading
                                EmojiKitchenProgress.Stage.Importing -> R.string.emoji_kitchen_importing
                                EmojiKitchenProgress.Stage.Installing -> R.string.emoji_kitchen_installing
                            }
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                    val amount = if (progress.stage == EmojiKitchenProgress.Stage.Installing) {
                        progress.fraction?.let { "${(it * 100).toInt()}%" }.orEmpty()
                    } else {
                        humanFileSize(progress.completed) + if (progress.total > 0) {
                            " / ${humanFileSize(progress.total)}"
                        } else ""
                    }
                    if (amount.isNotEmpty()) {
                        Text(
                            text = amount,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                EnhancedCancellableCircularProgressIndicator(
                    progress = { progress.fraction ?: 0f },
                    modifier = Modifier.size(24.dp),
                    trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                    strokeWidth = 3.dp,
                    onCancel = component::cancelInstallation,
                )
            }
        }
    }
}