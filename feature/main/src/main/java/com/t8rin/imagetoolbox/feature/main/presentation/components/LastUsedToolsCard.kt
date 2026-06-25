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

package com.t8rin.imagetoolbox.feature.main.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.FinanceMode
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.IconShapeContainer
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun LastUsedToolsCard(
    tools: List<UiLastUsedTool>,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = key(tools) {
        rememberLazyListState()
    }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .container(
                    shape = ShapeDefaults.large,
                    resultPadding = 0.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(11.dp))
            IconShapeContainer(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.scaleOnTap(
                    onRelease = {
                        scope.launch {
                            delay(200.milliseconds)
                            onNavigate(Screen.UsageStatistics)
                        }
                    }
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.FinanceMode,
                    contentDescription = null
                )
            }
            Spacer(Modifier.width(4.dp))

            val containerColor = takeColorFromScheme { isNightMode ->
                if (isNightMode) {
                    secondaryContainer.copy(0.4f)
                } else {
                    secondaryContainer.copy(0.6f)
                }
            }

            val contentColor = takeColorFromScheme {
                onSecondaryContainer.blend(
                    color = onTertiaryContainer,
                    fraction = 0.5f
                )
            }.copy(0.9f)

            LazyRow(
                state = lazyListState,
                modifier = Modifier
                    .weight(
                        weight = 1f,
                        fill = false
                    )
                    .fadingEdges(
                        scrollableState = lazyListState,
                        color = SafeLocalContainerColor,
                        length = 32.dp
                    ),
                flingBehavior = enhancedFlingBehavior(),
                contentPadding = PaddingValues(
                    start = 6.dp,
                    end = 6.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(tools) { tool ->
                    val icon = tool.screen.twoToneIcon ?: return@items

                    EnhancedChip(
                        selected = false,
                        onClick = {
                            onNavigate(tool.screen)
                        },
                        unselectedColor = containerColor,
                        selectedColor = Color.Transparent,
                        contentPadding = PaddingValues(
                            start = 6.dp,
                            end = 8.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = contentColor
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = stringResource(tool.screen.title),
                                style = MaterialTheme.typography.labelMedium,
                                color = contentColor
                            )
                        }
                    }
                }
                if (tools.size < 5) {
                    items(5 - tools.size) {
                        val shape = ShapeDefaults.small
                        val density = LocalDensity.current
                        val colorScheme = MaterialTheme.colorScheme
                        val stroke = 2.dp

                        val emptyColor = takeColorFromScheme { isNightMode ->
                            if (isNightMode) {
                                surfaceContainerLowest.copy(0.5f).blend(contentColor.copy(0.2f))
                            } else {
                                surfaceContainerLowest
                            }
                        }

                        Canvas(
                            modifier = Modifier
                                .height(36.dp)
                                .width(128.dp)
                                .padding(stroke / 2)
                        ) {
                            val outline = shape.createOutline(
                                size = size,
                                layoutDirection = layoutDirection,
                                density = density
                            )
                            drawOutline(
                                outline = outline,
                                color = emptyColor
                            )
                            drawOutline(
                                outline = outline,
                                color = colorScheme.surfaceVariant,
                                style = Stroke(
                                    width = stroke.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(
                                        intervals = floatArrayOf(6.dp.toPx(), 6.dp.toPx()),
                                        phase = 0f
                                    )
                                )
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.width(2.dp))
        }
    }
}

@Composable
private fun PreviewContent() {
    Button(
        onClick = {},
        modifier = Modifier.alpha(0f)
    ) { }

    CompositionLocalProvider(
        LocalSettingsState provides LocalSettingsState.current.copy(
            drawContainerShadows = false
        )
    ) {
        LastUsedToolsCard(
            tools = Screen.entries.take(2).map { screen ->
                UiLastUsedTool(
                    screen = screen,
                    openCount = Random.nextInt(1, 100),
                )
            },
            onNavigate = {}
        )
    }
}

@Preview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(true, Color.Green) {
    PreviewContent()
}

@Preview
@Composable
private fun Preview1() = ImageToolboxThemeForPreview(false, Color.Green) {
    PreviewContent()
}