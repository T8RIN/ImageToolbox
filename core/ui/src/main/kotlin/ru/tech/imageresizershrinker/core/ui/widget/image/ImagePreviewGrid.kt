/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.ui.widget.image

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.request.ImageRequest
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.rememberAppColorTuple
import com.t8rin.modalsheet.FullscreenPopup
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.safeAspectRatio
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalImageLoader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun ImagePreviewGrid(
    data: List<Uri>?,
    onAddImages: ((List<Uri>) -> Unit)?,
    onShareImage: (Uri) -> Unit,
    onRemove: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    minCellWidth: Dp = 150.dp,
    showTransparencyChecker: Boolean = true,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentPadding: PaddingValues? = null,
    initialShowImagePreviewDialog: Boolean = false
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

    var showImagePreviewDialog by rememberSaveable(initialShowImagePreviewDialog) {
        mutableStateOf(initialShowImagePreviewDialog)
    }
    var selectedUri by rememberSaveable { mutableStateOf<String?>(null) }

    val cutout = WindowInsets.displayCutout.asPaddingValues()
    val direction = LocalLayoutDirection.current

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minCellWidth),
        modifier = modifier,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(
            8.dp,
            Alignment.CenterHorizontally
        ),
        state = state,
        contentPadding = contentPadding ?: PaddingValues(
            bottom = 88.dp + WindowInsets
                .navigationBars
                .asPaddingValues()
                .calculateBottomPadding(),
            top = 12.dp,
            end = 12.dp + cutout.calculateEndPadding(direction),
            start = 12.dp + cutout.calculateStartPadding(direction)
        )
    ) {
        data?.forEachIndexed { index, uri ->
            item(
                key = uri.hashCode().takeIf { c -> c != 0 } ?: index
            ) {
                var aspectRatio by rememberSaveable {
                    mutableFloatStateOf(1f)
                }
                Picture(
                    model = uri,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .container(
                            shape = MaterialTheme.shapes.large,
                            color = color,
                            resultPadding = 0.dp
                        )
                        .clickable {
                            showImagePreviewDialog = true
                            selectedUri = uri.toString()
                        },
                    onError = {
                        onRemove(uri)
                    },
                    onSuccess = {
                        aspectRatio = it.result.drawable.safeAspectRatio
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
                        contentDescription = stringResource(R.string.pick_images),
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
            onShare = onShareImage,
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