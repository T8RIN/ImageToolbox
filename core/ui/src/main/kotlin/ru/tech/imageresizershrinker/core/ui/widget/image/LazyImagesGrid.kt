package ru.tech.imageresizershrinker.core.ui.widget.image

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.request.ImageRequest
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.rememberAppColorTuple
import com.t8rin.modalsheet.FullscreenPopup
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalImageLoader
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@Composable
fun LazyImagesGrid(
    data: List<Uri>?,
    onAddImages: ((List<Uri>) -> Unit)?,
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    width: Dp = 150.dp,
    showTransparencyChecker: Boolean = true,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentPadding: PaddingValues = PaddingValues(
        bottom = 88.dp + WindowInsets
            .navigationBars
            .asPaddingValues()
            .calculateBottomPadding(),
        top = 12.dp,
        end = 12.dp,
        start = 12.dp
    )
) {
    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let {
                val uris = (data ?: emptyList()).toMutableList()
                list.forEach {
                    if (it !in uris) uris.add(it)
                }
                onAddImages?.invoke(uris)
            }
        }

    var showImagePreviewDialog by rememberSaveable { mutableStateOf(false) }
    var selectedUri by rememberSaveable { mutableStateOf<String?>(null) }


    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(width),
        modifier = modifier,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(
            8.dp,
            Alignment.CenterHorizontally
        ),
        state = state,
        contentPadding = contentPadding
    ) {
        data?.forEachIndexed { index, it ->
            item(
                key = it.hashCode().takeIf { c -> c != 0 } ?: index
            ) {
                Picture(
                    model = it,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                        .container(
                            shape = MaterialTheme.shapes.large,
                            color = color,
                            resultPadding = 0.dp
                        )
                        .clickable {
                            showImagePreviewDialog = true
                            selectedUri = it.toString()
                        },
                    showTransparencyChecker = showTransparencyChecker,
                    shape = MaterialTheme.shapes.large
                )
            }
        }
        if (!data.isNullOrEmpty() && onAddImages != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f)
                        .container(
                            shape = MaterialTheme.shapes.large,
                            resultPadding = 0.dp,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                        .clickable {
                            pickImageLauncher.pickImage()
                        },
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
    FullscreenPopup {
        ImagePager(
            visible = showImagePreviewDialog && !data.isNullOrEmpty(),
            selectedUri = selectedUri?.toUri(),
            uris = data,
            onUriSelected = {
                selectedUri = it?.toString()
            },
            onDismiss = { showImagePreviewDialog = false }
        )
    }

    val themeState = LocalDynamicThemeState.current
    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val appColorTuple = rememberAppColorTuple(
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = settingsState.isDynamicColors,
        darkTheme = settingsState.isNightMode
    )


    val context = LocalContext.current
    val imageLoader = LocalImageLoader.current
    LaunchedEffect(selectedUri) {
        selectedUri?.let { uri ->
            if (allowChangeColor) {
                imageLoader.execute(
                    ImageRequest.Builder(context)
                        .data(uri.toUri())
                        .build()
                ).drawable?.toBitmap()?.let {
                    themeState.updateColorByImage(it)
                }
            }
        } ?: themeState.updateColorTuple(appColorTuple)
    }
}