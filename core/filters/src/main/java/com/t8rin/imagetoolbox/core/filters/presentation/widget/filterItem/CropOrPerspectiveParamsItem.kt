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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.OffsetModel
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CropOrPerspective.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.copy
import com.t8rin.imagetoolbox.core.filters.presentation.model.parameterInfo
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun CropOrPerspectiveParamsItem(
    value: Value,
    filter: Filter<Value>,
    onFilterChange: (Value) -> Unit,
    previewOnly: Boolean
) {
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CropPointFields(value.topLeft, filter.parameterInfo(Value::topLeft), previewOnly) {
            onFilterChange(value.copy(topLeft = it))
        }
        CropPointFields(value.topRight, filter.parameterInfo(Value::topRight), previewOnly) {
            onFilterChange(value.copy(topRight = it))
        }
        CropPointFields(value.bottomLeft, filter.parameterInfo(Value::bottomLeft), previewOnly) {
            onFilterChange(value.copy(bottomLeft = it))
        }
        CropPointFields(value.bottomRight, filter.parameterInfo(Value::bottomRight), previewOnly) {
            onFilterChange(value.copy(bottomRight = it))
        }
        FilterBooleanParamItem(
            value.isAbsolute,
            filter.parameterInfo(Value::isAbsolute),
            previewOnly
        ) {
            onFilterChange(value.copy(isAbsolute = it))
        }
    }
}

@Composable
private fun CropPointFields(
    value: OffsetModel,
    info: FilterParam,
    previewOnly: Boolean,
    onValueChange: (OffsetModel) -> Unit
) {
    var x by remember(value.x) { mutableStateOf(value.x.toString().trimTrailingZero()) }
    var y by remember(value.y) { mutableStateOf(value.y.toString().trimTrailingZero()) }

    Column {
        TitleItem(
            text = stringResource(requireNotNull(info.title)),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Row(modifier = Modifier.padding(8.dp)) {
            RoundedTextField(
                value = x,
                onValueChange = {
                    x = it
                    it.replace(',', '.').toFloatOrNull()?.takeIf(Float::isFinite)
                        ?.let { coordinate ->
                            onValueChange(value.copy(x = coordinate))
                        }
                },
                enabled = !previewOnly,
                shape = ShapeDefaults.smallStart,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text("X") },
                modifier = Modifier.weight(1f).padding(end = 2.dp)
            )
            RoundedTextField(
                value = y,
                onValueChange = {
                    y = it
                    it.replace(',', '.').toFloatOrNull()?.takeIf(Float::isFinite)
                        ?.let { coordinate ->
                            onValueChange(value.copy(y = coordinate))
                        }
                },
                enabled = !previewOnly,
                shape = ShapeDefaults.smallEnd,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text("Y") },
                modifier = Modifier.weight(1f).padding(start = 2.dp)
            )
        }
    }
}