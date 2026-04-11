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
import com.t8rin.palette.utils.hexString
import com.t8rin.palette.utils.xmlDecoded
import com.t8rin.palette.utils.xmlEscaped
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import java.io.OutputStream
import javax.xml.parsers.SAXParserFactory

/**
 * Android colors.xml palette coder
 */
class AndroidColorsXMLCoder(
    private val includeAlphaDuringExport: Boolean = true
) : PaletteCoder {

    private class AndroidXMLHandler : DefaultHandler() {
        val palette = Palette.Builder()
        private var currentElement = ""
        private var currentName: String? = null
        private var isInsideResourcesBlock = false
        private var currentChars = StringBuilder()
        private fun elementName(localName: String, qName: String?): String =
            localName.ifBlank { qName ?: "" }.substringAfter(':').lowercase()

        override fun startElement(
            uri: String?,
            localName: String,
            qName: String?,
            attributes: Attributes
        ) {
            currentChars.clear()
            when (elementName(localName, qName)) {
                "resources" -> isInsideResourcesBlock = true
                "color" -> {
                    currentElement = "color"
                    currentName = attributes.getValue("name")?.xmlDecoded()
                }
            }
        }

        override fun endElement(uri: String?, localName: String, qName: String?) {
            when (elementName(localName, qName)) {
                "resources" -> isInsideResourcesBlock = false
                "color" -> {
                    if (isInsideResourcesBlock && currentElement == "color") {
                        val colorString = currentChars.toString().trim()
                        val colorName = currentName ?: "color_${palette.colors.size}"
                        try {
                            val color = PaletteColor(
                                rgbHexString = colorString,
                                format = ColorByteFormat.ARGB,
                                name = colorName
                            )
                            palette.colors.add(color)
                        } catch (_: Throwable) {
                            // Skip invalid colors
                        }
                    }
                    currentElement = ""
                    currentName = null
                }
            }
            currentChars.clear()
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            currentChars.appendRange(ch, start, start + length)
        }
    }

    override fun decode(input: InputStream): Palette {
        val handler = AndroidXMLHandler()
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        parser.parse(input, handler)

        if (handler.palette.colors.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        return handler.palette.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        var xml = """<?xml version="1.0" encoding="utf-8"?>
<resources>
"""

        palette.allColors().forEachIndexed { index, color ->
            var name = color.name.ifEmpty { "color_$index" }
            name = name.replace(" ", "_").xmlEscaped()

            val format = if (includeAlphaDuringExport) ColorByteFormat.ARGB else ColorByteFormat.RGB
            val hex = color.hexString(format, hashmark = true, uppercase = true)

            xml += "   <color name=\"$name\">$hex</color>\n"
        }

        xml += "</resources>\n"

        output.write(xml.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
    }
}
