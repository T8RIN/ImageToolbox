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

package ru.tech.imageresizershrinker.noise_generation.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.noise_generation.domain.NoiseGenerator
import ru.tech.imageresizershrinker.noise_generation.domain.model.NoiseParams
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
        val generator = with(noiseParams) {
            FastNoiseLite().apply {
                setSeed(seed)
                setFrequency(frequency)
                setNoiseType(noiseType)
                setRotationType3D(rotationType3D)
                setFractalType(fractalType)
                setFractalOctaves(fractalOctaves)
                setFractalLacunarity(fractalLacunarity)
                setFractalGain(fractalGain)
                setFractalWeightedStrength(fractalWeightedStrength)
                setFractalPingPongStrength(fractalPingPongStrength)
                setCellularDistanceFunction(cellularDistanceFunction)
                setCellularReturnType(cellularReturnType)
                setCellularJitter(cellularJitter)
                setDomainWarpType(domainWarpType)
                setDomainWarpAmp(domainWarpAmp)
            }
        }

        runCatching {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            for (x in 0..<width) {
                for (y in 0..<height) {
                    val noise = generator.getNoise(x.toFloat(), y.toFloat()) + 1f
                    val color = lerp(Color.Black, Color.White, noise / 2f)
                    bitmap.setPixel(x, y, color.toArgb())
                }
            }

            bitmap
        }.onFailure(onFailure).getOrNull()
    }

}