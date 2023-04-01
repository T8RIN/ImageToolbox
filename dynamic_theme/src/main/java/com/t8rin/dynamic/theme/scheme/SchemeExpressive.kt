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

package com.t8rin.dynamic.theme.scheme;

import com.t8rin.dynamic.theme.hct.Hct;
import com.t8rin.dynamic.theme.palettes.TonalPalette;
import com.t8rin.dynamic.theme.utils.MathUtils;

/**
 * A playful theme - the source color's hue does not appear in the theme.
 */
public class SchemeExpressive extends DynamicScheme {
    // NOMUTANTS--arbitrary increments/decrements, correctly, still passes tests.
    private static final double[] HUES = {0, 21, 51, 121, 151, 191, 271, 321, 360};
    private static final double[] SECONDARY_ROTATIONS = {45, 95, 45, 20, 45, 90, 45, 45, 45};
    private static final double[] TERTIARY_ROTATIONS = {120, 120, 20, 45, 20, 15, 20, 120, 120};

    public SchemeExpressive(Hct sourceColorHct, boolean isDark, double contrastLevel) {
        super(
                sourceColorHct,
                Variant.EXPRESSIVE,
                isDark,
                contrastLevel,
                TonalPalette.fromHueAndChroma(
                        MathUtils.sanitizeDegreesDouble(sourceColorHct.getHue() + 120.0), 40.0),
                TonalPalette.fromHueAndChroma(
                        DynamicScheme.getRotatedHue(sourceColorHct, HUES, SECONDARY_ROTATIONS), 24.0),
                TonalPalette.fromHueAndChroma(
                        DynamicScheme.getRotatedHue(sourceColorHct, HUES, TERTIARY_ROTATIONS), 32.0),
                TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 8.0),
                TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 12.0));
    }
}
