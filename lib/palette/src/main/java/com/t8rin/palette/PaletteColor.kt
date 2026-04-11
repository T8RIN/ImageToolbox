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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.t8rin.palette.utils.extractHexRGBA
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * A color in the palette
 */
@Serializable
data class PaletteColor(
    val name: String = "",
    val colorType: ColorType = ColorType.Global,
    val colorSpace: ColorSpace,
    val colorComponents: List<Double>,
    val alpha: Double = 1.0
) {
    val id: String = UUID.randomUUID().toString()

    init {
        checkValidity()
    }

    /**
     * Returns true if the underlying color structure is valid for its colorspace
     */
    val isValid: Boolean
        get() = when (colorSpace) {
            ColorSpace.CMYK -> colorComponents.size == 4
            ColorSpace.RGB -> colorComponents.size == 3
            ColorSpace.LAB -> colorComponents.size == 3
            ColorSpace.Gray -> colorComponents.size == 1
        }

    /**
     * Throws an error if the color is in an invalid state
     */
    fun checkValidity() {
        if (!isValid) {
            throw PaletteCoderException.InvalidColorComponentCountForModelType()
        }
    }

    /**
     * Convert to Jetpack Compose Color (ARGB Int)
     */
    fun toComposeColor(): Color {
        return when (colorSpace) {
            ColorSpace.RGB -> {
                Color(
                    red = colorComponents[0].coerceIn(0.0, 1.0).toFloat(),
                    green = colorComponents[1].coerceIn(0.0, 1.0).toFloat(),
                    blue = colorComponents[2].coerceIn(0.0, 1.0).toFloat(),
                    alpha = alpha.coerceIn(0.0, 1.0).toFloat()
                )
            }

            ColorSpace.CMYK -> {
                // Convert CMYK to RGB
                val c = colorComponents[0].coerceIn(0.0, 1.0).toFloat()
                val m = colorComponents[1].coerceIn(0.0, 1.0).toFloat()
                val y = colorComponents[2].coerceIn(0.0, 1.0).toFloat()
                val k = colorComponents[3].coerceIn(0.0, 1.0).toFloat()

                Color(
                    red = (1f - c) * (1f - k),
                    green = (1f - m) * (1f - k),
                    blue = (1f - y) * (1f - k),
                    alpha = alpha.coerceIn(0.0, 1.0).toFloat()
                )
            }

            ColorSpace.Gray -> {
                val gray = colorComponents[0].coerceIn(0.0, 1.0).toFloat()
                val a = alpha.coerceIn(0.0, 1.0).toFloat()
                Color(
                    red = gray,
                    green = gray,
                    blue = gray,
                    alpha = a
                )
            }

            ColorSpace.LAB -> {
                // Convert LAB to RGB (simplified)

                Color(
                    ColorUtils.LABToColor(
                        colorComponents[0],
                        colorComponents[1],
                        colorComponents[2]
                    )
                ).copy(
                    alpha = alpha.coerceIn(0.0, 1.0).toFloat()
                )
            }
        }
    }

    /**
     * Convert to ARGB Int
     */
    fun toArgb(): Int = toComposeColor().toArgb()

    /**
     * Create from ARGB Int
     */
    companion object Companion {
        /**
         * Create RGB color
         */
        fun rgb(
            r: Double,
            g: Double,
            b: Double,
            a: Double = 1.0,
            name: String = "",
            colorType: ColorType = ColorType.Global
        ): PaletteColor {
            return PaletteColor(
                name = name,
                colorType = colorType,
                colorSpace = ColorSpace.RGB,
                colorComponents = listOf(
                    r.coerceIn(0.0, 1.0),
                    g.coerceIn(0.0, 1.0),
                    b.coerceIn(0.0, 1.0)
                ),
                alpha = a.coerceIn(0.0, 1.0)
            )
        }

        /**
         * Create CMYK color
         */
        fun cmyk(
            c: Double,
            m: Double,
            y: Double,
            k: Double,
            alpha: Double = 1.0,
            name: String = "",
            colorType: ColorType = ColorType.Global
        ): PaletteColor {
            return PaletteColor(
                name = name,
                colorType = colorType,
                colorSpace = ColorSpace.CMYK,
                colorComponents = listOf(
                    c.coerceIn(0.0, 1.0),
                    m.coerceIn(0.0, 1.0),
                    y.coerceIn(0.0, 1.0),
                    k.coerceIn(0.0, 1.0)
                ),
                alpha = alpha.coerceIn(0.0, 1.0)
            )
        }

        /**
         * Create Gray color
         */
        fun gray(
            white: Double,
            alpha: Double = 1.0,
            name: String = "",
            colorType: ColorType = ColorType.Global
        ): PaletteColor {
            return PaletteColor(
                name = name,
                colorType = colorType,
                colorSpace = ColorSpace.Gray,
                colorComponents = listOf(white.coerceIn(0.0, 1.0)),
                alpha = alpha.coerceIn(0.0, 1.0)
            )
        }

        /**
         * Create LAB color
         */
        fun lab(
            l: Double,
            a: Double,
            b: Double,
            alpha: Double = 1.0,
            name: String = "",
            colorType: ColorType = ColorType.Global
        ): PaletteColor {
            return PaletteColor(
                name = name,
                colorType = colorType,
                colorSpace = ColorSpace.LAB,
                colorComponents = listOf(l, a, b),
                alpha = alpha.coerceIn(0.0, 1.0)
            )
        }

        /**
         * Create HSB color (converts to RGB)
         * @param hf Hue (0.0 ... 1.0)
         * @param sf Saturation (0.0 ... 1.0)
         * @param bf Brightness (0.0 ... 1.0)
         */
        fun hsb(
            hf: Double,
            sf: Double,
            bf: Double,
            alpha: Double = 1.0,
            name: String = "",
            colorType: ColorType = ColorType.Global
        ): PaletteColor {
            val h = hf.coerceIn(0.0, 1.0)
            val s = sf.coerceIn(0.0, 1.0)
            val b = bf.coerceIn(0.0, 1.0)

            // Convert HSB to RGB
            val c = b * s
            val x = c * (1 - kotlin.math.abs((h * 6) % 2 - 1))
            val m = b - c

            val (r, g, bl) = when {
                h < 1.0 / 6 -> Triple(c, x, 0.0)
                h < 2.0 / 6 -> Triple(x, c, 0.0)
                h < 3.0 / 6 -> Triple(0.0, c, x)
                h < 4.0 / 6 -> Triple(0.0, x, c)
                h < 5.0 / 6 -> Triple(x, 0.0, c)
                else -> Triple(c, 0.0, x)
            }

            return rgb(
                r = (r + m).coerceIn(0.0, 1.0),
                g = (g + m).coerceIn(0.0, 1.0),
                b = (bl + m).coerceIn(0.0, 1.0),
                a = alpha,
                name = name,
                colorType = colorType
            )
        }

        /**
         * Create HSL color (converts to RGB)
         * @param hf Hue (0.0 ... 1.0)
         * @param sf Saturation (0.0 ... 1.0)
         * @param lf Lightness (0.0 ... 1.0)
         */
        fun hsl(
            hf: Double,
            sf: Double,
            lf: Double,
            alpha: Double = 1.0,
            name: String = "",
            colorType: ColorType = ColorType.Global
        ): PaletteColor {
            val h = hf.coerceIn(0.0, 1.0)
            val s = sf.coerceIn(0.0, 1.0)
            val l = lf.coerceIn(0.0, 1.0)

            // Convert HSL to RGB
            val c = (1 - kotlin.math.abs(2 * l - 1)) * s
            val x = c * (1 - kotlin.math.abs((h * 6) % 2 - 1))
            val m = l - c / 2

            val (r, g, b) = when {
                h < 1.0 / 6 -> Triple(c, x, 0.0)
                h < 2.0 / 6 -> Triple(x, c, 0.0)
                h < 3.0 / 6 -> Triple(0.0, c, x)
                h < 4.0 / 6 -> Triple(0.0, x, c)
                h < 5.0 / 6 -> Triple(x, 0.0, c)
                else -> Triple(c, 0.0, x)
            }

            return rgb(
                r = (r + m).coerceIn(0.0, 1.0),
                g = (g + m).coerceIn(0.0, 1.0),
                b = (b + m).coerceIn(0.0, 1.0),
                a = alpha,
                name = name,
                colorType = colorType
            )
        }

        /**
         * Create color with white (gray) value
         */
        fun white(
            white: Double,
            alpha: Double = 1.0,
            name: String = "",
            colorType: ColorType = ColorType.Global
        ): PaletteColor {
            return gray(white, alpha, name, colorType)
        }

        /**
         * Generate a random color
         */
        fun random(
            colorSpace: ColorSpace = ColorSpace.RGB,
            name: String = "",
            colorType: ColorType = ColorType.Global
        ): PaletteColor {
            return when (colorSpace) {
                ColorSpace.RGB -> rgb(
                    r = kotlin.random.Random.nextDouble(0.0, 1.0),
                    g = kotlin.random.Random.nextDouble(0.0, 1.0),
                    b = kotlin.random.Random.nextDouble(0.0, 1.0),
                    name = name,
                    colorType = colorType
                )

                ColorSpace.CMYK -> cmyk(
                    c = kotlin.random.Random.nextDouble(0.0, 1.0),
                    m = kotlin.random.Random.nextDouble(0.0, 1.0),
                    y = kotlin.random.Random.nextDouble(0.0, 1.0),
                    k = kotlin.random.Random.nextDouble(0.0, 1.0),
                    name = name,
                    colorType = colorType
                )

                ColorSpace.Gray -> gray(
                    white = kotlin.random.Random.nextDouble(0.0, 1.0),
                    name = name,
                    colorType = colorType
                )

                ColorSpace.LAB -> throw PaletteCoderException.NotImplemented()
            }
        }
    }

    /**
     * Return a copy with modified alpha
     */
    fun withAlpha(newAlpha: Double): PaletteColor {
        return copy(alpha = newAlpha.coerceIn(0.0, 1.0))
    }

    /**
     * Return a copy with modified name
     */
    fun named(newName: String): PaletteColor {
        return copy(name = newName)
    }

    /**
     * Convert color to another colorspace
     */
    fun converted(colorspace: ColorSpace): PaletteColor {
        if (this.colorSpace == colorspace) return this

        return when (colorspace) {
            ColorSpace.CMYK -> {
                val cmyk = toCmyk()
                cmyk(
                    c = cmyk.cf,
                    m = cmyk.mf,
                    y = cmyk.yf,
                    k = cmyk.kf,
                    alpha = cmyk.af,
                    name = name,
                    colorType = colorType
                )
            }

            ColorSpace.RGB -> {
                val rgb = toRgb()
                rgb(
                    r = rgb.rf,
                    g = rgb.gf,
                    b = rgb.bf,
                    a = rgb.af,
                    name = name,
                    colorType = colorType
                )
            }

            ColorSpace.LAB -> {
                val lab = toLab()
                lab(
                    l = lab.l,
                    a = lab.a,
                    b = lab.b,
                    alpha = lab.alpha,
                    name = name,
                    colorType = colorType
                )
            }

            ColorSpace.Gray -> {
                val rgb = toRgb()

                val gray = 0.299 * rgb.rf + 0.587 * rgb.gf + 0.114 * rgb.bf
                gray(
                    white = gray,
                    alpha = rgb.af,
                    name = name,
                    colorType = colorType
                )
            }
        }
    }

    /**
     * Convert color to RGB components
     */
    fun toRgb(): RGB {
        return when (colorSpace) {
            ColorSpace.RGB -> RGB(
                r = colorComponents[0],
                g = colorComponents[1],
                b = colorComponents[2],
                a = alpha
            )

            ColorSpace.CMYK -> {
                val c = colorComponents[0]
                val m = colorComponents[1]
                val y = colorComponents[2]
                val k = colorComponents[3]
                RGB(
                    r = (1.0 - c) * (1.0 - k),
                    g = (1.0 - m) * (1.0 - k),
                    b = (1.0 - y) * (1.0 - k),
                    a = alpha
                )
            }

            ColorSpace.Gray -> {
                val gray = colorComponents[0]
                RGB(r = gray, g = gray, b = gray, a = alpha)
            }

            ColorSpace.LAB -> {
                val labToRgb = Color(
                    ColorUtils.LABToColor(
                        colorComponents[0],
                        colorComponents[1],
                        colorComponents[2]
                    )
                )
                RGB(
                    r = labToRgb.red.toDouble(),
                    g = labToRgb.green.toDouble(),
                    b = labToRgb.blue.toDouble(),
                    a = alpha
                )
            }
        }
    }

    /**
     * Convert color to CMYK components
     */
    fun toCmyk(): CMYK {
        val cmyk = if (colorSpace == ColorSpace.CMYK) {
            this.colorComponents
        } else {
            val rgb = toRgb()
            val r = rgb.rf
            val g = rgb.gf
            val b = rgb.bf

            val k = 1.0 - maxOf(r, g, b)
            val c = if (k < 1.0) (1.0 - r - k) / (1.0 - k) else 0.0
            val m = if (k < 1.0) (1.0 - g - k) / (1.0 - k) else 0.0
            val y = if (k < 1.0) (1.0 - b - k) / (1.0 - k) else 0.0

            listOf(c, m, y, k)
        }
        return CMYK(
            c = cmyk[0],
            m = cmyk[1],
            y = cmyk[2],
            k = cmyk[3],
            a = alpha
        )
    }

    /**
     * Convert color to LAB components
     */
    fun toLab(): LAB {
        val lab = if (colorSpace == ColorSpace.LAB) this.colorComponents else {
            val arr = DoubleArray(3)
            ColorUtils.colorToLAB(toArgb(), arr)
            arr.toList()
        }
        return LAB(
            l = lab[0],
            a = lab[1],
            b = lab[2],
            alpha = alpha
        )
    }

    /**
     * Convert color to HSB components
     */
    fun toHsb(): HSB {
        val rgb = toRgb()
        val r = rgb.rf
        val g = rgb.gf
        val b = rgb.bf

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        val h = when {
            delta == 0.0 -> 0.0
            max == r -> ((g - b) / delta) % 6.0 * 60.0
            max == g -> ((b - r) / delta + 2.0) * 60.0
            else -> ((r - g) / delta + 4.0) * 60.0
        }.let { if (it < 0) it + 360.0 else it } / 360.0

        val s = if (max == 0.0) 0.0 else delta / max

        return HSB(
            h = h,
            s = s,
            b = max,
            a = alpha
        )
    }

    /**
     * RGB color components
     */
    data class RGB(
        val r: Double,
        val g: Double,
        val b: Double,
        val a: Double = 1.0
    ) {
        val rf: Double get() = r.coerceIn(0.0, 1.0)
        val gf: Double get() = g.coerceIn(0.0, 1.0)
        val bf: Double get() = b.coerceIn(0.0, 1.0)
        val af: Double get() = a.coerceIn(0.0, 1.0)

        val r255: Int get() = (rf * 255).toInt()
        val g255: Int get() = (gf * 255).toInt()
        val b255: Int get() = (bf * 255).toInt()
        val a255: Int get() = (af * 255).toInt()
    }

    /**
     * CMYK color components
     */
    data class CMYK(
        val c: Double,
        val m: Double,
        val y: Double,
        val k: Double,
        val a: Double = 1.0
    ) {
        val cf: Double get() = c.coerceIn(0.0, 1.0)
        val mf: Double get() = m.coerceIn(0.0, 1.0)
        val yf: Double get() = y.coerceIn(0.0, 1.0)
        val kf: Double get() = k.coerceIn(0.0, 1.0)
        val af: Double get() = a.coerceIn(0.0, 1.0)
    }

    /**
     * LAB color components
     */
    data class LAB(
        val l: Double,
        val a: Double,
        val b: Double,
        val alpha: Double = 1.0
    )

    /**
     * HSB color components
     */
    data class HSB(
        val h: Double,
        val s: Double,
        val b: Double,
        val a: Double = 1.0
    ) {
        val hf: Double get() = h.coerceIn(0.0, 1.0)
        val sf: Double get() = s.coerceIn(0.0, 1.0)
        val bf: Double get() = b.coerceIn(0.0, 1.0)
        val af: Double get() = a.coerceIn(0.0, 1.0)
    }
}

/**
 * Create from hex string
 */
fun PaletteColor(
    rgbHexString: String,
    format: ColorByteFormat = ColorByteFormat.RGBA,
    name: String = "",
    colorType: ColorType = ColorType.Normal
): PaletteColor {
    val rgb = extractHexRGBA(rgbHexString, format)
        ?: throw PaletteCoderException.InvalidRGBHexString(rgbHexString)

    return PaletteColor(
        name = name,
        colorType = colorType,
        colorSpace = ColorSpace.RGB,
        colorComponents = listOf(rgb.rf, rgb.gf, rgb.bf),
        alpha = rgb.af
    )
}

/**
 * Create from CMYK hex string
 */
fun PaletteColor(
    cmykHexString: String,
    name: String = "",
    colorType: ColorType = ColorType.Normal
): PaletteColor {
    var hex = cmykHexString.lowercase().replace(Regex("[^0-9a-f]"), "")
    if (hex.startsWith("0x") || hex.startsWith("#")) {
        hex = hex.substring(2)
    }
    if (hex.startsWith("#")) {
        hex = hex.substring(1)
    }

    if (hex.length != 8) {
        throw PaletteCoderException.InvalidFormat()
    }

    val `val` = hex.toLongOrNull(16) ?: throw PaletteCoderException.InvalidFormat()

    val c = ((`val` shr 24) and 0xFF) / 255.0
    val m = ((`val` shr 16) and 0xFF) / 255.0
    val y = ((`val` shr 8) and 0xFF) / 255.0
    val k = (`val` and 0xFF) / 255.0

    return PaletteColor(
        name = name,
        colorType = colorType,
        colorSpace = ColorSpace.CMYK,
        colorComponents = listOf(c, m, y, k),
        alpha = 1.0
    )
}

/**
 * Create from CMYK hex string
 */
fun PaletteColor(
    color: Color,
    name: String = "",
    colorType: ColorType = ColorType.Global
): PaletteColor = PaletteColor(
    name = name,
    colorType = colorType,
    colorSpace = ColorSpace.RGB,
    colorComponents = listOf(
        color.red.toDouble(),
        color.green.toDouble(),
        color.blue.toDouble()
    ),
    alpha = color.alpha.toDouble()
)

/**
 * Create PaletteColor from Jetpack Compose Color
 */
fun Color.toPaletteColor(
    name: String = "",
    colorType: ColorType = ColorType.Global
): PaletteColor = PaletteColor(
    color = this,
    name = name,
    colorType = colorType
)