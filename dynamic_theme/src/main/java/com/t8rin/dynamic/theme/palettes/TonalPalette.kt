/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.t8rin.dynamic.theme.palettes

import com.t8rin.dynamic.theme.hct.Hct

/**
 * A convenience class for retrieving colors that are constant in hue and chroma, but vary in tone.
 */
class TonalPalette private constructor(hue: Double, chroma: Double) {
    var cache: MutableMap<Int, Int> = HashMap()
    var hue: Double
    var chroma: Double

    init {
        this.hue = hue
        this.chroma = chroma
    }

    /**
     * Create an ARGB color with HCT hue and chroma of this Tones instance, and the provided HCT tone.
     *
     * @param tone HCT tone, measured from 0 to 100.
     * @return ARGB representation of a color with that tone.
     */
    // AndroidJdkLibsChecker is higher priority than ComputeIfAbsentUseValue (b/119581923)
    fun tone(tone: Int): Int {
        var color = cache[tone]
        if (color == null) {
            color = Hct.from(hue, chroma, tone.toDouble()).toInt()
            cache[tone] = color
        }
        return color
    }

    fun getHct(tone: Double): Hct {
        return Hct.from(hue, chroma, tone)
    }

    companion object {
        /**
         * Create tones using the HCT hue and chroma from a color.
         *
         * @param argb ARGB representation of a color
         * @return Tones matching that color's hue and chroma.
         */
        fun fromInt(argb: Int): TonalPalette {
            return fromHct(Hct.fromInt(argb))
        }

        /**
         * Create tones using a HCT color.
         *
         * @param hct HCT representation of a color.
         * @return Tones matching that color's hue and chroma.
         */
        @JvmStatic
        fun fromHct(hct: Hct): TonalPalette {
            return fromHueAndChroma(hct.hue, hct.chroma)
        }

        /**
         * Create tones from a defined HCT hue and chroma.
         *
         * @param hue    HCT hue
         * @param chroma HCT chroma
         * @return Tones matching hue and chroma.
         */
        @JvmStatic
        fun fromHueAndChroma(hue: Double, chroma: Double): TonalPalette {
            return TonalPalette(hue, chroma)
        }
    }
}