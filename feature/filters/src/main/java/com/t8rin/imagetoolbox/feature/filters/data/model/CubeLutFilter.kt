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
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.model.FileModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CubeLut.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle
import com.t8rin.trickle.TrickleUtils

@FilterDefinition(group = FilterGroups.LUT)
internal class CubeLutFilter(
    override val value: Value,
) : FilterTransformation, Filter.CubeLut {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        if (value.file.uri.isEmpty()) return input

        val lutPath = TrickleUtils.getAbsolutePath(
            uri = value.file.uri.toUri(),
            context = appContext
        )

        return Trickle.applyCubeLut(
            input = input,
            cubeLutPath = lutPath,
            intensity = value.intensity
        )
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.cube_lut, R.string.filter_help_cube_lut)

        override val parameters = structuredParameters {
            Value::intensity.float(R.string.strength, 1f, 0f..1f)
            Value::file.file(R.string.target_cube_lut_file) { FileModel("") }
        }
    }
}