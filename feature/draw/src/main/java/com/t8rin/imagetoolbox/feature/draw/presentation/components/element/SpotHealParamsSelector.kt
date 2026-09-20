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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.element

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.filters.presentation.utils.LamaLoader
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Check
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.FileImport
import com.t8rin.imagetoolbox.core.resources.icons.Link
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCancellableCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealValue
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.utils.filename
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
        val settingsState = LocalSettingsState.current
        val scope = retain { CoroutineScope(Dispatchers.IO) }
        val uiScope = rememberCoroutineScope()
        val uriHandler = LocalUriHandler.current
        val revealState = rememberRevealState()
        val simpleSettingsInteractor = LocalSimpleSettingsInteractor.current
        var downloadJob by retain {
            mutableStateOf<Job?>(null)
        }
        var modelJob by remember { mutableStateOf<Job?>(null) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var downloadProgress by remember(LamaLoader.isDownloaded) {
            mutableStateOf<DownloadProgress?>(null)
        }
        var useLama by remember(settingsState.spotHealMode) {
            mutableStateOf(settingsState.spotHealMode == 1)
        }
        val filePicker = rememberFilePicker { uri: Uri ->
            if (uri.filename()?.endsWith(".onnx", ignoreCase = true) != true) {
                AppToastHost.showFailureToast(R.string.only_lama_onnx_model)
            } else if (modelJob == null && downloadJob == null) {
                modelJob = scope.launch {
                    try {
                        LamaLoader.importModel(uri)
                        useLama = true
                        simpleSettingsInteractor.setSpotHealMode(1)
                        AppToastHost.showToast(
                            message = R.string.model_successfully_imported,
                            icon = Icons.Rounded.Check
                        )
                    } catch (error: Exception) {
                        AppToastHost.showFailureToast(error)
                    } finally {
                        modelJob = null
                    }
                }
            }
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

        EnhancedAlertDialog(
            visible = showDeleteDialog,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.delete_lama_model_sub)) },
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                EnhancedButton(
                    onClick = {
                        showDeleteDialog = false
                        if (modelJob == null && downloadJob == null) {
                            modelJob = scope.launch {
                                try {
                                    LamaLoader.deleteModel()
                                    useLama = false
                                    simpleSettingsInteractor.setSpotHealMode(0)
                                } catch (error: Exception) {
                                    AppToastHost.showFailureToast(error)
                                } finally {
                                    modelJob = null
                                }
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )

        SwipeToReveal(
            state = revealState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            directions = setOf(
                RevealDirection.StartToEnd,
                RevealDirection.EndToStart
            ),
            revealedContentStart = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .container(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(0.5f),
                            shape = ShapeDefaults.default,
                            autoShadowElevation = 0.dp,
                            resultPadding = 0.dp
                        )
                        .hapticsClickable {
                            uiScope.launch { revealState.animateTo(RevealValue.Default) }
                            uriHandler.openUri(LamaLoader.modelPointerLink)
                        }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Link,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(16.dp)
                            .padding(start = 8.dp)
                            .align(Alignment.CenterStart),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            },
            revealedContentEnd = {
                val isDownloaded = LamaLoader.isDownloaded
                Box(
                    Modifier
                        .fillMaxSize()
                        .container(
                            color = if (isDownloaded) MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.primaryContainer,
                            shape = ShapeDefaults.default,
                            autoShadowElevation = 0.dp,
                            resultPadding = 0.dp
                        )
                        .hapticsClickable {
                            uiScope.launch { revealState.animateTo(RevealValue.Default) }
                            if (modelJob == null && downloadJob == null) {
                                if (isDownloaded) showDeleteDialog = true
                                else filePicker.pickFile()
                            }
                        }
                ) {
                    Icon(
                        imageVector = if (isDownloaded) Icons.Outlined.Delete else Icons.Outlined.FileImport,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(16.dp)
                            .padding(end = 8.dp)
                            .align(Alignment.CenterEnd),
                        tint = if (isDownloaded) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            },
            swipeableContent = {
                PreferenceRowSwitch(
                    title = stringResource(R.string.generative_inpaint),
                    subtitle = stringResource(
                        if (LamaLoader.isDownloaded) R.string.generative_inpaint_ready_sub
                        else R.string.generative_inpaint_sub
                    ),
                    checked = useLama,
                    onClick = { new ->
                        if (downloadJob == null && modelJob == null) {
                            useLama = new

                            scope.launch { simpleSettingsInteractor.setSpotHealMode(if (useLama) 1 else 0) }

                            if (useLama && !LamaLoader.isDownloaded) {
                                downloadJob?.cancel()
                                downloadJob = scope.launch {
                                    LamaLoader.download()
                                        .onStart {
                                            downloadProgress = DownloadProgress(
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
                    contentInsteadOfSwitch = if (modelJob != null) {
                        {
                            EnhancedCircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    } else downloadProgress?.let { progress ->
                        {
                            EnhancedCancellableCircularProgressIndicator(
                                progress = { progress.currentPercent },
                                modifier = Modifier.size(24.dp),
                                trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                                strokeWidth = 3.dp,
                                onCancel = {
                                    downloadJob?.cancel()
                                    downloadProgress = null
                                    downloadJob = null
                                    useLama = false
                                    scope.launch {
                                        simpleSettingsInteractor.setSpotHealMode(0)
                                    }
                                }
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = ShapeDefaults.default,
                    modifier = Modifier.fillMaxWidth(),
                    resultModifier = Modifier.padding(16.dp),
                    applyHorizontalPadding = false
                )
            }
        )
    }
}