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

package ru.tech.imageresizershrinker.feature.filters.presentation.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.transform.Transformation
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.toUiFilter
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.EditAlt
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.ExpandableItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun MaskItem(
    mask: UiFilterMask,
    modifier: Modifier = Modifier,
    onRequestFilterMapping: ((UiFilter<*>) -> Transformation)? = null,
    onRequestPreview: (suspend (Bitmap, List<UiFilter<*>>, IntegerSize) -> Bitmap?)? = null,
    titleText: String,
    onMaskChange: (UiFilterMask) -> Unit,
    previewOnly: Boolean = false,
    backgroundColor: Color = MaterialTheme.colorScheme
        .surfaceContainerLow,
    showDragHandle: Boolean,
    onLongPress: (() -> Unit)? = null,
    onRemove: () -> Unit,
    imageUri: Uri? = null,
    previousMasks: List<UiFilterMask> = emptyList()
) {
    var showMaskRemoveDialog by rememberSaveable { mutableStateOf(false) }
    val showAddFilterSheet = rememberSaveable { mutableStateOf(false) }
    var showEditMaskSheet by rememberSaveable { mutableStateOf(false) }
    val settingsState = LocalSettingsState.current
    Box {
        Row(
            modifier = modifier
                .container(color = backgroundColor, shape = MaterialTheme.shapes.extraLarge)
                .animateContentSize()
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
                Icon(Icons.Rounded.DragHandle, null)
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
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = {
                                showMaskRemoveDialog = true
                            }
                        ) {
                            Icon(Icons.Rounded.RemoveCircleOutline, null)
                        }
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = { showEditMaskSheet = true }
                        ) {
                            Icon(Icons.Rounded.EditAlt, null)
                        }
                    }
                    if (showMaskRemoveDialog) {
                        AlertDialog(
                            modifier = Modifier.alertDialogBorder(),
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
                                    contentDescription = null
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
                }

                AnimatedVisibility(mask.filters.isNotEmpty()) {
                    val interactionSource = remember {
                        MutableInteractionSource()
                    }
                    val pressed by interactionSource.collectIsPressedAsState()

                    val cornerSize by animateDpAsState(
                        if (pressed) 8.dp
                        else 20.dp
                    )
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
                                    FilterItem(
                                        shape = RoundedCornerShape(16.dp),
                                        filter = filter.toUiFilter(),
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
                                                            this[index] =
                                                                filter.toUiFilter().copy(value)
                                                        }
                                                )
                                            )
                                        }
                                    )
                                }
                                AddFilterButton(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    onClick = {
                                        showAddFilterSheet.value = true
                                    }
                                )
                            }
                        },
                        interactionSource = interactionSource,
                        shape = RoundedCornerShape(cornerSize)
                    )
                }
            }
        }
        if (previewOnly) {
            Surface(color = Color.Transparent, modifier = Modifier.matchParentSize()) {}
        }
    }

    if (onRequestPreview != null) {
        AddFiltersSheet(
            visible = showAddFilterSheet,
            previewBitmap = null,
            onRequestPreview = onRequestPreview,
            onFilterPicked = { filter ->
                onMaskChange(
                    mask.copy(
                        filters = mask.filters + filter.newInstance()
                    )
                )
            },
            onRequestFilterMapping = onRequestFilterMapping,
            onFilterPickedWithParams = { filter ->
                onMaskChange(
                    mask.copy(
                        filters = mask.filters + filter
                    )
                )
            }
        )
    }

    AddEditMaskSheet(
        mask = mask,
        visible = showEditMaskSheet,
        targetBitmapUri = imageUri,
        masks = previousMasks,
        onDismiss = {
            showEditMaskSheet = false
        },
        onMaskPicked = onMaskChange
    )
}