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

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResourcesDownloadProgress
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel

@Composable
internal fun NeuralModelSelector(
    value: NeuralModel?,
    onSelectModel: (NeuralModel) -> Unit,
    onDownloadModel: (NeuralModel) -> Unit,
    onDeleteModel: (NeuralModel) -> Unit,
    downloadedModels: List<NeuralModel>,
    notDownloadedModels: List<NeuralModel>,
    onImportModel: (Uri, (SaveResult) -> Unit) -> Unit,
    downloadProgresses: Map<String, RemoteResourcesDownloadProgress>,
    occupiedStorageSize: Long
) {
    var showSelectionSheet by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItem(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.active_model),
        subtitle = value?.title ?: stringResource(R.string.select_one_to_start),
        onClick = { showSelectionSheet = true },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = ShapeDefaults.extraLarge,
        startIcon = Icons.Outlined.Neurology,
        endIcon = Icons.Rounded.MiniEdit,
        placeBottomContentInside = true,
        bottomContent = value?.type?.let { type ->
            {
                FlowRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    NeuralModelTypeBadge(
                        type = type,
                        isInverted = null
                    )

                    value.speed?.let { speed ->
                        NeuralModelSpeedBadge(
                            speed = speed,
                            isInverted = null
                        )
                    }

                    NeuralModelSizeBadge(
                        model = value,
                        isInverted = false
                    )
                }
            }
        }
    )

    NeuralModelSelectionSheet(
        visible = showSelectionSheet,
        onDismiss = { showSelectionSheet = it },
        selectedModel = value,
        onSelectModel = onSelectModel,
        onDownloadModel = onDownloadModel,
        onDeleteModel = onDeleteModel,
        downloadedModels = downloadedModels,
        notDownloadedModels = notDownloadedModels,
        onImportModel = onImportModel,
        downloadProgresses = downloadProgresses,
        occupiedStorageSize = occupiedStorageSize
    )
}

@Preview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(
    isDarkTheme = true,
    keyColor = Color.Blue
) {
    NeuralModelSelector(
        value = NeuralModel.entries.first(),
        onSelectModel = {},
        onDownloadModel = {},
        onDeleteModel = {},
        downloadedModels = emptyList(),
        notDownloadedModels = emptyList(),
        onImportModel = { _, _ -> },
        downloadProgresses = emptyMap(),
        occupiedStorageSize = 0
    )
}