package ru.tech.imageresizershrinker.core.data.image.filters

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil.size.Size
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import kotlin.math.pow
import kotlin.math.sqrt


class ReplaceColorFilter(
    override val value: Triple<Float, Color, Color> = Triple(
        first = 0f,
        second = Color(red = 0.0f, green = 0.0f, blue = 0.0f, alpha = 1.0f),
        third = Color(red = 1.0f, green = 1.0f, blue = 1.0f, alpha = 1.0f)
    ),
) : Filter.ReplaceColor<Bitmap, Color>, Transformation<Bitmap> {
    override val cacheKey: String
        get() = (value).hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: Size
    ): Bitmap = input.replaceColor(value.second, value.third, value.first)
}


private fun Bitmap.replaceColor(fromColor: Color, targetColor: Color, tolerance: Float): Bitmap {
    // Source image size
    val width = width
    val height = height
    val pixels = IntArray(width * height)
    //get pixels
    getPixels(pixels, 0, width, 0, 0, width, height)
    for (x in pixels.indices) {
        pixels[x] = if (Color(pixels[x]).distanceFrom(fromColor) <= tolerance) {
            targetColor.toArgb()
        } else pixels[x]
    }
    // create result bitmap output
    val result = Bitmap.createBitmap(width, height, config)
    //set pixels
    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

private fun Color.distanceFrom(color: Color): Float {
    return sqrt((red - color.red).pow(2) + (green - color.green).pow(2) + (blue - color.blue).pow(2))
}