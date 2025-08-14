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

package com.t8rin.imagetoolbox.feature.filters.data

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.model.ifVisible
import com.t8rin.imagetoolbox.core.domain.transformation.EmptyTransformation
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.FilterProvider
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.feature.filters.data.model.AcesFilmicToneMappingFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AcesHillToneMappingFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AchromatomalyFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AchromatopsiaFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AldridgeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AmatorkaFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AnaglyphFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AnisotropicDiffusionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ArcFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AsciiFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AtkinsonDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AutoCropFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AutoPerspectiveFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AutoRemoveRedEyesFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AutumnFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AutumnTonesFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BayerEightDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BayerFourDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BayerThreeDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BayerTwoDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BilaterialBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BlackAndWhiteFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BlackHatFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BleachBypassFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BokehFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BoneFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BoxBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BrightnessFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BrowniFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BulgeDistortionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BurkesDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CGAColorSpaceFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CandlelightFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CannyFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CaramelDarknessFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CelluloidFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ChannelMixFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CircleBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CirclePixelationFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CividisFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ClaheFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ClaheHSLFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ClaheHSVFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ClaheJzazbzFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ClaheLABFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ClaheLUVFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ClaheOklabFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ClaheOklchFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ClosingFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.Clustered2x2DitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.Clustered4x4DitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.Clustered8x8DitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CodaChromeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CoffeeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ColorAnomalyFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ColorBalanceFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ColorExplosionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ColorHalftoneFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ColorMatrix3x3Filter
import com.t8rin.imagetoolbox.feature.filters.data.model.ColorMatrix4x4Filter
import com.t8rin.imagetoolbox.feature.filters.data.model.ColorOverlayFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ColorPosterFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ColorfulSwirlFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ContourFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ContrastFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ConvexFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.Convolution3x3Filter
import com.t8rin.imagetoolbox.feature.filters.data.model.CoolFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CoolVariantFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CropOrPerspectiveFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CropToContentFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CrossBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CrosshatchFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CrystallizeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CubeLutFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.CyberpunkFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DeepGreenFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DeepPurpleFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DehazeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DeskewFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DespeckleFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DeutaromalyFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DeutaronotopiaFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DiamondPixelationFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DiffuseFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DigitalCodeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DilationFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DoGFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DragoFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.DropBluesFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EdgyAmberFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ElectricGradientFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EmbossFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EnhancedCirclePixelationFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EnhancedDiamondPixelationFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EnhancedGlitchFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EnhancedOilFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EnhancedPixelationFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EnhancedZoomBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EqualizeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EqualizeHistogramAdaptiveFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EqualizeHistogramAdaptiveHSLFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EqualizeHistogramAdaptiveHSVFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EqualizeHistogramAdaptiveLABFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EqualizeHistogramAdaptiveLUVFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EqualizeHistogramFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EqualizeHistogramHSVFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.EqualizeHistogramPixelationFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ErodeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ExposureFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FallColorsFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FalseColorFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FalseFloydSteinbergDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FantasyLandscapeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FastBilaterialBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FastBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FastGaussianBlur2DFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FastGaussianBlur3DFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FastGaussianBlur4DFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FilmStock50Filter
import com.t8rin.imagetoolbox.feature.filters.data.model.FloydSteinbergDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FoggyNightFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FractalGlassFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.FuturisticGradientFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GammaFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GaussianBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GaussianBoxBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GlassSphereRefractionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GlitchFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GlowFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GoldenForestFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GoldenHourFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GothamFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GrainFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GrayscaleFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GreenSunFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GreenishFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.HDRFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.HableFilmicToneMappingFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.HalftoneFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.HazeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.HejlBurgessToneMappingFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.HighlightsAndShadowsFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.HorizontalWindStaggerFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.HotFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.HotSummerFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.HsvFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.HueFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.InfernoFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.JarvisJudiceNinkeDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.JetFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.KaleidoscopeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.KodakFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.KuwaharaFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LUT512x512Filter
import com.t8rin.imagetoolbox.feature.filters.data.model.LaplacianFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LaplacianSimpleFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LavenderDreamFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LeftToRightDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LemonadeLightFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LensCorrectionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LinearBoxBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LinearFastGaussianBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LinearFastGaussianBlurNextFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LinearGaussianBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LinearGaussianBoxBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LinearStackBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LinearTentBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LinearTiltShiftFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LogarithmicToneMappingFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LookupFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.LowPolyFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.MagmaFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.MarbleFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.MedianBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.MirrorFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.MissEtikateFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.MobiusFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.MoireFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.MonochromeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.MorphologicalGradientFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.MotionBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.NativeStackBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.NegativeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.NeonFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.NightMagicFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.NightVisionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.NoiseFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.NonMaximumSuppressionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.OceanFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.OffsetFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.OilFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.OldTvFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.OpacityFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.OpeningFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.OrangeHazeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PaletteTransferFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PaletteTransferVariantFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ParulaFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PastelFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PerlinDistortionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PinchFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PinkDreamFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PinkFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PixelationFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PlasmaFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PointillizeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PoissonBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PolarCoordinatesFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PolaroidFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PolkaDotFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PopArtFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PosterizeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ProtanopiaFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ProtonomalyFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.PurpleMistFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.QuantizierFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.RGBFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.RadialTiltShiftFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.RainbowFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.RainbowWorldFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.RandomDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.RedSwirlFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ReduceNoiseFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.RemoveColorFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ReplaceColorFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.RetroYellowFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.RingBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.RubberStampFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SandPaintingFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SaturationFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SepiaFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SharpenFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ShuffleBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SideFadeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SierraDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SierraLiteDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SimpleOldTvFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SimpleSketchFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SimpleSolarizeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SimpleThresholdDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SketchFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SmearFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SmoothToonFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SobelEdgeDetectionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SobelSimpleFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SoftEleganceFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SoftEleganceVariantFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SoftSpringLightFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SolarizeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SpacePortalFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SparkleFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SpectralFireFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SphereLensDistortionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SphereRefractionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SpotHealFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SpringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.StackBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.StarBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.StrokePixelationFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.StuckiDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SummerFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SunriseFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SwirlDistortionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.TentBlurFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ThresholdFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ToneCurvesFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ToonFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.TopHatFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.TriToneFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.TritanopiaFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.TritonomalyFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.TurboFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.TwilightFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.TwilightShiftedFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.TwirlFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.TwoRowSierraDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.UchimuraFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.UnsharpFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.VibranceFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.VignetteFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.VintageFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ViridisFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.VoronoiCrystallizeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.WarmFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.WaterEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.WeakPixelFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.WeaveFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.WhiteBalanceFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.WinterFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.YililomaDitheringFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ZoomBlurFilter
import javax.inject.Inject

internal class AndroidFilterProvider @Inject constructor() : FilterProvider<Bitmap> {

    override fun filterToTransformation(
        filter: Filter<*>,
    ): Transformation<Bitmap> = filter.ifVisible {
        when (this) {
            is Filter.BilaterialBlur -> BilaterialBlurFilter(value)
            is Filter.BlackAndWhite -> BlackAndWhiteFilter(value)
            is Filter.BoxBlur -> BoxBlurFilter(value)
            is Filter.Brightness -> BrightnessFilter(value)
            is Filter.BulgeDistortion -> BulgeDistortionFilter(value)
            is Filter.CGAColorSpace -> CGAColorSpaceFilter(value)
            is Filter.CirclePixelation -> CirclePixelationFilter(value)
            is Filter.ColorBalance -> ColorBalanceFilter(value)
            is Filter.ColorOverlay -> ColorOverlayFilter(value)
            is Filter.ColorMatrix4x4 -> ColorMatrix4x4Filter(value)
            is Filter.Contrast -> ContrastFilter(value)
            is Filter.Convolution3x3 -> Convolution3x3Filter(value)
            is Filter.Crosshatch -> CrosshatchFilter(value)
            is Filter.DiamondPixelation -> DiamondPixelationFilter(value)
            is Filter.Dilation -> DilationFilter(value)
            is Filter.Emboss -> EmbossFilter(value)
            is Filter.EnhancedCirclePixelation -> EnhancedCirclePixelationFilter(value)
            is Filter.EnhancedDiamondPixelation -> EnhancedDiamondPixelationFilter(value)
            is Filter.EnhancedPixelation -> EnhancedPixelationFilter(value)
            is Filter.Exposure -> ExposureFilter(value)
            is Filter.FalseColor -> FalseColorFilter(value)
            is Filter.FastBlur -> FastBlurFilter(value)
            is Filter.Gamma -> GammaFilter(value)
            is Filter.GaussianBlur -> GaussianBlurFilter(value)
            is Filter.GlassSphereRefraction -> GlassSphereRefractionFilter(value)
            is Filter.Halftone -> HalftoneFilter(value)
            is Filter.Haze -> HazeFilter(value)
            is Filter.HighlightsAndShadows -> HighlightsAndShadowsFilter(value)
            is Filter.Hue -> HueFilter(value)
            is Filter.Kuwahara -> KuwaharaFilter(value)
            is Filter.Laplacian -> LaplacianFilter(value)
            is Filter.Lookup -> LookupFilter(value)
            is Filter.Monochrome -> MonochromeFilter(value)
            is Filter.Negative -> NegativeFilter(value)
            is Filter.NonMaximumSuppression -> NonMaximumSuppressionFilter(value)
            is Filter.Opacity -> OpacityFilter(value)
            is Filter.Pixelation -> PixelationFilter(value)
            is Filter.Posterize -> PosterizeFilter(value)
            is Filter.RemoveColor -> RemoveColorFilter(value)
            is Filter.ReplaceColor -> ReplaceColorFilter(value)
            is Filter.RGB -> RGBFilter(value)
            is Filter.Saturation -> SaturationFilter(value)
            is Filter.Sepia -> SepiaFilter(value)
            is Filter.Sharpen -> SharpenFilter(value)
            is Filter.Sketch -> SketchFilter(value)
            is Filter.SmoothToon -> SmoothToonFilter(value)
            is Filter.SobelEdgeDetection -> SobelEdgeDetectionFilter(value)
            is Filter.Solarize -> SolarizeFilter(value)
            is Filter.SphereRefraction -> SphereRefractionFilter(value)
            is Filter.StackBlur -> StackBlurFilter(value)
            is Filter.StrokePixelation -> StrokePixelationFilter(value)
            is Filter.SwirlDistortion -> SwirlDistortionFilter(value)
            is Filter.Toon -> ToonFilter(value)
            is Filter.Vibrance -> VibranceFilter(value)
            is Filter.Vignette -> VignetteFilter(value)
            is Filter.WeakPixel -> WeakPixelFilter(value)
            is Filter.WhiteBalance -> WhiteBalanceFilter(value)
            is Filter.ZoomBlur -> ZoomBlurFilter(value)
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
            is Filter.Neon -> NeonFilter(value)
            is Filter.OldTv -> OldTvFilter(value)
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
            is Filter.PaletteTransfer -> PaletteTransferFilter(value)
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
            is Filter.LUT512x512 -> LUT512x512Filter(value)
            is Filter.Amatorka -> AmatorkaFilter(value)
            is Filter.MissEtikate -> MissEtikateFilter(value)
            is Filter.SoftElegance -> SoftEleganceFilter(value)
            is Filter.SoftEleganceVariant -> SoftEleganceVariantFilter(value)
            is Filter.PaletteTransferVariant -> PaletteTransferVariantFilter(value)
            is Filter.CubeLut -> CubeLutFilter(value)
            is Filter.BleachBypass -> BleachBypassFilter(value)
            is Filter.Candlelight -> CandlelightFilter(value)
            is Filter.DropBlues -> DropBluesFilter(value)
            is Filter.EdgyAmber -> EdgyAmberFilter(value)
            is Filter.FallColors -> FallColorsFilter(value)
            is Filter.FilmStock50 -> FilmStock50Filter(value)
            is Filter.FoggyNight -> FoggyNightFilter(value)
            is Filter.Kodak -> KodakFilter(value)
            is Filter.PopArt -> PopArtFilter(value)
            is Filter.Celluloid -> CelluloidFilter(value)
            is Filter.Coffee -> CoffeeFilter(value)
            is Filter.GoldenForest -> GoldenForestFilter(value)
            is Filter.Greenish -> GreenishFilter(value)
            is Filter.RetroYellow -> RetroYellowFilter(value)
            is Filter.AutoCrop -> AutoCropFilter(value)
            is Filter.SpotHeal -> SpotHealFilter(value)
            is Filter.Opening -> OpeningFilter(value)
            is Filter.Closing -> ClosingFilter(value)
            is Filter.MorphologicalGradient -> MorphologicalGradientFilter(value)
            is Filter.TopHat -> TopHatFilter(value)
            is Filter.BlackHat -> BlackHatFilter(value)
            is Filter.Canny -> CannyFilter(value)
            is Filter.SobelSimple -> SobelSimpleFilter(value)
            is Filter.LaplacianSimple -> LaplacianSimpleFilter(value)
            is Filter.MotionBlur -> MotionBlurFilter(value)
            is Filter.AutoRemoveRedEyes -> AutoRemoveRedEyesFilter(value)
            is Filter.ToneCurves -> ToneCurvesFilter(value)
            is Filter.Mirror -> MirrorFilter(value)
            is Filter.Kaleidoscope -> KaleidoscopeFilter(value)
            is Filter.ChannelMix -> ChannelMixFilter(value)
            is Filter.ColorHalftone -> ColorHalftoneFilter(value)
            is Filter.Contour -> ContourFilter(value)
            is Filter.VoronoiCrystallize -> VoronoiCrystallizeFilter(value)
            is Filter.Despeckle -> DespeckleFilter(value)
            is Filter.Diffuse -> DiffuseFilter(value)
            is Filter.DoG -> DoGFilter(value)
            is Filter.Equalize -> EqualizeFilter(value)
            is Filter.Glow -> GlowFilter(value)
            is Filter.Offset -> OffsetFilter(value)
            is Filter.Pinch -> PinchFilter(value)
            is Filter.Pointillize -> PointillizeFilter(value)
            is Filter.PolarCoordinates -> PolarCoordinatesFilter(value)
            is Filter.ReduceNoise -> ReduceNoiseFilter(value)
            is Filter.SimpleSolarize -> SimpleSolarizeFilter(value)
            is Filter.Weave -> WeaveFilter(value)
            is Filter.Twirl -> TwirlFilter(value)
            is Filter.RubberStamp -> RubberStampFilter(value)
            is Filter.Smear -> SmearFilter(value)
            is Filter.SphereLensDistortion -> SphereLensDistortionFilter(value)
            is Filter.Arc -> ArcFilter(value)
            is Filter.Sparkle -> SparkleFilter(value)
            is Filter.Ascii -> AsciiFilter(value)
            is Filter.Moire -> MoireFilter(value)
            is Filter.Autumn -> AutumnFilter(value)
            is Filter.Bone -> BoneFilter(value)
            is Filter.Jet -> JetFilter(value)
            is Filter.Winter -> WinterFilter(value)
            is Filter.Rainbow -> RainbowFilter(value)
            is Filter.Ocean -> OceanFilter(value)
            is Filter.Summer -> SummerFilter(value)
            is Filter.Spring -> SpringFilter(value)
            is Filter.CoolVariant -> CoolVariantFilter(value)
            is Filter.Hsv -> HsvFilter(value)
            is Filter.Pink -> PinkFilter(value)
            is Filter.Hot -> HotFilter(value)
            is Filter.Parula -> ParulaFilter(value)
            is Filter.Magma -> MagmaFilter(value)
            is Filter.Inferno -> InfernoFilter(value)
            is Filter.Plasma -> PlasmaFilter(value)
            is Filter.Viridis -> ViridisFilter(value)
            is Filter.Cividis -> CividisFilter(value)
            is Filter.Twilight -> TwilightFilter(value)
            is Filter.TwilightShifted -> TwilightShiftedFilter(value)
            is Filter.AutoPerspective -> AutoPerspectiveFilter(value)
            is Filter.Deskew -> DeskewFilter(value)
            is Filter.CropOrPerspective -> CropOrPerspectiveFilter(value)
            is Filter.Turbo -> TurboFilter(value)
            is Filter.DeepGreen -> DeepGreenFilter(value)
            is Filter.LensCorrection -> LensCorrectionFilter(value)

            else -> throw IllegalArgumentException("No filter implementation for interface ${filter::class.simpleName}")
        }
    } ?: EmptyTransformation()

}