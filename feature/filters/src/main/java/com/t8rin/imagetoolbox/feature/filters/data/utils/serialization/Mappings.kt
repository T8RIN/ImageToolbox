/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import com.t8rin.imagetoolbox.core.domain.model.toColorModel
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.component6
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.component7
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.component8
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.component9
import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.domain.utils.simpleName
import com.t8rin.imagetoolbox.core.filters.domain.model.BilaterialBlurParams
import com.t8rin.imagetoolbox.core.filters.domain.model.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.ChannelMixParams
import com.t8rin.imagetoolbox.core.filters.domain.model.ClaheParams
import com.t8rin.imagetoolbox.core.filters.domain.model.EnhancedZoomBlurParams
import com.t8rin.imagetoolbox.core.filters.domain.model.FadeSide
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValueWrapper
import com.t8rin.imagetoolbox.core.filters.domain.model.GlitchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.KaleidoscopeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.LinearGaussianParams
import com.t8rin.imagetoolbox.core.filters.domain.model.LinearTiltShiftParams
import com.t8rin.imagetoolbox.core.filters.domain.model.MirrorSide
import com.t8rin.imagetoolbox.core.filters.domain.model.PinchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.PolarCoordinatesType
import com.t8rin.imagetoolbox.core.filters.domain.model.PopArtBlendingMode
import com.t8rin.imagetoolbox.core.filters.domain.model.RadialTiltShiftParams
import com.t8rin.imagetoolbox.core.filters.domain.model.SideFadeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.ToneCurvesParams
import com.t8rin.imagetoolbox.core.filters.domain.model.TransferFunc
import com.t8rin.imagetoolbox.core.filters.domain.model.VoronoiCrystallizeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.WaterParams

internal fun Any.toPair(): Pair<String, String>? {
    return when (this) {
        is Int -> Int::class.simpleName() to toString()
        is Float -> Float::class.simpleName() to toString()
        is Unit -> Unit::class.simpleName() to "Unit"
        is PolarCoordinatesType -> PolarCoordinatesType::class.simpleName() to name
        is FloatArray -> FloatArray::class.simpleName() to joinToString(separator = PROPERTIES_SEPARATOR) { it.toString() }
        is FilterValueWrapper<*> -> {
            when (wrapped) {
                is ColorModel -> "${FilterValueWrapper::class.simpleName()}{${ColorModel::class.simpleName}}" to (wrapped as ColorModel).colorInt
                    .toString()

                else -> null
            }
        }

        is Pair<*, *> -> {
            val firstPart = first!!.toPart()
            val secondPart = second!!.toPart()
            "${Pair::class.simpleName}{${first!!::class.simpleName}$PROPERTIES_SEPARATOR${second!!::class.simpleName}}" to listOf(
                firstPart,
                secondPart
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is Triple<*, *, *> -> {
            val firstPart = first!!.toPart()
            val secondPart = second!!.toPart()
            val thirdPart = third!!.toPart()

            "${Triple::class.simpleName}{${first!!::class.simpleName}$PROPERTIES_SEPARATOR${second!!::class.simpleName}$PROPERTIES_SEPARATOR${third!!::class.simpleName}}" to listOf(
                firstPart,
                secondPart,
                thirdPart
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is Quad<*, *, *, *> -> {
            val firstPart = first!!.toPart()
            val secondPart = second!!.toPart()
            val thirdPart = third!!.toPart()
            val fourthPart = fourth!!.toPart()

            "${Quad::class.simpleName}{${first!!::class.simpleName}$PROPERTIES_SEPARATOR${second!!::class.simpleName}$PROPERTIES_SEPARATOR${third!!::class.simpleName}$PROPERTIES_SEPARATOR${fourth!!::class.simpleName}}" to listOf(
                firstPart,
                secondPart,
                thirdPart,
                fourthPart
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is GlitchParams -> {
            GlitchParams::class.simpleName() to listOf(
                channelsShiftX,
                channelsShiftY,
                corruptionSize,
                corruptionCount,
                corruptionShiftX,
                corruptionShiftY
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is LinearTiltShiftParams -> {
            LinearTiltShiftParams::class.simpleName() to listOf(
                blurRadius,
                sigma,
                anchorX,
                anchorY,
                holeRadius,
                angle
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is RadialTiltShiftParams -> {
            RadialTiltShiftParams::class.simpleName() to listOf(
                blurRadius,
                sigma,
                anchorX,
                anchorY,
                holeRadius
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is EnhancedZoomBlurParams -> {
            EnhancedZoomBlurParams::class.simpleName() to listOf(
                radius,
                sigma,
                centerX,
                centerY,
                strength,
                angle
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is SideFadeParams.Relative -> {
            SideFadeParams::class.simpleName() to listOf(
                side.name, scale
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is WaterParams -> {
            WaterParams::class.simpleName() to listOf(
                fractionSize,
                frequencyX,
                frequencyY,
                amplitudeX,
                amplitudeY
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is ClaheParams -> {
            ClaheParams::class.simpleName() to listOf(
                threshold,
                gridSizeHorizontal,
                gridSizeVertical,
                binsCount
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is LinearGaussianParams -> {
            LinearGaussianParams::class.simpleName() to listOf(
                kernelSize,
                sigma,
                edgeMode.name,
                transferFunction.name
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is ToneCurvesParams -> {
            ToneCurvesParams::class.simpleName() to controlPoints.joinToString(PROPERTIES_SEPARATOR) {
                it.joinToString(ADDITIONAL_PROPERTIES_SEPARATOR)
            }
        }

        is BilaterialBlurParams -> {
            BilaterialBlurParams::class.simpleName() to listOf(
                radius,
                spatialSigma,
                rangeSigma,
                edgeMode.name
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is KaleidoscopeParams -> {
            KaleidoscopeParams::class.simpleName() to listOf(
                angle,
                angle2,
                centreX,
                centreY,
                sides,
                radius
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is ChannelMixParams -> {
            ChannelMixParams::class.simpleName() to listOf(
                blueGreen,
                redBlue,
                greenRed,
                intoR,
                intoG,
                intoB
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is VoronoiCrystallizeParams -> {
            VoronoiCrystallizeParams::class.simpleName!! to listOf(
                borderThickness,
                scale,
                randomness,
                shape,
                turbulence,
                angle,
                stretch,
                amount,
                color.colorInt
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is PinchParams -> {
            PinchParams::class.simpleName!! to listOf(
                angle,
                centreX,
                centreY,
                radius,
                amount
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        else -> null
    }
}

internal fun Pair<String, String>.fromPair(): Any? {
    val name = first.trim()
    val value = second.trim()

    return when {
        name == Int::class.simpleName -> value.toInt()
        name == Float::class.simpleName -> value.toFloat()
        name == Boolean::class.simpleName -> value.toBoolean()
        name == Unit::class.simpleName -> Unit
        name == PolarCoordinatesType::class.simpleName -> PolarCoordinatesType.valueOf(value)
        name == FloatArray::class.simpleName -> value.split(PROPERTIES_SEPARATOR)
            .map { it.toFloat() }
            .toFloatArray()

        "${FilterValueWrapper::class.simpleName}{" in name -> {
            when (name.getTypeFromBraces()) {
                ColorModel::class.simpleName -> FilterValueWrapper(ColorModel(value.toInt()))
                else -> null
            }
        }

        "${Pair::class.simpleName}{" in name -> {
            val (firstType, secondType) = name.getTypeFromBraces().split(PROPERTIES_SEPARATOR)
            val (firstPart, secondPart) = value.split(PROPERTIES_SEPARATOR)
            firstPart.fromPart(firstType) to secondPart.fromPart(secondType)
        }

        "${Triple::class.simpleName}{" in name -> {
            val (firstType, secondType, thirdType) = name.getTypeFromBraces()
                .split(PROPERTIES_SEPARATOR)
            val (firstPart, secondPart, thirdPart) = value.split(PROPERTIES_SEPARATOR)
            Triple(
                firstPart.fromPart(firstType),
                secondPart.fromPart(secondType),
                thirdPart.fromPart(thirdType)
            )
        }

        "${Quad::class.simpleName}{" in name -> {
            val (firstType, secondType, thirdType, fourthType) = name.getTypeFromBraces()
                .split(PROPERTIES_SEPARATOR)
            val (firstPart, secondPart, thirdPart, fourthPart) = value.split(PROPERTIES_SEPARATOR)
            Quad(
                firstPart.fromPart(firstType),
                secondPart.fromPart(secondType),
                thirdPart.fromPart(thirdType),
                fourthPart.fromPart(fourthType)
            )
        }

        name == GlitchParams::class.simpleName -> {
            val (
                channelsShiftX,
                channelsShiftY,
                corruptionSize,
                corruptionCount,
                corruptionShiftX,
                corruptionShiftY,
            ) = value.split(PROPERTIES_SEPARATOR)
            GlitchParams(
                channelsShiftX = channelsShiftX.toFloat(),
                channelsShiftY = channelsShiftY.toFloat(),
                corruptionSize = corruptionSize.toFloat(),
                corruptionCount = corruptionCount.toInt(),
                corruptionShiftX = corruptionShiftX.toFloat(),
                corruptionShiftY = corruptionShiftY.toFloat()
            )
        }

        name == LinearTiltShiftParams::class.simpleName -> {
            val (blurRadius, sigma, anchorX, anchorY, holeRadius, angle) = value.split(
                PROPERTIES_SEPARATOR
            )
            LinearTiltShiftParams(
                blurRadius = blurRadius.toFloat(),
                sigma = sigma.toFloat(),
                anchorX = anchorX.toFloat(),
                anchorY = anchorY.toFloat(),
                holeRadius = holeRadius.toFloat(),
                angle = angle.toFloat()
            )
        }

        name == RadialTiltShiftParams::class.simpleName -> {
            val (blurRadius, sigma, anchorX, anchorY, holeRadius) = value.split(
                PROPERTIES_SEPARATOR
            )
            RadialTiltShiftParams(
                blurRadius = blurRadius.toFloat(),
                sigma = sigma.toFloat(),
                anchorX = anchorX.toFloat(),
                anchorY = anchorY.toFloat(),
                holeRadius = holeRadius.toFloat()
            )
        }

        name == EnhancedZoomBlurParams::class.simpleName -> {
            val (radius, sigma, centerX, centerY, strength, angle) = value.split(
                PROPERTIES_SEPARATOR
            )
            EnhancedZoomBlurParams(
                radius = radius.toInt(),
                sigma = sigma.toFloat(),
                centerX = centerX.toFloat(),
                centerY = centerY.toFloat(),
                strength = strength.toFloat(),
                angle = angle.toFloat()
            )
        }

        name == SideFadeParams::class.simpleName -> {
            val (sideName, scale) = value.split(PROPERTIES_SEPARATOR)
            SideFadeParams.Relative(
                side = FadeSide.valueOf(sideName),
                scale = scale.toFloat()
            )
        }

        name == WaterParams::class.simpleName -> {
            val (fractionSize, frequencyX, frequencyY, amplitudeX, amplitudeY) = value.split(
                PROPERTIES_SEPARATOR
            )
            WaterParams(
                fractionSize = fractionSize.toFloat(),
                frequencyX = frequencyX.toFloat(),
                frequencyY = frequencyY.toFloat(),
                amplitudeX = amplitudeX.toFloat(),
                amplitudeY = amplitudeY.toFloat()
            )
        }

        name == ClaheParams::class.simpleName -> {
            val (threshold, gridSizeHorizontal, gridSizeVertical, binsCount) = value.split(
                PROPERTIES_SEPARATOR
            )
            ClaheParams(
                threshold = threshold.toFloat(),
                gridSizeHorizontal = gridSizeHorizontal.toInt(),
                gridSizeVertical = gridSizeVertical.toInt(),
                binsCount = binsCount.toInt()
            )
        }

        name == LinearGaussianParams::class.simpleName -> {
            val (kernelSize, sigma, edgeModeName, transferFunctionName) = value.split(
                PROPERTIES_SEPARATOR
            )
            LinearGaussianParams(
                kernelSize = kernelSize.toInt(),
                sigma = sigma.toFloat(),
                edgeMode = BlurEdgeMode.valueOf(edgeModeName),
                transferFunction = TransferFunc.valueOf(transferFunctionName)
            )
        }

        name == ToneCurvesParams::class.simpleName -> {
            val controlPoints = value.split(PROPERTIES_SEPARATOR).map { valueString ->
                valueString.split(ADDITIONAL_PROPERTIES_SEPARATOR).map {
                    it.toFloatOrNull() ?: 0f
                }
            }

            ToneCurvesParams(
                controlPoints = controlPoints
            )
        }

        name == BilaterialBlurParams::class.simpleName -> {
            val (radius, spatialSigma, rangeSigma, edgeMode) = value.split(
                PROPERTIES_SEPARATOR
            )
            BilaterialBlurParams(
                radius = radius.toInt(),
                spatialSigma = spatialSigma.toFloat(),
                rangeSigma = rangeSigma.toFloat(),
                edgeMode = BlurEdgeMode.valueOf(edgeMode)
            )
        }

        name == KaleidoscopeParams::class.simpleName -> {
            val (angle, angle2, centreX, centreY, sides, radius) = value.split(
                PROPERTIES_SEPARATOR
            )
            KaleidoscopeParams(
                angle = angle.toFloat(),
                angle2 = angle2.toFloat(),
                centreX = centreX.toFloat(),
                centreY = centreY.toFloat(),
                sides = sides.toInt(),
                radius = radius.toFloat()
            )
        }

        name == ChannelMixParams::class.simpleName -> {
            val (blueGreen, redBlue, greenRed, intoR, intoG, intoB) = value.split(
                PROPERTIES_SEPARATOR
            ).map { it.toInt() }

            ChannelMixParams(
                blueGreen = blueGreen,
                redBlue = redBlue,
                greenRed = greenRed,
                intoR = intoR,
                intoG = intoG,
                intoB = intoB
            )
        }

        name == VoronoiCrystallizeParams::class.simpleName -> {
            val (borderThickness, scale, randomness, shape, turbulence, angle, stretch, amount, color) = value.split(
                PROPERTIES_SEPARATOR
            )

            VoronoiCrystallizeParams(
                borderThickness = borderThickness.toFloat(),
                scale = scale.toFloat(),
                randomness = randomness.toFloat(),
                shape = shape.toInt(),
                turbulence = turbulence.toFloat(),
                angle = angle.toFloat(),
                stretch = stretch.toFloat(),
                amount = amount.toFloat(),
                color = color.toInt().toColorModel()
            )
        }

        name == PinchParams::class.simpleName -> {
            val (angle, centreX, centreY, radius, amount) = value.split(
                PROPERTIES_SEPARATOR
            ).map { it.toFloat() }

            PinchParams(
                angle = angle,
                centreX = centreX,
                centreY = centreY,
                radius = radius,
                amount = amount
            )
        }

        else -> null
    }
}

internal fun String.getTypeFromBraces(): String = removeSuffix("}").split("{")[1]

internal fun Any.toPart(): String {
    return when (this) {
        is Int -> toString()
        is Float -> toString()
        is ColorModel -> colorInt.toString()
        is Boolean -> toString()
        is BlurEdgeMode -> name
        is TransferFunc -> name
        is FadeSide -> name
        is PopArtBlendingMode -> name
        is MirrorSide -> name
        is PolarCoordinatesType -> name
        else -> ""
    }
}

internal fun String.fromPart(type: String): Any {
    return when (type) {
        Int::class.simpleName() -> toInt()
        Float::class.simpleName() -> toFloat()
        ColorModel::class.simpleName() -> ColorModel(toInt())
        Boolean::class.simpleName() -> toBoolean()
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
private const val ADDITIONAL_PROPERTIES_SEPARATOR = "*"