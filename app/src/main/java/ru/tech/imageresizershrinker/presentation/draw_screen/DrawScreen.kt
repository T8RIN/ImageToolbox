@file:Suppress("PrivatePropertyName")

package ru.tech.imageresizershrinker.presentation.draw_screen


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
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
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.FormatPaint
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.ScreenLockRotation
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.getAppColorTuple
import com.t8rin.dynamic.theme.observeAsState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.draw.DrawBehavior
import ru.tech.imageresizershrinker.domain.image.draw.DrawMode
import ru.tech.imageresizershrinker.domain.image.draw.pt
import ru.tech.imageresizershrinker.presentation.draw_screen.components.BitmapDrawer
import ru.tech.imageresizershrinker.presentation.draw_screen.components.BrushSoftnessSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawAlphaSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawArrowsSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawBackgroundSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawColorSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawModeSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.LineWidthSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.OpenColorPickerCard
import ru.tech.imageresizershrinker.presentation.draw_screen.components.PickColorFromImageSheet
import ru.tech.imageresizershrinker.presentation.draw_screen.viewModel.DrawViewModel
import ru.tech.imageresizershrinker.presentation.root.icons.material.Eraser
import ru.tech.imageresizershrinker.presentation.root.model.PtSaver
import ru.tech.imageresizershrinker.presentation.root.theme.mixedContainer
import ru.tech.imageresizershrinker.presentation.root.theme.onMixedContainer
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ImageUtils.restrict
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.root.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.containerFabBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.presentation.root.widget.other.DrawLockScreenOrientation
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.presentation.root.widget.saver.ColorSaver
import ru.tech.imageresizershrinker.presentation.root.widget.saver.DrawModeSaver
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
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
            transformations = listOf()
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
                value = viewModel.backgroundColor,
                onColorChange = viewModel::updateBackgroundColor
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

    val bitmap = viewModel.bitmap ?: (viewModel.drawBehavior as? DrawBehavior.Background)?.run {
        remember { ImageBitmap(width, height).asAndroidBitmap() }
    } ?: remember {
        ImageBitmap(
            configuration.screenWidthDp,
            configuration.screenHeightDp
        ).asAndroidBitmap()
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val topAppBar: @Composable () -> Unit = {
        AnimatedContent(
            targetState = viewModel.drawBehavior,
            transitionSpec = {
                fadeIn() togetherWith fadeOut() using SizeTransform(false)
            }
        ) { drawBehavior ->
            if (drawBehavior !is DrawBehavior.None) {
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
                                Icon(Icons.Rounded.Tune, null)
                            }
                        }
                        IconButton(
                            onClick = { viewModel.shareBitmap { showConfetti() } },
                            enabled = viewModel.drawBehavior !is DrawBehavior.None
                        ) {
                            Icon(Icons.Outlined.Share, null)
                        }
                        IconButton(
                            onClick = {
                                viewModel.clearDrawing()
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
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    }
                )
            } else {
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
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        TopAppBarEmoji()
                    }
                )
            }
        }
    }

    val drawBox = @Composable {
        AnimatedContent(
            targetState = remember(bitmap) {
                derivedStateOf {
                    bitmap.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()
                }
            }.value,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { imageBitmap ->
            val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
            BitmapDrawer(
                imageBitmap = imageBitmap,
                paths = viewModel.paths,
                strokeWidth = strokeWidth,
                brushSoftness = brushSoftness,
                drawColor = drawColor.copy(alpha),
                onAddPath = viewModel::addPath,
                isEraserOn = isEraserOn,
                drawMode = drawMode,
                modifier = Modifier
                    .padding(16.dp)
                    .aspectRatio(aspectRatio, portrait)
                    .fillMaxSize(),
                zoomEnabled = zoomEnabled,
                onDraw = {},
                imageManager = viewModel.getImageManager(),
                drawArrowsEnabled = drawArrowsEnabled,
                backgroundColor = viewModel.backgroundColor
            )
        }
    }


    val screenWidth = LocalConfiguration.current.screenWidthDp
    val easing = CubicBezierEasing(0.48f, 0.19f, 0.05f, 1.03f)
    AnimatedContent(
        transitionSpec = {
            if (this.targetState !is DrawBehavior.None) {
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
        },
        targetState = viewModel.drawBehavior
    ) { drawBehavior ->
        if (drawBehavior is DrawBehavior.None) {
            Box(Modifier.fillMaxSize()) {
                val showBackgroundDrawingSetup = rememberSaveable { mutableStateOf(false) }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    topAppBar()
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
                                onClick = pickImage,
                                icon = Icons.Rounded.Image,
                                title = stringResource(R.string.draw_on_image),
                                subtitle = stringResource(R.string.draw_on_image_sub),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            PreferenceItem(
                                onClick = { showBackgroundDrawingSetup.value = true },
                                icon = Icons.Rounded.FormatPaint,
                                title = stringResource(R.string.draw_on_background),
                                subtitle = stringResource(R.string.draw_on_background_sub),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            Box(contentAlignment = Alignment.Center) {
                                PreferenceRowSwitch(
                                    resultModifier = Modifier
                                        .padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        )
                                        .widthIn(max = 360.dp),
                                    modifier = Modifier.padding(top = 4.dp),
                                    applyHorPadding = false,
                                    onClick = {
                                        viewModel.toggleLockDrawOrientation()
                                    },
                                    title = stringResource(R.string.lock_draw_orientation),
                                    subtitle = stringResource(R.string.lock_draw_orientation_sub),
                                    checked = LocalSettingsState.current.lockDrawOrientation,
                                    startContent = {
                                        Icon(
                                            imageVector = Icons.Rounded.ScreenLockRotation,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 16.dp)
                                        )
                                    }
                                )
                            }
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
                            onClick = pickImage,
                            modifier = Modifier
                                .navigationBarsPadding()
                                .padding(16.dp)
                                .containerFabBorder(),
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

                val density = LocalDensity.current
                var height by remember(showBackgroundDrawingSetup.value, configuration) {
                    mutableIntStateOf(with(density) { configuration.screenHeightDp.dp.roundToPx() })
                }
                var width by remember(showBackgroundDrawingSetup.value, configuration) {
                    mutableIntStateOf(with(density) { configuration.screenWidthDp.dp.roundToPx() })
                }
                var sheetBackgroundColor by remember { mutableStateOf(Color.White) }
                SimpleSheet(
                    title = {
                        TitleItem(
                            text = stringResource(R.string.draw),
                            icon = Icons.Rounded.Draw
                        )
                    },
                    confirmButton = {
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = {
                                showBackgroundDrawingSetup.value = false
                                viewModel.startDrawOnBackground(
                                    reqWidth = width,
                                    reqHeight = height,
                                    color = sheetBackgroundColor
                                )
                            }
                        ) {
                            AutoSizeText(stringResource(R.string.ok))
                        }
                    },
                    sheetContent = {
                        Box {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                Row(
                                    Modifier
                                        .padding(16.dp)
                                        .container(shape = RoundedCornerShape(24.dp))
                                ) {
                                    RoundedTextField(
                                        value = width.takeIf { it != 0 }?.toString() ?: "",
                                        onValueChange = {
                                            width = it.restrict(8192).toIntOrNull() ?: 0
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        label = {
                                            Text(stringResource(R.string.width, " "))
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(
                                                start = 8.dp,
                                                top = 8.dp,
                                                bottom = 8.dp,
                                                end = 4.dp
                                            )
                                    )
                                    RoundedTextField(
                                        value = height.takeIf { it != 0 }?.toString() ?: "",
                                        onValueChange = {
                                            height = it.restrict(8192).toIntOrNull() ?: 0
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        label = {
                                            Text(stringResource(R.string.height, " "))
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(
                                                start = 4.dp,
                                                top = 8.dp,
                                                bottom = 8.dp,
                                                end = 8.dp
                                            ),
                                    )
                                }
                                DrawBackgroundSelector(
                                    value = sheetBackgroundColor,
                                    onColorChange = { sheetBackgroundColor = it }
                                )
                            }
                        }
                    },
                    visible = showBackgroundDrawingSetup
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
                                        if (drawBehavior is DrawBehavior.Image) {
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
                    content = {
                        Column(
                            Modifier.padding(it),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            topAppBar()
                            drawBox()
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
                            drawBox()
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
                                secondaryControls()
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
                            FloatingActionButton(
                                onClick = pickImage,
                                modifier = Modifier.autoElevatedBorder(),
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            ) {
                                Icon(Icons.Rounded.AddPhotoAlternate, null)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            FloatingActionButton(
                                onClick = saveBitmap,
                                modifier = Modifier.autoElevatedBorder(),
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            ) {
                                Icon(Icons.Rounded.Save, null)
                            }
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

    var colorPickerColor by rememberSaveable(stateSaver = ColorSaver) { mutableStateOf(Color.Black) }
    PickColorFromImageSheet(
        visible = showPickColorSheet,
        bitmap = viewModel.colorPickerBitmap,
        onColorChange = { colorPickerColor = it },
        color = colorPickerColor
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