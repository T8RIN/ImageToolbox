package ru.tech.imageresizershrinker.presentation.single_edit_screen

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.size.Size
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.crop_screen.components.AspectRatioSelection
import ru.tech.imageresizershrinker.presentation.crop_screen.components.CropMaskSelection
import ru.tech.imageresizershrinker.presentation.crop_screen.components.Cropper
import ru.tech.imageresizershrinker.presentation.crop_screen.components.aspectRatios
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.transformation.filter.SaturationFilter
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.presentation.root.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ImageExtraTransformBar
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ImageTransformBar
import ru.tech.imageresizershrinker.presentation.root.widget.controls.PresetWidget
import ru.tech.imageresizershrinker.presentation.root.widget.controls.QualityWidget
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ResizeGroup
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ResizeImageField
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.presentation.root.widget.image.ImageContainer
import ru.tech.imageresizershrinker.presentation.root.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.root.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.presentation.root.widget.other.Loading
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.CompareSheet
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.presentation.root.widget.utils.isExpanded
import ru.tech.imageresizershrinker.presentation.root.widget.utils.middleImageState
import ru.tech.imageresizershrinker.presentation.single_edit_screen.components.EditExifSheet
import ru.tech.imageresizershrinker.presentation.single_edit_screen.viewModel.SingleEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleEditScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: SingleEditViewModel = hiltViewModel(),
) {
    val settingsState = LocalSettingsState.current
    val toastHostState = LocalToastHost.current
    val context = LocalContext.current as ComponentActivity
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
        uriState?.let {
            viewModel.setUri(it)
            viewModel.decodeBitmapByUri(
                uri = it,
                onGetMimeType = viewModel::setMime,
                onGetExif = viewModel::updateExif,
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

    val focus = LocalFocusManager.current
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val bitmapInfo = viewModel.imageInfo

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
                viewModel.setUri(it)
                viewModel.decodeBitmapByUri(
                    uri = it,
                    onGetMimeType = viewModel::setMime,
                    onGetExif = viewModel::updateExif,
                    onGetBitmap = viewModel::updateBitmap,
                    onError = {
                        scope.launch {
                            toastHostState.showError(context, it)
                        }
                    }
                )
            }
        }

    val pickImage = pickImageLauncher::pickImage

    val saveBitmap: () -> Unit = {
        viewModel.saveBitmap { saveResult ->
            parseSaveResult(
                saveResult = saveResult,
                onSuccess = showConfetti,
                toastHostState = toastHostState,
                scope = scope,
                context = context
            )
        }
    }

    val imageInside =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    var imageState by remember { mutableStateOf(middleImageState()) }

    val imageBlock = @Composable {
        ImageContainer(
            imageInside = imageInside,
            showOriginal = showOriginal,
            previewBitmap = viewModel.previewBitmap,
            originalBitmap = viewModel.bitmap,
            isLoading = viewModel.isImageLoading,
            shouldShowPreview = viewModel.shouldShowPreview
        )
    }

    val actions: @Composable RowScope.() -> Unit = {
        IconButton(
            onClick = {
                viewModel.shareBitmap(
                    onComplete = showConfetti
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
        if (viewModel.bitmap != null && viewModel.canShow()) {
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
                                if (viewModel.canShow()) {
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
            targetState = (viewModel.bitmap == null) to imageInside,
            onPickImage = pickImage,
            onSaveBitmap = saveBitmap,
            actions = actions
        )
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState, canScroll = { !imageState.isExpanded() && !showOriginal }
    )

    LaunchedEffect(imageState, showOriginal) {
        if (imageState.isExpanded() || showOriginal) {
            while (topAppBarState.heightOffset > topAppBarState.heightOffsetLimit) {
                topAppBarState.heightOffset -= 5f
                delay(1)
            }
        }
    }

    val showSheet = rememberSaveable { mutableStateOf(false) }
    val zoomButton = @Composable {
        AnimatedVisibility(viewModel.bitmap != null && viewModel.shouldShowPreview) {
            IconButton(
                onClick = {
                    showSheet.value = true
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
        visible = showSheet
    )

    val onBack = {
        if (bitmapInfo.haveChanges(viewModel.bitmap)) showExitDialog = true
        else onGoBack()
    }

    var showCropper by rememberSaveable { mutableStateOf(false) }

    val controls: @Composable () -> Unit = {
        val showEditExifDialog = rememberSaveable { mutableStateOf(false) }
        ImageTransformBar(
            onEditExif = { showEditExifDialog.value = true },
            onRotateLeft = viewModel::rotateLeft,
            onFlip = viewModel::flip,
            onRotateRight = viewModel::rotateRight
        )
        Spacer(Modifier.size(8.dp))
        ImageExtraTransformBar(
            onCrop = { showCropper = true },
            onFilter = {},
            onDraw = {},
            onEraseBackground = {}
        )
        Spacer(Modifier.size(16.dp))
        PresetWidget(
            selectedPreset = viewModel.presetSelected,
            includeTelegramOption = true,
            onPresetSelected = {
                viewModel.setPreset(it)
            }
        )
        Spacer(Modifier.size(8.dp))
        ResizeImageField(
            imageInfo = bitmapInfo,
            bitmap = viewModel.bitmap,
            onHeightChange = viewModel::updateHeight,
            onWidthChange = viewModel::updateWidth,
            showWarning = viewModel.showWarning
        )
        if (bitmapInfo.imageFormat.canChangeQuality) Spacer(
            Modifier.height(8.dp)
        )
        QualityWidget(
            visible = bitmapInfo.imageFormat.canChangeQuality,
            enabled = viewModel.bitmap != null,
            quality = bitmapInfo.quality.coerceIn(0f, 100f),
            onQualityChange = viewModel::setQuality
        )
        Spacer(Modifier.height(8.dp))
        ExtensionGroup(
            enabled = viewModel.bitmap != null,
            imageFormat = bitmapInfo.imageFormat,
            onFormatChange = viewModel::setMime
        )
        Spacer(Modifier.height(8.dp))
        ResizeGroup(
            enabled = viewModel.bitmap != null,
            resizeType = bitmapInfo.resizeType,
            onResizeChange = viewModel::setResizeType
        )

        EditExifSheet(
            visible = showEditExifDialog,
            exif = viewModel.exif,
            onClearExif = viewModel::clearExif,
            onUpdateTag = viewModel::updateExifByTag,
            onRemoveTag = viewModel::removeExifTag
        )
    }

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
                            title = stringResource(R.string.single_edit),
                            bitmap = viewModel.bitmap,
                            isLoading = viewModel.isImageLoading,
                            size = viewModel.imageInfo.sizeInBytes.toLong()
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
                        compareButton()
                        zoomButton()
                        if (!imageInside && viewModel.bitmap != null) actions()
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (!imageInside && viewModel.bitmap != null) {
                        Box(
                            Modifier
                                .weight(1.2f)
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
                                    controls()
                                } else if (!viewModel.isImageLoading) {
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

            ExitWithoutSavingDialog(
                onExit = onGoBack,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )

            if (viewModel.isSaving) LoadingDialog()

            BackHandler(onBack = onBack)

        }
    }

    AnimatedVisibility(showCropper) {
        val cropControls = @Composable {
            val aspectRatios = aspectRatios()
            AspectRatioSelection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                selectedIndex = aspectRatios.indexOfFirst { cr ->
                    cr.aspectRatio == viewModel.cropProperties.aspectRatio
                }
            ) { aspect ->
                viewModel.setCropAspectRatio(aspect.aspectRatio)
            }
            HorizontalDivider()
            CropMaskSelection(
                onCropMaskChange = { viewModel.setCropMask(it) },
                selectedItem = viewModel.cropProperties.cropOutlineProperty,
                loadImage = {
                    viewModel.loadImage(it)?.asImageBitmap()
                }
            )
        }
        var crop by remember { mutableStateOf(false) }
        val cropButton = @Composable {
            var job by remember { mutableStateOf<Job?>(null) }
            FloatingActionButton(
                onClick = {
                    job?.cancel()
                    job = scope.launch {
                        delay(500)
                        crop = true
                    }
                },
                modifier = Modifier.fabBorder(),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            ) {
                Icon(Icons.Rounded.Crop, null)
            }
        }
        Surface(Modifier.fillMaxSize()) {
            // TODO: FALL ASLEEP AND AFTER SUNRISE REALIZE HOW BAD THIS CODE IS AND REWRITE IT TO BE MORE CONVENIENT AND CREATE COMPOSABLE FUNCTION TO WRAP SUCH BOTTOMSHEET CASES BETTER, BECAUSE CREATING EVERYTIME SO BIG BOILERPLATE ARE DULL
            viewModel.bitmap?.let { bitmap ->
                var stateBitmap by remember { mutableStateOf(bitmap) }
                var loading by remember { mutableStateOf(false) }
                val cropper = @Composable {
                    Box(contentAlignment = Alignment.Center) {
                        Cropper(
                            bitmap = stateBitmap,
                            crop = crop,
                            imageCropStarted = { loading = true },
                            imageCropFinished = {
                                loading = false
                                stateBitmap = it
                                crop = false
                            },
                            cropProperties = viewModel.cropProperties
                        )
                        AnimatedVisibility(
                            visible = loading,
                            modifier = Modifier.fillMaxSize(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
                            ) {
                                Loading()
                            }
                        }
                    }
                }
                val topBar = @Composable {
                    CenterAlignedTopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { showCropper = false }) {
                                Icon(Icons.Rounded.Close, null)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            MaterialTheme.colorScheme.surfaceColorAtElevation(
                                3.dp
                            )
                        ),
                        modifier = Modifier.drawHorizontalStroke(),
                        actions = {
                            AnimatedVisibility(visible = stateBitmap != bitmap) {
                                OutlinedIconButton(
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(),
                                    onClick = {
                                        viewModel.updateBitmap(stateBitmap)
                                        showCropper = false
                                    }
                                ) {
                                    Icon(Icons.Rounded.Done, null)
                                }
                            }
                        },
                        title = {
                            Marquee(edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) {
                                Text(
                                    text = stringResource(R.string.crop),
                                )
                            }
                        }
                    )
                }
                Column {
                    if (imageInside) {
                        val scaffoldState = rememberBottomSheetScaffoldState()
                        BottomSheetScaffold(
                            topBar = topBar,
                            scaffoldState = scaffoldState,
                            sheetPeekHeight = 80.dp + WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding(),
                            sheetDragHandle = null,
                            sheetShape = RectangleShape,
                            sheetContent = {
                                Column(Modifier.fillMaxHeight(0.6f)) {
                                    BottomAppBar(
                                        modifier = Modifier.drawHorizontalStroke(true),
                                        actions = {
                                            IconButton(
                                                onClick = { scope.launch { scaffoldState.bottomSheetState.expand() } }
                                            ) {
                                                Icon(Icons.Rounded.Build, null)
                                            }
                                        },
                                        floatingActionButton = { cropButton() }
                                    )
                                    HorizontalDivider()
                                    cropControls()
                                }
                            },
                            content = {
                                Box(Modifier.padding(it)) {
                                    cropper()
                                }
                            }
                        )
                    } else {
                        topBar()
                        Row(
                            modifier = Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier.weight(0.8f)
                            ) {
                                cropper()
                            }
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                    .background(MaterialTheme.colorScheme.outlineVariant())
                            )

                            Column(
                                Modifier
                                    .weight(0.5f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                cropControls()
                            }

                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                    .background(MaterialTheme.colorScheme.outlineVariant())
                                    .padding(start = 20.dp)
                            )
                            Column(
                                Modifier
                                    .padding(horizontal = 20.dp)
                                    .fillMaxHeight()
                                    .navigationBarsPadding(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                cropButton()
                            }
                        }
                    }
                }
            }
        }
        if (showCropper) {
            BackHandler {
                showCropper = false
            }
        }
    }
}