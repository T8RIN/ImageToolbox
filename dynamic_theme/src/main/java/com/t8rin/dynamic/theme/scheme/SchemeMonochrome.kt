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
 * A monochrome theme, colors are purely black / white / gray.
 */
class SchemeMonochrome(sourceColorHct: Hct, isDark: Boolean, contrastLevel: Double) : DynamicScheme(
    sourceColorHct,
    Variant.MONOCHROME,
    isDark,
    contrastLevel,
    fromHueAndChroma(sourceColorHct.hue, 0.0),
    fromHueAndChroma(sourceColorHct.hue, 0.0),
    fromHueAndChroma(sourceColorHct.hue, 0.0),
    fromHueAndChroma(sourceColorHct.hue, 0.0),
    fromHueAndChroma(sourceColorHct.hue, 0.0)
)