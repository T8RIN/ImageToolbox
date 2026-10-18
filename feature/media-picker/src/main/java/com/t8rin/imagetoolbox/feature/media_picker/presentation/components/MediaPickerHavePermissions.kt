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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.KeyboardArrowDown
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Album
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AlbumState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent

@Composable
internal fun MediaPickerHavePermissions(
    component: MediaPickerComponent,
    allowedMedia: AllowedMedia,
    allowMultiple: Boolean,
    isGalleryMode: Boolean,
    onRequestManagePermission: () -> Unit,
    isManagePermissionAllowed: Boolean,
    onPicked: (List<Uri>) -> Unit,
    mediaState: MediaState,
    albumsState: AlbumState,
    showCategories: Boolean,
    visibleCategories: List<SemanticSearchCategoryResult>,
    selectedCategory: String?,
    categoriesLoading: Boolean
) {
    var selectedAlbumIndex by rememberSaveable { mutableLongStateOf(-1) }

    var isSearching by rememberSaveable {
        mutableStateOf(false)
    }

    var editingCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    BackHandler(selectedAlbumIndex != -1L) {
        selectedAlbumIndex = -1L
        component.getAlbum(selectedAlbumIndex)
    }

    BackHandler(isSearching) {
        isSearching = false
    }

    BackHandler(selectedCategory != null && !isSearching) {
        component.selectSemanticCategory(null)
    }

    Scaffold(
        topBar = {
            val hasAlbums = albumsState.albums.size > 1
            val isRefreshing = mediaState.isLoading && mediaState.media.isNotEmpty()

            Column(
                Modifier
                    .drawHorizontalStroke(
                        enabled = hasAlbums || isRefreshing || showCategories
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                AnimatedVisibility(
                    modifier = Modifier.fillMaxWidth(),
                    visible = hasAlbums
                ) {
                    val layoutDirection = LocalLayoutDirection.current
                    var showAlbumThumbnail by rememberSaveable {
                        mutableStateOf(false)
                    }
                    val listState = rememberLazyListState()
                    Row {
                        LazyRow(
                            modifier = Modifier
                                .weight(1f)
                                .fadingEdges(listState)
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 8.dp
                            ),
                            contentPadding = PaddingValues(
                                start = WindowInsets.displayCutout
                                    .asPaddingValues()
                                    .calculateStartPadding(layoutDirection) + 8.dp,
                                end = WindowInsets.displayCutout
                                    .asPaddingValues()
                                    .calculateEndPadding(layoutDirection) + 8.dp
                            ),
                            state = listState,
                            flingBehavior = enhancedFlingBehavior()
                        ) {
                            items(
                                items = albumsState.albums,
                                key = Album::toString
                            ) { album ->
                                val selected = selectedAlbumIndex == album.id
                                val isImageVisible = showAlbumThumbnail && album.uri.isNotEmpty()
                                EnhancedChip(
                                    selected = selected,
                                    selectedColor = MaterialTheme.colorScheme.secondaryContainer,
                                    unselectedColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    onClick = {
                                        selectedAlbumIndex = album.id
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
                                            if (album.id == -1L) stringResource(R.string.all) else album.label
                                        MediaPickerChipContent(
                                            title = title,
                                            previewUri = album.uri,
                                            count = album.count,
                                            showThumbnail = showAlbumThumbnail
                                        )
                                    },
                                    defaultMinSize = 32.dp,
                                    shape = ShapeDefaults.default
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
                AnimatedVisibility(
                    visible = showCategories,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    MediaSemanticSearchCategories(
                        categories = visibleCategories,
                        selectedCategory = selectedCategory,
                        isLoading = categoriesLoading,
                        onCategorySelected = component::selectSemanticCategory,
                        onEditCategory = {
                            focusManager.clearFocus()
                            editingCategoryId = it
                        },
                        modifier = Modifier.padding(
                            top = if (hasAlbums) 0.dp else 8.dp,
                            bottom = 8.dp
                        )
                    )
                }
                AnimatedVisibility(
                    visible = isRefreshing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearWavyProgressIndicator(
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets()
    ) { contentPadding ->
        MediaPickerGridWithOverlays(
            component = component,
            isSearching = isSearching,
            isGalleryMode = isGalleryMode,
            allowedMedia = allowedMedia,
            allowMultiple = allowMultiple,
            onRequestManagePermission = onRequestManagePermission,
            isManagePermissionAllowed = isManagePermissionAllowed,
            selectedAlbumIndex = selectedAlbumIndex,
            onSearchingChange = { isSearching = it },
            onPicked = onPicked,
            mediaState = mediaState,
            albumsState = albumsState,
            modifier = Modifier.padding(contentPadding)
        )
    }

    SemanticSearchCategoryEditor(
        visible = editingCategoryId != null,
        onDismiss = { if (!it) editingCategoryId = null },
        id = editingCategoryId.orEmpty(),
        component = component
    )
}