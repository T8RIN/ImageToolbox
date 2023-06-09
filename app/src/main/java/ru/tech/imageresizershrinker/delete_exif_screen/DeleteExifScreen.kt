package ru.tech.imageresizershrinker.delete_exif_screen


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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChangeCircle
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import coil.size.Size
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.delete_exif_screen.viewModel.DeleteExifViewModel
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.LocalConfettiController
import ru.tech.imageresizershrinker.utils.coil.BitmapInfoTransformation
import ru.tech.imageresizershrinker.utils.coil.filters.SaturationFilter
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.decodeBitmapByUri
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.decodeBitmapFromUriWithMime
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.fileSize
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.getBitmapByUri
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
import ru.tech.imageresizershrinker.widget.TopAppBarEmoji
import ru.tech.imageresizershrinker.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.widget.image.SimplePicture
import ru.tech.imageresizershrinker.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.widget.showError
import ru.tech.imageresizershrinker.widget.text.Marquee
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.widget.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteExifScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: DeleteExifViewModel = viewModel()
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

    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.updateUris(uris)
            context.decodeBitmapByUri(
                originalSize = false,
                uri = uris[0],
                onGetMimeType = {},
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
                    originalSize = false,
                    onGetMimeType = {},
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

    var showSaveLoading by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val fileController = LocalFileController.current
    val saveBitmaps: () -> Unit = {
        showSaveLoading = true
        viewModel.saveBitmaps(
            fileController = fileController,
            getBitmap = { uri ->
                context.decodeBitmapFromUriWithMime(uri)
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
            targetState = Triple(viewModel.isLoading, viewModel.bitmap, viewModel.previewBitmap),
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { (loading, _, _) ->
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
                    SimplePicture(bitmap = viewModel.previewBitmap, loading = loading)
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
                        onClick = pickImage,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Rounded.AddPhotoAlternate, null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(id = R.string.pick_image_alt))
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = saveBitmaps,
                        modifier = Modifier.fabBorder(),
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Rounded.Save, null)
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
                    onClick = saveBitmaps,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    modifier = Modifier.fabBorder(),
                ) {
                    Icon(Icons.Rounded.Save, null)
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
                                        stringResource(R.string.delete_exif),
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
                            onClick = {
                                if (viewModel.uris?.isNotEmpty() == true) showExitDialog = true
                                else onGoBack()
                            }
                        ) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        if (viewModel.bitmap == null) {
                            TopAppBarEmoji()
                        }
                        zoomButton()
                        if (viewModel.previewBitmap != null) {
                            IconButton(
                                onClick = {
                                    showSaveLoading = true
                                    context.shareBitmaps(
                                        uris = viewModel.uris ?: emptyList(),
                                        scope = viewModel.viewModelScope,
                                        bitmapLoader = { uri ->
                                            context.decodeBitmapFromUriWithMime(uri)
                                                .takeIf { it.first != null }?.let {
                                                    it.first!! to BitmapInfo(
                                                        mimeTypeInt = it.third,
                                                        width = it.first!!.width,
                                                        height = it.first!!.height
                                                    )
                                                }
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
                                }
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
                                .weight(1000f)
                                .padding(20.dp)
                        ) {
                            Box(Modifier.align(Alignment.Center)) {
                                imageBlock()
                            }
                        }
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
                                } else if (!viewModel.isLoading) {
                                    ImageNotPickedWidget(onPickImage = pickImage)
                                    Spacer(Modifier.height(8.dp))
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
                transformations = listOf(
                    BitmapInfoTransformation(
                        bitmapInfo = BitmapInfo(),
                        preset = 100
                    )
                ),
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
                                context.getBitmapByUri(uri, originalSize = false)
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
                            context.getBitmapByUri(it, originalSize = false)
                        }
                    )
                },
                columns = if (imageInside) 2 else 4,
            )

            ExitWithoutSavingDialog(
                onExit = onGoBack,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )

            BackHandler {
                if (viewModel.uris?.isNotEmpty() == true) showExitDialog = true
                else onGoBack()
            }
        }
    }
}