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

package com.t8rin.imagetoolbox.feature.erase_background.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AutoFixHigh
import com.t8rin.imagetoolbox.core.resources.icons.DownloadForOffline
import com.t8rin.imagetoolbox.core.resources.icons.SettingsBackupRestore
import com.t8rin.imagetoolbox.core.resources.icons.TouchApp
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCancellableCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.erase_background.domain.model.BgModelType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

@Composable
fun AutoEraseBackgroundCard(
    modifier: Modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
    onClick: (BgModelType) -> Unit,
    onReset: () -> Unit,
    onSelectSubject: (() -> Unit)? = null,
    isSelectingSubject: Boolean = false,
    state: AutoEraseBackgroundCardState? = null
) {
    val cardState = state ?: rememberAutoEraseBackgroundCardState()
    val selectedModel = cardState.selectedModel
    val downloadedModels = cardState.downloadedModels

    LaunchedEffect(cardState) {
        cardState.checkModels()
    }

    Column(
        Modifier
            .then(modifier)
            .container(
                resultPadding = 8.dp,
                shape = ShapeDefaults.extraLarge
            )
    ) {
        if (cardState.models.size > 1) {
            EnhancedButtonGroup(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                entries = cardState.models,
                value = selectedModel,
                title = null,
                onValueChange = cardState::selectModel,
                itemContent = { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = type.title
                        )

                        if (type != BgModelType.MlKit) {
                            AnimatedVisibility(type !in downloadedModels) {
                                cardState.downloadProgresses[type]?.let { progress ->
                                    EnhancedCancellableCircularProgressIndicator(
                                        progress = { progress.currentPercent },
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .size(24.dp),
                                        trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                                        strokeWidth = 3.dp,
                                        onCancel = cardState::cancelDownload
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
                    enabled = !cardState.isDownloading && !isSelectingSubject &&
                            selectedModel in downloadedModels,
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
        Spacer(Modifier.height(4.dp))
        val showPointsButton = selectedModel == BgModelType.MobileSAM &&
                selectedModel in downloadedModels &&
                onSelectSubject != null

        AnimatedVisibility(showPointsButton) {
            EnhancedButton(
                onClick = onSelectSubject ?: {},
                enabled = !isSelectingSubject,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                shape = ShapeDefaults.top,
            ) {
                if (isSelectingSubject) {
                    EnhancedCircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.TouchApp,
                        contentDescription = null
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.sam_select_subject))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        EnhancedButton(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.3f),
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            onClick = onReset,
            modifier = Modifier.fillMaxWidth(),
            shape = if (showPointsButton) ShapeDefaults.bottom else ShapeDefaults.default,
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

@Composable
private fun rememberAutoEraseBackgroundCardState(): AutoEraseBackgroundCardState {
    val scope = retain { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) }
    RetainedEffect(scope) {
        onRetire { scope.cancel() }
    }
    return retain(scope) { AutoEraseBackgroundCardState(scope) }
}