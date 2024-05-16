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

    interface BilaterialBlur<Image> : Filter<Image, Triple<Float, Float, Float>>
    interface BlackAndWhite<Image> : SimpleFilter<Image>
    interface BoxBlur<Image> : Filter<Image, Float>
    interface Brightness<Image> : Filter<Image, Float>
    interface BulgeDistortion<Image> : Filter<Image, Pair<Float, Float>>
    interface CGAColorSpace<Image> : SimpleFilter<Image>
    interface ColorBalance<Image> : Filter<Image, FloatArray>
    interface Color<Image, Color : Any> : Filter<Image, FilterValueWrapper<Color>>
    interface ColorMatrix4x4<Image> : Filter<Image, FloatArray>
    interface Contrast<Image> : Filter<Image, Float>
    interface Convolution3x3<Image> : Filter<Image, FloatArray>
    interface Crosshatch<Image> : Filter<Image, Pair<Float, Float>>
    interface Dilation<Image> : Filter<Image, Float>
    interface Emboss<Image> : Filter<Image, Float>
    interface Exposure<Image> : Filter<Image, Float>
    interface FalseColor<Image, Color> : Filter<Image, Pair<Color, Color>>
    interface FastBlur<Image> : Filter<Image, Pair<Float, Int>>
    interface Gamma<Image> : Filter<Image, Float>
    interface GaussianBlur<Image> : Filter<Image, Pair<Float, Float>>
    interface GlassSphereRefraction<Image> : Filter<Image, Pair<Float, Float>>
    interface Halftone<Image> : Filter<Image, Float>
    interface Haze<Image> : Filter<Image, Pair<Float, Float>>
    interface HighlightsAndShadows<Image> : Filter<Image, Pair<Float, Float>>
    interface Hue<Image> : Filter<Image, Float>
    interface Kuwahara<Image> : Filter<Image, Float>
    interface Laplacian<Image> : SimpleFilter<Image>
    interface Lookup<Image> : Filter<Image, Float>
    interface Monochrome<Image, Color> : Filter<Image, Pair<Float, Color>>
    interface Negative<Image> : SimpleFilter<Image>
    interface NonMaximumSuppression<Image> : SimpleFilter<Image>
    interface Opacity<Image> : Filter<Image, Float>
    interface Posterize<Image> : Filter<Image, Float>
    interface RGB<Image, Color : Any> : Filter<Image, FilterValueWrapper<Color>>
    interface Saturation<Image> : Filter<Image, Float>
    interface Sepia<Image> : SimpleFilter<Image>
    interface Sharpen<Image> : Filter<Image, Float>
    interface Sketch<Image> : Filter<Image, Float>
    interface SmoothToon<Image> : Filter<Image, Triple<Float, Float, Float>>
    interface SobelEdgeDetection<Image> : Filter<Image, Float>
    interface Solarize<Image> : Filter<Image, Float>
    interface SphereRefraction<Image> : Filter<Image, Pair<Float, Float>>
    interface StackBlur<Image> : Filter<Image, Pair<Float, Int>>
    interface SwirlDistortion<Image> : Filter<Image, Pair<Float, Float>>
    interface Toon<Image> : Filter<Image, Pair<Float, Float>>
    interface Vibrance<Image> : Filter<Image, Float>
    interface Vignette<Image, Color> : Filter<Image, Triple<Float, Float, Color>>
    interface WeakPixel<Image> : SimpleFilter<Image>
    interface WhiteBalance<Image> : Filter<Image, Pair<Float, Float>>
    interface ZoomBlur<Image> : Filter<Image, Triple<Float, Float, Float>>
    interface Pixelation<Image> : Filter<Image, Float>
    interface EnhancedPixelation<Image> : Filter<Image, Float>
    interface StrokePixelation<Image, Color> : Filter<Image, Pair<Float, Color>>
    interface CirclePixelation<Image> : Filter<Image, Float>
    interface DiamondPixelation<Image> : Filter<Image, Float>
    interface EnhancedCirclePixelation<Image> : Filter<Image, Float>
    interface EnhancedDiamondPixelation<Image> : Filter<Image, Float>
    interface ReplaceColor<Image, Color> : Filter<Image, Triple<Float, Color, Color>>
    interface RemoveColor<Image, Color> : Filter<Image, Pair<Float, Color>>
    interface SideFade<Image> : Filter<Image, SideFadeParams>
    interface BayerTwoDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface BayerThreeDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface BayerFourDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface BayerEightDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface RandomDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface FloydSteinbergDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface JarvisJudiceNinkeDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface SierraDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface TwoRowSierraDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface SierraLiteDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface AtkinsonDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface StuckiDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface BurkesDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface FalseFloydSteinbergDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface LeftToRightDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface SimpleThresholdDithering<Image> : Filter<Image, Pair<Float, Boolean>>
    interface MedianBlur<Image> : Filter<Image, Pair<Float, Int>>
    interface NativeStackBlur<Image> : Filter<Image, Float>
    interface RadialTiltShift<Image> : Filter<Image, RadialTiltShiftParams>
    interface Glitch<Image> : Filter<Image, Triple<Float, Float, Float>>
    interface Noise<Image> : Filter<Image, Float>
    interface Anaglyph<Image> : Filter<Image, Float>
    interface EnhancedGlitch<Image> : Filter<Image, GlitchParams>
    interface TentBlur<Image> : Filter<Image, Float>
    interface Erode<Image> : Filter<Image, Float>
    interface AnisotropicDiffusion<Image> : Filter<Image, Triple<Int, Float, Float>>
    interface HorizontalWindStagger<Image, Color> : Filter<Image, Triple<Float, Int, Color>>
    interface FastBilaterialBlur<Image> : Filter<Image, Pair<Float, Float>>
    interface PoissonBlur<Image> : Filter<Image, Float>
    interface LogarithmicToneMapping<Image> : Filter<Image, Float>
    interface AcesFilmicToneMapping<Image> : Filter<Image, Float>
    interface Crystallize<Image, Color> : Filter<Image, Pair<Float, Color>>
    interface FractalGlass<Image> : Filter<Image, Pair<Float, Float>>
    interface Marble<Image> : Filter<Image, Triple<Float, Float, Float>>
    interface Oil<Image> : Filter<Image, Pair<Int, Float>>
    interface WaterEffect<Image> : Filter<Image, WaterParams>
    interface HableFilmicToneMapping<Image> : Filter<Image, Float>
    interface AcesHillToneMapping<Image> : Filter<Image, Float>
    interface HejlBurgessToneMapping<Image> : Filter<Image, Float>
    interface PerlinDistortion<Image> : Filter<Image, Triple<Float, Float, Float>>
    interface Grayscale<Image> : Filter<Image, Triple<Float, Float, Float>>
    interface Dehaze<Image> : Filter<Image, Pair<Int, Float>>
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
    interface Neon<Image, Color : Any> : Filter<Image, Triple<Float, Float, Color>>
    interface OldTv<Image> : SimpleFilter<Image>
    interface ShuffleBlur<Image> : Filter<Image, Pair<Int, Float>>
    interface Mobius<Image> : Filter<Image, Triple<Float, Float, Float>>
    interface Uchimura<Image> : Filter<Image, Float>
    interface Aldridge<Image> : Filter<Image, Pair<Float, Float>>
    interface Drago<Image> : Filter<Image, Pair<Float, Float>>
    interface ColorAnomaly<Image> : Filter<Image, Float>
    interface Quantizier<Image> : Filter<Image, Float>
    interface RingBlur<Image> : Filter<Image, Float>
    interface CrossBlur<Image> : Filter<Image, Float>
    interface CircleBlur<Image> : Filter<Image, Float>
    interface StarBlur<Image> : Filter<Image, Float>
    interface LinearTiltShift<Image> : Filter<Image, LinearTiltShiftParams>
    interface MotionBlur<Image> : Filter<Image, MotionBlurParams>
    interface Convex<Image> : Filter<Image, Float>
    interface FastGaussianBlur2D<Image> : Filter<Image, Float>
    interface FastGaussianBlur3D<Image> : Filter<Image, Float>
    interface FastGaussianBlur4D<Image> : Filter<Image, Float>
    interface EqualizeHistogram<Image> : SimpleFilter<Image>
    interface EqualizeHistogramHSV<Image> : SimpleFilter<Image>
}

interface SimpleFilter<Image> : Filter<Image, Unit>