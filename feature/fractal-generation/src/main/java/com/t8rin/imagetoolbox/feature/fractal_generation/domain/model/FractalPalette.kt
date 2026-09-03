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

package com.t8rin.imagetoolbox.feature.fractal_generation.domain.model

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import kotlin.math.floor
import kotlin.math.roundToInt

enum class FractalPalette(
    val stableKey: String,
    val displayName: String,
    private vararg val stops: Int
) {
    Classic(
        "classic",
        "Classic",
        0xFF05051A.toInt(),
        0xFF123EAB.toInt(),
        0xFF26BCE1.toInt(),
        0xFFF8E16C.toInt(),
        0xFFF06A24.toInt(),
        0xFF3A0812.toInt()
    ),
    Fire(
        "fire",
        "Fire",
        0xFF090004.toInt(),
        0xFF5A0900.toInt(),
        0xFFD33A00.toInt(),
        0xFFFFA51F.toInt(),
        0xFFFFFFB0.toInt()
    ),
    Ocean(
        "ocean",
        "Ocean",
        0xFF020B22.toInt(),
        0xFF063B73.toInt(),
        0xFF087E8B.toInt(),
        0xFF31C5C0.toInt(),
        0xFFD8FFF2.toInt()
    ),
    Viridis(
        "viridis",
        "Viridis",
        0xFF440154.toInt(),
        0xFF3B528B.toInt(),
        0xFF21918C.toInt(),
        0xFF5EC962.toInt(),
        0xFFFDE725.toInt()
    ),
    Magma(
        "magma",
        "Magma",
        0xFF000004.toInt(),
        0xFF3B0F70.toInt(),
        0xFF8C2981.toInt(),
        0xFFDE4968.toInt(),
        0xFFFE9F6D.toInt(),
        0xFFFCFDBF.toInt()
    ),
    Inferno(
        "inferno",
        "Inferno",
        0xFF000004.toInt(),
        0xFF420A68.toInt(),
        0xFF932667.toInt(),
        0xFFDD513A.toInt(),
        0xFFFCA50A.toInt(),
        0xFFFCFFA4.toInt()
    ),
    Plasma(
        "plasma",
        "Plasma",
        0xFF0D0887.toInt(),
        0xFF6A00A8.toInt(),
        0xFFB12A90.toInt(),
        0xFFE16462.toInt(),
        0xFFFCA636.toInt(),
        0xFFF0F921.toInt()
    ),
    Turbo(
        "turbo",
        "Turbo",
        0xFF30123B.toInt(),
        0xFF4145AB.toInt(),
        0xFF2A9DF4.toInt(),
        0xFF20D5A5.toInt(),
        0xFF8BEB55.toInt(),
        0xFFF9D423.toInt(),
        0xFFF36B1B.toInt(),
        0xFF7A0403.toInt()
    ),
    Twilight(
        "twilight",
        "Twilight",
        0xFF20134E.toInt(),
        0xFF6D3580.toInt(),
        0xFFC75D75.toInt(),
        0xFFF6B36B.toInt(),
        0xFF8ED1C5.toInt(),
        0xFF315A8A.toInt(),
        0xFF20134E.toInt()
    ),
    Ice(
        "ice",
        "Ice",
        0xFF02040F.toInt(),
        0xFF102A56.toInt(),
        0xFF2D78B7.toInt(),
        0xFF8BE4F0.toInt(),
        0xFFF5FFFF.toInt()
    ),
    Forest(
        "forest",
        "Forest",
        0xFF07150A.toInt(),
        0xFF174B2B.toInt(),
        0xFF3B7D3A.toInt(),
        0xFF9BBF48.toInt(),
        0xFFF1E7A1.toInt()
    ),
    Neon(
        "neon",
        "Neon",
        0xFF050011.toInt(),
        0xFF7400B8.toInt(),
        0xFFFF007A.toInt(),
        0xFFFFC800.toInt(),
        0xFF00F5D4.toInt(),
        0xFF0077FF.toInt()
    ),
    Cividis(
        "cividis",
        "Cividis",
        0xFF00204C.toInt(),
        0xFF2E4A7D.toInt(),
        0xFF666870.toInt(),
        0xFFA08A5B.toInt(),
        0xFFD6AF3C.toInt(),
        0xFFFFE945.toInt()
    ),
    Cubehelix(
        "cubehelix",
        "Cubehelix",
        0xFF000000.toInt(),
        0xFF1D2B53.toInt(),
        0xFF5E3C99.toInt(),
        0xFFB35C8C.toInt(),
        0xFFE3A35D.toInt(),
        0xFFFFFFFF.toInt()
    ),
    Spectral(
        "spectral",
        "Spectral",
        0xFF9E0142.toInt(),
        0xFFD53E4F.toInt(),
        0xFFF46D43.toInt(),
        0xFFFEE08B.toInt(),
        0xFFE6F598.toInt(),
        0xFF66C2A5.toInt(),
        0xFF3288BD.toInt(),
        0xFF5E4FA2.toInt()
    ),
    Aurora(
        "aurora",
        "Aurora",
        0xFF07152B.toInt(),
        0xFF44318D.toInt(),
        0xFF0B8F9C.toInt(),
        0xFF35D07F.toInt(),
        0xFFD9F36A.toInt()
    ),
    Sunset(
        "sunset",
        "Sunset",
        0xFF10143D.toInt(),
        0xFF4A236B.toInt(),
        0xFFA33B69.toInt(),
        0xFFF06A4D.toInt(),
        0xFFFFC56E.toInt(),
        0xFFFFF0B3.toInt()
    ),
    Copper(
        "copper",
        "Copper",
        0xFF080403.toInt(),
        0xFF3A1C12.toInt(),
        0xFF814425.toInt(),
        0xFFC8783E.toInt(),
        0xFFF0B878.toInt(),
        0xFFFFE0B8.toInt()
    ),
    Rocket(
        "rocket",
        "Rocket",
        0xFF03051A.toInt(),
        0xFF3F1B43.toInt(),
        0xFF841E5A.toInt(),
        0xFFCB1B4F.toInt(),
        0xFFF06043.toInt(),
        0xFFF6B48F.toInt(),
        0xFFFAEBDD.toInt()
    ),
    Mako(
        "mako",
        "Mako",
        0xFF0B0405.toInt(),
        0xFF342032.toInt(),
        0xFF3B496C.toInt(),
        0xFF357BA3.toInt(),
        0xFF39A7A5.toInt(),
        0xFF8BDAB2.toInt(),
        0xFFDEF5E5.toInt()
    ),
    Amethyst(
        "amethyst",
        "Amethyst",
        0xFF10002B.toInt(),
        0xFF240046.toInt(),
        0xFF5A189A.toInt(),
        0xFF9D4EDD.toInt(),
        0xFFE0AAFF.toInt(),
        0xFFFFF0FF.toInt()
    ),
    Vaporwave(
        "vaporwave",
        "Vaporwave",
        0xFF17002E.toInt(),
        0xFF5800A3.toInt(),
        0xFFB5179E.toInt(),
        0xFFF72585.toInt(),
        0xFF4CC9F0.toInt(),
        0xFF00F5D4.toInt()
    ),
    Earth(
        "earth",
        "Earth",
        0xFF071A12.toInt(),
        0xFF1D4D32.toInt(),
        0xFF607D3B.toInt(),
        0xFFB49A55.toInt(),
        0xFFD9C9A2.toInt(),
        0xFFF2EFE6.toInt()
    ),
    Grayscale(
        "grayscale",
        "Grayscale",
        0xFF000000.toInt(),
        0xFF404040.toInt(),
        0xFF909090.toInt(),
        0xFFFFFFFF.toInt()
    );

    val colors: List<ColorModel> = stops.map(::ColorModel)
    val suggestedColors: List<ColorModel> = List(SUGGESTED_COLOR_COUNT) { index ->
        colorAt(index.toDouble() / SUGGESTED_COLOR_COUNT)
    }.distinctBy(ColorModel::colorInt)

    fun colorAt(position: Double): ColorModel = ColorModel(colorIntAt(position))

    fun colorIntAt(position: Double): Int {
        if (stops.size == 1) return stops.first()

        val wrapped = position
            .takeIf(Double::isFinite)
            ?.let { it - floor(it) }
            ?: 0.0
        val scaled = wrapped * (stops.size - 1)
        val firstIndex = floor(scaled).toInt().coerceIn(0, stops.lastIndex)
        val secondIndex = (firstIndex + 1).coerceAtMost(stops.lastIndex)
        val fraction = scaled - firstIndex

        return interpolate(stops[firstIndex], stops[secondIndex], fraction)
    }

    private fun interpolate(
        first: Int,
        second: Int,
        fraction: Double
    ): Int {
        fun channel(shift: Int): Int {
            val start = first ushr shift and 0xFF
            val end = second ushr shift and 0xFF
            return (start + (end - start) * fraction).roundToInt().coerceIn(0, 255)
        }

        return channel(24) shl 24 or
                (channel(16) shl 16) or
                (channel(8) shl 8) or
                channel(0)
    }

}

private const val SUGGESTED_COLOR_COUNT = 12
