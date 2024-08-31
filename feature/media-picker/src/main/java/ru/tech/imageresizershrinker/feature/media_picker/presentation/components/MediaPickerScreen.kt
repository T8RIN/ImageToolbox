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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.twotone.ImageNotSupported
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.Black
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButtonType
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.AllowedMedia
import ru.tech.imageresizershrinker.feature.media_picker.presentation.viewModel.MediaPickerViewModel

@Composable
fun MediaPickerScreen(
    allowedMedia: AllowedMedia,
    allowSelection: Boolean,
    viewModel: MediaPickerViewModel,
    sendMediaAsResult: (List<Uri>) -> Unit,
    onRequestManagePermission: () -> Unit,
    isManagePermissionAllowed: Boolean
) {
    val scope = rememberCoroutineScope()
    var selectedAlbumIndex by rememberSaveable { mutableLongStateOf(-1) }
    val selectedMedia = viewModel.selectedMedia

    val albumsState by viewModel.albumsState.collectAsState()
    val mediaState by viewModel.mediaState.collectAsState()
    val chipColors = InputChipDefaults.inputChipColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val haptics = LocalHapticFeedback.current

    Column {
        val layoutDirection = LocalLayoutDirection.current

        AnimatedVisibility(
            visible = albumsState.albums.size > 1
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawHorizontalStroke()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
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
                )
            ) {
                items(
                    items = albumsState.albums,
                    key = { it.toString() }
                ) {
                    val selected = selectedAlbumIndex == it.id
                    InputChip(
                        onClick = {
                            haptics.performHapticFeedback(
                                HapticFeedbackType.LongPress
                            )
                            selectedAlbumIndex = it.id
                            viewModel.getAlbum(selectedAlbumIndex)
                        },
                        colors = chipColors,
                        shape = RoundedCornerShape(16.dp),
                        label = {
                            val title =
                                if (it.id == -1L) stringResource(R.string.all) else it.label
                            Text(text = title)
                        },
                        selected = selected,
                        border = null
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
                val isButtonVisible = !allowSelection || selectedMedia.isNotEmpty()
                MediaPickerGrid(
                    state = mediaState,
                    selectedMedia = selectedMedia,
                    allowSelection = allowSelection,
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
                                if (selectedMedia.isNotEmpty() && allowSelection) {
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
            val visible = mediaState.isLoading

            val backgroundColor by animateColorAsState(
                Black.copy(
                    if (mediaState.isLoading && mediaState.media.isNotEmpty()) 0.5f else 0f
                )
            )
            BoxAnimatedVisibility(
                visible = visible,
                modifier = Modifier
                    .fillMaxSize()
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
                    Loading()
                }
            }

            BoxAnimatedVisibility(
                visible = mediaState.media.isEmpty() && !mediaState.isLoading,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
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
                            viewModel.init(allowedMedia)
                        }
                    ) {
                        Text(stringResource(id = R.string.try_again))
                    }
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
    BackHandler(selectedAlbumIndex != -1L) {
        selectedAlbumIndex = -1L
        viewModel.getAlbum(selectedAlbumIndex)
    }
}