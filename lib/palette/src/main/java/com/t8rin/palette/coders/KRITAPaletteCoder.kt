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

import com.t8rin.palette.ColorGroup
import com.t8rin.palette.ColorSpace
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.utils.xmlDecoded
import com.t8rin.palette.utils.xmlEscaped
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.xml.parsers.SAXParserFactory

/**
 * KRITA Palette (KPL) coder
 * KPL format is a ZIP archive containing colorset.xml and profiles.xml
 */
class KRITAPaletteCoder : PaletteCoder {

    private class KRITAColorsetHandler : DefaultHandler() {
        val palette = Palette.Builder()
        private var currentGroup: ColorGroup? = null
        private var currentColorName: String = ""
        private var currentColorSpace: String = "RGBA"
        private var currentColorComponents = mutableListOf<Double>()
        private var currentAlpha: Double = 1.0
        private var currentChars = StringBuilder()
        private var isInColorSetEntry = false
        private var isInGroup = false

        override fun startElement(
            uri: String?,
            localName: String,
            qName: String?,
            attributes: Attributes
        ) {
            currentChars.clear()
            val elementName = (qName ?: localName).trim().lowercase()

            when (elementName) {
                "colorset" -> {
                    val name = attributes.getValue("name")?.xmlDecoded()
                    if (name != null) {
                        palette.name = name
                    }
                }

                "group" -> {
                    isInGroup = true
                    val name = attributes.getValue("name")?.xmlDecoded() ?: ""
                    currentGroup = ColorGroup(name = name, colors = mutableListOf())
                }

                "colorsetentry" -> {
                    isInColorSetEntry = true
                    currentColorName = attributes.getValue("name")?.xmlDecoded() ?: ""
                    currentColorSpace = attributes.getValue("colorspace") ?: "RGBA"
                    currentColorComponents.clear()
                    currentAlpha = 1.0
                }

                "color" -> {
                    // Color components are in the text content
                }
            }
        }

        override fun endElement(uri: String?, localName: String, qName: String?) {
            val elementName = (qName ?: localName).trim().lowercase()
            val content = currentChars.toString().trim()

            when (elementName) {
                "group" -> {
                    currentGroup?.let { group ->
                        if (group.colors.isNotEmpty()) {
                            palette.groups.add(group)
                        }
                    }
                    currentGroup = null
                    isInGroup = false
                }

                "colorsetentry" -> {
                    if (isInColorSetEntry && currentColorComponents.isNotEmpty()) {
                        val color = createColorFromComponents(
                            currentColorSpace,
                            currentColorComponents,
                            currentAlpha,
                            currentColorName
                        )
                        if (color != null) {
                            if (isInGroup && currentGroup != null) {
                                val groupColors = currentGroup!!.colors.toMutableList()
                                groupColors.add(color)
                                currentGroup = currentGroup!!.copy(colors = groupColors)
                            } else {
                                palette.colors.add(color)
                            }
                        }
                    }
                    isInColorSetEntry = false
                    currentColorComponents.clear()
                }

                "color" -> {
                    // Parse color components from text content
                    val components = content.split(Regex("\\s+"))
                        .mapNotNull { it.toDoubleOrNull() }
                        .toMutableList()
                    if (components.isNotEmpty()) {
                        currentColorComponents = components
                        // Alpha is typically the last component for RGBA
                        if (currentColorSpace.equals(
                                "RGBA",
                                ignoreCase = true
                            ) && components.size >= 4
                        ) {
                            currentAlpha = components[3].coerceIn(0.0, 1.0)
                        } else if (components.size >= 4) {
                            currentAlpha = components.last().coerceIn(0.0, 1.0)
                        }
                    }
                }
            }
            currentChars.clear()
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            currentChars.appendRange(ch, start, start + length)
        }

        private fun createColorFromComponents(
            colorSpace: String,
            components: List<Double>,
            alpha: Double,
            name: String
        ): PaletteColor? {
            return when (colorSpace.uppercase()) {
                "RGBA", "RGB" -> {
                    if (components.size >= 3) {
                        PaletteColor.rgb(
                            r = components[0].coerceIn(0.0, 1.0),
                            g = components[1].coerceIn(0.0, 1.0),
                            b = components[2].coerceIn(0.0, 1.0),
                            a = if (components.size >= 4) components[3].coerceIn(
                                0.0,
                                1.0
                            ) else alpha,
                            name = name
                        )
                    } else null
                }

                "CMYK" -> {
                    if (components.size >= 4) {
                        PaletteColor.cmyk(
                            c = components[0].coerceIn(0.0, 1.0),
                            m = components[1].coerceIn(0.0, 1.0),
                            y = components[2].coerceIn(0.0, 1.0),
                            k = components[3].coerceIn(0.0, 1.0),
                            alpha = alpha,
                            name = name
                        )
                    } else null
                }

                "LAB" -> {
                    if (components.size >= 3) {
                        PaletteColor.lab(
                            l = components[0],
                            a = components[1],
                            b = components[2],
                            alpha = alpha,
                            name = name
                        )
                    } else null
                }

                "GRAY", "GRAYA" -> {
                    if (components.isNotEmpty()) {
                        PaletteColor.gray(
                            white = components[0].coerceIn(0.0, 1.0),
                            alpha = if (components.size >= 2) components[1].coerceIn(
                                0.0,
                                1.0
                            ) else alpha,
                            name = name
                        )
                    } else null
                }

                "HSVA", "HSV" -> {
                    if (components.size >= 3) {
                        PaletteColor.hsb(
                            hf = components[0].coerceIn(0.0, 1.0),
                            sf = components[1].coerceIn(0.0, 1.0),
                            bf = components[2].coerceIn(0.0, 1.0),
                            alpha = if (components.size >= 4) components[3].coerceIn(
                                0.0,
                                1.0
                            ) else alpha,
                            name = name
                        )
                    } else null
                }

                "HSLA", "HSL" -> {
                    if (components.size >= 3) {
                        PaletteColor.hsl(
                            hf = components[0].coerceIn(0.0, 1.0),
                            sf = components[1].coerceIn(0.0, 1.0),
                            lf = components[2].coerceIn(0.0, 1.0),
                            alpha = if (components.size >= 4) components[3].coerceIn(
                                0.0,
                                1.0
                            ) else alpha,
                            name = name
                        )
                    } else null
                }

                else -> null
            }
        }
    }

    override fun decode(input: InputStream): Palette {
        val data = input.readBytes()
        val zipInputStream = ZipInputStream(data.inputStream())
        var colorsetXmlData = ByteArray(0)
        var entry: ZipEntry? = zipInputStream.nextEntry

        while (entry != null) {
            if (entry.name.equals("colorset.xml", ignoreCase = true)) {
                colorsetXmlData = zipInputStream.readBytes()
                break
            }
            entry = zipInputStream.nextEntry
        }
        zipInputStream.close()

        if (colorsetXmlData.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        val handler = KRITAColorsetHandler()
        val factory = SAXParserFactory.newInstance()
        factory.isNamespaceAware = true
        factory.isValidating = false
        val parser = factory.newSAXParser()
        parser.parse(colorsetXmlData.inputStream(), handler)

        if (handler.palette.colors.isEmpty() && handler.palette.groups.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        return handler.palette.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val zipOutputStream = ZipOutputStream(output)

        // Write colorset.xml
        zipOutputStream.putNextEntry(ZipEntry("colorset.xml"))
        val colorsetXml = buildString {
            appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
            appendLine("""<colorset name="${palette.name.xmlEscaped()}">""")

            // Global colors
            palette.colors.forEach { color ->
                appendColorSetEntry(color)
            }

            // Groups
            palette.groups.forEach { group ->
                appendLine("  <group name=\"${group.name.xmlEscaped()}\">")
                group.colors.forEach { color ->
                    appendColorSetEntry(color, indent = "    ")
                }
                appendLine("  </group>")
            }

            appendLine("</colorset>")
        }
        zipOutputStream.write(colorsetXml.toByteArray(Charsets.UTF_8))
        zipOutputStream.closeEntry()

        // Write profiles.xml (minimal version)
        zipOutputStream.putNextEntry(ZipEntry("profiles.xml"))
        val profilesXml = """<?xml version="1.0" encoding="UTF-8"?>
<profiles>
</profiles>
"""
        zipOutputStream.write(profilesXml.toByteArray(Charsets.UTF_8))
        zipOutputStream.closeEntry()

        zipOutputStream.close()
    }

    private fun StringBuilder.appendColorSetEntry(color: PaletteColor, indent: String = "  ") {
        val converted =
            if (color.colorSpace == ColorSpace.RGB) color else color.converted(ColorSpace.RGB)
        val rgb = converted.toRgb()

        appendLine("$indent<colorsetentry name=\"${color.name.xmlEscaped()}\" colorspace=\"RGBA\">")
        appendLine("$indent  <color>${rgb.rf} ${rgb.gf} ${rgb.bf} ${rgb.af}</color>")
        appendLine("$indent</colorsetentry>")
    }
}

