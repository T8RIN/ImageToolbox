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
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.PaletteFormat
import com.t8rin.palette.decode
import com.t8rin.palette.encode
import com.t8rin.palette.getCoder
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaletteCoderCompatibilityTest {

    @Test
    fun `every readable format round trips a non-empty palette`() {
        val source = Palette(
            name = "Round Trip",
            colors = listOf(
                PaletteColor.rgb(1.0, 0.0, 0.0, name = "Red"),
                PaletteColor.rgb(0.0, 1.0, 0.0, name = "Green"),
                PaletteColor.rgb(0.0, 0.0, 1.0, name = "Blue"),
                PaletteColor.rgb(0.25, 0.5, 0.75, name = "Accent")
            )
        )
        val failures = PaletteFormat.entries
            .filterNot { it in JVM_UNREADABLE_FORMATS }
            .mapNotNull { format ->
                runCatching {
                    val encoded = format.getCoder().encode(source)
                    assertTrue(encoded.isNotEmpty(), "$format encoded an empty file")

                    val decoded = format.getCoder().decode(encoded)
                    assertTrue(
                        decoded.totalColorCount > 0,
                        "$format decoded its own output to an empty palette"
                    )
                }.exceptionOrNull()?.let { format to it }
            }

        assertTrue(
            failures.isEmpty(),
            failures.joinToString(",\n") { (format, error) ->
                "$format: ${error::class.simpleName}: ${error.message}"
            }
        )
    }

    @Test
    fun `every readable decoder rejects empty input`() {
        val acceptedFormats = PaletteFormat.entries
            .filterNot { it in JVM_UNREADABLE_FORMATS }
            .filter { format ->
                runCatching { format.getCoder().decode(byteArrayOf()) }.isSuccess
            }

        assertTrue(
            acceptedFormats.isEmpty(),
            "Decoders accepted an empty file: ${acceptedFormats.joinToString()}"
        )
    }

    @Test
    fun `encoded palettes are detected by filename without false positives`() {
        val source = Palette(
            name = "Detection",
            colors = List(20) { index ->
                PaletteColor.rgb(
                    r = (index % 5) / 4.0,
                    g = (index % 4) / 3.0,
                    b = (index % 3) / 2.0,
                    name = "Color $index"
                )
            }
        )
        val failures = PaletteFormat.entries
            .filterNot { it in JVM_UNREADABLE_FORMATS }
            .mapNotNull { expectedFormat ->
                runCatching {
                    val encoded = expectedFormat.getCoder().encode(source)
                    val extension = expectedFormat.fileExtension.first()
                    val detectedFormat = PaletteFormat.decodeOrderForFilename(
                        "PALETTE.${extension.uppercase()}"
                    ).firstOrNull { candidate ->
                        runCatching { candidate.getCoder().decode(encoded) }
                            .getOrNull()
                            ?.totalColorCount
                            ?.let { it > 0 } == true
                    }

                    assertEquals(expectedFormat, detectedFormat)
                }.exceptionOrNull()?.let { expectedFormat to it }
            }

        assertTrue(
            failures.isEmpty(),
            failures.joinToString(",\n") { (format, error) ->
                "$format: ${error::class.simpleName}: ${error.message}"
            }
        )
    }

    @Test
    fun `filename matching uses extension boundaries and returns all candidates`() {
        assertEquals(
            listOf(
                PaletteFormat.ANDROID_XML,
                PaletteFormat.BASIC_XML,
                PaletteFormat.COREL_DRAW,
                PaletteFormat.SCRIBUS_XML
            ),
            PaletteFormat.matchingFilename("Palette.XML")
        )
        assertTrue(PaletteFormat.matchingFilename("not-a-palette-pal").isEmpty())
    }

    @Test
    fun `krita decoder reads documented colorset structure`() {
        val colorsetXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Colorset name="Demo Palette" comment="" columns="2" rows="1" readonly="false" version="1.0">
              <ColorSetEntry name="Red" id="red_1" bitdepth="F32" spot="false">
                <Position row="0" column="0"/>
                <sRGB r="1.0" g="0.0" b="0.0"/>
              </ColorSetEntry>
              <Group name="Group One" rows="1">
                <ColorSetEntry name="Green Spot" id="green_1" bitdepth="F32" spot="true">
                  <Position row="0" column="0"/>
                  <CMYK c="1.0" m="0.0" y="1.0" k="0.0"/>
                </ColorSetEntry>
              </Group>
            </Colorset>
        """.trimIndent()

        val data = zipOf(
            "mimetype" to "application/x-krita-palette".toByteArray(Charsets.US_ASCII),
            "colorset.xml" to colorsetXml.toByteArray(Charsets.UTF_8),
            "profiles.xml" to """<?xml version="1.0" encoding="UTF-8"?><Profiles/>""".toByteArray(
                Charsets.UTF_8
            )
        )

        val palette = KRITAPaletteCoder().decode(data.inputStream())

        assertEquals("Demo Palette", palette.name)
        assertEquals(1, palette.colors.size)
        assertEquals("Red", palette.colors.first().name)
        assertEquals(1, palette.groups.size)
        assertEquals("Group One", palette.groups.first().name)
        assertEquals("Green Spot", palette.groups.first().colors.first().name)
        assertEquals(
            com.t8rin.palette.ColorType.Spot,
            palette.groups.first().colors.first().colorType
        )
    }

    @Test
    fun `krita encoder writes mimetype and documented xml`() {
        val palette = Palette(
            name = "Krita Export",
            colors = listOf(PaletteColor.rgb(1.0, 0.0, 0.0, name = "Red")),
            groups = listOf(
                ColorGroup(
                    name = "Group One",
                    colors = listOf(PaletteColor.cmyk(1.0, 0.0, 1.0, 0.0, name = "CMYK Color"))
                )
            )
        )

        val data = KRITAPaletteCoder().encode(palette)
        val entries = unzip(data)

        assertEquals("mimetype", entries.first().first)
        assertEquals(ZipEntry.STORED, entries.first().second.method)
        assertEquals(
            "application/x-krita-palette",
            entries.first().third.toString(Charsets.US_ASCII)
        )

        val colorsetXml =
            entries.first { it.first == "colorset.xml" }.third.toString(Charsets.UTF_8)
        assertContains(colorsetXml, "<Colorset")
        assertContains(colorsetXml, "<ColorSetEntry")
        assertContains(
            colorsetXml,
            "<sRGB r=\"1\" g=\"0\" b=\"0\"/>\n    <Position row=\"0\" column=\"0\"/>"
        )

        val decoded = KRITAPaletteCoder().decode(data.inputStream())
        assertEquals(2, decoded.totalColorCount)
        assertEquals("Krita Export", decoded.name)
        assertEquals("Group One", decoded.groups.first().name)
    }

    @Test
    fun `gimp encoder always writes version 2 header`() {
        val encoded = GIMPPaletteCoder().encode(
            Palette(
                colors = listOf(PaletteColor.rgb(1.0, 0.0, 0.0, name = "Primary\tRed"))
            )
        ).toString(Charsets.UTF_8)

        val lines = encoded.lines()
        assertEquals("GIMP Palette", lines[0])
        assertEquals("Name: ", lines[1])
        assertEquals("Columns: 0", lines[2])
        assertEquals("#Colors: 1", lines[3])
        assertEquals("255\t0\t0\tPrimary Red", lines[4])
    }

    @Test
    fun `basic xml decoder works without namespace aware parser`() {
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <palette name="Basic">
              <color name="Red" r="255" g="0" b="0" a="255" />
            </palette>
        """.trimIndent()

        val palette = BasicXMLCoder().decode(xml.byteInputStream())

        assertEquals("Basic", palette.name)
        assertEquals(1, palette.colors.size)
        assertEquals("Red", palette.colors.first().name)
    }

    @Test
    fun `android colors xml decoder reads plain resources`() {
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
              <color name="accent">#80FF0000</color>
            </resources>
        """.trimIndent()

        val palette = AndroidColorsXMLCoder().decode(xml.byteInputStream())

        assertEquals(1, palette.colors.size)
        assertEquals("accent", palette.colors.first().name)
    }

    @Test
    fun `act encoder stays within standard file size`() {
        val data = ACTPaletteCoder().encode(
            Palette(
                colors = listOf(PaletteColor.rgb(1.0, 0.0, 0.0, name = "Named Color"))
            )
        )

        assertEquals(772, data.size)
    }

    @Test
    fun `riff encoder writes little endian chunk size`() {
        val data = RIFFPaletteCoder().encode(
            Palette(
                colors = listOf(
                    PaletteColor.rgb(1.0, 0.0, 0.0, name = "Red"),
                    PaletteColor.rgb(0.0, 1.0, 0.0)
                )
            )
        )

        val riffSize = ByteBuffer.wrap(data, 4, 4).order(ByteOrder.LITTLE_ENDIAN).int
        assertEquals(data.size - 8, riffSize)
    }

    private fun zipOf(vararg entries: Pair<String, ByteArray>): ByteArray {
        val output = java.io.ByteArrayOutputStream()
        ZipOutputStream(output).use { zipOutputStream ->
            entries.forEachIndexed { index, (name, data) ->
                val entry = ZipEntry(name)
                if (index == 0 && name == "mimetype") {
                    val crc = CRC32().apply { update(data) }
                    entry.method = ZipEntry.STORED
                    entry.size = data.size.toLong()
                    entry.compressedSize = data.size.toLong()
                    entry.crc = crc.value
                }
                zipOutputStream.putNextEntry(entry)
                zipOutputStream.write(data)
                zipOutputStream.closeEntry()
            }
        }
        return output.toByteArray()
    }

    private fun unzip(data: ByteArray): List<Triple<String, ZipEntry, ByteArray>> {
        val entries = mutableListOf<Triple<String, ZipEntry, ByteArray>>()
        ZipInputStream(data.inputStream()).use { zipInputStream ->
            var entry = zipInputStream.nextEntry
            while (entry != null) {
                entries.add(Triple(entry.name, entry, zipInputStream.readBytes()))
                entry = zipInputStream.nextEntry
            }
        }
        return entries
    }

    private companion object {
        val JVM_UNREADABLE_FORMATS = setOf(
            PaletteFormat.SWIFT,
            PaletteFormat.KOTLIN,
            PaletteFormat.IMAGE
        )
    }
}
