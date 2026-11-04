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

package com.t8rin.imagetoolbox.core.filters.domain.model

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.ErrorOwner
import com.t8rin.imagetoolbox.core.domain.model.FileModel
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.OffsetModel
import com.t8rin.imagetoolbox.core.domain.model.VisibilityOwner
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.DisplacementBoundary
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.DisplacementInterpolation
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.FadeSide
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.MirrorSide
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PaletteTransferSpace
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PolarCoordinatesType
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PopArtBlendingMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.TransferFunc
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValue
import com.t8rin.imagetoolbox.core.ksp.annotations.GenerateFilterValues
import com.t8rin.imagetoolbox.core.settings.domain.model.FontType


@GenerateFilterValues
interface Filter<Value : Any> : VisibilityOwner, ErrorOwner {
    val value: Value

    interface ParameterValue

    interface EmptyValue : ParameterValue

    interface Schema<T : Any> {
        val info: FilterInfo?
        val parameters: FilterParameterSchema<T>?

        interface Structured<T : ParameterValue> : Schema<T> {
            override val info: FilterInfo
            override val parameters: StructuredFilterParameters<T>
        }

        interface Custom<T : ParameterValue> : Structured<T>

        interface Empty<T : EmptyValue> : Schema<T> {
            override val info: FilterInfo
            override val parameters: Nothing? get() = null
        }

        interface HiddenValue<T : ParameterValue> : Schema<T> {
            override val info: Nothing? get() = null
            override val parameters: StructuredFilterParameters<T>
        }
    }

    interface BilaterialBlur : Filter<BilaterialBlur.Value> {
        interface Value : ParameterValue {
            val radius: Int
            val spatialSigma: Float
            val rangeSigma: Float
            val edgeMode: BlurEdgeMode
        }
    }

    interface BlackAndWhite : Filter<BlackAndWhite.Value> {
        interface Value : EmptyValue
    }

    interface BoxBlur : Filter<BoxBlur.Value> {
        interface Value : ParameterValue {
            val kernelSize: Float
        }
    }

    interface Brightness : Filter<Brightness.Value> {
        interface Value : ParameterValue {
            val bias: Float
        }
    }

    interface BulgeDistortion : Filter<BulgeDistortion.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val scale: Float
        }
    }

    interface CGAColorSpace : Filter<CGAColorSpace.Value> {
        interface Value : EmptyValue
    }

    interface ColorBalance : Filter<ColorBalance.Value> {
        interface Value : ParameterValue {
            val adjustments: FloatArray
        }
    }

    interface ColorOverlay : Filter<ColorOverlay.Value> {
        interface Value : ParameterValue {
            val color: ColorModel
        }
    }

    interface ColorMatrix4x4 : Filter<ColorMatrix4x4.Value> {
        interface Value : ParameterValue {
            val matrix: FloatArray
        }
    }

    interface Contrast : Filter<Contrast.Value> {
        interface Value : ParameterValue {
            val gain: Float
        }
    }

    interface Convolution3x3 : Filter<Convolution3x3.Value> {
        interface Value : ParameterValue {
            val kernel: FloatArray
        }
    }

    interface Crosshatch : Filter<Crosshatch.Value> {
        interface Value : ParameterValue {
            val spacing: Float
            val lineWidth: Float
        }
    }

    interface Dilation : Filter<Dilation.Value> {
        interface Value : ParameterValue {
            val justSize: Float
            val useCircleKernel: Boolean
        }
    }

    interface Emboss : Filter<Emboss.Value> {
        interface Value : ParameterValue {
            val intensity: Float
        }
    }

    interface Exposure : Filter<Exposure.Value> {
        interface Value : ParameterValue {
            val amount: Float
        }
    }

    interface FalseColor : Filter<FalseColor.Value> {
        interface Value : ParameterValue {
            val darkColor: ColorModel
            val lightColor: ColorModel
        }
    }

    interface FastBlur : Filter<FastBlur.Value> {
        interface Value : ParameterValue {
            val scale: Float
            val radius: Float
        }
    }

    interface Gamma : Filter<Gamma.Value> {
        interface Value : ParameterValue {
            val gamma: Float
        }
    }

    interface GaussianBlur : Filter<GaussianBlur.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val sigma: Float
            val edgeMode: BlurEdgeMode
        }
    }

    interface GlassSphereRefraction : Filter<GlassSphereRefraction.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val refractiveIndex: Float
        }
    }

    interface Halftone : Filter<Halftone.Value> {
        interface Value : ParameterValue {
            val amount: Float
        }
    }

    interface Haze : Filter<Haze.Value> {
        interface Value : ParameterValue {
            val distance: Float
            val slope: Float
        }
    }

    interface HighlightsAndShadows : Filter<HighlightsAndShadows.Value> {
        interface Value : ParameterValue {
            val amount: Float
        }
    }

    interface Hue : Filter<Hue.Value> {
        interface Value : ParameterValue {
            val hue: Float
        }
    }

    interface Kuwahara : Filter<Kuwahara.Value> {
        interface Value : ParameterValue {
            val radius: Float
        }
    }

    interface Laplacian : Filter<Laplacian.Value> {
        interface Value : EmptyValue
    }

    interface Lookup : Filter<Lookup.Value> {
        interface Value : ParameterValue {
            val amount: Float
        }
    }

    interface Monochrome : Filter<Monochrome.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val color: ColorModel
        }
    }

    interface Negative : Filter<Negative.Value> {
        interface Value : EmptyValue
    }

    interface NonMaximumSuppression : Filter<NonMaximumSuppression.Value> {
        interface Value : EmptyValue
    }

    interface Opacity : Filter<Opacity.Value> {
        interface Value : ParameterValue {
            val opacity: Float
        }
    }

    interface Posterize : Filter<Posterize.Value> {
        interface Value : ParameterValue {
            val amount: Float
        }
    }

    interface RGB : Filter<RGB.Value> {
        interface Value : ParameterValue {
            val color: ColorModel
        }
    }

    interface Saturation : Filter<Saturation.Value> {
        interface Value : ParameterValue {
            val strength: Float
            val enableTonemapping: Boolean
        }
    }

    interface Sepia : Filter<Sepia.Value> {
        interface Value : EmptyValue
    }

    interface Sharpen : Filter<Sharpen.Value> {
        interface Value : ParameterValue {
            val sharpness: Float
        }
    }

    interface Sketch : Filter<Sketch.Value> {
        interface Value : ParameterValue {
            val kernelSize: Float
        }
    }

    interface SmoothToon : Filter<SmoothToon.Value> {
        interface Value : ParameterValue {
            val blurSize: Float
            val threshold: Float
            val quantizationLevels: Float
        }
    }

    interface SobelEdgeDetection : Filter<SobelEdgeDetection.Value> {
        interface Value : ParameterValue {
            val lineWidth: Float
        }
    }

    interface Solarize : Filter<Solarize.Value> {
        interface Value : ParameterValue {
            val amount: Float
        }
    }

    interface SphereRefraction : Filter<SphereRefraction.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val refractiveIndex: Float
        }
    }

    interface StackBlur : Filter<StackBlur.Value> {
        interface Value : ParameterValue {
            val scale: Float
            val radius: Float
        }
    }

    interface SwirlDistortion : Filter<SwirlDistortion.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val angle: Float
            val centerX: Float
            val centerY: Float
        }
    }

    interface Toon : Filter<Toon.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val quantizationLevels: Float
        }
    }

    interface Vibrance : Filter<Vibrance.Value> {
        interface Value : ParameterValue {
            val vibrance: Float
        }
    }

    interface Vignette : Filter<Vignette.Value> {
        interface Value : ParameterValue {
            val start: Float
            val end: Float
            val color: ColorModel
        }
    }

    interface WeakPixel : Filter<WeakPixel.Value> {
        interface Value : EmptyValue
    }

    interface WhiteBalance : Filter<WhiteBalance.Value> {
        interface Value : ParameterValue {
            val temperature: Float
            val tint: Float
        }
    }

    interface ZoomBlur : Filter<ZoomBlur.Value> {
        interface Value : ParameterValue {
            val blurCenterX: Float
            val blurCenterY: Float
            val blurSize: Float
        }
    }

    interface Pixelation : Filter<Pixelation.Value> {
        interface Value : ParameterValue {
            val size: Float
        }
    }

    interface EnhancedPixelation : Filter<EnhancedPixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface StrokePixelation : Filter<StrokePixelation.Value> {
        interface Value : ParameterValue {
            val pixelSize: Float
            val backgroundColor: ColorModel
        }
    }

    interface CirclePixelation : Filter<CirclePixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface DiamondPixelation : Filter<DiamondPixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface EnhancedCirclePixelation : Filter<EnhancedCirclePixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface EnhancedDiamondPixelation : Filter<EnhancedDiamondPixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface ReplaceColor : Filter<ReplaceColor.Value> {
        interface Value : ParameterValue {
            val tolerance: Float
            val colorToReplace: ColorModel
            val targetColor: ColorModel
        }
    }

    interface RemoveColor : Filter<RemoveColor.Value> {
        interface Value : ParameterValue {
            val tolerance: Float
            val colorToRemove: ColorModel
        }
    }

    interface SideFade : Filter<SideFade.Value> {
        interface Value : ParameterValue {
            val side: FadeSide
            val scale: Float
            val strength: Float
        }
    }

    interface BayerTwoDithering : Filter<BayerTwoDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface BayerThreeDithering : Filter<BayerThreeDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface BayerFourDithering : Filter<BayerFourDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface BayerEightDithering : Filter<BayerEightDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface RandomDithering : Filter<RandomDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface FloydSteinbergDithering : Filter<FloydSteinbergDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface JarvisJudiceNinkeDithering : Filter<JarvisJudiceNinkeDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface SierraDithering : Filter<SierraDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface TwoRowSierraDithering : Filter<TwoRowSierraDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface SierraLiteDithering : Filter<SierraLiteDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface AtkinsonDithering : Filter<AtkinsonDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface StuckiDithering : Filter<StuckiDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface BurkesDithering : Filter<BurkesDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface FalseFloydSteinbergDithering : Filter<FalseFloydSteinbergDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface LeftToRightDithering : Filter<LeftToRightDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface SimpleThresholdDithering : Filter<SimpleThresholdDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface RiemersmaDithering : Filter<RiemersmaDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface OstromoukhovDithering : Filter<OstromoukhovDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface ShiauFanDithering : Filter<ShiauFanDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface SerpentineFloydSteinbergDithering : Filter<SerpentineFloydSteinbergDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface SerpentineAtkinsonDithering : Filter<SerpentineAtkinsonDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface SerpentineBurkesDithering : Filter<SerpentineBurkesDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface BayerSixteenDithering : Filter<BayerSixteenDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface BlueNoiseDithering : Filter<BlueNoiseDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface InterleavedGradientNoiseDithering : Filter<InterleavedGradientNoiseDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface SpiralHalftoneDithering : Filter<SpiralHalftoneDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface HexagonalDotDithering : Filter<HexagonalDotDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface WaveLinesDithering : Filter<WaveLinesDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface ClusteredDotDithering : Filter<ClusteredDotDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface DiamondDithering : Filter<DiamondDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface HorizontalLinesDithering : Filter<HorizontalLinesDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface VerticalLinesDithering : Filter<VerticalLinesDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface DiagonalLinesDithering : Filter<DiagonalLinesDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface CrosshatchMatrixDithering : Filter<CrosshatchMatrixDithering.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val grayscale: Boolean
        }
    }

    interface MedianBlur : Filter<MedianBlur.Value> {
        interface Value : ParameterValue {
            val radius: Float
        }
    }

    interface NativeStackBlur : Filter<NativeStackBlur.Value> {
        interface Value : ParameterValue {
            val radius: Float
        }
    }

    interface RadialTiltShift : Filter<RadialTiltShift.Value> {
        interface Value : ParameterValue {
            val blurRadius: Float
            val sigma: Float
            val anchorX: Float
            val anchorY: Float
            val holeRadius: Float
        }
    }

    interface Glitch : Filter<Glitch.Value> {
        interface Value : ParameterValue {
            val amount: Float
            val seed: Float
            val repeatCount: Float
        }
    }

    interface Noise : Filter<Noise.Value> {
        interface Value : ParameterValue {
            val threshold: Float
        }
    }

    interface Anaglyph : Filter<Anaglyph.Value> {
        interface Value : ParameterValue {
            val amount: Float
        }
    }

    interface EnhancedGlitch : Filter<EnhancedGlitch.Value> {
        interface Value : ParameterValue {
            val channelsShiftX: Float
            val channelsShiftY: Float
            val corruptionSize: Float
            val corruptionCount: Int
            val corruptionShiftX: Float
            val corruptionShiftY: Float
        }
    }

    interface TentBlur : Filter<TentBlur.Value> {
        interface Value : ParameterValue {
            val sigma: Float
        }
    }

    interface Erode : Filter<Erode.Value> {
        interface Value : ParameterValue {
            val justSize: Float
            val useCircleKernel: Boolean
        }
    }

    interface AnisotropicDiffusion : Filter<AnisotropicDiffusion.Value> {
        interface Value : ParameterValue {
            val repeatCount: Float
            val conduction: Float
            val diffusion: Float
        }
    }

    interface HorizontalWindStagger : Filter<HorizontalWindStagger.Value> {
        interface Value : ParameterValue {
            val strength: Float
            val amount: Int
            val color: ColorModel
        }
    }

    interface FastBilaterialBlur : Filter<FastBilaterialBlur.Value> {
        interface Value : ParameterValue {
            val justSize: Float
            val sigma: Float
            val spatialSigma: Float
        }
    }

    interface PoissonBlur : Filter<PoissonBlur.Value> {
        interface Value : ParameterValue {
            val kernelSize: Float
        }
    }

    interface LogarithmicToneMapping : Filter<LogarithmicToneMapping.Value> {
        interface Value : ParameterValue {
            val exposure: Float
        }
    }

    interface AcesFilmicToneMapping : Filter<AcesFilmicToneMapping.Value> {
        interface Value : ParameterValue {
            val exposure: Float
        }
    }

    interface Crystallize : Filter<Crystallize.Value> {
        interface Value : ParameterValue {
            val amount: Float
            val strokeColor: ColorModel
        }
    }

    interface FractalGlass : Filter<FractalGlass.Value> {
        interface Value : ParameterValue {
            val strength: Float
            val amplitude: Float
        }
    }

    interface Marble : Filter<Marble.Value> {
        interface Value : ParameterValue {
            val strength: Float
            val turbulence: Float
            val amplitude: Float
        }
    }

    interface Oil : Filter<Oil.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val strength: Float
        }
    }

    interface WaterEffect : Filter<WaterEffect.Value> {
        interface Value : ParameterValue {
            val fractionSize: Float
            val frequencyX: Float
            val frequencyY: Float
            val amplitudeX: Float
            val amplitudeY: Float
        }
    }

    interface HableFilmicToneMapping : Filter<HableFilmicToneMapping.Value> {
        interface Value : ParameterValue {
            val exposure: Float
        }
    }

    interface AcesHillToneMapping : Filter<AcesHillToneMapping.Value> {
        interface Value : ParameterValue {
            val exposure: Float
        }
    }

    interface HejlBurgessToneMapping : Filter<HejlBurgessToneMapping.Value> {
        interface Value : ParameterValue {
            val exposure: Float
        }
    }

    interface PerlinDistortion : Filter<PerlinDistortion.Value> {
        interface Value : ParameterValue {
            val strength: Float
            val turbulence: Float
            val amplitude: Float
        }
    }

    interface Grayscale : Filter<Grayscale.Value> {
        interface Value : ParameterValue {
            val colorRed: Float
            val colorGreen: Float
            val colorBlue: Float
        }
    }

    interface Dehaze : Filter<Dehaze.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val omega: Float
        }
    }

    interface Threshold : Filter<Threshold.Value> {
        interface Value : ParameterValue {
            val threshold: Float
        }
    }

    interface ColorMatrix3x3 : Filter<ColorMatrix3x3.Value> {
        interface Value : ParameterValue {
            val matrix: FloatArray
        }
    }

    interface Polaroid : Filter<Polaroid.Value> {
        interface Value : EmptyValue
    }

    interface Cool : Filter<Cool.Value> {
        interface Value : EmptyValue
    }

    interface Warm : Filter<Warm.Value> {
        interface Value : EmptyValue
    }

    interface NightVision : Filter<NightVision.Value> {
        interface Value : EmptyValue
    }

    interface CodaChrome : Filter<CodaChrome.Value> {
        interface Value : EmptyValue
    }

    interface Browni : Filter<Browni.Value> {
        interface Value : EmptyValue
    }

    interface Vintage : Filter<Vintage.Value> {
        interface Value : EmptyValue
    }

    interface Protonomaly : Filter<Protonomaly.Value> {
        interface Value : EmptyValue
    }

    interface Deutaromaly : Filter<Deutaromaly.Value> {
        interface Value : EmptyValue
    }

    interface Tritonomaly : Filter<Tritonomaly.Value> {
        interface Value : EmptyValue
    }

    interface Protanopia : Filter<Protanopia.Value> {
        interface Value : EmptyValue
    }

    interface Deuteranopia : Filter<Deuteranopia.Value> {
        interface Value : EmptyValue
    }

    interface Tritanopia : Filter<Tritanopia.Value> {
        interface Value : EmptyValue
    }

    interface Achromatopsia : Filter<Achromatopsia.Value> {
        interface Value : EmptyValue
    }

    interface Achromatomaly : Filter<Achromatomaly.Value> {
        interface Value : EmptyValue
    }

    interface Grain : Filter<Grain.Value> {
        interface Value : ParameterValue {
            val intensity: Float
        }
    }

    interface Unsharp : Filter<Unsharp.Value> {
        interface Value : ParameterValue {
            val intensity: Float
        }
    }

    interface Pastel : Filter<Pastel.Value> {
        interface Value : EmptyValue
    }

    interface OrangeHaze : Filter<OrangeHaze.Value> {
        interface Value : EmptyValue
    }

    interface PinkDream : Filter<PinkDream.Value> {
        interface Value : EmptyValue
    }

    interface GoldenHour : Filter<GoldenHour.Value> {
        interface Value : EmptyValue
    }

    interface HotSummer : Filter<HotSummer.Value> {
        interface Value : EmptyValue
    }

    interface PurpleMist : Filter<PurpleMist.Value> {
        interface Value : EmptyValue
    }

    interface Sunrise : Filter<Sunrise.Value> {
        interface Value : EmptyValue
    }

    interface ColorfulSwirl : Filter<ColorfulSwirl.Value> {
        interface Value : EmptyValue
    }

    interface SoftSpringLight : Filter<SoftSpringLight.Value> {
        interface Value : EmptyValue
    }

    interface AutumnTones : Filter<AutumnTones.Value> {
        interface Value : EmptyValue
    }

    interface LavenderDream : Filter<LavenderDream.Value> {
        interface Value : EmptyValue
    }

    interface Cyberpunk : Filter<Cyberpunk.Value> {
        interface Value : EmptyValue
    }

    interface LemonadeLight : Filter<LemonadeLight.Value> {
        interface Value : EmptyValue
    }

    interface SpectralFire : Filter<SpectralFire.Value> {
        interface Value : EmptyValue
    }

    interface NightMagic : Filter<NightMagic.Value> {
        interface Value : EmptyValue
    }

    interface FantasyLandscape : Filter<FantasyLandscape.Value> {
        interface Value : EmptyValue
    }

    interface ColorExplosion : Filter<ColorExplosion.Value> {
        interface Value : EmptyValue
    }

    interface ElectricGradient : Filter<ElectricGradient.Value> {
        interface Value : EmptyValue
    }

    interface CaramelDarkness : Filter<CaramelDarkness.Value> {
        interface Value : EmptyValue
    }

    interface FuturisticGradient : Filter<FuturisticGradient.Value> {
        interface Value : EmptyValue
    }

    interface GreenSun : Filter<GreenSun.Value> {
        interface Value : EmptyValue
    }

    interface RainbowWorld : Filter<RainbowWorld.Value> {
        interface Value : EmptyValue
    }

    interface DeepPurple : Filter<DeepPurple.Value> {
        interface Value : EmptyValue
    }

    interface SpacePortal : Filter<SpacePortal.Value> {
        interface Value : EmptyValue
    }

    interface RedSwirl : Filter<RedSwirl.Value> {
        interface Value : EmptyValue
    }

    interface DigitalCode : Filter<DigitalCode.Value> {
        interface Value : EmptyValue
    }

    interface Bokeh : Filter<Bokeh.Value> {
        interface Value : ParameterValue {
            val size: Float
            val amount: Float
        }
    }

    interface Neon : Filter<Neon.Value> {
        interface Value : ParameterValue {
            val amount: Float
            val strength: Float
            val color: ColorModel
        }
    }

    interface OldTv : Filter<OldTv.Value> {
        interface Value : EmptyValue
    }

    interface ShuffleBlur : Filter<ShuffleBlur.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val threshold: Float
        }
    }

    interface Mobius : Filter<Mobius.Value> {
        interface Value : ParameterValue {
            val exposure: Float
            val transition: Float
            val peak: Float
        }
    }

    interface Uchimura : Filter<Uchimura.Value> {
        interface Value : ParameterValue {
            val exposure: Float
        }
    }

    interface Aldridge : Filter<Aldridge.Value> {
        interface Value : ParameterValue {
            val exposure: Float
            val cutoff: Float
        }
    }

    interface Drago : Filter<Drago.Value> {
        interface Value : ParameterValue {
            val exposure: Float
            val threshold: Float
        }
    }

    interface ColorAnomaly : Filter<ColorAnomaly.Value> {
        interface Value : ParameterValue {
            val amount: Float
        }
    }

    interface Quantizier : Filter<Quantizier.Value> {
        interface Value : ParameterValue {
            val maxColors: Float
        }
    }

    interface RingBlur : Filter<RingBlur.Value> {
        interface Value : ParameterValue {
            val size: Float
        }
    }

    interface CrossBlur : Filter<CrossBlur.Value> {
        interface Value : ParameterValue {
            val size: Float
        }
    }

    interface CircleBlur : Filter<CircleBlur.Value> {
        interface Value : ParameterValue {
            val size: Float
        }
    }

    interface StarBlur : Filter<StarBlur.Value> {
        interface Value : ParameterValue {
            val size: Float
        }
    }

    interface LinearTiltShift : Filter<LinearTiltShift.Value> {
        interface Value : ParameterValue {
            val blurRadius: Float
            val sigma: Float
            val anchorX: Float
            val anchorY: Float
            val holeRadius: Float
            val angle: Float
        }
    }

    interface EnhancedZoomBlur : Filter<EnhancedZoomBlur.Value> {
        interface Value : ParameterValue {
            val radius: Int
            val sigma: Float
            val centerX: Float
            val centerY: Float
            val strength: Float
            val angle: Float
        }
    }

    interface Convex : Filter<Convex.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface FastGaussianBlur2D : Filter<FastGaussianBlur2D.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val edgeMode: BlurEdgeMode
        }
    }

    interface FastGaussianBlur3D : Filter<FastGaussianBlur3D.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val edgeMode: BlurEdgeMode
        }
    }

    interface FastGaussianBlur4D : Filter<FastGaussianBlur4D.Value> {
        interface Value : ParameterValue {
            val radius: Float
        }
    }

    interface EqualizeHistogram : Filter<EqualizeHistogram.Value> {
        interface Value : EmptyValue
    }

    interface EqualizeHistogramHSV : Filter<EqualizeHistogramHSV.Value> {
        interface Value : ParameterValue {
            val binsCount: Float
        }
    }

    interface EqualizeHistogramPixelation : Filter<EqualizeHistogramPixelation.Value> {
        interface Value : ParameterValue {
            val gridSizeX: Float
            val gridSizeY: Float
        }
    }

    interface EqualizeHistogramAdaptive : Filter<EqualizeHistogramAdaptive.Value> {
        interface Value : ParameterValue {
            val gridSizeX: Float
            val gridSizeY: Float
        }
    }

    interface EqualizeHistogramAdaptiveLUV : Filter<EqualizeHistogramAdaptiveLUV.Value> {
        interface Value : ParameterValue {
            val gridSizeX: Float
            val gridSizeY: Float
            val binsCount: Float
        }
    }

    interface EqualizeHistogramAdaptiveLAB : Filter<EqualizeHistogramAdaptiveLAB.Value> {
        interface Value : ParameterValue {
            val gridSizeX: Float
            val gridSizeY: Float
            val binsCount: Float
        }
    }

    interface Clahe : Filter<Clahe.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val gridSizeX: Float
            val gridSizeY: Float
        }
    }

    interface ClaheLUV : Filter<ClaheLUV.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val gridSizeHorizontal: Int
            val gridSizeVertical: Int
            val binsCount: Int
        }
    }

    interface ClaheLAB : Filter<ClaheLAB.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val gridSizeHorizontal: Int
            val gridSizeVertical: Int
            val binsCount: Int
        }
    }

    interface CropToContent : Filter<CropToContent.Value> {
        interface Value : ParameterValue {
            val tolerance: Float
            val colorToIgnore: ColorModel
        }
    }

    interface ClaheHSL : Filter<ClaheHSL.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val gridSizeHorizontal: Int
            val gridSizeVertical: Int
            val binsCount: Int
        }
    }

    interface ClaheHSV : Filter<ClaheHSV.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val gridSizeHorizontal: Int
            val gridSizeVertical: Int
            val binsCount: Int
        }
    }

    interface EqualizeHistogramAdaptiveHSV : Filter<EqualizeHistogramAdaptiveHSV.Value> {
        interface Value : ParameterValue {
            val gridSizeX: Float
            val gridSizeY: Float
            val binsCount: Float
        }
    }

    interface EqualizeHistogramAdaptiveHSL : Filter<EqualizeHistogramAdaptiveHSL.Value> {
        interface Value : ParameterValue {
            val gridSizeX: Float
            val gridSizeY: Float
            val binsCount: Float
        }
    }

    interface LinearBoxBlur : Filter<LinearBoxBlur.Value> {
        interface Value : ParameterValue {
            val radius: Int
            val transferFunction: TransferFunc
        }
    }

    interface LinearTentBlur : Filter<LinearTentBlur.Value> {
        interface Value : ParameterValue {
            val sigma: Float
            val transferFunction: TransferFunc
        }
    }

    interface LinearGaussianBoxBlur : Filter<LinearGaussianBoxBlur.Value> {
        interface Value : ParameterValue {
            val sigma: Float
            val transferFunction: TransferFunc
        }
    }

    interface LinearStackBlur : Filter<LinearStackBlur.Value> {
        interface Value : ParameterValue {
            val radius: Int
            val transferFunction: TransferFunc
        }
    }

    interface GaussianBoxBlur : Filter<GaussianBoxBlur.Value> {
        interface Value : ParameterValue {
            val sigma: Float
        }
    }

    interface LinearFastGaussianBlurNext : Filter<LinearFastGaussianBlurNext.Value> {
        interface Value : ParameterValue {
            val radius: Int
            val transferFunction: TransferFunc
            val edgeMode: BlurEdgeMode
        }
    }

    interface LinearFastGaussianBlur : Filter<LinearFastGaussianBlur.Value> {
        interface Value : ParameterValue {
            val radius: Int
            val transferFunction: TransferFunc
            val edgeMode: BlurEdgeMode
        }
    }

    interface LinearGaussianBlur : Filter<LinearGaussianBlur.Value> {
        interface Value : ParameterValue {
            val kernelSize: Int
            val sigma: Float
            val edgeMode: BlurEdgeMode
            val transferFunction: TransferFunc
        }
    }

    interface LowPoly : Filter<LowPoly.Value> {
        interface Value : ParameterValue {
            val strength: Float
            val fill: Boolean
        }
    }

    interface SandPainting : Filter<SandPainting.Value> {
        interface Value : ParameterValue {
            val strength: Int
            val threshold: Int
            val backgroundColor: ColorModel
        }
    }

    interface PaletteTransfer : Filter<PaletteTransfer.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val image: ImageModel
        }
    }

    interface EnhancedOil : Filter<EnhancedOil.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface SimpleOldTv : Filter<SimpleOldTv.Value> {
        interface Value : EmptyValue
    }

    interface HDR : Filter<HDR.Value> {
        interface Value : EmptyValue
    }

    interface SimpleSketch : Filter<SimpleSketch.Value> {
        interface Value : EmptyValue
    }

    interface Gotham : Filter<Gotham.Value> {
        interface Value : EmptyValue
    }

    interface ColorPoster : Filter<ColorPoster.Value> {
        interface Value : ParameterValue {
            val strength: Float
            val color: ColorModel
        }
    }

    interface TriTone : Filter<TriTone.Value> {
        interface Value : ParameterValue {
            val shadowColor: ColorModel
            val midtoneColor: ColorModel
            val highlightColor: ColorModel
        }
    }

    interface ClaheOklch : Filter<ClaheOklch.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val gridSizeHorizontal: Int
            val gridSizeVertical: Int
            val binsCount: Int
        }
    }

    interface ClaheJzazbz : Filter<ClaheJzazbz.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val gridSizeHorizontal: Int
            val gridSizeVertical: Int
            val binsCount: Int
        }
    }

    interface ClaheOklab : Filter<ClaheOklab.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val gridSizeHorizontal: Int
            val gridSizeVertical: Int
            val binsCount: Int
        }
    }

    interface PolkaDot : Filter<PolkaDot.Value> {
        interface Value : ParameterValue {
            val radius: Int
            val spacing: Int
            val backgroundColor: ColorModel
        }
    }

    interface Clustered2x2Dithering : Filter<Clustered2x2Dithering.Value> {
        interface Value : ParameterValue {
            val grayscale: Boolean
        }
    }

    interface Clustered4x4Dithering : Filter<Clustered4x4Dithering.Value> {
        interface Value : ParameterValue {
            val grayscale: Boolean
        }
    }

    interface Clustered8x8Dithering : Filter<Clustered8x8Dithering.Value> {
        interface Value : ParameterValue {
            val grayscale: Boolean
        }
    }

    interface YililomaDithering : Filter<YililomaDithering.Value> {
        interface Value : ParameterValue {
            val grayscale: Boolean
        }
    }

    interface LUT512x512 : Filter<LUT512x512.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val image: ImageModel
        }
    }

    interface Amatorka : Filter<Amatorka.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface MissEtikate : Filter<MissEtikate.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface SoftElegance : Filter<SoftElegance.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface SoftEleganceVariant : Filter<SoftEleganceVariant.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface PaletteTransferVariant : Filter<PaletteTransferVariant.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val colorSpace: PaletteTransferSpace
            val image: ImageModel
        }
    }

    interface CubeLut : Filter<CubeLut.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val file: FileModel
        }
    }

    interface Shader : Filter<Shader.Value> {
        interface Value : ParameterValue {
            val preset: ShaderPreset?
            val values: Map<String, ShaderValue>
        }
    }

    interface BleachBypass : Filter<BleachBypass.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface Candlelight : Filter<Candlelight.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface DropBlues : Filter<DropBlues.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface EdgyAmber : Filter<EdgyAmber.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface FallColors : Filter<FallColors.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface FilmStock50 : Filter<FilmStock50.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface FoggyNight : Filter<FoggyNight.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface Kodak : Filter<Kodak.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface PopArt : Filter<PopArt.Value> {
        interface Value : ParameterValue {
            val strength: Float
            val color: ColorModel
            val overlayMode: PopArtBlendingMode
        }
    }

    interface Celluloid : Filter<Celluloid.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface Coffee : Filter<Coffee.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface GoldenForest : Filter<GoldenForest.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface Greenish : Filter<Greenish.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface RetroYellow : Filter<RetroYellow.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface AutoCrop : Filter<AutoCrop.Value> {
        interface Value : ParameterValue {
            val tolerance: Float
        }
    }

    interface SpotHeal : Filter<SpotHeal.Value> {
        interface Value : ParameterValue {
            val mask: ImageModel
            val mode: SpotHealMode
        }
    }

    interface Opening : Filter<Opening.Value> {
        interface Value : ParameterValue {
            val justSize: Float
            val useCircleKernel: Boolean
        }
    }

    interface Closing : Filter<Closing.Value> {
        interface Value : ParameterValue {
            val justSize: Float
            val useCircleKernel: Boolean
        }
    }

    interface MorphologicalGradient : Filter<MorphologicalGradient.Value> {
        interface Value : ParameterValue {
            val justSize: Float
            val useCircleKernel: Boolean
        }
    }

    interface TopHat : Filter<TopHat.Value> {
        interface Value : ParameterValue {
            val justSize: Float
            val useCircleKernel: Boolean
        }
    }

    interface BlackHat : Filter<BlackHat.Value> {
        interface Value : ParameterValue {
            val justSize: Float
            val useCircleKernel: Boolean
        }
    }

    interface Canny : Filter<Canny.Value> {
        interface Value : ParameterValue {
            val thresholdOne: Float
            val thresholdTwo: Float
        }
    }

    interface SobelSimple : Filter<SobelSimple.Value> {
        interface Value : EmptyValue
    }

    interface LaplacianSimple : Filter<LaplacianSimple.Value> {
        interface Value : EmptyValue
    }

    interface MotionBlur : Filter<MotionBlur.Value> {
        interface Value : ParameterValue {
            val kernelSize: Int
            val angle: Float
            val edgeMode: BlurEdgeMode
        }
    }

    interface AutoRemoveRedEyes : Filter<AutoRemoveRedEyes.Value> {
        interface Value : ParameterValue {
            val threshold: Float
        }
    }

    interface ToneCurves : Filter<ToneCurves.Value> {
        interface Value : ParameterValue {
            val controlPoints: List<List<Float>>
        }
    }

    interface Mirror : Filter<Mirror.Value> {
        interface Value : ParameterValue {
            val center: Float
            val side: MirrorSide
        }
    }

    interface Kaleidoscope : Filter<Kaleidoscope.Value> {
        interface Value : ParameterValue {
            val angle: Float
            val angle2: Float
            val centreX: Float
            val centreY: Float
            val sides: Int
            val radius: Float
        }
    }

    interface SpiralArms : Filter<SpiralArms.Value> {
        interface Value : ParameterValue {
            val count: Int
            val intensity: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface LogarithmicSpiral : Filter<LogarithmicSpiral.Value> {
        interface Value : ParameterValue {
            val count: Int
            val intensity: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface SpiralDroste : Filter<SpiralDroste.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val distortion: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface SquareSpiralDroste : Filter<SquareSpiralDroste.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val distortion: Float
            val shapeAspect: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface HexKaleidoscope : Filter<HexKaleidoscope.Value> {
        interface Value : ParameterValue {
            val count: Int
            val frequency: Float
            val offset: Float
            val mode: Int
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface SmoothKaleidoscope : Filter<SmoothKaleidoscope.Value> {
        interface Value : ParameterValue {
            val blend: Float
            val offset: Float
            val frequency: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface RandomKaleidoscopeGrid : Filter<RandomKaleidoscopeGrid.Value> {
        interface Value : ParameterValue {
            val count: Int
            val frequency: Float
            val seed: Int
            val variability: Float
            val blend: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface SquareFresnel : Filter<SquareFresnel.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface InversionFractal : Filter<InversionFractal.Value> {
        interface Value : ParameterValue {
            val mode: Int
            val iterations: Int
            val intensity: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface StreakExpand : Filter<StreakExpand.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val size: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface RadialInterpolate : Filter<RadialInterpolate.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val count: Int
            val radius: Float
            val width: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface RadialStreak : Filter<RadialStreak.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val count: Int
            val radius: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface ColumnStreak : Filter<ColumnStreak.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val variability: Float
            val count: Int
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface StreakCircles : Filter<StreakCircles.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val count: Int
            val radius: Float
            val layers: Int
            val seed: Int
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface TiledStreak : Filter<TiledStreak.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val balance: Float
            val variability: Float
            val frequency: Float
            val seed: Int
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface BrokenCells : Filter<BrokenCells.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val distortion: Float
            val variability: Float
            val frequency: Float
            val seed: Int
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface SpiralBreaks : Filter<SpiralBreaks.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val distortion: Float
            val count: Int
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface ConcentricCircleBreaks : Filter<ConcentricCircleBreaks.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val dampening: Float
            val distortion: Float
            val frequency: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface ConcentricSquareBreaks : Filter<ConcentricSquareBreaks.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val dampening: Float
            val distortion: Float
            val frequency: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface CircularMirror : Filter<CircularMirror.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val radius: Float
            val mode: Int
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface ProgressiveScaling : Filter<ProgressiveScaling.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val ratio: Float
            val radius: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface BrokenGlass : Filter<BrokenGlass.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val frequency: Float
            val seed: Int
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface InlineBreaks : Filter<InlineBreaks.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val distortion: Float
            val frequency: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface BokehLights : Filter<BokehLights.Value> {
        interface Value : ParameterValue {
            val lightColor: Color
            val intensity: Float
            val count: Int
            val blades: Int
            val radius: Float
            val variability: Float
            val colorVariation: Float
            val seed: Int
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface Fireworks : Filter<Fireworks.Value> {
        interface Value : ParameterValue {
            val seed: Int
            val explosions: Int
            val particles: Int
            val mode: Int
            val intensity: Float
            val time: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface HueShiftBands : Filter<HueShiftBands.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val saturation: Float
            val hueOffset: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface HueShiftTunnel : Filter<HueShiftTunnel.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val saturation: Float
            val hueOffset: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface HueShiftFan : Filter<HueShiftFan.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val saturation: Float
            val hueOffset: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface RadialShimmer : Filter<RadialShimmer.Value> {
        interface Value : ParameterValue {
            val count: Int
            val intensity: Float
            val spacing: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface RandomColorDispersion : Filter<RandomColorDispersion.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val seed: Int
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface HelixWaves : Filter<HelixWaves.Value> {
        interface Value : ParameterValue {
            val frequency: Float
            val intensity: Float
            val mode: Int
            val lighting: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface GridSineDistortion2 : Filter<GridSineDistortion2.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val frequency: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface Quicksilver : Filter<Quicksilver.Value> {
        interface Value : ParameterValue {
            val displacement: Float
            val intensity: Float
            val frequency: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface Gallium : Filter<Gallium.Value> {
        interface Value : ParameterValue {
            val iterations: Int
            val intensity: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface Rubidium : Filter<Rubidium.Value> {
        interface Value : ParameterValue {
            val displacement: Float
            val intensity: Float
            val balance: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface GlassRectTiles : Filter<GlassRectTiles.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val distortion: Float
            val frequency: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface GlassHexTiles : Filter<GlassHexTiles.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val distortion: Float
            val frequency: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface GlassTriangleTiles : Filter<GlassTriangleTiles.Value> {
        interface Value : ParameterValue {
            val intensity: Float
            val distortion: Float
            val frequency: Float
            val offsetX: Float
            val offsetY: Float
            val scale: Float
            val rotation: Float
        }
    }

    interface ChannelMix : Filter<ChannelMix.Value> {
        interface Value : ParameterValue {
            val blueGreen: Int
            val redBlue: Int
            val greenRed: Int
            val intoR: Int
            val intoG: Int
            val intoB: Int
        }
    }

    interface ColorHalftone : Filter<ColorHalftone.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val cyan: Float
            val magenta: Float
            val yellow: Float
        }
    }

    interface Contour : Filter<Contour.Value> {
        interface Value : ParameterValue {
            val levels: Float
            val scale: Float
            val offset: Float
            val color: ColorModel
        }
    }

    interface VoronoiCrystallize : Filter<VoronoiCrystallize.Value> {
        interface Value : ParameterValue {
            val borderThickness: Float
            val scale: Float
            val randomness: Float
            val shape: Int
            val turbulence: Float
            val angle: Float
            val stretch: Float
            val amount: Float
            val color: ColorModel
        }
    }

    interface Despeckle : Filter<Despeckle.Value> {
        interface Value : EmptyValue
    }

    interface Diffuse : Filter<Diffuse.Value> {
        interface Value : ParameterValue {
            val scale: Float
        }
    }

    interface DoG : Filter<DoG.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val secondRadius: Float
        }
    }

    interface Equalize : Filter<Equalize.Value> {
        interface Value : EmptyValue
    }

    interface Glow : Filter<Glow.Value> {
        interface Value : ParameterValue {
            val amount: Float
        }
    }

    interface Offset : Filter<Offset.Value> {
        interface Value : ParameterValue {
            val offsetX: Float
            val offsetY: Float
        }
    }

    interface Pinch : Filter<Pinch.Value> {
        interface Value : ParameterValue {
            val angle: Float
            val centreX: Float
            val centreY: Float
            val radius: Float
            val amount: Float
        }
    }

    interface Pointillize : Filter<Pointillize.Value> {
        interface Value : ParameterValue {
            val borderThickness: Float
            val scale: Float
            val randomness: Float
            val shape: Int
            val turbulence: Float
            val angle: Float
            val stretch: Float
            val amount: Float
            val color: ColorModel
        }
    }

    interface PolarCoordinates : Filter<PolarCoordinates.Value> {
        interface Value : ParameterValue {
            val type: PolarCoordinatesType
        }
    }

    interface ReduceNoise : Filter<ReduceNoise.Value> {
        interface Value : EmptyValue
    }

    interface SimpleSolarize : Filter<SimpleSolarize.Value> {
        interface Value : EmptyValue
    }

    interface Weave : Filter<Weave.Value> {
        interface Value : ParameterValue {
            val xWidth: Float
            val yWdth: Float
            val xGap: Float
            val yGap: Float
        }
    }

    interface Twirl : Filter<Twirl.Value> {
        interface Value : ParameterValue {
            val angle: Float
            val centerX: Float
            val centerY: Float
            val radius: Float
        }
    }

    interface RubberStamp : Filter<RubberStamp.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val softness: Float
            val radius: Float
            val firstColor: ColorModel
            val secondColor: ColorModel
        }
    }

    interface Smear : Filter<Smear.Value> {
        interface Value : ParameterValue {
            val angle: Float
            val density: Float
            val mix: Float
            val distance: Int
            val shape: Int
        }
    }

    interface SphereLensDistortion : Filter<SphereLensDistortion.Value> {
        interface Value : ParameterValue {
            val refractionIndex: Float
            val radius: Float
            val centerX: Float
            val centerY: Float
        }
    }

    interface Arc : Filter<Arc.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val height: Float
            val angle: Float
            val spreadAngle: Float
            val centreX: Float
            val centreY: Float
        }
    }

    interface Sparkle : Filter<Sparkle.Value> {
        interface Value : ParameterValue {
            val amount: Int
            val rays: Int
            val radius: Float
            val randomness: Int
            val centreX: Float
            val centreY: Float
            val color: ColorModel
        }
    }

    interface Ascii : Filter<Ascii.Value> {
        interface Value : ParameterValue {
            val gradient: String
            val fontSize: Float
            val backgroundColor: ColorModel
            val isGrayscale: Boolean
            val font: FontType?
        }
    }

    interface Moire : Filter<Moire.Value> {
        interface Value : EmptyValue
    }

    interface Autumn : Filter<Autumn.Value> {
        interface Value : EmptyValue
    }

    interface Bone : Filter<Bone.Value> {
        interface Value : EmptyValue
    }

    interface Jet : Filter<Jet.Value> {
        interface Value : EmptyValue
    }

    interface Winter : Filter<Winter.Value> {
        interface Value : EmptyValue
    }

    interface Rainbow : Filter<Rainbow.Value> {
        interface Value : EmptyValue
    }

    interface Ocean : Filter<Ocean.Value> {
        interface Value : EmptyValue
    }

    interface Summer : Filter<Summer.Value> {
        interface Value : EmptyValue
    }

    interface Spring : Filter<Spring.Value> {
        interface Value : EmptyValue
    }

    interface CoolVariant : Filter<CoolVariant.Value> {
        interface Value : EmptyValue
    }

    interface Hsv : Filter<Hsv.Value> {
        interface Value : EmptyValue
    }

    interface Pink : Filter<Pink.Value> {
        interface Value : EmptyValue
    }

    interface Hot : Filter<Hot.Value> {
        interface Value : EmptyValue
    }

    interface Parula : Filter<Parula.Value> {
        interface Value : EmptyValue
    }

    interface Magma : Filter<Magma.Value> {
        interface Value : EmptyValue
    }

    interface Inferno : Filter<Inferno.Value> {
        interface Value : EmptyValue
    }

    interface Plasma : Filter<Plasma.Value> {
        interface Value : EmptyValue
    }

    interface Viridis : Filter<Viridis.Value> {
        interface Value : EmptyValue
    }

    interface Cividis : Filter<Cividis.Value> {
        interface Value : EmptyValue
    }

    interface Twilight : Filter<Twilight.Value> {
        interface Value : EmptyValue
    }

    interface TwilightShifted : Filter<TwilightShifted.Value> {
        interface Value : EmptyValue
    }

    interface AutoPerspective : Filter<AutoPerspective.Value> {
        interface Value : EmptyValue
    }

    interface Deskew : Filter<Deskew.Value> {
        interface Value : ParameterValue {
            val max: Float
            val allowCrop: Boolean
        }
    }

    interface CropOrPerspective : Filter<CropOrPerspective.Value> {
        interface Value : ParameterValue {
            val topLeft: OffsetModel
            val topRight: OffsetModel
            val bottomLeft: OffsetModel
            val bottomRight: OffsetModel
            val isAbsolute: Boolean
        }
    }

    interface Turbo : Filter<Turbo.Value> {
        interface Value : EmptyValue
    }

    interface DeepGreen : Filter<DeepGreen.Value> {
        interface Value : EmptyValue
    }

    interface GradientMap : Filter<GradientMap.Value> {
        interface Value : ParameterValue {
            val palette: GradientPalette
        }
    }

    interface LensCorrection : Filter<LensCorrection.Value> {
        interface Value : ParameterValue {
            val strength: Float
            val profile: FileModel
        }
    }

    interface SeamCarving : Filter<SeamCarving.Value> {
        interface Value : ParameterValue {
            val size: IntegerSize
            val maskFile: FileModel
            val useBackwardEnergy: Boolean
            val useMaskAsRemoval: Boolean
        }
    }

    interface ErrorLevelAnalysis : Filter<ErrorLevelAnalysis.Value> {
        interface Value : ParameterValue {
            val quality: Float
        }
    }

    interface LuminanceGradient : Filter<LuminanceGradient.Value> {
        interface Value : EmptyValue
    }

    interface AverageDistance : Filter<AverageDistance.Value> {
        interface Value : EmptyValue
    }

    interface CopyMoveDetection : Filter<CopyMoveDetection.Value> {
        interface Value : ParameterValue {
            val retain: Float
            val coefficent: Float
        }
    }

    interface SimpleWeavePixelation : Filter<SimpleWeavePixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface StaggeredPixelation : Filter<StaggeredPixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface CrossPixelation : Filter<CrossPixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface MicroMacroPixelation : Filter<MicroMacroPixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface OrbitalPixelation : Filter<OrbitalPixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface VortexPixelation : Filter<VortexPixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface PulseGridPixelation : Filter<PulseGridPixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface NucleusPixelation : Filter<NucleusPixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface RadialWeavePixelation : Filter<RadialWeavePixelation.Value> {
        interface Value : ParameterValue {
            val layers: Float
        }
    }

    interface BorderFrame : Filter<BorderFrame.Value> {
        interface Value : ParameterValue {
            val horizontalBorderThickness: Float
            val verticalBorderThickness: Float
            val borderColor: ColorModel
        }
    }

    interface GlitchVariant : Filter<GlitchVariant.Value> {
        interface Value : ParameterValue {
            val repeatCount: Float
            val maxOffset: Float
            val channelShift: Float
        }
    }

    interface VHS : Filter<VHS.Value> {
        interface Value : ParameterValue {
            val seed: Float
            val strength: Float
        }
    }

    interface BlockGlitch : Filter<BlockGlitch.Value> {
        interface Value : ParameterValue {
            val blockSize: Float
            val strength: Float
        }
    }

    interface CrtCurvature : Filter<CrtCurvature.Value> {
        interface Value : ParameterValue {
            val curvature: Float
            val vignette: Float
            val chroma: Float
        }
    }

    interface PixelMelt : Filter<PixelMelt.Value> {
        interface Value : ParameterValue {
            val maxDrop: Float
            val strength: Float
        }
    }

    interface Bloom : Filter<Bloom.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val intensity: Float
            val radius: Int
            val softKnee: Float
            val exposure: Float
            val gamma: Float
        }
    }

    interface Distortion : Filter<Distortion.Value> {
        interface Value : ParameterValue {
            val strength: Float
        }
    }

    interface VHSNtsc : Filter<VHSNtsc.Value> {
        interface Value : ParameterValue {
            val amount: Float
            val chromaBleed: Float
            val tapeWear: Float
            val noise: Float
            val tracking: Float
            val seed: Int
            val lumaSmear: Float
            val compositeSharpening: Float
            val ringing: Float
            val snow: Float
            val useField: Int
            val filterType: Int
            val inputLumaFilter: Int
            val chromaLowpassIn: Int
            val chromaDemodulation: Int
            val videoScanlinePhaseShift: Int
            val videoScanlinePhaseShiftOffset: Int
            val headSwitchingEnabled: Boolean
            val headSwitchingHeight: Int
            val headSwitchingOffset: Int
            val headSwitchingHorizontalShift: Float
            val headSwitchingMidLinePosition: Float
            val headSwitchingMidLineJitter: Float
            val trackingNoiseEnabled: Boolean
            val trackingNoiseHeight: Int
            val trackingNoiseWaveIntensity: Float
            val trackingNoiseSnowIntensity: Float
            val trackingNoiseSnowAnisotropy: Float
            val trackingNoiseNoiseIntensity: Float
            val compositeNoiseEnabled: Boolean
            val compositeNoiseFrequency: Float
            val compositeNoiseIntensity: Float
            val compositeNoiseDetail: Int
            val ringingEnabled: Boolean
            val ringingFrequency: Float
            val ringingPower: Float
            val lumaNoiseEnabled: Boolean
            val lumaNoiseFrequency: Float
            val lumaNoiseIntensity: Float
            val lumaNoiseDetail: Int
            val chromaNoiseEnabled: Boolean
            val chromaNoiseFrequency: Float
            val chromaNoiseIntensity: Float
            val chromaNoiseDetail: Int
            val snowIntensity: Float
            val snowAnisotropy: Float
            val chromaPhaseNoiseIntensity: Float
            val chromaPhaseError: Float
            val chromaDelayHorizontal: Float
            val chromaDelayVertical: Int
            val vhsEnabled: Boolean
            val vhsTapeSpeed: Int
            val vhsChromaLoss: Float
            val vhsSharpenIntensity: Float
            val vhsSharpenFrequency: Float
            val vhsEdgeWaveIntensity: Float
            val vhsEdgeWaveSpeed: Float
            val vhsEdgeWaveFrequency: Float
            val vhsEdgeWaveDetail: Int
            val chromaLowpassOut: Int
            val scaleEnabled: Boolean
            val scaleHorizontal: Float
            val scaleVertical: Float
            val scaleFactorX: Float
            val scaleFactorY: Float
        }
    }

    interface ExpandImage : Filter<ExpandImage.Value> {
        interface Value : ParameterValue {
            val top: Float
            val start: Float
            val end: Float
            val bottom: Float
        }
    }

    interface DropShadow : Filter<DropShadow.Value> {
        interface Value : ParameterValue {
            val color: ColorModel
            val offsetX: Float
            val offsetY: Float
            val blurRadius: Float
        }
    }

    interface TornEdge : Filter<TornEdge.Value> {
        interface Value : ParameterValue {
            val toothHeight: Int
            val horizontalToothRange: Int
            val verticalToothRange: Int
            val top: Boolean
            val right: Boolean
            val bottom: Boolean
            val left: Boolean
        }
    }

    interface Flare : Filter<Flare.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val baseAmount: Float
            val ringAmount: Float
            val rayAmount: Float
            val ringWidth: Float
            val centreX: Float
            val centreY: Float
            val color: ColorModel
        }
    }

    interface DistortPerspective : Filter<DistortPerspective.Value> {
        interface Value : ParameterValue {
            val topLeft: OffsetModel
            val topRight: OffsetModel
            val bottomLeft: OffsetModel
            val bottomRight: OffsetModel
            val clip: Boolean
        }
    }

    interface JavaLookAndFeel : Filter<JavaLookAndFeel.Value> {
        interface Value : EmptyValue
    }

    interface Shear : Filter<Shear.Value> {
        interface Value : ParameterValue {
            val xAngle: Float
            val yAngle: Float
            val resize: Boolean
        }
    }

    interface WaterDrop : Filter<WaterDrop.Value> {
        interface Value : ParameterValue {
            val wavelength: Float
            val amplitude: Float
            val phase: Float
            val centreX: Float
            val centreY: Float
            val radius: Float
        }
    }

    interface HighPass : Filter<HighPass.Value> {
        interface Value : ParameterValue {
            val radius: Float
        }
    }

    interface ColorMask : Filter<ColorMask.Value> {
        interface Value : ParameterValue {
            val color: ColorModel
        }
    }

    interface Chrome : Filter<Chrome.Value> {
        interface Value : ParameterValue {
            val amount: Float
            val exposure: Float
        }
    }

    interface Dissolve : Filter<Dissolve.Value> {
        interface Value : ParameterValue {
            val density: Float
            val softness: Float
        }
    }

    interface Feedback : Filter<Feedback.Value> {
        interface Value : ParameterValue {
            val distance: Float
            val angle: Float
            val rotation: Float
            val zoom: Float
        }
    }

    interface LensBlur : Filter<LensBlur.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val sides: Float
            val bloom: Float
            val threshold: Float
        }
    }

    interface Levels : Filter<Levels.Value> {
        interface Value : ParameterValue {
            val lowInput: Float
            val highInput: Float
            val lowOutput: Float
            val highOutput: Float
        }
    }

    interface LightEffects : Filter<LightEffects.Value> {
        interface Value : ParameterValue {
            val bumpHeight: Float
            val bumpSoftness: Float
        }
    }

    interface Rays : Filter<Rays.Value> {
        interface Value : ParameterValue {
            val opacity: Float
            val threshold: Float
            val strength: Float
        }
    }

    interface Ripple : Filter<Ripple.Value> {
        interface Value : ParameterValue {
            val xAmplitude: Float
            val xWavelength: Float
            val yAmplitude: Float
            val yWavelength: Float
        }
    }

    interface AdaptiveBlur : Filter<AdaptiveBlur.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val threshold: Float
        }
    }

    interface AutoWhiteBalance : Filter<AutoWhiteBalance.Value> {
        interface Value : ParameterValue {
            val strength: Float
            val clipping: Float
        }
    }

    interface PalComposite : Filter<PalComposite.Value> {
        interface Value : ParameterValue {
            val lumaFilter: Float
            val chromaRadius: Int
            val noise: Float
            val phaseError: Int
            val scanSamples: Int
            val lineCompensation: Float
            val seed: Int
            val strength: Float
        }
    }

    interface Secam : Filter<Secam.Value> {
        interface Value : ParameterValue {
            val chromaRadius: Int
            val frequencyDeviation: Float
            val noise: Float
            val preEmphasis: Float
            val lineCrosstalk: Float
            val seed: Int
            val strength: Float
        }
    }

    interface RfReception : Filter<RfReception.Value> {
        interface Value : ParameterValue {
            val delay: Int
            val reflection: Float
            val noise: Float
            val frequency: Int
            val seed: Int
            val strength: Float
        }
    }

    interface SstvReception : Filter<SstvReception.Value> {
        interface Value : ParameterValue {
            val transmissionMode: Int
            val frequencyDrift: Float
            val syncError: Float
            val noise: Float
            val dropout: Float
            val seed: Int
            val strength: Float
        }
    }

    interface LcdSubpixels : Filter<LcdSubpixels.Value> {
        interface Value : ParameterValue {
            val size: Int
            val blackMatrix: Float
            val subpixelOrder: Int
            val viewAngle: Int
            val strength: Float
        }
    }

    interface FilmGrain : Filter<FilmGrain.Value> {
        interface Value : ParameterValue {
            val size: Float
            val intensity: Float
            val sizeVariation: Float
            val exposureDependence: Float
            val colorGrain: Float
            val seed: Int
        }
    }

    interface FilmNegative : Filter<FilmNegative.Value> {
        interface Value : ParameterValue {
            val exposure: Float
            val densityRange: Float
            val contrast: Float
            val toneCurve: Float
            val filmBase: Color
            val strength: Float
        }
    }

    interface AgxToneMapping : Filter<AgxToneMapping.Value> {
        interface Value : ParameterValue {
            val exposure: Float
            val look: Int
            val saturation: Float
            val strength: Float
        }
    }

    interface WaveletDenoise : Filter<WaveletDenoise.Value> {
        interface Value : ParameterValue {
            val luminanceNoise: Float
            val ntscChromaNoise: Float
            val levels: Float
            val strength: Float
        }
    }

    interface Daltonization : Filter<Daltonization.Value> {
        interface Value : ParameterValue {
            val visionType: Int
            val severity: Float
            val correction: Float
            val preserveLuminance: Float
            val strength: Float
        }
    }

    interface RetroHardware : Filter<RetroHardware.Value> {
        interface Value : ParameterValue {
            val system: Int
            val scale: Float
            val dithering: Float
            val strength: Float
        }
    }

    interface Risograph : Filter<Risograph.Value> {
        interface Value : ParameterValue {
            val spacing: Int
            val misregistration: Float
            val dotGain: Float
            val inks: Int
            val paperGrain: Float
            val seed: Int
            val strength: Float
        }
    }

    interface CmykPress : Filter<CmykPress.Value> {
        interface Value : ParameterValue {
            val spacing: Int
            val blackGeneration: Float
            val dotGain: Float
            val misregistration: Float
            val inkLimit: Float
            val paperGrain: Float
            val seed: Int
            val strength: Float
        }
    }

    interface ThermalReceipt : Filter<ThermalReceipt.Value> {
        interface Value : ParameterValue {
            val size: Int
            val heat: Float
            val heatRetention: Float
            val defects: Float
            val fading: Float
            val seed: Int
            val strength: Float
        }
    }

    interface PhotoCrossStitch : Filter<PhotoCrossStitch.Value> {
        interface Value : ParameterValue {
            val size: Int
            val threadColors: Int
            val threadWidth: Float
            val lightAngle: Int
            val relief: Float
            val seed: Int
            val strength: Float
        }
    }

    interface Autostereogram : Filter<Autostereogram.Value> {
        interface Value : ParameterValue {
            val repeatDistance: Float
            val depth: Float
            val gamma: Float
            val size: Int
            val softness: Float
            val invert: Boolean
            val seed: Int
        }
    }

    interface PaletteRemap : Filter<PaletteRemap.Value> {
        interface Value : ParameterValue {
            val dithering: Float
            val shadowColor: Color
            val midtoneColor: Color
            val highlightColor: Color
        }
    }

    interface ContrastPreservingGrayscale : Filter<ContrastPreservingGrayscale.Value> {
        interface Value : ParameterValue {
            val colorContrast: Float
            val contrast: Float
        }
    }

    interface FilmHalation : Filter<FilmHalation.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val knee: Float
            val radius: Float
            val warmth: Float
            val intensity: Float
        }
    }

    interface AnamorphicStreaks : Filter<AnamorphicStreaks.Value> {
        interface Value : ParameterValue {
            val threshold: Float
            val angle: Float
            val length: Float
            val width: Float
            val falloff: Float
            val intensity: Float
        }
    }

    interface DiffractionPsf : Filter<DiffractionPsf.Value> {
        interface Value : ParameterValue {
            val blades: Int
            val rotation: Float
            val radius: Float
            val spectralSpread: Float
            val threshold: Float
            val intensity: Float
        }
    }

    interface BayerSensor : Filter<BayerSensor.Value> {
        interface Value : ParameterValue {
            val pattern: Int
            val scale: Float
            val shotNoise: Float
            val readNoise: Float
            val seed: Int
            val strength: Float
            val edgeAware: Boolean
        }
    }

    interface GuidedSmoothing : Filter<GuidedSmoothing.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val epsilon: Float
            val guide: Int
            val strength: Float
        }
    }

    interface RollingGuidance : Filter<RollingGuidance.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val sensitivity: Float
            val iterations: Float
            val strength: Float
        }
    }

    interface L0Smoothing : Filter<L0Smoothing.Value> {
        interface Value : ParameterValue {
            val lambda: Float
            val penaltyGrowth: Float
            val iterations: Float
            val strength: Float
        }
    }

    interface NonLocalMeans : Filter<NonLocalMeans.Value> {
        interface Value : ParameterValue {
            val luminanceNoise: Float
            val ntscChromaNoise: Float
            val searchWindow: Float
            val strength: Float
        }
    }

    interface WienerDeconvolution : Filter<WienerDeconvolution.Value> {
        interface Value : ParameterValue {
            val psf: Int
            val radius: Float
            val angle: Float
            val regularization: Float
            val strength: Float
        }
    }

    interface SpectralNotch : Filter<SpectralNotch.Value> {
        interface Value : ParameterValue {
            val frequencyX: Float
            val frequencyY: Float
            val bandwidth: Float
            val strength: Float
        }
    }

    interface AdaptiveBinarization : Filter<AdaptiveBinarization.Value> {
        interface Value : ParameterValue {
            val algorithm: Int
            val radius: Float
            val coefficient: Float
            val dynamicRange: Float
            val ink: Color
            val paper: Color
            val invert: Boolean
        }
    }

    interface RgbChannelRegistration : Filter<RgbChannelRegistration.Value> {
        interface Value : ParameterValue {
            val maxShift: Float
            val minConfidence: Float
            val strength: Float
        }
    }

    interface CapillaryInkBleed : Filter<CapillaryInkBleed.Value> {
        interface Value : ParameterValue {
            val wetness: Float
            val direction: Float
            val spread: Float
            val roughness: Float
            val steps: Int
            val seed: Int
            val strength: Float
        }
    }

    interface DirectionalEmbroidery : Filter<DirectionalEmbroidery.Value> {
        interface Value : ParameterValue {
            val stitchLength: Float
            val density: Float
            val thickness: Float
            val relief: Float
            val colorLevels: Int
            val seed: Int
            val strength: Float
        }
    }

    interface LayeredPaperCut : Filter<LayeredPaperCut.Value> {
        interface Value : ParameterValue {
            val layers: Int
            val smoothness: Float
            val depth: Float
            val lightAngle: Float
            val grain: Float
            val strength: Float
        }
    }

    interface AnisotropicKuwahara : Filter<AnisotropicKuwahara.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val anisotropy: Float
            val selectivity: Float
            val strength: Float
        }
    }

    interface Xdog : Filter<Xdog.Value> {
        interface Value : ParameterValue {
            val sigma: Float
            val ratio: Float
            val weight: Float
            val threshold: Float
            val sharpness: Float
            val ink: Color
            val paper: Color
            val strength: Float
        }
    }

    interface ContourHatching : Filter<ContourHatching.Value> {
        interface Value : ParameterValue {
            val spacing: Float
            val width: Float
            val angle: Float
            val contrast: Float
            val ink: Color
            val paper: Color
            val strength: Float
        }
    }

    interface SingleLineDrawing : Filter<SingleLineDrawing.Value> {
        interface Value : ParameterValue {
            val pointCount: Int
            val densityCurve: Float
            val lineWidth: Float
            val ink: Color
            val paper: Color
            val optimization: Int
            val seed: Int
            val strength: Float
        }
    }

    interface DctGlitch : Filter<DctGlitch.Value> {
        interface Value : ParameterValue {
            val blockSize: Int
            val minimumFrequency: Float
            val maximumFrequency: Float
            val operation: Int
            val probability: Float
            val seed: Int
            val strength: Float
        }
    }

    interface FourierPhaseScramble : Filter<FourierPhaseScramble.Value> {
        interface Value : ParameterValue {
            val minFrequency: Float
            val maxFrequency: Float
            val phase: Float
            val seed: Int
            val strength: Float
        }
    }

    interface SignedDistanceField : Filter<SignedDistanceField.Value> {
        interface Value : ParameterValue {
            val alphaMask: Boolean
            val threshold: Float
            val distance: Float
            val mode: Int
            val spacing: Float
            val insideColor: Color
            val outsideColor: Color
            val invert: Boolean
        }
    }

    interface DomainTransform : Filter<DomainTransform.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val sensitivity: Float
            val iterations: Float
            val strength: Float
        }
    }

    interface Isophotes : Filter<Isophotes.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface LocalOrientation : Filter<LocalOrientation.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Skeleton : Filter<Skeleton.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface SuperPixels : Filter<SuperPixels.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface FadedArray : Filter<FadedArray.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface MirroredArray : Filter<MirroredArray.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RandomPatchArray : Filter<RandomPatchArray.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RandomColorArray : Filter<RandomColorArray.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Blocks3D : Filter<Blocks3D.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ChessboardOverlay : Filter<ChessboardOverlay.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Grid : Filter<Grid.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface DiceMosaic : Filter<DiceMosaic.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Ministeck : Filter<Ministeck.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ShufflePatches : Filter<ShufflePatches.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Taquin : Filter<Taquin.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface TileableRotation : Filter<TileableRotation.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface MakeSeamless : Filter<MakeSeamless.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ChromaticAberrations : Filter<ChromaticAberrations.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface CrtPhosphors : Filter<CrtPhosphors.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface HuffmanGlitch : Filter<HuffmanGlitch.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface JpegArtifacts : Filter<JpegArtifacts.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface PixelSort : Filter<PixelSort.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RainAndSnow : Filter<RainAndSnow.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Streak : Filter<Streak.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface CrtScanlines : Filter<CrtScanlines.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Dirty : Filter<Dirty.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface FlipAndRotateBlocks : Filter<FlipAndRotateBlocks.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Lomo : Filter<Lomo.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface MessWithBits : Filter<MessWithBits.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface OffsetStripes : Filter<OffsetStripes.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface OldMovieStripes : Filter<OldMovieStripes.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface OldSchool8Bit : Filter<OldSchool8Bit.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RandomShadeStripes : Filter<RandomShadeStripes.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RebuildFromSimilarBlocks : Filter<RebuildFromSimilarBlocks.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface CircleAbstraction : Filter<CircleAbstraction.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Cubism : Filter<Cubism.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface FeltPen : Filter<FeltPen.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Ghost : Filter<Ghost.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Linify : Filter<Linify.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface MarkerDrawing : Filter<MarkerDrawing.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Rodilius : Filter<Rodilius.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Shapeism : Filter<Shapeism.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface SharpAbstract : Filter<SharpAbstract.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface SmoothAbstract : Filter<SmoothAbstract.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Stringify : Filter<Stringify.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Engrave : Filter<Engrave.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Filaments : Filter<Filaments.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface InkWash : Filter<InkWash.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Brushify : Filter<Brushify.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Cutout : Filter<Cutout.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Ellipsionism : Filter<Ellipsionism.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Fractalize : Filter<Fractalize.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface HopePoster : Filter<HopePoster.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface LylejkPainting : Filter<LylejkPainting.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Painting : Filter<Painting.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface PosterEdges : Filter<PosterEdges.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Quadtree : Filter<Quadtree.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface VectorPainting : Filter<VectorPainting.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Cracks : Filter<Cracks.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface MineralMosaic : Filter<MineralMosaic.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface StainedGlass : Filter<StainedGlass.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface DiffusionTensors : Filter<DiffusionTensors.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Doodle : Filter<Doodle.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Charcoal : Filter<Charcoal.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface PencilPortrait : Filter<PencilPortrait.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ColorAbstraction : Filter<ColorAbstraction.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface CinematicDream : Filter<CinematicDream.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface GraphicNovel : Filter<GraphicNovel.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface DreamPainting : Filter<DreamPainting.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface AnalogDamage : Filter<AnalogDamage.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Breaks : Filter<Breaks.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface CircleTransform : Filter<CircleTransform.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Crease : Filter<Crease.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Flower : Filter<Flower.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface SquareToCircle : Filter<SquareToCircle.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface WarpByIntensity : Filter<WarpByIntensity.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Displacement : Filter<Displacement.Value> {
        interface Value : ParameterValue {
            val horizontalMap: ImageModel
            val verticalMap: ImageModel?
            val strengthX: Float
            val strengthY: Float
            val interpolation: DisplacementInterpolation
            val boundary: DisplacementBoundary
        }
    }

    interface Droste : Filter<Droste.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ConformalMap : Filter<ConformalMap.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface PoincareDisk : Filter<PoincareDisk.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface StereographicProjection : Filter<StereographicProjection.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Tunnel : Filter<Tunnel.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface EquirectangularProjection : Filter<EquirectangularProjection.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface WaterReflection : Filter<WaterReflection.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Puzzle : Filter<Puzzle.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Retinex : Filter<Retinex.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface DetailsEqualizer : Filter<DetailsEqualizer.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface FreakyDetails : Filter<FreakyDetails.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface MagicDetails : Filter<MagicDetails.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface MightyDetails : Filter<MightyDetails.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface PopShadows : Filter<PopShadows.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Deblur : Filter<Deblur.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface AlphaSharpen : Filter<AlphaSharpen.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface GoldMeinelSharpen : Filter<GoldMeinelSharpen.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RichardsonLucySharpen : Filter<RichardsonLucySharpen.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface MultiscaleSharpen : Filter<MultiscaleSharpen.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface SmoothSkin : Filter<SmoothSkin.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface CubeFrame : Filter<CubeFrame.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface BlurredFrame : Filter<BlurredFrame.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface FuzzyFrame : Filter<FuzzyFrame.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface MirrorFrame : Filter<MirrorFrame.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface PaintingFrame : Filter<PaintingFrame.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RoundedFrame : Filter<RoundedFrame.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface SmoothFrame : Filter<SmoothFrame.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface FrostedGlass : Filter<FrostedGlass.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface LightPatch : Filter<LightPatch.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ReliefLight : Filter<ReliefLight.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ShadowPatch : Filter<ShadowPatch.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RawGmic : Filter<RawGmic.Value> {
        var auxiliaryImages: List<ImageModel>

        interface Value : ParameterValue {
            val command: String
        }
    }

    interface Unpurple : Filter<Unpurple.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Unstrip : Filter<Unstrip.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface BandingDenoise : Filter<BandingDenoise.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface DcpDehaze : Filter<DcpDehaze.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface HessianSharpen : Filter<HessianSharpen.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface WhitenSharpen : Filter<WhitenSharpen.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface HoughSketch : Filter<HoughSketch.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Warhol : Filter<Warhol.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Descreen : Filter<Descreen.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface DenoiseSmooth : Filter<DenoiseSmooth.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface CleanText : Filter<CleanText.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface FillHoles : Filter<FillHoles.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RemoveScratches : Filter<RemoveScratches.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RemoveHotPixels : Filter<RemoveHotPixels.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface TextureSharpen : Filter<TextureSharpen.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface PolaroidFrame : Filter<PolaroidFrame.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface PenDrawing : Filter<PenDrawing.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface OldPhotograph : Filter<OldPhotograph.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RetroFade : Filter<RetroFade.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface SixtiesCinema : Filter<SixtiesCinema.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface MetallicLook : Filter<MetallicLook.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ZoneSystem : Filter<ZoneSystem.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface BufferError : Filter<BufferError.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface FreakyBlackAndWhite : Filter<FreakyBlackAndWhite.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface DodgeSketch : Filter<DodgeSketch.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface PosterizedDithering : Filter<PosterizedDithering.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface Hedcut : Filter<Hedcut.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface LensDistortion : Filter<LensDistortion.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface PowerTwirl : Filter<PowerTwirl.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RandomDeformations : Filter<RandomDeformations.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ShockWaves : Filter<ShockWaves.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface RowShifter : Filter<RowShifter.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface PolarTransform : Filter<PolarTransform.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface FishEye : Filter<FishEye.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface BoostChromaticity : Filter<BoostChromaticity.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface EqualizeLight : Filter<EqualizeLight.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface EqualizeShadow : Filter<EqualizeShadow.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface LightGlow : Filter<LightGlow.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ColorTemperature : Filter<ColorTemperature.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface TemperatureBalance : Filter<TemperatureBalance.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface SelectiveDesaturation : Filter<SelectiveDesaturation.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface GraduatedColorAccent : Filter<GraduatedColorAccent.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface HslAdjustment : Filter<HslAdjustment.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface SpecificSaturation : Filter<SpecificSaturation.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ChannelProcessing : Filter<ChannelProcessing.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface HsvEqualizer : Filter<HsvEqualizer.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface TargetColorSpot : Filter<TargetColorSpot.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface BlackAndWhiteFilm : Filter<BlackAndWhiteFilm.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface LocalContrastEnhancement : Filter<LocalContrastEnhancement.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface LocalVarianceNormalization : Filter<LocalVarianceNormalization.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface GradientSharpen : Filter<GradientSharpen.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ToneSharpen : Filter<ToneSharpen.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ConstrainedSharpen : Filter<ConstrainedSharpen.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface HighlightSynthesis : Filter<HighlightSynthesis.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface DesaturateNorm : Filter<DesaturateNorm.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface SaturationEqualizer : Filter<SaturationEqualizer.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface DynamicContrast : Filter<DynamicContrast.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface ToneEnhance : Filter<ToneEnhance.Value> {
        interface Value : ParameterValue {
            val settings: GmicFilterParams
        }
    }

    interface HeightAmbientOcclusion : Filter<HeightAmbientOcclusion.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val depth: Float
            val directions: Int
            val strength: Float
        }
    }

    interface HeightCastShadows : Filter<HeightCastShadows.Value> {
        interface Value : ParameterValue {
            val azimuth: Float
            val elevation: Float
            val depth: Float
            val softness: Float
            val strength: Float
        }
    }

    interface RefractiveCaustics : Filter<RefractiveCaustics.Value> {
        interface Value : ParameterValue {
            val scale: Float
            val refraction: Float
            val dispersion: Float
            val focus: Float
            val strength: Float
        }
    }

    interface ThinFilmIridescence : Filter<ThinFilmIridescence.Value> {
        interface Value : ParameterValue {
            val thickness: Float
            val variation: Float
            val angle: Float
            val saturation: Float
            val strength: Float
        }
    }

    interface DiffractionGrating : Filter<DiffractionGrating.Value> {
        interface Value : ParameterValue {
            val spacing: Float
            val angle: Float
            val relief: Float
            val bandwidth: Float
            val strength: Float
        }
    }

    interface FlowTexture : Filter<FlowTexture.Value> {
        interface Value : ParameterValue {
            val length: Float
            val detail: Float
            val contrast: Float
            val seed: Int
            val strength: Float
        }
    }

    interface HilbertRibbon : Filter<HilbertRibbon.Value> {
        interface Value : ParameterValue {
            val order: Int
            val lineWidth: Float
            val contrast: Float
            val ink: Color
            val paper: Color
            val strength: Float
        }
    }

    interface MazePortrait : Filter<MazePortrait.Value> {
        interface Value : ParameterValue {
            val cellSize: Float
            val wallWidth: Float
            val bias: Float
            val seed: Int
            val ink: Color
            val paper: Color
            val strength: Float
        }
    }

    interface KnittedFabric : Filter<KnittedFabric.Value> {
        interface Value : ParameterValue {
            val stitchSize: Float
            val yarnWidth: Float
            val relief: Float
            val twist: Float
            val strength: Float
        }
    }

    interface OrigamiRelief : Filter<OrigamiRelief.Value> {
        interface Value : ParameterValue {
            val cellSize: Float
            val foldHeight: Float
            val lightAngle: Float
            val crease: Float
            val strength: Float
        }
    }

    interface PageCurl : Filter<PageCurl.Value> {
        interface Value : ParameterValue {
            val amount: Float
            val radius: Float
            val angle: Float
            val shadow: Float
            val backside: Color
            val strength: Float
        }
    }

    interface CurlAdvection : Filter<CurlAdvection.Value> {
        interface Value : ParameterValue {
            val scale: Float
            val distance: Float
            val steps: Int
            val seed: Int
            val strength: Float
        }
    }

    interface HistogramMosaic : Filter<HistogramMosaic.Value> {
        interface Value : ParameterValue {
            val cells: Int
            val bins: Int
            val gap: Float
            val strength: Float
        }
    }

    interface LocalBinaryPattern : Filter<LocalBinaryPattern.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val tolerance: Float
            val strength: Float
        }
    }

    interface LocalEntropyMap : Filter<LocalEntropyMap.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val bins: Int
            val gain: Float
            val strength: Float
        }
    }

    interface FourierSpectrum : Filter<FourierSpectrum.Value> {
        interface Value : ParameterValue {
            val windowing: Float
            val contrast: Float
            val strength: Float
        }
    }

    interface PhaseOnlyReconstruction : Filter<PhaseOnlyReconstruction.Value> {
        interface Value : ParameterValue {
            val contrast: Float
            val strength: Float
        }
    }

    interface SpectralSaliency : Filter<SpectralSaliency.Value> {
        interface Value : ParameterValue {
            val spectralRadius: Int
            val smoothing: Float
            val overlay: Float
            val strength: Float
        }
    }

    interface MorphologicalReconstruction : Filter<MorphologicalReconstruction.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val mode: Int
            val strength: Float
        }
    }

    interface NailStringArt : Filter<NailStringArt.Value> {
        interface Value : ParameterValue {
            val nails: Int
            val threads: Int
            val threadWidth: Float
            val contrast: Float
            val ink: Color
            val paper: Color
            val strength: Float
        }
    }

    interface FoldedBook : Filter<FoldedBook.Value> {
        interface Value : ParameterValue {
            val pages: Int
            val depth: Float
            val viewAngle: Float
            val paper: Color
            val strength: Float
        }
    }

    interface PaperQuilling : Filter<PaperQuilling.Value> {
        interface Value : ParameterValue {
            val cellSize: Float
            val turns: Float
            val stripWidth: Float
            val relief: Float
            val background: Color
            val strength: Float
        }
    }

    interface LenticularPrint : Filter<LenticularPrint.Value> {
        interface Value : ParameterValue {
            val lensWidth: Float
            val viewAngle: Float
            val refraction: Float
            val gloss: Float
            val rotation: Float
            val strength: Float
        }
    }

    interface PinArt : Filter<PinArt.Value> {
        interface Value : ParameterValue {
            val spacing: Float
            val height: Float
            val angle: Float
            val gloss: Float
            val strength: Float
        }
    }

    interface CeramicGlaze : Filter<CeramicGlaze.Value> {
        interface Value : ParameterValue {
            val size: Float
            val pooling: Float
            val gloss: Float
            val crackle: Float
            val seed: Int
            val strength: Float
        }
    }

    interface Marquetry : Filter<Marquetry.Value> {
        interface Value : ParameterValue {
            val size: Float
            val grain: Float
            val bevel: Float
            val seed: Int
            val strength: Float
        }
    }

    interface PhotoelasticStress : Filter<PhotoelasticStress.Value> {
        interface Value : ParameterValue {
            val load: Float
            val smoothing: Float
            val angle: Float
            val ambient: Float
            val strength: Float
        }
    }

    interface Condensation : Filter<Condensation.Value> {
        interface Value : ParameterValue {
            val size: Float
            val coverage: Float
            val refraction: Float
            val haze: Float
            val seed: Int
            val strength: Float
        }
    }

    interface SpecularHighlightSuppression : Filter<SpecularHighlightSuppression.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val threshold: Float
            val strength: Float
        }
    }

    interface GradientDomainToneMapping : Filter<GradientDomainToneMapping.Value> {
        interface Value : ParameterValue {
            val compression: Float
            val detail: Float
            val strength: Float
        }
    }

    interface ShockSharpening : Filter<ShockSharpening.Value> {
        interface Value : ParameterValue {
            val iterations: Int
            val smoothing: Float
            val strength: Float
        }
    }

    interface ColorSpillSuppression : Filter<ColorSpillSuppression.Value> {
        interface Value : ParameterValue {
            val color: Int
            val tolerance: Float
            val recovery: Float
            val strength: Float
        }
    }

    interface UnderwaterRestoration : Filter<UnderwaterRestoration.Value> {
        interface Value : ParameterValue {
            val radius: Float
            val restoration: Float
            val redRecovery: Float
            val strength: Float
        }
    }
}

private typealias Color = ColorModel
