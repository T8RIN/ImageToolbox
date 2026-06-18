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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.DateRange
import com.t8rin.imagetoolbox.core.resources.icons.FinanceMode
import com.t8rin.imagetoolbox.core.resources.icons.History
import com.t8rin.imagetoolbox.core.resources.icons.TouchApp
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import java.util.Calendar
import kotlin.random.Random

@Composable
internal fun UsageStatisticsContentImpl(
    state: UsageStatisticsState,
    onNavigate: (Screen) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier
) {
    val statistics = state.tools
    val totalToolOpens = statistics.sumOf { it.openCount }
    val lastOpened = statistics.maxByOrNull { it.lastOpenedTimestamp }?.lastOpenedTimestamp
    val maxOpenCount = statistics.maxOfOrNull { it.openCount } ?: 0

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding + PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        flingBehavior = enhancedFlingBehavior()
    ) {
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                UsageStatisticSummaryItem(
                    icon = Icons.Rounded.TouchApp,
                    title = stringResource(R.string.app_opens),
                    value = state.appOpenCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                UsageStatisticSummaryItem(
                    icon = Icons.Rounded.History,
                    title = stringResource(R.string.tool_opens),
                    value = totalToolOpens.toString(),
                    modifier = Modifier.weight(1f)
                )
                UsageStatisticSummaryItem(
                    icon = Icons.Outlined.DateRange,
                    title = stringResource(R.string.last_tool_opened),
                    value = lastOpened.asReadableDate(),
                    modifier = Modifier.weight(1f)
                )
            }
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
                    progress = item.openCount / maxOpenCount.toFloat(),
                    shape = ShapeDefaults.byIndex(index, statistics.size),
                    onClick = { onNavigate(item.screen) }
                )
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
            tools = emptyList()
        ),
        onNavigate = {}
    )
}