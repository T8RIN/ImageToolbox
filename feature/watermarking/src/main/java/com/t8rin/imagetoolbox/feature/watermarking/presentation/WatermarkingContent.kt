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

package com.t8rin.imagetoolbox.feature.watermarking.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.t8rin.imagetoolbox.core.ui.widget.controls.SaveExifWidget
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
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
import com.t8rin.imagetoolbox.core.ui.widget.sheets.PickImageFromUrisSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.compare.presentation.components.CompareSheet
import com.t8rin.imagetoolbox.feature.watermarking.domain.WatermarkParams
import com.t8rin.imagetoolbox.feature.watermarking.presentation.components.HiddenWatermarkInfo
import com.t8rin.imagetoolbox.feature.watermarking.presentation.components.WatermarkDataSelector
import com.t8rin.imagetoolbox.feature.watermarking.presentation.components.WatermarkParamsSelectionGroup
import com.t8rin.imagetoolbox.feature.watermarking.presentation.components.WatermarkingTypeSelector
import com.t8rin.imagetoolbox.feature.watermarking.presentation.screenLogic.WatermarkingComponent

@Composable
fun WatermarkingContent(
    component: WatermarkingComponent
) {
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    AutoContentBasedColors(
        model = component.selectedUri
    )

    val imagePicker = rememberImagePicker { uris: List<Uri> ->
        component.setUris(
            uris = uris,
            onFailure = essentials::showFailureToast
        )
    }

    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    val isPortrait by isPortraitOrientationAsState()

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }
    var showPickImageFromUrisSheet by rememberSaveable { mutableStateOf(false) }
    var showCompareSheet by rememberSaveable { mutableStateOf(false) }
    var showResetDialog by rememberSaveable { mutableStateOf(false) }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.watermarking),
                input = component.selectedUri.takeIf { it != Uri.EMPTY },
                isLoading = component.isImageLoading,
                size = null
            )
        },
        onGoBack = {
            if (component.haveChanges) showExitDialog = true
            else component.onGoBack()
        },
        topAppBarPersistentActions = {
            if (component.previewBitmap == null) TopAppBarEmoji()
            CompareButton(
                onClick = { showCompareSheet = true },
                visible = component.previewBitmap != null && component.internalBitmap != null
            )
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.previewBitmap != null
            )
        },
        actions = {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            ShareButton(
                enabled = component.previewBitmap != null,
                onShare = {
                    component.shareBitmaps(showConfetti)
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
                enabled = component.internalBitmap != null,
                onClick = { showResetDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ImageReset,
                    contentDescription = stringResource(R.string.reset_image)
                )
            }
            if (component.internalBitmap != null) {
                ShowOriginalButton(
                    onStateChange = {
                        showOriginal = it
                    }
                )
            }
        },
        forceImagePreviewToMax = showOriginal,
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
                originalBitmap = component.internalBitmap,
                isLoading = component.isImageLoading,
                shouldShowPreview = true
            )
        },
        controls = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageCounter(
                    imageCount = component.uris.size.takeIf { it > 1 },
                    onRepick = {
                        showPickImageFromUrisSheet = true
                    }
                )
                HiddenWatermarkInfo(
                    hiddenWatermark = component.currentHiddenWatermark
                )
                WatermarkingTypeSelector(
                    value = component.watermarkParams,
                    onValueChange = component::updateWatermarkParams
                )
                WatermarkDataSelector(
                    value = component.watermarkParams,
                    onValueChange = component::updateWatermarkParams
                )
                WatermarkParamsSelectionGroup(
                    value = component.watermarkParams,
                    onValueChange = component::updateWatermarkParams
                )
                SaveExifWidget(
                    checked = component.keepExif,
                    imageFormat = component.imageFormat,
                    onCheckedChange = component::toggleKeepExif
                )
                QualitySelector(
                    imageFormat = component.imageFormat,
                    quality = component.quality,
                    onQualityChange = component::setQuality
                )
                ImageFormatSelector(
                    value = component.imageFormat,
                    onValueChange = component::setImageFormat,
                    quality = component.quality,
                )
            }
        },
        buttons = {
            val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = { oneTimeSaveLocationUri ->
                component.saveBitmaps(
                    oneTimeSaveLocationUri = oneTimeSaveLocationUri,
                    onResult = essentials::parseSaveResults
                )
            }
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = component.uris.isEmpty(),
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = {
                    saveBitmaps(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) it()
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
        noDataControls = {
            ImageNotPickedWidget(onPickImage = pickImage)
        },
        canShowScreenData = component.uris.isNotEmpty()
    )

    val transformations by remember(component.previewBitmap) {
        derivedStateOf {
            listOf(
                component.getWatermarkTransformation()
            )
        }
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

    ResetDialog(
        visible = showResetDialog,
        onDismiss = { showResetDialog = false },
        title = stringResource(R.string.reset_properties),
        text = stringResource(R.string.reset_properties_sub),
        onReset = {
            component.updateWatermarkParams(WatermarkParams.Default)
        }
    )

    CompareSheet(
        data = component.internalBitmap to component.previewBitmap,
        visible = showCompareSheet,
        onDismiss = {
            showCompareSheet = false
        }
    )

    ZoomModalSheet(
        data = component.selectedUri,
        visible = showZoomSheet,
        transformations = transformations,
        onDismiss = {
            showZoomSheet = false
        }
    )

    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.left,
        onCancelLoading = component::cancelSaving
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}