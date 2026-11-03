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

data class AutostereogramParams(
    val repeatDistance: Float,
    val depth: Float,
    val gamma: Float,
    val size: Int,
    val softness: Float,
    val invert: Boolean,
    val seed: Int
) {
    companion object {
        val Default = AutostereogramParams(
            repeatDistance = 0.15f,
            depth = 0.34f,
            gamma = 1f,
            size = 2,
            softness = 3f,
            invert = false,
            seed = 0
        )
    }
}