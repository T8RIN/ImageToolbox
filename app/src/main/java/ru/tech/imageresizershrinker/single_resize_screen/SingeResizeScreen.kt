package ru.tech.imageresizershrinker.single_resize_screen

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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.Dataset
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.components.LocalAlignment
import ru.tech.imageresizershrinker.main_screen.components.LocalAllowChangeColorByImage
import ru.tech.imageresizershrinker.main_screen.components.LocalBorderWidth
import ru.tech.imageresizershrinker.main_screen.components.LocalSelectedEmoji
import ru.tech.imageresizershrinker.main_screen.components.SimpleSheet
import ru.tech.imageresizershrinker.main_screen.components.TitleItem
import ru.tech.imageresizershrinker.single_resize_screen.components.BadImageWidget
import ru.tech.imageresizershrinker.single_resize_screen.components.BitmapInfo
import ru.tech.imageresizershrinker.single_resize_screen.components.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.single_resize_screen.components.ExtensionGroup
import ru.tech.imageresizershrinker.single_resize_screen.components.ImageNotPickedWidget
import ru.tech.imageresizershrinker.single_resize_screen.components.ImageTransformBar
import ru.tech.imageresizershrinker.single_resize_screen.components.Loading
import ru.tech.imageresizershrinker.single_resize_screen.components.LoadingDialog
import ru.tech.imageresizershrinker.single_resize_screen.components.PresetWidget
import ru.tech.imageresizershrinker.single_resize_screen.components.QualityWidget
import ru.tech.imageresizershrinker.single_resize_screen.components.ResizeGroup
import ru.tech.imageresizershrinker.single_resize_screen.components.ResizeImageField
import ru.tech.imageresizershrinker.single_resize_screen.components.SimplePicture
import ru.tech.imageresizershrinker.single_resize_screen.components.TelegramButton
import ru.tech.imageresizershrinker.single_resize_screen.components.ZoomModalSheet
import ru.tech.imageresizershrinker.single_resize_screen.components.byteCount
import ru.tech.imageresizershrinker.single_resize_screen.components.extension
import ru.tech.imageresizershrinker.single_resize_screen.viewModel.SingleResizeViewModel
import ru.tech.imageresizershrinker.theme.EmojiItem
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.BitmapUtils
import ru.tech.imageresizershrinker.utils.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri
import ru.tech.imageresizershrinker.utils.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.shareBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.toMap
import ru.tech.imageresizershrinker.utils.BitmapUtils.with
import ru.tech.imageresizershrinker.utils.ContextUtils.isExternalStorageWritable
import ru.tech.imageresizershrinker.utils.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.utils.Picker
import ru.tech.imageresizershrinker.utils.SavingFolder
import ru.tech.imageresizershrinker.utils.localImagePickerMode
import ru.tech.imageresizershrinker.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.utils.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.utils.modifier.scaleOnTap
import ru.tech.imageresizershrinker.utils.rememberImagePicker
import ru.tech.imageresizershrinker.widget.LocalToastHost
import ru.tech.imageresizershrinker.widget.Marquee
import ru.tech.imageresizershrinker.widget.RoundedTextField

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun SingleResizeScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    showConfetti: () -> Unit,
    getSavingFolder: (bitmapInfo: BitmapInfo) -> SavingFolder,
    savingPathString: String,
    viewModel: SingleResizeViewModel = viewModel(),
) {
    val toastHostState = LocalToastHost.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current as ComponentActivity
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = LocalAllowChangeColorByImage.current

    LaunchedEffect(uriState) {
        uriState?.let {
            context.decodeBitmapFromUri(
                uri = it,
                onGetMimeType = viewModel::setMime,
                onGetExif = viewModel::updateExif,
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

    val focus = LocalFocusManager.current
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showSaveLoading by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }
    val showEditExifDialog = rememberSaveable { mutableStateOf(false) }
    val showCropDialog = rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val state = rememberLazyListState()

    val bitmapInfo = viewModel.bitmapInfo
    var map by remember(viewModel.exif) {
        mutableStateOf(viewModel.exif?.toMap())
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
                context.decodeBitmapFromUri(
                    uri = it,
                    onGetMimeType = viewModel::setMime,
                    onGetExif = viewModel::updateExif,
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

    val saveBitmap: () -> Unit = {
        showSaveLoading = true
        viewModel.saveBitmap(
            isExternalStorageWritable = context.isExternalStorageWritable(),
            getSavingFolder = getSavingFolder,
            getFileDescriptor = { uri ->
                uri?.let { context.contentResolver.openFileDescriptor(it, "rw", null) }
            }
        ) { success ->
            if (!success) context.requestStoragePermission()
            else {
                scope.launch {
                    toastHostState.showToast(
                        context.getString(
                            R.string.saved_to,
                            savingPathString
                        ),
                        Icons.Rounded.Save
                    )
                }
                showConfetti()
            }
            showSaveLoading = false
        }
    }

    val imageInside =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val imageBlock = @Composable {

        AnimatedContent(
            targetState = Triple(viewModel.previewBitmap, viewModel.isLoading, showOriginal),
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { (bmp, loading, showOrig) ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.then(
                    if (!imageInside) {
                        Modifier.padding(
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()
                        )
                    } else Modifier
                )
            ) {
                if (showOrig) {
                    SimplePicture(bitmap = viewModel.bitmap, loading = loading)
                } else {
                    SimplePicture(
                        loading = loading,
                        bitmap = bmp,
                        visible = viewModel.shouldShowPreview
                    )
                    if (!viewModel.shouldShowPreview && !loading && viewModel.bitmap != null && bmp == null) BadImageWidget()
                }
                if (loading) Loading()
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
                    TelegramButton(
                        enabled = viewModel.bitmap != null,
                        isTelegramSpecs = viewModel.isTelegramSpecs,
                        onClick = { viewModel.setTelegramSpecs() },
                    )
                    IconButton(
                        onClick = {
                            showSaveLoading = true
                            context.shareBitmap(
                                bitmap = viewModel.previewBitmap,
                                bitmapInfo = viewModel.bitmapInfo,
                                onComplete = {
                                    showSaveLoading = false
                                    showConfetti()
                                }
                            )
                        },
                        enabled = viewModel.previewBitmap != null
                    ) {
                        Icon(Icons.Outlined.Share, null)
                    }

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
                                                state.animateScrollToItem(
                                                    0,
                                                    -10000
                                                )
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
                        Spacer(Modifier.width(16.dp))
                        FloatingActionButton(
                            onClick = saveBitmap,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            modifier = Modifier.fabBorder(),
                        ) {
                            Icon(Icons.Rounded.Save, null)
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
                FloatingActionButton(
                    onClick = pickImage,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    modifier = Modifier.fabBorder(),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                ) {
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                }
                Spacer(Modifier.height(16.dp))
                FloatingActionButton(
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    modifier = Modifier.fabBorder(),
                    onClick = saveBitmap
                ) {
                    Icon(Icons.Rounded.Save, null)
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val showSheet = rememberSaveable { mutableStateOf(false) }
    val zoomButton = @Composable {
        AnimatedVisibility(viewModel.bitmap != null && viewModel.shouldShowPreview) {
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
                                targetState = Triple(
                                    viewModel.bitmap,
                                    viewModel.isLoading,
                                    bitmapInfo.sizeInBytes
                                ),
                                transitionSpec = { fadeIn() togetherWith fadeOut() }
                            ) { (bmp, loading, sizeInBytes) ->
                                if (bmp == null) {
                                    Text(
                                        stringResource(R.string.single_resize)
                                    )
                                } else if (!loading && sizeInBytes != 0) {
                                    Text(
                                        stringResource(
                                            R.string.size,
                                            byteCount(sizeInBytes.toLong())
                                        )
                                    )
                                } else {
                                    Text(
                                        stringResource(R.string.loading)
                                    )
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
                                if (viewModel.bitmap != null) showExitDialog = true
                                else onGoBack()
                            }
                        ) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        if (viewModel.bitmap == null) {
                            EmojiItem(
                                emoji = LocalSelectedEmoji.current,
                                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .scaleOnTap(onRelease = showConfetti),
                            )
                        }
                        zoomButton()
                        if (!imageInside && viewModel.bitmap != null) {
                            TelegramButton(
                                enabled = viewModel.bitmap != null,
                                isTelegramSpecs = viewModel.isTelegramSpecs,
                                onClick = { viewModel.setTelegramSpecs() },
                            )
                            IconButton(
                                onClick = {
                                    showSaveLoading = true
                                    context.shareBitmap(
                                        bitmap = viewModel.previewBitmap,
                                        bitmapInfo = viewModel.bitmapInfo,
                                        onComplete = {
                                            showSaveLoading = true
                                            showConfetti()
                                        }
                                    )
                                },
                                enabled = viewModel.previewBitmap != null
                            ) {
                                Icon(Icons.Outlined.Share, null)
                            }

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
                                .width(LocalBorderWidth.current.coerceAtLeast(0.25.dp))
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
                                        onEditExif = { showEditExifDialog.value = true },
                                        onCrop = { showCropDialog.value = true },
                                        onRotateLeft = viewModel::rotateLeft,
                                        onFlip = viewModel::flip,
                                        onRotateRight = viewModel::rotateRight
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    PresetWidget(
                                        selectedPreset = viewModel.presetSelected,
                                        onPresetSelected = {
                                            viewModel.setBitmapInfo(
                                                it.with(
                                                    viewModel.bitmap,
                                                    viewModel.bitmapInfo
                                                )
                                            )
                                        }
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    ResizeImageField(
                                        bitmapInfo = bitmapInfo,
                                        bitmap = viewModel.bitmap,
                                        onHeightChange = viewModel::updateHeight,
                                        onWidthChange = viewModel::updateWidth
                                    )
                                    if (bitmapInfo.mimeTypeInt.extension != "png") Spacer(
                                        Modifier.height(
                                            8.dp
                                        )
                                    )
                                    QualityWidget(
                                        visible = bitmapInfo.mimeTypeInt.extension != "png",
                                        enabled = viewModel.bitmap != null,
                                        quality = bitmapInfo.quality.coerceIn(0f, 100f),
                                        onQualityChange = viewModel::setQuality
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    ExtensionGroup(
                                        enabled = viewModel.bitmap != null,
                                        mime = bitmapInfo.mimeTypeInt,
                                        onMimeChange = viewModel::setMime
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    ResizeGroup(
                                        enabled = viewModel.bitmap != null,
                                        resizeType = bitmapInfo.resizeType,
                                        onResizeChange = viewModel::setResizeType
                                    )
                                } else if (!viewModel.isLoading) {
                                    ImageNotPickedWidget(onPickImage = pickImage)
                                }
                            }
                        }
                    }
                    if (!imageInside && viewModel.bitmap != null) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(LocalBorderWidth.current.coerceAtLeast(0.25.dp))
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


            if (showSaveLoading) {
                LoadingDialog()
            } else if (showResetDialog) {
                AlertDialog(
                    modifier = Modifier.alertDialog(),
                    icon = { Icon(Icons.Rounded.RestartAlt, null) },
                    title = { Text(stringResource(R.string.reset_image)) },
                    text = { Text(stringResource(R.string.reset_image_sub)) },
                    onDismissRequest = { showResetDialog = false },
                    confirmButton = {
                        OutlinedButton(
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            border = BorderStroke(
                                LocalBorderWidth.current,
                                MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                            ), onClick = { showResetDialog = false }) {
                            Text(stringResource(R.string.close))
                        }
                    },
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

            val showAddExifDialog = rememberSaveable { mutableStateOf(false) }
            var showClearExifDialog by rememberSaveable { mutableStateOf(false) }
            SimpleSheet(
                nestedScrollEnabled = false,
                endConfirmButtonPadding = 0.dp,
                confirmButton = {
                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        border = BorderStroke(
                            LocalBorderWidth.current,
                            MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                        ),
                        onClick = { showEditExifDialog.value = false }
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                },
                title = {
                    val count =
                        remember(map) {
                            BitmapUtils.tags.count {
                                it !in (map?.keys ?: emptyList())
                            }
                        }
                    Row {
                        if (map?.isEmpty() == false) {
                            OutlinedButton(
                                onClick = {
                                    showClearExifDialog = true
                                }, border = BorderStroke(
                                    LocalBorderWidth.current,
                                    MaterialTheme.colorScheme.outlineVariant()
                                )
                            ) {
                                Text(stringResource(R.string.clear))
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                        if (count > 0) {
                            OutlinedButton(
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                ),
                                border = BorderStroke(
                                    LocalBorderWidth.current,
                                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                ),
                                onClick = { showAddExifDialog.value = true }
                            ) {
                                Text(stringResource(R.string.add_tag))
                            }
                        }
                    }
                },
                visible = showEditExifDialog,
                sheetContent = {
                    if (map?.isEmpty() == false) {
                        TitleItem(
                            text = stringResource(id = R.string.edit_exif),
                            icon = Icons.Rounded.Dataset
                        )
                        Box {
                            LazyColumn(
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(map!!.toList()) { (tag, value) ->
                                    OutlinedCard(
                                        border = BorderStroke(
                                            LocalBorderWidth.current,
                                            MaterialTheme.colorScheme.outlineVariant()
                                        ),
                                        modifier = Modifier
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                                alpha = 0.5f
                                            )
                                        )
                                    ) {
                                        Column(Modifier.fillMaxWidth()) {
                                            Row {
                                                Text(
                                                    text = tag,
                                                    fontSize = 16.sp,
                                                    modifier = Modifier
                                                        .padding(12.dp)
                                                        .weight(1f),
                                                    textAlign = TextAlign.Start
                                                )
                                                IconButton(
                                                    onClick = {
                                                        viewModel.removeExifTag(
                                                            tag
                                                        )
                                                        map =
                                                            map?.toMutableMap()
                                                                ?.apply {
                                                                    remove(tag)
                                                                }
                                                    }
                                                ) {
                                                    Icon(
                                                        Icons.Rounded.RemoveCircleOutline,
                                                        null
                                                    )
                                                }
                                            }
                                            OutlinedTextField(
                                                onValueChange = {
                                                    viewModel.updateExifByTag(
                                                        tag,
                                                        it
                                                    )
                                                    map =
                                                        map?.toMutableMap()
                                                            ?.apply {
                                                                this[tag] = it
                                                            }
                                                },
                                                value = value,
                                                textStyle = LocalTextStyle.current.copy(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                keyboardOptions = KeyboardOptions.Default.copy(
                                                    imeAction = ImeAction.Next
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Divider(Modifier.align(Alignment.TopCenter))
                            Divider(Modifier.align(Alignment.BottomCenter))
                        }
                    } else {
                        Text(
                            stringResource(R.string.no_exif),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    SimpleSheet(
                        nestedScrollEnabled = false,
                        visible = showAddExifDialog,
                        sheetContent = {
                            Column {
                                val tags =
                                    remember(map) {
                                        BitmapUtils.tags.filter {
                                            it !in (map?.keys ?: emptyList())
                                        }.sorted()
                                    }
                                if (tags.isEmpty()) {
                                    SideEffect {
                                        showAddExifDialog.value = false
                                    }
                                }
                                var query by rememberSaveable { mutableStateOf("") }
                                val list = remember(tags, query) {
                                    tags.filter {
                                        query.lowercase() in it.lowercase()
                                    }
                                }
                                LazyColumn(
                                    contentPadding = PaddingValues(8.dp),
                                    modifier = Modifier.weight(1f, false)
                                ) {
                                    stickyHeader {
                                        Column(
                                            Modifier.background(
                                                MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                    2.dp
                                                )
                                            )
                                        ) {
                                            RoundedTextField(
                                                textStyle = LocalTextStyle.current.copy(
                                                    textAlign = TextAlign.Start
                                                ),
                                                shape = RoundedCornerShape(30),
                                                label = stringResource(R.string.search_here),
                                                onValueChange = { query = it },
                                                value = query
                                            )
                                            Spacer(Modifier.height(8.dp))
                                            Divider()
                                        }
                                    }
                                    item {
                                        Spacer(Modifier.height(8.dp))
                                    }
                                    items(list) { tag ->
                                        OutlinedCard(
                                            border = BorderStroke(
                                                LocalBorderWidth.current,
                                                MaterialTheme.colorScheme.outlineVariant()
                                            ),
                                            modifier = Modifier
                                                .padding(vertical = 4.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                                    alpha = 0.5f
                                                )
                                            )
                                        ) {
                                            Column(Modifier.fillMaxWidth()) {
                                                Row {
                                                    Text(
                                                        text = tag,
                                                        fontSize = 16.sp,
                                                        modifier = Modifier
                                                            .padding(12.dp)
                                                            .weight(1f),
                                                        textAlign = TextAlign.Start
                                                    )
                                                    IconButton(
                                                        onClick = {
                                                            viewModel.removeExifTag(
                                                                tag
                                                            )
                                                            map =
                                                                map?.toMutableMap()
                                                                    ?.apply {
                                                                        this[tag] =
                                                                            ""
                                                                    }
                                                        }
                                                    ) {
                                                        Icon(
                                                            Icons.Rounded.AddCircle,
                                                            null
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (list.isEmpty()) {
                                        item {
                                            Box(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        top = 16.dp,
                                                        bottom = 16.dp,
                                                        start = 24.dp,
                                                        end = 24.dp
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(stringResource(R.string.nothing_found_by_search))
                                            }
                                        }
                                    }
                                }
                                Divider()
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .padding(end = 16.dp)
                                        .navigationBarsPadding()
                                ) {
                                    TitleItem(
                                        text = stringResource(R.string.add_tag),
                                        icon = Icons.Rounded.Dataset
                                    )
                                    Spacer(Modifier.weight(1f))
                                    OutlinedButton(
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary,
                                        ),
                                        border = BorderStroke(
                                            LocalBorderWidth.current,
                                            MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                                        ),
                                        onClick = { showAddExifDialog.value = false }
                                    ) {
                                        Text(stringResource(R.string.ok))
                                    }
                                }
                            }
                        }
                    )
                    if (showClearExifDialog) {
                        AlertDialog(
                            modifier = Modifier.alertDialog(),
                            onDismissRequest = { showClearExifDialog = false },
                            title = { Text(stringResource(R.string.clear_exif)) },
                            icon = { Icon(Icons.Rounded.Delete, null) },
                            confirmButton = {
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                    border = BorderStroke(
                                        LocalBorderWidth.current,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                                    ),
                                    onClick = { showClearExifDialog = false }
                                ) {
                                    Text(stringResource(R.string.cancel))
                                }
                            },
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
                                        showClearExifDialog = false
                                        map = emptyMap()
                                        viewModel.clearExif()
                                    }
                                ) {
                                    Text(stringResource(R.string.clear))
                                }
                            },
                            text = {
                                Text(stringResource(R.string.clear_exif_sub))
                            }
                        )
                    }
                }
            )

            viewModel.bitmap?.let {
                val bmp = remember(it) {
                    if (!it.canShow()) it.resizeBitmap(4000, 4000, 1).asImageBitmap()
                    else it.asImageBitmap()
                }
                var crop by remember { mutableStateOf(false) }
                SimpleSheet(
                    sheetContent = {
                        ImageCropper(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                )
                                .border(
                                    LocalBorderWidth.current.coerceAtLeast(1.dp),
                                    MaterialTheme.colorScheme.outlineVariant(),
                                    RoundedCornerShape(4.dp)
                                )
                                .background(
                                    MaterialTheme.colorScheme
                                        .outlineVariant()
                                        .copy(alpha = 0.1f),
                                    RoundedCornerShape(4.dp)
                                ),
                            imageBitmap = bmp,
                            contentDescription = null,
                            cropProperties = CropDefaults.properties(
                                cropOutlineProperty = CropOutlineProperty(
                                    OutlineType.Rect,
                                    RectCropShape(0, "")
                                )
                            ),
                            onCropStart = {},
                            crop = crop,
                            onCropSuccess = { image ->
                                viewModel.updateBitmap(image.asAndroidBitmap())
                                crop = false
                                showCropDialog.value = false
                            }
                        )
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .padding(end = 16.dp)
                                .navigationBarsPadding()
                        ) {
                            TitleItem(
                                text = stringResource(R.string.cropping),
                                icon = Icons.Rounded.Crop
                            )
                            Spacer(Modifier.weight(1f))
                            OutlinedButton(
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                border = BorderStroke(
                                    LocalBorderWidth.current,
                                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                                ),
                                onClick = { crop = true }
                            ) {
                                Text(stringResource(R.string.crop))
                            }
                        }
                    },
                    visible = showCropDialog
                )
            }

            ExitWithoutSavingDialog(
                onExit = onGoBack,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )

            BackHandler {
                if (viewModel.bitmap != null) showExitDialog = true
                else onGoBack()
            }

        }
    }
}
