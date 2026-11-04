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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.UnderwaterRestoration.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.UnderwaterRestoration

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class UnderwaterRestorationFilter(
    override val value: Value
) : FilterTransformation, Filter.UnderwaterRestoration {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = UnderwaterRestoration.apply(
        input = input,
        radius = value.radius,
        restoration = value.restoration,
        redRecovery = value.redRecovery,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.underwater_restoration,
            R.string.filter_help_underwater_restoration
        )

        override val parameters = structuredParameters {
            Value::radius.float(R.string.radius, 15f, 3f..60f)
            Value::restoration.float(R.string.restoration, 0.8f, 0f..1f)
            Value::redRecovery.float(R.string.red_recovery, 0.7f, 0f..1f)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}