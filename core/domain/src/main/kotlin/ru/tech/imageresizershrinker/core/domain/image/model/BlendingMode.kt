/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.domain.image.model

@JvmInline
value class BlendingMode internal constructor(val value: Int) {

    companion object {

        /**
         * Drop both the source and destination images, leaving nothing.
         */
        val Clear = BlendingMode(0)

        /**
         * Drop the destination image, only paint the source image.
         *
         * Conceptually, the destination is first cleared, then the source image is
         * painted.
         */
        val Src = BlendingMode(1)

        /**
         * Drop the source image, only paint the destination image.
         *
         * Conceptually, the source image is discarded, leaving the destination
         * untouched.
         */
        val Dst = BlendingMode(2)

        /**
         * Composite the source image over the destination image.
         *
         * This is the default value. It represents the most intuitive case, where
         * shapes are painted on top of what is below, with transparent areas showing
         * the destination layer.
         */
        val SrcOver = BlendingMode(3)

        /**
         * Composite the source image under the destination image.
         *
         * This is the opposite of [SrcOver].
         *
         * This is useful when the source image should have been painted before the
         * destination image, but could not be.
         */
        val DstOver = BlendingMode(4)

        /**
         * Show the source image, but only where the two images overlap. The
         * destination image is not rendered, it is treated merely as a mask. The
         * color channels of the destination are ignored, only the opacity has an
         * effect.
         *
         * To show the destination image instead, consider [DstIn].
         *
         * To reverse the semantic of the mask (only showing the source where the
         * destination is absent, rather than where it is present), consider
         * [SrcOut].
         */
        val SrcIn = BlendingMode(5)

        /**
         * Show the destination image, but only where the two images overlap. The
         * source image is not rendered, it is treated merely as a mask. The color
         * channels of the source are ignored, only the opacity has an effect.
         *
         * To show the source image instead, consider [SrcIn].
         *
         * To reverse the semantic of the mask (only showing the source where the
         * destination is present, rather than where it is absent), consider [DstOut].
         */
        val DstIn = BlendingMode(6)

        /**
         * Show the source image, but only where the two images do not overlap. The
         * destination image is not rendered, it is treated merely as a mask. The color
         * channels of the destination are ignored, only the opacity has an effect.
         *
         * To show the destination image instead, consider [DstOut].
         *
         * To reverse the semantic of the mask (only showing the source where the
         * destination is present, rather than where it is absent), consider [SrcIn].
         *
         * This corresponds to the "Source out Destination" Porter-Duff operator.
         */
        val SrcOut = BlendingMode(7)

        /**
         * Show the destination image, but only where the two images do not overlap. The
         * source image is not rendered, it is treated merely as a mask. The color
         * channels of the source are ignored, only the opacity has an effect.
         *
         * To show the source image instead, consider [SrcOut].
         *
         * To reverse the semantic of the mask (only showing the destination where the
         * source is present, rather than where it is absent), consider [DstIn].
         *
         * This corresponds to the "Destination out Source" Porter-Duff operator.
         */
        val DstOut = BlendingMode(8)

        /**
         * Composite the source image over the destination image, but only where it
         * overlaps the destination.
         *
         * This is essentially the [SrcOver] operator, but with the output's opacity
         * channel being set to that of the destination image instead of being a
         * combination of both image's opacity channels.
         *
         * For a variant with the destination on top instead of the source, see
         * [DstAtop].
         */
        val SrcAtop = BlendingMode(9)

        /**
         * Composite the destination image over the source image, but only where it
         * overlaps the source.
         *
         * This is essentially the [DstOver] operator, but with the output's opacity
         * channel being set to that of the source image instead of being a
         * combination of both image's opacity channels.
         *
         * For a variant with the source on top instead of the destination, see
         * [SrcAtop].
         */
        val DstAtop = BlendingMode(10)

        /**
         * Apply a bitwise `xor` operator to the source and destination images. This
         * leaves transparency where they would overlap.
         */
        val Xor = BlendingMode(11)

        /**
         * Sum the components of the source and destination images.
         *
         * Transparency in a pixel of one of the images reduces the contribution of
         * that image to the corresponding output pixel, as if the color of that
         * pixel in that image was darker.
         *
         */
        val Plus = BlendingMode(12)

        /**
         * Multiply the color components of the source and destination images.
         *
         * This can only result in the same or darker colors (multiplying by white,
         * 1.0, results in no change; multiplying by black, 0.0, results in black).
         *
         * When compositing two opaque images, this has similar effect to overlapping
         * two transparencies on a projector.
         *
         * For a variant that also multiplies the alpha channel, consider [Multiply].
         *
         * See also:
         *
         *  * [Screen], which does a similar computation but inverted.
         *  * [Overlay], which combines [Modulate] and [Screen] to favor the
         *    destination image.
         *  * [Hardlight], which combines [Modulate] and [Screen] to favor the
         *    source image.
         */
        val Modulate = BlendingMode(13)

        /**
         * Multiply the inverse of the components of the source and destination
         * images, and inverse the result.
         *
         * Inverting the components means that a fully saturated channel (opaque
         * white) is treated as the value 0.0, and values normally treated as 0.0
         * (black, transparent) are treated as 1.0.
         *
         * This is essentially the same as [Modulate] blend mode, but with the values
         * of the colors inverted before the multiplication and the result being
         * inverted back before rendering.
         *
         * This can only result in the same or lighter colors (multiplying by black,
         * 1.0, results in no change; multiplying by white, 0.0, results in white).
         * Similarly, in the alpha channel, it can only result in more opaque colors.
         *
         * This has similar effect to two projectors displaying their images on the
         * same screen simultaneously.
         *
         * See also:
         *
         *  * [Modulate], which does a similar computation but without inverting the
         *    values.
         *  * [Overlay], which combines [Modulate] and [Screen] to favor the
         *    destination image.
         *  * [Hardlight], which combines [Modulate] and [Screen] to favor the
         *    source image.
         */
        val Screen = BlendingMode(14) // The last coeff mode.

        /**
         * Multiply the components of the source and destination images after
         * adjusting them to favor the destination.
         *
         * Specifically, if the destination value is smaller, this multiplies it with
         * the source value, whereas is the source value is smaller, it multiplies
         * the inverse of the source value with the inverse of the destination value,
         * then inverts the result.
         *
         * Inverting the components means that a fully saturated channel (opaque
         * white) is treated as the value 0.0, and values normally treated as 0.0
         * (black, transparent) are treated as 1.0.
         *
         * See also:
         *
         *  * [Modulate], which always multiplies the values.
         *  * [Screen], which always multiplies the inverses of the values.
         *  * [Hardlight], which is similar to [Overlay] but favors the source image
         *    instead of the destination image.
         */
        val Overlay = BlendingMode(15)

        /**
         * Composite the source and destination image by choosing the lowest value
         * from each color channel.
         *
         * The opacity of the output image is computed in the same way as for
         * [SrcOver].
         */
        val Darken = BlendingMode(16)

        /**
         * Composite the source and destination image by choosing the highest value
         * from each color channel.
         *
         * The opacity of the output image is computed in the same way as for
         * [SrcOver].
         */
        val Lighten = BlendingMode(17)

        /**
         * Divide the destination by the inverse of the source.
         *
         * Inverting the components means that a fully saturated channel (opaque
         * white) is treated as the value 0.0, and values normally treated as 0.0
         * (black, transparent) are treated as 1.0.
         *
         * **NOTE** This [BlendingMode] can only be used on Android API level 29 and above
         */
        val ColorDodge = BlendingMode(18)

        /**
         * Divide the inverse of the destination by the source, and inverse the result.
         *
         * Inverting the components means that a fully saturated channel (opaque
         * white) is treated as the value 0.0, and values normally treated as 0.0
         * (black, transparent) are treated as 1.0.
         *
         * **NOTE** This [BlendingMode] can only be used on Android API level 29 and above
         */
        val ColorBurn = BlendingMode(19)

        /**
         * Multiply the components of the source and destination images after
         * adjusting them to favor the source.
         *
         * Specifically, if the source value is smaller, this multiplies it with the
         * destination value, whereas is the destination value is smaller, it
         * multiplies the inverse of the destination value with the inverse of the
         * source value, then inverts the result.
         *
         * Inverting the components means that a fully saturated channel (opaque
         * white) is treated as the value 0.0, and values normally treated as 0.0
         * (black, transparent) are treated as 1.0.
         *
         * **NOTE** This [BlendingMode] can only be used on Android API level 29 and above
         *
         * See also:
         *
         *  * [Modulate], which always multiplies the values.
         *  * [Screen], which always multiplies the inverses of the values.
         *  * [Overlay], which is similar to [Hardlight] but favors the destination
         *    image instead of the source image.
         */
        val Hardlight = BlendingMode(20)

        /**
         * Use [ColorDodge] for source values below 0.5 and [ColorBurn] for source
         * values above 0.5.
         *
         * This results in a similar but softer effect than [Overlay].
         *
         * **NOTE** This [BlendingMode] can only be used on Android API level 29 and above
         *
         * See also:
         *
         *  * [Color], which is a more subtle tinting effect.
         */
        val Softlight = BlendingMode(21)

        /**
         * Subtract the smaller value from the bigger value for each channel.
         *
         * Compositing black has no effect; compositing white inverts the colors of
         * the other image.
         *
         * The opacity of the output image is computed in the same way as for
         * [SrcOver].
         *
         * **NOTE** This [BlendingMode] can only be used on Android API level 29 and above
         *
         * The effect is similar to [Exclusion] but harsher.
         */
        val Difference = BlendingMode(22)

        /**
         * Subtract double the product of the two images from the sum of the two
         * images.
         *
         * Compositing black has no effect; compositing white inverts the colors of
         * the other image.
         *
         * The opacity of the output image is computed in the same way as for
         * [SrcOver].
         *
         * **NOTE** This [BlendingMode] can only be used on Android API level 29 and above
         *
         * The effect is similar to [Difference] but softer.
         */
        val Exclusion = BlendingMode(23)

        /**
         * Multiply the components of the source and destination images, including
         * the alpha channel.
         *
         * This can only result in the same or darker colors (multiplying by white,
         * 1.0, results in no change; multiplying by black, 0.0, results in black).
         *
         * Since the alpha channel is also multiplied, a fully-transparent pixel
         * (opacity 0.0) in one image results in a fully transparent pixel in the
         * output. This is similar to [DstIn], but with the colors combined.
         *
         * For a variant that multiplies the colors but does not multiply the alpha
         * channel, consider [Modulate].
         *
         * **NOTE** This [BlendingMode] can only be used on Android API level 29 and above
         */
        val Multiply = BlendingMode(24) // The last separable mode.

        /**
         * Take the hue of the source image, and the saturation and luminosity of the
         * destination image.
         *
         * The effect is to tint the destination image with the source image.
         *
         * The opacity of the output image is computed in the same way as for
         * [SrcOver]. Regions that are entirely transparent in the source image take
         * their hue from the destination.
         *
         * **NOTE** This [BlendingMode] can only be used on Android API level 29 and above
         */
        val Hue = BlendingMode(25)

        /**
         * Take the saturation of the source image, and the hue and luminosity of the
         * destination image.
         *
         * The opacity of the output image is computed in the same way as for
         * [SrcOver]. Regions that are entirely transparent in the source image take
         * their saturation from the destination.
         *
         * **NOTE** This [BlendingMode] can only be used on Android API level 29 and above
         *
         * See also:
         *
         *  * [Color], which also applies the hue of the source image.
         *  * [Luminosity], which applies the luminosity of the source image to the
         *    destination.
         */
        val Saturation = BlendingMode(26)

        /**
         * Take the hue and saturation of the source image, and the luminosity of the
         * destination image.
         *
         * The effect is to tint the destination image with the source image.
         *
         * The opacity of the output image is computed in the same way as for
         * [SrcOver]. Regions that are entirely transparent in the source image take
         * their hue and saturation from the destination.
         *
         * **NOTE** This [BlendingMode] can only be used on Android API level 29 and above
         *
         * See also:
         *
         *  * [Hue], which is a similar but weaker effect.
         *  * [Softlight], which is a similar tinting effect but also tints white.
         *  * [Saturation], which only applies the saturation of the source image.
         */
        val Color = BlendingMode(27)

        /**
         * Take the luminosity of the source image, and the hue and saturation of the
         * destination image.
         *
         * The opacity of the output image is computed in the same way as for
         * [SrcOver]. Regions that are entirely transparent in the source image take
         * their luminosity from the destination.
         *
         * **NOTE** This [BlendingMode] can only be used on Android API level 29 and above
         *
         * See also:
         *
         *  * [Saturation], which applies the saturation of the source image to the
         *    destination.
         */
        val Luminosity = BlendingMode(28)

        val newEntries by lazy {
            listOf(
                Clear,
                Src,
                Dst,
                SrcOver,
                DstOver,
                SrcIn,
                DstIn,
                SrcOut,
                DstOut,
                SrcAtop,
                DstAtop,
                Xor,
                Plus,
                Modulate,
                Screen,
                Overlay,
                Darken,
                Lighten,
                ColorDodge,
                ColorBurn,
                Hardlight,
                Softlight,
                Difference,
                Exclusion,
                Multiply,
                Hue,
                Saturation,
                Color,
                Luminosity,
            )
        }
        val oldEntries by lazy {
            listOf(
                Clear,
                Src,
                Dst,
                SrcOver,
                DstOver,
                SrcIn,
                DstIn,
                SrcOut,
                DstOut,
                SrcAtop,
                DstAtop,
                Xor,
                Plus,
                Modulate,
                Screen,
                Overlay,
                Darken,
                Lighten,
            )
        }
    }

    override fun toString() = when (this) {
        Clear -> "Clear"
        Src -> "Src"
        Dst -> "Dst"
        SrcOver -> "SrcOver"
        DstOver -> "DstOver"
        SrcIn -> "SrcIn"
        DstIn -> "DstIn"
        SrcOut -> "SrcOut"
        DstOut -> "DstOut"
        SrcAtop -> "SrcAtop"
        DstAtop -> "DstAtop"
        Xor -> "Xor"
        Plus -> "Plus"
        Modulate -> "Modulate"
        Screen -> "Screen"
        Overlay -> "Overlay"
        Darken -> "Darken"
        Lighten -> "Lighten"
        ColorDodge -> "ColorDodge"
        ColorBurn -> "ColorBurn"
        Hardlight -> "HardLight"
        Softlight -> "Softlight"
        Difference -> "Difference"
        Exclusion -> "Exclusion"
        Multiply -> "Multiply"
        Hue -> "Hue"
        Saturation -> "Saturation"
        Color -> "Color"
        Luminosity -> "Luminosity"
        else -> "Unknown" // Should not get here since we have an internal constructor
    }

}