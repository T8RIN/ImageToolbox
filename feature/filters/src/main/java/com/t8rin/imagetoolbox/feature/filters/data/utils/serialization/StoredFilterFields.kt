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

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal class StoredFilterParameters(
    val type: String,
    val fields: List<KProperty1<*, *>>
)

internal fun KClass<out Filter<*>>.storedFieldOrder(): List<KProperty1<*, *>> =
    storedParameters()?.fields.orEmpty()

// Frozen from templates before the schema refactor, independently of the current control order.
// New filters use named fields and need no entry here.
internal fun KClass<out Filter<*>>.storedParameters(): StoredFilterParameters? = when (this) {
    Filter.AcesFilmicToneMapping::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.AcesFilmicToneMapping.Value::exposure)
    )

    Filter.AcesHillToneMapping::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.AcesHillToneMapping.Value::exposure)
    )

    Filter.AdaptiveBinarization::class -> StoredFilterParameters(
        "AdaptiveBinarizationParams",
        listOf(
            Filter.AdaptiveBinarization.Value::algorithm,
            Filter.AdaptiveBinarization.Value::radius,
            Filter.AdaptiveBinarization.Value::coefficient,
            Filter.AdaptiveBinarization.Value::dynamicRange,
            Filter.AdaptiveBinarization.Value::ink,
            Filter.AdaptiveBinarization.Value::paper,
            Filter.AdaptiveBinarization.Value::invert
        )
    )

    Filter.AdaptiveBlur::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.AdaptiveBlur.Value::radius,
            Filter.AdaptiveBlur.Value::threshold
        )
    )

    Filter.AgxToneMapping::class -> StoredFilterParameters(
        "AgxToneMappingParams",
        listOf(
            Filter.AgxToneMapping.Value::exposure,
            Filter.AgxToneMapping.Value::look,
            Filter.AgxToneMapping.Value::saturation,
            Filter.AgxToneMapping.Value::strength
        )
    )

    Filter.Aldridge::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.Aldridge.Value::exposure,
            Filter.Aldridge.Value::cutoff
        )
    )

    Filter.AlphaSharpen::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.AlphaSharpen.Value::settings)
    )

    Filter.Amatorka::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Amatorka.Value::strength)
    )

    Filter.Anaglyph::class -> StoredFilterParameters("Float", listOf(Filter.Anaglyph.Value::amount))
    Filter.AnalogDamage::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.AnalogDamage.Value::settings)
    )

    Filter.AnamorphicStreaks::class -> StoredFilterParameters(
        "AnamorphicStreaksParams",
        listOf(
            Filter.AnamorphicStreaks.Value::threshold,
            Filter.AnamorphicStreaks.Value::angle,
            Filter.AnamorphicStreaks.Value::length,
            Filter.AnamorphicStreaks.Value::width,
            Filter.AnamorphicStreaks.Value::falloff,
            Filter.AnamorphicStreaks.Value::intensity
        )
    )

    Filter.AnisotropicDiffusion::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.AnisotropicDiffusion.Value::repeatCount,
            Filter.AnisotropicDiffusion.Value::conduction,
            Filter.AnisotropicDiffusion.Value::diffusion
        )
    )

    Filter.AnisotropicKuwahara::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.AnisotropicKuwahara.Value::radius,
            Filter.AnisotropicKuwahara.Value::anisotropy,
            Filter.AnisotropicKuwahara.Value::selectivity,
            Filter.AnisotropicKuwahara.Value::strength
        )
    )

    Filter.Arc::class -> StoredFilterParameters(
        "ArcParams",
        listOf(
            Filter.Arc.Value::radius,
            Filter.Arc.Value::height,
            Filter.Arc.Value::angle,
            Filter.Arc.Value::spreadAngle,
            Filter.Arc.Value::centreX,
            Filter.Arc.Value::centreY
        )
    )

    Filter.Ascii::class -> StoredFilterParameters(
        "AsciiParams",
        listOf(
            Filter.Ascii.Value::gradient,
            Filter.Ascii.Value::fontSize,
            Filter.Ascii.Value::font,
            Filter.Ascii.Value::backgroundColor,
            Filter.Ascii.Value::isGrayscale
        )
    )

    Filter.AtkinsonDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.AtkinsonDithering.Value::threshold,
            Filter.AtkinsonDithering.Value::grayscale
        )
    )

    Filter.AutoCrop::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.AutoCrop.Value::tolerance)
    )

    Filter.AutoRemoveRedEyes::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.AutoRemoveRedEyes.Value::threshold)
    )

    Filter.AutoWhiteBalance::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.AutoWhiteBalance.Value::strength,
            Filter.AutoWhiteBalance.Value::clipping
        )
    )

    Filter.Autostereogram::class -> StoredFilterParameters(
        "AutostereogramParams",
        listOf(
            Filter.Autostereogram.Value::repeatDistance,
            Filter.Autostereogram.Value::depth,
            Filter.Autostereogram.Value::gamma,
            Filter.Autostereogram.Value::size,
            Filter.Autostereogram.Value::softness,
            Filter.Autostereogram.Value::invert,
            Filter.Autostereogram.Value::seed
        )
    )

    Filter.BandingDenoise::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.BandingDenoise.Value::settings)
    )

    Filter.BayerEightDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.BayerEightDithering.Value::threshold,
            Filter.BayerEightDithering.Value::grayscale
        )
    )

    Filter.BayerFourDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.BayerFourDithering.Value::threshold,
            Filter.BayerFourDithering.Value::grayscale
        )
    )

    Filter.BayerSensor::class -> StoredFilterParameters(
        "BayerSensorParams",
        listOf(
            Filter.BayerSensor.Value::pattern,
            Filter.BayerSensor.Value::scale,
            Filter.BayerSensor.Value::shotNoise,
            Filter.BayerSensor.Value::readNoise,
            Filter.BayerSensor.Value::seed,
            Filter.BayerSensor.Value::strength,
            Filter.BayerSensor.Value::edgeAware
        )
    )

    Filter.BayerSixteenDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.BayerSixteenDithering.Value::threshold,
            Filter.BayerSixteenDithering.Value::grayscale
        )
    )

    Filter.BayerThreeDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.BayerThreeDithering.Value::threshold,
            Filter.BayerThreeDithering.Value::grayscale
        )
    )

    Filter.BayerTwoDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.BayerTwoDithering.Value::threshold,
            Filter.BayerTwoDithering.Value::grayscale
        )
    )

    Filter.BilaterialBlur::class -> StoredFilterParameters(
        "BilaterialBlurParams",
        listOf(
            Filter.BilaterialBlur.Value::radius,
            Filter.BilaterialBlur.Value::spatialSigma,
            Filter.BilaterialBlur.Value::rangeSigma,
            Filter.BilaterialBlur.Value::edgeMode
        )
    )

    Filter.BlackAndWhiteFilm::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.BlackAndWhiteFilm.Value::settings)
    )

    Filter.BlackHat::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.BlackHat.Value::justSize,
            Filter.BlackHat.Value::useCircleKernel
        )
    )

    Filter.BleachBypass::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.BleachBypass.Value::strength)
    )

    Filter.BlockGlitch::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.BlockGlitch.Value::blockSize,
            Filter.BlockGlitch.Value::strength
        )
    )

    Filter.Blocks3D::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Blocks3D.Value::settings)
    )

    Filter.Bloom::class -> StoredFilterParameters(
        "BloomParams",
        listOf(
            Filter.Bloom.Value::threshold,
            Filter.Bloom.Value::intensity,
            Filter.Bloom.Value::radius,
            Filter.Bloom.Value::softKnee,
            Filter.Bloom.Value::exposure,
            Filter.Bloom.Value::gamma
        )
    )

    Filter.BlueNoiseDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.BlueNoiseDithering.Value::threshold,
            Filter.BlueNoiseDithering.Value::grayscale
        )
    )

    Filter.BlurredFrame::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.BlurredFrame.Value::settings)
    )

    Filter.Bokeh::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.Bokeh.Value::size,
            Filter.Bokeh.Value::amount
        )
    )

    Filter.BokehLights::class -> StoredFilterParameters(
        "BokehLightsParams",
        listOf(
            Filter.BokehLights.Value::lightColor,
            Filter.BokehLights.Value::intensity,
            Filter.BokehLights.Value::count,
            Filter.BokehLights.Value::blades,
            Filter.BokehLights.Value::radius,
            Filter.BokehLights.Value::variability,
            Filter.BokehLights.Value::colorVariation,
            Filter.BokehLights.Value::seed,
            Filter.BokehLights.Value::offsetX,
            Filter.BokehLights.Value::offsetY,
            Filter.BokehLights.Value::scale,
            Filter.BokehLights.Value::rotation
        )
    )

    Filter.BoostChromaticity::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.BoostChromaticity.Value::settings)
    )

    Filter.BorderFrame::class -> StoredFilterParameters(
        "Triple{Float\$Float\$ColorModel}",
        listOf(
            Filter.BorderFrame.Value::horizontalBorderThickness,
            Filter.BorderFrame.Value::verticalBorderThickness,
            Filter.BorderFrame.Value::borderColor
        )
    )

    Filter.BoxBlur::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.BoxBlur.Value::kernelSize)
    )

    Filter.Breaks::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Breaks.Value::settings)
    )

    Filter.Brightness::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Brightness.Value::bias)
    )

    Filter.BrokenCells::class -> StoredFilterParameters(
        "BrokenCellsParams",
        listOf(
            Filter.BrokenCells.Value::intensity,
            Filter.BrokenCells.Value::distortion,
            Filter.BrokenCells.Value::variability,
            Filter.BrokenCells.Value::frequency,
            Filter.BrokenCells.Value::seed,
            Filter.BrokenCells.Value::offsetX,
            Filter.BrokenCells.Value::offsetY,
            Filter.BrokenCells.Value::scale,
            Filter.BrokenCells.Value::rotation
        )
    )

    Filter.BrokenGlass::class -> StoredFilterParameters(
        "BrokenGlassParams",
        listOf(
            Filter.BrokenGlass.Value::intensity,
            Filter.BrokenGlass.Value::frequency,
            Filter.BrokenGlass.Value::seed,
            Filter.BrokenGlass.Value::offsetX,
            Filter.BrokenGlass.Value::offsetY,
            Filter.BrokenGlass.Value::scale,
            Filter.BrokenGlass.Value::rotation
        )
    )

    Filter.Brushify::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Brushify.Value::settings)
    )

    Filter.BufferError::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.BufferError.Value::settings)
    )

    Filter.BulgeDistortion::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.BulgeDistortion.Value::radius,
            Filter.BulgeDistortion.Value::scale
        )
    )

    Filter.BurkesDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.BurkesDithering.Value::threshold,
            Filter.BurkesDithering.Value::grayscale
        )
    )

    Filter.Candlelight::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Candlelight.Value::strength)
    )

    Filter.Canny::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.Canny.Value::thresholdOne,
            Filter.Canny.Value::thresholdTwo
        )
    )

    Filter.CapillaryInkBleed::class -> StoredFilterParameters(
        "CapillaryInkBleedParams",
        listOf(
            Filter.CapillaryInkBleed.Value::wetness,
            Filter.CapillaryInkBleed.Value::direction,
            Filter.CapillaryInkBleed.Value::spread,
            Filter.CapillaryInkBleed.Value::roughness,
            Filter.CapillaryInkBleed.Value::steps,
            Filter.CapillaryInkBleed.Value::seed,
            Filter.CapillaryInkBleed.Value::strength
        )
    )

    Filter.Celluloid::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Celluloid.Value::strength)
    )

    Filter.ChannelMix::class -> StoredFilterParameters(
        "ChannelMixParams",
        listOf(
            Filter.ChannelMix.Value::blueGreen,
            Filter.ChannelMix.Value::redBlue,
            Filter.ChannelMix.Value::greenRed,
            Filter.ChannelMix.Value::intoR,
            Filter.ChannelMix.Value::intoG,
            Filter.ChannelMix.Value::intoB
        )
    )

    Filter.ChannelProcessing::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ChannelProcessing.Value::settings)
    )

    Filter.Charcoal::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Charcoal.Value::settings)
    )

    Filter.ChessboardOverlay::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ChessboardOverlay.Value::settings)
    )

    Filter.ChromaticAberrations::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ChromaticAberrations.Value::settings)
    )

    Filter.Chrome::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.Chrome.Value::amount,
            Filter.Chrome.Value::exposure
        )
    )

    Filter.CinematicDream::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.CinematicDream.Value::settings)
    )

    Filter.CircleAbstraction::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.CircleAbstraction.Value::settings)
    )

    Filter.CircleBlur::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.CircleBlur.Value::size)
    )

    Filter.CirclePixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.CirclePixelation.Value::layers)
    )

    Filter.CircleTransform::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.CircleTransform.Value::settings)
    )

    Filter.CircularMirror::class -> StoredFilterParameters(
        "CircularMirrorParams",
        listOf(
            Filter.CircularMirror.Value::intensity,
            Filter.CircularMirror.Value::radius,
            Filter.CircularMirror.Value::mode,
            Filter.CircularMirror.Value::offsetX,
            Filter.CircularMirror.Value::offsetY,
            Filter.CircularMirror.Value::scale,
            Filter.CircularMirror.Value::rotation
        )
    )

    Filter.Clahe::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.Clahe.Value::threshold,
            Filter.Clahe.Value::gridSizeX,
            Filter.Clahe.Value::gridSizeY
        )
    )

    Filter.ClaheHSL::class -> StoredFilterParameters(
        "ClaheParams",
        listOf(
            Filter.ClaheHSL.Value::threshold,
            Filter.ClaheHSL.Value::gridSizeHorizontal,
            Filter.ClaheHSL.Value::gridSizeVertical,
            Filter.ClaheHSL.Value::binsCount
        )
    )

    Filter.ClaheHSV::class -> StoredFilterParameters(
        "ClaheParams",
        listOf(
            Filter.ClaheHSV.Value::threshold,
            Filter.ClaheHSV.Value::gridSizeHorizontal,
            Filter.ClaheHSV.Value::gridSizeVertical,
            Filter.ClaheHSV.Value::binsCount
        )
    )

    Filter.ClaheJzazbz::class -> StoredFilterParameters(
        "ClaheParams",
        listOf(
            Filter.ClaheJzazbz.Value::threshold,
            Filter.ClaheJzazbz.Value::gridSizeHorizontal,
            Filter.ClaheJzazbz.Value::gridSizeVertical,
            Filter.ClaheJzazbz.Value::binsCount
        )
    )

    Filter.ClaheLAB::class -> StoredFilterParameters(
        "ClaheParams",
        listOf(
            Filter.ClaheLAB.Value::threshold,
            Filter.ClaheLAB.Value::gridSizeHorizontal,
            Filter.ClaheLAB.Value::gridSizeVertical,
            Filter.ClaheLAB.Value::binsCount
        )
    )

    Filter.ClaheLUV::class -> StoredFilterParameters(
        "ClaheParams",
        listOf(
            Filter.ClaheLUV.Value::threshold,
            Filter.ClaheLUV.Value::gridSizeHorizontal,
            Filter.ClaheLUV.Value::gridSizeVertical,
            Filter.ClaheLUV.Value::binsCount
        )
    )

    Filter.ClaheOklab::class -> StoredFilterParameters(
        "ClaheParams",
        listOf(
            Filter.ClaheOklab.Value::threshold,
            Filter.ClaheOklab.Value::gridSizeHorizontal,
            Filter.ClaheOklab.Value::gridSizeVertical,
            Filter.ClaheOklab.Value::binsCount
        )
    )

    Filter.ClaheOklch::class -> StoredFilterParameters(
        "ClaheParams",
        listOf(
            Filter.ClaheOklch.Value::threshold,
            Filter.ClaheOklch.Value::gridSizeHorizontal,
            Filter.ClaheOklch.Value::gridSizeVertical,
            Filter.ClaheOklch.Value::binsCount
        )
    )

    Filter.CleanText::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.CleanText.Value::settings)
    )

    Filter.Closing::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.Closing.Value::justSize,
            Filter.Closing.Value::useCircleKernel
        )
    )

    Filter.Clustered2x2Dithering::class -> StoredFilterParameters(
        "null",
        listOf(Filter.Clustered2x2Dithering.Value::grayscale)
    )

    Filter.Clustered4x4Dithering::class -> StoredFilterParameters(
        "null",
        listOf(Filter.Clustered4x4Dithering.Value::grayscale)
    )

    Filter.Clustered8x8Dithering::class -> StoredFilterParameters(
        "null",
        listOf(Filter.Clustered8x8Dithering.Value::grayscale)
    )

    Filter.ClusteredDotDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.ClusteredDotDithering.Value::threshold,
            Filter.ClusteredDotDithering.Value::grayscale
        )
    )

    Filter.CmykPress::class -> StoredFilterParameters(
        "CmykPressParams",
        listOf(
            Filter.CmykPress.Value::spacing,
            Filter.CmykPress.Value::blackGeneration,
            Filter.CmykPress.Value::dotGain,
            Filter.CmykPress.Value::misregistration,
            Filter.CmykPress.Value::inkLimit,
            Filter.CmykPress.Value::paperGrain,
            Filter.CmykPress.Value::seed,
            Filter.CmykPress.Value::strength
        )
    )

    Filter.Coffee::class -> StoredFilterParameters("Float", listOf(Filter.Coffee.Value::strength))
    Filter.ColorAbstraction::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ColorAbstraction.Value::settings)
    )

    Filter.ColorAnomaly::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.ColorAnomaly.Value::amount)
    )

    Filter.ColorBalance::class -> StoredFilterParameters(
        "FloatArray",
        listOf(Filter.ColorBalance.Value::adjustments)
    )

    Filter.ColorHalftone::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.ColorHalftone.Value::radius,
            Filter.ColorHalftone.Value::cyan,
            Filter.ColorHalftone.Value::magenta,
            Filter.ColorHalftone.Value::yellow
        )
    )

    Filter.ColorMask::class -> StoredFilterParameters(
        "FilterValueWrapper{ColorModel}",
        listOf(Filter.ColorMask.Value::color)
    )

    Filter.ColorMatrix3x3::class -> StoredFilterParameters(
        "FloatArray",
        listOf(Filter.ColorMatrix3x3.Value::matrix)
    )

    Filter.ColorMatrix4x4::class -> StoredFilterParameters(
        "FloatArray",
        listOf(Filter.ColorMatrix4x4.Value::matrix)
    )

    Filter.ColorOverlay::class -> StoredFilterParameters(
        "FilterValueWrapper{ColorModel}",
        listOf(Filter.ColorOverlay.Value::color)
    )

    Filter.ColorPoster::class -> StoredFilterParameters(
        "Pair{Float\$ColorModel}",
        listOf(
            Filter.ColorPoster.Value::strength,
            Filter.ColorPoster.Value::color
        )
    )

    Filter.ColorTemperature::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ColorTemperature.Value::settings)
    )

    Filter.ColumnStreak::class -> StoredFilterParameters(
        "ColumnStreakParams",
        listOf(
            Filter.ColumnStreak.Value::intensity,
            Filter.ColumnStreak.Value::variability,
            Filter.ColumnStreak.Value::count,
            Filter.ColumnStreak.Value::offsetX,
            Filter.ColumnStreak.Value::offsetY,
            Filter.ColumnStreak.Value::scale,
            Filter.ColumnStreak.Value::rotation
        )
    )

    Filter.ConcentricCircleBreaks::class -> StoredFilterParameters(
        "ConcentricCircleBreaksParams",
        listOf(
            Filter.ConcentricCircleBreaks.Value::intensity,
            Filter.ConcentricCircleBreaks.Value::dampening,
            Filter.ConcentricCircleBreaks.Value::distortion,
            Filter.ConcentricCircleBreaks.Value::frequency,
            Filter.ConcentricCircleBreaks.Value::offsetX,
            Filter.ConcentricCircleBreaks.Value::offsetY,
            Filter.ConcentricCircleBreaks.Value::scale,
            Filter.ConcentricCircleBreaks.Value::rotation
        )
    )

    Filter.ConcentricSquareBreaks::class -> StoredFilterParameters(
        "ConcentricSquareBreaksParams",
        listOf(
            Filter.ConcentricSquareBreaks.Value::intensity,
            Filter.ConcentricSquareBreaks.Value::dampening,
            Filter.ConcentricSquareBreaks.Value::distortion,
            Filter.ConcentricSquareBreaks.Value::frequency,
            Filter.ConcentricSquareBreaks.Value::offsetX,
            Filter.ConcentricSquareBreaks.Value::offsetY,
            Filter.ConcentricSquareBreaks.Value::scale,
            Filter.ConcentricSquareBreaks.Value::rotation
        )
    )

    Filter.ConformalMap::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ConformalMap.Value::settings)
    )

    Filter.ConstrainedSharpen::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ConstrainedSharpen.Value::settings)
    )

    Filter.Contour::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$ColorModel}",
        listOf(
            Filter.Contour.Value::levels,
            Filter.Contour.Value::scale,
            Filter.Contour.Value::offset,
            Filter.Contour.Value::color
        )
    )

    Filter.ContourHatching::class -> StoredFilterParameters(
        "ContourHatchingParams",
        listOf(
            Filter.ContourHatching.Value::spacing,
            Filter.ContourHatching.Value::width,
            Filter.ContourHatching.Value::angle,
            Filter.ContourHatching.Value::contrast,
            Filter.ContourHatching.Value::ink,
            Filter.ContourHatching.Value::paper,
            Filter.ContourHatching.Value::strength
        )
    )

    Filter.Contrast::class -> StoredFilterParameters("Float", listOf(Filter.Contrast.Value::gain))
    Filter.ContrastPreservingGrayscale::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.ContrastPreservingGrayscale.Value::colorContrast,
            Filter.ContrastPreservingGrayscale.Value::contrast
        )
    )

    Filter.Convex::class -> StoredFilterParameters("Float", listOf(Filter.Convex.Value::strength))
    Filter.Convolution3x3::class -> StoredFilterParameters(
        "FloatArray",
        listOf(Filter.Convolution3x3.Value::kernel)
    )

    Filter.CopyMoveDetection::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.CopyMoveDetection.Value::retain,
            Filter.CopyMoveDetection.Value::coefficent
        )
    )

    Filter.Cracks::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Cracks.Value::settings)
    )

    Filter.Crease::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Crease.Value::settings)
    )

    Filter.CropOrPerspective::class -> StoredFilterParameters(
        "CropOrPerspectiveParams",
        listOf(
            Filter.CropOrPerspective.Value::topLeft,
            Filter.CropOrPerspective.Value::topRight,
            Filter.CropOrPerspective.Value::bottomLeft,
            Filter.CropOrPerspective.Value::bottomRight,
            Filter.CropOrPerspective.Value::isAbsolute
        )
    )

    Filter.CropToContent::class -> StoredFilterParameters(
        "Pair{Float\$ColorModel}",
        listOf(
            Filter.CropToContent.Value::tolerance,
            Filter.CropToContent.Value::colorToIgnore
        )
    )

    Filter.CrossBlur::class -> StoredFilterParameters("Float", listOf(Filter.CrossBlur.Value::size))
    Filter.CrossPixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.CrossPixelation.Value::layers)
    )

    Filter.Crosshatch::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.Crosshatch.Value::spacing,
            Filter.Crosshatch.Value::lineWidth
        )
    )

    Filter.CrosshatchMatrixDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.CrosshatchMatrixDithering.Value::threshold,
            Filter.CrosshatchMatrixDithering.Value::grayscale
        )
    )

    Filter.CrtCurvature::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.CrtCurvature.Value::curvature,
            Filter.CrtCurvature.Value::vignette,
            Filter.CrtCurvature.Value::chroma
        )
    )

    Filter.CrtPhosphors::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.CrtPhosphors.Value::settings)
    )

    Filter.CrtScanlines::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.CrtScanlines.Value::settings)
    )

    Filter.Crystallize::class -> StoredFilterParameters(
        "Pair{Float\$ColorModel}",
        listOf(
            Filter.Crystallize.Value::amount,
            Filter.Crystallize.Value::strokeColor
        )
    )

    Filter.CubeFrame::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.CubeFrame.Value::settings)
    )

    Filter.Cubism::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Cubism.Value::settings)
    )

    Filter.Cutout::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Cutout.Value::settings)
    )

    Filter.Daltonization::class -> StoredFilterParameters(
        "DaltonizationParams",
        listOf(
            Filter.Daltonization.Value::visionType,
            Filter.Daltonization.Value::severity,
            Filter.Daltonization.Value::correction,
            Filter.Daltonization.Value::preserveLuminance,
            Filter.Daltonization.Value::strength
        )
    )

    Filter.DcpDehaze::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.DcpDehaze.Value::settings)
    )

    Filter.DctGlitch::class -> StoredFilterParameters(
        "DctGlitchParams",
        listOf(
            Filter.DctGlitch.Value::blockSize,
            Filter.DctGlitch.Value::minimumFrequency,
            Filter.DctGlitch.Value::maximumFrequency,
            Filter.DctGlitch.Value::operation,
            Filter.DctGlitch.Value::probability,
            Filter.DctGlitch.Value::seed,
            Filter.DctGlitch.Value::strength
        )
    )

    Filter.Deblur::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Deblur.Value::settings)
    )

    Filter.Dehaze::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.Dehaze.Value::radius,
            Filter.Dehaze.Value::omega
        )
    )

    Filter.DenoiseSmooth::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.DenoiseSmooth.Value::settings)
    )

    Filter.DesaturateNorm::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.DesaturateNorm.Value::settings)
    )

    Filter.Descreen::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Descreen.Value::settings)
    )

    Filter.Deskew::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.Deskew.Value::max,
            Filter.Deskew.Value::allowCrop
        )
    )

    Filter.DetailsEqualizer::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.DetailsEqualizer.Value::settings)
    )

    Filter.DiagonalLinesDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.DiagonalLinesDithering.Value::threshold,
            Filter.DiagonalLinesDithering.Value::grayscale
        )
    )

    Filter.DiamondDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.DiamondDithering.Value::threshold,
            Filter.DiamondDithering.Value::grayscale
        )
    )

    Filter.DiamondPixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.DiamondPixelation.Value::layers)
    )

    Filter.DiceMosaic::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.DiceMosaic.Value::settings)
    )

    Filter.DiffractionPsf::class -> StoredFilterParameters(
        "DiffractionPsfParams",
        listOf(
            Filter.DiffractionPsf.Value::blades,
            Filter.DiffractionPsf.Value::rotation,
            Filter.DiffractionPsf.Value::radius,
            Filter.DiffractionPsf.Value::spectralSpread,
            Filter.DiffractionPsf.Value::threshold,
            Filter.DiffractionPsf.Value::intensity
        )
    )

    Filter.Diffuse::class -> StoredFilterParameters("Float", listOf(Filter.Diffuse.Value::scale))
    Filter.DiffusionTensors::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.DiffusionTensors.Value::settings)
    )

    Filter.Dilation::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.Dilation.Value::justSize,
            Filter.Dilation.Value::useCircleKernel
        )
    )

    Filter.DirectionalEmbroidery::class -> StoredFilterParameters(
        "DirectionalEmbroideryParams",
        listOf(
            Filter.DirectionalEmbroidery.Value::stitchLength,
            Filter.DirectionalEmbroidery.Value::density,
            Filter.DirectionalEmbroidery.Value::thickness,
            Filter.DirectionalEmbroidery.Value::relief,
            Filter.DirectionalEmbroidery.Value::colorLevels,
            Filter.DirectionalEmbroidery.Value::seed,
            Filter.DirectionalEmbroidery.Value::strength
        )
    )

    Filter.Dirty::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Dirty.Value::settings)
    )

    Filter.Dissolve::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.Dissolve.Value::density,
            Filter.Dissolve.Value::softness
        )
    )

    Filter.DistortPerspective::class -> StoredFilterParameters(
        "DistortPerspectiveParams",
        listOf(
            Filter.DistortPerspective.Value::topLeft,
            Filter.DistortPerspective.Value::topRight,
            Filter.DistortPerspective.Value::bottomLeft,
            Filter.DistortPerspective.Value::bottomRight,
            Filter.DistortPerspective.Value::clip
        )
    )

    Filter.Distortion::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Distortion.Value::strength)
    )

    Filter.DoG::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.DoG.Value::radius,
            Filter.DoG.Value::secondRadius
        )
    )

    Filter.DodgeSketch::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.DodgeSketch.Value::settings)
    )

    Filter.DomainTransform::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.DomainTransform.Value::radius,
            Filter.DomainTransform.Value::sensitivity,
            Filter.DomainTransform.Value::iterations,
            Filter.DomainTransform.Value::strength
        )
    )

    Filter.Doodle::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Doodle.Value::settings)
    )

    Filter.Drago::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.Drago.Value::exposure,
            Filter.Drago.Value::threshold
        )
    )

    Filter.DreamPainting::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.DreamPainting.Value::settings)
    )

    Filter.DropBlues::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.DropBlues.Value::strength)
    )

    Filter.DropShadow::class -> StoredFilterParameters(
        "DropShadowParams",
        listOf(
            Filter.DropShadow.Value::color,
            Filter.DropShadow.Value::offsetX,
            Filter.DropShadow.Value::offsetY,
            Filter.DropShadow.Value::blurRadius
        )
    )

    Filter.Droste::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Droste.Value::settings)
    )

    Filter.DynamicContrast::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.DynamicContrast.Value::settings)
    )

    Filter.EdgyAmber::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.EdgyAmber.Value::strength)
    )

    Filter.Ellipsionism::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Ellipsionism.Value::settings)
    )

    Filter.Emboss::class -> StoredFilterParameters("Float", listOf(Filter.Emboss.Value::intensity))
    Filter.Engrave::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Engrave.Value::settings)
    )

    Filter.EnhancedCirclePixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.EnhancedCirclePixelation.Value::layers)
    )

    Filter.EnhancedDiamondPixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.EnhancedDiamondPixelation.Value::layers)
    )

    Filter.EnhancedGlitch::class -> StoredFilterParameters(
        "GlitchParams",
        listOf(
            Filter.EnhancedGlitch.Value::channelsShiftX,
            Filter.EnhancedGlitch.Value::channelsShiftY,
            Filter.EnhancedGlitch.Value::corruptionSize,
            Filter.EnhancedGlitch.Value::corruptionCount,
            Filter.EnhancedGlitch.Value::corruptionShiftX,
            Filter.EnhancedGlitch.Value::corruptionShiftY
        )
    )

    Filter.EnhancedOil::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.EnhancedOil.Value::strength)
    )

    Filter.EnhancedPixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.EnhancedPixelation.Value::layers)
    )

    Filter.EnhancedZoomBlur::class -> StoredFilterParameters(
        "EnhancedZoomBlurParams",
        listOf(
            Filter.EnhancedZoomBlur.Value::radius,
            Filter.EnhancedZoomBlur.Value::sigma,
            Filter.EnhancedZoomBlur.Value::centerX,
            Filter.EnhancedZoomBlur.Value::centerY,
            Filter.EnhancedZoomBlur.Value::strength,
            Filter.EnhancedZoomBlur.Value::angle
        )
    )

    Filter.EqualizeHistogramAdaptive::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.EqualizeHistogramAdaptive.Value::gridSizeX,
            Filter.EqualizeHistogramAdaptive.Value::gridSizeY
        )
    )

    Filter.EqualizeHistogramAdaptiveHSL::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.EqualizeHistogramAdaptiveHSL.Value::gridSizeX,
            Filter.EqualizeHistogramAdaptiveHSL.Value::gridSizeY,
            Filter.EqualizeHistogramAdaptiveHSL.Value::binsCount
        )
    )

    Filter.EqualizeHistogramAdaptiveHSV::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.EqualizeHistogramAdaptiveHSV.Value::gridSizeX,
            Filter.EqualizeHistogramAdaptiveHSV.Value::gridSizeY,
            Filter.EqualizeHistogramAdaptiveHSV.Value::binsCount
        )
    )

    Filter.EqualizeHistogramAdaptiveLAB::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.EqualizeHistogramAdaptiveLAB.Value::gridSizeX,
            Filter.EqualizeHistogramAdaptiveLAB.Value::gridSizeY,
            Filter.EqualizeHistogramAdaptiveLAB.Value::binsCount
        )
    )

    Filter.EqualizeHistogramAdaptiveLUV::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.EqualizeHistogramAdaptiveLUV.Value::gridSizeX,
            Filter.EqualizeHistogramAdaptiveLUV.Value::gridSizeY,
            Filter.EqualizeHistogramAdaptiveLUV.Value::binsCount
        )
    )

    Filter.EqualizeHistogramHSV::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.EqualizeHistogramHSV.Value::binsCount)
    )

    Filter.EqualizeHistogramPixelation::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.EqualizeHistogramPixelation.Value::gridSizeX,
            Filter.EqualizeHistogramPixelation.Value::gridSizeY
        )
    )

    Filter.EqualizeLight::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.EqualizeLight.Value::settings)
    )

    Filter.EqualizeShadow::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.EqualizeShadow.Value::settings)
    )

    Filter.EquirectangularProjection::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.EquirectangularProjection.Value::settings)
    )

    Filter.Erode::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.Erode.Value::justSize,
            Filter.Erode.Value::useCircleKernel
        )
    )

    Filter.ErrorLevelAnalysis::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.ErrorLevelAnalysis.Value::quality)
    )

    Filter.ExpandImage::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.ExpandImage.Value::top,
            Filter.ExpandImage.Value::start,
            Filter.ExpandImage.Value::end,
            Filter.ExpandImage.Value::bottom
        )
    )

    Filter.Exposure::class -> StoredFilterParameters("Float", listOf(Filter.Exposure.Value::amount))
    Filter.FadedArray::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.FadedArray.Value::settings)
    )

    Filter.FallColors::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.FallColors.Value::strength)
    )

    Filter.FalseColor::class -> StoredFilterParameters(
        "Pair{ColorModel\$ColorModel}",
        listOf(
            Filter.FalseColor.Value::darkColor,
            Filter.FalseColor.Value::lightColor
        )
    )

    Filter.FalseFloydSteinbergDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.FalseFloydSteinbergDithering.Value::threshold,
            Filter.FalseFloydSteinbergDithering.Value::grayscale
        )
    )

    Filter.FastBilaterialBlur::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.FastBilaterialBlur.Value::justSize,
            Filter.FastBilaterialBlur.Value::sigma,
            Filter.FastBilaterialBlur.Value::spatialSigma
        )
    )

    Filter.FastBlur::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.FastBlur.Value::scale,
            Filter.FastBlur.Value::radius
        )
    )

    Filter.FastGaussianBlur2D::class -> StoredFilterParameters(
        "Pair{Float\$BlurEdgeMode}",
        listOf(
            Filter.FastGaussianBlur2D.Value::radius,
            Filter.FastGaussianBlur2D.Value::edgeMode
        )
    )

    Filter.FastGaussianBlur3D::class -> StoredFilterParameters(
        "Pair{Float\$BlurEdgeMode}",
        listOf(
            Filter.FastGaussianBlur3D.Value::radius,
            Filter.FastGaussianBlur3D.Value::edgeMode
        )
    )

    Filter.FastGaussianBlur4D::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.FastGaussianBlur4D.Value::radius)
    )

    Filter.Feedback::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.Feedback.Value::distance,
            Filter.Feedback.Value::angle,
            Filter.Feedback.Value::rotation,
            Filter.Feedback.Value::zoom
        )
    )

    Filter.FeltPen::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.FeltPen.Value::settings)
    )

    Filter.Filaments::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Filaments.Value::settings)
    )

    Filter.FillHoles::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.FillHoles.Value::settings)
    )

    Filter.FilmGrain::class -> StoredFilterParameters(
        "FilmGrainParams",
        listOf(
            Filter.FilmGrain.Value::size,
            Filter.FilmGrain.Value::intensity,
            Filter.FilmGrain.Value::sizeVariation,
            Filter.FilmGrain.Value::exposureDependence,
            Filter.FilmGrain.Value::colorGrain,
            Filter.FilmGrain.Value::seed
        )
    )

    Filter.FilmHalation::class -> StoredFilterParameters(
        "FilmHalationParams",
        listOf(
            Filter.FilmHalation.Value::threshold,
            Filter.FilmHalation.Value::knee,
            Filter.FilmHalation.Value::radius,
            Filter.FilmHalation.Value::warmth,
            Filter.FilmHalation.Value::intensity
        )
    )

    Filter.FilmNegative::class -> StoredFilterParameters(
        "FilmNegativeParams",
        listOf(
            Filter.FilmNegative.Value::exposure,
            Filter.FilmNegative.Value::densityRange,
            Filter.FilmNegative.Value::contrast,
            Filter.FilmNegative.Value::toneCurve,
            Filter.FilmNegative.Value::filmBase,
            Filter.FilmNegative.Value::strength
        )
    )

    Filter.FilmStock50::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.FilmStock50.Value::strength)
    )

    Filter.Fireworks::class -> StoredFilterParameters(
        "FireworksParams",
        listOf(
            Filter.Fireworks.Value::seed,
            Filter.Fireworks.Value::explosions,
            Filter.Fireworks.Value::particles,
            Filter.Fireworks.Value::mode,
            Filter.Fireworks.Value::intensity,
            Filter.Fireworks.Value::time,
            Filter.Fireworks.Value::offsetX,
            Filter.Fireworks.Value::offsetY,
            Filter.Fireworks.Value::scale,
            Filter.Fireworks.Value::rotation
        )
    )

    Filter.FishEye::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.FishEye.Value::settings)
    )

    Filter.Flare::class -> StoredFilterParameters(
        "FlareParams",
        listOf(
            Filter.Flare.Value::radius,
            Filter.Flare.Value::baseAmount,
            Filter.Flare.Value::ringAmount,
            Filter.Flare.Value::rayAmount,
            Filter.Flare.Value::ringWidth,
            Filter.Flare.Value::centreX,
            Filter.Flare.Value::centreY,
            Filter.Flare.Value::color
        )
    )

    Filter.FlipAndRotateBlocks::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.FlipAndRotateBlocks.Value::settings)
    )

    Filter.Flower::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Flower.Value::settings)
    )

    Filter.FloydSteinbergDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.FloydSteinbergDithering.Value::threshold,
            Filter.FloydSteinbergDithering.Value::grayscale
        )
    )

    Filter.FoggyNight::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.FoggyNight.Value::strength)
    )

    Filter.FourierPhaseScramble::class -> StoredFilterParameters(
        "FourierPhaseScrambleParams",
        listOf(
            Filter.FourierPhaseScramble.Value::minFrequency,
            Filter.FourierPhaseScramble.Value::maxFrequency,
            Filter.FourierPhaseScramble.Value::phase,
            Filter.FourierPhaseScramble.Value::seed,
            Filter.FourierPhaseScramble.Value::strength
        )
    )

    Filter.FractalGlass::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.FractalGlass.Value::strength,
            Filter.FractalGlass.Value::amplitude
        )
    )

    Filter.Fractalize::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Fractalize.Value::settings)
    )

    Filter.FreakyBlackAndWhite::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.FreakyBlackAndWhite.Value::settings)
    )

    Filter.FreakyDetails::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.FreakyDetails.Value::settings)
    )

    Filter.FrostedGlass::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.FrostedGlass.Value::settings)
    )

    Filter.FuzzyFrame::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.FuzzyFrame.Value::settings)
    )

    Filter.Gallium::class -> StoredFilterParameters(
        "GalliumParams",
        listOf(
            Filter.Gallium.Value::iterations,
            Filter.Gallium.Value::intensity,
            Filter.Gallium.Value::offsetX,
            Filter.Gallium.Value::offsetY,
            Filter.Gallium.Value::scale,
            Filter.Gallium.Value::rotation
        )
    )

    Filter.Gamma::class -> StoredFilterParameters("Float", listOf(Filter.Gamma.Value::gamma))
    Filter.GaussianBlur::class -> StoredFilterParameters(
        "Triple{Float\$Float\$BlurEdgeMode}",
        listOf(
            Filter.GaussianBlur.Value::radius,
            Filter.GaussianBlur.Value::sigma,
            Filter.GaussianBlur.Value::edgeMode
        )
    )

    Filter.GaussianBoxBlur::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.GaussianBoxBlur.Value::sigma)
    )

    Filter.Ghost::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Ghost.Value::settings)
    )

    Filter.GlassHexTiles::class -> StoredFilterParameters(
        "GlassHexTilesParams",
        listOf(
            Filter.GlassHexTiles.Value::intensity,
            Filter.GlassHexTiles.Value::distortion,
            Filter.GlassHexTiles.Value::frequency,
            Filter.GlassHexTiles.Value::offsetX,
            Filter.GlassHexTiles.Value::offsetY,
            Filter.GlassHexTiles.Value::scale,
            Filter.GlassHexTiles.Value::rotation
        )
    )

    Filter.GlassRectTiles::class -> StoredFilterParameters(
        "GlassRectTilesParams",
        listOf(
            Filter.GlassRectTiles.Value::intensity,
            Filter.GlassRectTiles.Value::distortion,
            Filter.GlassRectTiles.Value::frequency,
            Filter.GlassRectTiles.Value::offsetX,
            Filter.GlassRectTiles.Value::offsetY,
            Filter.GlassRectTiles.Value::scale,
            Filter.GlassRectTiles.Value::rotation
        )
    )

    Filter.GlassSphereRefraction::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.GlassSphereRefraction.Value::radius,
            Filter.GlassSphereRefraction.Value::refractiveIndex
        )
    )

    Filter.GlassTriangleTiles::class -> StoredFilterParameters(
        "GlassTriangleTilesParams",
        listOf(
            Filter.GlassTriangleTiles.Value::intensity,
            Filter.GlassTriangleTiles.Value::distortion,
            Filter.GlassTriangleTiles.Value::frequency,
            Filter.GlassTriangleTiles.Value::offsetX,
            Filter.GlassTriangleTiles.Value::offsetY,
            Filter.GlassTriangleTiles.Value::scale,
            Filter.GlassTriangleTiles.Value::rotation
        )
    )

    Filter.Glitch::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.Glitch.Value::amount,
            Filter.Glitch.Value::seed,
            Filter.Glitch.Value::repeatCount
        )
    )

    Filter.GlitchVariant::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.GlitchVariant.Value::repeatCount,
            Filter.GlitchVariant.Value::maxOffset,
            Filter.GlitchVariant.Value::channelShift
        )
    )

    Filter.Glow::class -> StoredFilterParameters("Float", listOf(Filter.Glow.Value::amount))
    Filter.GoldMeinelSharpen::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.GoldMeinelSharpen.Value::settings)
    )

    Filter.GoldenForest::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.GoldenForest.Value::strength)
    )

    Filter.GradientMap::class -> StoredFilterParameters(
        "GradientPalette",
        listOf(Filter.GradientMap.Value::palette)
    )

    Filter.GradientSharpen::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.GradientSharpen.Value::settings)
    )

    Filter.GraduatedColorAccent::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.GraduatedColorAccent.Value::settings)
    )

    Filter.Grain::class -> StoredFilterParameters("Float", listOf(Filter.Grain.Value::intensity))
    Filter.GraphicNovel::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.GraphicNovel.Value::settings)
    )

    Filter.Grayscale::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.Grayscale.Value::colorRed,
            Filter.Grayscale.Value::colorGreen,
            Filter.Grayscale.Value::colorBlue
        )
    )

    Filter.Greenish::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Greenish.Value::strength)
    )

    Filter.Grid::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Grid.Value::settings)
    )

    Filter.GridSineDistortion2::class -> StoredFilterParameters(
        "GridSineDistortion2Params",
        listOf(
            Filter.GridSineDistortion2.Value::intensity,
            Filter.GridSineDistortion2.Value::frequency,
            Filter.GridSineDistortion2.Value::offsetX,
            Filter.GridSineDistortion2.Value::offsetY,
            Filter.GridSineDistortion2.Value::scale,
            Filter.GridSineDistortion2.Value::rotation
        )
    )

    Filter.GuidedSmoothing::class -> StoredFilterParameters(
        "GuidedSmoothingParams",
        listOf(
            Filter.GuidedSmoothing.Value::radius,
            Filter.GuidedSmoothing.Value::epsilon,
            Filter.GuidedSmoothing.Value::guide,
            Filter.GuidedSmoothing.Value::strength
        )
    )

    Filter.HableFilmicToneMapping::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.HableFilmicToneMapping.Value::exposure)
    )

    Filter.Halftone::class -> StoredFilterParameters("Float", listOf(Filter.Halftone.Value::amount))
    Filter.Haze::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.Haze.Value::distance,
            Filter.Haze.Value::slope
        )
    )

    Filter.Hedcut::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Hedcut.Value::settings)
    )

    Filter.HejlBurgessToneMapping::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.HejlBurgessToneMapping.Value::exposure)
    )

    Filter.HelixWaves::class -> StoredFilterParameters(
        "HelixWavesParams",
        listOf(
            Filter.HelixWaves.Value::frequency,
            Filter.HelixWaves.Value::intensity,
            Filter.HelixWaves.Value::mode,
            Filter.HelixWaves.Value::lighting,
            Filter.HelixWaves.Value::offsetX,
            Filter.HelixWaves.Value::offsetY,
            Filter.HelixWaves.Value::scale,
            Filter.HelixWaves.Value::rotation
        )
    )

    Filter.HessianSharpen::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.HessianSharpen.Value::settings)
    )

    Filter.HexKaleidoscope::class -> StoredFilterParameters(
        "HexKaleidoscopeParams",
        listOf(
            Filter.HexKaleidoscope.Value::count,
            Filter.HexKaleidoscope.Value::frequency,
            Filter.HexKaleidoscope.Value::offset,
            Filter.HexKaleidoscope.Value::mode,
            Filter.HexKaleidoscope.Value::offsetX,
            Filter.HexKaleidoscope.Value::offsetY,
            Filter.HexKaleidoscope.Value::scale,
            Filter.HexKaleidoscope.Value::rotation
        )
    )

    Filter.HexagonalDotDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.HexagonalDotDithering.Value::threshold,
            Filter.HexagonalDotDithering.Value::grayscale
        )
    )

    Filter.HighPass::class -> StoredFilterParameters("Float", listOf(Filter.HighPass.Value::radius))
    Filter.HighlightSynthesis::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.HighlightSynthesis.Value::settings)
    )

    Filter.HighlightsAndShadows::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.HighlightsAndShadows.Value::amount)
    )

    Filter.HopePoster::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.HopePoster.Value::settings)
    )

    Filter.HorizontalLinesDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.HorizontalLinesDithering.Value::threshold,
            Filter.HorizontalLinesDithering.Value::grayscale
        )
    )

    Filter.HorizontalWindStagger::class -> StoredFilterParameters(
        "Triple{Float\$Int\$ColorModel}",
        listOf(
            Filter.HorizontalWindStagger.Value::strength,
            Filter.HorizontalWindStagger.Value::amount,
            Filter.HorizontalWindStagger.Value::color
        )
    )

    Filter.HoughSketch::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.HoughSketch.Value::settings)
    )

    Filter.HslAdjustment::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.HslAdjustment.Value::settings)
    )

    Filter.HsvEqualizer::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.HsvEqualizer.Value::settings)
    )

    Filter.Hue::class -> StoredFilterParameters("Float", listOf(Filter.Hue.Value::hue))
    Filter.HueShiftBands::class -> StoredFilterParameters(
        "HueShiftBandsParams",
        listOf(
            Filter.HueShiftBands.Value::intensity,
            Filter.HueShiftBands.Value::saturation,
            Filter.HueShiftBands.Value::hueOffset,
            Filter.HueShiftBands.Value::offsetX,
            Filter.HueShiftBands.Value::offsetY,
            Filter.HueShiftBands.Value::scale,
            Filter.HueShiftBands.Value::rotation
        )
    )

    Filter.HueShiftFan::class -> StoredFilterParameters(
        "HueShiftFanParams",
        listOf(
            Filter.HueShiftFan.Value::intensity,
            Filter.HueShiftFan.Value::saturation,
            Filter.HueShiftFan.Value::hueOffset,
            Filter.HueShiftFan.Value::offsetX,
            Filter.HueShiftFan.Value::offsetY,
            Filter.HueShiftFan.Value::scale,
            Filter.HueShiftFan.Value::rotation
        )
    )

    Filter.HueShiftTunnel::class -> StoredFilterParameters(
        "HueShiftTunnelParams",
        listOf(
            Filter.HueShiftTunnel.Value::intensity,
            Filter.HueShiftTunnel.Value::saturation,
            Filter.HueShiftTunnel.Value::hueOffset,
            Filter.HueShiftTunnel.Value::offsetX,
            Filter.HueShiftTunnel.Value::offsetY,
            Filter.HueShiftTunnel.Value::scale,
            Filter.HueShiftTunnel.Value::rotation
        )
    )

    Filter.HuffmanGlitch::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.HuffmanGlitch.Value::settings)
    )

    Filter.InkWash::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.InkWash.Value::settings)
    )

    Filter.InlineBreaks::class -> StoredFilterParameters(
        "InlineBreaksParams",
        listOf(
            Filter.InlineBreaks.Value::intensity,
            Filter.InlineBreaks.Value::distortion,
            Filter.InlineBreaks.Value::frequency,
            Filter.InlineBreaks.Value::offsetX,
            Filter.InlineBreaks.Value::offsetY,
            Filter.InlineBreaks.Value::scale,
            Filter.InlineBreaks.Value::rotation
        )
    )

    Filter.InterleavedGradientNoiseDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.InterleavedGradientNoiseDithering.Value::threshold,
            Filter.InterleavedGradientNoiseDithering.Value::grayscale
        )
    )

    Filter.InversionFractal::class -> StoredFilterParameters(
        "InversionFractalParams",
        listOf(
            Filter.InversionFractal.Value::mode,
            Filter.InversionFractal.Value::iterations,
            Filter.InversionFractal.Value::intensity,
            Filter.InversionFractal.Value::offsetX,
            Filter.InversionFractal.Value::offsetY,
            Filter.InversionFractal.Value::scale,
            Filter.InversionFractal.Value::rotation
        )
    )

    Filter.Isophotes::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Isophotes.Value::settings)
    )

    Filter.JarvisJudiceNinkeDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.JarvisJudiceNinkeDithering.Value::threshold,
            Filter.JarvisJudiceNinkeDithering.Value::grayscale
        )
    )

    Filter.JpegArtifacts::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.JpegArtifacts.Value::settings)
    )

    Filter.Kaleidoscope::class -> StoredFilterParameters(
        "KaleidoscopeParams",
        listOf(
            Filter.Kaleidoscope.Value::angle,
            Filter.Kaleidoscope.Value::angle2,
            Filter.Kaleidoscope.Value::centreX,
            Filter.Kaleidoscope.Value::centreY,
            Filter.Kaleidoscope.Value::sides,
            Filter.Kaleidoscope.Value::radius
        )
    )

    Filter.Kodak::class -> StoredFilterParameters("Float", listOf(Filter.Kodak.Value::strength))
    Filter.Kuwahara::class -> StoredFilterParameters("Float", listOf(Filter.Kuwahara.Value::radius))
    Filter.L0Smoothing::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.L0Smoothing.Value::lambda,
            Filter.L0Smoothing.Value::penaltyGrowth,
            Filter.L0Smoothing.Value::iterations,
            Filter.L0Smoothing.Value::strength
        )
    )

    Filter.LayeredPaperCut::class -> StoredFilterParameters(
        "LayeredPaperCutParams",
        listOf(
            Filter.LayeredPaperCut.Value::layers,
            Filter.LayeredPaperCut.Value::smoothness,
            Filter.LayeredPaperCut.Value::depth,
            Filter.LayeredPaperCut.Value::lightAngle,
            Filter.LayeredPaperCut.Value::grain,
            Filter.LayeredPaperCut.Value::strength
        )
    )

    Filter.LcdSubpixels::class -> StoredFilterParameters(
        "LcdSubpixelsParams",
        listOf(
            Filter.LcdSubpixels.Value::size,
            Filter.LcdSubpixels.Value::blackMatrix,
            Filter.LcdSubpixels.Value::subpixelOrder,
            Filter.LcdSubpixels.Value::viewAngle,
            Filter.LcdSubpixels.Value::strength
        )
    )

    Filter.LeftToRightDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.LeftToRightDithering.Value::threshold,
            Filter.LeftToRightDithering.Value::grayscale
        )
    )

    Filter.LensBlur::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.LensBlur.Value::radius,
            Filter.LensBlur.Value::sides,
            Filter.LensBlur.Value::bloom,
            Filter.LensBlur.Value::threshold
        )
    )

    Filter.LensDistortion::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.LensDistortion.Value::settings)
    )

    Filter.Levels::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.Levels.Value::lowInput,
            Filter.Levels.Value::highInput,
            Filter.Levels.Value::lowOutput,
            Filter.Levels.Value::highOutput
        )
    )

    Filter.LightEffects::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.LightEffects.Value::bumpHeight,
            Filter.LightEffects.Value::bumpSoftness
        )
    )

    Filter.LightGlow::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.LightGlow.Value::settings)
    )

    Filter.LightPatch::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.LightPatch.Value::settings)
    )

    Filter.LinearBoxBlur::class -> StoredFilterParameters(
        "Pair{Int\$TransferFunc}",
        listOf(
            Filter.LinearBoxBlur.Value::radius,
            Filter.LinearBoxBlur.Value::transferFunction
        )
    )

    Filter.LinearFastGaussianBlur::class -> StoredFilterParameters(
        "Triple{Int\$TransferFunc\$BlurEdgeMode}",
        listOf(
            Filter.LinearFastGaussianBlur.Value::radius,
            Filter.LinearFastGaussianBlur.Value::transferFunction,
            Filter.LinearFastGaussianBlur.Value::edgeMode
        )
    )

    Filter.LinearFastGaussianBlurNext::class -> StoredFilterParameters(
        "Triple{Int\$TransferFunc\$BlurEdgeMode}",
        listOf(
            Filter.LinearFastGaussianBlurNext.Value::radius,
            Filter.LinearFastGaussianBlurNext.Value::transferFunction,
            Filter.LinearFastGaussianBlurNext.Value::edgeMode
        )
    )

    Filter.LinearGaussianBlur::class -> StoredFilterParameters(
        "LinearGaussianParams",
        listOf(
            Filter.LinearGaussianBlur.Value::kernelSize,
            Filter.LinearGaussianBlur.Value::sigma,
            Filter.LinearGaussianBlur.Value::edgeMode,
            Filter.LinearGaussianBlur.Value::transferFunction
        )
    )

    Filter.LinearGaussianBoxBlur::class -> StoredFilterParameters(
        "Pair{Float\$TransferFunc}",
        listOf(
            Filter.LinearGaussianBoxBlur.Value::sigma,
            Filter.LinearGaussianBoxBlur.Value::transferFunction
        )
    )

    Filter.LinearStackBlur::class -> StoredFilterParameters(
        "Pair{Int\$TransferFunc}",
        listOf(
            Filter.LinearStackBlur.Value::radius,
            Filter.LinearStackBlur.Value::transferFunction
        )
    )

    Filter.LinearTentBlur::class -> StoredFilterParameters(
        "Pair{Float\$TransferFunc}",
        listOf(
            Filter.LinearTentBlur.Value::sigma,
            Filter.LinearTentBlur.Value::transferFunction
        )
    )

    Filter.LinearTiltShift::class -> StoredFilterParameters(
        "LinearTiltShiftParams",
        listOf(
            Filter.LinearTiltShift.Value::blurRadius,
            Filter.LinearTiltShift.Value::sigma,
            Filter.LinearTiltShift.Value::anchorX,
            Filter.LinearTiltShift.Value::anchorY,
            Filter.LinearTiltShift.Value::holeRadius,
            Filter.LinearTiltShift.Value::angle
        )
    )

    Filter.Linify::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Linify.Value::settings)
    )

    Filter.LocalContrastEnhancement::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.LocalContrastEnhancement.Value::settings)
    )

    Filter.LocalOrientation::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.LocalOrientation.Value::settings)
    )

    Filter.LocalVarianceNormalization::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.LocalVarianceNormalization.Value::settings)
    )

    Filter.LogarithmicSpiral::class -> StoredFilterParameters(
        "LogarithmicSpiralParams",
        listOf(
            Filter.LogarithmicSpiral.Value::count,
            Filter.LogarithmicSpiral.Value::intensity,
            Filter.LogarithmicSpiral.Value::offsetX,
            Filter.LogarithmicSpiral.Value::offsetY,
            Filter.LogarithmicSpiral.Value::scale,
            Filter.LogarithmicSpiral.Value::rotation
        )
    )

    Filter.LogarithmicToneMapping::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.LogarithmicToneMapping.Value::exposure)
    )

    Filter.Lomo::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Lomo.Value::settings)
    )

    Filter.Lookup::class -> StoredFilterParameters("Float", listOf(Filter.Lookup.Value::amount))
    Filter.LowPoly::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.LowPoly.Value::strength,
            Filter.LowPoly.Value::fill
        )
    )

    Filter.LylejkPainting::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.LylejkPainting.Value::settings)
    )

    Filter.MagicDetails::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.MagicDetails.Value::settings)
    )

    Filter.MakeSeamless::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.MakeSeamless.Value::settings)
    )

    Filter.Marble::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.Marble.Value::strength,
            Filter.Marble.Value::turbulence,
            Filter.Marble.Value::amplitude
        )
    )

    Filter.MarkerDrawing::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.MarkerDrawing.Value::settings)
    )

    Filter.MedianBlur::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.MedianBlur.Value::radius)
    )

    Filter.MessWithBits::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.MessWithBits.Value::settings)
    )

    Filter.MetallicLook::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.MetallicLook.Value::settings)
    )

    Filter.MicroMacroPixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.MicroMacroPixelation.Value::layers)
    )

    Filter.MightyDetails::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.MightyDetails.Value::settings)
    )

    Filter.MineralMosaic::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.MineralMosaic.Value::settings)
    )

    Filter.Ministeck::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Ministeck.Value::settings)
    )

    Filter.Mirror::class -> StoredFilterParameters(
        "Pair{Float\$MirrorSide}",
        listOf(
            Filter.Mirror.Value::center,
            Filter.Mirror.Value::side
        )
    )

    Filter.MirrorFrame::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.MirrorFrame.Value::settings)
    )

    Filter.MirroredArray::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.MirroredArray.Value::settings)
    )

    Filter.MissEtikate::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.MissEtikate.Value::strength)
    )

    Filter.Mobius::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.Mobius.Value::exposure,
            Filter.Mobius.Value::transition,
            Filter.Mobius.Value::peak
        )
    )

    Filter.Monochrome::class -> StoredFilterParameters(
        "Pair{Float\$ColorModel}",
        listOf(
            Filter.Monochrome.Value::intensity,
            Filter.Monochrome.Value::color
        )
    )

    Filter.MorphologicalGradient::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.MorphologicalGradient.Value::justSize,
            Filter.MorphologicalGradient.Value::useCircleKernel
        )
    )

    Filter.MotionBlur::class -> StoredFilterParameters(
        "Triple{Int\$Float\$BlurEdgeMode}",
        listOf(
            Filter.MotionBlur.Value::kernelSize,
            Filter.MotionBlur.Value::angle,
            Filter.MotionBlur.Value::edgeMode
        )
    )

    Filter.MultiscaleSharpen::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.MultiscaleSharpen.Value::settings)
    )

    Filter.NativeStackBlur::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.NativeStackBlur.Value::radius)
    )

    Filter.Neon::class -> StoredFilterParameters(
        "Triple{Float\$Float\$ColorModel}",
        listOf(
            Filter.Neon.Value::amount,
            Filter.Neon.Value::strength,
            Filter.Neon.Value::color
        )
    )

    Filter.Noise::class -> StoredFilterParameters("Float", listOf(Filter.Noise.Value::threshold))
    Filter.NonLocalMeans::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.NonLocalMeans.Value::luminanceNoise,
            Filter.NonLocalMeans.Value::ntscChromaNoise,
            Filter.NonLocalMeans.Value::searchWindow,
            Filter.NonLocalMeans.Value::strength
        )
    )

    Filter.NucleusPixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.NucleusPixelation.Value::layers)
    )

    Filter.Offset::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.Offset.Value::offsetX,
            Filter.Offset.Value::offsetY
        )
    )

    Filter.OffsetStripes::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.OffsetStripes.Value::settings)
    )

    Filter.Oil::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.Oil.Value::radius,
            Filter.Oil.Value::strength
        )
    )

    Filter.OldMovieStripes::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.OldMovieStripes.Value::settings)
    )

    Filter.OldPhotograph::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.OldPhotograph.Value::settings)
    )

    Filter.OldSchool8Bit::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.OldSchool8Bit.Value::settings)
    )

    Filter.Opacity::class -> StoredFilterParameters("Float", listOf(Filter.Opacity.Value::opacity))
    Filter.Opening::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.Opening.Value::justSize,
            Filter.Opening.Value::useCircleKernel
        )
    )

    Filter.OrbitalPixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.OrbitalPixelation.Value::layers)
    )

    Filter.OstromoukhovDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.OstromoukhovDithering.Value::threshold,
            Filter.OstromoukhovDithering.Value::grayscale
        )
    )

    Filter.Painting::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Painting.Value::settings)
    )

    Filter.PaintingFrame::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.PaintingFrame.Value::settings)
    )

    Filter.PalComposite::class -> StoredFilterParameters(
        "PalCompositeParams",
        listOf(
            Filter.PalComposite.Value::lumaFilter,
            Filter.PalComposite.Value::chromaRadius,
            Filter.PalComposite.Value::noise,
            Filter.PalComposite.Value::phaseError,
            Filter.PalComposite.Value::scanSamples,
            Filter.PalComposite.Value::lineCompensation,
            Filter.PalComposite.Value::seed,
            Filter.PalComposite.Value::strength
        )
    )

    Filter.PaletteRemap::class -> StoredFilterParameters(
        "PaletteRemapParams",
        listOf(
            Filter.PaletteRemap.Value::dithering,
            Filter.PaletteRemap.Value::shadowColor,
            Filter.PaletteRemap.Value::midtoneColor,
            Filter.PaletteRemap.Value::highlightColor
        )
    )

    Filter.PenDrawing::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.PenDrawing.Value::settings)
    )

    Filter.PencilPortrait::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.PencilPortrait.Value::settings)
    )

    Filter.PerlinDistortion::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.PerlinDistortion.Value::strength,
            Filter.PerlinDistortion.Value::turbulence,
            Filter.PerlinDistortion.Value::amplitude
        )
    )

    Filter.PhotoCrossStitch::class -> StoredFilterParameters(
        "PhotoCrossStitchParams",
        listOf(
            Filter.PhotoCrossStitch.Value::size,
            Filter.PhotoCrossStitch.Value::threadColors,
            Filter.PhotoCrossStitch.Value::threadWidth,
            Filter.PhotoCrossStitch.Value::lightAngle,
            Filter.PhotoCrossStitch.Value::relief,
            Filter.PhotoCrossStitch.Value::seed,
            Filter.PhotoCrossStitch.Value::strength
        )
    )

    Filter.Pinch::class -> StoredFilterParameters(
        "PinchParams",
        listOf(
            Filter.Pinch.Value::angle,
            Filter.Pinch.Value::centreX,
            Filter.Pinch.Value::centreY,
            Filter.Pinch.Value::radius,
            Filter.Pinch.Value::amount
        )
    )

    Filter.PixelMelt::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.PixelMelt.Value::maxDrop,
            Filter.PixelMelt.Value::strength
        )
    )

    Filter.PixelSort::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.PixelSort.Value::settings)
    )

    Filter.Pixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Pixelation.Value::size)
    )

    Filter.PoincareDisk::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.PoincareDisk.Value::settings)
    )

    Filter.Pointillize::class -> StoredFilterParameters(
        "VoronoiCrystallizeParams",
        listOf(
            Filter.Pointillize.Value::borderThickness,
            Filter.Pointillize.Value::scale,
            Filter.Pointillize.Value::randomness,
            Filter.Pointillize.Value::shape,
            Filter.Pointillize.Value::turbulence,
            Filter.Pointillize.Value::angle,
            Filter.Pointillize.Value::stretch,
            Filter.Pointillize.Value::amount,
            Filter.Pointillize.Value::color
        )
    )

    Filter.PoissonBlur::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.PoissonBlur.Value::kernelSize)
    )

    Filter.PolarCoordinates::class -> StoredFilterParameters(
        "PolarCoordinatesType",
        listOf(Filter.PolarCoordinates.Value::type)
    )

    Filter.PolarTransform::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.PolarTransform.Value::settings)
    )

    Filter.PolaroidFrame::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.PolaroidFrame.Value::settings)
    )

    Filter.PolkaDot::class -> StoredFilterParameters(
        "Triple{Int\$Int\$ColorModel}",
        listOf(
            Filter.PolkaDot.Value::radius,
            Filter.PolkaDot.Value::spacing,
            Filter.PolkaDot.Value::backgroundColor
        )
    )

    Filter.PopArt::class -> StoredFilterParameters(
        "Triple{Float\$ColorModel\$PopArtBlendingMode}",
        listOf(
            Filter.PopArt.Value::strength,
            Filter.PopArt.Value::color,
            Filter.PopArt.Value::overlayMode
        )
    )

    Filter.PopShadows::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.PopShadows.Value::settings)
    )

    Filter.PosterEdges::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.PosterEdges.Value::settings)
    )

    Filter.Posterize::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Posterize.Value::amount)
    )

    Filter.PosterizedDithering::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.PosterizedDithering.Value::settings)
    )

    Filter.PowerTwirl::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.PowerTwirl.Value::settings)
    )

    Filter.ProgressiveScaling::class -> StoredFilterParameters(
        "ProgressiveScalingParams",
        listOf(
            Filter.ProgressiveScaling.Value::intensity,
            Filter.ProgressiveScaling.Value::ratio,
            Filter.ProgressiveScaling.Value::radius,
            Filter.ProgressiveScaling.Value::offsetX,
            Filter.ProgressiveScaling.Value::offsetY,
            Filter.ProgressiveScaling.Value::scale,
            Filter.ProgressiveScaling.Value::rotation
        )
    )

    Filter.PulseGridPixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.PulseGridPixelation.Value::layers)
    )

    Filter.Puzzle::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Puzzle.Value::settings)
    )

    Filter.Quadtree::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Quadtree.Value::settings)
    )

    Filter.Quantizier::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Quantizier.Value::maxColors)
    )

    Filter.Quicksilver::class -> StoredFilterParameters(
        "QuicksilverParams",
        listOf(
            Filter.Quicksilver.Value::displacement,
            Filter.Quicksilver.Value::intensity,
            Filter.Quicksilver.Value::frequency,
            Filter.Quicksilver.Value::offsetX,
            Filter.Quicksilver.Value::offsetY,
            Filter.Quicksilver.Value::scale,
            Filter.Quicksilver.Value::rotation
        )
    )

    Filter.RGB::class -> StoredFilterParameters(
        "FilterValueWrapper{ColorModel}",
        listOf(Filter.RGB.Value::color)
    )

    Filter.RadialInterpolate::class -> StoredFilterParameters(
        "RadialInterpolateParams",
        listOf(
            Filter.RadialInterpolate.Value::intensity,
            Filter.RadialInterpolate.Value::count,
            Filter.RadialInterpolate.Value::radius,
            Filter.RadialInterpolate.Value::width,
            Filter.RadialInterpolate.Value::offsetX,
            Filter.RadialInterpolate.Value::offsetY,
            Filter.RadialInterpolate.Value::scale,
            Filter.RadialInterpolate.Value::rotation
        )
    )

    Filter.RadialShimmer::class -> StoredFilterParameters(
        "RadialShimmerParams",
        listOf(
            Filter.RadialShimmer.Value::count,
            Filter.RadialShimmer.Value::intensity,
            Filter.RadialShimmer.Value::spacing,
            Filter.RadialShimmer.Value::offsetX,
            Filter.RadialShimmer.Value::offsetY,
            Filter.RadialShimmer.Value::scale,
            Filter.RadialShimmer.Value::rotation
        )
    )

    Filter.RadialStreak::class -> StoredFilterParameters(
        "RadialStreakParams",
        listOf(
            Filter.RadialStreak.Value::intensity,
            Filter.RadialStreak.Value::count,
            Filter.RadialStreak.Value::radius,
            Filter.RadialStreak.Value::offsetX,
            Filter.RadialStreak.Value::offsetY,
            Filter.RadialStreak.Value::scale,
            Filter.RadialStreak.Value::rotation
        )
    )

    Filter.RadialTiltShift::class -> StoredFilterParameters(
        "RadialTiltShiftParams",
        listOf(
            Filter.RadialTiltShift.Value::blurRadius,
            Filter.RadialTiltShift.Value::sigma,
            Filter.RadialTiltShift.Value::anchorX,
            Filter.RadialTiltShift.Value::anchorY,
            Filter.RadialTiltShift.Value::holeRadius
        )
    )

    Filter.RadialWeavePixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.RadialWeavePixelation.Value::layers)
    )

    Filter.RainAndSnow::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.RainAndSnow.Value::settings)
    )

    Filter.RandomColorArray::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.RandomColorArray.Value::settings)
    )

    Filter.RandomColorDispersion::class -> StoredFilterParameters(
        "RandomColorDispersionParams",
        listOf(
            Filter.RandomColorDispersion.Value::intensity,
            Filter.RandomColorDispersion.Value::seed,
            Filter.RandomColorDispersion.Value::offsetX,
            Filter.RandomColorDispersion.Value::offsetY,
            Filter.RandomColorDispersion.Value::scale,
            Filter.RandomColorDispersion.Value::rotation
        )
    )

    Filter.RandomDeformations::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.RandomDeformations.Value::settings)
    )

    Filter.RandomDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.RandomDithering.Value::threshold,
            Filter.RandomDithering.Value::grayscale
        )
    )

    Filter.RandomKaleidoscopeGrid::class -> StoredFilterParameters(
        "RandomKaleidoscopeGridParams",
        listOf(
            Filter.RandomKaleidoscopeGrid.Value::count,
            Filter.RandomKaleidoscopeGrid.Value::frequency,
            Filter.RandomKaleidoscopeGrid.Value::seed,
            Filter.RandomKaleidoscopeGrid.Value::variability,
            Filter.RandomKaleidoscopeGrid.Value::blend,
            Filter.RandomKaleidoscopeGrid.Value::offsetX,
            Filter.RandomKaleidoscopeGrid.Value::offsetY,
            Filter.RandomKaleidoscopeGrid.Value::scale,
            Filter.RandomKaleidoscopeGrid.Value::rotation
        )
    )

    Filter.RandomPatchArray::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.RandomPatchArray.Value::settings)
    )

    Filter.RandomShadeStripes::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.RandomShadeStripes.Value::settings)
    )

    Filter.RawGmic::class -> StoredFilterParameters("String", listOf(Filter.RawGmic.Value::command))
    Filter.Rays::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.Rays.Value::opacity,
            Filter.Rays.Value::threshold,
            Filter.Rays.Value::strength
        )
    )

    Filter.RebuildFromSimilarBlocks::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.RebuildFromSimilarBlocks.Value::settings)
    )

    Filter.ReliefLight::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ReliefLight.Value::settings)
    )

    Filter.RemoveColor::class -> StoredFilterParameters(
        "Pair{Float\$ColorModel}",
        listOf(
            Filter.RemoveColor.Value::tolerance,
            Filter.RemoveColor.Value::colorToRemove
        )
    )

    Filter.RemoveHotPixels::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.RemoveHotPixels.Value::settings)
    )

    Filter.RemoveScratches::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.RemoveScratches.Value::settings)
    )

    Filter.ReplaceColor::class -> StoredFilterParameters(
        "Triple{Float\$ColorModel\$ColorModel}",
        listOf(
            Filter.ReplaceColor.Value::tolerance,
            Filter.ReplaceColor.Value::colorToReplace,
            Filter.ReplaceColor.Value::targetColor
        )
    )

    Filter.Retinex::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Retinex.Value::settings)
    )

    Filter.RetroFade::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.RetroFade.Value::settings)
    )

    Filter.RetroHardware::class -> StoredFilterParameters(
        "RetroHardwareParams",
        listOf(
            Filter.RetroHardware.Value::system,
            Filter.RetroHardware.Value::scale,
            Filter.RetroHardware.Value::dithering,
            Filter.RetroHardware.Value::strength
        )
    )

    Filter.RetroYellow::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.RetroYellow.Value::strength)
    )

    Filter.RfReception::class -> StoredFilterParameters(
        "RfReceptionParams",
        listOf(
            Filter.RfReception.Value::delay,
            Filter.RfReception.Value::reflection,
            Filter.RfReception.Value::noise,
            Filter.RfReception.Value::frequency,
            Filter.RfReception.Value::seed,
            Filter.RfReception.Value::strength
        )
    )

    Filter.RgbChannelRegistration::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.RgbChannelRegistration.Value::maxShift,
            Filter.RgbChannelRegistration.Value::minConfidence,
            Filter.RgbChannelRegistration.Value::strength
        )
    )

    Filter.RichardsonLucySharpen::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.RichardsonLucySharpen.Value::settings)
    )

    Filter.RiemersmaDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.RiemersmaDithering.Value::threshold,
            Filter.RiemersmaDithering.Value::grayscale
        )
    )

    Filter.RingBlur::class -> StoredFilterParameters("Float", listOf(Filter.RingBlur.Value::size))
    Filter.Ripple::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.Ripple.Value::xAmplitude,
            Filter.Ripple.Value::xWavelength,
            Filter.Ripple.Value::yAmplitude,
            Filter.Ripple.Value::yWavelength
        )
    )

    Filter.Risograph::class -> StoredFilterParameters(
        "RisographParams",
        listOf(
            Filter.Risograph.Value::spacing,
            Filter.Risograph.Value::misregistration,
            Filter.Risograph.Value::dotGain,
            Filter.Risograph.Value::inks,
            Filter.Risograph.Value::paperGrain,
            Filter.Risograph.Value::seed,
            Filter.Risograph.Value::strength
        )
    )

    Filter.Rodilius::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Rodilius.Value::settings)
    )

    Filter.RollingGuidance::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.RollingGuidance.Value::radius,
            Filter.RollingGuidance.Value::sensitivity,
            Filter.RollingGuidance.Value::iterations,
            Filter.RollingGuidance.Value::strength
        )
    )

    Filter.RoundedFrame::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.RoundedFrame.Value::settings)
    )

    Filter.RowShifter::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.RowShifter.Value::settings)
    )

    Filter.RubberStamp::class -> StoredFilterParameters(
        "RubberStampParams",
        listOf(
            Filter.RubberStamp.Value::threshold,
            Filter.RubberStamp.Value::softness,
            Filter.RubberStamp.Value::radius,
            Filter.RubberStamp.Value::firstColor,
            Filter.RubberStamp.Value::secondColor
        )
    )

    Filter.Rubidium::class -> StoredFilterParameters(
        "RubidiumParams",
        listOf(
            Filter.Rubidium.Value::displacement,
            Filter.Rubidium.Value::intensity,
            Filter.Rubidium.Value::balance,
            Filter.Rubidium.Value::offsetX,
            Filter.Rubidium.Value::offsetY,
            Filter.Rubidium.Value::scale,
            Filter.Rubidium.Value::rotation
        )
    )

    Filter.SandPainting::class -> StoredFilterParameters(
        "Triple{Int\$Int\$ColorModel}",
        listOf(
            Filter.SandPainting.Value::strength,
            Filter.SandPainting.Value::threshold,
            Filter.SandPainting.Value::backgroundColor
        )
    )

    Filter.SaturationEqualizer::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.SaturationEqualizer.Value::settings)
    )

    Filter.Saturation::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.Saturation.Value::strength,
            Filter.Saturation.Value::enableTonemapping
        )
    )

    Filter.Secam::class -> StoredFilterParameters(
        "SecamParams",
        listOf(
            Filter.Secam.Value::chromaRadius,
            Filter.Secam.Value::frequencyDeviation,
            Filter.Secam.Value::noise,
            Filter.Secam.Value::preEmphasis,
            Filter.Secam.Value::lineCrosstalk,
            Filter.Secam.Value::seed,
            Filter.Secam.Value::strength
        )
    )

    Filter.SelectiveDesaturation::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.SelectiveDesaturation.Value::settings)
    )

    Filter.SerpentineAtkinsonDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.SerpentineAtkinsonDithering.Value::threshold,
            Filter.SerpentineAtkinsonDithering.Value::grayscale
        )
    )

    Filter.SerpentineBurkesDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.SerpentineBurkesDithering.Value::threshold,
            Filter.SerpentineBurkesDithering.Value::grayscale
        )
    )

    Filter.SerpentineFloydSteinbergDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.SerpentineFloydSteinbergDithering.Value::threshold,
            Filter.SerpentineFloydSteinbergDithering.Value::grayscale
        )
    )

    Filter.ShadowPatch::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ShadowPatch.Value::settings)
    )

    Filter.Shapeism::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Shapeism.Value::settings)
    )

    Filter.SharpAbstract::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.SharpAbstract.Value::settings)
    )

    Filter.Sharpen::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Sharpen.Value::sharpness)
    )

    Filter.Shear::class -> StoredFilterParameters(
        "ShearParams",
        listOf(
            Filter.Shear.Value::xAngle,
            Filter.Shear.Value::yAngle,
            Filter.Shear.Value::resize
        )
    )

    Filter.ShiauFanDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.ShiauFanDithering.Value::threshold,
            Filter.ShiauFanDithering.Value::grayscale
        )
    )

    Filter.ShockWaves::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ShockWaves.Value::settings)
    )

    Filter.ShuffleBlur::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.ShuffleBlur.Value::radius,
            Filter.ShuffleBlur.Value::threshold
        )
    )

    Filter.ShufflePatches::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ShufflePatches.Value::settings)
    )

    Filter.SideFade::class -> StoredFilterParameters(
        "SideFadeParams",
        listOf(
            Filter.SideFade.Value::side,
            Filter.SideFade.Value::scale
        )
    )

    Filter.SierraDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.SierraDithering.Value::threshold,
            Filter.SierraDithering.Value::grayscale
        )
    )

    Filter.SierraLiteDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.SierraLiteDithering.Value::threshold,
            Filter.SierraLiteDithering.Value::grayscale
        )
    )

    Filter.SignedDistanceField::class -> StoredFilterParameters(
        "SignedDistanceFieldParams",
        listOf(
            Filter.SignedDistanceField.Value::alphaMask,
            Filter.SignedDistanceField.Value::threshold,
            Filter.SignedDistanceField.Value::distance,
            Filter.SignedDistanceField.Value::mode,
            Filter.SignedDistanceField.Value::spacing,
            Filter.SignedDistanceField.Value::insideColor,
            Filter.SignedDistanceField.Value::outsideColor,
            Filter.SignedDistanceField.Value::invert
        )
    )

    Filter.SimpleThresholdDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.SimpleThresholdDithering.Value::threshold,
            Filter.SimpleThresholdDithering.Value::grayscale
        )
    )

    Filter.SimpleWeavePixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.SimpleWeavePixelation.Value::layers)
    )

    Filter.SingleLineDrawing::class -> StoredFilterParameters(
        "SingleLineDrawingParams",
        listOf(
            Filter.SingleLineDrawing.Value::pointCount,
            Filter.SingleLineDrawing.Value::densityCurve,
            Filter.SingleLineDrawing.Value::lineWidth,
            Filter.SingleLineDrawing.Value::ink,
            Filter.SingleLineDrawing.Value::paper,
            Filter.SingleLineDrawing.Value::optimization,
            Filter.SingleLineDrawing.Value::seed,
            Filter.SingleLineDrawing.Value::strength
        )
    )

    Filter.SixtiesCinema::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.SixtiesCinema.Value::settings)
    )

    Filter.Skeleton::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Skeleton.Value::settings)
    )

    Filter.Sketch::class -> StoredFilterParameters("Float", listOf(Filter.Sketch.Value::kernelSize))
    Filter.Smear::class -> StoredFilterParameters(
        "SmearParams",
        listOf(
            Filter.Smear.Value::angle,
            Filter.Smear.Value::density,
            Filter.Smear.Value::mix,
            Filter.Smear.Value::distance,
            Filter.Smear.Value::shape
        )
    )

    Filter.SmoothAbstract::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.SmoothAbstract.Value::settings)
    )

    Filter.SmoothFrame::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.SmoothFrame.Value::settings)
    )

    Filter.SmoothKaleidoscope::class -> StoredFilterParameters(
        "SmoothKaleidoscopeParams",
        listOf(
            Filter.SmoothKaleidoscope.Value::blend,
            Filter.SmoothKaleidoscope.Value::offset,
            Filter.SmoothKaleidoscope.Value::frequency,
            Filter.SmoothKaleidoscope.Value::offsetX,
            Filter.SmoothKaleidoscope.Value::offsetY,
            Filter.SmoothKaleidoscope.Value::scale,
            Filter.SmoothKaleidoscope.Value::rotation
        )
    )

    Filter.SmoothSkin::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.SmoothSkin.Value::settings)
    )

    Filter.SmoothToon::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.SmoothToon.Value::blurSize,
            Filter.SmoothToon.Value::threshold,
            Filter.SmoothToon.Value::quantizationLevels
        )
    )

    Filter.SobelEdgeDetection::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.SobelEdgeDetection.Value::lineWidth)
    )

    Filter.SoftElegance::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.SoftElegance.Value::strength)
    )

    Filter.SoftEleganceVariant::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.SoftEleganceVariant.Value::strength)
    )

    Filter.Solarize::class -> StoredFilterParameters("Float", listOf(Filter.Solarize.Value::amount))
    Filter.Sparkle::class -> StoredFilterParameters(
        "SparkleParams",
        listOf(
            Filter.Sparkle.Value::amount,
            Filter.Sparkle.Value::rays,
            Filter.Sparkle.Value::radius,
            Filter.Sparkle.Value::randomness,
            Filter.Sparkle.Value::centreX,
            Filter.Sparkle.Value::centreY,
            Filter.Sparkle.Value::color
        )
    )

    Filter.SpecificSaturation::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.SpecificSaturation.Value::settings)
    )

    Filter.SpectralNotch::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.SpectralNotch.Value::frequencyX,
            Filter.SpectralNotch.Value::frequencyY,
            Filter.SpectralNotch.Value::bandwidth,
            Filter.SpectralNotch.Value::strength
        )
    )

    Filter.SphereLensDistortion::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.SphereLensDistortion.Value::refractionIndex,
            Filter.SphereLensDistortion.Value::radius,
            Filter.SphereLensDistortion.Value::centerX,
            Filter.SphereLensDistortion.Value::centerY
        )
    )

    Filter.SphereRefraction::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.SphereRefraction.Value::radius,
            Filter.SphereRefraction.Value::refractiveIndex
        )
    )

    Filter.SpiralArms::class -> StoredFilterParameters(
        "SpiralArmsParams",
        listOf(
            Filter.SpiralArms.Value::count,
            Filter.SpiralArms.Value::intensity,
            Filter.SpiralArms.Value::offsetX,
            Filter.SpiralArms.Value::offsetY,
            Filter.SpiralArms.Value::scale,
            Filter.SpiralArms.Value::rotation
        )
    )

    Filter.SpiralBreaks::class -> StoredFilterParameters(
        "SpiralBreaksParams",
        listOf(
            Filter.SpiralBreaks.Value::intensity,
            Filter.SpiralBreaks.Value::distortion,
            Filter.SpiralBreaks.Value::count,
            Filter.SpiralBreaks.Value::offsetX,
            Filter.SpiralBreaks.Value::offsetY,
            Filter.SpiralBreaks.Value::scale,
            Filter.SpiralBreaks.Value::rotation
        )
    )

    Filter.SpiralDroste::class -> StoredFilterParameters(
        "SpiralDrosteParams",
        listOf(
            Filter.SpiralDroste.Value::intensity,
            Filter.SpiralDroste.Value::distortion,
            Filter.SpiralDroste.Value::offsetX,
            Filter.SpiralDroste.Value::offsetY,
            Filter.SpiralDroste.Value::scale,
            Filter.SpiralDroste.Value::rotation
        )
    )

    Filter.SpiralHalftoneDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.SpiralHalftoneDithering.Value::threshold,
            Filter.SpiralHalftoneDithering.Value::grayscale
        )
    )

    Filter.SquareFresnel::class -> StoredFilterParameters(
        "SquareFresnelParams",
        listOf(
            Filter.SquareFresnel.Value::intensity,
            Filter.SquareFresnel.Value::offsetX,
            Filter.SquareFresnel.Value::offsetY,
            Filter.SquareFresnel.Value::scale,
            Filter.SquareFresnel.Value::rotation
        )
    )

    Filter.SquareSpiralDroste::class -> StoredFilterParameters(
        "SquareSpiralDrosteParams",
        listOf(
            Filter.SquareSpiralDroste.Value::intensity,
            Filter.SquareSpiralDroste.Value::distortion,
            Filter.SquareSpiralDroste.Value::shapeAspect,
            Filter.SquareSpiralDroste.Value::offsetX,
            Filter.SquareSpiralDroste.Value::offsetY,
            Filter.SquareSpiralDroste.Value::scale,
            Filter.SquareSpiralDroste.Value::rotation
        )
    )

    Filter.SquareToCircle::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.SquareToCircle.Value::settings)
    )

    Filter.SstvReception::class -> StoredFilterParameters(
        "SstvReceptionParams",
        listOf(
            Filter.SstvReception.Value::transmissionMode,
            Filter.SstvReception.Value::frequencyDrift,
            Filter.SstvReception.Value::syncError,
            Filter.SstvReception.Value::noise,
            Filter.SstvReception.Value::dropout,
            Filter.SstvReception.Value::seed,
            Filter.SstvReception.Value::strength
        )
    )

    Filter.StackBlur::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.StackBlur.Value::scale,
            Filter.StackBlur.Value::radius
        )
    )

    Filter.StaggeredPixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.StaggeredPixelation.Value::layers)
    )

    Filter.StainedGlass::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.StainedGlass.Value::settings)
    )

    Filter.StarBlur::class -> StoredFilterParameters("Float", listOf(Filter.StarBlur.Value::size))
    Filter.StereographicProjection::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.StereographicProjection.Value::settings)
    )

    Filter.StreakCircles::class -> StoredFilterParameters(
        "StreakCirclesParams",
        listOf(
            Filter.StreakCircles.Value::intensity,
            Filter.StreakCircles.Value::count,
            Filter.StreakCircles.Value::radius,
            Filter.StreakCircles.Value::layers,
            Filter.StreakCircles.Value::seed,
            Filter.StreakCircles.Value::offsetX,
            Filter.StreakCircles.Value::offsetY,
            Filter.StreakCircles.Value::scale,
            Filter.StreakCircles.Value::rotation
        )
    )

    Filter.StreakExpand::class -> StoredFilterParameters(
        "StreakExpandParams",
        listOf(
            Filter.StreakExpand.Value::intensity,
            Filter.StreakExpand.Value::size,
            Filter.StreakExpand.Value::offsetX,
            Filter.StreakExpand.Value::offsetY,
            Filter.StreakExpand.Value::scale,
            Filter.StreakExpand.Value::rotation
        )
    )

    Filter.Streak::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Streak.Value::settings)
    )

    Filter.Stringify::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Stringify.Value::settings)
    )

    Filter.StrokePixelation::class -> StoredFilterParameters(
        "Pair{Float\$ColorModel}",
        listOf(
            Filter.StrokePixelation.Value::pixelSize,
            Filter.StrokePixelation.Value::backgroundColor
        )
    )

    Filter.StuckiDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.StuckiDithering.Value::threshold,
            Filter.StuckiDithering.Value::grayscale
        )
    )

    Filter.SuperPixels::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.SuperPixels.Value::settings)
    )

    Filter.SwirlDistortion::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.SwirlDistortion.Value::radius,
            Filter.SwirlDistortion.Value::angle,
            Filter.SwirlDistortion.Value::centerX,
            Filter.SwirlDistortion.Value::centerY
        )
    )

    Filter.Taquin::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Taquin.Value::settings)
    )

    Filter.TargetColorSpot::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.TargetColorSpot.Value::settings)
    )

    Filter.TemperatureBalance::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.TemperatureBalance.Value::settings)
    )

    Filter.TentBlur::class -> StoredFilterParameters("Float", listOf(Filter.TentBlur.Value::sigma))
    Filter.TextureSharpen::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.TextureSharpen.Value::settings)
    )

    Filter.ThermalReceipt::class -> StoredFilterParameters(
        "ThermalReceiptParams",
        listOf(
            Filter.ThermalReceipt.Value::size,
            Filter.ThermalReceipt.Value::heat,
            Filter.ThermalReceipt.Value::heatRetention,
            Filter.ThermalReceipt.Value::defects,
            Filter.ThermalReceipt.Value::fading,
            Filter.ThermalReceipt.Value::seed,
            Filter.ThermalReceipt.Value::strength
        )
    )

    Filter.Threshold::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Threshold.Value::threshold)
    )

    Filter.TileableRotation::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.TileableRotation.Value::settings)
    )

    Filter.TiledStreak::class -> StoredFilterParameters(
        "TiledStreakParams",
        listOf(
            Filter.TiledStreak.Value::intensity,
            Filter.TiledStreak.Value::balance,
            Filter.TiledStreak.Value::variability,
            Filter.TiledStreak.Value::frequency,
            Filter.TiledStreak.Value::seed,
            Filter.TiledStreak.Value::offsetX,
            Filter.TiledStreak.Value::offsetY,
            Filter.TiledStreak.Value::scale,
            Filter.TiledStreak.Value::rotation
        )
    )

    Filter.ToneCurves::class -> StoredFilterParameters(
        "ToneCurvesParams",
        listOf(Filter.ToneCurves.Value::controlPoints)
    )

    Filter.ToneEnhance::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ToneEnhance.Value::settings)
    )

    Filter.ToneSharpen::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ToneSharpen.Value::settings)
    )

    Filter.Toon::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.Toon.Value::threshold,
            Filter.Toon.Value::quantizationLevels
        )
    )

    Filter.TopHat::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.TopHat.Value::justSize,
            Filter.TopHat.Value::useCircleKernel
        )
    )

    Filter.TornEdge::class -> StoredFilterParameters(
        "TornEdgeParams",
        listOf(
            Filter.TornEdge.Value::toothHeight,
            Filter.TornEdge.Value::horizontalToothRange,
            Filter.TornEdge.Value::verticalToothRange,
            Filter.TornEdge.Value::top,
            Filter.TornEdge.Value::right,
            Filter.TornEdge.Value::bottom,
            Filter.TornEdge.Value::left
        )
    )

    Filter.TriTone::class -> StoredFilterParameters(
        "Triple{ColorModel\$ColorModel\$ColorModel}",
        listOf(
            Filter.TriTone.Value::shadowColor,
            Filter.TriTone.Value::midtoneColor,
            Filter.TriTone.Value::highlightColor
        )
    )

    Filter.Tunnel::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Tunnel.Value::settings)
    )

    Filter.Twirl::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.Twirl.Value::angle,
            Filter.Twirl.Value::centerX,
            Filter.Twirl.Value::centerY,
            Filter.Twirl.Value::radius
        )
    )

    Filter.TwoRowSierraDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.TwoRowSierraDithering.Value::threshold,
            Filter.TwoRowSierraDithering.Value::grayscale
        )
    )

    Filter.Uchimura::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Uchimura.Value::exposure)
    )

    Filter.Unpurple::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Unpurple.Value::settings)
    )

    Filter.Unsharp::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Unsharp.Value::intensity)
    )

    Filter.Unstrip::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Unstrip.Value::settings)
    )

    Filter.VHS::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.VHS.Value::seed,
            Filter.VHS.Value::strength
        )
    )

    Filter.VHSNtsc::class -> StoredFilterParameters(
        "NtscParams",
        listOf(
            Filter.VHSNtsc.Value::amount,
            Filter.VHSNtsc.Value::chromaBleed,
            Filter.VHSNtsc.Value::tapeWear,
            Filter.VHSNtsc.Value::noise,
            Filter.VHSNtsc.Value::tracking,
            Filter.VHSNtsc.Value::seed,
            Filter.VHSNtsc.Value::lumaSmear,
            Filter.VHSNtsc.Value::compositeSharpening,
            Filter.VHSNtsc.Value::ringing,
            Filter.VHSNtsc.Value::snow,
            Filter.VHSNtsc.Value::useField,
            Filter.VHSNtsc.Value::filterType,
            Filter.VHSNtsc.Value::inputLumaFilter,
            Filter.VHSNtsc.Value::chromaLowpassIn,
            Filter.VHSNtsc.Value::chromaDemodulation,
            Filter.VHSNtsc.Value::videoScanlinePhaseShift,
            Filter.VHSNtsc.Value::videoScanlinePhaseShiftOffset,
            Filter.VHSNtsc.Value::headSwitchingHeight,
            Filter.VHSNtsc.Value::headSwitchingOffset,
            Filter.VHSNtsc.Value::headSwitchingHorizontalShift,
            Filter.VHSNtsc.Value::headSwitchingMidLinePosition,
            Filter.VHSNtsc.Value::headSwitchingMidLineJitter,
            Filter.VHSNtsc.Value::trackingNoiseHeight,
            Filter.VHSNtsc.Value::trackingNoiseWaveIntensity,
            Filter.VHSNtsc.Value::trackingNoiseSnowIntensity,
            Filter.VHSNtsc.Value::trackingNoiseSnowAnisotropy,
            Filter.VHSNtsc.Value::trackingNoiseNoiseIntensity,
            Filter.VHSNtsc.Value::compositeNoiseFrequency,
            Filter.VHSNtsc.Value::compositeNoiseIntensity,
            Filter.VHSNtsc.Value::compositeNoiseDetail,
            Filter.VHSNtsc.Value::ringingFrequency,
            Filter.VHSNtsc.Value::ringingPower,
            Filter.VHSNtsc.Value::lumaNoiseFrequency,
            Filter.VHSNtsc.Value::lumaNoiseIntensity,
            Filter.VHSNtsc.Value::lumaNoiseDetail,
            Filter.VHSNtsc.Value::chromaNoiseFrequency,
            Filter.VHSNtsc.Value::chromaNoiseIntensity,
            Filter.VHSNtsc.Value::chromaNoiseDetail,
            Filter.VHSNtsc.Value::snowIntensity,
            Filter.VHSNtsc.Value::snowAnisotropy,
            Filter.VHSNtsc.Value::chromaPhaseNoiseIntensity,
            Filter.VHSNtsc.Value::chromaPhaseError,
            Filter.VHSNtsc.Value::chromaDelayHorizontal,
            Filter.VHSNtsc.Value::chromaDelayVertical,
            Filter.VHSNtsc.Value::vhsTapeSpeed,
            Filter.VHSNtsc.Value::vhsChromaLoss,
            Filter.VHSNtsc.Value::vhsSharpenIntensity,
            Filter.VHSNtsc.Value::vhsSharpenFrequency,
            Filter.VHSNtsc.Value::vhsEdgeWaveIntensity,
            Filter.VHSNtsc.Value::vhsEdgeWaveSpeed,
            Filter.VHSNtsc.Value::vhsEdgeWaveFrequency,
            Filter.VHSNtsc.Value::vhsEdgeWaveDetail,
            Filter.VHSNtsc.Value::chromaLowpassOut,
            Filter.VHSNtsc.Value::scaleHorizontal,
            Filter.VHSNtsc.Value::scaleVertical,
            Filter.VHSNtsc.Value::scaleFactorX,
            Filter.VHSNtsc.Value::scaleFactorY,
            Filter.VHSNtsc.Value::headSwitchingEnabled,
            Filter.VHSNtsc.Value::trackingNoiseEnabled,
            Filter.VHSNtsc.Value::compositeNoiseEnabled,
            Filter.VHSNtsc.Value::ringingEnabled,
            Filter.VHSNtsc.Value::lumaNoiseEnabled,
            Filter.VHSNtsc.Value::chromaNoiseEnabled,
            Filter.VHSNtsc.Value::vhsEnabled,
            Filter.VHSNtsc.Value::scaleEnabled
        )
    )

    Filter.VectorPainting::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.VectorPainting.Value::settings)
    )

    Filter.VerticalLinesDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.VerticalLinesDithering.Value::threshold,
            Filter.VerticalLinesDithering.Value::grayscale
        )
    )

    Filter.Vibrance::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.Vibrance.Value::vibrance)
    )

    Filter.Vignette::class -> StoredFilterParameters(
        "Triple{Float\$Float\$ColorModel}",
        listOf(
            Filter.Vignette.Value::start,
            Filter.Vignette.Value::end,
            Filter.Vignette.Value::color
        )
    )

    Filter.VoronoiCrystallize::class -> StoredFilterParameters(
        "VoronoiCrystallizeParams",
        listOf(
            Filter.VoronoiCrystallize.Value::borderThickness,
            Filter.VoronoiCrystallize.Value::scale,
            Filter.VoronoiCrystallize.Value::randomness,
            Filter.VoronoiCrystallize.Value::shape,
            Filter.VoronoiCrystallize.Value::turbulence,
            Filter.VoronoiCrystallize.Value::angle,
            Filter.VoronoiCrystallize.Value::stretch,
            Filter.VoronoiCrystallize.Value::amount,
            Filter.VoronoiCrystallize.Value::color
        )
    )

    Filter.VortexPixelation::class -> StoredFilterParameters(
        "Float",
        listOf(Filter.VortexPixelation.Value::layers)
    )

    Filter.Warhol::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.Warhol.Value::settings)
    )

    Filter.WarpByIntensity::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.WarpByIntensity.Value::settings)
    )

    Filter.WaterDrop::class -> StoredFilterParameters(
        "WaterDropParams",
        listOf(
            Filter.WaterDrop.Value::wavelength,
            Filter.WaterDrop.Value::amplitude,
            Filter.WaterDrop.Value::phase,
            Filter.WaterDrop.Value::centreX,
            Filter.WaterDrop.Value::centreY,
            Filter.WaterDrop.Value::radius
        )
    )

    Filter.WaterEffect::class -> StoredFilterParameters(
        "WaterParams",
        listOf(
            Filter.WaterEffect.Value::fractionSize,
            Filter.WaterEffect.Value::frequencyX,
            Filter.WaterEffect.Value::frequencyY,
            Filter.WaterEffect.Value::amplitudeX,
            Filter.WaterEffect.Value::amplitudeY
        )
    )

    Filter.WaterReflection::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.WaterReflection.Value::settings)
    )

    Filter.WaveLinesDithering::class -> StoredFilterParameters(
        "Pair{Float\$Boolean}",
        listOf(
            Filter.WaveLinesDithering.Value::threshold,
            Filter.WaveLinesDithering.Value::grayscale
        )
    )

    Filter.WaveletDenoise::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.WaveletDenoise.Value::luminanceNoise,
            Filter.WaveletDenoise.Value::ntscChromaNoise,
            Filter.WaveletDenoise.Value::levels,
            Filter.WaveletDenoise.Value::strength
        )
    )

    Filter.Weave::class -> StoredFilterParameters(
        "Quad{Float\$Float\$Float\$Float}",
        listOf(
            Filter.Weave.Value::xWidth,
            Filter.Weave.Value::yWdth,
            Filter.Weave.Value::xGap,
            Filter.Weave.Value::yGap
        )
    )

    Filter.WhiteBalance::class -> StoredFilterParameters(
        "Pair{Float\$Float}",
        listOf(
            Filter.WhiteBalance.Value::temperature,
            Filter.WhiteBalance.Value::tint
        )
    )

    Filter.WhitenSharpen::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.WhitenSharpen.Value::settings)
    )

    Filter.WienerDeconvolution::class -> StoredFilterParameters(
        "WienerDeconvolutionParams",
        listOf(
            Filter.WienerDeconvolution.Value::psf,
            Filter.WienerDeconvolution.Value::radius,
            Filter.WienerDeconvolution.Value::angle,
            Filter.WienerDeconvolution.Value::regularization,
            Filter.WienerDeconvolution.Value::strength
        )
    )

    Filter.Xdog::class -> StoredFilterParameters(
        "XdogParams",
        listOf(
            Filter.Xdog.Value::sigma,
            Filter.Xdog.Value::ratio,
            Filter.Xdog.Value::weight,
            Filter.Xdog.Value::threshold,
            Filter.Xdog.Value::sharpness,
            Filter.Xdog.Value::ink,
            Filter.Xdog.Value::paper,
            Filter.Xdog.Value::strength
        )
    )

    Filter.YililomaDithering::class -> StoredFilterParameters(
        "null",
        listOf(Filter.YililomaDithering.Value::grayscale)
    )

    Filter.ZoneSystem::class -> StoredFilterParameters(
        "GmicFilterParams",
        listOf(Filter.ZoneSystem.Value::settings)
    )

    Filter.ZoomBlur::class -> StoredFilterParameters(
        "Triple{Float\$Float\$Float}",
        listOf(
            Filter.ZoomBlur.Value::blurCenterX,
            Filter.ZoomBlur.Value::blurCenterY,
            Filter.ZoomBlur.Value::blurSize
        )
    )

    else -> null
}
