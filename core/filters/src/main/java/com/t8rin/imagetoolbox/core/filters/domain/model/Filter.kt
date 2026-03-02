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
import com.t8rin.imagetoolbox.core.domain.model.FileModel
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.VisibilityOwner
import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.MirrorSide
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PaletteTransferSpace
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PolarCoordinatesType
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PopArtBlendingMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.TransferFunc
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ArcParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AsciiParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BilaterialBlurParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BloomParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ChannelMixParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ClaheParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.CropOrPerspectiveParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.EnhancedZoomBlurParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GlitchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.KaleidoscopeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LinearGaussianParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LinearTiltShiftParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PinchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RadialTiltShiftParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RubberStampParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SideFadeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SmearParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SparkleParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ToneCurvesParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.VoronoiCrystallizeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.WaterParams


interface Filter<Value : Any> : VisibilityOwner {
    val value: Value

    interface BilaterialBlur : Filter<BilaterialBlurParams>
    interface BlackAndWhite : SimpleFilter
    interface BoxBlur : FloatFilter
    interface Brightness : FloatFilter
    interface BulgeDistortion : PairFloatFilter
    interface CGAColorSpace : SimpleFilter
    interface ColorBalance : FloatArrayFilter
    interface ColorOverlay : ColorValueFilter
    interface ColorMatrix4x4 : FloatArrayFilter
    interface Contrast : FloatFilter
    interface Convolution3x3 : FloatArrayFilter
    interface Crosshatch : PairFloatFilter
    interface Dilation : FloatBooleanFilter
    interface Emboss : FloatFilter
    interface Exposure : FloatFilter
    interface FalseColor : PairFilter<Color, Color>
    interface FastBlur : PairFloatFilter
    interface Gamma : FloatFilter
    interface GaussianBlur : TripleFilter<Float, Float, BlurEdgeMode>
    interface GlassSphereRefraction : PairFloatFilter
    interface Halftone : FloatFilter
    interface Haze : PairFloatFilter
    interface HighlightsAndShadows : FloatFilter
    interface Hue : FloatFilter
    interface Kuwahara : FloatFilter
    interface Laplacian : SimpleFilter
    interface Lookup : FloatFilter
    interface Monochrome : FloatColorModelFilter
    interface Negative : SimpleFilter
    interface NonMaximumSuppression : SimpleFilter
    interface Opacity : FloatFilter
    interface Posterize : FloatFilter
    interface RGB : ColorValueFilter
    interface Saturation : FloatBooleanFilter
    interface Sepia : SimpleFilter
    interface Sharpen : FloatFilter
    interface Sketch : FloatFilter
    interface SmoothToon : TripleFloatFilter
    interface SobelEdgeDetection : FloatFilter
    interface Solarize : FloatFilter
    interface SphereRefraction : PairFloatFilter
    interface StackBlur : PairFloatFilter
    interface SwirlDistortion : QuadFloatFilter
    interface Toon : PairFloatFilter
    interface Vibrance : FloatFilter
    interface Vignette : TripleFilter<Float, Float, Color>
    interface WeakPixel : SimpleFilter
    interface WhiteBalance : PairFloatFilter
    interface ZoomBlur : TripleFloatFilter
    interface Pixelation : FloatFilter
    interface EnhancedPixelation : FloatFilter
    interface StrokePixelation : FloatColorModelFilter
    interface CirclePixelation : FloatFilter
    interface DiamondPixelation : FloatFilter
    interface EnhancedCirclePixelation : FloatFilter
    interface EnhancedDiamondPixelation : FloatFilter
    interface ReplaceColor : TripleFilter<Float, Color, Color>
    interface RemoveColor : FloatColorModelFilter
    interface SideFade : Filter<SideFadeParams>
    interface BayerTwoDithering : FloatBooleanFilter
    interface BayerThreeDithering : FloatBooleanFilter
    interface BayerFourDithering : FloatBooleanFilter
    interface BayerEightDithering : FloatBooleanFilter
    interface RandomDithering : FloatBooleanFilter
    interface FloydSteinbergDithering : FloatBooleanFilter
    interface JarvisJudiceNinkeDithering : FloatBooleanFilter
    interface SierraDithering : FloatBooleanFilter
    interface TwoRowSierraDithering : FloatBooleanFilter
    interface SierraLiteDithering : FloatBooleanFilter
    interface AtkinsonDithering : FloatBooleanFilter
    interface StuckiDithering : FloatBooleanFilter
    interface BurkesDithering : FloatBooleanFilter
    interface FalseFloydSteinbergDithering : FloatBooleanFilter
    interface LeftToRightDithering : FloatBooleanFilter
    interface SimpleThresholdDithering : FloatBooleanFilter
    interface MedianBlur : FloatFilter
    interface NativeStackBlur : FloatFilter
    interface RadialTiltShift : Filter<RadialTiltShiftParams>
    interface Glitch : TripleFloatFilter
    interface Noise : FloatFilter
    interface Anaglyph : FloatFilter
    interface EnhancedGlitch : Filter<GlitchParams>
    interface TentBlur : FloatFilter
    interface Erode : FloatBooleanFilter
    interface AnisotropicDiffusion : TripleFloatFilter
    interface HorizontalWindStagger : TripleFilter<Float, Int, Color>
    interface FastBilaterialBlur : TripleFloatFilter
    interface PoissonBlur : FloatFilter
    interface LogarithmicToneMapping : FloatFilter
    interface AcesFilmicToneMapping : FloatFilter
    interface Crystallize : FloatColorModelFilter
    interface FractalGlass : PairFloatFilter
    interface Marble : TripleFloatFilter
    interface Oil : PairFloatFilter
    interface WaterEffect : Filter<WaterParams>
    interface HableFilmicToneMapping : FloatFilter
    interface AcesHillToneMapping : FloatFilter
    interface HejlBurgessToneMapping : FloatFilter
    interface PerlinDistortion : TripleFloatFilter
    interface Grayscale : TripleFloatFilter
    interface Dehaze : PairFloatFilter
    interface Threshold : FloatFilter
    interface ColorMatrix3x3 : FloatArrayFilter
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
    interface Grain : FloatFilter
    interface Unsharp : FloatFilter
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
    interface Bokeh : PairFloatFilter
    interface Neon : TripleFilter<Float, Float, Color>
    interface OldTv : SimpleFilter
    interface ShuffleBlur : PairFloatFilter
    interface Mobius : TripleFloatFilter
    interface Uchimura : FloatFilter
    interface Aldridge : PairFloatFilter
    interface Drago : PairFloatFilter
    interface ColorAnomaly : FloatFilter
    interface Quantizier : FloatFilter
    interface RingBlur : FloatFilter
    interface CrossBlur : FloatFilter
    interface CircleBlur : FloatFilter
    interface StarBlur : FloatFilter
    interface LinearTiltShift : Filter<LinearTiltShiftParams>
    interface EnhancedZoomBlur : Filter<EnhancedZoomBlurParams>
    interface Convex : FloatFilter
    interface FastGaussianBlur2D : PairFilter<Float, BlurEdgeMode>
    interface FastGaussianBlur3D : PairFilter<Float, BlurEdgeMode>
    interface FastGaussianBlur4D : FloatFilter
    interface EqualizeHistogram : SimpleFilter
    interface EqualizeHistogramHSV : FloatFilter
    interface EqualizeHistogramPixelation : PairFloatFilter
    interface EqualizeHistogramAdaptive : PairFloatFilter
    interface EqualizeHistogramAdaptiveLUV : TripleFloatFilter
    interface EqualizeHistogramAdaptiveLAB : TripleFloatFilter
    interface Clahe : TripleFloatFilter
    interface ClaheLUV : ClaheValueFilter
    interface ClaheLAB : ClaheValueFilter
    interface CropToContent : FloatColorModelFilter
    interface ClaheHSL : ClaheValueFilter
    interface ClaheHSV : ClaheValueFilter
    interface EqualizeHistogramAdaptiveHSV : TripleFloatFilter
    interface EqualizeHistogramAdaptiveHSL : TripleFloatFilter
    interface LinearBoxBlur : PairFilter<Int, TransferFunc>
    interface LinearTentBlur : PairFilter<Float, TransferFunc>
    interface LinearGaussianBoxBlur : PairFilter<Float, TransferFunc>
    interface LinearStackBlur : PairFilter<Int, TransferFunc>
    interface GaussianBoxBlur : FloatFilter
    interface LinearFastGaussianBlurNext : TripleFilter<Int, TransferFunc, BlurEdgeMode>
    interface LinearFastGaussianBlur : TripleFilter<Int, TransferFunc, BlurEdgeMode>
    interface LinearGaussianBlur : Filter<LinearGaussianParams>
    interface LowPoly : FloatBooleanFilter
    interface SandPainting : TripleFilter<Int, Int, Color>
    interface PaletteTransfer : FileImageFilter
    interface EnhancedOil : FloatFilter
    interface SimpleOldTv : SimpleFilter
    interface HDR : SimpleFilter
    interface SimpleSketch : SimpleFilter
    interface Gotham : SimpleFilter
    interface ColorPoster : FloatColorModelFilter
    interface TriTone : TripleFilter<Color, Color, Color>
    interface ClaheOklch : ClaheValueFilter
    interface ClaheJzazbz : ClaheValueFilter
    interface ClaheOklab : ClaheValueFilter
    interface PolkaDot : TripleFilter<Int, Int, Color>
    interface Clustered2x2Dithering : BooleanFilter
    interface Clustered4x4Dithering : BooleanFilter
    interface Clustered8x8Dithering : BooleanFilter
    interface YililomaDithering : BooleanFilter
    interface LUT512x512 : FileImageFilter
    interface Amatorka : FloatFilter
    interface MissEtikate : FloatFilter
    interface SoftElegance : FloatFilter
    interface SoftEleganceVariant : FloatFilter
    interface PaletteTransferVariant : TripleFilter<Float, PaletteTransferSpace, Image>
    interface CubeLut : FileFilter
    interface BleachBypass : FloatFilter
    interface Candlelight : FloatFilter
    interface DropBlues : FloatFilter
    interface EdgyAmber : FloatFilter
    interface FallColors : FloatFilter
    interface FilmStock50 : FloatFilter
    interface FoggyNight : FloatFilter
    interface Kodak : FloatFilter
    interface PopArt : TripleFilter<Float, Color, PopArtBlendingMode>
    interface Celluloid : FloatFilter
    interface Coffee : FloatFilter
    interface GoldenForest : FloatFilter
    interface Greenish : FloatFilter
    interface RetroYellow : FloatFilter
    interface AutoCrop : FloatFilter
    interface SpotHeal : PairFilter<Image, SpotHealMode>
    interface Opening : FloatBooleanFilter
    interface Closing : FloatBooleanFilter
    interface MorphologicalGradient : FloatBooleanFilter
    interface TopHat : FloatBooleanFilter
    interface BlackHat : FloatBooleanFilter
    interface Canny : PairFloatFilter
    interface SobelSimple : SimpleFilter
    interface LaplacianSimple : SimpleFilter
    interface MotionBlur : TripleFilter<Int, Float, BlurEdgeMode>
    interface AutoRemoveRedEyes : FloatFilter
    interface ToneCurves : Filter<ToneCurvesParams>
    interface Mirror : PairFilter<Float, MirrorSide>
    interface Kaleidoscope : Filter<KaleidoscopeParams>
    interface ChannelMix : Filter<ChannelMixParams>
    interface ColorHalftone : QuadFloatFilter
    interface Contour : QuadFilter<Float, Float, Float, Color>
    interface VoronoiCrystallize : Filter<VoronoiCrystallizeParams>
    interface Despeckle : SimpleFilter
    interface Diffuse : FloatFilter
    interface DoG : PairFloatFilter
    interface Equalize : SimpleFilter
    interface Glow : FloatFilter
    interface Offset : PairFloatFilter
    interface Pinch : Filter<PinchParams>
    interface Pointillize : Filter<VoronoiCrystallizeParams>
    interface PolarCoordinates : Filter<PolarCoordinatesType>
    interface ReduceNoise : SimpleFilter
    interface SimpleSolarize : SimpleFilter
    interface Weave : QuadFloatFilter
    interface Twirl : QuadFloatFilter
    interface RubberStamp : Filter<RubberStampParams>
    interface Smear : Filter<SmearParams>
    interface SphereLensDistortion : QuadFloatFilter
    interface Arc : Filter<ArcParams>
    interface Sparkle : Filter<SparkleParams>
    interface Ascii : Filter<AsciiParams>
    interface Moire : SimpleFilter
    interface Autumn : SimpleFilter
    interface Bone : SimpleFilter
    interface Jet : SimpleFilter
    interface Winter : SimpleFilter
    interface Rainbow : SimpleFilter
    interface Ocean : SimpleFilter
    interface Summer : SimpleFilter
    interface Spring : SimpleFilter
    interface CoolVariant : SimpleFilter
    interface Hsv : SimpleFilter
    interface Pink : SimpleFilter
    interface Hot : SimpleFilter
    interface Parula : SimpleFilter
    interface Magma : SimpleFilter
    interface Inferno : SimpleFilter
    interface Plasma : SimpleFilter
    interface Viridis : SimpleFilter
    interface Cividis : SimpleFilter
    interface Twilight : SimpleFilter
    interface TwilightShifted : SimpleFilter
    interface AutoPerspective : SimpleFilter
    interface Deskew : FloatBooleanFilter
    interface CropOrPerspective : Filter<CropOrPerspectiveParams>
    interface Turbo : SimpleFilter
    interface DeepGreen : SimpleFilter
    interface LensCorrection : FileFilter
    interface SeamCarving : Filter<IntegerSize>
    interface ErrorLevelAnalysis : FloatFilter
    interface LuminanceGradient : SimpleFilter
    interface AverageDistance : SimpleFilter
    interface CopyMoveDetection : PairFloatFilter
    interface SimpleWeavePixelation : FloatFilter
    interface StaggeredPixelation : FloatFilter
    interface CrossPixelation : FloatFilter
    interface MicroMacroPixelation : FloatFilter
    interface OrbitalPixelation : FloatFilter
    interface VortexPixelation : FloatFilter
    interface PulseGridPixelation : FloatFilter
    interface NucleusPixelation : FloatFilter
    interface RadialWeavePixelation : FloatFilter
    interface BorderFrame : TripleFilter<Float, Float, Color>
    interface GlitchVariant : TripleFloatFilter
    interface VHS : PairFloatFilter
    interface BlockGlitch : PairFloatFilter
    interface CrtCurvature : TripleFloatFilter
    interface PixelMelt : PairFloatFilter
    interface Bloom : Filter<BloomParams>
}

interface SimpleFilter : Filter<Unit>
interface PairFilter<Value1, Value2> : Filter<Pair<Value1, Value2>>
interface TripleFilter<Value1, Value2, Value3> : Filter<Triple<Value1, Value2, Value3>>
interface QuadFilter<Value1, Value2, Value3, Value4> : Filter<Quad<Value1, Value2, Value3, Value4>>

interface FloatFilter : Filter<Float>
interface PairFloatFilter : PairFilter<Float, Float>
interface TripleFloatFilter : TripleFilter<Float, Float, Float>
interface QuadFloatFilter : QuadFilter<Float, Float, Float, Float>

interface FloatArrayFilter : Filter<FloatArray>
interface FileFilter : PairFilter<Float, File>
interface FileImageFilter : PairFilter<Float, Image>
interface FloatBooleanFilter : PairFilter<Float, Boolean>
interface FloatColorModelFilter : PairFilter<Float, Color>
interface BooleanFilter : Filter<Boolean>
interface ClaheValueFilter : Filter<ClaheParams>
interface ColorValueFilter : WrapperFilter<Color>

interface WrapperFilter<Wrapped : Any> : Filter<FilterValueWrapper<Wrapped>>

private typealias Image = ImageModel
private typealias File = FileModel
private typealias Color = ColorModel