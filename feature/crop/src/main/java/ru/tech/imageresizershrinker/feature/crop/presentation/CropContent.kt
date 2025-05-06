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

package ru.tech.imageresizershrinker.feature.crop.presentation


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
import androidx.compose.material3.Text
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormatGroup
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.CropSmall
import ru.tech.imageresizershrinker.core.resources.icons.ImageReset
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.Picker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveBottomScaffoldLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.AspectRatioSelector
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.AutoContentBasedColors
import ru.tech.imageresizershrinker.feature.crop.presentation.components.CoercePointsToImageBoundsToggle
import ru.tech.imageresizershrinker.feature.crop.presentation.components.CropMaskSelection
import ru.tech.imageresizershrinker.feature.crop.presentation.components.CropType
import ru.tech.imageresizershrinker.feature.crop.presentation.components.Cropper
import ru.tech.imageresizershrinker.feature.crop.presentation.components.FreeCornersCropToggle
import ru.tech.imageresizershrinker.feature.crop.presentation.screenLogic.CropComponent

@Composable
fun CropContent(
    component: CropComponent
) {
    val essentials = rememberLocalEssentials()
    val scope = essentials.coroutineScope
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
            var job by remember { mutableStateOf<Job?>(null) }
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = {
                    job?.cancel()
                    job = scope.launch {
                        delay(500)
                        crop = true
                    }
                }
            ) {
                Text(stringResource(R.string.crop))
            }
        },
        topAppBarPersistentActions = { scaffoldState ->
            if (component.bitmap == null) TopAppBarEmoji()
            else {
                if (isPortrait) {
                    EnhancedIconButton(
                        onClick = {
                            scope.launch {
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
                    visible = component.cropType == CropType.FreeCorners,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    CoercePointsToImageBoundsToggle(
                        value = coercePointsToImageArea,
                        onValueChange = { coercePointsToImageArea = it },
                        modifier = Modifier.fillMaxWidth()
                    )
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
                onSecondaryButtonClick = pickImage,
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                },
                onPrimaryButtonClick = {
                    saveBitmap(null)
                },
                isPrimaryButtonVisible = component.isBitmapChanged,
                columnarFab = {
                    EnhancedFloatingActionButton(
                        onClick = {
                            job?.cancel()
                            job = scope.launch {
                                delay(500)
                                crop = true
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CropSmall,
                            contentDescription = stringResource(R.string.crop)
                        )
                    }
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