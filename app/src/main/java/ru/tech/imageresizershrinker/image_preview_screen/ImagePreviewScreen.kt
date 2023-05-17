package ru.tech.imageresizershrinker.image_preview_screen

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smarttoolfactory.image.zoom.ZoomLevel
import com.smarttoolfactory.image.zoom.animatedZoom
import com.smarttoolfactory.image.zoom.rememberAnimatedZoomState
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.getAppColorTuple
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.image_preview_screen.viewModel.ImagePreviewViewModel
import ru.tech.imageresizershrinker.main_screen.components.BytesResizePreference
import ru.tech.imageresizershrinker.main_screen.components.CropPreference
import ru.tech.imageresizershrinker.main_screen.components.DeleteExifPreference
import ru.tech.imageresizershrinker.main_screen.components.GeneratePalettePreference
import ru.tech.imageresizershrinker.main_screen.components.LocalAlignment
import ru.tech.imageresizershrinker.main_screen.components.LocalAllowChangeColorByImage
import ru.tech.imageresizershrinker.main_screen.components.LocalAppColorTuple
import ru.tech.imageresizershrinker.main_screen.components.LocalBorderWidth
import ru.tech.imageresizershrinker.main_screen.components.LocalDynamicColors
import ru.tech.imageresizershrinker.main_screen.components.LocalNightMode
import ru.tech.imageresizershrinker.main_screen.components.LocalSelectedEmoji
import ru.tech.imageresizershrinker.main_screen.components.PickColorPreference
import ru.tech.imageresizershrinker.main_screen.components.Screen
import ru.tech.imageresizershrinker.main_screen.components.SingleResizePreference
import ru.tech.imageresizershrinker.main_screen.components.isNightMode
import ru.tech.imageresizershrinker.resize_screen.components.ImageNotPickedWidget
import ru.tech.imageresizershrinker.theme.CreateAlt
import ru.tech.imageresizershrinker.theme.EmojiItem
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeSampledBitmapFromUri
import ru.tech.imageresizershrinker.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.utils.modifier.scaleOnTap
import ru.tech.imageresizershrinker.widget.Marquee
import ru.tech.imageresizershrinker.widget.Picture

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    pushNewUris: (List<Uri>?) -> Unit,
    navController: NavController<Screen>,
    showConfetti: () -> Unit,
    viewModel: ImagePreviewViewModel = viewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = LocalAllowChangeColorByImage.current

    val appColorTuple = getAppColorTuple(
        defaultColorTuple = LocalAppColorTuple.current,
        dynamicColor = LocalDynamicColors.current,
        darkTheme = LocalNightMode.current.isNightMode()
    )

    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() && it != listOf(viewModel.selectedUri) }?.let { uris ->
            viewModel.updateUris(uris)
            pushNewUris(null)
        }
    }

    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor) themeState.updateColorByImage(it)
        } ?: themeState.updateColorTuple(appColorTuple)
    }

    val pickImageLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia()
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let {
                viewModel.updateUris(list)
            }
        }

    val pickImage = {
        pickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    var showImagePreviewDialog by rememberSaveable { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.background
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
                            Text(
                                stringResource(R.string.image_preview),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = { onGoBack() }
                        ) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        EmojiItem(
                            emoji = LocalSelectedEmoji.current,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .scaleOnTap(onRelease = showConfetti),
                        )
                    }
                )
                if (viewModel.uris.isNullOrEmpty()) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ImageNotPickedWidget(
                            onPickImage = pickImage,
                            modifier = Modifier.padding(
                                PaddingValues(
                                    bottom = 88.dp + WindowInsets
                                        .navigationBars
                                        .asPaddingValues()
                                        .calculateBottomPadding(),
                                    top = 12.dp,
                                    end = 12.dp,
                                    start = 12.dp
                                )
                            )
                        )
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(150.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalItemSpacing = 12.dp,
                        horizontalArrangement = Arrangement.spacedBy(
                            12.dp,
                            Alignment.CenterHorizontally
                        ),
                        contentPadding = PaddingValues(
                            bottom = 88.dp + WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding(),
                            top = 12.dp,
                            end = 12.dp,
                            start = 12.dp
                        )
                    ) {
                        viewModel.uris?.forEach {
                            item {
                                Picture(
                                    model = it,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.large)
                                        .clickable {
                                            showImagePreviewDialog = true
                                            viewModel.selectUri(it)
                                            viewModel.updateBitmap {
                                                context.decodeSampledBitmapFromUri(
                                                    uri = it,
                                                    reqWidth = 300,
                                                    reqHeight = 300
                                                )
                                            }
                                        }
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.shapes.large
                                        )
                                        .border(
                                            LocalBorderWidth.current,
                                            MaterialTheme.colorScheme.outlineVariant(),
                                            MaterialTheme.shapes.large
                                        ),
                                    shape = MaterialTheme.shapes.large
                                )
                            }
                        }
                    }
                }
            }

            ExtendedFloatingActionButton(
                onClick = pickImage,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .fabBorder()
                    .align(LocalAlignment.current),
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                text = {
                    Text(stringResource(R.string.pick_image_alt))
                },
                icon = {
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                }
            )

            BackHandler { onGoBack() }
        }
    }

    fun <T> animationSpec(
        duration: Int = 500,
        delay: Int = 0
    ) = tween<T>(
        durationMillis = duration,
        delayMillis = delay,
        easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    )

    AnimatedVisibility(
        showImagePreviewDialog,
        modifier = Modifier.fillMaxSize(),
        enter = slideInHorizontally(
            animationSpec()
        ) { -it / 3 } + fadeIn(
            animationSpec(500)
        ),
        exit = slideOutHorizontally(
            animationSpec()
        ) { -it / 3 } + fadeOut(
            animationSpec(500)
        )
    ) {
        var wantToEdit by remember { mutableStateOf(false) }
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                .pointerInput(Unit) {
                    detectTapGestures { }
                }
        ) {
            Picture(
                model = viewModel.selectedUri,
                modifier = Modifier
                    .fillMaxSize()
                    .animatedZoom(
                        animatedZoomState = rememberAnimatedZoomState(
                            minZoom = 0.5f,
                            maxZoom = 6f,
                            moveToBounds = true,
                            zoomable = true,
                            pannable = true,
                            rotatable = true
                        ),
                        zoomOnDoubleTap = {
                            when (it) {
                                ZoomLevel.Min -> 1f
                                ZoomLevel.Mid -> 3f
                                ZoomLevel.Max -> 6f
                            }
                        }
                    )
                    .padding(vertical = 64.dp)
                    .systemBarsPadding()
                    .navigationBarsPadding(),
                contentScale = ContentScale.Fit,
                shape = RectangleShape
            )
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = { },
                actions = {
                    AnimatedVisibility(viewModel.selectedUri != null) {
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                    CircleShape
                                )
                                .clip(CircleShape)
                                .clickable {
                                    if (viewModel.selectedUri != null) wantToEdit = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CreateAlt,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                CircleShape
                            )
                            .clip(CircleShape)
                            .clickable {
                                showImagePreviewDialog = false
                                viewModel.selectUri(null)
                                viewModel.updateBitmap { null }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
            )
        }

        if (wantToEdit && viewModel.selectedUri != null) {
            AlertDialog(
                modifier = Modifier.alertDialog(),
                onDismissRequest = {},
                title = {
                    Text(stringResource(R.string.image))
                },
                icon = {
                    Icon(
                        Icons.Rounded.Image,
                        null
                    )
                },
                confirmButton = {
                    OutlinedButton(
                        onClick = {
                            wantToEdit = false
                        },
                        border = BorderStroke(
                            LocalBorderWidth.current,
                            MaterialTheme.colorScheme.outlineVariant()
                        )
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }
                },
                text = {
                    val navigate: (Screen) -> Unit = { screen ->
                        navController.navigate(screen)
                        pushNewUris(listOf(viewModel.selectedUri!!))
                        wantToEdit = false
                    }
                    val color = MaterialTheme.colorScheme.secondaryContainer
                    Box {
                        Divider(Modifier.align(Alignment.TopCenter))
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            item {
                                SingleResizePreference(
                                    onClick = { navigate(Screen.SingleResize) },
                                    color = color
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                BytesResizePreference(
                                    onClick = { navigate(Screen.ResizeByBytes) },
                                    color = color
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DeleteExifPreference(
                                    onClick = { navigate(Screen.DeleteExif) },
                                    color = color
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                CropPreference(
                                    onClick = { navigate(Screen.Crop) },
                                    color = color
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                PickColorPreference(
                                    onClick = { navigate(Screen.PickColorFromImage) },
                                    color = color
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                GeneratePalettePreference(
                                    onClick = { navigate(Screen.GeneratePalette) },
                                    color = color
                                )
                            }
                        }
                        Divider(Modifier.align(Alignment.BottomCenter))
                    }
                }
            )
        }

        BackHandler {
            showImagePreviewDialog = false
            viewModel.selectUri(null)
            viewModel.updateBitmap { null }
        }
    }
}