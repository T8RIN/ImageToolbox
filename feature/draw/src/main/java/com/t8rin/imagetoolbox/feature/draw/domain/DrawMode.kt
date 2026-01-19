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

package com.t8rin.imagetoolbox.feature.draw.domain

import com.t8rin.imagetoolbox.core.domain.model.Pt
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.settings.domain.model.FontType

sealed class DrawMode(open val ordinal: Int) {
    data object Neon : DrawMode(2)
    data object Highlighter : DrawMode(3)
    data object Pen : DrawMode(0)

    sealed class PathEffect(override val ordinal: Int) : DrawMode(ordinal) {
        data class PrivacyBlur(
            val blurRadius: Int = 20
        ) : PathEffect(1)

        data class Pixelation(
            val pixelSize: Float = 35f
        ) : PathEffect(4)

        data class Custom(
            val filter: Filter<*>? = null
        ) : PathEffect(5)
    }

    data class Text(
        val text: String = "Text",
        val font: FontType? = null,
        val isRepeated: Boolean = false,
        val repeatingInterval: Pt = Pt.Zero
    ) : DrawMode(6)

    data class Image(
        val imageData: Any = "file:///android_asset/svg/emotions/aasparkles.svg",
        val repeatingInterval: Pt = Pt.Zero
    ) : DrawMode(7)

    data class SpotHeal(
        val mode: SpotHealMode = SpotHealMode.OpenCV
    ) : DrawMode(8)

    data class Warp(
        val warpMode: WarpMode = WarpMode.MOVE,
        val strength: Float = 0.25f,
        val hardness: Float = 0.5f,
        val strokes: List<WarpStroke> = emptyList()
    ) : DrawMode(9)

    companion object {
        val entries by lazy {
            listOf(
                Pen,
                PathEffect.PrivacyBlur(),
                SpotHeal(),
                Warp(),
                Neon,
                Highlighter,
                Text(),
                Image(),
                PathEffect.Pixelation(),
                PathEffect.Custom()
            )
        }
    }
}