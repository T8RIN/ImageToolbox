package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import coil.size.Size
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.R


data class ColorFilter(
    private val context: Context,
    override val value: Float = 0f,
) : FilterTransformation(
    context = context,
    title = R.string.color_filter,
    value = value,
    valueRange = 0f..0f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    private val Bitmap.safeConfig: Bitmap.Config
        get() = config ?: Bitmap.Config.ARGB_8888

    override fun createFilter(): GPUImageFilter = error("No filter")

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val output = createBitmap(input.width, input.height, input.safeConfig)

        val canvas = Canvas(output)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.colorFilter = PorterDuffColorFilter(value.toInt(), PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(input, 0f, 0f, paint)

        return output
    }
}