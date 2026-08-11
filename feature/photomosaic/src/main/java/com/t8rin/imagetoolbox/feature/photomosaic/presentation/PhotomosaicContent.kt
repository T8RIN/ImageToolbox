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

package com.t8rin.imagetoolbox.feature.photomosaic.presentation

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.resources.icons.HashTag
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Opacity
import com.t8rin.imagetoolbox.core.resources.icons.PhotoSizeSelectSmall
import com.t8rin.imagetoolbox.core.resources.icons.RepeatOne
import com.t8rin.imagetoolbox.core.resources.icons.ViewQuilt
import com.t8rin.imagetoolbox.core.resources.utils.compositeOverSafe
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.CompareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageReorderCarousel
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageContainer
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageCounter
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.detectSwipes
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.sheets.PickImageFromUrisSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.compare.presentation.components.CompareSheet
import com.t8rin.imagetoolbox.feature.photomosaic.domain.PhotomosaicParams
import com.t8rin.imagetoolbox.feature.photomosaic.presentation.screenLogic.PhotomosaicComponent
import kotlin.math.roundToInt

@Composable
fun PhotomosaicContent(component: PhotomosaicComponent) {
    AutoContentBasedColors(component.previewBitmap)

    val imagePicker = rememberImagePicker(onSuccess = component::setUris)
    val pickImage = imagePicker::pickImage
    val tilePicker = rememberImagePicker(onSuccess = component::addTileUris)

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showSaveFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showTilePickerDialog by rememberSaveable { mutableStateOf(false) }
    var showPickImageFromUrisSheet by rememberSaveable { mutableStateOf(false) }
    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showCompareSheet by rememberSaveable { mutableStateOf(false) }
    var editSheetData by remember { mutableStateOf(listOf<Uri>()) }
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
                title = stringResource(R.string.photomosaic),
                input = component.selectedUri,
                isLoading = component.isImageLoading,
                size = null
            )
        },
        onGoBack = onBack,
        topAppBarPersistentActions = {
            if (component.previewBitmap == null) TopAppBarEmoji()
            CompareButton(
                onClick = { showCompareSheet = true },
                visible = component.selectedUri != null && component.previewBitmap != null
            )
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.previewBitmap != null
            )
        },
        actions = {
            ShareButton(
                enabled = component.previewBitmap != null,
                onShare = component::share,
                onCopy = { component.cache(Clipboard::copy) },
                onEdit = { component.cacheAll { editSheetData = it } }
            )
            ProcessImagesPreferenceSheet(
                uris = editSheetData,
                visible = editSheetData.isNotEmpty(),
                onDismiss = { editSheetData = emptyList() },
                onNavigate = component.onNavigate
            )
        },
        imagePreview = {
            if (component.tileUris.isEmpty()) {
                var aspectRatio by remember {
                    mutableFloatStateOf(1f)
                }

                Picture(
                    model = component.selectedUri,
                    size = 1024,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .aspectRatio(aspectRatio)
                        .detectSwipes(
                            onSwipeRight = component::selectLeftUri,
                            onSwipeLeft = component::selectRightUri
                        ),
                    onSuccess = {
                        aspectRatio = it.result.image.toBitmap().safeAspectRatio
                    },
                    error = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    takeColorFromScheme { isNightMode ->
                                        errorContainer.copy(
                                            if (isNightMode) 0.25f
                                            else 1f
                                        ).compositeOverSafe(surface)
                                    }
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.BrokenImageAlt,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.5f),
                                tint = MaterialTheme.colorScheme.onErrorContainer.copy(0.8f)
                            )
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                )
            } else {
                ImageContainer(
                    modifier = Modifier.detectSwipes(
                        onSwipeRight = component::selectLeftUri,
                        onSwipeLeft = component::selectRightUri
                    ),
                    imageInside = isPortrait,
                    showOriginal = false,
                    previewBitmap = component.previewBitmap,
                    originalBitmap = null,
                    isLoading = component.isImageLoading,
                    shouldShowPreview = true
                )
            }
        },
        controls = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageCounter(
                    imageCount = component.uris.size.takeIf { it > 1 },
                    onRepick = { showPickImageFromUrisSheet = true }
                )
                AnimatedContent(
                    targetState = component.tileUris.isEmpty()
                ) { isEmpty ->
                    if (isEmpty) {
                        PreferenceItem(
                            title = stringResource(R.string.photomosaic_tiles),
                            subtitle = stringResource(R.string.photomosaic_tiles_empty_sub),
                            startIcon = Icons.TwoTone.ViewQuilt,
                            endIcon = Icons.Rounded.MiniEdit,
                            onClick = { showTilePickerDialog = true },
                            shape = ShapeDefaults.extraLarge,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        ImageReorderCarousel(
                            images = component.tileUris,
                            onReorder = component::setTileUris,
                            onNeedToAddImage = { showTilePickerDialog = true },
                            onNeedToRemoveImageAt = component::removeTileAt,
                            onNavigate = component.onNavigate,
                            title = stringResource(R.string.photomosaic_tiles)
                        )
                    }
                }
                EnhancedSliderItem(
                    value = component.params.columns,
                    title = stringResource(R.string.photomosaic_columns),
                    icon = Icons.Outlined.PhotoSizeSelectSmall,
                    valueRange = PhotomosaicParams.MIN_COLUMNS.toFloat()..PhotomosaicParams.MAX_COLUMNS.toFloat(),
                    steps = PhotomosaicParams.MAX_COLUMNS - PhotomosaicParams.MIN_COLUMNS - 1,
                    internalStateTransformation = Float::roundToInt,
                    onValueChange = {},
                    onValueChangeFinished = {
                        component.updateParams(component.params.copy(columns = it.roundToInt()))
                    },
                    shape = ShapeDefaults.extraLarge
                )
                EnhancedSliderItem(
                    value = component.params.colorBlend * 100,
                    title = stringResource(R.string.photomosaic_color_blend),
                    icon = Icons.Rounded.Opacity,
                    valueRange = 0f..PhotomosaicParams.MAX_COLOR_BLEND * 100,
                    valueSuffix = "%",
                    internalStateTransformation = Float::roundToInt,
                    onValueChange = {},
                    onValueChangeFinished = {
                        component.updateParams(component.params.copy(colorBlend = it / 100f))
                    },
                    shape = ShapeDefaults.extraLarge
                )
                EnhancedSliderItem(
                    value = component.params.repeatDistance,
                    title = stringResource(R.string.photomosaic_repeat_distance),
                    valueRange = 0f..PhotomosaicParams.MAX_REPEAT_DISTANCE.toFloat(),
                    steps = PhotomosaicParams.MAX_REPEAT_DISTANCE - 1,
                    internalStateTransformation = Float::roundToInt,
                    icon = Icons.Rounded.RepeatOne,
                    onValueChange = {},
                    onValueChangeFinished = {
                        component.updateParams(component.params.copy(repeatDistance = it.roundToInt()))
                    },
                    shape = ShapeDefaults.extraLarge
                )
                EnhancedSliderItem(
                    value = component.params.maxTiles,
                    title = stringResource(R.string.photomosaic_max_tiles),
                    valueRange = PhotomosaicParams.MIN_TILES.toFloat()..PhotomosaicParams.MAX_TILES.toFloat(),
                    steps = 48,
                    icon = Icons.Rounded.HashTag,
                    internalStateTransformation = { (it / 10).roundToInt() * 10 },
                    onValueChange = {},
                    onValueChangeFinished = {
                        component.updateParams(component.params.copy(maxTiles = it.roundToInt()))
                    },
                    shape = ShapeDefaults.extraLarge
                )
                QualitySelector(
                    imageFormat = component.imageFormat,
                    quality = component.quality,
                    onQualityChange = component::setQuality
                )
                ImageFormatSelector(
                    modifier = Modifier.navigationBarsPadding(),
                    value = component.imageFormat,
                    quality = component.quality,
                    onValueChange = component::setImageFormat,
                    forceEnabled = true
                )
            }
        },
        buttons = { actions ->
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = component.uris.isEmpty(),
                onSecondaryButtonClick = pickImage,
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                },
                onPrimaryButtonClick = { component.save(null) },
                onPrimaryButtonLongClick = { showSaveFolderDialog = true },
                isPrimaryButtonEnabled = component.previewBitmap != null,
                actions = {
                    if (isPortrait) actions()
                }
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showSaveFolderDialog,
                onDismiss = { showSaveFolderDialog = false },
                onSaveRequest = component::save,
                formatForFilenameSelection = component.getFormatForFilenameSelection(),
                hasOriginalUri = false
            )
            OneTimeImagePickingDialog(
                visible = showOneTimeImagePickingDialog,
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker
            )
        },
        noDataControls = {
            ImageNotPickedWidget(onPickImage = pickImage)
        },
        canShowScreenData = component.uris.isNotEmpty()
    )

    val transformations by remember(component.params, component.tileUris) {
        derivedStateOf {
            listOf(component.getPhotomosaicTransformation())
        }
    }

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

    CompareSheet(
        beforeContent = {
            Picture(
                model = component.selectedUri,
                modifier = Modifier.aspectRatio(
                    component.previewBitmap?.safeAspectRatio ?: 1f
                )
            )
        },
        afterContent = {
            Picture(
                model = component.previewBitmap,
                modifier = Modifier.aspectRatio(
                    component.previewBitmap?.safeAspectRatio ?: 1f
                )
            )
        },
        visible = showCompareSheet,
        onDismiss = { showCompareSheet = false }
    )

    ZoomModalSheet(
        data = component.previewBitmap,
        visible = showZoomSheet,
        onDismiss = { showZoomSheet = false }
    )

    OneTimeImagePickingDialog(
        visible = showTilePickerDialog,
        onDismiss = { showTilePickerDialog = false },
        picker = Picker.Multiple,
        imagePicker = tilePicker
    )
    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.total,
        onCancelLoading = component::cancelSaving
    )
    ExitWithoutSavingDialog(
        visible = showExitDialog,
        onDismiss = { showExitDialog = false },
        onExit = component.onGoBack
    )
}
