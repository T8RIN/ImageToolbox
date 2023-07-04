package ru.tech.imageresizershrinker.presentation.root.model.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import coil.size.Size
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.core.android.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.domain.model.ResizeType
import kotlin.math.roundToInt


@Parcelize
class FastBlurFilter(
    private val context: @RawValue Context,
    override val value: Pair<Float, Int> = 0.5f to 25,
) : FilterTransformation<Pair<Float, Int>>(
    context = context,
    title = R.string.fast_blur,
    value = value,
    paramsInfo = listOf(
        FilterParam(R.string.scale, 0.01f..1f, 2),
        FilterParam(R.string.radius, 0f..100f, 0)
    )
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()


    override fun createFilter(): GPUImageFilter = error("Not present")

    override suspend fun transform(
        input: Bitmap,
        size: Size
    ): Bitmap = input.fastBlur(value.first, value.second)

}

private fun Bitmap.fastBlur(scale: Float, radius: Int): Bitmap {
    var sentBitmap = this@fastBlur
    val width = (sentBitmap.width * scale).roundToInt()
    val height = (sentBitmap.height * scale).roundToInt()
    sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false)
    val bitmap = sentBitmap.copy(sentBitmap.config, true)
    if (radius < 1) {
        return this
    }
    val w = bitmap.width
    val h = bitmap.height
    val wm = w - 1
    val hm = h - 1
    val wh = w * h
    val div = radius + radius + 1
    val r = IntArray(wh)
    val g = IntArray(wh)
    val b = IntArray(wh)
    var rsum: Int
    var gsum: Int
    var bsum: Int
    var x: Int
    var y: Int
    var i: Int
    var p: Int
    var p1: Int
    var p2: Int
    var yp: Int
    var yi: Int
    val vmin = IntArray(w.coerceAtLeast(h))
    val vmax = IntArray(w.coerceAtLeast(h))
    val pix = IntArray(w * h)
    bitmap.getPixels(pix, 0, w, 0, 0, w, h)
    val dv = IntArray(256 * div)
    i = 0
    while (i < 256 * div) {
        dv[i] = i / div
        i++
    }
    yi = 0
    var yw = 0
    y = 0
    while (y < h) {
        bsum = 0
        gsum = 0
        rsum = 0
        i = -radius
        while (i <= radius) {
            p = pix[yi + wm.coerceAtMost(i.coerceAtLeast(0))]
            rsum += p and 0xff0000 shr 16
            gsum += p and 0x00ff00 shr 8
            bsum += p and 0x0000ff
            i++
        }
        x = 0
        while (x < w) {
            r[yi] = dv[rsum]
            g[yi] = dv[gsum]
            b[yi] = dv[bsum]
            if (y == 0) {
                vmin[x] = (x + radius + 1).coerceAtMost(wm)
                vmax[x] = (x - radius).coerceAtLeast(0)
            }
            p1 = pix[yw + vmin[x]]
            p2 = pix[yw + vmax[x]]
            rsum += (p1 and 0xff0000) - (p2 and 0xff0000) shr 16
            gsum += (p1 and 0x00ff00) - (p2 and 0x00ff00) shr 8
            bsum += (p1 and 0x0000ff) - (p2 and 0x0000ff)
            yi++
            x++
        }
        yw += w
        y++
    }
    x = 0
    while (x < w) {
        bsum = 0
        gsum = 0
        rsum = 0
        yp = -radius * w
        i = -radius
        while (i <= radius) {
            yi = 0.coerceAtLeast(yp) + x
            rsum += r[yi]
            gsum += g[yi]
            bsum += b[yi]
            yp += w
            i++
        }
        yi = x
        y = 0
        while (y < h) {
            pix[yi] = -0x1000000 or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
            if (x == 0) {
                vmin[y] = (y + radius + 1).coerceAtMost(hm) * w
                vmax[y] = (y - radius).coerceAtLeast(0) * w
            }
            p1 = x + vmin[y]
            p2 = x + vmax[y]
            rsum += r[p1] - r[p2]
            gsum += g[p1] - g[p2]
            bsum += b[p1] - b[p2]
            yi += w
            y++
        }
        x++
    }
    bitmap.setPixels(pix, 0, w, 0, 0, w, h)
    return bitmap.resizeBitmap(
        (bitmap.width / scale).toInt(),
        (bitmap.height / scale).toInt(),
        ResizeType.Explicit
    )
}