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

package com.t8rin.imagetoolbox.feature.image_stitch.domain

import com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode
import com.t8rin.imagetoolbox.core.ui.utils.helper.entries
import com.t8rin.logger.makeLog

data class SavableCombiningParams(
    val stitchMode: String,
    val spacing: Int,
    val scaleSmallImagesToLarge: Boolean,
    val backgroundColor: Int,
    val fadingEdgesMode: StitchFadeSide,
    val alignment: StitchAlignment,
    val outputScale: Float,
    val blendingMode: Int,
    val fadeStrength: Float
)

fun CombiningParams.toSavable() = SavableCombiningParams(
    stitchMode = "${stitchMode.ordinal}_${stitchMode.gridCellsCount()}_${
        stitchMode.drops().joinToString(separator = "_")
    }",
    spacing = spacing,
    scaleSmallImagesToLarge = scaleSmallImagesToLarge,
    backgroundColor = backgroundColor,
    fadingEdgesMode = fadingEdgesMode,
    alignment = alignment,
    outputScale = outputScale,
    blendingMode = blendingMode.value,
    fadeStrength = fadeStrength
)

fun SavableCombiningParams.toParams() = CombiningParams(
    stitchMode = stitchMode.split("_").let {
        if (it.size < 2) StitchMode.Horizontal
        else {
            val mode = StitchMode.fromOrdinal(it.getOrNull(0)?.toIntOrNull() ?: 0)
            val cells = it.getOrNull(1)?.toIntOrNull() ?: 0

            when (mode) {
                is StitchMode.Grid.Horizontal -> mode.copy(rows = cells)
                is StitchMode.Grid.Vertical -> mode.copy(columns = cells)
                is StitchMode.Auto -> StitchMode.Auto(it.drop(2).map { s -> s.toIntOrNull() ?: 0 })

                else -> mode
            }
        }
    },
    spacing = spacing,
    scaleSmallImagesToLarge = scaleSmallImagesToLarge,
    backgroundColor = backgroundColor,
    fadingEdgesMode = fadingEdgesMode,
    alignment = alignment,
    outputScale = outputScale,
    blendingMode = BlendingMode.entries.find { it.value == blendingMode } ?: BlendingMode.SrcOver,
    fadeStrength = fadeStrength
).makeLog("COCK")