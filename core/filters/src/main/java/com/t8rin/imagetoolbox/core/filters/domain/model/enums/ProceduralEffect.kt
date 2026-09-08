/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.filters.domain.model.enums

import com.t8rin.imagetoolbox.core.filters.domain.model.params.ProceduralParams
import kotlin.math.round

enum class ProceduralEffect(
    parameters: List<ProceduralParameter>,
    val colors: List<ProceduralColorParameter> = emptyList(),
    hasTransform: Boolean = true
) {
    RandomColorDispersion(
        listOf(
            ProceduralParameter("intensity", 0.15f, -1.0f..1.0f),
            ProceduralParameter("seed", 0.0f, 0.0f..1000.0f, integer = true)
        )
    ),
    RadialShimmer(
        listOf(
            ProceduralParameter("count", 8.0f, 1.0f..32.0f, integer = true),
            ProceduralParameter("intensity", 0.2f, -1.0f..1.0f),
            ProceduralParameter("spacing", 1.0f, 0.1f..4.0f)
        )
    ),
    HelixWaves(
        listOf(
            ProceduralParameter("frequency", 8.0f, 1.0f..40.0f),
            ProceduralParameter("intensity", 0.2f, -1.0f..1.0f),
            ProceduralParameter("mode", 0.0f, 0.0f..2.0f, integer = true),
            ProceduralParameter("lighting", 0.0f, 0.0f..1.0f)
        )
    ),
    GridSineDistortion2(
        listOf(
            ProceduralParameter("intensity", 0.2f, -1.0f..1.0f),
            ProceduralParameter("frequency", 8.0f, 1.0f..40.0f)
        )
    ),
    Quicksilver(
        listOf(
            ProceduralParameter("displacement", 0.1f, -0.5f..0.5f),
            ProceduralParameter("intensity", 0.2f, -1.0f..1.0f),
            ProceduralParameter("frequency", 8.0f, 1.0f..40.0f)
        )
    ),
    Gallium(
        listOf(
            ProceduralParameter("iterations", 5.0f, 1.0f..16.0f, integer = true),
            ProceduralParameter("intensity", 0.2f, -1.0f..1.0f)
        )
    ),
    Rubidium(
        listOf(
            ProceduralParameter("displacement", 0.1f, -0.5f..0.5f),
            ProceduralParameter("intensity", 0.2f, -1.0f..1.0f),
            ProceduralParameter("balance", 0.5f, 0.0f..1.0f)
        )
    ),
    GlassRectTiles(
        listOf(
            ProceduralParameter("intensity", 0.2f, -1.0f..1.0f),
            ProceduralParameter("distortion", 0.25f, -1.0f..1.0f),
            ProceduralParameter("frequency", 8.0f, 1.0f..40.0f)
        )
    ),
    GlassHexTiles(
        listOf(
            ProceduralParameter("intensity", 0.2f, -1.0f..1.0f),
            ProceduralParameter("distortion", 0.25f, -1.0f..1.0f),
            ProceduralParameter("frequency", 8.0f, 1.0f..40.0f)
        )
    ),
    GlassTriangleTiles(
        listOf(
            ProceduralParameter("intensity", 0.2f, -1.0f..1.0f),
            ProceduralParameter("distortion", 0.25f, -1.0f..1.0f),
            ProceduralParameter("frequency", 8.0f, 1.0f..40.0f)
        )
    ),
    SpiralArms(
        listOf(
            ProceduralParameter("count", 6.0f, 1.0f..32.0f, integer = true),
            ProceduralParameter("intensity", 1.0f, -4.0f..4.0f)
        )
    ),
    LogarithmicSpiral(
        listOf(
            ProceduralParameter("count", 2.0f, 1.0f..32.0f, integer = true),
            ProceduralParameter("intensity", 1.0f, -4.0f..4.0f)
        )
    ),
    SpiralDroste(
        listOf(
            ProceduralParameter("intensity", 2.0f, 1.1f..8.0f),
            ProceduralParameter("distortion", 0.2f, -1.0f..1.0f)
        )
    ),
    SquareSpiralDroste(
        listOf(
            ProceduralParameter("intensity", 2.0f, 1.1f..8.0f),
            ProceduralParameter("distortion", 0.15f, -1.0f..1.0f),
            ProceduralParameter("shapeAspect", 1.0f, 0.25f..4.0f)
        )
    ),
    HexKaleidoscope(
        listOf(
            ProceduralParameter("count", 6.0f, 1.0f..32.0f, integer = true),
            ProceduralParameter("frequency", 4.0f, 1.0f..16.0f),
            ProceduralParameter("offset", 0.0f, -1.0f..1.0f),
            ProceduralParameter("mode", 0.0f, 0.0f..2.0f, integer = true)
        )
    ),
    SmoothKaleidoscope(
        listOf(
            ProceduralParameter("blend", 0.25f, 0.0f..1.0f),
            ProceduralParameter("offset", 0.0f, -1.0f..1.0f),
            ProceduralParameter("frequency", 2.0f, 1.0f..16.0f)
        )
    ),
    RandomKaleidoscopeGrid(
        listOf(
            ProceduralParameter("count", 6.0f, 1.0f..32.0f, integer = true),
            ProceduralParameter("frequency", 3.0f, 1.0f..16.0f),
            ProceduralParameter("seed", 0.0f, 0.0f..1000.0f, integer = true),
            ProceduralParameter("variability", 0.5f, 0.0f..1.0f),
            ProceduralParameter("blend", 0.25f, 0.0f..1.0f)
        )
    ),
    SquareFresnel(
        listOf(
            ProceduralParameter("intensity", 6.0f, 1.0f..32.0f)
        )
    ),
    InversionFractal(
        listOf(
            ProceduralParameter("mode", 0.0f, 0.0f..2.0f, integer = true),
            ProceduralParameter("iterations", 6.0f, 1.0f..16.0f, integer = true),
            ProceduralParameter("intensity", 0.8f, 0.1f..2.0f)
        )
    ),
    HueShiftBands(
        listOf(
            ProceduralParameter("intensity", 2f, -10f..10f),
            ProceduralParameter("saturation", 1f, 0f..2f),
            ProceduralParameter("hueOffset", 0f, -1f..1f)
        )
    ),
    HueShiftTunnel(
        listOf(
            ProceduralParameter("intensity", 2f, -10f..10f),
            ProceduralParameter("saturation", 1f, 0f..2f),
            ProceduralParameter("hueOffset", 0f, -1f..1f)
        )
    ),
    HueShiftFan(
        listOf(
            ProceduralParameter("intensity", 2f, -10f..10f),
            ProceduralParameter("saturation", 1f, 0f..2f),
            ProceduralParameter("hueOffset", 0f, -1f..1f)
        )
    ),
    BrokenGlass(
        listOf(
            ProceduralParameter("intensity", 0.4f, -1.0f..1.0f),
            ProceduralParameter("frequency", 8.0f, 1.0f..40.0f),
            ProceduralParameter("seed", 0.0f, 0.0f..1000.0f, integer = true)
        )
    ),
    InlineBreaks(
        listOf(
            ProceduralParameter("intensity", 0.3f, -1.0f..1.0f),
            ProceduralParameter("distortion", 0.2f, -1.0f..1.0f),
            ProceduralParameter("frequency", 32.0f, 1.0f..128.0f)
        )
    ),
    BokehLights(
        listOf(
            ProceduralParameter("intensity", 0.4f, 0.0f..2.0f),
            ProceduralParameter("count", 24.0f, 1.0f..64.0f, integer = true),
            ProceduralParameter("blades", 6.0f, 3.0f..12.0f, integer = true),
            ProceduralParameter("radius", 0.07f, 0.005f..0.3f),
            ProceduralParameter("variability", 0.5f, 0.0f..1.0f),
            ProceduralParameter("colorVariation", 0.3f, 0.0f..1.0f),
            ProceduralParameter("seed", 0.0f, 0.0f..1000.0f, integer = true)
        ),
        colors = listOf(ProceduralColorParameter("lightColor", -675749))
    ),
    Fireworks(
        listOf(
            ProceduralParameter("seed", 0.0f, 0.0f..1000.0f, integer = true),
            ProceduralParameter("explosions", 4.0f, 1.0f..12.0f, integer = true),
            ProceduralParameter("particles", 48.0f, 8.0f..128.0f, integer = true),
            ProceduralParameter("mode", 0.0f, 0.0f..2.0f, integer = true),
            ProceduralParameter("intensity", 0.6f, 0.0f..2.0f),
            ProceduralParameter("time", 0.5f, 0.05f..1.0f)
        )
    ),
    StreakExpand(
        listOf(
            ProceduralParameter("intensity", 1.0f, 0.0f..1.0f),
            ProceduralParameter("size", 0.3f, 0.01f..0.9f)
        )
    ),
    RadialInterpolate(
        listOf(
            ProceduralParameter("intensity", 1.0f, 0.0f..1.0f),
            ProceduralParameter("count", 12.0f, 1.0f..64.0f, integer = true),
            ProceduralParameter("radius", 0.3f, 0.01f..1.0f),
            ProceduralParameter("width", 0.2f, 0.01f..0.8f)
        )
    ),
    RadialStreak(
        listOf(
            ProceduralParameter("intensity", 0.5f, 0.0f..1.0f),
            ProceduralParameter("count", 8.0f, 1.0f..64.0f, integer = true),
            ProceduralParameter("radius", 0.3f, 0.01f..1.0f)
        )
    ),
    ColumnStreak(
        listOf(
            ProceduralParameter("intensity", 1.0f, 0.0f..1.0f),
            ProceduralParameter("variability", 0.6f, 0.0f..1.0f),
            ProceduralParameter("count", 16.0f, 1.0f..128.0f, integer = true)
        )
    ),
    StreakCircles(
        listOf(
            ProceduralParameter("intensity", 1.0f, 0.0f..1.0f),
            ProceduralParameter("count", 20.0f, 1.0f..64.0f, integer = true),
            ProceduralParameter("radius", 0.15f, 0.01f..0.5f),
            ProceduralParameter("layers", 8.0f, 1.0f..32.0f, integer = true),
            ProceduralParameter("seed", 0.0f, 0.0f..1000.0f, integer = true)
        )
    ),
    TiledStreak(
        listOf(
            ProceduralParameter("intensity", 1.0f, 0.0f..1.0f),
            ProceduralParameter("balance", 0.5f, 0.0f..1.0f),
            ProceduralParameter("variability", 0.5f, 0.0f..1.0f),
            ProceduralParameter("frequency", 8.0f, 1.0f..32.0f),
            ProceduralParameter("seed", 0.0f, 0.0f..1000.0f, integer = true)
        )
    ),
    BrokenCells(
        listOf(
            ProceduralParameter("intensity", 0.5f, 0.0f..1.0f),
            ProceduralParameter("distortion", 0.2f, -1.0f..1.0f),
            ProceduralParameter("variability", 0.5f, 0.0f..1.0f),
            ProceduralParameter("frequency", 8.0f, 1.0f..40.0f),
            ProceduralParameter("seed", 0.0f, 0.0f..1000.0f, integer = true)
        )
    ),
    SpiralBreaks(
        listOf(
            ProceduralParameter("intensity", 0.5f, -1.0f..1.0f),
            ProceduralParameter("distortion", 0.3f, -1.0f..1.0f),
            ProceduralParameter("count", 16.0f, 1.0f..64.0f, integer = true)
        )
    ),
    ConcentricCircleBreaks(
        listOf(
            ProceduralParameter("intensity", 0.5f, -1.0f..1.0f),
            ProceduralParameter("dampening", 2.0f, 0.0f..8.0f),
            ProceduralParameter("distortion", 0.2f, -1.0f..1.0f),
            ProceduralParameter("frequency", 16.0f, 1.0f..64.0f)
        )
    ),
    ConcentricSquareBreaks(
        listOf(
            ProceduralParameter("intensity", 0.5f, -1.0f..1.0f),
            ProceduralParameter("dampening", 2.0f, 0.0f..8.0f),
            ProceduralParameter("distortion", 0.2f, -1.0f..1.0f),
            ProceduralParameter("frequency", 16.0f, 1.0f..64.0f)
        )
    ),
    CircularMirror(
        listOf(
            ProceduralParameter("intensity", 1.0f, 0.0f..1.0f),
            ProceduralParameter("radius", 0.3f, 0.01f..1.0f),
            ProceduralParameter("mode", 0.0f, 0.0f..1.0f, integer = true)
        )
    ),
    ProgressiveScaling(
        listOf(
            ProceduralParameter("intensity", 1.0f, 0.0f..1.0f),
            ProceduralParameter("ratio", 1.5f, 1.05f..4.0f),
            ProceduralParameter("radius", 0.3f, 0.01f..1.0f)
        )
    );

    val parameters: List<ProceduralParameter> = parameters + if (hasTransform) listOf(
        ProceduralParameter("offsetX", 0.0f, -1.0f..1.0f),
        ProceduralParameter("offsetY", 0.0f, -1.0f..1.0f),
        ProceduralParameter("scale", 1.0f, 0.05f..8.0f),
        ProceduralParameter("rotation", 0.0f, -180.0f..180.0f)
    ) else emptyList()

    fun resolveColors(value: ProceduralParams): Map<String, Int> = colors.associate { param ->
        param.name to (value.colors[param.name] ?: param.defaultValue)
    }

    fun resolve(value: ProceduralParams): Map<String, Float> = parameters.associate { param ->
        val number = value.values[param.name]
            ?.takeIf { it.isFinite() }
            ?.coerceIn(param.range)
            ?: param.defaultValue
        param.name to if (param.integer) round(number) else number
    }
}

data class ProceduralParameter(
    val name: String,
    val defaultValue: Float,
    val range: ClosedFloatingPointRange<Float>,
    val integer: Boolean = false
)

data class ProceduralColorParameter(
    val name: String,
    val defaultValue: Int
)