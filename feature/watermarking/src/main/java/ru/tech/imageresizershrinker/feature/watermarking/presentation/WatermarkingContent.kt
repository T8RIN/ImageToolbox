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

package ru.tech.imageresizershrinker.feature.watermarking.presentation

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import coil.request.ImageRequest
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ImageReset
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResults
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalImageLoader
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.CompareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShowOriginalButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.QualitySelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageContainer
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageCounter
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.modifier.detectSwipes
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareSheet
import ru.tech.imageresizershrinker.feature.watermarking.domain.WatermarkParams
import ru.tech.imageresizershrinker.feature.watermarking.presentation.components.WatermarkDataSelector
import ru.tech.imageresizershrinker.feature.watermarking.presentation.components.WatermarkParamsSelectionGroup
import ru.tech.imageresizershrinker.feature.watermarking.presentation.components.WatermarkingTypeSelector
import ru.tech.imageresizershrinker.feature.watermarking.presentation.viewModel.WatermarkingComponent

@Composable
fun WatermarkingContent(
    onGoBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    viewModel: WatermarkingComponent
) {
    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current

    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val context = LocalContext.current as Activity

    val confettiHostState = LocalConfettiHostState.current
    val toastHostState = LocalToastHostState.current

    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    val imageLoader = LocalImageLoader.current
    LaunchedEffect(viewModel.selectedUri) {
        viewModel.selectedUri.let {
            if (allowChangeColor) {
                imageLoader.execute(
                    ImageRequest.Builder(context).data(it).build()
                ).drawable?.toBitmap()?.let { bitmap ->
                    themeState.updateColorByImage(bitmap)
                }
            }
        }
    }

    val imagePicker = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple)
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let {
            viewModel.setUris(it) {
                scope.launch {
                    toastHostState.showError(context, it)
                }
            }
        }
    }

    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = !viewModel.initialUris.isNullOrEmpty()
    )

    val isPortrait by isPortraitOrientationAsState()

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }
    var showPickImageFromUrisSheet by rememberSaveable { mutableStateOf(false) }
    var showCompareSheet by rememberSaveable { mutableStateOf(false) }
    var showResetDialog by rememberSaveable { mutableStateOf(false) }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !viewModel.haveChanges,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.watermarking),
                input = viewModel.selectedUri.takeIf { it != Uri.EMPTY },
                isLoading = viewModel.isImageLoading,
                size = null
            )
        },
        onGoBack = {
            if (viewModel.haveChanges) showExitDialog = true
            else onGoBack()
        },
        topAppBarPersistentActions = {
            if (viewModel.previewBitmap == null) TopAppBarEmoji()
            CompareButton(
                onClick = { showCompareSheet = true },
                visible = viewModel.previewBitmap != null && viewModel.internalBitmap != null
            )
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = viewModel.previewBitmap != null
            )
        },
        actions = {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            ShareButton(
                enabled = viewModel.previewBitmap != null,
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
                enabled = viewModel.internalBitmap != null,
                onClick = { showResetDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ImageReset,
                    contentDescription = stringResource(R.string.reset_image)
                )
            }
            if (viewModel.internalBitmap != null) {
                ShowOriginalButton(
                    canShow = true,
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
                        onSwipeRight = viewModel::selectLeftUri,
                        onSwipeLeft = viewModel::selectRightUri
                    ),
                imageInside = isPortrait,
                showOriginal = showOriginal,
                previewBitmap = viewModel.previewBitmap,
                originalBitmap = viewModel.internalBitmap,
                isLoading = viewModel.isImageLoading,
                shouldShowPreview = true
            )
        },
        controls = {
            ImageCounter(
                imageCount = viewModel.uris.size.takeIf { it > 1 },
                onRepick = {
                    showPickImageFromUrisSheet = true
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            WatermarkingTypeSelector(
                value = viewModel.watermarkParams,
                onValueChange = viewModel::updateWatermarkParams
            )
            Spacer(modifier = Modifier.height(8.dp))
            WatermarkParamsSelectionGroup(
                value = viewModel.watermarkParams,
                onValueChange = viewModel::updateWatermarkParams
            )
            Spacer(modifier = Modifier.height(8.dp))
            WatermarkDataSelector(
                value = viewModel.watermarkParams,
                onValueChange = viewModel::updateWatermarkParams
            )
            Spacer(modifier = Modifier.height(8.dp))
            SaveExifWidget(
                checked = viewModel.keepExif,
                imageFormat = viewModel.imageFormat,
                onCheckedChange = viewModel::toggleKeepExif
            )
            if (viewModel.imageFormat.canChangeCompressionValue) {
                Spacer(Modifier.height(8.dp))
            }
            QualitySelector(
                imageFormat = viewModel.imageFormat,
                quality = viewModel.quality,
                onQualityChange = viewModel::setQuality
            )
            Spacer(modifier = Modifier.height(8.dp))
            ImageFormatSelector(
                value = viewModel.imageFormat,
                onValueChange = viewModel::setImageFormat
            )
            Spacer(modifier = Modifier.height(8.dp))
        },
        buttons = {
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
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                targetState = (viewModel.uris.isEmpty()) to isPortrait,
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
            if (showFolderSelectionDialog) {
                OneTimeSaveLocationSelectionDialog(
                    onDismiss = { showFolderSelectionDialog = false },
                    onSaveRequest = saveBitmaps,
                    formatForFilenameSelection = viewModel.getFormatForFilenameSelection()
                )
            }
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
        isPortrait = isPortrait,
        canShowScreenData = viewModel.uris.isNotEmpty()
    )

    val transformations by remember(viewModel.previewBitmap) {
        derivedStateOf {
            listOf(
                viewModel.getWatermarkTransformation()
            )
        }
    }

    PickImageFromUrisSheet(
        transformations = transformations,
        visible = showPickImageFromUrisSheet,
        onDismiss = {
            showPickImageFromUrisSheet = false
        },
        uris = viewModel.uris,
        selectedUri = viewModel.selectedUri,
        onUriPicked = { uri ->
            viewModel.updateSelectedUri(uri = uri) {
                scope.launch {
                    toastHostState.showError(context, it)
                }
            }
        },
        onUriRemoved = { uri ->
            viewModel.updateUrisSilently(removedUri = uri)
        },
        columns = if (isPortrait) 2 else 4,
    )

    ResetDialog(
        visible = showResetDialog,
        onDismiss = { showResetDialog = false },
        title = stringResource(R.string.reset_properties),
        text = stringResource(R.string.reset_properties_sub),
        onReset = {
            viewModel.updateWatermarkParams(WatermarkParams.Default)
        }
    )

    CompareSheet(
        data = viewModel.internalBitmap to viewModel.previewBitmap,
        visible = showCompareSheet,
        onDismiss = {
            showCompareSheet = false
        }
    )

    ZoomModalSheet(
        data = viewModel.selectedUri,
        visible = showZoomSheet,
        transformations = transformations,
        onDismiss = {
            showZoomSheet = false
        }
    )

    if (viewModel.isSaving) {
        LoadingDialog(
            done = viewModel.done,
            left = viewModel.left,
            onCancelLoading = viewModel::cancelSaving
        )
    }

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}