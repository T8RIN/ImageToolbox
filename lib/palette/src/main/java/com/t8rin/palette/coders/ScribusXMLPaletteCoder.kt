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
import com.t8rin.palette.ColorSpace
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.utils.hexString
import com.t8rin.palette.utils.xmlDecoded
import com.t8rin.palette.utils.xmlEscaped
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import javax.xml.parsers.SAXParserFactory

/**
 * Scribus XML palette coder
 */
class ScribusXMLPaletteCoder : PaletteCoder {

    private class ScribusXMLHandler : DefaultHandler() {
        val palette = Palette.Builder()
        private var currentChars = StringBuilder()

        override fun startElement(
            uri: String?,
            localName: String,
            qName: String?,
            attributes: Attributes
        ) {
            currentChars.clear()
            val elementName = (qName ?: localName).lowercase()

            if (elementName == "scribuscolors" || elementName == "scolors") {
                val name = attributes.getValue("Name") ?: attributes.getValue("name") ?: ""
                palette.name = name.xmlDecoded()
            } else if (elementName == "color") {
                val name =
                    (attributes.getValue("NAME") ?: attributes.getValue("name") ?: "").xmlDecoded()
                val rgbHex = attributes.getValue("RGB") ?: attributes.getValue("rgb")
                val cmykHex = attributes.getValue("CMYK") ?: attributes.getValue("cmyk")

                try {
                    val color = if (rgbHex != null) {
                        PaletteColor(
                            rgbHexString = rgbHex,
                            format = ColorByteFormat.RGB,
                            name = name
                        )
                    } else if (cmykHex != null) {
                        // CMYK hex format - parse it
                        PaletteColor(cmykHexString = cmykHex, name = name)
                    } else {
                        null
                    }

                    if (color != null) {
                        palette.colors.add(color)
                    }
                } catch (_: Throwable) {
                    // Skip invalid colors
                }
            }
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            currentChars.appendRange(ch, start, start + length)
        }
    }

    override fun decode(input: InputStream): Palette {
        val handler = ScribusXMLHandler()
        val factory = SAXParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newSAXParser()
        parser.parse(input, handler)

        if (handler.palette.colors.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        return handler.palette.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        var xml = "<?xml version=\"1.0\"?>\n"
        xml += "<SCRIBUSCOLORS"
        if (palette.name.isNotEmpty()) {
            xml += " Name=\"${palette.name.xmlEscaped()}\""
        }
        xml += " >\n"

        palette.allColors().forEach { color ->
            try {
                if (color.colorSpace == ColorSpace.CMYK) {
                    val cmyk = color.toCmyk()
                    // CMYK hex representation (simplified)
                    val c = (cmyk.c * 255).toInt().coerceIn(0, 255)
                    val m = (cmyk.m * 255).toInt().coerceIn(0, 255)
                    val y = (cmyk.y * 255).toInt().coerceIn(0, 255)
                    val k = (cmyk.k * 255).toInt().coerceIn(0, 255)
                    val hex = String.format("#%02x%02x%02x%02x", c, m, y, k)
                    xml += "<COLOR CMYK=\"$hex\""
                } else {
                    val rgb = color.toRgb()
                    val hex = rgb.hexString(ColorByteFormat.RGB, hashmark = true, uppercase = false)
                    xml += "<COLOR RGB=\"$hex\""
                }

                if (color.name.isNotEmpty()) {
                    xml += " NAME=\"${color.name.xmlEscaped()}\""
                }
                xml += " />\n"
            } catch (_: Throwable) {
                // Skip colors that can't be converted
            }
        }

        xml += "</SCRIBUSCOLORS>\n"

        output.write(xml.toByteArray(StandardCharsets.UTF_8))
    }
}