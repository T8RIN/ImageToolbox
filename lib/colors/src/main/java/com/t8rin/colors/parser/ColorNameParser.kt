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

package com.t8rin.colors.parser

import android.content.Context
import android.util.JsonReader
import androidx.compose.ui.graphics.Color
import com.t8rin.colors.util.ColorUtil
import com.t8rin.colors.util.HexUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

interface ColorNameParser {
    val colorNames: Map<String, ColorWithName>

    fun parseColorName(color: Color): String
    fun parseColorFromName(name: String): List<ColorWithName>
    fun parseColorFromNameSingle(name: String): Color

    suspend fun init(
        context: Context,
        colorsAsset: String = "color_names.json"
    )

    companion object : ColorNameParser by ColorNameParserImpl
}

/**
 * Parses color name from [Color]
 */
private object ColorNameParserImpl : ColorNameParser {

    override val colorNames: MutableMap<String, ColorWithName> = mutableMapOf()

    /**
     * Parse name of [Color]
     */
    override fun parseColorName(color: Color): String {
        val hex = ColorUtil.colorToHex(color).uppercase().replace("#", "#FF")
        return colorNames[hex]?.name ?: run {
            val red = color.red
            val green = color.green
            val blue = color.blue

            colorNames.values.minByOrNull { color ->
                sqrt(
                    (color.red - red) * (color.red - red) +
                            (color.green - green) * (color.green - green) +
                            (color.blue - blue) * (color.blue - blue)
                )
            }?.name ?: "?????"
        }
    }

    override fun parseColorFromName(
        name: String
    ): List<ColorWithName> = colorNames.values.filter {
        it.name.contains(
            other = name,
            ignoreCase = true
        ) || name.contains(
            other = it.name,
            ignoreCase = true
        )
    }.ifEmpty {
        listOf(
            ColorWithName(
                color = Color.Black,
                name = "Black"
            )
        )
    }

    override fun parseColorFromNameSingle(name: String): Color {
        val normalizedName = name.trim().lowercase()
        val values = colorNames.values

        return values.firstOrNull { color ->
            color.name.lowercase() == normalizedName
        }?.color ?: values.firstOrNull { color ->
            color.name.lowercase().contains(normalizedName)
                    || normalizedName.contains(color.name.lowercase())
        }?.color ?: Color.Black
    }

    override suspend fun init(
        context: Context,
        colorsAsset: String
    ) = withContext(Dispatchers.IO) {
        try {
            JsonReader(context.assets.open(colorsAsset).bufferedReader()).use { reader ->
                reader.beginObject()

                while (reader.hasNext() && isActive) {
                    val hex = reader.nextName()
                    val name = reader.nextString()
                    val color = HexUtil.hexToColor(hex)

                    colorNames[hex] = ColorWithName(
                        color = color,
                        name = name
                    )
                }

                reader.endObject()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

}

data class ColorWithName(
    val color: Color,
    val name: String
) {
    val red get() = color.red
    val green get() = color.green
    val blue get() = color.blue
}