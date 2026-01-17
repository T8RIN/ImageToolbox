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

@file:Suppress("KotlinConstantConditions")

package com.t8rin.imagetoolbox.feature.erase_background.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.DownloadForOffline
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.domain.utils.Flavor
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAutoCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.erase_background.domain.model.BgModelType
import com.t8rin.neural_tools.DownloadProgress
import com.t8rin.neural_tools.bgremover.BgRemover
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@Composable
fun AutoEraseBackgroundCard(
    modifier: Modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
    onClick: (BgModelType) -> Unit,
    onReset: () -> Unit
) {
    var selectedModel by rememberSaveable {
        mutableStateOf(flavoredEntries.first())
    }
    var downloadJob by remember {
        mutableStateOf<Job?>(null)
    }

    val downloadedModelsRaw by BgRemover.downloadedModels.collectAsStateWithLifecycle()
    val downloadedModels by remember(downloadedModelsRaw) {
        derivedStateOf {
            downloadedModelsRaw.mapNotNull { it.toDomain() }
        }
    }

    val downloadProgresses = remember(downloadedModels) {
        mutableStateMapOf<BgModelType, DownloadProgress>()
    }

    LaunchedEffect(Unit) {
        flavoredEntries.forEach {
            BgRemover.getRemover(it.toLib()).checkModel()
            delay(100)
        }
    }

    LaunchedEffect(downloadedModels) {
        if (!downloadedModels.contains(selectedModel)) {
            selectedModel = BgModelType.U2NetP
        }
    }

    val scope = retain { CoroutineScope(Dispatchers.IO) }

    Column(
        Modifier
            .then(modifier)
            .container(
                resultPadding = 8.dp,
                shape = ShapeDefaults.extraLarge
            )
    ) {
        if (flavoredEntries.size > 1) {
            EnhancedButtonGroup(
                modifier = Modifier.fillMaxWidth(),
                entries = flavoredEntries,
                value = selectedModel,
                title = null,
                onValueChange = { type ->
                    if (downloadJob == null) {
                        if (selectedModel != type) {
                            BgRemover.getRemover(selectedModel.toLib()).close()
                        }

                        selectedModel = type

                        if (type in downloadedModels) return@EnhancedButtonGroup

                        downloadJob?.cancel()
                        downloadJob = scope.launch {
                            BgRemover.downloadModel(type.toLib())
                                .onStart {
                                    downloadProgresses[type] = DownloadProgress(
                                        currentPercent = 0f,
                                        currentTotalSize = 0
                                    )
                                }
                                .onCompletion {
                                    downloadProgresses.remove(type)
                                    downloadJob = null
                                    delay(100)
                                    BgRemover.getRemover(type.toLib()).checkModel()
                                }
                                .catch {
                                    selectedModel = BgModelType.U2NetP
                                    downloadProgresses.remove(type)
                                    downloadJob = null
                                }
                                .collect {
                                    downloadProgresses[type] = it
                                }
                        }
                    }
                },
                itemContent = { type ->
                    if (type == BgModelType.MlKit) {
                        Text(
                            text = type.title
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = type.title
                            )

                            AnimatedVisibility(type !in downloadedModels) {
                                downloadProgresses[type]?.let { progress ->
                                    EnhancedAutoCircularProgressIndicator(
                                        progress = { progress.currentPercent },
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .size(24.dp),
                                        trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                                        strokeWidth = 3.dp
                                    )
                                } ?: Icon(
                                    imageVector = Icons.Rounded.DownloadForOffline,
                                    contentDescription = null,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                },
                isScrollable = true,
                contentPadding = PaddingValues(0.dp),
                activeButtonColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Row(
            modifier = Modifier
                .container(
                    resultPadding = 0.dp,
                    color = MaterialTheme.colorScheme.mixedContainer.copy(0.7f)
                )
                .hapticsClickable(
                    onClick = { onClick(selectedModel) }
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(id = R.string.auto_erase_background),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onMixedContainer
            )
            Icon(
                imageVector = Icons.Rounded.AutoFixHigh,
                contentDescription = stringResource(R.string.auto_erase_background),
                tint = MaterialTheme.colorScheme.onMixedContainer
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedButton(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.2f),
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            onClick = onReset,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.default,
            isShadowClip = true
        ) {
            Icon(
                imageVector = Icons.Rounded.SettingsBackupRestore,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(id = R.string.restore_image))
        }
    }
}

private val flavoredEntries: List<BgModelType> = BgModelType.entries.let {
    if (Flavor.isFoss()) it.toggle(BgModelType.MlKit)
    else it
}

private fun BgModelType.toLib(): BgRemover.Type = when (this) {
    BgModelType.MlKit,
    BgModelType.U2NetP -> BgRemover.Type.U2NetP

    BgModelType.U2Net -> BgRemover.Type.U2Net
    BgModelType.RMBG -> BgRemover.Type.RMBG1_4
    BgModelType.InSPyReNet -> BgRemover.Type.InSPyReNet
    BgModelType.BiRefNetTiny -> BgRemover.Type.BiRefNetTiny
    BgModelType.ISNet -> BgRemover.Type.ISNet
}

private fun BgRemover.Type.toDomain(): BgModelType? = when (this) {
    BgRemover.Type.U2NetP -> BgModelType.U2NetP
    BgRemover.Type.U2Net -> BgModelType.U2Net
    BgRemover.Type.RMBG1_4 -> BgModelType.RMBG
    BgRemover.Type.InSPyReNet -> BgModelType.InSPyReNet
    BgRemover.Type.BiRefNetTiny -> BgModelType.BiRefNetTiny
    BgRemover.Type.BiRefNet -> null
    BgRemover.Type.ISNet -> BgModelType.ISNet
}