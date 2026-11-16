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

package com.t8rin.imagetoolbox.texture_generation.domain.model

import kotlin.math.roundToInt

enum class PatternTextureType(
    val parameters: List<PatternParameter>,
    val colorCount: Int,
    val defaultFrequency: Float
) {
    HatchPattern(
        listOf(
            PatternParameter("thickness", 0.5f, 0.02f..0.98f)
        ),
        colorCount = 2,
        defaultFrequency = 12f
    ),
    SmoothHatch(
        listOf(
            PatternParameter("hardness", 1.0f, 0.1f..8.0f),
            PatternParameter("balance", 0.5f, 0.01f..0.99f)
        ),
        colorCount = 2,
        defaultFrequency = 12f
    ),
    XorPattern(
        listOf(
            PatternParameter("intensity", 4.0f, 1.0f..16.0f, integer = true)
        ),
        colorCount = 2,
        defaultFrequency = 12f
    ),
    SquareSpiralPattern(
        listOf(
            PatternParameter("count", 4.0f, 1.0f..12.0f, integer = true),
            PatternParameter("thickness", 0.4f, 0.05f..0.95f)
        ),
        colorCount = 2,
        defaultFrequency = 5f
    ),
    CrossStitchPattern(
        listOf(
            PatternParameter("thickness", 0.65f, 0.1f..0.95f),
            PatternParameter("shadows", 0.6f, 0.0f..1.0f)
        ),
        colorCount = 3,
        defaultFrequency = 12f
    ),
    PlaidPattern(
        listOf(
            PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true),
            PatternParameter("thickness", 0.45f, 0.05f..0.95f)
        ),
        colorCount = 4,
        defaultFrequency = 12f
    ),
    GeneratedWaves(
        listOf(
            PatternParameter("variability", 0.5f, 0.0f..1.0f),
            PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true)
        ),
        colorCount = 4,
        defaultFrequency = 12f
    ),
    VoronoiHatch(
        listOf(
            PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true),
            PatternParameter("thickness", 0.45f, 0.05f..0.95f),
            PatternParameter("variability", 1.0f, 0.0f..1.0f)
        ),
        colorCount = 2,
        defaultFrequency = 12f
    ),
    LoopPattern(
        listOf(
            PatternParameter("count", 8.0f, 1.0f..64.0f, integer = true),
            PatternParameter("radius", 0.15f, 0.01f..0.5f),
            PatternParameter("thickness", 0.1f, 0.01f..0.5f),
            PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true)
        ),
        colorCount = 2,
        defaultFrequency = 1f
    ),
    ScintillatingGrid(
        listOf(
            PatternParameter("thickness", 0.08f, 0.01f..0.4f),
            PatternParameter("radius", 0.09f, 0.01f..0.4f)
        ),
        colorCount = 3,
        defaultFrequency = 12f
    ),
    CircleSpiralPattern(
        listOf(
            PatternParameter("count", 5.0f, 1.0f..24.0f, integer = true)
        ),
        colorCount = 2,
        defaultFrequency = 1f
    ),
    TestChart(emptyList(), colorCount = 2, defaultFrequency = 1f),
    BentRows(
        listOf(
            PatternParameter("offset", 0.2f, 0.0f..0.5f),
            PatternParameter("thickness", 0.04f, 0.005f..0.15f)
        ),
        colorCount = 3,
        defaultFrequency = 12f
    ),
    DiamondsIllusion(
        listOf(
            PatternParameter("count", 5.0f, 1.0f..16.0f, integer = true),
            PatternParameter("thickness", 0.5f, 0.05f..0.95f)
        ),
        colorCount = 2,
        defaultFrequency = 4f
    ),
    RandomTriangles(
        listOf(
            PatternParameter("count", 40.0f, 1.0f..100.0f, integer = true),
            PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true)
        ),
        colorCount = 4,
        defaultFrequency = 1f
    ),
    HexCubePattern(
        listOf(
            PatternParameter("thickness", 0.03f, 0.0f..0.3f)
        ),
        colorCount = 4,
        defaultFrequency = 8f
    ),
    SquareLooper(
        listOf(
            PatternParameter("time", 0.0f, 0.0f..10.0f),
            PatternParameter("thickness", 0.15f, 0.01f..0.8f)
        ),
        colorCount = 2,
        defaultFrequency = 8f
    ),
    MoireInterference(
        listOf(
            PatternParameter("intensity", 30.0f, 1.0f..100.0f),
            PatternParameter("thickness", 0.5f, 0.05f..0.95f)
        ),
        colorCount = 2,
        defaultFrequency = 1f
    ),
    TruchetArcs(
        listOf(
            PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true),
            PatternParameter("thickness", 0.15f, 0.02f..0.45f)
        ),
        colorCount = 2,
        defaultFrequency = 8f
    ),
    CircuitPattern(
        listOf(
            PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true),
            PatternParameter("thickness", 0.05f, 0.01f..0.25f),
            PatternParameter("radius", 0.13f, 0.03f..0.3f)
        ),
        colorCount = 2,
        defaultFrequency = 8f
    ),
    RosacePattern(
        listOf(
            PatternParameter("count", 12.0f, 3.0f..64.0f, integer = true),
            PatternParameter("radius", 0.24f, 0.05f..0.8f),
            PatternParameter("thickness", 0.005f, 0.005f..0.15f)
        ),
        colorCount = 2,
        defaultFrequency = 1f
    ),
    RosettePattern(
        listOf(
            PatternParameter("count", 12.0f, 3.0f..64.0f, integer = true),
            PatternParameter("radius", 0.4f, 0.05f..0.8f),
            PatternParameter("thickness", 0.006f, 0.005f..0.15f)
        ),
        colorCount = 2,
        defaultFrequency = 1f
    ),
    LightArray(
        listOf(
            PatternParameter("intensity", 0.7f, 0.0f..2.0f),
            PatternParameter("layers", 3.0f, 1.0f..6.0f, integer = true),
            PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true)
        ),
        colorCount = 4,
        defaultFrequency = 4f
    ),
    VoronoiGems(
        listOf(
            PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true),
            PatternParameter("shadows", 0.5f, 0.0f..1.0f),
            PatternParameter("variability", 1.0f, 0.0f..1.0f)
        ),
        colorCount = 4,
        defaultFrequency = 8f
    ),
    CitySkyline(
        listOf(
            PatternParameter("layers", 4.0f, 1.0f..8.0f, integer = true),
            PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true)
        ),
        colorCount = 4,
        defaultFrequency = 1f
    ),
    WinterLandscape(
        listOf(
            PatternParameter("time", 0.0f, 0.0f..10.0f),
            PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true)
        ),
        colorCount = 4,
        defaultFrequency = 1f
    ),
    CircleRipplePattern(
        listOf(
            PatternParameter("radius", 0.4f, 0.1f..0.49f),
            PatternParameter("shapeAspect", 0.7f, 0.25f..2.0f)
        ),
        colorCount = 4,
        defaultFrequency = 8f
    ),
    SquareRipplePattern(
        listOf(
            PatternParameter("thickness", 0.08f, 0.01f..0.3f),
            PatternParameter("offset", 0.08f, 0.0f..0.3f)
        ),
        colorCount = 4,
        defaultFrequency = 8f
    );

    val defaultColors: List<Int>
        get() = when (this) {
            PlaidPattern -> listOf(
                0xFF102B33.toInt(), 0xFF8C3444.toInt(), 0xFF356B65.toInt(), 0xFFE2C58B.toInt()
            )

            GeneratedWaves -> listOf(
                0xFF173A56.toInt(), 0xFF3E8B95.toInt(), 0xFFF0C88C.toInt(), 0xFFE58D7A.toInt()
            )
            CrossStitchPattern -> listOf(-16777216, -2767701, -13338717, -1)
            ScintillatingGrid -> listOf(-16777216, -8355712, -1, -1)
            RandomTriangles -> listOf(
                0xFF16334B.toInt(), 0xFF408C9A.toInt(), 0xFFE8B66C.toInt(), 0xFFDA7770.toInt()
            )

            VoronoiGems -> listOf(
                0xFF174B69.toInt(), 0xFF43A7A0.toInt(), 0xFF9D71BD.toInt(), 0xFFF0C67A.toInt()
            )

            HexCubePattern -> listOf(
                0xFFBCDCD7.toInt(), 0xFF5F9B9E.toInt(), 0xFF284E66.toInt(), 0xFF132E40.toInt()
            )
            LightArray -> listOf(-16777216, -256, -16711681, -65281)
            CitySkyline -> listOf(-16117736, -9424331, -32768, -256)
            WinterLandscape -> listOf(-16117736, -10053172, -14587077, -1)
            CircleRipplePattern -> listOf(
                0xFF101E38.toInt(), 0xFF315988.toInt(), 0xFFA7C9DC.toInt(), 0xFFF6DCA1.toInt()
            )

            SquareRipplePattern -> listOf(
                0xFF142C3C.toInt(), 0xFF439A9B.toInt(), 0xFFD99472.toInt(), 0xFFF4E3B5.toInt()
            )
            else -> listOf(-16777216, -1, -8355712, -16711681)
        }

    fun resolve(values: Map<String, Float>): Map<String, Float> =
        parameters.associate { parameter ->
            val value = values[parameter.name]?.takeIf { it.isFinite() } ?: parameter.defaultValue
            parameter.name to value.coerceIn(parameter.range)
                .let { if (parameter.integer) it.roundToInt().toFloat() else it }
        }
}

data class PatternParameter(
    val name: String,
    val defaultValue: Float,
    val range: ClosedFloatingPointRange<Float>,
    val integer: Boolean = false
)

data class PatternTextureParams(
    val values: Map<String, Float> = emptyMap(),
    val colors: List<Int> = listOf(-16777216, -1, -8355712, -16711681),
    val frequency: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val rotation: Float = 0f
)