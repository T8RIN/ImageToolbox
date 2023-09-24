package ru.tech.imageresizershrinker.presentation.pick_color_from_image_screen

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Colorize
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.size.Size
import com.smarttoolfactory.colordetector.ImageColorDetector
import com.smarttoolfactory.colordetector.parser.rememberColorParser
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.transparencyChecker
import ru.tech.imageresizershrinker.presentation.pick_color_from_image_screen.viewModel.PickColorViewModel
import ru.tech.imageresizershrinker.presentation.root.icons.material.PaletteSwatch
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.transformation.filter.SaturationFilter
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.copyColorIntoClipboard
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.toHex
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.container
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.containerFabBorder
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.navBarsPaddingOnlyIfTheyAtTheBottom
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import ru.tech.imageresizershrinker.presentation.root.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickColorFromImageScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: PickColorViewModel = hiltViewModel()
) {
    val settingsState = LocalSettingsState.current
    val navController = LocalNavController.current
    val context = LocalContext.current
    val toastHostState = LocalToastHost.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val parser = rememberColorParser()

    val scope = rememberCoroutineScope()

    var canZoom by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uriState) {
        uriState?.let {
            viewModel.setUri(it)
            viewModel.decodeBitmapByUri(
                uri = it,
                originalSize = false,
                onGetMimeType = {},
                onGetExif = {},
                onGetBitmap = viewModel::updateBitmap,
                onError = {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            )
        }
    }

    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor) {
                themeState.updateColorByImage(
                    SaturationFilter(context, 2f).transform(it, Size.ORIGINAL)
                )
            }
        }
    }

    LaunchedEffect(viewModel.color) {
        if (!viewModel.color.isUnspecified) {
            if (allowChangeColor) themeState.updateColor(viewModel.color)
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
                viewModel.setUri(it)
                viewModel.decodeBitmapByUri(
                    uri = it,
                    originalSize = false,
                    onGetMimeType = {},
                    onGetExif = {},
                    onGetBitmap = viewModel::updateBitmap,
                    onError = {
                        scope.launch {
                            toastHostState.showError(context, it)
                        }
                    }
                )
            }
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

    val portrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val switch = @Composable {
        Switch(
            modifier = Modifier.padding(horizontal = 16.dp),
            colors = SwitchDefaults.colors(
                uncheckedBorderColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            checked = !canZoom,
            onCheckedChange = { canZoom = !canZoom },
            thumbContent = {
                AnimatedContent(canZoom) { zoom ->
                    Icon(
                        if (!zoom) Icons.Rounded.Colorize else Icons.Rounded.ZoomIn,
                        null,
                        Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            }
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        val color = viewModel.color
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedContent(
                modifier = Modifier.drawHorizontalStroke(),
                targetState = viewModel.bitmap == null,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { noBmp ->
                if (noBmp) {
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
                                Text(stringResource(R.string.pick_color))
                            }
                        },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ),
                        actions = {
                            if (viewModel.bitmap == null) {
                                TopAppBarEmoji()
                            }
                        }
                    )
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        modifier = Modifier.animateContentSize(),
                    ) {
                        Column {
                            Column(Modifier.navBarsPaddingOnlyIfTheyAtTheEnd()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            onGoBack()
                                        },
                                        modifier = Modifier.statusBarsPadding()
                                    ) {
                                        Icon(Icons.Rounded.ArrowBack, null)
                                    }
                                    if (!portrait) {
                                        ProvideTextStyle(
                                            value = LocalTextStyle.current.merge(
                                                MaterialTheme.typography.headlineSmall
                                            )
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .padding(
                                                        start = 16.dp,
                                                        end = 16.dp
                                                    )
                                                    .statusBarsPadding()
                                            ) {
                                                Text(stringResource(R.string.color))

                                                Text(
                                                    modifier = Modifier
                                                        .padding(horizontal = 8.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .clickable {
                                                            context.copyColorIntoClipboard(
                                                                context.getString(R.string.color),
                                                                color.toHex()
                                                            )
                                                            scope.launch {
                                                                toastHostState.showToast(
                                                                    icon = Icons.Rounded.ContentPaste,
                                                                    message = context.getString(R.string.color_copied)
                                                                )
                                                            }
                                                        }
                                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                                        .border(
                                                            settingsState.borderWidth,
                                                            MaterialTheme.colorScheme.outlineVariant(
                                                                onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                                            ),
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .padding(horizontal = 6.dp),
                                                    text = color.toHex(),
                                                    style = LocalTextStyle.current.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                )

                                                Text(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(2.dp),
                                                    text = remember(color) {
                                                        derivedStateOf {
                                                            parser.parseColorName(color)
                                                        }
                                                    }.value
                                                )

                                                Box(
                                                    Modifier
                                                        .padding(
                                                            vertical = 4.dp,
                                                            horizontal = 16.dp
                                                        )
                                                        .background(
                                                            color = animateColorAsState(color).value,
                                                            shape = RoundedCornerShape(12.dp)
                                                        )
                                                        .height(40.dp)
                                                        .width(72.dp)
                                                        .border(
                                                            width = settingsState.borderWidth,
                                                            color = MaterialTheme.colorScheme.outlineVariant(
                                                                onTopOf = animateColorAsState(color).value
                                                            ),
                                                            shape = RoundedCornerShape(11.dp)
                                                        )
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .clickable {
                                                            context.copyColorIntoClipboard(
                                                                context.getString(R.string.color),
                                                                color.toHex()
                                                            )
                                                            scope.launch {
                                                                toastHostState.showToast(
                                                                    icon = Icons.Rounded.ContentPaste,
                                                                    message = context.getString(R.string.color_copied)
                                                                )
                                                            }
                                                        }
                                                )

                                                if (viewModel.uri != null) {
                                                    IconButton(
                                                        onClick = {
                                                            if (navController.backstack.entries.isNotEmpty()) navController.pop()
                                                            navController.navigate(
                                                                Screen.GeneratePalette(
                                                                    viewModel.uri
                                                                )
                                                            )
                                                        },
                                                        modifier = Modifier.statusBarsPadding()
                                                    ) {
                                                        Icon(Icons.Rounded.PaletteSwatch, null)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer(
                                        Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp)
                                    )
                                    if (viewModel.uri != null && portrait) {
                                        IconButton(
                                            onClick = {
                                                if (navController.backstack.entries.isNotEmpty()) navController.pop()
                                                navController.navigate(
                                                    Screen.GeneratePalette(
                                                        viewModel.uri
                                                    )
                                                )
                                            },
                                            modifier = Modifier.statusBarsPadding()
                                        ) {
                                            Icon(Icons.Rounded.PaletteSwatch, null)
                                        }
                                    }
                                }
                                if (portrait) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    ProvideTextStyle(
                                        value = LocalTextStyle.current.merge(
                                            MaterialTheme.typography.headlineSmall
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                        ) {
                                            Text(stringResource(R.string.color))

                                            Text(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        context.copyColorIntoClipboard(
                                                            context.getString(R.string.color),
                                                            color.toHex()
                                                        )
                                                        scope.launch {
                                                            toastHostState.showToast(
                                                                icon = Icons.Rounded.ContentPaste,
                                                                message = context.getString(R.string.color_copied)
                                                            )
                                                        }
                                                    }
                                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                                    .border(
                                                        settingsState.borderWidth,
                                                        MaterialTheme.colorScheme.outlineVariant(
                                                            onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                                        ),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(horizontal = 6.dp),
                                                text = color.toHex(),
                                                style = LocalTextStyle.current.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            )

                                            Spacer(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(2.dp)
                                            )

                                            Box(
                                                Modifier
                                                    .padding(vertical = 4.dp)
                                                    .background(
                                                        color = animateColorAsState(color).value,
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                                    .height(40.dp)
                                                    .width(72.dp)
                                                    .border(
                                                        width = settingsState.borderWidth,
                                                        color = MaterialTheme.colorScheme.outlineVariant(
                                                            onTopOf = animateColorAsState(color).value
                                                        ),
                                                        shape = RoundedCornerShape(11.dp)
                                                    )
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable {
                                                        context.copyColorIntoClipboard(
                                                            context.getString(R.string.color),
                                                            color.toHex()
                                                        )
                                                        scope.launch {
                                                            toastHostState.showToast(
                                                                icon = Icons.Rounded.ContentPaste,
                                                                message = context.getString(R.string.color_copied)
                                                            )
                                                        }
                                                    }
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier.weight(1f)
            ) {
                viewModel.bitmap?.let {
                    if (portrait) {
                        AnimatedContent(
                            targetState = it
                        ) { bitmap ->
                            ImageColorDetector(
                                canZoom = canZoom,
                                imageBitmap = bitmap.asImageBitmap(),
                                color = viewModel.color,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .navBarsPaddingOnlyIfTheyAtTheEnd()
                                    .container(resultPadding = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .transparencyChecker(),
                                onColorChange = viewModel::updateColor
                            )
                        }
                    } else {
                        Row(Modifier.navBarsPaddingOnlyIfTheyAtTheEnd()) {
                            Box(
                                Modifier
                                    .weight(0.8f)
                                    .padding(20.dp)
                            ) {
                                Box(Modifier.align(Alignment.Center)) {
                                    AnimatedContent(
                                        targetState = it
                                    ) { bitmap ->
                                        ImageColorDetector(
                                            canZoom = canZoom,
                                            imageBitmap = bitmap.asImageBitmap(),
                                            color = viewModel.color,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .navBarsPaddingOnlyIfTheyAtTheBottom()
                                                .container(resultPadding = 8.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .transparencyChecker(),
                                            onColorChange = viewModel::updateColor
                                        )
                                    }
                                }
                            }
                            Column(
                                Modifier
                                    .container(
                                        shape = RectangleShape,
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        resultPadding = 0.dp
                                    )
                                    .fillMaxHeight()
                                    .padding(horizontal = 20.dp)
                                    .navigationBarsPadding(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                switch()
                                Spacer(modifier = Modifier.height(16.dp))
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
                            .navigationBarsPadding()
                    )
                }
            }
            if (viewModel.bitmap != null && portrait) {
                BottomAppBar(
                    modifier = Modifier
                        .drawHorizontalStroke(true),
                    actions = {
                        switch()
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .offset(x = (-10).dp),
                            text = remember(color) {
                                derivedStateOf {
                                    parser.parseColorName(color)
                                }
                            }.value,
                            textAlign = TextAlign.Center
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = pickImage,
                            modifier = Modifier.fabBorder(),
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(Icons.Rounded.AddPhotoAlternate, null)
                        }
                    }
                )
            }
        }

        if (viewModel.bitmap == null) {
            ExtendedFloatingActionButton(
                onClick = pickImage,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .align(settingsState.fabAlignment)
                    .fabBorder(),
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

    if (viewModel.isImageLoading) LoadingDialog(false) {}

    BackHandler {
        onGoBack()
    }
}