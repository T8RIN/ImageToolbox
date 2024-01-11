package ru.tech.imageresizershrinker.feature.resize_convert.presentation

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.rounded.Save
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
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.transformation.ImageInfoTransformation
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageTransformBar
import ru.tech.imageresizershrinker.core.ui.widget.controls.PresetWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.QualityWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.ResizeImageField
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.ScaleModeSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.resize_group.ResizeTypeSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageContainer
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageCounter
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.CompareSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.EditExifSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.utils.isExpanded
import ru.tech.imageresizershrinker.core.ui.widget.utils.middleImageState
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberAvailableHeight
import ru.tech.imageresizershrinker.feature.resize_convert.presentation.viewModel.ResizeAndConvertViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResizeAndConvertScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: ResizeAndConvertViewModel = hiltViewModel()
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
            viewModel.decodeBitmapFromUri(
                uri = it[0],
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

    LaunchedEffect(viewModel.uris) {
        if (viewModel.uris?.size == 1) {
            viewModel.decodeBitmapFromUri(
                uri = viewModel.uris!![0],
                onError = {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            )
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let {
                viewModel.updateUris(list)
                viewModel.decodeBitmapFromUri(
                    uri = it[0],
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

    val saveBitmaps: () -> Unit = {
        viewModel.saveBitmaps { savingPath ->
            if (savingPath.isNotEmpty()) {
                scope.launch {
                    toastHostState.showToast(
                        context.getString(
                            R.string.saved_to_without_filename,
                            savingPath
                        ),
                        Icons.Rounded.Save
                    )
                }
                showConfetti()
            }
        }
    }

    val focus = LocalFocusManager.current
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }

    val showPickImageFromUrisSheet = rememberSaveable { mutableStateOf(false) }

    val showEditExifDialog = rememberSaveable { mutableStateOf(false) }

    val imageInfo = viewModel.imageInfo

    val imageInside =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    var imageState by remember { mutableStateOf(middleImageState()) }
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

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.imageInfo.haveChanges(viewModel.bitmap)) showExitDialog = true
        else onGoBack()
    }

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
        EnhancedIconButton(
            containerColor = Color.Transparent,
            contentColor = LocalContentColor.current,
            enableAutoShadowAndBorder = false,
            onClick = {
                viewModel.shareBitmaps { showConfetti() }
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
                                if (viewModel.canShow()) showOriginal = true
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
            ) {
                Icon(Icons.Rounded.History, null)
            }
        }
    }

    val buttons = @Composable {
        BottomButtonsBlock(
            targetState = (viewModel.uris.isNullOrEmpty()) to imageInside,
            onPickImage = pickImage,
            onSaveBitmap = saveBitmaps,
            actions = {
                if (imageInside) actions()
            }
        )
    }

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }

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
                    showZoomSheet.value = true
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
        data = viewModel.bitmap to viewModel.previewBitmap,
        visible = showCompareSheet
    )

    ZoomModalSheet(
        bitmap = viewModel.previewBitmap,
        visible = showZoomSheet
    )

    val controls: @Composable () -> Unit = {
        ImageCounter(
            imageCount = viewModel.uris?.size?.takeIf { it > 1 },
            onRepick = {
                showPickImageFromUrisSheet.value = true
            }
        )
        AnimatedContent(
            targetState = viewModel.uris?.size == 1
        ) { oneUri ->
            if (oneUri) {
                ImageTransformBar(
                    onEditExif = { showEditExifDialog.value = true },
                    onRotateLeft = viewModel::rotateLeft,
                    onFlip = viewModel::flip,
                    imageFormat = viewModel.imageInfo.imageFormat,
                    onRotateRight = viewModel::rotateRight
                )
            } else {
                LaunchedEffect(Unit) {
                    showEditExifDialog.value = false
                    viewModel.updateExif(null)
                }
                ImageTransformBar(
                    onRotateLeft = viewModel::rotateLeft,
                    onFlip = viewModel::flip,
                    onRotateRight = viewModel::rotateRight
                )
            }
        }
        Spacer(Modifier.size(8.dp))
        PresetWidget(
            selectedPreset = viewModel.presetSelected,
            includeTelegramOption = true,
            onPresetSelected = {
                viewModel.updatePreset(it)
            }
        )
        Spacer(Modifier.size(8.dp))
        AnimatedVisibility(
            visible = viewModel.uris?.size != 1,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                SaveExifWidget(
                    imageFormat = viewModel.imageInfo.imageFormat,
                    checked = viewModel.keepExif,
                    onCheckedChange = { viewModel.setKeepExif(!viewModel.keepExif) }
                )
                Spacer(Modifier.size(8.dp))
            }
        }
        ResizeImageField(
            imageInfo = imageInfo,
            originalSize = viewModel.originalSize,
            onHeightChange = viewModel::updateHeight,
            onWidthChange = viewModel::updateWidth,
            showWarning = viewModel.showWarning
        )
        if (imageInfo.imageFormat.canChangeCompressionValue) {
            Spacer(Modifier.height(8.dp))
        }
        QualityWidget(
            imageFormat = imageInfo.imageFormat,
            enabled = viewModel.bitmap != null,
            quality = imageInfo.quality,
            onQualityChange = viewModel::setQuality
        )
        Spacer(Modifier.height(8.dp))
        ExtensionGroup(
            enabled = viewModel.bitmap != null,
            value = imageInfo.imageFormat,
            onValueChange = viewModel::setImageFormat
        )
        Spacer(Modifier.height(8.dp))
        ResizeTypeSelector(
            enabled = viewModel.bitmap != null,
            value = imageInfo.resizeType,
            onValueChange = viewModel::setResizeType
        )
        Spacer(Modifier.height(8.dp))
        ScaleModeSelector(
            value = imageInfo.imageScaleMode,
            onValueChange = viewModel::setImageScaleMode
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
                            title = stringResource(R.string.resize_and_convert),
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
                    actions = {
                        if (viewModel.bitmap == null) TopAppBarEmoji()
                        compareButton()
                        zoomButton()
                        if (!imageInside && !viewModel.uris.isNullOrEmpty()) actions()
                    },
                    navigationIcon = {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = onBack
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
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
                onReset = viewModel::resetValues
            )

            PickImageFromUrisSheet(
                transformations = listOf(
                    ImageInfoTransformation(
                        imageInfo = viewModel.imageInfo,
                        preset = viewModel.presetSelected,
                        imageManager = viewModel.getImageManager()
                    )
                ),
                visible = showPickImageFromUrisSheet,
                uris = viewModel.uris,
                selectedUri = viewModel.selectedUri,
                onUriPicked = { uri ->
                    try {
                        viewModel.setBitmap(uri = uri)
                    } catch (e: Exception) {
                        scope.launch {
                            toastHostState.showError(context, e)
                        }
                    }
                },
                onUriRemoved = { uri ->
                    viewModel.updateUrisSilently(removedUri = uri)
                },
                columns = if (imageInside) 2 else 4,
            )

            EditExifSheet(
                visible = showEditExifDialog,
                exif = viewModel.exif,
                onClearExif = viewModel::clearExif,
                onUpdateTag = viewModel::updateExifByTag,
                onRemoveTag = viewModel::removeExifTag
            )

            ExitWithoutSavingDialog(
                onExit = onGoBack,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )

            if (viewModel.isSaving) {
                LoadingDialog(
                    done = viewModel.done,
                    left = viewModel.uris?.size ?: 1
                ) {
                    viewModel.cancelSaving()
                }
            }

            BackHandler(onBack = onBack)
        }
    }
}