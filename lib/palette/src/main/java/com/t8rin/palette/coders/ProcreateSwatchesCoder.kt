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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Procreate Swatches coder
 */
class ProcreateSwatchesCoder : PaletteCoder {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Serializable
    private data class Swatch(
        val name: String? = null,
        val hue: Double,
        val saturation: Double,
        val brightness: Double,
        val alpha: Double = 1.0,
        val colorSpace: Int? = null,
        val origin: Int? = null,
        val colorModel: Int? = null,
        val colorProfile: String? = null,
        val version: String? = null,
        val components: List<Double>? = null
    )

    @Serializable
    private data class SwatchPalette(
        val name: String,
        val swatches: List<Swatch?>
    )

    override fun decode(input: InputStream): Palette {
        val data = input.readBytes()
        val zipInputStream = ZipInputStream(java.io.ByteArrayInputStream(data))

        var jsonData = ByteArray(0)
        var entry: ZipEntry? = zipInputStream.nextEntry
        while (entry != null) {
            if (entry.name == "Swatches.json") {
                jsonData = zipInputStream.readBytes()
                break
            }
            entry = zipInputStream.nextEntry
        }
        zipInputStream.close()

        if (jsonData.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        val jsonText = String(jsonData, java.nio.charset.StandardCharsets.UTF_8)

        val swatches: List<SwatchPalette> = try {
            json.decodeFromString<List<SwatchPalette>>(jsonText)
        } catch (_: Throwable) {
            // Try single palette
            try {
                listOf(json.decodeFromString(SwatchPalette.serializer(), jsonText))
            } catch (_: Throwable) {
                throw PaletteCoderException.InvalidFormat()
            }
        }

        val result = Palette.Builder()

        swatches.forEach { palette ->
            val groupColors = palette.swatches
                .filterNotNull()
                .map { swatch ->
                    val color = PaletteColor.hsb(
                        hf = swatch.hue,
                        sf = swatch.saturation,
                        bf = swatch.brightness,
                        alpha = swatch.alpha,
                        name = swatch.name ?: ""
                    )
                    color
                }

            if (groupColors.isNotEmpty()) {
                // Add colors to main palette if it's the first group or if palette name is empty
                if (result.colors.isEmpty() && result.groups.isEmpty()) {
                    result.colors.addAll(groupColors)
                    result.name = palette.name
                } else {
                    val group =
                        ColorGroup(colors = groupColors.toMutableList(), name = palette.name)
                    result.groups.add(group)
                }
            }
        }

        val palette = result.build()

        if (palette.totalColorCount == 0) {
            throw PaletteCoderException.InvalidFormat()
        }

        return palette
    }

    override fun encode(palette: Palette, output: OutputStream) {
        if (palette.totalColorCount == 0) {
            throw PaletteCoderException.TooFewColors()
        }

        // Map each group in the palette
        val groups =
            listOf(ColorGroup(colors = palette.colors, name = palette.name)) + palette.groups

        val mapped: List<SwatchPalette> = groups.mapNotNull { group ->
            if (group.colors.isEmpty()) {
                return@mapNotNull null
            }

            SwatchPalette(
                name = group.name,
                swatches = group.colors.map { color ->
                    val hsb = color.toHsb()
                    Swatch(
                        name = color.name.ifEmpty { null },
                        hue = hsb.hf,
                        saturation = hsb.sf,
                        brightness = hsb.bf,
                        alpha = hsb.af
                    )
                }
            )
        }

        val jsonData = json.encodeToString<List<SwatchPalette>>(mapped)

        // Create ZIP archive
        val zipOutputStream = ZipOutputStream(output)
        val entry = ZipEntry("Swatches.json")
        zipOutputStream.putNextEntry(entry)
        zipOutputStream.write(jsonData.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
        zipOutputStream.closeEntry()
        zipOutputStream.close()
    }
}

