package ru.tech.imageresizershrinker.feature.filters.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Reorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.coreui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.coreui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.coreui.widget.text.TitleItem

@Composable
fun MaskReorderSheet(
    maskList: List<UiFilterMask>,
    visible: MutableState<Boolean>,
    updateOrder: (List<UiFilterMask>) -> Unit
) {
    SimpleSheet(
        sheetContent = {
            if (maskList.size < 2) {
                visible.value = false
            }
            Box {
                val data = remember { mutableStateOf(maskList) }
                val state = rememberReorderableLazyListState(
                    onMove = { from, to ->
                        data.value = data.value.toMutableList().apply {
                            add(to.index, removeAt(from.index))
                        }
                    },
                    onDragEnd = { _, _ ->
                        updateOrder(data.value)
                    }
                )
                LazyColumn(
                    state = state.listState,
                    modifier = Modifier
                        .reorderable(state)
                        .detectReorderAfterLongPress(state),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(data.value, key = { _, it -> it.hashCode() }) { index, mask ->
                        ReorderableItem(state, key = mask.hashCode()) { isDragging ->
                            val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
                            val tonalElevation by animateDpAsState(if (isDragging) 16.dp else 1.dp)
                            MaskItem(
                                mask = mask,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation, RoundedCornerShape(16.dp)),
                                onMaskChange = {},
                                previewOnly = true,
                                backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    tonalElevation
                                ),
                                titleText = stringResource(R.string.mask_indexed, index + 1),
                                showDragHandle = maskList.size >= 2,
                                onRemove = {}
                            )
                        }
                    }
                }
            }
        },
        visible = visible,
        title = {
            TitleItem(
                text = stringResource(R.string.reorder),
                icon = Icons.Rounded.Reorder
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { visible.value = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
    )
}