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

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.t8rin.archive.ArchiveEncryptionStatus
import com.t8rin.archive.ArchiveFormat
import com.t8rin.archive.SevenZipCompressionMethod
import com.t8rin.archive.ZipCompressionMethod
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.CreateNewFolder
import com.t8rin.imagetoolbox.core.resources.icons.FileOpen
import com.t8rin.imagetoolbox.core.resources.icons.FolderOpen
import com.t8rin.imagetoolbox.core.resources.icons.FolderZip
import com.t8rin.imagetoolbox.core.resources.icons.KeyVariant
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.NoteAdd
import com.t8rin.imagetoolbox.core.resources.icons.Password
import com.t8rin.imagetoolbox.core.resources.icons.VisibilityOff
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.rememberFilename
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.image.UrisPreview
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.negativePadding
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.archive_tools.domain.model.ArchiveMode
import com.t8rin.imagetoolbox.feature.archive_tools.presentation.screenLogic.ArchiveToolsComponent

@Composable
internal fun ArchiveToolsControls(
    component: ArchiveToolsComponent
) {
    val isPortrait by isPortraitOrientationAsState()
    val additionalArchivePicker = rememberFilePicker(onSuccess = component::addUris)
    val replacementArchivePicker = rememberFilePicker(
        onSuccess = { uri: Uri -> component.setUris(listOf(uri)) }
    )

    if (component.mode == ArchiveMode.Archive) {
        val formats = ArchiveFormat.entries.filter {
            it.supportsMultipleFiles || component.uris.size <= 1
        }
        DataSelector(
            value = component.format,
            onValueChange = component::setFormat,
            entries = formats,
            title = stringResource(R.string.archive_format),
            titleIcon = Icons.Outlined.FolderZip,
            itemContentText = { it.title },
            badgeContent = {
                Text(formats.size.toString())
            },
            initialExpanded = true,
            spanCount = 2,
            shape = ShapeDefaults.top,
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedContent(
            targetState = component.format,
            modifier = Modifier.fillMaxWidth()
        ) { format ->
            when (format) {
                ArchiveFormat.Zip -> {
                    DataSelector(
                        value = component.zipCompressionMethod,
                        onValueChange = component::setZipCompressionMethod,
                        entries = ZipCompressionMethod.entries,
                        title = stringResource(R.string.compression_type),
                        titleIcon = Icons.Outlined.FolderZip,
                        itemContentText = { it.title },
                        spanCount = 1,
                        shape = ShapeDefaults.center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                ArchiveFormat.SevenZip -> {
                    DataSelector(
                        value = component.sevenZipCompressionMethod,
                        onValueChange = component::setSevenZipCompressionMethod,
                        entries = SevenZipCompressionMethod.entries,
                        title = stringResource(R.string.compression_type),
                        titleIcon = Icons.Outlined.FolderZip,
                        itemContentText = { it.title },
                        spanCount = 1,
                        shape = ShapeDefaults.center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                else -> Spacer(Modifier.fillMaxWidth())
            }
        }
        Spacer(Modifier.height(4.dp))
        PreferenceRowSwitch(
            title = stringResource(R.string.protect_archive_with_password),
            subtitle = stringResource(
                when {
                    component.canProtectWithPassword -> R.string.protect_archive_with_password_sub
                    component.format.supportsEncryption -> {
                        R.string.archive_password_method_not_supported
                    }

                    else -> R.string.archive_password_supported_formats
                }
            ),
            checked = component.protectWithPassword,
            enabled = component.canProtectWithPassword,
            onClick = component::setProtectWithPassword,
            startIcon = Icons.Outlined.KeyVariant,
            shape = ShapeDefaults.bottom,
            modifier = Modifier.fillMaxWidth(),
            additionalContent = {
                AnimatedVisibility(
                    visible = component.protectWithPassword,
                    modifier = Modifier
                        .fillMaxWidth()
                        .negativePadding(
                            start = 8.dp,
                            end = 8.dp
                        )
                ) {
                    RoundedTextField(
                        value = component.passphrase,
                        onValueChange = component::setPassphrase,
                        label = stringResource(R.string.password),
                        startIcon = Icons.Rounded.Password,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .offset(y = 8.dp)
                            .container(
                                resultPadding = 8.dp,
                                color = MaterialTheme.colorScheme.surface
                            )
                    )
                }
            }
        )
        Spacer(Modifier.height(16.dp))
        UrisPreview(
            uris = component.uris,
            isPortrait = isPortrait,
            onRemoveUri = component::removeUri,
            onAddUris = additionalArchivePicker::pickFile,
            addUrisContent = { width ->
                Icon(
                    imageVector = Icons.Rounded.NoteAdd,
                    contentDescription = stringResource(R.string.add),
                    modifier = Modifier.size(width / 3f)
                )
            }
        )
    } else {
        val archive = component.uris.firstOrNull() ?: return
        val encryptionStatus = component.archiveEncryptionStatus
        val hasEncryptionDetails =
            encryptionStatus == ArchiveEncryptionStatus.PasswordRequired ||
                    encryptionStatus == ArchiveEncryptionStatus.Unsupported

        PreferenceItem(
            title = rememberFilename(archive) ?: archive.toString(),
            subtitle = rememberHumanFileSize(archive),
            startIcon = Icons.Rounded.FileOpen,
            endIcon = Icons.Rounded.MiniEdit,
            onClick = replacementArchivePicker::pickFile,
            shape = ShapeDefaults.byIndex(
                index = 0,
                size = if (hasEncryptionDetails) 2 else 1
            ),
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedVisibility(
            visible = encryptionStatus == ArchiveEncryptionStatus.PasswordRequired,
            modifier = Modifier.fillMaxWidth()
        ) {
            PreferenceItem(
                title = stringResource(R.string.encrypted_file_detected),
                subtitle = stringResource(R.string.archive_password_required_sub),
                startIcon = Icons.Outlined.KeyVariant,
                shape = ShapeDefaults.bottom,
                bottomContent = {
                    RoundedTextField(
                        value = component.passphrase,
                        onValueChange = component::setPassphrase,
                        label = stringResource(R.string.password),
                        startIcon = Icons.Rounded.Password,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .container(
                                resultPadding = 8.dp,
                                color = MaterialTheme.colorScheme.surface
                            )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
        AnimatedVisibility(
            visible = encryptionStatus == ArchiveEncryptionStatus.Unsupported,
            modifier = Modifier.fillMaxWidth()
        ) {
            PreferenceItem(
                title = stringResource(R.string.unsupported_archive_encryption),
                subtitle = stringResource(R.string.unsupported_archive_encryption_sub),
                startIcon = Icons.Outlined.KeyVariant,
                shape = ShapeDefaults.bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        PreferenceRowSwitch(
            title = stringResource(R.string.extract_to_subfolder),
            subtitle = stringResource(R.string.extract_to_subfolder_sub),
            checked = component.extractionOptions.createSubfolder,
            onClick = component::setCreateSubfolder,
            startIcon = Icons.Outlined.CreateNewFolder,
            shape = ShapeDefaults.top,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        PreferenceRowSwitch(
            title = stringResource(R.string.preserve_archive_folders),
            subtitle = stringResource(R.string.preserve_archive_folders_sub),
            checked = component.extractionOptions.preserveDirectoryStructure,
            onClick = component::setPreserveDirectoryStructure,
            startIcon = Icons.Outlined.FolderOpen,
            shape = ShapeDefaults.center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        PreferenceRowSwitch(
            title = stringResource(R.string.skip_hidden_archive_files),
            subtitle = stringResource(R.string.skip_hidden_archive_files_sub),
            checked = component.extractionOptions.skipHiddenFiles,
            onClick = component::setSkipHiddenFiles,
            startIcon = Icons.Outlined.VisibilityOff,
            shape = ShapeDefaults.bottom,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
