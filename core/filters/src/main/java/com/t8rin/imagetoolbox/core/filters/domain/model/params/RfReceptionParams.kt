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

data class RfReceptionParams(
    val delay: Int,
    val reflection: Float,
    val noise: Float,
    val frequency: Int,
    val seed: Int,
    val strength: Float
) {
    companion object {
        val Default = RfReceptionParams(
            delay = 19,
            reflection = 0.42f,
            noise = 0.09f,
            frequency = 21,
            seed = 0,
            strength = 1f
        )
    }
}