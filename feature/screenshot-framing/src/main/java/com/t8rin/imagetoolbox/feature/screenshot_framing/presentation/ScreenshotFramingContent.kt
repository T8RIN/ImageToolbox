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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
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
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageContainer
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageCounter
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.modifier.detectSwipes
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.PickImageFromUrisSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.components.ScreenshotFramingControls
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.screenLogic.ScreenshotFramingComponent

@Composable
fun ScreenshotFramingContent(
    component: ScreenshotFramingComponent
) {
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showPickImageFromUrisSheet by rememberSaveable { mutableStateOf(false) }
    var editSheetData by remember { mutableStateOf(emptyList<Uri>()) }
    val isPortrait by isPortraitOrientationAsState()
    val imagePicker = rememberImagePicker { uris: List<Uri> -> component.setUris(uris) }
    val pickImage = imagePicker::pickImage
    val isLoading = component.previewBitmap == null &&
            (component.isImageLoading || component.isPreviewLoading)

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    AdaptiveLayoutScreen(
        isLoading = isLoading,
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.screenshot_framing),
                input = component.previewBitmap != null,
                isLoading = isLoading,
                size = null
            )
        },
        onGoBack = {
            if (component.haveChanges) showExitDialog = true
            else component.onGoBack()
        },
        actions = {
            ShareButton(
                enabled = component.previewBitmap != null,
                onShare = component::shareBitmaps,
                onCopy = { component.cacheCurrentImage(Clipboard::copy) },
                onEdit = { component.cacheImages { editSheetData = it } }
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
            ImageContainer(
                modifier = Modifier
                    .detectSwipes(
                        onSwipeRight = component::selectLeftUri,
                        onSwipeLeft = component::selectRightUri
                    ),
                imageInside = isPortrait,
                showOriginal = false,
                previewBitmap = component.previewBitmap,
                originalBitmap = null,
                isLoading = isLoading,
                shouldShowPreview = true,
                generatePreviews = true
            )
        },
        controls = {
            ImageCounter(
                imageCount = component.uris.size.takeIf { it > 1 },
                onRepick = { showPickImageFromUrisSheet = true }
            )
            ScreenshotFramingControls(component)
        },
        noDataControls = {
            ImageNotPickedWidget(onPickImage = pickImage)
        },
        buttons = { actions ->
            var showFolderSelectionDialog by rememberSaveable { mutableStateOf(false) }
            var showOneTimeImagePickingDialog by rememberSaveable { mutableStateOf(false) }
            BottomButtonsBlock(
                isNoData = component.uris.isEmpty(),
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = { component.saveBitmaps(null) },
                onPrimaryButtonLongClick = { showFolderSelectionDialog = true },
                isPrimaryButtonEnabled = component.previewBitmap != null,
                actions = { if (isPortrait) actions() },
                onSecondaryButtonLongClick = { showOneTimeImagePickingDialog = true }
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = component::saveBitmaps,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        canShowScreenData = component.uris.isNotEmpty()
    )

    ZoomModalSheet(
        data = component.previewBitmap,
        visible = showZoomSheet,
        onDismiss = { showZoomSheet = false }
    )

    ProcessImagesPreferenceSheet(
        uris = editSheetData,
        visible = editSheetData.isNotEmpty(),
        onDismiss = { editSheetData = emptyList() },
        onNavigate = component.onNavigate
    )

    PickImageFromUrisSheet(
        visible = showPickImageFromUrisSheet,
        onDismiss = { showPickImageFromUrisSheet = false },
        uris = component.uris,
        selectedUri = component.selectedUri,
        onUriPicked = component::updateSelectedUri,
        onUriRemoved = component::updateUrisSilently,
        columns = if (isPortrait) 2 else 4
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