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

import com.t8rin.dynamic.theme.dislike.DislikeAnalyzer.Companion.fixIfDisliked
import com.t8rin.dynamic.theme.hct.Hct
import com.t8rin.dynamic.theme.palettes.TonalPalette.Companion.fromHct
import com.t8rin.dynamic.theme.palettes.TonalPalette.Companion.fromHueAndChroma
import com.t8rin.dynamic.theme.temperature.TemperatureCache

/**
 * A scheme that places the source color in Scheme.primaryContainer.
 *
 *
 * Primary Container is the source color, adjusted for color relativity. It maintains constant
 * appearance in light mode and dark mode. This adds ~5 tone in light mode, and subtracts ~5 tone in
 * dark mode.
 *
 *
 * Tertiary Container is an analogous color, specifically, the analog of a color wheel divided
 * into 6, and the precise analog is the one found by increasing hue. This is a scientifically
 * grounded equivalent to rotating hue clockwise by 60 degrees. It also maintains constant
 * appearance.
 */
class SchemeContent(sourceColorHct: Hct, isDark: Boolean, contrastLevel: Double) : DynamicScheme(
    sourceColorHct,
    Variant.CONTENT,
    isDark,
    contrastLevel,
    fromHueAndChroma(sourceColorHct.hue, sourceColorHct.chroma),
    fromHueAndChroma(
        sourceColorHct.hue,
        Math.max(sourceColorHct.chroma - 32.0, sourceColorHct.chroma * 0.5)
    ),
    fromHct(
        fixIfDisliked(
            TemperatureCache(sourceColorHct)
                .getAnalogousColors( /* count= */3,  /* divisions= */6)[2]
        )
    ),
    fromHueAndChroma(sourceColorHct.hue, sourceColorHct.chroma / 8.0),
    fromHueAndChroma(
        sourceColorHct.hue, sourceColorHct.chroma / 8.0 + 4.0
    )
)