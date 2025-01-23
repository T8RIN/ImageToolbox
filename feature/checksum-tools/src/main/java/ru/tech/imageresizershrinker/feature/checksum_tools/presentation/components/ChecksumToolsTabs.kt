/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.checksum_tools.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CompareArrows
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.FolderCompare
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalScreenSize
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke

@Composable
internal fun ChecksumToolsTabs(
    pagerState: PagerState
) {
    val scope = rememberCoroutineScope()

    val haptics = LocalHapticFeedback.current
    val topAppBarColor = MaterialTheme.colorScheme.surfaceContainer
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawHorizontalStroke()
            .background(topAppBarColor)
            .drawWithContent {
                drawContent()
                drawRect(
                    color = topAppBarColor,
                    size = size.copy(height = 6.dp.toPx()),
                    topLeft = Offset(
                        x = 0f,
                        y = -4.dp.toPx()
                    )
                )
            },
        horizontalArrangement = Arrangement.Center
    ) {
        val modifier = Modifier.windowInsetsPadding(
            WindowInsets.statusBars.union(
                WindowInsets.displayCutout
            ).only(
                WindowInsetsSides.Horizontal
            )
        )

        val indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = { tabPositions ->
            if (pagerState.currentPage < tabPositions.size) {
                val width by animateDpAsState(targetValue = tabPositions[pagerState.currentPage].contentWidth)
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    width = width,
                    height = 4.dp,
                    shape = RoundedCornerShape(
                        topStart = 100f,
                        topEnd = 100f
                    )
                )
            }
        }

        val tabs: @Composable () -> Unit = {
            repeat(pagerState.pageCount) { index ->
                val selected = pagerState.currentPage == index
                val color by animateColorAsState(
                    if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else MaterialTheme.colorScheme.onSurface
                )
                val (icon, textRes) = when (index) {
                    0 -> Icons.Rounded.Calculate to R.string.calculate
                    1 -> Icons.Rounded.TextFields to R.string.text_hash
                    2 -> Icons.AutoMirrored.Rounded.CompareArrows to R.string.compare
                    3 -> Icons.Rounded.FolderCompare to R.string.batch_compare
                    else -> throw IllegalArgumentException("Not presented index $index of ChecksumPage")
                }
                Tab(
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clip(CircleShape),
                    selected = selected,
                    onClick = {
                        haptics.performHapticFeedback(
                            HapticFeedbackType.LongPress
                        )
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(textRes),
                            color = color
                        )
                    }
                )
            }
        }

        var disableScroll by remember { mutableStateOf(false) }

        if (disableScroll) {
            TabRow(
                modifier = modifier.padding(horizontal = 8.dp),
                divider = {},
                containerColor = Color.Transparent,
                selectedTabIndex = pagerState.currentPage,
                indicator = indicator,
                tabs = tabs
            )
        } else {
            val screenWidth = LocalScreenSize.current.widthPx
            ScrollableTabRow(
                modifier = modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    if (!disableScroll) {
                        disableScroll = screenWidth > placeable.measuredWidth
                    }
                    layout(placeable.measuredWidth, placeable.measuredHeight) {
                        placeable.placeWithLayer(x = 0, y = 0)
                    }
                },
                edgePadding = 8.dp,
                divider = {},
                containerColor = Color.Transparent,
                selectedTabIndex = pagerState.currentPage,
                indicator = indicator,
                tabs = tabs
            )
        }
    }
}