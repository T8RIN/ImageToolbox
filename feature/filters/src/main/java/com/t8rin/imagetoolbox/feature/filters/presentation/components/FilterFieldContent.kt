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
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.FileModel
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilterBooleanParamItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilterColorParamItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilterEnumParamItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilterFileParamItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilterImageParamItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilterPaletteParamItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilterParamItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FloatArrayItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.GmicFilterParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.StringItem
import com.t8rin.imagetoolbox.feature.filters.data.model.FilterField
import com.t8rin.imagetoolbox.feature.filters.data.model.GmicParameters
import kotlin.math.roundToInt

@Composable
@Suppress("UNCHECKED_CAST")
internal fun <T> FilterField<T>.Content(
    value: T,
    filter: UiFilter<*>,
    previewOnly: Boolean,
    onValueChange: (T) -> Unit
) {
    // A field is bound to a property of the same type by the schema builder.
    val onChange: (Any?) -> Unit = { if (!previewOnly) onValueChange(it as T) }
    when (this) {
        is FilterField.Number -> FilterParamItem(
            value = value as Float,
            info = info,
            previewOnly = previewOnly,
            onValueChange = onChange
        )

        is FilterField.Integer -> FilterParamItem(
            value = (value as Int).toFloat(),
            info = info,
            previewOnly = previewOnly,
            onValueChange = { onChange(it.roundToInt()) }
        )

        is FilterField.Toggle -> FilterBooleanParamItem(
            value = value as Boolean,
            info = info,
            previewOnly = previewOnly,
            onValueChange = onChange
        )

        is FilterField.Color -> FilterColorParamItem(
            value = value as ColorModel,
            info = info,
            previewOnly = previewOnly,
            allowAlpha = allowAlpha,
            onValueChange = onChange
        )

        is FilterField.Text -> StringItem(
            value = value as String,
            filter = filter,
            info = info,
            previewOnly = previewOnly,
            onFilterChange = onChange
        )

        is FilterField.Array -> FloatArrayItem(
            value = value as FloatArray,
            columns = columns,
            default = { default },
            previewOnly = previewOnly,
            onFilterChange = onChange
        )

        is FilterField.Choice<*> -> ChoiceContent(
            value = value,
            previewOnly = previewOnly,
            onValueChange = onChange
        )

        is FilterField.Palette -> FilterPaletteParamItem(
            value = value as GradientPalette,
            previewOnly = previewOnly,
            onValueChange = onChange
        )

        is FilterField.Image -> FilterImageParamItem(
            value = value as ImageModel,
            info = info,
            previewOnly = previewOnly,
            onValueChange = onChange
        )

        is FilterField.File -> FilterFileParamItem(
            value = value as FileModel,
            info = info,
            previewOnly = previewOnly,
            onValueChange = onChange
        )

        is GmicParameters -> GmicFilterParamsItem(
            value = value as GmicFilterParams,
            paramsInfo = fields,
            onFilterChange = onChange,
            previewOnly = previewOnly
        )

        else -> error("This parameter needs a custom editor")
    }
}

@Composable
@Suppress("UNCHECKED_CAST")
private fun <T : Enum<T>> FilterField.Choice<T>.ChoiceContent(
    value: Any?,
    previewOnly: Boolean,
    onValueChange: (Any?) -> Unit
) {
    FilterEnumParamItem(
        value = value as T,
        info = info,
        entries = entries,
        label = { stringResource(label(it)) },
        previewOnly = previewOnly,
        onValueChange = onValueChange
    )
}