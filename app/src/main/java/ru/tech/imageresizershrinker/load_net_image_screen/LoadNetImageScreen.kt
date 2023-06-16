package ru.tech.imageresizershrinker.load_net_image_screen

import android.content.res.Configuration
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Size
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.getAppColorTuple
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.load_net_image_screen.viewModel.LoadNetImageViewModel
import ru.tech.imageresizershrinker.main_screen.components.BytesResizePreference
import ru.tech.imageresizershrinker.main_screen.components.CropPreference
import ru.tech.imageresizershrinker.main_screen.components.DeleteExifPreference
import ru.tech.imageresizershrinker.main_screen.components.FilterPreference
import ru.tech.imageresizershrinker.main_screen.components.GeneratePalettePreference
import ru.tech.imageresizershrinker.main_screen.components.LimitsPreference
import ru.tech.imageresizershrinker.main_screen.components.PickColorPreference
import ru.tech.imageresizershrinker.main_screen.components.SingleResizePreference
import ru.tech.imageresizershrinker.theme.icons.CreateAlt
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.LocalConfettiController
import ru.tech.imageresizershrinker.utils.coil.filters.SaturationFilter
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.cacheImage
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.shareBitmap
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.toBitmap
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.utils.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.utils.navigation.Screen
import ru.tech.imageresizershrinker.utils.storage.LocalFileController
import ru.tech.imageresizershrinker.widget.LoadingDialog
import ru.tech.imageresizershrinker.widget.LocalToastHost
import ru.tech.imageresizershrinker.widget.TitleItem
import ru.tech.imageresizershrinker.widget.TopAppBarEmoji
import ru.tech.imageresizershrinker.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.widget.image.Picture
import ru.tech.imageresizershrinker.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.widget.text.Marquee
import ru.tech.imageresizershrinker.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.widget.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadNetImageScreen(
    onGoBack: () -> Unit,
    viewModel: LoadNetImageViewModel = viewModel()
) {
    val navController = LocalNavController.current
    val context = LocalContext.current as ComponentActivity
    val themeState = LocalDynamicThemeState.current
    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage
    val toastHostState = LocalToastHost.current

    val confettiController = LocalConfettiController.current

    val scope = rememberCoroutineScope()

    val appColorTuple = getAppColorTuple(
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = settingsState.isDynamicColors,
        darkTheme = settingsState.isNightMode
    )

    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let { image ->
            if (allowChangeColor) {
                themeState.updateColorByImage(
                    SaturationFilter(context, 2f).transform(image, Size.ORIGINAL)
                )
            }
        } ?: themeState.updateColorTuple(appColorTuple)
    }

    var scaleType by rememberSaveable(
        saver = Saver(
            save = {
                if (it == ContentScale.FillWidth) 0 else 1
            },
            restore = {
                mutableStateOf(
                    if (it == 0) {
                        ContentScale.FillWidth
                    } else {
                        ContentScale.Fit
                    }
                )
            }
        )
    ) { mutableStateOf(ContentScale.FillWidth) }

    val landscape =
        LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact || LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var link by rememberSaveable { mutableStateOf("") }
    var state: AsyncImagePainter.State by remember {
        mutableStateOf(
            AsyncImagePainter.State.Empty
        )
    }
    val imageBlock = @Composable {
        AnimatedContent(scaleType) { scale ->
            Picture(
                allowHardware = false,
                model = link,
                modifier = Modifier
                    .then(if (scale == ContentScale.FillWidth) Modifier.fillMaxWidth() else Modifier)
                    .padding(bottom = 16.dp)
                    .then(
                        if (viewModel.bitmap == null) Modifier.height(140.dp)
                        else Modifier
                    )
                    .block()
                    .padding(4.dp),
                contentScale = scale,
                shape = MaterialTheme.shapes.small,
                error = {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.BrokenImage,
                            null,
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .size(64.dp)
                        )
                        Text(stringResource(id = R.string.no_image))
                        Spacer(Modifier.height(8.dp))
                    }
                },
                onState = {
                    if (it is AsyncImagePainter.State.Error) {
                        viewModel.updateBitmap(it.result.drawable?.toBitmap())
                    }
                    if (it is AsyncImagePainter.State.Success) {
                        viewModel.updateBitmap(it.result.drawable.toBitmap())
                    }
                    state = it
                },
            )
        }
    }

    val showSheet = rememberSaveable { mutableStateOf(false) }
    val zoomButton = @Composable {
        AnimatedVisibility(viewModel.bitmap != null) {
            IconButton(
                onClick = {
                    showSheet.value = true
                }
            ) {
                Icon(Icons.Rounded.ZoomIn, null)
            }
        }
    }

    ZoomModalSheet(
        bitmap = viewModel.bitmap,
        visible = showSheet
    )

    var showSaveLoading by rememberSaveable { mutableStateOf(false) }

    val wantToEdit = rememberSaveable { mutableStateOf(false) }

    val focus = LocalFocusManager.current

    val fileController = LocalFileController.current
    val saveBitmap: () -> Unit = {
        showSaveLoading = true
        viewModel.saveBitmap(
            getBitmap = {
                val loader = context.imageLoader.newBuilder().components {
                    if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                    else add(GifDecoder.Factory())
                    add(SvgDecoder.Factory())
                }.allowHardware(false).build()
                loader.execute(
                    ImageRequest
                        .Builder(context)
                        .data(link)
                        .build()
                ).drawable?.toBitmap()
            },
            fileController = fileController
        ) { success ->
            if (!success) fileController.requestReadWritePermissions()
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
                scope.launch {
                    confettiController.showEmpty()
                }
            }
            showSaveLoading = false
        }
    }

    val buttons = @Composable {
        val b = @Composable {
            FloatingActionButton(
                onClick = {
                    viewModel.bitmap?.let { bitmap ->
                        showSaveLoading = true
                        val uri = context.cacheImage(
                            image = bitmap,
                            bitmapInfo = BitmapInfo(
                                width = bitmap.width,
                                height = bitmap.height,
                            )
                        )
                        scope.launch {
                            confettiController.showEmpty()
                        }
                        showSaveLoading = false
                        viewModel.setTempUri(uri)
                        wantToEdit.value = true
                    }
                },
                modifier = Modifier.fabBorder(),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Rounded.CreateAlt, null)
            }
            FloatingActionButton(
                onClick = { saveBitmap() },
                modifier = Modifier.fabBorder(),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Rounded.Save, null)
            }
        }
        if (landscape) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                b()
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                b()
            }
        }
    }

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
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
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
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
                            Text(
                                stringResource(R.string.load_image_from_net),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    navigationIcon = {
                        IconButton(onClick = onGoBack) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        if (viewModel.bitmap == null) {
                            TopAppBarEmoji()
                        } else {
                            IconButton(
                                onClick = {
                                    viewModel.bitmap?.let { bitmap ->
                                        showSaveLoading = true
                                        context.shareBitmap(
                                            bitmap = bitmap,
                                            bitmapInfo = BitmapInfo(
                                                width = bitmap.width,
                                                height = bitmap.height,
                                            ),
                                            onComplete = {
                                                showSaveLoading = false
                                                scope.launch {
                                                    confettiController.showEmpty()
                                                }
                                            }
                                        )
                                    }
                                }
                            ) {
                                Icon(Icons.Outlined.Share, null)
                            }
                            zoomButton()
                        }
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (landscape) {
                        Box(
                            Modifier
                                .weight(0.8f)
                                .padding(20.dp)
                        ) {
                            Box(Modifier.align(Alignment.Center)) {
                                imageBlock()
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                        )
                    }
                    LazyColumn(
                        state = rememberLazyListState(),
                        contentPadding = PaddingValues(
                            bottom = WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + WindowInsets.ime
                                .asPaddingValues()
                                .calculateBottomPadding() + (if (landscape && viewModel.bitmap != null) 20.dp else 100.dp),
                            top = 20.dp,
                            start = 20.dp,
                            end = 20.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clipToBounds()
                    ) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .navBarsLandscapePadding(viewModel.bitmap == null),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (!landscape) imageBlock()
                                ToggleGroupButton(
                                    modifier = Modifier
                                        .block(shape = RoundedCornerShape(24.dp))
                                        .padding(start = 3.dp, end = 2.dp),
                                    title = stringResource(id = R.string.content_scale),
                                    enabled = viewModel.bitmap != null,
                                    items = listOf(
                                        stringResource(R.string.fill),
                                        stringResource(R.string.fit)
                                    ),
                                    selectedIndex = (scaleType == ContentScale.Fit).toInt(),
                                    indexChanged = {
                                        scaleType = if (it == 0) {
                                            ContentScale.FillWidth
                                        } else {
                                            ContentScale.Fit
                                        }
                                    }
                                )
                                Spacer(Modifier.height(8.dp))
                                RoundedTextField(
                                    modifier = Modifier
                                        .block(shape = RoundedCornerShape(24.dp))
                                        .padding(8.dp),
                                    value = link,
                                    onValueChange = {
                                        link = it
                                    },
                                    singleLine = false,
                                    label = {
                                        Text(stringResource(id = R.string.image_link))
                                    }
                                )
                                if (link.isNotEmpty()) {
                                    OutlinedButton(
                                        colors = ButtonDefaults.filledTonalButtonColors(),
                                        border = BorderStroke(
                                            settingsState.borderWidth,
                                            MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                        ),
                                        onClick = { link = "" },
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(stringResource(id = R.string.clear))
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                    if (landscape && viewModel.bitmap != null) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                                .padding(start = 20.dp)
                        )
                        buttons()
                    }
                }
            }

            if (!landscape && viewModel.bitmap != null) {
                Box(
                    Modifier
                        .align(settingsState.fabAlignment)
                        .navigationBarsPadding()
                ) {
                    buttons()
                }
            }


            if (showSaveLoading) LoadingDialog()

            BackHandler { onGoBack() }
        }
    }

    SimpleSheet(
        title = {
            TitleItem(text = stringResource(R.string.image), icon = Icons.Rounded.Image)
        },
        visible = wantToEdit,
        confirmButton = {
            OutlinedButton(
                onClick = {
                    wantToEdit.value = false
                },
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant()
                )
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        sheetContent = {
            val navigate: (Screen) -> Unit = { screen ->
                scope.launch {
                    wantToEdit.value = false
                    delay(200)
                    navController.navigate(screen)
                }
            }
            val color = MaterialTheme.colorScheme.secondaryContainer
            Box {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(250.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    item {
                        SingleResizePreference(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navigate(Screen.SingleResize(viewModel.tempUri!!)) },
                            color = color
                        )
                    }
                    item {
                        BytesResizePreference(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navigate(Screen.ResizeByBytes(listOf(viewModel.tempUri!!))) },
                            color = color
                        )
                    }
                    item {
                        FilterPreference(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navigate(Screen.Filter(listOf(viewModel.tempUri!!))) },
                            color = color
                        )
                    }
                    item {
                        CropPreference(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navigate(Screen.Crop(viewModel.tempUri)) },
                            color = color
                        )
                    }
                    item {
                        PickColorPreference(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navigate(Screen.PickColorFromImage(viewModel.tempUri)) },
                            color = color
                        )
                    }
                    item {
                        GeneratePalettePreference(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navigate(Screen.GeneratePalette(viewModel.tempUri)) },
                            color = color
                        )
                    }
                    item {
                        LimitsPreference(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navigate(Screen.LimitResize(listOf(viewModel.tempUri!!))) },
                            color = color
                        )
                    }
                    item {
                        DeleteExifPreference(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navigate(Screen.DeleteExif(listOf(viewModel.tempUri!!))) },
                            color = color
                        )
                    }
                }
                Divider(Modifier.align(Alignment.TopCenter))
                Divider(Modifier.align(Alignment.BottomCenter))
            }
        }
    )
}

private fun Boolean.toInt() = if (this) 1 else 0