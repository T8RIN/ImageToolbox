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

package com.t8rin.imagetoolbox.feature.archive_tools.presentation.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.archive.ArchiveEntryInfo
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.Refresh
import com.t8rin.imagetoolbox.core.resources.icons.SelectAll
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCheckbox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload

@Composable
internal fun ArchiveEntriesSheet(
    visible: Boolean,
    entries: List<ArchiveEntryInfo>?,
    selectedEntries: Set<String>,
    isLoading: Boolean,
    onEntrySelected: (String, Boolean) -> Unit,
    onAllEntriesSelected: (Boolean) -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    val selectedCount = entries?.count { it.path in selectedEntries } ?: 0
    val allSelected = entries?.isNotEmpty() == true && selectedCount == entries.size

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { show ->
            if (!show) onDismiss()
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.heightIn(min = 64.dp)
            ) {
                AnimatedVisibility(
                    visible = entries?.isNotEmpty() == true && !allSelected,
                    enter = fadeIn() + scaleIn() + expandHorizontally(),
                    exit = fadeOut() + scaleOut() + shrinkHorizontally()
                ) {
                    EnhancedIconButton(
                        onClick = { onAllEntriesSelected(true) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SelectAll,
                            contentDescription = stringResource(R.string.select_all)
                        )
                    }
                }
                AnimatedVisibility(
                    modifier = Modifier
                        .padding(8.dp)
                        .container(
                            shape = ShapeDefaults.circle,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            resultPadding = 0.dp
                        ),
                    visible = selectedCount != 0
                ) {
                    Row(
                        modifier = Modifier.padding(start = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = selectedCount.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                        EnhancedIconButton(
                            onClick = { onAllEntriesSelected(false) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.deselect_all)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()

                entries == null -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(stringResource(R.string.archive_contents_load_failed))
                    EnhancedButton(onClick = onRetry) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.refresh))
                    }
                }

                entries.isEmpty() -> Text(stringResource(R.string.archive_contains_no_files))

                else -> LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    itemsIndexed(
                        items = entries,
                        key = { _, entry -> entry.path }
                    ) { index, entry ->
                        val selected = entry.path in selectedEntries
                        ArchiveEntryItem(
                            entry = entry,
                            selected = selected,
                            onSelected = { onEntrySelected(entry.path, it) },
                            modifier = Modifier.animateItem(),
                            shape = ShapeDefaults.byIndex(index, entries.size)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchiveEntryItem(
    entry: ArchiveEntryInfo,
    selected: Boolean,
    onSelected: (Boolean) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    val parentPath = entry.path.substringBeforeLast('/', missingDelimiterValue = "")
    PreferenceItemOverload(
        title = entry.path.substringAfterLast('/'),
        subtitle = listOfNotNull(
            parentPath.takeIf(String::isNotEmpty),
            humanFileSize(entry.size)
        ).joinToString(" • "),
        endIcon = {
            EnhancedCheckbox(
                checked = selected,
                onCheckedChange = onSelected
            )
        },
        onClick = { onSelected(!selected) },
        shape = shape,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    )
}
