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

package com.t8rin.imagetoolbox.feature.zip.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.archive.ArchiveEncryptionStatus
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FileOpen
import com.t8rin.imagetoolbox.core.resources.icons.FolderZip
import com.t8rin.imagetoolbox.core.resources.icons.Unarchive
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFolderPicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.zip.domain.model.ArchiveMode
import com.t8rin.imagetoolbox.feature.zip.presentation.components.ArchiveToolsNoDataControls
import com.t8rin.imagetoolbox.feature.zip.presentation.components.ZipControls
import com.t8rin.imagetoolbox.feature.zip.presentation.screenLogic.ZipComponent


@Composable
fun ZipContent(
    component: ZipComponent
) {
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.haveChanges) showExitDialog = true else component.onGoBack()
    }

    val archivePicker = rememberFilePicker { uris: List<Uri> ->
        component.setMode(ArchiveMode.Archive)
        component.setUris(uris)
    }
    val extractPicker = rememberFilePicker(
        onSuccess = { uri: Uri ->
            component.setMode(ArchiveMode.Extract)
            component.setUris(listOf(uri))
        }
    )
    val filePicker = if (component.mode == ArchiveMode.Archive) archivePicker else extractPicker
    val folderPicker = rememberFolderPicker(onSuccess = component::startExtraction)
    val archiveCreator = rememberFileCreator(
        mimeType = MimeType.Single(component.format.mimeType),
        onSuccess = component::startArchiving
    )

    val isPortrait by isPortraitOrientationAsState()

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            Text(
                text = stringResource(R.string.archive_tools),
                modifier = Modifier.marquee()
            )
        },
        topAppBarPersistentActions = {
            TopAppBarEmoji()
        },
        onGoBack = onBack,
        actions = {
            if (component.mode == ArchiveMode.Archive && component.uris.isNotEmpty()) {
                ShareButton(
                    enabled = !component.protectWithPassword || component.passphrase.isNotEmpty(),
                    onShare = component::shareArchive,
                    dialogTitle = stringResource(R.string.archive),
                    dialogIcon = Icons.Outlined.FolderZip
                )
            }
        },
        imagePreview = {},
        showImagePreviewAsStickyHeader = false,
        placeImagePreview = false,
        contentPadding = if (component.uris.isEmpty()) 0.dp else 20.dp,
        addHorizontalCutoutPaddingIfNoPreview = component.uris.isNotEmpty(),
        noDataControls = {
            ArchiveToolsNoDataControls(
                onArchive = {
                    component.setMode(ArchiveMode.Archive)
                    archivePicker.pickFile()
                },
                onExtract = {
                    component.setMode(ArchiveMode.Extract)
                    extractPicker.pickFile()
                }
            )
        },
        controls = {
            if (isPortrait) Spacer(Modifier.height(20.dp))

            ZipControls(component = component)
        },
        buttons = { screenActions ->
            BottomButtonsBlock(
                isNoData = component.uris.isEmpty(),
                onSecondaryButtonClick = filePicker::pickFile,
                secondaryButtonIcon = Icons.Rounded.FileOpen,
                secondaryButtonText = stringResource(R.string.pick_file),
                isPrimaryButtonVisible = component.uris.isNotEmpty(),
                isPrimaryButtonEnabled = when (component.mode) {
                    ArchiveMode.Archive -> !component.protectWithPassword ||
                            component.passphrase.isNotEmpty()

                    ArchiveMode.Extract -> when (component.archiveEncryptionStatus) {
                        ArchiveEncryptionStatus.Unsupported -> false
                        ArchiveEncryptionStatus.PasswordRequired -> component.passphrase.isNotEmpty()
                        ArchiveEncryptionStatus.None, null -> true
                    }
                },
                onPrimaryButtonClick = {
                    if (component.mode == ArchiveMode.Archive) {
                        archiveCreator.make(component.createTargetFilename())
                    } else folderPicker.pickFolder()
                },
                primaryButtonIcon = if (component.mode == ArchiveMode.Archive) {
                    Icons.Rounded.FolderZip
                } else {
                    Icons.Rounded.Unarchive
                },
                primaryButtonText = stringResource(
                    if (component.mode == ArchiveMode.Archive) R.string.archive
                    else R.string.extract
                ),
                showNullDataButtonAsContainer = true,
                drawBothStrokes = true,
                actions = screenActions
            )
        },
        canShowScreenData = component.uris.isNotEmpty()
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.left,
        onCancelLoading = component::cancelSaving
    )

}