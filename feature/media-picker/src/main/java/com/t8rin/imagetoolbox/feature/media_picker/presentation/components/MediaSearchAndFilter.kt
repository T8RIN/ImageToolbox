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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FilterAlt
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonChecked
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonUnchecked
import com.t8rin.imagetoolbox.core.resources.icons.Search
import com.t8rin.imagetoolbox.core.resources.icons.SwapHoriz
import com.t8rin.imagetoolbox.core.resources.icons.SwapVerticalCircle
import com.t8rin.imagetoolbox.core.resources.icons.ViewComfy
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.Disableable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaDateGroup
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaDisplaySettings
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaGrouping
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaOrder
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.OrderType
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent
import kotlinx.coroutines.launch

@Composable
internal fun MediaSearchAndFilter(
    component: MediaPickerComponent,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by component.displaySettings.collectAsStateWithLifecycle()
    var showOptions by rememberSaveable { mutableStateOf(false) }
    val pagerState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        EnhancedIconButton(
            onClick = onSearch,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.size(width = 38.dp, height = 48.dp),
            shape = ShapeDefaults.byIndex(
                index = 0,
                size = 2,
                vertical = false
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null
            )
        }
        EnhancedIconButton(
            onClick = { showOptions = true },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(width = 38.dp, height = 48.dp),
            shape = ShapeDefaults.byIndex(
                index = 1,
                size = 2,
                vertical = false
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.FilterAlt,
                contentDescription = null
            )
        }
    }

    EnhancedModalBottomSheet(
        visible = showOptions,
        onDismiss = { showOptions = it },
        dragHandle = {
            EnhancedModalSheetDragHandle {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PrimaryTabRow(
                        divider = {},
                        modifier = Modifier.padding(horizontal = 8.dp),
                        containerColor = EnhancedBottomSheetDefaults.barContainerColor,
                        selectedTabIndex = pagerState.currentPage,
                        indicator = {
                            TabRowDefaults.PrimaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(
                                    selectedTabIndex = pagerState.currentPage,
                                    matchContentSize = true
                                ),
                                width = Dp.Unspecified,
                                height = 4.dp,
                                shape = AutoCornersShape(topStart = 100f, topEnd = 100f)
                            )
                        }
                    ) {
                        listOf(
                            Icons.Outlined.ViewComfy to R.string.gallery_grouping,
                            Icons.Outlined.SwapVerticalCircle to R.string.sorting
                        ).forEachIndexed { index, (icon, title) ->
                            val selected = pagerState.currentPage == index
                            val color by animateColorAsState(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                            val interactionSource = remember { MutableInteractionSource() }
                            val shape = shapeByInteraction(
                                shape = AutoCornersShape(42.dp),
                                pressedShape = ShapeDefaults.default,
                                interactionSource = interactionSource
                            )
                            Tab(
                                interactionSource = interactionSource,
                                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clip(shape),
                                selected = selected,
                                onClick = {
                                    haptics.longPress()
                                    scope.launch { pagerState.animateScrollToPage(index) }
                                },
                                icon = {
                                    Icon(icon, contentDescription = null, tint = color)
                                },
                                text = {
                                    Text(stringResource(title), color = color)
                                }
                            )
                        }
                    }
                }
            }
        },
        title = {
            TitleItem(
                text = stringResource(R.string.gallery_display_options),
                icon = Icons.Rounded.FilterAlt
            )
        },
        confirmButton = {
            EnhancedButton(onClick = { showOptions = false }) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f, false),
            verticalAlignment = Alignment.Top,
            beyondViewportPageCount = 1
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .enhancedVerticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                if (page == 0) {
                    MediaGroupingOptions(settings, component::updateDisplaySettings)
                } else {
                    MediaSortingOptions(settings.mediaOrder) {
                        component.updateDisplaySettings(
                            component.displaySettings.value.copy(mediaOrder = it)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaGroupingOptions(
    settings: MediaDisplaySettings,
    onChange: (MediaDisplaySettings) -> Unit
) {
    val count = MediaGrouping.entries.size + 1
    val allSelected = settings.dateGroup == MediaDateGroup.None
    PreferenceItem(
        title = stringResource(R.string.all),
        shape = ShapeDefaults.byIndex(0, count),
        containerColor = takeColorFromScheme {
            if (allSelected) secondaryContainer else SafeLocalContainerColor
        },
        endIcon = if (allSelected) Icons.Rounded.RadioButtonChecked
        else Icons.Rounded.RadioButtonUnchecked,
        modifier = Modifier.fillMaxWidth(),
        onClick = { onChange(settings.copy(dateGroup = MediaDateGroup.None)) }
    )
    MediaGrouping.entries.forEachIndexed { index, grouping ->
        val selected = settings.dateGroup != MediaDateGroup.None && settings.grouping == grouping
        PreferenceItem(
            title = stringResource(grouping.title),
            shape = ShapeDefaults.byIndex(index + 1, count),
            containerColor = takeColorFromScheme {
                if (selected) secondaryContainer else SafeLocalContainerColor
            },
            endIcon = if (selected) Icons.Rounded.RadioButtonChecked
            else Icons.Rounded.RadioButtonUnchecked,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onChange(
                    settings.copy(
                        grouping = grouping,
                        dateGroup = settings.dateGroup.takeUnless { it == MediaDateGroup.None }
                            ?: MediaDateGroup.Day
                    )
                )
            },
            bottomContent = {
                if (grouping == MediaGrouping.DateModified || grouping == MediaGrouping.DateTaken) {
                    AnimatedVisibility(
                        visible = selected,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val groups = MediaDateGroup.entries.filter { it != MediaDateGroup.None }
                        EnhancedButtonGroup(
                            items = groups.map { stringResource(it.title) },
                            selectedIndex = groups.indexOf(settings.dateGroup),
                            onIndexChange = { onChange(settings.copy(dateGroup = groups[it])) },
                            isScrollable = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        )
                    }
                }
            }
        )
    }
    MediaOrderOptions(settings.groupOrder) {
        onChange(settings.copy(groupOrder = it))
    }
}

@Composable
private fun MediaSortingOptions(
    mediaOrder: MediaOrder,
    onChange: (MediaOrder) -> Unit
) {
    val order = mediaOrder.orderType
    val items = remember(order) {
        listOf(
            R.string.filename to MediaOrder.Label(order),
            R.string.path to MediaOrder.Path(order),
            R.string.sort_by_size to MediaOrder.Size(order),
            R.string.sort_by_date_modified to MediaOrder.Date(order),
            R.string.caption_date_taken to MediaOrder.DateTaken(order),
            R.string.shuffle to MediaOrder.Random(order)
        )
    }
    items.forEachIndexed { index, (title, item) ->
        val selected = mediaOrder::class == item::class
        PreferenceItem(
            title = stringResource(title),
            shape = ShapeDefaults.byIndex(index, items.size),
            containerColor = takeColorFromScheme {
                if (selected) secondaryContainer else SafeLocalContainerColor
            },
            endIcon = if (selected) Icons.Rounded.RadioButtonChecked
            else Icons.Rounded.RadioButtonUnchecked,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onChange(if (item is MediaOrder.Random) item.copy(order) else item)
            }
        )
    }
    Disableable(enabled = mediaOrder !is MediaOrder.Random) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            MediaOrderOptions(order) {
                onChange(mediaOrder.copy(it))
            }
        }
    }
}

@Composable
private fun MediaOrderOptions(order: OrderType, onChange: (OrderType) -> Unit) {
    TitleItem(
        text = stringResource(R.string.gallery_order),
        icon = Icons.Rounded.SwapHoriz
    )
    listOf(OrderType.Ascending, OrderType.Descending).forEachIndexed { index, type ->
        val selected = type == order
        PreferenceItem(
            title = stringResource(
                if (type == OrderType.Ascending) R.string.gallery_ascending
                else R.string.gallery_descending
            ),
            shape = ShapeDefaults.byIndex(index, 2),
            containerColor = takeColorFromScheme {
                if (selected) tertiaryContainer else SafeLocalContainerColor
            },
            endIcon = if (selected) Icons.Rounded.RadioButtonChecked
            else Icons.Rounded.RadioButtonUnchecked,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onChange(type) }
        )
    }
}

private val MediaDateGroup.title: Int
    get() = when (this) {
        MediaDateGroup.Year -> R.string.gallery_years
        MediaDateGroup.Month -> R.string.gallery_months
        MediaDateGroup.Day -> R.string.gallery_days
        MediaDateGroup.None -> R.string.all
    }

private val MediaGrouping.title: Int
    get() = when (this) {
        MediaGrouping.DateModified -> R.string.sort_by_date_modified
        MediaGrouping.DateTaken -> R.string.caption_date_taken
        MediaGrouping.MimeType -> R.string.sort_by_mime_type
        MediaGrouping.Extension -> R.string.sort_by_extension
    }