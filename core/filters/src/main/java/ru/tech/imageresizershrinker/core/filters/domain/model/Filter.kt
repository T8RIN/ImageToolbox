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

    interface BilaterialBlur<Image> : Filter<Image, Float>
    interface BlackAndWhite<Image> : Filter<Image, Unit>
    interface BoxBlur<Image> : Filter<Image, Float>
    interface Brightness<Image> : Filter<Image, Float>
    interface BulgeDistortion<Image> : Filter<Image, Pair<Float, Float>>
    interface CGAColorSpace<Image> : Filter<Image, Unit>
    interface ColorBalance<Image> : Filter<Image, FloatArray>
    interface Color<Image, Color> : Filter<Image, Color>
    interface ColorMatrix<Image> : Filter<Image, FloatArray>
    interface Contrast<Image> : Filter<Image, Float>
    interface Convolution3x3<Image> : Filter<Image, FloatArray>
    interface Crosshatch<Image> : Filter<Image, Pair<Float, Float>>
    interface Dilation<Image> : Filter<Image, Float>
    interface Emboss<Image> : Filter<Image, Float>
    interface Exposure<Image> : Filter<Image, Float>
    interface FalseColor<Image, Color> : Filter<Image, Pair<Color, Color>>
    interface FastBlur<Image> : Filter<Image, Pair<Float, Int>>
    interface Gamma<Image> : Filter<Image, Float>
    interface GaussianBlur<Image> : Filter<Image, Float>
    interface GlassSphereRefraction<Image> : Filter<Image, Pair<Float, Float>>
    interface Halftone<Image> : Filter<Image, Float>
    interface Haze<Image> : Filter<Image, Pair<Float, Float>>
    interface HighlightsAndShadows<Image> : Filter<Image, Pair<Float, Float>>
    interface Hue<Image> : Filter<Image, Float>
    interface Kuwahara<Image> : Filter<Image, Float>
    interface Laplacian<Image> : Filter<Image, Unit>
    interface Lookup<Image> : Filter<Image, Float>
    interface LuminanceThreshold<Image> : Filter<Image, Float>
    interface Monochrome<Image> : Filter<Image, Float>
    interface Negative<Image> : Filter<Image, Unit>
    interface NonMaximumSuppression<Image> : Filter<Image, Unit>
    interface Opacity<Image> : Filter<Image, Float>
    interface Posterize<Image> : Filter<Image, Float>
    interface RGB<Image, Color> : Filter<Image, Color>
    interface Saturation<Image> : Filter<Image, Float>
    interface Sepia<Image> : Filter<Image, Float>
    interface Sharpen<Image> : Filter<Image, Float>
    interface Sketch<Image> : Filter<Image, Unit>
    interface SmoothToon<Image> : Filter<Image, Triple<Float, Float, Float>>
    interface SobelEdgeDetection<Image> : Filter<Image, Float>
    interface Solarize<Image> : Filter<Image, Float>
    interface SphereRefraction<Image> : Filter<Image, Pair<Float, Float>>
    interface StackBlur<Image> : Filter<Image, Pair<Float, Int>>
    interface SwirlDistortion<Image> : Filter<Image, Pair<Float, Float>>
    interface Toon<Image> : Filter<Image, Pair<Float, Float>>
    interface Vibrance<Image> : Filter<Image, Float>
    interface Vignette<Image, Color> : Filter<Image, Triple<Float, Float, Color>>
    interface WeakPixel<Image> : Filter<Image, Unit>
    interface WhiteBalance<Image> : Filter<Image, Pair<Float, Float>>
    interface ZoomBlur<Image> : Filter<Image, Triple<Float, Float, Float>>
    interface Pixelation<Image> : Filter<Image, Float>
    interface EnhancedPixelation<Image> : Filter<Image, Float>
    interface StrokePixelation<Image> : Filter<Image, Float>
    interface CirclePixelation<Image> : Filter<Image, Float>
    interface DiamondPixelation<Image> : Filter<Image, Float>
    interface EnhancedCirclePixelation<Image> : Filter<Image, Float>
    interface EnhancedDiamondPixelation<Image> : Filter<Image, Float>
    interface ReplaceColor<Image, Color> : Filter<Image, Triple<Float, Color, Color>>
    interface RemoveColor<Image, Color> : Filter<Image, Pair<Float, Color>>
    interface SideFade<Image> : Filter<Image, Pair<FadeSide, Int>>
    interface Quantizier<Image> : Filter<Image, Float>
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
}