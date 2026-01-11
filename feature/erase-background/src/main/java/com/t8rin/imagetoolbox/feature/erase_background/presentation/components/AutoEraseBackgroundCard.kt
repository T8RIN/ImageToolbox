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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResourcesDownloadProgress
import com.t8rin.imagetoolbox.core.domain.utils.Flavor
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.erase_background.domain.model.ModelType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@Composable
fun AutoEraseBackgroundCard(
    modifier: Modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
    onClick: (ModelType) -> Unit,
    onReset: () -> Unit
) {
    var selectedModel by rememberSaveable {
        mutableStateOf(flavoredEntries.first())
    }
    var downloadJob by remember {
        mutableStateOf<Job?>(null)
    }
    var downloadProgress by remember(RMBGLoader.isDownloaded) {
        mutableStateOf<RemoteResourcesDownloadProgress?>(null)
    }

    LaunchedEffect(RMBGLoader.isDownloaded) {
        if (!RMBGLoader.isDownloaded && selectedModel == ModelType.RMBG) {
            selectedModel = ModelType.U2Net
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
                        selectedModel = type

                        if (type == ModelType.RMBG && !RMBGLoader.isDownloaded) {
                            downloadJob?.cancel()
                            downloadJob = scope.launch {
                                RMBGLoader.download()
                                    .onStart {
                                        downloadProgress = RemoteResourcesDownloadProgress(
                                            currentPercent = 0f,
                                            currentTotalSize = 0
                                        )
                                    }
                                    .onCompletion {
                                        downloadProgress = null
                                        downloadJob = null
                                    }
                                    .catch {
                                        selectedModel = ModelType.U2Net
                                        downloadProgress = null
                                        downloadJob = null
                                    }
                                    .collect {
                                        downloadProgress = it
                                    }
                            }
                        }
                    }
                },
                itemContent = {
                    when (it) {
                        ModelType.RMBG -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(it.name)

                                AnimatedVisibility(!RMBGLoader.isDownloaded) {
                                    downloadProgress?.let { progress ->
                                        EnhancedCircularProgressIndicator(
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

                        else -> Text(it.name)
                    }
                },
                isScrollable = false,
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

private val flavoredEntries: List<ModelType> = ModelType.entries.let {
    if (Flavor.isFoss()) it.toggle(ModelType.MlKit)
    else it
}