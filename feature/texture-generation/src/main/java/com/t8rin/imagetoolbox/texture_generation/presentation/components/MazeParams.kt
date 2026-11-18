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

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.SeedSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.texture_generation.domain.model.MazeAlgorithm
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams

@Composable
internal fun MazeParams(value: TextureParams, onValueChange: (TextureParams) -> Unit) {
    val params = value.mazeParams
    ParamColumn {
        EnumParam(
            value = params.algorithm,
            entries = MazeAlgorithm.entries,
            title = stringResource(R.string.texture_maze_algorithm),
            onValueChange = { onValueChange(value.copy(mazeParams = params.copy(algorithm = it))) },
            shape = ShapeDefaults.top
        )
        IntParam(
            value = params.columns,
            title = stringResource(R.string.columns),
            range = 1f..256f,
            onValueChange = { onValueChange(value.copy(mazeParams = params.copy(columns = it))) }
        )
        IntParam(
            value = params.rows,
            title = stringResource(R.string.rows),
            range = 1f..256f,
            onValueChange = { onValueChange(value.copy(mazeParams = params.copy(rows = it))) }
        )
        FloatParam(
            value = params.wallWidth,
            title = stringResource(R.string.texture_maze_wall_width),
            range = 0.02f..0.8f,
            onValueChange = { onValueChange(value.copy(mazeParams = params.copy(wallWidth = it))) }
        )
        FloatParam(
            value = params.padding,
            title = stringResource(R.string.padding),
            range = 0.01f..0.2f,
            onValueChange = { onValueChange(value.copy(mazeParams = params.copy(padding = it))) }
        )
        if (params.algorithm != MazeAlgorithm.BlobbyDivision) {
            FloatParam(
                value = params.directionBias,
                title = stringResource(R.string.texture_maze_direction_bias),
                range = 0f..1f,
                onValueChange = { onValueChange(value.copy(mazeParams = params.copy(directionBias = it))) }
            )
        }
        if (params.algorithm in listOf(
                MazeAlgorithm.GrowingTree,
                MazeAlgorithm.GrowingBinaryTree,
                MazeAlgorithm.Eller,
                MazeAlgorithm.Houston,
                MazeAlgorithm.OriginShift
            )
        ) {
            FloatParam(
                value = params.branching,
                title = stringResource(R.string.texture_maze_branching),
                range = 0f..1f,
                onValueChange = { onValueChange(value.copy(mazeParams = params.copy(branching = it))) }
            )
        }
        FloatParam(
            value = params.braid,
            title = stringResource(R.string.texture_maze_braid),
            range = 0f..1f,
            onValueChange = { onValueChange(value.copy(mazeParams = params.copy(braid = it))) }
        )
        PreferenceRowSwitch(
            title = stringResource(R.string.texture_maze_openings),
            checked = params.openings,
            onClick = { onValueChange(value.copy(mazeParams = params.copy(openings = it))) },
            applyHorizontalPadding = false,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = ShapeDefaults.center
        )
        ColorParam(
            title = stringResource(R.string.texture_wall_color),
            value = value.foregroundColor.toColor(),
            onValueChange = { onValueChange(value.copy(foregroundColor = it.toModel())) }
        )
        ColorParam(
            title = stringResource(R.string.background_color),
            value = value.backgroundColor.toColor(),
            onValueChange = { onValueChange(value.copy(backgroundColor = it.toModel())) }
        )
        SeedSelector(
            value = params.seed,
            title = stringResource(R.string.seed),
            valueRange = -10000f..10000f,
            onValueChange = { onValueChange(value.copy(mazeParams = params.copy(seed = it.toInt()))) },
            shape = ShapeDefaults.bottom
        )
    }
}
