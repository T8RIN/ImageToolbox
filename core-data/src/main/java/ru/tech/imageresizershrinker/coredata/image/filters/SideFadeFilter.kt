package ru.tech.imageresizershrinker.coredata.image.filters

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil.size.Size
import ru.tech.imageresizershrinker.coredomain.image.Transformation
import ru.tech.imageresizershrinker.coredomain.image.filters.FadeSide
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class SideFadeFilter(
    override val value: Pair<FadeSide, Int> = FadeSide.Start to 60
) : Transformation<Bitmap>, Filter.SideFade<Bitmap> {
    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val bitmap = input.copy(input.config, true).apply { setHasAlpha(true) }
        val canvas = Canvas(bitmap)
        canvas.drawPaint(value.first.getPaint(input, value.second))
        return bitmap
    }

    private fun FadeSide.getPaint(bmp: Bitmap, length: Int): Paint {
        val paint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        }

        val width = bmp.width.toFloat()
        val height = bmp.height.toFloat()

        val sideLength = length.toFloat()
        val g1x: Float
        val g1y: Float
        val g2x: Float
        val g2y: Float
        val startColor: Int
        val endColor: Int
        when (this) {
            FadeSide.Start -> {
                //left
                g1x = 0f
                g1y = height / 2
                g2x = sideLength
                g2y = height / 2
                startColor = Color.Transparent.toArgb()
                endColor = Color.Black.toArgb()
            }

            FadeSide.Top -> {
                //top
                g1x = width / 2
                g1y = 0f
                g2x = width / 2
                g2y = sideLength
                startColor = Color.Transparent.toArgb()
                endColor = Color.Black.toArgb()
            }

            FadeSide.End -> {
                //right
                g1x = width
                g1y = height / 2
                g2x = width - sideLength
                g2y = height / 2
                startColor = Color.Transparent.toArgb()
                endColor = Color.Black.toArgb()
            }

            FadeSide.Bottom -> {
                //bottom
                g1x = width / 2
                g1y = height
                g2x = width / 2
                g2y = height - sideLength
                startColor = Color.Transparent.toArgb()
                endColor = Color.Black.toArgb()
            }
        }
        paint.setShader(
            LinearGradient(
                g1x,
                g1y,
                g2x,
                g2y,
                startColor,
                endColor,
                Shader.TileMode.CLAMP
            )
        )
        return paint
    }

}