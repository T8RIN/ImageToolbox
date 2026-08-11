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

package com.t8rin.imagetoolbox.feature.multi_frame_fusion.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.CompareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageReorderCarousel
import com.t8rin.imagetoolbox.core.ui.widget.controls.UndoRedoButtons
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageContainer
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.compare.presentation.components.CompareSheet
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain.FusionParams
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.presentation.components.FusionParamsSelector
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.presentation.screenLogic.MultiFrameFusionComponent

@Composable
fun MultiFrameFusionContent(component: MultiFrameFusionComponent) {
    AutoContentBasedColors(component.previewBitmap)

    val imagePicker = rememberImagePicker { uris: List<Uri> ->
        if (uris.size < FusionParams.MIN_IMAGES) {
            AppToastHost.showFailureToast(R.string.pick_at_least_two_images)
        } else {
            component.setUris(uris)
        }
    }
    val addImagePicker = rememberImagePicker(onSuccess = component::addUris)
    val pickImages = imagePicker::pickImage
    val addImages = addImagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImages,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showSaveFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showCompareSheet by rememberSaveable { mutableStateOf(false) }
    var editSheetData by remember { mutableStateOf(listOf<Uri>()) }
    val isPortrait by isPortraitOrientationAsState()

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else component.onGoBack()
    }
    val imageLimitMessage = stringResource(
        R.string.fusion_image_limit,
        FusionParams.MAX_IMAGES
    )
    val addMoreImages = {
        if (component.uris.size < FusionParams.MAX_IMAGES) addImages()
        else AppToastHost.showToast(imageLimitMessage)
    }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.multi_frame_fusion),
                input = component.uris,
                isLoading = component.isImageLoading,
                size = null
            )
        },
        onGoBack = onBack,
        topAppBarPersistentActions = {
            if (component.uris.isEmpty()) TopAppBarEmoji()
            CompareButton(
                onClick = { showCompareSheet = true },
                visible = component.previewBitmap != null
            )
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.previewBitmap != null
            )
        },
        actions = {
            if (!isPortrait) {
                UndoRedoButtons(
                    canUndo = component.canUndo,
                    canRedo = component.canRedo,
                    onUndo = component::undo,
                    onRedo = component::redo,
                    modifier = Modifier.padding(2.dp)
                )
            }
            ShareButton(
                enabled = component.previewBitmap != null,
                onShare = component::share,
                onCopy = { component.cache(Clipboard::copy) },
                onEdit = { component.cache { editSheetData = listOf(it) } }
            )
            ProcessImagesPreferenceSheet(
                uris = editSheetData,
                visible = editSheetData.isNotEmpty(),
                onDismiss = { editSheetData = emptyList() },
                onNavigate = component.onNavigate
            )
            if (isPortrait) {
                UndoRedoButtons(
                    canUndo = component.canUndo,
                    canRedo = component.canRedo,
                    onUndo = component::undo,
                    onRedo = component::redo,
                    modifier = Modifier.padding(2.dp)
                )
            }
        },
        imagePreview = {
            ImageContainer(
                imageInside = isPortrait,
                showOriginal = false,
                previewBitmap = component.previewBitmap,
                originalBitmap = null,
                isLoading = component.isImageLoading,
                shouldShowPreview = true
            )
        },
        controls = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageReorderCarousel(
                    images = component.uris,
                    onReorder = component::reorderUris,
                    onNeedToAddImage = addMoreImages,
                    onNeedToRemoveImageAt = component::removeImageAt,
                    onNavigate = component.onNavigate,
                    title = stringResource(R.string.source_frames)
                )
                FusionParamsSelector(
                    value = component.params,
                    onValueChange = component::updateParams
                )
                QualitySelector(
                    imageFormat = component.imageFormat,
                    quality = component.quality,
                    onQualityChange = component::setQuality
                )
                ImageFormatSelector(
                    value = component.imageFormat,
                    quality = component.quality,
                    onValueChange = component::setImageFormat,
                    forceEnabled = true,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        },
        buttons = { actions ->
            var showOneTimeImagePickingDialog by rememberSaveable { mutableStateOf(false) }
            BottomButtonsBlock(
                isNoData = component.uris.isEmpty(),
                onSecondaryButtonClick = pickImages,
                onSecondaryButtonLongClick = { showOneTimeImagePickingDialog = true },
                secondaryButtonText = stringResource(R.string.pick_images),
                onPrimaryButtonClick = { component.save(null) },
                onPrimaryButtonLongClick = { showSaveFolderDialog = true },
                isPrimaryButtonEnabled = component.previewBitmap != null,
                actions = { if (isPortrait) actions() }
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showSaveFolderDialog,
                onDismiss = { showSaveFolderDialog = false },
                onSaveRequest = component::save,
                formatForFilenameSelection = component.getFormatForFilenameSelection(),
                hasOriginalUri = false
            )
            OneTimeImagePickingDialog(
                visible = showOneTimeImagePickingDialog,
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker
            )
        },
        noDataControls = {
            ImageNotPickedWidget(
                onPickImage = pickImages,
                text = stringResource(R.string.pick_at_least_two_images)
            )
        },
        canShowScreenData = component.uris.isNotEmpty()
    )

    CompareSheet(
        beforeContent = {
            Picture(
                model = component.uris.firstOrNull(),
                modifier = Modifier.aspectRatio(component.previewBitmap?.safeAspectRatio ?: 1f)
            )
        },
        afterContent = {
            Picture(
                model = component.previewBitmap,
                modifier = Modifier.aspectRatio(component.previewBitmap?.safeAspectRatio ?: 1f)
            )
        },
        visible = showCompareSheet,
        onDismiss = { showCompareSheet = false }
    )

    ZoomModalSheet(
        data = component.previewBitmap,
        visible = showZoomSheet,
        onDismiss = { showZoomSheet = false }
    )

    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.total,
        onCancelLoading = component::cancelSaving
    )
    ExitWithoutSavingDialog(
        visible = showExitDialog,
        onDismiss = { showExitDialog = false },
        onExit = component.onGoBack
    )
}
