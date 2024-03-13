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

package ru.tech.imageresizershrinker.media_picker.presentation.components

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TaskAlt
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.media_picker.domain.AllowedMedia
import ru.tech.imageresizershrinker.media_picker.presentation.viewModel.PickerViewModel

@Composable
fun PickerScreen(
    allowedMedia: AllowedMedia,
    allowSelection: Boolean,
    viewModel: PickerViewModel,
    sendMediaAsResult: (List<Uri>) -> Unit
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

    Column {
        AnimatedVisibility(visible = albumsState.albums.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                contentPadding = PaddingValues(start = 32.dp)
            ) {
                items(
                    items = albumsState.albums,
                    key = { it.toString() }
                ) {
                    val selected = selectedAlbumIndex == it.id
                    InputChip(
                        onClick = {
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
        AnimatedContent(
            targetState = mediaState.media.isNotEmpty(),
            modifier = Modifier.weight(1f)
        ) { haveData ->
            if (haveData) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PickerMediaScreen(
                        state = mediaState,
                        selectedMedia = selectedMedia,
                        allowSelection = allowSelection
                    )
                    androidx.compose.animation.AnimatedVisibility(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(32.dp),
                        visible = !allowSelection || selectedMedia.isNotEmpty(),
                        enter = slideInVertically { it * 2 },
                        exit = slideOutVertically { it * 2 }
                    ) {
                        val enabled = selectedMedia.isNotEmpty()
                        val containerColor by animateColorAsState(
                            targetValue = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            label = "containerColor"
                        )
                        val contentColor by animateColorAsState(
                            targetValue = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            label = "contentColor"
                        )
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
                                    if (allowSelection) {
                                        Text(text = "Add (${selectedMedia.size})")
                                    } else Text(text = "Add")
                                }
                            },
                            containerColor = containerColor,
                            contentColor = contentColor,
                            onClick = {
                                if (enabled) {
                                    scope.launch {
                                        sendMediaAsResult(selectedMedia.map { it.uri })
                                    }
                                }
                            },
                            modifier = Modifier
                                .semantics {
                                    contentDescription = "Add media"
                                }
                        )
                        BackHandler(selectedMedia.isNotEmpty()) {
                            selectedMedia.clear()
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Loading()
                }
            }
        }
    }
    BackHandler(selectedAlbumIndex != -1L) {
        selectedAlbumIndex = -1L
        viewModel.getAlbum(selectedAlbumIndex)
    }
}