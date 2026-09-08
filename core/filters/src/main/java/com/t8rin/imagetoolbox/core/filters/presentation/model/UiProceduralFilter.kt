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

package com.t8rin.imagetoolbox.core.filters.presentation.model

import androidx.annotation.StringRes
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.ProceduralEffect
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ProceduralParams
import com.t8rin.imagetoolbox.core.resources.R

sealed class UiProceduralFilter(
    @StringRes title: Int,
    val effect: ProceduralEffect,
    override val value: ProceduralParams
) : UiFilter<ProceduralParams>(
    title = title,
    value = value,
    paramsInfo = effect.parameters.map { param ->
        FilterParam(
            title = when (param.name) {
                "intensity" -> R.string.texture_intensity
                "count" -> R.string.procedural_count
                "time" -> R.string.time
                "offsetX" -> R.string.offset_x
                "offsetY" -> R.string.offset_y
                "scale" -> R.string.scale
                "rotation" -> R.string.rotation
                "frequency" -> R.string.frequency
                "spacing" -> R.string.spacing
                "mode" -> R.string.gmic_param_mode
                "lighting" -> R.string.texture_lighting
                "iterations" -> R.string.iterations
                "balance" -> R.string.procedural_balance
                "variability" -> R.string.procedural_variability
                "seed" -> R.string.procedural_seed
                "layers" -> R.string.texture_layers
                "displacement" -> R.string.displacement
                "distortion" -> R.string.distortion
                "blend" -> R.string.raw_highlight_recovery_blend
                "offset" -> R.string.offset
                "shapeAspect" -> R.string.procedural_shape_aspect
                "ratio" -> R.string.procedural_scale_ratio
                "saturation" -> R.string.saturation
                "hueOffset" -> R.string.hue
                "blades" -> R.string.procedural_blades
                "radius" -> R.string.radius
                "colorVariation" -> R.string.procedural_color_variation
                "explosions" -> R.string.procedural_explosions
                "particles" -> R.string.procedural_particles
                "dampening" -> R.string.procedural_dampening
                "size" -> R.string.size
                "width" -> R.string.width
                else -> error("Unknown procedural parameter: ${param.name}")
            },
            valueRange = param.range,
            roundTo = if (param.integer) 0 else 3
        )
    }
)