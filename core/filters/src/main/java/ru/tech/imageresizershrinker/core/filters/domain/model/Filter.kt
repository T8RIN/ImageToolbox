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

package ru.tech.imageresizershrinker.core.filters.domain.model


interface Filter<Image, Value> {
    val value: Value

    interface BilaterialBlur<Image> : TripleFilter<Image, Float, Float, Float>
    interface BlackAndWhite<Image> : SimpleFilter<Image>
    interface BoxBlur<Image> : Filter<Image, Float>
    interface Brightness<Image> : Filter<Image, Float>
    interface BulgeDistortion<Image> : PairFilter<Image, Float, Float>
    interface CGAColorSpace<Image> : SimpleFilter<Image>
    interface ColorBalance<Image> : Filter<Image, FloatArray>
    interface Color<Image, Color : Any> : WrapperFilter<Image, Color>
    interface ColorMatrix4x4<Image> : Filter<Image, FloatArray>
    interface Contrast<Image> : Filter<Image, Float>
    interface Convolution3x3<Image> : Filter<Image, FloatArray>
    interface Crosshatch<Image> : PairFilter<Image, Float, Float>
    interface Dilation<Image> : Filter<Image, Float>
    interface Emboss<Image> : Filter<Image, Float>
    interface Exposure<Image> : Filter<Image, Float>
    interface FalseColor<Image, Color> : PairFilter<Image, Color, Color>
    interface FastBlur<Image> : PairFilter<Image, Float, Int>
    interface Gamma<Image> : Filter<Image, Float>
    interface GaussianBlur<Image> : TripleFilter<Image, Float, Float, BlurEdgeMode>
    interface GlassSphereRefraction<Image> : PairFilter<Image, Float, Float>
    interface Halftone<Image> : Filter<Image, Float>
    interface Haze<Image> : PairFilter<Image, Float, Float>
    interface HighlightsAndShadows<Image> : PairFilter<Image, Float, Float>
    interface Hue<Image> : Filter<Image, Float>
    interface Kuwahara<Image> : Filter<Image, Float>
    interface Laplacian<Image> : SimpleFilter<Image>
    interface Lookup<Image> : Filter<Image, Float>
    interface Monochrome<Image, Color> : PairFilter<Image, Float, Color>
    interface Negative<Image> : SimpleFilter<Image>
    interface NonMaximumSuppression<Image> : SimpleFilter<Image>
    interface Opacity<Image> : Filter<Image, Float>
    interface Posterize<Image> : Filter<Image, Float>
    interface RGB<Image, Color : Any> : WrapperFilter<Image, Color>
    interface Saturation<Image> : Filter<Image, Float>
    interface Sepia<Image> : SimpleFilter<Image>
    interface Sharpen<Image> : Filter<Image, Float>
    interface Sketch<Image> : Filter<Image, Float>
    interface SmoothToon<Image> : TripleFilter<Image, Float, Float, Float>
    interface SobelEdgeDetection<Image> : Filter<Image, Float>
    interface Solarize<Image> : Filter<Image, Float>
    interface SphereRefraction<Image> : PairFilter<Image, Float, Float>
    interface StackBlur<Image> : PairFilter<Image, Float, Int>
    interface SwirlDistortion<Image> : PairFilter<Image, Float, Float>
    interface Toon<Image> : PairFilter<Image, Float, Float>
    interface Vibrance<Image> : Filter<Image, Float>
    interface Vignette<Image, Color> : TripleFilter<Image, Float, Float, Color>
    interface WeakPixel<Image> : SimpleFilter<Image>
    interface WhiteBalance<Image> : PairFilter<Image, Float, Float>
    interface ZoomBlur<Image> : TripleFilter<Image, Float, Float, Float>
    interface Pixelation<Image> : Filter<Image, Float>
    interface EnhancedPixelation<Image> : Filter<Image, Float>
    interface StrokePixelation<Image, Color> : PairFilter<Image, Float, Color>
    interface CirclePixelation<Image> : Filter<Image, Float>
    interface DiamondPixelation<Image> : Filter<Image, Float>
    interface EnhancedCirclePixelation<Image> : Filter<Image, Float>
    interface EnhancedDiamondPixelation<Image> : Filter<Image, Float>
    interface ReplaceColor<Image, Color> : TripleFilter<Image, Float, Color, Color>
    interface RemoveColor<Image, Color> : PairFilter<Image, Float, Color>
    interface SideFade<Image> : Filter<Image, SideFadeParams>
    interface BayerTwoDithering<Image> : PairFilter<Image, Float, Boolean>
    interface BayerThreeDithering<Image> : PairFilter<Image, Float, Boolean>
    interface BayerFourDithering<Image> : PairFilter<Image, Float, Boolean>
    interface BayerEightDithering<Image> : PairFilter<Image, Float, Boolean>
    interface RandomDithering<Image> : PairFilter<Image, Float, Boolean>
    interface FloydSteinbergDithering<Image> : PairFilter<Image, Float, Boolean>
    interface JarvisJudiceNinkeDithering<Image> : PairFilter<Image, Float, Boolean>
    interface SierraDithering<Image> : PairFilter<Image, Float, Boolean>
    interface TwoRowSierraDithering<Image> : PairFilter<Image, Float, Boolean>
    interface SierraLiteDithering<Image> : PairFilter<Image, Float, Boolean>
    interface AtkinsonDithering<Image> : PairFilter<Image, Float, Boolean>
    interface StuckiDithering<Image> : PairFilter<Image, Float, Boolean>
    interface BurkesDithering<Image> : PairFilter<Image, Float, Boolean>
    interface FalseFloydSteinbergDithering<Image> : PairFilter<Image, Float, Boolean>
    interface LeftToRightDithering<Image> : PairFilter<Image, Float, Boolean>
    interface SimpleThresholdDithering<Image> : PairFilter<Image, Float, Boolean>
    interface MedianBlur<Image> : PairFilter<Image, Float, Int>
    interface NativeStackBlur<Image> : Filter<Image, Float>
    interface RadialTiltShift<Image> : Filter<Image, RadialTiltShiftParams>
    interface Glitch<Image> : TripleFilter<Image, Float, Float, Float>
    interface Noise<Image> : Filter<Image, Float>
    interface Anaglyph<Image> : Filter<Image, Float>
    interface EnhancedGlitch<Image> : Filter<Image, GlitchParams>
    interface TentBlur<Image> : Filter<Image, Float>
    interface Erode<Image> : Filter<Image, Float>
    interface AnisotropicDiffusion<Image> : TripleFilter<Image, Int, Float, Float>
    interface HorizontalWindStagger<Image, Color> : TripleFilter<Image, Float, Int, Color>
    interface FastBilaterialBlur<Image> : PairFilter<Image, Float, Float>
    interface PoissonBlur<Image> : Filter<Image, Float>
    interface LogarithmicToneMapping<Image> : Filter<Image, Float>
    interface AcesFilmicToneMapping<Image> : Filter<Image, Float>
    interface Crystallize<Image, Color> : PairFilter<Image, Float, Color>
    interface FractalGlass<Image> : PairFilter<Image, Float, Float>
    interface Marble<Image> : TripleFilter<Image, Float, Float, Float>
    interface Oil<Image> : PairFilter<Image, Int, Float>
    interface WaterEffect<Image> : Filter<Image, WaterParams>
    interface HableFilmicToneMapping<Image> : Filter<Image, Float>
    interface AcesHillToneMapping<Image> : Filter<Image, Float>
    interface HejlBurgessToneMapping<Image> : Filter<Image, Float>
    interface PerlinDistortion<Image> : TripleFilter<Image, Float, Float, Float>
    interface Grayscale<Image> : TripleFilter<Image, Float, Float, Float>
    interface Dehaze<Image> : PairFilter<Image, Int, Float>
    interface Threshold<Image> : Filter<Image, Float>
    interface ColorMatrix3x3<Image> : Filter<Image, FloatArray>
    interface Polaroid<Image> : SimpleFilter<Image>
    interface Cool<Image> : SimpleFilter<Image>
    interface Warm<Image> : SimpleFilter<Image>
    interface NightVision<Image> : SimpleFilter<Image>
    interface CodaChrome<Image> : SimpleFilter<Image>
    interface Browni<Image> : SimpleFilter<Image>
    interface Vintage<Image> : SimpleFilter<Image>
    interface Protonomaly<Image> : SimpleFilter<Image>
    interface Deutaromaly<Image> : SimpleFilter<Image>
    interface Tritonomaly<Image> : SimpleFilter<Image>
    interface Protanopia<Image> : SimpleFilter<Image>
    interface Deutaronotopia<Image> : SimpleFilter<Image>
    interface Tritanopia<Image> : SimpleFilter<Image>
    interface Achromatopsia<Image> : SimpleFilter<Image>
    interface Achromatomaly<Image> : SimpleFilter<Image>
    interface Grain<Image> : Filter<Image, Float>
    interface Unsharp<Image> : Filter<Image, Float>
    interface Pastel<Image> : SimpleFilter<Image>
    interface OrangeHaze<Image> : SimpleFilter<Image>
    interface PinkDream<Image> : SimpleFilter<Image>
    interface GoldenHour<Image> : SimpleFilter<Image>
    interface HotSummer<Image> : SimpleFilter<Image>
    interface PurpleMist<Image> : SimpleFilter<Image>
    interface Sunrise<Image> : SimpleFilter<Image>
    interface ColorfulSwirl<Image> : SimpleFilter<Image>
    interface SoftSpringLight<Image> : SimpleFilter<Image>
    interface AutumnTones<Image> : SimpleFilter<Image>
    interface LavenderDream<Image> : SimpleFilter<Image>
    interface Cyberpunk<Image> : SimpleFilter<Image>
    interface LemonadeLight<Image> : SimpleFilter<Image>
    interface SpectralFire<Image> : SimpleFilter<Image>
    interface NightMagic<Image> : SimpleFilter<Image>
    interface FantasyLandscape<Image> : SimpleFilter<Image>
    interface ColorExplosion<Image> : SimpleFilter<Image>
    interface ElectricGradient<Image> : SimpleFilter<Image>
    interface CaramelDarkness<Image> : SimpleFilter<Image>
    interface FuturisticGradient<Image> : SimpleFilter<Image>
    interface GreenSun<Image> : SimpleFilter<Image>
    interface RainbowWorld<Image> : SimpleFilter<Image>
    interface DeepPurple<Image> : SimpleFilter<Image>
    interface SpacePortal<Image> : SimpleFilter<Image>
    interface RedSwirl<Image> : SimpleFilter<Image>
    interface DigitalCode<Image> : SimpleFilter<Image>
    interface Bokeh<Image> : Filter<Image, BokehParams>
    interface Neon<Image, Color : Any> : TripleFilter<Image, Float, Float, Color>
    interface OldTv<Image> : SimpleFilter<Image>
    interface ShuffleBlur<Image> : PairFilter<Image, Int, Float>
    interface Mobius<Image> : TripleFilter<Image, Float, Float, Float>
    interface Uchimura<Image> : Filter<Image, Float>
    interface Aldridge<Image> : PairFilter<Image, Float, Float>
    interface Drago<Image> : PairFilter<Image, Float, Float>
    interface ColorAnomaly<Image> : Filter<Image, Float>
    interface Quantizier<Image> : Filter<Image, Float>
    interface RingBlur<Image> : Filter<Image, Float>
    interface CrossBlur<Image> : Filter<Image, Float>
    interface CircleBlur<Image> : Filter<Image, Float>
    interface StarBlur<Image> : Filter<Image, Float>
    interface LinearTiltShift<Image> : Filter<Image, LinearTiltShiftParams>
    interface MotionBlur<Image> : Filter<Image, MotionBlurParams>
    interface Convex<Image> : Filter<Image, Float>
    interface FastGaussianBlur2D<Image> : PairFilter<Image, Float, BlurEdgeMode>
    interface FastGaussianBlur3D<Image> : PairFilter<Image, Float, BlurEdgeMode>
    interface FastGaussianBlur4D<Image> : Filter<Image, Float>
    interface EqualizeHistogram<Image> : SimpleFilter<Image>
    interface EqualizeHistogramHSV<Image> : Filter<Image, Int>
    interface EqualizeHistogramPixelation<Image> : PairFilter<Image, Int, Int>
    interface EqualizeHistogramAdaptive<Image> : PairFilter<Image, Int, Int>
    interface EqualizeHistogramAdaptiveLUV<Image> : TripleFilter<Image, Int, Int, Int>
    interface EqualizeHistogramAdaptiveLAB<Image> : TripleFilter<Image, Int, Int, Int>
    interface Clahe<Image> : TripleFilter<Image, Float, Int, Int>
    interface ClaheLUV<Image> : Filter<Image, ClaheParams>
    interface ClaheLAB<Image> : Filter<Image, ClaheParams>
    interface CropToContent<Image, Color : Any> : PairFilter<Image, Float, Color>
    interface ClaheHSL<Image> : Filter<Image, ClaheParams>
    interface ClaheHSV<Image> : Filter<Image, ClaheParams>
    interface EqualizeHistogramAdaptiveHSV<Image> : TripleFilter<Image, Int, Int, Int>
    interface EqualizeHistogramAdaptiveHSL<Image> : TripleFilter<Image, Int, Int, Int>
    interface LinearBoxBlur<Image> : PairFilter<Image, Int, TransferFunc>
    interface LinearTentBlur<Image> : PairFilter<Image, Int, TransferFunc>
    interface LinearGaussianBoxBlur<Image> : PairFilter<Image, Int, TransferFunc>
    interface LinearStackBlur<Image> : PairFilter<Image, Int, TransferFunc>
    interface GaussianBoxBlur<Image> : Filter<Image, Int>
    interface LinearFastGaussianNext<Image> : TripleFilter<Image, Int, TransferFunc, BlurEdgeMode>
    interface LinearFastGaussian<Image> : TripleFilter<Image, Int, TransferFunc, BlurEdgeMode>
    interface LinearGaussian<Image> : Filter<Image, LinearGaussianParams>
}

interface SimpleFilter<Image> : Filter<Image, Unit>
interface PairFilter<Image, Value1, Value2> : Filter<Image, Pair<Value1, Value2>>
interface TripleFilter<Image, Value1, Value2, Value3> :
    Filter<Image, Triple<Value1, Value2, Value3>>

interface WrapperFilter<Image, Wrapped : Any> : Filter<Image, FilterValueWrapper<Wrapped>>