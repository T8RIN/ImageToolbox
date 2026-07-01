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
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams

@Composable
internal fun CausticsParams(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit
) {
    ParamColumn {
        ColorParam(
            title = stringResource(R.string.color),
            value = value.backgroundColor.toColor(),
            onValueChange = {
                onValueChange(value.copy(backgroundColor = it.toModel()))
            }
        )
        FloatParam(
            value = value.scale,
            title = stringResource(R.string.scale),
            range = 1f..512f,
            onValueChange = { onValueChange(value.copy(scale = it)) }
        )
        IntParam(
            value = value.brightness,
            title = stringResource(R.string.brightness),
            range = 0f..100f,
            onValueChange = { onValueChange(value.copy(brightness = it)) }
        )
        FloatParam(
            value = value.amount,
            title = stringResource(R.string.amount),
            range = 0f..10f,
            onValueChange = { onValueChange(value.copy(amount = it)) }
        )
        FloatParam(
            value = value.turbulence,
            title = stringResource(R.string.turbulence),
            range = 0f..10f,
            onValueChange = { onValueChange(value.copy(turbulence = it)) }
        )
        FloatParam(
            value = value.dispersion,
            title = stringResource(R.string.dispersion),
            range = 0f..10f,
            onValueChange = { onValueChange(value.copy(dispersion = it)) }
        )
        FloatParam(
            value = value.time,
            title = stringResource(R.string.time),
            range = 0f..10f,
            onValueChange = { onValueChange(value.copy(time = it)) }
        )
        IntParam(
            value = value.samples,
            title = stringResource(R.string.samples),
            range = 1f..32f,
            onValueChange = { onValueChange(value.copy(samples = it)) },
            shape = ShapeDefaults.bottom
        )
    }
}