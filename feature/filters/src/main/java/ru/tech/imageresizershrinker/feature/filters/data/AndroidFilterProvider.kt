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

package ru.tech.imageresizershrinker.feature.filters.data

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.tech.imageresizershrinker.core.domain.transformation.EmptyTransformation
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.feature.filters.data.model.AcesFilmicToneMappingFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AcesHillToneMappingFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AchromatomalyFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AchromatopsiaFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AldridgeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AmatorkaFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AnaglyphFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AnisotropicDiffusionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AtkinsonDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AutoCropFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AutoRemoveRedEyesFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.AutumnTonesFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BayerEightDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BayerFourDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BayerThreeDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BayerTwoDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BilaterialBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BlackAndWhiteFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BlackHatFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BleachBypassFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BokehFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BoxBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BrightnessFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BrowniFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BulgeDistortionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.BurkesDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CGAColorSpaceFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CandlelightFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CannyFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CaramelDarknessFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CelluloidFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CircleBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CirclePixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ClaheFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ClaheHSLFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ClaheHSVFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ClaheJzazbzFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ClaheLABFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ClaheLUVFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ClaheOklabFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ClaheOklchFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ClosingFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.Clustered2x2DitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.Clustered4x4DitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.Clustered8x8DitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CodaChromeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CoffeeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ColorAnomalyFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ColorBalanceFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ColorExplosionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ColorFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ColorMatrix3x3Filter
import ru.tech.imageresizershrinker.feature.filters.data.model.ColorMatrix4x4Filter
import ru.tech.imageresizershrinker.feature.filters.data.model.ColorPosterFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ColorfulSwirlFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ContrastFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ConvexFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.Convolution3x3Filter
import ru.tech.imageresizershrinker.feature.filters.data.model.CoolFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CropToContentFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CrossBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CrosshatchFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CrystallizeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CubeLutFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.CyberpunkFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.DeepPurpleFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.DehazeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.DeutaromalyFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.DeutaronotopiaFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.DiamondPixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.DigitalCodeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.DilationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.DragoFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.DropBluesFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EdgyAmberFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ElectricGradientFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EmbossFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EnhancedCirclePixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EnhancedDiamondPixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EnhancedGlitchFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EnhancedOilFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EnhancedPixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EnhancedZoomBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EqualizeHistogramAdaptiveFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EqualizeHistogramAdaptiveHSLFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EqualizeHistogramAdaptiveHSVFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EqualizeHistogramAdaptiveLABFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EqualizeHistogramAdaptiveLUVFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EqualizeHistogramFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EqualizeHistogramHSVFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.EqualizeHistogramPixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ErodeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ExposureFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FallColorsFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FalseColorFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FalseFloydSteinbergDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FantasyLandscapeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FastBilaterialBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FastBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FastGaussianBlur2DFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FastGaussianBlur3DFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FastGaussianBlur4DFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FilmStock50Filter
import ru.tech.imageresizershrinker.feature.filters.data.model.FloydSteinbergDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FoggyNightFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FractalGlassFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.FuturisticGradientFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GammaFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GaussianBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GaussianBoxBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GlassSphereRefractionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GlitchFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GoldenForestFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GoldenHourFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GothamFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GrainFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GrayscaleFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GreenSunFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.GreenishFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HDRFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HableFilmicToneMappingFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HalftoneFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HazeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HejlBurgessToneMappingFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HighlightsAndShadowsFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HorizontalWindStaggerFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HotSummerFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.HueFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.JarvisJudiceNinkeDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.KodakFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.KuwaharaFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LUT512x512Filter
import ru.tech.imageresizershrinker.feature.filters.data.model.LaplacianFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LaplacianSimpleFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LavenderDreamFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LeftToRightDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LemonadeLightFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LinearBoxBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LinearFastGaussianBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LinearFastGaussianBlurNextFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LinearGaussianBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LinearGaussianBoxBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LinearStackBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LinearTentBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LinearTiltShiftFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LogarithmicToneMappingFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LookupFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.LowPolyFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.MarbleFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.MedianBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.MissEtikateFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.MobiusFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.MonochromeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.MorphologicalGradientFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.MotionBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.NativeStackBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.NegativeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.NeonFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.NightMagicFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.NightVisionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.NoiseFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.NonMaximumSuppressionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.OilFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.OldTvFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.OpacityFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.OpeningFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.OrangeHazeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PaletteTransferFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PaletteTransferVariantFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PastelFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PerlinDistortionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PinkDreamFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PoissonBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PolaroidFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PolkaDotFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PopArtFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PosterizeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ProtanopiaFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ProtonomalyFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.PurpleMistFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.QuantizierFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.RGBFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.RadialTiltShiftFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.RainbowWorldFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.RandomDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.RedSwirlFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.RemoveColorFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ReplaceColorFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.RetroYellowFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.RingBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SandPaintingFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SaturationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SepiaFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SharpenFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ShuffleBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SideFadeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SierraDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SierraLiteDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SimpleOldTvFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SimpleSketchFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SimpleThresholdDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SketchFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SmoothToonFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SobelEdgeDetectionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SobelSimpleFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SoftEleganceFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SoftEleganceVariantFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SoftSpringLightFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SolarizeFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SpacePortalFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SpectralFireFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SphereRefractionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SpotHealFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.StackBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.StarBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.StrokePixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.StuckiDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SunriseFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.SwirlDistortionFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.TentBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ThresholdFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ToneCurvesFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ToonFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.TopHatFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.TriToneFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.TritanopiaFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.TritonomalyFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.TwoRowSierraDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.UchimuraFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.UnsharpFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.VibranceFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.VignetteFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.VintageFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.WarmFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.WaterEffectFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.WeakPixelFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.WhiteBalanceFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.YililomaDitheringFilter
import ru.tech.imageresizershrinker.feature.filters.data.model.ZoomBlurFilter
import javax.inject.Inject

internal class AndroidFilterProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val paletteTransferFilterFactory: PaletteTransferFilter.Factory,
    private val lutFilterFactory: LUT512x512Filter.Factory,
    private val paletteTransferVariantFilterFactory: PaletteTransferVariantFilter.Factory,
    private val spotHealFilterFactory: SpotHealFilter.Factory,
) : FilterProvider<Bitmap> {

    override fun filterToTransformation(
        filter: Filter<*>,
    ): Transformation<Bitmap> = if (filter.isVisible) {
        filter.run {
            when (this) {
                is Filter.BilaterialBlur -> BilaterialBlurFilter(value)
                is Filter.BlackAndWhite -> BlackAndWhiteFilter(context)
                is Filter.BoxBlur -> BoxBlurFilter(value)
                is Filter.Brightness -> BrightnessFilter(value)
                is Filter.BulgeDistortion -> BulgeDistortionFilter(context, value)
                is Filter.CGAColorSpace -> CGAColorSpaceFilter(context)
                is Filter.CirclePixelation -> CirclePixelationFilter(value)
                is Filter.ColorBalance -> ColorBalanceFilter(context, value)
                is Filter.Color -> ColorFilter(value)
                is Filter.ColorMatrix4x4 -> ColorMatrix4x4Filter(context, value)
                is Filter.Contrast -> ContrastFilter(value)
                is Filter.Convolution3x3 -> Convolution3x3Filter(value)
                is Filter.Crosshatch -> CrosshatchFilter(context, value)
                is Filter.DiamondPixelation -> DiamondPixelationFilter(value)
                is Filter.Dilation -> DilationFilter(value)
                is Filter.Emboss -> EmbossFilter(value)
                is Filter.EnhancedCirclePixelation -> EnhancedCirclePixelationFilter(value)
                is Filter.EnhancedDiamondPixelation -> EnhancedDiamondPixelationFilter(value)
                is Filter.EnhancedPixelation -> EnhancedPixelationFilter(value)
                is Filter.Exposure -> ExposureFilter(context, value)
                is Filter.FalseColor -> FalseColorFilter(context, value)
                is Filter.FastBlur -> FastBlurFilter(value)
                is Filter.Gamma -> GammaFilter(value)
                is Filter.GaussianBlur -> GaussianBlurFilter(value)
                is Filter.GlassSphereRefraction -> GlassSphereRefractionFilter(context, value)
                is Filter.Halftone -> HalftoneFilter(context, value)
                is Filter.Haze -> HazeFilter(context, value)
                is Filter.HighlightsAndShadows -> HighlightsAndShadowsFilter(context, value)
                is Filter.Hue -> HueFilter(context, value)
                is Filter.Kuwahara -> KuwaharaFilter(context, value)
                is Filter.Laplacian -> LaplacianFilter(context)
                is Filter.Lookup -> LookupFilter(context, value)
                is Filter.Monochrome -> MonochromeFilter(value)
                is Filter.Negative -> NegativeFilter(context)
                is Filter.NonMaximumSuppression -> NonMaximumSuppressionFilter(context)
                is Filter.Opacity -> OpacityFilter(context, value)
                is Filter.Pixelation -> PixelationFilter(value)
                is Filter.Posterize -> PosterizeFilter(context, value)
                is Filter.RemoveColor -> RemoveColorFilter(value)
                is Filter.ReplaceColor -> ReplaceColorFilter(value)
                is Filter.RGB -> RGBFilter(context, value)
                is Filter.Saturation -> SaturationFilter(value)
                is Filter.Sepia -> SepiaFilter(value)
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
                is Filter.Vibrance -> VibranceFilter(value)
                is Filter.Vignette -> VignetteFilter(context, value)
                is Filter.WeakPixel -> WeakPixelFilter(context)
                is Filter.WhiteBalance -> WhiteBalanceFilter(context, value)
                is Filter.ZoomBlur -> ZoomBlurFilter(context, value)
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
                is Filter.RadialTiltShift -> RadialTiltShiftFilter(value)
                is Filter.Glitch -> GlitchFilter(value)
                is Filter.Anaglyph -> AnaglyphFilter(value)
                is Filter.Noise -> NoiseFilter(value)
                is Filter.EnhancedGlitch -> EnhancedGlitchFilter(value)
                is Filter.TentBlur -> TentBlurFilter(value)
                is Filter.SideFade -> SideFadeFilter(value)
                is Filter.Erode -> ErodeFilter(value)
                is Filter.AnisotropicDiffusion -> AnisotropicDiffusionFilter(value)
                is Filter.HorizontalWindStagger -> HorizontalWindStaggerFilter(value)
                is Filter.FastBilaterialBlur -> FastBilaterialBlurFilter(value)
                is Filter.PoissonBlur -> PoissonBlurFilter(value)
                is Filter.LogarithmicToneMapping -> LogarithmicToneMappingFilter(value)
                is Filter.AcesFilmicToneMapping -> AcesFilmicToneMappingFilter(value)
                is Filter.Crystallize -> CrystallizeFilter(value)
                is Filter.FractalGlass -> FractalGlassFilter(value)
                is Filter.Marble -> MarbleFilter(value)
                is Filter.Oil -> OilFilter(value)
                is Filter.WaterEffect -> WaterEffectFilter(value)
                is Filter.PerlinDistortion -> PerlinDistortionFilter(value)
                is Filter.HableFilmicToneMapping -> HableFilmicToneMappingFilter(value)
                is Filter.AcesHillToneMapping -> AcesHillToneMappingFilter(value)
                is Filter.HejlBurgessToneMapping -> HejlBurgessToneMappingFilter(value)
                is Filter.Grayscale -> GrayscaleFilter(value)
                is Filter.Dehaze -> DehazeFilter(value)
                is Filter.Threshold -> ThresholdFilter(value)
                is Filter.ColorMatrix3x3 -> ColorMatrix3x3Filter(value)
                is Filter.Achromatomaly -> AchromatomalyFilter(value)
                is Filter.Achromatopsia -> AchromatopsiaFilter(value)
                is Filter.Browni -> BrowniFilter(value)
                is Filter.CodaChrome -> CodaChromeFilter(value)
                is Filter.Cool -> CoolFilter(value)
                is Filter.Deutaromaly -> DeutaromalyFilter(value)
                is Filter.Deutaronotopia -> DeutaronotopiaFilter(value)
                is Filter.NightVision -> NightVisionFilter(value)
                is Filter.Polaroid -> PolaroidFilter(value)
                is Filter.Protanopia -> ProtanopiaFilter(value)
                is Filter.Protonomaly -> ProtonomalyFilter(value)
                is Filter.Tritanopia -> TritanopiaFilter(value)
                is Filter.Tritonomaly -> TritonomalyFilter(value)
                is Filter.Vintage -> VintageFilter(value)
                is Filter.Warm -> WarmFilter(value)
                is Filter.Grain -> GrainFilter(value)
                is Filter.Unsharp -> UnsharpFilter(value)
                is Filter.Pastel -> PastelFilter(value)
                is Filter.OrangeHaze -> OrangeHazeFilter(value)
                is Filter.PinkDream -> PinkDreamFilter(value)
                is Filter.GoldenHour -> GoldenHourFilter(value)
                is Filter.HotSummer -> HotSummerFilter(value)
                is Filter.PurpleMist -> PurpleMistFilter(value)
                is Filter.Sunrise -> SunriseFilter(value)
                is Filter.ColorfulSwirl -> ColorfulSwirlFilter(value)
                is Filter.SoftSpringLight -> SoftSpringLightFilter(value)
                is Filter.AutumnTones -> AutumnTonesFilter(value)
                is Filter.LavenderDream -> LavenderDreamFilter(value)
                is Filter.Cyberpunk -> CyberpunkFilter(value)
                is Filter.LemonadeLight -> LemonadeLightFilter(value)
                is Filter.SpectralFire -> SpectralFireFilter(value)
                is Filter.NightMagic -> NightMagicFilter(value)
                is Filter.FantasyLandscape -> FantasyLandscapeFilter(value)
                is Filter.ColorExplosion -> ColorExplosionFilter(value)
                is Filter.ElectricGradient -> ElectricGradientFilter(value)
                is Filter.CaramelDarkness -> CaramelDarknessFilter(value)
                is Filter.FuturisticGradient -> FuturisticGradientFilter(value)
                is Filter.GreenSun -> GreenSunFilter(value)
                is Filter.RainbowWorld -> RainbowWorldFilter(value)
                is Filter.DeepPurple -> DeepPurpleFilter(value)
                is Filter.SpacePortal -> SpacePortalFilter(value)
                is Filter.RedSwirl -> RedSwirlFilter(value)
                is Filter.DigitalCode -> DigitalCodeFilter(value)
                is Filter.Bokeh -> BokehFilter(value)
                is Filter.Neon -> NeonFilter(context, value)
                is Filter.OldTv -> OldTvFilter(context, value)
                is Filter.ShuffleBlur -> ShuffleBlurFilter(value)
                is Filter.Mobius -> MobiusFilter(value)
                is Filter.Aldridge -> AldridgeFilter(value)
                is Filter.Drago -> DragoFilter(value)
                is Filter.Uchimura -> UchimuraFilter(value)
                is Filter.ColorAnomaly -> ColorAnomalyFilter(value)
                is Filter.Quantizier -> QuantizierFilter(value)
                is Filter.CircleBlur -> CircleBlurFilter(value)
                is Filter.CrossBlur -> CrossBlurFilter(value)
                is Filter.RingBlur -> RingBlurFilter(value)
                is Filter.StarBlur -> StarBlurFilter(value)
                is Filter.LinearTiltShift -> LinearTiltShiftFilter(value)
                is Filter.EnhancedZoomBlur -> EnhancedZoomBlurFilter(value)
                is Filter.Convex -> ConvexFilter(value)
                is Filter.FastGaussianBlur2D -> FastGaussianBlur2DFilter(value)
                is Filter.FastGaussianBlur3D -> FastGaussianBlur3DFilter(value)
                is Filter.FastGaussianBlur4D -> FastGaussianBlur4DFilter(value)
                is Filter.EqualizeHistogramHSV -> EqualizeHistogramHSVFilter(value)
                is Filter.EqualizeHistogram -> EqualizeHistogramFilter(value)
                is Filter.EqualizeHistogramPixelation -> EqualizeHistogramPixelationFilter(value)
                is Filter.EqualizeHistogramAdaptive -> EqualizeHistogramAdaptiveFilter(value)
                is Filter.EqualizeHistogramAdaptiveLUV -> EqualizeHistogramAdaptiveLUVFilter(value)
                is Filter.EqualizeHistogramAdaptiveLAB -> EqualizeHistogramAdaptiveLABFilter(value)
                is Filter.Clahe -> ClaheFilter(value)
                is Filter.ClaheLUV -> ClaheLUVFilter(value)
                is Filter.ClaheLAB -> ClaheLABFilter(value)
                is Filter.CropToContent -> CropToContentFilter(value)
                is Filter.ClaheHSL -> ClaheHSLFilter(value)
                is Filter.ClaheHSV -> ClaheHSVFilter(value)
                is Filter.EqualizeHistogramAdaptiveHSL -> EqualizeHistogramAdaptiveHSLFilter(value)
                is Filter.EqualizeHistogramAdaptiveHSV -> EqualizeHistogramAdaptiveHSVFilter(value)
                is Filter.LinearBoxBlur -> LinearBoxBlurFilter(value)
                is Filter.LinearStackBlur -> LinearStackBlurFilter(value)
                is Filter.LinearTentBlur -> LinearTentBlurFilter(value)
                is Filter.LinearGaussianBoxBlur -> LinearGaussianBoxBlurFilter(value)
                is Filter.GaussianBoxBlur -> GaussianBoxBlurFilter(value)
                is Filter.LinearFastGaussianBlurNext -> LinearFastGaussianBlurNextFilter(value)
                is Filter.LinearFastGaussianBlur -> LinearFastGaussianBlurFilter(value)
                is Filter.LinearGaussianBlur -> LinearGaussianBlurFilter(value)
                is Filter.LowPoly -> LowPolyFilter(value)
                is Filter.SandPainting -> SandPaintingFilter(value)
                is Filter.PaletteTransfer -> paletteTransferFilterFactory(value)
                is Filter.EnhancedOil -> EnhancedOilFilter(value)
                is Filter.SimpleOldTv -> SimpleOldTvFilter(value)
                is Filter.HDR -> HDRFilter(value)
                is Filter.Gotham -> GothamFilter(value)
                is Filter.SimpleSketch -> SimpleSketchFilter(value)
                is Filter.ColorPoster -> ColorPosterFilter(value)
                is Filter.TriTone -> TriToneFilter(value)
                is Filter.ClaheOklch -> ClaheOklchFilter(value)
                is Filter.ClaheOklab -> ClaheOklabFilter(value)
                is Filter.ClaheJzazbz -> ClaheJzazbzFilter(value)
                is Filter.PolkaDot -> PolkaDotFilter(value)
                is Filter.Clustered2x2Dithering -> Clustered2x2DitheringFilter(value)
                is Filter.Clustered4x4Dithering -> Clustered4x4DitheringFilter(value)
                is Filter.Clustered8x8Dithering -> Clustered8x8DitheringFilter(value)
                is Filter.YililomaDithering -> YililomaDitheringFilter(value)
                is Filter.LUT512x512 -> lutFilterFactory(value)
                is Filter.Amatorka -> AmatorkaFilter(value, lutFilterFactory)
                is Filter.MissEtikate -> MissEtikateFilter(value, lutFilterFactory)
                is Filter.SoftElegance -> SoftEleganceFilter(value, lutFilterFactory)
                is Filter.SoftEleganceVariant -> SoftEleganceVariantFilter(value, lutFilterFactory)
                is Filter.PaletteTransferVariant -> paletteTransferVariantFilterFactory(value)
                is Filter.CubeLut -> CubeLutFilter(context, value)
                is Filter.BleachBypass -> BleachBypassFilter(value, lutFilterFactory)
                is Filter.Candlelight -> CandlelightFilter(value, lutFilterFactory)
                is Filter.DropBlues -> DropBluesFilter(value, lutFilterFactory)
                is Filter.EdgyAmber -> EdgyAmberFilter(value, lutFilterFactory)
                is Filter.FallColors -> FallColorsFilter(value, lutFilterFactory)
                is Filter.FilmStock50 -> FilmStock50Filter(value, lutFilterFactory)
                is Filter.FoggyNight -> FoggyNightFilter(value, lutFilterFactory)
                is Filter.Kodak -> KodakFilter(value, lutFilterFactory)
                is Filter.PopArt -> PopArtFilter(value)
                is Filter.Celluloid -> CelluloidFilter(value, lutFilterFactory)
                is Filter.Coffee -> CoffeeFilter(value, lutFilterFactory)
                is Filter.GoldenForest -> GoldenForestFilter(value, lutFilterFactory)
                is Filter.Greenish -> GreenishFilter(value, lutFilterFactory)
                is Filter.RetroYellow -> RetroYellowFilter(value, lutFilterFactory)
                is Filter.AutoCrop -> AutoCropFilter(value)
                is Filter.SpotHeal -> spotHealFilterFactory(value)
                is Filter.Opening -> OpeningFilter(value)
                is Filter.Closing -> ClosingFilter(value)
                is Filter.MorphologicalGradient -> MorphologicalGradientFilter(value)
                is Filter.TopHat -> TopHatFilter(value)
                is Filter.BlackHat -> BlackHatFilter(value)
                is Filter.Canny -> CannyFilter(value)
                is Filter.SobelSimple -> SobelSimpleFilter(value)
                is Filter.LaplacianSimple -> LaplacianSimpleFilter(value)
                is Filter.MotionBlur -> MotionBlurFilter(value)
                is Filter.AutoRemoveRedEyes -> AutoRemoveRedEyesFilter(context, value)
                is Filter.ToneCurves -> ToneCurvesFilter(context, value)

                else -> throw IllegalArgumentException("No filter implementation for interface ${filter::class.simpleName}")
            }
        }
    } else {
        EmptyTransformation()
    }

}