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

package com.t8rin.imagetoolbox.feature.filters.data.model

import androidx.annotation.StringRes
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.GmicParameterInfo
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import java.math.BigDecimal
import kotlin.io.encoding.Base64

internal class GmicParameters(
    defaults: List<String>,
    val fields: List<GmicParameterInfo>
) : FilterField.Value<GmicFilterParams>(
    { GmicFilterParams(defaults) },
    control(valueRange = 0f..0f),
    fields.map { field ->
        when (field) {
            is GmicParameterInfo.Number -> FilterParam(
                field.title, field.range, field.roundTo, isSeed = field.isSeed
            )

            is GmicParameterInfo.Toggle -> control(field.title, 0f..1f, 0)
            is GmicParameterInfo.Selection -> control(
                field.title,
                0f..field.entries.lastIndex.toFloat(),
                0
            )

            is GmicParameterInfo.Color -> control(field.title, 0f..0f)
        }
    },
    encode = { params -> params.values.joinToString("*") { Base64.encode(it.encodeToByteArray()) } },
    decode = { value ->
        GmicFilterParams(value.split('*').map { Base64.decode(it).decodeToString() })
    }
) {
    override val hasEditor: Boolean get() = true
}

internal data class GmicField(val default: String, val control: GmicParameterInfo)

internal fun gmicParam(vararg fields: GmicField): GmicParameters =
    GmicParameters(fields.map(GmicField::default), fields.map(GmicField::control))

internal fun gmicNumber(
    @StringRes title: Int,
    default: Number,
    range: ClosedFloatingPointRange<Float>,
    roundTo: Int = 2
): GmicField = GmicField(
    if (default is BigDecimal) default.toPlainString() else default.toString(),
    GmicParameterInfo.Number(title, range, isInteger = false, roundTo)
)

internal fun gmicInteger(
    @StringRes title: Int,
    default: Int,
    range: ClosedFloatingPointRange<Float>,
    roundTo: Int = 0
): GmicField = GmicField(
    default.toString(),
    GmicParameterInfo.Number(title, range, isInteger = true, roundTo)
)

internal fun gmicSeed(
    @StringRes title: Int,
    default: Int,
    range: ClosedFloatingPointRange<Float>
): GmicField = GmicField(
    default.toString(),
    GmicParameterInfo.Number(title, range, isInteger = true, isSeed = true)
)

internal fun gmicToggle(@StringRes title: Int, default: Boolean): GmicField =
    GmicField(default.toString(), GmicParameterInfo.Toggle(title))

internal fun gmicSelection(
    @StringRes title: Int,
    default: String,
    entries: List<String>
): GmicField {
    require(default in entries)
    return GmicField(default, GmicParameterInfo.Selection(title, entries))
}

internal fun gmicColor(@StringRes title: Int, default: Int): GmicField =
    GmicField(default.toString(), GmicParameterInfo.Color(title))
