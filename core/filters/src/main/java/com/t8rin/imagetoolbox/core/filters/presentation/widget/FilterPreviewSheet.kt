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

package com.t8rin.imagetoolbox.core.filters.presentation.widget

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters.AddFiltersSheetComponent
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.image.SimplePicture
import com.t8rin.imagetoolbox.core.ui.widget.image.imageStickyHeader
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.core.ui.widget.utils.rememberAvailableHeight
import com.t8rin.imagetoolbox.core.ui.widget.utils.rememberImageState

@Composable
internal fun FilterPreviewSheet(
    component: AddFiltersSheetComponent,
    onFilterPickedWithParams: (UiFilter<*>) -> Unit,
    onVisibleChange: (Boolean) -> Unit,
    previewBitmap: Bitmap?
) {
    val previewSheetData = component.previewData
    var imageState by rememberImageState()
    LaunchedEffect(previewSheetData) {
        if (previewBitmap != null && previewSheetData != null) {
            if (previewSheetData.size == 1 && previewSheetData.firstOrNull()?.value is Unit) {
                imageState = imageState.copy(position = 2)
            }
            component.updatePreview(previewBitmap)
        }
    }

    EnhancedModalBottomSheet(
        dragHandle = {
            EnhancedModalSheetDragHandle(
                showDragHandle = false
            ) {
                EnhancedTopAppBar(
                    type = EnhancedTopAppBarType.Center,
                    drawHorizontalStroke = false,
                    navigationIcon = {
                        EnhancedIconButton(
                            onClick = {
                                component.setPreviewData(null)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.close)
                            )
                        }
                    },
                    colors = EnhancedTopAppBarDefaults.colors(
                        containerColor = EnhancedBottomSheetDefaults.barContainerColor
                    ),
                    actions = {
                        EnhancedIconButton(
                            onClick = {
                                previewSheetData?.forEach { filter ->
                                    onFilterPickedWithParams(
                                        filter.copy(filter.value).also {
                                            it.isVisible = true
                                        }
                                    )
                                }
                                component.setPreviewData(null)
                                onVisibleChange(false)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Done,
                                contentDescription = "Done"
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(
                                id = previewSheetData?.let {
                                    if (it.size == 1) it.first().title
                                    else R.string.filter_preview
                                } ?: R.string.filter_preview
                            ),
                            modifier = Modifier.marquee()
                        )
                    }
                )
            }
        },
        sheetContent = {
            val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow

            DisposableEffect(Unit) {
                onDispose {
                    imageState = imageState.copy(position = 2)
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val imageBlock = @Composable {
                    AnimatedContent(
                        targetState = component.previewBitmap == null,
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { isNull ->
                        if (isNull) {
                            Box(
                                modifier = if (component.previewBitmap == null) {
                                    Modifier
                                        .aspectRatio(
                                            previewBitmap?.safeAspectRatio ?: (1 / 2f)
                                        )
                                        .clip(ShapeDefaults.mini)
                                        .shimmer(true)
                                } else Modifier
                            )
                        } else {
                            SimplePicture(
                                bitmap = component.previewBitmap,
                                loading = component.isImageLoading,
                                modifier = Modifier
                            )
                        }
                    }
                }
                val isPortrait by isPortraitOrientationAsState()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    val isUnit =
                        previewSheetData?.size == 1 && previewSheetData.firstOrNull()?.value is Unit
                    if (!isPortrait) {
                        Box(
                            modifier = Modifier
                                .container(RectangleShape)
                                .weight(1.2f)
                                .padding(20.dp)
                        ) {
                            Box(Modifier.align(Alignment.Center)) {
                                imageBlock()
                            }
                        }
                    }

                    val internalHeight = rememberAvailableHeight(imageState = imageState)
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .then(
                                if (!isPortrait && !isUnit) Modifier.weight(1f)
                                else Modifier
                            )
                            .clipToBounds(),
                        flingBehavior = enhancedFlingBehavior()
                    ) {
                        imageStickyHeader(
                            visible = isPortrait,
                            imageState = imageState,
                            internalHeight = internalHeight,
                            onStateChange = { imageState = it },
                            imageBlock = imageBlock,
                            backgroundColor = backgroundColor
                        )
                        item {
                            previewSheetData?.takeIf { !isUnit }?.let { list ->
                                list.forEachIndexed { index, filter ->
                                    FilterItem(
                                        backgroundColor = MaterialTheme
                                            .colorScheme
                                            .surface,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        filter = filter,
                                        showDragHandle = false,
                                        onRemove = {
                                            if (list.size == 1) {
                                                component.setPreviewData(null)
                                            } else component.removeFilterAtIndex(index)
                                        },
                                        onCreateTemplate = null,
                                        onFilterChange = { value ->
                                            component.updateFilter(value, index)
                                        }
                                    )
                                    if (index != list.lastIndex) {
                                        Spacer(Modifier.height(8.dp))
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                            }
                            Spacer(
                                Modifier.height(
                                    WindowInsets
                                        .navigationBars
                                        .asPaddingValues()
                                        .calculateBottomPadding()
                                )
                            )
                        }
                    }
                }
            }
        },
        visible = previewSheetData != null,
        onDismiss = {
            if (!it) {
                component.setPreviewData(null)
            }
        }
    )
}