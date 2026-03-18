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
import java.util.Locale
import javax.xml.parsers.SAXParserFactory

/**
 * CorelDraw XML palette coder (исправлена обработка имён элементов и поиск colorspaces по регистру)
 */
class CorelXMLPaletteCoder : PaletteCoder {

    private class CorelXMLHandler : DefaultHandler() {
        val palette = Palette.Builder()
        private var currentGroup: ColorGroup? = null
        private var isInColorsSection = false
        private var isInColorspaceSection = false
        private val colorspaces = mutableListOf<Colorspace>()
        private var currentChars = StringBuilder()

        private class Colorspace(val name: String) {
            val colors = mutableListOf<PaletteColor>()
        }

        private fun elmName(localName: String?, qName: String?) =
            (qName ?: localName ?: "").trim()

        override fun startElement(
            uri: String?,
            localName: String,
            qName: String?,
            attributes: Attributes
        ) {
            currentChars.clear()
            when (elmName(localName, qName).lowercase()) {
                "palette" -> {
                    val name = attributes.getValue("name")?.xmlDecoded()
                    if (!name.isNullOrEmpty()) {
                        palette.name = name
                    }
                }

                "colorspaces" -> isInColorspaceSection = true
                "cs" -> {
                    val name = attributes.getValue("name")?.xmlDecoded() ?: ""
                    colorspaces.add(Colorspace(name.lowercase()))
                }

                "colors" -> isInColorsSection = true
                "page" -> {
                    val name = attributes.getValue("name")?.xmlDecoded() ?: ""
                    currentGroup = ColorGroup(name = name)
                }

                "color" -> {
                    val csRaw = attributes.getValue("cs")
                    val cs = csRaw?.lowercase()
                    val name = attributes.getValue("name")?.xmlDecoded() ?: ""
                    val tints = attributes.getValue("tints") ?: ""
                    val components = tints.split(",").mapNotNull { it.trim().toDoubleOrNull() }

                    val color: PaletteColor? = when (cs) {
                        "cmyk" -> {
                            if (components.size >= 4) {
                                try {
                                    PaletteColor.cmyk(
                                        c = components[0],
                                        m = components[1],
                                        y = components[2],
                                        k = components[3],
                                        name = name
                                    )
                                } catch (_: Throwable) {
                                    null
                                }
                            } else null
                        }

                        "rgb" -> {
                            if (components.size >= 3) {
                                try {
                                    PaletteColor.rgb(
                                        r = components[0],
                                        g = components[1],
                                        b = components[2],
                                        name = name
                                    )
                                } catch (_: Throwable) {
                                    null
                                }
                            } else null
                        }

                        "lab" -> {
                            if (components.size >= 3) {
                                try {
                                    val l = components[0] * 100.0
                                    val a = components[1] * 256.0 - 128.0
                                    val b = components[2] * 256.0 - 128.0
                                    PaletteColor.lab(l = l, a = a, b = b, name = name)
                                } catch (_: Throwable) {
                                    null
                                }
                            } else null
                        }

                        "gray", "grey" -> {
                            if (components.isNotEmpty()) {
                                try {
                                    PaletteColor.gray(white = components[0], name = name)
                                } catch (_: Throwable) {
                                    null
                                }
                            } else null
                        }

                        else -> {
                            // Если указан colorspace (cs) и он описан в секции colorspaces,
                            // берём первый цвет из описанной colorspace как шаблон.
                            if (!cs.isNullOrEmpty()) {
                                colorspaces.find { it.name == cs }?.colors?.firstOrNull()
                                    ?.let { existingColor ->
                                        try {
                                            PaletteColor(
                                                name = name,
                                                colorSpace = existingColor.colorSpace,
                                                colorComponents = existingColor.colorComponents.toMutableList(),
                                                alpha = existingColor.alpha,
                                                colorType = existingColor.colorType
                                            )
                                        } catch (_: Throwable) {
                                            null
                                        }
                                    }
                            } else null
                        }
                    }

                    if (color != null) {
                        if (isInColorspaceSection) {
                            colorspaces.lastOrNull()?.colors?.add(color)
                        } else {
                            if (currentGroup == null) currentGroup = ColorGroup()
                            currentGroup?.let {
                                currentGroup = it.copy(
                                    colors = it.colors + color
                                )
                            }
                        }
                    }
                }
            }
        }

        override fun endElement(uri: String?, localName: String, qName: String?) {
            when (elmName(localName, qName).lowercase()) {
                "page" -> {
                    currentGroup?.let { group ->
                        if (group.colors.isNotEmpty()) {
                            if (group.name.isEmpty() && palette.colors.isEmpty() && palette.groups.isEmpty()) {
                                palette.colors.addAll(group.colors)
                            } else if (group.name.isNotEmpty()) {
                                palette.groups.add(group)
                            } else {
                                palette.colors.addAll(group.colors)
                            }
                        }
                    }
                    currentGroup = null
                }

                "colors" -> isInColorsSection = false
                "colorspaces" -> isInColorspaceSection = false
            }
            currentChars.clear()
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            currentChars.appendRange(ch, start, start + length)
        }
    }

    override fun decode(input: InputStream): Palette {
        val handler = CorelXMLHandler()
        val factory = SAXParserFactory.newInstance()
        factory.isNamespaceAware = true
        factory.isValidating = false
        val parser = factory.newSAXParser()
        parser.parse(input, handler)

        if (handler.palette.colors.isEmpty() && handler.palette.groups.isNotEmpty()) {
            val firstGroup = handler.palette.groups[0]
            handler.palette.colors.addAll(firstGroup.colors)
            handler.palette.groups.removeAt(0)
        }

        if (handler.palette.colors.isEmpty() && handler.palette.groups.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        return handler.palette.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\"?>\n")
        sb.append("<palette guid=\"${java.util.UUID.randomUUID()}\"")
        if (palette.name.isNotEmpty()) sb.append(" name=\"${palette.name.xmlEscaped()}\"")
        sb.append(">\n")
        sb.append("<colors>\n")

        if (palette.colors.isNotEmpty()) {
            sb.append(pageData(name = "", colors = palette.colors))
        }

        palette.groups.forEach { group ->
            sb.append(pageData(name = group.name, colors = group.colors))
        }

        sb.append("</colors>\n")
        sb.append("</palette>\n")

        output.write(sb.toString().toByteArray(java.nio.charset.StandardCharsets.UTF_8))
    }

    private fun pageData(name: String, colors: List<PaletteColor>): String {
        val locale = Locale.US
        val result = StringBuilder()
        result.append("<page")
        if (name.isNotEmpty()) result.append(" name=\"${name.xmlEscaped()}\"")
        result.append(">")
        colors.forEach { color ->
            result.append("<color")
            if (color.name.isNotEmpty()) result.append(" name=\"${color.name.xmlEscaped()}\"")

            val (cs, tints) = when (color.colorSpace) {
                ColorSpace.CMYK -> {
                    "CMYK" to color.colorComponents.joinToString(",") { "%.6f".format(locale, it) }
                }

                ColorSpace.RGB -> {
                    "RGB" to color.colorComponents.joinToString(",") { "%.6f".format(locale, it) }
                }

                ColorSpace.Gray -> {
                    "GRAY" to color.colorComponents.joinToString(",") { "%.6f".format(locale, it) }
                }

                ColorSpace.LAB -> {
                    if (color.colorComponents.size < 3) {
                        "LAB" to ""
                    } else {
                        "LAB" to listOf(
                            color.colorComponents[0] / 100.0,
                            (color.colorComponents[1] + 128.0) / 256.0,
                            (color.colorComponents[2] + 128.0) / 256.0
                        ).joinToString(",") { "%.6f".format(locale, it) }
                    }
                }

            }

            result.append(" cs=\"$cs\"")
            if (tints.isNotEmpty()) result.append(" tints=\"$tints\"")
            result.append("/>\n")
        }
        result.append("</page>\n")
        return result.toString()
    }
}