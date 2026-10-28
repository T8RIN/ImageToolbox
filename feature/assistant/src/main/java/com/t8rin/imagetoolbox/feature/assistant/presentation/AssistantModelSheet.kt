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

package com.t8rin.imagetoolbox.feature.assistant.presentation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Cloud
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Download
import com.t8rin.imagetoolbox.core.resources.icons.DownloadDone
import com.t8rin.imagetoolbox.core.resources.icons.DownloadForOffline
import com.t8rin.imagetoolbox.core.resources.icons.File
import com.t8rin.imagetoolbox.core.resources.icons.Link
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonChecked
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonUnchecked
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCancellableCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealValue
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.assistant.presentation.screenLogic.AssistantComponent
import com.t8rin.neural_tools.assistant.AssistantModels
import kotlinx.coroutines.launch

@Composable
internal fun AssistantModelSheet(
    component: AssistantComponent,
    visible: Boolean,
    onDismiss: (Boolean) -> Unit
) {
    var deleteModel by remember { mutableStateOf<AssistantModel?>(null) }
    val (downloaded, available) = AssistantModel.entries.partition { variant ->
        component.installations.any { it.variant == variant && it.installed }
    }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = {
            TitleItem(
                text = stringResource(R.string.models),
                icon = Icons.Outlined.Neurology
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { onDismiss(false) }
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 16.dp, top = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            if (downloaded.isNotEmpty()) {
                item {
                    TitleItem(
                        text = stringResource(R.string.downloaded_models),
                        icon = Icons.Rounded.DownloadDone,
                        endContent = {
                            EnhancedBadge(containerColor = MaterialTheme.colorScheme.tertiary) {
                                Text(humanFileSize(component.installations.sumOf { it.installedSize }))
                            }
                        }
                    )
                }
            }
            itemsIndexed(downloaded, key = { _, variant -> variant.id }) { index, variant ->
                AssistantModelItem(
                    component = component,
                    variant = variant,
                    index = index,
                    count = downloaded.size,
                    onDismiss = onDismiss,
                    onDelete = { deleteModel = variant }
                )
            }
            if (available.isNotEmpty()) {
                item {
                    TitleItem(
                        text = stringResource(R.string.available_models),
                        icon = Icons.Rounded.Download
                    )
                }
            }
            itemsIndexed(available, key = { _, variant -> variant.id }) { index, variant ->
                AssistantModelItem(
                    component = component,
                    variant = variant,
                    index = index,
                    count = available.size,
                    onDismiss = onDismiss,
                    onDelete = { deleteModel = variant }
                )
            }
        }
    }

    EnhancedAlertDialog(
        visible = deleteModel != null,
        onDismissRequest = { deleteModel = null },
        icon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
        title = { Text(stringResource(R.string.delete)) },
        text = {
            Text(
                stringResource(
                    R.string.delete_model_sub,
                    deleteModel?.displayName.orEmpty()
                )
            )
        },
        confirmButton = {
            EnhancedButton(onClick = {
                deleteModel?.let(component::deleteModel)
                deleteModel = null
            }) { Text(stringResource(R.string.confirm)) }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { deleteModel = null }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun AssistantModelItem(
    component: AssistantComponent,
    variant: AssistantModel,
    index: Int,
    count: Int,
    onDismiss: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val state = component.installations.firstOrNull { it.variant == variant }
    val progress = state?.progress
    val installed = state?.installed == true
    val checking = (component.isCheckingModels &&
            (state == null || state.phase == AssistantModels.Phase.Missing)) ||
            (state?.phase == AssistantModels.Phase.Verifying && state.progress == null)
    val busy = component.isGenerating
    val selectable = component.enabled && !busy && !checking && installed && progress == null
    val downloadable = component.enabled && !component.isLoading && !checking &&
            !installed && progress == null && AssistantModels.isSupported()
    val canDelete = installed && !busy && !checking && progress == null
    val selected = installed && component.selectedModel == variant
    val revealState = rememberRevealState()
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()
    val shape = ShapeDefaults.byIndex(index, count, forceDefault = isDragged)

    SwipeToReveal(
        state = revealState,
        interactionSource = interactionSource,
        directions = if (canDelete) {
            setOf(RevealDirection.StartToEnd, RevealDirection.EndToStart)
        } else setOf(RevealDirection.StartToEnd),
        revealedContentStart = {
            val uriHandler = LocalUriHandler.current
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .container(
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(0.5f),
                        shape = shape,
                        autoShadowElevation = 0.dp,
                        resultPadding = 0.dp
                    )
                    .hapticsClickable {
                        scope.launch { revealState.animateTo(RevealValue.Default) }
                        uriHandler.openUri(variant.sourceUrl)
                    }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Link,
                    contentDescription = stringResource(R.string.website),
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
                        shape = shape,
                        autoShadowElevation = 0.dp,
                        resultPadding = 0.dp
                    )
                    .hapticsClickable(enabled = canDelete) {
                        scope.launch { revealState.animateTo(RevealValue.Default) }
                        onDelete()
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete),
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
                modifier = Modifier.fillMaxWidth(),
                title = assistantModelTitle(variant),
                subtitle = variant.displayName,
                shape = shape,
                containerColor = animateColorAsState(
                    if (selected) MaterialTheme.colorScheme.mixedContainer.copy(0.8f)
                    else EnhancedBottomSheetDefaults.contentContainerColor
                ).value,
                onClick = when {
                    selectable -> {
                        {
                            component.selectModel(variant)
                            onDismiss(false)
                        }
                    }

                    downloadable -> {
                        { component.downloadModel(variant) }
                    }

                    else -> null
                },
                onLongClick = {
                    scope.launch {
                        revealState.animateTo(
                            if (canDelete) RevealValue.FullyRevealedStart else RevealValue.FullyRevealedEnd
                        )
                    }
                },
                endIcon = {
                    when {
                        checking -> EnhancedCircularProgressIndicator(modifier = Modifier.size(24.dp))
                        progress != null -> {
                            Row(
                                modifier = Modifier.container(
                                    shape = ShapeDefaults.circle,
                                    color = MaterialTheme.colorScheme.surface,
                                    resultPadding = 8.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = humanFileSize(
                                        ((state.downloadSize ?: 0L) * progress).toLong()
                                    ),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                EnhancedCancellableCircularProgressIndicator(
                                    progress = {
                                        if (state.phase == AssistantModels.Phase.Verifying) 0f
                                        else progress.coerceIn(0f, 1f)
                                    },
                                    modifier = Modifier.size(24.dp),
                                    trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                                    strokeWidth = 3.dp,
                                    onCancel = { component.cancelDownload(variant) }
                                )
                            }
                        }

                        else -> Icon(
                            imageVector = when {
                                selected -> Icons.Rounded.RadioButtonChecked
                                installed -> Icons.Rounded.RadioButtonUnchecked
                                else -> Icons.Rounded.DownloadForOffline
                            },
                            contentDescription = null
                        )
                    }
                },
                placeBottomContentInside = true,
                bottomContent = {
                    FlowRow(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        state?.downloadSize?.let { size ->
                            AssistantModelSizeBadge(size, installed, selected)
                        }
                    }
                }
            )
        }
    )
}

@Composable
private fun AssistantModelSizeBadge(size: Long, installed: Boolean, selected: Boolean) {
    Row(
        modifier = Modifier
            .height(22.dp)
            .container(
                color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                shape = ShapeDefaults.circle,
                resultPadding = 0.dp
            )
            .padding(start = 4.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val color =
            if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (installed) Icons.Rounded.File else Icons.Rounded.Cloud,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(text = humanFileSize(size), color = color, style = MaterialTheme.typography.labelSmall)
    }
}
