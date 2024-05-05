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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.FreeArrow
import ru.tech.imageresizershrinker.core.resources.icons.FreeDoubleArrow
import ru.tech.imageresizershrinker.core.resources.icons.FreeDraw
import ru.tech.imageresizershrinker.core.resources.icons.Lasso
import ru.tech.imageresizershrinker.core.resources.icons.Line
import ru.tech.imageresizershrinker.core.resources.icons.LineArrow
import ru.tech.imageresizershrinker.core.resources.icons.LineDoubleArrow
import ru.tech.imageresizershrinker.core.resources.icons.Square
import ru.tech.imageresizershrinker.core.resources.icons.Triangle
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.SupportingButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode

@Composable
fun DrawPathModeSelector(
    modifier: Modifier,
    values: List<DrawPathMode> = DrawPathMode.entries,
    value: DrawPathMode,
    onValueChange: (DrawPathMode) -> Unit,
    containerColor: Color = Color.Unspecified
) {
    val state = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(value, values) {
        if (values.find { it::class.isInstance(value) } == null) {
            values.firstOrNull()?.let { onValueChange(it) }
        }
    }

    Column(
        modifier = modifier
            .container(
                shape = RoundedCornerShape(24.dp),
                color = containerColor
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ToggleGroupButton(
            enabled = true,
            itemCount = values.size,
            title = {
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
            },
            selectedIndex = remember(values, value) {
                derivedStateOf {
                    values.indexOfFirst {
                        value::class.isInstance(it)
                    }
                }
            }.value,
            buttonIcon = {},
            activeButtonColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            itemContent = {
                Icon(
                    imageVector = values[it].getIcon(),
                    contentDescription = null
                )
            },
            indexChanged = {
                onValueChange(values[it])
            }
        )
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

private fun DrawPathMode.getSubtitle(): Int =
    when (this) {
        DrawPathMode.DoubleLinePointingArrow -> R.string.double_line_arrow_sub
        DrawPathMode.DoublePointingArrow -> R.string.double_arrow_sub
        DrawPathMode.Free -> R.string.free_drawing_sub
        DrawPathMode.Line -> R.string.line_sub
        DrawPathMode.LinePointingArrow -> R.string.line_arrow_sub
        DrawPathMode.PointingArrow -> R.string.arrow_sub
        DrawPathMode.OutlinedOval -> R.string.outlined_oval_sub
        DrawPathMode.OutlinedRect -> R.string.outlined_rect_sub
        DrawPathMode.Oval -> R.string.oval_sub
        DrawPathMode.Rect -> R.string.rect_sub
        DrawPathMode.Lasso -> R.string.lasso_sub
        DrawPathMode.OutlinedTriangle -> R.string.outlined_triangle_sub
        DrawPathMode.Triangle -> R.string.triangle_sub
    }

private fun DrawPathMode.getTitle(): Int =
    when (this) {
        DrawPathMode.DoubleLinePointingArrow -> R.string.double_line_arrow
        DrawPathMode.DoublePointingArrow -> R.string.double_arrow
        DrawPathMode.Free -> R.string.free_drawing
        DrawPathMode.Line -> R.string.line
        DrawPathMode.LinePointingArrow -> R.string.line_arrow
        DrawPathMode.PointingArrow -> R.string.arrow
        DrawPathMode.OutlinedOval -> R.string.outlined_oval
        DrawPathMode.OutlinedRect -> R.string.outlined_rect
        DrawPathMode.Oval -> R.string.oval
        DrawPathMode.Rect -> R.string.rect
        DrawPathMode.Lasso -> R.string.lasso
        DrawPathMode.OutlinedTriangle -> R.string.outlined_triangle
        DrawPathMode.Triangle -> R.string.triangle
    }

private fun DrawPathMode.getIcon(): ImageVector =
    when (this) {
        DrawPathMode.DoubleLinePointingArrow -> Icons.Rounded.LineDoubleArrow
        DrawPathMode.DoublePointingArrow -> Icons.Rounded.FreeDoubleArrow
        DrawPathMode.Free -> Icons.Rounded.FreeDraw
        DrawPathMode.Line -> Icons.Rounded.Line
        DrawPathMode.LinePointingArrow -> Icons.Rounded.LineArrow
        DrawPathMode.PointingArrow -> Icons.Rounded.FreeArrow
        DrawPathMode.OutlinedOval -> Icons.Rounded.RadioButtonUnchecked
        DrawPathMode.OutlinedRect -> Icons.Rounded.CheckBoxOutlineBlank
        DrawPathMode.Oval -> Icons.Rounded.Circle
        DrawPathMode.Rect -> Icons.Rounded.Square
        DrawPathMode.Lasso -> Icons.Rounded.Lasso
        DrawPathMode.Triangle -> Icons.Rounded.Triangle
        DrawPathMode.OutlinedTriangle -> Icons.Outlined.Triangle
    }