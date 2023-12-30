package ru.tech.imageresizershrinker.presentation.single_edit_screen

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.LocalIndication
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.ZoomIn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ImageExtraTransformBar
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ImageTransformBar
import ru.tech.imageresizershrinker.presentation.root.widget.controls.PresetWidget
import ru.tech.imageresizershrinker.presentation.root.widget.controls.QualityWidget
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ResizeImageField
import ru.tech.imageresizershrinker.presentation.root.widget.controls.resize_group.ResizeTypeSelector
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.presentation.root.widget.image.ImageContainer
import ru.tech.imageresizershrinker.presentation.root.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.root.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.CompareSheet
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.presentation.root.widget.utils.isExpanded
import ru.tech.imageresizershrinker.presentation.root.widget.utils.middleImageState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.rememberAvailableHeight
import ru.tech.imageresizershrinker.presentation.single_edit_screen.components.CropEditOption
import ru.tech.imageresizershrinker.presentation.single_edit_screen.components.DrawEditOption
import ru.tech.imageresizershrinker.presentation.single_edit_screen.components.EditExifSheet
import ru.tech.imageresizershrinker.presentation.single_edit_screen.components.EraseBackgroundEditOption
import ru.tech.imageresizershrinker.presentation.single_edit_screen.components.FilterEditOption
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
                themeState.updateColorByImage(it)
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
            originalBitmap = viewModel.initialBitmap,
            isLoading = viewModel.isImageLoading,
            shouldShowPreview = viewModel.shouldShowPreview
        )
    }

    val actions: @Composable RowScope.() -> Unit = {
        EnhancedIconButton(
            containerColor = Color.Transparent,
            contentColor = LocalContentColor.current,
            enableAutoShadowAndBorder = false,
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
        EnhancedIconButton(
            containerColor = Color.Transparent,
            contentColor = LocalContentColor.current,
            enableAutoShadowAndBorder = false,
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
            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
                enabled = false,
                onClick = {}
            ) { Icon(Icons.Rounded.History, null) }
        }
    }

    val buttons = @Composable {
        BottomButtonsBlock(
            targetState = (viewModel.uri == Uri.EMPTY) to imageInside,
            onPickImage = pickImage,
            onSaveBitmap = saveBitmap,
            actions = {
                if (imageInside) actions()
            }
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
        AnimatedVisibility(
            visible = viewModel.bitmap != null && viewModel.shouldShowPreview,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
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
        AnimatedVisibility(
            visible = viewModel.bitmap != null && viewModel.shouldShowPreview,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
                onClick = {
                    showCompareSheet.value = true
                }
            ) {
                Icon(Icons.Rounded.Compare, null)
            }
        }
    }

    CompareSheet(
        data = viewModel.initialBitmap to viewModel.previewBitmap,
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
    var showFiltering by rememberSaveable { mutableStateOf(false) }
    var showDrawing by rememberSaveable { mutableStateOf(false) }
    var showEraseBackground by rememberSaveable { mutableStateOf(false) }

    val controls: @Composable () -> Unit = {
        val showEditExifDialog = rememberSaveable { mutableStateOf(false) }
        ImageTransformBar(
            onEditExif = { showEditExifDialog.value = true },
            imageFormat = viewModel.imageInfo.imageFormat,
            onRotateLeft = viewModel::rotateBitmapLeft,
            onFlip = viewModel::flipImage,
            onRotateRight = viewModel::rotateBitmapRight
        )
        Spacer(Modifier.size(8.dp))
        ImageExtraTransformBar(
            onCrop = { showCropper = true },
            onFilter = { showFiltering = true },
            onDraw = { showDrawing = true },
            onEraseBackground = { showEraseBackground = true }
        )
        Spacer(Modifier.size(16.dp))
        PresetWidget(
            selectedPreset = viewModel.presetSelected,
            includeTelegramOption = true,
            onPresetSelected = viewModel::setPreset
        )
        Spacer(Modifier.size(8.dp))
        ResizeImageField(
            imageInfo = bitmapInfo,
            originalSize = viewModel.originalSize,
            onHeightChange = viewModel::updateHeight,
            onWidthChange = viewModel::updateWidth,
            showWarning = viewModel.showWarning
        )
        if (bitmapInfo.imageFormat.canChangeCompressionValue) Spacer(
            Modifier.height(8.dp)
        )
        QualityWidget(
            imageFormat = bitmapInfo.imageFormat,
            enabled = viewModel.bitmap != null,
            quality = bitmapInfo.quality.coerceIn(0f, 100f),
            onQualityChange = viewModel::setQuality
        )
        Spacer(Modifier.height(8.dp))
        ExtensionGroup(
            enabled = viewModel.bitmap != null,
            imageFormat = bitmapInfo.imageFormat,
            onFormatChange = viewModel::setImageFormat
        )
        Spacer(Modifier.height(8.dp))
        ResizeTypeSelector(
            enabled = viewModel.bitmap != null,
            value = bitmapInfo.resizeType,
            onValueChange = viewModel::setResizeType
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
                            input = viewModel.bitmap,
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
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = onBack
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
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
                    val internalHeight = rememberAvailableHeight(
                        imageState = imageState,
                        expanded = showOriginal
                    )
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
                            internalHeight = internalHeight,
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
                onReset = {
                    viewModel.resetValues(true)
                }
            )

            ExitWithoutSavingDialog(
                onExit = onGoBack,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )

            if (viewModel.isSaving) LoadingDialog(onCancelLoading = viewModel::cancelSaving)

            BackHandler(onBack = onBack)

        }
    }

    CropEditOption(
        visible = showCropper,
        onDismiss = { showCropper = false },
        useScaffold = imageInside,
        bitmap = viewModel.previewBitmap,
        onGetBitmap = viewModel::updateBitmapAfterEditing,
        cropProperties = viewModel.cropProperties,
        setCropAspectRatio = viewModel::setCropAspectRatio,
        setCropMask = viewModel::setCropMask,
        selectedAspectRatio = viewModel.selectedAspectRatio,
        loadImage = viewModel::loadImage
    )

    FilterEditOption(
        visible = showFiltering,
        onDismiss = {
            showFiltering = false
            viewModel.clearFilterList()
        },
        useScaffold = imageInside,
        bitmap = viewModel.previewBitmap,
        onGetBitmap = viewModel::updateBitmapAfterEditing,
        imageManager = viewModel.getImageManager(),
        filterList = viewModel.filterList,
        updateOrder = viewModel::updateOrder,
        updateFilter = viewModel::updateFilter,
        removeAt = viewModel::removeFilterAtIndex,
        addFilter = viewModel::addFilter
    )

    DrawEditOption(
        imageManager = viewModel.getImageManager(),
        visible = showDrawing,
        onDismiss = {
            showDrawing = false
            viewModel.clearDrawing()
        },
        useScaffold = imageInside,
        bitmap = viewModel.previewBitmap,
        onGetBitmap = {
            viewModel.updateBitmapAfterEditing(it)
            viewModel.clearDrawing()
        },
        orientation = remember(viewModel.previewBitmap) {
            viewModel.calculateScreenOrientationBasedOnBitmap(viewModel.previewBitmap)
        },
        undo = viewModel::undoDraw,
        redo = viewModel::redoDraw,
        paths = viewModel.drawPaths,
        lastPaths = viewModel.drawLastPaths,
        undonePaths = viewModel.drawUndonePaths,
        addPath = viewModel::addPathToDrawList
    )

    EraseBackgroundEditOption(
        visible = showEraseBackground,
        onDismiss = {
            showEraseBackground = false
            viewModel.clearErasing()
        },
        useScaffold = imageInside,
        bitmap = viewModel.previewBitmap,
        orientation = remember(viewModel.previewBitmap) {
            viewModel.calculateScreenOrientationBasedOnBitmap(viewModel.previewBitmap)
        },
        onGetBitmap = viewModel::updateBitmapAfterEditing,
        clearErasing = viewModel::clearErasing,
        undo = viewModel::undoErase,
        redo = viewModel::redoErase,
        paths = viewModel.erasePaths,
        lastPaths = viewModel.eraseLastPaths,
        undonePaths = viewModel.eraseUndonePaths,
        addPath = viewModel::addPathToEraseList,
        imageManager = viewModel.getImageManager()
    )
}