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

package ru.tech.imageresizershrinker.feature.checksum_tools.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CompareArrows
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.model.ChecksumType
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.plus
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.FileSelector
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.scaleOnTap
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.checksum_tools.presentation.screenLogic.ChecksumToolsComponent

@Composable
fun ChecksumToolsContent(
    component: ChecksumToolsComponent
) {
    LocalComponentActivity.current

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti
    val scope = essentials.coroutineScope
    val haptics = LocalHapticFeedback.current

    val isPortrait by isPortraitOrientationAsState()

    val pagerState = rememberPagerState { 3 }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = true,
        onGoBack = component.onGoBack,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.marquee()
            ) {
                Text(
                    text = stringResource(R.string.checksum_tools)
                )
                Badge(
                    content = {
                        Text(
                            text = ChecksumType.entries.size.toString()
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .padding(bottom = 12.dp)
                        .scaleOnTap {
                            showConfetti()
                        }
                )
            }
        },
        actions = {

        },
        topAppBarPersistentActions = {
            TopAppBarEmoji()
        },
        imagePreview = {},
        placeImagePreview = false,
        addHorizontalCutoutPaddingIfNoPreview = false,
        showImagePreviewAsStickyHeader = false,
        canShowScreenData = true,
        isPortrait = isPortrait,
        underTopAppBarContent = {
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
                TabRow(
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.statusBars.union(
                            WindowInsets.displayCutout
                        ).only(
                            WindowInsetsSides.Horizontal
                        )
                    ),
                    divider = {},
                    containerColor = Color.Transparent,
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
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
                ) {
                    repeat(3) { index ->
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
                            else -> throw IllegalArgumentException("Not presented index $index of ChecksumPage")
                        }
                        Tab(
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(8.dp)
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
            }
        },
        contentPadding = 0.dp,
        controls = {
            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = 2,
                contentPadding = WindowInsets.navigationBars.union(
                    WindowInsets.displayCutout.only(
                        WindowInsetsSides.Horizontal
                    )
                ).asPaddingValues() + PaddingValues(20.dp)
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (page) {
                        0 -> {
                            val calculateFromUriPage = component.calculateFromUriPage

                            FileSelector(
                                value = calculateFromUriPage.uri?.toString(),
                                onValueChange = component::setUri,
                                subtitle = null
                            )
                            AnimatedContent(calculateFromUriPage.checksum) { checksum ->
                                if (checksum.isNotEmpty()) {
                                    RoundedTextField(
                                        modifier = Modifier
                                            .container(
                                                shape = MaterialTheme.shapes.large,
                                                resultPadding = 8.dp
                                            ),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Text
                                        ),
                                        onValueChange = {},
                                        readOnly = true,
                                        value = checksum,
                                        label = stringResource(R.string.checksum)
                                    )
                                } else {

                                }
                            }
                        }

                        1 -> {

                        }

                        2 -> {

                        }
                    }
                }
            }
        },
        buttons = {

        }
    )
}