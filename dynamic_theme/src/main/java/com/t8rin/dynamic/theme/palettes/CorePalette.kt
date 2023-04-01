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
 * An intermediate concept between the key color for a UI theme, and a full color scheme. 5 sets of
 * tones are generated, all except one use the same hue as the key color, and all vary in chroma.
 */
class CorePalette private constructor(argb: Int, isContent: Boolean) {
    @JvmField
    var a1: TonalPalette? = null

    @JvmField
    var a2: TonalPalette? = null

    @JvmField
    var a3: TonalPalette? = null

    @JvmField
    var n1: TonalPalette? = null

    @JvmField
    var n2: TonalPalette? = null

    @JvmField
    var error: TonalPalette

    init {
        val hct = Hct.fromInt(argb)
        val hue = hct.hue
        val chroma = hct.chroma
        if (isContent) {
            a1 = TonalPalette.Companion.fromHueAndChroma(hue, chroma)
            a2 = TonalPalette.Companion.fromHueAndChroma(hue, chroma / 3.0)
            a3 = TonalPalette.Companion.fromHueAndChroma(hue + 60.0, chroma / 2.0)
            n1 = TonalPalette.Companion.fromHueAndChroma(hue, Math.min(chroma / 12.0, 4.0))
            n2 = TonalPalette.Companion.fromHueAndChroma(hue, Math.min(chroma / 6.0, 8.0))
        } else {
            a1 = TonalPalette.Companion.fromHueAndChroma(hue, Math.max(48.0, chroma))
            a2 = TonalPalette.Companion.fromHueAndChroma(hue, 16.0)
            a3 = TonalPalette.Companion.fromHueAndChroma(hue + 60.0, 24.0)
            n1 = TonalPalette.Companion.fromHueAndChroma(hue, 4.0)
            n2 = TonalPalette.Companion.fromHueAndChroma(hue, 8.0)
        }
        error = TonalPalette.Companion.fromHueAndChroma(25.0, 84.0)
    }

    companion object {
        /**
         * Create key tones from a color.
         *
         * @param argb ARGB representation of a color
         */
        @JvmStatic
        fun of(argb: Int): CorePalette {
            return CorePalette(argb, false)
        }

        /**
         * Create content key tones from a color.
         *
         * @param argb ARGB representation of a color
         */
        @JvmStatic
        fun contentOf(argb: Int): CorePalette {
            return CorePalette(argb, true)
        }
    }
}