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

package ru.tech.imageresizershrinker.feature.single_edit.presentation

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.data.utils.fileSize
import ru.tech.imageresizershrinker.core.domain.image.model.Preset
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ImageReset
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.CompareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShowOriginalButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageExtraTransformBar
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageTransformBar
import ru.tech.imageresizershrinker.core.ui.widget.controls.ResizeImageField
import ru.tech.imageresizershrinker.core.ui.widget.controls.resize_group.ResizeTypeSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.PresetSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.QualitySelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ScaleModeSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageContainer
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.sheets.EditExifSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareSheet
import ru.tech.imageresizershrinker.feature.single_edit.presentation.components.CropEditOption
import ru.tech.imageresizershrinker.feature.single_edit.presentation.components.DrawEditOption
import ru.tech.imageresizershrinker.feature.single_edit.presentation.components.EraseBackgroundEditOption
import ru.tech.imageresizershrinker.feature.single_edit.presentation.components.FilterEditOption
import ru.tech.imageresizershrinker.feature.single_edit.presentation.components.ToneCurvesEditOption
import ru.tech.imageresizershrinker.feature.single_edit.presentation.viewModel.SingleEditComponent

@Composable
fun SingleEditContent(
    onGoBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    viewModel: SingleEditComponent,
) {
    val settingsState = LocalSettingsState.current
    val toastHostState = LocalToastHostState.current
    val context = LocalContext.current as ComponentActivity
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor) {
                themeState.updateColorByImage(it)
            }
        }
    }

    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val imageInfo = viewModel.imageInfo

    val imagePicker = rememberImagePicker(
        mode = localImagePickerMode(Picker.Single)
    ) { uris ->
        uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
            viewModel.setUri(it)
        }
    }

    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = viewModel.initialUri != null
    )

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        viewModel.saveBitmap(it) { saveResult ->
            context.parseSaveResult(
                saveResult = saveResult,
                onSuccess = showConfetti,
                toastHostState = toastHostState,
                scope = scope
            )
        }
    }

    val isPortrait by isPortraitOrientationAsState()

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showCompareSheet by rememberSaveable { mutableStateOf(false) }

    CompareSheet(
        data = viewModel.initialBitmap to viewModel.previewBitmap,
        visible = showCompareSheet,
        onDismiss = {
            showCompareSheet = false
        }
    )

    ZoomModalSheet(
        data = viewModel.previewBitmap,
        visible = showZoomSheet,
        onDismiss = {
            showZoomSheet = false
        }
    )

    val onBack = {
        if (viewModel.haveChanges) showExitDialog = true
        else onGoBack()
    }

    var showCropper by rememberSaveable { mutableStateOf(false) }
    var showFiltering by rememberSaveable { mutableStateOf(false) }
    var showDrawing by rememberSaveable { mutableStateOf(false) }
    var showEraseBackground by rememberSaveable { mutableStateOf(false) }
    var showApplyCurves by rememberSaveable { mutableStateOf(false) }


    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !viewModel.haveChanges,
        title = {
            val originalSize = viewModel.uri.fileSize(context) ?: 0
            val compressedSize = viewModel.imageInfo.sizeInBytes.toLong()
            TopAppBarTitle(
                title = stringResource(R.string.single_edit),
                input = viewModel.bitmap,
                isLoading = viewModel.isImageLoading,
                size = compressedSize,
                originalSize = originalSize
            )
        },
        onGoBack = onBack,
        topAppBarPersistentActions = {
            if (viewModel.bitmap == null) {
                TopAppBarEmoji()
            }
            CompareButton(
                onClick = { showCompareSheet = true },
                visible = viewModel.previewBitmap != null
                        && viewModel.bitmap != null
                        && viewModel.shouldShowPreview
            )
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = viewModel.previewBitmap != null && viewModel.shouldShowPreview
            )
        },
        actions = {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            ShareButton(
                enabled = viewModel.bitmap != null,
                onShare = {
                    viewModel.shareBitmap(showConfetti)
                },
                onCopy = { manager ->
                    viewModel.cacheCurrentImage { uri ->
                        manager.setClip(uri.asClip(context))
                        showConfetti()
                    }
                },
                onEdit = {
                    viewModel.cacheCurrentImage { uri ->
                        editSheetData = listOf(uri)
                    }
                }
            )
            ProcessImagesPreferenceSheet(
                uris = editSheetData,
                visible = editSheetData.isNotEmpty(),
                onDismiss = {
                    if (!it) {
                        editSheetData = emptyList()
                    }
                },
                onNavigate = { screen ->
                    scope.launch {
                        editSheetData = emptyList()
                        delay(200)
                        onNavigate(screen)
                    }
                }
            )

            EnhancedIconButton(
                enabled = viewModel.bitmap != null,
                onClick = { showResetDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ImageReset,
                    contentDescription = stringResource(R.string.reset_image)
                )
            }
            if (viewModel.bitmap != null) {
                ShowOriginalButton(
                    canShow = viewModel.canShow(),
                    onStateChange = {
                        showOriginal = it
                    }
                )
            } else {
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    contentColor = LocalContentColor.current,
                    enableAutoShadowAndBorder = false,
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
                previewBitmap = viewModel.previewBitmap,
                originalBitmap = viewModel.initialBitmap,
                isLoading = viewModel.isImageLoading,
                shouldShowPreview = viewModel.shouldShowPreview
            )
        },
        controls = {
            var showEditExifDialog by rememberSaveable { mutableStateOf(false) }
            val preset = viewModel.presetSelected
            ImageTransformBar(
                onEditExif = { showEditExifDialog = true },
                imageFormat = viewModel.imageInfo.imageFormat,
                onRotateLeft = viewModel::rotateBitmapLeft,
                onFlip = viewModel::flipImage,
                onRotateRight = viewModel::rotateBitmapRight,
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
                value = viewModel.presetSelected,
                includeTelegramOption = true,
                includeAspectRatioOption = true,
                onValueChange = viewModel::setPreset
            )
            Spacer(Modifier.size(8.dp))
            ResizeImageField(
                imageInfo = imageInfo,
                originalSize = viewModel.originalSize,
                onHeightChange = viewModel::updateHeight,
                onWidthChange = viewModel::updateWidth,
                showWarning = viewModel.showWarning
            )
            if (imageInfo.imageFormat.canChangeCompressionValue) Spacer(
                Modifier.height(8.dp)
            )
            QualitySelector(
                imageFormat = imageInfo.imageFormat,
                enabled = viewModel.bitmap != null,
                quality = imageInfo.quality,
                onQualityChange = viewModel::setQuality
            )
            Spacer(Modifier.height(8.dp))
            ImageFormatSelector(
                value = imageInfo.imageFormat,
                onValueChange = viewModel::setImageFormat
            )
            Spacer(Modifier.height(8.dp))
            ResizeTypeSelector(
                enabled = viewModel.bitmap != null,
                value = imageInfo.resizeType,
                onValueChange = viewModel::setResizeType
            )
            Spacer(Modifier.height(8.dp))
            ScaleModeSelector(
                value = imageInfo.imageScaleMode,
                onValueChange = viewModel::setImageScaleMode
            )

            EditExifSheet(
                visible = showEditExifDialog,
                onDismiss = {
                    showEditExifDialog = false
                },
                exif = viewModel.exif,
                onClearExif = viewModel::clearExif,
                onUpdateTag = viewModel::updateExifByTag,
                onRemoveTag = viewModel::removeExifTag
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
                targetState = (viewModel.uri == Uri.EMPTY) to isPortrait,
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
            if (showFolderSelectionDialog) {
                OneTimeSaveLocationSelectionDialog(
                    onDismiss = { showFolderSelectionDialog = false },
                    onSaveRequest = saveBitmap,
                    formatForFilenameSelection = viewModel.getFormatForFilenameSelection()
                )
            }
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Single,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        canShowScreenData = viewModel.bitmap != null,
        noDataControls = {
            if (!viewModel.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
        },
        forceImagePreviewToMax = showOriginal,
        isPortrait = isPortrait
    )

    ResetDialog(
        visible = showResetDialog,
        onDismiss = { showResetDialog = false },
        onReset = {
            viewModel.resetValues(true)
        }
    )

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    if (viewModel.isSaving) {
        LoadingDialog(onCancelLoading = viewModel::cancelSaving)
    }

    CropEditOption(
        visible = showCropper,
        onDismiss = { showCropper = false },
        useScaffold = isPortrait,
        bitmap = viewModel.previewBitmap,
        onGetBitmap = viewModel::updateBitmapAfterEditing,
        cropProperties = viewModel.cropProperties,
        setCropAspectRatio = viewModel::setCropAspectRatio,
        setCropMask = viewModel::setCropMask,
        selectedAspectRatio = viewModel.selectedAspectRatio,
        loadImage = viewModel::loadImage
    )

    FilterEditOption(
        visible = showFiltering,
        onDismiss = {
            showFiltering = false
            viewModel.clearFilterList()
        },
        useScaffold = isPortrait,
        bitmap = viewModel.previewBitmap,
        onGetBitmap = {
            viewModel.updateBitmapAfterEditing(it, true)
        },
        onRequestMappingFilters = viewModel::mapFilters,
        filterList = viewModel.filterList,
        updateOrder = viewModel::updateOrder,
        updateFilter = viewModel::updateFilter,
        removeAt = viewModel::removeFilterAtIndex,
        addFilter = viewModel::addFilter,
        filterTemplateCreationSheetViewModel = viewModel.filterTemplateCreationSheetViewModel,
        addFilterSheetViewModel = viewModel.addFiltersSheetViewModel
    )

    DrawEditOption(
        addFiltersSheetViewModel = viewModel.addFiltersSheetViewModel,
        filterTemplateCreationSheetViewModel = viewModel.filterTemplateCreationSheetViewModel,
        onRequestFiltering = viewModel::filter,
        visible = showDrawing,
        onDismiss = {
            showDrawing = false
            viewModel.clearDrawing()
        },
        useScaffold = isPortrait,
        bitmap = viewModel.previewBitmap,
        onGetBitmap = {
            viewModel.updateBitmapAfterEditing(it, true)
            viewModel.clearDrawing()
        },
        undo = viewModel::undoDraw,
        redo = viewModel::redoDraw,
        paths = viewModel.drawPaths,
        lastPaths = viewModel.drawLastPaths,
        undonePaths = viewModel.drawUndonePaths,
        addPath = viewModel::addPathToDrawList,
        drawMode = viewModel.drawMode,
        onUpdateDrawMode = viewModel::updateDrawMode,
        drawPathMode = viewModel.drawPathMode,
        onUpdateDrawPathMode = viewModel::updateDrawPathMode,
        drawLineStyle = viewModel.drawLineStyle,
        onUpdateDrawLineStyle = viewModel::updateDrawLineStyle,
        helperGridParams = viewModel.helperGridParams,
        onUpdateHelperGridParams = viewModel::updateHelperGridParams
    )

    EraseBackgroundEditOption(
        visible = showEraseBackground,
        onDismiss = {
            showEraseBackground = false
            viewModel.clearErasing()
        },
        useScaffold = isPortrait,
        bitmap = viewModel.previewBitmap,
        onGetBitmap = {
            viewModel.updateBitmapAfterEditing(it, true)
        },
        clearErasing = viewModel::clearErasing,
        undo = viewModel::undoErase,
        redo = viewModel::redoErase,
        paths = viewModel.erasePaths,
        lastPaths = viewModel.eraseLastPaths,
        undonePaths = viewModel.eraseUndonePaths,
        addPath = viewModel::addPathToEraseList,
        drawPathMode = viewModel.drawPathMode,
        onUpdateDrawPathMode = viewModel::updateDrawPathMode,
        autoBackgroundRemover = viewModel.getBackgroundRemover(),
        helperGridParams = viewModel.helperGridParams,
        onUpdateHelperGridParams = viewModel::updateHelperGridParams
    )

    ToneCurvesEditOption(
        visible = showApplyCurves,
        onDismiss = { showApplyCurves = false },
        useScaffold = isPortrait,
        bitmap = viewModel.previewBitmap,
        editorState = viewModel.imageCurvesEditorState,
        onResetState = viewModel::resetImageCurvesEditorState,
        onGetBitmap = {
            viewModel.updateBitmapAfterEditing(it, true)
        }
    )
}