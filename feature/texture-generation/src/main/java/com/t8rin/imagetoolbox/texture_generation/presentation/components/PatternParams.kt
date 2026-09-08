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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams

@Composable
internal fun PatternParams(value: TextureParams, onValueChange: (TextureParams) -> Unit) {
    val type = requireNotNull(value.textureFilterType.pattern)
    val params = value.patternParams
    val values = type.resolve(params.values)
    ParamColumn {
        repeat(type.colorCount) { index ->
            ColorParam(
                title = stringResource(R.string.color) + " ${index + 1}",
                value = Color(params.colors.getOrElse(index) { -1 }),
                onValueChange = { color ->
                    val colors = List(4) {
                        if (it == index) color.toArgb() else params.colors.getOrElse(it) { -1 }
                    }
                    onValueChange(value.copy(patternParams = params.copy(colors = colors)))
                },
                shape = if (index == 0) ShapeDefaults.top else ShapeDefaults.center
            )
        }
        type.parameters.forEach { parameter ->
            val title = stringResource(
                when (parameter.name) {
                    "hardness" -> R.string.procedural_hardness
                    "shapeAspect" -> R.string.procedural_shape_aspect
                    "seed" -> R.string.procedural_seed
                    "variability" -> R.string.procedural_variability
                    "thickness" -> R.string.texture_thickness
                    "balance" -> R.string.balance
                    "intensity" -> R.string.intensity
                    "count" -> R.string.procedural_count
                    "shadows" -> R.string.shadows
                    "radius" -> R.string.radius
                    "offset" -> R.string.offset
                    "layers" -> R.string.texture_pattern_layers
                    "time" -> R.string.time
                    else -> error("Unknown pattern parameter: ${parameter.name}")
                }
            )
            val update: (Float) -> Unit = {
                onValueChange(value.copy(patternParams = params.copy(values = values + (parameter.name to it))))
            }
            if (parameter.integer) {
                IntParam(
                    value = values.getValue(parameter.name).toInt(),
                    title = title,
                    range = parameter.range,
                    onValueChange = { update(it.toFloat()) })
            } else {
                FloatParam(
                    value = values.getValue(parameter.name),
                    title = title,
                    range = parameter.range,
                    onValueChange = update
                )
            }
        }
        FloatParam(
            value = params.frequency,
            title = stringResource(R.string.frequency),
            range = 0.1f..64f,
            onValueChange = { onValueChange(value.copy(patternParams = params.copy(frequency = it))) }
        )
        FloatParam(
            value = params.offsetX,
            title = stringResource(R.string.offset_x),
            range = -1f..1f,
            onValueChange = { onValueChange(value.copy(patternParams = params.copy(offsetX = it))) }
        )
        FloatParam(
            value = params.offsetY,
            title = stringResource(R.string.offset_y),
            range = -1f..1f,
            onValueChange = { onValueChange(value.copy(patternParams = params.copy(offsetY = it))) }
        )
        FloatParam(
            value = params.rotation,
            title = stringResource(R.string.rotation),
            range = -180f..180f,
            onValueChange = { onValueChange(value.copy(patternParams = params.copy(rotation = it))) },
            shape = ShapeDefaults.bottom
        )
    }
}