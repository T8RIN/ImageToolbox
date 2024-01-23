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

package ru.tech.imageresizershrinker.feature.draw.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.FreeArrow
import ru.tech.imageresizershrinker.core.ui.icons.material.FreeDoubleArrow
import ru.tech.imageresizershrinker.core.ui.icons.material.FreeDraw
import ru.tech.imageresizershrinker.core.ui.icons.material.Lasso
import ru.tech.imageresizershrinker.core.ui.icons.material.Line
import ru.tech.imageresizershrinker.core.ui.icons.material.LineArrow
import ru.tech.imageresizershrinker.core.ui.icons.material.LineDoubleArrow
import ru.tech.imageresizershrinker.core.ui.icons.material.Square
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.SupportingButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.materialShadow
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawPathModeSelector(
    modifier: Modifier,
    values: List<ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode> = ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.entries,
    value: ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode,
    onValueChange: (ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val state = rememberSaveable { mutableStateOf(false) }

    val settingsState = LocalSettingsState.current
    Column(
        modifier = modifier
            .container(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.draw_path_mode),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            SupportingButton(
                onClick = {
                    state.value = true
                }
            )
        }
        Box {
            SingleChoiceSegmentedButtonRow(
                space = max(settingsState.borderWidth, 1.dp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 6.dp, end = 6.dp, bottom = 8.dp, top = 8.dp)
            ) {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentEnforcement provides false
                ) {
                    values.forEachIndexed { index, item ->
                        val selected by remember(value, item) {
                            derivedStateOf {
                                value::class.isInstance(item)
                            }
                        }
                        val shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = values.size
                        )
                        SegmentedButton(
                            onClick = {
                                haptics.performHapticFeedback(
                                    HapticFeedbackType.LongPress
                                )
                                onValueChange(item)
                            },
                            selected = selected,
                            icon = {},
                            border = BorderStroke(
                                width = settingsState.borderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant()
                            ),
                            colors = SegmentedButtonDefaults.colors(
                                activeBorderColor = MaterialTheme.colorScheme.outlineVariant(),
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    1.dp
                                ),
                                activeContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    10.dp
                                )
                            ),
                            modifier = Modifier.materialShadow(
                                shape = shape,
                                elevation = animateDpAsState(
                                    if (settingsState.borderWidth >= 0.dp || !settingsState.drawButtonShadows) 0.dp
                                    else if (selected) 2.dp
                                    else 1.dp
                                ).value
                            ),
                            shape = shape
                        ) {
                            Icon(
                                imageVector = item.getIcon(),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(8.dp)
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to MaterialTheme.colorScheme.surfaceContainer,
                            1f to Color.Transparent
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(8.dp)
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to Color.Transparent,
                            1f to MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
            )
        }
    }
    SimpleSheet(
        sheetContent = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                values.forEachIndexed { index, item ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .container(
                                shape = ContainerShapeDefaults.shapeForIndex(
                                    index = index,
                                    size = values.size
                                ),
                                resultPadding = 0.dp
                            )
                    ) {
                        TitleItem(text = stringResource(item.getTitle()), icon = item.getIcon())
                        Text(
                            text = stringResource(item.getSubtitle()),
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        visible = state,
        title = {
            TitleItem(text = stringResource(R.string.draw_path_mode))
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { state.value = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    )
}

private fun ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.getSubtitle(): Int =
    when (this) {
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.DoubleLinePointingArrow -> R.string.double_line_arrow_sub
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.DoublePointingArrow -> R.string.double_arrow_sub
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Free -> R.string.free_drawing_sub
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Line -> R.string.line_sub
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.LinePointingArrow -> R.string.line_arrow_sub
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.PointingArrow -> R.string.arrow_sub
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.OutlinedOval -> R.string.outlined_oval_sub
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.OutlinedRect -> R.string.outlined_rect_sub
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Oval -> R.string.oval_sub
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Rect -> R.string.rect_sub
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Lasso -> R.string.lasso_sub
    }

private fun ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.getTitle(): Int =
    when (this) {
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.DoubleLinePointingArrow -> R.string.double_line_arrow
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.DoublePointingArrow -> R.string.double_arrow
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Free -> R.string.free_drawing
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Line -> R.string.line
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.LinePointingArrow -> R.string.line_arrow
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.PointingArrow -> R.string.arrow
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.OutlinedOval -> R.string.outlined_oval
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.OutlinedRect -> R.string.outlined_rect
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Oval -> R.string.oval
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Rect -> R.string.rect
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Lasso -> R.string.lasso
    }

private fun ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.getIcon(): ImageVector =
    when (this) {
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.DoubleLinePointingArrow -> Icons.Rounded.LineDoubleArrow
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.DoublePointingArrow -> Icons.Rounded.FreeDoubleArrow
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Free -> Icons.Rounded.FreeDraw
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Line -> Icons.Rounded.Line
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.LinePointingArrow -> Icons.Rounded.LineArrow
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.PointingArrow -> Icons.Rounded.FreeArrow
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.OutlinedOval -> Icons.Rounded.RadioButtonUnchecked
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.OutlinedRect -> Icons.Rounded.CheckBoxOutlineBlank
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Oval -> Icons.Rounded.Circle
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Rect -> Icons.Rounded.Square
        ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode.Lasso -> Icons.Rounded.Lasso
    }