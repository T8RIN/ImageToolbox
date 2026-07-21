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

package com.t8rin.imagetoolbox.texture_generation.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.texture_generation.domain.model.OrganicFibersPalette
import com.t8rin.imagetoolbox.texture_generation.domain.model.OrganicFibersParams
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams

@Composable
internal fun OrganicFibersParams(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit
) {
    val params = value.gmicTextureParams.organicFibers
    val update = { updated: OrganicFibersParams ->
        onValueChange(value.copy(gmicTextureParams = value.gmicTextureParams.copy(organicFibers = updated)))
    }

    ParamColumn {
        FloatParam(
            value = params.agentDensity,
            title = stringResource(R.string.gmic_param_agent_density),
            range = 0f..50f,
            onValueChange = { update(params.copy(agentDensity = it)) },
            shape = ShapeDefaults.top
        )
        IntParam(
            value = params.iterations,
            title = stringResource(R.string.iterations),
            range = 1f..256f,
            onValueChange = { update(params.copy(iterations = it)) }
        )
        IntParam(
            value = params.orientations,
            title = stringResource(R.string.orientation),
            range = 2f..16f,
            onValueChange = { update(params.copy(orientations = it)) }
        )
        IntParam(
            value = params.sensorDistance,
            title = stringResource(R.string.gmic_param_sensor_distance),
            range = 0f..400f,
            onValueChange = { update(params.copy(sensorDistance = it)) }
        )
        FloatParam(
            value = params.sensorAngle,
            title = stringResource(R.string.gmic_param_sensor_angle),
            range = 0f..180f,
            onValueChange = { update(params.copy(sensorAngle = it)) }
        )
        IntParam(
            value = params.motionDistance,
            title = stringResource(R.string.gmic_param_motion_distance),
            range = 0f..400f,
            onValueChange = { update(params.copy(motionDistance = it)) }
        )
        FloatParam(
            value = params.motionAngle,
            title = stringResource(R.string.gmic_param_motion_angle),
            range = 0f..180f,
            onValueChange = { update(params.copy(motionAngle = it)) }
        )
        FloatParam(
            value = params.motionMoment,
            title = stringResource(R.string.gmic_param_motion_moment),
            range = 0f..100f,
            onValueChange = { update(params.copy(motionMoment = it)) }
        )
        FloatParam(
            value = params.trailBlur,
            title = stringResource(R.string.gmic_param_trail_blur),
            range = 0f..20f,
            onValueChange = { update(params.copy(trailBlur = it)) }
        )
        IntParam(
            value = params.particleSize,
            title = stringResource(R.string.gmic_param_particle_size),
            range = 1f..128f,
            onValueChange = { update(params.copy(particleSize = it)) }
        )
        FloatParam(
            value = params.particleThickness,
            title = stringResource(R.string.gmic_param_particle_thickness),
            range = 0f..100f,
            onValueChange = { update(params.copy(particleThickness = it)) }
        )
        IntParam(
            value = params.quantizedOrientations,
            title = stringResource(R.string.gmic_param_quantized_orientations),
            range = 1f..32f,
            onValueChange = { update(params.copy(quantizedOrientations = it)) }
        )
        FloatParam(
            value = params.opacity,
            title = stringResource(R.string.opacity),
            range = 0f..100f,
            onValueChange = { update(params.copy(opacity = it)) }
        )
        FloatParam(
            value = params.sharpening,
            title = stringResource(R.string.sharpen),
            range = 0f..100f,
            onValueChange = { update(params.copy(sharpening = it)) }
        )
        EnumParam(
            value = params.palette,
            entries = OrganicFibersPalette.entries,
            title = stringResource(R.string.palette),
            onValueChange = { update(params.copy(palette = it)) },
            shape = ShapeDefaults.bottom
        )
    }
}
