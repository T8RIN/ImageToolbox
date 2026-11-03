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
import com.t8rin.imagetoolbox.core.domain.model.OffsetModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValues
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.data.model.toFontType
import com.t8rin.imagetoolbox.feature.filters.data.model.toFiniteFloat
import kotlin.io.encoding.Base64
import kotlin.reflect.KClass

internal fun String.pixelationKey(): String? =
    if (startsWith("$REAL_PACKAGE.core.filters.presentation.model.Ui") && endsWith("PixelizationFilter")) {
        removeSuffix("PixelizationFilter") + "PixelationFilter"
    } else null

internal fun KClass<out Filter<*>>.readOldParams(
    key: String,
    payload: String
): Filter.ParameterValue? {
    val fields = payload.split(FILTER_FIELDS_SEPARATOR)
    return when {
        this == Filter.Ascii::class && key == "AsciiParams" && fields.size == 5 -> {
            FilterValues.ascii(
                gradient = Base64.decode(fields[0]).decodeToString(),
                fontSize = fields[1].toFiniteFloat(),
                backgroundColor = ColorModel(fields[2].toInt()),
                isGrayscale = fields[3].toBooleanStrict(),
                font = DomainFontFamily.fromString(fields[4]).toFontType()
            )
        }

        this == Filter.DistortPerspective::class && key == "DistortPerspectiveParams" && fields.size == 9 -> {
            FilterValues.distortPerspective(
                topLeft = OffsetModel(fields[0].toFiniteFloat(), fields[1].toFiniteFloat()),
                topRight = OffsetModel(fields[2].toFiniteFloat(), fields[3].toFiniteFloat()),
                bottomLeft = OffsetModel(fields[4].toFiniteFloat(), fields[5].toFiniteFloat()),
                bottomRight = OffsetModel(fields[6].toFiniteFloat(), fields[7].toFiniteFloat()),
                clip = fields[8].toBooleanStrict()
            )
        }

        this == Filter.ToneCurves::class && key == "ToneCurvesParams" -> {
            FilterValues.toneCurves(
                controlPoints = fields.map { row ->
                    row.split('*').map(String::toFiniteFloat)
                }
            )
        }

        else -> null
    }
}
