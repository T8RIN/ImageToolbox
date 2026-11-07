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

package com.t8rin.imagetoolbox.collage_maker.domain

import com.t8rin.collages.model.CollageSlotStyle
import com.t8rin.imagetoolbox.core.domain.model.GradientFill

data class CollageParams(
    val spacing: Float = 10f,
    val outerPadding: Float = 10f,
    val shadowRadius: Float = 0f,
    val shadowColor: Int = 0x55000000,
    val borderWidth: Float = 0f,
    val borderColor: Int = -1,
    val borderGradient: GradientFill? = null,
    val slotBorderGradients: List<GradientFill?> = emptyList(),
    val slotStyles: List<CollageSlotStyle> = emptyList(),
    val cornerRadius: Float = 0f,
    val outputScaleRatio: Float = 0.5f,
    val disableRotation: Boolean = true,
    val enableSnapToBorders: Boolean = true
) {
    fun swapPhotos(first: Int, second: Int, imageCount: Int): CollageParams {
        if (first !in 0 until imageCount || second !in 0 until imageCount || first == second) return this
        val defaultStyle = CollageSlotStyle(
            borderWidth = borderWidth,
            borderColor = borderColor,
            shadowRadius = shadowRadius,
            shadowColor = shadowColor
        )
        val indices = List(imageCount) { index ->
            when (index) {
                first -> second
                second -> first
                else -> index
            }
        }
        return copy(
            slotStyles = indices.map { slotStyles.getOrNull(it) ?: defaultStyle },
            slotBorderGradients = indices.map {
                if (it in slotBorderGradients.indices) slotBorderGradients[it] else borderGradient
            }
        )
    }
}