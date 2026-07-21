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
import com.t8rin.imagetoolbox.texture_generation.domain.model.ReactionDiffusionMode
import com.t8rin.imagetoolbox.texture_generation.domain.model.ReactionDiffusionParams
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams

@Composable
internal fun ReactionDiffusionParams(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit
) {
    val params = value.gmicTextureParams.reactionDiffusion
    val update = { updated: ReactionDiffusionParams ->
        onValueChange(
            value.copy(
                gmicTextureParams = value.gmicTextureParams.copy(
                    reactionDiffusion = updated
                )
            )
        )
    }

    ParamColumn {
        IntParam(
            value = params.iterations,
            title = stringResource(R.string.iterations),
            range = 1f..10f,
            onValueChange = { update(params.copy(iterations = it)) },
            shape = ShapeDefaults.top
        )
        FloatParam(
            value = params.size,
            title = stringResource(R.string.just_size),
            range = 0f..20f,
            onValueChange = { update(params.copy(size = it)) }
        )
        EnumParam(
            value = params.mode,
            entries = ReactionDiffusionMode.entries,
            title = stringResource(R.string.gmic_param_mode),
            onValueChange = { update(params.copy(mode = it)) },
            shape = ShapeDefaults.bottom
        )
    }
}
