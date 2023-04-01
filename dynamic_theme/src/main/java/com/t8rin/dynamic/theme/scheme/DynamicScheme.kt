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
import com.t8rin.dynamic.theme.palettes.TonalPalette
import com.t8rin.dynamic.theme.palettes.TonalPalette.Companion.fromHueAndChroma
import com.t8rin.dynamic.theme.utils.MathUtils.sanitizeDegreesDouble

/**
 * Provides important settings for creating colors dynamically, and 6 color palettes. Requires: 1. A
 * color. (source color) 2. A theme. (Variant) 3. Whether or not its dark mode. 4. Contrast level.
 * (-1 to 1, currently contrast ratio 3.0 and 7.0)
 */
open class DynamicScheme(
    sourceColorHct: Hct,
    variant: Variant,
    isDark: Boolean,
    contrastLevel: Double,
    primaryPalette: TonalPalette,
    secondaryPalette: TonalPalette,
    tertiaryPalette: TonalPalette,
    neutralPalette: TonalPalette,
    neutralVariantPalette: TonalPalette
) {
    val sourceColorArgb: Int
    val sourceColorHct: Hct
    val variant: Variant
    val isDark: Boolean
    val contrastLevel: Double
    val primaryPalette: TonalPalette
    val secondaryPalette: TonalPalette
    val tertiaryPalette: TonalPalette
    val neutralPalette: TonalPalette
    val neutralVariantPalette: TonalPalette
    val errorPalette: TonalPalette

    init {
        sourceColorArgb = sourceColorHct.toInt()
        this.sourceColorHct = sourceColorHct
        this.variant = variant
        this.isDark = isDark
        this.contrastLevel = contrastLevel
        this.primaryPalette = primaryPalette
        this.secondaryPalette = secondaryPalette
        this.tertiaryPalette = tertiaryPalette
        this.neutralPalette = neutralPalette
        this.neutralVariantPalette = neutralVariantPalette
        errorPalette = fromHueAndChroma(25.0, 84.0)
    }

    companion object {
        fun getRotatedHue(sourceColorHct: Hct, hues: DoubleArray, rotations: DoubleArray): Double {
            val sourceHue = sourceColorHct.hue
            if (rotations.size == 1) {
                return sanitizeDegreesDouble(sourceHue + rotations[0])
            }
            val size = hues.size
            for (i in 0..size - 2) {
                val thisHue = hues[i]
                val nextHue = hues[i + 1]
                if (thisHue < sourceHue && sourceHue < nextHue) {
                    return sanitizeDegreesDouble(sourceHue + rotations[i])
                }
            }
            // If this statement executes, something is wrong, there should have been a rotation
            // found using the arrays.
            return sourceHue
        }
    }
}