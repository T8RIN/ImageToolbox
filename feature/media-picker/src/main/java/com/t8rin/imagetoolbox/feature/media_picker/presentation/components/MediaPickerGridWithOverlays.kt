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
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
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
import com.t8rin.imagetoolbox.core.resources.icons.Restore
import com.t8rin.imagetoolbox.core.resources.icons.SearchOff
import com.t8rin.imagetoolbox.core.resources.icons.SelectAll
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
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Album
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AlbumState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaAction
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaDateGroup
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaGrouping
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaOrder
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.isFilenameOnlyQuery
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
    val inTrash = selectedAlbumIndex == Album.TRASH_ID
    val hasTrash = albumsState.albums.any { it.id == Album.TRASH_ID && it.count > 0 }
    val isSemanticSearchEnabled by component.isSemanticSearchEnabled.collectAsStateWithLifecycle()
    val isSearchingContent by component.isSearchingContent.collectAsStateWithLifecycle()
    val semanticSearchState by component.semanticSearchState.collectAsStateWithLifecycle()
    val ocrState by component.galleryOcrState.collectAsStateWithLifecycle()
    val ocrEnabled = component.settingsState.isGalleryOcrEnabled &&
            allowedMedia != AllowedMedia.Videos && !inTrash
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    var searchPanelHeight by remember { mutableStateOf(0.dp) }
    var controlsHeight by remember { mutableStateOf(0.dp) }
    var selectionActionsSize by remember { mutableStateOf(DpSize.Zero) }
    val selectionActionsVisibility = remember { MutableTransitionState(false) }
    selectionActionsVisibility.targetState =
        !inTrash && selectedMedia.isNotEmpty() && component.areMediaActionsAvailable

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
    val searchesText = searchImage == null && !searchKeyword.trim().isFilenameOnlyQuery()
    val waitingForOcr = searchesText && ocrEnabled && !ocrState.hasIndexed && ocrState.isPreparing
    val waitingForSemantic = isSemanticSearchEnabled && (searchesText || searchImage != null) &&
            (semanticSearchState.isDownloading || semanticSearchState.isWaitingForPermission ||
                    semanticSearchState.indexedImages == 0 &&
                    (semanticSearchState.isIndexing || semanticSearchState.isQueued))

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

    val (filteredMediaState, appliedKeyword, appliedImage, appliedCategory) =
        component.filteredMediaState.collectAsState().value
    val displaySettings by component.displaySettings.collectAsStateWithLifecycle()
    val selectedCategory by component.selectedSemanticCategory.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    LaunchedEffect(
        selectedAlbumIndex,
        displaySettings,
        searchKeyword.trim(),
        searchImage,
        selectedCategory,
        appliedKeyword,
        appliedImage,
        appliedCategory
    ) {
        gridState.requestScrollToItem(0)
    }
    val sortByDateTaken = displaySettings.mediaOrder is MediaOrder.DateTaken
    val useDateTaken = if (displaySettings.dateGroup == MediaDateGroup.None) sortByDateTaken
    else when (displaySettings.grouping) {
        MediaGrouping.DateTaken -> true
        MediaGrouping.DateModified -> false
        else -> sortByDateTaken
    }
    val selectedUris = if (inTrash) selectedMedia.mapTo(hashSetOf()) { it.uri } else emptySet()
    val canSelectAll = inTrash && filteredMediaState.media.any { it.uri !in selectedUris }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val isButtonVisible =
                !inTrash && (!allowMultiple || selectedMedia.isNotEmpty()) && !isSearching
            MediaPickerGrid(
                albumId = selectedAlbumIndex,
                state = filteredMediaState,
                gridState = gridState,
                isSelectionOfAll = selectedAlbumIndex == Album.ALL_ID || inTrash,
                selectedMedia = selectedMedia,
                allowMultiple = allowMultiple || inTrash,
                isGalleryMode = isGalleryMode,
                isButtonVisible = isButtonVisible,
                useDateTaken = useDateTaken,
                isManagePermissionAllowed = isManagePermissionAllowed,
                onRequestManagePermission = onRequestManagePermission,
                onMediaAction = component::requestMediaAction.takeIf { component.areMediaActionsAvailable },
                bottomContentPadding = maxOf(
                    if (isSearching) searchPanelHeight else controlsHeight,
                    if (selectedMedia.isNotEmpty()) selectionActionsSize.height else 0.dp
                ) + 32.dp
            )
            BoxAnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .safeDrawingPadding(),
                visible = isButtonVisible || (inTrash && selectedMedia.isNotEmpty()),
                keepContentOnCollapse = true,
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
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.onSizeChanged {
                        selectionActionsSize =
                            with(density) { DpSize(it.width.toDp(), it.height.toDp()) }
                    }
                ) {
                    AnimatedVisibility(visible = selectedMedia.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            if (canSelectAll) EnhancedFloatingActionButton(
                                type = EnhancedFloatingActionButtonType.Small,
                                onClick = {
                                    selectedMedia += filteredMediaState.media.filter { it.uri !in selectedUris }
                                }.takeUnless { component.isMediaActionRunning },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                content = {
                                    Icon(
                                        imageVector = Icons.Outlined.SelectAll,
                                        contentDescription = null
                                    )
                                }
                            )
                            EnhancedFloatingActionButton(
                                type = EnhancedFloatingActionButtonType.Small,
                                onClick = selectedMedia::clear.takeUnless { component.isMediaActionRunning },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                content = {
                                    Icon(
                                        imageVector = Icons.Outlined.Deselect,
                                        contentDescription = stringResource(R.string.close)
                                    )
                                }
                            )
                        }
                    }
                    BadgedBox(
                        badge = {
                            BoxAnimatedVisibility(selectedMedia.isNotEmpty() && (allowMultiple || inTrash)) {
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
                        if (inTrash) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                EnhancedFloatingActionButton(
                                    type = EnhancedFloatingActionButtonType.SecondaryHorizontal,
                                    onClick = {
                                        component.requestMediaActions(
                                            MediaAction.Restore,
                                            selectedMedia.toList()
                                        )
                                    }.takeUnless { component.isMediaActionRunning },
                                    content = {
                                        Icon(
                                            imageVector = Icons.Rounded.Restore,
                                            contentDescription = null
                                        )
                                    }
                                )
                                EnhancedFloatingActionButton(
                                    onClick = {
                                        component.requestMediaActions(
                                            MediaAction.Delete,
                                            selectedMedia.toList()
                                        )
                                    }.takeUnless { component.isMediaActionRunning },
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                    content = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Delete,
                                                contentDescription = null
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(stringResource(R.string.delete))
                                        }
                                    }
                                )
                            }
                        } else EnhancedFloatingActionButton(
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
                            }
                        )
                    }
                }
            }
            BackHandler(selectedMedia.isNotEmpty() && !component.isMediaActionRunning) {
                selectedMedia.clear()
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
                    !isSearchingContent && !waitingForSemantic && !waitingForOcr,
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
                    imageVector = if (inTrash) Icons.Outlined.Delete else Icons.Rounded.BrokenImageAlt,
                    contentDescription = null,
                    modifier = Modifier.size(108.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage.trim().ifEmpty {
                        stringResource(id = if (inTrash) R.string.gallery_trash_empty else R.string.no_data)
                    },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (!inTrash || errorMessage.isNotBlank()) EnhancedButton(
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
            visible = (isSearching || (!mediaState.isLoading && (!isHaveNoData || hasTrash))) &&
                    (!inTrash || selectedMedia.isEmpty()),
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
                BoxWithConstraints(
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
                                                isSemanticSearchEnabled && ocrEnabled -> R.string.gallery_search_content_hint
                                                isSemanticSearchEnabled &&
                                                        component.settingsState.semanticSearchSettings.groupPeople -> {
                                                    R.string.gallery_search_people_hint
                                                }

                                                isSemanticSearchEnabled -> R.string.gallery_search_hint
                                                ocrEnabled -> R.string.gallery_ocr_search_hint
                                                else -> R.string.search_here
                                            }
                                        ),
                                        modifier = Modifier.marquee()
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
                            selectionActionsVisibility = selectionActionsVisibility,
                            onSearch = {
                                onSearchingChange(true)
                                filterMedia()
                            },
                            modifier = Modifier
                                .widthIn(
                                    max = if (
                                        !inTrash && (
                                                selectedMedia.isNotEmpty() || selectionActionsVisibility.currentState ||
                                                        !selectionActionsVisibility.isIdle
                                                )
                                    ) {
                                        (maxWidth - selectionActionsSize.width - 8.dp).coerceAtLeast(
                                            78.dp
                                        )
                                    } else maxWidth
                                )
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