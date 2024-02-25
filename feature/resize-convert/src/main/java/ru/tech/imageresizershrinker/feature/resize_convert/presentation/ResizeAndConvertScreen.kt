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

import android.content.res.Configuration
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
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.failedToSaveImages
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.CompareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShowOriginalButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageTransformBar
import ru.tech.imageresizershrinker.core.ui.widget.controls.PresetWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.QualityWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.ResizeImageField
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.ScaleModeSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.resize_group.ResizeTypeSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageContainer
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageCounter
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.EditExifSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareSheet
import ru.tech.imageresizershrinker.feature.resize_convert.presentation.viewModel.ResizeAndConvertViewModel

@Composable
fun ResizeAndConvertScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: ResizeAndConvertViewModel = hiltViewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current
    val themeState = LocalDynamicThemeState.current

    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiController.showEmpty()
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

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    val saveBitmaps: () -> Unit = {
        viewModel.saveBitmaps { results, savingPath ->
            context.failedToSaveImages(
                scope = scope,
                results = results,
                toastHostState = toastHostState,
                savingPathString = savingPath,
                isOverwritten = settingsState.overwriteFiles,
                showConfetti = showConfetti
            )
        }
    }

    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }

    val showPickImageFromUrisSheet = rememberSaveable { mutableStateOf(false) }

    val showEditExifDialog = rememberSaveable { mutableStateOf(false) }

    val isPortrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.imageInfo.haveChanges(viewModel.bitmap)) showExitDialog = true
        else onGoBack()
    }

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }

    val showCompareSheet = rememberSaveable { mutableStateOf(false) }

    CompareSheet(
        data = viewModel.bitmap to viewModel.previewBitmap,
        visible = showCompareSheet
    )

    ZoomModalSheet(
        data = viewModel.previewBitmap,
        visible = showZoomSheet
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
                ) {
                    Icon(Icons.Rounded.History, null)
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
                    showPickImageFromUrisSheet.value = true
                }
            )
            AnimatedContent(
                targetState = viewModel.uris?.size == 1
            ) { oneUri ->
                if (oneUri) {
                    ImageTransformBar(
                        onEditExif = { showEditExifDialog.value = true },
                        onRotateLeft = viewModel::rotateLeft,
                        onFlip = viewModel::flip,
                        imageFormat = viewModel.imageInfo.imageFormat,
                        onRotateRight = viewModel::rotateRight
                    )
                } else {
                    LaunchedEffect(Unit) {
                        showEditExifDialog.value = false
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
            PresetWidget(
                selectedPreset = viewModel.presetSelected,
                includeTelegramOption = true,
                onPresetSelected = {
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
            QualityWidget(
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
            BottomButtonsBlock(
                targetState = (viewModel.uris.isNullOrEmpty()) to isPortrait,
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = saveBitmaps,
                actions = {
                    if (isPortrait) actions()
                }
            )
        },
        topAppBarPersistentActions = {
            if (viewModel.bitmap == null) TopAppBarEmoji()
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
            left = viewModel.uris?.size ?: 1
        ) {
            viewModel.cancelSaving()
        }
    }
}