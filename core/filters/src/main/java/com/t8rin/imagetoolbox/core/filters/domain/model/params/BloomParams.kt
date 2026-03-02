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

data class BloomParams(
    val threshold: Float,
    val intensity: Float,
    val radius: Int,
    val softKnee: Float,
    val exposure: Float,
    val gamma: Float
) {
    companion object {
        val Default = BloomParams(
            threshold = 0.6f,
            intensity = 1.5f,
            radius = 25,
            softKnee = 0.5f,
            exposure = 0.02f,
            gamma = 1.1f
        )
    }
}