package ru.tech.imageresizershrinker.presentation.compare_screen

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.RotateLeft
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.beforeafter.BeforeAfterImage
import com.smarttoolfactory.image.zoom.animatedZoom
import com.smarttoolfactory.image.zoom.rememberAnimatedZoomState
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.extractPrimaryColor
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.presentation.compare_screen.viewModel.CompareViewModel
import ru.tech.imageresizershrinker.presentation.root.theme.blend
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.container
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.containerFabBorder
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.navBarsPaddingOnlyIfTheyAtTheBottom
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.transparencyChecker
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.root.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.root.widget.image.Picture
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass


private val topShape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp,
    bottomStart = 6.dp,
    bottomEnd = 6.dp
)

private val bottomShape = RoundedCornerShape(
    topStart = 6.dp,
    topEnd = 6.dp,
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    comparableUris: Pair<Uri, Uri>?,
    onGoBack: () -> Unit,
    viewModel: CompareViewModel = hiltViewModel()
) {
    val settingsState = LocalSettingsState.current

    val context = LocalContext.current
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

    var progress by rememberSaveable { mutableFloatStateOf(50f) }

    LaunchedEffect(viewModel.bitmapData) {
        viewModel.bitmapData?.let { (b, a) ->
            if (allowChangeColor && a != null && b != null) {
                themeState.updateColor(a.extractPrimaryColor().blend(b.extractPrimaryColor(), 0.5f))
            }
        }
    }

    LaunchedEffect(comparableUris) {
        comparableUris?.let { (before, after) ->
            val newBeforeBitmap = viewModel.getBitmapByUri(before, originalSize = false)
            val newAfterBitmap = viewModel.getBitmapByUri(after, originalSize = false)
            if (newAfterBitmap != null && newBeforeBitmap != null) {
                viewModel.updateBitmapData(
                    newBeforeBitmap = newBeforeBitmap,
                    newAfterBitmap = newAfterBitmap
                )
                progress = 50f
            } else {
                scope.launch {
                    toastHostState.showToast(
                        context.getString(R.string.something_went_wrong),
                        Icons.Rounded.ErrorOutline
                    )
                }
            }
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.let {
                if (uris.size != 2) {
                    scope.launch {
                        toastHostState.showToast(
                            message = context.getString(R.string.pick_two_images),
                            icon = Icons.Rounded.ErrorOutline
                        )
                    }
                } else {
                    viewModel.updateBitmapDataAsync(
                        onSuccess = {
                            progress = 50f
                        }, loader = {
                            viewModel.getBitmapByUri(
                                uris[0],
                                originalSize = false,
                            ) to viewModel.getBitmapByUri(uris[1], originalSize = false)
                        }, onError = {
                            scope.launch {
                                toastHostState.showToast(
                                    context.getString(R.string.something_went_wrong),
                                    Icons.Rounded.ErrorOutline
                                )
                            }
                        }
                    )
                }
            }
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

    val portrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val showShareSheet = rememberSaveable { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (viewModel.bitmapData == null) {
                LargeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                onGoBack()
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    title = {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
                            Text(stringResource(R.string.compare))
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    ),
                    actions = {
                        TopAppBarEmoji()
                    },
                    modifier = Modifier.drawHorizontalStroke()
                )
            } else {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                onGoBack()
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showShareSheet.value = true }
                        ) {
                            Icon(Icons.Outlined.Share, null)
                        }
                        IconButton(
                            onClick = {
                                viewModel.swap()
                            }
                        ) {
                            Icon(Icons.Rounded.SwapHoriz, null)
                        }
                        IconButton(
                            onClick = {
                                viewModel.rotate()
                            }
                        ) {
                            AnimatedContent(
                                viewModel.rotation
                            ) { rotation ->
                                Icon(
                                    imageVector = if (rotation == 90f) Icons.AutoMirrored.Rounded.RotateLeft
                                    else Icons.AutoMirrored.Rounded.RotateRight,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    title = {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
                            Text(stringResource(R.string.compare))
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    ),
                    modifier = Modifier.drawHorizontalStroke()
                )
            }

            AnimatedContent(viewModel.bitmapData == null) { nil ->
                viewModel.bitmapData.takeIf { !nil }?.let { bitmapPair ->
                    if (portrait) {
                        Column {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .animatedZoom(
                                        animatedZoomState = rememberAnimatedZoomState(
                                            maxZoom = 30f
                                        )
                                    )
                            ) {
                                AnimatedContent(targetState = bitmapPair) { data ->
                                    data.let { (b, a) ->
                                        val before = remember(data) { b?.asImageBitmap() }
                                        val after = remember(data) { a?.asImageBitmap() }
                                        if (before != null && after != null) {
                                            BeforeAfterImage(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .container(RoundedCornerShape(16.dp))
                                                    .padding(4.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .transparencyChecker(),
                                                progress = animateFloatAsState(targetValue = progress).value,
                                                onProgressChange = {
                                                    progress = it
                                                },
                                                enableZoom = false,
                                                beforeImage = before,
                                                afterImage = after,
                                                beforeLabel = { },
                                                afterLabel = { }
                                            )
                                        }
                                    }
                                }
                            }
                            BottomAppBar(
                                modifier = Modifier.drawHorizontalStroke(true),
                                floatingActionButton = {
                                    FloatingActionButton(
                                        onClick = pickImage,
                                        modifier = Modifier.containerFabBorder(),
                                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                                    ) {
                                        Icon(Icons.Rounded.AddPhotoAlternate, null)
                                    }
                                },
                                actions = {
                                    EnhancedSlider(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .weight(100f, true)
                                            .offset(y = (-2).dp),
                                        value = progress,
                                        onValueChange = {
                                            progress = it
                                        },
                                        valueRange = 0f..100f
                                    )
                                }
                            )
                        }
                    } else {
                        Row {
                            Box(
                                Modifier
                                    .weight(0.8f)
                                    .animatedZoom(
                                        animatedZoomState = rememberAnimatedZoomState(
                                            maxZoom = 30f
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Box(
                                    Modifier
                                        .align(Alignment.Center)
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AnimatedContent(targetState = bitmapPair) { data ->
                                        data.let { (b, a) ->
                                            val before = remember(data) { b?.asImageBitmap() }
                                            val after = remember(data) { a?.asImageBitmap() }

                                            if (before != null && after != null) {
                                                BeforeAfterImage(
                                                    enableZoom = false,
                                                    modifier = Modifier
                                                        .navBarsPaddingOnlyIfTheyAtTheBottom()
                                                        .container(RoundedCornerShape(16.dp))
                                                        .padding(4.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .transparencyChecker(),
                                                    progress = animateFloatAsState(targetValue = progress).value,
                                                    onProgressChange = {
                                                        progress = it
                                                    },
                                                    beforeImage = before,
                                                    afterImage = after,
                                                    beforeLabel = { },
                                                    afterLabel = { }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Column(
                                Modifier
                                    .container(
                                        shape = RectangleShape,
                                        color = MaterialTheme.colorScheme.surfaceContainer
                                    )
                                    .padding(horizontal = 20.dp)
                                    .fillMaxHeight()
                                    .navigationBarsPadding(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val modifier = Modifier
                                    .padding(16.dp)
                                    .graphicsLayer {
                                        rotationZ = 270f
                                        transformOrigin = TransformOrigin(0f, 0f)
                                    }
                                    .layout { measurable, constraints ->
                                        val placeable = measurable.measure(
                                            Constraints(
                                                minWidth = constraints.minHeight,
                                                maxWidth = constraints.maxHeight,
                                                minHeight = constraints.minWidth,
                                                maxHeight = constraints.maxHeight,
                                            )
                                        )
                                        layout(placeable.height, placeable.width) {
                                            placeable.place(-placeable.width, 0)
                                        }
                                    }
                                    .width((LocalConfiguration.current.screenHeightDp / 2f).dp)

                                EnhancedSlider(
                                    modifier = modifier,
                                    value = animateFloatAsState(targetValue = progress).value,
                                    onValueChange = {
                                        progress = it
                                    },
                                    valueRange = 0f..100f
                                )

                                FloatingActionButton(
                                    onClick = pickImage,
                                    modifier = Modifier.containerFabBorder(),
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                                ) {
                                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                                }
                            }
                        }
                    }
                } ?: Column(Modifier.verticalScroll(scrollState)) {
                    ImageNotPickedWidget(
                        onPickImage = pickImage,
                        modifier = Modifier
                            .padding(bottom = 88.dp, top = 20.dp, start = 20.dp, end = 20.dp)
                            .navigationBarsPadding(),
                        text = stringResource(R.string.pick_two_images)
                    )
                }
            }
        }

        if (viewModel.bitmapData == null) {
            ExtendedFloatingActionButton(
                onClick = pickImage,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .align(settingsState.fabAlignment)
                    .autoElevatedBorder(),
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                icon = {
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                },
                text = {
                    Text(stringResource(R.string.pick_images))
                }
            )
        }
    }

    SimpleSheet(
        sheetContent = {
            var imageFormat by remember { mutableStateOf<ImageFormat>(ImageFormat.Png) }
            val saveBitmap: () -> Unit = {
                viewModel.saveBitmap(progress, imageFormat) { saveResult ->
                    parseSaveResult(
                        saveResult = saveResult,
                        onSuccess = showConfetti,
                        toastHostState = toastHostState,
                        scope = scope,
                        context = context
                    )
                }
            }
            Box {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .padding(
                                bottom = 8.dp,
                                start = 4.dp,
                                end = 4.dp,
                                top = 16.dp
                            )
                            .height(100.dp)
                            .width(120.dp)
                            .container(
                                shape = MaterialTheme.shapes.extraLarge,
                                resultPadding = 0.dp
                            )
                    ) {
                        Picture(
                            model = remember(viewModel.bitmapData) {
                                derivedStateOf {
                                    viewModel.getOverlayedImage(progress)
                                }
                            }.value,
                            shape = RectangleShape,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    ExtensionGroup(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        enabled = true,
                        imageFormat = imageFormat,
                        onFormatChange = { imageFormat = it }
                    )
                    Spacer(Modifier.height(8.dp))
                    PreferenceItem(
                        title = stringResource(id = R.string.save),
                        onClick = {
                            saveBitmap()
                            showShareSheet.value = false
                        },
                        shape = topShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        endIcon = Icons.Rounded.Save
                    )
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        title = stringResource(id = R.string.share),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = bottomShape,
                        onClick = {
                            viewModel.shareBitmap(progress, imageFormat) {
                                showConfetti()
                            }
                            showShareSheet.value = false
                        },
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        endIcon = Icons.Rounded.Share
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    showShareSheet.value = false
                }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.share),
                icon = Icons.Rounded.IosShare
            )
        },
        visible = showShareSheet
    )

    if (viewModel.isImageLoading) LoadingDialog { viewModel.cancelSaving() }

    BackHandler {
        onGoBack()
    }
}