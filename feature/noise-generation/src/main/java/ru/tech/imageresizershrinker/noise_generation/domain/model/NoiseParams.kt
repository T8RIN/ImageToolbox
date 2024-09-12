/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.noise_generation.domain.model

data class NoiseParams(
    val seed: Int,
    val frequency: Float,
    val noiseType: NoiseType,
    val rotationType3D: RotationType3D,
    val fractalType: FractalType,
    val fractalOctaves: Int,
    val fractalLacunarity: Float,
    val fractalGain: Float,
    val fractalWeightedStrength: Float,
    val fractalPingPongStrength: Float,
    val cellularDistanceFunction: CellularDistanceFunction,
    val cellularReturnType: CellularReturnType,
    val cellularJitter: Float,
    val domainWarpType: DomainWarpType,
    val domainWarpAmp: Float
) {
    companion object {
        val Default by lazy {
            NoiseParams(
                seed = 1337,
                frequency = 0.01f,
                noiseType = NoiseType.OpenSimplex2,
                rotationType3D = RotationType3D.None,
                fractalType = FractalType.None,
                fractalOctaves = 3,
                fractalLacunarity = 2f,
                fractalGain = 0.5f,
                fractalWeightedStrength = 0f,
                fractalPingPongStrength = 2f,
                cellularDistanceFunction = CellularDistanceFunction.EuclideanSq,
                cellularReturnType = CellularReturnType.Distance,
                cellularJitter = 1f,
                domainWarpType = DomainWarpType.OpenSimplex2,
                domainWarpAmp = 1f
            )
        }
    }
}