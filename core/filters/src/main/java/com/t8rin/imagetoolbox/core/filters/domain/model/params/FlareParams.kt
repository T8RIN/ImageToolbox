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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.toColorModel

data class FlareParams(
    val radius: Float,
    val baseAmount: Float,
    val ringAmount: Float,
    val rayAmount: Float,
    val ringWidth: Float,
    val centreX: Float,
    val centreY: Float,
    val color: ColorModel
) {
    companion object {
        val Default = FlareParams(
            radius = 0.3f,
            baseAmount = 1f,
            ringAmount = 0.2f,
            rayAmount = 0.1f,
            ringWidth = 1.6f,
            centreX = 0.5f,
            centreY = 0.5f,
            color = Color.White.toArgb().toColorModel()
        )
    }
}
