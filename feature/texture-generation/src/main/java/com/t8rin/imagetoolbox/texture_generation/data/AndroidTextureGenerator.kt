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

package com.t8rin.imagetoolbox.texture_generation.data

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.jhlabs.BrushedMetalFilter
import com.jhlabs.CausticsFilter
import com.jhlabs.CellularFilter
import com.jhlabs.CheckFilter
import com.jhlabs.FBMFilter
import com.jhlabs.JhFilter
import com.jhlabs.MarbleTexFilter
import com.jhlabs.PlasmaFilter
import com.jhlabs.QuiltFilter
import com.jhlabs.WoodFilter
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.texture_generation.domain.TextureGenerator
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AndroidTextureGenerator @Inject constructor(
    dispatchersHolder: DispatchersHolder
) : TextureGenerator<Bitmap>, DispatchersHolder by dispatchersHolder {

    override suspend fun generateTexture(
        width: Int,
        height: Int,
        textureParams: TextureParams,
        onFailure: (Throwable) -> Unit
    ): Bitmap? = withContext(defaultDispatcher) {
        runCatching {
            createFilter(textureParams).filter(
                createBitmap(width, height).apply {
                    val sourceColor = when (textureParams.textureFilterType) {
                        TextureFilterType.Cellular,
                        TextureFilterType.Plasma -> textureParams.color.colorInt

                        else -> Color.TRANSPARENT
                    }
                    eraseColor(sourceColor)
                }
            )
        }.onFailure(onFailure).getOrNull()
    }

    private fun createFilter(
        textureParams: TextureParams
    ): JhFilter = when (textureParams.textureFilterType) {
        TextureFilterType.BrushedMetal -> BrushedMetalFilter().apply {
            color = textureParams.color.colorInt
            radius = textureParams.radius
            amount = textureParams.amount
            monochrome = textureParams.monochrome
            shine = textureParams.shine
        }

        TextureFilterType.Caustics -> CausticsFilter().apply {
            scale = textureParams.scale
            brightness = textureParams.brightness
            amount = textureParams.amount
            turbulence = textureParams.turbulence
            dispersion = textureParams.dispersion
            time = textureParams.time
            samples = textureParams.samples
            bgColor = textureParams.backgroundColor.colorInt
        }

        TextureFilterType.Cellular -> CellularFilter().apply {
            setScale(textureParams.scale)
            setStretch(textureParams.stretch)
            setAngle(textureParams.angle)
            setAngleCoefficient(textureParams.angleCoefficient)
            gradientCoefficient = textureParams.gradientCoefficient
            f1 = textureParams.f1
            f2 = textureParams.f2
            f3 = textureParams.f3
            f4 = textureParams.f4
            setRandomness(textureParams.randomness)
            setGridType(textureParams.gridType.value)
            setDistancePower(textureParams.distancePower)
            setTurbulence(textureParams.turbulence)
            setAmount(textureParams.amount)
            gain = textureParams.gain
            bias = textureParams.bias
            useColor = true
        }

        TextureFilterType.Check -> CheckFilter().apply {
            foreground = textureParams.foregroundColor.colorInt
            background = textureParams.backgroundColor.colorInt
            xScale = textureParams.xScale
            yScale = textureParams.yScale
            fuzziness = textureParams.fuzziness
            angle = textureParams.angle
        }

        TextureFilterType.FBM -> FBMFilter().apply {
            amount = textureParams.amount
            scale = textureParams.scale
            stretch = textureParams.stretch
            angle = textureParams.angle
            octaves = textureParams.octaves
            h = textureParams.h
            lacunarity = textureParams.lacunarity
            gain = textureParams.gain
            bias = textureParams.bias
            basisType = textureParams.basisType.value
        }

        TextureFilterType.Marble -> MarbleTexFilter().apply {
            scale = textureParams.scale
            stretch = textureParams.stretch
            angle = textureParams.angle
            turbulence = textureParams.turbulence
            turbulenceFactor = textureParams.turbulenceFactor
        }

        TextureFilterType.Plasma -> PlasmaFilter().apply {
            turbulence = textureParams.turbulence
            scaling = textureParams.scaling
            useImageColors = true
        }

        TextureFilterType.Quilt -> QuiltFilter().apply {
            iterations = textureParams.iterations
            a = textureParams.a
            b = textureParams.b
            c = textureParams.c
            d = textureParams.d
            k = textureParams.k
        }

        TextureFilterType.Wood -> WoodFilter().apply {
            rings = textureParams.rings
            turbulence = textureParams.turbulence
            gain = textureParams.gain
            bias = textureParams.bias
            scale = textureParams.scale
            stretch = textureParams.stretch
            angle = textureParams.angle
        }
    }

}
