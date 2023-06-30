package ru.tech.imageresizershrinker.presentation.image_preview_screen

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.size.Size
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.getAppColorTuple
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.image_preview_screen.components.ImagePager
import ru.tech.imageresizershrinker.presentation.image_preview_screen.viewModel.ImagePreviewViewModel
import ru.tech.imageresizershrinker.presentation.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.utils.coil.filters.SaturationFilter
import ru.tech.imageresizershrinker.core.android.BitmapUtils.decodeSampledBitmapFromUri
import ru.tech.imageresizershrinker.presentation.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.presentation.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.widget.image.Picture
import ru.tech.imageresizershrinker.presentation.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.widget.utils.isScrollingUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: ImagePreviewViewModel = viewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val themeState = LocalDynamicThemeState.current
    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val appColorTuple = getAppColorTuple(
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = settingsState.isDynamicColors,
        darkTheme = settingsState.isNightMode
    )

    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() && it != listOf(viewModel.selectedUri) }?.let { uris ->
            viewModel.updateUris(uris)
        }
    }

    LaunchedEffect(viewModel.selectedUri) {
        viewModel.selectedUri?.let {
            if (allowChangeColor) {
                val image = context.decodeSampledBitmapFromUri(
                    uri = it,
                    reqWidth = 1200,
                    reqHeight = 1200
                )
                image?.let { it1 ->
                    themeState.updateColorByImage(
                        SaturationFilter(context, 2f).transform(it1, Size.ORIGINAL)
                    )
                }
            }
        } ?: themeState.updateColorTuple(appColorTuple)
    }

    var addImages by remember { mutableStateOf(false) }
    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple),
            onFailure = { addImages = false }
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let {
                if (!addImages) {
                    viewModel.updateUris(list)
                } else {
                    val uris = (viewModel.uris ?: emptyList()).toMutableList()
                    list.forEach {
                        if (it !in uris) uris.add(it)
                    }
                    viewModel.updateUris(uris)
                }
            }
            addImages = false
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    var showImagePreviewDialog by rememberSaveable { mutableStateOf(false) }

    val gridState = rememberLazyStaggeredGridState()

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
                        IconButton(onClick = onGoBack) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        TopAppBarEmoji()
                    }
                )
                if (viewModel.uris.isNullOrEmpty()) {
                    Column(
                        Modifier
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
                        modifier = Modifier.fillMaxSize(),
                        verticalItemSpacing = 12.dp,
                        horizontalArrangement = Arrangement.spacedBy(
                            12.dp,
                            Alignment.CenterHorizontally
                        ),
                        state = gridState,
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
                                        }
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.shapes.large
                                        )
                                        .border(
                                            settingsState.borderWidth,
                                            MaterialTheme.colorScheme.outlineVariant(),
                                            MaterialTheme.shapes.large
                                        ),
                                    shape = MaterialTheme.shapes.large
                                )
                            }
                        }
                        if (!viewModel.uris.isNullOrEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(2f)
                                        .clip(MaterialTheme.shapes.large)
                                        .clickable {
                                            addImages = true
                                            pickImageLauncher.pickImage()
                                        }
                                        .background(
                                            MaterialTheme.colorScheme.secondaryContainer,
                                            MaterialTheme.shapes.large
                                        )
                                        .border(
                                            settingsState.borderWidth,
                                            MaterialTheme.colorScheme.outlineVariant(),
                                            MaterialTheme.shapes.large
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.AddPhotoAlternate,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(0.5f)
                                    )
                                }
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
                    .align(settingsState.fabAlignment),
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                expanded = if (settingsState.fabAlignment == Alignment.BottomCenter) true else gridState.isScrollingUp(),
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

    ImagePager(
        visible = showImagePreviewDialog && !viewModel.uris.isNullOrEmpty(),
        selectedUri = viewModel.selectedUri,
        uris = viewModel.uris,
        onUriSelected = viewModel::selectUri,
        onDismiss = { showImagePreviewDialog = false }
    )
}