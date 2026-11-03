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

data class CmykPressParams(
    val spacing: Int,
    val blackGeneration: Float,
    val dotGain: Float,
    val misregistration: Float,
    val inkLimit: Float,
    val paperGrain: Float,
    val seed: Int,
    val strength: Float
) {
    companion object {
        val Default = CmykPressParams(
            spacing = 8,
            blackGeneration = 0.68f,
            dotGain = 0.22f,
            misregistration = 1.2f,
            inkLimit = 2.6f,
            paperGrain = 0.4f,
            seed = 0,
            strength = 1f
        )
    }
}