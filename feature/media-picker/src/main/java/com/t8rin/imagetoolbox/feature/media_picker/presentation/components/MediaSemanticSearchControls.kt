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
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.ImageSearch
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.resources.icons.Person
import com.t8rin.imagetoolbox.core.resources.icons.PhotoLibrary
import com.t8rin.imagetoolbox.core.resources.icons.Schedule
import com.t8rin.imagetoolbox.core.resources.icons.Tune
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.ImagePickerMode
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
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
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent

@Composable
internal fun MediaSemanticSearchControls(
    component: MediaPickerComponent,
    onSearchImagePicked: (Uri) -> Unit
) = LocalContentColor.ProvidesValue(MaterialTheme.colorScheme.onSurface) {
    val enabled by component.isSemanticSearchEnabled.collectAsStateWithLifecycle()
    val autoIndexing = component.settingsState.isSemanticSearchEnabled
    val context = LocalComponentActivity.current
    val focusManager = LocalFocusManager.current
    val state by component.semanticSearchState.collectAsStateWithLifecycle()
    val searching by component.isSemanticSearching.collectAsStateWithLifecycle()
    val searchError by component.semanticSearchError.collectAsStateWithLifecycle()
    val error = searchError ?: state.error
    val busy = state.isDownloading || state.isFacesDownloading || state.isIndexing || state.isQueued
    var showOptions by rememberSaveable { mutableStateOf(false) }
    var showClearConfirmation by rememberSaveable { mutableStateOf(false) }
    var showCategories by rememberSaveable { mutableStateOf(false) }
    var showAccuracy by rememberSaveable { mutableStateOf(false) }
    val imagePicker = rememberImagePicker(ImagePickerMode.PhotoPickerSingle) { uris ->
        uris.firstOrNull()?.let {
            onSearchImagePicked(it)
            showOptions = false
        }
    }
    val downloadProgress by rememberUpdatedState(state.downloadProgress)
    val cancelDownload by rememberUpdatedState { component.setSemanticSearchAutoIndexing(false) }
    val downloadContent: @Composable () -> Unit = remember {
        {
            SearchModelDownloadProgress(
                progress = { downloadProgress },
                onCancel = { cancelDownload() }
            )
        }
    }

    val faceDownloadProgress by rememberUpdatedState(state.facesDownloadProgress)
    val cancelFaceDownload by rememberUpdatedState { component.setFaceGrouping(false) }
    val faceDownloadContent: @Composable () -> Unit = remember {
        {
            SearchModelDownloadProgress(
                progress = { faceDownloadProgress },
                onCancel = { cancelFaceDownload() }
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
        modifier = Modifier.padding(end = 4.dp)
    ) {
        if (searching || state.isDownloading || state.isFacesDownloading || (busy && state.indexedImages == 0)) {
            EnhancedCircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp
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
            verticalArrangement = Arrangement.spacedBy(4.dp)
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
                shape = ShapeDefaults.top,
                modifier = Modifier.fillMaxWidth()
            )
            PreferenceRowSwitch(
                title = stringResource(R.string.gallery_people_grouping),
                subtitle = stringResource(R.string.gallery_people_grouping_description),
                checked = component.settingsState.semanticSearchSettings.groupPeople || state.isFacesDownloading,
                enabled = autoIndexing && !state.isDownloading,
                onClick = component::setFaceGrouping,
                startIcon = Icons.Outlined.Person,
                contentInsteadOfSwitch = faceDownloadContent.takeIf { state.isFacesDownloading },
                shape = ShapeDefaults.center,
                modifier = Modifier.fillMaxWidth()
            )
            if (
                (autoIndexing && (state.isQueued || state.isIndexing)) || (state.modelsReady && state.indexedImages > 0)
            ) {
                PreferenceItem(
                    title = when {
                        autoIndexing && state.isIndexing -> stringResource(
                            if (state.isFacesIndexing) R.string.gallery_faces_indexing
                            else R.string.gallery_search_indexing,
                            state.processedImages,
                            state.totalImages
                        )

                        autoIndexing && state.isQueued -> stringResource(
                            if (state.indexedImages > 0) R.string.gallery_search_update_queued
                            else R.string.gallery_search_queued
                        )

                        else -> stringResource(R.string.gallery_search_indexed, state.indexedImages)
                    },
                    animateTitle = false,
                    shape = ShapeDefaults.center,
                    startIcon = when {
                        autoIndexing && state.isQueued -> Icons.Outlined.Schedule
                        else -> Icons.Rounded.PhotoLibrary
                    },
                    placeBottomContentInside = true,
                    bottomContent = if (autoIndexing && busy) {
                        {
                            SearchPreparationProgress(
                                if (state.isIndexing) {
                                    state.processedImages / state.totalImages.coerceAtLeast(1)
                                        .toFloat()
                                } else null
                            )
                        }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (autoIndexing && state.isWaitingForPermission) {
                PreferenceItem(
                    title = stringResource(R.string.gallery_search_permission),
                    shape = ShapeDefaults.center,
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
            PreferenceItem(
                title = stringResource(R.string.gallery_search_similar),
                subtitle = stringResource(
                    when {
                        !autoIndexing -> R.string.gallery_search_similar_enable
                        !state.modelsReady -> R.string.gallery_search_similar_download
                        state.indexedImages == 0 -> R.string.gallery_search_similar_prepare
                        else -> R.string.gallery_search_similar_description
                    }
                ),
                startIcon = Icons.Outlined.ImageSearch,
                enabled = autoIndexing && state.modelsReady && state.indexedImages > 0,
                onClick = {
                    focusManager.clearFocus()
                    imagePicker.pickImage()
                },
                shape = ShapeDefaults.center,
                modifier = Modifier.fillMaxWidth(),
                endIcon = Icons.Rounded.MiniEdit
            )
            PreferenceItem(
                title = stringResource(R.string.gallery_search_manage_categories),
                subtitle = stringResource(R.string.gallery_search_manage_categories_description),
                startIcon = Icons.Outlined.Neurology,
                onClick = { showCategories = true },
                shape = ShapeDefaults.center,
                modifier = Modifier.fillMaxWidth(),
                endIcon = Icons.Rounded.MiniEdit
            )
            PreferenceItem(
                title = stringResource(R.string.gallery_search_accuracy),
                subtitle = stringResource(R.string.gallery_search_accuracy_description),
                startIcon = Icons.Rounded.Tune,
                onClick = { showAccuracy = true },
                shape = ShapeDefaults.bottom,
                modifier = Modifier.fillMaxWidth(),
                endIcon = Icons.Rounded.MiniEdit
            )
        }
    }

    MediaSemanticSearchCategoriesSheet(
        visible = showCategories,
        onDismiss = { showCategories = it },
        component = component
    )
    MediaSemanticSearchAccuracySheet(
        visible = showAccuracy,
        onDismiss = { showAccuracy = it },
        component = component
    )

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

@Composable
private fun SearchModelDownloadProgress(
    progress: () -> Float,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier.size(width = 52.dp, height = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        EnhancedCancellableCircularProgressIndicator(
            progress = progress,
            modifier = Modifier.size(24.dp),
            trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
            strokeWidth = 3.dp,
            onCancel = onCancel
        )
    }
}