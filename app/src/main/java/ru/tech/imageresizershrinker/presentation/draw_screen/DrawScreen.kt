@file:Suppress("PrivatePropertyName")

package ru.tech.imageresizershrinker.presentation.draw_screen


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.getAppColorTuple
import com.t8rin.dynamic.theme.observeAsState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.presentation.draw_screen.components.BrushSoftnessSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawAlphaSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawArrowsSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawBackgroundSelector
import ru.tech.imageresizershrinker.domain.image.draw.DrawBehavior
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawColorSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawHost
import ru.tech.imageresizershrinker.domain.image.draw.DrawMode
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawModeSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.LineWidthSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.OpenColorPickerCard
import ru.tech.imageresizershrinker.presentation.draw_screen.components.PickColorFromImageSheet
import ru.tech.imageresizershrinker.presentation.draw_screen.viewModel.DrawViewModel
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.PtSaver
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.pt
import ru.tech.imageresizershrinker.presentation.root.icons.material.Eraser
import ru.tech.imageresizershrinker.presentation.root.theme.mixedContainer
import ru.tech.imageresizershrinker.presentation.root.theme.onMixedContainer
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.theme.toColor
import ru.tech.imageresizershrinker.presentation.root.transformation.filter.SaturationFilter
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.root.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.containerFabBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.widget.other.DrawLockScreenOrientation
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.saver.ColorSaver
import ru.tech.imageresizershrinker.presentation.root.widget.saver.DrawModeSaver
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass

@SuppressLint("AutoboxingStateCreation")
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
            viewModel.setUri(it)
            viewModel.decodeBitmapByUri(
                uri = it,
                onGetMimeType = viewModel::updateMimeType,
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
    LaunchedEffect(viewModel.uri, viewModel.paths) {
        viewModel.getBitmapFromUriWithTransformations(
            uri = viewModel.uri,
            transformations = listOf(SaturationFilter(context, 2f))
        )?.let {
            if (allowChangeColor) themeState.updateColorByImage(it)
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
                    onGetBitmap = viewModel::updateBitmap,
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

    val showPickColorSheet = rememberSaveable { mutableStateOf(false) }

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

    var strokeWidth by rememberSaveable(
        viewModel.drawBehavior,
        stateSaver = PtSaver
    ) { mutableStateOf(20.pt) }
    var backgroundColor by rememberSaveable(
        stateSaver = ColorSaver,
        inputs = arrayOf(viewModel.drawBehavior)
    ) {
        mutableStateOf(
            if (viewModel.drawBehavior is DrawBehavior.Background) {
                (viewModel.drawBehavior as DrawBehavior.Background).color.toColor()
            } else Color.Transparent
        )
    }
    var drawColor by rememberSaveable(
        stateSaver = ColorSaver,
        inputs = arrayOf(viewModel.drawBehavior)
    ) { mutableStateOf(Color.Black) }
    var isEraserOn by rememberSaveable(viewModel.drawBehavior) { mutableStateOf(false) }
    var drawMode by rememberSaveable(
        stateSaver = DrawModeSaver,
        inputs = arrayOf(viewModel.drawBehavior)
    ) { mutableStateOf(DrawMode.Pen) }
    var alpha by rememberSaveable(viewModel.drawBehavior, drawMode) {
        mutableFloatStateOf(if (drawMode is DrawMode.Highlighter) 0.4f else 1f)
    }
    var brushSoftness by rememberSaveable(viewModel.drawBehavior, drawMode, stateSaver = PtSaver) {
        mutableStateOf(if (drawMode is DrawMode.Neon) 35.pt else 0.pt)
    }
    var drawArrowsEnabled by rememberSaveable(viewModel.drawBehavior) {
        mutableStateOf(false)
    }

    val controls = @Composable {
        OpenColorPickerCard(
            onOpen = {
                viewModel.openColorPicker()
                showPickColorSheet.value = true
            }
        )
        LineWidthSelector(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp
            ),
            value = strokeWidth.value,
            onValueChange = { strokeWidth = it.pt }
        )
        AnimatedVisibility(
            visible = drawMode !is DrawMode.Highlighter && drawMode !is DrawMode.PathEffect,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            BrushSoftnessSelector(
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp, start = 16.dp),
                value = brushSoftness.value,
                onValueChange = { brushSoftness = it.pt }
            )
        }
        if (viewModel.drawBehavior is DrawBehavior.Background) {
            DrawBackgroundSelector(
                value = backgroundColor,
                onColorChange = { backgroundColor = it }
            )
        } else {
            Spacer(Modifier.height(16.dp))
        }
        AnimatedVisibility(
            visible = drawMode !is DrawMode.PathEffect,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            DrawColorSelector(
                drawColor = drawColor,
                onColorChange = { drawColor = it }
            )
        }
        AnimatedVisibility(
            visible = drawMode !is DrawMode.Neon && drawMode !is DrawMode.PathEffect,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            DrawAlphaSelector(
                value = alpha,
                onValueChange = { alpha = it }
            )
        }
        DrawModeSelector(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            drawMode = drawMode,
            onDrawModeChange = { drawMode = it }
        )
        AnimatedVisibility(
            visible = !isEraserOn,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            DrawArrowsSelector(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
                checked = drawArrowsEnabled,
                onCheckedChange = { drawArrowsEnabled = it }
            )
        }
        SaveExifWidget(
            modifier = Modifier.padding(horizontal = 16.dp),
            selected = viewModel.saveExif,
            imageFormat = viewModel.imageFormat,
            onCheckedChange = viewModel::setSaveExif,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer
        )
        ExtensionGroup(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            enabled = viewModel.drawBehavior !is DrawBehavior.None,
            imageFormat = viewModel.imageFormat,
            onFormatChange = {
                viewModel.updateMimeType(it)
            },
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer
        )
    }

    val secondaryControls = @Composable {
        Row(
            Modifier
                .padding(16.dp)
                .container(shape = CircleShape)
        ) {
            switch()
            EnhancedIconButton(
                containerColor = Color.Transparent,
                borderColor = MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.1f
                ),
                onClick = viewModel::undo,
                enabled = viewModel.lastPaths.isNotEmpty() || viewModel.paths.isNotEmpty()
            ) {
                Icon(Icons.AutoMirrored.Rounded.Undo, null)
            }
            EnhancedIconButton(
                containerColor = Color.Transparent,
                borderColor = MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.1f
                ),
                onClick = viewModel::redo,
                enabled = viewModel.undonePaths.isNotEmpty()
            ) {
                Icon(Icons.AutoMirrored.Rounded.Redo, null)
            }
            EnhancedIconButton(
                borderColor = MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.1f
                ),
                containerColor = animateColorAsState(
                    if (isEraserOn) MaterialTheme.colorScheme.mixedContainer
                    else Color.Transparent
                ).value,
                contentColor = animateColorAsState(
                    if (isEraserOn) MaterialTheme.colorScheme.onMixedContainer
                    else MaterialTheme.colorScheme.onSurface
                ).value,
                onClick = {
                    isEraserOn = !isEraserOn
                }
            ) {
                Icon(Icons.Rounded.Eraser, null)
            }
        }
    }

    val content: @Composable (PaddingValues) -> Unit = { paddingValues ->
        DrawHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            navController = viewModel.navController,
            portrait = portrait,
            onToggleLockOrientation = viewModel::toggleLockDrawOrientation,
            zoomEnabled = zoomEnabled,
            clearDrawing = viewModel::clearDrawing,
            onSaveRequest = saveBitmap,
            onPickImage = pickImage,
            startDrawOnBackground = viewModel::startDrawOnBackground,
            scaffoldState = scaffoldState,
            isBitmapChanged = viewModel.isBitmapChanged,
            onBack = onBack,
            imageManager = viewModel.getImageManager(),
            onShare = { viewModel.shareBitmap { showConfetti() } },
            paths = viewModel.paths,
            isEraserOn = isEraserOn,
            drawArrowsEnabled = drawArrowsEnabled,
            drawMode = drawMode,
            backgroundColor = backgroundColor,
            drawColor = drawColor.copy(alpha),
            drawAlpha = alpha,
            strokeWidth = strokeWidth,
            bitmap = viewModel.bitmap ?: (viewModel.drawBehavior as? DrawBehavior.Background)?.run {
                remember { ImageBitmap(width, height).asAndroidBitmap() }
            } ?: remember {
                ImageBitmap(
                    configuration.screenWidthDp,
                    configuration.screenHeightDp
                ).asAndroidBitmap()
            },
            brushSoftness = brushSoftness,
            addPath = viewModel::addPath,
            onDraw = viewModel::updateDrawing,
            controls = controls,
            secondaryControls = secondaryControls
        )
    }


    val screenWidth = LocalConfiguration.current.screenWidthDp
    val easing = CubicBezierEasing(0.48f, 0.19f, 0.05f, 1.03f)
    AnimatedContent(
        targetState = portrait && viewModel.drawBehavior !is DrawBehavior.None,
        transitionSpec = {
            if (this.targetState) {
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
            }
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
                                    onClick = viewModel::undo,
                                    enabled = viewModel.lastPaths.isNotEmpty() || viewModel.paths.isNotEmpty()
                                ) {
                                    Icon(Icons.AutoMirrored.Rounded.Undo, null)
                                }
                                IconButton(
                                    onClick = viewModel::redo,
                                    enabled = viewModel.undonePaths.isNotEmpty()
                                ) {
                                    Icon(Icons.AutoMirrored.Rounded.Redo, null)
                                }
                                EnhancedIconButton(
                                    containerColor = animateColorAsState(
                                        if (isEraserOn) MaterialTheme.colorScheme.mixedContainer
                                        else Color.Transparent
                                    ).value,
                                    contentColor = animateColorAsState(
                                        if (isEraserOn) MaterialTheme.colorScheme.onMixedContainer
                                        else MaterialTheme.colorScheme.onSurface
                                    ).value,
                                    borderColor = animateColorAsState(
                                        if (isEraserOn) MaterialTheme.colorScheme.outlineVariant
                                        else Color.Transparent
                                    ).value,
                                    onClick = { isEraserOn = !isEraserOn }
                                ) {
                                    Icon(Icons.Rounded.Eraser, null)
                                }
                            },
                            floatingActionButton = {
                                Row {
                                    if (viewModel.drawBehavior is DrawBehavior.Image) {
                                        FloatingActionButton(
                                            onClick = pickImage,
                                            modifier = Modifier.containerFabBorder(),
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                        ) {
                                            Icon(Icons.Rounded.AddPhotoAlternate, null)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    FloatingActionButton(
                                        onClick = saveBitmap,
                                        modifier = Modifier.containerFabBorder(),
                                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                    ) {
                                        Icon(Icons.Rounded.Save, null)
                                    }
                                }
                            }
                        )
                        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                            item {
                                controls()
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

    if (viewModel.isSaving || viewModel.isImageLoading) {
        LoadingDialog(viewModel.isSaving) {
            viewModel.cancelSaving()
        }
    }

    PickColorFromImageSheet(
        visible = showPickColorSheet,
        bitmap = viewModel.colorPickerBitmap,
        onColorChange = viewModel::updateColor,
        color = viewModel.color
    )

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

    DrawLockScreenOrientation(
        orientation = remember(viewModel.bitmap) {
            viewModel.calculateScreenOrientationBasedOnBitmap(viewModel.bitmap)
        }
    )
}