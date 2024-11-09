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

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.twotone.ImageNotSupported
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedFloatingActionButtonType
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingIndicator
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.Album
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.AllowedMedia
import ru.tech.imageresizershrinker.feature.media_picker.presentation.screenLogic.MediaPickerComponent

@Composable
fun MediaPickerScreen(
    allowedMedia: AllowedMedia,
    allowMultiple: Boolean,
    component: MediaPickerComponent,
    sendMediaAsResult: (List<Uri>) -> Unit,
    onRequestManagePermission: () -> Unit,
    isManagePermissionAllowed: Boolean
) {
    val scope = rememberCoroutineScope()
    var selectedAlbumIndex by rememberSaveable { mutableLongStateOf(-1) }
    val selectedMedia = component.selectedMedia

    val albumsState by component.albumsState.collectAsState()
    val mediaState by component.mediaState.collectAsState()

    var isSearching by rememberSaveable {
        mutableStateOf(false)
    }
    var searchKeyword by rememberSaveable(isSearching) {
        mutableStateOf("")
    }

    val filterMedia = {
        component.filterMedia(
            searchKeyword = searchKeyword.trim(),
            isForceReset = !isSearching || searchKeyword.trim()
                .isBlank() || mediaState.media.isEmpty()
        )
    }

    val filteredMediaState by component.filteredMediaState.collectAsState()

    Column {
        val layoutDirection = LocalLayoutDirection.current

        AnimatedVisibility(
            visible = albumsState.albums.size > 1
        ) {
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
                                            Picture(
                                                model = it.uri,
                                                modifier = Modifier
                                                    .padding(top = 8.dp)
                                                    .height(100.dp)
                                                    .width(width),
                                                shape = RoundedCornerShape(12.dp)
                                            )
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
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(1f)
            ) {
                val isButtonVisible =
                    (!allowMultiple || selectedMedia.isNotEmpty()) && !isSearching
                MediaPickerGrid(
                    state = filteredMediaState,
                    isSelectionOfAll = selectedAlbumIndex == -1L,
                    selectedMedia = selectedMedia,
                    allowMultiple = allowMultiple,
                    isButtonVisible = isButtonVisible,
                    isManagePermissionAllowed = isManagePermissionAllowed,
                    onRequestManagePermission = onRequestManagePermission
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
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = stringResource(R.string.close)
                                    )
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        BadgedBox(
                            badge = {
                                if (selectedMedia.isNotEmpty() && allowMultiple) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(selectedMedia.size.toString())
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
                                            imageVector = Icons.Outlined.TaskAlt,
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
                                        scope.launch {
                                            sendMediaAsResult(selectedMedia.map { it.uri.toUri() })
                                        }
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
            val visible = mediaState.isLoading || filteredMediaState.isLoading

            val backgroundColor by animateColorAsState(
                MaterialTheme.colorScheme.scrim.copy(
                    if (visible && filteredMediaState.media.isNotEmpty()) 0.5f else 0f
                )
            )
            BoxAnimatedVisibility(
                visible = visible,
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
                        .padding(
                            start = WindowInsets.displayCutout
                                .asPaddingValues()
                                .calculateStartPadding(layoutDirection),
                            end = WindowInsets.displayCutout
                                .asPaddingValues()
                                .calculateEndPadding(layoutDirection),
                            bottom = WindowInsets.navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding()
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }

            BoxAnimatedVisibility(
                visible = filteredMediaState.media.isEmpty() && !filteredMediaState.isLoading && isSearching,
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
                        imageVector = Icons.Rounded.SearchOff,
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
                visible = mediaState.media.isEmpty() && !mediaState.isLoading,
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
                        imageVector = Icons.TwoTone.ImageNotSupported,
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
                visible = !mediaState.isLoading,
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
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        if (it) {
                            RoundedTextField(
                                maxLines = 1,
                                hint = { Text(stringResource(id = R.string.search_here)) },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Search,
                                    autoCorrectEnabled = null
                                ),
                                value = searchKeyword,
                                onValueChange = {
                                    searchKeyword = it
                                    filterMedia()
                                },
                                startIcon = {
                                    EnhancedIconButton(
                                        onClick = {
                                            searchKeyword = ""
                                            isSearching = false
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
                                    BoxAnimatedVisibility(
                                        visible = searchKeyword.isNotEmpty(),
                                        enter = fadeIn() + scaleIn(),
                                        exit = fadeOut() + scaleOut()
                                    ) {
                                        EnhancedIconButton(
                                            onClick = {
                                                searchKeyword = ""
                                                filterMedia()
                                            },
                                            modifier = Modifier.padding(end = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.DeleteOutline,
                                                contentDescription = stringResource(R.string.close),
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                },
                                shape = CircleShape
                            )
                        } else {
                            EnhancedIconButton(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier
                                    .padding(bottom = 6.dp)
                                    .size(44.dp),
                                onClick = {
                                    isSearching = true
                                    filterMedia()
                                },
                                shape = MaterialStarShape
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    BackHandler(selectedAlbumIndex != -1L) {
        selectedAlbumIndex = -1L
        component.getAlbum(selectedAlbumIndex)
    }

    BackHandler(isSearching) {
        isSearching = false
    }
}