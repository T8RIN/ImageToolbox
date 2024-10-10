/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.controls.selection

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeContainer
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRow
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRowDefaults
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun ColorRowSelector(
    value: Color,
    onValueChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.background_color),
    icon: ImageVector? = Icons.Rounded.FormatColorFill,
    allowAlpha: Boolean = true,
    allowScroll: Boolean = true,
    defaultColors: List<Color> = defaultColorList,
    contentHorizontalPadding: Dp = 12.dp,
    titleFontWeight: FontWeight = FontWeight.Bold,
) {
    val isCompactLayout = LocalSettingsState.current.isCompactSelectorsLayout

    Column(modifier = modifier) {
        if (!isCompactLayout) {
            TitleItem(
                icon = icon,
                text = title,
                modifier = Modifier.padding(top = 12.dp, start = contentHorizontalPadding),
                fontWeight = titleFontWeight
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCompactLayout) {
                Box {
                    AnimatedContent(icon) { icon ->
                        if (icon != null) {
                            val tooltipState = rememberTooltipState()
                            val scope = rememberCoroutineScope()

                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                                tooltip = {
                                    RichTooltip(
                                        colors = TooltipDefaults.richTooltipColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        title = { Text(title) },
                                        text = {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(value)
                                            )
                                        }
                                    )
                                },
                                state = tooltipState,
                                content = {
                                    IconShapeContainer(
                                        enabled = true,
                                        content = {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null
                                            )
                                        },
                                        modifier = Modifier
                                            .padding(
                                                start = contentHorizontalPadding
                                            )
                                            .clip(
                                                LocalSettingsState.current.iconShape?.shape
                                                    ?: CircleShape
                                            )
                                            .combinedClickable(
                                                onLongClick = {
                                                    scope.launch { tooltipState.show() }
                                                },
                                                onClick = {
                                                    scope.launch { tooltipState.show() }
                                                }
                                            )
                                    )
                                }
                            )
                        }
                    }
                    BoxAnimatedVisibility(icon == null) {
                        Text(
                            text = title,
                            modifier = Modifier
                                .padding(
                                    start = contentHorizontalPadding
                                )
                                .widthIn(max = 100.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 16.sp
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
            }
            ColorSelectionRow(
                defaultColors = defaultColors,
                allowAlpha = allowAlpha,
                contentPadding = PaddingValues(
                    start = if (isCompactLayout) 0.dp else contentHorizontalPadding,
                    end = contentHorizontalPadding
                ),
                allowScroll = allowScroll,
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private val defaultColorList by lazy {
    listOf(
        Color(0xFFFFFFFF),
        Color(0xFF768484),
        Color(0xFF333333),
        Color(0xFF000000),
    ).plus(
        ColorSelectionRowDefaults.colorList.reversed().drop(4)
    )
}