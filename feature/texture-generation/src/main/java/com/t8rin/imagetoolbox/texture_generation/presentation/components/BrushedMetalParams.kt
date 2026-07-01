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
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams

@Composable
internal fun BrushedMetalParams(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit
) {
    ParamColumn {
        ColorParam(
            title = stringResource(R.string.color),
            value = value.color.toColor(),
            onValueChange = {
                onValueChange(value.copy(color = it.toModel()))
            }
        )
        IntParam(
            value = value.radius,
            title = stringResource(R.string.radius),
            range = 0f..50f,
            onValueChange = {
                onValueChange(value.copy(radius = it))
            }
        )
        FloatParam(
            value = value.amount,
            title = stringResource(R.string.amount),
            range = 0f..1f,
            onValueChange = {
                onValueChange(value.copy(amount = it))
            }
        )
        FloatParam(
            value = value.shine,
            title = stringResource(R.string.shine),
            range = 0f..1f,
            onValueChange = {
                onValueChange(value.copy(shine = it))
            }
        )
        PreferenceRowSwitch(
            title = stringResource(R.string.monochrome),
            checked = value.monochrome,
            onClick = {
                onValueChange(value.copy(monochrome = it))
            },
            applyHorizontalPadding = false,
            shape = ShapeDefaults.bottom
        )
    }
}