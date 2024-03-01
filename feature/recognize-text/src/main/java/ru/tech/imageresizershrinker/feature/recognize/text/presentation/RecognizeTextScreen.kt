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

package ru.tech.imageresizershrinker.feature.recognize.text.presentation

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.utils.readableByteCount
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.CropSmall
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.theme.onMixedContainer
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.copyToClipboard
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.shareText
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageTransformBar
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalImageLoader
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.utils.notNullAnd
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionType
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.DownloadLanguageDialog
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.FilterSelectionBar
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.ModelTypeSelector
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.OCRTextPreviewItem
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.RecognitionTypeSelector
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.RecognizeLanguageSelector
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.UiDownloadData
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.toUi
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.viewModel.RecognizeTextViewModel
import ru.tech.imageresizershrinker.feature.single_edit.presentation.components.CropEditOption


@Composable
fun RecognizeTextScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: RecognizeTextViewModel = hiltViewModel()
) {
    val isHaveText = viewModel.recognitionData?.text.notNullAnd { it.isNotEmpty() }

    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current

    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val context = LocalContext.current

    val confettiController = LocalConfettiController.current
    val toastHostState = LocalToastHostState.current

    var downloadDialogData by rememberSaveable {
        mutableStateOf<List<UiDownloadData>>(emptyList())
    }

    val startRecognition = {
        viewModel.startRecognition(
            onError = {
                scope.launch {
                    toastHostState.showError(context, it)
                }
            },
            onRequestDownload = { data ->
                downloadDialogData = data.map { it.toUi() }
            }
        )
    }

    LaunchedEffect(uriState) {
        uriState?.let {
            viewModel.updateUri(it, startRecognition)
        }
    }

    val imageLoader = LocalImageLoader.current
    LaunchedEffect(viewModel.uri) {
        viewModel.uri?.let {
            if (allowChangeColor) {
                imageLoader.execute(
                    ImageRequest.Builder(context).data(it).build()
                ).drawable?.toBitmap()?.let { bitmap ->
                    themeState.updateColorByImage(bitmap)
                }
            }
        }
    }

    LaunchedEffect(viewModel.previewBitmap, viewModel.filtersAdded) {
        if (viewModel.previewBitmap != null) {
            startRecognition()
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { list ->
            list.firstOrNull()?.let {
                viewModel.updateUri(it, startRecognition)
            }
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    val isPortrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val copyText: () -> Unit = {
        viewModel.recognitionData?.text?.let {
            context.copyToClipboard(
                label = context.getString(R.string.recognize_text),
                value = it
            )
            scope.launch {
                toastHostState.showToast(
                    icon = Icons.Rounded.ContentCopy,
                    message = context.getString(R.string.copied),
                )
            }
        }
    }

    val shareText: () -> Unit = {
        viewModel.recognitionData?.text?.let {
            context.shareText(it)
            scope.launch {
                confettiController.showEmpty()
            }
        }
    }

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }

    var showCropper by rememberSaveable { mutableStateOf(false) }

    AdaptiveLayoutScreen(
        title = {
            AnimatedContent(
                targetState = viewModel.recognitionData
            ) { data ->
                TopAppBarTitle(
                    title = if (data == null) {
                        stringResource(R.string.recognize_text)
                    } else {
                        stringResource(
                            R.string.accuracy,
                            data.accuracy
                        )
                    },
                    input = viewModel.uri,
                    isLoading = viewModel.isTextLoading,
                    size = null
                )
            }
        },
        onGoBack = onGoBack,
        topAppBarPersistentActions = {
            if (viewModel.uri == null) TopAppBarEmoji()
            ZoomButton(
                onClick = { showZoomSheet.value = true },
                visible = viewModel.uri != null,
            )
        },
        actions = {
            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
                onClick = shareText,
                enabled = isHaveText
            ) {
                Icon(Icons.Outlined.Share, null)
            }
        },
        imagePreview = {
            Box(
                modifier = Modifier
                    .container()
                    .padding(4.dp)
                    .animateContentSize(),
                contentAlignment = Alignment.Center
            ) {
                Picture(
                    model = viewModel.previewBitmap,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.aspectRatio(
                        viewModel.previewBitmap?.let {
                            it.width.toFloat() / it.height
                        } ?: 1f
                    ),
                    transformations = viewModel.getTransformations(),
                    shape = MaterialTheme.shapes.medium,
                    isLoadingFromDifferentPlace = viewModel.isImageLoading
                )
            }
        },
        controls = {
            val text = viewModel.recognitionData?.text?.takeIf {
                it.isNotEmpty()
            } ?: stringResource(R.string.picture_has_no_text)

            ImageTransformBar(
                onRotateLeft = viewModel::rotateBitmapLeft,
                onFlip = viewModel::flipImage,
                onRotateRight = viewModel::rotateBitmapRight
            ) {
                EnhancedIconButton(
                    containerColor = MaterialTheme.colorScheme.mixedContainer,
                    contentColor = MaterialTheme.colorScheme.onMixedContainer,
                    onClick = {
                        showCropper = true
                    }
                ) {
                    Icon(Icons.Rounded.CropSmall, null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            FilterSelectionBar(
                addedFilters = viewModel.filtersAdded,
                onContrastClick = viewModel::toggleContrastFilter,
                onThresholdClick = viewModel::toggleThresholdFilter,
                onSharpnessClick = viewModel::toggleSharpnessFilter
            )
            Spacer(modifier = Modifier.height(16.dp))
            RecognizeLanguageSelector(
                currentRecognitionType = viewModel.recognitionType,
                value = viewModel.selectedLanguages,
                availableLanguages = viewModel.languages,
                onValueChange = { codeList, type ->
                    viewModel.onLanguagesSelected(codeList)
                    viewModel.setRecognitionType(type)
                    startRecognition()
                },
                onDeleteLanguage = { language, types ->
                    viewModel.deleteLanguage(language, types) {
                        startRecognition()
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OCRTextPreviewItem(
                text = text,
                isLoading = viewModel.isTextLoading,
                loadingProgress = viewModel.textLoadingProgress,
                accuracy = viewModel.recognitionData?.accuracy ?: 0
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecognitionTypeSelector(
                value = viewModel.recognitionType,
                onValueChange = { recognitionType ->
                    viewModel.setRecognitionType(recognitionType)
                    startRecognition()
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ModelTypeSelector(
                value = viewModel.segmentationMode,
                onValueChange = {
                    viewModel.setSegmentationMode(it)
                    startRecognition()
                }
            )
        },
        buttons = {
            BottomButtonsBlock(
                targetState = (viewModel.uri == null) to isPortrait,
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = copyText,
                primaryButtonIcon = Icons.Rounded.CopyAll,
                isPrimaryButtonVisible = isHaveText,
                actions = {
                    if (isPortrait) it()
                }
            )
        },
        noDataControls = {
            ImageNotPickedWidget(onPickImage = pickImage)
        },
        isPortrait = isPortrait,
        canShowScreenData = viewModel.uri != null
    )

    ZoomModalSheet(
        data = viewModel.uri,
        visible = showZoomSheet,
        transformations = viewModel.getTransformations()
    )

    if (downloadDialogData.isNotEmpty()) {
        var progress by rememberSaveable(downloadDialogData) {
            mutableFloatStateOf(0f)
        }
        var dataRemaining by rememberSaveable(downloadDialogData) {
            mutableStateOf("")
        }
        DownloadLanguageDialog(
            downloadDialogData = downloadDialogData,
            onDownloadRequest = { downloadData ->
                viewModel.downloadTrainData(
                    type = downloadData.firstOrNull()?.type
                        ?: RecognitionType.Standard,
                    languageCode = downloadDialogData.joinToString(separator = "+") { it.languageCode },
                    onProgress = { p, size ->
                        dataRemaining = readableByteCount(size)
                        progress = p
                    },
                    onComplete = {
                        downloadDialogData = emptyList()
                        scope.launch {
                            confettiController.showEmpty()
                        }
                        startRecognition()
                    }
                )
            },
            downloadProgress = progress,
            dataRemaining = dataRemaining,
            onNoConnection = {
                downloadDialogData = emptyList()
                scope.launch {
                    toastHostState.showToast(
                        message = context.getString(R.string.no_connection),
                        icon = Icons.Outlined.SignalCellularConnectedNoInternet0Bar,
                        duration = ToastDuration.Long
                    )
                }
            },
            onDismiss = {
                downloadDialogData = emptyList()
            }
        )
    }

    CropEditOption(
        visible = showCropper,
        onDismiss = { showCropper = false },
        useScaffold = isPortrait,
        bitmap = viewModel.previewBitmap,
        onGetBitmap = viewModel::updateBitmap,
        cropProperties = viewModel.cropProperties,
        setCropAspectRatio = viewModel::setCropAspectRatio,
        setCropMask = viewModel::setCropMask,
        selectedAspectRatio = viewModel.selectedAspectRatio,
        loadImage = viewModel::loadImage
    )
}