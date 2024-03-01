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
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.failedToSaveImages
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.CompareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShowOriginalButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageContainer
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageCounter
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalImageLoader
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareSheet
import ru.tech.imageresizershrinker.feature.watermarking.presentation.components.WatermarkDataSelector
import ru.tech.imageresizershrinker.feature.watermarking.presentation.components.WatermarkParamsSelectionGroup
import ru.tech.imageresizershrinker.feature.watermarking.presentation.components.WatermarkingTypeSelector
import ru.tech.imageresizershrinker.feature.watermarking.presentation.viewModel.WatermarkingViewModel

@Composable
fun WatermarkingScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: WatermarkingViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current

    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val context = LocalContext.current as Activity

    val confettiController = LocalConfettiController.current
    val toastHostState = LocalToastHostState.current

    val showConfetti: () -> Unit = {
        scope.launch {
            confettiController.showEmpty()
        }
    }

    LaunchedEffect(uriState) {
        uriState?.let {
            viewModel.setUris(it) {
                scope.launch {
                    toastHostState.showError(context, it)
                }
            }
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

    val pickImageLauncher =
        rememberImagePicker(
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

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    val isPortrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }
    val showPickImageFromUrisSheet = rememberSaveable { mutableStateOf(false) }
    val showCompareSheet = rememberSaveable { mutableStateOf(false) }

    AdaptiveLayoutScreen(
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.watermarking),
                input = viewModel.selectedUri.takeIf { it != Uri.EMPTY },
                isLoading = viewModel.isImageLoading,
                size = null
            )
        },
        onGoBack = {
            if (viewModel.uris.isEmpty()) onGoBack()
            else showExitDialog = true
        },
        topAppBarPersistentActions = {
            if (viewModel.previewBitmap == null) TopAppBarEmoji()
            CompareButton(
                onClick = { showCompareSheet.value = true },
                visible = viewModel.previewBitmap != null && viewModel.internalBitmap != null
            )
            ZoomButton(
                onClick = { showZoomSheet.value = true },
                visible = viewModel.previewBitmap != null
            )
        },
        actions = {
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
                }
            )
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
                    showPickImageFromUrisSheet.value = true
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
            Spacer(modifier = Modifier.height(8.dp))
            ImageFormatSelector(
                value = viewModel.imageFormat,
                onValueChange = viewModel::setImageFormat
            )
            Spacer(modifier = Modifier.height(8.dp))
        },
        buttons = {
            BottomButtonsBlock(
                targetState = (viewModel.uris.isEmpty()) to isPortrait,
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = {
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
                },
                actions = {
                    if (isPortrait) it()
                }
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
        uris = viewModel.uris,
        selectedUri = viewModel.selectedUri,
        onUriPicked = { uri ->
            viewModel.setUri(uri = uri) {
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

    CompareSheet(
        data = viewModel.internalBitmap to viewModel.previewBitmap,
        visible = showCompareSheet
    )

    ZoomModalSheet(
        data = viewModel.selectedUri,
        visible = showZoomSheet,
        transformations = transformations
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