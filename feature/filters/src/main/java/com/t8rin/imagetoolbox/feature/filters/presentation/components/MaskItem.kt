/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.filters.presentation.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.presentation.model.toUiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.AddFilterButton
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters.AddFiltersSheet
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.EditAlt
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.filters.presentation.components.addEditMaskSheet.AddEditMaskSheet
import com.t8rin.imagetoolbox.feature.filters.presentation.components.addEditMaskSheet.AddMaskSheetComponent

@Composable
fun MaskItem(
    addMaskSheetComponent: AddMaskSheetComponent?,
    mask: UiFilterMask,
    modifier: Modifier = Modifier,
    titleText: String,
    onMaskChange: (UiFilterMask) -> Unit,
    previewOnly: Boolean = false,
    backgroundColor: Color = Color.Unspecified,
    showDragHandle: Boolean,
    onLongPress: (() -> Unit)? = null,
    onCreateTemplate: (() -> Unit)?,
    onRemove: () -> Unit,
    imageUri: Uri? = null,
    previousMasks: List<UiFilterMask> = emptyList(),
    shape: Shape = MaterialTheme.shapes.extraLarge
) {
    var showMaskRemoveDialog by rememberSaveable { mutableStateOf(false) }
    var showAddFilterSheet by rememberSaveable { mutableStateOf(false) }
    var showEditMaskSheet by rememberSaveable { mutableStateOf(false) }
    val settingsState = LocalSettingsState.current
    Box {
        Row(
            modifier = modifier
                .container(
                    color = backgroundColor,
                    shape = shape
                )
                .animateContentSizeNoClip()
                .then(
                    onLongPress?.let {
                        Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = { it() }
                            )
                        }
                    } ?: Modifier
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showDragHandle) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Rounded.DragHandle,
                    contentDescription = stringResource(R.string.drag_handle_width)
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .height(32.dp)
                        .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant())
                        .padding(start = 20.dp)
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .alpha(if (previewOnly) 0.5f else 1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PathPaintPreview(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .sizeIn(maxHeight = 30.dp, maxWidth = 30.dp),
                            pathPaints = mask.maskPaints
                        )
                        Text(
                            text = titleText,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(
                                    end = 8.dp,
                                    start = 16.dp
                                )
                        )
                        Spacer(Modifier.weight(1f))
                        EnhancedIconButton(
                            onClick = { showMaskRemoveDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.RemoveCircleOutline,
                                contentDescription = stringResource(R.string.remove)
                            )
                        }
                        EnhancedIconButton(
                            onClick = { showEditMaskSheet = true }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EditAlt,
                                contentDescription = stringResource(R.string.edit)
                            )
                        }
                    }
                    EnhancedAlertDialog(
                        visible = showMaskRemoveDialog,
                        onDismissRequest = { showMaskRemoveDialog = false },
                        confirmButton = {
                            EnhancedButton(
                                onClick = { showMaskRemoveDialog = false }
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        },
                        dismissButton = {
                            EnhancedButton(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                onClick = {
                                    showMaskRemoveDialog = false
                                    onRemove()
                                }
                            ) {
                                Text(stringResource(R.string.delete))
                            }
                        },
                        title = {
                            Text(stringResource(R.string.delete_mask))
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        },
                        text = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                PathPaintPreview(
                                    pathPaints = mask.maskPaints,
                                    modifier = Modifier.sizeIn(
                                        maxHeight = 80.dp,
                                        maxWidth = 80.dp
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(stringResource(R.string.delete_mask_warn))
                            }
                        }
                    )
                }

                AnimatedVisibility(mask.filters.isNotEmpty()) {
                    ExpandableItem(
                        modifier = Modifier.padding(8.dp),
                        visibleContent = {
                            TitleItem(text = stringResource(id = R.string.filters) + " (${mask.filters.size})")
                        },
                        expandableContent = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                mask.filters.forEachIndexed { index, filter ->
                                    val uiFilter by remember(filter) {
                                        derivedStateOf {
                                            filter.toUiFilter()
                                        }
                                    }
                                    FilterItem(
                                        backgroundColor = MaterialTheme.colorScheme.surface,
                                        filter = uiFilter,
                                        showDragHandle = false,
                                        onRemove = {
                                            onMaskChange(
                                                mask.copy(filters = mask.filters - filter)
                                            )
                                        },
                                        onFilterChange = { value ->
                                            onMaskChange(
                                                mask.copy(
                                                    filters = mask.filters.toMutableList()
                                                        .apply {
                                                            this[index] = uiFilter.copy(value)
                                                        }
                                                )
                                            )
                                        },
                                        onCreateTemplate = onCreateTemplate
                                    )
                                }
                                AddFilterButton(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    onClick = {
                                        showAddFilterSheet = true
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
        if (previewOnly) {
            Surface(
                color = Color.Transparent,
                modifier = modifier.matchParentSize()
            ) {}
        }
    }

    addMaskSheetComponent?.let {
        AddFiltersSheet(
            visible = showAddFilterSheet,
            onVisibleChange = { showAddFilterSheet = it },
            previewBitmap = null,
            onFilterPicked = { filter ->
                onMaskChange(
                    mask.copy(
                        filters = mask.filters + filter.newInstance()
                    )
                )
            },
            onFilterPickedWithParams = { filter ->
                onMaskChange(
                    mask.copy(
                        filters = mask.filters + filter
                    )
                )
            },
            component = addMaskSheetComponent.addFiltersSheetComponent,
            filterTemplateCreationSheetComponent = addMaskSheetComponent.filterTemplateCreationSheetComponent
        )

        AddEditMaskSheet(
            mask = mask,
            visible = showEditMaskSheet,
            targetBitmapUri = imageUri,
            masks = previousMasks,
            onDismiss = {
                showEditMaskSheet = false
            },
            onMaskPicked = onMaskChange,
            component = addMaskSheetComponent
        )
    }
}