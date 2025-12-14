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

package com.t8rin.imagetoolbox.feature.filters.data.model

import android.graphics.Bitmap
import androidx.core.graphics.scale
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import kotlin.math.roundToInt

@FilterInject
internal class FastBlurFilter(
    override val value: Pair<Float, Float> = 0.5f to 5f,
) : Transformation<Bitmap>, Filter.FastBlur {
    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = input.fastBlur(
        scale = value.first,
        radius = value.second.roundToInt()
    )

}

private fun Bitmap.fastBlur(
    scale: Float,
    radius: Int
): Bitmap {
    var sentBitmap = this@fastBlur
    val width = (sentBitmap.width * scale).roundToInt().coerceAtLeast(1)
    val height = (sentBitmap.height * scale).roundToInt().coerceAtLeast(1)
    sentBitmap = sentBitmap.scale(width, height)
    val bitmap = sentBitmap.copy(sentBitmap.safeConfig, true)
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
    return bitmap.scale(
        width = (bitmap.width / scale).toInt(),
        height = (bitmap.height / scale).toInt()
    )
}