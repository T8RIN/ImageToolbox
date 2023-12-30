package ru.tech.imageresizershrinker.presentation.erase_background_screen

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SheetValue
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import ru.tech.imageresizershrinker.domain.image.draw.pt
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.presentation.draw_screen.components.BrushSoftnessSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.LineWidthSelector
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.AutoEraseBackgroundCard
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.BitmapEraser
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.EraseModeButton
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.EraseModeCard
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.TrimImageToggle
import ru.tech.imageresizershrinker.presentation.erase_background_screen.viewModel.EraseBackgroundViewModel
import ru.tech.imageresizershrinker.presentation.root.icons.material.Transparency
import ru.tech.imageresizershrinker.presentation.root.model.PtSaver
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedSwitch
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedSwitchDefaults
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.root.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.presentation.root.widget.other.DrawLockScreenOrientation
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
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
                themeState.updateColorByImage(it)
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

    var strokeWidth by rememberSaveable(stateSaver = PtSaver) { mutableStateOf(20.pt) }
    var brushSoftness by rememberSaveable(stateSaver = PtSaver) { mutableStateOf(0.pt) }

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
        EnhancedSwitch(
            modifier = Modifier.padding(horizontal = if (!portrait) 8.dp else 16.dp),
            colors = EnhancedSwitchDefaults.uncheckableColors(),
            checked = !zoomEnabled,
            onCheckedChange = { zoomEnabled = !zoomEnabled },
            thumbIcon = if (!zoomEnabled) {
                Icons.Filled.Transparency
            } else Icons.Rounded.ZoomIn,
        )
        OutlinedIconButton(
            border = if (portrait) {
                BorderStroke(0.dp, Color.Transparent)
            } else ButtonDefaults.outlinedButtonBorder,
            onClick = { viewModel.undo() },
            enabled = viewModel.lastPaths.isNotEmpty() || viewModel.paths.isNotEmpty()
        ) {
            Icon(Icons.AutoMirrored.Rounded.Undo, null)
        }
        OutlinedIconButton(
            border = if (portrait) {
                BorderStroke(0.dp, Color.Transparent)
            } else ButtonDefaults.outlinedButtonBorder,
            onClick = { viewModel.redo() },
            enabled = viewModel.undonePaths.isNotEmpty()
        ) {
            Icon(Icons.AutoMirrored.Rounded.Redo, null)
        }
    }

    val image: @Composable () -> Unit = @Composable {
        AnimatedContent(
            targetState = remember(viewModel.bitmap) {
                derivedStateOf {
                    viewModel.bitmap?.copy(
                        Bitmap.Config.ARGB_8888,
                        true
                    )?.asImageBitmap() ?: ImageBitmap(
                        configuration.screenWidthDp,
                        configuration.screenHeightDp
                    )
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
                brushSoftness = brushSoftness,
                onAddPath = viewModel::addPath,
                isRecoveryOn = viewModel.isRecoveryOn,
                modifier = Modifier
                    .padding(16.dp)
                    .aspectRatio(aspectRatio, portrait)
                    .fillMaxSize(),
                zoomEnabled = zoomEnabled,
                onErased = {}
            )
        }
    }

    val topAppBar = @Composable {
        AnimatedContent(
            targetState = viewModel.bitmap == null,
            transitionSpec = {
                fadeIn() togetherWith fadeOut() using SizeTransform(false)
            }
        ) { noBitmap ->
            if (noBitmap) {
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
                            Text(stringResource(R.string.background_remover))
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
                            onClick = { viewModel.shareBitmap { showConfetti() } }
                        ) {
                            Icon(Icons.Outlined.Share, null)
                        }
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
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
            }
        }
    }

    val controls = @Composable {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!portrait) {
                Row(
                    Modifier
                        .padding(top = 8.dp)
                        .container(CircleShape)
                ) {
                    secondaryControls()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
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
                value = strokeWidth.value,
                onValueChange = { strokeWidth = it.pt }
            )
            BrushSoftnessSelector(
                modifier = Modifier
                    .padding(top = 8.dp, end = 16.dp, start = 16.dp),
                value = brushSoftness.value,
                onValueChange = { brushSoftness = it.pt }
            )
            TrimImageToggle(
                checked = viewModel.trimImage,
                onCheckedChange = { viewModel.setTrimImage(it) },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
            SaveExifWidget(
                imageFormat = viewModel.imageFormat,
                checked = viewModel.saveExif,
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

    AnimatedContent(
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        targetState = viewModel.bitmap == null
    ) { noBitmap ->
        if (noBitmap) {
            Box(Modifier.fillMaxSize()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    topAppBar()
                    Column(
                        Modifier.verticalScroll(rememberScrollState())
                    ) {
                        ImageNotPickedWidget(
                            onPickImage = pickImage,
                            modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(108.dp))
                    }
                }
                EnhancedFloatingActionButton(
                    onClick = pickImage,
                    modifier = Modifier
                        .align(settingsState.fabAlignment)
                        .navigationBarsPadding()
                        .padding(16.dp),
                    content = {
                        Spacer(Modifier.width(16.dp))
                        Icon(Icons.Rounded.AddPhotoAlternate, null)
                        Spacer(Modifier.width(16.dp))
                        Text(stringResource(R.string.pick_image_alt))
                        Spacer(Modifier.width(16.dp))
                    }
                )
            }
        } else {
            if (portrait) {
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
                                        EnhancedFloatingActionButton(
                                            onClick = pickImage,
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                                contentDescription = null
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        EnhancedFloatingActionButton(
                                            onClick = saveBitmap
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Save,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            )
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                controls()
                            }
                        }
                    },
                    content = {
                        Column(
                            Modifier.padding(it),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            topAppBar()
                            image()
                        }
                    }
                )
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
                                .container(
                                    shape = RectangleShape,
                                    resultPadding = 0.dp,
                                    color = MaterialTheme.colorScheme.surfaceContainer
                                )
                                .weight(1.2f)
                                .clipToBounds(),
                            contentAlignment = Alignment.Center
                        ) {
                            image()
                        }
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
                            EnhancedFloatingActionButton(
                                onClick = pickImage,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AddPhotoAlternate,
                                    contentDescription = null
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            EnhancedFloatingActionButton(
                                onClick = saveBitmap
                            ) {
                                Icon(imageVector = Icons.Rounded.Save, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }


    if (viewModel.isSaving || viewModel.isImageLoading || viewModel.isErasingBG) {
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

    DrawLockScreenOrientation(orientation = viewModel.orientation)
}