package ru.tech.imageresizershrinker.presentation.batch_resize_screen

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Save
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
import androidx.compose.ui.draw.clip
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
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.presentation.batch_resize_screen.components.SaveExifWidget
import ru.tech.imageresizershrinker.presentation.batch_resize_screen.viewModel.BatchResizeViewModel
import ru.tech.imageresizershrinker.presentation.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.utils.coil.filters.SaturationFilter
import ru.tech.imageresizershrinker.presentation.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.utils.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.presentation.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.presentation.widget.buttons.TelegramButton
import ru.tech.imageresizershrinker.presentation.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.widget.controls.ImageTransformBar
import ru.tech.imageresizershrinker.presentation.widget.controls.PresetWidget
import ru.tech.imageresizershrinker.presentation.widget.controls.QualityWidget
import ru.tech.imageresizershrinker.presentation.widget.controls.ResizeGroup
import ru.tech.imageresizershrinker.presentation.widget.controls.ResizeImageField
import ru.tech.imageresizershrinker.presentation.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.presentation.widget.image.ImageContainer
import ru.tech.imageresizershrinker.presentation.widget.image.ImageCounter
import ru.tech.imageresizershrinker.presentation.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.widget.other.imageStickyHeader
import ru.tech.imageresizershrinker.presentation.widget.other.showError
import ru.tech.imageresizershrinker.presentation.widget.sheets.CompareSheet
import ru.tech.imageresizershrinker.presentation.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.presentation.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.presentation.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.presentation.widget.utils.isExpanded
import ru.tech.imageresizershrinker.presentation.widget.utils.middleImageState
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.applyPresetBy
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.decodeBitmapByUri
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.getBitmapByUri
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.shareBitmaps
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.utils.storage.Picker
import ru.tech.imageresizershrinker.utils.storage.localImagePickerMode
import ru.tech.imageresizershrinker.utils.storage.rememberImagePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchResizeScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: BatchResizeViewModel = hiltViewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHost.current
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
            viewModel.updateUris(it)
            context.decodeBitmapByUri(
                uri = it[0],
                onGetMimeType = viewModel::setMime,
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
            list.takeIf { it.isNotEmpty() }?.let {
                viewModel.updateUris(list)
                context.decodeBitmapByUri(
                    uri = it[0],
                    onGetMimeType = viewModel::setMime,
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
    val saveBitmaps: () -> Unit = {
        showSaveLoading = true
        viewModel.saveBitamps(
            getBitmap = { uri ->
                context.getBitmapByUri(uri)
            }
        ) { success, savingPath ->
            if (!success) context.requestStoragePermission()
            else {
                scope.launch {
                    toastHostState.showToast(
                        context.getString(
                            R.string.saved_to,
                            savingPath
                        ),
                        Icons.Rounded.Save
                    )
                }
                showConfetti()
            }
            showSaveLoading = false
        }
    }

    val focus = LocalFocusManager.current
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }
    var showPickImageFromUrisDialog by rememberSaveable { mutableStateOf(false) }

    val bitmapInfo = viewModel.bitmapInfo

    val imageInside =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    var imageState by remember { mutableStateOf(middleImageState()) }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState, canScroll = { imageState.isExpanded() && !showOriginal }
    )

    LaunchedEffect(imageState, showOriginal) {
        if (imageState.isExpanded() || showOriginal) {
            while (topAppBarState.heightOffset > topAppBarState.heightOffsetLimit) {
                topAppBarState.heightOffset -= 5f
                delay(1)
            }
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.bitmapInfo.haveChanges(viewModel.bitmap)) showExitDialog = true
        else onGoBack()
    }

    val imageBlock = @Composable {
        ImageContainer(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = { showPickImageFromUrisDialog = true }
                )
            },
            imageInside = imageInside,
            showOriginal = showOriginal,
            previewBitmap = viewModel.previewBitmap,
            originalBitmap = viewModel.bitmap,
            isLoading = viewModel.isLoading,
            shouldShowPreview = viewModel.shouldShowPreview
        )
    }

    val actions: @Composable RowScope.() -> Unit = {
        TelegramButton(
            enabled = viewModel.bitmap != null,
            isTelegramSpecs = viewModel.isTelegramSpecs,
            onClick = { viewModel.setTelegramSpecs() },
        )

        IconButton(
            onClick = {
                showSaveLoading = true
                context.shareBitmaps(
                    uris = viewModel.uris ?: emptyList(),
                    scope = viewModel.viewModelScope,
                    bitmapLoader = {
                        viewModel.proceedBitmap(
                            kotlin.runCatching {
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
            enabled = viewModel.previewBitmap != null
        ) {
            Icon(Icons.Outlined.Share, null)
        }

        val interactionSource = remember { MutableInteractionSource() }
        IconButton(
            enabled = viewModel.bitmap != null,
            onClick = { showResetDialog = true }
        ) {
            Icon(
                imageVector = Icons.Rounded.RestartAlt,
                contentDescription = null
            )
        }
        if (viewModel.bitmap != null && viewModel.bitmap?.canShow() == true) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .indication(
                        interactionSource,
                        LocalIndication.current
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                val press = PressInteraction.Press(it)
                                interactionSource.emit(press)
                                if (viewModel.bitmap?.canShow() == true) {
                                    showOriginal = true
                                }
                                tryAwaitRelease()
                                showOriginal = false
                                interactionSource.emit(
                                    PressInteraction.Release(
                                        press
                                    )
                                )
                            }
                        )
                    }
            ) {
                Icon(
                    Icons.Rounded.History,
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp)
                )
            }
        } else {
            IconButton(
                enabled = false,
                onClick = {}
            ) { Icon(Icons.Rounded.History, null) }
        }
    }

    val buttons = @Composable {
        BottomButtonsBlock(
            targetState = (viewModel.uris.isNullOrEmpty()) to imageInside,
            onPickImage = pickImage,
            onSaveBitmap = saveBitmaps,
            actions = actions
        )
    }

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }

    val zoomButton = @Composable {
        AnimatedVisibility(viewModel.bitmap != null && viewModel.shouldShowPreview) {
            IconButton(
                onClick = {
                    showZoomSheet.value = true
                }
            ) {
                Icon(Icons.Rounded.ZoomIn, null)
            }
        }
    }

    val showCompareSheet = rememberSaveable { mutableStateOf(false) }
    val compareButton = @Composable {
        AnimatedVisibility(viewModel.bitmap != null && viewModel.shouldShowPreview) {
            IconButton(
                onClick = {
                    showCompareSheet.value = true
                }
            ) {
                Icon(Icons.Rounded.Compare, null)
            }
        }
    }

    CompareSheet(
        data = viewModel.bitmap to viewModel.previewBitmap,
        visible = showCompareSheet
    )

    ZoomModalSheet(
        bitmap = viewModel.previewBitmap,
        visible = showZoomSheet
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
                            title = stringResource(R.string.batch_resize),
                            bitmap = viewModel.bitmap,
                            isLoading = viewModel.isLoading,
                            size = viewModel.bitmapInfo.sizeInBytes.toLong()
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    actions = {
                        if (viewModel.bitmap == null) TopAppBarEmoji()
                        compareButton()
                        zoomButton()
                        if (!imageInside && !viewModel.uris.isNullOrEmpty()) actions()
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
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
                            expanded = showOriginal,
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
                                    ImageTransformBar(
                                        onRotateLeft = viewModel::rotateLeft,
                                        onFlip = viewModel::flip,
                                        onRotateRight = viewModel::rotateRight
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    PresetWidget(
                                        selectedPreset = viewModel.presetSelected,
                                        onPresetSelected = {
                                            viewModel.setBitmapInfo(
                                                it.applyPresetBy(
                                                    viewModel.bitmap,
                                                    viewModel.bitmapInfo
                                                )
                                            )
                                        }
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    SaveExifWidget(
                                        selected = viewModel.keepExif,
                                        onCheckedChange = { viewModel.setKeepExif(!viewModel.keepExif) }
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    ResizeImageField(
                                        bitmapInfo = bitmapInfo,
                                        bitmap = viewModel.bitmap,
                                        onHeightChange = viewModel::updateHeight,
                                        onWidthChange = viewModel::updateWidth,
                                        showWarning = viewModel.showWarning
                                    )
                                    if (bitmapInfo.mimeType !is MimeType.Png) {
                                        Spacer(Modifier.height(8.dp))
                                    }
                                    QualityWidget(
                                        visible = bitmapInfo.mimeType !is MimeType.Png,
                                        enabled = viewModel.bitmap != null,
                                        quality = bitmapInfo.quality.coerceIn(0f, 100f),
                                        onQualityChange = viewModel::setQuality
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    ExtensionGroup(
                                        enabled = viewModel.bitmap != null,
                                        mimeType = bitmapInfo.mimeType,
                                        onMimeChange = viewModel::setMime
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    ResizeGroup(
                                        enabled = viewModel.bitmap != null,
                                        resizeType = bitmapInfo.resizeType,
                                        onResizeChange = viewModel::setResizeType
                                    )
                                } else if (!viewModel.isLoading) {
                                    ImageNotPickedWidget(onPickImage = pickImage)
                                }
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

            ResetDialog(
                visible = showResetDialog,
                onDismiss = { showResetDialog = false },
                onReset = viewModel::resetValues
            )

            PickImageFromUrisSheet(
                transformations = listOf(
                    ru.tech.imageresizershrinker.presentation.utils.coil.BitmapInfoTransformation(
                        bitmapInfo = viewModel.bitmapInfo,
                        preset = viewModel.presetSelected
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

            if (showSaveLoading) {
                LoadingDialog(viewModel.done, viewModel.uris?.size ?: 1)
            }

            BackHandler(onBack = onBack)
        }
    }
}