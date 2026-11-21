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
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.SeedSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.texture_generation.domain.model.PatternPlacement
import com.t8rin.imagetoolbox.texture_generation.domain.model.PatternTextureType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import kotlin.math.abs

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
        type.parameters.forEachIndexed { index, parameter ->
            val title = stringResource(parameter.title)
            val shape = if (!type.hasScale && index == type.parameters.lastIndex) {
                ShapeDefaults.bottom
            } else ShapeDefaults.center
            val update: (Float) -> Unit = {
                onValueChange(value.copy(patternParams = params.copy(values = values + (parameter.name to it))))
            }
            if (parameter.name == "seed") {
                SeedSelector(
                    value = values.getValue(parameter.name),
                    title = title,
                    valueRange = parameter.range,
                    onValueChange = update,
                    shape = if (type.placement == PatternPlacement.Canvas) ShapeDefaults.default else shape
                )
            } else if (parameter.integer) {
                IntParam(
                    value = values.getValue(parameter.name).toInt(),
                    title = title,
                    range = parameter.range,
                    onValueChange = { update(it.toFloat()) },
                    shape = shape
                )
            } else {
                FloatParam(
                    value = values.getValue(parameter.name),
                    title = title,
                    range = parameter.range,
                    roundTo = when {
                        type.placement == PatternPlacement.Canvas -> 2
                        parameter.range.endInclusive - parameter.range.start <= 0.1f -> 4
                        parameter.range.endInclusive - parameter.range.start <= 1f -> 3
                        else -> 2
                    },
                    onValueChange = update,
                    shape = shape
                )
            }
        }
        val hasPhase = type.placement == PatternPlacement.Phase ||
            type.placement == PatternPlacement.PhaseXY
        if (type.placement == PatternPlacement.Canvas) {
            FloatParam(
                value = params.frequency,
                title = stringResource(R.string.frequency),
                range = 0.1f..64f,
                onValueChange = { onValueChange(value.copy(patternParams = params.copy(frequency = it))) }
            )
            val hatchAngle =
                if (type == PatternTextureType.HatchPattern) abs(params.rotation % 180f) else null
            if (hatchAngle != 90f) {
                FloatParam(
                    value = params.offsetX,
                    title = stringResource(R.string.offset_x),
                    range = -1f..1f,
                    onValueChange = { onValueChange(value.copy(patternParams = params.copy(offsetX = it))) }
                )
            }
            if (hatchAngle != 0f) {
                FloatParam(
                    value = params.offsetY,
                    title = stringResource(R.string.offset_y),
                    range = -1f..1f,
                    onValueChange = { onValueChange(value.copy(patternParams = params.copy(offsetY = it))) }
                )
            }
        } else if (type.hasScale) {
            FloatParam(
                value = type.defaultFrequency / type.resolveFrequency(params.frequency),
                title = stringResource(R.string.scale),
                range = 0.25f..4f,
                onValueChange = {
                    onValueChange(value.copy(patternParams = params.copy(frequency = type.defaultFrequency / it)))
                },
                shape = if (type.hasRotation || hasPhase) ShapeDefaults.center else ShapeDefaults.bottom
            )
        }
        if (type.hasRotation) {
            FloatParam(
                value = params.rotation,
                title = stringResource(R.string.rotation),
                range = -180f..180f,
                onValueChange = { onValueChange(value.copy(patternParams = params.copy(rotation = it))) },
                shape = if (hasPhase) ShapeDefaults.center else ShapeDefaults.bottom
            )
        }
        if (hasPhase) {
            val twoAxes = type.placement == PatternPlacement.PhaseXY
            FloatParam(
                value = params.phaseX,
                title = stringResource(if (twoAxes) R.string.texture_phase_x else R.string.phase),
                range = 0f..1f,
                onValueChange = { onValueChange(value.copy(patternParams = params.copy(phaseX = it))) },
                shape = if (twoAxes) ShapeDefaults.center else ShapeDefaults.bottom
            )
            if (twoAxes) {
                FloatParam(
                    value = params.phaseY,
                    title = stringResource(R.string.texture_phase_y),
                    range = 0f..1f,
                    onValueChange = { onValueChange(value.copy(patternParams = params.copy(phaseY = it))) },
                    shape = ShapeDefaults.bottom
                )
            }
        }
    }
}