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

package ru.tech.imageresizershrinker.core.ui.widget.value

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.utils.trimTrailingZero
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Counter
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedAlertDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun ValueDialog(
    roundTo: Int?,
    valueRange: ClosedFloatingPointRange<Float>,
    valueState: String,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onValueUpdate: (Float) -> Unit
) {
    val focus = LocalFocusManager.current
    var value by remember(valueState, expanded) { mutableStateOf(valueState.trimTrailingZero()) }
    EnhancedAlertDialog(
        visible = expanded,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures {
                    focus.clearFocus()
                }
            },
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    shape = RoundedCornerShape(16.dp),
                    value = value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                    maxLines = 1,
                    onValueChange = { number ->
                        var tempS = number.trim {
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
                        value = when (temp.size) {
                            1 -> temp[0]
                            2 -> temp[0] + "." + temp[1]
                            else -> {
                                temp[0] + "." + temp[1] + temp.drop(2).joinToString("")
                            }
                        }
                    }
                )
            }
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    onDismiss()
                    onValueUpdate(
                        (value.toFloatOrNull() ?: 0f).roundTo(roundTo).coerceIn(valueRange)
                    )
                },
            ) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

private fun Float.roundTo(
    digits: Int? = 2
) = digits?.let {
    (this * 10f.pow(digits)).roundToInt() / (10f.pow(digits))
} ?: this