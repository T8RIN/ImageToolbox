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
internal fun CheckParams(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit
) {
    ParamColumn {
        ColorParam(
            title = stringResource(R.string.light_color),
            value = value.foregroundColor.toColor(),
            onValueChange = {
                onValueChange(value.copy(foregroundColor = it.toModel()))
            }
        )
        ColorParam(
            title = stringResource(R.string.dark_color),
            value = value.backgroundColor.toColor(),
            onValueChange = {
                onValueChange(value.copy(backgroundColor = it.toModel()))
            }
        )
        IntParam(
            value = value.xScale,
            title = stringResource(R.string.scale_x),
            range = 1f..128f,
            onValueChange = { onValueChange(value.copy(xScale = it)) }
        )
        IntParam(
            value = value.yScale,
            title = stringResource(R.string.scale_y),
            range = 1f..128f,
            onValueChange = { onValueChange(value.copy(yScale = it)) }
        )
        IntParam(
            value = value.fuzziness,
            title = stringResource(R.string.fuzziness),
            range = 0f..128f,
            onValueChange = { onValueChange(value.copy(fuzziness = it)) }
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