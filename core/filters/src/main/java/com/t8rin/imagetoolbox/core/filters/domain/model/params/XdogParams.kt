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

import com.t8rin.imagetoolbox.core.domain.model.ColorModel

data class XdogParams(
    val sigma: Float,
    val ratio: Float,
    val weight: Float,
    val threshold: Float,
    val sharpness: Float,
    val ink: ColorModel,
    val paper: ColorModel,
    val strength: Float
) {
    companion object {
        val Default = XdogParams(
            sigma = 2.5f,
            ratio = 1.6f,
            weight = 12f,
            threshold = 0.42f,
            sharpness = 14f,
            ink = ColorModel(0xFF251E1B.toInt()),
            paper = ColorModel(0xFFFAF0DF.toInt()),
            strength = 1f
        )
    }
}