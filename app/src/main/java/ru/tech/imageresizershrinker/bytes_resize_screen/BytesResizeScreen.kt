package ru.tech.imageresizershrinker.bytes_resize_screen

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChangeCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FrontHand
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.batch_resize_screen.components.SaveExifWidget
import ru.tech.imageresizershrinker.bytes_resize_screen.viewModel.BytesResizeViewModel
import ru.tech.imageresizershrinker.main_screen.components.LocalAlignment
import ru.tech.imageresizershrinker.main_screen.components.LocalAllowChangeColorByImage
import ru.tech.imageresizershrinker.main_screen.components.LocalBorderWidth
import ru.tech.imageresizershrinker.main_screen.components.alertDialog
import ru.tech.imageresizershrinker.main_screen.components.block
import ru.tech.imageresizershrinker.main_screen.components.drawHorizontalStroke
import ru.tech.imageresizershrinker.main_screen.components.fabBorder
import ru.tech.imageresizershrinker.main_screen.components.navBarsLandscapePadding
import ru.tech.imageresizershrinker.resize_screen.components.ExtensionGroup
import ru.tech.imageresizershrinker.resize_screen.components.ImageNotPickedWidget
import ru.tech.imageresizershrinker.resize_screen.components.ImageTransformBar
import ru.tech.imageresizershrinker.resize_screen.components.Loading
import ru.tech.imageresizershrinker.resize_screen.components.LoadingDialog
import ru.tech.imageresizershrinker.resize_screen.components.Picture
import ru.tech.imageresizershrinker.resize_screen.components.PresetWidget
import ru.tech.imageresizershrinker.resize_screen.components.ZoomModalSheet
import ru.tech.imageresizershrinker.resize_screen.components.byteCount
import ru.tech.imageresizershrinker.resize_screen.components.extension
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeSampledBitmapFromUri
import ru.tech.imageresizershrinker.utils.BitmapUtils.fileSize
import ru.tech.imageresizershrinker.utils.BitmapUtils.getBitmapByUri
import ru.tech.imageresizershrinker.utils.BitmapUtils.restrict
import ru.tech.imageresizershrinker.utils.ContextUtils.failedToSaveImages
import ru.tech.imageresizershrinker.utils.ContextUtils.isExternalStorageWritable
import ru.tech.imageresizershrinker.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.utils.SavingFolder
import ru.tech.imageresizershrinker.widget.LocalToastHost
import ru.tech.imageresizershrinker.widget.Marquee
import ru.tech.imageresizershrinker.widget.RoundedTextField

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BytesResizeScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    pushNewUris: (List<Uri>?) -> Unit,
    getSavingFolder: (name: String, ext: String) -> SavingFolder,
    savingPathString: String,
    showConfetti: () -> Unit,
    viewModel: BytesResizeViewModel = viewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHost.current
    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = LocalAllowChangeColorByImage.current

    var showAlert by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.updateUris(uris)
            pushNewUris(null)
            context.decodeBitmapFromUri(
                uri = uris[0],
                onGetMimeType = {
                    showAlert = it.extension == "png"
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
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia()
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let { uris ->
                viewModel.updateUris(list)
                context.decodeBitmapFromUri(
                    uri = uris[0],
                    onGetMimeType = {
                        showAlert = it.extension == "png"
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

    val pickImage = {
        pickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    var showSaveLoading by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val saveBitmaps: () -> Unit = {
        showSaveLoading = true
        viewModel.save(
            isExternalStorageWritable = context.isExternalStorageWritable(),
            getSavingFolder = getSavingFolder,
            getFileDescriptor = { uri ->
                uri?.let { context.contentResolver.openFileDescriptor(uri, "rw", null) }
            },
            getBitmap = { uri ->
                context.decodeBitmapFromUri(uri)
            },
            getImageSize = { uri ->
                uri.fileSize(context)
            }
        ) { failed ->
            context.failedToSaveImages(
                scope = scope,
                failed = failed,
                done = viewModel.done,
                toastHostState = toastHostState,
                savingPathString = savingPathString,
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
            transitionSpec = { fadeIn() with fadeOut() }
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
                                LocalBorderWidth.current,
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
                    Picture(bitmap = viewModel.previewBitmap, loading = loading)
                    if (loading) Loading()
                }
            }
        }
    }

    val switch = @Composable {
        Switch(
            modifier = Modifier.padding(horizontal = 16.dp),
            colors = SwitchDefaults.colors(
                uncheckedBorderColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            checked = viewModel.handMode,
            onCheckedChange = { viewModel.updateHandMode() },
            thumbContent = {
                AnimatedContent(viewModel.handMode) { handMode ->
                    Icon(
                        if (handMode) Icons.Rounded.FrontHand else Icons.Rounded.PhotoSizeSelectSmall,
                        null,
                        Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            }
        )
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
                    switch()
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
                Modifier
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
            ) {
                switch()
                Spacer(Modifier.height(16.dp))
                FloatingActionButton(
                    onClick = pickImage,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    modifier = Modifier.fabBorder(),
                ) {
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
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
        if (viewModel.bitmap != null) {
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
                                transitionSpec = { fadeIn() with fadeOut() }
                            ) { (loading, bmp) ->
                                val size = viewModel.selectedUri?.fileSize(LocalContext.current)
                                if (bmp == null) {
                                    Text(
                                        stringResource(R.string.by_bytes_resize),
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
                        zoomButton()
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
                                .width(1.dp)
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
                                    ImageTransformBar(
                                        onRotateLeft = viewModel::rotateLeft,
                                        onFlip = viewModel::flip,
                                        onRotateRight = viewModel::rotateRight
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    AnimatedContent(
                                        targetState = viewModel.handMode,
                                        transitionSpec = {
                                            if (!targetState) {
                                                slideInVertically { it } + fadeIn() with slideOutVertically { -it } + fadeOut()
                                            } else {
                                                slideInVertically { -it } + fadeIn() with slideOutVertically { it } + fadeOut()
                                            }
                                        }
                                    ) { handMode ->
                                        if (handMode) {
                                            RoundedTextField(
                                                modifier = Modifier
                                                    .block()
                                                    .padding(8.dp),
                                                enabled = viewModel.bitmap != null,
                                                value = (viewModel.maxBytes / 1024).toString()
                                                    .takeIf { it != "0" } ?: "",
                                                onValueChange = {
                                                    viewModel.updateMaxBytes(it.restrict(1_000_000))
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number
                                                ),
                                                label = stringResource(R.string.max_bytes)
                                            )
                                        } else {
                                            PresetWidget(
                                                selectedPreset = viewModel.presetSelected,
                                                onPresetSelected = viewModel::selectPreset
                                            )
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
                                        mime = viewModel.mime,
                                        onMimeChange = {
                                            showAlert = it.extension == "png"
                                            viewModel.setMime(it)
                                        }
                                    )
                                    AnimatedVisibility(
                                        visible = showAlert,
                                        enter = fadeIn() + slideInVertically(),
                                        exit = fadeOut() + slideOutVertically(),
                                    ) {
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                                    alpha = 0.7f
                                                ),
                                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                                            ),
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .border(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.onErrorContainer.copy(
                                                        0.4f
                                                    ),
                                                    RoundedCornerShape(12.dp)
                                                )
                                        ) {
                                            Text(
                                                text = stringResource(R.string.png_warning_bytes),
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(8.dp),
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.SemiBold,
                                                lineHeight = 14.sp,
                                                color = LocalContentColor.current.copy(alpha = 0.5f)
                                            )
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
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(start = 20.dp)
                        )
                        buttons()
                    }
                }
            }

            if (imageInside || viewModel.bitmap == null) {
                Box(
                    modifier = Modifier.align(LocalAlignment.current)
                ) {
                    buttons()
                }
            }

            if (showExitDialog) {
                AlertDialog(
                    modifier = Modifier.alertDialog(),
                    onDismissRequest = { showExitDialog = false },
                    dismissButton = {
                        OutlinedButton(
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                            border = BorderStroke(
                                LocalBorderWidth.current,
                                MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                            ),
                            onClick = {
                                showExitDialog = false
                                onGoBack()
                            }
                        ) {
                            Text(stringResource(R.string.close))
                        }
                    },
                    confirmButton = {
                        OutlinedButton(
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            border = BorderStroke(
                                LocalBorderWidth.current,
                                MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                            ), onClick = { showExitDialog = false }) {
                            Text(stringResource(R.string.stay))
                        }
                    },
                    title = { Text(stringResource(R.string.image_not_saved)) },
                    text = {
                        Text(
                            stringResource(R.string.image_not_saved_sub),
                            textAlign = TextAlign.Center
                        )
                    },
                    icon = { Icon(Icons.Outlined.Save, null) }
                )
            } else if (showSaveLoading) {
                LoadingDialog(viewModel.done, viewModel.uris?.size ?: 1)
            } else if (showPickImageFromUrisDialog) {
                if ((viewModel.uris?.size ?: 0) > 1) {
                    AlertDialog(
                        properties = DialogProperties(usePlatformDefaultWidth = false),
                        modifier = Modifier
                            .systemBarsPadding()
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .fillMaxWidth()
                            .alertDialog(),
                        icon = { Icon(Icons.Rounded.PhotoLibrary, null) },
                        title = { Text(stringResource(R.string.change_preview)) },
                        text = {
                            val pix = with(LocalDensity.current) { 100.dp.roundToPx() }
                            val gridState = rememberLazyGridState()
                            LaunchedEffect(Unit) {
                                gridState.scrollToItem(
                                    viewModel.uris?.indexOf(viewModel.selectedUri) ?: 0
                                )
                            }
                            Box {
                                Divider(Modifier.align(Alignment.TopCenter))
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(if (imageInside) 2 else 4),
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    state = gridState
                                ) {
                                    viewModel.uris?.let { uris ->
                                        items(uris, key = { it.toString() }) { uri ->
                                            var bmp: Bitmap? by remember(uri) {
                                                mutableStateOf(null)
                                            }

                                            LaunchedEffect(uri) {
                                                viewModel.loadBitmapAsync(
                                                    loader = {
                                                        context.decodeSampledBitmapFromUri(
                                                            uri = uri,
                                                            reqWidth = pix,
                                                            reqHeight = pix
                                                        )
                                                    },
                                                    onGetBitmap = { bmp = it }
                                                )
                                            }

                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                bmp?.asImageBitmap()?.let { bitmap ->
                                                    Image(
                                                        modifier = Modifier
                                                            .padding(top = 8.dp)
                                                            .padding(4.dp)
                                                            .aspectRatio(1f)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .clickable {
                                                                try {
                                                                    viewModel.setBitmap(
                                                                        loader = {
                                                                            context.getBitmapByUri(
                                                                                uri
                                                                            )
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
                                                                showPickImageFromUrisDialog = false
                                                            }
                                                            .then(
                                                                if (uri == viewModel.selectedUri) {
                                                                    Modifier.border(
                                                                        3.dp,
                                                                        MaterialTheme.colorScheme.outlineVariant(),
                                                                        RoundedCornerShape(8.dp)
                                                                    )
                                                                } else Modifier
                                                            )
                                                            .block(RoundedCornerShape(8.dp)),
                                                        bitmap = bitmap,
                                                        contentDescription = null
                                                    )
                                                } ?: Loading(
                                                    modifier = Modifier
                                                        .padding(top = 8.dp)
                                                        .padding(4.dp)
                                                        .aspectRatio(1f)
                                                        .fillMaxWidth()
                                                )
                                                FilledTonalButton(
                                                    onClick = {
                                                        viewModel.updateUrisSilently(
                                                            removedUri = uri,
                                                            loader = {
                                                                context.getBitmapByUri(it)
                                                            }
                                                        )
                                                    },
                                                    contentPadding = PaddingValues(
                                                        horizontal = 8.dp,
                                                        vertical = 2.dp
                                                    ),
                                                    modifier = Modifier.defaultMinSize(minHeight = 10.dp)
                                                ) {
                                                    Text(stringResource(R.string.remove))
                                                }
                                                Divider()
                                            }
                                        }
                                    }
                                }
                                Divider(Modifier.align(Alignment.BottomCenter))
                            }
                        },
                        onDismissRequest = { showPickImageFromUrisDialog = false },
                        confirmButton = {
                            OutlinedButton(
                                onClick = { showPickImageFromUrisDialog = false },
                                border = BorderStroke(
                                    LocalBorderWidth.current,
                                    MaterialTheme.colorScheme.outlineVariant()
                                )
                            ) {
                                Text(stringResource(R.string.close))
                            }
                        }
                    )
                } else {
                    showPickImageFromUrisDialog = false
                }
            }

            BackHandler {
                if (viewModel.uris?.isNotEmpty() == true) showExitDialog = true
                else onGoBack()
            }
        }
    }
}