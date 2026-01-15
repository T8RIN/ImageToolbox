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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResourcesDownloadProgress
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
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
    var showDetails by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItem(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.active_model),
        subtitle = value?.title ?: stringResource(R.string.select_one_to_start),
        onClick = {
            showDetails = true
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = ShapeDefaults.extraLarge,
        startIcon = Icons.Outlined.Neurology,
        endIcon = Icons.Rounded.MiniEdit
    )

    EnhancedModalBottomSheet(
        visible = showDetails,
        onDismiss = {
            showDetails = it
        },
        enableBottomContentWeight = true,
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    showDetails = false
                }
            ) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.models),
                icon = Icons.Outlined.Neurology
            )
        },
        sheetContent = {
            NeuralModelsColumn(
                selectedModel = value,
                downloadedModels = downloadedModels,
                notDownloadedModels = notDownloadedModels,
                onSelectModel = onSelectModel,
                onDownloadModel = onDownloadModel,
                onDeleteModel = onDeleteModel,
                onImportModel = onImportModel,
                downloadProgresses = downloadProgresses,
                occupiedStorageSize = occupiedStorageSize
            )
        }
    )
}