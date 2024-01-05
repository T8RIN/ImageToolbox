package ru.tech.imageresizershrinker.feature.limits_resize.presentation

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Share
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
import ru.tech.imageresizershrinker.coredomain.model.ImageInfo
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.components.AutoRotateLimitBoxToggle
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.components.LimitsResizeSelector
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.viewModel.LimitsResizeViewModel
import ru.tech.imageresizershrinker.coreui.transformation.ImageInfoTransformation
import ru.tech.imageresizershrinker.coreui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.coreui.utils.helper.ImageUtils.fileSize
import ru.tech.imageresizershrinker.coreui.utils.helper.Picker
import ru.tech.imageresizershrinker.coreui.utils.helper.failedToSaveImages
import ru.tech.imageresizershrinker.coreui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.coreui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.coreui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.coreui.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.coreui.widget.controls.QualityWidget
import ru.tech.imageresizershrinker.coreui.widget.controls.ResizeImageField
import ru.tech.imageresizershrinker.coreui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.coreui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.coreui.widget.image.ImageContainer
import ru.tech.imageresizershrinker.coreui.widget.image.ImageCounter
import ru.tech.imageresizershrinker.coreui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.coreui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.coreui.widget.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.coreui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.coreui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.coreui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.coreui.widget.other.showError
import ru.tech.imageresizershrinker.coreui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.coreui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.coreui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.coreui.widget.utils.isExpanded
import ru.tech.imageresizershrinker.coreui.widget.utils.middleImageState
import ru.tech.imageresizershrinker.coreui.widget.utils.rememberAvailableHeight

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
            viewModel.decodeBitmapFromUri(
                uri = uris[0],
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

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let { uris ->
                viewModel.updateUris(list)
                viewModel.decodeBitmapFromUri(
                    uri = uris[0],
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

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.canSave) showExitDialog = true
        else onGoBack()
    }

    val saveBitmaps: () -> Unit = {
        viewModel.saveBitmaps { failed, savingPath ->
            context.failedToSaveImages(
                scope = scope,
                failed = failed,
                done = viewModel.done,
                toastHostState = toastHostState,
                savingPathString = savingPath,
                showConfetti = showConfetti
            )
        }
    }

    val focus = LocalFocusManager.current
    val showPickImageFromUrisSheet = rememberSaveable { mutableStateOf(false) }

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
                    onTap = { showPickImageFromUrisSheet.value = true }
                )
            },
            imageInside = imageInside,
            showOriginal = false,
            previewBitmap = viewModel.previewBitmap,
            originalBitmap = viewModel.bitmap,
            isLoading = viewModel.isImageLoading,
            shouldShowPreview = true
        )
    }

    val showSheet = rememberSaveable { mutableStateOf(false) }
    val zoomButton = @Composable {
        AnimatedVisibility(
            visible = viewModel.bitmap != null,
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

    val actions: @Composable RowScope.() -> Unit = {
        if (viewModel.previewBitmap != null) {
            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
                onClick = {
                    viewModel.shareBitmaps { showConfetti() }
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
            canSave = viewModel.canSave,
            targetState = (viewModel.uris.isNullOrEmpty()) to imageInside,
            onPickImage = pickImage,
            onSaveBitmap = saveBitmaps,
            actions = {
                if (imageInside) actions()
            }
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
                            input = viewModel.bitmap,
                            isLoading = viewModel.isImageLoading,
                            size = viewModel.selectedUri?.fileSize(LocalContext.current) ?: 0L
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
                                .container(
                                    shape = RectangleShape,
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

                    val internalHeight = rememberAvailableHeight(imageState = imageState)
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
                            internalHeight = internalHeight,
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
                                        imageCount = viewModel.uris?.size?.takeIf { it > 1 },
                                        onRepick = {
                                            showPickImageFromUrisSheet.value = true
                                        }
                                    )
                                    ResizeImageField(
                                        imageInfo = viewModel.imageInfo,
                                        originalSize = viewModel.originalSize,
                                        onWidthChange = viewModel::updateWidth,
                                        onHeightChange = viewModel::updateHeight
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    SaveExifWidget(
                                        imageFormat = viewModel.imageInfo.imageFormat,
                                        checked = viewModel.keepExif,
                                        onCheckedChange = { viewModel.setKeepExif(!viewModel.keepExif) }
                                    )
                                    if (viewModel.imageInfo.imageFormat.canChangeCompressionValue) Spacer(
                                        Modifier.size(8.dp)
                                    )
                                    QualityWidget(
                                        imageFormat = viewModel.imageInfo.imageFormat,
                                        enabled = viewModel.bitmap != null,
                                        quality = viewModel.imageInfo.quality,
                                        onQualityChange = viewModel::setQuality
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    ExtensionGroup(
                                        enabled = viewModel.bitmap != null,
                                        imageFormat = viewModel.imageInfo.imageFormat,
                                        onFormatChange = viewModel::setMime
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    AutoRotateLimitBoxToggle(
                                        value = viewModel.resizeType.autoRotateLimitBox,
                                        onClick = viewModel::toggleAutoRotateLimitBox
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    LimitsResizeSelector(
                                        enabled = viewModel.bitmap != null,
                                        value = viewModel.resizeType,
                                        onValueChange = viewModel::setResizeType
                                    )
                                } else if (!viewModel.isImageLoading) {
                                    ImageNotPickedWidget(onPickImage = pickImage)
                                    Spacer(Modifier.size(8.dp))
                                }
                                Spacer(Modifier.height(8.dp))
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

            if (viewModel.isSaving) {
                LoadingDialog(
                    done = viewModel.done,
                    left = viewModel.uris?.size ?: 1
                ) {
                    viewModel.cancelSaving()
                }
            }

            PickImageFromUrisSheet(
                transformations = listOf(
                    ImageInfoTransformation(
                        imageInfo = ImageInfo(),
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

            ExitWithoutSavingDialog(
                onExit = onGoBack,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )

            BackHandler(onBack = onBack)
        }
    }
}