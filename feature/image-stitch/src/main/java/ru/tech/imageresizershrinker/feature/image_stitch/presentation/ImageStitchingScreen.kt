package ru.tech.imageresizershrinker.feature.image_stitch.presentation

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.ErrorOutline
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
import androidx.compose.ui.graphics.toArgb
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
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.DrawBackgroundSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageReorderCarousel
import ru.tech.imageresizershrinker.core.ui.widget.controls.QualityWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.ScaleSmallImagesToLargeToggle
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageContainer
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.utils.isExpanded
import ru.tech.imageresizershrinker.core.ui.widget.utils.middleImageState
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberAvailableHeight
import ru.tech.imageresizershrinker.feature.image_stitch.presentation.components.ImageFadingEdgesSelector
import ru.tech.imageresizershrinker.feature.image_stitch.presentation.components.ImageScaleSelector
import ru.tech.imageresizershrinker.feature.image_stitch.presentation.components.SpacingSelector
import ru.tech.imageresizershrinker.feature.image_stitch.presentation.components.StitchModeSelector
import ru.tech.imageresizershrinker.feature.image_stitch.presentation.viewModel.ImageStitchingViewModel
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageStitchingScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: ImageStitchingViewModel = hiltViewModel()
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
        uriState?.takeIf { it.size > 1 }?.let { uris ->
            viewModel.updateUris(uris)
        }
    }

    LaunchedEffect(viewModel.previewBitmap) {
        viewModel.previewBitmap?.let {
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
                if (uris.size < 2) {
                    scope.launch {
                        toastHostState.showToast(
                            message = context.getString(R.string.pick_at_least_two_images),
                            icon = Icons.Rounded.ErrorOutline
                        )
                    }
                } else {
                    viewModel.updateUris(uris)
                }
            }
        }

    val addImagesLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let { uris ->
                viewModel.addUrisToEnd(uris)
            }
        }

    val addImages = {
        addImagesLauncher.pickImage()
    }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.previewBitmap != null) showExitDialog = true
        else onGoBack()
    }

    val saveBitmaps: () -> Unit = {
        viewModel.saveBitmaps { saveResult ->
            parseSaveResult(
                saveResult = saveResult,
                onSuccess = showConfetti,
                toastHostState = toastHostState,
                scope = scope,
                context = context
            )
        }
    }

    val focus = LocalFocusManager.current

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
            imageInside = imageInside,
            showOriginal = false,
            previewBitmap = viewModel.previewBitmap,
            originalBitmap = null,
            isLoading = viewModel.isImageLoading,
            shouldShowPreview = true
        )
    }

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }
    val zoomButton = @Composable {
        AnimatedVisibility(
            visible = viewModel.previewBitmap != null,
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

    val actions: @Composable RowScope.() -> Unit = {
        if (viewModel.previewBitmap != null) {
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
        }
        zoomButton()
    }

    val buttons = @Composable {
        BottomButtonsBlock(
            canSave = viewModel.previewBitmap != null,
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
                            title = stringResource(R.string.image_stitching),
                            input = viewModel.uris,
                            isLoading = viewModel.isImageLoading,
                            size = viewModel
                                .imageByteSize?.times(viewModel.imageScale)?.roundToLong(),
                            updateOnSizeChange = false
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
                        if (viewModel.uris.isNullOrEmpty()) {
                            TopAppBarEmoji()
                        }
                        if (!imageInside) actions()
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (!imageInside && !viewModel.uris.isNullOrEmpty()) {
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
                                .calculateBottomPadding() + (if (!imageInside && !viewModel.uris.isNullOrEmpty()) 20.dp else 100.dp),
                            top = if (viewModel.uris.isNullOrEmpty() || !imageInside) 20.dp else 0.dp,
                            start = 20.dp,
                            end = 20.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clipToBounds()
                    ) {
                        imageStickyHeader(
                            visible = imageInside && !viewModel.uris.isNullOrEmpty(),
                            imageState = imageState,
                            internalHeight = internalHeight,
                            onStateChange = { imageState = it },
                            imageBlock = imageBlock
                        )
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .navBarsLandscapePadding(viewModel.uris.isNullOrEmpty()),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (imageInside && viewModel.uris.isNullOrEmpty()) imageBlock()
                                if (!viewModel.uris.isNullOrEmpty()) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        ImageReorderCarousel(
                                            images = viewModel.uris,
                                            onReorder = viewModel::updateUris,
                                            onNeedToAddImage = addImages,
                                            onNeedToRemoveImageAt = viewModel::removeImageAt
                                        )
                                        ImageScaleSelector(
                                            modifier = Modifier.padding(top = 8.dp),
                                            value = viewModel.imageScale,
                                            onValueChange = viewModel::updateImageScale,
                                            approximateImageSize = viewModel.imageSize
                                        )
                                        StitchModeSelector(
                                            value = viewModel.combiningParams.stitchMode,
                                            onValueChange = viewModel::setStitchMode
                                        )
                                        SpacingSelector(
                                            value = viewModel.combiningParams.spacing,
                                            onValueChange = viewModel::updateImageSpacing
                                        )
                                        AnimatedVisibility(
                                            visible = viewModel.combiningParams.spacing < 0,
                                            enter = fadeIn() + expandVertically(),
                                            exit = fadeOut() + shrinkVertically()
                                        ) {
                                            ImageFadingEdgesSelector(
                                                value = viewModel.combiningParams.fadingEdgesMode,
                                                onValueChange = viewModel::setFadingEdgesMode
                                            )
                                        }
                                        ScaleSmallImagesToLargeToggle(
                                            checked = viewModel.combiningParams.scaleSmallImagesToLarge,
                                            onCheckedChange = viewModel::toggleScaleSmallImagesToLarge
                                        )
                                        DrawBackgroundSelector(
                                            value = Color(viewModel.combiningParams.backgroundColor),
                                            onColorChange = {
                                                viewModel.updateBackgroundSelector(it.toArgb())
                                            },
                                            modifier = Modifier.container(
                                                shape = RoundedCornerShape(
                                                    24.dp
                                                )
                                            )
                                        )
                                        QualityWidget(
                                            imageFormat = viewModel.imageInfo.imageFormat,
                                            enabled = !viewModel.uris.isNullOrEmpty(),
                                            quality = viewModel.imageInfo.quality,
                                            onQualityChange = viewModel::setQuality
                                        )
                                        ExtensionGroup(
                                            enabled = !viewModel.uris.isNullOrEmpty(),
                                            value = viewModel.imageInfo.imageFormat,
                                            onValueChange = viewModel::setMime
                                        )
                                    }
                                } else if (!viewModel.isImageLoading) {
                                    ImageNotPickedWidget(onPickImage = pickImage)
                                    Spacer(Modifier.height(8.dp))
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }

                    if (!imageInside && !viewModel.uris.isNullOrEmpty()) {
                        buttons()
                    }
                }
            }

            if (imageInside || viewModel.uris.isNullOrEmpty()) {
                Box(
                    modifier = Modifier.align(settingsState.fabAlignment)
                ) {
                    buttons()
                }
            }

            if (viewModel.isSaving) {
                LoadingDialog {
                    viewModel.cancelSaving()
                }
            }


            ExitWithoutSavingDialog(
                onExit = onGoBack,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )

            BackHandler(onBack = onBack)
        }
    }
}