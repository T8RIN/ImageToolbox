package ru.tech.imageresizershrinker.compare_screen

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.RotateLeft
import androidx.compose.material.icons.rounded.RotateRight
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smarttoolfactory.beforeafter.BeforeAfterImage
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.extractPrimaryColor
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.compare_screen.viewModel.CompareViewModel
import ru.tech.imageresizershrinker.theme.blend
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.getBitmapByUri
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.utils.modifier.navBarsPaddingOnlyIfTheyAtTheBottom
import ru.tech.imageresizershrinker.utils.storage.Picker
import ru.tech.imageresizershrinker.utils.storage.localImagePickerMode
import ru.tech.imageresizershrinker.utils.storage.rememberImagePicker
import ru.tech.imageresizershrinker.widget.LoadingDialog
import ru.tech.imageresizershrinker.widget.LocalToastHost
import ru.tech.imageresizershrinker.widget.TopAppBarEmoji
import ru.tech.imageresizershrinker.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.widget.text.Marquee
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.widget.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    comparableUris: Pair<Uri, Uri>?,
    onGoBack: () -> Unit,
    viewModel: CompareViewModel = viewModel()
) {
    val settingsState = LocalSettingsState.current

    val context = LocalContext.current
    val toastHostState = LocalToastHost.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()

    var progress by rememberSaveable { mutableStateOf(50f) }

    LaunchedEffect(viewModel.bitmapData) {
        viewModel.bitmapData?.let { (b, a) ->
            if (allowChangeColor && a != null && b != null) {
                themeState.updateColor(a.extractPrimaryColor().blend(b.extractPrimaryColor(), 0.5f))
            }
        }
    }

    LaunchedEffect(comparableUris) {
        comparableUris?.let { (before, after) ->
            val newBeforeBitmap = context.getBitmapByUri(before, originalSize = false)
            val newAfterBitmap = context.getBitmapByUri(after, originalSize = false)
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
                            context.getBitmapByUri(
                                uris[0],
                                originalSize = false,
                            ) to context.getBitmapByUri(uris[1], originalSize = false)
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
                            Icon(Icons.Rounded.ArrowBack, null)
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
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
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
                                    if (rotation == 90f) Icons.Rounded.RotateLeft
                                    else Icons.Rounded.RotateRight, null
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
                        AnimatedContent(targetState = bitmapPair) { data ->
                            data.let { (b, a) ->
                                val before = remember(data) { b?.asImageBitmap() }
                                val after = remember(data) { a?.asImageBitmap() }
                                if (before != null && after != null) {
                                    BeforeAfterImage(
                                        modifier = Modifier
                                            .padding(bottom = 88.dp)
                                            .padding(16.dp)
                                            .navigationBarsPadding()
                                            .block(RoundedCornerShape(4.dp))
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(4.dp)),
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
                    } else {
                        Row {
                            Box(
                                Modifier
                                    .weight(0.8f)
                                    .padding(20.dp)
                            ) {
                                Box(Modifier.align(Alignment.Center)) {
                                    AnimatedContent(targetState = bitmapPair) { data ->
                                        data.let { (b, a) ->
                                            val before = remember(data) { b?.asImageBitmap() }
                                            val after = remember(data) { a?.asImageBitmap() }

                                            if (before != null && after != null) {
                                                BeforeAfterImage(
                                                    modifier = Modifier
                                                        .navBarsPaddingOnlyIfTheyAtTheBottom()
                                                        .block(RoundedCornerShape(4.dp))
                                                        .padding(4.dp)
                                                        .clip(RoundedCornerShape(4.dp)),
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
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                            Column(
                                Modifier
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
                                    .height(50.dp)
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        CircleShape
                                    )
                                    .border(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                                        CircleShape
                                    )
                                    .padding(horizontal = 16.dp)

                                Slider(
                                    modifier = modifier,
                                    value = animateFloatAsState(targetValue = progress).value,
                                    onValueChange = {
                                        progress = it
                                    },
                                    colors = SliderDefaults.colors(
                                        inactiveTrackColor =
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                    ),
                                    valueRange = 0f..100f
                                )

                                FloatingActionButton(
                                    onClick = pickImage,
                                    modifier = Modifier.fabBorder(),
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
                    .fabBorder(),
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                icon = {
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                },
                text = {
                    Text(stringResource(R.string.pick_images))
                }
            )
        } else if (portrait) {
            BottomAppBar(
                modifier = Modifier
                    .drawHorizontalStroke(true)
                    .align(Alignment.BottomCenter),
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = pickImage,
                        modifier = Modifier.fabBorder(),
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Rounded.AddPhotoAlternate, null)
                    }
                },
                actions = {
                    Slider(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(100f, true)
                            .offset(y = (-2).dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            )
                            .border(
                                width = settingsState.borderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                                shape = CircleShape
                            )
                            .padding(horizontal = 16.dp),
                        colors = SliderDefaults.colors(
                            inactiveTrackColor =
                            MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                        ),
                        value = animateFloatAsState(targetValue = progress).value,
                        onValueChange = {
                            progress = it
                        },
                        valueRange = 0f..100f
                    )
                }
            )
        }
    }

    if (viewModel.isLoading) LoadingDialog()

    BackHandler {
        onGoBack()
    }
}