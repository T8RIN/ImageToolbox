package ru.tech.imageresizershrinker.presentation.filters_screen

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PhotoFilter
import androidx.compose.material.icons.rounded.Reorder
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import coil.size.Size
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.batch_resize_screen.components.SaveExifWidget
import ru.tech.imageresizershrinker.presentation.filters_screen.components.AddFiltersSheet
import ru.tech.imageresizershrinker.presentation.filters_screen.components.FilterItem
import ru.tech.imageresizershrinker.presentation.filters_screen.viewModel.FilterViewModel
import ru.tech.imageresizershrinker.presentation.theme.mixedColor
import ru.tech.imageresizershrinker.presentation.theme.onMixedColor
import ru.tech.imageresizershrinker.presentation.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.utils.coil.filters.SaturationFilter
import ru.tech.imageresizershrinker.presentation.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.utils.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.presentation.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.presentation.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.widget.image.ImageContainer
import ru.tech.imageresizershrinker.presentation.widget.image.ImageCounter
import ru.tech.imageresizershrinker.presentation.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.widget.other.imageStickyHeader
import ru.tech.imageresizershrinker.presentation.widget.other.showError
import ru.tech.imageresizershrinker.presentation.widget.sheets.CompareSheet
import ru.tech.imageresizershrinker.presentation.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.presentation.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.presentation.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.presentation.widget.utils.isExpanded
import ru.tech.imageresizershrinker.presentation.widget.utils.middleImageState
import ru.tech.imageresizershrinker.presentation.utils.helper.BitmapUtils.applyTransformations
import ru.tech.imageresizershrinker.presentation.utils.helper.BitmapUtils.canShow
import ru.tech.imageresizershrinker.presentation.utils.helper.BitmapUtils.decodeBitmapByUri
import ru.tech.imageresizershrinker.presentation.utils.helper.BitmapUtils.getBitmapByUri
import ru.tech.imageresizershrinker.presentation.utils.helper.BitmapUtils.getBitmapFromUriWithTransformations
import ru.tech.imageresizershrinker.presentation.utils.helper.BitmapUtils.shareBitmaps
import ru.tech.imageresizershrinker.presentation.utils.helper.ContextUtils.failedToSaveImages
import ru.tech.imageresizershrinker.presentation.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.utils.helper.rememberImagePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: FilterViewModel = hiltViewModel()
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
            if (viewModel.needToApplyFilters) {
                viewModel.setFilteredPreview {
                    context.applyTransformations(
                        bitmap = it,
                        originalSize = false,
                        transformations = filterList
                    )
                }
            }
        }
    }

    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.updateUris(uris)
            context.decodeBitmapByUri(
                uri = uris[0],
                onGetMimeType = {
                    viewModel.setMime(it)
                },
                onGetExif = {},
                onGetBitmap = {
                    uris.firstOrNull()?.let { uri ->
                        viewModel.setBitmap(
                            loader = { it },
                            getPreview = {
                                context.getBitmapFromUriWithTransformations(
                                    uri = uri,
                                    transformations = filterList,
                                    originalSize = false
                                )
                            },
                            uri = uri
                        )
                    }
                },
                onError = {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            )
        }
    }
    LaunchedEffect(viewModel.previewBitmap) {
        viewModel.previewBitmap?.let {
            if (allowChangeColor) {
                themeState.updateColorByImage(
                    SaturationFilter(context, 2f).transform(it, Size.ORIGINAL)
                )
            }
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let { uris ->
                viewModel.updateUris(list)
                context.decodeBitmapByUri(
                    uri = uris[0],
                    onGetMimeType = viewModel::setMime,
                    onGetExif = {},
                    originalSize = false,
                    onGetBitmap = {
                        uris.firstOrNull()?.let { uri ->
                            viewModel.setBitmap(
                                loader = { it },
                                getPreview = {
                                    context.getBitmapFromUriWithTransformations(
                                        uri = uri,
                                        transformations = filterList,
                                        originalSize = false
                                    )
                                },
                                uri = uri
                            )
                        }
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

    var showSaveLoading by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    val showFilterSheet = rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.canSave) showExitDialog = true
        else onGoBack()
    }

    val saveBitmaps: () -> Unit = {
        showSaveLoading = true
        viewModel.saveBitmaps(
            getBitmap = { uri ->
                context.getBitmapFromUriWithTransformations(uri, filterList)
            },
        ) { failed, savingPath ->
            context.failedToSaveImages(
                scope = scope,
                failed = failed,
                done = viewModel.done,
                toastHostState = toastHostState,
                savingPathString = savingPath,
                showConfetti = showConfetti
            )
            showSaveLoading = false
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

    val interactionSource = remember { MutableInteractionSource() }

    val focus = LocalFocusManager.current
    var showPickImageFromUrisDialog by rememberSaveable { mutableStateOf(false) }

    var showOriginal by remember { mutableStateOf(false) }

    val imageInside =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    var imageState by remember { mutableStateOf(middleImageState()) }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState, canScroll = { !imageState.isExpanded() && !showOriginal }
    )

    LaunchedEffect(imageState, showOriginal) {
        if (imageState.isExpanded() || showOriginal) {
            while (topAppBarState.heightOffset > topAppBarState.heightOffsetLimit) {
                topAppBarState.heightOffset -= 5f
                delay(1)
            }
        }
    }

    val showReorderSheet = rememberSaveable { mutableStateOf(false) }

    val imageBlock = @Composable {
        ImageContainer(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = { showPickImageFromUrisDialog = true }
                )
            },
            imageInside = imageInside,
            showOriginal = showOriginal,
            previewBitmap = viewModel.previewBitmap,
            originalBitmap = viewModel.bitmap,
            isLoading = viewModel.isLoading,
            shouldShowPreview = true,
            animatePreviewChange = false
        )
    }

    val actions: @Composable RowScope.() -> Unit = {
        Spacer(modifier = Modifier.width(8.dp))
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
                                if (viewModel.bitmap?.canShow() == true) {
                                    showOriginal = true
                                }
                                tryAwaitRelease()
                                showOriginal = false
                                interactionSource.emit(
                                    PressInteraction.Release(
                                        press
                                    )
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
        if (viewModel.bitmap != null && filterList.size >= 2) {
            IconButton(onClick = { showReorderSheet.value = true }) {
                Icon(Icons.Rounded.Build, null)
            }
        }
    }

    val buttons = @Composable {
        BottomButtonsBlock(
            targetState = (viewModel.uris.isNullOrEmpty()) to imageInside,
            onPickImage = pickImage,
            onSaveBitmap = saveBitmaps,
            canSave = viewModel.canSave,
            actions = actions
        )
    }

    ZoomModalSheet(
        bitmap = viewModel.previewBitmap,
        visible = showSheet
    )

    CompareSheet(
        data = viewModel.bitmap to viewModel.previewBitmap,
        visible = showCompareSheet
    )

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
                        TopAppBarTitle(
                            title = stringResource(R.string.filter),
                            bitmap = viewModel.bitmap,
                            isLoading = viewModel.isLoading,
                            size = viewModel.bitmapSize ?: 0L
                        )
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
                        if (viewModel.previewBitmap != null) {
                            IconButton(
                                onClick = {
                                    showSaveLoading = true
                                    context.shareBitmaps(
                                        uris = viewModel.uris ?: emptyList(),
                                        scope = viewModel.viewModelScope,
                                        bitmapLoader = {
                                            viewModel.proceedBitmap(
                                                bitmapResult = kotlin.runCatching {
                                                    context.getBitmapFromUriWithTransformations(
                                                        uri = it,
                                                        transformations = filterList
                                                    )
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
                        if (viewModel.bitmap == null) {
                            TopAppBarEmoji()
                        }
                        if (viewModel.bitmap != null && !imageInside) actions()
                        if (viewModel.bitmap != null && imageInside) {
                            OutlinedIconButton(
                                onClick = { showFilterSheet.value = true },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.mixedColor,
                                    contentColor = MaterialTheme.colorScheme.onMixedColor
                                ),
                                border = BorderStroke(
                                    settingsState.borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant(
                                        onTopOf = MaterialTheme.colorScheme.mixedColor
                                    )
                                ),
                            ) {
                                Icon(Icons.Rounded.PhotoFilter, null)
                            }
                        }
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (!imageInside && !viewModel.uris.isNullOrEmpty()) {
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
                        contentPadding = PaddingValues(
                            bottom = WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + WindowInsets.ime
                                .asPaddingValues()
                                .calculateBottomPadding() + (if (!imageInside && viewModel.bitmap != null) 20.dp else 100.dp),
                            top = if (viewModel.bitmap == null || !imageInside) 20.dp else 0.dp,
                            start = 20.dp,
                            end = 20.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clipToBounds()
                    ) {
                        imageStickyHeader(
                            visible = imageInside && viewModel.bitmap != null,
                            expanded = showOriginal,
                            imageState = imageState,
                            onStateChange = { imageState = it },
                            imageBlock = imageBlock
                        )
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .navBarsLandscapePadding(viewModel.bitmap == null),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (imageInside && viewModel.bitmap == null) imageBlock()
                                if (viewModel.bitmap != null) {
                                    ImageCounter(
                                        imageCount = viewModel.uris?.size?.takeIf { it > 1 && !viewModel.isLoading },
                                        onRepick = {
                                            showPickImageFromUrisDialog = true
                                        }
                                    )
                                    if (filterList.isNotEmpty()) {
                                        Column(Modifier.block(MaterialTheme.shapes.extraLarge)) {
                                            TitleItem(text = stringResource(R.string.filters))
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.padding(8.dp)
                                            ) {
                                                filterList.forEachIndexed { index, filter ->
                                                    FilterItem(
                                                        filter = filter,
                                                        onFilterChange = {
                                                            viewModel.updateFilter(
                                                                value = it,
                                                                index = index,
                                                                showError = {
                                                                    scope.launch {
                                                                        toastHostState.showError(
                                                                            context,
                                                                            it
                                                                        )
                                                                    }
                                                                }
                                                            )
                                                        },
                                                        onLongPress = {
                                                            showReorderSheet.value = true
                                                        },
                                                        showDragHandle = false,
                                                        onRemove = {
                                                            viewModel.removeFilterAtIndex(index)
                                                        }
                                                    )
                                                }
                                                OutlinedButton(
                                                    colors = ButtonDefaults.filledTonalButtonColors(
                                                        containerColor = MaterialTheme.colorScheme.mixedColor,
                                                        contentColor = MaterialTheme.colorScheme.onMixedColor
                                                    ),
                                                    border = BorderStroke(
                                                        settingsState.borderWidth,
                                                        MaterialTheme.colorScheme.outlineVariant(
                                                            onTopOf = MaterialTheme.colorScheme.mixedColor
                                                        )
                                                    ),
                                                    onClick = { showFilterSheet.value = true },
                                                    modifier = Modifier.padding(horizontal = 16.dp)
                                                ) {
                                                    Icon(Icons.Rounded.PhotoFilter, null)
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(stringResource(id = R.string.add_filter))
                                                }
                                            }
                                        }
                                    } else {
                                        OutlinedButton(
                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                containerColor = MaterialTheme.colorScheme.mixedColor,
                                                contentColor = MaterialTheme.colorScheme.onMixedColor
                                            ),
                                            border = BorderStroke(
                                                width = settingsState.borderWidth,
                                                color = MaterialTheme.colorScheme.outlineVariant(
                                                    onTopOf = MaterialTheme.colorScheme.mixedColor
                                                )
                                            ),
                                            onClick = { showFilterSheet.value = true },
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        ) {
                                            Icon(Icons.Rounded.PhotoFilter, null)
                                            Spacer(Modifier.width(8.dp))
                                            Text(stringResource(id = R.string.add_filter))
                                        }
                                    }
                                    Spacer(Modifier.size(8.dp))
                                    SaveExifWidget(
                                        selected = viewModel.keepExif,
                                        onCheckedChange = { viewModel.setKeepExif(!viewModel.keepExif) }
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    ExtensionGroup(
                                        enabled = viewModel.bitmap != null,
                                        mimeType = viewModel.mimeType,
                                        onMimeChange = {
                                            viewModel.setMime(it)
                                        }
                                    )
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
                                .background(MaterialTheme.colorScheme.outlineVariant())
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
                                context.getBitmapByUri(uri, false)
                            },
                            getPreview = {
                                context.getBitmapFromUriWithTransformations(
                                    uri = uri,
                                    transformations = filterList,
                                    originalSize = false
                                )
                            },
                            uri = uri
                        )
                    } catch (e: Exception) {
                        scope.launch {
                            toastHostState.showError(context, e)
                        }
                    }
                },
                onUriRemoved = { uri ->
                    viewModel.updateUrisSilently(
                        removedUri = uri,
                        loader = {
                            context.getBitmapByUri(it, false)
                        }
                    )
                },
                columns = if (imageInside) 2 else 4,
            )

            AddFiltersSheet(
                visible = showFilterSheet,
                previewBitmap = viewModel.previewBitmap,
                onFilterPicked = { viewModel.addFilter(it.newInstance()) },
                onFilterPickedWithParams = { viewModel.addFilter(it) }
            )

            ExitWithoutSavingDialog(
                onExit = onGoBack,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )

            SimpleSheet(
                sheetContent = {
                    if (filterList.size < 2) {
                        showReorderSheet.value = false
                    }
                    Box {
                        val data = remember { mutableStateOf(filterList) }
                        val state = rememberReorderableLazyListState(
                            onMove = { from, to ->
                                data.value = data.value.toMutableList().apply {
                                    add(to.index, removeAt(from.index))
                                }
                            },
                            onDragEnd = { _, _ ->
                                viewModel.updateOrder(data.value)
                            }
                        )
                        LazyColumn(
                            state = state.listState,
                            modifier = Modifier
                                .reorderable(state)
                                .detectReorderAfterLongPress(state),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(data.value, key = { it.hashCode() }) { filter ->
                                ReorderableItem(state, key = filter.hashCode()) { isDragging ->
                                    val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
                                    val tonalElevation by animateDpAsState(if (isDragging) 16.dp else 1.dp)
                                    FilterItem(
                                        filter = filter,
                                        onFilterChange = {},
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(elevation, RoundedCornerShape(16.dp)),
                                        backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                            tonalElevation
                                        ),
                                        previewOnly = true,
                                        showDragHandle = filterList.size >= 2,
                                        onRemove = {}
                                    )
                                }
                            }
                        }
                        Divider(Modifier.align(Alignment.TopCenter))
                        Divider(Modifier.align(Alignment.BottomCenter))
                    }
                },
                visible = showReorderSheet,
                title = {
                    TitleItem(
                        text = stringResource(R.string.reorder),
                        icon = Icons.Rounded.Reorder
                    )
                },
                confirmButton = {
                    OutlinedButton(
                        colors = ButtonDefaults.filledTonalButtonColors(),
                        border = BorderStroke(
                            settingsState.borderWidth,
                            MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                        ),
                        onClick = { showReorderSheet.value = false }
                    ) {
                        Text(stringResource(R.string.close))
                    }
                },
            )

            BackHandler(onBack = onBack)
        }
    }
}