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
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.OrigamiRelief.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class OrigamiReliefFilter(
    override val value: Value
) : FilterTransformation, Filter.OrigamiRelief {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.origamiRelief(
        input = input,
        cellSize = value.cellSize,
        foldHeight = value.foldHeight,
        lightAngle = value.lightAngle,
        crease = value.crease,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.origami_relief,
            R.string.filter_help_origami_relief
        )

        override val parameters = structuredParameters {
            Value::cellSize.float(R.string.cell_size, 72f, 16f..160f)
            Value::foldHeight.float(R.string.fold_height, 0.5f, 0f..1f)
            Value::lightAngle.float(R.string.light_angle, 315f, 0f..360f)
            Value::crease.float(R.string.crease, 0.45f, 0f..1f)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}