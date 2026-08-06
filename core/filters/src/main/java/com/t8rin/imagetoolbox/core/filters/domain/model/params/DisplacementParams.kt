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

package com.t8rin.imagetoolbox.core.filters.domain.model.params

import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.DisplacementBoundary
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.DisplacementInterpolation

data class DisplacementParams(
    val horizontalMap: ImageModel,
    val verticalMap: ImageModel?,
    val strengthX: Float,
    val strengthY: Float,
    val interpolation: DisplacementInterpolation,
    val boundary: DisplacementBoundary
) {
    companion object {
        val Default = DisplacementParams(
            horizontalMap = ImageModel(""),
            verticalMap = null,
            strengthX = 0.1f,
            strengthY = 0.1f,
            interpolation = DisplacementInterpolation.Linear,
            boundary = DisplacementBoundary.Clamp
        )
    }
}
