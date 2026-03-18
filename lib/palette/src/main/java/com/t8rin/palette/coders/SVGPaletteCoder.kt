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

import com.t8rin.palette.ColorByteFormat
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.utils.extractHexRGBA
import com.t8rin.palette.utils.hexString
import com.t8rin.palette.utils.xmlEscaped
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import javax.xml.parsers.SAXParserFactory

/**
 * SVG palette coder
 */
class SVGPaletteCoder(
    private val swatchSize: Size = Size(width = 40.0, height = 40.0),
    private val maxExportWidth: Double = 600.0,
    private val edgeInset: EdgeInsets = EdgeInsets(top = 4.0, left = 4.0, bottom = 4.0, right = 4.0)
) : PaletteCoder {

    data class Size(val width: Double, val height: Double)
    data class EdgeInsets(val top: Double, val left: Double, val bottom: Double, val right: Double)

    private val formatter = DecimalFormat("#.###").apply {
        decimalFormatSymbols = java.text.DecimalFormatSymbols(java.util.Locale.US)
    }

    private class SVGHandler : DefaultHandler() {
        val palette = Palette.Builder()
        private var currentChars = StringBuilder()

        override fun startElement(
            uri: String?,
            localName: String,
            qName: String?,
            attributes: Attributes
        ) {
            currentChars.clear()
            val elementName = (qName ?: localName).trim().lowercase()

            if (elementName == "rect") {
                val fill = attributes.getValue("fill")
                val fillOpacity = attributes.getValue("fill-opacity")?.toDoubleOrNull() ?: 1.0
                val name = attributes.getValue("id") ?: ""

                if (fill != null && fill.isNotEmpty()) {
                    try {
                        val color = when {
                            fill.startsWith("#") -> {
                                val rgb = extractHexRGBA(fill, ColorByteFormat.RGB)
                                if (rgb != null) {
                                    PaletteColor.rgb(
                                        r = rgb.rf,
                                        g = rgb.gf,
                                        b = rgb.bf,
                                        a = fillOpacity,
                                        name = name
                                    )
                                } else null
                            }

                            fill.startsWith("rgb") -> {
                                // Parse rgb(r, g, b) or rgba(r, g, b, a)
                                val rgbMatch =
                                    Regex("""rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*([\d.]+))?\)""")
                                        .find(fill)
                                if (rgbMatch != null) {
                                    val r = rgbMatch.groupValues[1].toIntOrNull() ?: 0
                                    val g = rgbMatch.groupValues[2].toIntOrNull() ?: 0
                                    val b = rgbMatch.groupValues[3].toIntOrNull() ?: 0
                                    val a = rgbMatch.groupValues[4].toDoubleOrNull() ?: fillOpacity
                                    PaletteColor.rgb(
                                        r = (r / 255.0).coerceIn(0.0, 1.0),
                                        g = (g / 255.0).coerceIn(0.0, 1.0),
                                        b = (b / 255.0).coerceIn(0.0, 1.0),
                                        a = a.coerceIn(0.0, 1.0),
                                        name = name
                                    )
                                } else null
                            }

                            else -> null
                        }

                        if (color != null) {
                            palette.colors.add(color)
                        }
                    } catch (_: Throwable) {
                        // Skip invalid colors
                    }
                }
            }
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            currentChars.appendRange(ch, start, start + length)
        }
    }

    override fun decode(input: InputStream): Palette {
        val handler = SVGHandler()
        val factory = SAXParserFactory.newInstance()
        factory.isNamespaceAware = true
        factory.isValidating = false
        val parser = factory.newSAXParser()
        parser.parse(input, handler)

        if (handler.palette.colors.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        return handler.palette.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        var xOffset = edgeInset.left
        var yOffset = edgeInset.top

        fun exportGrouping(colors: List<PaletteColor>): String {
            var result = ""
            colors.forEach { color ->
                val rgb = color.toRgb()
                val hex = rgb.hexString(
                    format = ColorByteFormat.RGB,
                    hashmark = true,
                    uppercase = true
                )

                result += "      <rect x=\"${formatter.format(xOffset)}\" y=\"${
                    formatter.format(
                        yOffset
                    )
                }\" "
                result += "width=\"${formatter.format(swatchSize.width)}\" height=\"${
                    formatter.format(
                        swatchSize.height
                    )
                }\" "
                if (color.name.isNotEmpty()) {
                    result += "id=\"${color.name.xmlEscaped()}\" "
                }
                result += "fill=\"$hex\" fill-opacity=\"${formatter.format(color.alpha)}\""
                result += " />\n"

                xOffset += swatchSize.width + 1
                if (xOffset + swatchSize.width + edgeInset.right > maxExportWidth) {
                    yOffset += swatchSize.height + 1
                    xOffset = edgeInset.left
                }
            }
            return result
        }

        var colorsXml = ""
        // Global colors first
        colorsXml += exportGrouping(palette.colors)

        palette.groups.forEach { group ->
            xOffset = edgeInset.left
            colorsXml += exportGrouping(group.colors)

            if (group.name.isNotEmpty()) {
                yOffset += swatchSize.height + 10
                colorsXml += "      <text x='5' y='${formatter.format(yOffset)}' font-size='8' alignment-baseline='middle'>${group.name.xmlEscaped()}</text>\n\n"
            }

            yOffset += 10
        }

        yOffset += edgeInset.bottom

        val result = """<?xml version="1.0" encoding="utf-8"?>
	<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1" viewBox="0 0 ${
            formatter.format(
                maxExportWidth
            )
        } ${formatter.format(yOffset + swatchSize.height)}" xml:space="preserve">

"""
        output.write(result.toByteArray(StandardCharsets.UTF_8))
        output.write(colorsXml.toByteArray(StandardCharsets.UTF_8))
        output.write("</svg>\n".toByteArray(StandardCharsets.UTF_8))
    }
}

