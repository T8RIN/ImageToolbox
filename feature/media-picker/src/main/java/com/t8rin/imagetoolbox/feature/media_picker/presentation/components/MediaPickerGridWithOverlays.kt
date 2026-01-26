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
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.twotone.ImageNotSupported
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButtonType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent

@Composable
internal fun ColumnScope.MediaPickerGridWithOverlays(
    component: MediaPickerComponent,
    isSearching: Boolean,
    allowedMedia: AllowedMedia,
    allowMultiple: Boolean,
    onRequestManagePermission: () -> Unit,
    isManagePermissionAllowed: Boolean,
    selectedAlbumIndex: Long,
    onSearchingChange: (Boolean) -> Unit,
    onPicked: (List<Uri>) -> Unit
) {
    val albumsState by component.albumsState.collectAsState()
    val mediaState by component.mediaState.collectAsState()
    val selectedMedia = component.selectedMedia

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
                                EnhancedBadge(
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
        val showLoading = (mediaState.isLoading || filteredMediaState.isLoading) && !isHaveNoData

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
            visible = !mediaState.isLoading && !isHaveNoData,
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
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = stringResource(R.string.close),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            },
                            shape = ShapeDefaults.circle
                        )
                    } else {
                        EnhancedIconButton(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier
                                .padding(bottom = 6.dp)
                                .size(44.dp),
                            onClick = {
                                onSearchingChange(true)
                                filterMedia()
                            },
                            shape = MaterialStarShape,
                            pressedShape = MaterialStarShape
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