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
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.domain.utils.simpleName
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.FadeSide
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.MirrorSide
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PolarCoordinatesType
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PopArtBlendingMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.TransferFunc
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.feature.filters.data.model.toFiniteFloat
import kotlin.io.encoding.Base64

internal fun Pair<String, String>.fromPair(): Any? {
    val name = first.trim()
    val value = second.trim()

    return when {
        name == Int::class.simpleName -> value.toInt()
        name == Float::class.simpleName -> value.toFiniteFloat()
        name == String::class.simpleName -> Base64.decode(value).toString(Charsets.UTF_8)
        name == Boolean::class.simpleName -> value.toBooleanStrict()
        name == Unit::class.simpleName -> Unit
        name == PolarCoordinatesType::class.simpleName -> PolarCoordinatesType.valueOf(value)
        name == GradientPalette::class.simpleName -> requireNotNull(
            GradientPalette.fromSerializedString(
                value
            )
        )
        name == FloatArray::class.simpleName -> value.split(PROPERTIES_SEPARATOR)
            .map { it.toFiniteFloat() }
            .toFloatArray()

        name == GmicFilterParams::class.simpleName -> GmicFilterParams(
            values = value.split(PROPERTIES_SEPARATOR).map {
                Base64.decode(it).toString(Charsets.UTF_8)
            }
        )

        name == "FilterValueWrapper{ColorModel}" -> ColorModel(value.toInt())

        "${Pair::class.simpleName}{" in name -> {
            val (firstType, secondType) = name.getTypeFromBraces().parts(2)
            val (firstPart, secondPart) = value.parts(2)
            firstPart.fromPart(firstType) to secondPart.fromPart(secondType)
        }

        "${Triple::class.simpleName}{" in name -> {
            val (firstType, secondType, thirdType) = name.getTypeFromBraces().parts(3)
            val (firstPart, secondPart, thirdPart) = value.parts(3)
            Triple(
                firstPart.fromPart(firstType),
                secondPart.fromPart(secondType),
                thirdPart.fromPart(thirdType)
            )
        }

        "${Quad::class.simpleName}{" in name -> {
            val (firstType, secondType, thirdType, fourthType) = name.getTypeFromBraces().parts(4)
            val (firstPart, secondPart, thirdPart, fourthPart) = value.parts(4)
            Quad(
                firstPart.fromPart(firstType),
                secondPart.fromPart(secondType),
                thirdPart.fromPart(thirdType),
                fourthPart.fromPart(fourthType)
            )
        }

        name == IntegerSize::class.simpleName -> {
            val (width, height) = value.parts(2)

            IntegerSize(
                width = width.toInt(),
                height = height.toInt()
            )
        }

        else -> null
    }
}

internal fun String.getTypeFromBraces(): String = removeSuffix("}").split("{")[1]

internal fun String.fromPart(type: String): Any {
    return when (type) {
        Int::class.simpleName() -> toInt()
        Float::class.simpleName() -> toFiniteFloat()
        ColorModel::class.simpleName() -> ColorModel(toInt())
        Boolean::class.simpleName() -> toBooleanStrict()
        BlurEdgeMode::class.simpleName() -> BlurEdgeMode.valueOf(this)
        TransferFunc::class.simpleName() -> TransferFunc.valueOf(this)
        FadeSide::class.simpleName() -> FadeSide.valueOf(this)
        PopArtBlendingMode::class.simpleName() -> PopArtBlendingMode.valueOf(this)
        MirrorSide::class.simpleName() -> MirrorSide.valueOf(this)
        PolarCoordinatesType::class.simpleName() -> PolarCoordinatesType.valueOf(this)
        else -> ""
    }
}

private const val PROPERTIES_SEPARATOR = "$"

private fun String.parts(count: Int): List<String> = split(PROPERTIES_SEPARATOR).also {
    require(it.size == count) { "Expected $count stored parameters, got ${it.size}" }
}

internal fun storedParameterValues(value: Any?, count: Int): List<Any?>? {
    val fields = when (value) {
        null -> return null
        is Pair<*, *> -> listOf(value.first, value.second)
        is Triple<*, *, *> -> listOf(value.first, value.second, value.third)
        is Quad<*, *, *, *> -> listOf(value.first, value.second, value.third, value.fourth)
        else -> listOf(value)
    }
    return fields.takeIf { it.size == count }
}
