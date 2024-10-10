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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.utils.trimTrailingZero
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeContainer
import ru.tech.imageresizershrinker.core.ui.utils.helper.ProvidesValue
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
    titleHorizontalPadding: Dp = if (behaveAsContainer) 16.dp
    else 6.dp,
    additionalContent: (@Composable () -> Unit)? = null,
) {
    val internalColor = contentColor
        ?: if (color == MaterialTheme.colorScheme.surfaceContainer) {
            contentColorFor(backgroundColor = MaterialTheme.colorScheme.surfaceVariant)
        } else contentColorFor(backgroundColor = color)

    var showValueDialog by rememberSaveable { mutableStateOf(false) }
    val internalState = remember(value) { mutableStateOf(value) }

    val isCompactLayout = LocalSettingsState.current.isCompactSelectorsLayout

    AnimatedVisibility(visible = visible) {
        LocalContentColor.ProvidesValue(internalColor) {
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
                val slider = @Composable {
                    AnimatedContent(
                        targetState = Triple(
                            valueRange,
                            steps,
                            sliderModifier
                        )
                    ) { (valueRange, steps, sliderModifier) ->
                        EnhancedSlider(
                            modifier = if (isCompactLayout) Modifier.padding(
                                top = topContentPadding,
                                start = 12.dp,
                                end = 12.dp
                            )
                            else sliderModifier,
                            enabled = enabled,
                            value = internalState.value.toFloat(),
                            onValueChange = {
                                internalState.value = internalStateTransformation(it)
                                onValueChange(it)
                            },
                            onValueChangeFinished = onValueChangeFinished?.let {
                                {
                                    it(internalState.value.toFloat())
                                }
                            },
                            valueRange = valueRange,
                            steps = steps
                        )
                    }
                }
                if (isCompactLayout) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
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
                                                val trimmed = value.toString().trimTrailingZero()
                                                Text("$trimmed$valueSuffix")
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
                                                    top = topContentPadding,
                                                    start = 12.dp
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
                        AnimatedVisibility(icon == null) {
                            Text(
                                text = title,
                                modifier = Modifier
                                    .padding(
                                        top = topContentPadding,
                                        start = 12.dp
                                    )
                                    .widthIn(max = 100.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 16.sp
                            )
                        }
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            slider()
                        }
                        ValueText(
                            enabled = valueTextTapEnabled && enabled,
                            value = internalStateTransformation(internalState.value.toFloat()),
                            valueSuffix = valueSuffix,
                            modifier = Modifier
                                .width(108.dp)
                                .padding(
                                    top = topContentPadding,
                                    end = 8.dp
                                ),
                            onClick = {
                                showValueDialog = true
                            },
                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                0.38f
                            )
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        AnimatedContent(icon) { icon ->
                            if (icon != null) {
                                IconShapeContainer(
                                    enabled = true,
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
                                    end = titleHorizontalPadding,
                                    start = titleHorizontalPadding
                                )
                                .weight(1f),
                            lineHeight = 18.sp,
                            fontWeight = if (behaveAsContainer) {
                                FontWeight.Medium
                            } else FontWeight.Normal
                        )
                        ValueText(
                            enabled = valueTextTapEnabled && enabled,
                            value = internalStateTransformation(internalState.value.toFloat()),
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
                    slider()
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