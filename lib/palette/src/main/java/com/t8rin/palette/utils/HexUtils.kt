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

package com.t8rin.palette.utils

import com.t8rin.palette.ColorByteFormat
import com.t8rin.palette.PaletteColor

/**
 * Hex color utilities
 */

private data class Quad(val a: Long, val b: Long, val c: Long, val d: Long)

/**
 * Extract RGBA from hex string
 */
internal fun extractHexRGBA(rgbHexString: String, format: ColorByteFormat): PaletteColor.RGB? {
    var hex = rgbHexString.lowercase().replace(Regex("[^0-9a-f]"), "")
    if (hex.startsWith("0x")) {
        hex = hex.substring(2)
    }

    val `val` = hex.toLongOrNull(16) ?: return null

    val hasAlpha = hex.length == 4 || hex.length == 8

    val quad = when (hex.length) {
        3 -> { // RGB (12-bit)
            val r = ((`val` shr 8) and 0xF) * 17
            val g = ((`val` shr 4) and 0xF) * 17
            val b = (`val` and 0xF) * 17
            Quad(r, g, b, 255L)
        }

        4 -> { // RGBA (12-bit)
            val r = ((`val` shr 12) and 0xF) * 17
            val g = ((`val` shr 8) and 0xF) * 17
            val b = ((`val` shr 4) and 0xF) * 17
            val a = (`val` and 0xF) * 17
            Quad(r, g, b, a)
        }

        6 -> { // RRGGBB (24-bit)
            val r = (`val` shr 16) and 0xFF
            val g = (`val` shr 8) and 0xFF
            val b = `val` and 0xFF
            Quad(r, g, b, 255L)
        }

        8 -> { // RRGGBBAA (32-bit)
            val r = (`val` shr 24) and 0xFF
            val g = (`val` shr 16) and 0xFF
            val b = (`val` shr 8) and 0xFF
            val a = `val` and 0xFF
            Quad(r, g, b, a)
        }

        else -> return null
    }

    val c0 = quad.a
    val c1 = quad.b
    val c2 = quad.c
    val c3 = quad.d

    return when (format) {
        ColorByteFormat.RGB -> PaletteColor.RGB(
            r = c0 / 255.0,
            g = c1 / 255.0,
            b = c2 / 255.0,
            a = 1.0
        )

        ColorByteFormat.BGR -> PaletteColor.RGB(
            r = c2 / 255.0,
            g = c1 / 255.0,
            b = c0 / 255.0,
            a = 1.0
        )

        ColorByteFormat.RGBA -> PaletteColor.RGB(
            r = c0 / 255.0,
            g = c1 / 255.0,
            b = c2 / 255.0,
            a = if (hasAlpha) c3 / 255.0 else 1.0
        )

        ColorByteFormat.ARGB -> PaletteColor.RGB(
            r = c1 / 255.0,
            g = c2 / 255.0,
            b = c3 / 255.0,
            a = if (hasAlpha) c0 / 255.0 else 1.0
        )

        ColorByteFormat.BGRA -> PaletteColor.RGB(
            r = c2 / 255.0,
            g = c1 / 255.0,
            b = c0 / 255.0,
            a = if (hasAlpha) c3 / 255.0 else 1.0
        )

        ColorByteFormat.ABGR -> PaletteColor.RGB(
            r = c3 / 255.0,
            g = c2 / 255.0,
            b = c1 / 255.0,
            a = if (hasAlpha) c0 / 255.0 else 1.0
        )
    }
}

/**
 * Generate hex string
 */
internal fun hexRGBString(
    r255: Int,
    g255: Int,
    b255: Int,
    a255: Int = 255,
    format: ColorByteFormat,
    hashmark: Boolean = true,
    uppercase: Boolean = false
): String {
    val prefix = if (hashmark) "#" else ""
    val fmt = if (uppercase) "%02X%02X%02X%02X" else "%02x%02x%02x%02x"
    val fmt3 = if (uppercase) "%02X%02X%02X" else "%02x%02x%02x"

    return when (format) {
        ColorByteFormat.RGB -> prefix + String.format(fmt3, r255, g255, b255)
        ColorByteFormat.BGR -> prefix + String.format(fmt3, b255, g255, r255)
        ColorByteFormat.ARGB -> prefix + String.format(fmt, a255, r255, g255, b255)
        ColorByteFormat.RGBA -> prefix + String.format(fmt, r255, g255, b255, a255)
        ColorByteFormat.ABGR -> prefix + String.format(fmt, a255, b255, g255, r255)
        ColorByteFormat.BGRA -> prefix + String.format(fmt, b255, g255, r255, a255)
    }
}

/**
 * Extension functions for PaletteColor hex support
 */
fun PaletteColor.hexString(format: ColorByteFormat, hashmark: Boolean, uppercase: Boolean): String {
    val rgb = toRgb()
    return hexRGBString(
        r255 = (rgb.rf * 255).toInt().coerceIn(0, 255),
        g255 = (rgb.gf * 255).toInt().coerceIn(0, 255),
        b255 = (rgb.bf * 255).toInt().coerceIn(0, 255),
        a255 = (rgb.af * 255).toInt().coerceIn(0, 255),
        format = format,
        hashmark = hashmark,
        uppercase = uppercase
    )
}

/**
 * Extension functions for PaletteColor.RGB hex support
 */
fun PaletteColor.RGB.hexString(
    format: ColorByteFormat,
    hashmark: Boolean,
    uppercase: Boolean
): String {
    return hexRGBString(
        r255 = (rf * 255).toInt().coerceIn(0, 255),
        g255 = (gf * 255).toInt().coerceIn(0, 255),
        b255 = (bf * 255).toInt().coerceIn(0, 255),
        a255 = (af * 255).toInt().coerceIn(0, 255),
        format = format,
        hashmark = hashmark,
        uppercase = uppercase
    )
}