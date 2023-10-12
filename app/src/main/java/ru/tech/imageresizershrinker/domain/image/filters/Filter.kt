package ru.tech.imageresizershrinker.domain.image.filters

import ru.tech.imageresizershrinker.domain.image.Transformation

sealed interface Filter<IMAGE, VALUE> : Transformation<IMAGE> {
    val value: VALUE

    interface BilaterialBlur<IMAGE> : Filter<IMAGE, Float>
    interface BlackAndWhite<IMAGE> : Filter<IMAGE, Unit>
    interface BoxBlur<IMAGE> : Filter<IMAGE, Float>
    interface Brightness<IMAGE> : Filter<IMAGE, Float>
    interface BulgeDistortion<IMAGE> : Filter<IMAGE, Pair<Float, Float>>
    interface CGAColorSpace<IMAGE> : Filter<IMAGE, Unit>
    interface ColorBalance<IMAGE> : Filter<IMAGE, FloatArray>
    interface Color<IMAGE, COLOR> : Filter<IMAGE, COLOR>
    interface ColorMatrix<IMAGE> : Filter<IMAGE, FloatArray>
    interface Contrast<IMAGE> : Filter<IMAGE, Float>
    interface Convolution3x3<IMAGE> : Filter<IMAGE, FloatArray>
    interface Crosshatch<IMAGE> : Filter<IMAGE, Pair<Float, Float>>
    interface Dilation<IMAGE> : Filter<IMAGE, Float>
    interface Emboss<IMAGE> : Filter<IMAGE, Float>
    interface Exposure<IMAGE> : Filter<IMAGE, Float>
    interface FalseColor<IMAGE, COLOR> : Filter<IMAGE, Pair<COLOR, COLOR>>
    interface FastBlur<IMAGE> : Filter<IMAGE, Pair<Float, Int>>
    interface Gamma<IMAGE> : Filter<IMAGE, Float>
    interface GaussianBlur<IMAGE> : Filter<IMAGE, Float>
    interface GlassSphereRefraction<IMAGE> : Filter<IMAGE, Pair<Float, Float>>
    interface Halftone<IMAGE> : Filter<IMAGE, Float>
    interface Haze<IMAGE> : Filter<IMAGE, Pair<Float, Float>>
    interface HighlightsAndShadows<IMAGE> : Filter<IMAGE, Pair<Float, Float>>
    interface Hue<IMAGE> : Filter<IMAGE, Float>
    interface Kuwahara<IMAGE> : Filter<IMAGE, Float>
    interface Laplacian<IMAGE> : Filter<IMAGE, Unit>
    interface Lookup<IMAGE> : Filter<IMAGE, Float>
    interface LuminanceThreshold<IMAGE> : Filter<IMAGE, Float>
    interface Monochrome<IMAGE> : Filter<IMAGE, Float>
    interface Negative<IMAGE> : Filter<IMAGE, Unit>
    interface NonMaximumSuppression<IMAGE> : Filter<IMAGE, Unit>
    interface Opacity<IMAGE> : Filter<IMAGE, Float>
    interface Posterize<IMAGE> : Filter<IMAGE, Float>
    interface RGB<IMAGE, COLOR> : Filter<IMAGE, COLOR>
    interface Saturation<IMAGE> : Filter<IMAGE, Float>
    interface Sepia<IMAGE> : Filter<IMAGE, Float>
    interface Sharpen<IMAGE> : Filter<IMAGE, Float>
    interface Sketch<IMAGE> : Filter<IMAGE, Unit>
    interface SmoothToon<IMAGE> : Filter<IMAGE, Triple<Float, Float, Float>>
    interface SobelEdgeDetection<IMAGE> : Filter<IMAGE, Float>
    interface Solarize<IMAGE> : Filter<IMAGE, Float>
    interface SphereRefraction<IMAGE> : Filter<IMAGE, Pair<Float, Float>>
    interface StackBlur<IMAGE> : Filter<IMAGE, Pair<Float, Int>>
    interface SwirlDistortion<IMAGE> : Filter<IMAGE, Pair<Float, Float>>
    interface Toon<IMAGE> : Filter<IMAGE, Pair<Float, Float>>
    interface Vibrance<IMAGE> : Filter<IMAGE, Float>
    interface Vignette<IMAGE> : Filter<IMAGE, Pair<Float, Float>>
    interface WeakPixel<IMAGE> : Filter<IMAGE, Unit>
    interface WhiteBalance<IMAGE> : Filter<IMAGE, Pair<Float, Float>>
    interface ZoomBlur<IMAGE> : Filter<IMAGE, Triple<Float, Float, Float>>
    interface Pixelation<IMAGE> : Filter<IMAGE, Float>
}
