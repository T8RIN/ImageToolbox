package ru.tech.imageresizershrinker.feature.recognize.text.presentation

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.ModelTraining
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
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
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.LanguageSelector
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.components.UiDownloadData
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

    var showDownloadDialogData by rememberSaveable {
        mutableStateOf<UiDownloadData?>(null)
    }

    val startRecognition = {
        viewModel.startRecognition(
            onError = {
                scope.launch {
                    toastHostState.showError(context, it)
                }
            },
            onRequestDownload = { type, lang ->
                showDownloadDialogData = UiDownloadData(type, lang)
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
        }
    }

    val shareText: () -> Unit = {
        viewModel.recognitionData?.text?.let {
            context.shareText(it)
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
                contentScale = ContentScale.Inside,
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
        val text = viewModel.recognitionData?.text ?: stringResource(R.string.picture_has_no_text)

        LanguageSelector(
            value = viewModel.selectedLanguage,
            availableLanguages = viewModel.languages,
            isLanguagesLoading = viewModel.isLanguagesLoading,
            onValueChange = {
                viewModel.onLanguageSelected(it)
                startRecognition()
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .container(shape = RoundedCornerShape(24.dp))
                .animateContentSize(),
            contentAlignment = Alignment.Center
        ) {
            ToggleGroupButton(
                modifier = Modifier.padding(8.dp),
                enabled = true,
                items = RecognitionType.entries.map { it.translatedName },
                selectedIndex = RecognitionType.entries.indexOf(viewModel.recognitionType),
                title = stringResource(id = R.string.recognition_type),
                indexChanged = {
                    val recognitionType = when (it) {
                        0 -> RecognitionType.Fast
                        2 -> RecognitionType.Best
                        else -> RecognitionType.Standard
                    }
                    viewModel.setRecognitionType(recognitionType)
                    startRecognition()
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedContent(targetState = viewModel.isTextLoading to text) { (loading, dataText) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .container(shape = RoundedCornerShape(24.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(24.dp),
                            color = MaterialTheme.colorScheme.tertiary.copy(0.5f),
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            strokeCap = StrokeCap.Round
                        )
                        CircularProgressIndicator(
                            modifier = Modifier.padding(24.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            strokeCap = StrokeCap.Round,
                            progress = {
                                viewModel.textLoadingProgress / 100f
                            }
                        )
                    }
                } else {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.accuracy,
                                viewModel.recognitionData?.accuracy ?: 0
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        SelectionContainer {
                            Text(dataText)
                        }
                    }
                }
            }
        }
    }

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }

    val zoomButton = @Composable {
        AnimatedVisibility(
            visible = viewModel.uri != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
                onClick = {
                    showZoomSheet.value = true
                }
            ) {
                Icon(Icons.Rounded.ZoomIn, null)
            }
        }
    }

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
                            TopAppBarTitle(
                                title = stringResource(R.string.recognize_text),
                                input = viewModel.uri,
                                isLoading = viewModel.isTextLoading,
                                size = null
                            )
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
                        zoomButton()
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

    if (showDownloadDialogData != null) {
        var downloadStarted by rememberSaveable(showDownloadDialogData) {
            mutableStateOf(false)
        }
        var progress by rememberSaveable(showDownloadDialogData) {
            mutableFloatStateOf(0f)
        }
        var dataRemaining by rememberSaveable(showDownloadDialogData) {
            mutableStateOf("")
        }
        if (!downloadStarted) {
            AlertDialog(
                modifier = Modifier.alertDialogBorder(),
                icon = { Icon(Icons.Outlined.ModelTraining, null) },
                title = { Text(stringResource(id = R.string.no_data)) },
                text = {
                    Text(
                        stringResource(
                            id = R.string.download_description,
                            showDownloadDialogData?.type?.displayName ?: "",
                            showDownloadDialogData?.language ?: ""
                        )
                    )
                },
                onDismissRequest = {},
                confirmButton = {
                    EnhancedButton(
                        onClick = {
                            if (context.isNetworkAvailable()) {
                                showDownloadDialogData?.let {
                                    viewModel.downloadTrainData(
                                        type = it.type,
                                        language = it.language,
                                        onProgress = { p, size ->
                                            dataRemaining = readableByteCount(size)
                                            progress = p
                                        },
                                        onComplete = {
                                            showDownloadDialogData = null
                                            scope.launch {
                                                confettiController.showEmpty()
                                            }
                                            startRecognition()
                                        }
                                    )
                                    downloadStarted = true
                                }
                            } else {
                                showDownloadDialogData = null
                                scope.launch {
                                    toastHostState.showToast(
                                        message = context.getString(R.string.no_connection),
                                        icon = Icons.Outlined.SignalCellularConnectedNoInternet0Bar,
                                        duration = ToastDuration.Long
                                    )
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.download))
                    }
                },
                dismissButton = {
                    EnhancedButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            showDownloadDialogData = null
                        }
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            )
        } else {
            BasicAlertDialog(onDismissRequest = {}) {
                Box(
                    Modifier.fillMaxSize()
                ) {
                    Loading(progress / 100) {
                        AutoSizeText(
                            text = dataRemaining,
                            maxLines = 1,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.width(it * 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private val RecognitionType.translatedName: String
    @Composable
    get() = when (this) {
        RecognitionType.Best -> stringResource(id = R.string.best)
        RecognitionType.Fast -> stringResource(id = R.string.fast)
        RecognitionType.Standard -> stringResource(id = R.string.standard)
    }

private fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    } else {
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }
}