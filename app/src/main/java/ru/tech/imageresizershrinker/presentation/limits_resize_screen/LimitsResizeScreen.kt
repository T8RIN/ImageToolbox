package ru.tech.imageresizershrinker.presentation.limits_resize_screen

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import coil.size.Size
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.batch_resize_screen.components.SaveExifWidget
import ru.tech.imageresizershrinker.presentation.limits_resize_screen.viewModel.LimitsResizeViewModel
import ru.tech.imageresizershrinker.presentation.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.utils.coil.filters.SaturationFilter
import ru.tech.imageresizershrinker.domain.model.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.decodeBitmapByUri
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.fileSize
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.getBitmapByUri
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.shareBitmaps
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.failedToSaveImages
import ru.tech.imageresizershrinker.presentation.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.utils.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.utils.storage.LocalFileController
import ru.tech.imageresizershrinker.utils.storage.Picker
import ru.tech.imageresizershrinker.utils.storage.localImagePickerMode
import ru.tech.imageresizershrinker.utils.storage.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.presentation.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.widget.controls.ResizeImageField
import ru.tech.imageresizershrinker.presentation.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.widget.image.ImageContainer
import ru.tech.imageresizershrinker.presentation.widget.image.ImageCounter
import ru.tech.imageresizershrinker.presentation.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.widget.other.imageStickyHeader
import ru.tech.imageresizershrinker.presentation.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.presentation.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.presentation.widget.other.showError
import ru.tech.imageresizershrinker.presentation.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.presentation.widget.utils.isExpanded
import ru.tech.imageresizershrinker.presentation.widget.utils.middleImageState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitsResizeScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: LimitsResizeViewModel = hiltViewModel()
) {
    val settingsState = LocalSettingsState.current

    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHost.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiController.showEmpty()
        }
    }

    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.updateUris(uris)
            context.decodeBitmapByUri(
                uri = uris[0],
                onGetMimeType = {
                    viewModel.setMime(it)
                },
                onGetExif = {},
                onGetBitmap = viewModel::updateBitmap,
                onError = {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            )
        }
    }
    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor) {
                themeState.updateColorByImage(
                    SaturationFilter(context, 2f).transform(it, Size.ORIGINAL)
                )
            }
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let { uris ->
                viewModel.updateUris(list)
                context.decodeBitmapByUri(
                    uri = uris[0],
                    onGetMimeType = {
                        viewModel.setMime(it)
                    },
                    onGetExif = {},
                    onGetBitmap = viewModel::updateBitmap,
                    onError = {
                        scope.launch {
                            toastHostState.showError(context, it)
                        }
                    }
                )
            }
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    var showSaveLoading by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.canSave) showExitDialog = true
        else onGoBack()
    }

    val fileController = LocalFileController.current
    val saveBitmaps: () -> Unit = {
        showSaveLoading = true
        viewModel.saveBitmaps(
            fileController = fileController,
            getBitmap = { uri ->
                context.decodeBitmapByUri(uri)
            },
        ) { failed ->
            context.failedToSaveImages(
                scope = scope,
                failed = failed,
                done = viewModel.done,
                toastHostState = toastHostState,
                savingPathString = fileController.savingPath,
                showConfetti = showConfetti
            )
            showSaveLoading = false
        }
    }

    val focus = LocalFocusManager.current
    var showPickImageFromUrisDialog by rememberSaveable { mutableStateOf(false) }

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

    val imageBlock = @Composable {
        ImageContainer(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = { showPickImageFromUrisDialog = true }
                )
            },
            imageInside = imageInside,
            showOriginal = false,
            previewBitmap = viewModel.previewBitmap,
            originalBitmap = viewModel.bitmap,
            isLoading = viewModel.isLoading,
            shouldShowPreview = true
        )
    }

    val showSheet = rememberSaveable { mutableStateOf(false) }
    val zoomButton = @Composable {
        AnimatedVisibility(viewModel.bitmap != null) {
            IconButton(
                onClick = {
                    showSheet.value = true
                }
            ) {
                Icon(Icons.Rounded.ZoomIn, null)
            }
        }
    }

    val actions: @Composable RowScope.() -> Unit = {
        if (viewModel.previewBitmap != null) {
            IconButton(
                onClick = {
                    showSaveLoading = true
                    context.shareBitmaps(
                        uris = viewModel.uris ?: emptyList(),
                        scope = viewModel.viewModelScope,
                        bitmapLoader = {
                            viewModel.proceedBitmap(
                                bitmapResult = kotlin.runCatching {
                                    context.decodeBitmapByUri(it).first
                                }
                            )
                        },
                        onProgressChange = {
                            if (it == -1) {
                                showSaveLoading = false
                                viewModel.setProgress(0)
                                showConfetti()
                            } else {
                                viewModel.setProgress(it)
                            }
                        }
                    )
                },
                enabled = viewModel.canSave
            ) {
                Icon(Icons.Outlined.Share, null)
            }
        }
        zoomButton()
    }

    val buttons = @Composable {
        BottomButtonsBlock(
            targetState = (viewModel.uris.isNullOrEmpty()) to imageInside,
            onPickImage = pickImage,
            onSaveBitmap = saveBitmaps,
            actions = actions
        )
    }

    ZoomModalSheet(
        bitmap = viewModel.previewBitmap,
        visible = showSheet
    )

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focus.clearFocus()
                    }
                )
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
                        TopAppBarTitle(
                            title = stringResource(R.string.limits_resize),
                            bitmap = viewModel.bitmap,
                            isLoading = viewModel.isLoading,
                            size = viewModel.selectedUri?.fileSize(LocalContext.current) ?: 0L
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = onBack
                        ) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        if (viewModel.bitmap == null) {
                            TopAppBarEmoji()
                        }
                        if (!imageInside) actions()
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (!imageInside && viewModel.bitmap != null) {
                        Box(
                            Modifier
                                .weight(0.8f)
                                .padding(20.dp)
                        ) {
                            Box(Modifier.align(Alignment.Center)) {
                                imageBlock()
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                        )
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(
                            bottom = WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + WindowInsets.ime
                                .asPaddingValues()
                                .calculateBottomPadding() + (if (!imageInside && viewModel.bitmap != null) 20.dp else 100.dp),
                            top = if (viewModel.bitmap == null || !imageInside) 20.dp else 0.dp,
                            start = 20.dp,
                            end = 20.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clipToBounds()
                    ) {
                        imageStickyHeader(
                            visible = imageInside && viewModel.bitmap != null,
                            imageState = imageState,
                            onStateChange = { imageState = it },
                            imageBlock = imageBlock
                        )
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .navBarsLandscapePadding(viewModel.bitmap == null),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (imageInside && viewModel.bitmap == null) imageBlock()
                                if (viewModel.bitmap != null) {
                                    ImageCounter(
                                        imageCount = viewModel.uris?.size?.takeIf { it > 1 && !viewModel.isLoading },
                                        onRepick = {
                                            showPickImageFromUrisDialog = true
                                        }
                                    )
                                    ResizeImageField(
                                        bitmapInfo = viewModel.bitmapInfo,
                                        bitmap = viewModel.bitmap,
                                        onWidthChange = viewModel::updateWidth,
                                        onHeightChange = viewModel::updateHeight
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    SaveExifWidget(
                                        selected = viewModel.keepExif,
                                        onCheckedChange = { viewModel.setKeepExif(!viewModel.keepExif) }
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    ExtensionGroup(
                                        enabled = viewModel.bitmap != null,
                                        mimeType = viewModel.bitmapInfo.mimeTypeInt,
                                        onMimeChange = {
                                            viewModel.setMime(it)
                                        }
                                    )
                                } else if (!viewModel.isLoading) {
                                    ImageNotPickedWidget(onPickImage = pickImage)
                                    Spacer(Modifier.size(8.dp))
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }

                    if (!imageInside && viewModel.bitmap != null) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                                .padding(start = 20.dp)
                        )
                        buttons()
                    }
                }
            }

            if (imageInside || viewModel.bitmap == null) {
                Box(
                    modifier = Modifier.align(settingsState.fabAlignment)
                ) {
                    buttons()
                }
            }

            if (showSaveLoading) {
                LoadingDialog(viewModel.done, viewModel.uris?.size ?: 1)
            }

            PickImageFromUrisSheet(
                transformations = listOf(
                    ru.tech.imageresizershrinker.presentation.utils.coil.BitmapInfoTransformation(
                        bitmapInfo = BitmapInfo(),
                        preset = 100
                    )
                ),
                visible = showPickImageFromUrisDialog,
                uris = viewModel.uris,
                selectedUri = viewModel.selectedUri,
                onDismiss = {
                    showPickImageFromUrisDialog = false
                },
                onUriPicked = { uri ->
                    try {
                        viewModel.setBitmap(
                            loader = {
                                context.getBitmapByUri(uri)
                            },
                            uri = uri
                        )
                    } catch (e: Exception) {
                        scope.launch {
                            toastHostState.showError(context, e)
                        }
                    }
                },
                onUriRemoved = { uri ->
                    viewModel.updateUrisSilently(
                        removedUri = uri,
                        loader = {
                            context.getBitmapByUri(it)
                        }
                    )
                },
                columns = if (imageInside) 2 else 4,
            )

            ExitWithoutSavingDialog(
                onExit = onGoBack,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )

            BackHandler(onBack = onBack)
        }
    }
}