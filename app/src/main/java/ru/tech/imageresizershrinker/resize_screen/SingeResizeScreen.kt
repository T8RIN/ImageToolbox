package ru.tech.imageresizershrinker.resize_screen

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cookhelper.dynamic.theme.LocalDynamicThemeState
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.components.Screen
import ru.tech.imageresizershrinker.main_screen.components.navBarsLandscapePadding
import ru.tech.imageresizershrinker.resize_screen.components.*
import ru.tech.imageresizershrinker.resize_screen.viewModel.SingleResizeViewModel
import ru.tech.imageresizershrinker.utils.BitmapUtils
import ru.tech.imageresizershrinker.utils.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri
import ru.tech.imageresizershrinker.utils.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.shareBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.toMap
import ru.tech.imageresizershrinker.utils.ContextUtils.isExternalStorageWritable
import ru.tech.imageresizershrinker.utils.ContextUtils.requestPermission
import ru.tech.imageresizershrinker.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.utils.SavingFolder
import ru.tech.imageresizershrinker.widget.Marquee

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Context.SingleResizeScreen(
    viewModel: SingleResizeViewModel = viewModel(),
    onGoBack: () -> Unit,
    pushNewUri: (Uri?) -> Unit,
    uriState: Uri?,
    getSavingFolder: (name: String, ext: String) -> SavingFolder,
    savingPathString: String,
    navController: NavController<Screen>
) {
    val toastHostState = rememberToastHostState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current as ComponentActivity
    val themeState = LocalDynamicThemeState.current
    LaunchedEffect(uriState) {
        uriState?.let {
            decodeBitmapFromUri(
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
            pushNewUri(null)
        }
    }
    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            themeState.updateColorByImage(it)
        }
    }

    val focus = LocalFocusManager.current
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showSaveLoading by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }
    var showEditExifDialog by rememberSaveable { mutableStateOf(false) }
    var showExifSavingDialog by rememberSaveable { mutableStateOf(false) }
    var showCropDialog by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val state = rememberLazyListState()

    val bitmapInfo = viewModel.bitmapInfo
    var map by remember(viewModel.exif) {
        mutableStateOf(viewModel.exif?.toMap())
    }

    val pickImageLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                decodeBitmapFromUri(
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
        pickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    val saveBitmap = {
        showSaveLoading = true
        viewModel.saveBitmap(
            isExternalStorageWritable = context.isExternalStorageWritable(),
            getSavingFolder = getSavingFolder,
            getFileDescriptor = { uri ->
                uri?.let { context.contentResolver.openFileDescriptor(it, "rw", null) }
            }
        ) { success ->
            if (!success) context.requestPermission()
            else {
                scope.launch {
                    toastHostState.showToast(
                        getString(
                            R.string.saved_to,
                            savingPathString
                        ),
                        Icons.Rounded.Save
                    )
                }
            }
            showSaveLoading = false
        }
    }

    val imageInside =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val imageBlock = @Composable {
        AnimatedContent(
            targetState = Triple(viewModel.previewBitmap, viewModel.isLoading, showOriginal),
            transitionSpec = { fadeIn() with fadeOut() }
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

    val buttons = @Composable {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.End
        ) {
            if (viewModel.bitmap != null) {
                FloatingActionButton(
                    onClick = {
                        if (bitmapInfo.mime.extension !in listOf(
                                "jpg",
                                "jpeg"
                            ) && viewModel.bitmap != null && map?.isNotEmpty() == true
                        ) {
                            showExifSavingDialog = true
                        } else {
                            saveBitmap()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    BadgedBox(
                        badge = {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = bitmapInfo.mime.extension !in listOf(
                                    "jpg",
                                    "jpeg"
                                ) && map?.isNotEmpty() == true,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                Badge(modifier = Modifier.size(8.dp))
                            }
                        }
                    ) {
                        Icon(Icons.Rounded.Save, null)
                    }
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
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
                            AnimatedContent(
                                targetState = viewModel.bitmap to viewModel.isLoading,
                                transitionSpec = { fadeIn() with fadeOut() }
                            ) { (bmp, loading) ->
                                if (bmp == null) {
                                    Text(
                                        stringResource(R.string.single_resize),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else if (!loading) {
                                    Text(
                                        stringResource(
                                            R.string.size,
                                            byteCount(bitmapInfo.size.toLong())
                                        ),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        stringResource(R.string.loading),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
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
                        Row {
                            TelegramButton(
                                enabled = viewModel.bitmap != null,
                                isTelegramSpecs = viewModel.isTelegramSpecs,
                                onClick = { viewModel.setTelegramSpecs() },
                            )
                            IconButton(
                                onClick = {
                                    shareBitmap(
                                        bitmap = viewModel.previewBitmap,
                                        bitmapInfo = viewModel.bitmapInfo
                                    )
                                },
                                enabled = viewModel.previewBitmap != null
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
                                        onEditExif = { showEditExifDialog = true },
                                        onCrop = { showCropDialog = true },
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
                                if (bitmapInfo.mime.extension != "png") Spacer(Modifier.height(8.dp))
                                QualityWidget(
                                    visible = bitmapInfo.mime.extension != "png",
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
                LoadingDialog()
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
                                    getString(R.string.values_reset),
                                    Icons.Rounded.DoneOutline
                                )
                            }
                        }) {
                            Text(stringResource(R.string.reset))
                        }
                    }
                )
            } else if (showEditExifDialog) {
                var showAddExifDialog by rememberSaveable { mutableStateOf(false) }
                var showClearExifDialog by rememberSaveable { mutableStateOf(false) }
                AlertDialog(
                    modifier = Modifier
                        .systemBarsPadding()
                        .animateContentSize()
                        .widthIn(max = 640.dp)
                        .padding(16.dp),
                    onDismissRequest = { showEditExifDialog = false },
                    title = { Text(stringResource(R.string.edit_exif)) },
                    icon = { Icon(Icons.Rounded.Dataset, null) },
                    confirmButton = {
                        Button(
                            onClick = { showEditExifDialog = false }
                        ) {
                            Text(stringResource(R.string.ok))
                        }
                    },
                    dismissButton = {
                        val count =
                            remember(map) {
                                BitmapUtils.tags.count {
                                    it !in (map?.keys ?: emptyList())
                                }
                            }
                        Row {
                            if (map?.isEmpty() == false) {
                                OutlinedButton(onClick = {
                                    showClearExifDialog = true
                                }) {
                                    Text(stringResource(R.string.clear))
                                }
                                Spacer(Modifier.width(8.dp))
                            }
                            if (count > 0) {
                                FilledTonalButton(
                                    onClick = { showAddExifDialog = true }
                                ) {
                                    Text(stringResource(R.string.add_tag))
                                }
                            }
                        }
                    },
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                    text = {
                        if (map?.isEmpty() == false) {
                            Box {
                                Divider(Modifier.align(Alignment.TopCenter))
                                Column(Modifier.verticalScroll(rememberScrollState())) {
                                    Spacer(Modifier.height(8.dp))
                                    map?.forEach { (tag, value) ->
                                        Card(
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
                                                            .weight(1f)
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
                                    Spacer(Modifier.height(8.dp))
                                }
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
                    }
                )
                if (showAddExifDialog) {
                    AlertDialog(
                        modifier = Modifier
                            .systemBarsPadding()
                            .animateContentSize()
                            .widthIn(max = 640.dp)
                            .padding(16.dp),
                        onDismissRequest = { showAddExifDialog = false },
                        title = { Text(stringResource(R.string.add_tag)) },
                        icon = { Icon(Icons.Rounded.Dataset, null) },
                        confirmButton = {
                            Button(
                                onClick = { showAddExifDialog = false }
                            ) {
                                Text(stringResource(R.string.ok))
                            }
                        },
                        properties = DialogProperties(usePlatformDefaultWidth = false),
                        text = {
                            Box {
                                Divider(Modifier.align(Alignment.TopCenter))
                                Column(
                                    Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    val tags =
                                        remember(map) {
                                            BitmapUtils.tags.filter {
                                                it !in (map?.keys ?: emptyList())
                                            }.sorted()
                                        }
                                    if (tags.isEmpty()) {
                                        SideEffect {
                                            showAddExifDialog = false
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    tags.forEach { tag ->
                                        Card(
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
                                                            .weight(1f)
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
                                    Spacer(Modifier.height(8.dp))
                                }
                                Divider(Modifier.align(Alignment.BottomCenter))
                            }
                        }
                    )
                } else if (showClearExifDialog) {
                    AlertDialog(
                        onDismissRequest = { showClearExifDialog = false },
                        title = { Text(stringResource(R.string.clear_exif)) },
                        icon = { Icon(Icons.Rounded.Delete, null) },
                        confirmButton = {
                            Button(
                                onClick = { showClearExifDialog = false }
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        },
                        dismissButton = {
                            FilledTonalButton(
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
            } else if (showExifSavingDialog) {
                AlertDialog(
                    icon = { Icon(Icons.Rounded.Save, null) },
                    title = { Text(stringResource(R.string.exif)) },
                    text = { Text(stringResource(R.string.might_be_error_with_exif)) },
                    onDismissRequest = { showExifSavingDialog = false },
                    confirmButton = {
                        Button(onClick = { showExifSavingDialog = false }) {
                            Text(stringResource(R.string.close))
                        }
                    },
                    dismissButton = {
                        FilledTonalButton(
                            onClick = {
                                showExifSavingDialog = false
                                saveBitmap()
                            }
                        ) {
                            Text(stringResource(R.string.save))
                        }
                    }
                )
            } else if (viewModel.bitmap != null && showCropDialog) {
                viewModel.bitmap?.let {
                    val bmp = remember(it) {
                        if (!it.canShow()) it.resizeBitmap(4000, 4000, 1).asImageBitmap()
                        else it.asImageBitmap()
                    }
                    var crop by remember { mutableStateOf(false) }
                    AlertDialog(
                        modifier = Modifier
                            .systemBarsPadding()
                            .animateContentSize()
                            .widthIn(max = 640.dp)
                            .padding(16.dp),
                        properties = DialogProperties(usePlatformDefaultWidth = false),
                        onDismissRequest = { showCropDialog = false },
                        text = {
                            ImageCropper(
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
                                    showCropDialog = false
                                }
                            )
                        },
                        confirmButton = {
                            Button(onClick = { crop = true }) {
                                Text(stringResource(R.string.crop))
                            }
                        },
                        dismissButton = {
                            FilledTonalButton(onClick = { showCropDialog = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }
            }

            ToastHost(hostState = toastHostState)
            BackHandler {
                if (viewModel.bitmap != null) showExitDialog = true
                else if (navController.backstack.entries.isNotEmpty()) {
                    navController.pop()
                    onGoBack()
                }
            }

        }
    }
}