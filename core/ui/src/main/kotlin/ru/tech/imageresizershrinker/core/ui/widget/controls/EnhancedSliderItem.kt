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

package ru.tech.imageresizershrinker.core.ui.widget.controls

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeContainer
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.value.ValueDialog
import ru.tech.imageresizershrinker.core.ui.widget.value.ValueText

@Composable
fun EnhancedSliderItem(
    value: Number,
    title: String,
    modifier: Modifier = Modifier,
    sliderModifier: Modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
    icon: ImageVector? = null,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: ((Float) -> Unit)? = null,
    steps: Int = 0,
    topContentPadding: Dp = 8.dp,
    valueSuffix: String = "",
    internalStateTransformation: (Float) -> Number = { it },
    visible: Boolean = true,
    color: Color = Color.Unspecified,
    contentColor: Color? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    valueTextTapEnabled: Boolean = true,
    behaveAsContainer: Boolean = true,
    enabled: Boolean = true,
    additionalContent: (@Composable () -> Unit)? = null
) {
    val internalColor = contentColor
        ?: if (color == MaterialTheme.colorScheme.surfaceContainer) {
            contentColorFor(backgroundColor = MaterialTheme.colorScheme.surfaceVariant)
        } else contentColorFor(backgroundColor = color)

    var showValueDialog by rememberSaveable { mutableStateOf(false) }
    var internalState by remember(value) { mutableStateOf(value) }
    AnimatedVisibility(visible = visible) {
        CompositionLocalProvider(LocalContentColor provides internalColor) {
            Column(
                modifier = modifier
                    .then(
                        if (behaveAsContainer) {
                            Modifier.container(
                                shape = shape,
                                color = color
                            )
                        } else Modifier
                    )
                    .alpha(
                        animateFloatAsState(if (enabled) 1f else 0.5f).value
                    )
                    .animateContentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    AnimatedContent(icon) { icon ->
                        if (icon != null) {
                            IconShapeContainer(
                                enabled = true,
                                underlyingColor = color,
                                content = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.padding(
                                    top = topContentPadding,
                                    start = 12.dp
                                )
                            )
                        }
                    }
                    Text(
                        text = title,
                        modifier = Modifier
                            .padding(
                                top = topContentPadding,
                                end = 16.dp,
                                start = if (behaveAsContainer) 16.dp
                                else 6.dp
                            )
                            .weight(1f),
                        lineHeight = 18.sp,
                        fontWeight = if (behaveAsContainer) {
                            FontWeight.Medium
                        } else FontWeight.Normal
                    )
                    ValueText(
                        enabled = valueTextTapEnabled && enabled,
                        value = internalStateTransformation(internalState.toFloat()),
                        valueSuffix = valueSuffix,
                        modifier = Modifier.padding(
                            top = topContentPadding,
                            end = 8.dp
                        ),
                        onClick = {
                            showValueDialog = true
                        }
                    )
                }
                AnimatedContent(
                    targetState = Triple(
                        valueRange,
                        steps,
                        sliderModifier
                    )
                ) { (valueRange, steps, sliderModifier) ->
                    EnhancedSlider(
                        modifier = sliderModifier,
                        enabled = enabled,
                        value = internalState.toFloat(),
                        onValueChange = {
                            internalState = internalStateTransformation(it)
                            onValueChange(it)
                        },
                        onValueChangeFinished = onValueChangeFinished?.let {
                            {
                                it(internalState.toFloat())
                            }
                        },
                        valueRange = valueRange,
                        steps = steps
                    )
                }
                additionalContent?.invoke()
            }
        }
    }
    ValueDialog(
        roundTo = null,
        valueRange = valueRange,
        valueState = internalStateTransformation(value.toFloat()).toString(),
        expanded = visible && showValueDialog,
        onDismiss = { showValueDialog = false },
        onValueUpdate = {
            onValueChange(it)
            onValueChangeFinished?.invoke(it)
        }
    )
}