package ru.tech.imageresizershrinker.core.ui.widget.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.model.SortType
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.hapticsClickable
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun SortButton(
    modifier: Modifier = Modifier,
    iconSize: Dp = 20.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onSortTypeSelected: (SortType) -> Unit
) {
    var showSortTypeSelection by rememberSaveable {
        mutableStateOf(false)
    }

    EnhancedIconButton(
        onClick = {
            showSortTypeSelection = true
        },
        containerColor = containerColor,
        contentColor = contentColor,
        forceMinimumInteractiveComponentSize = false,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Rounded.FilterAlt,
            contentDescription = stringResource(R.string.sorting),
            modifier = Modifier.size(iconSize)
        )
    }

    EnhancedModalBottomSheet(
        visible = showSortTypeSelection,
        onDismiss = { showSortTypeSelection = it },
        title = {
            TitleItem(
                text = stringResource(R.string.sorting),
                icon = Icons.Rounded.FilterAlt
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showSortTypeSelection = false
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val items = SortType.entries

            items.forEachIndexed { index, item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = ContainerShapeDefaults.shapeForIndex(
                                index = index,
                                size = items.size
                            ),
                            resultPadding = 0.dp
                        )
                        .hapticsClickable {
                            onSortTypeSelected(item)
                            showSortTypeSelection = false
                        }
                ) {
                    TitleItem(text = item.title)
                }
            }
        }
    }
}

private val SortType.title: String
    @Composable
    get() = when (this) {
        SortType.DateModified -> stringResource(R.string.sort_by_date_modified)
        SortType.DateModifiedReversed -> stringResource(R.string.sort_by_date_modified_reversed)
        SortType.Name -> stringResource(R.string.sort_by_name)
        SortType.NameReversed -> stringResource(R.string.sort_by_name_reversed)
        SortType.Size -> stringResource(R.string.sort_by_size)
        SortType.SizeReversed -> stringResource(R.string.sort_by_size_reversed)
        SortType.MimeType -> stringResource(R.string.sort_by_mime_type)
        SortType.MimeTypeReversed -> stringResource(R.string.sort_by_mime_type_reversed)
        SortType.Extension -> stringResource(R.string.sort_by_extension)
        SortType.ExtensionReversed -> stringResource(R.string.sort_by_extension_reversed)
        SortType.DateAdded -> stringResource(R.string.sort_by_date_added)
        SortType.DateAddedReversed -> stringResource(R.string.sort_by_date_added_reversed)
    }