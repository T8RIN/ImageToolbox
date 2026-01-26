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

package com.t8rin.imagetoolbox.feature.limits_resize.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberFileSize
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.ResizeImageField
import com.t8rin.imagetoolbox.core.ui.widget.controls.SaveExifWidget
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ScaleModeSelector
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
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.limits_resize.presentation.components.AutoRotateLimitBoxToggle
import com.t8rin.imagetoolbox.feature.limits_resize.presentation.components.LimitsResizeSelector
import com.t8rin.imagetoolbox.feature.limits_resize.presentation.screenLogic.LimitsResizeComponent

@Composable
fun LimitsResizeContent(
    component: LimitsResizeComponent
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

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else component.onGoBack()
    }

    val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmaps(
            oneTimeSaveLocationUri = it,
            onResult = essentials::parseSaveResults
        )
    }

    var showPickImageFromUrisSheet by rememberSaveable { mutableStateOf(false) }

    val isPortrait by isPortraitOrientationAsState()


    var showZoomSheet by rememberSaveable { mutableStateOf(false) }

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
                title = stringResource(R.string.limits_resize),
                input = component.bitmap,
                isLoading = component.isImageLoading,
                size = rememberFileSize(component.selectedUri)
            )
        },
        onGoBack = onBack,
        actions = {
            if (component.previewBitmap != null) {
                var editSheetData by remember {
                    mutableStateOf(listOf<Uri>())
                }
                ShareButton(
                    enabled = component.canSave,
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
            }
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.bitmap != null,
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
                originalBitmap = component.bitmap,
                isLoading = component.isImageLoading,
                shouldShowPreview = true
            )
        },
        controls = {
            ImageCounter(
                imageCount = component.uris?.size?.takeIf { it > 1 },
                onRepick = {
                    showPickImageFromUrisSheet = true
                }
            )
            ResizeImageField(
                imageInfo = component.imageInfo,
                originalSize = component.originalSize,
                onWidthChange = component::updateWidth,
                onHeightChange = component::updateHeight
            )
            Spacer(Modifier.size(8.dp))
            SaveExifWidget(
                imageFormat = component.imageInfo.imageFormat,
                checked = component.keepExif,
                onCheckedChange = component::setKeepExif
            )
            if (component.imageInfo.imageFormat.canChangeCompressionValue) Spacer(
                Modifier.size(8.dp)
            )
            QualitySelector(
                imageFormat = component.imageInfo.imageFormat,
                quality = component.imageInfo.quality,
                onQualityChange = component::setQuality
            )
            Spacer(Modifier.size(8.dp))
            ImageFormatSelector(
                value = component.imageInfo.imageFormat,
                onValueChange = component::setImageFormat,
                quality = component.imageInfo.quality
            )
            Spacer(Modifier.size(8.dp))
            AutoRotateLimitBoxToggle(
                value = component.resizeType.autoRotateLimitBox,
                onClick = component::toggleAutoRotateLimitBox
            )
            Spacer(Modifier.size(8.dp))
            LimitsResizeSelector(
                enabled = component.bitmap != null,
                value = component.resizeType,
                onValueChange = component::setResizeType
            )
            Spacer(Modifier.height(8.dp))
            ScaleModeSelector(
                value = component.imageInfo.imageScaleMode,
                onValueChange = component::setImageScaleMode
            )
        },
        noDataControls = {
            if (!component.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
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
                isPrimaryButtonVisible = component.canSave,
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
            if (component.bitmap == null) {
                TopAppBarEmoji()
            }
        },
        canShowScreenData = component.bitmap != null
    )

    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.uris?.size ?: 1,
        onCancelLoading = component::cancelSaving
    )

    PickImageFromUrisSheet(
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

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}