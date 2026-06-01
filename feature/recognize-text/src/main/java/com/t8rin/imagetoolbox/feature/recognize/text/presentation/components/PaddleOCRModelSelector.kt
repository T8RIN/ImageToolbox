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

package com.t8rin.imagetoolbox.feature.recognize.text.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Download
import com.t8rin.imagetoolbox.core.resources.icons.DownloadDone
import com.t8rin.imagetoolbox.core.resources.icons.Language
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Search
import com.t8rin.imagetoolbox.core.resources.icons.SearchOff
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.recognize.text.domain.PaddleOCRModel
import kotlinx.coroutines.delay

@Composable
fun PaddleOCRModelSelector(
    value: PaddleOCRModel,
    updateKey: Int,
    isDownloaded: (PaddleOCRModel) -> Boolean,
    onValueChange: (PaddleOCRModel) -> Unit,
    onDeleteModel: (PaddleOCRModel) -> Unit
) {
    var showSelectionSheet by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItem(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.language),
        subtitle = value.localizedName(),
        onClick = {
            showSelectionSheet = true
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = ShapeDefaults.extraLarge,
        startIcon = Icons.Rounded.Language,
        endIcon = Icons.Rounded.MiniEdit
    )

    var isSearching by rememberSaveable {
        mutableStateOf(false)
    }
    var searchKeyword by rememberSaveable {
        mutableStateOf("")
    }

    EnhancedModalBottomSheet(
        visible = showSelectionSheet,
        onDismiss = {
            showSelectionSheet = it
        },
        enableBottomContentWeight = false,
        confirmButton = {},
        title = {
            AnimatedContent(
                targetState = isSearching
            ) { searching ->
                if (searching) {
                    BackHandler {
                        searchKeyword = ""
                        isSearching = false
                    }
                    ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                        RoundedTextField(
                            maxLines = 1,
                            hint = { Text(stringResource(id = R.string.search_here)) },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search,
                                autoCorrectEnabled = null
                            ),
                            value = searchKeyword,
                            onValueChange = {
                                searchKeyword = it
                            },
                            startIcon = {
                                EnhancedIconButton(
                                    onClick = {
                                        searchKeyword = ""
                                        isSearching = false
                                    },
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowBack,
                                        contentDescription = stringResource(R.string.exit),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            endIcon = {
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = searchKeyword.isNotEmpty(),
                                    enter = fadeIn() + scaleIn(),
                                    exit = fadeOut() + scaleOut()
                                ) {
                                    EnhancedIconButton(
                                        onClick = {
                                            searchKeyword = ""
                                        },
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = stringResource(R.string.close),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            },
                            shape = ShapeDefaults.circle
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TitleItem(
                            text = stringResource(id = R.string.language),
                            icon = Icons.Rounded.Language
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        EnhancedIconButton(
                            onClick = { isSearching = true },
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = stringResource(R.string.search_here)
                            )
                        }
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = {
                                showSelectionSheet = false
                            }
                        ) {
                            Text(stringResource(R.string.close))
                        }
                    }
                }
            }
        },
        sheetContent = {
            PaddleOCRModelSelectorSheetContent(
                value = value,
                updateKey = updateKey,
                isDownloaded = isDownloaded,
                searchKeyword = searchKeyword,
                isSearching = isSearching,
                onValueChange = onValueChange,
                onDeleteModel = onDeleteModel
            )
        }
    )
}

@Composable
private fun PaddleOCRModelSelectorSheetContent(
    value: PaddleOCRModel,
    updateKey: Int,
    isDownloaded: (PaddleOCRModel) -> Boolean,
    searchKeyword: String,
    isSearching: Boolean,
    onValueChange: (PaddleOCRModel) -> Unit,
    onDeleteModel: (PaddleOCRModel) -> Unit
) {
    val models = PaddleOCRModel.entries
    val downloadedModels by remember(updateKey) {
        derivedStateOf {
            models.filter(isDownloaded)
        }
    }
    val notDownloadedModels by remember(updateKey) {
        derivedStateOf {
            models.filterNot(isDownloaded)
        }
    }

    var modelsForSearch by remember {
        mutableStateOf(downloadedModels + notDownloadedModels)
    }

    LaunchedEffect(searchKeyword, updateKey) {
        delay(400L)
        val allModels = downloadedModels + notDownloadedModels
        modelsForSearch = if (searchKeyword.isEmpty()) {
            allModels
        } else {
            allModels.filter {
                it.englishName.contains(searchKeyword, ignoreCase = true) ||
                        it.localizedName().contains(searchKeyword, ignoreCase = true)
            }.sortedBy(PaddleOCRModel::englishName)
        }
    }

    AnimatedContent(targetState = isSearching to modelsForSearch.isNotEmpty()) { (searching, haveData) ->
        if (searching && !haveData) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.nothing_found_by_search),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
                Icon(
                    imageVector = Icons.Outlined.SearchOff,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(2f)
                        .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                        .fillMaxSize()
                )
                Spacer(Modifier.weight(1f))
            }
        } else {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                flingBehavior = enhancedFlingBehavior()
            ) {
                val shownDownloaded = if (searching) {
                    modelsForSearch.filter(isDownloaded)
                } else {
                    downloadedModels
                }
                val shownAvailable = if (searching) {
                    modelsForSearch.filterNot(isDownloaded)
                } else {
                    notDownloadedModels
                }

                if (shownDownloaded.isNotEmpty()) {
                    item {
                        TitleItem(
                            icon = Icons.Rounded.DownloadDone,
                            text = stringResource(id = R.string.downloaded_models)
                        )
                    }
                }
                itemsIndexed(
                    items = shownDownloaded,
                    key = { _, model -> model.name }
                ) { index, model ->
                    PaddleOCRModelItem(
                        model = model,
                        selected = value == model,
                        downloaded = true,
                        index = index,
                        size = shownDownloaded.size,
                        onValueChange = onValueChange,
                        onDeleteModel = onDeleteModel
                    )
                }
                if (shownAvailable.isNotEmpty()) {
                    item {
                        TitleItem(
                            icon = Icons.Rounded.Download,
                            text = stringResource(id = R.string.available_models)
                        )
                    }
                }
                itemsIndexed(
                    items = shownAvailable,
                    key = { _, model -> model.name }
                ) { index, model ->
                    PaddleOCRModelItem(
                        model = model,
                        selected = value == model,
                        downloaded = false,
                        index = index,
                        size = shownAvailable.size,
                        onValueChange = onValueChange,
                        onDeleteModel = onDeleteModel
                    )
                }
            }
        }
    }
}

@Composable
private fun LazyItemScope.PaddleOCRModelItem(
    model: PaddleOCRModel,
    selected: Boolean,
    downloaded: Boolean,
    index: Int,
    size: Int,
    onValueChange: (PaddleOCRModel) -> Unit,
    onDeleteModel: (PaddleOCRModel) -> Unit
) {
    var deleteDialogData by remember {
        mutableStateOf<PaddleOCRModel?>(null)
    }
    val shape = ShapeDefaults.byIndex(index = index, size = size)
    val item = @Composable {
        PreferenceItem(
            title = model.englishName,
            subtitle = model.localizedName().takeIf { it != model.englishName },
            onClick = {
                onValueChange(model)
            },
            containerColor = animateColorAsState(
                if (selected) {
                    MaterialTheme.colorScheme.mixedContainer.copy(0.8f)
                } else {
                    EnhancedBottomSheetDefaults.contentContainerColor
                }
            ).value,
            shape = shape,
            modifier = Modifier
                .animateItem()
                .fillMaxWidth()
        )
    }

    DeletePaddleOCRModelDialog(
        modelToDelete = deleteDialogData,
        onDismiss = { deleteDialogData = null },
        onDeleteModel = onDeleteModel
    )

    if (downloaded) {
        val state = rememberRevealState()
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
                item()
            }
        )
    } else {
        item()
    }
}

@Composable
private fun DeletePaddleOCRModelDialog(
    modelToDelete: PaddleOCRModel?,
    onDismiss: () -> Unit,
    onDeleteModel: (PaddleOCRModel) -> Unit
) {
    EnhancedAlertDialog(
        visible = modelToDelete != null,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        },
        title = { Text(stringResource(id = R.string.delete)) },
        text = {
            Text(
                stringResource(
                    id = R.string.delete_model_sub,
                    modelToDelete?.localizedName() ?: ""
                )
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.error,
                onClick = {
                    modelToDelete?.let(onDeleteModel)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private fun PaddleOCRModel.localizedName(): String = englishName