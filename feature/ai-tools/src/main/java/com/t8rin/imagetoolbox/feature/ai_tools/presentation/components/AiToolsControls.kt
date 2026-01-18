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

package com.t8rin.imagetoolbox.feature.ai_tools.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormatGroup
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Cube
import com.t8rin.imagetoolbox.core.resources.icons.Exercise
import com.t8rin.imagetoolbox.core.resources.icons.Stack
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import com.t8rin.imagetoolbox.feature.ai_tools.presentation.screenLogic.AiToolsComponent
import kotlin.math.roundToInt

@Composable
internal fun AiToolsControls(component: AiToolsComponent) {
    val selectedModel by component.selectedModel.collectAsStateWithLifecycle()
    val downloadedModels by component.downloadedModels.collectAsStateWithLifecycle()
    val notDownloadedModels by component.notDownloadedModels.collectAsStateWithLifecycle()
    val occupiedStorageSize by component.occupiedStorageSize.collectAsStateWithLifecycle()
    val isModelChunkable = selectedModel?.isNonChunkable != true
    val isChunkable = isModelChunkable && component.params.enableChunking

    NeuralModelSelector(
        value = selectedModel,
        onSelectModel = component::selectModel,
        onDownloadModel = component::downloadModel,
        onDeleteModel = component::deleteModel,
        onImportModel = component::importModel,
        downloadedModels = downloadedModels,
        notDownloadedModels = notDownloadedModels,
        downloadProgresses = component.downloadProgresses,
        occupiedStorageSize = occupiedStorageSize
    )

    AnimatedVisibility(
        visible = isChunkable,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            AnimatedVisibility(
                visible = selectedModel?.supportsStrength == true,
                modifier = Modifier.fillMaxWidth()
            ) {
                EnhancedSliderItem(
                    value = component.params.strength,
                    internalStateTransformation = { it.roundToInt() },
                    steps = 100,
                    valueRange = 0f..100f,
                    onValueChange = {
                        component.updateParams { copy(strength = it) }
                    },
                    title = stringResource(R.string.strength),
                    icon = Icons.Outlined.Exercise,
                    modifier = Modifier.padding(top = 8.dp),
                    shape = ShapeDefaults.large,
                )
            }

            Spacer(Modifier.height(8.dp))

            val chunkPowers = remember {
                generateSequence(128) { it * 2 }.takeWhile { it <= 2048 }.toList()
            }
            val overlapPowers = remember {
                generateSequence(16) { it * 2 }.takeWhile { it <= 128 }.toList()
            }

            PowerSliderItem(
                label = stringResource(id = R.string.chunk_size),
                icon = Icons.Outlined.Cube,
                value = component.params.chunkSize,
                powers = chunkPowers,
                onValueChange = { component.updateParams { copy(chunkSize = it) } }
            )
            AnimatedVisibility(
                visible = component.params.chunkSize >= 2048,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    InfoContainer(
                        text = stringResource(R.string.large_chunk_warning),
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.4f),
                        contentColor = MaterialTheme.colorScheme.onErrorContainer.copy(0.7f),
                        icon = Icons.Rounded.WarningAmber,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            PowerSliderItem(
                label = stringResource(R.string.overlap_size),
                icon = Icons.Outlined.Stack,
                value = component.params.overlap,
                powers = overlapPowers,
                maxAllowed = component.params.chunkSize,
                onValueChange = { component.updateParams { copy(overlap = it) } }
            )
        }
    }

    Spacer(Modifier.height(8.dp))

    InfoContainer(
        text = if (isChunkable) {
            stringResource(R.string.note_chunk_info, component.params.chunkSize)
        } else if (!isModelChunkable) {
            stringResource(R.string.current_model_not_chunkable)
        } else {
            stringResource(R.string.chunking_disabled)
        },
        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.4f),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.8f)
    )

    Spacer(Modifier.height(8.dp))

    ImageFormatSelector(
        value = component.imageFormat,
        entries = if (selectedModel?.type != NeuralModel.Type.REMOVE_BG) {
            ImageFormatGroup.entries
        } else {
            ImageFormatGroup.alphaContainedEntries
        },
        onValueChange = component::setImageFormat,
        onAutoClick = { component.setImageFormat(null) }
    )
}