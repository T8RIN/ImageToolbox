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

package ru.tech.imageresizershrinker.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import kotlin.math.absoluteValue

@Composable
internal fun FloatArrayItem(
    value: FloatArray,
    filter: UiFilter<FloatArray>,
    onFilterChange: (value: FloatArray) -> Unit,
    previewOnly: Boolean
) {
    val rows = filter.paramsInfo[0].valueRange.start.toInt().absoluteValue
    var text by rememberSaveable(value) {
        mutableStateOf(
            value.let {
                var string = ""
                it.forEachIndexed { index, float ->
                    string += "$float, "
                    if (index % rows == (rows - 1)) string += "\n"
                }
                string.dropLast(3)
            }
        )
    }
    RoundedTextField(
        enabled = !previewOnly,
        modifier = Modifier.padding(16.dp),
        singleLine = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = { text = it },
        onLoseFocusTransformation = {
            val matrix = filter.newInstance().value as FloatArray
            this.trim { it.isWhitespace() }.split(",").mapIndexed { index, num ->
                num.toFloatOrNull()?.let {
                    matrix[index] = it
                }
            }
            onFilterChange(matrix)
            this
        },
        endIcon = {
            EnhancedIconButton(
                onClick = {
                    val matrix = filter.newInstance().value as FloatArray
                    text.trim { it.isWhitespace() }.split(",")
                        .mapIndexed { index, num ->
                            num.toFloatOrNull()?.let {
                                matrix[index] = it
                            }
                        }
                    onFilterChange(matrix)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Done,
                    contentDescription = "Done"
                )
            }
        },
        value = text,
        label = {
            Text(stringResource(R.string.float_array_of))
        }
    )
}