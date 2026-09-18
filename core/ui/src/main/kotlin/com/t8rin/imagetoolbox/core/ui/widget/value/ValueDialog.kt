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

package com.t8rin.imagetoolbox.core.ui.widget.value

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddCircle
import com.t8rin.imagetoolbox.core.resources.icons.Counter
import com.t8rin.imagetoolbox.core.resources.icons.RemoveCircle
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import kotlinx.coroutines.android.awaitFrame
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

@Composable
fun ValueDialog(
    valueRange: ClosedFloatingPointRange<Float>,
    valueState: String,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onValueUpdate: (Float) -> Unit,
    steps: Int = 0,
    sliderRange: ClosedFloatingPointRange<Float> = valueRange,
    valueTransformation: (Float) -> Number = { it }
) {
    var value by remember(valueState, expanded) {
        val text = valueState.trimTrailingZero()
        mutableStateOf(
            TextFieldValue(
                text = text,
                selection = TextRange(0, text.length)
            )
        )
    }
    val parsedValue = value.text.toFloatOrNull()?.takeIf(Float::isFinite)
    val step = remember(valueState, sliderRange, steps, valueTransformation) {
        valueStep(
            valueState = valueState,
            valueRange = sliderRange,
            steps = steps,
            valueTransformation = valueTransformation
        )
    }
    val updateValue: (Int) -> Unit = { direction ->
        parsedValue?.let {
            val text = it.stepBy(
                direction = direction,
                valueRange = valueRange,
                step = step,
                valueTransformation = valueTransformation
            ).toString().trimTrailingZero()

            value = TextFieldValue(
                text = text,
                selection = TextRange(text.length)
            )
        }
    }
    val submit: () -> Unit = {
        if (parsedValue != null) {
            onDismiss()
            onValueUpdate(valueTransformation(parsedValue).toFloat().coerceIn(valueRange))
        }
    }

    EnhancedAlertDialog(
        visible = expanded,
        modifier = Modifier.clearFocusOnTap(),
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Counter,
                contentDescription = null
            )
        },
        title = {
            Text(
                stringResource(
                    R.string.value_in_range,
                    valueRange.start.toString().trimTrailingZero(),
                    valueRange.endInclusive.toString().trimTrailingZero()
                )
            )
        },
        text = {
            val requester = remember { FocusRequester() }

            LaunchedEffect(Unit) {
                awaitFrame()
                runCatching { requester.requestFocus() }
            }

            val canSubtract = parsedValue != null && parsedValue > valueRange.start
            val canAdd = parsedValue != null && parsedValue < valueRange.endInclusive
            val addRemoveButtonsColor = MaterialTheme.colorScheme.secondaryContainer

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                AnimatedVisibility(
                    visible = canSubtract || canAdd,
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    EnhancedIconButton(
                        onClick = { updateValue(-1) },
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(40.dp)
                            .alpha(animateFloatAsState(if (canSubtract) 1f else 0.5f).value),
                        shape = if (canSubtract) ShapeDefaults.start else ShapeDefaults.default,
                        containerColor = addRemoveButtonsColor,
                        forceMinimumInteractiveComponentSize = false,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RemoveCircle,
                            contentDescription = null
                        )
                    }
                }
                OutlinedTextField(
                    shape = animateShape(
                        when {
                            canAdd && canSubtract -> ShapeDefaults.center
                            !canAdd && canSubtract -> ShapeDefaults.end
                            canAdd && !canSubtract -> ShapeDefaults.start
                            else -> ShapeDefaults.default
                        }
                    ),
                    value = value,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { submit() }
                    ),
                    textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                    maxLines = 1,
                    onValueChange = { number ->
                        val text = number.text.filterDecimal()
                        value = number.copy(
                            text = text,
                            selection = TextRange(
                                start = number.selection.start.coerceAtMost(text.length),
                                end = number.selection.end.coerceAtMost(text.length)
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(requester)
                )
                AnimatedVisibility(
                    visible = canAdd || canSubtract,
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    EnhancedIconButton(
                        onClick = { updateValue(1) },
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(40.dp)
                            .alpha(animateFloatAsState(if (canAdd) 1f else 0.5f).value),
                        shape = if (canAdd) ShapeDefaults.end else ShapeDefaults.default,
                        containerColor = addRemoveButtonsColor,
                        forceMinimumInteractiveComponentSize = false
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddCircle,
                            contentDescription = null
                        )
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                enabled = parsedValue != null,
                onClick = submit,
            ) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

fun String.filterDecimal(): String {
    var tempS = replace(',', '.').trim {
        it !in listOf(
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            '0',
            '.',
            '-'
        )
    }
    tempS = (if (tempS.firstOrNull() == '-') "-" else "").plus(
        tempS.replace("-", "")
    )
    val temp = tempS.split(".")
    return when (temp.size) {
        1 -> temp[0]
        2 -> temp[0] + "." + temp[1]
        else -> {
            temp[0] + "." + temp[1] + temp.drop(2).joinToString("")
        }
    }
}

internal fun Float.stepBy(
    direction: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    step: Float,
    valueTransformation: (Float) -> Number
): Float {
    return valueTransformation(this + direction * step)
        .toFloat()
        .roundTo(step.decimalPlaces())
        .coerceIn(valueRange)
}

internal fun valueStep(
    valueState: String,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueTransformation: (Float) -> Number
): Float {
    val rangeSize = valueRange.endInclusive - valueRange.start
    if (steps > 0) return rangeSize / (steps + 1)

    val value = valueState.toFloatOrNull()?.takeIf(Float::isFinite)
        ?: return valueState.decimalStep()
    val transformedValue = valueTransformation(value).toFloat()
    var probe = max(
        max(Math.ulp(value), Math.ulp(transformedValue)),
        abs(rangeSize) * 1e-7f
    )

    if (probe > 0f && probe.isFinite()) {
        val hasPlateau = sequenceOf(-probe, probe)
            .map { value + it }
            .filter { it in valueRange }
            .all { valueTransformation(it).toFloat() == transformedValue }

        if (hasPlateau) {
            repeat(32) {
                val step = sequenceOf(-probe, probe)
                    .map { value + it }
                    .filter { it in valueRange }
                    .map {
                        valueTransformation(it).toFloat().decimalDifference(transformedValue)
                    }
                    .filter { it > 0f }
                    .minOrNull()

                if (step != null) return step
                probe *= 2
            }
        }
    }

    return valueState.decimalStep()
}

private fun String.decimalStep(): Float = 1f / 10f.pow(decimalPlaces())

private fun Float.decimalPlaces(): Int = toString().decimalPlaces()

private fun Float.decimalDifference(other: Float): Float = runCatching {
    toString().toBigDecimal().subtract(other.toString().toBigDecimal()).abs().toFloat()
}.getOrDefault(abs(this - other))

private fun String.decimalPlaces(): Int = toBigDecimalOrNull()
    ?.stripTrailingZeros()
    ?.scale()
    ?.coerceAtLeast(0)
    ?: 0