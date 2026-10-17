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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.ImageSearch
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.resources.icons.PhotoLibrary
import com.t8rin.imagetoolbox.core.resources.icons.RestartAlt
import com.t8rin.imagetoolbox.core.resources.icons.Schedule
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.requestPermissions
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalComponentActivity
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCancellableCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent

@Composable
internal fun MediaSemanticSearchControls(
    component: MediaPickerComponent
) = LocalContentColor.ProvidesValue(MaterialTheme.colorScheme.onSurface) {
    val enabled by component.isSemanticSearchEnabled.collectAsStateWithLifecycle()
    val autoIndexing = component.settingsState.isSemanticSearchEnabled
    val context = LocalComponentActivity.current
    val focusManager = LocalFocusManager.current
    val state by component.semanticSearchState.collectAsStateWithLifecycle()
    val searching by component.isSemanticSearching.collectAsStateWithLifecycle()
    val searchError by component.semanticSearchError.collectAsStateWithLifecycle()
    val error = searchError ?: state.error
    val busy = state.isDownloading || state.isIndexing || state.isQueued
    val description = stringResource(
        if (searching) R.string.gallery_search_searching else R.string.gallery_search_settings
    )
    var showOptions by rememberSaveable { mutableStateOf(false) }
    var showClearConfirmation by rememberSaveable { mutableStateOf(false) }
    val downloadProgress by rememberUpdatedState(state.downloadProgress)
    val cancelDownload by rememberUpdatedState { component.setSemanticSearchAutoIndexing(false) }
    val downloadContent: @Composable () -> Unit = remember {
        {
            EnhancedCancellableCircularProgressIndicator(
                progress = { downloadProgress },
                modifier = Modifier.size(24.dp),
                trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                strokeWidth = 3.dp,
                onCancel = { cancelDownload() }
            )
        }
    }

    EnhancedIconButton(
        onClick = {
            focusManager.clearFocus()
            showOptions = true
        },
        containerColor = if (enabled) MaterialTheme.colorScheme.secondaryContainer
        else Color.Transparent,
        modifier = Modifier
            .padding(end = 4.dp)
            .semantics { contentDescription = description }
    ) {
        if (searching || busy) {
            EnhancedCircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = if (enabled) Icons.Rounded.ImageSearch else Icons.Outlined.ImageSearch,
                contentDescription = null,
                tint = if (error != null && enabled) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }

    EnhancedModalBottomSheet(
        visible = showOptions,
        onDismiss = { showOptions = it },
        title = {
            TitleItem(
                text = stringResource(R.string.gallery_search_settings),
                icon = Icons.Rounded.ImageSearch
            )
        },
        confirmButton = {
            EnhancedIconButton(
                onClick = { showClearConfirmation = true },
                containerColor = MaterialTheme.colorScheme.errorContainer,
                enabled = !state.isDownloading && (state.modelsReady || state.indexedImages > 0 || busy)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.gallery_search_clear)
                )
            }
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { showOptions = false }
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .enhancedVerticalScroll(rememberScrollState())
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PreferenceRowSwitch(
                title = stringResource(R.string.gallery_search_auto),
                subtitle = stringResource(R.string.gallery_search_description) + if (!state.modelsReady) {
                    "\n" + stringResource(R.string.gallery_search_download_hint)
                } else "",
                checked = autoIndexing,
                onClick = component::setSemanticSearchAutoIndexing,
                startIcon = Icons.Outlined.Neurology,
                contentInsteadOfSwitch = downloadContent.takeIf { state.isDownloading },
                modifier = Modifier.fillMaxWidth()
            )
            if (state.modelsReady && ((autoIndexing && busy) || state.indexedImages > 0)) {
                PreferenceItem(
                    title = when {
                        autoIndexing && state.isIndexing -> stringResource(
                            R.string.gallery_search_indexing,
                            state.processedImages,
                            state.totalImages
                        )

                        autoIndexing && state.isQueued -> stringResource(R.string.gallery_search_queued)
                        else -> stringResource(R.string.gallery_search_indexed, state.indexedImages)
                    },
                    animateTitle = false,
                    startIcon = when {
                        autoIndexing && state.isQueued -> Icons.Rounded.Schedule
                        else -> Icons.Rounded.PhotoLibrary
                    },
                    placeBottomContentInside = true,
                    bottomContent = if (autoIndexing && busy) {
                        {
                            SearchPreparationProgress(
                                when {
                                    state.isIndexing -> state.processedImages.toFloat() /
                                            state.totalImages.coerceAtLeast(1)

                                    else -> null
                                }
                            )
                        }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (autoIndexing && state.isWaitingForPermission) {
                PreferenceItem(
                    title = stringResource(R.string.gallery_search_permission),
                    startIcon = Icons.Rounded.PhotoLibrary,
                    onClick = {
                        context.requestPermissions(
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> listOf(
                                    Manifest.permission.READ_MEDIA_IMAGES,
                                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                                )

                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                                    Manifest.permission.READ_MEDIA_IMAGES
                                )

                                else -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            error?.let {
                PreferenceItem(
                    title = stringResource(R.string.try_again),
                    subtitle = it,
                    startIcon = Icons.Rounded.RestartAlt,
                    onClick = {
                        if (autoIndexing) component.retrySemanticSearch()
                        else component.setSemanticSearchAutoIndexing(true)
                    },
                    enabled = !state.isDownloading && !state.isIndexing,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    EnhancedAlertDialog(
        visible = showClearConfirmation,
        onDismissRequest = { showClearConfirmation = false },
        icon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
        title = { Text(stringResource(R.string.gallery_search_clear)) },
        text = { Text(stringResource(R.string.gallery_search_clear_description)) },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.error,
                onClick = {
                    showClearConfirmation = false
                    component.clearSemanticSearch()
                }
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { showClearConfirmation = false }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun SearchPreparationProgress(progress: Float?) {
    val modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
    if (progress == null) {
        LinearWavyProgressIndicator(modifier = modifier)
    } else {
        LinearWavyProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = modifier
        )
    }
}