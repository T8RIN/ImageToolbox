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

package com.t8rin.imagetoolbox.core.ui.widget.value

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Counter
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
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
    var value by remember(valueState, expanded) { mutableStateOf(valueState.trimTrailingZero()) }
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    shape = ShapeDefaults.default,
                    value = value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                    maxLines = 1,
                    onValueChange = { number ->
                        value = number.filterDecimal()
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

fun String.filterDecimal(): String {
    var tempS = trim {
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

private fun Float.roundTo(
    digits: Int? = 2
) = digits?.let {
    (this * 10f.pow(digits)).roundToInt() / (10f.pow(digits))
} ?: this