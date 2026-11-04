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

package com.t8rin.imagetoolbox.feature.filters.data.model

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.MazePortrait.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class MazePortraitFilter(
    override val value: Value
) : FilterTransformation, Filter.MazePortrait {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.mazePortrait(
        input = input,
        cellSize = value.cellSize,
        wallWidth = value.wallWidth,
        bias = value.bias,
        seed = value.seed,
        ink = value.ink.colorInt,
        paper = value.paper.colorInt,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.maze_portrait,
            R.string.filter_help_maze_portrait
        )

        override val parameters = structuredParameters {
            Value::cellSize.float(R.string.cell_size, 12f, 4f..64f)
            Value::wallWidth.float(R.string.wall_width, 0.46f, 0.05f..0.48f)
            Value::bias.float(R.string.image_guidance, 3f, 0f..8f)
            Value::seed.seed(R.string.seed, 1, 0..10000)
            Value::ink.color(R.string.ink_color, ColorModel(0xFF18232B.toInt()))
            Value::paper.color(R.string.paper_color, ColorModel(0xFFF5F0E4.toInt()))
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}