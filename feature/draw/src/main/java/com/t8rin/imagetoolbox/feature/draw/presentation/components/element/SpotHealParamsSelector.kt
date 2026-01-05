/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResourcesDownloadProgress
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.filters.presentation.utils.LamaLoader
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@Composable
internal fun SpotHealParamsSelector(
    value: DrawMode,
    onValueChange: (DrawMode) -> Unit
) {
    AnimatedVisibility(
        visible = value is DrawMode.SpotHeal,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        val essentials = rememberLocalEssentials()
        val settingsState = LocalSettingsState.current
        val scope = retain { CoroutineScope(Dispatchers.IO) }
        val simpleSettingsInteractor = LocalSimpleSettingsInteractor.current
        var downloadJob by remember {
            mutableStateOf<Job?>(null)
        }
        var downloadProgress by remember(LamaLoader.isDownloaded) {
            mutableStateOf<RemoteResourcesDownloadProgress?>(null)
        }
        var useLama by remember(settingsState.spotHealMode) {
            mutableStateOf(settingsState.spotHealMode == 1)
        }
        LaunchedEffect(LamaLoader.isDownloaded) {
            if (!LamaLoader.isDownloaded) {
                useLama = false
                simpleSettingsInteractor.setSpotHealMode(0)
            }
        }

        LaunchedEffect(useLama) {
            onValueChange(
                DrawMode.SpotHeal(
                    if (useLama) SpotHealMode.LaMa else SpotHealMode.OpenCV
                )
            )
        }

        PreferenceRowSwitch(
            title = stringResource(R.string.generative_inpaint),
            subtitle = stringResource(
                if (LamaLoader.isDownloaded) R.string.generative_inpaint_ready_sub
                else R.string.generative_inpaint_sub
            ),
            checked = useLama,
            onClick = { new ->
                if (downloadJob == null) {
                    useLama = new

                    scope.launch { simpleSettingsInteractor.setSpotHealMode(if (useLama) 1 else 0) }

                    if (useLama && !LamaLoader.isDownloaded) {
                        downloadJob?.cancel()
                        downloadJob = scope.launch {
                            LamaLoader.download()
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
                                    simpleSettingsInteractor.setSpotHealMode(0)
                                    essentials.showFailureToast(it)
                                    useLama = false
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
            contentInsteadOfSwitch = downloadProgress?.let { progress ->
                {
                    EnhancedCircularProgressIndicator(
                        progress = { progress.currentPercent },
                        modifier = Modifier.size(24.dp),
                        trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                        strokeWidth = 3.dp
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = ShapeDefaults.default,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            resultModifier = Modifier.padding(16.dp),
            applyHorizontalPadding = false
        )
    }
}