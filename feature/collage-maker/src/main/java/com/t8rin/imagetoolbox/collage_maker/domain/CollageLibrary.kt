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

data class CollageStyle(
    val spacing: Float,
    val outerPadding: Float,
    val cornerRadius: Float,
    val borderWidth: Float,
    val borderColor: Int,
    val backgroundColor: Int,
    val gradient: GradientFill? = null,
    val shadowRadius: Float = 0f,
    val shadowColor: Int = 0x55000000,
    val borderGradient: GradientFill? = null,
    val layoutId: String? = null,
    val layoutParams: List<Float> = emptyList(),
    val imageCount: Int = 0,
    val slotStyles: List<CollageSlotStyle> = emptyList(),
    val slotBorderGradients: List<GradientFill?> = emptyList()
)

data class SavedCollageStyle(
    val name: String,
    val style: CollageStyle
)

data class CollageLibrary(
    val favorites: Set<String> = emptySet(),
    val styles: List<SavedCollageStyle> = emptyList()
)

fun CollageStyle.applyTo(params: CollageParams, imageCount: Int): CollageParams = params.copy(
    spacing = spacing,
    outerPadding = outerPadding,
    cornerRadius = cornerRadius,
    borderWidth = borderWidth,
    borderColor = borderColor,
    borderGradient = borderGradient,
    shadowRadius = shadowRadius,
    shadowColor = shadowColor,
    slotStyles = List(imageCount) { index ->
        slotStyles.getOrNull(index) ?: CollageSlotStyle(
            borderWidth = borderWidth,
            borderColor = borderColor,
            shadowRadius = shadowRadius,
            shadowColor = shadowColor
        )
    },
    slotBorderGradients = List(imageCount) { index ->
        if (index in slotBorderGradients.indices) slotBorderGradients[index] else borderGradient
    }
)