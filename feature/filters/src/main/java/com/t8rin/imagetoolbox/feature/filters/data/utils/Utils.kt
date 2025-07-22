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

package com.t8rin.imagetoolbox.feature.filters.data.utils

import com.awxkee.aire.EdgeMode
import com.awxkee.aire.PaletteTransferColorspace
import com.awxkee.aire.TransferFunction
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PaletteTransferSpace
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PopArtBlendingMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.TransferFunc
import com.t8rin.trickle.PopArtBlendMode

fun BlurEdgeMode.toEdgeMode(): EdgeMode = when (this) {
    BlurEdgeMode.Clamp -> EdgeMode.CLAMP
    BlurEdgeMode.Reflect101 -> EdgeMode.REFLECT_101
    BlurEdgeMode.Wrap -> EdgeMode.WRAP
    BlurEdgeMode.Reflect -> EdgeMode.REFLECT
}

fun TransferFunc.toFunc(): TransferFunction = when (this) {
    TransferFunc.SRGB -> TransferFunction.SRGB
    TransferFunc.REC709 -> TransferFunction.REC709
    TransferFunc.GAMMA2P2 -> TransferFunction.GAMMA2P2
    TransferFunc.GAMMA2P8 -> TransferFunction.GAMMA2P8
}

fun PaletteTransferSpace.toSpace(): PaletteTransferColorspace = when (this) {
    PaletteTransferSpace.LALPHABETA -> PaletteTransferColorspace.LALPHABETA
    PaletteTransferSpace.LAB -> PaletteTransferColorspace.LAB
    PaletteTransferSpace.OKLAB -> PaletteTransferColorspace.OKLAB
    PaletteTransferSpace.LUV -> PaletteTransferColorspace.LUV
}

fun PopArtBlendingMode.toMode(): PopArtBlendMode = when (this) {
    PopArtBlendingMode.MULTIPLY -> PopArtBlendMode.MULTIPLY
    PopArtBlendingMode.COLOR_BURN -> PopArtBlendMode.COLOR_BURN
    PopArtBlendingMode.SOFT_LIGHT -> PopArtBlendMode.SOFT_LIGHT
    PopArtBlendingMode.HSL_COLOR -> PopArtBlendMode.HSL_COLOR
    PopArtBlendingMode.HSL_HUE -> PopArtBlendMode.HSL_HUE
    PopArtBlendingMode.DIFFERENCE -> PopArtBlendMode.DIFFERENCE
}