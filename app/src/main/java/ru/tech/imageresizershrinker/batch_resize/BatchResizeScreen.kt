package ru.tech.imageresizershrinker.batch_resize

import android.content.ContentValues
import android.content.res.Configuration
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cookhelper.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.batch_resize.components.SaveExifWidget
import ru.tech.imageresizershrinker.batch_resize.viewModel.BatchResizeViewModel
import ru.tech.imageresizershrinker.main_screen.Screen
import ru.tech.imageresizershrinker.main_screen.block
import ru.tech.imageresizershrinker.main_screen.isExternalStorageWritable
import ru.tech.imageresizershrinker.main_screen.requestPermission
import ru.tech.imageresizershrinker.resize_screen.components.*
import ru.tech.imageresizershrinker.utils.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri
import ru.tech.imageresizershrinker.utils.BitmapUtils.getUriByName
import ru.tech.imageresizershrinker.utils.LocalWindowSizeClass
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BatchResizeScreen(
    uriState: List<Uri>?,
    navController: NavController<Screen>,
    onGoBack: () -> Unit,
    viewModel: BatchResizeViewModel = viewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = rememberToastHostState()
    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current

    LaunchedEffect(uriState) {
        uriState?.let {
            try {
                viewModel.updateUris(it)
                context.decodeBitmapFromUri(
                    uri = it[0],
                    onGetMimeType = viewModel::setMime,
                    onGetExif = {},
                    onGetBitmap = viewModel::updateBitmap,
                )
            } catch (e: Exception) {
                scope.launch {
                    toastHostState.showToast(
                        context.getString(
                            R.string.smth_went_wrong,
                            e.localizedMessage ?: ""
                        ),
                        Icons.Rounded.ErrorOutline
                    )
                }
            }
        }
    }
    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            themeState.updateColorByImage(it)
        }
    }

    val pickImageLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia()
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let {
                try {
                    viewModel.updateUris(list)
                    context.decodeBitmapFromUri(
                        uri = it[0],
                        onGetMimeType = viewModel::setMime,
                        onGetExif = {},
                        onGetBitmap = viewModel::updateBitmap,
                    )
                } catch (e: Exception) {
                    scope.launch {
                        toastHostState.showToast(
                            context.getString(
                                R.string.smth_went_wrong,
                                e.localizedMessage ?: ""
                            ),
                            Icons.Rounded.ErrorOutline
                        )
                    }
                }
            }
        }

    val pickImage = {
        pickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    var showSaveLoading by rememberSaveable { mutableStateOf(false) }
    val saveBitmaps: () -> Unit = {
        showSaveLoading = true
        viewModel.save(
            isExternalStorageWritable = context.isExternalStorageWritable(),
            getFileOutputStream = { name, ext ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(
                        MediaStore.MediaColumns.MIME_TYPE,
                        "image/$ext"
                    )
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "DCIM/ResizedImages"
                    )
                }
                val imageUri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                context.contentResolver.openOutputStream(imageUri!!)
            },
            getExternalStorageDir = {
                File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                    ), "ResizedImages"
                )
            },
            getFileDescriptor = { name ->
                context.getUriByName(name)?.let {
                    context.contentResolver.openFileDescriptor(it, "rw", null)
                }
            },
            getBitmap = { uri ->
                context.decodeBitmapFromUri(uri)
            }
        ) { success ->
            if (!success) context.requestPermission()
            else {
                scope.launch {
                    toastHostState.showToast(
                        context.getString(R.string.saved_to),
                        Icons.Rounded.Save
                    )
                }
            }
            showSaveLoading = false
        }
    }

    val focus = LocalFocusManager.current
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }

    val state = rememberLazyListState()

    val bitmapInfo = viewModel.bitmapInfo

    val imageInside =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val imageBlock = @Composable {
        AnimatedContent(
            targetState = Triple(viewModel.previewBitmap, viewModel.isLoading, showOriginal),
            transitionSpec = { fadeIn() with fadeOut() }
        ) { (bmp, loading, showOrig) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                viewModel.uris?.size?.takeIf { it > 1 && !viewModel.isLoading }?.let {
                    Text(
                        stringResource(R.string.images, it),
                        Modifier
                            .block()
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Box {
                    if (showOrig) {
                        Picture(bitmap = viewModel.bitmap)
                    } else {
                        Picture(
                            bitmap = bmp,
                            visible = viewModel.shouldShowPreview
                        )
                        if (!viewModel.shouldShowPreview && !loading && viewModel.bitmap != null && bmp == null) BadImageWidget()
                    }
                    if (loading) Loading()
                }
            }
        }
    }

    val buttons = @Composable {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.End
        ) {
            if (viewModel.bitmap != null) {
                FloatingActionButton(
                    onClick = saveBitmaps,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Icon(Icons.Rounded.Save, null)
                }
            }
            Spacer(Modifier.height(16.dp))
            FloatingActionButton(onClick = pickImage) {
                val expanded =
                    state.isScrollingUp() && (imageInside || viewModel.bitmap == null)
                val horizontalPadding by animateDpAsState(targetValue = if (expanded) 16.dp else 0.dp)
                Row(
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                    AnimatedVisibility(visible = expanded) {
                        Row {
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.pick_image_alt))
                        }
                    }
                }
            }
        }
    }

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
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                CenterAlignedTopAppBar(
                    modifier = Modifier.shadow(6.dp),
                    title = {
                        AnimatedContent(
                            targetState = viewModel.bitmap to viewModel.isLoading,
                            transitionSpec = { fadeIn() with fadeOut() }
                        ) { (bmp, loading) ->
                            if (bmp == null) {
                                Text(stringResource(R.string.app_name))
                            } else if (!loading) {
                                Text(
                                    stringResource(
                                        R.string.size,
                                        byteCount(bitmapInfo.size)
                                    )
                                )
                            } else {
                                Text(stringResource(R.string.loading))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    actions = {
                        val interactionSource = remember { MutableInteractionSource() }
                        IconButton(
                            enabled = viewModel.bitmap != null,
                            onClick = { showResetDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.RestartAlt,
                                contentDescription = null
                            )
                        }
                        if (viewModel.bitmap != null && viewModel.bitmap?.canShow() == true) {
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
                                                    if (imageInside) {
                                                        state.animateScrollToItem(
                                                            0,
                                                            -10000
                                                        )
                                                    }
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
                        } else {
                            IconButton(
                                enabled = false,
                                onClick = {}
                            ) { Icon(Icons.Rounded.History, null) }
                        }
                    },
                    navigationIcon = {
                        TelegramButton(
                            enabled = viewModel.bitmap != null,
                            isTelegramSpecs = viewModel.isTelegramSpecs,
                            onClick = { viewModel.setTelegramSpecs() },
                        )
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
                                .calculateBottomPadding() + (if (!imageInside && viewModel.bitmap != null) 20.dp else 160.dp),
                            top = 20.dp,
                            start = 20.dp,
                            end = 20.dp
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxSize(),
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
                                    PresetWidget(
                                        selectedPreset = viewModel.presetSelected,
                                        bitmap = viewModel.bitmap,
                                        bitmapInfo = bitmapInfo,
                                        onChangeBitmapInfo = viewModel::setBitmapInfo
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    SaveExifWidget(
                                        selected = viewModel.keepExif,
                                        onCheckedChange = { viewModel.setKeepExif(!viewModel.keepExif) }
                                    )
                                } else if (!viewModel.isLoading) {
                                    ImageNotPickedWidget(onPickImage = pickImage)
                                    Spacer(Modifier.size(8.dp))
                                }
                                Spacer(Modifier.size(8.dp))
                                ResizeImageField(
                                    bitmapInfo = bitmapInfo,
                                    bitmap = viewModel.bitmap,
                                    onHeightChange = viewModel::updateHeight,
                                    onWidthChange = viewModel::updateWidth
                                )
                                if (bitmapInfo.mime != 2) Spacer(Modifier.height(8.dp))
                                QualityWidget(
                                    visible = bitmapInfo.mime != 2,
                                    enabled = viewModel.bitmap != null,
                                    quality = bitmapInfo.quality,
                                    onQualityChange = viewModel::setQuality
                                )
                                Spacer(Modifier.height(8.dp))
                                ExtensionGroup(
                                    enabled = viewModel.bitmap != null,
                                    mime = bitmapInfo.mime,
                                    onMimeChange = viewModel::setMime
                                )
                                Spacer(Modifier.height(8.dp))
                                ResizeGroup(
                                    enabled = viewModel.bitmap != null,
                                    resizeType = bitmapInfo.resizeType,
                                    onResizeChange = viewModel::setResizeType
                                )
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
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    buttons()
                }
            }

            var showExitDialog by rememberSaveable { mutableStateOf(false) }

            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    dismissButton = {
                        FilledTonalButton(
                            onClick = {
                                showExitDialog = false
                                if (navController.backstack.entries.isNotEmpty()) navController.pop()
                                themeState.reset()
                                onGoBack()
                            }
                        ) {
                            Text(stringResource(R.string.close))
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showExitDialog = false }) {
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
            } else if (showResetDialog) {
                AlertDialog(
                    icon = { Icon(Icons.Rounded.RestartAlt, null) },
                    title = { Text(stringResource(R.string.reset_image)) },
                    text = { Text(stringResource(R.string.reset_image_sub)) },
                    onDismissRequest = { showResetDialog = false },
                    confirmButton = {
                        Button(onClick = { showResetDialog = false }) {
                            Text(stringResource(R.string.close))
                        }
                    },
                    dismissButton = {
                        FilledTonalButton(onClick = {
                            viewModel.resetValues()
                            showResetDialog = false
                            scope.launch {
                                toastHostState.showToast(
                                    context.getString(R.string.values_reset),
                                    Icons.Rounded.DoneOutline
                                )
                            }
                        }) {
                            Text(stringResource(R.string.reset))
                        }
                    }
                )
            }

            ToastHost(hostState = toastHostState)
            BackHandler {
                if (viewModel.uris?.isNotEmpty() == true) showExitDialog = true
                else if (navController.backstack.entries.isNotEmpty()) {
                    navController.pop()
                    onGoBack()
                }
            }
        }
    }
}