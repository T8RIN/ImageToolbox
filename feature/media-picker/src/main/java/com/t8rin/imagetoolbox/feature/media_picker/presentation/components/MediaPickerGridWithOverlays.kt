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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Deselect
import com.t8rin.imagetoolbox.core.resources.icons.SearchOff
import com.t8rin.imagetoolbox.core.resources.icons.Verified
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.ImagePickerMode
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButtonType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AlbumState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaState
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent

@Composable
internal fun MediaPickerGridWithOverlays(
    component: MediaPickerComponent,
    mediaState: MediaState,
    albumsState: AlbumState,
    isSearching: Boolean,
    isGalleryMode: Boolean,
    allowedMedia: AllowedMedia,
    allowMultiple: Boolean,
    onRequestManagePermission: () -> Unit,
    isManagePermissionAllowed: Boolean,
    selectedAlbumIndex: Long,
    onSearchingChange: (Boolean) -> Unit,
    onPicked: (List<Uri>) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedMedia = component.selectedMedia
    val isSemanticSearchEnabled by component.isSemanticSearchEnabled.collectAsStateWithLifecycle()
    val isSemanticSearching by component.isSemanticSearching.collectAsStateWithLifecycle()
    val semanticSearchError by component.semanticSearchError.collectAsStateWithLifecycle()
    val semanticSearchState by component.semanticSearchState.collectAsStateWithLifecycle()
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    var searchPanelHeight by remember { mutableStateOf(0.dp) }
    var controlsHeight by remember { mutableStateOf(0.dp) }

    var searchKeyword by rememberSaveable(isSearching) {
        mutableStateOf("")
    }
    val searchImage by component.semanticSearchImage.collectAsStateWithLifecycle()
    val onSearchImagePicked: (Uri) -> Unit = {
        searchKeyword = ""
        component.searchSimilarImages(it.toString())
    }
    val imagePicker = rememberImagePicker(ImagePickerMode.PhotoPickerSingle) { uris ->
        uris.firstOrNull()?.let(onSearchImagePicked)
    }
    val pickSearchImage = {
        focusManager.clearFocus()
        imagePicker.pickImage()
    }
    val isFilenameOnlySearch = searchImage == null && (
            !isSemanticSearchEnabled || searchKeyword.trim().let {
                it.startsWith("*") || it.endsWith("*")
            }
            )

    val filterMedia = {
        component.filterMedia(
            searchKeyword = searchKeyword.trim(),
            isForceReset = !isSearching || searchKeyword.trim()
                .isBlank() || mediaState.media.isEmpty()
        )
    }

    LaunchedEffect(isSearching) {
        if (!isSearching) {
            component.clearSearchImage()
            component.filterMedia("", isForceReset = true)
        }
    }

    val filteredMediaState by component.filteredMediaState.collectAsState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val isButtonVisible =
                (!allowMultiple || selectedMedia.isNotEmpty()) && !isSearching
            MediaPickerGrid(
                state = filteredMediaState,
                isSelectionOfAll = selectedAlbumIndex == -1L,
                selectedMedia = selectedMedia,
                allowMultiple = allowMultiple,
                isGalleryMode = isGalleryMode,
                isButtonVisible = isButtonVisible,
                isManagePermissionAllowed = isManagePermissionAllowed,
                onRequestManagePermission = onRequestManagePermission,
                bottomContentPadding = (if (isSearching) searchPanelHeight else controlsHeight) + 32.dp
            )
            BoxAnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .safeDrawingPadding(),
                visible = isButtonVisible,
                enter = slideInVertically { it * 2 },
                exit = slideOutVertically { it * 2 }
            ) {
                val enabled = selectedMedia.isNotEmpty()
                val containerColor by animateColorAsState(
                    targetValue = if (enabled) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else MaterialTheme.colorScheme.surfaceVariant
                )
                val contentColor by animateColorAsState(
                    targetValue = if (enabled) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    AnimatedVisibility(visible = selectedMedia.isNotEmpty()) {
                        EnhancedFloatingActionButton(
                            type = EnhancedFloatingActionButtonType.Small,
                            onClick = selectedMedia::clear,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            content = {
                                Icon(
                                    imageVector = Icons.Outlined.Deselect,
                                    contentDescription = stringResource(R.string.close)
                                )
                            },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    BadgedBox(
                        badge = {
                            BoxAnimatedVisibility(selectedMedia.isNotEmpty() && allowMultiple) {
                                EnhancedBadge(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    AnimatedContent(
                                        targetState = selectedMedia.size,
                                        transitionSpec = {
                                            fadeIn() + scaleIn(initialScale = 0.85f) togetherWith fadeOut() + scaleOut(
                                                targetScale = 0.85f
                                            )
                                        },
                                        modifier = Modifier.animateContentSizeNoClip()
                                    ) { size ->
                                        Text(
                                            text = size.toString(),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        EnhancedFloatingActionButton(
                            content = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Verified,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.pick))
                                }
                            },
                            containerColor = containerColor,
                            contentColor = contentColor,
                            onClick = {
                                if (enabled) {
                                    onPicked(selectedMedia.map { it.uri.toUri() })
                                }
                            },
                            modifier = Modifier
                                .semantics {
                                    contentDescription = "Add media"
                                }
                        )
                    }
                }
                BackHandler(selectedMedia.isNotEmpty()) {
                    selectedMedia.clear()
                }
            }
        }

        val isHaveNoData = mediaState.media.isEmpty() && !mediaState.isLoading
        val showLoading = (
                (mediaState.isLoading && mediaState.media.isEmpty()) || filteredMediaState.isLoading
                ) && !isHaveNoData

        val backgroundColor by animateColorAsState(
            MaterialTheme.colorScheme.scrim.copy(
                if (showLoading && filteredMediaState.media.isNotEmpty()) 0.5f else 0f
            )
        )
        BoxAnimatedVisibility(
            visible = showLoading,
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .background(backgroundColor),
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(
                        WindowInsets.displayCutout
                            .union(WindowInsets.navigationBars)
                    ),
                contentAlignment = Alignment.Center
            ) {
                EnhancedLoadingIndicator()
            }
        }

        BoxAnimatedVisibility(
            visible = filteredMediaState.media.isEmpty() && !filteredMediaState.isLoading && isSearching &&
                    (isFilenameOnlySearch || (
                            !isSemanticSearching && semanticSearchError == null && semanticSearchState.error == null &&
                                    semanticSearchState.modelsReady && !semanticSearchState.isDownloading &&
                                    (semanticSearchState.indexedImages > 0 || (
                                            !semanticSearchState.isIndexing && !semanticSearchState.isQueued &&
                                                    !semanticSearchState.isWaitingForPermission
                                            ))
                            )),
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.nothing_found_by_search),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                )
                Icon(
                    imageVector = Icons.Outlined.SearchOff,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(2f)
                        .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                        .fillMaxSize()
                )
                Spacer(Modifier.weight(1f))
            }
        }

        BoxAnimatedVisibility(
            visible = isHaveNoData,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            val errorMessage = albumsState.error + "\n" + mediaState.error
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Rounded.BrokenImageAlt,
                    contentDescription = null,
                    modifier = Modifier.size(108.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage.trim().ifEmpty {
                        stringResource(id = R.string.no_data)
                    },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                EnhancedButton(
                    onClick = {
                        component.init(allowedMedia)
                    }
                ) {
                    Text(stringResource(id = R.string.try_again))
                }
                Spacer(Modifier.weight(1f))
            }
        }

        BoxAnimatedVisibility(
            visible = isSearching || (!mediaState.isLoading && !isHaveNoData),
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AnimatedContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .safeDrawingPadding(),
                targetState = isSearching,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { searchMode ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomStart
                ) {
                    if (searchMode) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onSizeChanged {
                                    searchPanelHeight = with(density) { it.height.toDp() }
                                }
                        ) {
                            RoundedTextField(
                                maxLines = 1,
                                hint = {
                                    Text(
                                        stringResource(
                                            when {
                                                isSemanticSearchEnabled &&
                                                        component.settingsState.semanticSearchSettings.groupPeople -> {
                                                    R.string.gallery_search_people_hint
                                                }

                                                isSemanticSearchEnabled -> R.string.gallery_search_hint
                                                else -> R.string.search_here
                                            }
                                        )
                                    )
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Search,
                                    autoCorrectEnabled = null
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = { focusManager.clearFocus() }
                                ),
                                value = if (searchImage == null) searchKeyword else stringResource(R.string.gallery_search_similar_results),
                                readOnly = searchImage != null,
                                onValueChange = {
                                    searchKeyword = it
                                    filterMedia()
                                },
                                startIcon = {
                                    EnhancedIconButton(
                                        onClick = {
                                            searchKeyword = ""
                                            onSearchingChange(false)
                                            filterMedia()
                                        },
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = stringResource(R.string.exit),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                endIcon = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        BoxAnimatedVisibility(
                                            visible = searchKeyword.isNotEmpty() || searchImage != null,
                                            enter = fadeIn() + scaleIn(),
                                            exit = fadeOut() + scaleOut()
                                        ) {
                                            EnhancedIconButton(
                                                onClick = {
                                                    searchKeyword = ""
                                                    component.clearSearchImage()
                                                    filterMedia()
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Delete,
                                                    contentDescription = stringResource(R.string.clear),
                                                    tint = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                        if (allowedMedia != AllowedMedia.Videos) {
                                            if (isSemanticSearchEnabled && semanticSearchState.modelsReady && semanticSearchState.indexedImages > 0 && searchImage != null) {
                                                EnhancedIconButton(
                                                    onClick = pickSearchImage,
                                                    shape = ShapeDefaults.small
                                                ) {
                                                    Picture(
                                                        model = searchImage,
                                                        contentDescription = stringResource(R.string.gallery_search_change_image),
                                                        shape = RectangleShape,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                }
                                            }
                                            MediaSemanticSearchControls(
                                                component = component,
                                                onSearchImagePicked = onSearchImagePicked
                                            )
                                        }
                                    }
                                },
                                shape = ShapeDefaults.circle
                            )
                        }
                    } else {
                        MediaSearchAndFilter(
                            component = component,
                            onSearch = {
                                onSearchingChange(true)
                                filterMedia()
                            },
                            modifier = Modifier
                                .onSizeChanged {
                                    controlsHeight = with(density) { it.height.toDp() }
                                }
                                .padding(bottom = 6.dp)
                        )
                    }
                }
            }
        }
    }
}