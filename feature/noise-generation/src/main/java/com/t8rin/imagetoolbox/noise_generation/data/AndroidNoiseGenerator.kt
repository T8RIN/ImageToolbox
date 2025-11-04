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

package com.t8rin.imagetoolbox.noise_generation.data

import android.graphics.Bitmap
import com.t8rin.fast_noise.FastNoise
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.noise_generation.domain.NoiseGenerator
import com.t8rin.imagetoolbox.noise_generation.domain.model.NoiseParams
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AndroidNoiseGenerator @Inject constructor(
    dispatchersHolder: DispatchersHolder
) : NoiseGenerator<Bitmap>, DispatchersHolder by dispatchersHolder {

    override suspend fun generateNoise(
        width: Int,
        height: Int,
        noiseParams: NoiseParams,
        onFailure: (Throwable) -> Unit
    ): Bitmap? = withContext(defaultDispatcher) {
        runCatching {
            with(noiseParams) {
                FastNoise.generateNoiseImage(
                    width = width,
                    height = height,
                    seed = seed,
                    frequency = frequency,
                    noiseType = noiseType.ordinal,
                    rotationType3D = rotationType3D.ordinal,
                    fractalType = fractalType.ordinal,
                    fractalOctaves = fractalOctaves,
                    fractalLacunarity = fractalLacunarity,
                    fractalGain = fractalGain,
                    fractalWeightedStrength = fractalWeightedStrength,
                    fractalPingPongStrength = fractalPingPongStrength,
                    cellularDistanceFunction = cellularDistanceFunction.ordinal,
                    cellularReturnType = cellularReturnType.ordinal,
                    cellularJitter = cellularJitter,
                    domainWarpType = domainWarpType.ordinal,
                    domainWarpAmp = domainWarpAmp,
                )
            }
        }.onFailure(onFailure).getOrNull()
    }

}