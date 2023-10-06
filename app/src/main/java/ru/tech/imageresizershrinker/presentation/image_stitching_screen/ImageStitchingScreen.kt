package ru.tech.imageresizershrinker.presentation.image_stitching_screen

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.SaveAlt
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.toArgb
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawBackgroundSelector
import ru.tech.imageresizershrinker.presentation.image_stitching_screen.components.ImageOrientationToggle
import ru.tech.imageresizershrinker.presentation.image_stitching_screen.components.ImageScaleSelector
import ru.tech.imageresizershrinker.presentation.image_stitching_screen.components.ScaleSmallImagesToLargeToggle
import ru.tech.imageresizershrinker.presentation.image_stitching_screen.components.SpacingSelector
import ru.tech.imageresizershrinker.presentation.image_stitching_screen.viewModel.ImageStitchingViewModel
import ru.tech.imageresizershrinker.presentation.root.transformation.filter.SaturationFilter
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.root.widget.controls.QualityWidget
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.image.ImageContainer
import ru.tech.imageresizershrinker.presentation.root.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.root.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.presentation.root.widget.other.GradientEdge
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.presentation.root.widget.utils.isExpanded
import ru.tech.imageresizershrinker.presentation.root.widget.utils.middleImageState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
                themeState.updateColorByImage(
                    SaturationFilter(context, 2f).transform(it, Size(500, 500))
                )
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
                } else viewModel.updateUris(uris)
            }
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
        AnimatedVisibility(viewModel.previewBitmap != null) {
            IconButton(
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
            IconButton(
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
                            bitmap = viewModel.previewBitmap,
                            isLoading = viewModel.isImageLoading,
                            size = null
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
                                    val pagerState = rememberPagerState(pageCount = { 2 })
                                    PrimaryTabRow(
                                        divider = {},
                                        containerColor = Color.Transparent,
                                        selectedTabIndex = pagerState.currentPage,
                                        indicator = { tabPositions ->
                                            if (pagerState.currentPage < tabPositions.size) {
                                                val width by animateDpAsState(targetValue = tabPositions[pagerState.currentPage].contentWidth)
                                                TabRowDefaults.PrimaryIndicator(
                                                    modifier = Modifier.tabIndicatorOffset(
                                                        tabPositions[pagerState.currentPage]
                                                    ),
                                                    width = width,
                                                    height = 4.dp
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .container(
                                                resultPadding = 0.dp,
                                                shape = RoundedCornerShape(36.dp)
                                            )
                                            .padding(bottom = 8.dp)
                                    ) {
                                        listOf(
                                            Icons.Rounded.Tune to stringResource(R.string.options),
                                            Icons.Rounded.SaveAlt to stringResource(R.string.saving),
                                        ).forEachIndexed { index, (icon, title) ->
                                            val selected = pagerState.currentPage == index
                                            Tab(
                                                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .clip(CircleShape),
                                                selected = selected,
                                                onClick = {
                                                    scope.launch {
                                                        pagerState.animateScrollToPage(index)
                                                    }
                                                },
                                                icon = {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                        tint = if (selected) {
                                                            MaterialTheme.colorScheme.primary
                                                        } else MaterialTheme.colorScheme.onSurface
                                                    )
                                                },
                                                text = { Text(title) }
                                            )
                                        }
                                    }
                                    Box {
                                        HorizontalPager(
                                            modifier = Modifier.fillMaxWidth(),
                                            state = pagerState,
                                            verticalAlignment = Alignment.Top
                                        ) { page ->
                                            Column(
                                                modifier = Modifier.padding(
                                                    vertical = 16.dp,
                                                    horizontal = 8.dp
                                                ),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                when (page) {
                                                    1 -> {
                                                        if (viewModel.imageInfo.imageFormat.canChangeCompressionValue) {
                                                            Spacer(Modifier.height(8.dp))
                                                        }
                                                        QualityWidget(
                                                            imageFormat = viewModel.imageInfo.imageFormat,
                                                            enabled = !viewModel.uris.isNullOrEmpty(),
                                                            quality = viewModel.imageInfo.quality,
                                                            onQualityChange = viewModel::setQuality
                                                        )
                                                        ExtensionGroup(
                                                            enabled = !viewModel.uris.isNullOrEmpty(),
                                                            imageFormat = viewModel.imageInfo.imageFormat,
                                                            onFormatChange = viewModel::setMime
                                                        )
                                                    }

                                                    0 -> {
                                                        ImageScaleSelector(
                                                            modifier = Modifier.padding(top = 8.dp),
                                                            value = viewModel.imageScale,
                                                            onValueChange = viewModel::updateImageScale,
                                                            approximateImageSize = viewModel.imageSize
                                                        )
                                                        ImageOrientationToggle(
                                                            selected = viewModel.combiningParams.isHorizontal,
                                                            onCheckedChange = viewModel::toggleIsHorizontal
                                                        )
                                                        SpacingSelector(
                                                            value = viewModel.combiningParams.spacing,
                                                            onValueChange = viewModel::updateImageSpacing
                                                        )
                                                        ScaleSmallImagesToLargeToggle(
                                                            selected = viewModel.combiningParams.scaleSmallImagesToLarge,
                                                            onCheckedChange = viewModel::toggleScaleSmallImagesToLarge
                                                        )
                                                        DrawBackgroundSelector(
                                                            backgroundColor = Color(viewModel.combiningParams.backgroundColor),
                                                            onColorChange = {
                                                                viewModel.updateBackgroundSelector(it.toArgb())
                                                            },
                                                            modifier = Modifier.container(shape = RoundedCornerShape(24.dp))
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        GradientEdge(
                                            modifier = Modifier
                                                .fillParentMaxHeight()
                                                .width(8.dp)
                                                .align(Alignment.CenterStart),
                                            startColor = MaterialTheme.colorScheme.surface,
                                            endColor = Color.Transparent
                                        )
                                        GradientEdge(
                                            modifier = Modifier
                                                .fillParentMaxHeight()
                                                .width(8.dp)
                                                .align(Alignment.CenterEnd),
                                            startColor = Color.Transparent,
                                            endColor = MaterialTheme.colorScheme.surface
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