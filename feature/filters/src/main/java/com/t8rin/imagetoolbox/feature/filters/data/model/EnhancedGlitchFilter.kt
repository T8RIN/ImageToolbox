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
import com.awxkee.aire.Aire
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.EnhancedGlitch.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation

@FilterDefinition(group = FilterGroups.GLITCH)
internal class EnhancedGlitchFilter(
    override val value: Value
) : FilterTransformation, Filter.EnhancedGlitch {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Aire.glitch(
        bitmap = input,
        channelsShiftX = value.channelsShiftX,
        channelsShiftY = value.corruptionShiftY,
        corruptionSize = value.corruptionSize,
        corruptionCount = value.corruptionCount,
        corruptionShiftX = value.channelsShiftX,
        corruptionShiftY = value.corruptionShiftY
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.enhanced_glitch, R.string.filter_help_enhanced_glitch)

        override val parameters = structuredParameters {
            Value::channelsShiftX.float(R.string.channel_shift_x, -0.08f, -1f..1f)
            Value::channelsShiftY.float(R.string.channel_shift_y, -0.08f, -1f..1f)
            Value::corruptionSize.float(R.string.corruption_size, 0.01f, 0f..1f)
            Value::corruptionCount.int(R.string.amount, 60, 1..100)
            Value::corruptionShiftX.float(R.string.corruption_shift_x, -0.05f, -1f..1f)
            Value::corruptionShiftY.float(R.string.corruption_shift_y, 0.0f, -1f..1f)
        }
    }
}