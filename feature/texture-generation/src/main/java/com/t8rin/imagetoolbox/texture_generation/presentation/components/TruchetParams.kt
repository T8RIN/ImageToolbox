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
import com.t8rin.imagetoolbox.texture_generation.domain.model.TruchetColorMode
import com.t8rin.imagetoolbox.texture_generation.domain.model.TruchetParams
import com.t8rin.imagetoolbox.texture_generation.domain.model.TruchetType

@Composable
internal fun TruchetParams(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit
) {
    val params = value.gmicTextureParams.truchet
    val update = { updated: TruchetParams ->
        onValueChange(value.copy(gmicTextureParams = value.gmicTextureParams.copy(truchet = updated)))
    }

    ParamColumn {
        IntParam(
            value = params.scale,
            title = stringResource(R.string.scale),
            range = 1f..256f,
            onValueChange = { update(params.copy(scale = it)) },
            shape = ShapeDefaults.top
        )
        IntParam(
            value = params.radius,
            title = stringResource(R.string.radius),
            range = 1f..64f,
            onValueChange = { update(params.copy(radius = it)) }
        )
        FloatParam(
            value = params.smoothness,
            title = stringResource(R.string.gmic_param_smoothness),
            range = 0f..10f,
            onValueChange = { update(params.copy(smoothness = it)) }
        )
        EnumParam(
            value = params.type,
            entries = TruchetType.entries,
            title = stringResource(R.string.type),
            onValueChange = { update(params.copy(type = it)) }
        )
        EnumParam(
            value = params.colorMode,
            entries = TruchetColorMode.entries,
            title = stringResource(R.string.gmic_param_color_mode),
            onValueChange = { update(params.copy(colorMode = it)) },
            shape = ShapeDefaults.bottom
        )
    }
}
