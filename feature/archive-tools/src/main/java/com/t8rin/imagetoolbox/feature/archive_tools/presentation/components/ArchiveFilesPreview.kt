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

package com.t8rin.imagetoolbox.feature.archive_tools.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.archive.ArchiveEncryptionStatus
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Archive
import com.t8rin.imagetoolbox.core.resources.icons.Lock
import com.t8rin.imagetoolbox.core.resources.icons.LockOpen
import com.t8rin.imagetoolbox.core.resources.icons.NoteAdd
import com.t8rin.imagetoolbox.core.resources.icons.WarningAmber
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.widget.image.UrisPreview
import com.t8rin.imagetoolbox.core.ui.widget.image.urisPreview
import com.t8rin.imagetoolbox.feature.archive_tools.presentation.screenLogic.ArchivePassphraseStatus
import com.t8rin.imagetoolbox.feature.archive_tools.presentation.screenLogic.ArchiveToolsComponent

@Composable
internal fun ArchiveFilesPreview(
    component: ArchiveToolsComponent,
    isPortrait: Boolean
) {
    val additionalArchivePicker = rememberFilePicker(onSuccess = component::addUris)
    val previewUris = component.uris.sortedBy { uri ->
        if (
            component.archiveEncryptionStatus(uri) ==
            ArchiveEncryptionStatus.PasswordRequired
        ) 0 else 1
    }

    UrisPreview(
        uris = previewUris,
        isPortrait = true,
        onRemoveUri = component::removeUri,
        onAddUris = additionalArchivePicker::pickFile,
        onClickUri = { uri ->
            when (component.archiveEncryptionStatus(uri)) {
                ArchiveEncryptionStatus.PasswordRequired -> {
                    if (
                        component.archivePassphraseStatus(uri) !=
                        ArchivePassphraseStatus.Checking
                    ) {
                        component.requestArchivePassphrase(uri)
                    }
                }

                ArchiveEncryptionStatus.Unsupported -> {
                    AppToastHost.showFailureToast(R.string.unsupported_archive_encryption)
                }

                ArchiveEncryptionStatus.None, null -> Unit
            }
        },
        addUrisContent = { width ->
            Icon(
                imageVector = Icons.Rounded.NoteAdd,
                contentDescription = stringResource(R.string.add),
                modifier = Modifier.size(width / 3f)
            )
        },
        errorContent = { index, width ->
            val uri = previewUris[index]
            val status = component.archiveEncryptionStatus(uri)
            val passphraseStatus = component.archivePassphraseStatus(uri)
            val tileColor = when {
                status == ArchiveEncryptionStatus.Unsupported -> {
                    MaterialTheme.colorScheme.errorContainer
                }

                passphraseStatus == ArchivePassphraseStatus.Verified -> {
                    MaterialTheme.colorScheme.primaryContainer
                }

                passphraseStatus == ArchivePassphraseStatus.Checking -> {
                    MaterialTheme.colorScheme.tertiaryContainer
                }

                status == ArchiveEncryptionStatus.PasswordRequired -> {
                    MaterialTheme.colorScheme.errorContainer
                }

                else -> if (isPortrait) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.surface
            }
            val tileContentColor = when {
                status == ArchiveEncryptionStatus.Unsupported -> {
                    MaterialTheme.colorScheme.onErrorContainer
                }

                passphraseStatus == ArchivePassphraseStatus.Verified -> {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }

                passphraseStatus == ArchivePassphraseStatus.Checking -> {
                    MaterialTheme.colorScheme.onTertiaryContainer
                }

                status == ArchiveEncryptionStatus.PasswordRequired -> {
                    MaterialTheme.colorScheme.onErrorContainer
                }

                else -> MaterialTheme.colorScheme.onSurface
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .background(tileColor),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {
                Icon(
                    imageVector = when {
                        status == ArchiveEncryptionStatus.Unsupported -> {
                            Icons.Outlined.WarningAmber
                        }

                        passphraseStatus == ArchivePassphraseStatus.Verified -> {
                            Icons.Rounded.LockOpen
                        }

                        passphraseStatus == ArchivePassphraseStatus.Invalid -> {
                            Icons.Outlined.WarningAmber
                        }

                        status == ArchiveEncryptionStatus.PasswordRequired -> {
                            Icons.Rounded.Lock
                        }

                        else -> Icons.Outlined.Archive
                    },
                    contentDescription = null,
                    modifier = Modifier.size(width / 3f),
                    tint = tileContentColor
                )
            }
        },
        showTransparencyChecker = false,
        allowHorizontal = isPortrait,
        modifier = Modifier.urisPreview(
            allowHorizontal = isPortrait,
            isPortrait = isPortrait
        )
    )
}
