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

package com.t8rin.palette.coders

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import java.io.InputStream
import java.io.OutputStream
import android.graphics.Color as AndroidColor

/**
 * Image-based palette coder
 * Note: This requires Android Bitmap API
 */
class ImagePaletteCoder : PaletteCoder {

    override fun decode(input: InputStream): Palette {
        val data = input.readBytes()
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            ?: throw PaletteCoderException.InvalidFormat()

        val result = Palette.Builder()
        val colorOrder = mutableListOf<ColorPixel>()

        // Read first row of pixels - one pixel per color swatch
        val width = bitmap.width
        val swatchWidth = 32 // Each color is 32 pixels wide
        val numColors = width / swatchWidth

        // Read one pixel from the center of each swatch
        for (i in 0 until numColors) {
            val x = i * swatchWidth + swatchWidth / 2
            if (x < width) {
                val pixel = bitmap[x, 0]
                val a = AndroidColor.alpha(pixel) / 255.0
                val r = AndroidColor.red(pixel) / 255.0
                val g = AndroidColor.green(pixel) / 255.0
                val b = AndroidColor.blue(pixel) / 255.0

                val colorPixel = ColorPixel(r, g, b, a)
                colorOrder.add(colorPixel)
            }
        }

        // Try to read color names from PNG text chunk or extension
        val colorNames = mutableListOf<String>()
        try {
            // Check if there's a JSON extension after PNG data
            val pngEndMarker = byteArrayOf(
                0x49, 0x45, 0x4E, 0x44, 0xAE.toByte(), 0x42, 0x60,
                0x82.toByte()
            ) // IEND chunk
            val pngEndIndex = data.indexOfSlice(pngEndMarker)
            if (pngEndIndex >= 0 && pngEndIndex + 8 < data.size) {
                val extensionData = data.sliceArray(pngEndIndex + 8 until data.size)
                val extensionText = String(extensionData, java.nio.charset.StandardCharsets.UTF_8)
                if (extensionText.startsWith("\n; IMAGE_NAMES: ")) {
                    val namesLine = extensionText.lines().find { it.startsWith("; IMAGE_NAMES:") }
                    if (namesLine != null) {
                        val namesStr = namesLine.substring("; IMAGE_NAMES: ".length).trim()
                        colorNames.addAll(namesStr.split("|"))
                    }
                }
            }
        } catch (_: Throwable) {
            // No names extension
        }

        // Convert to PaletteColor, preserving order and names
        // Don't filter duplicates - preserve all colors in order
        colorOrder.forEachIndexed { index, pixel ->
            val colorName = if (index < colorNames.size) colorNames[index] else ""
            val color = PaletteColor.rgb(
                r = pixel.r,
                g = pixel.g,
                b = pixel.b,
                a = pixel.a,
                name = colorName
            )
            result.colors.add(color)
        }

        return result.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val colors = palette.allColors()
        if (colors.isEmpty()) {
            throw PaletteCoderException.TooFewColors()
        }

        val swatchWidth = 32
        val swatchHeight = 32
        val bitmapWidth = colors.size * swatchWidth

        val bitmap = createBitmap(bitmapWidth, swatchHeight)
        val canvas = Canvas(bitmap)

        colors.forEachIndexed { index, color ->
            val rgb = color.toRgb()
            val androidColor = AndroidColor.argb(
                (rgb.af * 255).toInt().coerceIn(0, 255),
                (rgb.rf * 255).toInt().coerceIn(0, 255),
                (rgb.gf * 255).toInt().coerceIn(0, 255),
                (rgb.bf * 255).toInt().coerceIn(0, 255)
            )

            val x = index * swatchWidth
            canvas.drawRect(
                x.toFloat(), 0f,
                (x + swatchWidth).toFloat(), swatchHeight.toFloat(),
                android.graphics.Paint().apply {
                    this.color = androidColor
                    style = android.graphics.Paint.Style.FILL
                }
            )
        }

        val pngOutputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, pngOutputStream)
        val pngData = pngOutputStream.toByteArray()
        output.write(pngData)

        // Append color names as extension (non-standard but preserves names)
        // Save all names, including empty ones, to preserve order
        val names = colors.map { it.name }
        val nameText = "\n; IMAGE_NAMES: ${names.joinToString("|")}\n"
        output.write(nameText.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
    }

    private data class ColorPixel(
        val r: Double,
        val g: Double,
        val b: Double,
        val a: Double
    )
}

private fun ByteArray.indexOfSlice(slice: ByteArray, startIndex: Int = 0): Int {
    if (slice.isEmpty() || this.isEmpty() || slice.size > this.size) return -1
    outer@ for (i in startIndex..this.size - slice.size) {
        for (j in slice.indices) {
            if (this[i + j] != slice[j]) continue@outer
        }
        return i
    }
    return -1
}