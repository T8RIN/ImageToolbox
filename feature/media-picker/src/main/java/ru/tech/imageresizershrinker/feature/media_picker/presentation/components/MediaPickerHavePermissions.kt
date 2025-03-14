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

package ru.tech.imageresizershrinker.feature.media_picker.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.Album
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.AllowedMedia
import ru.tech.imageresizershrinker.feature.media_picker.presentation.screenLogic.MediaPickerComponent

@Composable
internal fun MediaPickerHavePermissions(
    component: MediaPickerComponent,
    allowedMedia: AllowedMedia,
    allowMultiple: Boolean,
    onRequestManagePermission: () -> Unit,
    isManagePermissionAllowed: Boolean
) {
    var selectedAlbumIndex by rememberSaveable { mutableLongStateOf(-1) }

    val albumsState by component.albumsState.collectAsState()
    var isSearching by rememberSaveable {
        mutableStateOf(false)
    }

    Column {
        AnimatedVisibility(
            visible = albumsState.albums.size > 1
        ) {
            val layoutDirection = LocalLayoutDirection.current
            var showAlbumThumbnail by rememberSaveable {
                mutableStateOf(false)
            }
            val listState = rememberLazyListState()
            Row(
                modifier = Modifier
                    .drawHorizontalStroke()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                LazyRow(
                    modifier = Modifier
                        .weight(1f)
                        .fadingEdges(listState)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    contentPadding = PaddingValues(
                        start = WindowInsets.displayCutout
                            .asPaddingValues()
                            .calculateStartPadding(layoutDirection) + 8.dp,
                        end = WindowInsets.displayCutout
                            .asPaddingValues()
                            .calculateEndPadding(layoutDirection) + 8.dp
                    ),
                    state = listState
                ) {
                    items(
                        items = albumsState.albums,
                        key = Album::toString
                    ) {
                        val selected = selectedAlbumIndex == it.id
                        val isImageVisible = showAlbumThumbnail && it.uri.isNotEmpty()
                        EnhancedChip(
                            selected = selected,
                            selectedColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            onClick = {
                                selectedAlbumIndex = it.id
                                component.getAlbum(selectedAlbumIndex)
                            },
                            contentPadding = PaddingValues(
                                horizontal = animateDpAsState(
                                    if (isImageVisible) 8.dp
                                    else 12.dp
                                ).value,
                                vertical = animateDpAsState(
                                    if (isImageVisible) 8.dp
                                    else 0.dp
                                ).value
                            ),
                            label = {
                                val title =
                                    if (it.id == -1L) stringResource(R.string.all) else it.label
                                Column(
                                    modifier = Modifier.animateContentSize(
                                        alignment = Alignment.Center
                                    ),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    var width by remember {
                                        mutableStateOf(1.dp)
                                    }
                                    val density = LocalDensity.current
                                    Text(
                                        text = title,
                                        modifier = Modifier.onSizeChanged {
                                            width = with(density) {
                                                it.width.toDp().coerceAtLeast(100.dp)
                                            }
                                        }
                                    )
                                    BoxAnimatedVisibility(
                                        visible = isImageVisible,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Box {
                                            BoxAnimatedVisibility(
                                                visible = width > 1.dp,
                                                enter = fadeIn() + scaleIn(),
                                                exit = fadeOut() + scaleOut()
                                            ) {
                                                Picture(
                                                    model = it.uri,
                                                    modifier = Modifier
                                                        .padding(top = 8.dp)
                                                        .height(100.dp)
                                                        .width(width),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 8.dp)
                                                    .height(100.dp)
                                                    .width(width)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        MaterialTheme
                                                            .colorScheme
                                                            .surfaceContainer
                                                            .copy(0.6f)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AutoSizeText(
                                                    text = it.count.toString(),
                                                    style = MaterialTheme.typography.headlineLarge.copy(
                                                        fontSize = 20.sp,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            defaultMinSize = 32.dp,
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
                EnhancedIconButton(
                    onClick = { showAlbumThumbnail = !showAlbumThumbnail }
                ) {
                    val rotation by animateFloatAsState(if (showAlbumThumbnail) 180f else 0f)
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Expand",
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
        }
        MediaPickerGridWithOverlays(
            component = component,
            isSearching = isSearching,
            allowedMedia = allowedMedia,
            allowMultiple = allowMultiple,
            onRequestManagePermission = onRequestManagePermission,
            isManagePermissionAllowed = isManagePermissionAllowed,
            selectedAlbumIndex = selectedAlbumIndex,
            onSearchingChange = { isSearching = it }
        )
    }
    BackHandler(selectedAlbumIndex != -1L) {
        selectedAlbumIndex = -1L
        component.getAlbum(selectedAlbumIndex)
    }

    BackHandler(isSearching) {
        isSearching = false
    }
}