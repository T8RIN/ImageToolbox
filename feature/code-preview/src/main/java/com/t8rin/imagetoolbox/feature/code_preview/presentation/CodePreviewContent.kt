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

package com.t8rin.imagetoolbox.feature.code_preview.presentation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.code_preview.presentation.components.CodePreviewCard
import com.t8rin.imagetoolbox.feature.code_preview.presentation.components.CodePreviewControls
import com.t8rin.imagetoolbox.feature.code_preview.presentation.screenLogic.CodePreviewComponent

@Composable
fun CodePreviewContent(component: CodePreviewComponent) {
    val params = component.params

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showFolderSelectionDialog by rememberSaveable { mutableStateOf(false) }
    var editSheetData by remember { mutableStateOf(emptyList<Uri>()) }

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }

    ZoomModalSheet(
        data = component.previewBitmap,
        visible = showZoomSheet,
        onDismiss = {
            showZoomSheet = false
        }
    )

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            Text(
                text = stringResource(R.string.code_preview_generator),
                textAlign = TextAlign.Center,
                modifier = Modifier.marquee()
            )
        },
        onGoBack = {
            if (component.haveChanges) showExitDialog = true
            else component.onGoBack()
        },
        actions = {
            ShareButton(
                enabled = params.code.isNotBlank() && !component.isSaving,
                onShare = component::shareBitmap,
                onCopy = {
                    component.cacheBitmap(
                        onComplete = Clipboard::copy
                    )
                },
                onEdit = {
                    component.cacheBitmap {
                        editSheetData = listOf(it)
                    }
                }
            )
        },
        topAppBarPersistentActions = {
            if (component.previewBitmap == null) {
                TopAppBarEmoji()
            }

            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.previewBitmap != null
            )
        },
        imagePreview = {
            CodePreviewCard(
                previewBitmap = component.previewBitmap,
                showCanvasBackground = params.showCanvasBackground,
                canvasCornerRadius = params.canvasCornerRadius,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        },
        controls = {
            CodePreviewControls(component = component)
        },
        buttons = {
            BottomButtonsBlock(
                isNoData = false,
                onSecondaryButtonClick = {},
                isSecondaryButtonVisible = false,
                onPrimaryButtonClick = {
                    component.saveBitmap(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                isPrimaryButtonEnabled = params.code.isNotBlank() && !component.isSaving,
                actions = {}
            )
        },
        canShowScreenData = true
    )

    OneTimeSaveLocationSelectionDialog(
        visible = showFolderSelectionDialog,
        onDismiss = { showFolderSelectionDialog = false },
        onSaveRequest = { uri ->
            component.saveBitmap(uri)
        },
        formatForFilenameSelection = params.outputFormat,
        hasOriginalUri = false
    )

    ProcessImagesPreferenceSheet(
        uris = editSheetData,
        visible = editSheetData.isNotEmpty(),
        onDismiss = { editSheetData = emptyList() },
        onNavigate = component.onNavigate
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    LoadingDialog(
        visible = component.isSaving,
        onCancelLoading = component::cancelSaving
    )
}
