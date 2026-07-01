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
internal fun QuiltParams(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit
) {
    ParamColumn {
        IntParam(
            value = value.iterations,
            title = stringResource(R.string.iterations),
            range = 1f..50000f,
            onValueChange = { onValueChange(value.copy(iterations = it)) }
        )
        FloatParam(
            value = value.a,
            title = "A",
            range = -2f..2f,
            onValueChange = { onValueChange(value.copy(a = it)) }
        )
        FloatParam(
            value = value.b,
            title = "B",
            range = -2f..2f,
            onValueChange = { onValueChange(value.copy(b = it)) }
        )
        FloatParam(
            value = value.c,
            title = "C",
            range = -2f..2f,
            onValueChange = { onValueChange(value.copy(c = it)) }
        )
        FloatParam(
            value = value.d,
            title = "D",
            range = -2f..2f,
            onValueChange = { onValueChange(value.copy(d = it)) }
        )
        IntParam(
            value = value.k,
            title = "K",
            range = 0f..20f,
            onValueChange = { onValueChange(value.copy(k = it)) },
            shape = ShapeDefaults.bottom
        )
    }
}