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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.texture_generation.domain.model.CellularGridType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams

@Composable
internal fun CellularParams(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit
) {
    ParamColumn {
        ColorParam(
            title = stringResource(R.string.color),
            value = value.color.toColor(),
            onValueChange = { onValueChange(value.copy(color = it.toModel())) }
        )
        FloatParam(
            value = value.scale,
            title = stringResource(R.string.scale),
            range = 1f..512f,
            onValueChange = { onValueChange(value.copy(scale = it)) }
        )
        FloatParam(
            value = value.stretch,
            title = stringResource(R.string.stretch),
            range = 0.1f..20f,
            onValueChange = { onValueChange(value.copy(stretch = it)) }
        )
        FloatParam(
            value = value.angle,
            title = stringResource(R.string.angle),
            range = -360f..360f,
            onValueChange = { onValueChange(value.copy(angle = it)) }
        )
        FloatParam(
            value = value.angleCoefficient,
            title = stringResource(R.string.angle_coefficient),
            range = -10f..10f,
            onValueChange = { onValueChange(value.copy(angleCoefficient = it)) }
        )
        FloatParam(
            value = value.gradientCoefficient,
            title = stringResource(R.string.gradient_coefficient),
            range = -10f..10f,
            onValueChange = { onValueChange(value.copy(gradientCoefficient = it)) }
        )
        FloatParam(
            value = value.f1,
            title = "F1",
            range = -5f..5f,
            onValueChange = { onValueChange(value.copy(f1 = it)) }
        )
        FloatParam(
            value = value.f2,
            title = "F2",
            range = -5f..5f,
            onValueChange = { onValueChange(value.copy(f2 = it)) }
        )
        FloatParam(
            value = value.f3,
            title = "F3",
            range = -5f..5f,
            onValueChange = { onValueChange(value.copy(f3 = it)) }
        )
        FloatParam(
            value = value.f4,
            title = "F4",
            range = -5f..5f,
            onValueChange = { onValueChange(value.copy(f4 = it)) }
        )
        FloatParam(
            value = value.randomness,
            title = stringResource(R.string.randomness),
            range = 0f..1f,
            onValueChange = { onValueChange(value.copy(randomness = it)) }
        )
        DataSelector(
            value = value.gridType,
            onValueChange = {
                onValueChange(value.copy(gridType = it))
            },
            entries = CellularGridType.entries,
            title = stringResource(R.string.grid_type),
            titleIcon = null,
            itemContentText = {
                stringResource(it.titleRes())
            },
            spanCount = 1,
            containerColor = Color.Unspecified,
            shape = ShapeDefaults.center
        )
        FloatParam(
            value = value.distancePower,
            title = stringResource(R.string.distance_power),
            range = 0.1f..10f,
            onValueChange = { onValueChange(value.copy(distancePower = it)) }
        )
        FloatParam(
            value = value.turbulence,
            title = stringResource(R.string.turbulence),
            range = 0f..10f,
            onValueChange = { onValueChange(value.copy(turbulence = it)) }
        )
        FloatParam(
            value = value.amount,
            title = stringResource(R.string.amount),
            range = -10f..10f,
            onValueChange = { onValueChange(value.copy(amount = it)) }
        )
        FloatParam(
            value = value.gain,
            title = stringResource(R.string.gain),
            range = 0f..1f,
            onValueChange = { onValueChange(value.copy(gain = it)) }
        )
        FloatParam(
            value = value.bias,
            title = stringResource(R.string.bias),
            range = 0f..1f,
            onValueChange = { onValueChange(value.copy(bias = it)) },
            shape = ShapeDefaults.bottom
        )
    }
}