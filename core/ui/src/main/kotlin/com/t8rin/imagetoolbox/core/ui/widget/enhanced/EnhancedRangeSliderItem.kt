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

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.IconShapeContainer
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.value.ValueDialog
import com.t8rin.imagetoolbox.core.ui.widget.value.ValueText
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.launch

@Composable
fun EnhancedRangeSliderItem(
    value: ClosedFloatingPointRange<Float>,
    title: String,
    modifier: Modifier = Modifier,
    sliderModifier: Modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
    icon: ImageVector? = null,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangeFinished: ((ClosedFloatingPointRange<Float>) -> Unit)? = null,
    steps: Int = 0,
    topContentPadding: Dp = 8.dp,
    valueSuffix: String = "",
    internalStateTransformation: (ClosedFloatingPointRange<Float>) -> ClosedFloatingPointRange<Float> = { it },
    visible: Boolean = true,
    color: Color = Color.Unspecified,
    contentColor: Color? = null,
    shape: Shape = ShapeDefaults.default,
    valueTextTapEnabled: Boolean = true,
    behaveAsContainer: Boolean = true,
    enabled: Boolean = true,
    titleHorizontalPadding: Dp = if (behaveAsContainer) 16.dp
    else 6.dp,
    valuesPreviewMapping: ImmutableMap<Float, String> = remember { persistentMapOf() },
    additionalContent: (@Composable () -> Unit)? = null,
) {
    val internalColor = contentColor
        ?: if (color == MaterialTheme.colorScheme.surfaceContainer) {
            contentColorFor(backgroundColor = MaterialTheme.colorScheme.surfaceVariant)
        } else contentColorFor(backgroundColor = color)

    var showStartValueDialog by rememberSaveable { mutableStateOf(false) }
    var showEndValueDialog by rememberSaveable { mutableStateOf(false) }
    val internalState = remember(value) { mutableStateOf(internalStateTransformation(value)) }

    val isCompactLayout = LocalSettingsState.current.isCompactSelectorsLayout

    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

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
                    .animateContentSizeNoClip()
                    .then(
                        if (isCompactLayout && icon != null) {
                            Modifier.pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        scope.launch {
                                            tooltipState.show()
                                        }
                                    }
                                )
                            }
                        } else Modifier
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val slider = @Composable {
                    AnimatedContent(
                        targetState = Pair(
                            valueRange,
                            steps
                        )
                    ) { (valueRange, steps) ->
                        EnhancedRangeSlider(
                            modifier = if (isCompactLayout) {
                                Modifier.padding(
                                    top = topContentPadding,
                                    start = 12.dp,
                                    end = 12.dp
                                )
                            } else {
                                sliderModifier
                            },
                            enabled = enabled,
                            value = internalState.value,
                            onValueChange = {
                                internalState.value = internalStateTransformation(it)
                                onValueChange(it)
                            },
                            onValueChangeFinished = onValueChangeFinished?.let {
                                {
                                    it(internalState.value)
                                }
                            },
                            valueRange = valueRange,
                            steps = steps
                        )
                    }
                }
                AnimatedContent(
                    targetState = isCompactLayout,
                    transitionSpec = { fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically() }
                ) { isCompactLayout ->
                    Column {
                        if (isCompactLayout) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                AnimatedContent(icon) { icon ->
                                    if (icon != null) {
                                        TooltipBox(
                                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                                TooltipAnchorPosition.Above
                                            ),
                                            tooltip = {
                                                RichTooltip(
                                                    colors = TooltipDefaults.richTooltipColors(
                                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(
                                                            0.5f
                                                        ),
                                                        titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                                    ),
                                                    title = { Text(title) },
                                                    text = {
                                                        val trimmedStart =
                                                            internalState.value.start.toString()
                                                                .trimTrailingZero()
                                                        val trimmedEnd =
                                                            internalState.value.endInclusive.toString()
                                                                .trimTrailingZero()
                                                        val startPart =
                                                            valuesPreviewMapping[internalState.value.start]
                                                                ?: trimmedStart
                                                        val endPart =
                                                            valuesPreviewMapping[internalState.value.endInclusive]
                                                                ?: trimmedEnd
                                                        Text(
                                                            "$startPart..$endPart $valueSuffix"
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
                                                            top = topContentPadding,
                                                            start = 12.dp
                                                        )
                                                        .clip(
                                                            LocalSettingsState.current.iconShape?.shape
                                                                ?: ShapeDefaults.circle
                                                        )
                                                        .hapticsCombinedClickable(
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
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(
                                        top = topContentPadding,
                                        end = 8.dp
                                    )
                                ) {
                                    ValueText(
                                        enabled = valueTextTapEnabled && enabled,
                                        value = internalStateTransformation(internalState.value).start,
                                        valueSuffix = valueSuffix,
                                        customText = valuesPreviewMapping[internalState.value.start],
                                        modifier = Modifier.width(
                                            if (valuesPreviewMapping.isNotEmpty()) 108.dp
                                            else 72.dp
                                        ),
                                        onClick = {
                                            showStartValueDialog = true
                                        }
                                    )
                                    Text("··")
                                    ValueText(
                                        enabled = valueTextTapEnabled && enabled,
                                        value = internalStateTransformation(internalState.value).endInclusive,
                                        valueSuffix = valueSuffix,
                                        customText = valuesPreviewMapping[internalState.value.endInclusive],
                                        modifier = Modifier.width(
                                            if (valuesPreviewMapping.isNotEmpty()) 108.dp
                                            else 72.dp
                                        ),
                                        onClick = {
                                            showEndValueDialog = true
                                        }
                                    )
                                }
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .height(IntrinsicSize.Max)
                                        .padding(
                                            top = topContentPadding,
                                            end = 14.dp
                                        )
                                ) {
                                    ValueText(
                                        enabled = valueTextTapEnabled && enabled,
                                        value = internalStateTransformation(internalState.value).start,
                                        customText = valuesPreviewMapping[internalState.value.start],
                                        valueSuffix = valueSuffix,
                                        modifier = Modifier.fillMaxHeight(),
                                        onClick = {
                                            showStartValueDialog = true
                                        }
                                    )
                                    Text("··")
                                    ValueText(
                                        enabled = valueTextTapEnabled && enabled,
                                        value = internalStateTransformation(internalState.value).endInclusive,
                                        customText = valuesPreviewMapping[internalState.value.endInclusive],
                                        valueSuffix = valueSuffix,
                                        modifier = Modifier.fillMaxHeight(),
                                        onClick = {
                                            showEndValueDialog = true
                                        }
                                    )
                                }

                            }
                            slider()
                        }
                    }
                }
                additionalContent?.invoke()
            }
        }
    }

    ValueDialog(
        roundTo = null,
        valueRange = valueRange.start..internalState.value.endInclusive,
        valueState = internalState.value.start.toString(),
        expanded = visible && showStartValueDialog,
        onDismiss = { showStartValueDialog = false },
        onValueUpdate = {
            val range =
                it.coerceAtMost(internalState.value.endInclusive)..internalState.value.endInclusive

            onValueChange(range)
            onValueChangeFinished?.invoke(range)
        }
    )
    ValueDialog(
        roundTo = null,
        valueRange = internalState.value.start..valueRange.endInclusive,
        valueState = internalState.value.endInclusive.toString(),
        expanded = visible && showEndValueDialog,
        onDismiss = { showEndValueDialog = false },
        onValueUpdate = {
            val range = internalState.value.start..it.coerceAtLeast(internalState.value.start)

            onValueChange(range)
            onValueChangeFinished?.invoke(range)
        }
    )
}