package ru.tech.imageresizershrinker.filters_screen

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChangeCircle
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PhotoFilter
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.batch_resize_screen.components.SaveExifWidget
import ru.tech.imageresizershrinker.filters_screen.components.AddFiltersSheet
import ru.tech.imageresizershrinker.filters_screen.components.FilterItem
import ru.tech.imageresizershrinker.filters_screen.viewModel.FilterViewModel
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.LocalConfettiController
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.applyTransformations
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.decodeBitmapFromUri
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.fileSize
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.getBitmapByUri
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.getBitmapFromUriWithTransformations
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.shareBitmaps
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.failedToSaveImages
import ru.tech.imageresizershrinker.utils.helper.byteCount
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.utils.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.utils.storage.LocalFileController
import ru.tech.imageresizershrinker.utils.storage.Picker
import ru.tech.imageresizershrinker.utils.storage.localImagePickerMode
import ru.tech.imageresizershrinker.utils.storage.rememberImagePicker
import ru.tech.imageresizershrinker.widget.Loading
import ru.tech.imageresizershrinker.widget.LoadingDialog
import ru.tech.imageresizershrinker.widget.LocalToastHost
import ru.tech.imageresizershrinker.widget.TitleItem
import ru.tech.imageresizershrinker.widget.TopAppBarEmoji
import ru.tech.imageresizershrinker.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.widget.image.SimplePicture
import ru.tech.imageresizershrinker.widget.sheets.CompareSheet
import ru.tech.imageresizershrinker.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.widget.text.Marquee
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.widget.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: FilterViewModel = viewModel()
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

    val filterList = viewModel.filterList

    LaunchedEffect(filterList) {
        viewModel.bitmap?.let {
            context.applyTransformations(
                bitmap = it,
                transformations = filterList,
                onSuccess = viewModel::setFilteredPreview
            )
        }
    }

    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.updateUris(uris)
            context.decodeBitmapFromUri(
                uri = uris[0],
                onGetMimeType = {
                    viewModel.setMime(it)
                },
                onGetExif = {},
                onGetBitmap = viewModel::updateBitmap,
                onError = {
                    scope.launch {
                        toastHostState.showToast(
                            context.getString(
                                R.string.smth_went_wrong,
                                it.localizedMessage ?: ""
                            ),
                            Icons.Rounded.ErrorOutline
                        )
                    }
                }
            )
        }
    }
    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor) themeState.updateColorByImage(it)
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let { uris ->
                viewModel.updateUris(list)
                context.decodeBitmapFromUri(
                    uri = uris[0],
                    onGetMimeType = viewModel::setMime,
                    onGetExif = {},
                    onGetBitmap = viewModel::updateBitmap,
                    onError = {
                        scope.launch {
                            toastHostState.showToast(
                                context.getString(
                                    R.string.smth_went_wrong,
                                    it.localizedMessage ?: ""
                                ),
                                Icons.Rounded.ErrorOutline
                            )
                        }
                    }
                )
            }
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    var showSaveLoading by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    val showFilterSheet = rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.canSave) showExitDialog = true
        else onGoBack()
    }

    val fileController = LocalFileController.current
    val saveBitmaps: () -> Unit = {
        showSaveLoading = true
        viewModel.saveBitmaps(
            fileController = fileController,
            getBitmap = { uri ->
                context.decodeBitmapFromUri(uri)
            },
        ) { failed ->
            context.failedToSaveImages(
                scope = scope,
                failed = failed,
                done = viewModel.done,
                toastHostState = toastHostState,
                savingPathString = fileController.savingPath,
                showConfetti = showConfetti
            )
            showSaveLoading = false
        }
    }

    val focus = LocalFocusManager.current
    var showPickImageFromUrisDialog by rememberSaveable { mutableStateOf(false) }

    var showOriginal by remember { mutableStateOf(false) }

    val state = rememberLazyListState()

    val imageInside =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val imageBlock = @Composable {
        AnimatedContent(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if ((viewModel.uris?.size ?: 0) > 1) {
                            showPickImageFromUrisDialog = true
                        }
                    }
                )
            },
            targetState = Pair(viewModel.isLoading, showOriginal),
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { (loading, showOrig) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                viewModel.uris?.size?.takeIf { it > 1 && !loading }?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.images, it),
                            Modifier
                                .block()
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedIconButton(
                            onClick = {
                                if ((viewModel.uris?.size ?: 0) > 1) {
                                    showPickImageFromUrisDialog = true
                                }
                            },
                            border = BorderStroke(
                                settingsState.borderWidth,
                                MaterialTheme.colorScheme.outlineVariant(
                                    0.1f,
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                                ),
                            ),
                            shape = RoundedCornerShape(16.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(Icons.Rounded.ChangeCircle, null)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.then(
                        if (!imageInside) {
                            Modifier.padding(
                                bottom = WindowInsets
                                    .navigationBars
                                    .asPaddingValues()
                                    .calculateBottomPadding()
                            )
                        } else Modifier
                    )
                ) {
                    viewModel.previewBitmap?.let {
                        if (!showOrig) {
                            SimplePicture(bitmap = it, loading = loading)
                        } else {
                            SimplePicture(
                                loading = loading,
                                bitmap = viewModel.bitmap,
                                visible = true
                            )
                        }
                    }
                    if (loading) Loading()
                }
            }
        }
    }


    val buttons = @Composable {
        if (viewModel.bitmap == null) {
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
        } else if (imageInside) {
            BottomAppBar(
                modifier = Modifier.drawHorizontalStroke(true),
                actions = {
                    OutlinedButton(
                        colors = ButtonDefaults.filledTonalButtonColors(),
                        border = BorderStroke(
                            settingsState.borderWidth,
                            MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                        ),
                        onClick = { showFilterSheet.value = true },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Rounded.PhotoFilter, null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(id = R.string.add_filter))
                    }
                },
                floatingActionButton = {
                    Row {
                        FloatingActionButton(
                            onClick = pickImage,
                            modifier = Modifier.fabBorder(),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(Icons.Rounded.AddPhotoAlternate, null)
                        }
                        AnimatedVisibility(viewModel.canSave) {
                            Row {
                                Spacer(Modifier.width(16.dp))
                                FloatingActionButton(
                                    onClick = saveBitmaps,
                                    modifier = Modifier.fabBorder(),
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                                ) {
                                    Icon(Icons.Rounded.Save, null)
                                }
                            }
                        }
                    }
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                FloatingActionButton(
                    onClick = pickImage,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    modifier = Modifier.fabBorder(),
                ) {
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                }
                Spacer(Modifier.height(16.dp))
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    onClick = { showFilterSheet.value = true },
                    modifier = Modifier.fabBorder()
                ) {
                    Icon(Icons.Rounded.PhotoFilter, null)
                }
                Spacer(Modifier.height(16.dp))
                AnimatedVisibility(viewModel.canSave) {
                    FloatingActionButton(
                        onClick = saveBitmaps,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        modifier = Modifier.fabBorder(),
                    ) {
                        Icon(Icons.Rounded.Save, null)
                    }
                }
            }
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
        bitmap = viewModel.previewBitmap,
        visible = showSheet
    )

    val showCompareSheet = rememberSaveable { mutableStateOf(false) }
    val compareButton = @Composable {
        AnimatedVisibility(viewModel.bitmap != null && filterList.isNotEmpty()) {
            IconButton(
                onClick = {
                    showCompareSheet.value = true
                }
            ) {
                Icon(Icons.Rounded.Compare, null)
            }
        }
    }

    CompareSheet(
        data = viewModel.bitmap to viewModel.previewBitmap,
        visible = showCompareSheet
    )

    val interactionSource = remember { MutableInteractionSource() }


    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focus.clearFocus()
                    }
                )
            }
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
                            AnimatedContent(
                                targetState = viewModel.isLoading to viewModel.bitmap,
                                transitionSpec = { fadeIn() togetherWith fadeOut() }
                            ) { (loading, bmp) ->
                                val size = viewModel.selectedUri?.fileSize(LocalContext.current)
                                if (bmp == null) {
                                    Text(
                                        stringResource(R.string.filter),
                                        textAlign = TextAlign.Center
                                    )
                                } else if (size != null && !loading) {
                                    Text(
                                        stringResource(
                                            R.string.size,
                                            byteCount(size)
                                        )
                                    )
                                } else {
                                    Text(stringResource(R.string.loading))
                                }
                            }
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
                        if (viewModel.bitmap == null) {
                            TopAppBarEmoji()
                        }
                        if (viewModel.bitmap != null) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .indication(
                                        interactionSource,
                                        LocalIndication.current
                                    )
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                val press = PressInteraction.Press(it)
                                                interactionSource.emit(press)
                                                val pos =
                                                    state.firstVisibleItemIndex to state.firstVisibleItemScrollOffset
                                                if (viewModel.bitmap?.canShow() == true) {
                                                    showOriginal = true
                                                    delay(100)
                                                }
                                                tryAwaitRelease()
                                                showOriginal = false
                                                interactionSource.emit(
                                                    PressInteraction.Release(
                                                        press
                                                    )
                                                )
                                                state.animateScrollToItem(
                                                    pos.first,
                                                    pos.second
                                                )
                                            }
                                        )
                                    }
                            ) {
                                Icon(
                                    Icons.Rounded.History,
                                    null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(8.dp)
                                )
                            }
                        }
                        compareButton()
                        zoomButton()
                        if (viewModel.previewBitmap != null) {
                            IconButton(
                                onClick = {
                                    showSaveLoading = true
                                    context.shareBitmaps(
                                        uris = viewModel.uris ?: emptyList(),
                                        scope = viewModel.viewModelScope,
                                        bitmapLoader = {
                                            viewModel.proceedBitmap(
                                                uri = it,
                                                bitmapResult = kotlin.runCatching {
                                                    context.decodeBitmapFromUri(it).first
                                                },
                                                getImageSize = { uri ->
                                                    uri.fileSize(context)
                                                }
                                            )
                                        },
                                        onProgressChange = {
                                            if (it == -1) {
                                                showSaveLoading = false
                                                viewModel.setProgress(0)
                                                showConfetti()
                                            } else {
                                                viewModel.setProgress(it)
                                            }
                                        }
                                    )
                                },
                                enabled = viewModel.canSave
                            ) {
                                Icon(Icons.Outlined.Share, null)
                            }
                        }
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (!imageInside && viewModel.bitmap != null) {
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
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                    LazyColumn(
                        state = state,
                        contentPadding = PaddingValues(
                            bottom = WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + WindowInsets.ime
                                .asPaddingValues()
                                .calculateBottomPadding() + (if (!imageInside && viewModel.bitmap != null) 20.dp else 100.dp),
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
                                if (imageInside) imageBlock()
                                if (viewModel.bitmap != null) {
                                    if (imageInside) Spacer(Modifier.size(20.dp))
                                    Spacer(Modifier.size(8.dp))
                                    SaveExifWidget(
                                        selected = viewModel.keepExif,
                                        onCheckedChange = { viewModel.setKeepExif(!viewModel.keepExif) }
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    ExtensionGroup(
                                        enabled = viewModel.bitmap != null,
                                        mime = viewModel.mime,
                                        onMimeChange = {
                                            viewModel.setMime(it)
                                        }
                                    )
                                    if(filterList.isNotEmpty()) {
                                        Spacer(Modifier.size(16.dp))
                                        Column(Modifier.block(MaterialTheme.shapes.extraLarge)) {
                                            TitleItem(text = stringResource(R.string.filters))
                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.padding(8.dp)
                                            ) {
                                                filterList.forEach { filter ->
                                                    FilterItem(
                                                        filter = filter,
                                                        onFilterChange = {
                                                            viewModel.updateFilter(filter, it)
                                                        },
                                                        showDragHandle = filterList.size >= 2,
                                                        onRemove = {
                                                            viewModel.removeFilter(filter)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else if (!viewModel.isLoading) {
                                    ImageNotPickedWidget(onPickImage = pickImage)
                                    Spacer(Modifier.size(8.dp))
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                    if (!imageInside && viewModel.bitmap != null) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(start = 20.dp)
                        )
                        buttons()
                    }
                }
            }

            if (imageInside || viewModel.bitmap == null) {
                Box(
                    modifier = Modifier.align(settingsState.fabAlignment)
                ) {
                    buttons()
                }
            }

            if (showSaveLoading) {
                LoadingDialog(viewModel.done, viewModel.uris?.size ?: 1)
            }

            PickImageFromUrisSheet(
                transformations = filterList,
                visible = showPickImageFromUrisDialog,
                uris = viewModel.uris,
                selectedUri = viewModel.selectedUri,
                onDismiss = {
                    showPickImageFromUrisDialog = false
                },
                onUriPicked = { uri ->
                    try {
                        viewModel.setBitmap(
                            loader = {
                                context.getBitmapByUri(uri)
                            },
                            getPreview = {
                                context.getBitmapFromUriWithTransformations(uri, filterList)
                            },
                            uri = uri
                        )
                    } catch (e: Exception) {
                        scope.launch {
                            toastHostState.showToast(
                                context.getString(
                                    R.string.smth_went_wrong,
                                    e.localizedMessage
                                        ?: ""
                                ),
                                Icons.Rounded.ErrorOutline
                            )
                        }
                    }
                },
                onUriRemoved = { uri ->
                    viewModel.updateUrisSilently(
                        removedUri = uri,
                        loader = {
                            context.getBitmapByUri(it)
                        }
                    )
                },
                columns = if (imageInside) 2 else 4,
            )

            AddFiltersSheet(
                visible = showFilterSheet,
                onFilterPicked = { viewModel.addFilter(it) }
            )

            ExitWithoutSavingDialog(
                onExit = onGoBack,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )

            BackHandler(onBack = onBack)
        }
    }
}