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

package com.t8rin.imagetoolbox.feature.filters.presentation.components

import androidx.compose.runtime.Composable
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.OptionalImageParamItem
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.model.FilterField
import com.t8rin.imagetoolbox.feature.filters.data.model.StructuredParameters
import kotlin.reflect.KProperty1

private class FieldEditor<T>(
    val property: KProperty1<*, T>,
    val content: @Composable (T, UiFilter<*>, Boolean, (T) -> Unit) -> Unit
)

private val <T> KProperty1<*, T>.editor:
            (@Composable (T, UiFilter<*>, Boolean, (T) -> Unit) -> Unit) -> FieldEditor<T>
    get() = { FieldEditor(this, it) }

private val fieldEditors: Map<KProperty1<*, *>, FieldEditor<*>> = listOf<FieldEditor<*>>(
    Filter.Displacement.Value::verticalMap.editor { value, _, previewOnly, onValueChange ->
        OptionalImageParamItem(
            value = value,
            title = R.string.vertical_displacement_map,
            toggleTitle = R.string.use_separate_vertical_map,
            previewOnly = previewOnly,
            onValueChange = onValueChange
        )
    }
).associateBy { it.property }

internal fun requireFieldEditors(parameters: StructuredParameters<*>) {
    val missing = parameters.fields.filter { (property, field) ->
        !field.hasEditor && property !in fieldEditors
    }.keys
    require(missing.isEmpty()) {
        "Missing editors for ${missing.map { it.name }}; register a field editor or use Filter.Schema.Custom"
    }
}

@Composable
internal fun <T> FilterFieldItem(
    property: KProperty1<*, T>,
    field: FilterField<T>,
    value: T,
    filter: UiFilter<*>,
    previewOnly: Boolean,
    onValueChange: (T) -> Unit
) {
    // A registration binds the property and editor to the same value type.
    @Suppress("UNCHECKED_CAST")
    val editor = fieldEditors[property] as FieldEditor<T>?
    val onChange: (T) -> Unit = { if (!previewOnly) onValueChange(it) }
    if (editor == null) {
        field.Content(value, filter, previewOnly, onChange)
    } else {
        editor.content(value, filter, previewOnly, onChange)
    }
}
