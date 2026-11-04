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

package com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.EMOJI_KITCHEN_HOME_URL
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.FamilyStar
import com.t8rin.imagetoolbox.core.resources.icons.Link
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAutoCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealValue
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.screenLogic.EmojiKitchenComponent
import kotlinx.coroutines.launch

@Composable
internal fun EmojiKitchenCollectionInfoCard(
    component: EmojiKitchenComponent
) {
    val collection = component.collectionState.collection ?: return
    val enabled = component.collectionState.progress == null && !component.isSaving
    val revealState = rememberRevealState()
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    var showDelete by rememberSaveable { mutableStateOf(false) }

    SwipeToReveal(
        state = revealState,
        enableSwipe = enabled,
        modifier = Modifier.fillMaxWidth(),
        directions = setOf(
            RevealDirection.StartToEnd,
            RevealDirection.EndToStart
        ),
        revealedContentStart = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .container(
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(0.5f),
                        shape = ShapeDefaults.large,
                        autoShadowElevation = 0.dp,
                        resultPadding = 0.dp
                    )
                    .hapticsClickable(enabled = enabled) {
                        scope.launch {
                            revealState.animateTo(RevealValue.Default)
                        }
                        uriHandler.openUri(EMOJI_KITCHEN_HOME_URL)
                    }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(start = 8.dp)
                        .align(Alignment.CenterStart)
                )
            }
        },
        revealedContentEnd = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .container(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = ShapeDefaults.large,
                        autoShadowElevation = 0.dp,
                        resultPadding = 0.dp
                    )
                    .hapticsClickable(enabled = enabled) {
                        scope.launch {
                            revealState.animateTo(RevealValue.Default)
                        }
                        showDelete = true
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(end = 8.dp)
                        .align(Alignment.CenterEnd)
                )
            }
        },
        swipeableContent = {
            PreferenceItemOverload(
                title = stringResource(R.string.emoji_kitchen_collection),
                subtitle = stringResource(
                    R.string.emoji_kitchen_collection_stats,
                    collection.emojis.size,
                    collection.combinationCount
                ),
                enabled = enabled,
                shape = ShapeDefaults.large,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.fillMaxWidth(),
                startIcon = {
                    Icon(
                        imageVector = Icons.TwoTone.FamilyStar,
                        contentDescription = null
                    )
                },
                endIcon = if (component.collectionState.progress != null) {
                    {
                        EnhancedAutoCircularProgressIndicator(
                            progress = { 0f },
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                } else null
            )
        }
    )

    EnhancedAlertDialog(
        visible = showDelete,
        onDismissRequest = { showDelete = false },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(R.string.emoji_kitchen_delete_title)
            )
        },
        text = {
            Text(
                text = stringResource(
                    R.string.emoji_kitchen_delete_message,
                    humanFileSize(collection.size)
                )
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    component.deleteCollection()
                    showDelete = false
                }
            ) {
                Text(
                    text = stringResource(R.string.delete)
                )
            }
        },
        dismissButton = {
            EnhancedButton(
                onClick = { showDelete = false },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = stringResource(R.string.cancel)
                )
            }
        }
    )
}