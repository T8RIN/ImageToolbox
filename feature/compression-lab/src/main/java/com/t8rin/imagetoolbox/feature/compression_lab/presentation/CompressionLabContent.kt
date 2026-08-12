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

package com.t8rin.imagetoolbox.feature.compression_lab.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.CompareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
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
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.core.utils.fileSize
import com.t8rin.imagetoolbox.feature.compare.presentation.components.CompareSheet
import com.t8rin.imagetoolbox.feature.compression_lab.presentation.components.CompressionLabControls
import com.t8rin.imagetoolbox.feature.compression_lab.presentation.screenLogic.CompressionLabComponent

@Composable
fun CompressionLabContent(
    component: CompressionLabComponent
) {
    AutoContentBasedColors(component.sourceBitmap)

    val imagePicker = rememberImagePicker(
        picker = Picker.Multiple,
        onSuccess = component::setUris
    )
    val pickImages = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImages,
        isPickedAlready = component.initialUri != null
    )

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showCompareSheet by rememberSaveable { mutableStateOf(false) }
    var showReferenceImageSheet by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else component.onGoBack()
    }

    CompareSheet(
        data = component.sourceBitmap to component.resultBitmap,
        visible = showCompareSheet,
        onDismiss = { showCompareSheet = false }
    )

    ZoomModalSheet(
        data = component.resultBitmap ?: component.sourceBitmap,
        visible = showZoomSheet,
        onDismiss = { showZoomSheet = false }
    )

    val isPortrait by isPortraitOrientationAsState()

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.compression_lab),
                input = component.sourceBitmap,
                isLoading = component.isImageLoading,
                size = component.selectedResult?.sizeBytes,
                originalSize = component.selectedUri?.fileSize()
            )
        },
        onGoBack = onBack,
        actions = {},
        topAppBarPersistentActions = {
            if (component.sourceBitmap == null) TopAppBarEmoji()
            CompareButton(
                onClick = { showCompareSheet = true },
                visible = component.selectedResult != null
            )
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.sourceBitmap != null
            )
        },
        imagePreview = {
            ImageContainer(
                modifier = Modifier.detectSwipes(
                    onSwipeRight = component::selectPreviousBenchmark,
                    onSwipeLeft = component::selectNextBenchmark
                ),
                imageInside = isPortrait,
                showOriginal = false,
                previewBitmap = component.resultBitmap ?: component.sourceBitmap,
                originalBitmap = component.sourceBitmap,
                isLoading = component.isImageLoading,
                shouldShowPreview = true
            )
        },
        controls = {
            ImageCounter(
                imageCount = component.uris.size.takeIf { it > 1 },
                onRepick = { showReferenceImageSheet = true }
            )
            CompressionLabControls(component = component)
        },
        buttons = { actions ->
            var showFolderSelectionDialog by rememberSaveable { mutableStateOf(false) }
            var showOneTimeImagePickingDialog by rememberSaveable { mutableStateOf(false) }

            BottomButtonsBlock(
                isNoData = component.uris.isEmpty(),
                onSecondaryButtonClick = pickImages,
                onSecondaryButtonLongClick = { showOneTimeImagePickingDialog = true },
                onPrimaryButtonClick = { component.saveResults(null) },
                onPrimaryButtonLongClick = { showFolderSelectionDialog = true },
                isPrimaryButtonVisible = component.selectedResult != null,
                isPrimaryButtonEnabled = component.selectedResult != null &&
                        !component.isImageLoading && !component.isSaving,
                actions = {
                    actions()
                    ShareButton(
                        enabled = component.selectedResult != null &&
                                !component.isImageLoading && !component.isSaving,
                        onShare = component::shareResults
                    )
                }
            )

            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = component::saveResults,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        canShowScreenData = component.sourceBitmap != null,
        noDataControls = {
            if (!component.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImages)
            }
        }
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    PickImageFromUrisSheet(
        visible = showReferenceImageSheet,
        onDismiss = { showReferenceImageSheet = false },
        uris = component.uris,
        selectedUri = component.selectedUri,
        onUriPicked = component::selectBenchmark,
        onUriRemoved = component::removeUri,
        columns = if (isPortrait) 2 else 4
    )

    LoadingDialog(
        visible = component.isImageLoading && component.sourceBitmap != null,
        done = component.done,
        left = component.selectedFormats.size,
        onCancelLoading = component::cancelRunning
    )
    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.uris.size,
        onCancelLoading = component::cancelSaving
    )
}
