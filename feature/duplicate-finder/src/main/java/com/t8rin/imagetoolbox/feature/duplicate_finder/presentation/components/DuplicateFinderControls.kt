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

package com.t8rin.imagetoolbox.feature.duplicate_finder.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkStar
import com.t8rin.imagetoolbox.core.resources.icons.DataThresholding
import com.t8rin.imagetoolbox.core.resources.icons.FolderMatch
import com.t8rin.imagetoolbox.core.resources.icons.ImageSearch
import com.t8rin.imagetoolbox.core.resources.icons.WarningAmber
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCheckbox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.helper.DuplicateGrouping
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateGroup
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateKeepStrategy
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateScanMode
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateType
import com.t8rin.imagetoolbox.feature.duplicate_finder.presentation.screenLogic.DuplicateFinderComponent
import kotlin.math.roundToInt

@Composable
internal fun DuplicateFinderControls(
    component: DuplicateFinderComponent,
    isPortrait: Boolean,
    onPickImages: () -> Unit,
    onPreview: (String) -> Unit
) {
    var showErrors by rememberSaveable { mutableStateOf(false) }
    val exactGroups = remember(component.groups) {
        component.groups.filter { it.type == DuplicateType.Exact }
    }
    val similarGroups = remember(component.groups) {
        component.groups.filter { it.type == DuplicateType.Similar }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 20.dp,
            end = 20.dp,
            bottom = if (isPortrait) 128.dp else 20.dp
        ) + WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal).asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        flingBehavior = enhancedFlingBehavior()
    ) {
        item(key = "static") {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                EnhancedButtonGroup(
                    enabled = true,
                    items = listOf(
                        stringResource(R.string.duplicate_scan_exact_only),
                        stringResource(R.string.duplicate_scan_exact_and_similar)
                    ),
                    selectedIndex = DuplicateScanMode.entries.indexOf(component.scanMode),
                    title = {
                        TitleItem(
                            text = stringResource(R.string.duplicate_scan_mode),
                            icon = Icons.Outlined.ImageSearch,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 12.dp,
                                    end = 12.dp,
                                    top = 12.dp,
                                    bottom = 8.dp
                                )
                        )
                    },
                    onIndexChange = {
                        component.updateScanMode(DuplicateScanMode.entries[it])
                    },
                    isScrollable = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(ShapeDefaults.large)
                )

                DataSelector(
                    value = component.keepStrategy,
                    onValueChange = component::updateKeepStrategy,
                    entries = DuplicateKeepStrategy.entries,
                    title = stringResource(R.string.duplicate_keep_strategy),
                    titleIcon = Icons.Outlined.BookmarkStar,
                    itemContentText = {
                        stringResource(
                            when (it) {
                                DuplicateKeepStrategy.BestQuality -> R.string.duplicate_keep_best_quality
                                DuplicateKeepStrategy.SmallestFile -> R.string.duplicate_keep_smallest_file
                                DuplicateKeepStrategy.Newest -> R.string.duplicate_keep_newest
                                DuplicateKeepStrategy.Oldest -> R.string.duplicate_keep_oldest
                                DuplicateKeepStrategy.FirstSelected -> R.string.duplicate_keep_first_selected
                            }
                        )
                    },
                    spanCount = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(component.scanMode == DuplicateScanMode.ExactAndSimilar) {
                    EnhancedSliderItem(
                        value = component.sensitivity,
                        title = stringResource(R.string.duplicate_finder_sensitivity),
                        valueRange = DuplicateGrouping.MIN_SENSITIVITY.toFloat()..
                                DuplicateGrouping.MAX_SENSITIVITY.toFloat(),
                        steps = DuplicateGrouping.MAX_SENSITIVITY -
                                DuplicateGrouping.MIN_SENSITIVITY - 1,
                        onValueChange = { component.updateSensitivity(it.roundToInt()) },
                        icon = Icons.Outlined.DataThresholding,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        component.analysisResult?.let { result ->
            item(key = "result-summary") {
                ResultSummary(
                    exactGroupCount = exactGroups.size,
                    similarGroupCount = similarGroups.size,
                    reclaimableBytes = component.groups.sumOf(DuplicateGroup::reclaimableBytes),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }

            if (result.errors.isNotEmpty()) {
                item(key = "errors") {
                    PreferenceItem(
                        title = stringResource(
                            R.string.duplicate_analysis_errors,
                            result.errors.size
                        ),
                        startIcon = Icons.Outlined.WarningAmber,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        onClick = { showErrors = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                    )
                }
            }

            if (component.groups.isEmpty()) {
                item(key = "empty-results") {
                    PreferenceItem(
                        title = stringResource(R.string.no_duplicates_found),
                        subtitle = stringResource(
                            if (component.scanMode == DuplicateScanMode.ExactOnly) {
                                R.string.no_exact_duplicates_found_sub
                            } else R.string.no_duplicates_found_sub
                        ),
                        startIcon = Icons.Outlined.ImageSearch,
                        onClick = onPickImages,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                    )
                }
            } else {
                duplicateGroups(
                    type = DuplicateType.Exact,
                    groups = exactGroups,
                    selectedUris = component.selectedUris,
                    onToggleGroupSelection = component::toggleGroupSelection,
                    onToggleSelection = component::toggleSelection,
                    onPreview = onPreview
                )
                duplicateGroups(
                    type = DuplicateType.Similar,
                    groups = similarGroups,
                    selectedUris = component.selectedUris,
                    onToggleGroupSelection = component::toggleGroupSelection,
                    onToggleSelection = component::toggleSelection,
                    onPreview = onPreview
                )
            }
        }
    }

    DuplicateAnalysisErrorsSheet(
        errors = component.analysisResult?.errors.orEmpty(),
        visible = showErrors,
        onDismiss = { showErrors = false }
    )
}

private fun LazyListScope.duplicateGroups(
    type: DuplicateType,
    groups: List<DuplicateGroup>,
    selectedUris: Set<String>,
    onToggleGroupSelection: (DuplicateGroup) -> Unit,
    onToggleSelection: (String) -> Unit,
    onPreview: (String) -> Unit
) {
    groups.forEachIndexed { groupIndex, group ->
        item(key = "group-title-${type.name}-$groupIndex-${group.recommendedUri}") {
            val selectableUris = remember(group) {
                group.items
                    .map { it.uri }
                    .filterNot { it == group.recommendedUri }
            }
            val isGroupSelected = remember(selectableUris, selectedUris) {
                selectableUris.isNotEmpty() && selectableUris.all {
                    it in selectedUris
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
                    .container(
                        shape = ShapeDefaults.circle,
                        resultPadding = 0.dp,
                        color = takeColorFromScheme {
                            if (isGroupSelected) secondaryContainer.copy(0.5f) else surfaceContainer
                        }
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    )
                    .animateItem(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val imagesType = stringResource(
                    if (type == DuplicateType.Exact) R.string.exact_copies
                    else R.string.similar_images
                )

                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (type == DuplicateType.Exact) {
                        Icons.Rounded.FolderMatch
                    } else {
                        Icons.Rounded.ImageSearch
                    },
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        R.string.duplicate_group_summary,
                        "${group.items.size} $imagesType",
                        rememberHumanFileSize(group.reclaimableBytes)
                    ),
                    style = PreferenceItemDefaults.TitleFontStyle,
                    modifier = Modifier.weight(1f, false)
                )
                EnhancedCheckbox(
                    checked = isGroupSelected,
                    onCheckedChange = { onToggleGroupSelection(group) }
                )
            }
        }

        itemsIndexed(
            items = group.sortedItems,
            key = { _, item -> "${type.name}-$groupIndex-${item.uri}-${group.recommendedUri}" }
        ) { index, item ->
            DuplicateItemRow(
                item = item,
                isRecommended = item.uri == group.recommendedUri,
                isSelected = item.uri in selectedUris,
                shape = ShapeDefaults.byIndex(index, group.sortedItems.size),
                onToggleSelection = { onToggleSelection(item.uri) },
                onPreview = { onPreview(item.uri) },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
            )
        }
    }
}
