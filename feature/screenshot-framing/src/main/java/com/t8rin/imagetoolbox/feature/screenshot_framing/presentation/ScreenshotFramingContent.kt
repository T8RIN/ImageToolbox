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

package com.t8rin.imagetoolbox.feature.screenshot_framing.presentation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.UndoRedoButtons
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.SimplePicture
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.components.ScreenshotFramingControls
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.screenLogic.ScreenshotFramingComponent

@Composable
fun ScreenshotFramingContent(component: ScreenshotFramingComponent) {
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showFolderSelectionDialog by rememberSaveable { mutableStateOf(false) }
    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var editSheetData by remember { mutableStateOf(emptyList<Uri>()) }
    val isPortrait by isPortraitOrientationAsState()
    val imagePicker = rememberImagePicker { uri: Uri -> component.setUri(uri) }
    val canExport =
        component.previewBitmap != null && !component.isSaving && !component.isImageLoading

    AutoFilePicker(
        onAutoPick = imagePicker::pickImage,
        isPickedAlready = component.initialUri != null
    )

    AdaptiveLayoutScreen(
        isLoading = component.isPreviewLoading,
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.screenshot_framing),
                input = component.previewBitmap,
                isLoading = component.isPreviewLoading,
                size = null
            )
        },
        onGoBack = {
            if (component.haveChanges) showExitDialog = true
            else component.onGoBack()
        },
        actions = {
            ShareButton(
                enabled = canExport,
                onShare = component::shareBitmap,
                onCopy = { component.cacheBitmap(Clipboard::copy) },
                onEdit = {
                    component.cacheBitmap { editSheetData = listOf(it) }
                }
            )
            UndoRedoButtons(
                canUndo = component.canUndo,
                canRedo = component.canRedo,
                onUndo = component::undo,
                onRedo = component::redo,
                modifier = Modifier.padding(2.dp)
            )
        },
        topAppBarPersistentActions = {
            if (component.previewBitmap == null) TopAppBarEmoji()
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.previewBitmap != null
            )
        },
        imagePreview = {
            SimplePicture(
                bitmap = component.previewBitmap,
                loading = component.isPreviewLoading,
                enableContainer = false,
                shape = RectangleShape
            )
        },
        controls = { ScreenshotFramingControls(component) },
        noDataControls = {
            ImageNotPickedWidget(onPickImage = imagePicker::pickImage)
        },
        buttons = { actions ->
            BottomButtonsBlock(
                isNoData = component.uri == null,
                onSecondaryButtonClick = imagePicker::pickImage,
                onPrimaryButtonClick = { component.saveBitmap(null) },
                onPrimaryButtonLongClick = { showFolderSelectionDialog = true },
                isPrimaryButtonEnabled = canExport,
                actions = { if (isPortrait) actions() }
            )
        },
        canShowScreenData = component.uri != null
    )

    ZoomModalSheet(
        data = component.previewBitmap,
        visible = showZoomSheet,
        onDismiss = { showZoomSheet = false }
    )

    OneTimeSaveLocationSelectionDialog(
        visible = showFolderSelectionDialog,
        onDismiss = { showFolderSelectionDialog = false },
        onSaveRequest = component::saveBitmap,
        formatForFilenameSelection = component.params.outputFormat,
        hasOriginalUri = true
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
        visible = component.isSaving || component.isImageLoading,
        onCancelLoading = component::cancelSaving,
        canCancel = component.isSaving
    )
}