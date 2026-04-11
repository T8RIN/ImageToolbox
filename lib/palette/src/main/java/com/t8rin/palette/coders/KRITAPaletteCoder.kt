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
import com.t8rin.palette.ColorType
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
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.xml.parsers.SAXParserFactory
import kotlin.math.ceil
import kotlin.math.sqrt

/**
 * KRITA Palette (KPL) coder
 * KPL is a ZIP archive containing a mimetype entry, colorset.xml and profiles.xml.
 */
class KRITAPaletteCoder : PaletteCoder {

    private data class PositionedColor(
        val row: Int,
        val column: Int,
        val color: PaletteColor
    )

    private class ColorsetHandler : DefaultHandler() {
        val palette = Palette.Builder()

        private data class GroupState(
            val name: String,
            val colors: MutableList<PositionedColor> = mutableListOf()
        )

        private val globalColors = mutableListOf<PositionedColor>()
        private var currentGroup: GroupState? = null
        private var currentEntryName = ""
        private var currentEntrySpot = false
        private var currentEntryColor: PaletteColor? = null
        private var currentEntryRow = Int.MAX_VALUE
        private var currentEntryColumn = Int.MAX_VALUE

        private fun elementName(localName: String, qName: String?): String =
            localName.ifBlank { qName ?: "" }.substringAfter(':').trim().lowercase()

        override fun startElement(
            uri: String?,
            localName: String,
            qName: String?,
            attributes: Attributes
        ) {
            when (elementName(localName, qName)) {
                "colorset" -> {
                    val name = attributes.getValue("name")?.xmlDecoded()
                    if (!name.isNullOrEmpty()) {
                        palette.name = name
                    }
                }

                "group" -> {
                    currentGroup = GroupState(
                        name = attributes.getValue("name")?.xmlDecoded().orEmpty()
                    )
                }

                "colorsetentry" -> {
                    currentEntryName = attributes.getValue("name")?.xmlDecoded().orEmpty()
                    currentEntrySpot = attributes.getValue("spot")
                        ?.equals("true", ignoreCase = true) == true
                    currentEntryColor = null
                    currentEntryRow = Int.MAX_VALUE
                    currentEntryColumn = Int.MAX_VALUE
                }

                "position" -> {
                    currentEntryRow = attributes.getValue("row")?.toIntOrNull() ?: Int.MAX_VALUE
                    currentEntryColumn =
                        attributes.getValue("column")?.toIntOrNull() ?: Int.MAX_VALUE
                }

                "srgb", "rgb" -> {
                    currentEntryColor = createRgbColor(attributes)
                }

                "cmyk" -> {
                    currentEntryColor = createCmykColor(attributes)
                }

                "lab" -> {
                    currentEntryColor = createLabColor(attributes)
                }

                "gray" -> {
                    currentEntryColor = createGrayColor(attributes)
                }
            }
        }

        override fun endElement(uri: String?, localName: String, qName: String?) {
            when (elementName(localName, qName)) {
                "colorsetentry" -> {
                    currentEntryColor?.let { color ->
                        val positionedColor = PositionedColor(
                            row = currentEntryRow,
                            column = currentEntryColumn,
                            color = color
                        )
                        currentGroup?.colors?.add(positionedColor) ?: globalColors.add(
                            positionedColor
                        )
                    }

                    currentEntryColor = null
                    currentEntryName = ""
                    currentEntrySpot = false
                    currentEntryRow = Int.MAX_VALUE
                    currentEntryColumn = Int.MAX_VALUE
                }

                "group" -> {
                    currentGroup?.let { group ->
                        if (group.colors.isNotEmpty()) {
                            palette.groups.add(
                                ColorGroup(
                                    name = group.name,
                                    colors = group.colors.sorted().map { it.color }
                                )
                            )
                        }
                    }
                    currentGroup = null
                }
            }
        }

        fun buildPalette(): Palette {
            palette.colors = globalColors.sorted().map { it.color }.toMutableList()
            if (palette.colors.isEmpty() && palette.groups.isEmpty()) {
                throw PaletteCoderException.InvalidFormat()
            }
            return palette.build()
        }

        private fun createRgbColor(attributes: Attributes): PaletteColor? {
            val r = attributes.getValue("r")?.toDoubleOrNull() ?: return null
            val g = attributes.getValue("g")?.toDoubleOrNull() ?: return null
            val b = attributes.getValue("b")?.toDoubleOrNull() ?: return null

            return PaletteColor.rgb(
                r = r.coerceIn(0.0, 1.0),
                g = g.coerceIn(0.0, 1.0),
                b = b.coerceIn(0.0, 1.0),
                name = currentEntryName,
                colorType = currentEntryColorType()
            )
        }

        private fun createCmykColor(attributes: Attributes): PaletteColor? {
            val c = attributes.getValue("c")?.toDoubleOrNull() ?: return null
            val m = attributes.getValue("m")?.toDoubleOrNull() ?: return null
            val y = attributes.getValue("y")?.toDoubleOrNull() ?: return null
            val k = attributes.getValue("k")?.toDoubleOrNull() ?: return null

            return PaletteColor.cmyk(
                c = c.coerceIn(0.0, 1.0),
                m = m.coerceIn(0.0, 1.0),
                y = y.coerceIn(0.0, 1.0),
                k = k.coerceIn(0.0, 1.0),
                name = currentEntryName,
                colorType = currentEntryColorType()
            )
        }

        private fun createLabColor(attributes: Attributes): PaletteColor? {
            val l = (attributes.getValue("L") ?: attributes.getValue("l"))
                ?.toDoubleOrNull() ?: return null
            val a = attributes.getValue("a")?.toDoubleOrNull() ?: return null
            val b = attributes.getValue("b")?.toDoubleOrNull() ?: return null

            return PaletteColor.lab(
                l = l,
                a = a,
                b = b,
                name = currentEntryName,
                colorType = currentEntryColorType()
            )
        }

        private fun createGrayColor(attributes: Attributes): PaletteColor? {
            val gray = (attributes.getValue("g") ?: attributes.getValue("gray"))
                ?.toDoubleOrNull() ?: return null

            return PaletteColor.gray(
                white = gray.coerceIn(0.0, 1.0),
                name = currentEntryName,
                colorType = currentEntryColorType()
            )
        }

        private fun currentEntryColorType(): ColorType =
            if (currentEntrySpot) ColorType.Spot else ColorType.Normal

        private fun List<PositionedColor>.sorted(): List<PositionedColor> =
            sortedWith(compareBy({ it.row }, { it.column }))
    }

    private val numberFormatter = DecimalFormat("0.######", DecimalFormatSymbols(Locale.US))

    override fun decode(input: InputStream): Palette {
        var colorsetXmlData: ByteArray? = null

        ZipInputStream(input.buffered()).use { zipInputStream ->
            var entry = zipInputStream.nextEntry
            while (entry != null) {
                if (entry.name.equals("colorset.xml", ignoreCase = true) ||
                    entry.name.lowercase().endsWith("/colorset.xml")
                ) {
                    colorsetXmlData = zipInputStream.readBytes()
                    break
                }
                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }
        }

        val xmlData = colorsetXmlData ?: throw PaletteCoderException.InvalidFormat()
        val handler = ColorsetHandler()
        val factory = SAXParserFactory.newInstance()
        factory.isNamespaceAware = true
        factory.isValidating = false
        val parser = factory.newSAXParser()
        parser.parse(xmlData.inputStream(), handler)

        return handler.buildPalette()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        if (palette.totalColorCount == 0) {
            throw PaletteCoderException.TooFewColors()
        }

        val colorsetXml = buildColorsetXml(palette)
        val profilesXml = """<?xml version="1.0" encoding="UTF-8"?>
<Profiles/>
"""

        ZipOutputStream(output).use { zipOutputStream ->
            writeStoredEntry(
                zipOutputStream = zipOutputStream,
                name = "mimetype",
                data = KPL_MIME_TYPE.toByteArray(Charsets.US_ASCII)
            )
            writeTextEntry(zipOutputStream, "colorset.xml", colorsetXml)
            writeTextEntry(zipOutputStream, "profiles.xml", profilesXml)
        }
    }

    private fun buildColorsetXml(palette: Palette): String {
        val maxColorCount = maxOf(
            palette.colors.size,
            palette.groups.maxOfOrNull { it.colors.size } ?: 0,
            1
        )
        val columns = ceil(sqrt(maxColorCount.toDouble())).toInt().coerceAtLeast(1)

        return buildString {
            appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
            appendLine(
                """<Colorset name="${sanitizeXmlText(palette.name).xmlEscaped()}" comment="" columns="$columns" rows="${
                    rowCount(
                        palette.colors.size,
                        columns
                    )
                }" readonly="false" version="1.0">"""
            )

            appendColorEntries(
                colors = palette.colors,
                columns = columns,
                indent = "  ",
                idPrefix = "global"
            )

            palette.groups.forEachIndexed { index, group ->
                appendLine(
                    """  <Group name="${sanitizeXmlText(group.name).xmlEscaped()}" rows="${
                        rowCount(
                            group.colors.size,
                            columns
                        )
                    }">"""
                )
                appendColorEntries(
                    colors = group.colors,
                    columns = columns,
                    indent = "    ",
                    idPrefix = "group_${index + 1}"
                )
                appendLine("  </Group>")
            }

            appendLine("</Colorset>")
        }
    }

    private fun StringBuilder.appendColorEntries(
        colors: List<PaletteColor>,
        columns: Int,
        indent: String,
        idPrefix: String
    ) {
        colors.forEachIndexed { index, color ->
            val row = index / columns
            val column = index % columns
            val colorName = sanitizeXmlText(color.name)
            val colorId = sanitizeId("${idPrefix}_${index + 1}")
            val rgb = color.toRgb()

            appendLine(
                """${indent}<ColorSetEntry name="${colorName.xmlEscaped()}" id="$colorId" bitdepth="F32" spot="${color.colorType == ColorType.Spot}">"""
            )
            appendLine(
                """${indent}  <sRGB r="${formatUnit(rgb.rf)}" g="${formatUnit(rgb.gf)}" b="${
                    formatUnit(
                        rgb.bf
                    )
                }"/>"""
            )
            appendLine("""${indent}  <Position row="$row" column="$column"/>""")
            appendLine("${indent}</ColorSetEntry>")
        }
    }

    private fun writeStoredEntry(
        zipOutputStream: ZipOutputStream,
        name: String,
        data: ByteArray
    ) {
        val crc32 = CRC32().apply { update(data) }
        val entry = ZipEntry(name).apply {
            method = ZipEntry.STORED
            size = data.size.toLong()
            compressedSize = data.size.toLong()
            crc = crc32.value
        }
        zipOutputStream.putNextEntry(entry)
        zipOutputStream.write(data)
        zipOutputStream.closeEntry()
    }

    private fun writeTextEntry(
        zipOutputStream: ZipOutputStream,
        name: String,
        content: String
    ) {
        zipOutputStream.putNextEntry(ZipEntry(name))
        zipOutputStream.write(content.toByteArray(Charsets.UTF_8))
        zipOutputStream.closeEntry()
    }

    private fun formatUnit(value: Double): String = numberFormatter.format(value.coerceIn(0.0, 1.0))

    private fun rowCount(colorCount: Int, columns: Int): Int =
        if (colorCount <= 0) 0 else ceil(colorCount / columns.toDouble()).toInt()

    private fun sanitizeXmlText(value: String): String =
        value.replace(Regex("[\\r\\n\\t]+"), " ").trim()

    private fun sanitizeId(value: String): String =
        value.replace(Regex("[^A-Za-z0-9_.-]+"), "_").trim('_').ifEmpty { "color" }

    companion object {
        private const val KPL_MIME_TYPE = "application/x-krita-palette"
    }
}
