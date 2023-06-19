@file:Suppress("UNUSED_PARAMETER")

package ru.tech.imageresizershrinker.draw_screen


import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Paint
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.FormatPaint
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.viewmodel.compose.viewModel
import com.t8rin.drawbox.domain.DrawController
import com.t8rin.drawbox.presentation.compose.DrawBox
import com.t8rin.drawbox.presentation.model.DrawPath
import com.t8rin.drawbox.presentation.model.PaintOptions
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.getAppColorTuple
import com.t8rin.dynamic.theme.observeAsState
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.draw_screen.components.DrawAlphaSelector
import ru.tech.imageresizershrinker.draw_screen.components.DrawBackgroundSelector
import ru.tech.imageresizershrinker.draw_screen.components.DrawBehavior
import ru.tech.imageresizershrinker.draw_screen.components.DrawColorSelector
import ru.tech.imageresizershrinker.draw_screen.components.LineWidthSelector
import ru.tech.imageresizershrinker.draw_screen.viewModel.DrawViewModel
import ru.tech.imageresizershrinker.main_screen.components.PreferenceItem
import ru.tech.imageresizershrinker.theme.icons.Eraser
import ru.tech.imageresizershrinker.theme.mixedColor
import ru.tech.imageresizershrinker.theme.onMixedColor
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.LocalConfettiController
import ru.tech.imageresizershrinker.utils.coil.UpscaleTransformation
import ru.tech.imageresizershrinker.utils.coil.filters.SaturationFilter
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.getBitmapByUri
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.getBitmapFromUriWithTransformations
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.overlayWith
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.restrict
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.shareBitmap
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.utils.helper.compressFormat
import ru.tech.imageresizershrinker.utils.helper.extension
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.utils.modifier.navBarsPaddingOnlyIfTheyAtTheBottom
import ru.tech.imageresizershrinker.utils.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.utils.storage.LocalFileController
import ru.tech.imageresizershrinker.utils.storage.Picker
import ru.tech.imageresizershrinker.utils.storage.localImagePickerMode
import ru.tech.imageresizershrinker.utils.storage.rememberImagePicker
import ru.tech.imageresizershrinker.widget.LoadingDialog
import ru.tech.imageresizershrinker.widget.LocalToastHost
import ru.tech.imageresizershrinker.widget.LockScreenOrientation
import ru.tech.imageresizershrinker.widget.TitleItem
import ru.tech.imageresizershrinker.widget.TopAppBarEmoji
import ru.tech.imageresizershrinker.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.widget.image.Picture
import ru.tech.imageresizershrinker.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.widget.text.Marquee
import ru.tech.imageresizershrinker.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.widget.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: DrawViewModel = viewModel()
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
    val showBackgroundDrawingSetup = rememberSaveable { mutableStateOf(false) }

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
        remember(LocalLifecycleOwner.current.lifecycle.observeAsState(), sizeClass, configuration) {
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
                                    viewModel.drawController?.clearPaths()
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
                AnimatedContent(
                    modifier = Modifier.weight(1f),
                    targetState = Pair(
                        viewModel.drawBehavior,
                        portrait
                    )
                ) { (drawBehavior, isPortrait) ->
                    val drawController = viewModel.drawController
                    when (drawBehavior) {
                        is DrawBehavior.Background -> {
                            if (isPortrait) {
                                DrawBox(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    drawController = drawController,
                                    drawingModifier = Modifier.border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant()
                                    ),
                                    onGetDrawController = viewModel::updateDrawController
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(drawBehavior.run { width / height.toFloat() })
                                            .fillMaxSize()
                                            .background(
                                                drawController?.backgroundColor ?: Color.Transparent
                                            )
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        Modifier.weight(0.8f)
                                    ) {
                                        DrawBox(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp)
                                                .navBarsPaddingOnlyIfTheyAtTheBottom(),
                                            drawController = drawController,
                                            drawingModifier = Modifier.border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.outlineVariant()
                                            ),
                                            onGetDrawController = viewModel::updateDrawController
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .aspectRatio(drawBehavior.run { width / height.toFloat() })
                                                    .fillMaxSize()
                                                    .background(
                                                        drawController?.backgroundColor
                                                            ?: Color.Transparent
                                                    )
                                            )
                                        }
                                    }
                                    Box(
                                        Modifier
                                            .fillMaxHeight()
                                            .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                            .background(MaterialTheme.colorScheme.outlineVariant())
                                    )
                                    drawController?.let { drawController ->
                                        LazyColumn(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            contentPadding = PaddingValues(
                                                bottom = WindowInsets
                                                    .navigationBars
                                                    .asPaddingValues()
                                                    .calculateBottomPadding() + WindowInsets.ime
                                                    .asPaddingValues()
                                                    .calculateBottomPadding(),
                                                top = if (drawBehavior is DrawBehavior.None) 20.dp else 0.dp,
                                            ),
                                            modifier = Modifier
                                                .weight(0.5f)
                                                .clipToBounds()
                                        ) {
                                            item {
                                                val border = BorderStroke(
                                                    settingsState.borderWidth,
                                                    MaterialTheme.colorScheme.outlineVariant(
                                                        luminance = 0.1f
                                                    )
                                                )
                                                Row(
                                                    Modifier
                                                        .padding(16.dp)
                                                        .block(shape = CircleShape)
                                                ) {
                                                    OutlinedIconButton(
                                                        border = border,
                                                        onClick = { drawController.undo() },
                                                        enabled = drawController.paths.isNotEmpty()
                                                    ) {
                                                        Icon(Icons.Rounded.Undo, null)
                                                    }
                                                    OutlinedIconButton(
                                                        border = border,
                                                        onClick = { drawController.redo() },
                                                        enabled = drawController.undonePaths.isNotEmpty()
                                                    ) {
                                                        Icon(Icons.Rounded.Redo, null)
                                                    }
                                                    val isEraserOn = drawController.isEraserOn
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
                                                        border = border,
                                                        onClick = { drawController.toggleEraser() }
                                                    ) {
                                                        Icon(Icons.Rounded.Eraser, null)
                                                    }
                                                }
                                                DrawBackgroundSelector(drawController)
                                                DrawColorSelector(drawController)
                                                DrawAlphaSelector(drawController)
                                                LineWidthSelector(drawController)
                                                ExtensionGroup(
                                                    modifier = Modifier
                                                        .padding(16.dp)
                                                        .navigationBarsPadding(),
                                                    orientation = Orientation.Horizontal,
                                                    enabled = drawBehavior !is DrawBehavior.None,
                                                    mimeTypeInt = viewModel.mimeType,
                                                    onMimeChange = {
                                                        viewModel.updateMimeType(it)
                                                    }
                                                )
                                            }
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

                        is DrawBehavior.Image -> {
                            if (isPortrait) {
                                DrawBox(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    drawController = drawController,
                                    drawingModifier = Modifier.border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant()
                                    ),
                                    onGetDrawController = viewModel::updateDrawController
                                ) {
                                    Picture(
                                        model = viewModel.uri,
                                        contentScale = ContentScale.Fit,
                                        shape = RectangleShape,
                                        transformations = listOf(UpscaleTransformation())
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        Modifier.weight(0.8f)
                                    ) {
                                        DrawBox(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp)
                                                .navBarsPaddingOnlyIfTheyAtTheBottom(),
                                            drawController = drawController,
                                            drawingModifier = Modifier.border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.outlineVariant()
                                            ),
                                            onGetDrawController = viewModel::updateDrawController
                                        ) {
                                            Picture(
                                                model = viewModel.uri,
                                                contentScale = ContentScale.Fit,
                                                shape = RectangleShape,
                                                transformations = listOf(UpscaleTransformation())
                                            )
                                        }
                                    }
                                    Box(
                                        Modifier
                                            .fillMaxHeight()
                                            .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                            .background(MaterialTheme.colorScheme.outlineVariant())
                                    )
                                    drawController?.let { drawController ->
                                        LazyColumn(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            contentPadding = PaddingValues(
                                                bottom = WindowInsets
                                                    .navigationBars
                                                    .asPaddingValues()
                                                    .calculateBottomPadding() + WindowInsets.ime
                                                    .asPaddingValues()
                                                    .calculateBottomPadding(),
                                                top = if (drawBehavior is DrawBehavior.None) 20.dp else 0.dp,
                                            ),
                                            modifier = Modifier
                                                .weight(0.5f)
                                                .clipToBounds()
                                        ) {
                                            item {
                                                val border = BorderStroke(
                                                    settingsState.borderWidth,
                                                    MaterialTheme.colorScheme.outlineVariant(
                                                        luminance = 0.1f
                                                    )
                                                )
                                                Row(
                                                    Modifier
                                                        .padding(16.dp)
                                                        .block(shape = CircleShape)
                                                ) {
                                                    OutlinedIconButton(
                                                        border = border,
                                                        onClick = { drawController.undo() },
                                                        enabled = drawController.paths.isNotEmpty()
                                                    ) {
                                                        Icon(Icons.Rounded.Undo, null)
                                                    }
                                                    OutlinedIconButton(
                                                        border = border,
                                                        onClick = { drawController.redo() },
                                                        enabled = drawController.undonePaths.isNotEmpty()
                                                    ) {
                                                        Icon(Icons.Rounded.Redo, null)
                                                    }
                                                    val isEraserOn = drawController.isEraserOn
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
                                                        border = border,
                                                        onClick = { drawController.toggleEraser() }
                                                    ) {
                                                        Icon(Icons.Rounded.Eraser, null)
                                                    }
                                                }
                                                Spacer(Modifier.height(16.dp))
                                                DrawColorSelector(drawController)
                                                DrawAlphaSelector(drawController)
                                                LineWidthSelector(drawController)
                                                ExtensionGroup(
                                                    modifier = Modifier
                                                        .padding(16.dp)
                                                        .navigationBarsPadding(),
                                                    orientation = Orientation.Horizontal,
                                                    enabled = drawBehavior !is DrawBehavior.None,
                                                    mimeTypeInt = viewModel.mimeType,
                                                    onMimeChange = {
                                                        viewModel.updateMimeType(it)
                                                    }
                                                )
                                            }
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
                                            content = {
                                                Icon(Icons.Rounded.AddPhotoAlternate, null)
                                            }
                                        )
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

                        is DrawBehavior.None -> {
                            val cutout = WindowInsets.displayCutout.asPaddingValues()
                            LazyVerticalStaggeredGrid(
                                modifier = Modifier.fillMaxSize(),
                                columns = StaggeredGridCells.Adaptive(400.dp),
                                horizontalArrangement = Arrangement.spacedBy(
                                    12.dp,
                                    Alignment.CenterHorizontally
                                ),
                                verticalItemSpacing = 12.dp,
                                contentPadding = PaddingValues(
                                    bottom = 12.dp + WindowInsets
                                        .navigationBars
                                        .asPaddingValues()
                                        .calculateBottomPadding(),
                                    top = 12.dp,
                                    end = 12.dp + cutout.calculateEndPadding(LocalLayoutDirection.current),
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
                            }
                        }
                    }
                }
                if (viewModel.drawBehavior is DrawBehavior.None) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                            .drawHorizontalStroke(true),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = pickImage,
                            modifier = Modifier
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
                }
            }
        }
    }

    if (portrait && viewModel.drawBehavior !is DrawBehavior.None) {
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
                            IconButton(
                                onClick = { viewModel.drawController?.undo() },
                                enabled = !viewModel.drawController?.paths.isNullOrEmpty()
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

    val density = LocalDensity.current
    var height by remember(showBackgroundDrawingSetup.value, configuration) {
        mutableIntStateOf(with(density) { configuration.screenHeightDp.dp.roundToPx() })
    }
    var width by remember(showBackgroundDrawingSetup.value, configuration) {
        mutableIntStateOf(with(density) { configuration.screenWidthDp.dp.roundToPx() })
    }
    var color by remember { mutableStateOf(Color.White) }
    SimpleSheet(
        title = {
            TitleItem(
                text = stringResource(R.string.draw),
                icon = Icons.Rounded.Draw
            )
        },
        confirmButton = {
            OutlinedButton(
                colors = ButtonDefaults.filledTonalButtonColors(),
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                ),
                onClick = {
                    showBackgroundDrawingSetup.value = false
                    viewModel.startDrawOnBackground(
                        width = width,
                        height = height,
                        color = color
                    )
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        sheetContent = {
            Box {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Row(
                        Modifier
                            .padding(16.dp)
                            .block(shape = RoundedCornerShape(24.dp))
                    ) {
                        RoundedTextField(
                            value = width.toString(),
                            onValueChange = {
                                width = it.restrict(4096).toIntOrNull() ?: 0
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
                            value = height.toString(),
                            onValueChange = {
                                height = it.restrict(4096).toIntOrNull() ?: 0
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
                        drawController = colorSelectorDrawController(
                            onColorChange = { color = it },
                            color = color
                        )
                    )
                }
                Divider()
                Divider(Modifier.align(Alignment.BottomCenter))
            }
        },
        visible = showBackgroundDrawingSetup
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

@Composable
private fun colorSelectorDrawController(onColorChange: (Color) -> Unit, color: Color) =
    remember(onColorChange, color) {
        object : DrawController {
            override val paths: Map<DrawPath, PaintOptions>
                get() = error("")
            override val lastPaths: Map<DrawPath, PaintOptions>
                get() = error("")
            override val undonePaths: Map<DrawPath, PaintOptions>
                get() = error("")
            override var paint: Paint
                get() = error("")
                set(value) {}
            override var drawPath: DrawPath
                get() = error("")
                set(value) {}
            override var paintOptions: PaintOptions
                get() = error("")
                set(value) {}
            override val backgroundColor: Color
                get() = color
            override var curX: Float
                get() = error("")
                set(value) {}
            override var curY: Float
                get() = error("")
                set(value) {}
            override var startX: Float
                get() = error("")
                set(value) {}
            override var startY: Float
                get() = error("")
                set(value) {}
            override var isStrokeWidthBarEnabled: Boolean
                get() = error("")
                set(value) {}
            override var isEraserOn: Boolean
                get() = error("")
                set(value) {}

            override fun undo() {
                error("")
            }

            override fun redo() {
                error("")
            }

            override fun setColor(newColor: Int) {
                error("")
            }

            override fun setDrawBackground(color: Color) {
                onColorChange(color)
            }

            override fun setAlpha(newAlpha: Int) {
                error("")
            }

            override fun setStrokeWidth(newStrokeWidth: Float) {
                error("")
            }

            override suspend fun getBitmap(): Bitmap? {
                error("")
            }

            override fun addPath(path: DrawPath, options: PaintOptions) {
                error("")
            }

            override fun changePaint(paintOptions: PaintOptions) {
                error("")
            }

            override fun toggleEraser() {
                error("")
            }

            override fun clearPaths() {
                error("")
            }

        }
    }