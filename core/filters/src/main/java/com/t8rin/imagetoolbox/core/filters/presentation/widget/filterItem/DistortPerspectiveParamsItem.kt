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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.DistortPerspective.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.copy
import com.t8rin.imagetoolbox.core.filters.presentation.model.parameterInfo
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun DistortPerspectiveParamsItem(
    value: Value,
    filter: Filter<Value>,
    onFilterChange: (value: Value) -> Unit,
    previewOnly: Boolean
) {
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PerspectivePointFields(
            value = value.topLeft,
            info = filter.parameterInfo(Value::topLeft),
            enabled = !previewOnly,
            onValueChange = { onFilterChange(value.copy(topLeft = it)) }
        )
        PerspectivePointFields(
            value = value.topRight,
            info = filter.parameterInfo(Value::topRight),
            enabled = !previewOnly,
            onValueChange = { onFilterChange(value.copy(topRight = it)) }
        )
        PerspectivePointFields(
            value = value.bottomLeft,
            info = filter.parameterInfo(Value::bottomLeft),
            enabled = !previewOnly,
            onValueChange = { onFilterChange(value.copy(bottomLeft = it)) }
        )
        PerspectivePointFields(
            value = value.bottomRight,
            info = filter.parameterInfo(Value::bottomRight),
            enabled = !previewOnly,
            onValueChange = { onFilterChange(value.copy(bottomRight = it)) }
        )
        PreferenceRowSwitch(
            title = stringResource(filter.parameterInfo(Value::clip).title!!),
            checked = value.clip,
            onClick = { onFilterChange(value.copy(clip = it)) },
            modifier = Modifier.padding(
                top = 8.dp,
                start = 4.dp,
                end = 4.dp
            ),
            applyHorizontalPadding = false,
            startContent = {},
            resultModifier = Modifier.padding(16.dp),
            enabled = !previewOnly
        )
    }
}

@Composable
private fun PerspectivePointFields(
    value: OffsetModel,
    info: FilterParam,
    enabled: Boolean,
    onValueChange: (OffsetModel) -> Unit
) {
    var x by remember(value.x) {
        mutableStateOf(value.x.toString().trimTrailingZero())
    }
    var y by remember(value.y) {
        mutableStateOf(value.y.toString().trimTrailingZero())
    }

    Column {
        TitleItem(
            text = "${stringResource(info.title!!)} (0–1)",
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            PerspectiveCoordinateField(
                value = x,
                label = "X",
                shape = ShapeDefaults.smallStart,
                info = info,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = 8.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 2.dp
                    ),
                onValueChange = {
                    x = it
                    it.toNormalizedFloatOrNull()?.let { newX ->
                        onValueChange(value.copy(x = newX.roundTo(info.roundTo)))
                    }
                }
            )
            PerspectiveCoordinateField(
                value = y,
                label = "Y",
                shape = ShapeDefaults.smallEnd,
                info = info,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = 2.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 8.dp
                    ),
                onValueChange = {
                    y = it
                    it.toNormalizedFloatOrNull()?.let { newY ->
                        onValueChange(value.copy(y = newY.roundTo(info.roundTo)))
                    }
                }
            )
        }
    }
}

@Composable
private fun PerspectiveCoordinateField(
    value: String,
    label: String,
    shape: androidx.compose.ui.graphics.Shape,
    info: FilterParam,
    enabled: Boolean,
    modifier: Modifier,
    onValueChange: (String) -> Unit
) {
    RoundedTextField(
        value = value,
        onValueChange = {
            if (it.isValidCoordinate(info)) onValueChange(it)
        },
        shape = shape,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        label = {
            Text(label)
        },
        modifier = modifier
    )
}

private fun String.isValidCoordinate(info: FilterParam): Boolean {
    if (isEmpty()) return true
    if (!matches(Regex("""\d*(?:[.,]\d{0,${info.roundTo}})?"""))) return false

    return toNormalizedFloatOrNull()?.let { it in info.valueRange } ?: true
}

private fun String.toNormalizedFloatOrNull(): Float? = replace(',', '.').toFloatOrNull()