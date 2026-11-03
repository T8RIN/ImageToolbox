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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.LensCorrection.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.lens_correction.LensCorrection
import com.t8rin.opencv_tools.lens_correction.model.SAMPLE_LENS_PROFILE

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class LensCorrectionFilter(
    override val value: Value,
) : FilterTransformation, Filter.LensCorrection {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = value.profile.uri.let { uri ->
        if (uri.isEmpty()) {
            LensCorrection.undistort(
                bitmap = input,
                lensDataJson = LensCorrection.SAMPLE_LENS_PROFILE,
                intensity = value.strength.toDouble()
            )
        } else {
            LensCorrection.undistort(
                bitmap = input,
                lensDataUri = uri.toUri(),
                intensity = value.strength.toDouble()
            )
        }
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.lens_correction, R.string.filter_help_lens_correction)

        override val parameters = structuredParameters {
            Value::strength.float(R.string.strength, 1f, -1f..3f)
            Value::profile.file(R.string.target_lens_profile) { FileModel("") }
        }
    }
}