package ru.tech.imageresizershrinker.coredomain.image.filters


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
    interface Vignette<Image> : Filter<Image, Pair<Float, Float>>
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
}
