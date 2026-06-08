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

package com.t8rin.imagetoolbox.feature.image_stitch.presentation

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageReorderCarousel
import com.t8rin.imagetoolbox.core.ui.widget.controls.ScaleSmallImagesToLargeToggle
import com.t8rin.imagetoolbox.core.ui.widget.controls.UndoRedoButtons
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.BlendingModeSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageContainer
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchFadeSide
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchMode
import com.t8rin.imagetoolbox.feature.image_stitch.presentation.components.FadeStrengthSelector
import com.t8rin.imagetoolbox.feature.image_stitch.presentation.components.GridSpacingSelector
import com.t8rin.imagetoolbox.feature.image_stitch.presentation.components.ImageFadingEdgesSelector
import com.t8rin.imagetoolbox.feature.image_stitch.presentation.components.ImageScaleSelector
import com.t8rin.imagetoolbox.feature.image_stitch.presentation.components.SpacingSelector
import com.t8rin.imagetoolbox.feature.image_stitch.presentation.components.StitchAlignmentSelector
import com.t8rin.imagetoolbox.feature.image_stitch.presentation.components.StitchModeSelector
import com.t8rin.imagetoolbox.feature.image_stitch.presentation.screenLogic.ImageStitchingComponent
import kotlin.math.pow
import kotlin.math.roundToLong

@Composable
fun ImageStitchingContent(
    component: ImageStitchingComponent
) {
    AutoContentBasedColors(component.previewBitmap)

    val imagePicker = rememberImagePicker(onSuccess = component::updateUris)

    val addImagesImagePicker = rememberImagePicker(onSuccess = component::addUrisToEnd)

    val addImages = addImagesImagePicker::pickImage

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
            oneTimeSaveLocationUri = it
        )
    }

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
                title = stringResource(R.string.image_stitching),
                input = component.uris,
                isLoading = component.isImageLoading,
                size = component
                    .imageByteSize?.times(component.combiningParams.outputScale.pow(2))
                    ?.roundToLong(),
                updateOnSizeChange = false
            )
        },
        onGoBack = onBack,
        actions = {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
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
                onShare = component::shareBitmap,
                onCopy = {
                    component.cacheCurrentImage(Clipboard::copy)
                },
                onEdit = {
                    component.cacheCurrentImage {
                        editSheetData = listOf(it)
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
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.previewBitmap != null,
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
        topAppBarPersistentActions = {
            if (component.uris.isNullOrEmpty()) {
                TopAppBarEmoji()
            }
        },
        controls = {
            val combiningParams = component.combiningParams
            val stitchMode = combiningParams.stitchMode
            val isGridMode = stitchMode is StitchMode.Grid
            val hasNegativeSpacing = if (isGridMode) {
                combiningParams.hasNegativeSpacing()
            } else {
                combiningParams.spacingFor(stitchMode.isHorizontal()) < 0
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageReorderCarousel(
                    images = component.uris,
                    onReorder = component::reorderUris,
                    onNeedToAddImage = addImages,
                    onNeedToRemoveImageAt = component::removeImageAt,
                    onNavigate = component.onNavigate
                )
                ImageScaleSelector(
                    modifier = Modifier.padding(top = 8.dp),
                    value = combiningParams.outputScale,
                    onValueChange = component::updateImageScale,
                    approximateImageSize = component.imageSize
                )
                StitchModeSelector(
                    value = stitchMode,
                    onValueChange = component::setStitchMode
                )
                AnimatedVisibility(
                    visible = stitchMode !is StitchMode.Auto,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    if (isGridMode) {
                        GridSpacingSelector(
                            horizontalValue = combiningParams.horizontalSpacing,
                            verticalValue = combiningParams.verticalSpacing,
                            onHorizontalValueChange = component::updateHorizontalImageSpacing,
                            onVerticalValueChange = component::updateVerticalImageSpacing
                        )
                    } else {
                        SpacingSelector(
                            value = combiningParams.spacingFor(stitchMode.isHorizontal()),
                            onValueChange = component::updateImageSpacing
                        )
                    }
                }
                AnimatedVisibility(
                    visible = hasNegativeSpacing && stitchMode !is StitchMode.Auto,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ImageFadingEdgesSelector(
                        value = combiningParams.fadingEdgesMode,
                        onValueChange = component::setFadingEdgesMode
                    )
                }
                AnimatedVisibility(
                    visible = hasNegativeSpacing &&
                            combiningParams.fadingEdgesMode != StitchFadeSide.None &&
                            stitchMode !is StitchMode.Auto,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    FadeStrengthSelector(
                        value = combiningParams.fadeStrength,
                        onValueChange = component::setFadeStrength
                    )
                }
                AnimatedVisibility(
                    visible = stitchMode !is StitchMode.Auto,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ScaleSmallImagesToLargeToggle(
                        checked = combiningParams.scaleSmallImagesToLarge,
                        onCheckedChange = component::toggleScaleSmallImagesToLarge
                    )
                }
                AnimatedVisibility(
                    visible = hasNegativeSpacing && stitchMode !is StitchMode.Auto,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    BlendingModeSelector(
                        value = combiningParams.blendingMode,
                        onValueChange = component::setBlendingMode,
                        color = Color.Unspecified
                    )
                }
                AnimatedVisibility(
                    visible = !combiningParams.scaleSmallImagesToLarge && stitchMode !is StitchMode.Auto,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    StitchAlignmentSelector(
                        value = combiningParams.alignment,
                        onValueChange = component::setStitchAlignment
                    )
                }
                ColorRowSelector(
                    value = Color(combiningParams.backgroundColor),
                    onValueChange = {
                        component.updateBackgroundSelector(it.toArgb())
                    },
                    modifier = Modifier.container(
                        shape = ShapeDefaults.extraLarge
                    )
                )
                QualitySelector(
                    imageFormat = component.imageInfo.imageFormat,
                    quality = component.imageInfo.quality,
                    onQualityChange = component::setQuality
                )
                ImageFormatSelector(
                    value = component.imageInfo.imageFormat,
                    onValueChange = component::setImageFormat,
                    quality = component.imageInfo.quality,
                )
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
                isPrimaryButtonVisible = component.previewBitmap != null,
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
        noDataControls = {
            if (!component.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
        },
        canShowScreenData = !component.uris.isNullOrEmpty()
    )

    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.uris?.size ?: 1,
        onCancelLoading = component::cancelSaving
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}