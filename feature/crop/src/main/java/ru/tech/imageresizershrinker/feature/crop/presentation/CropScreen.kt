package ru.tech.imageresizershrinker.feature.crop.presentation


import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.smarttoolfactory.cropper.model.OutlineType
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.feature.crop.presentation.components.AspectRatioSelection
import ru.tech.imageresizershrinker.feature.crop.presentation.components.CropMaskSelection
import ru.tech.imageresizershrinker.feature.crop.presentation.components.Cropper
import ru.tech.imageresizershrinker.feature.crop.presentation.viewModel.CropViewModel
import ru.tech.imageresizershrinker.coreui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.coreui.utils.helper.Picker
import ru.tech.imageresizershrinker.coreui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.coreui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.coreui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.coreui.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.coreui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.coreui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.coreui.widget.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.coreui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.coreui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.coreui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.coreui.widget.other.showError
import ru.tech.imageresizershrinker.coreui.widget.text.Marquee
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.coreui.widget.utils.isScrollingUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: CropViewModel = hiltViewModel()
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

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.bitmap != null) showExitDialog = true
        else onGoBack()
    }

    LaunchedEffect(uriState) {
        uriState?.let {
            viewModel.setUri(it)
            viewModel.decodeBitmapByUri(
                uri = it,
                onGetMimeType = viewModel::updateMimeType,
                onGetExif = {},
                onGetBitmap = { bmp ->
                    viewModel.updateBitmap(
                        bitmap = bmp, newBitmap = true
                    )
                },
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
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
                viewModel.setUri(it)
                viewModel.decodeBitmapByUri(
                    uri = it,
                    onGetMimeType = viewModel::updateMimeType,
                    onGetExif = {},
                    onGetBitmap = { bmp ->
                        viewModel.updateBitmap(
                            bitmap = bmp, newBitmap = true
                        )
                    },
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

    val saveBitmap: (Bitmap) -> Unit = {
        viewModel.saveBitmap(bitmap = it) { saveResult ->
            parseSaveResult(
                saveResult = saveResult,
                onSuccess = showConfetti,
                toastHostState = toastHostState,
                scope = scope,
                context = context
            )
        }
    }


    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

    val portrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            confirmValueChange = {
                when (it) {
                    SheetValue.Hidden -> false
                    else -> true
                }
            }
        )
    )

    val controls: @Composable () -> Unit = {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            AspectRatioSelection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                selectedAspectRatio = viewModel.selectedAspectRatio,
                onAspectRatioChange = { domainAspect, aspect ->
                    viewModel.setCropAspectRatio(domainAspect, aspect)
                }
            )
            HorizontalDivider()
            CropMaskSelection(
                onCropMaskChange = { viewModel.setCropMask(it) },
                selectedItem = viewModel.cropProperties.cropOutlineProperty,
                loadImage = {
                    viewModel.loadImage(it)?.asImageBitmap()
                }
            )
            HorizontalDivider()
            ExtensionGroup(
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding(),
                entries = if (viewModel.cropProperties.cropOutlineProperty.outlineType == OutlineType.Rect) {
                    ImageFormat.entries
                } else ImageFormat.alphaContainedEntries,
                enabled = viewModel.bitmap != null,
                imageFormat = viewModel.imageFormat,
                onFormatChange = {
                    viewModel.updateMimeType(it)
                }
            )
        }
    }

    var crop by remember { mutableStateOf(false) }
    val content: @Composable (PaddingValues) -> Unit = { paddingValues ->
        Box(
            Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (viewModel.bitmap == null) {
                    LargeTopAppBar(
                        scrollBehavior = scrollBehavior,
                        modifier = Modifier.drawHorizontalStroke(),
                        title = {
                            Marquee(
                                edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            ) {
                                Text(stringResource(R.string.crop))
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
                                onClick = onBack
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                            }
                        },
                        actions = {
                            TopAppBarEmoji()
                        }
                    )
                } else {
                    TopAppBar(
                        modifier = Modifier.drawHorizontalStroke(),
                        title = {
                            Marquee(
                                edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            ) {
                                Text(stringResource(R.string.crop))
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
                                onClick = onBack
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                            }
                        },
                        actions = {
                            if (portrait) {
                                EnhancedIconButton(
                                    containerColor = Color.Transparent,
                                    contentColor = LocalContentColor.current,
                                    enableAutoShadowAndBorder = false,
                                    onClick = {
                                        scope.launch {
                                            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                                scaffoldState.bottomSheetState.partialExpand()
                                            } else {
                                                scaffoldState.bottomSheetState.expand()
                                            }
                                        }
                                    },
                                ) {
                                    Icon(Icons.Rounded.Tune, null)
                                }
                            }
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = {
                                    viewModel.resetBitmap()
                                },
                                enabled = viewModel.bitmap != null && viewModel.isBitmapChanged
                            ) {
                                Icon(Icons.Outlined.RestartAlt, null)
                            }
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = {
                                    viewModel.shareBitmap(
                                        onComplete = showConfetti
                                    )
                                },
                                enabled = viewModel.isBitmapChanged
                            ) {
                                Icon(Icons.Outlined.Share, null)
                            }
                        }
                    )
                }
                viewModel.bitmap?.let { bitmap ->
                    if (portrait) {
                        Cropper(
                            bitmap = bitmap,
                            crop = crop,
                            imageCropStarted = viewModel::imageCropStarted,
                            imageCropFinished = {
                                viewModel.imageCropFinished()
                                viewModel.updateBitmap(it)
                                crop = false
                            },
                            cropProperties = viewModel.cropProperties
                        )
                    } else {
                        Row(
                            modifier = Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .zIndex(-100f)
                                    .container(shape = RectangleShape, resultPadding = 0.dp)
                                    .weight(0.8f)
                            ) {
                                Cropper(
                                    bitmap = bitmap,
                                    crop = crop,
                                    imageCropStarted = viewModel::imageCropStarted,
                                    imageCropFinished = {
                                        viewModel.imageCropFinished()
                                        viewModel.updateBitmap(it)
                                        crop = false
                                    },
                                    cropProperties = viewModel.cropProperties
                                )
                            }

                            Column(
                                Modifier.weight(0.5f)
                            ) {
                                controls()
                            }
                            Column(
                                Modifier
                                    .container(
                                        RectangleShape,
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                                    )
                                    .padding(horizontal = 20.dp)
                                    .fillMaxHeight()
                                    .navigationBarsPadding(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                EnhancedFloatingActionButton(
                                    onClick = pickImage,
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    content = {
                                        Icon(
                                            imageVector = Icons.Rounded.AddPhotoAlternate,
                                            contentDescription = null
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                var job by remember { mutableStateOf<Job?>(null) }
                                EnhancedFloatingActionButton(
                                    onClick = {
                                        job?.cancel()
                                        job = scope.launch {
                                            delay(500)
                                            crop = true
                                        }
                                    },
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Crop,
                                        contentDescription = null
                                    )
                                }
                                AnimatedVisibility(viewModel.isBitmapChanged) {
                                    Column {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        EnhancedFloatingActionButton(
                                            onClick = {
                                                viewModel.bitmap?.let(saveBitmap)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Save,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } ?: Column(Modifier.verticalScroll(scrollState)) {
                    ImageNotPickedWidget(
                        onPickImage = pickImage,
                        modifier = Modifier
                            .padding(bottom = 88.dp, top = 20.dp, start = 20.dp, end = 20.dp)
                            .navigationBarsPadding()
                    )
                }
            }

            if (viewModel.bitmap == null) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding()
                        .align(settingsState.fabAlignment)
                ) {
                    EnhancedFloatingActionButton(
                        onClick = pickImage,
                        content = {
                            Spacer(Modifier.width(16.dp))
                            Icon(Icons.Rounded.AddPhotoAlternate, null)
                            Spacer(Modifier.width(16.dp))
                            Text(stringResource(R.string.pick_image_alt))
                            Spacer(Modifier.width(16.dp))
                        }
                    )
                }
            }
        }
    }

    if (portrait && viewModel.bitmap != null) {
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 80.dp + WindowInsets.navigationBars.asPaddingValues()
                .calculateBottomPadding(),
            sheetDragHandle = null,
            sheetShape = RectangleShape,
            sheetContent = {
                Column(Modifier.heightIn(screenHeight * 0.7f)) {
                    BottomAppBar(
                        modifier = Modifier.drawHorizontalStroke(true),
                        actions = {
                            var job by remember { mutableStateOf<Job?>(null) }
                            EnhancedButton(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                onClick = {
                                    job?.cancel()
                                    job = scope.launch {
                                        delay(500)
                                        crop = true
                                    }
                                }
                            ) {
                                Text(stringResource(R.string.crop))
                            }
                        },
                        floatingActionButton = {
                            Row {
                                EnhancedFloatingActionButton(
                                    onClick = pickImage,
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    val expanded =
                                        scrollState.isScrollingUp() && viewModel.bitmap == null
                                    val horizontalPadding by animateDpAsState(targetValue = if (expanded) 16.dp else 0.dp)
                                    Row(
                                        modifier = Modifier.padding(horizontal = horizontalPadding),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AddPhotoAlternate,
                                            contentDescription = null
                                        )
                                        AnimatedVisibility(visible = expanded) {
                                            Row {
                                                Spacer(Modifier.width(8.dp))
                                                Text(stringResource(R.string.pick_image_alt))
                                            }
                                        }
                                    }
                                }
                                AnimatedVisibility(viewModel.isBitmapChanged) {
                                    Row {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        EnhancedFloatingActionButton(
                                            onClick = {
                                                viewModel.bitmap?.let(saveBitmap)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Save,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                    controls()
                }
            },
            content = content
        )
    } else {
        content(PaddingValues())
    }

    if (viewModel.isSaving || viewModel.isImageLoading) {
        LoadingDialog(canCancel = viewModel.isSaving) { viewModel.cancelSaving() }
    }

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    BackHandler(onBack = onBack)
}