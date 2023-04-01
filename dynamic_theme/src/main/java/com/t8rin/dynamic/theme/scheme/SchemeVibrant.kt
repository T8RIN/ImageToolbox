/*
 * Copyright 2022 Google LLC
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
package com.t8rin.dynamic.theme.scheme

import com.t8rin.dynamic.theme.hct.Hct
import com.t8rin.dynamic.theme.palettes.TonalPalette.Companion.fromHueAndChroma

/**
 * A loud theme, colorfulness is maximum for Primary palette, increased for others.
 */
class SchemeVibrant(sourceColorHct: Hct, isDark: Boolean, contrastLevel: Double) : DynamicScheme(
    sourceColorHct,
    Variant.VIBRANT,
    isDark,
    contrastLevel,
    fromHueAndChroma(sourceColorHct.hue, 200.0),
    fromHueAndChroma(
        DynamicScheme.Companion.getRotatedHue(sourceColorHct, HUES, SECONDARY_ROTATIONS), 24.0
    ),
    fromHueAndChroma(
        DynamicScheme.Companion.getRotatedHue(sourceColorHct, HUES, TERTIARY_ROTATIONS), 32.0
    ),
    fromHueAndChroma(sourceColorHct.hue, 8.0),
    fromHueAndChroma(sourceColorHct.hue, 12.0)
) {
    companion object {
        private val HUES = doubleArrayOf(0.0, 41.0, 61.0, 101.0, 131.0, 181.0, 251.0, 301.0, 360.0)
        private val SECONDARY_ROTATIONS =
            doubleArrayOf(18.0, 15.0, 10.0, 12.0, 15.0, 18.0, 15.0, 12.0, 12.0)
        private val TERTIARY_ROTATIONS =
            doubleArrayOf(35.0, 30.0, 20.0, 25.0, 30.0, 35.0, 30.0, 25.0, 25.0)
    }
}