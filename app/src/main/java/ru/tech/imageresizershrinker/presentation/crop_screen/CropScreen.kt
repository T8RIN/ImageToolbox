package ru.tech.imageresizershrinker.presentation.crop_screen


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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
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
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.presentation.crop_screen.components.AspectRatioSelection
import ru.tech.imageresizershrinker.presentation.crop_screen.components.CropMaskSelection
import ru.tech.imageresizershrinker.presentation.crop_screen.components.Cropper
import ru.tech.imageresizershrinker.presentation.crop_screen.components.aspectRatios
import ru.tech.imageresizershrinker.presentation.crop_screen.viewModel.CropViewModel
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.containerFabBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.presentation.root.widget.utils.isScrollingUp

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

    val aspectRatios = aspectRatios()
    val controls: @Composable () -> Unit = {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            AspectRatioSelection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                selectedIndex = aspectRatios.indexOfFirst { cr ->
                    cr.aspectRatio == viewModel.cropProperties.aspectRatio
                },
                onAspectRatioChange = { aspect ->
                    viewModel.setCropAspectRatio(aspect.aspectRatio)
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
    var share by remember { mutableStateOf(false) }
    val content: @Composable (PaddingValues) -> Unit = { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
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
                            IconButton(
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
                            IconButton(
                                onClick = onBack
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                            }
                        },
                        actions = {
                            if (portrait) {
                                IconButton(
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
                            IconButton(
                                onClick = {
                                    viewModel.resetBitmap()
                                },
                                enabled = viewModel.bitmap != null && viewModel.isBitmapChanged
                            ) {
                                Icon(Icons.Outlined.RestartAlt, null)
                            }
                            IconButton(
                                onClick = {
                                    share = true
                                    crop = true
                                },
                                enabled = viewModel.bitmap != null
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
                                if (share) {
                                    viewModel.shareBitmap(
                                        bitmap = it,
                                        onComplete = showConfetti
                                    )
                                } else {
                                    viewModel.updateBitmap(it)
                                }
                                crop = false
                                share = false
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
                                        if (share) {
                                            viewModel.shareBitmap(
                                                bitmap = it,
                                                onComplete = showConfetti
                                            )
                                        } else {
                                            viewModel.updateBitmap(it)
                                        }
                                        crop = false
                                        share = false
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
                                FloatingActionButton(
                                    onClick = pickImage,
                                    modifier = Modifier.containerFabBorder(),
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                    content = {
                                        Icon(Icons.Rounded.AddPhotoAlternate, null)
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                var job by remember { mutableStateOf<Job?>(null) }
                                FloatingActionButton(
                                    onClick = {
                                        job?.cancel()
                                        job = scope.launch {
                                            delay(500)
                                            crop = true
                                        }
                                    },
                                    modifier = Modifier.containerFabBorder(),
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                ) {
                                    Icon(Icons.Rounded.Crop, null)
                                }
                                AnimatedVisibility(viewModel.isBitmapChanged) {
                                    Column {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        FloatingActionButton(
                                            onClick = {
                                                viewModel.bitmap?.let(saveBitmap)
                                            },
                                            modifier = Modifier.containerFabBorder(),
                                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                        ) {
                                            Icon(Icons.Rounded.Save, null)
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
                    ExtendedFloatingActionButton(
                        onClick = pickImage,
                        modifier = Modifier.autoElevatedBorder(),
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        text = {
                            Text(stringResource(R.string.pick_image_alt))
                        },
                        icon = {
                            Icon(Icons.Rounded.AddPhotoAlternate, null)
                        }
                    )
                }
            }
        }
    }

    if (portrait && viewModel.bitmap != null) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 80.dp + WindowInsets.navigationBars.asPaddingValues()
                .calculateBottomPadding(),
            sheetDragHandle = null,
            sheetShape = RectangleShape,
            sheetContent = {
                Column(Modifier.fillMaxHeight(0.8f)) {
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
                                FloatingActionButton(
                                    onClick = pickImage,
                                    modifier = Modifier.autoElevatedBorder(),
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                ) {
                                    val expanded =
                                        scrollState.isScrollingUp() && viewModel.bitmap == null
                                    val horizontalPadding by animateDpAsState(targetValue = if (expanded) 16.dp else 0.dp)
                                    Row(
                                        modifier = Modifier.padding(horizontal = horizontalPadding),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Rounded.AddPhotoAlternate, null)
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
                                        FloatingActionButton(
                                            onClick = {
                                                viewModel.bitmap?.let(saveBitmap)
                                            },
                                            modifier = Modifier.autoElevatedBorder(),
                                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                        ) {
                                            Icon(Icons.Rounded.Save, null)
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