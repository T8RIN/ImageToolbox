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

package com.t8rin.imagetoolbox.feature.curves.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.curves.ImageCurvesEditor
import com.t8rin.curves.ImageCurvesEditorLayout
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ImageReset
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.CompareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.SaveExifWidget
import com.t8rin.imagetoolbox.core.ui.widget.controls.UndoRedoButtons
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedHorizontalScroll
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageCounter
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.detectSwipes
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.PickImageFromUrisSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.compare.presentation.components.CompareSheet
import com.t8rin.imagetoolbox.feature.curves.presentation.components.CurvesLivePreview
import com.t8rin.imagetoolbox.feature.curves.presentation.components.CurvesPresetImportExport
import com.t8rin.imagetoolbox.feature.curves.presentation.screenLogic.CurvesComponent
import com.t8rin.imagetoolbox.feature.settings.presentation.components.RawDevelopSettingsCard

@Composable
fun CurvesContent(component: CurvesComponent) {
    AutoContentBasedColors(component.bitmap)

    val imagePicker = rememberImagePicker { uris: List<Uri> ->
        component.setUris(uris)
    }
    val pickImage = imagePicker::pickImage

    val presetPicker = rememberFilePicker(
        mimeType = MimeType.All,
        onSuccess = component::importCurvesPreset
    )

    val presetCreator = rememberFileCreator(
        onSuccess = { component.exportCurvesPreset(it) }
    )

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    var showPickImageFromUrisSheet by rememberSaveable { mutableStateOf(false) }
    var showCompareSheet by rememberSaveable { mutableStateOf(false) }
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    val isPortrait by isPortraitOrientationAsState()

    val onBack = {
        if (component.haveChanges) {
            showExitDialog = true
        } else {
            component.onGoBack()
        }
    }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.tone_curves),
                input = Unit,
                isLoading = false,
                size = null,
            )
        },
        onGoBack = onBack,
        actions = {
            val state = rememberScrollState()
            Row(
                modifier = Modifier
                    .fadingEdges(state)
                    .enhancedHorizontalScroll(state),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isPortrait) {
                    UndoRedoButtons(
                        canUndo = component.canUndo,
                        canRedo = component.canRedo,
                        onUndo = component::undo,
                        onRedo = component::redo,
                        modifier = Modifier.padding(2.dp)
                    )
                }
                var editSheetData by remember { mutableStateOf(listOf<Uri>()) }
                ShareButton(
                    enabled = component.bitmap != null,
                    onShare = component::shareBitmaps,
                    onCopy = {
                        component.cacheCurrentImage(Clipboard::copy)
                    },
                    onEdit = {
                        component.cacheImages { editSheetData = it }
                    }
                )
                ProcessImagesPreferenceSheet(
                    uris = editSheetData,
                    visible = editSheetData.isNotEmpty(),
                    onDismiss = { editSheetData = emptyList() },
                    onNavigate = component.onNavigate
                )
                EnhancedIconButton(
                    enabled = component.bitmap != null,
                    onClick = { showResetDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ImageReset,
                        contentDescription = stringResource(R.string.reset_image)
                    )
                }
                if (isPortrait) {
                    UndoRedoButtons(
                        canUndo = component.canUndo,
                        canRedo = component.canRedo,
                        onUndo = component::undo,
                        onRedo = component::redo,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        },
        imagePreview = {
            CurvesLivePreview(
                modifier = Modifier.detectSwipes(
                    onSwipeRight = component::selectLeftUri,
                    onSwipeLeft = component::selectRightUri
                ),
                bitmap = component.bitmap,
                state = component.curvesState,
                imageInside = isPortrait,
                isLoading = component.isImageLoading
            )
        },
        controls = {
            ImageCounter(
                imageCount = component.uris?.size?.takeIf { it > 1 },
                onRepick = { showPickImageFromUrisSheet = true }
            )
            RawDevelopSettingsCard(
                uri = component.selectedUri,
                onSettingsChanged = component::calculatePreview
            )
            Box(
                modifier = Modifier.container(resultPadding = 0.dp)
            ) {
                ImageCurvesEditor(
                    bitmap = component.bitmap,
                    state = component.curvesState,
                    onStateChange = component::setCurvesState,
                    imageObtainingTrigger = false,
                    onImageObtained = {},
                    modifier = Modifier.fillMaxWidth(),
                    containerModifier = Modifier.fillMaxWidth(),
                    layout = ImageCurvesEditorLayout.Separate,
                    showImagePreview = false,
                    showAsRow = false
                )
            }
            Spacer(Modifier.height(16.dp))
            CurvesPresetImportExport(
                onImport = presetPicker::pickFile,
                onExport = {
                    presetCreator.make(component.createTargetFilename())
                }
            )
            Spacer(Modifier.height(16.dp))
            SaveExifWidget(
                imageFormat = component.imageInfo.imageFormat,
                checked = component.keepExif,
                onCheckedChange = component::setKeepExif
            )
            Spacer(Modifier.height(8.dp))
            ImageFormatSelector(
                value = component.imageInfo.imageFormat,
                onValueChange = component::setImageFormat,
                quality = component.imageInfo.quality
            )
        },
        buttons = { actions ->
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = component.uris.isNullOrEmpty(),
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = {
                    component.saveBitmaps(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) actions()
                },
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                }
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
        topAppBarPersistentActions = {
            CompareButton(
                visible = component.selectedUri != null,
                onClick = { showCompareSheet = true }
            )
            ZoomButton(
                visible = component.selectedUri != null,
                onClick = { showZoomSheet = true }
            )
            if (component.bitmap == null) TopAppBarEmoji()
        },
        canShowScreenData = component.bitmap != null,
        forceImagePreviewToMax = false,
        noDataControls = {
            if (!component.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
        }
    )

    ResetDialog(
        visible = showResetDialog,
        onDismiss = { showResetDialog = false },
        onReset = component::resetValues
    )

    val transformations by remember(
        component.imageInfo,
        component.curvesState
    ) {
        derivedStateOf(component::getConversionTransformation)
    }

    CompareSheet(
        beforeContent = {
            var aspectRatio by remember(component.selectedUri) {
                mutableFloatStateOf(1f)
            }
            Picture(
                model = component.selectedUri,
                modifier = Modifier.aspectRatio(aspectRatio),
                onSuccess = {
                    aspectRatio = it.result.image.safeAspectRatio
                }
            )
        },
        afterContent = {
            var aspectRatio by remember(component.selectedUri) {
                mutableFloatStateOf(1f)
            }
            Picture(
                model = component.selectedUri,
                transformations = transformations,
                modifier = Modifier.aspectRatio(aspectRatio),
                onSuccess = {
                    aspectRatio = it.result.image.safeAspectRatio
                }
            )
        },
        visible = showCompareSheet,
        onDismiss = { showCompareSheet = false }
    )

    ZoomModalSheet(
        data = component.selectedUri,
        transformations = transformations,
        visible = showZoomSheet,
        onDismiss = { showZoomSheet = false }
    )

    PickImageFromUrisSheet(
        transformations = transformations,
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
        left = component.uris?.size ?: 1,
        onCancelLoading = component::cancelSaving
    )
}