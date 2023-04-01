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
import com.t8rin.dynamic.theme.utils.MathUtils.sanitizeDegreesDouble

/**
 * A playful theme - the source color's hue does not appear in the theme.
 */
class SchemeExpressive(sourceColorHct: Hct, isDark: Boolean, contrastLevel: Double) : DynamicScheme(
    sourceColorHct,
    Variant.EXPRESSIVE,
    isDark,
    contrastLevel,
    fromHueAndChroma(
        sanitizeDegreesDouble(sourceColorHct.hue + 120.0), 40.0
    ),
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
        // NOMUTANTS--arbitrary increments/decrements, correctly, still passes tests.
        private val HUES = doubleArrayOf(0.0, 21.0, 51.0, 121.0, 151.0, 191.0, 271.0, 321.0, 360.0)
        private val SECONDARY_ROTATIONS =
            doubleArrayOf(45.0, 95.0, 45.0, 20.0, 45.0, 90.0, 45.0, 45.0, 45.0)
        private val TERTIARY_ROTATIONS =
            doubleArrayOf(120.0, 120.0, 20.0, 45.0, 20.0, 15.0, 20.0, 120.0, 120.0)
    }
}