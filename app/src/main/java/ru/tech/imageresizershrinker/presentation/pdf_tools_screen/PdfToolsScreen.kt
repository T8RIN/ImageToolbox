package ru.tech.imageresizershrinker.presentation.pdf_tools_screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Deselect
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Preview
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.observeAsState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.image_stitching_screen.components.ImageReorderCarousel
import ru.tech.imageresizershrinker.presentation.image_stitching_screen.components.ScaleSmallImagesToLargeToggle
import ru.tech.imageresizershrinker.presentation.pdf_tools_screen.viewModel.PdfToolsViewModel
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.getFileName
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.showReview
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.root.widget.controls.PresetWidget
import ru.tech.imageresizershrinker.presentation.root.widget.controls.QualityWidget
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.containerFabBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.PdfViewer
import ru.tech.imageresizershrinker.presentation.root.widget.other.PdfViewerOrientation
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun PdfToolsScreen(
    type: Screen.PdfTools.Type?,
    onGoBack: () -> Unit,
    viewModel: PdfToolsViewModel = hiltViewModel()
) {
    LaunchedEffect(type) {
        type?.let { viewModel.setType(it) }
    }

    val context = LocalContext.current as Activity
    val toastHostState = LocalToastHost.current
    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val sizeClass = LocalWindowSizeClass.current.widthSizeClass
    val portrait by remember(
        LocalLifecycleOwner.current.lifecycle.observeAsState().value,
        sizeClass,
        configuration
    ) {
        derivedStateOf {
            configuration.orientation != Configuration.ORIENTATION_LANDSCAPE || sizeClass == WindowWidthSizeClass.Compact
        }
    }

    val onBack = {
        if (!viewModel.canGoBack()) showExitDialog = true
        else if (viewModel.pdfType != null) {
            viewModel.clearType()
        } else onGoBack()
    }

    val showConfetti: () -> Unit = {
        scope.launch {
            confettiController.showEmpty()
        }
    }

    val savePdfLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument(),
        onResult = {
            it?.let { uri ->
                viewModel.savePdfTo(
                    outputStream = context.contentResolver.openOutputStream(uri, "rw")
                ) { t ->
                    if (t != null) {
                        scope.launch {
                            toastHostState.showError(context, t)
                        }
                    } else {
                        scope.launch {
                            confettiController.showEmpty()
                        }
                        scope.launch {
                            toastHostState.showToast(
                                context.getString(
                                    R.string.saved_to_without_filename,
                                    ""
                                ),
                                Icons.Rounded.Save
                            )
                            showReview(context)
                        }
                    }
                }
            }
        }
    )

    val pdfToImagesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                viewModel.setPdfToImagesUri(it)
            }
        }
    )

    val pdfPreviewPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                viewModel.setPdfPreview(it)
            }
        }
    )

    var tempSelectionUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val showSelectionPdfPicker = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(showSelectionPdfPicker.value) {
        if (!showSelectionPdfPicker.value) tempSelectionUri = null
    }
    val selectionPdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                tempSelectionUri = it
                showSelectionPdfPicker.value = true
            }
        }
    )

    SimpleSheet(
        visible = showSelectionPdfPicker,
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showSelectionPdfPicker.value = false
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(id = R.string.close))
            }
        },
        sheetContent = {
            if (tempSelectionUri == null) showSelectionPdfPicker.value = false

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(250.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(12.dp),
            ) {
                item {
                    PreferenceItem(
                        onClick = {
                            viewModel.setPdfPreview(tempSelectionUri)
                            showSelectionPdfPicker.value = false
                        },
                        icon = Icons.Rounded.Preview,
                        title = stringResource(R.string.preview_pdf),
                        subtitle = stringResource(R.string.preview_pdf_sub),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    PreferenceItem(
                        onClick = {
                            viewModel.setPdfToImagesUri(tempSelectionUri)
                            showSelectionPdfPicker.value = false
                        },
                        icon = Icons.Rounded.Collections,
                        title = stringResource(R.string.pdf_to_images),
                        subtitle = stringResource(R.string.pdf_to_images_sub),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        title = {
            TitleItem(text = stringResource(id = R.string.pick_file), icon = Icons.Rounded.FileOpen)
        }
    )

    val imagesToPdfPicker = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple)
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.setImagesToPdf(uris)
        }
    }

    val addImagesToPdfPicker = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple)
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.addImagesToPdf(uris)
        }
    }

    val focus = LocalFocusManager.current

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState,
        canScroll = { (viewModel.pdfType !is Screen.PdfTools.Type.Preview && portrait) || viewModel.pdfType == null }
    )

    LaunchedEffect(viewModel.pdfType) {
        while (viewModel.pdfType is Screen.PdfTools.Type.Preview || (viewModel.pdfType != null && !portrait)) {
            topAppBarState.apply {
                heightOffset = (heightOffset - 10).coerceAtLeast(heightOffsetLimit)
            }
            delay(10)
        }
    }

    val selectAllToggle = remember { mutableStateOf(false) }
    val deselectAllToggle = remember { mutableStateOf(false) }

    val actionButtons: @Composable RowScope.(pdfType: Screen.PdfTools.Type?) -> Unit = {
        val pdfType = it
        AnimatedVisibility(
            visible = pdfType != null,
            enter = fadeIn() + scaleIn() + expandHorizontally(),
            exit = fadeOut() + scaleOut() + shrinkHorizontally()
        ) {
            IconButton(
                onClick = {
                    viewModel.preformSharing(showConfetti)
                }
            ) {
                Icon(Icons.Outlined.Share, null)
            }
        }
        val visible by remember(viewModel.pdfToImageState?.pages, pdfType) {
            derivedStateOf {
                (viewModel.pdfToImageState?.pages?.size != 0 && pdfType is Screen.PdfTools.Type.PdfToImages)
            }
        }
        AnimatedVisibility(
            visible = pdfType is Screen.PdfTools.Type.PdfToImages,
            enter = fadeIn() + scaleIn() + expandHorizontally(),
            exit = fadeOut() + scaleOut() + shrinkHorizontally()
        ) {
            IconButton(
                onClick = {
                    selectAllToggle.value = true
                },
                enabled = pdfType != null
            ) {
                Icon(Icons.Outlined.SelectAll, null)
            }
        }
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn() + expandHorizontally(),
            exit = fadeOut() + scaleOut() + shrinkHorizontally()
        ) {
            IconButton(
                onClick = {
                    deselectAllToggle.value = true
                },
                enabled = pdfType != null
            ) {
                Icon(Icons.Outlined.Deselect, null)
            }
        }
    }

    val buttons: @Composable (pdfType: Screen.PdfTools.Type?) -> Unit = {
        val pdfType = it
        FloatingActionButton(
            onClick = {
                when (pdfType) {
                    is Screen.PdfTools.Type.ImagesToPdf -> {
                        imagesToPdfPicker.pickImage()
                    }

                    is Screen.PdfTools.Type.Preview -> {
                        pdfPreviewPicker.launch(arrayOf("application/pdf"))
                    }

                    else -> {
                        pdfToImagesPicker.launch(arrayOf("application/pdf"))
                    }
                }
            },
            modifier = Modifier.containerFabBorder(),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        ) {
            Icon(
                imageVector = when (pdfType) {
                    is Screen.PdfTools.Type.ImagesToPdf -> Icons.Rounded.AddPhotoAlternate
                    else -> Icons.Rounded.FileOpen
                },
                contentDescription = null
            )
        }
        if (pdfType !is Screen.PdfTools.Type.Preview) {
            val visible by remember(viewModel.pdfToImageState?.pages, pdfType) {
                derivedStateOf {
                    (viewModel.pdfToImageState?.pages?.size != 0 && pdfType is Screen.PdfTools.Type.PdfToImages) || pdfType !is Screen.PdfTools.Type.PdfToImages
                }
            }
            if (visible) {
                if (portrait) {
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + scaleIn() + expandHorizontally(),
                exit = fadeOut() + scaleOut() + shrinkHorizontally()
            ) {
                FloatingActionButton(
                    onClick = {
                        if (pdfType is Screen.PdfTools.Type.ImagesToPdf && viewModel.imagesToPdfState != null) {
                            val name = viewModel.generatePdfFilename()
                            viewModel.convertImagesToPdf {
                                savePdfLauncher.launch("application/pdf#$name.pdf")
                            }
                        } else if (pdfType is Screen.PdfTools.Type.PdfToImages) {
                            viewModel.savePdfToImage { savingPath ->
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
                    },
                    modifier = Modifier.containerFabBorder(),
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                ) {
                    Icon(Icons.Rounded.Save, null)
                }
            }
        }
    }

    val controls: @Composable (pdfType: Screen.PdfTools.Type?) -> Unit = {
        val pdfType = it
        if (pdfType is Screen.PdfTools.Type.ImagesToPdf) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageReorderCarousel(
                    images = viewModel.imagesToPdfState,
                    onReorder = viewModel::reorderImagesToPdf,
                    onNeedToAddImage = { addImagesToPdfPicker.pickImage() },
                    onNeedToRemoveImageAt = viewModel::removeImageToPdfAt
                )
                Spacer(Modifier.height(8.dp))
                ScaleSmallImagesToLargeToggle(
                    selected = viewModel.scaleSmallImagesToLarge,
                    onCheckedChange = {
                        viewModel.toggleScaleSmallImagesToLarge()
                    }
                )
            }
        } else if (pdfType is Screen.PdfTools.Type.PdfToImages) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PresetWidget(
                    selectedPreset = viewModel.presetSelected,
                    includeTelegramOption = false,
                    onPresetSelected = viewModel::selectPreset
                )
                if (viewModel.imageInfo.imageFormat.canChangeCompressionValue) {
                    Spacer(
                        Modifier.height(8.dp)
                    )
                }
                QualityWidget(
                    imageFormat = viewModel.imageInfo.imageFormat,
                    enabled = true,
                    quality = viewModel.imageInfo.quality.coerceIn(0f, 100f),
                    onQualityChange = viewModel::setQuality
                )
                Spacer(
                    Modifier.height(8.dp)
                )
                ExtensionGroup(
                    enabled = true,
                    imageFormat = viewModel.imageInfo.imageFormat,
                    onFormatChange = viewModel::updateImageFormat
                )
            }
        }
    }

    Box {
        Surface(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focus.clearFocus()
                    }
                )
            },
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                Column(Modifier.fillMaxSize()) {
                    val modifier = Modifier.drawHorizontalStroke()
                    val title = @Composable {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
                            AnimatedContent(
                                targetState = viewModel.pdfType to viewModel.pdfPreviewUri,
                                transitionSpec = { fadeIn() togetherWith fadeOut() }
                            ) { (pdfType, previewUri) ->
                                Text(
                                    text = previewUri?.let {
                                        context.getFileName(it)
                                    } ?: stringResource(pdfType?.title ?: R.string.pdf_tools),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    val colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    )
                    val navigationIcon = @Composable {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    }
                    val actions: @Composable RowScope.() -> Unit = {
                        if (!portrait) {
                            actionButtons(viewModel.pdfType)
                        }
                        if (viewModel.pdfType == null) {
                            TopAppBarEmoji()
                        }
                    }

                    LargeTopAppBar(
                        scrollBehavior = scrollBehavior,
                        modifier = modifier,
                        title = title,
                        colors = colors,
                        navigationIcon = navigationIcon,
                        actions = actions
                    )

                    val screenWidth = LocalConfiguration.current.screenWidthDp
                    val easing = CubicBezierEasing(0.48f, 0.19f, 0.05f, 1.03f)
                    AnimatedContent(
                        transitionSpec = {
                            if (targetState != null) {
                                slideInHorizontally(
                                    animationSpec = tween(600, easing = easing),
                                    initialOffsetX = { screenWidth }) + fadeIn(
                                    tween(300, 100)
                                ) togetherWith slideOutHorizontally(
                                    animationSpec = tween(600, easing = easing),
                                    targetOffsetX = { -screenWidth }) + fadeOut(
                                    tween(300, 100)
                                )
                            } else {
                                slideInHorizontally(
                                    animationSpec = tween(600, easing = easing),
                                    initialOffsetX = { -screenWidth }) + fadeIn(
                                    tween(300, 100)
                                ) togetherWith slideOutHorizontally(
                                    animationSpec = tween(600, easing = easing),
                                    targetOffsetX = { screenWidth }) + fadeOut(
                                    tween(300, 100)
                                )
                            } using SizeTransform(false)
                        },
                        targetState = viewModel.pdfType
                    ) { pdfType ->
                        when (pdfType) {
                            null -> {
                                Column {
                                    val cutout = WindowInsets.displayCutout.asPaddingValues()
                                    LazyVerticalStaggeredGrid(
                                        modifier = Modifier.weight(1f),
                                        columns = StaggeredGridCells.Adaptive(300.dp),
                                        horizontalArrangement = Arrangement.spacedBy(
                                            space = 12.dp,
                                            alignment = Alignment.CenterHorizontally
                                        ),
                                        verticalItemSpacing = 12.dp,
                                        contentPadding = PaddingValues(
                                            bottom = 12.dp + WindowInsets
                                                .navigationBars
                                                .asPaddingValues()
                                                .calculateBottomPadding(),
                                            top = 12.dp,
                                            end = 12.dp + cutout.calculateEndPadding(
                                                LocalLayoutDirection.current
                                            ),
                                            start = 12.dp + cutout.calculateStartPadding(
                                                LocalLayoutDirection.current
                                            )
                                        ),
                                    ) {
                                        item {
                                            PreferenceItem(
                                                onClick = {
                                                    pdfPreviewPicker.launch(arrayOf("application/pdf"))
                                                },
                                                icon = Icons.Rounded.Preview,
                                                title = stringResource(R.string.preview_pdf),
                                                subtitle = stringResource(R.string.preview_pdf_sub),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        item {
                                            PreferenceItem(
                                                onClick = {
                                                    pdfToImagesPicker.launch(arrayOf("application/pdf"))
                                                },
                                                icon = Icons.Rounded.Collections,
                                                title = stringResource(R.string.pdf_to_images),
                                                subtitle = stringResource(R.string.pdf_to_images_sub),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        item {
                                            PreferenceItem(
                                                onClick = {
                                                    imagesToPdfPicker.pickImage()
                                                },
                                                icon = Icons.Rounded.PictureAsPdf,
                                                title = stringResource(R.string.images_to_pdf),
                                                subtitle = stringResource(R.string.images_to_pdf_sub),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .drawHorizontalStroke(true)
                                            .background(
                                                MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                    3.dp
                                                )
                                            ),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        ExtendedFloatingActionButton(
                                            onClick = {
                                                selectionPdfPicker.launch(arrayOf("application/pdf"))
                                            },
                                            modifier = Modifier
                                                .navigationBarsPadding()
                                                .padding(16.dp)
                                                .containerFabBorder(),
                                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                            text = {
                                                Text(stringResource(R.string.pick_file))
                                            },
                                            icon = {
                                                Icon(Icons.Rounded.FileOpen, null)
                                            }
                                        )
                                    }
                                }
                            }

                            else -> {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .navBarsPaddingOnlyIfTheyAtTheEnd(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (pdfType is Screen.PdfTools.Type.Preview || pdfType is Screen.PdfTools.Type.PdfToImages) {
                                            Box(
                                                Modifier
                                                    .container(
                                                        shape = RectangleShape,
                                                        resultPadding = 0.dp,
                                                        color = if (pdfType is Screen.PdfTools.Type.Preview || !portrait) {
                                                            MaterialTheme.colorScheme.surfaceContainer
                                                        } else MaterialTheme.colorScheme.surface
                                                    )
                                                    .weight(1.2f)
                                                    .clipToBounds(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (pdfType is Screen.PdfTools.Type.Preview) {
                                                    PdfViewer(
                                                        uriState = viewModel.pdfPreviewUri
                                                    )
                                                } else {
                                                    Column(
                                                        modifier = if (portrait) {
                                                            Modifier
                                                                .fillMaxSize()
                                                                .verticalScroll(rememberScrollState())
                                                        } else Modifier
                                                    ) {
                                                        PdfViewer(
                                                            modifier = if (portrait) {
                                                                Modifier
                                                                    .height(configuration.screenHeightDp.dp * (4 / 7f))
                                                                    .fillMaxWidth()
                                                            } else Modifier.fillMaxWidth(),
                                                            uriState = viewModel.pdfToImageState?.uri,
                                                            orientation = PdfViewerOrientation.Grid,
                                                            enableSelection = true,
                                                            selectAllToggle = selectAllToggle,
                                                            deselectAllToggle = deselectAllToggle,
                                                            selectedPages = viewModel.pdfToImageState?.pages
                                                                ?: emptyList(),
                                                            updateSelectedPages = viewModel::updatePdfToImageSelection,
                                                            spacing = 4.dp
                                                        )
                                                        if (portrait) {
                                                            controls(pdfType)
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (pdfType !is Screen.PdfTools.Type.Preview && !portrait || pdfType is Screen.PdfTools.Type.ImagesToPdf) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(0.7f)
                                                    .fillMaxHeight()
                                                    .clipToBounds()
                                            ) {
                                                Column(
                                                    modifier = Modifier.verticalScroll(
                                                        rememberScrollState()
                                                    )
                                                ) {
                                                    controls(pdfType)
                                                }
                                            }
                                        }
                                        if (!portrait) {
                                            Column(
                                                Modifier
                                                    .container(
                                                        shape = RectangleShape,
                                                        color = MaterialTheme.colorScheme.surfaceContainer
                                                    )
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxHeight()
                                                    .navigationBarsPadding(),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                buttons(pdfType)
                                            }
                                        }
                                    }
                                    if (portrait) {
                                        BottomAppBar(
                                            actions = {
                                                actionButtons(pdfType)
                                            },
                                            floatingActionButton = {
                                                Row {
                                                    buttons(pdfType)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (viewModel.isSaving) {
                    if (viewModel.left != 0) {
                        LoadingDialog(viewModel.done, viewModel.left) {
                            viewModel.cancelSaving()
                        }
                    } else {
                        LoadingDialog {
                            viewModel.cancelSaving()
                        }
                    }
                }
            }
        }
    }

    ExitWithoutSavingDialog(
        onExit = {
            if (viewModel.pdfType != null) {
                viewModel.clearType()
            } else onGoBack()
        },
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    BackHandler(onBack = onBack)
}

private class CreateDocument : ActivityResultContracts.CreateDocument("*/*") {
    override fun createIntent(context: Context, input: String): Intent {
        return super.createIntent(
            context = context,
            input = input.split("#")[0]
        ).putExtra(Intent.EXTRA_TITLE, input.split("#")[1])
    }
}