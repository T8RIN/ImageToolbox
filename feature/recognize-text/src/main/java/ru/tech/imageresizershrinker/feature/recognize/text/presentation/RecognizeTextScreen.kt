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
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.utils.readableByteCount
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.copyToClipboard
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.shareText
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalImageLoader
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.utils.isExpanded
import ru.tech.imageresizershrinker.core.ui.widget.utils.middleImageState
import ru.tech.imageresizershrinker.core.ui.widget.utils.notNullAnd
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberAvailableHeight
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionType
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.DownloadLanguageDialog
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.ModelTypeSelector
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.OCRTextPreviewItem
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.RecognitionTypeSelector
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.RecognizeLanguageSelector
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.UiDownloadData
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.toUi
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.viewModel.RecognizeTextViewModel


@OptIn(ExperimentalMaterial3Api::class)
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
    val toastHostState = LocalToastHost.current

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
            viewModel.updateUri(it)
            startRecognition()
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

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { list ->
            list.firstOrNull()?.let {
                viewModel.updateUri(it)
                startRecognition()
            }
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    val imageInside =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    var imageState by remember { mutableStateOf(middleImageState()) }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState, canScroll = { !imageState.isExpanded() }
    )

    LaunchedEffect(imageState) {
        if (imageState.isExpanded()) {
            while (topAppBarState.heightOffset > topAppBarState.heightOffsetLimit) {
                topAppBarState.heightOffset -= 5f
                delay(1)
            }
        }
    }

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

    val imageBlock = @Composable {
        Box(
            modifier = Modifier
                .container()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Picture(
                model = viewModel.uri,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxHeight(),
                contentDescription = null,
                shape = MaterialTheme.shapes.medium
            )
        }
    }

    val actions: @Composable RowScope.() -> Unit = {
        EnhancedIconButton(
            containerColor = Color.Transparent,
            contentColor = LocalContentColor.current,
            enableAutoShadowAndBorder = false,
            onClick = shareText,
            enabled = isHaveText
        ) {
            Icon(Icons.Outlined.Share, null)
        }
    }

    val buttons = @Composable {
        BottomButtonsBlock(
            targetState = (viewModel.uri == null) to imageInside,
            onPickImage = pickImage,
            onPrimaryButtonClick = copyText,
            primaryButtonIcon = Icons.Rounded.CopyAll,
            isPrimaryButtonVisible = isHaveText,
            actions = {
                if (imageInside) actions()
            }
        )
    }

    val controls: @Composable () -> Unit = {
        val text = viewModel.recognitionData?.text?.takeIf {
            it.isNotEmpty()
        } ?: stringResource(R.string.picture_has_no_text)

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
    }

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }

    val focus = LocalFocusManager.current
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures {
                focus.clearFocus()
            }
        }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(Modifier.fillMaxSize()) {
                LargeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.drawHorizontalStroke(),
                    title = {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
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
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    navigationIcon = {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = onGoBack
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        if (viewModel.uri == null) TopAppBarEmoji()
                        ZoomButton(
                            onClick = { showZoomSheet.value = true },
                            visible = viewModel.uri != null,
                        )
                        if (!imageInside && viewModel.uri != null) actions()
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (!imageInside && viewModel.uri != null) {
                        Box(
                            Modifier
                                .container(
                                    RectangleShape,
                                    color = MaterialTheme.colorScheme.surfaceContainer
                                )
                                .weight(1.2f)
                                .padding(20.dp)
                        ) {
                            Box(Modifier.align(Alignment.Center)) {
                                imageBlock()
                            }
                        }
                    }
                    val internalHeight = rememberAvailableHeight(imageState)

                    LazyColumn(
                        contentPadding = PaddingValues(
                            bottom = WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + WindowInsets.ime
                                .asPaddingValues()
                                .calculateBottomPadding() + (if (!imageInside && viewModel.uri != null) 20.dp else 100.dp),
                            top = if (viewModel.uri == null || !imageInside) 20.dp else 0.dp,
                            start = 20.dp,
                            end = 20.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clipToBounds()
                    ) {
                        imageStickyHeader(
                            visible = imageInside && viewModel.uri != null,
                            internalHeight = internalHeight,
                            imageState = imageState,
                            onStateChange = { imageState = it },
                            imageBlock = imageBlock
                        )
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .navBarsLandscapePadding(viewModel.uri == null),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (viewModel.uri != null) {
                                    controls()
                                } else {
                                    ImageNotPickedWidget(onPickImage = pickImage)
                                }
                            }
                        }
                    }
                    if (!imageInside && viewModel.uri != null) {
                        buttons()
                    }
                }
            }

            if (imageInside || viewModel.uri == null) {
                Box(
                    modifier = Modifier.align(settingsState.fabAlignment)
                ) {
                    buttons()
                }
            }

            BackHandler { onGoBack() }
        }
    }

    ZoomModalSheet(
        data = viewModel.uri,
        visible = showZoomSheet
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
}