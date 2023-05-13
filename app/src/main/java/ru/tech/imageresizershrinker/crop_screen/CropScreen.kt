package ru.tech.imageresizershrinker.crop_screen


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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.AspectRatio
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.crop_screen.components.AspectRatioSelection
import ru.tech.imageresizershrinker.crop_screen.components.aspectRatios
import ru.tech.imageresizershrinker.crop_screen.viewModel.CropViewModel
import ru.tech.imageresizershrinker.generate_palette_screen.isScrollingUp
import ru.tech.imageresizershrinker.main_screen.components.LocalAlignment
import ru.tech.imageresizershrinker.main_screen.components.LocalAllowChangeColorByImage
import ru.tech.imageresizershrinker.main_screen.components.LocalBorderWidth
import ru.tech.imageresizershrinker.resize_screen.components.ImageNotPickedWidget
import ru.tech.imageresizershrinker.resize_screen.components.LoadingDialog
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri
import ru.tech.imageresizershrinker.utils.BitmapUtils.shareBitmap
import ru.tech.imageresizershrinker.utils.ContextUtils.isExternalStorageWritable
import ru.tech.imageresizershrinker.utils.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.utils.SavingFolder
import ru.tech.imageresizershrinker.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.utils.modifier.navBarsPaddingOnlyIfTheyAtTheBottom
import ru.tech.imageresizershrinker.utils.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.widget.LocalToastHost
import ru.tech.imageresizershrinker.widget.Marquee

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CropScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    pushNewUri: (Uri?) -> Unit,
    getSavingFolder: (ext: String) -> SavingFolder,
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
                onGetBitmap = { bmp ->
                    viewModel.updateBitmap(
                        bitmap = bmp, newBitmap = true
                    )
                },
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
                    onGetBitmap = { bmp ->
                        viewModel.updateBitmap(
                            bitmap = bmp, newBitmap = true
                        )
                    },
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


    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

    val portrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    var crop by remember { mutableStateOf(false) }
    var share by remember { mutableStateOf(false) }
    var save by remember { mutableStateOf(false) }
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
            }
            else {
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
                                viewModel.resetBitmap()
                            },
                            enabled = viewModel.bitmap != null
                        ) {
                            Icon(Icons.Outlined.RestartAlt, null)
                        }
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
                if (portrait) {
                    Column {
                        AnimatedContent(
                            targetState = (viewModel.cropProperties.aspectRatio != AspectRatio.Original) to it,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        ) { (fixedAspectRatio, bitmap) ->
                            val bmp = remember(bitmap) { it.asImageBitmap() }
                            ImageCropper(
                                background = MaterialTheme.colorScheme.surface,
                                imageBitmap = bmp,
                                contentDescription = null,
                                cropProperties = viewModel.cropProperties.copy(fixedAspectRatio = fixedAspectRatio),
                                onCropStart = {},
                                crop = crop,
                                onCropSuccess = { image ->
                                    if (share) {
                                        context.shareBitmap(
                                            bitmap = image.asAndroidBitmap(),
                                            compressFormat = viewModel.mimeType
                                        )
                                    } else if (save) {
                                        saveBitmap(image.asAndroidBitmap())
                                    } else {
                                        viewModel.updateBitmap(image.asAndroidBitmap())
                                    }
                                    save = false
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
                            actions = {
                                var job by remember { mutableStateOf<Job?>(null) }
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    ),
                                    border = BorderStroke(
                                        LocalBorderWidth.current,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    onClick = {
                                        job?.cancel()
                                        job = scope.launch {
                                            kotlinx.coroutines.delay(500)
                                            crop = true
                                        }
                                    }
                                ) {
                                    Text(stringResource(R.string.crop))
                                }
                            },
                            floatingActionButton = {
                                Row {
                                    FloatingActionButton(
                                        onClick = pickImage,
                                        modifier = Modifier.fabBorder(),
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
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
                                    Spacer(modifier = Modifier.width(16.dp))
                                    FloatingActionButton(
                                        onClick = {
                                            crop = true
                                            save = true
                                        },
                                        modifier = Modifier.fabBorder(),
                                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                    ) {
                                        Icon(Icons.Rounded.Save, null)
                                    }
                                }
                            }
                        )
                    }
                } else {
                    Row(Modifier.navBarsPaddingOnlyIfTheyAtTheEnd()) {
                        Box(
                            Modifier
                                .weight(0.8f)
                                .padding(20.dp)
                        ) {
                            AnimatedContent(
                                targetState = (viewModel.cropProperties.aspectRatio != AspectRatio.Original) to it,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxSize()
                                    .navBarsPaddingOnlyIfTheyAtTheBottom()
                            ) { (fixedAspectRatio, bitmap) ->
                                val bmp = remember(bitmap) { it.asImageBitmap() }
                                ImageCropper(
                                    background = MaterialTheme.colorScheme.surface,
                                    imageBitmap = bmp,
                                    contentDescription = null,
                                    cropProperties = viewModel.cropProperties.copy(fixedAspectRatio = fixedAspectRatio),
                                    onCropStart = {},
                                    crop = crop,
                                    onCropSuccess = { image ->
                                        if (share) {
                                            context.shareBitmap(
                                                bitmap = image.asAndroidBitmap(),
                                                compressFormat = viewModel.mimeType
                                            )
                                        } else if (save) {
                                            saveBitmap(image.asAndroidBitmap())
                                        } else {
                                            viewModel.updateBitmap(image.asAndroidBitmap())
                                        }
                                        save = false
                                        crop = false
                                        share = false
                                    }
                                )
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(LocalBorderWidth.current.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        val aspectRatios = aspectRatios()
                        AspectRatioSelection(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(horizontal = 20.dp),
                            horizontal = false,
                            selectedIndex = aspectRatios.indexOfFirst { cr ->
                                cr.aspectRatio == viewModel.cropProperties.aspectRatio
                            }
                        ) { aspect ->
                            viewModel.setCropAspectRatio(aspect.aspectRatio)
                        }
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(LocalBorderWidth.current.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(start = 20.dp)
                        )
                        Column(
                            Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxHeight()
                                .navigationBarsPadding(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            FloatingActionButton(
                                onClick = pickImage,
                                modifier = Modifier.fabBorder(),
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                content = {
                                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            var job by remember { mutableStateOf<Job?>(null) }
                            FloatingActionButton(
                                onClick = {
                                    job?.cancel()
                                    job = scope.launch {
                                        kotlinx.coroutines.delay(500)
                                        crop = true
                                    }
                                },
                                modifier = Modifier.fabBorder(),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            ) {
                                Icon(Icons.Rounded.Crop, null)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            FloatingActionButton(
                                onClick = {
                                    crop = true
                                    save = true
                                },
                                modifier = Modifier.fabBorder(),
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            ) {
                                Icon(Icons.Rounded.Save, null)
                            }
                        }
                    }
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
                    .align(LocalAlignment.current)
            ) {
                ExtendedFloatingActionButton(
                    onClick = pickImage,
                    modifier = Modifier.fabBorder(),
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    text = {
                        Text(stringResource(R.string.pick_image_alt))
                    },
                    icon = {
                        Icon(Icons.Rounded.AddPhotoAlternate, null)
                    }
                )
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
    }

    BackHandler {
        if (viewModel.bitmap != null) showExitDialog = true
        else onGoBack()
    }
}