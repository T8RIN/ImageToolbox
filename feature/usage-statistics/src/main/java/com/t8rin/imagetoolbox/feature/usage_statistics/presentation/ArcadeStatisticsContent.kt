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

package com.t8rin.imagetoolbox.feature.usage_statistics.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.games.ArcadeGameStatistics
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Crown
import com.t8rin.imagetoolbox.core.resources.icons.DeleteSweep
import com.t8rin.imagetoolbox.core.resources.icons.FinanceMode
import com.t8rin.imagetoolbox.core.resources.icons.Flag
import com.t8rin.imagetoolbox.core.resources.icons.Info
import com.t8rin.imagetoolbox.core.resources.icons.StadiaController
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalWindowSizeClass
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.usage_statistics.presentation.components.UsageStatisticProgressIndicator
import com.t8rin.imagetoolbox.feature.usage_statistics.presentation.components.UsageStatisticSummaryItem
import com.t8rin.imagetoolbox.feature.usage_statistics.presentation.components.UsageStatisticsInfoDialog
import com.t8rin.imagetoolbox.feature.usage_statistics.presentation.components.asReadableDate
import com.t8rin.imagetoolbox.feature.usage_statistics.presentation.screenLogic.ArcadeStatisticsComponent

@Composable
fun ArcadeStatisticsContent(component: ArcadeStatisticsComponent) {
    val statistics by component.statistics.collectAsStateWithLifecycle()
    val maxStartedRounds = statistics.maxOfOrNull { it.startedRounds }?.coerceAtLeast(1) ?: 1
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isPortrait by isPortraitOrientationAsState()
    val useGrid = LocalWindowSizeClass.current.widthSizeClass >= WindowWidthSizeClass.Medium ||
            !isPortrait
    var showInfoDialog by rememberSaveable { mutableStateOf(false) }
    var showResetDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            EnhancedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.arcade_statistics),
                        modifier = Modifier.marquee()
                    )
                },
                navigationIcon = {
                    EnhancedIconButton(onClick = component.onGoBack) {
                        Icon(Icons.Rounded.ArrowBack, null)
                    }
                },
                type = EnhancedTopAppBarType.Large,
                scrollBehavior = scrollBehavior,
                actions = { TopAppBarEmoji() }
            )
        },
        floatingActionButton = {
            EnhancedFloatingActionButton(
                onClick = { showInfoDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )
            }
        }
    ) { padding ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            columns = if (useGrid) StaggeredGridCells.Adaptive(400.dp)
            else StaggeredGridCells.Fixed(1),
            contentPadding = padding + PaddingValues(12.dp) + PaddingValues(bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalItemSpacing = 4.dp,
            flingBehavior = enhancedFlingBehavior()
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    UsageStatisticSummaryItem(
                        icon = Icons.Rounded.StadiaController,
                        title = stringResource(R.string.arcade_rounds_started),
                        value = statistics.sumOf { it.startedRounds }.toString(),
                        modifier = Modifier.weight(1f),
                        showZero = true
                    )
                    UsageStatisticSummaryItem(
                        icon = Icons.Rounded.Flag,
                        title = stringResource(R.string.arcade_rounds_finished),
                        value = statistics.sumOf { it.finishedRounds }.toString(),
                        modifier = Modifier.weight(1f),
                        showZero = true
                    )
                }
            }

            if (statistics.isEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    PreferenceItem(
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeDefaults.default,
                        title = stringResource(R.string.arcade_no_statistics),
                        subtitle = stringResource(R.string.arcade_no_statistics_sub),
                        startIcon = Icons.Outlined.FinanceMode
                    )
                }
            }

            itemsIndexed(statistics, key = { _, item -> item.game.name }) { index, item ->
                PreferenceItemOverload(
                    title = stringResource(item.game.title),
                    subtitle = item.subtitle(),
                    onClick = { component.openGame(item.game) },
                    startIcon = { Icon(item.game.icon, null) },
                    shape = if (useGrid) ShapeDefaults.default
                    else ShapeDefaults.byIndex(index, statistics.size),
                    badgeAlignment = Alignment.CenterVertically,
                    badge = {
                        item.highScore?.let { score ->
                            EnhancedBadge(
                                modifier = Modifier.padding(start = 8.dp),
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Icon(
                                    Icons.Rounded.Crown,
                                    null,
                                    modifier = Modifier
                                        .padding(end = 2.dp)
                                        .size(14.dp)
                                )
                                Text(text = score.toString())
                            }
                        }
                    },
                    placeBottomContentInside = true,
                    bottomContent = {
                        UsageStatisticProgressIndicator(item.startedRounds / maxStartedRounds.toFloat())
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    UsageStatisticsInfoDialog(
        visible = showInfoDialog,
        onDismiss = { showInfoDialog = false },
        onWantReset = { showResetDialog = true },
        title = stringResource(R.string.arcade_statistics),
        text = stringResource(R.string.arcade_statistics_info),
        resetContentDescription = null
    )

    ResetDialog(
        visible = showResetDialog,
        onDismiss = { showResetDialog = false },
        onReset = {
            showInfoDialog = false
            showResetDialog = false
            component.resetStatistics()
        },
        title = stringResource(R.string.arcade_reset_statistics),
        text = stringResource(R.string.arcade_reset_statistics_sub),
        icon = Icons.Outlined.DeleteSweep
    )
}

@Composable
private fun ArcadeGameStatistics.subtitle(): String {
    val rounds = stringResource(R.string.arcade_rounds_subtitle, finishedRounds, startedRounds)
    return if (lastPlayed == null) rounds
    else "$rounds • ${lastPlayed.asReadableDate()}"
}
