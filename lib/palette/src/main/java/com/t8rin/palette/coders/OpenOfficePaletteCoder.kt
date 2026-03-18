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
import java.nio.charset.StandardCharsets
import javax.xml.parsers.SAXParserFactory

/**
 * OpenOffice palette coder
 */
class OpenOfficePaletteCoder : PaletteCoder {

    private class OpenOfficeXMLHandler : DefaultHandler() {
        val palette = Palette.Builder()
        private var currentChars = StringBuilder()

        override fun startElement(
            uri: String?,
            localName: String,
            qName: String?,
            attributes: Attributes
        ) {
            currentChars.clear()
            if (localName == "draw:color" || qName == "draw:color") {
                val name = attributes.getValue("draw:name")?.xmlDecoded() ?: ""
                val colorString = attributes.getValue("draw:color") ?: ""

                try {
                    val color = PaletteColor(
                        rgbHexString = colorString,
                        format = ColorByteFormat.RGB,
                        name = name
                    )
                    palette.colors.add(color)
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
        val handler = OpenOfficeXMLHandler()
        val factory = SAXParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newSAXParser()
        parser.parse(input, handler)

        val palette = handler.palette.build()

        if (palette.totalColorCount == 0) {
            throw PaletteCoderException.InvalidFormat()
        }

        return palette
    }

    override fun encode(palette: Palette, output: OutputStream) {
        var xml = """<?xml version="1.0" encoding="UTF-8"?>
<office:color-table xmlns:office="http://openoffice.org/2000/office" xmlns:style="http://openoffice.org/2000/style" xmlns:text="http://openoffice.org/2000/text" xmlns:table="http://openoffice.org/2000/table" xmlns:draw="http://openoffice.org/2000/drawing" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="http://openoffice.org/2000/meta" xmlns:number="http://openoffice.org/2000/datastyle" xmlns:svg="http://www.w3.org/2000/svg" xmlns:chart="http://openoffice.org/2000/chart" xmlns:dr3d="http://openoffice.org/2000/dr3d" xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:form="http://openoffice.org/2000/form" xmlns:script="http://openoffice.org/2000/script">
"""

        palette.allColors().forEach { color ->
            try {
                val hex = color.hexString(ColorByteFormat.RGB, hashmark = true, uppercase = true)
                xml += "<draw:color draw:name=\"${color.name.xmlEscaped()}\" draw:color=\"$hex\"/>\n"
            } catch (_: Throwable) {
                // Skip colors that can't be converted
            }
        }

        xml += "</office:color-table>\n"

        output.write(xml.toByteArray(StandardCharsets.UTF_8))
    }
}
