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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asComposePath
import com.t8rin.logger.makeLog
import java.util.LinkedList
import java.util.Queue
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Path as ComposePath

internal class FloodFill(image: Bitmap) {
    private val path = Path()

    private val width: Int = image.width
    private val height: Int = image.height
    private val pixels: IntArray = IntArray(width * height)

    private lateinit var pixelsChecked: BooleanArray
    private lateinit var ranges: Queue<FloodFillRange>

    private var tolerance = 0

    private var startColorRed = 0
    private var startColorGreen = 0
    private var startColorBlue = 0
    private var startColorAlpha = 0

    init {
        image.getPixels(pixels, 0, width, 0, 0, width, height)
    }

    private fun prepare() {
        // Called before starting flood-fill
        pixelsChecked = BooleanArray(pixels.size)
        ranges = LinkedList()
    }

    //  Fills the specified point on the bitmap with the currently selected fill color.
    //  int x, int y: The starting coordinates for the fill
    fun performFloodFill(
        x: Int,
        y: Int,
        tolerance: Float
    ): Path? {
        if (x >= width || y >= height || x < 0 || y < 0) return null

        path.rewind()
        this.tolerance = (tolerance * 255).roundToInt().coerceIn(0, 255)
        // Setup
        prepare()

        // Get starting color.
        val startPixel = pixels.getOrNull(width * y + x) ?: return null
        startColorRed = Color.red(startPixel)
        startColorGreen = Color.green(startPixel)
        startColorBlue = Color.blue(startPixel)
        startColorAlpha = Color.alpha(startPixel)

        // Do first call to flood-fill.
        linearFill(x, y)

        // Call flood-fill routine while flood-fill ranges still exist on the queue
        var range: FloodFillRange
        while (ranges.isNotEmpty()) {
            // Get Next Range Off the Queue
            range = ranges.remove()

            // Check Above and Below Each Pixel in the flood-fill Range
            var downPxIdx = width * (range.Y + 1) + range.startX
            var upPxIdx = width * (range.Y - 1) + range.startX
            val upY = range.Y - 1 // so we can pass the y coordinate by ref
            val downY = range.Y + 1
            for (i in range.startX..range.endX) {
                // Start Fill Upwards
                // if we're not above the top of the bitmap and the pixel above this one is within the color tolerance
                if (range.Y > 0 && !pixelsChecked[upPxIdx] && isPixelColorWithinTolerance(upPxIdx)) {
                    linearFill(i, upY)
                }

                // Start Fill Downwards
                // if we're not below the bottom of the bitmap and the pixel below this one is within the color tolerance
                if (
                    range.Y < height - 1 && !pixelsChecked[downPxIdx]
                    && isPixelColorWithinTolerance(downPxIdx)
                ) {
                    linearFill(i, downY)
                }
                downPxIdx++
                upPxIdx++
            }
        }

        return path
    }

    //  Finds the furthermost left and right boundaries of the fill area
    //  on a given y coordinate, starting from a given x coordinate, filling as it goes.
    //  Adds the resulting horizontal range to the queue of flood-fill ranges,
    //  to be processed in the main loop.
    //
    //  int x, int y: The starting coordinates
    private fun linearFill(x: Int, y: Int) {
        // Find Left Edge of Color Area
        var lFillLoc = x // the location to check/fill on the left
        var pxIdx = width * y + x
        path.moveTo(x.toFloat(), y.toFloat())
        while (true) {
            pixelsChecked[pxIdx] = true
            lFillLoc--
            pxIdx--
            // exit loop if we're at edge of bitmap or color area
            if (lFillLoc < 0 || pixelsChecked[pxIdx] || !isPixelColorWithinTolerance(pxIdx)) {
                break
            }
        }
        vectorFill(pxIdx + 1)
        lFillLoc++

        // Find Right Edge of Color Area
        var rFillLoc = x // the location to check/fill on the left
        pxIdx = width * y + x
        while (true) {
            pixelsChecked[pxIdx] = true
            rFillLoc++
            pxIdx++
            if (rFillLoc >= width || pixelsChecked[pxIdx] || !isPixelColorWithinTolerance(pxIdx)) {
                break
            }
        }
        vectorFill(pxIdx - 1)
        rFillLoc--

        // add range to queue
        val r = FloodFillRange(lFillLoc, rFillLoc, y)
        ranges.offer(r)
    }

    // vector fill pixels with color
    private fun vectorFill(pxIndex: Int) {
        val x = (pxIndex % width).toFloat()
        val y = (pxIndex - x) / width
        path.lineTo(x, y)
    }

    // Sees if a pixel is within the color tolerance range.
    private fun isPixelColorWithinTolerance(px: Int): Boolean {
        val alpha = pixels[px] ushr 24 and 0xff
        val red = pixels[px] ushr 16 and 0xff
        val green = pixels[px] ushr 8 and 0xff
        val blue = pixels[px] and 0xff

        return alpha >= startColorAlpha - tolerance && alpha <= startColorAlpha + tolerance &&
                red >= startColorRed - tolerance && red <= startColorRed + tolerance &&
                green >= startColorGreen - tolerance && green <= startColorGreen + tolerance &&
                blue >= startColorBlue - tolerance && blue <= startColorBlue + tolerance
    }

    //  Represents a linear range to be filled and branched from.
    private data class FloodFillRange(
        val startX: Int,
        val endX: Int,
        val Y: Int
    )
}

fun ImageBitmap.floodFill(
    offset: Offset,
    tolerance: Float
): ComposePath? = runCatching {
    FloodFill(
        asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)
    ).performFloodFill(
        x = offset.x.roundToInt().coerceIn(0, width - 1),
        y = offset.y.roundToInt().coerceIn(0, height - 1),
        tolerance = tolerance
    )?.asComposePath()
}.onFailure { it.makeLog("floodFill") }.getOrNull()