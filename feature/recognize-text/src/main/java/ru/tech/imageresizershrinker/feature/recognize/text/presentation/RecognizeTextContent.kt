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

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.utils.notNullAnd
import ru.tech.imageresizershrinker.core.domain.utils.readableByteCount
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.CropSmall
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.theme.onMixedContainer
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.copyToClipboard
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.safeAspectRatio
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseFileSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalImageLoader
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageTransformBar
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.LinkPreviewList
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionType
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.DownloadLanguageDialog
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.FilterSelectionBar
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.ModelTypeSelector
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.OCRTextPreviewItem
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.OcrEngineModeSelector
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.RecognitionTypeSelector
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.RecognizeLanguageSelector
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.TessParamsSelector
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.UiDownloadData
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.toUi
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.screenLogic.RecognizeTextComponent
import ru.tech.imageresizershrinker.feature.single_edit.presentation.components.CropEditOption


@Composable
fun RecognizeTextContent(
    onGoBack: () -> Unit,
    component: RecognizeTextComponent
) {
    val text = component.recognitionData?.text?.takeIf {
        it.isNotEmpty()
    }
    var editedText by rememberSaveable(text, component.isTextLoading) {
        mutableStateOf(text)
    }
    val isHaveText = editedText.notNullAnd { it.isNotEmpty() }

    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current

    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val context = LocalContext.current

    val confettiHostState = LocalConfettiHostState.current
    val toastHostState = LocalToastHostState.current

    var downloadDialogData by rememberSaveable {
        mutableStateOf<List<UiDownloadData>>(emptyList())
    }

    val startRecognition = {
        component.startRecognition(
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

    LaunchedEffect(component.initialUri) {
        component.initialUri?.let {
            component.updateUri(it, startRecognition)
        }
    }

    val imageLoader = LocalImageLoader.current
    LaunchedEffect(component.uri) {
        component.uri?.let {
            if (allowChangeColor) {
                imageLoader.execute(
                    ImageRequest.Builder(context).data(it).build()
                ).drawable?.toBitmap()?.let { bitmap ->
                    themeState.updateColorByImage(bitmap)
                }
            }
        }
    }

    LaunchedEffect(component.previewBitmap, component.filtersAdded) {
        if (component.previewBitmap != null) {
            startRecognition()
        }
    }

    val imagePickerMode = localImagePickerMode(Picker.Single)

    val imagePicker = rememberImagePicker(
        mode = imagePickerMode
    ) { list ->
        list.firstOrNull()?.let {
            component.updateUri(it, startRecognition)
        }
    }

    val captureImageLauncher = rememberImagePicker(
        mode = ImagePickerMode.CameraCapture
    ) { list ->
        list.firstOrNull()?.let {
            component.updateUri(it, startRecognition)
        }
    }

    val captureImage = captureImageLauncher::pickImage

    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = component.initialUri != null
    )

    val isPortrait by isPortraitOrientationAsState()

    val copyText: () -> Unit = {
        editedText?.let {
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
        component.shareText(editedText) {
            scope.launch {
                confettiHostState.showConfetti()
            }
        }
    }

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }

    var showCropper by rememberSaveable { mutableStateOf(false) }

    val exportLanguagesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip"),
        onResult = {
            it?.let { uri ->
                component.exportLanguagesTo(uri) { result ->
                    context.parseFileSaveResult(
                        saveResult = result,
                        onSuccess = {
                            confettiHostState.showConfetti()
                        },
                        toastHostState = toastHostState,
                        scope = scope
                    )
                }
            }
        }
    )

    val importLanguagesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                component.importLanguagesFrom(
                    uri = uri,
                    onSuccess = {
                        scope.launch {
                            confettiHostState.showConfetti()
                        }
                        scope.launch {
                            toastHostState.showToast(
                                message = context.getString(R.string.languages_imported),
                                icon = Icons.Outlined.Language
                            )
                        }
                        startRecognition()
                    },
                    onFailure = {
                        scope.launch {
                            toastHostState.showError(context, it)
                        }
                    }
                )
            }
        }
    )

    val onExportLanguages: () -> Unit = {
        exportLanguagesPicker.launch(component.generateExportFilename())
    }

    val onImportLanguages: () -> Unit = {
        importLanguagesPicker.launch(arrayOf("application/zip"))
    }

    val saveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            component.saveContentToTxt(
                uri = uri,
                onResult = { result ->
                    context.parseFileSaveResult(
                        saveResult = result,
                        onSuccess = {
                            confettiHostState.showConfetti()
                        },
                        toastHostState = toastHostState,
                        scope = scope
                    )
                }
            )
        }
    }

    val saveText: () -> Unit = {
        saveLauncher.launch(component.generateTextFilename())
    }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = true,
        title = {
            AnimatedContent(
                targetState = component.recognitionData
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
                    input = component.uri,
                    isLoading = component.isTextLoading,
                    size = null
                )
            }
        },
        onGoBack = onGoBack,
        topAppBarPersistentActions = {
            if (component.uri == null) TopAppBarEmoji()
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.uri != null,
            )
        },
        actions = {
            ShareButton(
                onShare = shareText,
                enabled = isHaveText
            )
            EnhancedIconButton(
                onClick = saveText,
                enabled = !text.isNullOrEmpty()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Save,
                    contentDescription = null
                )
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
                    model = component.previewBitmap,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.aspectRatio(
                        component.previewBitmap?.safeAspectRatio ?: 1f
                    ),
                    transformations = component.getTransformations(),
                    shape = MaterialTheme.shapes.medium,
                    isLoadingFromDifferentPlace = component.isImageLoading
                )
            }
        },
        controls = {
            ImageTransformBar(
                onRotateLeft = component::rotateBitmapLeft,
                onFlip = component::flipImage,
                onRotateRight = component::rotateBitmapRight
            ) {
                if (imagePickerMode != ImagePickerMode.CameraCapture) {
                    EnhancedIconButton(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        onClick = captureImage
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CameraAlt,
                            contentDescription = stringResource(R.string.camera)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                }
                EnhancedIconButton(
                    containerColor = MaterialTheme.colorScheme.mixedContainer,
                    contentColor = MaterialTheme.colorScheme.onMixedContainer,
                    onClick = {
                        showCropper = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CropSmall,
                        contentDescription = stringResource(R.string.crop)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            FilterSelectionBar(
                addedFilters = component.filtersAdded,
                onContrastClick = component::toggleContrastFilter,
                onThresholdClick = component::toggleThresholdFilter,
                onSharpnessClick = component::toggleSharpnessFilter
            )
            Spacer(modifier = Modifier.height(16.dp))
            RecognizeLanguageSelector(
                currentRecognitionType = component.recognitionType,
                value = component.selectedLanguages,
                availableLanguages = component.languages,
                onValueChange = { codeList, type ->
                    component.onLanguagesSelected(codeList)
                    component.setRecognitionType(type)
                    startRecognition()
                },
                onDeleteLanguage = { language, types ->
                    component.deleteLanguage(
                        language = language,
                        types = types,
                        onSuccess = startRecognition
                    )
                },
                onImportLanguages = onImportLanguages,
                onExportLanguages = onExportLanguages
            )
            LinkPreviewList(
                text = editedText ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OCRTextPreviewItem(
                text = editedText,
                onTextEdit = {
                    if (editedText != null) {
                        editedText = it
                    }
                },
                isLoading = component.isTextLoading,
                loadingProgress = component.textLoadingProgress,
                accuracy = component.recognitionData?.accuracy ?: 0
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecognitionTypeSelector(
                value = component.recognitionType,
                onValueChange = { recognitionType ->
                    component.setRecognitionType(recognitionType)
                    startRecognition()
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ModelTypeSelector(
                value = component.segmentationMode,
                onValueChange = {
                    component.setSegmentationMode(it)
                    startRecognition()
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OcrEngineModeSelector(
                value = component.ocrEngineMode,
                onValueChange = {
                    component.setOcrEngineMode(it)
                    startRecognition()
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TessParamsSelector(
                value = component.params,
                onValueChange = {
                    component.updateParams(it)
                    startRecognition()
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        buttons = {
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                targetState = (component.uri == null) to isPortrait,
                onSecondaryButtonClick = pickImage,
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                },
                onPrimaryButtonClick = copyText,
                primaryButtonIcon = Icons.Rounded.CopyAll,
                isPrimaryButtonVisible = isHaveText,
                actions = {
                    if (isPortrait) it()
                }
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
        isPortrait = isPortrait,
        canShowScreenData = component.uri != null
    )

    ZoomModalSheet(
        data = component.uri,
        visible = showZoomSheet,
        onDismiss = {
            showZoomSheet = false
        },
        transformations = component.getTransformations()
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
                component.downloadTrainData(
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
                            confettiHostState.showConfetti()
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
        bitmap = component.previewBitmap,
        onGetBitmap = component::updateBitmap,
        cropProperties = component.cropProperties,
        setCropAspectRatio = component::setCropAspectRatio,
        setCropMask = component::setCropMask,
        selectedAspectRatio = component.selectedAspectRatio,
        loadImage = component::loadImage
    )

    LoadingDialog(
        visible = component.isExporting,
        canCancel = false
    )
}