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
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams

@Composable
internal fun WoodParams(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit
) {
    ParamColumn {
        FloatParam(
            value = value.rings,
            title = stringResource(R.string.rings),
            range = 0f..5f,
            onValueChange = { onValueChange(value.copy(rings = it)) }
        )
        FloatParam(
            value = value.turbulence,
            title = stringResource(R.string.turbulence),
            range = 0f..10f,
            onValueChange = { onValueChange(value.copy(turbulence = it)) }
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
            onValueChange = { onValueChange(value.copy(bias = it)) }
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
            onValueChange = { onValueChange(value.copy(angle = it)) },
            shape = ShapeDefaults.bottom
        )
    }
}