package ru.tech.imageresizershrinker.presentation.draw_screen


import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.getAppColorTuple
import com.t8rin.dynamic.theme.observeAsState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawAlphaSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawBackgroundSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawBehavior
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawColorSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawHost
import ru.tech.imageresizershrinker.presentation.draw_screen.components.LineWidthSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.viewModel.DrawViewModel
import ru.tech.imageresizershrinker.presentation.theme.icons.Eraser
import ru.tech.imageresizershrinker.presentation.theme.mixedColor
import ru.tech.imageresizershrinker.presentation.theme.onMixedColor
import ru.tech.imageresizershrinker.presentation.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.utils.coil.UpscaleTransformation
import ru.tech.imageresizershrinker.presentation.utils.coil.filters.SaturationFilter
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.getBitmapByUri
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.getBitmapFromUriWithTransformations
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.overlayWith
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.shareBitmap
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.utils.helper.compressFormat
import ru.tech.imageresizershrinker.utils.helper.extension
import ru.tech.imageresizershrinker.presentation.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.utils.storage.LocalFileController
import ru.tech.imageresizershrinker.utils.storage.Picker
import ru.tech.imageresizershrinker.utils.storage.localImagePickerMode
import ru.tech.imageresizershrinker.utils.storage.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.widget.other.LockScreenOrientation
import ru.tech.imageresizershrinker.presentation.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: DrawViewModel = hiltViewModel()
) {
    val settingsState = LocalSettingsState.current
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHost.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val appColorTuple = getAppColorTuple(
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = settingsState.isDynamicColors,
        darkTheme = settingsState.isNightMode
    )

    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiController.showEmpty()
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.drawBehavior !is DrawBehavior.None && viewModel.isBitmapChanged) showExitDialog =
            true
        else if (viewModel.drawBehavior !is DrawBehavior.None) {
            viewModel.resetDrawBehavior()
            themeState.updateColorTuple(appColorTuple)
        } else onGoBack()
    }

    LaunchedEffect(uriState) {
        uriState?.let {
            viewModel.setUri(it) { uri ->
                context.calculateScreenOrientationBasedOnUri(uri)
            }
        }
    }
    LaunchedEffect(viewModel.uri, viewModel.drawController?.paths) {
        context.getBitmapFromUriWithTransformations(
            uri = viewModel.uri,
            transformations = listOf(SaturationFilter(context, 2f))
        )?.let {
            val overlay = viewModel.drawController?.getBitmap()
            if (allowChangeColor) {
                if (overlay != null) {
                    themeState.updateColorByImage(it.overlayWith(overlay))
                } else {
                    themeState.updateColorByImage(it)
                }
            }
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
                viewModel.setUri(it) { uri ->
                    context.calculateScreenOrientationBasedOnUri(uri)
                }
            }
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    var showSaveLoading by rememberSaveable { mutableStateOf(false) }

    val fileController = LocalFileController.current
    val saveBitmap: () -> Unit = {
        showSaveLoading = true
        viewModel.saveBitmap(
            getBitmap = { uri ->
                context.getBitmapFromUriWithTransformations(
                    uri = uri,
                    originalSize = false,
                    transformations = listOf(UpscaleTransformation())
                )
            },
            fileController = fileController,
        ) { success ->
            if (!success) context.requestStoragePermission()
            else {
                scope.launch {
                    toastHostState.showToast(
                        context.getString(
                            R.string.saved_to,
                            fileController.savingPath
                        ),
                        Icons.Rounded.Save
                    )
                }
                showConfetti()
            }
            showSaveLoading = false
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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

    var zoomEnabled by rememberSaveable(viewModel.drawBehavior) { mutableStateOf(false) }

    val switch = @Composable {
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
                        if (!zoom) Icons.Rounded.Draw else Icons.Rounded.ZoomIn,
                        null,
                        Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            }
        )
    }

    val content: @Composable (PaddingValues) -> Unit = { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (viewModel.drawBehavior is DrawBehavior.None) {
                    LargeTopAppBar(
                        scrollBehavior = scrollBehavior,
                        modifier = Modifier.drawHorizontalStroke(),
                        title = {
                            Marquee(
                                edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            ) {
                                Text(stringResource(R.string.draw))
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
                } else {
                    TopAppBar(
                        modifier = Modifier.drawHorizontalStroke(),
                        title = {
                            Marquee(
                                edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            ) {
                                Text(stringResource(R.string.draw))
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
                                    Icon(Icons.Rounded.Build, null)
                                }
                            }
                            IconButton(
                                onClick = {
                                    viewModel.processBitmapForSharing(
                                        getBitmap = { uri ->
                                            context.getBitmapFromUriWithTransformations(
                                                uri = uri,
                                                originalSize = false,
                                                transformations = listOf(UpscaleTransformation())
                                            )
                                        }
                                    ) { bitmap ->
                                        showSaveLoading = true
                                        context.shareBitmap(
                                            bitmap = bitmap,
                                            compressFormat = viewModel.mimeType.extension.compressFormat
                                        ) {
                                            showSaveLoading = false
                                            showConfetti()
                                        }
                                    }
                                },
                                enabled = viewModel.drawBehavior !is DrawBehavior.None
                            ) {
                                Icon(Icons.Outlined.Share, null)
                            }
                            IconButton(
                                onClick = {
                                    viewModel.drawController?.clearDrawing()
                                },
                                enabled = viewModel.drawBehavior !is DrawBehavior.None && viewModel.isBitmapChanged
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
                DrawHost(
                    modifier = Modifier.weight(1f),
                    navController = viewModel.navController,
                    drawController = viewModel.drawController,
                    portrait = portrait,
                    zoomEnabled = zoomEnabled,
                    onGetDrawController = viewModel::updateDrawController,
                    onSaveRequest = saveBitmap,
                    mimeType = viewModel.mimeType,
                    onMimeTypeChange = viewModel::updateMimeType,
                    uri = viewModel.uri,
                    onPickImage = pickImage,
                    switch = switch,
                    startDrawOnBackground = viewModel::startDrawOnBackground
                )
            }
        }
    }

    AnimatedContent(
        targetState = portrait && viewModel.drawBehavior !is DrawBehavior.None,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }
    ) { showScaffold ->
        if (showScaffold) {
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
                                switch()
                                IconButton(
                                    onClick = { viewModel.drawController?.undo() },
                                    enabled = !viewModel.drawController?.lastPaths.isNullOrEmpty() || !viewModel.drawController?.paths.isNullOrEmpty()
                                ) {
                                    Icon(Icons.Rounded.Undo, null)
                                }
                                IconButton(
                                    onClick = { viewModel.drawController?.redo() },
                                    enabled = !viewModel.drawController?.undonePaths.isNullOrEmpty()
                                ) {
                                    Icon(Icons.Rounded.Redo, null)
                                }
                                val isEraserOn = viewModel.drawController?.isEraserOn == true
                                OutlinedIconButton(
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = animateColorAsState(
                                            if (isEraserOn) MaterialTheme.colorScheme.mixedColor
                                            else Color.Transparent
                                        ).value,
                                        contentColor = animateColorAsState(
                                            if (isEraserOn) MaterialTheme.colorScheme.onMixedColor
                                            else MaterialTheme.colorScheme.onSurface
                                        ).value,
                                        disabledContainerColor = Color.Transparent
                                    ),
                                    border = BorderStroke(
                                        max(settingsState.borderWidth, 1.dp), animateColorAsState(
                                            if (isEraserOn) MaterialTheme.colorScheme.outlineVariant
                                            else Color.Transparent
                                        ).value
                                    ),
                                    onClick = { viewModel.drawController?.toggleEraser() }
                                ) {
                                    Icon(Icons.Rounded.Eraser, null)
                                }
                            },
                            floatingActionButton = {
                                Row {
                                    if (viewModel.drawBehavior is DrawBehavior.Image) {
                                        FloatingActionButton(
                                            onClick = pickImage,
                                            modifier = Modifier.fabBorder(),
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                        ) {
                                            Icon(Icons.Rounded.AddPhotoAlternate, null)
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                    }
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
                        Divider()
                        viewModel.drawController?.let { drawController ->
                            LazyColumn {
                                item {
                                    if (viewModel.drawBehavior is DrawBehavior.Background) {
                                        DrawBackgroundSelector(drawController)
                                    } else {
                                        Spacer(Modifier.height(16.dp))
                                    }
                                    DrawColorSelector(drawController)
                                    DrawAlphaSelector(drawController)
                                    LineWidthSelector(drawController)
                                    ExtensionGroup(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .navigationBarsPadding(),
                                        orientation = Orientation.Horizontal,
                                        enabled = viewModel.drawBehavior !is DrawBehavior.None,
                                        mimeTypeInt = viewModel.mimeType,
                                        onMimeChange = {
                                            viewModel.updateMimeType(it)
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                content = content
            )
        } else {
            content(PaddingValues())
        }
    }

    if (showSaveLoading || viewModel.isLoading) {
        LoadingDialog()
    }

    ExitWithoutSavingDialog(
        onExit = {
            if (viewModel.drawBehavior !is DrawBehavior.None) {
                viewModel.resetDrawBehavior()
                themeState.updateColorTuple(appColorTuple)
            } else onGoBack()
        },
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    BackHandler(onBack = onBack)

    LockScreenOrientation(orientation = viewModel.drawBehavior.orientation)
}

private suspend fun Context.calculateScreenOrientationBasedOnUri(uri: Uri): Int {
    val bmp = getBitmapByUri(uri = uri, originalSize = false)
    val imageRatio = (bmp?.width ?: 0) / (bmp?.height?.toFloat() ?: 1f)
    return if (imageRatio <= 1f) {
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    } else {
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}