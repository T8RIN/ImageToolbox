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

package com.t8rin.imagetoolbox.feature.resize_convert.presentation

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageTransformBar
import com.t8rin.imagetoolbox.core.ui.widget.controls.ResizeImageField
import com.t8rin.imagetoolbox.core.ui.widget.controls.SaveExifWidget
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
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageCounter
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.modifier.detectSwipes
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.EditExifSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.PickImageFromUrisSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.compare.presentation.components.CompareSheet
import com.t8rin.imagetoolbox.feature.resize_convert.presentation.screenLogic.ResizeAndConvertComponent

@Composable
fun ResizeAndConvertContent(
    component: ResizeAndConvertComponent
) {
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    AutoContentBasedColors(component.bitmap)

    val imagePicker = rememberImagePicker { uris: List<Uri> ->
        component.updateUris(
            uris = uris,
            onFailure = essentials::showFailureToast
        )
    }

    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmaps(
            oneTimeSaveLocationUri = it,
            onComplete = essentials::parseSaveResults
        )
    }

    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }

    var showPickImageFromUrisSheet by rememberSaveable { mutableStateOf(false) }

    var showEditExifDialog by rememberSaveable { mutableStateOf(false) }

    val isPortrait by isPortraitOrientationAsState()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else component.onGoBack()
    }

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showCompareSheet by rememberSaveable { mutableStateOf(false) }

    CompareSheet(
        data = component.bitmap to component.previewBitmap,
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

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.resize_and_convert),
                input = component.bitmap,
                isLoading = component.isImageLoading,
                size = component.imageInfo.sizeInBytes.toLong(),
                originalSize = component.selectedUri?.fileSize()
            )
        },
        onGoBack = onBack,
        actions = {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            ShareButton(
                enabled = component.bitmap != null,
                onShare = {
                    component.performSharing(showConfetti)
                },
                onCopy = {
                    component.cacheCurrentImage(essentials::copyToClipboard)
                },
                onEdit = {
                    component.cacheImages {
                        editSheetData = it
                    }
                }
            )
            ProcessImagesPreferenceSheet(
                uris = editSheetData,
                visible = editSheetData.isNotEmpty(),
                onDismiss = {
                    editSheetData = emptyList()
                },
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
                modifier = Modifier
                    .detectSwipes(
                        onSwipeRight = component::selectLeftUri,
                        onSwipeLeft = component::selectRightUri
                    ),
                imageInside = isPortrait,
                showOriginal = showOriginal,
                previewBitmap = component.previewBitmap,
                originalBitmap = component.bitmap,
                isLoading = component.isImageLoading,
                shouldShowPreview = component.shouldShowPreview
            )
        },
        controls = {
            val imageInfo = component.imageInfo
            ImageCounter(
                imageCount = component.uris?.size?.takeIf { it > 1 },
                onRepick = {
                    showPickImageFromUrisSheet = true
                }
            )
            AnimatedContent(
                targetState = component.uris?.size == 1
            ) { oneUri ->
                val preset = component.presetSelected
                if (oneUri) {
                    ImageTransformBar(
                        onEditExif = { showEditExifDialog = true },
                        onRotateLeft = component::rotateLeft,
                        onFlip = component::flip,
                        imageFormat = component.imageInfo.imageFormat,
                        onRotateRight = component::rotateRight,
                        canRotate = !(preset is Preset.AspectRatio && preset.ratio != 1f)
                    )
                } else {
                    LaunchedEffect(Unit) {
                        showEditExifDialog = false
                        component.updateExif(null)
                    }
                    ImageTransformBar(
                        onRotateLeft = component::rotateLeft,
                        onFlip = component::flip,
                        onRotateRight = component::rotateRight,
                        canRotate = !(preset is Preset.AspectRatio && preset.ratio != 1f)
                    )
                }
            }
            Spacer(Modifier.size(8.dp))
            PresetSelector(
                value = component.presetSelected,
                includeTelegramOption = true,
                includeAspectRatioOption = true,
                onValueChange = component::updatePreset
            )
            Spacer(Modifier.size(8.dp))
            AnimatedVisibility(
                visible = component.uris?.size != 1,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    SaveExifWidget(
                        imageFormat = component.imageInfo.imageFormat,
                        checked = component.keepExif,
                        onCheckedChange = component::setKeepExif
                    )
                    Spacer(Modifier.size(8.dp))
                }
            }
            ResizeImageField(
                imageInfo = imageInfo,
                originalSize = component.originalSize,
                onHeightChange = component::updateHeight,
                onWidthChange = component::updateWidth,
                showWarning = component.showWarning
            )
            if (imageInfo.imageFormat.canChangeCompressionValue) {
                Spacer(Modifier.height(8.dp))
            }
            QualitySelector(
                imageFormat = imageInfo.imageFormat,
                quality = imageInfo.quality,
                onQualityChange = component::setQuality
            )
            Spacer(Modifier.height(8.dp))
            ImageFormatSelector(
                value = imageInfo.imageFormat,
                onValueChange = component::setImageFormat,
                quality = imageInfo.quality,
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
                    saveBitmaps(null)
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
                onSaveRequest = saveBitmaps,
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
            if (component.bitmap == null) TopAppBarEmoji()
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
        canShowScreenData = component.bitmap != null,
        forceImagePreviewToMax = showOriginal,
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

    val transformations by remember(component.imageInfo, component.presetSelected) {
        derivedStateOf(component::getTransformations)
    }

    PickImageFromUrisSheet(
        transformations = transformations,
        visible = showPickImageFromUrisSheet,
        onDismiss = {
            showPickImageFromUrisSheet = false
        },
        uris = component.uris,
        selectedUri = component.selectedUri,
        onUriPicked = { uri ->
            component.updateSelectedUri(
                uri = uri,
                onFailure = essentials::showFailureToast
            )
        },
        onUriRemoved = { uri ->
            component.updateUrisSilently(removedUri = uri)
        },
        columns = if (isPortrait) 2 else 4,
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