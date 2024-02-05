/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

@file:Suppress("UNCHECKED_CAST")

package ru.tech.imageresizershrinker.feature.filters.data.provider

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.feature.filters.data.model.AcesFilmicToneMappingFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AcesHillToneMappingFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AnaglyphFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AnisotropicDiffusionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AtkinsonDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BayerEightDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BayerFourDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BayerThreeDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BayerTwoDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BilaterialBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BlackAndWhiteFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BoxBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BrightnessFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BulgeDistortionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BurkesDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CGAColorSpaceFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CirclePixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ColorBalanceFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ColorFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ColorMatrixFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ContrastFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.Convolution3x3Filter
import ru.tech.imageresizershrinker.feature.filters.data.model.CrosshatchFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CrystallizeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.DiamondPixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.DilationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EmbossFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EnhancedCirclePixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EnhancedDiamondPixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EnhancedGlitchFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EnhancedPixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ErodeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ExposureFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FalseColorFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FalseFloydSteinbergDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FastBilaterialBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FastBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FloydSteinbergDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FractalGlassFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GammaFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GaussianBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GlassSphereRefractionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GlitchFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GrayscaleFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HableFilmicToneMappingFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HalftoneFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HazeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HejlBurgessToneMappingFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HighlightsAndShadowsFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HorizontalWindStaggerFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HueFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.JarvisJudiceNinkeDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.KuwaharaFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LaplacianFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LeftToRightDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LogarithmicToneMappingFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LookupFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LuminanceThresholdFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.MarbleFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.MedianBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.MonochromeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.NativeStackBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.NegativeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.NoiseFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.NonMaximumSuppressionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.OilFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.OpacityFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PerlinDistortionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PixelSortFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PoissonBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PosterizeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.QuantizierFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.RGBFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.RandomDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.RemoveColorFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ReplaceColorFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SaturationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SepiaFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SharpenFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ShuffleFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SideFadeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SierraDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SierraLiteDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SimpleThresholdDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SketchFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SmoothToonFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SobelEdgeDetectionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SolarizeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SphereRefractionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.StackBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.StrokePixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.StuckiDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SwirlDistortionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.TentBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.TiltShiftFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ToonFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.TwoRowSierraDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.VibranceFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.VignetteFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.WaterEffectFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.WeakPixelFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.WhiteBalanceFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ZoomBlurFilter
import javax.inject.Inject

internal class AndroidFilterProvider @Inject constructor(
    private val context: Context,
    private val glitchFilterFactory: GlitchFilter.Factory
) : FilterProvider<Bitmap> {

    override fun filterToTransformation(
        filter: Filter<Bitmap, *>
    ): Transformation<Bitmap> = filter.run {
        when (this) {
            is Filter.BilaterialBlur -> BilaterialBlurFilter(value)
            is Filter.BlackAndWhite -> BlackAndWhiteFilter(context)
            is Filter.BoxBlur -> BoxBlurFilter(value)
            is Filter.Brightness -> BrightnessFilter(context, value)
            is Filter.BulgeDistortion -> BulgeDistortionFilter(context, value)
            is Filter.CGAColorSpace -> CGAColorSpaceFilter(context)
            is Filter.CirclePixelation -> CirclePixelationFilter(value)
            is Filter.ColorBalance -> ColorBalanceFilter(context, value)
            is Filter.Color -> ColorFilter(value as Color)
            is Filter.ColorMatrix -> ColorMatrixFilter(context, value)
            is Filter.Contrast -> ContrastFilter(context, value)
            is Filter.Convolution3x3 -> Convolution3x3Filter(context, value)
            is Filter.Crosshatch -> CrosshatchFilter(context, value)
            is Filter.DiamondPixelation -> DiamondPixelationFilter(value)
            is Filter.Dilation -> DilationFilter(value)
            is Filter.Emboss -> EmbossFilter(context, value)
            is Filter.EnhancedCirclePixelation -> EnhancedCirclePixelationFilter(value)
            is Filter.EnhancedDiamondPixelation -> EnhancedDiamondPixelationFilter(value)
            is Filter.EnhancedPixelation -> EnhancedPixelationFilter(value)
            is Filter.Exposure -> ExposureFilter(context, value)
            is Filter.FalseColor<*, *> -> FalseColorFilter(context, value as Pair<Color, Color>)
            is Filter.FastBlur -> FastBlurFilter(value)
            is Filter.Gamma -> GammaFilter(context, value)
            is Filter.GaussianBlur -> GaussianBlurFilter(value)
            is Filter.GlassSphereRefraction -> GlassSphereRefractionFilter(context, value)
            is Filter.Halftone -> HalftoneFilter(context, value)
            is Filter.Haze -> HazeFilter(context, value)
            is Filter.HighlightsAndShadows -> HighlightsAndShadowsFilter(context, value)
            is Filter.Hue -> HueFilter(context, value)
            is Filter.Kuwahara -> KuwaharaFilter(context, value)
            is Filter.Laplacian -> LaplacianFilter(context)
            is Filter.Lookup -> LookupFilter(context, value)
            is Filter.LuminanceThreshold -> LuminanceThresholdFilter(context, value)
            is Filter.Monochrome<*, *> -> MonochromeFilter(value as Pair<Float, Color>)
            is Filter.Negative -> NegativeFilter(context)
            is Filter.NonMaximumSuppression -> NonMaximumSuppressionFilter(context)
            is Filter.Opacity -> OpacityFilter(context, value)
            is Filter.Pixelation -> PixelationFilter(value)
            is Filter.Posterize -> PosterizeFilter(context, value)
            is Filter.RemoveColor<*, *> -> RemoveColorFilter(value as Pair<Float, Color>)
            is Filter.ReplaceColor<*, *> -> ReplaceColorFilter(value as Triple<Float, Color, Color>)
            is Filter.RGB -> RGBFilter(context, value as Color)
            is Filter.Saturation -> SaturationFilter(context, value)
            is Filter.Sepia -> SepiaFilter(context, value)
            is Filter.Sharpen -> SharpenFilter(context, value)
            is Filter.Sketch -> SketchFilter(value)
            is Filter.SmoothToon -> SmoothToonFilter(context, value)
            is Filter.SobelEdgeDetection -> SobelEdgeDetectionFilter(context, value)
            is Filter.Solarize -> SolarizeFilter(context, value)
            is Filter.SphereRefraction -> SphereRefractionFilter(context, value)
            is Filter.StackBlur -> StackBlurFilter(value)
            is Filter.StrokePixelation -> StrokePixelationFilter(value)
            is Filter.SwirlDistortion -> SwirlDistortionFilter(context, value)
            is Filter.Toon -> ToonFilter(context, value)
            is Filter.Vibrance -> VibranceFilter(context, value)
            is Filter.Vignette<*, *> -> VignetteFilter(
                context,
                value as Triple<Float, Float, Color>
            )

            is Filter.WeakPixel -> WeakPixelFilter(context)
            is Filter.WhiteBalance -> WhiteBalanceFilter(context, value)
            is Filter.ZoomBlur -> ZoomBlurFilter(context, value)
            is Filter.Quantizier -> QuantizierFilter(value)
            is Filter.BayerTwoDithering -> BayerTwoDitheringFilter(value)
            is Filter.BayerThreeDithering -> BayerThreeDitheringFilter(value)
            is Filter.BayerFourDithering -> BayerFourDitheringFilter(value)
            is Filter.BayerEightDithering -> BayerEightDitheringFilter(value)
            is Filter.FloydSteinbergDithering -> FloydSteinbergDitheringFilter(value)
            is Filter.JarvisJudiceNinkeDithering -> JarvisJudiceNinkeDitheringFilter(value)
            is Filter.SierraDithering -> SierraDitheringFilter(value)
            is Filter.TwoRowSierraDithering -> TwoRowSierraDitheringFilter(value)
            is Filter.SierraLiteDithering -> SierraLiteDitheringFilter(value)
            is Filter.AtkinsonDithering -> AtkinsonDitheringFilter(value)
            is Filter.StuckiDithering -> StuckiDitheringFilter(value)
            is Filter.BurkesDithering -> BurkesDitheringFilter(value)
            is Filter.FalseFloydSteinbergDithering -> FalseFloydSteinbergDitheringFilter(value)
            is Filter.LeftToRightDithering -> LeftToRightDitheringFilter(value)
            is Filter.RandomDithering -> RandomDitheringFilter(value)
            is Filter.SimpleThresholdDithering -> SimpleThresholdDitheringFilter(value)
            is Filter.MedianBlur -> MedianBlurFilter(value)
            is Filter.NativeStackBlur -> NativeStackBlurFilter(value)
            is Filter.TiltShift -> TiltShiftFilter(value)
            is Filter.Glitch -> glitchFilterFactory(value)
            is Filter.Shuffle -> ShuffleFilter(value)
            is Filter.Anaglyph -> AnaglyphFilter(value)
            is Filter.PixelSort -> PixelSortFilter(value)
            is Filter.Noise -> NoiseFilter(value)
            is Filter.EnhancedGlitch -> EnhancedGlitchFilter(value)
            is Filter.TentBlur -> TentBlurFilter(value)
            is Filter.SideFade -> SideFadeFilter(value)
            is Filter.Erode -> ErodeFilter(value)
            is Filter.AnisotropicDiffusion -> AnisotropicDiffusionFilter(value)
            is Filter.HorizontalWindStagger<*, *> -> HorizontalWindStaggerFilter(value as Triple<Float, Int, Color>)
            is Filter.FastBilaterialBlur -> FastBilaterialBlurFilter(value)
            is Filter.PoissonBlur -> PoissonBlurFilter(value)
            is Filter.LogarithmicToneMapping -> LogarithmicToneMappingFilter(value)
            is Filter.AcesFilmicToneMapping -> AcesFilmicToneMappingFilter(value)
            is Filter.Crystallize<*, *> -> CrystallizeFilter(value as Pair<Float, Color>)
            is Filter.FractalGlass -> FractalGlassFilter(value)
            is Filter.Marble -> MarbleFilter(value)
            is Filter.Oil -> OilFilter(value)
            is Filter.WaterEffect -> WaterEffectFilter(value)
            is Filter.PerlinDistortion -> PerlinDistortionFilter(value)
            is Filter.HableFilmicToneMapping -> HableFilmicToneMappingFilter(value)
            is Filter.AcesHillToneMapping -> AcesHillToneMappingFilter(value)
            is Filter.HejlBurgessToneMapping -> HejlBurgessToneMappingFilter(value)
            is Filter.Grayscale -> GrayscaleFilter(value)

            else -> throw IllegalArgumentException("No filter implementation for interface ${filter::class.simpleName}")
        }
    }

}