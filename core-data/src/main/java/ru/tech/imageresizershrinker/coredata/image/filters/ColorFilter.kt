package ru.tech.imageresizershrinker.coredata.image.filters

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import coil.size.Size
import ru.tech.imageresizershrinker.coredomain.image.Transformation
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class ColorFilter(
    override val value: Color = Color.Transparent,
) : Filter.Color<Bitmap, Color>, Transformation<Bitmap> {
    override val cacheKey: String
        get() = (value).hashCode().toString()

    private val Bitmap.safeConfig: Bitmap.Config
        get() = config

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val output = createBitmap(input.width, input.height, input.safeConfig)

        val canvas = Canvas(output)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.colorFilter = PorterDuffColorFilter(value.toArgb(), PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(input, 0f, 0f, paint)

        return output
    }
}