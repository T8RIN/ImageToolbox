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

import com.t8rin.palette.ColorSpace
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.utils.readText
import java.io.InputStream
import java.io.OutputStream

/**
 * Kotlin/Jetpack Compose code generator
 */
class KotlinPaletteCoder : PaletteCoder {
    override fun decode(input: InputStream): Palette {
        val text = input.readText()
        val result = Palette.Builder()

        // Parse Color(0xAARRGGBB) or Color(0xRRGGBB)
        // Also try to capture variable names: val name: Color = Color(0x...)
        val colorRegex = Regex(
            """(?:val\s+(\w+)\s*:\s*Color\s*=\s*)?Color\s*\(\s*0x([0-9A-Fa-f]{6,8})\s*\)""",
            RegexOption.IGNORE_CASE
        )

        // Use Set to track unique colors by RGB values to avoid duplicates
        val seenColors = mutableSetOf<Triple<Int, Int, Int>>()
        var colorIndex = 0

        colorRegex.findAll(text).forEach { match ->
            try {
                val colorName = match.groupValues[1].takeIf { it.isNotEmpty() } ?: ""
                val hexValue = match.groupValues[2]
                val value = hexValue.toLongOrNull(16) ?: return@forEach

                val (r, g, b, a) = if (hexValue.length == 8) {
                    // AARRGGBB
                    val aVal = ((value shr 24) and 0xFF) / 255.0
                    val rVal = ((value shr 16) and 0xFF) / 255.0
                    val gVal = ((value shr 8) and 0xFF) / 255.0
                    val bVal = (value and 0xFF) / 255.0
                    Quad(rVal, gVal, bVal, aVal)
                } else {
                    // RRGGBB
                    val rVal = ((value shr 16) and 0xFF) / 255.0
                    val gVal = ((value shr 8) and 0xFF) / 255.0
                    val bVal = (value and 0xFF) / 255.0
                    Quad(rVal, gVal, bVal, 1.0)
                }

                // Check for duplicates by RGB values (rounded to avoid floating point issues)
                val rInt = (r * 255).toInt()
                val gInt = (g * 255).toInt()
                val bInt = (b * 255).toInt()
                val colorKey = Triple(rInt, gInt, bInt)

                if (colorKey !in seenColors) {
                    seenColors.add(colorKey)

                    val finalName = if (colorName.isNotEmpty()) {
                        colorName
                    } else {
                        "Color_$colorIndex"
                    }

                    val color = PaletteColor.rgb(
                        r = r.coerceIn(0.0, 1.0),
                        g = g.coerceIn(0.0, 1.0),
                        b = b.coerceIn(0.0, 1.0),
                        a = a.coerceIn(0.0, 1.0),
                        name = finalName
                    )
                    result.colors.add(color)
                    colorIndex++
                }
            } catch (_: Throwable) {
                // Skip invalid colors
            }
        }

        // Try to extract palette name from comments or object name
        val objectNameMatch = Regex("""object\s+(\w+)""").find(text)
        if (objectNameMatch != null) {
            result.name = objectNameMatch.groupValues[1]
        } else {
            val commentMatch = Regex("""Exported palette:\s*(.+)""").find(text)
            if (commentMatch != null) {
                result.name = commentMatch.groupValues[1].trim()
            }
        }

        if (result.colors.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        return result.build()
    }

    private data class Quad(val r: Double, val g: Double, val b: Double, val a: Double)

    private fun sanitizeName(name: String): String {
        // Remove invalid characters and make it a valid Kotlin identifier
        return name
            .replace(Regex("[^a-zA-Z0-9_]"), "_")
            .replace(Regex("^[0-9]"), "_$0") // Can't start with digit
            .takeIf { it.isNotEmpty() } ?: "color"
    }

    private fun formatColor(rgb: PaletteColor.RGB): String {
        val r = (rgb.rf * 255).toInt().coerceIn(0, 255)
        val g = (rgb.gf * 255).toInt().coerceIn(0, 255)
        val b = (rgb.bf * 255).toInt().coerceIn(0, 255)
        val a = (rgb.af * 255).toInt().coerceIn(0, 255)

        // Format: Color(0xAARRGGBB)
        val argb = (a shl 24) or (r shl 16) or (g shl 8) or b
        return "Color(0x${argb.toUInt().toString(16).uppercase().padStart(8, '0')})"
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val packageName = if (palette.name.isNotEmpty()) {
            palette.name.lowercase()
                .replace(Regex("[^a-z0-9]"), "")
                .takeIf { it.isNotEmpty() && it[0].isLetter() }
                ?: "palette"
        } else {
            "palette"
        }

        var result = "package $packageName\n\n"
        result += "import androidx.compose.ui.graphics.Color\n\n"
        result += "/**\n"
        result += " * Exported palette: ${palette.name.ifEmpty { "Untitled" }}\n"
        result += " * Total colors: ${palette.totalColorCount}\n"
        result += " */\n"
        result += "object ExportedPalette {\n\n"

        // Generate individual color constants with names
        val allColorsList = palette.allColors()
        if (allColorsList.isNotEmpty()) {
            result += "    // Individual color constants\n"
            allColorsList.forEachIndexed { index, color ->
                try {
                    val converted =
                        if (color.colorSpace == ColorSpace.RGB) color else color.converted(
                            ColorSpace.RGB
                        )
                    val rgb = converted.toRgb()
                    val colorName = if (color.name.isNotEmpty()) {
                        sanitizeName(color.name)
                    } else {
                        "color$index"
                    }
                    result += "    val $colorName: Color = ${formatColor(rgb)}\n"
                } catch (_: Throwable) {
                    // Skip invalid colors
                }
            }
            result += "\n"
        }

        // Generate groups
        palette.allGroups.forEachIndexed { groupIndex, group ->
            if (group.colors.isEmpty()) return@forEachIndexed

            val groupName = if (group.name.isNotEmpty() && group.name != "global") {
                sanitizeName(group.name)
            } else {
                "group$groupIndex"
            }

            result += "    // Group: ${group.name}\n"
            result += "    val $groupName: List<Color> = listOf(\n"

            group.colors.forEachIndexed { index, color ->
                try {
                    val converted =
                        if (color.colorSpace == ColorSpace.RGB) color else color.converted(
                            ColorSpace.RGB
                        )
                    val rgb = converted.toRgb()
                    val indent = "        "
                    result += "$indent${formatColor(rgb)}"

                    if (index < group.colors.size - 1) {
                        result += ","
                    }
                    result += "\n"
                } catch (_: Throwable) {
                    // Skip invalid colors
                }
            }

            result += "    )\n\n"
        }

        // Generate allColors list
        val allColors = allColorsList.mapNotNull { color ->
            try {
                val converted =
                    if (color.colorSpace == ColorSpace.RGB) color else color.converted(ColorSpace.RGB)
                converted.toRgb()
            } catch (_: Throwable) {
                null
            }
        }

        if (allColors.isNotEmpty()) {
            result += "    /**\n"
            result += "     * All colors from all groups\n"
            result += "     */\n"
            result += "    val allColors: List<Color> = listOf(\n"

            allColors.forEachIndexed { index, rgb ->
                val indent = "        "
                result += "$indent${formatColor(rgb)}"

                if (index < allColors.size - 1) {
                    result += ","
                }
                result += "\n"
            }

            result += "    )\n"
        }

        result += "}\n"

        output.write(result.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
    }
}

