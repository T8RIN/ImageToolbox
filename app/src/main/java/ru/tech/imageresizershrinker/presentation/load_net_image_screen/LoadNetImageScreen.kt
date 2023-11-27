package ru.tech.imageresizershrinker.presentation.load_net_image_screen

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.rememberAppColorTuple
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.presentation.load_net_image_screen.viewModel.LoadNetImageViewModel
import ru.tech.imageresizershrinker.presentation.root.icons.material.CreateAlt
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.presentation.root.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.presentation.root.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.presentation.root.widget.image.Picture
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadNetImageScreen(
    url: String,
    onGoBack: () -> Unit,
    viewModel: LoadNetImageViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val context = LocalContext.current as ComponentActivity
    val themeState = LocalDynamicThemeState.current
    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage
    val toastHostState = LocalToastHost.current

    val confettiController = LocalConfettiController.current

    val scope = rememberCoroutineScope()

    val appColorTuple = rememberAppColorTuple(
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = settingsState.isDynamicColors,
        darkTheme = settingsState.isNightMode
    )

    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let { image ->
            if (allowChangeColor) {
                themeState.updateColorByImage(image)
            }
        } ?: themeState.updateColorTuple(appColorTuple)
    }

    var scaleType by rememberSaveable(
        saver = Saver(
            save = {
                if (it == ContentScale.FillWidth) 0 else 1
            },
            restore = {
                mutableStateOf(
                    if (it == 0) {
                        ContentScale.FillWidth
                    } else {
                        ContentScale.Fit
                    }
                )
            }
        )
    ) { mutableStateOf(ContentScale.FillWidth) }

    val landscape =
        LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact || LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var link by rememberSaveable(url) { mutableStateOf(url) }
    var state: AsyncImagePainter.State by remember {
        mutableStateOf(
            AsyncImagePainter.State.Empty
        )
    }
    val imageBlock = @Composable {
        AnimatedContent(scaleType) { scale ->
            Picture(
                allowHardware = false,
                model = link,
                modifier = Modifier
                    .then(if (scale == ContentScale.FillWidth) Modifier.fillMaxWidth() else Modifier)
                    .padding(bottom = 16.dp)
                    .then(
                        if (viewModel.bitmap == null) Modifier.height(140.dp)
                        else Modifier
                    )
                    .container()
                    .padding(4.dp),
                contentScale = scale,
                shape = MaterialTheme.shapes.small,
                error = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.BrokenImage,
                            null,
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .size(64.dp)
                        )
                        Text(stringResource(id = R.string.no_image))
                        Spacer(Modifier.height(8.dp))
                    }
                },
                onState = {
                    if (it is AsyncImagePainter.State.Error) {
                        viewModel.updateBitmap(it.result.drawable?.toBitmap())
                    }
                    if (it is AsyncImagePainter.State.Success) {
                        viewModel.updateBitmap(it.result.drawable.toBitmap())
                    }
                    state = it
                },
            )
        }
    }

    val showSheet = rememberSaveable { mutableStateOf(false) }
    val zoomButton = @Composable {
        AnimatedVisibility(
            visible = viewModel.bitmap != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
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
        bitmap = viewModel.bitmap,
        visible = showSheet
    )

    val wantToEdit = rememberSaveable { mutableStateOf(false) }

    val focus = LocalFocusManager.current

    val saveBitmap: () -> Unit = {
        viewModel.saveBitmap(link) { saveResult ->
            parseSaveResult(
                saveResult = saveResult,
                onSuccess = {
                    confettiController.showEmpty()
                },
                toastHostState = toastHostState,
                scope = scope,
                context = context
            )
        }
    }

    val buttons = @Composable {
        val fab = @Composable {
            EnhancedFloatingActionButton(
                onClick = {
                    viewModel.bitmap?.let { bitmap ->
                        viewModel.cacheImage(
                            image = bitmap,
                            imageInfo = ImageInfo(
                                width = bitmap.width,
                                height = bitmap.height,
                            )
                        )
                        wantToEdit.value = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Icon(imageVector = Icons.Rounded.CreateAlt, contentDescription = null)
            }
            EnhancedFloatingActionButton(
                onClick = saveBitmap
            ) {
                Icon(imageVector = Icons.Rounded.Save, contentDescription = null)
            }
        }
        if (landscape) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                fab()
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                fab()
            }
        }
    }

    Surface(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    focus.clearFocus()
                }
            )
        },
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
                                stringResource(R.string.load_image_from_net),
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
                        IconButton(onClick = onGoBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        if (viewModel.bitmap == null) {
                            TopAppBarEmoji()
                        } else {
                            IconButton(
                                onClick = {
                                    viewModel.bitmap?.let { bitmap ->
                                        viewModel.shareBitmap(
                                            bitmap = bitmap,
                                            imageInfo = ImageInfo(
                                                width = bitmap.width,
                                                height = bitmap.height,
                                            ),
                                            onComplete = {
                                                scope.launch {
                                                    confettiController.showEmpty()
                                                }
                                            }
                                        )
                                    }
                                }
                            ) {
                                Icon(Icons.Outlined.Share, null)
                            }
                            zoomButton()
                        }
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (landscape) {
                        Box(
                            Modifier
                                .weight(1.2f)
                                .padding(20.dp)
                        ) {
                            Box(Modifier.align(Alignment.Center)) {
                                imageBlock()
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                        )
                    }
                    LazyColumn(
                        state = rememberLazyListState(),
                        contentPadding = PaddingValues(
                            bottom = WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + WindowInsets.ime
                                .asPaddingValues()
                                .calculateBottomPadding() + (if (landscape && viewModel.bitmap != null) 20.dp else 100.dp),
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
                                if (!landscape) imageBlock()
                                ToggleGroupButton(
                                    modifier = Modifier
                                        .container(shape = RoundedCornerShape(24.dp)),
                                    title = stringResource(id = R.string.content_scale),
                                    enabled = viewModel.bitmap != null,
                                    items = listOf(
                                        stringResource(R.string.fill),
                                        stringResource(R.string.fit)
                                    ),
                                    selectedIndex = (scaleType == ContentScale.Fit).toInt(),
                                    indexChanged = {
                                        scaleType = if (it == 0) {
                                            ContentScale.FillWidth
                                        } else {
                                            ContentScale.Fit
                                        }
                                    }
                                )
                                Spacer(Modifier.height(8.dp))
                                RoundedTextField(
                                    modifier = Modifier
                                        .container(shape = RoundedCornerShape(24.dp))
                                        .padding(8.dp),
                                    value = link,
                                    onValueChange = {
                                        link = it
                                    },
                                    singleLine = false,
                                    label = {
                                        Text(stringResource(id = R.string.image_link))
                                    },
                                    endIcon = {
                                        AnimatedVisibility(link.isNotBlank()) {
                                            IconButton(
                                                onClick = { link = "" },
                                                modifier = Modifier.padding(end = 4.dp)
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Cancel,
                                                    null,
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    if (landscape && viewModel.bitmap != null) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                                .padding(start = 20.dp)
                        )
                        buttons()
                    }
                }
            }

            if (!landscape && viewModel.bitmap != null) {
                Box(
                    Modifier
                        .align(settingsState.fabAlignment)
                        .navigationBarsPadding()
                ) {
                    buttons()
                }
            }


            if (viewModel.isSaving) LoadingDialog {
                viewModel.cancelSaving()
            }

            BackHandler { onGoBack() }
        }
    }

    ProcessImagesPreferenceSheet(
        uris = listOfNotNull(viewModel.tempUri),
        visible = wantToEdit,
        navigate = { screen ->
            scope.launch {
                wantToEdit.value = false
                delay(200)
                navController.navigate(screen)
            }
        }
    )
}

private fun Boolean.toInt() = if (this) 1 else 0