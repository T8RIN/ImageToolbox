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

package com.t8rin.imagetoolbox.core.filters.domain.model.params

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.toColorModel

data class RubberStampParams(
    val threshold: Float,
    val softness: Float,
    val radius: Float,
    val firstColor: ColorModel,
    val secondColor: ColorModel
) {
    companion object {
        val Default = RubberStampParams(
            threshold = 0.3f,
            softness = 0.1f,
            radius = 0.05f,
            firstColor = Color(0xff3FA08F).toArgb().toColorModel(),
            secondColor = Color(0xff003027).toArgb().toColorModel(),
        )
    }
}