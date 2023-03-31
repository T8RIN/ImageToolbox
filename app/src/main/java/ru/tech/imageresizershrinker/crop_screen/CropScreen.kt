package ru.tech.imageresizershrinker.crop_screen


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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cookhelper.dynamic.theme.LocalDynamicThemeState
import com.smarttoolfactory.cropper.ImageCropper
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.crop_screen.components.AspectRatioSelection
import ru.tech.imageresizershrinker.crop_screen.components.aspectRatios
import ru.tech.imageresizershrinker.crop_screen.viewModel.CropViewModel
import ru.tech.imageresizershrinker.generate_palette.isScrollingUp
import ru.tech.imageresizershrinker.main_screen.components.LocalAllowChangeColorByImage
import ru.tech.imageresizershrinker.main_screen.components.alertDialog
import ru.tech.imageresizershrinker.main_screen.components.drawHorizontalStroke
import ru.tech.imageresizershrinker.main_screen.components.fabBorder
import ru.tech.imageresizershrinker.resize_screen.components.ImageNotPickedWidget
import ru.tech.imageresizershrinker.resize_screen.components.LoadingDialog
import ru.tech.imageresizershrinker.resize_screen.components.LocalToastHost
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri
import ru.tech.imageresizershrinker.utils.BitmapUtils.shareBitmap
import ru.tech.imageresizershrinker.utils.ContextUtils.isExternalStorageWritable
import ru.tech.imageresizershrinker.utils.ContextUtils.requestPermission
import ru.tech.imageresizershrinker.utils.SavingFolder
import ru.tech.imageresizershrinker.widget.Marquee

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CropScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    pushNewUri: (Uri?) -> Unit,
    getSavingFolder: (name: String, ext: String) -> SavingFolder,
    savingPathString: String,
    showConfetti: () -> Unit,
    viewModel: CropViewModel = viewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHost.current
    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = LocalAllowChangeColorByImage.current

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

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
            if (allowChangeColor) themeState.updateColorByImage(it)
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
                showConfetti()
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
                    modifier = Modifier.drawHorizontalStroke(),
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
                                if (viewModel.bitmap != null) showExitDialog = true
                                else onGoBack()
                            }
                        ) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    }
                )
            } else {
                TopAppBar(
                    modifier = Modifier.drawHorizontalStroke(),
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
                                if (viewModel.bitmap != null) showExitDialog = true
                                else onGoBack()
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
                    AnimatedContent(cropProperties.cropType, Modifier.weight(1f)) { type ->
                        ImageCropper(
                            background = MaterialTheme.colorScheme.surface,
                            imageBitmap = bmp,
                            contentDescription = null,
                            cropProperties = cropProperties.copy(cropType = type),
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
                    }


                    val aspectRatios = aspectRatios()
                    AspectRatioSelection(
                        modifier = Modifier.fillMaxWidth(),
                        selectedIndex = aspectRatios.indexOfFirst { cr ->
                            cr.aspectRatio == viewModel.cropProperties.aspectRatio
                        }
                    ) { aspect ->
                        viewModel.setCropAspectRatio(aspect.aspectRatio)
                    }
                    BottomAppBar(
                        modifier = Modifier.drawHorizontalStroke(true),
                        actions = {},
                        floatingActionButton = {
                            Row {
                                FloatingActionButton(
                                    onClick = {
                                        crop = true
                                    },
                                    modifier = Modifier.fabBorder(),
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                ) {
                                    Icon(Icons.Rounded.Save, null)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                FloatingActionButton(
                                    onClick = pickImage,
                                    modifier = Modifier.fabBorder(),
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                ) {
                                    val expanded =
                                        scrollState.isScrollingUp() && viewModel.bitmap == null
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
                    )
                }
            } ?: Column(Modifier.verticalScroll(scrollState)) {
                ImageNotPickedWidget(
                    onPickImage = pickImage,
                    modifier = Modifier
                        .padding(bottom = 88.dp, top = 20.dp, start = 20.dp, end = 20.dp)
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
                    onClick = pickImage,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    modifier = Modifier.fabBorder(),
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
            }
        }
    }

    if (showSaveLoading || viewModel.isLoading) {
        LoadingDialog()
    } else if (showExitDialog) {
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
                        1.dp,
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
                        1.dp,
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
    }

    BackHandler {
        if (viewModel.bitmap != null) showExitDialog = true
        else onGoBack()
    }
}