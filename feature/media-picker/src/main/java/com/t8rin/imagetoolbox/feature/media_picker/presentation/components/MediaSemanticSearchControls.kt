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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import com.t8rin.imagetoolbox.core.resources.icons.TextSearch
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
    val groupPeople = component.settingsState.semanticSearchSettings.groupPeople
    val ocrEnabled = component.settingsState.isGalleryOcrEnabled
    val context = LocalComponentActivity.current
    val focusManager = LocalFocusManager.current
    val state by component.semanticSearchState.collectAsStateWithLifecycle()
    val ocrState by component.galleryOcrState.collectAsStateWithLifecycle()
    val searching by component.isSearchingContent.collectAsStateWithLifecycle()
    val busy = state.isDownloading || state.isFacesDownloading || state.isIndexing ||
            state.isQueued || state.isFacesIndexing || state.isFacesQueued
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

    val ocrDownloadProgress by rememberUpdatedState(ocrState.downloadProgress)
    val cancelOcrDownload by rememberUpdatedState { component.setGalleryOcrEnabled(false) }
    val ocrDownloadContent: @Composable () -> Unit = remember {
        {
            SearchModelDownloadProgress(
                progress = { ocrDownloadProgress },
                onCancel = { cancelOcrDownload() }
            )
        }
    }

    EnhancedIconButton(
        onClick = {
            focusManager.clearFocus()
            showOptions = true
        },
        containerColor = if (enabled || ocrEnabled) MaterialTheme.colorScheme.secondaryContainer
        else Color.Transparent,
        modifier = Modifier.padding(end = 4.dp)
    ) {
        if (searching || state.isDownloading || state.isFacesDownloading ||
            busy && state.indexedImages == 0 || ocrEnabled && !ocrState.hasIndexed && ocrState.isPreparing
        ) {
            EnhancedCircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp
            )
        } else {
            Icon(
                imageVector = if (enabled || ocrEnabled) Icons.Rounded.ImageSearch
                else Icons.Outlined.ImageSearch,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
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
            val indexingPhotos = autoIndexing && state.isIndexing
            val indexingFaces = autoIndexing && groupPeople && state.isFacesIndexing
            val photosQueued = autoIndexing && state.isQueued && !indexingPhotos &&
                    !state.isDownloading && !state.isWaitingForPermission
            val facesQueued =
                autoIndexing && groupPeople && state.isFacesQueued && !indexingFaces &&
                        !state.isWaitingForPermission
            PreferenceRowSwitch(
                title = stringResource(R.string.gallery_search_auto),
                subtitle = when {
                    indexingPhotos -> stringResource(
                        R.string.gallery_search_indexing, state.processedImages, state.totalImages
                    )

                    photosQueued -> stringResource(
                        if (state.indexedImages > 0) R.string.gallery_search_update_queued
                        else R.string.gallery_search_queued
                    )

                    else -> stringResource(R.string.gallery_search_description) + if (!state.modelsReady) {
                        "\n" + stringResource(R.string.gallery_search_download_hint)
                    } else ""
                },
                animateSubtitle = !indexingPhotos,
                checked = autoIndexing,
                onClick = component::setSemanticSearchAutoIndexing,
                startIcon = Icons.Outlined.Neurology,
                contentInsteadOfSwitch = downloadContent.takeIf { state.isDownloading },
                additionalContent = {
                    SearchPreparationProgress(
                        visible = indexingPhotos || photosQueued,
                        progress = (state.processedImages / state.totalImages.coerceAtLeast(1)
                            .toFloat())
                            .takeUnless { photosQueued }
                    )
                },
                shape = ShapeDefaults.top,
                modifier = Modifier.fillMaxWidth()
            )
            PreferenceRowSwitch(
                title = stringResource(R.string.gallery_people_grouping),
                subtitle = when {
                    indexingFaces -> stringResource(
                        R.string.gallery_faces_indexing,
                        state.processedFaceImages,
                        state.totalFaceImages
                    )

                    facesQueued -> stringResource(R.string.gallery_faces_queued)
                    else -> stringResource(R.string.gallery_people_grouping_description)
                },
                animateSubtitle = !indexingFaces,
                checked = groupPeople || autoIndexing && state.isFacesDownloading,
                enabled = autoIndexing && !state.isDownloading,
                onClick = component::setFaceGrouping,
                startIcon = Icons.Outlined.Person,
                contentInsteadOfSwitch = faceDownloadContent.takeIf { autoIndexing && state.isFacesDownloading },
                additionalContent = {
                    SearchPreparationProgress(
                        visible = indexingFaces || facesQueued,
                        progress = (state.processedFaceImages / state.totalFaceImages.coerceAtLeast(
                            1
                        ).toFloat())
                            .takeUnless { facesQueued }
                    )
                },
                shape = ShapeDefaults.center,
                modifier = Modifier.fillMaxWidth()
            )
            val ocrIndexing = ocrEnabled && ocrState.isIndexing
            val ocrQueued =
                ocrEnabled && ocrState.isQueued && !ocrIndexing && !ocrState.isWaitingForPermission
            PreferenceRowSwitch(
                title = stringResource(R.string.gallery_ocr),
                subtitle = when {
                    ocrIndexing -> stringResource(
                        R.string.gallery_ocr_progress,
                        ocrState.processedImages,
                        ocrState.totalImages
                    )

                    ocrQueued -> stringResource(R.string.gallery_ocr_pending)
                    else -> stringResource(R.string.gallery_ocr_sub) + if (!ocrState.modelsReady) {
                        "\n" + stringResource(R.string.gallery_ocr_download_hint)
                    } else ""
                },
                animateSubtitle = !ocrIndexing,
                checked = ocrEnabled,
                onClick = component::setGalleryOcrEnabled,
                startIcon = Icons.Outlined.TextSearch,
                contentInsteadOfSwitch = ocrDownloadContent.takeIf { ocrEnabled && ocrState.isDownloading },
                additionalContent = {
                    SearchPreparationProgress(
                        visible = ocrIndexing || ocrQueued,
                        progress = (ocrState.processedImages / ocrState.totalImages.coerceAtLeast(1)
                            .toFloat())
                            .takeUnless { ocrQueued }
                    )
                },
                shape = ShapeDefaults.center,
                modifier = Modifier.fillMaxWidth()
            )
            if (autoIndexing && state.isWaitingForPermission || ocrEnabled && ocrState.isWaitingForPermission) {
                PreferenceItem(
                    title = stringResource(
                        if (autoIndexing && state.isWaitingForPermission) R.string.gallery_search_permission
                        else R.string.gallery_ocr_permission
                    ),
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
private fun SearchPreparationProgress(visible: Boolean, progress: Float?) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
    ) {
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