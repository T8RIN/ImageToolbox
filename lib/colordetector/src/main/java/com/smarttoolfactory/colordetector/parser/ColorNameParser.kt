package com.smarttoolfactory.colordetector.parser

import android.content.Context
import android.util.JsonReader
import androidx.compose.ui.graphics.Color
import com.smarttoolfactory.colordetector.model.ColorItem
import com.smarttoolfactory.colordetector.util.ColorUtil
import com.smarttoolfactory.colordetector.util.HexUtil
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
            }?.name ?: ColorItem.Unspecified
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