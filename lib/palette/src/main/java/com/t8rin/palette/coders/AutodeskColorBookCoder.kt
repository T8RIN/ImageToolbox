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
import java.nio.charset.StandardCharsets
import javax.xml.parsers.SAXParserFactory

class AutodeskColorBookCoder : PaletteCoder {

    private class AutodeskXMLHandler : DefaultHandler() {
        val palette = Palette.Builder()
        private var currentGroup: ColorGroup? = null
        private var colorName: String? = null
        private var r: Int? = null
        private var g: Int? = null
        private var b: Int? = null
        private val xmlStack = mutableListOf<String>()
        private var currentChars = StringBuilder()

        private fun elm(localName: String?, qName: String?) = (qName ?: localName ?: "").trim()

        override fun startElement(
            uri: String?,
            localName: String,
            qName: String?,
            attributes: Attributes
        ) {
            currentChars.clear()
            val elementName = elm(localName, qName)
            when (elementName.lowercase()) {
                "colorpage" -> {
                    val groupName = attributes.getValue("name")?.xmlDecoded() ?: ""
                    currentGroup = ColorGroup(name = groupName)
                }

                "colorentry", "pagecolor" -> {
                    r = null; g = null; b = null; colorName = null
                }
            }
            xmlStack.add(elementName)
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            currentChars.appendRange(ch, start, start + length)
        }

        override fun endElement(uri: String?, localName: String, qName: String?) {
            val elementName = elm(localName, qName)
            val content = currentChars.toString().trim()
            when (elementName.lowercase()) {
                "bookname" -> if (content.isNotEmpty()) palette.name = content
                "colorname" -> if (content.isNotEmpty()) colorName = content
                "red" -> r = content.toIntOrNull()?.coerceIn(0, 255)
                "green" -> g = content.toIntOrNull()?.coerceIn(0, 255)
                "blue" -> b = content.toIntOrNull()?.coerceIn(0, 255)
                "colorentry", "pagecolor" -> {
                    if (r != null && g != null && b != null) {
                        val color = PaletteColor.rgb(
                            r = r!! / 255.0,
                            g = g!! / 255.0,
                            b = b!! / 255.0,
                            name = colorName ?: ""
                        )
                        if (currentGroup == null) currentGroup = ColorGroup()
                        currentGroup?.let {
                            currentGroup = it.copy(
                                colors = it.colors + color
                            )
                        }
                    }
                    r = null; g = null; b = null; colorName = null
                }

                "colorpage" -> {
                    currentGroup?.let { group ->
                        if (group.colors.isNotEmpty()) {
                            if (palette.colors.isEmpty() && palette.groups.isEmpty()) {
                                palette.colors.addAll(group.colors)
                                if (group.name.isNotEmpty() && palette.name.isEmpty()) palette.name =
                                    group.name
                            } else {
                                palette.groups.add(
                                    if (group.name.isEmpty()) {
                                        group.copy(
                                            name = "Color Page ${palette.groups.size + 1}"
                                        )
                                    } else group
                                )
                            }
                        }
                    }
                    currentGroup = null
                }
            }
            if (xmlStack.isNotEmpty()) xmlStack.removeAt(xmlStack.size - 1)
            currentChars.clear()
        }
    }

    override fun decode(input: InputStream): Palette {
        return try {
            val handler = AutodeskXMLHandler()
            val factory = SAXParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newSAXParser()
            parser.parse(input, handler)

            // Если нет главного цвета, но есть группы, возьмём первую группу
            if (handler.palette.colors.isEmpty() && handler.palette.groups.isNotEmpty()) {
                val firstGroup = handler.palette.groups[0]
                handler.palette.colors.addAll(firstGroup.colors)
                handler.palette.groups.removeAt(0)
            }

            if (handler.palette.colors.isEmpty() && handler.palette.groups.isEmpty()) {
                throw PaletteCoderException.InvalidFormat()
            }

            handler.palette.build()
        } catch (_: Throwable) {
            // Не удалось распарсить — не падаем, возвращаем пустой palette
            Palette()
        }
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<colorBook>\n")
        val name = palette.name.ifEmpty { "Untitled" }
        sb.append("   <bookName>${name.xmlEscaped()}</bookName>\n")
        sb.append("   <majorVersion>1</majorVersion>\n")
        sb.append("   <minorVersion>0</minorVersion>\n")

        val allGroups = palette.allGroups
        allGroups.forEach { group ->
            if (group.colors.isEmpty()) return@forEach
            sb.append("   <colorPage>\n")
            group.colors.forEach { color ->
                val colorName = color.name.ifEmpty { "Color" }
                sb.append("      <colorEntry>\n")
                sb.append("         <colorName>${colorName.xmlEscaped()}</colorName>\n")
                sb.append(encodeColor(color))
                sb.append("      </colorEntry>\n")
            }
            sb.append("   </colorPage>\n")
        }
        sb.append("</colorBook>\n")
        output.write(sb.toString().toByteArray(StandardCharsets.UTF_8))
    }

    private fun encodeColor(color: PaletteColor): String {
        val rgb = color.toRgb()
        return """         <RGB8>
            <red>${rgb.r255}</red>
            <green>${rgb.g255}</green>
            <blue>${rgb.b255}</blue>
         </RGB8>
"""
    }
}
