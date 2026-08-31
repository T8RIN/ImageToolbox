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

import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteColor
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.roundToInt

/**
 * Infinite Painter CLRS palette coder.
 *
 * Colors are stored as Android-style signed ARGB integers.
 */
class InfinitePainterPaletteCoder : PaletteCoder {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Serializable
    private data class InfinitePainterPalette(
        val colors: List<Int>,
        val name: String = ""
    )

    override fun decode(input: InputStream): Palette {
        val file = json.decodeFromString<InfinitePainterPalette>(
            input.bufferedReader().use { it.readText() }
        )

        return Palette(
            name = file.name,
            colors = file.colors.map { argb ->
                PaletteColor.rgb(
                    r = (argb ushr 16 and 0xFF) / 255.0,
                    g = (argb ushr 8 and 0xFF) / 255.0,
                    b = (argb and 0xFF) / 255.0,
                    a = (argb ushr 24 and 0xFF) / 255.0
                )
            }
        )
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val file = InfinitePainterPalette(
            name = palette.name,
            colors = palette.allColors().map { color ->
                val rgb = color.toRgb()
                val red = (rgb.rf * 255).roundToInt()
                val green = (rgb.gf * 255).roundToInt()
                val blue = (rgb.bf * 255).roundToInt()

                OPAQUE_ALPHA or (red shl 16) or (green shl 8) or blue
            }
        )

        output.bufferedWriter().use {
            it.write(json.encodeToString(InfinitePainterPalette.serializer(), file))
        }
    }

    private companion object {
        const val OPAQUE_ALPHA = 0xFF shl 24
    }
}
