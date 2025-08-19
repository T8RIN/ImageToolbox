/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import com.jhlabs.ArcFilter
import com.jhlabs.JhFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ArcParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation
import kotlin.math.max

@FilterInject
internal class ArcFilter(
    override val value: ArcParams = ArcParams.Default
) : JhFilterTransformation(), Filter.Arc {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): JhFilter = ArcFilter()

    override fun createFilter(image: Bitmap): JhFilter = ArcFilter().apply {
        radius = value.radius * (max(image.width, image.height) / 2f)
        height = value.height * (max(image.width, image.height) / 2f)
        angle = Math.toRadians(value.angle.toDouble()).toFloat()
        spreadAngle = Math.toRadians(value.spreadAngle.toDouble()).toFloat()
        centreX = value.centreX
        centreY = value.centreY
    }

}