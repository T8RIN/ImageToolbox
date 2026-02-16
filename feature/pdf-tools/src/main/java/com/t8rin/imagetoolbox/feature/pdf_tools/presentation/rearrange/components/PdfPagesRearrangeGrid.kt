/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.rearrange.components

import android.graphics.drawable.GradientDrawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.request.ImageRequest
import coil3.size.pxOrElse
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreview
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.utils.appContext
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
internal fun PdfPagesRearrangeGrid(
    pages: List<PageData>?,
    modifier: Modifier = Modifier
        .container(
            shape = ShapeDefaults.extraLarge,
            clip = false
        ),
    onReorder: (List<PageData>) -> Unit,
    title: String = stringResource(R.string.hold_drag_drop),
    coerceHeight: Boolean = true
) {
    val data = remember(pages) { mutableStateOf(pages ?: emptyList()) }

    val haptics = LocalHapticFeedback.current
    val listState = rememberLazyGridState()

    val state = rememberReorderableLazyGridState(
        lazyGridState = listState,
        onMove = { from, to ->
            haptics.press()
            data.value = data.value.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        }
    )

    Column(
        modifier = modifier
            .then(
                if (coerceHeight) {
                    Modifier
                        .heightIn(
                            max = LocalWindowInfo.current.containerDpSize.height * 0.7f
                        )
                } else {
                    Modifier
                }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 8.dp
                )
        ) {
            Text(
                fontWeight = FontWeight.Medium,
                text = title,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
            )
            Spacer(Modifier.width(16.dp))

            EnhancedButton(
                onClick = {
                    onReorder(data.value.sortedBy { it.index })
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 12.dp
                ),
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .height(30.dp),
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Rounded.Restore,
                        contentDescription = "Restore",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.reset)
                    )
                }
            }

        }
        Box(
            modifier = Modifier.weight(1f, false)
        ) {
            LazyVerticalGrid(
                state = listState,
                modifier = Modifier
                    .fadingEdges(
                        scrollableState = listState,
                        isVertical = true
                    )
                    .animateContentSizeNoClip(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                flingBehavior = enhancedFlingBehavior(),
                columns = GridCells.Adaptive(minSize = 150.dp)
            ) {
                items(
                    items = data.value,
                    key = { uri -> uri.toString() + uri.hashCode() }
                ) { uri ->
                    ReorderableItem(
                        state = state,
                        key = uri.toString() + uri.hashCode(),
                    ) { isDragging ->
                        val alpha by animateFloatAsState(if (isDragging) 0.3f else 0.6f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .container(
                                    color = MaterialTheme.colorScheme.surface,
                                    resultPadding = 8.dp
                                )
                                .container(
                                    shape = ShapeDefaults.small,
                                    color = Color.Transparent,
                                    resultPadding = 0.dp
                                )
                                .longPressDraggableHandle(
                                    onDragStarted = {
                                        haptics.longPress()
                                    },
                                    onDragStopped = {
                                        onReorder(data.value)
                                    }
                                )
                        ) {
                            Picture(
                                model = uri.request,
                                modifier = Modifier.fillMaxSize(),
                                shape = RectangleShape,
                                contentScale = ContentScale.Inside
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${uri.index + 1}",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

internal data class PageData(
    val index: Int,
    val request: ImageRequest
)

@Composable
@EnPreview
private fun Preview() = ImageToolboxThemeForPreview(true) {
    var files by remember {
        mutableStateOf(
            List(15) {
                PageData(
                    index = it,
                    request = ImageRequest.Builder(appContext).data("file:///uri_$it.pdf".toUri())
                        .build()
                )
            }
        )
    }

    CompositionLocalProvider(
        LocalAsyncImagePreviewHandler provides remember {
            AsyncImagePreviewHandler(
                image = { request: ImageRequest ->
                    GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        listOf(
                            Color.Yellow,
                            Color.Red
                        ).map { it.toArgb() }.toIntArray()
                    ).toBitmap(
                        request.sizeResolver.size().width.pxOrElse { 0 } - 200,
                        request.sizeResolver.size().height.pxOrElse { 0 }
                    ).asImage()
                }
            )
        }
    ) {
        LazyColumn {
            item {
                PdfPagesRearrangeGrid(
                    pages = files,
                    onReorder = {
                        files = it
                    }
                )
            }

            items(30) {
                Text("TEST $it")
            }
        }
    }
}