/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.settings.domain.model

sealed class SliderType(
    val ordinal: Int
) {

    data object MaterialYou : SliderType(0)
    data object Fancy : SliderType(1)
    data object Material : SliderType(2)
    data object HyperOS : SliderType(3)

    companion object {
        fun fromInt(ordinal: Int) = when (ordinal) {
            1 -> Fancy
            2 -> Material
            3 -> HyperOS
            else -> MaterialYou
        }

        val entries by lazy {
            listOf(
                MaterialYou, Fancy, Material, HyperOS
            )
        }
    }

}