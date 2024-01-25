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

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.CompareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShowOriginalButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageExtraTransformBar
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageTransformBar
import ru.tech.imageresizershrinker.core.ui.widget.controls.PresetWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.QualityWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.ResizeImageField
import ru.tech.imageresizershrinker.core.ui.widget.controls.ScaleModeSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.resize_group.ResizeTypeSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageContainer
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.EditExifSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareSheet
import ru.tech.imageresizershrinker.feature.single_edit.presentation.components.CropEditOption
import ru.tech.imageresizershrinker.feature.single_edit.presentation.components.DrawEditOption
import ru.tech.imageresizershrinker.feature.single_edit.presentation.components.EraseBackgroundEditOption
import ru.tech.imageresizershrinker.feature.single_edit.presentation.components.FilterEditOption
import ru.tech.imageresizershrinker.feature.single_edit.presentation.viewModel.SingleEditViewModel

@Composable
fun SingleEditScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: SingleEditViewModel = hiltViewModel(),
) {
    val settingsState = LocalSettingsState.current
    val toastHostState = LocalToastHost.current
    val context = LocalContext.current as ComponentActivity
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiController.showEmpty()
        }
    }

    LaunchedEffect(uriState) {
        uriState?.let {
            viewModel.setUri(it)
            viewModel.decodeBitmapByUri(
                uri = it,
                onError = {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            )
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

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
                viewModel.setUri(it)
                viewModel.decodeBitmapByUri(
                    uri = it,
                    onError = {
                        scope.launch {
                            toastHostState.showError(context, it)
                        }
                    }
                )
            }
        }

    val pickImage = pickImageLauncher::pickImage

    val saveBitmap: () -> Unit = {
        viewModel.saveBitmap { saveResult ->
            parseSaveResult(
                saveResult = saveResult,
                onSuccess = showConfetti,
                toastHostState = toastHostState,
                scope = scope,
                context = context
            )
        }
    }

    val isPortrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }

    val showCompareSheet = rememberSaveable { mutableStateOf(false) }

    CompareSheet(
        data = viewModel.initialBitmap to viewModel.previewBitmap,
        visible = showCompareSheet
    )

    ZoomModalSheet(
        data = viewModel.previewBitmap,
        visible = showZoomSheet
    )

    val onBack = {
        if (imageInfo.haveChanges(viewModel.bitmap)) showExitDialog = true
        else onGoBack()
    }

    var showCropper by rememberSaveable { mutableStateOf(false) }
    var showFiltering by rememberSaveable { mutableStateOf(false) }
    var showDrawing by rememberSaveable { mutableStateOf(false) }
    var showEraseBackground by rememberSaveable { mutableStateOf(false) }

    AdaptiveLayoutScreen(
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.single_edit),
                input = viewModel.bitmap,
                isLoading = viewModel.isImageLoading,
                size = viewModel.imageInfo.sizeInBytes.toLong()
            )
        },
        onGoBack = onBack,
        topAppBarPersistentActions = {
            if (viewModel.bitmap == null) {
                TopAppBarEmoji()
            }
            CompareButton(
                onClick = { showCompareSheet.value = true },
                visible = viewModel.previewBitmap != null
                        && viewModel.bitmap != null
                        && viewModel.shouldShowPreview
            )
            ZoomButton(
                onClick = { showZoomSheet.value = true },
                visible = viewModel.previewBitmap != null && viewModel.shouldShowPreview
            )
        },
        actions = {
            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
                onClick = {
                    viewModel.shareBitmap(
                        onComplete = showConfetti
                    )
                },
                enabled = viewModel.previewBitmap != null
            ) {
                Icon(Icons.Outlined.Share, null)
            }

            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
                enabled = viewModel.bitmap != null,
                onClick = { showResetDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.RestartAlt,
                    contentDescription = null
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
                ) { Icon(Icons.Rounded.History, null) }
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
            val showEditExifDialog = rememberSaveable { mutableStateOf(false) }
            ImageTransformBar(
                onEditExif = { showEditExifDialog.value = true },
                imageFormat = viewModel.imageInfo.imageFormat,
                onRotateLeft = viewModel::rotateBitmapLeft,
                onFlip = viewModel::flipImage,
                onRotateRight = viewModel::rotateBitmapRight
            )
            Spacer(Modifier.size(8.dp))
            ImageExtraTransformBar(
                onCrop = { showCropper = true },
                onFilter = { showFiltering = true },
                onDraw = { showDrawing = true },
                onEraseBackground = { showEraseBackground = true }
            )
            Spacer(Modifier.size(16.dp))
            PresetWidget(
                selectedPreset = viewModel.presetSelected,
                includeTelegramOption = true,
                onPresetSelected = viewModel::setPreset
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
            QualityWidget(
                imageFormat = imageInfo.imageFormat,
                enabled = viewModel.bitmap != null,
                quality = imageInfo.quality.coerceIn(0f, 100f),
                onQualityChange = viewModel::setQuality
            )
            Spacer(Modifier.height(8.dp))
            ExtensionGroup(
                enabled = viewModel.bitmap != null,
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
                exif = viewModel.exif,
                onClearExif = viewModel::clearExif,
                onUpdateTag = viewModel::updateExifByTag,
                onRemoveTag = viewModel::removeExifTag
            )
        },
        buttons = {
            BottomButtonsBlock(
                targetState = (viewModel.uri == Uri.EMPTY) to isPortrait,
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = saveBitmap,
                actions = {
                    if (isPortrait) it()
                }
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
        onGetBitmap = viewModel::updateBitmapAfterEditing,
        onRequestFiltering = viewModel::filter,
        onRequestMappingFilters = viewModel::mapFilters,
        filterList = viewModel.filterList,
        updateOrder = viewModel::updateOrder,
        updateFilter = viewModel::updateFilter,
        removeAt = viewModel::removeFilterAtIndex,
        addFilter = viewModel::addFilter
    )

    DrawEditOption(
        onRequestFiltering = viewModel::filter,
        visible = showDrawing,
        onDismiss = {
            showDrawing = false
            viewModel.clearDrawing()
        },
        useScaffold = isPortrait,
        bitmap = viewModel.previewBitmap,
        onGetBitmap = {
            viewModel.updateBitmapAfterEditing(it)
            viewModel.clearDrawing()
        },
        undo = viewModel::undoDraw,
        redo = viewModel::redoDraw,
        paths = viewModel.drawPaths,
        lastPaths = viewModel.drawLastPaths,
        undonePaths = viewModel.drawUndonePaths,
        addPath = viewModel::addPathToDrawList
    )

    EraseBackgroundEditOption(
        visible = showEraseBackground,
        onDismiss = {
            showEraseBackground = false
            viewModel.clearErasing()
        },
        useScaffold = isPortrait,
        bitmap = viewModel.previewBitmap,
        onGetBitmap = viewModel::updateBitmapAfterEditing,
        clearErasing = viewModel::clearErasing,
        undo = viewModel::undoErase,
        redo = viewModel::redoErase,
        paths = viewModel.erasePaths,
        lastPaths = viewModel.eraseLastPaths,
        undonePaths = viewModel.eraseUndonePaths,
        addPath = viewModel::addPathToEraseList,
        autoBackgroundRemover = viewModel.getBackgroundRemover()
    )
}