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

package ru.tech.imageresizershrinker.feature.resize_convert.presentation

import android.net.Uri
import androidx.activity.ComponentActivity
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
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ImageReset
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResults
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.CompareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShowOriginalButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageTransformBar
import ru.tech.imageresizershrinker.core.ui.widget.controls.ResizeImageField
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.resize_group.ResizeTypeSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.PresetSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.QualitySelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ScaleModeSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageContainer
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageCounter
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.EditExifSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareSheet
import ru.tech.imageresizershrinker.feature.resize_convert.presentation.viewModel.ResizeAndConvertViewModel

@Composable
fun ResizeAndConvertContent(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    viewModel: ResizeAndConvertViewModel = hiltViewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current
    val themeState = LocalDynamicThemeState.current

    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() }?.let {
            viewModel.updateUris(it) {
                scope.launch {
                    toastHostState.showError(context, it)
                }
            }
        }
    }
    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor) {
                themeState.updateColorByImage(it)
            }
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let {
                viewModel.updateUris(list) {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            }
        }

    val pickImage = pickImageLauncher::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = !uriState.isNullOrEmpty()
    )

    val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
        viewModel.saveBitmaps(it) { results ->
            context.parseSaveResults(
                scope = scope,
                results = results,
                toastHostState = toastHostState,
                isOverwritten = settingsState.overwriteFiles,
                showConfetti = showConfetti
            )
        }
    }

    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }

    var showPickImageFromUrisSheet by rememberSaveable { mutableStateOf(false) }

    var showEditExifDialog by rememberSaveable { mutableStateOf(false) }

    val isPortrait by isPortraitOrientationAsState()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.haveChanges) showExitDialog = true
        else onGoBack()
    }

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showCompareSheet by rememberSaveable { mutableStateOf(false) }

    CompareSheet(
        data = viewModel.bitmap to viewModel.previewBitmap,
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

    AdaptiveLayoutScreen(
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.resize_and_convert),
                input = viewModel.bitmap,
                isLoading = viewModel.isImageLoading,
                size = viewModel.imageInfo.sizeInBytes.toLong()
            )
        },
        onGoBack = onBack,
        actions = {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            ShareButton(
                enabled = viewModel.bitmap != null,
                onShare = {
                    viewModel.shareBitmaps(showConfetti)
                },
                onCopy = { manager ->
                    viewModel.cacheCurrentImage { uri ->
                        manager.setClip(uri.asClip(context))
                        showConfetti()
                    }
                },
                onEdit = {
                    viewModel.cacheImages {
                        editSheetData = it
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
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
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
                originalBitmap = viewModel.bitmap,
                isLoading = viewModel.isImageLoading,
                shouldShowPreview = viewModel.shouldShowPreview
            )
        },
        controls = {
            val imageInfo = viewModel.imageInfo
            ImageCounter(
                imageCount = viewModel.uris?.size?.takeIf { it > 1 },
                onRepick = {
                    showPickImageFromUrisSheet = true
                }
            )
            AnimatedContent(
                targetState = viewModel.uris?.size == 1
            ) { oneUri ->
                if (oneUri) {
                    ImageTransformBar(
                        onEditExif = { showEditExifDialog = true },
                        onRotateLeft = viewModel::rotateLeft,
                        onFlip = viewModel::flip,
                        imageFormat = viewModel.imageInfo.imageFormat,
                        onRotateRight = viewModel::rotateRight
                    )
                } else {
                    LaunchedEffect(Unit) {
                        showEditExifDialog = false
                        viewModel.updateExif(null)
                    }
                    ImageTransformBar(
                        onRotateLeft = viewModel::rotateLeft,
                        onFlip = viewModel::flip,
                        onRotateRight = viewModel::rotateRight
                    )
                }
            }
            Spacer(Modifier.size(8.dp))
            PresetSelector(
                value = viewModel.presetSelected,
                includeTelegramOption = true,
                onValueChange = {
                    viewModel.updatePreset(it)
                }
            )
            Spacer(Modifier.size(8.dp))
            AnimatedVisibility(
                visible = viewModel.uris?.size != 1,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    SaveExifWidget(
                        imageFormat = viewModel.imageInfo.imageFormat,
                        checked = viewModel.keepExif,
                        onCheckedChange = viewModel::setKeepExif
                    )
                    Spacer(Modifier.size(8.dp))
                }
            }
            ResizeImageField(
                imageInfo = imageInfo,
                originalSize = viewModel.originalSize,
                onHeightChange = viewModel::updateHeight,
                onWidthChange = viewModel::updateWidth,
                showWarning = viewModel.showWarning
            )
            if (imageInfo.imageFormat.canChangeCompressionValue) {
                Spacer(Modifier.height(8.dp))
            }
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
        },
        buttons = { actions ->
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                targetState = (viewModel.uris.isNullOrEmpty()) to isPortrait,
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = {
                    saveBitmaps(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) actions()
                }
            )
            if (showFolderSelectionDialog) {
                OneTimeSaveLocationSelectionDialog(
                    onDismiss = { showFolderSelectionDialog = false },
                    onSaveRequest = saveBitmaps
                )
            }
        },
        topAppBarPersistentActions = {
            if (viewModel.bitmap == null) TopAppBarEmoji()
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
        canShowScreenData = viewModel.bitmap != null,
        forceImagePreviewToMax = showOriginal,
        noDataControls = {
            if (!viewModel.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
        },
        isPortrait = isPortrait
    )

    ResetDialog(
        visible = showResetDialog,
        onDismiss = { showResetDialog = false },
        onReset = viewModel::resetValues
    )

    PickImageFromUrisSheet(
        transformations = listOf(
            viewModel.imageInfoTransformationFactory(
                imageInfo = viewModel.imageInfo,
                preset = viewModel.presetSelected
            )
        ),
        visible = showPickImageFromUrisSheet,
        onDismiss = {
            showPickImageFromUrisSheet = false
        },
        uris = viewModel.uris,
        selectedUri = viewModel.selectedUri,
        onUriPicked = { uri ->
            try {
                viewModel.setBitmap(uri = uri)
            } catch (e: Exception) {
                scope.launch {
                    toastHostState.showError(context, e)
                }
            }
        },
        onUriRemoved = { uri ->
            viewModel.updateUrisSilently(removedUri = uri)
        },
        columns = if (isPortrait) 2 else 4,
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

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    if (viewModel.isSaving) {
        LoadingDialog(
            done = viewModel.done,
            left = viewModel.uris?.size ?: 1,
            onCancelLoading = viewModel::cancelSaving
        )
    }
}