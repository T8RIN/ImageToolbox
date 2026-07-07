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

package com.t8rin.imagetoolbox.feature.batchrename.presentation

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FileOpen
import com.t8rin.imagetoolbox.core.resources.icons.FileRename
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.FileNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.batchrename.presentation.components.BatchRenameControls
import com.t8rin.imagetoolbox.feature.batchrename.presentation.screenLogic.BatchRenameComponent

@Composable
fun BatchRenameContent(component: BatchRenameComponent) {
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.files.isNotEmpty()) {
            showExitDialog = true
        } else {
            component.onGoBack()
        }
    }

    val filePicker = rememberFilePicker(onSuccess = component::setUris)
    val additionalFilePicker = rememberFilePicker(onSuccess = component::addUris)

    AutoFilePicker(
        onAutoPick = filePicker::pickFile,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        component.onPermissionResult(result.resultCode == Activity.RESULT_OK)
    }

    LaunchedEffect(component.pendingPermission) {
        component.pendingPermission?.let { sender ->
            runCatching {
                permissionLauncher.launch(IntentSenderRequest.Builder(sender).build())
            }.onFailure {
                component.onPermissionResult(false)
            }
        }
    }

    val validationError by component.validationError.collectAsStateWithLifecycle()

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = component.files.isEmpty(),
        onGoBack = onBack,
        title = {
            Text(
                text = stringResource(R.string.batch_rename),
                modifier = Modifier.marquee()
            )
        },
        actions = {},
        topAppBarPersistentActions = { TopAppBarEmoji() },
        imagePreview = {},
        placeImagePreview = false,
        showImagePreviewAsStickyHeader = false,
        addHorizontalCutoutPaddingIfNoPreview = component.files.isNotEmpty(),
        noDataControls = {
            FileNotPickedWidget(
                onPickFile = filePicker::pickFile,
                text = stringResource(R.string.pick_files_to_rename)
            )
        },
        controls = {
            BatchRenameControls(
                component = component,
                validationError = validationError
            )
        },
        buttons = {
            var showConfirmDialog by rememberSaveable {
                mutableStateOf(false)
            }

            BottomButtonsBlock(
                isNoData = component.files.isEmpty(),
                onSecondaryButtonClick = if (component.files.isEmpty()) {
                    filePicker::pickFile
                } else {
                    additionalFilePicker::pickFile
                },
                secondaryButtonIcon = Icons.Rounded.FileOpen,
                secondaryButtonText = stringResource(R.string.pick_files),
                onPrimaryButtonClick = { showConfirmDialog = true },
                primaryButtonIcon = Icons.Outlined.FileRename,
                primaryButtonText = stringResource(R.string.rename),
                isPrimaryButtonEnabled = validationError == null,
                actions = {
                    if (component.files.isNotEmpty()) {
                        EnhancedChip(
                            selected = true,
                            onClick = null,
                            selectedColor = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(component.files.size.toString())
                        }
                    }
                }
            )

            ResetDialog(
                visible = showConfirmDialog,
                onDismiss = { showConfirmDialog = false },
                onReset = component::rename,
                title = stringResource(R.string.confirm_rename_title),
                text = stringResource(R.string.rename_cannot_undone),
                icon = Icons.Outlined.FileRename,
                dismissText = stringResource(R.string.rename)
            )
        },
        canShowScreenData = component.files.isNotEmpty()
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    LoadingDialog(
        visible = component.isLoading,
        canCancel = component.pendingPermission == null,
        onCancelLoading = component::cancel,
    )
}
