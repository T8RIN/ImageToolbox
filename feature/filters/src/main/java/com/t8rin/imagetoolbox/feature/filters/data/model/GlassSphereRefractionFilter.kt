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


import android.graphics.PointF
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.GlassSphereRefraction.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GPUFilterTransformation
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGlassSphereFilter

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class GlassSphereRefractionFilter(
    override val value: Value,
) : GPUFilterTransformation(), Filter.GlassSphereRefraction {

    override fun createFilter(): GPUImageFilter = GPUImageGlassSphereFilter(
        PointF(0.5f, 0.5f),
        value.radius, value.refractiveIndex
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.glass_sphere_refraction,
            R.string.filter_help_glass_sphere_refraction
        )

        override val parameters = structuredParameters {
            Value::radius.float(R.string.radius, 0.25f, 0f..1f)
            Value::refractiveIndex.float(R.string.refractive_index, 0.71f, 0f..1f)
        }
    }
}