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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResourcesDownloadProgress
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggleByClass
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getStringLocalized
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import java.util.Locale

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

    var typeFilters by rememberSaveable(
        saver = listSaver(
            save = { state -> state.value.map { it.name } },
            restore = { list ->
                mutableStateOf(list.map { NeuralModel.Type.valueOf(it) })
            }
        )
    ) {
        mutableStateOf<List<NeuralModel.Type>>(emptyList())
    }

    var speedFilters by rememberSaveable(
        saver = listSaver(
            save = { state ->
                state.value.map { speed ->
                    NeuralModel.Speed.entries.indexOfFirst { it::class.isInstance(speed) }
                }
            },
            restore = { list ->
                mutableStateOf(
                    list.map { NeuralModel.Speed.entries[it] }
                )
            }
        )
    ) {
        mutableStateOf<List<NeuralModel.Speed>>(emptyList())
    }

    var keywordFilter by rememberSaveable {
        mutableStateOf("")
    }

    EnhancedModalBottomSheet(
        visible = showDetails,
        onDismiss = {
            showDetails = it
        },
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                var showPopup by remember {
                    mutableStateOf(false)
                }

                EnhancedIconButton(
                    onClick = {
                        showPopup = true
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
                            Badge()
                        }
                    }
                }

                EnhancedModalBottomSheet(
                    visible = showPopup,
                    onDismiss = {
                        showPopup = it
                    },
                    confirmButton = {
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = {
                                showPopup = false
                            }
                        ) {
                            Text(stringResource(R.string.close))
                        }
                    },
                    title = {
                        TitleItem(
                            text = stringResource(id = R.string.filter),
                            icon = Icons.Outlined.FilterAlt
                        )
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                            .clearFocusOnTap(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .container(
                                    shape = MaterialTheme.shapes.large,
                                    resultPadding = 8.dp,
                                    color = EnhancedBottomSheetDefaults.contentContainerColor
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TitleItem(
                                text = stringResource(R.string.type),
                                modifier = Modifier
                                    .padding(
                                        start = 4.dp,
                                        top = 4.dp,
                                        end = 4.dp
                                    )
                            )
                            Spacer(Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = 4.dp,
                                    alignment = Alignment.CenterHorizontally
                                ),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                NeuralModel.Type.entries.forEach { type ->
                                    NeuralModelTypeBadge(
                                        type = type,
                                        isInverted = type in typeFilters,
                                        onClick = {
                                            typeFilters = typeFilters.toggle(type)
                                        },
                                        height = 32.dp,
                                        endPadding = 10.dp
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .container(
                                    shape = MaterialTheme.shapes.large,
                                    resultPadding = 8.dp,
                                    color = EnhancedBottomSheetDefaults.contentContainerColor
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TitleItem(
                                text = stringResource(R.string.speed),
                                modifier = Modifier
                                    .padding(
                                        start = 4.dp,
                                        top = 4.dp,
                                        end = 4.dp
                                    )
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                NeuralModel.Speed.entries.forEach { speed ->
                                    NeuralModelSpeedBadge(
                                        speed = speed,
                                        isInverted = speedFilters.any { it::class.isInstance(speed) },
                                        onClick = {
                                            speedFilters = speedFilters.toggleByClass(speed)
                                        },
                                        height = 32.dp,
                                        endPadding = 10.dp,
                                        isWeighted = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        RoundedTextField(
                            value = keywordFilter,
                            onValueChange = { keywordFilter = it },
                            label = stringResource(R.string.keyword),
                            modifier = Modifier
                                .fillMaxWidth()
                                .container(
                                    shape = MaterialTheme.shapes.large,
                                    resultPadding = 8.dp,
                                    color = EnhancedBottomSheetDefaults.contentContainerColor
                                ),
                            singleLine = false,
                            endIcon = {
                                AnimatedVisibility(keywordFilter.isNotBlank()) {
                                    EnhancedIconButton(
                                        onClick = { keywordFilter = "" },
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Cancel,
                                            contentDescription = stringResource(R.string.cancel)
                                        )
                                    }
                                }
                            },
                            maxLines = 4
                        )
                    }
                }

                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        showDetails = false
                    }
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
            val filteredDownloadedModels by remember(
                downloadedModels,
                typeFilters,
                speedFilters,
                keywordFilter
            ) {
                derivedStateOf {
                    downloadedModels.filter(
                        typeFilters = typeFilters,
                        speedFilters = speedFilters,
                        keywordFilter = keywordFilter.trim()
                    )
                }
            }
            val filteredNotDownloadedModels by remember(
                notDownloadedModels,
                typeFilters,
                speedFilters,
                keywordFilter
            ) {
                derivedStateOf {
                    notDownloadedModels.filter(
                        typeFilters = typeFilters,
                        speedFilters = speedFilters,
                        keywordFilter = keywordFilter.trim()
                    )
                }
            }

            NeuralModelsColumn(
                selectedModel = value,
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

private fun List<NeuralModel>.filter(
    typeFilters: List<NeuralModel.Type>,
    speedFilters: List<NeuralModel.Speed>,
    keywordFilter: String
): List<NeuralModel> {
    return if (typeFilters.isEmpty() && speedFilters.isEmpty() && keywordFilter.isBlank()) this
    else filter { model ->
        val hasType =
            typeFilters.isEmpty() || model.type == null || model.type in typeFilters
        val hasSpeed =
            speedFilters.isEmpty() || model.speed == null || speedFilters.any {
                it::class.isInstance(model.speed)
            }
        val hasKeyword =
            keywordFilter.isBlank()
                    || model.name.contains(keywordFilter, true)
                    || model.title.contains(keywordFilter, true)
                    || (model.description?.let {
                appContext.getString(model.description)
                    .contains(keywordFilter, true)
                        || appContext.getStringLocalized(model.description, Locale.ENGLISH)
                    .contains(keywordFilter, true)
            } == true)

        hasType && hasSpeed && hasKeyword
    }
}