package ru.tech.imageresizershrinker.crop_screen


import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cookhelper.dynamic.theme.LocalDynamicThemeState
import com.smarttoolfactory.cropper.ImageCropper
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.crop_screen.components.AspectRatioSelection
import ru.tech.imageresizershrinker.crop_screen.components.aspectRatios
import ru.tech.imageresizershrinker.crop_screen.viewModel.CropViewModel
import ru.tech.imageresizershrinker.generate_palette.isScrollingUp
import ru.tech.imageresizershrinker.main_screen.components.Screen
import ru.tech.imageresizershrinker.resize_screen.components.ImageNotPickedWidget
import ru.tech.imageresizershrinker.resize_screen.components.LoadingDialog
import ru.tech.imageresizershrinker.resize_screen.components.ToastHost
import ru.tech.imageresizershrinker.resize_screen.components.rememberToastHostState
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri
import ru.tech.imageresizershrinker.utils.BitmapUtils.shareBitmap
import ru.tech.imageresizershrinker.utils.ContextUtils.isExternalStorageWritable
import ru.tech.imageresizershrinker.utils.ContextUtils.requestPermission
import ru.tech.imageresizershrinker.utils.SavingFolder
import ru.tech.imageresizershrinker.widget.Marquee

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropScreen(
    uriState: Uri?,
    navController: NavController<Screen>,
    onGoBack: () -> Unit,
    pushNewUri: (Uri?) -> Unit,
    getSavingFolder: (name: String, ext: String) -> SavingFolder,
    savingPathString: String,
    viewModel: CropViewModel = viewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = rememberToastHostState()
    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current

    LaunchedEffect(uriState) {
        uriState?.let {
            pushNewUri(null)
            context.decodeBitmapFromUri(
                uri = it,
                onGetMimeType = viewModel::updateMimeType,
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
            themeState.updateColorByImage(it)
        }
    }

    val pickImageLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                context.decodeBitmapFromUri(
                    uri = it,
                    onGetMimeType = {},
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
    val saveBitmap: (Bitmap) -> Unit = {
        showSaveLoading = true
        viewModel.saveBitmap(
            bitmap = it,
            isExternalStorageWritable = context.isExternalStorageWritable(),
            getSavingFolder = getSavingFolder
        ) { success ->
            if (!success) context.requestPermission()
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
            }
            showSaveLoading = false
        }
    }


    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

    var crop by remember { mutableStateOf(false) }
    var share by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (viewModel.bitmap == null) {
                LargeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.shadow(6.dp),
                    title = {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
                            Text(stringResource(R.string.crop))
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
                                if (navController.backstack.entries.isNotEmpty()) navController.pop()
                                onGoBack()
                                themeState.reset()
                            }
                        ) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                share = true
                                crop = true
                            },
                            enabled = viewModel.bitmap != null
                        ) {
                            Icon(Icons.Outlined.Share, null)
                        }
                    }
                )
            } else {
                TopAppBar(
                    modifier = Modifier.shadow(6.dp),
                    title = {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
                            Text(stringResource(R.string.crop))
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
                                if (navController.backstack.entries.isNotEmpty()) navController.pop()
                                onGoBack()
                                themeState.reset()
                            }
                        ) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                share = true
                                crop = true
                            },
                            enabled = viewModel.bitmap != null
                        ) {
                            Icon(Icons.Outlined.Share, null)
                        }
                    }
                )
            }
            viewModel.bitmap?.let {
                val bmp = remember(it) { it.asImageBitmap() }
                Column {
                    val cropProperties = viewModel.cropProperties
                    ImageCropper(
                        background = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f),
                        imageBitmap = bmp,
                        contentDescription = null,
                        cropProperties = cropProperties,
                        onCropStart = {},
                        crop = crop,
                        onCropSuccess = { image ->
                            if (share) {
                                context.shareBitmap(
                                    bitmap = image.asAndroidBitmap(),
                                    compressFormat = viewModel.mimeType
                                )
                            } else {
                                saveBitmap(image.asAndroidBitmap())
                            }
                            crop = false
                            share = false
                        }
                    )

                    val aspectRatios = aspectRatios()
                    AspectRatioSelection(
                        modifier = Modifier.fillMaxWidth(),
                        selectedIndex = aspectRatios.indexOfFirst { cr ->
                            cr.aspectRatio == viewModel.cropProperties.aspectRatio
                        }
                    ) { aspect ->
                        viewModel.setCropAspectRatio(aspect.aspectRatio)
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .fillMaxWidth()
                                .height(88.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            FloatingActionButton(
                                onClick = pickImage,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                            ) {
                                val expanded = scrollState.isScrollingUp()
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
                            Spacer(modifier = Modifier.width(16.dp))
                            FloatingActionButton(
                                onClick = {
                                    crop = true
                                },
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                            ) {
                                Icon(Icons.Rounded.Save, null)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
            } ?: Column(Modifier.verticalScroll(scrollState)) {
                ImageNotPickedWidget(
                    onPickImage = pickImage,
                    modifier = Modifier
                        .padding(bottom = 88.dp, top = 20.dp, start =  20.dp, end = 20.dp)
                        .navigationBarsPadding()
                )
            }
        }

        if (viewModel.bitmap == null) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
                    .align(Alignment.BottomEnd)
            ) {
                FloatingActionButton(
                    onClick = pickImage
                ) {
                    val expanded = scrollState.isScrollingUp()
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
                if (viewModel.bitmap != null) {
                    Spacer(modifier = Modifier.width(16.dp))
                    FloatingActionButton(
                        onClick = {
                            crop = true
                        },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Icon(Icons.Rounded.Save, null)
                    }
                }
            }
        }
    }

    if (showSaveLoading || viewModel.isLoading) {
        LoadingDialog()
    }

    ToastHost(hostState = toastHostState)
    BackHandler {
        if (navController.backstack.entries.isNotEmpty()) navController.pop()
        onGoBack()
        themeState.reset()
    }
}