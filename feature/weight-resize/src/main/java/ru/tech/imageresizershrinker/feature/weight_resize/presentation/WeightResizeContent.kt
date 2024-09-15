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

package ru.tech.imageresizershrinker.feature.weight_resize.presentation

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormatGroup
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.Preset
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.restrict
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResults
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.PanModeButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.PresetSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ScaleModeSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageContainer
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageCounter
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.detectSwipes
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.feature.weight_resize.presentation.components.ImageFormatAlert
import ru.tech.imageresizershrinker.feature.weight_resize.presentation.viewModel.WeightResizeViewModel

@Composable
fun WeightResizeContent(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    viewModel: WeightResizeViewModel = hiltViewModel()
) {
    val settingsState = LocalSettingsState.current

    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.updateUris(uris) {
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
            list.takeIf { it.isNotEmpty() }?.let { uris ->
                viewModel.updateUris(uris) {
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

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.haveChanges) showExitDialog = true
        else onGoBack()
    }


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

    var showPickImageFromUrisSheet by rememberSaveable { mutableStateOf(false) }

    val isPortrait by isPortraitOrientationAsState()

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }

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
                title = stringResource(R.string.by_bytes_resize),
                input = viewModel.bitmap,
                isLoading = viewModel.isImageLoading,
                size = viewModel.imageSize
            )
        },
        onGoBack = onBack,
        actions = {},
        topAppBarPersistentActions = {
            if (viewModel.bitmap == null) {
                TopAppBarEmoji()
            }
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = viewModel.bitmap != null,
            )
            if (viewModel.previewBitmap != null) {
                var editSheetData by remember {
                    mutableStateOf(listOf<Uri>())
                }
                ShareButton(
                    enabled = viewModel.canSave,
                    onShare = {
                        viewModel.shareBitmaps { showConfetti() }
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
            }
        },
        imagePreview = {
            ImageContainer(
                modifier = Modifier
                    .detectSwipes(
                        onSwipeRight = viewModel::selectLeftUri,
                        onSwipeLeft = viewModel::selectRightUri
                    ),
                imageInside = isPortrait,
                showOriginal = false,
                previewBitmap = viewModel.previewBitmap,
                originalBitmap = viewModel.bitmap,
                isLoading = viewModel.isImageLoading,
                shouldShowPreview = true
            )
        },
        controls = {
            ImageCounter(
                imageCount = viewModel.uris?.size?.takeIf { it > 1 },
                onRepick = {
                    showPickImageFromUrisSheet = true
                }
            )
            AnimatedContent(
                targetState = viewModel.handMode,
                transitionSpec = {
                    if (!targetState) {
                        slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
                    } else {
                        slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut()
                    }
                }
            ) { handMode ->
                if (handMode) {
                    RoundedTextField(
                        modifier = Modifier
                            .container(shape = RoundedCornerShape(24.dp))
                            .padding(8.dp),
                        enabled = viewModel.bitmap != null,
                        value = (viewModel.maxBytes / 1024).toString()
                            .takeIf { it != "0" } ?: "",
                        onValueChange = {
                            viewModel.updateMaxBytes(
                                it.restrict(1_000_000)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        label = stringResource(R.string.max_bytes)
                    )
                } else {
                    PresetSelector(
                        value = viewModel.presetSelected.let {
                            Preset.Percentage(it)
                        },
                        includeTelegramOption = false,
                        onValueChange = viewModel::selectPreset,
                        isBytesResize = true
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            SaveExifWidget(
                imageFormat = viewModel.imageFormat,
                checked = viewModel.keepExif,
                onCheckedChange = viewModel::setKeepExif
            )
            AnimatedVisibility(
                visible = viewModel.imageFormat.canChangeCompressionValue
            ) {
                Spacer(Modifier.height(8.dp))
            }
            ImageFormatAlert(
                format = viewModel.imageFormat,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            ImageFormatSelector(
                value = viewModel.imageFormat,
                onValueChange = viewModel::setImageFormat,
                entries = remember {
                    ImageFormatGroup.entries
                        .minus(ImageFormatGroup.Png)
                        .plus(
                            ImageFormatGroup.Custom(
                                title = "PNG Lossless",
                                formats = listOf(
                                    ImageFormat.Png.Lossless
                                )
                            )
                        )
                }
            )
            Spacer(Modifier.height(8.dp))
            ScaleModeSelector(
                value = viewModel.imageScaleMode,
                onValueChange = viewModel::setImageScaleMode
            )
        },
        buttons = {
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
                isPrimaryButtonVisible = viewModel.canSave,
                actions = {
                    PanModeButton(
                        selected = viewModel.handMode,
                        onClick = viewModel::updateHandMode
                    )
                }
            )
            if (showFolderSelectionDialog) {
                OneTimeSaveLocationSelectionDialog(
                    onDismiss = { showFolderSelectionDialog = false },
                    onSaveRequest = saveBitmaps
                )
            }
        },
        canShowScreenData = viewModel.bitmap != null,
        noDataControls = {
            if (!viewModel.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
        },
        isPortrait = isPortrait
    )

    if (viewModel.isSaving) {
        LoadingDialog(
            done = viewModel.done,
            left = viewModel.uris?.size ?: 1,
            onCancelLoading = viewModel::cancelSaving
        )
    }

    PickImageFromUrisSheet(
        transformations = listOf(
            viewModel.imageInfoTransformationFactory(
                imageInfo = ImageInfo()
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
                viewModel.updateSelectedUri(uri = uri)
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

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}