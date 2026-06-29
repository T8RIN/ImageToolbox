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

@file:Suppress("FunctionNaming", "PackageName", "PackageNaming")

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ChangeHistory
import com.t8rin.imagetoolbox.core.resources.icons.Circle
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.DragHandle
import com.t8rin.imagetoolbox.core.resources.icons.Draw
import com.t8rin.imagetoolbox.core.resources.icons.Image
import com.t8rin.imagetoolbox.core.resources.icons.Layers
import com.t8rin.imagetoolbox.core.resources.icons.Line
import com.t8rin.imagetoolbox.core.resources.icons.LineArrow
import com.t8rin.imagetoolbox.core.resources.icons.Rectangle
import com.t8rin.imagetoolbox.core.resources.icons.TextFormat
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import io.ak1.drawbox.domain.model.Element
import io.ak1.drawbox.domain.model.ShapeType
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
internal fun VectorCanvasLayersSheet(
    visible: Boolean,
    elements: List<Element>,
    selectedIds: Set<String>,
    actions: VectorCanvasLayerActions
) {
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = {
            if (!it) actions.onDismiss()
        },
        sheetContent = {
            LayersList(
                elements = elements,
                selectedIds = selectedIds,
                onDelete = actions.onDelete,
                onReorder = actions.onReorder
            )
        },
        title = {
            TitleItem(
                text = stringResource(R.string.vector_canvas_layers),
                icon = Icons.Rounded.Layers
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = actions.onDismiss
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    )
}

@Composable
private fun LayersList(
    elements: List<Element>,
    selectedIds: Set<String>,
    onDelete: (Element) -> Unit,
    onReorder: (List<Element>) -> Unit
) {
    val topToBottom = remember(elements) {
        elements.sortedByDescending(Element::zIndex)
    }
    var ordered by remember(topToBottom) { mutableStateOf(topToBottom) }
    val listState = rememberLazyListState()
    val haptics = LocalHapticFeedback.current
    val reorderState = rememberReorderableLazyListState(listState) { from, to ->
        haptics.press()
        ordered = ordered.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    if (ordered.isEmpty()) {
        EmptyLayersMessage()
        return
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        flingBehavior = enhancedFlingBehavior()
    ) {
        itemsIndexed(
            items = ordered,
            key = { _, element -> element.id }
        ) { index, element ->
            ReorderableItem(
                state = reorderState,
                key = element.id
            ) { isDragging ->
                val scale by animateFloatAsState(
                    if (isDragging) DRAGGING_LAYER_SCALE else DEFAULT_LAYER_SCALE
                )
                LayerItem(
                    element = element,
                    selected = element.id in selectedIds,
                    onDelete = { onDelete(element) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .longPressDraggableHandle(
                            onDragStarted = { haptics.longPress() },
                            onDragStopped = { onReorder(ordered.asReversed()) }
                        )
                        .scale(scale)
                        .container(
                            shape = ShapeDefaults.byIndex(index, ordered.size),
                            color = if (element.id in selectedIds) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainer
                            }
                        )
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyLayersMessage() {
    Text(
        text = stringResource(R.string.vector_canvas_no_layers),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(32.dp)
    )
}

@Composable
private fun LayerItem(
    element: Element,
    selected: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = element.layerIcon(),
            contentDescription = null,
            tint = if (selected) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        Text(
            text = element.layerTitle(),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        EnhancedIconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = MaterialTheme.colorScheme.error
            )
        }
        Icon(
            imageVector = Icons.Rounded.DragHandle,
            contentDescription = stringResource(R.string.reorder)
        )
    }
}

@Composable
private fun Element.layerTitle(): String = when (this) {
    is Element.Path -> stringResource(R.string.vector_canvas_layer_path)
    is Element.Image -> stringResource(R.string.image)
    is Element.Text -> text.ifBlank { stringResource(R.string.text) }
    is Element.Shape -> stringResource(
        when (shapeType) {
            ShapeType.RECTANGLE -> R.string.rect
            ShapeType.CIRCLE -> R.string.vector_canvas_circle
            ShapeType.TRIANGLE -> R.string.triangle
            ShapeType.ARROW -> R.string.arrow
            ShapeType.LINE -> R.string.line
        }
    )
}

private fun Element.layerIcon() = when (this) {
    is Element.Path -> Icons.Rounded.Draw
    is Element.Image -> Icons.Outlined.Image
    is Element.Text -> Icons.Rounded.TextFormat
    is Element.Shape -> when (shapeType) {
        ShapeType.RECTANGLE -> Icons.Outlined.Rectangle
        ShapeType.CIRCLE -> Icons.Outlined.Circle
        ShapeType.TRIANGLE -> Icons.Outlined.ChangeHistory
        ShapeType.ARROW -> Icons.Rounded.LineArrow
        ShapeType.LINE -> Icons.Rounded.Line
    }
}

internal data class VectorCanvasLayerActions(
    val onDismiss: () -> Unit,
    val onDelete: (Element) -> Unit,
    val onReorder: (List<Element>) -> Unit
)

private const val DRAGGING_LAYER_SCALE = 1.03f
private const val DEFAULT_LAYER_SCALE = 1f
