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

package com.t8rin.palette

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Color grouping within a palette
 */
sealed class ColorGrouping {
    object Global : ColorGrouping()
    data class Group(val index: Int) : ColorGrouping()
}

/**
 * A color palette
 */
@Serializable
data class Palette(
    val name: String = "",
    val colors: List<PaletteColor> = listOf(),
    val groups: List<ColorGroup> = listOf()
) {
    val id: String = UUID.randomUUID().toString()

    /**
     * The total number of colors in the palette
     */
    val totalColorCount: Int
        get() = colors.size + groups.sumOf { it.colors.size }

    /**
     * Returns all the groups for the palette. Global colors are represented in a group called 'global'
     */
    val allGroups: List<ColorGroup>
        get() = listOf(ColorGroup(colors = colors, name = "global")) + groups

    /**
     * Returns all the colors in the palette as a flat array of colors (all group information is lost)
     */
    fun allColors(): List<PaletteColor> {
        val results = colors.toMutableList()
        groups.forEach { results.addAll(it.colors) }
        return results
    }

    /**
     * Find the first instance of a color by name within the palette
     */
    fun color(name: String, caseSensitive: Boolean = false): PaletteColor? {
        return if (caseSensitive) {
            allColors().firstOrNull { it.name == name }
        } else {
            val lowerName = name.lowercase()
            allColors().firstOrNull { it.name.lowercase() == lowerName }
        }
    }

    /**
     * Return an array of colors for the specified palette group type
     */
    fun colors(groupType: ColorGrouping): List<PaletteColor> {
        return when (groupType) {
            is ColorGrouping.Global -> colors
            is ColorGrouping.Group -> {
                if (groupType.index >= 0 && groupType.index < groups.size) {
                    groups[groupType.index].colors
                } else {
                    throw PaletteCoderException.IndexOutOfRange()
                }
            }
        }
    }

    /**
     * An index for a color within the palette
     */
    data class ColorIndex(
        val group: ColorGrouping = ColorGrouping.Global,
        val colorIndex: Int
    )

    /**
     * Retrieve a color from the palette
     */
    fun color(group: ColorGrouping = ColorGrouping.Global, colorIndex: Int): PaletteColor {
        return when (group) {
            is ColorGrouping.Global -> {
                if (colorIndex >= 0 && colorIndex < colors.size) {
                    colors[colorIndex]
                } else {
                    throw PaletteCoderException.IndexOutOfRange()
                }
            }

            is ColorGrouping.Group -> {
                if (group.index >= 0 && group.index < groups.size &&
                    colorIndex >= 0 && colorIndex < groups[group.index].colors.size
                ) {
                    groups[group.index].colors[colorIndex]
                } else {
                    throw PaletteCoderException.IndexOutOfRange()
                }
            }
        }
    }

    /**
     * Retrieve a color from the palette using ColorIndex
     */
    fun color(index: ColorIndex): PaletteColor {
        return color(index.group, index.colorIndex)
    }

    /**
     * Update a color
     */
    fun updateColor(
        group: ColorGrouping = ColorGrouping.Global,
        colorIndex: Int,
        color: PaletteColor
    ): Palette {
        val builder = newBuilder()
        when (group) {
            is ColorGrouping.Global -> {
                if (colorIndex >= 0 && colorIndex < colors.size) {
                    builder.colors[colorIndex] = color
                } else {
                    throw PaletteCoderException.IndexOutOfRange()
                }
            }

            is ColorGrouping.Group -> {
                if (group.index >= 0 && group.index < groups.size &&
                    colorIndex >= 0 && colorIndex < groups[group.index].colors.size
                ) {
                    builder.groups[group.index] = groups[group.index].copy(
                        colors = colors.toMutableList().apply {
                            set(colorIndex, color)
                        }
                    )
                } else {
                    throw PaletteCoderException.IndexOutOfRange()
                }
            }
        }

        return builder.build()
    }

    /**
     * Update a color using ColorIndex
     */
    fun updateColor(index: ColorIndex, color: PaletteColor): Palette =
        updateColor(index.group, index.colorIndex, color)

    /**
     * Returns a bucketed color for a time value mapped within an evenly spaced array of colors
     */
    fun bucketedColor(at: Double, type: ColorGrouping = ColorGrouping.Global): PaletteColor {
        val colorList = colors(type)
        if (colorList.isEmpty()) throw PaletteCoderException.TooFewColors()

        val clampedT = at.coerceIn(0.0, 1.0)
        val index = (clampedT * (colorList.size - 1)).toInt().coerceIn(0, colorList.size - 1)
        return colorList[index]
    }

    /**
     * Returns an interpolated color for a time value mapped within an evenly spaced array of colors
     */
    fun interpolatedColor(at: Double, type: ColorGrouping = ColorGrouping.Global): PaletteColor {
        val colorList = colors(type)
        if (colorList.isEmpty()) throw PaletteCoderException.TooFewColors()
        if (colorList.size == 1) return colorList[0]

        val clampedT = at.coerceIn(0.0, 1.0)
        val position = clampedT * (colorList.size - 1)
        val index = position.toInt().coerceIn(0, colorList.size - 2)
        val fraction = position - index

        val c1 = colorList[index]
        val c2 = colorList[index + 1]

        // Simple linear interpolation in RGB space
        val rgb1 = c1.toRgb()
        val rgb2 = c2.toRgb()

        return PaletteColor.rgb(
            r = rgb1.rf + (rgb2.rf - rgb1.rf) * fraction,
            g = rgb1.gf + (rgb2.gf - rgb1.gf) * fraction,
            b = rgb1.bf + (rgb2.bf - rgb1.bf) * fraction,
            a = rgb1.af + (rgb2.af - rgb1.af) * fraction
        )
    }

    companion object Companion {
        /**
         * Return a palette containing random colors
         */
        fun random(
            count: Int,
            colorSpace: ColorSpace = ColorSpace.RGB,
            colorType: ColorType = ColorType.Global
        ): Palette {
            require(count > 0) { "Count must be greater than 0" }
            return Palette(
                colors = (0 until count).map {
                    PaletteColor.random(
                        colorSpace = colorSpace,
                        colorType = colorType,
                        name = "Color_$it"
                    )
                }.toMutableList()
            )
        }

        /**
         * Create a palette by interpolating between two colors
         */
        fun interpolated(
            startColor: PaletteColor,
            endColor: PaletteColor,
            count: Int,
            useOkLab: Boolean = false,
            name: String = ""
        ): Palette {
            require(count > 0) { "Count must be greater than 0" }

            val colors = if (useOkLab) {
                // TODO: Implement OkLab interpolation
                simpleInterpolate(startColor, endColor, count)
            } else {
                simpleInterpolate(startColor, endColor, count)
            }

            return Palette(colors = colors.toMutableList(), name = name)
        }

        private fun simpleInterpolate(
            start: PaletteColor,
            end: PaletteColor,
            count: Int
        ): List<PaletteColor> {
            val startRgb = start.toRgb()
            val endRgb = end.toRgb()

            return (0 until count).map { i ->
                val t = if (count > 1) i / (count - 1.0) else 0.0
                PaletteColor.rgb(
                    r = startRgb.rf + (endRgb.rf - startRgb.rf) * t,
                    g = startRgb.gf + (endRgb.gf - startRgb.gf) * t,
                    b = startRgb.bf + (endRgb.bf - startRgb.bf) * t,
                    a = startRgb.af + (endRgb.af - startRgb.af) * t
                )
            }
        }
    }

    class Builder(
        var name: String = "",
        var colors: MutableList<PaletteColor> = mutableListOf(),
        var groups: MutableList<ColorGroup> = mutableListOf()
    ) {
        fun build() = Palette(
            name = name,
            colors = colors,
            groups = groups
        )
    }

    fun newBuilder(): Builder = Builder(
        name = name,
        colors = colors.toMutableList(),
        groups = groups.toMutableList()
    )
}