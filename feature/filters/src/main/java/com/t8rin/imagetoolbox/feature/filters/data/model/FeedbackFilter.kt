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

import com.jhlabs.FeedbackFilter
import com.jhlabs.JhFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Feedback.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class FeedbackFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.Feedback {

    override fun createFilter(): JhFilter = FeedbackFilter().apply {
        distance = value.distance
        angle = Math.toRadians(value.angle.toDouble()).toFloat()
        rotation = Math.toRadians(value.rotation.toDouble()).toFloat()
        zoom = value.zoom
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.feedback, R.string.filter_help_feedback)

        override val parameters = structuredParameters {
            Value::distance.float(R.string.distance, 10f, -100f..100f, roundTo = 0)
            Value::angle.float(R.string.angle, 0f, -180f..180f, roundTo = 0)
            Value::rotation.float(R.string.rotation, 3f, -180f..180f, roundTo = 0)
            Value::zoom.float(R.string.zoom, 0.05f, -1f..1f)
        }
    }
}