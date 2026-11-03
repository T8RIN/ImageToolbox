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

data class SignedDistanceFieldParams(
    val alphaMask: Boolean,
    val threshold: Float,
    val distance: Float,
    val mode: Int,
    val spacing: Float,
    val insideColor: ColorModel,
    val outsideColor: ColorModel,
    val invert: Boolean
) {
    companion object {
        val Default = SignedDistanceFieldParams(
            alphaMask = false,
            threshold = 0.48f,
            distance = 44f,
            mode = 0,
            spacing = 8f,
            insideColor = ColorModel(0xFFF6EFDE.toInt()),
            outsideColor = ColorModel(0xFF101D36.toInt()),
            invert = false
        )
    }
}