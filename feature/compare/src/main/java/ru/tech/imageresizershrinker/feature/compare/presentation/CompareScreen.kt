package ru.tech.imageresizershrinker.feature.compare.presentation

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.beforeafter.BeforeAfterImage
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.extractPrimaryColor
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ZoomableDefaults.defaultZoomOnDoubleTap
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.core.ui.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareType
import ru.tech.imageresizershrinker.feature.compare.presentation.viewModel.CompareViewModel


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

    var compareProgress by rememberSaveable { mutableFloatStateOf(50f) }

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
                compareProgress = 50f
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
                            compareProgress = 50f
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

    val content: @Composable (Pair<Bitmap?, Bitmap?>) -> Unit = { bitmapPair ->
        val modifier = Modifier
            .padding(16.dp)
            .container(RoundedCornerShape(16.dp))
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .transparencyChecker()

        AnimatedContent(targetState = viewModel.compareType) { compareType ->
            when (compareType) {
                CompareType.Slide -> {
                    AnimatedContent(targetState = bitmapPair) { data ->
                        data.let { (b, a) ->
                            val before = remember(data) { b?.asImageBitmap() }
                            val after = remember(data) { a?.asImageBitmap() }

                            if (before != null && after != null) {
                                BeforeAfterImage(
                                    enableZoom = false,
                                    modifier = modifier,
                                    progress = animateFloatAsState(targetValue = compareProgress).value,
                                    onProgressChange = {
                                        compareProgress = it
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

                CompareType.SideBySide -> {
                    val first = bitmapPair.first
                    val second = bitmapPair.second

                    val zoomState = rememberZoomState(30f)
                    val zoomModifier = Modifier.zoomable(
                        zoomState = zoomState
                    )

                    Column(
                        modifier = modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (first != null) {
                            Image(
                                bitmap = first.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .then(zoomModifier)
                            )
                            HorizontalDivider()
                        }
                        if (second != null) {
                            Image(
                                bitmap = second.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .then(zoomModifier)
                            )
                        }
                    }
                }

                CompareType.Tap -> {
                    var showSecondImage by rememberSaveable {
                        mutableStateOf(false)
                    }
                    Box(
                        modifier = modifier
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    showSecondImage = !showSecondImage
                                }
                            }
                    ) {
                        val first = bitmapPair.first
                        val second = bitmapPair.second
                        if (!showSecondImage && first != null) {
                            Image(
                                bitmap = first.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Inside
                            )
                        }
                        if (showSecondImage && second != null) {
                            Image(
                                bitmap = second.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Inside
                            )
                        }
                    }
                }

                CompareType.Transparency -> {
                    Box(
                        modifier = modifier
                    ) {
                        val first = bitmapPair.first
                        val second = bitmapPair.second
                        if (first != null) {
                            Image(
                                bitmap = first.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Inside
                            )
                        }
                        if (second != null) {
                            Image(
                                bitmap = second.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Inside,
                                modifier = Modifier.alpha(compareProgress / 100f)
                            )
                        }
                    }
                }
            }
        }
    }

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
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
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
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = {
                                onGoBack()
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        AnimatedVisibility(visible = viewModel.compareType == CompareType.Slide) {
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = { showShareSheet.value = true }
                            ) {
                                Icon(Icons.Outlined.Share, null)
                            }
                        }
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = {
                                viewModel.swap()
                            }
                        ) {
                            Icon(Icons.Rounded.SwapHoriz, null)
                        }
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
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
                    val zoomEnabled = viewModel.compareType != CompareType.SideBySide
                    val zoomState = rememberZoomState(30f, key = viewModel.compareType)

                    if (portrait) {
                        Column {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .zoomable(
                                        zoomState = zoomState,
                                        onDoubleTap = {
                                            if (zoomEnabled) {
                                                zoomState.defaultZoomOnDoubleTap(it)
                                            }
                                        },
                                        enableOneFingerZoom = zoomEnabled,
                                        enabled = { _, _ ->
                                            zoomEnabled
                                        }
                                    )
                            ) {
                                content(bitmapPair)
                            }
                            val selectionButtons: @Composable () -> Unit = {
                                CompareType.entries.forEach {
                                    EnhancedIconButton(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        onClick = { viewModel.setCompareType(it) }
                                    ) {
                                        Icon(
                                            imageVector = it.icon,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = viewModel.compareType != CompareType.Tap && viewModel.compareType != CompareType.SideBySide
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .container(
                                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                3.dp
                                            ),
                                            shape = RectangleShape
                                        )
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(
                                        space = 4.dp,
                                        alignment = Alignment.CenterHorizontally
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    selectionButtons()
                                }
                            }
                            BottomAppBar(
                                modifier = Modifier.drawHorizontalStroke(true),
                                floatingActionButton = {
                                    EnhancedFloatingActionButton(
                                        onClick = pickImage
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AddPhotoAlternate,
                                            contentDescription = null
                                        )
                                    }
                                },
                                actions = {
                                    AnimatedContent(
                                        targetState = viewModel.compareType == CompareType.Tap || viewModel.compareType == CompareType.SideBySide
                                    ) { showButtons ->
                                        if (showButtons) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(
                                                    space = 4.dp,
                                                    alignment = Alignment.CenterHorizontally
                                                )
                                            ) {
                                                selectionButtons()
                                            }
                                        } else {
                                            EnhancedSlider(
                                                modifier = Modifier
                                                    .padding(horizontal = 16.dp)
                                                    .weight(100f, true)
                                                    .offset(y = (-2).dp),
                                                value = compareProgress,
                                                onValueChange = {
                                                    compareProgress = it
                                                },
                                                valueRange = 0f..100f
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    } else {
                        Row {
                            Box(
                                Modifier
                                    .weight(0.8f)
                                    .zoomable(
                                        zoomState = zoomState,
                                        onDoubleTap = {
                                            if (zoomEnabled) {
                                                zoomState.defaultZoomOnDoubleTap(it)
                                            }
                                        },
                                        enableOneFingerZoom = zoomEnabled,
                                        enabled = { _, _ ->
                                            zoomEnabled
                                        }
                                    )
                                    .padding(20.dp)
                            ) {
                                Box(
                                    Modifier
                                        .align(Alignment.Center)
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    content(bitmapPair)
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
                                AnimatedVisibility(visible = viewModel.compareType != CompareType.Tap) {
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
                                        value = compareProgress,
                                        onValueChange = {
                                            compareProgress = it
                                        },
                                        valueRange = 0f..100f
                                    )
                                }

                                EnhancedFloatingActionButton(
                                    onClick = pickImage
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.AddPhotoAlternate,
                                        contentDescription = null
                                    )
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
            EnhancedFloatingActionButton(
                onClick = pickImage,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .align(settingsState.fabAlignment),
                content = {
                    Spacer(Modifier.width(16.dp))
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                    Spacer(Modifier.width(16.dp))
                    Text(stringResource(R.string.pick_image_alt))
                    Spacer(Modifier.width(16.dp))
                }
            )
        }
    }

    SimpleSheet(
        sheetContent = {
            var imageFormat by remember { mutableStateOf<ImageFormat>(ImageFormat.Png) }
            val saveBitmap: () -> Unit = {
                viewModel.saveBitmap(compareProgress, imageFormat) { saveResult ->
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
                                    viewModel.getOverlayedImage(compareProgress)
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
                        value = imageFormat,
                        onValueChange = { imageFormat = it }
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
                            viewModel.shareBitmap(compareProgress, imageFormat) {
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