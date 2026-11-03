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
import com.t8rin.imagetoolbox.core.settings.presentation.model.asFontType
import kotlin.io.encoding.Base64
import kotlin.reflect.KClass

internal fun String.pixelationKey(): String? =
    if (startsWith("$REAL_PACKAGE.core.filters.presentation.model.Ui") && endsWith("PixelizationFilter")) {
        removeSuffix("PixelizationFilter") + "PixelationFilter"
    } else null

internal fun KClass<out Filter<*>>.acceptsOldParamsKey(key: String): Boolean = when (this) {
    Filter.ClaheHSL::class,
    Filter.ClaheHSV::class,
    Filter.ClaheJzazbz::class,
    Filter.ClaheLAB::class,
    Filter.ClaheLUV::class,
    Filter.ClaheOklab::class,
    Filter.ClaheOklch::class -> key == "ClaheParams"

    Filter.EnhancedGlitch::class -> key == "GlitchParams"
    Filter.LinearGaussianBlur::class -> key == "LinearGaussianParams"
    Filter.Pointillize::class -> key == "VoronoiCrystallizeParams"
    Filter.VHSNtsc::class -> key == "NtscParams"
    Filter.WaterEffect::class -> key == "WaterParams"
    else -> false
}

internal fun KClass<out Filter<*>>.readOldParams(
    key: String,
    payload: String
): Filter.ParameterValue? {
    val fields = payload.split(FILTER_FIELDS_SEPARATOR)
    return when {
        this == Filter.Ascii::class && key == "AsciiParams" && fields.size == 5 -> {
            FilterValues.ascii(
                gradient = Base64.decode(fields[0]).decodeToString(),
                fontSize = fields[1].toFloat(),
                backgroundColor = ColorModel(fields[2].toInt()),
                isGrayscale = fields[3].toBooleanStrict(),
                font = DomainFontFamily.fromString(fields[4]).asFontType()
            )
        }

        this == Filter.DistortPerspective::class && key == "DistortPerspectiveParams" && fields.size == 9 -> {
            FilterValues.distortPerspective(
                topLeft = OffsetModel(fields[0].toFloat(), fields[1].toFloat()),
                topRight = OffsetModel(fields[2].toFloat(), fields[3].toFloat()),
                bottomLeft = OffsetModel(fields[4].toFloat(), fields[5].toFloat()),
                bottomRight = OffsetModel(fields[6].toFloat(), fields[7].toFloat()),
                clip = fields[8].toBooleanStrict()
            )
        }

        this == Filter.ToneCurves::class && key == "ToneCurvesParams" -> {
            FilterValues.toneCurves(
                controlPoints = fields.map { row ->
                    row.split('*').map(String::toFloat)
                }
            )
        }

        else -> null
    }
}
