/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.single_edit.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.data.utils.fileSize
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ImageReset
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.CompareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShowOriginalButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageExtraTransformBar
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageTransformBar
import com.t8rin.imagetoolbox.core.ui.widget.controls.ResizeImageField
import com.t8rin.imagetoolbox.core.ui.widget.controls.resize_group.ResizeTypeSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.PresetSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ScaleModeSelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageContainer
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.EditExifSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.compare.presentation.components.CompareSheet
import com.t8rin.imagetoolbox.feature.single_edit.presentation.components.CropEditOption
import com.t8rin.imagetoolbox.feature.single_edit.presentation.components.DrawEditOption
import com.t8rin.imagetoolbox.feature.single_edit.presentation.components.EraseBackgroundEditOption
import com.t8rin.imagetoolbox.feature.single_edit.presentation.components.FilterEditOption
import com.t8rin.imagetoolbox.feature.single_edit.presentation.components.ToneCurvesEditOption
import com.t8rin.imagetoolbox.feature.single_edit.presentation.screenLogic.SingleEditComponent

@Composable
fun SingleEditContent(
    component: SingleEditComponent,
) {
    val context = LocalContext.current

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    AutoContentBasedColors(component.bitmap)

    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val imageInfo = component.imageInfo

    val imagePicker = rememberImagePicker(onSuccess = component::setUri)
    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = component.initialUri != null
    )

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmap(
            oneTimeSaveLocationUri = it,
            onComplete = essentials::parseSaveResult
        )
    }

    val isPortrait by isPortraitOrientationAsState()

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showCompareSheet by rememberSaveable { mutableStateOf(false) }

    CompareSheet(
        data = component.initialBitmap to component.previewBitmap,
        visible = showCompareSheet,
        onDismiss = {
            showCompareSheet = false
        }
    )

    ZoomModalSheet(
        data = component.previewBitmap,
        visible = showZoomSheet,
        onDismiss = {
            showZoomSheet = false
        }
    )

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else component.onGoBack()
    }

    var showCropper by rememberSaveable { mutableStateOf(false) }
    var showFiltering by rememberSaveable { mutableStateOf(false) }
    var showDrawing by rememberSaveable { mutableStateOf(false) }
    var showEraseBackground by rememberSaveable { mutableStateOf(false) }
    var showApplyCurves by rememberSaveable { mutableStateOf(false) }


    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            val originalSize = component.uri.fileSize(context) ?: 0
            val compressedSize = component.imageInfo.sizeInBytes.toLong()
            TopAppBarTitle(
                title = stringResource(R.string.single_edit),
                input = component.bitmap,
                isLoading = component.isImageLoading,
                size = compressedSize,
                originalSize = originalSize
            )
        },
        onGoBack = onBack,
        topAppBarPersistentActions = {
            if (component.bitmap == null) {
                TopAppBarEmoji()
            }
            CompareButton(
                onClick = { showCompareSheet = true },
                visible = component.previewBitmap != null
                        && component.bitmap != null
                        && component.shouldShowPreview
            )
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.previewBitmap != null && component.shouldShowPreview
            )
        },
        actions = {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            ShareButton(
                enabled = component.bitmap != null,
                onShare = {
                    component.shareBitmap(showConfetti)
                },
                onCopy = {
                    component.cacheCurrentImage(essentials::copyToClipboard)
                },
                onEdit = {
                    component.cacheCurrentImage { uri ->
                        editSheetData = listOf(uri)
                    }
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
            if (component.bitmap != null) {
                ShowOriginalButton(
                    canShow = component.canShow(),
                    onStateChange = {
                        showOriginal = it
                    }
                )
            } else {
                EnhancedIconButton(
                    enabled = false,
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = stringResource(R.string.original)
                    )
                }
            }
        },
        imagePreview = {
            ImageContainer(
                imageInside = isPortrait,
                showOriginal = showOriginal,
                previewBitmap = component.previewBitmap,
                originalBitmap = component.initialBitmap,
                isLoading = component.isImageLoading,
                shouldShowPreview = component.shouldShowPreview
            )
        },
        controls = {
            var showEditExifDialog by rememberSaveable { mutableStateOf(false) }
            val preset = component.presetSelected
            ImageTransformBar(
                onEditExif = { showEditExifDialog = true },
                imageFormat = component.imageInfo.imageFormat,
                onRotateLeft = component::rotateBitmapLeft,
                onFlip = component::flipImage,
                onRotateRight = component::rotateBitmapRight,
                canRotate = !(preset is Preset.AspectRatio && preset.ratio != 1f)
            )
            Spacer(Modifier.size(8.dp))
            ImageExtraTransformBar(
                onCrop = { showCropper = true },
                onFilter = { showFiltering = true },
                onDraw = { showDrawing = true },
                onEraseBackground = { showEraseBackground = true },
                onApplyCurves = { showApplyCurves = true }
            )
            Spacer(Modifier.size(16.dp))
            PresetSelector(
                value = component.presetSelected,
                includeTelegramOption = true,
                includeAspectRatioOption = true,
                onValueChange = component::setPreset
            )
            Spacer(Modifier.size(8.dp))
            ResizeImageField(
                imageInfo = imageInfo,
                originalSize = component.originalSize,
                onHeightChange = component::updateHeight,
                onWidthChange = component::updateWidth,
                showWarning = component.showWarning
            )
            if (imageInfo.imageFormat.canChangeCompressionValue) Spacer(
                Modifier.height(8.dp)
            )
            QualitySelector(
                imageFormat = imageInfo.imageFormat,
                quality = imageInfo.quality,
                onQualityChange = component::setQuality
            )
            Spacer(Modifier.height(8.dp))
            ImageFormatSelector(
                value = imageInfo.imageFormat,
                onValueChange = component::setImageFormat
            )
            Spacer(Modifier.height(8.dp))
            ResizeTypeSelector(
                enabled = component.bitmap != null,
                value = imageInfo.resizeType,
                onValueChange = component::setResizeType
            )
            Spacer(Modifier.height(8.dp))
            ScaleModeSelector(
                value = imageInfo.imageScaleMode,
                onValueChange = component::setImageScaleMode
            )

            EditExifSheet(
                visible = showEditExifDialog,
                onDismiss = {
                    showEditExifDialog = false
                },
                exif = component.exif,
                onClearExif = component::clearExif,
                onUpdateTag = component::updateExifByTag,
                onRemoveTag = component::removeExifTag
            )
        },
        buttons = {
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = component.uri == Uri.EMPTY,
                onSecondaryButtonClick = pickImage,
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                },
                onPrimaryButtonClick = {
                    saveBitmap(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) it()
                }
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmap,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Single,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        canShowScreenData = component.bitmap != null,
        noDataControls = {
            if (!component.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
        },
        forceImagePreviewToMax = showOriginal
    )

    ResetDialog(
        visible = showResetDialog,
        onDismiss = { showResetDialog = false },
        onReset = {
            component.resetValues(true)
        }
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

    CropEditOption(
        visible = showCropper,
        onDismiss = { showCropper = false },
        useScaffold = isPortrait,
        bitmap = component.previewBitmap,
        onGetBitmap = component::updateBitmapAfterEditing,
        cropProperties = component.cropProperties,
        setCropAspectRatio = component::setCropAspectRatio,
        setCropMask = component::setCropMask,
        selectedAspectRatio = component.selectedAspectRatio,
        loadImage = component::loadImage
    )

    FilterEditOption(
        visible = showFiltering,
        onDismiss = {
            showFiltering = false
            component.clearFilterList()
        },
        useScaffold = isPortrait,
        bitmap = component.previewBitmap,
        onGetBitmap = {
            component.updateBitmapAfterEditing(it, true)
        },
        onRequestMappingFilters = component::mapFilters,
        filterList = component.filterList,
        updateOrder = component::updateOrder,
        updateFilter = component::updateFilter,
        removeAt = component::removeFilterAtIndex,
        addFilter = component::addFilter,
        filterTemplateCreationSheetComponent = component.filterTemplateCreationSheetComponent,
        addFilterSheetComponent = component.addFiltersSheetComponent
    )

    DrawEditOption(
        addFiltersSheetComponent = component.addFiltersSheetComponent,
        filterTemplateCreationSheetComponent = component.filterTemplateCreationSheetComponent,
        onRequestFiltering = component::filter,
        visible = showDrawing,
        onDismiss = {
            showDrawing = false
            component.clearDrawing()
        },
        useScaffold = isPortrait,
        bitmap = component.previewBitmap,
        onGetBitmap = {
            component.updateBitmapAfterEditing(it, true)
            component.clearDrawing()
        },
        undo = component::undoDraw,
        redo = component::redoDraw,
        paths = component.drawPaths,
        lastPaths = component.drawLastPaths,
        undonePaths = component.drawUndonePaths,
        addPath = component::addPathToDrawList,
        drawMode = component.drawMode,
        onUpdateDrawMode = component::updateDrawMode,
        drawPathMode = component.drawPathMode,
        onUpdateDrawPathMode = component::updateDrawPathMode,
        drawLineStyle = component.drawLineStyle,
        onUpdateDrawLineStyle = component::updateDrawLineStyle,
        helperGridParams = component.helperGridParams,
        onUpdateHelperGridParams = component::updateHelperGridParams
    )

    EraseBackgroundEditOption(
        visible = showEraseBackground,
        onDismiss = {
            showEraseBackground = false
            component.clearErasing()
        },
        useScaffold = isPortrait,
        bitmap = component.previewBitmap,
        onGetBitmap = { bitmap, saveSize ->
            component.updateBitmapAfterEditing(
                bitmap = bitmap,
                saveOriginalSize = saveSize
            )
        },
        clearErasing = component::clearErasing,
        undo = component::undoErase,
        redo = component::redoErase,
        paths = component.erasePaths,
        lastPaths = component.eraseLastPaths,
        undonePaths = component.eraseUndonePaths,
        addPath = component::addPathToEraseList,
        drawPathMode = component.drawPathMode,
        onUpdateDrawPathMode = component::updateDrawPathMode,
        autoBackgroundRemover = component.getBackgroundRemover(),
        helperGridParams = component.helperGridParams,
        onUpdateHelperGridParams = component::updateHelperGridParams
    )

    ToneCurvesEditOption(
        visible = showApplyCurves,
        onDismiss = { showApplyCurves = false },
        useScaffold = isPortrait,
        bitmap = component.previewBitmap,
        editorState = component.imageCurvesEditorState,
        onResetState = component::resetImageCurvesEditorState,
        onGetBitmap = {
            component.updateBitmapAfterEditing(it, true)
        }
    )
}