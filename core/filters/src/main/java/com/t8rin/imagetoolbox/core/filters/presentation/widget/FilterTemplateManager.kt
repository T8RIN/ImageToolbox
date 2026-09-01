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

package com.t8rin.imagetoolbox.core.filters.presentation.widget

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.filters.domain.model.TemplateFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters.AddFiltersSheetComponent
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Add
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.ContentCopy
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Deselect
import com.t8rin.imagetoolbox.core.resources.icons.DownloadFile
import com.t8rin.imagetoolbox.core.resources.icons.ExtensionOff
import com.t8rin.imagetoolbox.core.resources.icons.Info
import com.t8rin.imagetoolbox.core.resources.icons.SelectAll
import com.t8rin.imagetoolbox.core.resources.icons.Share
import com.t8rin.imagetoolbox.core.resources.icons.UploadFile
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCheckbox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.core.ui.widget.utils.rememberRetainedLazyListState

@Composable
fun FilterTemplateManager(
    component: AddFiltersSheetComponent,
    creationComponent: FilterTemplateCreationSheetComponent,
    onGoBack: () -> Unit
) {
    BackHandler(onBack = onGoBack)
    component.AttachLifecycle()

    val templates by component.templatesFlow.collectAsStateWithLifecycle()
    val isLoading = component.isTemplatesLoading
    var selectedTemplates by remember { mutableStateOf(setOf<TemplateFilter>()) }
    var infoTemplate by remember { mutableStateOf<TemplateFilter?>(null) }
    var showCreationSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(templates) {
        selectedTemplates = selectedTemplates.intersect(templates.toSet())
    }

    val selected = remember(templates, selectedTemplates) {
        templates.filter { it in selectedTemplates }
    }
    val importPicker = rememberFilePicker(
        mimeType = MimeType.All,
        onSuccess = component::importTemplateFilters
    )
    val exportPicker = rememberFileCreator(
        mimeType = MimeType.Zip,
        onSuccess = {
            component.exportTemplateFilters(
                templateFilters = selected,
                uri = it,
                onSuccess = { selectedTemplates = emptySet() }
            )
        }
    )
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            EnhancedTopAppBar(
                type = EnhancedTopAppBarType.Large,
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(R.string.manage_filter_templates),
                        modifier = Modifier.marquee()
                    )
                },
                navigationIcon = {
                    EnhancedIconButton(onClick = onGoBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.exit)
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = !isLoading && templates.isNotEmpty(),
                        enter = fadeIn() + scaleIn() + expandHorizontally(),
                        exit = fadeOut() + scaleOut() + shrinkHorizontally()
                    ) {
                        EnhancedIconButton(
                            onClick = {
                                selectedTemplates = if (selected.size == templates.size) {
                                    emptySet()
                                } else {
                                    templates.toSet()
                                }
                            }
                        ) {
                            val allSelected = selected.size == templates.size
                            Icon(
                                imageVector = if (allSelected) {
                                    Icons.Outlined.Deselect
                                } else {
                                    Icons.Outlined.SelectAll
                                },
                                contentDescription = stringResource(
                                    if (allSelected) {
                                        R.string.deselect_all
                                    } else {
                                        R.string.select_all
                                    }
                                )
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = selected.isNotEmpty(),
                        enter = fadeIn() + scaleIn() + expandHorizontally(),
                        exit = fadeOut() + scaleOut() + shrinkHorizontally(),
                        modifier = Modifier
                            .padding(8.dp)
                            .container(
                                shape = ShapeDefaults.circle,
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                resultPadding = 0.dp
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = selected.size.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                            EnhancedIconButton(
                                onClick = { selectedTemplates = emptySet() }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(R.string.clear_selection)
                                )
                            }
                        }
                    }
                    if (!isLoading && templates.isEmpty()) {
                        TopAppBarEmoji()
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val noTemplates = templates.isEmpty()
                BottomButtonsBlock(
                    isNoData = noTemplates,
                    onSecondaryButtonClick = if (noTemplates) {
                        { showCreationSheet = true }
                    } else {
                        importPicker::pickFile
                    },
                    secondaryButtonIcon = if (noTemplates) {
                        Icons.Rounded.Add
                    } else {
                        Icons.Outlined.UploadFile
                    },
                    secondaryButtonText = if (noTemplates) {
                        stringResource(R.string.create_template)
                    } else {
                        stringResource(R.string.import_templates)
                    },
                    onPrimaryButtonClick = { showCreationSheet = true },
                    primaryButtonIcon = Icons.Rounded.Add,
                    primaryButtonText = stringResource(R.string.create_template),
                    isPrimaryButtonVisible = !noTemplates && selected.isEmpty(),
                    isSecondaryButtonVisible = !noTemplates && selected.isEmpty(),
                    showNullDataButtonAsContainer = true,
                    isScreenHaveNoDataContent = true,
                    showMiddleFabInRow = noTemplates,
                    middleFab = {
                        EnhancedFloatingActionButton(onClick = importPicker::pickFile) {
                            Icon(
                                imageVector = Icons.Outlined.UploadFile,
                                contentDescription = stringResource(R.string.import_templates)
                            )
                        }
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = selected.isNotEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row {
                                EnhancedIconButton(
                                    enabled = selected.size == 1,
                                    onClick = {
                                        selected.singleOrNull()
                                            ?.let(component::duplicateTemplateFilter)
                                        selectedTemplates = emptySet()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ContentCopy,
                                        contentDescription = stringResource(R.string.duplicate)
                                    )
                                }
                                EnhancedIconButton(
                                    onClick = {
                                        exportPicker.make("filter_templates_${timestamp()}.zip")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DownloadFile,
                                        contentDescription = stringResource(R.string.export)
                                    )
                                }
                                EnhancedIconButton(
                                    onClick = { component.shareTemplateFilters(selected) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Share,
                                        contentDescription = stringResource(R.string.share)
                                    )
                                }
                                EnhancedIconButton(
                                    onClick = { showDeleteDialog = true },
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = stringResource(R.string.delete)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    ) { contentPadding ->
        AnimatedContent(
            targetState = when {
                isLoading -> TemplateManagerState.Loading
                templates.isEmpty() -> TemplateManagerState.Empty
                else -> TemplateManagerState.Content
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) { state ->
            when (state) {
                TemplateManagerState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EnhancedLoadingIndicator()
                }

                TemplateManagerState.Empty -> EmptyTemplatesContent()

                TemplateManagerState.Content -> LazyColumn(
                    state = rememberRetainedLazyListState("filterTemplateManager"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    itemsIndexed(
                        items = templates,
                        key = { index, template -> "$index-${template.hashCode()}" }
                    ) { index, template ->
                        val checked = template in selectedTemplates
                        PreferenceItemOverload(
                            title = template.name,
                            subtitle = stringResource(
                                R.string.template_filters_count,
                                template.filters.size
                            ),
                            shape = ShapeDefaults.byIndex(index, templates.size),
                            modifier = Modifier.animateItem(),
                            containerColor = if (checked) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainer
                            },
                            contentColor = if (checked) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            onClick = {
                                selectedTemplates = if (checked) {
                                    selectedTemplates - template
                                } else {
                                    selectedTemplates + template
                                }
                            },
                            startIcon = {
                                TemplateFilterPreviewItem(
                                    modifier = Modifier.size(64.dp),
                                    templateFilter = template,
                                    onRequestFilterMapping = component::filterToTransformation
                                )
                            },
                            drawStartIconContainer = false,
                            endIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    EnhancedIconButton(onClick = { infoTemplate = template }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Info,
                                            contentDescription = stringResource(
                                                R.string.template_filter
                                            )
                                        )
                                    }
                                    EnhancedCheckbox(
                                        checked = checked,
                                        onCheckedChange = {
                                            selectedTemplates = if (it) {
                                                selectedTemplates + template
                                            } else {
                                                selectedTemplates - template
                                            }
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    FilterTemplateInfoSheet(
        component = creationComponent,
        visible = infoTemplate != null,
        onDismiss = { visible ->
            if (!visible) infoTemplate = null
        },
        templateFilter = infoTemplate ?: TemplateFilter.Default,
        onRequestFilterMapping = component::filterToTransformation,
        onShareImage = component::shareImage,
        onSaveImage = component::saveImage,
        onSaveFile = { fileUri, content ->
            component.saveContentTo(
                content = content,
                fileUri = fileUri
            )
        },
        onConvertTemplateFilterToString = component::convertTemplateFilterToString,
        onRemoveTemplateFilter = component::removeTemplateFilter,
        onTemplateUpdated = { infoTemplate = it },
        onRequestTemplateFilename = { component.createTemplateFilename(infoTemplate) },
        onShareFile = { content ->
            component.shareContent(
                content = content,
                filename = component.createTemplateFilename(infoTemplate)
            )
        }
    )

    FilterTemplateCreationSheet(
        component = creationComponent,
        visible = showCreationSheet,
        onDismiss = { showCreationSheet = false }
    )

    EnhancedAlertDialog(
        visible = showDeleteDialog,
        onDismissRequest = { showDeleteDialog = false },
        title = { Text(stringResource(R.string.delete_templates)) },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        },
        text = {
            Text(
                text = stringResource(
                    R.string.delete_templates_warning,
                    selected.size
                ),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                onClick = {
                    component.removeTemplateFilters(selected)
                    selectedTemplates = emptySet()
                    showDeleteDialog = false
                }
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            EnhancedButton(onClick = { showDeleteDialog = false }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private enum class TemplateManagerState {
    Loading,
    Empty,
    Content
}

@Composable
private fun EmptyTemplatesContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))
        Text(
            text = stringResource(R.string.no_template_filters),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.size(16.dp))
        Icon(
            imageVector = Icons.Outlined.ExtensionOff,
            contentDescription = null,
            modifier = Modifier.size(128.dp)
        )
        Spacer(Modifier.weight(1f))
    }
}
