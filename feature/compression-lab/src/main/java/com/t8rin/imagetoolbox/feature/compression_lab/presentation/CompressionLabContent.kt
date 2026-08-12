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

import android.net.Uri
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Labs
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
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
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageCounter
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.detectSwipes
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.PickImageFromUrisSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
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
        beforeContent = {
            CompressionLabPicture(model = component.benchmarkUri)
        },
        afterContent = {
            CompressionLabPicture(model = component.selectedResult?.uri)
        },
        visible = showCompareSheet,
        onDismiss = { showCompareSheet = false }
    )

    ZoomModalSheet(
        data = component.selectedUri,
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
                originalSize = component.benchmarkUri?.fileSize()
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
            var aspectRatio by remember(component.selectedUri) {
                mutableFloatStateOf(1f)
            }
            Picture(
                model = component.selectedUri,
                modifier = Modifier
                    .detectSwipes(
                        onSwipeRight = component::selectPreviousBenchmark,
                        onSwipeLeft = component::selectNextBenchmark
                    )
                    .container(MaterialTheme.shapes.medium)
                    .aspectRatio(aspectRatio),
                shape = MaterialTheme.shapes.medium,
                contentScale = ContentScale.FillBounds,
                isLoadingFromDifferentPlace = component.isImageLoading,
                onSuccess = {
                    aspectRatio = it.result.image.safeAspectRatio
                }
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
            var editSheetData by remember { mutableStateOf(emptyList<Uri>()) }
            val hasResult = component.selectedResult != null

            BottomButtonsBlock(
                isNoData = component.uris.isEmpty(),
                onSecondaryButtonClick = pickImages,
                onSecondaryButtonLongClick = { showOneTimeImagePickingDialog = true },
                onPrimaryButtonClick = {
                    if (hasResult) component.saveResults(null)
                    else component.runLab()
                },
                onPrimaryButtonLongClick = if (hasResult) {
                    { showFolderSelectionDialog = true }
                } else null,
                primaryButtonIcon = if (hasResult) {
                    Icons.Rounded.Save
                } else {
                    Icons.Rounded.Labs
                },
                isPrimaryButtonVisible = true,
                isPrimaryButtonEnabled = component.sourceBitmap != null &&
                        !component.isImageLoading && !component.isSaving,
                actions = {
                    actions()
                    ShareButton(
                        enabled = hasResult &&
                                !component.isImageLoading && !component.isSaving,
                        onShare = component::shareResults,
                        onEdit = {
                            component.cacheResults { editSheetData = it }
                        }
                    )
                }
            )

            ProcessImagesPreferenceSheet(
                uris = editSheetData,
                visible = editSheetData.isNotEmpty(),
                onDismiss = { editSheetData = emptyList() },
                onNavigate = component.onNavigate
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
        selectedUri = component.benchmarkUri,
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

@Composable
private fun CompressionLabPicture(
    model: Any?,
    modifier: Modifier = Modifier
) {
    var aspectRatio by remember(model) {
        mutableFloatStateOf(1f)
    }
    Picture(
        model = model,
        modifier = modifier.aspectRatio(aspectRatio),
        contentScale = ContentScale.FillBounds,
        onSuccess = {
            aspectRatio = it.result.image.safeAspectRatio
        }
    )
}
