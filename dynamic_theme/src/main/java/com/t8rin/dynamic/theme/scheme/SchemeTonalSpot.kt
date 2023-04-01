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
 * A calm theme, sedated colors that aren't particularly chromatic.
 */
class SchemeTonalSpot(sourceColorHct: Hct, isDark: Boolean, contrastLevel: Double) : DynamicScheme(
    sourceColorHct,
    Variant.TONAL_SPOT,
    isDark,
    contrastLevel,
    fromHueAndChroma(sourceColorHct.hue, 40.0),
    fromHueAndChroma(sourceColorHct.hue, 16.0),
    fromHueAndChroma(
        sanitizeDegreesDouble(sourceColorHct.hue + 60.0), 24.0
    ),
    fromHueAndChroma(sourceColorHct.hue, 6.0),
    fromHueAndChroma(sourceColorHct.hue, 8.0)
)