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

package com.t8rin.imagetoolbox.feature.filters.data.utils.serialization

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.feature.filters.data.model.FilterField
import kotlin.io.encoding.Base64
import kotlin.math.round

internal fun parseShaderTemplate(payload: String): Map<String, String> =
    Base64.decode(payload).decodeToString().split(';').mapNotNull { entry ->
        val separator = entry.indexOf('=')
        if (separator == -1) null
        else entry.substring(0, separator) to entry.substring(separator + 1)
    }.toMap()

internal fun Map<String, String>.shaderNumber(name: String, field: FilterField.Number): Float =
    this[name]?.toFloatOrNull()?.let(field::constrain) ?: field.default

internal fun Map<String, String>.shaderInteger(name: String, field: FilterField.Integer): Int =
    this[name]?.toFloatOrNull()?.takeIf(Float::isFinite)
        ?.let { field.constrain(round(it).toInt()) } ?: field.default

internal fun Map<String, String>.shaderColor(name: String, field: FilterField.Color): ColorModel =
    this["color:$name"]?.toIntOrNull()?.let(::ColorModel) ?: field.default
