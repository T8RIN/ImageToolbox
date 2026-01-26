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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel

@Composable
internal fun NeuralModelSelectionSheet(
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    selectedModel: NeuralModel?,
    onSelectModel: (NeuralModel) -> Unit,
    onDownloadModel: (NeuralModel) -> Unit,
    onDeleteModel: (NeuralModel) -> Unit,
    downloadedModels: List<NeuralModel>,
    notDownloadedModels: List<NeuralModel>,
    onImportModel: (Uri, (SaveResult) -> Unit) -> Unit,
    downloadProgresses: Map<String, DownloadProgress>,
    occupiedStorageSize: Long
) {
    var typeFilters by rememberSaveable(stateSaver = TypeFiltersSaver) {
        mutableStateOf(emptyList())
    }

    var speedFilters by rememberSaveable(stateSaver = SpeedFiltersSaver) {
        mutableStateOf(emptyList())
    }

    var keywordFilter by rememberSaveable {
        mutableStateOf("")
    }

    var showFilterSheet by rememberSaveable {
        mutableStateOf(false)
    }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnhancedIconButton(
                    onClick = {
                        showFilterSheet = true
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Rounded.FilterAlt,
                            contentDescription = "more"
                        )

                        BoxAnimatedVisibility(
                            visible = typeFilters.isNotEmpty() || speedFilters.isNotEmpty() || keywordFilter.isNotBlank(),
                            modifier = Modifier
                                .size(6.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            EnhancedBadge()
                        }
                    }
                }

                NeuralModelFilterSheet(
                    visible = showFilterSheet,
                    onDismiss = { showFilterSheet = it },
                    typeFilters = typeFilters,
                    speedFilters = speedFilters,
                    keywordFilter = keywordFilter,
                    onTypeFiltersChange = { typeFilters = it },
                    onSpeedFiltersChange = { speedFilters = it },
                    onKeywordFilterChange = { keywordFilter = it }
                )

                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { onDismiss(false) }
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.models),
                icon = Icons.Outlined.Neurology
            )
        },
        sheetContent = {
            val (filteredDownloadedModels, filteredNotDownloadedModels) = filteredModels(
                downloadedModels = downloadedModels,
                notDownloadedModels = notDownloadedModels,
                typeFilters = typeFilters,
                speedFilters = speedFilters,
                keywordFilter = keywordFilter
            ).value

            NeuralModelsColumn(
                selectedModel = selectedModel,
                downloadedModels = filteredDownloadedModels,
                notDownloadedModels = filteredNotDownloadedModels,
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