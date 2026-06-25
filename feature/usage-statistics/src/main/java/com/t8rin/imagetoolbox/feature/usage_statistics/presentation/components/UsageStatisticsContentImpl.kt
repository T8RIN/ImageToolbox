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

package com.t8rin.imagetoolbox.feature.usage_statistics.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Crown
import com.t8rin.imagetoolbox.core.resources.icons.Database
import com.t8rin.imagetoolbox.core.resources.icons.DateRange
import com.t8rin.imagetoolbox.core.resources.icons.FinanceMode
import com.t8rin.imagetoolbox.core.resources.icons.Interests
import com.t8rin.imagetoolbox.core.resources.icons.LocalFireDepartment
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.resources.icons.ServiceToolbox
import com.t8rin.imagetoolbox.core.resources.icons.TouchApp
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalWindowSizeClass
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import java.util.Calendar
import kotlin.random.Random
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed as staggeredItemsIndexed

@Composable
internal fun UsageStatisticsContentImpl(
    state: UsageStatisticsState,
    onNavigate: (Screen) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier
) {
    val statistics = state.tools
    val widthSizeClass = LocalWindowSizeClass.current.widthSizeClass

    val isPortrait by isPortraitOrientationAsState()
    val useGrid = widthSizeClass >= WindowWidthSizeClass.Medium || !isPortrait
    val padding = contentPadding + PaddingValues(12.dp)

    val statsCard = @Composable {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            UsageStatisticSummaryItem(
                icon = Icons.Rounded.TouchApp,
                title = stringResource(R.string.app_opens),
                value = state.appOpenCount.toString(),
                modifier = Modifier.weight(1f)
            )
            UsageStatisticSummaryItem(
                icon = Icons.Rounded.Interests,
                title = stringResource(R.string.tool_opens),
                value = state.totalToolOpens.toString(),
                modifier = Modifier.weight(1f)
            )
            UsageStatisticSummaryItem(
                icon = Icons.Rounded.ServiceToolbox,
                title = stringResource(R.string.tools_used),
                value = statistics.size.toString(),
                modifier = Modifier.weight(1f)
            )
            UsageStatisticSummaryItem(
                icon = Icons.Rounded.Save,
                title = stringResource(R.string.successful_saves),
                value = state.successfulSavesCount.toString(),
                modifier = Modifier.weight(1f)
            )
            UsageStatisticSummaryItem(
                icon = Icons.Rounded.LocalFireDepartment,
                title = stringResource(R.string.activity_streak),
                value = state.activityStreak.toString(),
                modifier = Modifier.weight(1f)
            )
            UsageStatisticSummaryItem(
                icon = Icons.Rounded.Database,
                title = stringResource(R.string.data_saved),
                value = rememberHumanFileSize(state.savedBytes),
                modifier = Modifier.weight(1f)
            )
            UsageStatisticSummaryItem(
                icon = Icons.Rounded.Crown,
                title = stringResource(R.string.top_format),
                value = state.topSavedFormat,
                modifier = Modifier.weight(1f)
            )
            UsageStatisticSummaryItem(
                icon = Icons.Rounded.DateRange,
                title = stringResource(R.string.last_tool_opened),
                value = state.lastOpened.asReadableDate(),
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (useGrid) {
        LazyVerticalStaggeredGrid(
            modifier = modifier.fillMaxSize(),
            contentPadding = padding,
            columns = StaggeredGridCells.Adaptive(400.dp),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            item(
                span = StaggeredGridItemSpan.FullLine
            ) {
                statsCard()
            }

            if (statistics.isEmpty()) {
                item(
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    PreferenceItem(
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeDefaults.default,
                        title = stringResource(R.string.no_usage_statistics),
                        subtitle = stringResource(R.string.no_usage_statistics_sub),
                        startIcon = Icons.Outlined.FinanceMode,
                    )
                }
            } else {
                staggeredItemsIndexed(
                    items = statistics,
                    key = { _, item -> item.screen.id }
                ) { _, item ->
                    ToolUsageStatisticItem(
                        item = item,
                        progress = item.openCount / state.maxOpenCount.toFloat(),
                        shape = ShapeDefaults.default,
                        onClick = { onNavigate(item.screen) }
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            item {
                statsCard()
            }

            if (statistics.isEmpty()) {
                item {
                    PreferenceItem(
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeDefaults.default,
                        title = stringResource(R.string.no_usage_statistics),
                        subtitle = stringResource(R.string.no_usage_statistics_sub),
                        startIcon = Icons.Outlined.FinanceMode,
                    )
                }
            } else {
                itemsIndexed(
                    items = statistics,
                    key = { _, item -> item.screen.id }
                ) { index, item ->
                    ToolUsageStatisticItem(
                        item = item,
                        progress = item.openCount / state.maxOpenCount.toFloat(),
                        shape = ShapeDefaults.byIndex(index, statistics.size),
                        onClick = { onNavigate(item.screen) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(true, keyColor = Color.Green) {
    UsageStatisticsContentImpl(
        onNavigate = {},
        state = remember {
            UsageStatisticsState(
                appOpenCount = 840,
                successfulSavesCount = 320,
                activityStreak = 12,
                savedBytes = 128_400_000,
                topSavedFormat = "JPG",
                tools = Screen.entries.map { screen ->
                    UiToolUsageStatistic(
                        screen = screen,
                        openCount = Random.nextInt(1, 100),
                        lastOpenedTimestamp = 0
                    )
                }.sortedByDescending { it.openCount }.mapIndexed { index, item ->
                    item.copy(
                        lastOpenedTimestamp = when (index % 5) {
                            1 -> Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                add(Calendar.DAY_OF_YEAR, -1)
                            }.timeInMillis

                            2 -> Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                add(Calendar.DAY_OF_YEAR, -7)
                            }.timeInMillis

                            3 -> Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                set(Calendar.YEAR, get(Calendar.YEAR) - 1)
                                set(Calendar.MONTH, Calendar.MAY)
                                set(Calendar.DAY_OF_MONTH, 4)
                            }.timeInMillis

                            else -> System.currentTimeMillis()
                        }
                    )
                }
            )
        }
    )
}

@Preview
@Composable
private fun Preview1() = ImageToolboxThemeForPreview(true) {
    UsageStatisticsContentImpl(
        state = UsageStatisticsState(
            appOpenCount = 840,
            activityStreak = 3,
            savedBytes = 12_000_000,
            tools = emptyList()
        ),
        onNavigate = {}
    )
}