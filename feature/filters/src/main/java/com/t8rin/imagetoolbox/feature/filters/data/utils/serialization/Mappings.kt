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
import com.t8rin.imagetoolbox.core.domain.model.toColorModel
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.component6
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.component7
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.component8
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.component9
import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.domain.utils.simpleName
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValueWrapper
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.FadeSide
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.MirrorSide
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PolarCoordinatesType
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PopArtBlendingMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.TransferFunc
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AdaptiveBinarizationParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AgxToneMappingParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AnamorphicStreaksParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ArcParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AsciiParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AutostereogramParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BayerSensorParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BilaterialBlurParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BloomParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.CapillaryInkBleedParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ChannelMixParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ClaheParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.CmykPressParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ContourHatchingParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.CropOrPerspectiveParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DaltonizationParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DctGlitchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DiffractionPsfParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DirectionalEmbroideryParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DistortPerspectiveParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DropShadowParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.EnhancedZoomBlurParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FilmGrainParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FilmHalationParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FilmNegativeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FlareParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FourierPhaseScrambleParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GlitchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GuidedSmoothingParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.KaleidoscopeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LayeredPaperCutParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LcdSubpixelsParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LinearGaussianParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LinearTiltShiftParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.NtscParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PalCompositeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PaletteRemapParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PhotoCrossStitchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PinchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ProceduralParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RadialTiltShiftParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RetroHardwareParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RfReceptionParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RisographParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RubberStampParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SecamParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ShearParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SideFadeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SignedDistanceFieldParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SingleLineDrawingParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SmearParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SparkleParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SstvReceptionParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ThermalReceiptParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ToneCurvesParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.TornEdgeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.VoronoiCrystallizeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.WaterDropParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.WaterParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.WienerDeconvolutionParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.XdogParams
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.presentation.model.asDomain
import com.t8rin.imagetoolbox.core.settings.presentation.model.asFontType
import kotlin.io.encoding.Base64

internal fun Any.toPair(): Pair<String, String>? {
    return when (this) {
        is Int -> Int::class.simpleName() to toString()
        is Float -> Float::class.simpleName() to toString()
        is String -> String::class.simpleName() to Base64.encode(toByteArray(Charsets.UTF_8))
        is Unit -> Unit::class.simpleName() to "Unit"
        is PolarCoordinatesType -> PolarCoordinatesType::class.simpleName() to name
        is GradientPalette -> GradientPalette::class.simpleName() to toSerializedString()
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

        is FlareParams -> {
            FlareParams::class.simpleName() to listOf(
                radius,
                baseAmount,
                ringAmount,
                rayAmount,
                ringWidth,
                centreX,
                centreY,
                color.colorInt
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is DistortPerspectiveParams -> {
            DistortPerspectiveParams::class.simpleName() to listOf(
                topLeft.first,
                topLeft.second,
                topRight.first,
                topRight.second,
                bottomLeft.first,
                bottomLeft.second,
                bottomRight.first,
                bottomRight.second,
                clip
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is ShearParams -> {
            ShearParams::class.simpleName() to listOf(
                xAngle,
                yAngle,
                resize
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is WaterDropParams -> {
            WaterDropParams::class.simpleName() to listOf(
                wavelength,
                amplitude,
                phase,
                centreX,
                centreY,
                radius
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

        is ProceduralParams -> {
            ProceduralParams::class.simpleName() to Base64.encode(
                (values.map { "${it.key}=${it.value}" } + colors.map { "color:${it.key}=${it.value}" })
                    .joinToString(";").toByteArray(Charsets.UTF_8)
            )
        }

        is AdaptiveBinarizationParams -> {
            AdaptiveBinarizationParams::class.simpleName() to listOf(
                algorithm,
                radius,
                coefficient,
                dynamicRange,
                ink.colorInt,
                paper.colorInt,
                invert
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is AgxToneMappingParams -> {
            AgxToneMappingParams::class.simpleName() to listOf(
                exposure,
                look,
                saturation,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is AnamorphicStreaksParams -> {
            AnamorphicStreaksParams::class.simpleName() to listOf(
                threshold,
                angle,
                length,
                width,
                falloff,
                intensity
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is AutostereogramParams -> {
            AutostereogramParams::class.simpleName() to listOf(
                repeatDistance,
                depth,
                gamma,
                size,
                softness,
                invert,
                seed
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is BayerSensorParams -> {
            BayerSensorParams::class.simpleName() to listOf(
                pattern,
                scale,
                shotNoise,
                readNoise,
                seed,
                strength,
                edgeAware
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is CapillaryInkBleedParams -> {
            CapillaryInkBleedParams::class.simpleName() to listOf(
                wetness,
                direction,
                spread,
                roughness,
                steps,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is CmykPressParams -> {
            CmykPressParams::class.simpleName() to listOf(
                spacing,
                blackGeneration,
                dotGain,
                misregistration,
                inkLimit,
                paperGrain,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is ContourHatchingParams -> {
            ContourHatchingParams::class.simpleName() to listOf(
                spacing,
                width,
                angle,
                contrast,
                ink.colorInt,
                paper.colorInt,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is DaltonizationParams -> {
            DaltonizationParams::class.simpleName() to listOf(
                visionType,
                severity,
                correction,
                preserveLuminance,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is DctGlitchParams -> {
            DctGlitchParams::class.simpleName() to listOf(
                blockSize,
                minimumFrequency,
                maximumFrequency,
                operation,
                probability,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is DiffractionPsfParams -> {
            DiffractionPsfParams::class.simpleName() to listOf(
                blades,
                rotation,
                radius,
                spectralSpread,
                threshold,
                intensity
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is DirectionalEmbroideryParams -> {
            DirectionalEmbroideryParams::class.simpleName() to listOf(
                stitchLength,
                density,
                thickness,
                relief,
                colorLevels,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is FilmGrainParams -> {
            FilmGrainParams::class.simpleName() to listOf(
                size,
                intensity,
                sizeVariation,
                exposureDependence,
                colorGrain,
                seed
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is FilmHalationParams -> {
            FilmHalationParams::class.simpleName() to listOf(
                threshold,
                knee,
                radius,
                warmth,
                intensity
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is FilmNegativeParams -> {
            FilmNegativeParams::class.simpleName() to listOf(
                exposure,
                densityRange,
                contrast,
                toneCurve,
                filmBase.colorInt,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is FourierPhaseScrambleParams -> {
            FourierPhaseScrambleParams::class.simpleName() to listOf(
                minFrequency,
                maxFrequency,
                phase,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is GuidedSmoothingParams -> {
            GuidedSmoothingParams::class.simpleName() to listOf(
                radius,
                epsilon,
                guide,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is LayeredPaperCutParams -> {
            LayeredPaperCutParams::class.simpleName() to listOf(
                layers,
                smoothness,
                depth,
                lightAngle,
                grain,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is LcdSubpixelsParams -> {
            LcdSubpixelsParams::class.simpleName() to listOf(
                size,
                blackMatrix,
                subpixelOrder,
                viewAngle,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is PalCompositeParams -> {
            PalCompositeParams::class.simpleName() to listOf(
                lumaFilter,
                chromaRadius,
                noise,
                phaseError,
                scanSamples,
                lineCompensation,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is PaletteRemapParams -> {
            PaletteRemapParams::class.simpleName() to listOf(
                dithering,
                shadowColor.colorInt,
                midtoneColor.colorInt,
                highlightColor.colorInt
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is PhotoCrossStitchParams -> {
            PhotoCrossStitchParams::class.simpleName() to listOf(
                size,
                threadColors,
                threadWidth,
                lightAngle,
                relief,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is RetroHardwareParams -> {
            RetroHardwareParams::class.simpleName() to listOf(
                system,
                scale,
                dithering,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is RfReceptionParams -> {
            RfReceptionParams::class.simpleName() to listOf(
                delay,
                reflection,
                noise,
                frequency,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is RisographParams -> {
            RisographParams::class.simpleName() to listOf(
                spacing,
                misregistration,
                dotGain,
                inks,
                paperGrain,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is SecamParams -> {
            SecamParams::class.simpleName() to listOf(
                chromaRadius,
                frequencyDeviation,
                noise,
                preEmphasis,
                lineCrosstalk,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is SignedDistanceFieldParams -> {
            SignedDistanceFieldParams::class.simpleName() to listOf(
                alphaMask,
                threshold,
                distance,
                mode,
                spacing,
                insideColor.colorInt,
                outsideColor.colorInt,
                invert
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is SingleLineDrawingParams -> {
            SingleLineDrawingParams::class.simpleName() to listOf(
                pointCount,
                densityCurve,
                lineWidth,
                ink.colorInt,
                paper.colorInt,
                optimization,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is SstvReceptionParams -> {
            SstvReceptionParams::class.simpleName() to listOf(
                transmissionMode,
                frequencyDrift,
                syncError,
                noise,
                dropout,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is ThermalReceiptParams -> {
            ThermalReceiptParams::class.simpleName() to listOf(
                size,
                heat,
                heatRetention,
                defects,
                fading,
                seed,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is WienerDeconvolutionParams -> {
            WienerDeconvolutionParams::class.simpleName() to listOf(
                psf,
                radius,
                angle,
                regularization,
                strength
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is XdogParams -> {
            XdogParams::class.simpleName() to listOf(
                sigma,
                ratio,
                weight,
                threshold,
                sharpness,
                ink.colorInt,
                paper.colorInt,
                strength
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

        is RubberStampParams -> {
            RubberStampParams::class.simpleName!! to listOf(
                threshold,
                softness,
                radius,
                firstColor.colorInt,
                secondColor.colorInt,
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is SmearParams -> {
            SmearParams::class.simpleName!! to listOf(
                angle,
                density,
                mix,
                distance,
                shape,
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is ArcParams -> {
            ArcParams::class.simpleName!! to listOf(
                radius,
                height,
                angle,
                spreadAngle,
                centreX,
                centreY
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is SparkleParams -> {
            SparkleParams::class.simpleName!! to listOf(
                amount,
                rays,
                radius,
                randomness,
                centreX,
                centreY,
                color.colorInt
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is AsciiParams -> {
            val font = font?.asDomain()?.takeIf { it !is DomainFontFamily.Custom }
                ?: DomainFontFamily.System

            AsciiParams::class.simpleName!! to listOf(
                Base64.encode(gradient.toByteArray(Charsets.UTF_8)),
                fontSize,
                backgroundColor.colorInt,
                isGrayscale,
                font.asString()
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is BloomParams -> {
            BloomParams::class.simpleName!! to listOf(
                threshold,
                intensity,
                radius,
                softKnee,
                exposure,
                gamma
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is DropShadowParams -> {
            DropShadowParams::class.simpleName!! to listOf(
                color.colorInt,
                offsetX,
                offsetY,
                blurRadius
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is TornEdgeParams -> {
            TornEdgeParams::class.simpleName!! to listOf(
                toothHeight,
                horizontalToothRange,
                verticalToothRange,
                top,
                right,
                bottom,
                left
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is NtscParams -> {
            NtscParams::class.simpleName!! to listOf(
                amount,
                chromaBleed,
                tapeWear,
                noise,
                tracking,
                seed,
                lumaSmear,
                compositeSharpening,
                ringing,
                snow,
                useField,
                filterType,
                inputLumaFilter,
                chromaLowpassIn,
                chromaDemodulation,
                videoScanlinePhaseShift,
                videoScanlinePhaseShiftOffset,
                headSwitchingHeight,
                headSwitchingOffset,
                headSwitchingHorizontalShift,
                headSwitchingMidLinePosition,
                headSwitchingMidLineJitter,
                trackingNoiseHeight,
                trackingNoiseWaveIntensity,
                trackingNoiseSnowIntensity,
                trackingNoiseSnowAnisotropy,
                trackingNoiseNoiseIntensity,
                compositeNoiseFrequency,
                compositeNoiseIntensity,
                compositeNoiseDetail,
                ringingFrequency,
                ringingPower,
                lumaNoiseFrequency,
                lumaNoiseIntensity,
                lumaNoiseDetail,
                chromaNoiseFrequency,
                chromaNoiseIntensity,
                chromaNoiseDetail,
                snowIntensity,
                snowAnisotropy,
                chromaPhaseNoiseIntensity,
                chromaPhaseError,
                chromaDelayHorizontal,
                chromaDelayVertical,
                vhsTapeSpeed,
                vhsChromaLoss,
                vhsSharpenIntensity,
                vhsSharpenFrequency,
                vhsEdgeWaveIntensity,
                vhsEdgeWaveSpeed,
                vhsEdgeWaveFrequency,
                vhsEdgeWaveDetail,
                chromaLowpassOut,
                scaleHorizontal,
                scaleVertical,
                scaleFactorX,
                scaleFactorY,
                headSwitchingEnabled,
                trackingNoiseEnabled,
                compositeNoiseEnabled,
                ringingEnabled,
                lumaNoiseEnabled,
                chromaNoiseEnabled,
                vhsEnabled,
                scaleEnabled
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is CropOrPerspectiveParams -> {
            CropOrPerspectiveParams::class.simpleName!! to listOf(
                topLeft.join(),
                topRight.join(),
                bottomLeft.join(),
                bottomRight.join(),
                isAbsolute
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is IntegerSize -> {
            IntegerSize::class.simpleName!! to listOf(
                width,
                height
            ).joinToString(PROPERTIES_SEPARATOR)
        }

        is GmicFilterParams -> {
            GmicFilterParams::class.simpleName!! to values.joinToString(PROPERTIES_SEPARATOR) {
                Base64.encode(it.toByteArray(Charsets.UTF_8))
            }
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
        name == String::class.simpleName -> Base64.decode(value).toString(Charsets.UTF_8)
        name == Boolean::class.simpleName -> value.toBoolean()
        name == Unit::class.simpleName -> Unit
        name == PolarCoordinatesType::class.simpleName -> PolarCoordinatesType.valueOf(value)
        name == GradientPalette::class.simpleName -> requireNotNull(
            GradientPalette.fromSerializedString(
                value
            )
        )
        name == FloatArray::class.simpleName -> value.split(PROPERTIES_SEPARATOR)
            .map { it.toFloat() }
            .toFloatArray()

        name == GmicFilterParams::class.simpleName -> GmicFilterParams(
            values = value.split(PROPERTIES_SEPARATOR).map {
                Base64.decode(it).toString(Charsets.UTF_8)
            }
        )

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

        name == FlareParams::class.simpleName -> {
            val (radius, baseAmount, ringAmount, rayAmount, ringWidth, centreX, centreY, color) = value.split(
                PROPERTIES_SEPARATOR
            )
            FlareParams(
                radius = radius.toFloat(),
                baseAmount = baseAmount.toFloat(),
                ringAmount = ringAmount.toFloat(),
                rayAmount = rayAmount.toFloat(),
                ringWidth = ringWidth.toFloat(),
                centreX = centreX.toFloat(),
                centreY = centreY.toFloat(),
                color = color.toInt().toColorModel()
            )
        }

        name == DistortPerspectiveParams::class.simpleName -> {
            val (
                topLeftX,
                topLeftY,
                topRightX,
                topRightY,
                bottomLeftX,
                bottomLeftY,
                bottomRightX,
                bottomRightY,
                clip
            ) = value.split(PROPERTIES_SEPARATOR)
            DistortPerspectiveParams(
                topLeft = topLeftX.toFloat() to topLeftY.toFloat(),
                topRight = topRightX.toFloat() to topRightY.toFloat(),
                bottomLeft = bottomLeftX.toFloat() to bottomLeftY.toFloat(),
                bottomRight = bottomRightX.toFloat() to bottomRightY.toFloat(),
                clip = clip.toBoolean()
            )
        }

        name == ShearParams::class.simpleName -> {
            val (xAngle, yAngle, resize) = value.split(PROPERTIES_SEPARATOR)
            ShearParams(
                xAngle = xAngle.toFloat(),
                yAngle = yAngle.toFloat(),
                resize = resize.toBoolean()
            )
        }

        name == WaterDropParams::class.simpleName -> {
            val (wavelength, amplitude, phase, centreX, centreY, radius) = value.split(
                PROPERTIES_SEPARATOR
            )
            WaterDropParams(
                wavelength = wavelength.toFloat(),
                amplitude = amplitude.toFloat(),
                phase = phase.toFloat(),
                centreX = centreX.toFloat(),
                centreY = centreY.toFloat(),
                radius = radius.toFloat()
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

        name == ProceduralParams::class.simpleName -> {
            val decoded = Base64.decode(value).toString(Charsets.UTF_8)
            val entries = decoded.split(";").filter { it.isNotEmpty() }.associate { entry ->
                val (key, number) = entry.split("=", limit = 2)
                key to number
            }
            ProceduralParams(
                values = entries.filterKeys { !it.contains(":") }.mapValues { it.value.toFloat() },
                colors = entries.filterKeys { it.startsWith("color:") }
                    .mapKeys { it.key.removePrefix("color:") }.mapValues { it.value.toInt() }
            )
        }

        name == AdaptiveBinarizationParams::class.simpleName -> {
            val (algorithm, radius, coefficient, dynamicRange, ink, paper, invert) = value.split(
                PROPERTIES_SEPARATOR
            )

            AdaptiveBinarizationParams(
                algorithm = algorithm.toInt(),
                radius = radius.toFloat(),
                coefficient = coefficient.toFloat(),
                dynamicRange = dynamicRange.toFloat(),
                ink = ink.toInt().toColorModel(),
                paper = paper.toInt().toColorModel(),
                invert = invert.toBoolean()
            )
        }

        name == AgxToneMappingParams::class.simpleName -> {
            val (exposure, look, saturation, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            AgxToneMappingParams(
                exposure = exposure.toFloat(),
                look = look.toInt(),
                saturation = saturation.toFloat(),
                strength = strength.toFloat()
            )
        }

        name == AnamorphicStreaksParams::class.simpleName -> {
            val (threshold, angle, length, width, falloff, intensity) = value.split(
                PROPERTIES_SEPARATOR
            )

            AnamorphicStreaksParams(
                threshold = threshold.toFloat(),
                angle = angle.toFloat(),
                length = length.toFloat(),
                width = width.toFloat(),
                falloff = falloff.toFloat(),
                intensity = intensity.toFloat()
            )
        }

        name == AutostereogramParams::class.simpleName -> {
            val (repeatDistance, depth, gamma, size, softness, invert, seed) = value.split(
                PROPERTIES_SEPARATOR
            )

            AutostereogramParams(
                repeatDistance = repeatDistance.toFloat(),
                depth = depth.toFloat(),
                gamma = gamma.toFloat(),
                size = size.toInt(),
                softness = softness.toFloat(),
                invert = invert.toBoolean(),
                seed = seed.toInt()
            )
        }

        name == BayerSensorParams::class.simpleName -> {
            val (pattern, scale, shotNoise, readNoise, seed, strength, edgeAware) = value.split(
                PROPERTIES_SEPARATOR
            )

            BayerSensorParams(
                pattern = pattern.toInt(),
                scale = scale.toFloat(),
                shotNoise = shotNoise.toFloat(),
                readNoise = readNoise.toFloat(),
                seed = seed.toInt(),
                strength = strength.toFloat(),
                edgeAware = edgeAware.toBoolean()
            )
        }

        name == CapillaryInkBleedParams::class.simpleName -> {
            val (wetness, direction, spread, roughness, steps, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            CapillaryInkBleedParams(
                wetness = wetness.toFloat(),
                direction = direction.toFloat(),
                spread = spread.toFloat(),
                roughness = roughness.toFloat(),
                steps = steps.toInt(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == CmykPressParams::class.simpleName -> {
            val (spacing, blackGeneration, dotGain, misregistration, inkLimit, paperGrain, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            CmykPressParams(
                spacing = spacing.toInt(),
                blackGeneration = blackGeneration.toFloat(),
                dotGain = dotGain.toFloat(),
                misregistration = misregistration.toFloat(),
                inkLimit = inkLimit.toFloat(),
                paperGrain = paperGrain.toFloat(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == ContourHatchingParams::class.simpleName -> {
            val (spacing, width, angle, contrast, ink, paper, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            ContourHatchingParams(
                spacing = spacing.toFloat(),
                width = width.toFloat(),
                angle = angle.toFloat(),
                contrast = contrast.toFloat(),
                ink = ink.toInt().toColorModel(),
                paper = paper.toInt().toColorModel(),
                strength = strength.toFloat()
            )
        }

        name == DaltonizationParams::class.simpleName -> {
            val (visionType, severity, correction, preserveLuminance, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            DaltonizationParams(
                visionType = visionType.toInt(),
                severity = severity.toFloat(),
                correction = correction.toFloat(),
                preserveLuminance = preserveLuminance.toFloat(),
                strength = strength.toFloat()
            )
        }

        name == DctGlitchParams::class.simpleName -> {
            val (blockSize, minimumFrequency, maximumFrequency, operation, probability, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            DctGlitchParams(
                blockSize = blockSize.toInt(),
                minimumFrequency = minimumFrequency.toFloat(),
                maximumFrequency = maximumFrequency.toFloat(),
                operation = operation.toInt(),
                probability = probability.toFloat(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == DiffractionPsfParams::class.simpleName -> {
            val (blades, rotation, radius, spectralSpread, threshold, intensity) = value.split(
                PROPERTIES_SEPARATOR
            )

            DiffractionPsfParams(
                blades = blades.toInt(),
                rotation = rotation.toFloat(),
                radius = radius.toFloat(),
                spectralSpread = spectralSpread.toFloat(),
                threshold = threshold.toFloat(),
                intensity = intensity.toFloat()
            )
        }

        name == DirectionalEmbroideryParams::class.simpleName -> {
            val (stitchLength, density, thickness, relief, colorLevels, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            DirectionalEmbroideryParams(
                stitchLength = stitchLength.toFloat(),
                density = density.toFloat(),
                thickness = thickness.toFloat(),
                relief = relief.toFloat(),
                colorLevels = colorLevels.toInt(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == FilmGrainParams::class.simpleName -> {
            val (size, intensity, sizeVariation, exposureDependence, colorGrain, seed) = value.split(
                PROPERTIES_SEPARATOR
            )

            FilmGrainParams(
                size = size.toFloat(),
                intensity = intensity.toFloat(),
                sizeVariation = sizeVariation.toFloat(),
                exposureDependence = exposureDependence.toFloat(),
                colorGrain = colorGrain.toFloat(),
                seed = seed.toInt()
            )
        }

        name == FilmHalationParams::class.simpleName -> {
            val (threshold, knee, radius, warmth, intensity) = value.split(
                PROPERTIES_SEPARATOR
            )

            FilmHalationParams(
                threshold = threshold.toFloat(),
                knee = knee.toFloat(),
                radius = radius.toFloat(),
                warmth = warmth.toFloat(),
                intensity = intensity.toFloat()
            )
        }

        name == FilmNegativeParams::class.simpleName -> {
            val (exposure, densityRange, contrast, toneCurve, filmBase, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            FilmNegativeParams(
                exposure = exposure.toFloat(),
                densityRange = densityRange.toFloat(),
                contrast = contrast.toFloat(),
                toneCurve = toneCurve.toFloat(),
                filmBase = filmBase.toInt().toColorModel(),
                strength = strength.toFloat()
            )
        }

        name == FourierPhaseScrambleParams::class.simpleName -> {
            val (minFrequency, maxFrequency, phase, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            FourierPhaseScrambleParams(
                minFrequency = minFrequency.toFloat(),
                maxFrequency = maxFrequency.toFloat(),
                phase = phase.toFloat(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == GuidedSmoothingParams::class.simpleName -> {
            val (radius, epsilon, guide, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            GuidedSmoothingParams(
                radius = radius.toFloat(),
                epsilon = epsilon.toFloat(),
                guide = guide.toInt(),
                strength = strength.toFloat()
            )
        }

        name == LayeredPaperCutParams::class.simpleName -> {
            val (layers, smoothness, depth, lightAngle, grain, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            LayeredPaperCutParams(
                layers = layers.toInt(),
                smoothness = smoothness.toFloat(),
                depth = depth.toFloat(),
                lightAngle = lightAngle.toFloat(),
                grain = grain.toFloat(),
                strength = strength.toFloat()
            )
        }

        name == LcdSubpixelsParams::class.simpleName -> {
            val (size, blackMatrix, subpixelOrder, viewAngle, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            LcdSubpixelsParams(
                size = size.toInt(),
                blackMatrix = blackMatrix.toFloat(),
                subpixelOrder = subpixelOrder.toInt(),
                viewAngle = viewAngle.toInt(),
                strength = strength.toFloat()
            )
        }

        name == PalCompositeParams::class.simpleName -> {
            val (lumaFilter, chromaRadius, noise, phaseError, scanSamples, lineCompensation, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            PalCompositeParams(
                lumaFilter = lumaFilter.toFloat(),
                chromaRadius = chromaRadius.toInt(),
                noise = noise.toFloat(),
                phaseError = phaseError.toInt(),
                scanSamples = scanSamples.toInt(),
                lineCompensation = lineCompensation.toFloat(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == PaletteRemapParams::class.simpleName -> {
            val (dithering, shadowColor, midtoneColor, highlightColor) = value.split(
                PROPERTIES_SEPARATOR
            )

            PaletteRemapParams(
                dithering = dithering.toFloat(),
                shadowColor = shadowColor.toInt().toColorModel(),
                midtoneColor = midtoneColor.toInt().toColorModel(),
                highlightColor = highlightColor.toInt().toColorModel()
            )
        }

        name == PhotoCrossStitchParams::class.simpleName -> {
            val (size, threadColors, threadWidth, lightAngle, relief, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            PhotoCrossStitchParams(
                size = size.toInt(),
                threadColors = threadColors.toInt(),
                threadWidth = threadWidth.toFloat(),
                lightAngle = lightAngle.toInt(),
                relief = relief.toFloat(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == RetroHardwareParams::class.simpleName -> {
            val (system, scale, dithering, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            RetroHardwareParams(
                system = system.toInt(),
                scale = scale.toFloat(),
                dithering = dithering.toFloat(),
                strength = strength.toFloat()
            )
        }

        name == RfReceptionParams::class.simpleName -> {
            val (delay, reflection, noise, frequency, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            RfReceptionParams(
                delay = delay.toInt(),
                reflection = reflection.toFloat(),
                noise = noise.toFloat(),
                frequency = frequency.toInt(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == RisographParams::class.simpleName -> {
            val (spacing, misregistration, dotGain, inks, paperGrain, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            RisographParams(
                spacing = spacing.toInt(),
                misregistration = misregistration.toFloat(),
                dotGain = dotGain.toFloat(),
                inks = inks.toInt(),
                paperGrain = paperGrain.toFloat(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == SecamParams::class.simpleName -> {
            val (chromaRadius, frequencyDeviation, noise, preEmphasis, lineCrosstalk, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            SecamParams(
                chromaRadius = chromaRadius.toInt(),
                frequencyDeviation = frequencyDeviation.toFloat(),
                noise = noise.toFloat(),
                preEmphasis = preEmphasis.toFloat(),
                lineCrosstalk = lineCrosstalk.toFloat(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == SignedDistanceFieldParams::class.simpleName -> {
            val (alphaMask, threshold, distance, mode, spacing, insideColor, outsideColor, invert) = value.split(
                PROPERTIES_SEPARATOR
            )

            SignedDistanceFieldParams(
                alphaMask = alphaMask.toBoolean(),
                threshold = threshold.toFloat(),
                distance = distance.toFloat(),
                mode = mode.toInt(),
                spacing = spacing.toFloat(),
                insideColor = insideColor.toInt().toColorModel(),
                outsideColor = outsideColor.toInt().toColorModel(),
                invert = invert.toBoolean()
            )
        }

        name == SingleLineDrawingParams::class.simpleName -> {
            val (pointCount, densityCurve, lineWidth, ink, paper, optimization, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            SingleLineDrawingParams(
                pointCount = pointCount.toInt(),
                densityCurve = densityCurve.toFloat(),
                lineWidth = lineWidth.toFloat(),
                ink = ink.toInt().toColorModel(),
                paper = paper.toInt().toColorModel(),
                optimization = optimization.toInt(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == SstvReceptionParams::class.simpleName -> {
            val (transmissionMode, frequencyDrift, syncError, noise, dropout, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            SstvReceptionParams(
                transmissionMode = transmissionMode.toInt(),
                frequencyDrift = frequencyDrift.toFloat(),
                syncError = syncError.toFloat(),
                noise = noise.toFloat(),
                dropout = dropout.toFloat(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == ThermalReceiptParams::class.simpleName -> {
            val (size, heat, heatRetention, defects, fading, seed, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            ThermalReceiptParams(
                size = size.toInt(),
                heat = heat.toFloat(),
                heatRetention = heatRetention.toFloat(),
                defects = defects.toFloat(),
                fading = fading.toFloat(),
                seed = seed.toInt(),
                strength = strength.toFloat()
            )
        }

        name == WienerDeconvolutionParams::class.simpleName -> {
            val (psf, radius, angle, regularization, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            WienerDeconvolutionParams(
                psf = psf.toInt(),
                radius = radius.toFloat(),
                angle = angle.toFloat(),
                regularization = regularization.toFloat(),
                strength = strength.toFloat()
            )
        }

        name == XdogParams::class.simpleName -> {
            val (sigma, ratio, weight, threshold, sharpness, ink, paper, strength) = value.split(
                PROPERTIES_SEPARATOR
            )

            XdogParams(
                sigma = sigma.toFloat(),
                ratio = ratio.toFloat(),
                weight = weight.toFloat(),
                threshold = threshold.toFloat(),
                sharpness = sharpness.toFloat(),
                ink = ink.toInt().toColorModel(),
                paper = paper.toInt().toColorModel(),
                strength = strength.toFloat()
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

        name == RubberStampParams::class.simpleName -> {
            val (threshold, softness, radius, firstColor, secondColor) = value.split(
                PROPERTIES_SEPARATOR
            ).map { it.toFloat() }

            RubberStampParams(
                threshold = threshold,
                softness = softness,
                radius = radius,
                firstColor = firstColor.toInt().toColorModel(),
                secondColor = secondColor.toInt().toColorModel()
            )
        }

        name == SmearParams::class.simpleName -> {
            val (angle, density, mix, distance, shape) = value.split(
                PROPERTIES_SEPARATOR
            ).map { it.toFloat() }

            SmearParams(
                angle = angle,
                density = density,
                mix = mix,
                distance = distance.toInt(),
                shape = shape.toInt()
            )
        }

        name == ArcParams::class.simpleName -> {
            val (radius, height, angle, spreadAngle, centreX, centreY) = value.split(
                PROPERTIES_SEPARATOR
            ).map { it.toFloat() }

            ArcParams(
                radius = radius,
                height = height,
                angle = angle,
                spreadAngle = spreadAngle,
                centreX = centreX,
                centreY = centreY
            )
        }

        name == SparkleParams::class.simpleName -> {
            val (amount, rays, radius, randomness, centreX, centreY, color) = value.split(
                PROPERTIES_SEPARATOR
            )

            SparkleParams(
                amount = amount.toInt(),
                rays = rays.toInt(),
                radius = radius.toFloat(),
                randomness = randomness.toInt(),
                centreX = centreX.toFloat(),
                centreY = centreY.toFloat(),
                color = color.toInt().toColorModel()
            )
        }

        name == AsciiParams::class.simpleName -> {
            val (gradient, fontSize, backgroundColor, isGrayscale, font) = value.split(
                PROPERTIES_SEPARATOR
            )

            AsciiParams(
                gradient = Base64.decode(gradient).toString(Charsets.UTF_8),
                fontSize = fontSize.toFloat(),
                backgroundColor = backgroundColor.toInt().toColorModel(),
                isGrayscale = isGrayscale.toBoolean(),
                font = DomainFontFamily.fromString(font).asFontType()
            )
        }

        name == BloomParams::class.simpleName -> {
            val (threshold, intensity, radius, softKnee, exposure, gamma) = value.split(
                PROPERTIES_SEPARATOR
            )

            BloomParams(
                threshold = threshold.toFloat(),
                intensity = intensity.toFloat(),
                radius = radius.toInt(),
                softKnee = softKnee.toFloat(),
                exposure = exposure.toFloat(),
                gamma = gamma.toFloat()
            )
        }

        name == DropShadowParams::class.simpleName -> {
            val (color, offsetX, offsetY, blurRadius) = value.split(
                PROPERTIES_SEPARATOR
            )

            DropShadowParams(
                color = color.toInt().toColorModel(),
                offsetX = offsetX.toFloat(),
                offsetY = offsetY.toFloat(),
                blurRadius = blurRadius.toFloat()
            )
        }

        name == TornEdgeParams::class.simpleName -> {
            val (
                toothHeight,
                horizontalToothRange,
                verticalToothRange,
                top,
                right,
                bottom,
                left
            ) = value.split(PROPERTIES_SEPARATOR)

            TornEdgeParams(
                toothHeight = toothHeight.toInt(),
                horizontalToothRange = horizontalToothRange.toInt(),
                verticalToothRange = verticalToothRange.toInt(),
                top = top.toBoolean(),
                right = right.toBoolean(),
                bottom = bottom.toBoolean(),
                left = left.toBoolean()
            )
        }

        name == NtscParams::class.simpleName -> {
            val parts = value.split(PROPERTIES_SEPARATOR)
            val default = NtscParams()

            fun floatAt(index: Int, fallback: Float): Float {
                return parts.getOrNull(index)?.toFloatOrNull() ?: fallback
            }

            fun intAt(index: Int, fallback: Int): Int {
                return parts.getOrNull(index)?.toIntOrNull() ?: fallback
            }

            fun booleanAt(index: Int, fallback: Boolean): Boolean {
                return when (parts.getOrNull(index)) {
                    "true" -> true
                    "false" -> false
                    else -> fallback
                }
            }

            NtscParams(
                amount = floatAt(0, default.amount),
                chromaBleed = floatAt(1, default.chromaBleed),
                tapeWear = floatAt(2, default.tapeWear),
                noise = floatAt(3, default.noise),
                tracking = floatAt(4, default.tracking),
                seed = intAt(5, default.seed),
                lumaSmear = floatAt(6, default.lumaSmear),
                compositeSharpening = floatAt(7, default.compositeSharpening),
                ringing = floatAt(8, default.ringing),
                snow = floatAt(9, default.snow),
                useField = intAt(10, default.useField),
                filterType = intAt(11, default.filterType),
                inputLumaFilter = intAt(12, default.inputLumaFilter),
                chromaLowpassIn = intAt(13, default.chromaLowpassIn),
                chromaDemodulation = intAt(14, default.chromaDemodulation),
                videoScanlinePhaseShift = intAt(15, default.videoScanlinePhaseShift),
                videoScanlinePhaseShiftOffset = intAt(16, default.videoScanlinePhaseShiftOffset),
                headSwitchingEnabled = booleanAt(57, default.headSwitchingEnabled),
                headSwitchingHeight = intAt(17, default.headSwitchingHeight),
                headSwitchingOffset = intAt(18, default.headSwitchingOffset),
                headSwitchingHorizontalShift = floatAt(19, default.headSwitchingHorizontalShift),
                headSwitchingMidLinePosition = floatAt(20, default.headSwitchingMidLinePosition),
                headSwitchingMidLineJitter = floatAt(21, default.headSwitchingMidLineJitter),
                trackingNoiseEnabled = booleanAt(58, default.trackingNoiseEnabled),
                trackingNoiseHeight = intAt(22, default.trackingNoiseHeight),
                trackingNoiseWaveIntensity = floatAt(23, default.trackingNoiseWaveIntensity),
                trackingNoiseSnowIntensity = floatAt(24, default.trackingNoiseSnowIntensity),
                trackingNoiseSnowAnisotropy = floatAt(25, default.trackingNoiseSnowAnisotropy),
                trackingNoiseNoiseIntensity = floatAt(26, default.trackingNoiseNoiseIntensity),
                compositeNoiseEnabled = booleanAt(59, default.compositeNoiseEnabled),
                compositeNoiseFrequency = floatAt(27, default.compositeNoiseFrequency),
                compositeNoiseIntensity = floatAt(28, default.compositeNoiseIntensity),
                compositeNoiseDetail = intAt(29, default.compositeNoiseDetail),
                ringingEnabled = booleanAt(60, default.ringingEnabled),
                ringingFrequency = floatAt(30, default.ringingFrequency),
                ringingPower = floatAt(31, default.ringingPower),
                lumaNoiseEnabled = booleanAt(61, default.lumaNoiseEnabled),
                lumaNoiseFrequency = floatAt(32, default.lumaNoiseFrequency),
                lumaNoiseIntensity = floatAt(33, default.lumaNoiseIntensity),
                lumaNoiseDetail = intAt(34, default.lumaNoiseDetail),
                chromaNoiseEnabled = booleanAt(62, default.chromaNoiseEnabled),
                chromaNoiseFrequency = floatAt(35, default.chromaNoiseFrequency),
                chromaNoiseIntensity = floatAt(36, default.chromaNoiseIntensity),
                chromaNoiseDetail = intAt(37, default.chromaNoiseDetail),
                snowIntensity = floatAt(38, default.snowIntensity),
                snowAnisotropy = floatAt(39, default.snowAnisotropy),
                chromaPhaseNoiseIntensity = floatAt(40, default.chromaPhaseNoiseIntensity),
                chromaPhaseError = floatAt(41, default.chromaPhaseError),
                chromaDelayHorizontal = floatAt(42, default.chromaDelayHorizontal),
                chromaDelayVertical = intAt(43, default.chromaDelayVertical),
                vhsEnabled = booleanAt(63, default.vhsEnabled),
                vhsTapeSpeed = intAt(44, default.vhsTapeSpeed),
                vhsChromaLoss = floatAt(45, default.vhsChromaLoss),
                vhsSharpenIntensity = floatAt(46, default.vhsSharpenIntensity),
                vhsSharpenFrequency = floatAt(47, default.vhsSharpenFrequency),
                vhsEdgeWaveIntensity = floatAt(48, default.vhsEdgeWaveIntensity),
                vhsEdgeWaveSpeed = floatAt(49, default.vhsEdgeWaveSpeed),
                vhsEdgeWaveFrequency = floatAt(50, default.vhsEdgeWaveFrequency),
                vhsEdgeWaveDetail = intAt(51, default.vhsEdgeWaveDetail),
                chromaLowpassOut = intAt(52, default.chromaLowpassOut),
                scaleEnabled = booleanAt(64, default.scaleEnabled),
                scaleHorizontal = floatAt(53, default.scaleHorizontal),
                scaleVertical = floatAt(54, default.scaleVertical),
                scaleFactorX = floatAt(55, default.scaleFactorX),
                scaleFactorY = floatAt(56, default.scaleFactorY)
            )
        }

        name == CropOrPerspectiveParams::class.simpleName -> {
            val (topLeft, topRight, bottomLeft, bottomRight, isAbsolute) = value.split(
                PROPERTIES_SEPARATOR
            )

            CropOrPerspectiveParams(
                topLeft = topLeft.toFloatPair(),
                topRight = topRight.toFloatPair(),
                bottomLeft = bottomLeft.toFloatPair(),
                bottomRight = bottomRight.toFloatPair(),
                isAbsolute = isAbsolute.toBoolean()
            )
        }

        name == IntegerSize::class.simpleName -> {
            val (width, height) = value.split(PROPERTIES_SEPARATOR)

            IntegerSize(
                width = width.toInt(),
                height = height.toInt()
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

private fun Pair<*, *>.join() = "$first$ADDITIONAL_PROPERTIES_SEPARATOR$second"
private fun String.toFloatPair() =
    split(ADDITIONAL_PROPERTIES_SEPARATOR).let { it[0].toFloat() to it[1].toFloat() }

private const val PROPERTIES_SEPARATOR = "$"
private const val ADDITIONAL_PROPERTIES_SEPARATOR = "*"
