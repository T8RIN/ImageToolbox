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

package com.t8rin.imagetoolbox.feature.crop.presentation


import android.net.Uri
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.model.OutlineType
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormatGroup
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.icons.CropSmall
import com.t8rin.imagetoolbox.core.resources.icons.ImageReset
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveBottomScaffoldLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.MagnifierEnabledSelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.image.AspectRatioSelector
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.crop.presentation.components.CoercePointsToImageBoundsToggle
import com.t8rin.imagetoolbox.feature.crop.presentation.components.CropMaskSelection
import com.t8rin.imagetoolbox.feature.crop.presentation.components.CropRotationSelector
import com.t8rin.imagetoolbox.feature.crop.presentation.components.CropType
import com.t8rin.imagetoolbox.feature.crop.presentation.components.Cropper
import com.t8rin.imagetoolbox.feature.crop.presentation.components.FreeCornersCropToggle
import com.t8rin.imagetoolbox.feature.crop.presentation.screenLogic.CropComponent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CropContent(
    component: CropComponent
) {
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.bitmap != null) showExitDialog = true
        else component.onGoBack()
    }

    AutoContentBasedColors(component.bitmap)

    var coercePointsToImageArea by rememberSaveable {
        mutableStateOf(true)
    }

    val rotationState = rememberSaveable {
        mutableFloatStateOf(0f)
    }

    val imagePicker = rememberImagePicker { uri: Uri ->
        rotationState.floatValue = 0f
        component.setUri(
            uri = uri,
            onFailure = essentials::showFailureToast
        )
    }

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

    var showResetDialog by rememberSaveable { mutableStateOf(false) }

    var crop by remember { mutableStateOf(false) }

    val actions = @Composable {
        var editSheetData by remember {
            mutableStateOf(listOf<Uri>())
        }
        ShareButton(
            enabled = component.bitmap != null,
            onShare = {
                component.shareBitmap(showConfetti)
            },
            onEdit = {
                component.cacheCurrentImage { uri ->
                    editSheetData = listOf(uri)
                }
            },
            onCopy = {
                component.cacheCurrentImage(essentials::copyToClipboard)
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

    AdaptiveBottomScaffoldLayoutScreen(
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.crop),
                input = component.bitmap,
                isLoading = component.isImageLoading,
                size = null,
                originalSize = null
            )
        },
        onGoBack = onBack,
        shouldDisableBackHandler = component.bitmap == null,
        actions = {
            actions()
        },
        topAppBarPersistentActions = { scaffoldState ->
            if (component.bitmap == null) TopAppBarEmoji()
            else {
                if (isPortrait) {
                    EnhancedIconButton(
                        onClick = {
                            essentials.launch {
                                if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                    scaffoldState.bottomSheetState.partialExpand()
                                } else {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = stringResource(R.string.properties)
                        )
                    }
                }
                EnhancedIconButton(
                    onClick = { showResetDialog = true },
                    enabled = component.bitmap != null && component.isBitmapChanged
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ImageReset,
                        contentDescription = stringResource(R.string.reset_image)
                    )
                }

                if (!isPortrait) actions()
            }
        },
        mainContent = {
            component.bitmap?.let { bitmap ->
                Cropper(
                    bitmap = bitmap,
                    crop = crop,
                    onImageCropStarted = component::imageCropStarted,
                    onImageCropFinished = {
                        component.imageCropFinished()
                        if (it != null) {
                            component.updateBitmap(it)
                        }
                        crop = false
                    },
                    rotationState = rotationState,
                    cropProperties = component.cropProperties,
                    cropType = component.cropType,
                    addVerticalInsets = !isPortrait,
                    coercePointsToImageArea = coercePointsToImageArea
                )
            }
        },
        controls = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FreeCornersCropToggle(
                    modifier = Modifier.fillMaxWidth(),
                    value = component.cropType == CropType.FreeCorners,
                    onClick = component::toggleFreeCornersCrop
                )
                BoxAnimatedVisibility(
                    visible = component.cropType == CropType.Default,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    CropRotationSelector(
                        value = rotationState.floatValue,
                        onValueChange = { rotationState.floatValue = it }
                    )
                }
                BoxAnimatedVisibility(
                    visible = component.cropType == CropType.FreeCorners,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        CoercePointsToImageBoundsToggle(
                            value = coercePointsToImageArea,
                            onValueChange = { coercePointsToImageArea = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        MagnifierEnabledSelector(
                            modifier = Modifier.fillMaxWidth(),
                            shape = ShapeDefaults.extraLarge,
                        )
                    }
                }
                BoxAnimatedVisibility(
                    visible = component.cropType != CropType.FreeCorners,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        AspectRatioSelector(
                            modifier = Modifier.fillMaxWidth(),
                            selectedAspectRatio = component.selectedAspectRatio,
                            onAspectRatioChange = component::setCropAspectRatio
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CropMaskSelection(
                            onCropMaskChange = component::setCropMask,
                            selectedItem = component.cropProperties.cropOutlineProperty,
                            loadImage = {
                                component.loadImage(it)?.asImageBitmap()
                            }
                        )
                    }
                }
                ImageFormatSelector(
                    modifier = Modifier.navigationBarsPadding(),
                    entries = if (component.cropProperties.cropOutlineProperty.outlineType == OutlineType.Rect) {
                        ImageFormatGroup.entries
                    } else ImageFormatGroup.alphaContainedEntries,
                    value = component.imageFormat,
                    onValueChange = {
                        component.setImageFormat(it)
                    }
                )
            }
        },
        buttons = {
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var job by remember { mutableStateOf<Job?>(null) }
            BottomButtonsBlock(
                isNoData = component.bitmap == null,
                onSecondaryButtonClick = {
                    if (component.bitmap == null) {
                        pickImage()
                    } else {
                        job?.cancel()
                        job = essentials.launch {
                            delay(500)
                            crop = true
                        }
                    }
                },
                onSecondaryButtonLongClick = if (component.bitmap == null) {
                    { showOneTimeImagePickingDialog = true }
                } else null,
                secondaryButtonIcon = if (component.bitmap == null) Icons.Rounded.AddPhotoAlt else Icons.Rounded.CropSmall,
                onPrimaryButtonClick = {
                    saveBitmap(null)
                },
                isPrimaryButtonVisible = component.isBitmapChanged,
                middleFab = {
                    EnhancedFloatingActionButton(
                        onClick = pickImage,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddPhotoAlt,
                            contentDescription = stringResource(R.string.add_image)
                        )
                    }
                },
                showMiddleFabInRow = component.bitmap != null,
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
        noDataControls = {
            ImageNotPickedWidget(onPickImage = pickImage)
        },
        canShowScreenData = component.bitmap != null,
        showActionsInTopAppBar = false
    )

    ResetDialog(
        visible = showResetDialog,
        onDismiss = { showResetDialog = false },
        onReset = component::resetBitmap
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    LoadingDialog(
        visible = component.isSaving || component.isImageLoading,
        onCancelLoading = component::cancelSaving,
        canCancel = component.isSaving
    )
}