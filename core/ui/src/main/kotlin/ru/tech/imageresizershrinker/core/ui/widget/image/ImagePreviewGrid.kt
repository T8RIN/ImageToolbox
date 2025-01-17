/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.request.ImageRequest
import coil3.toBitmap
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFrames
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalImageLoader
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.hapticsClickable
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.utils.AutoContentBasedColors

@Composable
fun ImagePreviewGrid(
    data: List<Uri>?,
    onNavigate: (Screen) -> Unit,
    imageFrames: ImageFrames?,
    onFrameSelectionChange: (ImageFrames) -> Unit,
    onAddImages: ((List<Uri>) -> Unit)?,
    onShareImage: (Uri) -> Unit,
    onRemove: ((Uri) -> Unit)?,
    verticalCellSize: Dp = 120.dp,
    horizontalCellSize: Dp = verticalCellSize,
    contentPadding: PaddingValues? = null,
    initialShowImagePreviewDialog: Boolean = false
) {
    val imagePicker = rememberImagePicker { uriList: List<Uri> ->
        val uris = (data ?: emptyList()).toMutableList()
        uriList.forEach {
            if (it !in uris) uris.add(it)
        }
        onAddImages?.invoke(uris)
    }

    var showImagePreviewDialog by rememberSaveable(initialShowImagePreviewDialog) {
        mutableStateOf(initialShowImagePreviewDialog)
    }
    var selectedUri by rememberSaveable { mutableStateOf<String?>(null) }

    val cutout = WindowInsets.displayCutout.asPaddingValues()
    val direction = LocalLayoutDirection.current

    var isSelectionMode by rememberSaveable {
        mutableStateOf(false)
    }
    var isSelectionModePrevious by rememberSaveable {
        mutableStateOf(false)
    }
    ImagesPreviewWithSelection(
        imageUris = remember(data) {
            data?.map { it.toString() } ?: emptyList()
        },
        imageFrames = imageFrames ?: ImageFrames.ManualSelection(emptyList()),
        onFrameSelectionChange = {
            if (it.isEmpty()) {
                isSelectionMode = false
            }
            onFrameSelectionChange(it)
        },
        isPortrait = false,
        isLoadingImages = false,
        isAutoExpandLayout = false,
        onError = {
            onRemove?.invoke(it.toUri())
        },
        contentPadding = contentPadding ?: PaddingValues(
            bottom = 88.dp + WindowInsets
                .navigationBars
                .asPaddingValues()
                .calculateBottomPadding(),
            top = 12.dp,
            end = 12.dp + cutout.calculateEndPadding(direction),
            start = 12.dp + cutout.calculateStartPadding(direction)
        ),
        isSelectionMode = isSelectionMode,
        onItemClick = { index ->
            if (!isSelectionModePrevious) {
                showImagePreviewDialog = true
                data?.getOrNull(index)?.let {
                    selectedUri = it.toString()
                }
            }
            isSelectionModePrevious = isSelectionMode
        },
        onItemLongClick = {
            isSelectionModePrevious = true
            isSelectionMode = true
        },
        verticalCellSize = verticalCellSize,
        horizontalCellSize = horizontalCellSize,
        aboveImageContent = {},
        isAboveImageScrimEnabled = isSelectionMode,
        endAdditionalItem = if (!isSelectionMode && !data.isNullOrEmpty() && onAddImages != null) {
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f)
                        .container(
                            shape = MaterialTheme.shapes.extraSmall,
                            resultPadding = 0.dp,
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(0.3f)
                        )
                        .hapticsClickable(onClick = imagePicker::pickImage),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = stringResource(R.string.pick_images),
                        modifier = Modifier.fillMaxSize(0.4f),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.8f)
                    )
                }
            }
        } else null,
        isContentAlignToCenter = false
    )

    ImagePager(
        visible = showImagePreviewDialog && !data.isNullOrEmpty(),
        selectedUri = selectedUri?.toUri(),
        uris = data,
        onUriSelected = {
            selectedUri = it?.toString()
        },
        onShare = onShareImage,
        onDismiss = { showImagePreviewDialog = false },
        onNavigate = {
            showImagePreviewDialog = false
            onNavigate(it)
        }
    )

    val context = LocalContext.current
    val imageLoader = LocalImageLoader.current

    AutoContentBasedColors(
        model = selectedUri,
        selector = { uri ->
            imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(uri.toUri())
                    .build()
            ).image?.toBitmap()
        }
    )
}
