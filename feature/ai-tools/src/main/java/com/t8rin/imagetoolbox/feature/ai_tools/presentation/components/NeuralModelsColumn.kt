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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.InsertDriveFile
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.DownloadForOffline
import androidx.compose.material.icons.rounded.ModelTraining
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.data.utils.getFilename
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.FileImport
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAutoCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealValue
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.saver.OneTimeEffect
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun NeuralModelsColumn(
    selectedModel: NeuralModel?,
    downloadedModels: List<NeuralModel>,
    notDownloadedModels: List<NeuralModel>,
    onSelectModel: (NeuralModel) -> Unit,
    onDownloadModel: (NeuralModel) -> Unit,
    onDeleteModel: (NeuralModel) -> Unit,
    onImportModel: (Uri, (SaveResult) -> Unit) -> Unit,
    downloadProgresses: Map<String, DownloadProgress>,
    occupiedStorageSize: Long
) {
    val essentials = rememberLocalEssentials()
    val scope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    val filePicker = rememberFilePicker { uri: Uri ->
        val name = uri.getFilename().orEmpty()
        if (name.endsWith(".onnx") || name.endsWith(".ort")) {
            onImportModel(uri) {
                when (it) {
                    SaveResult.Skipped -> {
                        essentials.showToast(
                            message = essentials.getString(R.string.model_already_downloaded),
                            icon = Icons.Outlined.Info
                        )
                    }

                    is SaveResult.Success -> {
                        essentials.showToast(
                            message = essentials.getString(R.string.model_successfully_imported),
                            icon = Icons.Outlined.CheckCircle
                        )
                    }

                    else -> essentials.parseFileSaveResult(it)
                }
            }
        } else {
            essentials.showFailureToast(
                essentials.getString(R.string.only_onnx_models)
            )
        }
    }

    val (importedModels, downloadedModels) = remember(downloadedModels) {
        derivedStateOf {
            downloadedModels.partition { it.isImported }
        }
    }.value

    val scrollToSelected = suspend {
        downloadedModels.indexOf(selectedModel).takeIf {
            it != -1
        }.let {
            listState.animateScrollToItem(it ?: 0)
        }
    }

    var isSelectRequested by remember {
        mutableStateOf(false)
    }

    OneTimeEffect {
        scrollToSelected()
    }

    LaunchedEffect(downloadedModels) {
        delay(250)
        scrollToSelected()
    }

    LaunchedEffect(selectedModel?.name) {
        delay(250)
        if (isSelectRequested) {
            isSelectRequested = false
            return@LaunchedEffect
        }
        scrollToSelected()
    }

    var deleteDialogData by remember {
        mutableStateOf<NeuralModel?>(null)
    }

    DeleteModelDialog(
        model = deleteDialogData,
        onDismiss = { deleteDialogData = null },
        onDeleteModel = onDeleteModel
    )

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        flingBehavior = enhancedFlingBehavior()
    ) {
        item {
            PreferenceItem(
                title = stringResource(R.string.import_model),
                subtitle = stringResource(R.string.import_model_sub),
                onClick = filePicker::pickFile,
                startIcon = Icons.Rounded.ModelTraining,
                containerColor = EnhancedBottomSheetDefaults.contentContainerColor,
                shape = ShapeDefaults.default,
                modifier = Modifier.fillMaxWidth(),
                endIcon = Icons.Rounded.FileImport
            )
        }
        if (importedModels.isNotEmpty()) {
            item {
                TitleItem(
                    icon = Icons.AutoMirrored.Rounded.InsertDriveFile,
                    text = stringResource(id = R.string.imported_models)
                )
            }
        }
        itemsIndexed(
            items = importedModels,
            key = { _, m -> m.name }
        ) { index, model ->
            val selected = selectedModel?.name == model.name
            val state = rememberRevealState()
            val interactionSource = remember {
                MutableInteractionSource()
            }
            val isDragged by interactionSource.collectIsDraggedAsState()
            val shape = ShapeDefaults.byIndex(
                index = index,
                size = importedModels.size,
                forceDefault = isDragged
            )
            SwipeToReveal(
                state = state,
                modifier = Modifier.animateItem(),
                revealedContentEnd = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .container(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = shape,
                                autoShadowElevation = 0.dp,
                                resultPadding = 0.dp
                            )
                            .hapticsClickable {
                                scope.launch {
                                    state.animateTo(RevealValue.Default)
                                }
                                deleteDialogData = model
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.delete),
                            modifier = Modifier
                                .padding(16.dp)
                                .padding(end = 8.dp)
                                .align(Alignment.CenterEnd),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                },
                directions = setOf(RevealDirection.EndToStart),
                swipeableContent = {
                    PreferenceItemOverload(
                        shape = shape,
                        containerColor = animateColorAsState(
                            if (selected) {
                                MaterialTheme
                                    .colorScheme
                                    .mixedContainer
                                    .copy(0.8f)
                            } else EnhancedBottomSheetDefaults.contentContainerColor
                        ).value,
                        onLongClick = {
                            scope.launch {
                                state.animateTo(RevealValue.FullyRevealedStart)
                            }
                        },
                        onClick = {
                            isSelectRequested = true
                            onSelectModel(model)
                        },
                        title = model.title,
                        subtitle = model.description?.let { stringResource(it) },
                        modifier = Modifier.fillMaxWidth(),
                        endIcon = {
                            Icon(
                                imageVector = if (selected) {
                                    Icons.Rounded.RadioButtonChecked
                                } else {
                                    Icons.Rounded.RadioButtonUnchecked
                                },
                                contentDescription = null
                            )
                        },
                        placeBottomContentInside = true,
                        bottomContent = {
                            NeuralModelSizeBadge(
                                model = model,
                                isInverted = selected,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    )
                },
                interactionSource = interactionSource
            )
        }
        if (downloadedModels.isNotEmpty()) {
            item {
                TitleItem(
                    icon = Icons.Rounded.DownloadDone,
                    text = stringResource(id = R.string.downloaded_models),
                    endContent = {
                        EnhancedBadge(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ) {
                            Text(
                                rememberHumanFileSize(
                                    byteCount = occupiedStorageSize,
                                    precision = 2
                                )
                            )
                        }
                    }
                )
            }
        }
        itemsIndexed(
            items = downloadedModels,
            key = { _, m -> m.name }
        ) { index, model ->
            val selected = selectedModel?.name == model.name
            val state = rememberRevealState()
            val interactionSource = remember {
                MutableInteractionSource()
            }
            val isDragged by interactionSource.collectIsDraggedAsState()
            val shape = ShapeDefaults.byIndex(
                index = index,
                size = downloadedModels.size,
                forceDefault = isDragged
            )
            SwipeToReveal(
                state = state,
                modifier = Modifier.animateItem(),
                revealedContentEnd = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .container(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = shape,
                                autoShadowElevation = 0.dp,
                                resultPadding = 0.dp
                            )
                            .hapticsClickable {
                                scope.launch {
                                    state.animateTo(RevealValue.Default)
                                }
                                deleteDialogData = model
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.delete),
                            modifier = Modifier
                                .padding(16.dp)
                                .padding(end = 8.dp)
                                .align(Alignment.CenterEnd),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                },
                revealedContentStart = {
                    val uriHandler = LocalUriHandler.current
                    Box(
                        Modifier
                            .fillMaxSize()
                            .container(
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(
                                    0.5f
                                ),
                                shape = shape,
                                autoShadowElevation = 0.dp,
                                resultPadding = 0.dp
                            )
                            .hapticsClickable {
                                scope.launch {
                                    state.animateTo(RevealValue.Default)
                                }
                                uriHandler.openUri(model.pointerLink)
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Link,
                            contentDescription = "link",
                            modifier = Modifier
                                .padding(16.dp)
                                .padding(start = 8.dp)
                                .align(Alignment.CenterStart),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                },
                directions = setOf(
                    RevealDirection.StartToEnd,
                    RevealDirection.EndToStart
                ),
                swipeableContent = {
                    PreferenceItemOverload(
                        shape = shape,
                        containerColor = animateColorAsState(
                            if (selected) {
                                MaterialTheme
                                    .colorScheme
                                    .mixedContainer
                                    .copy(0.8f)
                            } else EnhancedBottomSheetDefaults.contentContainerColor
                        ).value,
                        onLongClick = {
                            scope.launch {
                                state.animateTo(RevealValue.FullyRevealedStart)
                            }
                        },
                        onClick = {
                            isSelectRequested = true
                            onSelectModel(model)
                        },
                        title = model.title,
                        subtitle = model.description?.let { stringResource(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeBottomContentInside = true,
                        endIcon = {
                            Icon(
                                imageVector = if (selected) {
                                    Icons.Rounded.RadioButtonChecked
                                } else {
                                    Icons.Rounded.RadioButtonUnchecked
                                },
                                contentDescription = null
                            )
                        },
                        bottomContent = model.type?.let { type ->
                            {
                                FlowRow(
                                    modifier = Modifier.padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    NeuralModelTypeBadge(
                                        type = type,
                                        isInverted = selected
                                    )

                                    model.speed?.let { speed ->
                                        NeuralModelSpeedBadge(
                                            speed = speed,
                                            isInverted = selected
                                        )
                                    }

                                    NeuralModelSizeBadge(
                                        model = model,
                                        isInverted = selected
                                    )
                                }
                            }
                        }
                    )
                },
                interactionSource = interactionSource
            )
        }
        if (notDownloadedModels.isNotEmpty()) {
            item {
                TitleItem(
                    icon = Icons.Rounded.Download,
                    text = stringResource(id = R.string.available_models)
                )
            }
        }
        itemsIndexed(
            items = notDownloadedModels,
            key = { _, m -> m.name + "not" }
        ) { index, model ->
            val state = rememberRevealState()
            val interactionSource = remember {
                MutableInteractionSource()
            }
            val isDragged by interactionSource.collectIsDraggedAsState()
            val shape = ShapeDefaults.byIndex(
                index = index,
                size = notDownloadedModels.size,
                forceDefault = isDragged
            )
            SwipeToReveal(
                state = state,
                modifier = Modifier.animateItem(),
                revealedContentStart = {
                    val uriHandler = LocalUriHandler.current
                    Box(
                        Modifier
                            .fillMaxSize()
                            .container(
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(
                                    0.5f
                                ),
                                shape = shape,
                                autoShadowElevation = 0.dp,
                                resultPadding = 0.dp
                            )
                            .hapticsClickable {
                                scope.launch {
                                    state.animateTo(RevealValue.Default)
                                }
                                uriHandler.openUri(model.pointerLink)
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Link,
                            contentDescription = "link",
                            modifier = Modifier
                                .padding(16.dp)
                                .padding(start = 8.dp)
                                .align(Alignment.CenterStart),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                },
                directions = setOf(RevealDirection.StartToEnd),
                swipeableContent = {
                    PreferenceItemOverload(
                        shape = shape,
                        title = model.title,
                        subtitle = model.description?.let { stringResource(it) },
                        onClick = {
                            onDownloadModel(model)
                        },
                        onLongClick = {
                            scope.launch {
                                state.animateTo(RevealValue.FullyRevealedEnd)
                            }
                        },
                        containerColor = EnhancedBottomSheetDefaults.contentContainerColor,
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth(),
                        endIcon = {
                            AnimatedContent(
                                targetState = downloadProgresses[model.name],
                                contentKey = { key -> key?.currentTotalSize?.let { it > 0 } }
                            ) { progress ->
                                if (progress != null) {
                                    Row(
                                        modifier = Modifier.container(
                                            shape = ShapeDefaults.circle,
                                            color = MaterialTheme.colorScheme.surface,
                                            resultPadding = 8.dp
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (progress.currentTotalSize > 0) {
                                                rememberHumanFileSize(progress.currentTotalSize)
                                            } else {
                                                stringResource(R.string.preparing)
                                            },
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        EnhancedAutoCircularProgressIndicator(
                                            progress = { progress.currentPercent },
                                            modifier = Modifier.size(24.dp),
                                            trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                                            strokeWidth = 3.dp
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.DownloadForOffline,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        placeBottomContentInside = true,
                        bottomContent = model.type?.let { type ->
                            {
                                FlowRow(
                                    modifier = Modifier.padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    NeuralModelTypeBadge(
                                        type = type,
                                        isInverted = false
                                    )

                                    model.speed?.let { speed ->
                                        NeuralModelSpeedBadge(
                                            speed = speed,
                                            isInverted = false
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = downloadProgresses[model.name] == null
                                    ) {
                                        NeuralModelSizeBadge(
                                            model = model,
                                            isInverted = false
                                        )
                                    }
                                }
                            }
                        }
                    )
                },
                interactionSource = interactionSource
            )
        }

        if (downloadedModels.isEmpty() && notDownloadedModels.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 8.dp
                        )
                        .container(
                            shape = ShapeDefaults.large,
                            resultPadding = 0.dp,
                            color = EnhancedBottomSheetDefaults.contentContainerColor
                        )
                        .height(256.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.nothing_found_by_search),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 24.dp,
                            bottom = 8.dp
                        )
                    )
                    Icon(
                        imageVector = Icons.Rounded.SearchOff,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(2f)
                            .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}