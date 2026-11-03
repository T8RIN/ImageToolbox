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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Done
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField

@Composable
fun FloatArrayItem(
    value: FloatArray,
    columns: Int,
    default: () -> FloatArray,
    previewOnly: Boolean,
    onFilterChange: (FloatArray) -> Unit
) {
    var text by rememberSaveable(value, columns) {
        mutableStateOf(
            value.asList().chunked(columns).joinToString(",\n") { row ->
                row.joinToString(", ")
            }
        )
    }

    fun applyText(input: String) {
        if (previewOnly) return
        val matrix = default().copyOf()
        input.split(',').take(matrix.size).forEachIndexed { index, part ->
            part.trim().toFloatOrNull()?.takeIf(Float::isFinite)?.let { matrix[index] = it }
        }
        if (!matrix.contentEquals(value)) onFilterChange(matrix)
    }

    RoundedTextField(
        enabled = !previewOnly,
        modifier = Modifier.padding(16.dp),
        singleLine = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = { text = it },
        onLoseFocusTransformation = {
            applyText(this)
            this
        },
        endIcon = {
            EnhancedIconButton(
                onClick = {
                    applyText(text)
                },
                enabled = !previewOnly
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