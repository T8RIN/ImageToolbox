package ru.tech.imageresizershrinker.presentation.erase_background_screen

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.observeAsState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.presentation.draw_screen.components.BlurRadiusSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.LineWidthSelector
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.AutoEraseBackgroundCard
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.BitmapEraser
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.EraseModeButton
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.EraseModeCard
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.TrimImageToggle
import ru.tech.imageresizershrinker.presentation.erase_background_screen.viewModel.EraseBackgroundViewModel
import ru.tech.imageresizershrinker.presentation.root.icons.material.Transparency
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.transformation.filter.SaturationFilter
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.root.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.LockScreenOrientation
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EraseBackgroundScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: EraseBackgroundViewModel = hiltViewModel()
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
                    SaturationFilter(context, 2f).transform(it, coil.size.Size.ORIGINAL)
                )
            }
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }


    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
                viewModel.setUri(it)
                viewModel.decodeBitmapByUri(
                    uri = it,
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

    val pickImage = pickImageLauncher::pickImage

    val scaffoldState = rememberBottomSheetScaffoldState()

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState
    )

    val onBack = {
        if (viewModel.haveChanges) showExitDialog = true
        else onGoBack()
    }

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

    var strokeWidth by rememberSaveable { mutableFloatStateOf(20f) }
    var blurRadius by rememberSaveable { mutableFloatStateOf(0f) }

    val configuration = LocalConfiguration.current
    val sizeClass = LocalWindowSizeClass.current.widthSizeClass
    val portrait =
        remember(
            LocalLifecycleOwner.current.lifecycle.observeAsState().value,
            sizeClass,
            configuration
        ) {
            derivedStateOf {
                configuration.orientation != Configuration.ORIENTATION_LANDSCAPE || sizeClass == WindowWidthSizeClass.Compact
            }
        }.value

    var zoomEnabled by rememberSaveable { mutableStateOf(false) }

    val secondaryControls = @Composable {
        Switch(
            modifier = Modifier.padding(horizontal = if (!portrait) 8.dp else 16.dp),
            colors = SwitchDefaults.colors(
                uncheckedBorderColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            checked = !zoomEnabled,
            onCheckedChange = { zoomEnabled = !zoomEnabled },
            thumbContent = {
                AnimatedContent(zoomEnabled) { zoom ->
                    Icon(
                        if (!zoom) Icons.Filled.Transparency else Icons.Rounded.ZoomIn,
                        null,
                        Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            }
        )
        OutlinedIconButton(
            border = if (portrait) {
                BorderStroke(0.dp, Color.Transparent)
            } else ButtonDefaults.outlinedButtonBorder,
            onClick = { viewModel.undo() },
            enabled = viewModel.lastPaths.isNotEmpty() || viewModel.paths.isNotEmpty()
        ) {
            Icon(Icons.Rounded.Undo, null)
        }
        OutlinedIconButton(
            border = if (portrait) {
                BorderStroke(0.dp, Color.Transparent)
            } else ButtonDefaults.outlinedButtonBorder,
            onClick = { viewModel.redo() },
            enabled = viewModel.undonePaths.isNotEmpty()
        ) {
            Icon(Icons.Rounded.Redo, null)
        }
    }

    val image: @Composable () -> Unit = @Composable {
        viewModel.bitmap?.let { bitmap ->
            AnimatedContent(
                targetState = remember(bitmap) {
                    derivedStateOf {
                        bitmap.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()
                    }
                }.value,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { imageBitmap ->
                val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
                BitmapEraser(
                    imageBitmapForShader = viewModel.internalBitmap?.asImageBitmap(),
                    imageBitmap = imageBitmap,
                    paths = viewModel.paths,
                    strokeWidth = strokeWidth,
                    blurRadius = blurRadius,
                    onAddPath = viewModel::addPath,
                    isRecoveryOn = viewModel.isRecoveryOn,
                    modifier = Modifier
                        .padding(16.dp)
                        .aspectRatio(aspectRatio, portrait)
                        .fillMaxSize(),
                    zoomEnabled = zoomEnabled,
                    onErased = viewModel::updateErasedBitmap,
                )
            }

        } ?: ImageNotPickedWidget(onPickImage = pickImage, modifier = Modifier.padding(top = 20.dp))
    }

    val topAppBar = @Composable {
        TopAppBar(
            modifier = Modifier.drawHorizontalStroke(),
            title = {
                Marquee(
                    edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ) {
                    Text(stringResource(R.string.background_remover))
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
                    onClick = { viewModel.shareBitmap { showConfetti() } }
                ) {
                    Icon(Icons.Outlined.Share, null)
                }
                IconButton(
                    onClick = { viewModel.clearDrawing() },
                    enabled = viewModel.paths.isNotEmpty()
                ) {
                    Icon(Icons.Outlined.Delete, null)
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
                    Icon(Icons.Rounded.ArrowBack, null)
                }
            },
        )
    }

    val controls = @Composable {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!portrait) {
                Row(
                    Modifier
                        .padding(top = 8.dp)
                        .block(CircleShape)
                ) {
                    secondaryControls()
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            EraseModeCard(
                isRecoveryOn = viewModel.isRecoveryOn,
                onClick = viewModel::toggleEraser
            )
            AutoEraseBackgroundCard(
                onClick = {
                    scope.launch {
                        scaffoldState.bottomSheetState.partialExpand()
                        viewModel.autoEraseBackground(
                            onSuccess = showConfetti,
                            onFailure = {
                                scope.launch {
                                    toastHostState.showError(context, it)
                                }
                            }
                        )
                    }
                },
                onReset = viewModel::resetImage
            )
            LineWidthSelector(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                strokeWidth = strokeWidth,
                onChangeStrokeWidth = { strokeWidth = it }
            )
            BlurRadiusSelector(
                modifier = Modifier
                    .padding(top = 8.dp, end = 16.dp, start = 16.dp),
                blurRadius = blurRadius,
                onRadiusChange = { blurRadius = it }
            )
            TrimImageToggle(
                selected = viewModel.trimImage,
                onCheckedChange = { viewModel.setTrimImage(it) },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
            SaveExifWidget(
                imageFormat = viewModel.imageFormat,
                selected = viewModel.saveExif,
                onCheckedChange = { viewModel.setSaveExif(it) },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
            ExtensionGroup(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                    .navigationBarsPadding(),
                entries = ImageFormat.alphaContainedEntries,
                enabled = true,
                imageFormat = viewModel.imageFormat,
                onFormatChange = {
                    viewModel.setMime(it)
                }
            )
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
                            secondaryControls()
                            EraseModeButton(
                                isRecoveryOn = viewModel.isRecoveryOn,
                                onClick = viewModel::toggleEraser
                            )
                        },
                        floatingActionButton = {
                            Row {
                                FloatingActionButton(
                                    onClick = pickImage,
                                    modifier = Modifier.fabBorder(),
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                ) {
                                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                FloatingActionButton(
                                    onClick = saveBitmap,
                                    modifier = Modifier.fabBorder(),
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                ) {
                                    Icon(Icons.Rounded.Save, null)
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        controls()
                    }
                }
            },
            content = {
                Column(Modifier.padding(it), horizontalAlignment = Alignment.CenterHorizontally) {
                    topAppBar()
                    image()
                }
            }
        )
    } else {
        if (viewModel.bitmap == null) {
            Box(Modifier.fillMaxSize()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    LargeTopAppBar(
                        scrollBehavior = scrollBehavior,
                        modifier = Modifier.drawHorizontalStroke(),
                        title = {
                            Marquee(
                                edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            ) {
                                Text(stringResource(R.string.background_remover))
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
                                Icon(Icons.Rounded.ArrowBack, null)
                            }
                        },
                        actions = {
                            TopAppBarEmoji()
                        }
                    )
                    Column(
                        Modifier.verticalScroll(rememberScrollState())
                    ) {
                        image()
                        Spacer(modifier = Modifier.height(108.dp))
                    }
                }
                ExtendedFloatingActionButton(
                    onClick = pickImage,
                    modifier = Modifier
                        .align(settingsState.fabAlignment)
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .fabBorder(),
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    text = {
                        Text(stringResource(R.string.pick_image_alt))
                    },
                    icon = {
                        Icon(Icons.Rounded.AddPhotoAlternate, null)
                    }
                )
            }
        } else {
            Column {
                topAppBar()
                Row(
                    modifier = Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        Modifier
                            .weight(1.2f)
                            .clipToBounds(),
                        contentAlignment = Alignment.Center
                    ) {
                        image()
                    }
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant())
                    )
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(
                            bottom = WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + WindowInsets.ime
                                .asPaddingValues()
                                .calculateBottomPadding(),
                        ),
                        modifier = Modifier
                            .weight(0.7f)
                            .clipToBounds()
                    ) {
                        item {
                            controls()
                        }
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
                        FloatingActionButton(
                            onClick = pickImage,
                            modifier = Modifier.fabBorder(),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        ) {
                            Icon(Icons.Rounded.AddPhotoAlternate, null)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        FloatingActionButton(
                            onClick = saveBitmap,
                            modifier = Modifier.fabBorder(),
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        ) {
                            Icon(Icons.Rounded.Save, null)
                        }
                    }
                }
            }
        }
    }

    if (viewModel.isSaving || viewModel.isImageLoading) {
        LoadingDialog(viewModel.isSaving) {
            viewModel.cancelSaving()
        }
    }

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    BackHandler(onBack = onBack)

    LockScreenOrientation(orientation = viewModel.orientation)
}