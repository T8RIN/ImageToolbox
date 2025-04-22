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

import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.domain.model.FileModel
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.domain.model.VisibilityOwner


interface Filter<Value> : VisibilityOwner {
    val value: Value

    interface BilaterialBlur : TripleFilter<Float, Float, Float>
    interface BlackAndWhite : SimpleFilter
    interface BoxBlur : Filter<Float>
    interface Brightness : Filter<Float>
    interface BulgeDistortion : PairFilter<Float, Float>
    interface CGAColorSpace : SimpleFilter
    interface ColorBalance : Filter<FloatArray>
    interface Color : WrapperFilter<ColorModel>
    interface ColorMatrix4x4 : Filter<FloatArray>
    interface Contrast : Filter<Float>
    interface Convolution3x3 : Filter<FloatArray>
    interface Crosshatch : PairFilter<Float, Float>
    interface Dilation : PairFilter<Float, Boolean>
    interface Emboss : Filter<Float>
    interface Exposure : Filter<Float>
    interface FalseColor : PairFilter<ColorModel, ColorModel>
    interface FastBlur : PairFilter<Float, Int>
    interface Gamma : Filter<Float>
    interface GaussianBlur : TripleFilter<Float, Float, BlurEdgeMode>
    interface GlassSphereRefraction : PairFilter<Float, Float>
    interface Halftone : Filter<Float>
    interface Haze : PairFilter<Float, Float>
    interface HighlightsAndShadows : Filter<Float>
    interface Hue : Filter<Float>
    interface Kuwahara : Filter<Float>
    interface Laplacian : SimpleFilter
    interface Lookup : Filter<Float>
    interface Monochrome : PairFilter<Float, ColorModel>
    interface Negative : SimpleFilter
    interface NonMaximumSuppression : SimpleFilter
    interface Opacity : Filter<Float>
    interface Posterize : Filter<Float>
    interface RGB : WrapperFilter<ColorModel>
    interface Saturation : PairFilter<Float, Boolean>
    interface Sepia : SimpleFilter
    interface Sharpen : Filter<Float>
    interface Sketch : Filter<Float>
    interface SmoothToon : TripleFilter<Float, Float, Float>
    interface SobelEdgeDetection : Filter<Float>
    interface Solarize : Filter<Float>
    interface SphereRefraction : PairFilter<Float, Float>
    interface StackBlur : PairFilter<Float, Int>
    interface SwirlDistortion : PairFilter<Float, Float>
    interface Toon : PairFilter<Float, Float>
    interface Vibrance : Filter<Float>
    interface Vignette : TripleFilter<Float, Float, ColorModel>
    interface WeakPixel : SimpleFilter
    interface WhiteBalance : PairFilter<Float, Float>
    interface ZoomBlur : TripleFilter<Float, Float, Float>
    interface Pixelation : Filter<Float>
    interface EnhancedPixelation : Filter<Float>
    interface StrokePixelation : PairFilter<Float, ColorModel>
    interface CirclePixelation : Filter<Float>
    interface DiamondPixelation : Filter<Float>
    interface EnhancedCirclePixelation : Filter<Float>
    interface EnhancedDiamondPixelation : Filter<Float>
    interface ReplaceColor : TripleFilter<Float, ColorModel, ColorModel>
    interface RemoveColor : PairFilter<Float, ColorModel>
    interface SideFade : Filter<SideFadeParams>
    interface BayerTwoDithering : PairFilter<Float, Boolean>
    interface BayerThreeDithering : PairFilter<Float, Boolean>
    interface BayerFourDithering : PairFilter<Float, Boolean>
    interface BayerEightDithering : PairFilter<Float, Boolean>
    interface RandomDithering : PairFilter<Float, Boolean>
    interface FloydSteinbergDithering : PairFilter<Float, Boolean>
    interface JarvisJudiceNinkeDithering : PairFilter<Float, Boolean>
    interface SierraDithering : PairFilter<Float, Boolean>
    interface TwoRowSierraDithering : PairFilter<Float, Boolean>
    interface SierraLiteDithering : PairFilter<Float, Boolean>
    interface AtkinsonDithering : PairFilter<Float, Boolean>
    interface StuckiDithering : PairFilter<Float, Boolean>
    interface BurkesDithering : PairFilter<Float, Boolean>
    interface FalseFloydSteinbergDithering : PairFilter<Float, Boolean>
    interface LeftToRightDithering : PairFilter<Float, Boolean>
    interface SimpleThresholdDithering : PairFilter<Float, Boolean>
    interface MedianBlur : Filter<Float>
    interface NativeStackBlur : Filter<Float>
    interface RadialTiltShift : Filter<RadialTiltShiftParams>
    interface Glitch : TripleFilter<Float, Float, Float>
    interface Noise : Filter<Float>
    interface Anaglyph : Filter<Float>
    interface EnhancedGlitch : Filter<GlitchParams>
    interface TentBlur : Filter<Float>
    interface Erode : PairFilter<Float, Boolean>
    interface AnisotropicDiffusion : TripleFilter<Int, Float, Float>
    interface HorizontalWindStagger : TripleFilter<Float, Int, ColorModel>
    interface FastBilaterialBlur : TripleFilter<Int, Float, Float>
    interface PoissonBlur : Filter<Float>
    interface LogarithmicToneMapping : Filter<Float>
    interface AcesFilmicToneMapping : Filter<Float>
    interface Crystallize : PairFilter<Float, ColorModel>
    interface FractalGlass : PairFilter<Float, Float>
    interface Marble : TripleFilter<Float, Float, Float>
    interface Oil : PairFilter<Int, Float>
    interface WaterEffect : Filter<WaterParams>
    interface HableFilmicToneMapping : Filter<Float>
    interface AcesHillToneMapping : Filter<Float>
    interface HejlBurgessToneMapping : Filter<Float>
    interface PerlinDistortion : TripleFilter<Float, Float, Float>
    interface Grayscale : TripleFilter<Float, Float, Float>
    interface Dehaze : PairFilter<Int, Float>
    interface Threshold : Filter<Float>
    interface ColorMatrix3x3 : Filter<FloatArray>
    interface Polaroid : SimpleFilter
    interface Cool : SimpleFilter
    interface Warm : SimpleFilter
    interface NightVision : SimpleFilter
    interface CodaChrome : SimpleFilter
    interface Browni : SimpleFilter
    interface Vintage : SimpleFilter
    interface Protonomaly : SimpleFilter
    interface Deutaromaly : SimpleFilter
    interface Tritonomaly : SimpleFilter
    interface Protanopia : SimpleFilter
    interface Deutaronotopia : SimpleFilter
    interface Tritanopia : SimpleFilter
    interface Achromatopsia : SimpleFilter
    interface Achromatomaly : SimpleFilter
    interface Grain : Filter<Float>
    interface Unsharp : Filter<Float>
    interface Pastel : SimpleFilter
    interface OrangeHaze : SimpleFilter
    interface PinkDream : SimpleFilter
    interface GoldenHour : SimpleFilter
    interface HotSummer : SimpleFilter
    interface PurpleMist : SimpleFilter
    interface Sunrise : SimpleFilter
    interface ColorfulSwirl : SimpleFilter
    interface SoftSpringLight : SimpleFilter
    interface AutumnTones : SimpleFilter
    interface LavenderDream : SimpleFilter
    interface Cyberpunk : SimpleFilter
    interface LemonadeLight : SimpleFilter
    interface SpectralFire : SimpleFilter
    interface NightMagic : SimpleFilter
    interface FantasyLandscape : SimpleFilter
    interface ColorExplosion : SimpleFilter
    interface ElectricGradient : SimpleFilter
    interface CaramelDarkness : SimpleFilter
    interface FuturisticGradient : SimpleFilter
    interface GreenSun : SimpleFilter
    interface RainbowWorld : SimpleFilter
    interface DeepPurple : SimpleFilter
    interface SpacePortal : SimpleFilter
    interface RedSwirl : SimpleFilter
    interface DigitalCode : SimpleFilter
    interface Bokeh : PairFilter<Int, Int>
    interface Neon : TripleFilter<Float, Float, ColorModel>
    interface OldTv : SimpleFilter
    interface ShuffleBlur : PairFilter<Int, Float>
    interface Mobius : TripleFilter<Float, Float, Float>
    interface Uchimura : Filter<Float>
    interface Aldridge : PairFilter<Float, Float>
    interface Drago : PairFilter<Float, Float>
    interface ColorAnomaly : Filter<Float>
    interface Quantizier : Filter<Float>
    interface RingBlur : Filter<Float>
    interface CrossBlur : Filter<Float>
    interface CircleBlur : Filter<Float>
    interface StarBlur : Filter<Float>
    interface LinearTiltShift : Filter<LinearTiltShiftParams>
    interface EnhancedZoomBlur : Filter<EnhancedZoomBlurParams>
    interface Convex : Filter<Float>
    interface FastGaussianBlur2D : PairFilter<Float, BlurEdgeMode>
    interface FastGaussianBlur3D : PairFilter<Float, BlurEdgeMode>
    interface FastGaussianBlur4D : Filter<Float>
    interface EqualizeHistogram : SimpleFilter
    interface EqualizeHistogramHSV : Filter<Float>
    interface EqualizeHistogramPixelation : PairFilter<Int, Int>
    interface EqualizeHistogramAdaptive : PairFilter<Int, Int>
    interface EqualizeHistogramAdaptiveLUV : TripleFilter<Int, Int, Int>
    interface EqualizeHistogramAdaptiveLAB : TripleFilter<Int, Int, Int>
    interface Clahe : TripleFilter<Float, Int, Int>
    interface ClaheLUV : Filter<ClaheParams>
    interface ClaheLAB : Filter<ClaheParams>
    interface CropToContent : PairFilter<Float, ColorModel>
    interface ClaheHSL : Filter<ClaheParams>
    interface ClaheHSV : Filter<ClaheParams>
    interface EqualizeHistogramAdaptiveHSV : TripleFilter<Int, Int, Int>
    interface EqualizeHistogramAdaptiveHSL : TripleFilter<Int, Int, Int>
    interface LinearBoxBlur : PairFilter<Int, TransferFunc>
    interface LinearTentBlur : PairFilter<Float, TransferFunc>
    interface LinearGaussianBoxBlur : PairFilter<Float, TransferFunc>
    interface LinearStackBlur : PairFilter<Int, TransferFunc>
    interface GaussianBoxBlur : Filter<Float>
    interface LinearFastGaussianBlurNext : TripleFilter<Int, TransferFunc, BlurEdgeMode>
    interface LinearFastGaussianBlur : TripleFilter<Int, TransferFunc, BlurEdgeMode>
    interface LinearGaussianBlur : Filter<LinearGaussianParams>
    interface LowPoly : PairFilter<Int, Boolean>
    interface SandPainting : TripleFilter<Int, Int, ColorModel>
    interface PaletteTransfer : PairFilter<Float, ImageModel>
    interface EnhancedOil : Filter<Float>
    interface SimpleOldTv : SimpleFilter
    interface HDR : SimpleFilter
    interface SimpleSketch : SimpleFilter
    interface Gotham : SimpleFilter
    interface ColorPoster : PairFilter<Float, ColorModel>
    interface TriTone : TripleFilter<ColorModel, ColorModel, ColorModel>
    interface ClaheOklch : Filter<ClaheParams>
    interface ClaheJzazbz : Filter<ClaheParams>
    interface ClaheOklab : Filter<ClaheParams>
    interface PolkaDot : TripleFilter<Int, Int, ColorModel>
    interface Clustered2x2Dithering : Filter<Boolean>
    interface Clustered4x4Dithering : Filter<Boolean>
    interface Clustered8x8Dithering : Filter<Boolean>
    interface YililomaDithering : Filter<Boolean>
    interface LUT512x512 : PairFilter<Float, ImageModel>
    interface Amatorka : Filter<Float>
    interface MissEtikate : Filter<Float>
    interface SoftElegance : Filter<Float>
    interface SoftEleganceVariant : Filter<Float>
    interface PaletteTransferVariant : TripleFilter<Float, PaletteTransferSpace, ImageModel>
    interface CubeLut : PairFilter<Float, FileModel>
    interface BleachBypass : Filter<Float>
    interface Candlelight : Filter<Float>
    interface DropBlues : Filter<Float>
    interface EdgyAmber : Filter<Float>
    interface FallColors : Filter<Float>
    interface FilmStock50 : Filter<Float>
    interface FoggyNight : Filter<Float>
    interface Kodak : Filter<Float>
    interface PopArt : TripleFilter<Float, ColorModel, PopArtBlendingMode>
    interface Celluloid : Filter<Float>
    interface Coffee : Filter<Float>
    interface GoldenForest : Filter<Float>
    interface Greenish : Filter<Float>
    interface RetroYellow : Filter<Float>
    interface AutoCrop : Filter<Float>
    interface SpotHeal : TripleFilter<ImageModel, Float, Int>
    interface Opening : PairFilter<Float, Boolean>
    interface Closing : PairFilter<Float, Boolean>
    interface MorphologicalGradient : PairFilter<Float, Boolean>
    interface TopHat : PairFilter<Float, Boolean>
    interface BlackHat : PairFilter<Float, Boolean>
    interface Canny : PairFilter<Float, Float>
    interface SobelSimple : SimpleFilter
    interface LaplacianSimple : SimpleFilter
    interface MotionBlur : TripleFilter<Int, Float, BlurEdgeMode>
    interface AutoRemoveRedEyes : Filter<Float>
    interface ToneCurves : Filter<ToneCurvesParams>
}

interface SimpleFilter : Filter<Unit>
interface PairFilter<Value1, Value2> : Filter<Pair<Value1, Value2>>
interface TripleFilter<Value1, Value2, Value3> :
    Filter<Triple<Value1, Value2, Value3>>

interface WrapperFilter<Wrapped : Any> : Filter<FilterValueWrapper<Wrapped>>